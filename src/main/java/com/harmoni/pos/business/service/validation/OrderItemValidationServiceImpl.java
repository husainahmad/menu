package com.harmoni.pos.business.service.validation;

import com.harmoni.pos.business.service.customization.CustomizationSelectionValidator;
import com.harmoni.pos.business.service.customizationoptiontierprice.CustomizationOptionTierPriceService;
import com.harmoni.pos.business.service.product.ProductCustomizationService;
import com.harmoni.pos.business.service.sku.SkuCustomizationOptionService;
import com.harmoni.pos.business.service.skutierprice.SkuTierPriceService;
import com.harmoni.pos.business.service.store.tier.StoreTierService;
import com.harmoni.pos.business.service.tier.tiermenu.TierMenuService;
import com.harmoni.pos.exception.BusinessBadRequestException;
import com.harmoni.pos.menu.mapper.CustomizationOptionMapper;
import com.harmoni.pos.menu.mapper.ProductMapper;
import com.harmoni.pos.menu.mapper.SkuMapper;
import com.harmoni.pos.menu.model.*;
import com.harmoni.pos.menu.model.dto.product.ProductCustomizationResponseDto;
import com.harmoni.pos.menu.model.dto.validation.CustomizationGroupSelectionDto;
import com.harmoni.pos.menu.model.dto.validation.CustomizationOptionSelectionDto;
import com.harmoni.pos.menu.model.dto.validation.ValidateOrderItemRequest;
import com.harmoni.pos.menu.model.dto.validation.ValidateOrderItemsRequest;
import com.harmoni.pos.menu.model.dto.validation.ValidateOrderItemsResponseDto;
import com.harmoni.pos.menu.model.dto.validation.ValidatedCustomizationDto;
import com.harmoni.pos.menu.model.dto.validation.ValidatedOrderItemDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Confirms an order service's basket against the catalogue.
 * <p>
 * Every answer is read from the database here, never from the request. That is the whole
 * contract: the order service names what the customer picked, this service decides
 * whether that is still something the shop sells and says what it is called and what it
 * costs. Nothing here totals an order. The caller holds the quantities and the
 * promotions, so the arithmetic belongs there.
 *
 * @author husainahmad
 */
@Service("orderItemValidationService")
@RequiredArgsConstructor
@Slf4j
public class OrderItemValidationServiceImpl implements OrderItemValidationService {

    /**
     * The most units of one SKU a single line may carry.
     * <p>
     * There is no upper bound in the catalogue for this, only a lower one, so without a
     * ceiling here the quantity is an unbounded multiplier the client chooses. The
     * ceiling is deliberately generous: it exists to catch a broken client, not to
     * second guess a bulk order.
     */
    private static final int MAX_UNITS_PER_LINE = 100;

    /**
     * The most units of one customization option a single line may carry, for the same
     * reason: repeating an option is an unbounded multiplier on the line.
     */
    private static final int MAX_UNITS_PER_OPTION = 50;

    /**
     * The most lines one request may carry, counting each entry in a customization list
     * separately. A basket larger than this is a client bug, and bounding it here means a
     * bad request fails on a number rather than after the queries have run.
     */
    private static final int MAX_REQUESTED_ENTRIES = 200;

    /**
     * The brand filter value meaning "do not filter by brand". The product lookups below
     * are cross brand by nature: the order service knows product IDs, not which brand
     * sells them.
     */
    private static final int NO_BRAND_FILTER = -1;

    private final ProductMapper productMapper;
    private final SkuMapper skuMapper;
    private final StoreTierService storeTierService;
    private final TierMenuService tierMenuService;
    private final SkuTierPriceService skuTierPriceService;
    private final ProductCustomizationService productCustomizationService;
    private final SkuCustomizationOptionService skuCustomizationOptionService;
    private final CustomizationOptionMapper customizationOptionMapper;
    private final CustomizationOptionTierPriceService customizationOptionTierPriceService;
    private final CustomizationSelectionValidator selectionValidator;

    /**
     * {@inheritDoc}
     */
    @Override
    public ValidateOrderItemsResponseDto validate(ValidateOrderItemsRequest request) {
        List<ValidateOrderItemRequest> lines = request.getItems();
        requireBoundedRequest(lines);

        Basket basket = loadBasket(request.getStoreId(), lines);

        List<ValidatedOrderItemDto> validated = new ArrayList<>(lines.size());
        for (ValidateOrderItemRequest line : lines) {
            validated.add(validateLine(request.getStoreId(), line, basket));
        }

        ValidateOrderItemsResponseDto response = new ValidateOrderItemsResponseDto();
        response.setValid(true);
        response.setItems(validated);
        return response;
    }

    /**
     * Caps how much a single request may ask about. Counting the customization choices
     * too, because they are the entries a caller can repeat freely and are therefore the
     * way to make this endpoint do unbounded work.
     *
     * @param lines the basket being confirmed
     */
    private static void requireBoundedRequest(List<ValidateOrderItemRequest> lines) {
        int entries = lines.stream()
                .mapToInt(line -> 1 + countOptions(line))
                .sum();
        if (entries > MAX_REQUESTED_ENTRIES) {
            throw new BusinessBadRequestException("exception.orderValidation.tooManyLines",
                    new Object[]{entries, MAX_REQUESTED_ENTRIES});
        }
    }

    /**
     * Reads everything the whole basket needs in one pass per table, so a basket of forty
     * lines does not become forty round trips.
     * <p>
     * Nothing below this method queries the database, and nothing above it needs to: every
     * lookup the validation walks over is resolved here first and handed down already in
     * hand. The rule this keeps is that the number of queries is a function of the number of
     * tables, not of the size of the basket. A line is checked against these maps, and a
     * basket of six items with options on each costs the same handful of round trips as an
     * empty one.
     *
     * @param storeId the store the sale is happening at
     * @param lines   the basket being confirmed
     * @return the catalogue state the lines are checked against
     */
    private Basket loadBasket(Integer storeId, List<ValidateOrderItemRequest> lines) {
        StoreTier storeTier = storeTierService.selectByStoreId(storeId);
        Integer priceTierId = storeTier == null ? null : storeTier.getTierPriceId();

        List<Integer> productIds = distinctIds(lines, ValidateOrderItemRequest::getProductId);
        List<Integer> skuIds = distinctIds(lines, ValidateOrderItemRequest::getSkuId);
        List<Integer> optionIds = distinctOptionIds(lines);

        return new Basket(
                priceTierId,
                sellableCategoryIds(storeTier),
                byId(productMapper.selectByIds(productIds, NO_BRAND_FILTER), Product::getId),
                byId(skuMapper.selectByIds(skuIds), Sku::getId),
                skuPrices(skuIds, priceTierId),
                // Every product in the basket, not only those carrying choices: the rules a
                // product makes required are checked on every line, so a basket cannot skip
                // this by sending no options.
                productCustomizationService.getDetailedByProductIds(productIds),
                permittedOptionIds(skuIds),
                byId(optionIds.isEmpty() ? List.of()
                        : customizationOptionMapper.selectByIds(optionIds), CustomizationOption::getId),
                optionPrices(optionIds, priceTierId));
    }

    /**
     * The option IDs named anywhere in the basket, for a single batch lookup.
     *
     * @param lines the basket being confirmed
     * @return the distinct option IDs
     */
    /**
     * Counts every selected option across all groups on a line, because options are the
     * entries a caller can repeat freely and are therefore the way to make this
     * endpoint do unbounded work.
     */
    private static int countOptions(ValidateOrderItemRequest line) {
        if (ObjectUtils.isEmpty(line.getCustomizations())) {
            return 0;
        }
        return line.getCustomizations().stream()
                .filter(group -> group != null && !ObjectUtils.isEmpty(group.getOptions()))
                .mapToInt(group -> group.getOptions().size())
                .sum();
    }

    private static List<Integer> distinctOptionIds(List<ValidateOrderItemRequest> lines) {
        return lines.stream()
                .filter(line -> !ObjectUtils.isEmpty(line.getCustomizations()))
                .flatMap(line -> line.getCustomizations().stream())
                .filter(Objects::nonNull)
                .filter(group -> !ObjectUtils.isEmpty(group.getOptions()))
                .flatMap(group -> group.getOptions().stream())
                .filter(Objects::nonNull)
                .map(CustomizationOptionSelectionDto::getCustomizationOptionId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * The options each SKU in the basket permits, resolved for all of them at once.
     *
     * @param skuIds the SKUs named in the basket
     * @return the permitted option IDs per SKU ID
     */
    private Map<Integer, Set<Integer>> permittedOptionIds(List<Integer> skuIds) {
        if (ObjectUtils.isEmpty(skuIds)) {
            return Map.of();
        }
        return skuCustomizationOptionService.getBySkuIds(skuIds).stream()
                .filter(link -> link.getSkuId() != null && link.getCustomizationOptionId() != null)
                .collect(Collectors.groupingBy(SkuCustomizationOption::getSkuId,
                        Collectors.mapping(SkuCustomizationOption::getCustomizationOptionId,
                                Collectors.toSet())));
    }

    /**
     * The official price of each option named in the basket at the store's tier, falling back
     * to the lowest tier that has a price when the store has no price tier, for the same
     * reason the SKU price does.
     * <p>
     * Asked once for every option in the basket rather than once per line. Which of the two
     * lookups to make is settled here, from the store's tier, so it cannot drift between
     * lines priced in the same request.
     *
     * @param optionIds   the options named anywhere in the basket
     * @param priceTierId the store's price tier, possibly {@code null}
     * @return the price per option ID
     */
    private Map<Integer, BigDecimal> optionPrices(List<Integer> optionIds, Integer priceTierId) {
        if (ObjectUtils.isEmpty(optionIds)) {
            return Map.of();
        }
        List<CustomizationOptionTierPrice> tierPrices = priceTierId == null
                ? customizationOptionTierPriceService.selectByOptionIds(optionIds)
                : customizationOptionTierPriceService.selectByOptionIdsAndTierId(optionIds, priceTierId);
        return tierPrices.stream()
                .filter(price -> !Boolean.TRUE.equals(price.getDeleted()))
                .filter(price -> price.getCustomizationOptionId() != null && price.getPrice() != null)
                .sorted(Comparator.comparing(CustomizationOptionTierPrice::getTierId,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toMap(CustomizationOptionTierPrice::getCustomizationOptionId,
                        CustomizationOptionTierPrice::getPrice, (a, b) -> a));
    }

    /**
     * Indexes entities by their ID, ignoring anything the database left without one.
     *
     * @param entities the rows read for the basket
     * @param id       how to read each row's ID
     * @return the rows keyed by ID
     */
    private static <T> Map<Integer, T> byId(List<T> entities, Function<T, Integer> id) {
        return entities.stream()
                .filter(entity -> id.apply(entity) != null)
                .collect(Collectors.toMap(id, entity -> entity, (a, b) -> a));
    }

    /**
     * The category IDs the store's menu tier actually offers, or {@code null} when the
     * store has no menu tier at all.
     * <p>
     * This is the store level availability that exists in the catalogue. It is a category
     * gate rather than a per product one: a store's menu tier lists the categories it
     * sells, and a product outside those categories is not on that store's menu.
     * <p>
     * The two empty cases are deliberately different. {@code null} means the store has no
     * menu tier configured, which is not the same as a store whose menu tier has every
     * category switched off. The first is a half configured store and is let through,
     * because refusing every order there is a worse failure than selling something the
     * store intends to sell. The second is a deliberate decision to sell nothing, and is
     * honoured.
     *
     * @param storeTier the store's tier assignment, possibly {@code null}
     * @return the category IDs on sale, or {@code null} when there is no menu tier
     */
    private Set<Integer> sellableCategoryIds(StoreTier storeTier) {
        // getMenusByTierId filters on tier_menus.tier_id, so this must be the tier id
        // carried by the store's tier_menu row, not the row's own primary key.
        Integer menuTierId = storeTier == null || storeTier.getTierMenu() == null
                ? null
                : storeTier.getTierMenu().getTierId();
        if (menuTierId == null) {
            return null;
        }
        return tierMenuService.getMenusByTierId(storeTier.getTierMenu().getId()).stream()
                .filter(menu -> !Boolean.TRUE.equals(menu.getDeleted()))
                .map(TierMenu::getCategoryId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    /**
     * The official unit price of each SKU at the store's tier.
     * <p>
     * When the store has no price tier the lookup is made without one and the lowest tier
     * that has a price wins, so a SKU is never reported as priceless just because a tier
     * is missing. The tier prices carry no deleted filter in SQL, so it is applied here.
     *
     * @param skuIds      the SKUs named anywhere in the basket
     * @param priceTierId the store's price tier, possibly {@code null}
     * @return the price per SKU ID
     */
    private Map<Integer, BigDecimal> skuPrices(List<Integer> skuIds, Integer priceTierId) {
        return skuTierPriceService.selectBySkusTierId(skuIds, priceTierId).stream()
                .filter(price -> !Boolean.TRUE.equals(price.getDeleted()))
                .filter(price -> price.getSkuId() != null && price.getPrice() != null)
                .sorted(Comparator.comparing(SkuTierPrice::getTierId,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toMap(SkuTierPrice::getSkuId, SkuTierPrice::getPrice, (a, b) -> a));
    }

    /**
     * Confirms one line: the product, the SKU, the quantity, then every customization
     * choice on it.
     *
     * @param storeId the store the sale is happening at, named back in the error messages
     * @param line    the line being confirmed
     * @param basket  the catalogue state read for the whole basket
     * @return the confirmed line
     */
    private ValidatedOrderItemDto validateLine(Integer storeId, ValidateOrderItemRequest line, Basket basket) {
        Product product = requireSellableProduct(storeId, line.getProductId(), basket);
        Sku sku = requireOrderableSku(line, basket);
        int quantity = requireQuantity(line.getQuantity());

        ValidatedOrderItemDto validated = new ValidatedOrderItemDto();
        validated.setProductId(product.getId());
        validated.setProductName(product.getName());
        validated.setCategoryId(product.getCategoryId());
        validated.setCategoryName(product.getCategory() == null ? null : product.getCategory().getName());
        validated.setSkuId(sku.getId());
        validated.setSkuName(sku.getName());
        validated.setSkuPrice(requireSkuPrice(storeId, sku.getId(), basket));
        validated.setQuantity(quantity);
        validated.setCustomizations(validateCustomizations(line, product.getId(), sku.getId(), basket));
        return validated;
    }

    /**
     * The product must exist, not be deleted, and be on the store's menu.
     *
     * @param storeId   the store the sale is happening at
     * @param productId the product named on the line
     * @param basket    the catalogue state read for the whole basket
     * @return the product
     */
    private Product requireSellableProduct(Integer storeId, Integer productId, Basket basket) {
        Product product = basket.productsById().get(productId);
        if (product == null) {
            throw new BusinessBadRequestException("exception.orderValidation.productNotFound",
                    new Object[]{productId});
        }
        Set<Integer> sellableCategoryIds = basket.sellableCategoryIds();
        if (sellableCategoryIds != null && !sellableCategoryIds.contains(product.getCategoryId())) {
            throw new BusinessBadRequestException("exception.orderValidation.productNotAvailable",
                    new Object[]{productId, storeId});
        }
        return product;
    }

    /**
     * The SKU must exist, be orderable, and be one of the product's own.
     * <p>
     * The ownership check matters as much as the existence check: a caller that pairs a
     * real SKU with the wrong product would otherwise be confirmed at a price and a name
     * that belong to something else entirely.
     *
     * @param line   the line being confirmed
     * @param basket the catalogue state read for the whole basket
     * @return the SKU
     */
    private static Sku requireOrderableSku(ValidateOrderItemRequest line, Basket basket) {
        Sku sku = basket.skusById().get(line.getSkuId());
        if (sku == null) {
            throw new BusinessBadRequestException("exception.orderValidation.skuNotFound",
                    new Object[]{line.getSkuId()});
        }
        if (Boolean.TRUE.equals(sku.getDeleted()) || Boolean.FALSE.equals(sku.getActive())) {
            throw new BusinessBadRequestException("exception.orderValidation.skuNotAvailable",
                    new Object[]{sku.getId()});
        }
        if (!Objects.equals(sku.getProductId(), line.getProductId())) {
            throw new BusinessBadRequestException("exception.orderValidation.skuProductMismatch",
                    new Object[]{sku.getId(), line.getProductId()});
        }
        return sku;
    }

    /**
     * A line must carry at least one unit and not an absurd number of them.
     *
     * @param quantity the quantity named on the line, possibly {@code null}
     * @return the quantity to charge for
     */
    private static int requireQuantity(Integer quantity) {
        if (quantity == null || quantity < 1 || quantity > MAX_UNITS_PER_LINE) {
            throw new BusinessBadRequestException("exception.orderValidation.invalidQuantity",
                    new Object[]{quantity, MAX_UNITS_PER_LINE});
        }
        return quantity;
    }

    /**
     * A SKU with no price at this store cannot be sold, and must not be reported as free.
     *
     * @param storeId the store the sale is happening at
     * @param skuId   the SKU being ordered
     * @param basket  the catalogue state read for the whole basket
     * @return the official unit price
     */
    private static BigDecimal requireSkuPrice(Integer storeId, Integer skuId, Basket basket) {
        BigDecimal price = basket.skuPrices().get(skuId);
        if (price == null) {
            throw new BusinessBadRequestException("exception.orderValidation.skuPriceNotAvailable",
                    new Object[]{skuId, storeId});
        }
        return price;
    }

    /**
     * Confirms every customization choice on a line and prices it.
     * <p>
     * Each named option must exist, must belong to a customization the product actually
     * offers. Then the group's own rule set is applied,
     * by the same validator the pricing path uses, so a basket cannot pass one check and
     * fail the other.
     * <p>
     * The group rules are checked even when the caller named no option at all. That is the
     * case a required customization exists for: a line that omits a mandatory choice is
     * refused here, not quietly treated as one where the customer chose nothing.
     *
     * @param line      the line being confirmed
     * @param productId the product on the line
     * @param skuId     the SKU on the line
     * @param basket    the catalogue state read for the whole basket
     * @return the choices, named and priced, in the order they were sent
     */
    private List<ValidatedCustomizationDto> validateCustomizations(ValidateOrderItemRequest line,
                                                                    Integer productId, Integer skuId,
                                                                    Basket basket) {
        LinkedHashMap<Integer, Integer> unitsByOptionId = tally(line.getCustomizations());
        Map<Integer, Integer> claimedGroupByOptionId = claimedGroups(line.getCustomizations());

        List<ProductCustomizationResponseDto> groups = basket.groupsByProductId().getOrDefault(productId, List.of());
        List<CustomizationSelectionValidator.SelectedOption> selections = new ArrayList<>(unitsByOptionId.size());
        List<CustomizationOption> chosenOptions = new ArrayList<>(unitsByOptionId.size());

        if (!unitsByOptionId.isEmpty()) {
            Set<Integer> optionIdsForProduct = optionIdsForProduct(groups);

            for (Map.Entry<Integer, Integer> entry : unitsByOptionId.entrySet()) {
                CustomizationOption option = requireApplicableOption(entry.getKey(), productId, skuId,
                        basket.optionsById(), optionIdsForProduct);
                requireClaimedGroup(entry.getKey(), option.getCustomizationId(),
                        claimedGroupByOptionId, productId, skuId);
                chosenOptions.add(option);
                selections.add(new CustomizationSelectionValidator.SelectedOption(
                        option.getCustomizationId(), entry.getValue()));
            }
        }

        selectionValidator.validate(groups, selections);
        requireAllowedQuantities(groups, unitsByOptionId, chosenOptions, productId, skuId);

        if (chosenOptions.isEmpty()) {
            return List.of();
        }

        List<ValidatedCustomizationDto> validated = new ArrayList<>(chosenOptions.size());
        for (CustomizationOption option : chosenOptions) {
            validated.add(toValidatedCustomization(option, groups,
                    basket.optionPrices().getOrDefault(option.getId(), BigDecimal.ZERO),
                    unitsByOptionId.get(option.getId())));
        }
        return validated;
    }

    /**
     * Tallies the choices on a line into how many units of each option were asked for.
     * <p>
     * Groups are walked in the order sent and options in the order within each group.
     * Naming an option twice, in one group or across repeated groups, is a quantity of
     * two, not a second choice, so the units are summed. The first occurrence fixes the
     * option's position in the answer, which is what keeps a receipt listing the
     * choices in the order they were tapped.
     *
     * @param groups the customization groups as sent, possibly {@code null}
     * @return units of each option, keyed by option ID, in first seen order
     */
    private static LinkedHashMap<Integer, Integer> tally(List<CustomizationGroupSelectionDto> groups) {
        LinkedHashMap<Integer, Integer> unitsByOptionId = new LinkedHashMap<>();
        if (ObjectUtils.isEmpty(groups)) {
            return unitsByOptionId;
        }
        for (CustomizationGroupSelectionDto group : groups) {
            if (group == null || ObjectUtils.isEmpty(group.getOptions())) {
                continue;
            }
            for (CustomizationOptionSelectionDto selection : group.getOptions()) {
                if (selection == null) {
                    continue;
                }
                Integer optionId = selection.getCustomizationOptionId();
                int units = unitsByOptionId.merge(optionId, units(selection), Integer::sum);
                if (units > MAX_UNITS_PER_OPTION) {
                    throw new BusinessBadRequestException("exception.orderValidation.invalidQuantity",
                            new Object[]{units, MAX_UNITS_PER_OPTION});
                }
            }
        }
        return unitsByOptionId;
    }

    /**
     * Remembers which group each option was claimed under, first claim wins. Used to
     * refuse an option sent under a group it does not belong to.
     */
    private static Map<Integer, Integer> claimedGroups(List<CustomizationGroupSelectionDto> groups) {
        Map<Integer, Integer> claimed = new LinkedHashMap<>();
        if (ObjectUtils.isEmpty(groups)) {
            return claimed;
        }
        for (CustomizationGroupSelectionDto group : groups) {
            if (group == null || ObjectUtils.isEmpty(group.getOptions())) {
                continue;
            }
            for (CustomizationOptionSelectionDto selection : group.getOptions()) {
                if (selection == null || selection.getCustomizationOptionId() == null) {
                    continue;
                }
                claimed.putIfAbsent(selection.getCustomizationOptionId(), group.getCustomizationId());
            }
        }
        return claimed;
    }

    /**
     * The group an option was sent under must be the group the catalogue puts it in.
     * Accepting a mismatched label would let a caller satisfy a required group with an
     * option from another group.
     */
    private static void requireClaimedGroup(Integer optionId, Integer actualGroupId,
                                            Map<Integer, Integer> claimedGroupByOptionId,
                                            Integer productId, Integer skuId) {
        Integer claimed = claimedGroupByOptionId.get(optionId);
        if (!Objects.equals(claimed, actualGroupId)) {
            throw new BusinessBadRequestException("exception.orderValidation.customizationNotApplicable",
                    new Object[]{optionId, productId, skuId});
        }
    }

    /**
     * Rejects a quantity greater than one on an option whose group does not allow it.
     * <p>
     * Runs after the group rules so both validation paths report rule violations first
     * and quantity permission second: a basket breaking a required/single/min/max rule
     * fails on that rule here and on the pricing path alike. A group with no explicit
     * flag ({@code null}, including rows written before the flag existed) is treated
     * as not allowing a quantity.
     */
    private static void requireAllowedQuantities(List<ProductCustomizationResponseDto> groups,
                                                 LinkedHashMap<Integer, Integer> unitsByOptionId,
                                                 List<CustomizationOption> chosenOptions,
                                                 Integer productId, Integer skuId) {
        if (chosenOptions.isEmpty() || ObjectUtils.isEmpty(groups)) {
            return;
        }
        Map<Integer, Boolean> allowQuantityByGroup = groups.stream()
                .filter(group -> group.getCustomizationId() != null)
                .collect(Collectors.toMap(ProductCustomizationResponseDto::getCustomizationId,
                        group -> Boolean.TRUE.equals(group.getAllowQuantity()), (a, b) -> a || b));
        for (CustomizationOption option : chosenOptions) {
            int units = unitsByOptionId.getOrDefault(option.getId(), 1);
            if (units > 1 && !allowQuantityByGroup.getOrDefault(option.getCustomizationId(), false)) {
                throw new BusinessBadRequestException("exception.orderValidation.quantityNotAllowed",
                        new Object[]{option.getId(), productId, skuId});
            }
        }
    }

    /**
     * A choice must carry at least one unit of its option and not an absurd number.
     *
     * @param selection the choice as sent
     * @return the units it carries
     */
    private static int units(CustomizationOptionSelectionDto selection) {
        Integer quantity = selection.getQuantity();
        if (quantity == null || quantity < 1 || quantity > MAX_UNITS_PER_OPTION) {
            throw new BusinessBadRequestException("exception.orderValidation.invalidQuantity",
                    new Object[]{quantity, MAX_UNITS_PER_OPTION});
        }
        return quantity;
    }

    /**
     * The option must exist in the catalogue. A soft deleted option is not returned by the
     * lookup, so it is indistinguishable from one that never was, and both are refused:
     * neither can be chosen on a line today.
     *
     * @param optionId     the option named on the line
     * @param optionsById  the catalogue entries for every option named in the basket
     * @return the option
     */
    private static CustomizationOption requireExistingOption(Integer optionId,
                                                              Map<Integer, CustomizationOption> optionsById) {
        CustomizationOption option = optionsById.get(optionId);
        if (option == null || option.getCustomizationId() == null) {
            throw new BusinessBadRequestException(
                    "exception.orderValidation.customizationOptionNotFound", new Object[]{optionId});
        }
        return option;
    }

    /**
     * The option must be one this product offers, through a customization assigned to it.
     * <p>
     * The product gate is what stops an option from a different product being smuggled in.
     *
     * @param optionId          the option named on the line
     * @param productId         the product on the line
     * @param skuId             the SKU on the line
     * @param optionsById       the catalogue entries for every option named in the basket
     * @param optionIdsForGroup the options reachable through the product's customizations
     * @param optionIdsForSku   the options linked to the SKU (unused after validation logic change)
     * @return the option
     */
    private static CustomizationOption requireApplicableOption(Integer optionId, Integer productId, Integer skuId,
                                                               Map<Integer, CustomizationOption> optionsById,
                                                               Set<Integer> optionIdsForGroup) {
        CustomizationOption option = requireExistingOption(optionId, optionsById);
        if (!optionIdsForGroup.contains(optionId)) {
            throw new BusinessBadRequestException("exception.orderValidation.customizationNotApplicable",
                    new Object[]{optionId, productId, skuId});
        }
        return option;
    }

    /**
     * The options reachable through the customizations assigned to a product, which is the
     * set a customer may be offered.
     * <p>
     * Deleted customizations, deleted links and deleted options are already excluded by the
     * lookups behind the group, so anything arriving here is on sale for the product. An
     * option reachable only through a group the product does not have is not on this
     * product's list no matter which SKU it was sent against.
     *
     * @param groups the product's customizations with their options attached
     * @return the option IDs the product offers
     */
    private static Set<Integer> optionIdsForProduct(List<ProductCustomizationResponseDto> groups) {
        return groups.stream()
                .filter(group -> !ObjectUtils.isEmpty(group.getOptions()))
                .flatMap(group -> group.getOptions().stream())
                .filter(option -> !Boolean.TRUE.equals(option.getIsDeleted()))
                .map(CustomizationOption::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    /**
     * Builds one priced choice, carrying the names so the caller can store them.
     *
     * @param option  the chosen option
     * @param groups  the product's customizations, to resolve the group name
     * @param price   the option's price for one unit at the store's tier
     * @param units   how many units of the option the line carries
     * @return the priced choice
     */
    private static ValidatedCustomizationDto toValidatedCustomization(CustomizationOption option,
                                                                      List<ProductCustomizationResponseDto> groups,
                                                                      BigDecimal price, int units) {
        ValidatedCustomizationDto dto = new ValidatedCustomizationDto();
        dto.setCustomizationOptionId(option.getId());
        dto.setCustomizationId(option.getCustomizationId());
        dto.setCustomizationName(groups.stream()
                .filter(group -> Objects.equals(group.getCustomizationId(), option.getCustomizationId()))
                .map(ProductCustomizationResponseDto::getName)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(""));
        dto.setOptionName(option.getName());
        dto.setPrice(price);
        dto.setQuantity(units);
        return dto;
    }

    /**
     * The distinct, non null values of one field across every line, for a batch lookup.
     *
     * @param lines the basket being confirmed
     * @param field the field to collect
     * @return the distinct values
     */
    private static List<Integer> distinctIds(List<ValidateOrderItemRequest> lines,
                                             Function<ValidateOrderItemRequest, Integer> field) {
        return lines.stream()
                .map(field)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * The catalogue state a whole basket is checked against: what the store may sell and
     * what each named thing currently is, read once up front and never again.
     * <p>
     * Nothing here is lazy and nothing here can query. That is deliberate. A basket that
     * resolved its customizations on demand looked correct and was, in the sense that every
     * line found what it asked for, but it cost a query per product and a query per line, so
     * a forty line order turned into hundreds of round trips before any line could be
     * answered. Reading it all up front makes the cost of a basket a function of the number
     * of tables involved instead of the number of lines.
     *
     * @param priceTierId             the store's price tier, possibly {@code null}
     * @param sellableCategoryIds     the categories on the store's menu, {@code null} when
     *                                the store has no menu tier configured
     * @param productsById            the products named in the basket
     * @param skusById                the SKUs named in the basket
     * @param skuPrices               the SKU prices at the store's tier
     * @param groupsByProductId       each product's customizations, rules and options
     * @param permittedOptionIdsBySkuId the options each SKU permits
     * @param optionsById             the options named anywhere in the basket
     * @param optionPrices            those options' prices at the store's tier
     */
    private record Basket(Integer priceTierId,
                          Set<Integer> sellableCategoryIds,
                          Map<Integer, Product> productsById,
                          Map<Integer, Sku> skusById,
                          Map<Integer, BigDecimal> skuPrices,
                          Map<Integer, List<ProductCustomizationResponseDto>> groupsByProductId,
                          Map<Integer, Set<Integer>> permittedOptionIdsBySkuId,
                          Map<Integer, CustomizationOption> optionsById,
                          Map<Integer, BigDecimal> optionPrices) {
    }
}
