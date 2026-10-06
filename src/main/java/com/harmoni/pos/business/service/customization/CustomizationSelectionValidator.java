package com.harmoni.pos.business.service.customization;

import com.harmoni.pos.exception.BusinessBadRequestException;
import com.harmoni.pos.menu.model.SelectionType;
import com.harmoni.pos.menu.model.dto.product.ProductCustomizationResponseDto;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Objects;

/**
 * Enforces the selection rules a customization group carries: how many choices it takes,
 * whether one is mandatory, and whether more than one is allowed.
 * <p>
 * The rules are the same however the choices arrived, so they live here rather than in the
 * caller that happened to receive them. Two callers need them and they must not drift: one
 * prices a single line while a basket is being assembled, the other validates the whole
 * basket when the order service confirms it. A group that means "pick exactly one" has to
 * mean it in both places, otherwise the same basket is accepted on one path and rejected
 * on the other.
 * <p>
 * This checks rules only. It does not decide whether a given option is permitted for a
 * SKU, nor what anything costs; the caller resolves those and passes in only the choices
 * it has already accepted.
 *
 * @author husainahmad
 */
@Component
public class CustomizationSelectionValidator {

    /**
     * Checks the accepted choices against every customization assigned to the product,
     * using the effective rules: the per product override where one exists, otherwise the
     * customization's own value.
     * <p>
     * Every rule counts <em>distinct options</em>, never summed quantities: three shots
     * of one topping fill one option slot, not three. Quantities are governed separately
     * by each group's {@code allowQuantity} flag, which the caller enforces. Each entry
     * in {@code selections} is one distinct option, so the count for a group is simply
     * how many entries name it.
     *
     * @param groups     the effective customization groups for the product
     * @param selections the choices that survived the caller's own resolution, one entry
     *                   per distinct option
     * @throws BusinessBadRequestException if any group's rules are not met
     */
    public void validate(List<ProductCustomizationResponseDto> groups,
                         List<SelectedOption> selections) {
        if (ObjectUtils.isEmpty(groups)) {
            return;
        }

        for (ProductCustomizationResponseDto group : groups) {
            Integer groupId = group.getCustomizationId();
            int selectedCount = (int) selections.stream()
                    .filter(selection -> Objects.equals(selection.customizationId(), groupId))
                    .count();
            validateSingleSelection(group, selectedCount);
            validateRequired(group, selectedCount);
            validateBounds(group, selectedCount);
        }
    }

    /**
     * A single-select customization takes one distinct option and no more. Repeating
     * the same option with a quantity is not a second choice; whether that quantity
     * is allowed is the group's {@code allowQuantity} decision, enforced by the caller.
     *
     * @param group         the customization group
     * @param selectedCount how many distinct options were chosen for it
     */
    private static void validateSingleSelection(ProductCustomizationResponseDto group, int selectedCount) {
        if (SelectionType.SINGLE.equals(group.getSelectionType()) && selectedCount > 1) {
            throw new BusinessBadRequestException(
                    "exception.customizationPrice.singleSelection",
                    new Object[]{group.getName(), selectedCount});
        }
    }

    /**
     * A required customization must have something chosen.
     *
     * @param group         the customization group
     * @param selectedCount how many distinct options were chosen for it
     */
    private static void validateRequired(ProductCustomizationResponseDto group, int selectedCount) {
        if (Boolean.TRUE.equals(group.getRequired()) && selectedCount == 0) {
            throw new BusinessBadRequestException(
                    "exception.customizationPrice.required", new Object[]{group.getName()});
        }
    }

    /**
     * Enforces the effective minimum and maximum over distinct options. Both are
     * optional: a group with no bounds simply takes what was chosen, up to the per option
     * ceiling the caller already applied when it tallied the request.
     *
     * @param group         the customization group
     * @param selectedCount how many distinct options were chosen for it
     */
    private static void validateBounds(ProductCustomizationResponseDto group, int selectedCount) {
        Integer minimum = group.getMinSelection();
        if (minimum != null && selectedCount < minimum) {
            throw new BusinessBadRequestException(
                    "exception.customizationPrice.belowMinimum",
                    new Object[]{group.getName(), selectedCount, minimum});
        }
        Integer maximum = group.getMaxSelection();
        if (maximum != null && selectedCount > maximum) {
            throw new BusinessBadRequestException(
                    "exception.customizationPrice.aboveMaximum",
                    new Object[]{group.getName(), selectedCount, maximum});
        }
    }

    /**
     * One accepted choice: the customization group it was made under, and how many
     * units of that option were chosen. Only the group membership counts towards the
     * selection rules; the units drive pricing and the quantity permission check.
     *
     * @param customizationId the group the choice belongs to
     * @param units           how many units of the option were chosen
     */
    public record SelectedOption(Integer customizationId, int units) {
    }
}