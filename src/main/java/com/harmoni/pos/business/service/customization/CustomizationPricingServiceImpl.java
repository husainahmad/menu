package com.harmoni.pos.business.service.customization;

import com.harmoni.pos.business.service.customizationoptiontierprice.CustomizationOptionTierPriceService;
import com.harmoni.pos.business.service.product.ProductCustomizationService;
import com.harmoni.pos.business.service.sku.SkuCustomizationOptionService;
import com.harmoni.pos.business.service.store.tier.StoreTierService;
import com.harmoni.pos.business.service.user.UserService;
import com.harmoni.pos.exception.BusinessBadRequestException;
import com.harmoni.pos.menu.mapper.CustomizationOptionMapper;
import com.harmoni.pos.menu.model.CustomizationOption;
import com.harmoni.pos.menu.model.CustomizationOptionTierPrice;
import com.harmoni.pos.menu.model.StoreTier;
import com.harmoni.pos.menu.model.User;
import com.harmoni.pos.menu.model.dto.pricing.CustomizationPriceRequestDto;
import com.harmoni.pos.menu.model.dto.pricing.CustomizationPriceResponseDto;
import com.harmoni.pos.menu.model.dto.pricing.PricedCustomizationDto;
import com.harmoni.pos.menu.model.dto.product.ProductCustomizationResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Validates a customer's customization choices against the catalogue and prices them.
 * <p>
 * This is the only place a customization price is decided. The tier the price is read at
 * is resolved here from the operator's store, because the menu service is the one that
 * knows it: a caller naming its own price would make the tier meaningless.
 *
 * @author husainahmad
 */
@Service("customizationPricingService")
@RequiredArgsConstructor
@Slf4j
public class CustomizationPricingServiceImpl implements CustomizationPricingService {

    /**
     * The most units of one option a single line may carry.
     * <p>
     * A customization's own {@code maxSelection} is the cap that means something to the
     * person ordering, and most groups have one. This is the backstop for the ones that
     * do not: without it, repeating an option in the request is an unbounded multiplier
     * on the line total, decided entirely by the client.
     */
    private static final int MAX_UNITS_PER_OPTION = 50;

    /**
     * The most entries a request may name in total, repeats included. A client that
     * sends more has made a mistake rather than a large order, and counting them all is
     * cheaper than trusting them.
     */
    private static final int MAX_REQUESTED_ENTRIES = 200;

    private final UserService userService;
    private final StoreTierService storeTierService;
    private final ProductCustomizationService productCustomizationService;
    private final SkuCustomizationOptionService skuCustomizationOptionService;
    private final CustomizationOptionMapper customizationOptionMapper;
    private final CustomizationOptionTierPriceService tierPriceService;
    private final CustomizationSelectionValidator selectionValidator;

    /**
     * {@inheritDoc}
     */
    @Override
    public CustomizationPriceResponseDto price(CustomizationPriceRequestDto request, String username) {
        requireProductAndSku(request);
        int quantity = resolveQuantity(request.getQuantity());

        LinkedHashMap<Integer, Integer> chosenCounts =
                countRequestedOptions(request.getCustomizationOptionIds());

        User user = userService.selectByUsername(username);
        StoreTier storeTier = storeTierService.selectByStoreId(user.getStoreId());
        Integer tierId = storeTier == null ? null : storeTier.getTierPriceId();

        List<ProductCustomizationResponseDto> groups = productCustomizationService
                .getDetailedByProductId(request.getProductId());

        Map<Integer, String> groupNamesById = groups.stream()
                .filter(group -> group.getCustomizationId() != null)
                .collect(Collectors.toMap(ProductCustomizationResponseDto::getCustomizationId,
                        group -> group.getName() == null ? "" : group.getName(), (a, b) -> a));

        Set<Integer> permitted = permittedOptionIds(request.getSkuId());
        List<ChosenOption> chosen = resolveChosenOptions(chosenCounts, permitted);
        Map<Integer, PricedCustomizationDto> priced =
                priceChosenOptions(chosen, tierId, quantity, groupNamesById);

        validateChoices(groups, chosen);

        CustomizationPriceResponseDto response = new CustomizationPriceResponseDto();
        response.setProductId(request.getProductId());
        response.setSkuId(request.getSkuId());
        response.setCustomizations(new ArrayList<>(priced.values()));
        response.setTotalAmount(priced.values().stream()
                .map(PricedCustomizationDto::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        return response;
    }

    /**
     * Tally the request into how many units of each option were asked for.
     * <p>
     * A repeated ID is a quantity, not a second choice: the caller sends the option once
     * per unit, so this counts occurrences rather than discarding them. The first
     * occurrence fixes the position of the option in the answer, which is what keeps a
     * receipt listing the choices in the order they were tapped.
     *
     * @param requestedOptionIds the option IDs the caller sent, repeats and all
     * @return units of each option, keyed by option ID, in first-seen order
     */
    private static LinkedHashMap<Integer, Integer> countRequestedOptions(List<Integer> requestedOptionIds) {
        LinkedHashMap<Integer, Integer> counts = new LinkedHashMap<>();
        if (ObjectUtils.isEmpty(requestedOptionIds)) {
            return counts;
        }
        if (requestedOptionIds.size() > MAX_REQUESTED_ENTRIES) {
            throw new BusinessBadRequestException("exception.customizationPrice.tooManyChoices", new Object[]{
                    requestedOptionIds.size(), MAX_REQUESTED_ENTRIES});
        }
        for (Integer optionId : requestedOptionIds) {
            if (optionId == null) {
                continue;
            }
            int units = counts.merge(optionId, 1, Integer::sum);
            if (units > MAX_UNITS_PER_OPTION) {
                throw new BusinessBadRequestException("exception.customizationPrice.tooManyUnits", new Object[]{
                        optionId, units, MAX_UNITS_PER_OPTION});
            }
        }
        return counts;
    }

    /**
     * Rejects a line that cannot be priced at all, rather than answering with an empty
     * set of options that would look like the customer chose nothing.
     *
     * @param request the line being priced
     */
    private void requireProductAndSku(CustomizationPriceRequestDto request) {
        if (request == null || request.getProductId() == null || request.getSkuId() == null) {
            throw new BusinessBadRequestException("exception.customizationPrice.productSkuRequired", null);
        }
    }

    /**
     * A missing quantity means one unit rather than zero. Zero would silently make every
     * option free, which is not a state an order can be sold in.
     *
     * @param quantity the requested quantity, possibly {@code null}
     * @return the quantity to charge for
     */
    private static int resolveQuantity(Integer quantity) {
        return quantity == null || quantity < 1 ? 1 : quantity;
    }

    /**
     * The option IDs linked to a SKU, which is the set a customer is allowed to pick
     * from. A SKU with no links cannot be customised at all.
     *
     * @param skuId the SKU being ordered
     * @return the permitted option IDs
     */
    private Set<Integer> permittedOptionIds(Integer skuId) {
        return skuCustomizationOptionService.getBySkuId(skuId).stream()
                .map(link -> link.getCustomizationOptionId())
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    /**
     * Prices every option the caller chose, in the order they first named them.
     * <p>
     * Only options that exist and are permitted for this SKU reach here; the rest were
     * dropped when the choices were resolved. The unit count each result carries is then
     * checked against the customization's own rules, so a repeated option is priced as a
     * quantity rather than as a free extra.
     *
     * @param chosen         the resolved choices, in first-seen order
     * @param tierId         the operator's price tier, possibly {@code null}
     * @param quantity       how many units of the SKU are being ordered
     * @param groupNamesById customization name per customization ID, for the snapshot
     * @return the priced choices keyed by option ID, in the order the caller gave them
     */
    private Map<Integer, PricedCustomizationDto> priceChosenOptions(
            List<ChosenOption> chosen, Integer tierId, int quantity, Map<Integer, String> groupNamesById) {
        if (chosen.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<Integer, BigDecimal> pricesByOptionId = pricesAtTier(
                chosen.stream().map(choice -> choice.option().getId()).collect(Collectors.toList()), tierId);

        Map<Integer, PricedCustomizationDto> priced = new LinkedHashMap<>();
        for (ChosenOption choice : chosen) {
            CustomizationOption option = choice.option();
            BigDecimal unitPrice = pricesByOptionId.getOrDefault(option.getId(), BigDecimal.ZERO);
            priced.put(option.getId(),
                    toPricedCustomization(option, unitPrice, choice.units(), quantity, groupNamesById));
        }
        return priced;
    }

    /**
     * Reads the option prices at the operator's tier, falling back to every tier when the
     * store has no price tier configured. The fallback matches how product listings are
     * served, so an option is never simply free because a tier is missing.
     *
     * @param optionIds the options being priced
     * @param tierId    the operator's price tier, possibly {@code null}
     * @return the price per option ID
     */
    private Map<Integer, BigDecimal> pricesAtTier(List<Integer> optionIds, Integer tierId) {
        List<CustomizationOptionTierPrice> tierPrices = tierId == null
                ? tierPriceService.selectByOptionIds(optionIds)
                : tierPriceService.selectByOptionIdsAndTierId(optionIds, tierId);
        return tierPrices.stream()
                .filter(price -> price.getCustomizationOptionId() != null)
                .collect(Collectors.toMap(CustomizationOptionTierPrice::getCustomizationOptionId,
                        CustomizationOptionTierPrice::getPrice, (a, b) -> a));
    }

    /**
     * Builds one priced choice, carrying the names so the caller can store them.
     *
     * @param option    the chosen option
     * @param unitPrice the option's price for one unit
     * @param units     how many units of the option the line carries
     * @param lineQuantity how many units of the SKU are being ordered
     * @param groupNamesById customization name per customization ID
     * @return the priced choice
     */
    private PricedCustomizationDto toPricedCustomization(CustomizationOption option, BigDecimal unitPrice,
                                                          int units, int lineQuantity, Map<Integer, String> groupNamesById) {
        PricedCustomizationDto dto = new PricedCustomizationDto();
        dto.setCustomizationOptionId(option.getId());
        dto.setCustomizationId(option.getCustomizationId());
        dto.setCustomizationName(groupNamesById.getOrDefault(option.getCustomizationId(), ""));
        dto.setOptionName(option.getName());
        dto.setPrice(unitPrice);
        dto.setQuantity(units);
        dto.setAmount(unitPrice.multiply(BigDecimal.valueOf(units))
                .multiply(BigDecimal.valueOf(lineQuantity)));
        return dto;
    }

    /**
     * Checks the choices against every customization assigned to the product, using the
     * effective rules: the per product override where one exists, otherwise the
     * customization's own value. The rules themselves live in
     * {@link CustomizationSelectionValidator}, shared with the order validation path.
     *
     * @param groups        the effective customization groups for the product
     * @param chosenOptions the chosen options that survived pricing
     */
    private void validateChoices(List<ProductCustomizationResponseDto> groups,
                             List<ChosenOption> chosenOptions) {
        selectionValidator.validate(groups, chosenOptions.stream()
                .map(choice -> new CustomizationSelectionValidator.SelectedOption(
                        choice.option().getCustomizationId(), choice.units()))
                .collect(Collectors.toList()));
        requireAllowedQuantities(groups, chosenOptions);
    }

    /**
     * Rejects a repeated option whose group does not allow a quantity, mirroring the
     * order validation path: group rule violations are reported first (by the shared
     * validator above) and quantity permission second, so the same basket is accepted
     * or rejected the same way on both paths. A group with no explicit flag is
     * treated as not allowing a quantity.
     */
    private static void requireAllowedQuantities(List<ProductCustomizationResponseDto> groups,
                                                 List<ChosenOption> chosenOptions) {
        // No groups means no flag to consult; nothing here may refuse the repeats,
        // mirroring the shared validator which returns early on an empty group list.
        if (ObjectUtils.isEmpty(groups) || ObjectUtils.isEmpty(chosenOptions)) {
            return;
        }
        Map<Integer, Boolean> allowQuantityByGroup = groups.stream()
                .filter(group -> group.getCustomizationId() != null)
                .collect(Collectors.toMap(ProductCustomizationResponseDto::getCustomizationId,
                        group -> Boolean.TRUE.equals(group.getAllowQuantity()), (a, b) -> a || b));
        for (ChosenOption choice : chosenOptions) {
            if (choice.units() > 1
                    && !allowQuantityByGroup.getOrDefault(choice.option().getCustomizationId(), false)) {
                throw new BusinessBadRequestException("exception.customizationPrice.quantityNotAllowed",
                        new Object[]{choice.option().getId(), choice.units()});
            }
        }
    }

    /**
     * Resolves each chosen option to the catalogue entry it names, so the choice can be
     * priced and counted against its group.
     * <p>
     * Options the SKU does not offer, or that no longer exist, are left out. They were
     * never priced, and counting them would let a client satisfy a required group with an
     * ID that does not exist, or spend a maximum on nothing.
     *
     * @param chosenCounts       units of each chosen option, keyed by option ID
     * @param permittedOptionIds the option IDs this SKU offers
     * @return the chosen options with their catalogue entry and unit count, in first-seen
     *         order
     */
    private List<ChosenOption> resolveChosenOptions(Map<Integer, Integer> chosenCounts,
                                                    Set<Integer> permittedOptionIds) {
        List<Integer> wanted = chosenCounts.keySet().stream()
                .filter(permittedOptionIds::contains)
                .collect(Collectors.toList());
        if (wanted.isEmpty()) {
            return Collections.emptyList();
        }
        return customizationOptionMapper.selectByIds(wanted).stream()
                .filter(option -> option.getId() != null && option.getCustomizationId() != null)
                .filter(option -> permittedOptionIds.contains(option.getId()))
                .map(option -> new ChosenOption(option, chosenCounts.getOrDefault(option.getId(), 1)))
                .collect(Collectors.toList());
    }

    /**
     * One chosen option, the catalogue entry it names, and how many units of it the line
     * carries.
     *
     * @param option the catalogue entry for the chosen option
     * @param units  how many units of it were chosen
     */
    private record ChosenOption(CustomizationOption option, int units) {
    }
}
