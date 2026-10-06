package com.harmoni.pos.business.service.customization;

import com.harmoni.pos.exception.BusinessBadRequestException;
import com.harmoni.pos.menu.model.SelectionType;
import com.harmoni.pos.menu.model.dto.product.ProductCustomizationResponseDto;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CustomizationSelectionValidatorTest {

    private final CustomizationSelectionValidator validator = new CustomizationSelectionValidator();

    private static ProductCustomizationResponseDto group(String name, SelectionType selectionType,
                                                         Boolean required, Integer min, Integer max) {
        ProductCustomizationResponseDto group = new ProductCustomizationResponseDto();
        group.setCustomizationId(1);
        group.setName(name);
        group.setSelectionType(selectionType);
        group.setRequired(required);
        group.setMinSelection(min);
        group.setMaxSelection(max);
        return group;
    }

    /**
     * One entry per distinct option, mirroring how both callers build the list: the
     * validation service emits one entry per requested option id, the pricing path one
     * per resolved catalogue option. Units ride along for pricing but never count
     * towards the selection rules.
     */
    private static List<CustomizationSelectionValidator.SelectedOption> options(int... unitsPerOption) {
        List<CustomizationSelectionValidator.SelectedOption> selections = new ArrayList<>();
        for (int units : unitsPerOption) {
            selections.add(new CustomizationSelectionValidator.SelectedOption(1, units));
        }
        return selections;
    }

    private void validate(ProductCustomizationResponseDto group,
                          List<CustomizationSelectionValidator.SelectedOption> selections) {
        validator.validate(List.of(group), selections);
    }

    @Test
    void validate_shouldAcceptASelectionInsideTheBounds() {
        assertDoesNotThrow(() -> validate(
                group("Milk", SelectionType.MULTIPLE, Boolean.TRUE, 1, 3), options(1, 1)));
    }

    @Test
    void validate_shouldRejectAMissingRequiredChoice() {
        BusinessBadRequestException e = assertThrows(BusinessBadRequestException.class,
                () -> validate(group("Milk", SelectionType.SINGLE, Boolean.TRUE, null, null),
                        List.of()));

        assertEquals("exception.customizationPrice.required", e.getMessage());
    }

    @Test
    void validate_shouldRejectTooFewSelections() {
        BusinessBadRequestException e = assertThrows(BusinessBadRequestException.class,
                () -> validate(group("Milk", SelectionType.MULTIPLE, Boolean.FALSE, 2, null),
                        options(1)));

        assertEquals("exception.customizationPrice.belowMinimum", e.getMessage());
    }

    @Test
    void validate_shouldRejectTooManySelections() {
        BusinessBadRequestException e = assertThrows(BusinessBadRequestException.class,
                () -> validate(group("Milk", SelectionType.MULTIPLE, Boolean.FALSE, null, 2),
                        options(1, 1, 1)));

        assertEquals("exception.customizationPrice.aboveMaximum", e.getMessage());
    }

    @Test
    void validate_shouldRejectTwoDistinctOptionsOnASingleSelectGroup() {
        BusinessBadRequestException e = assertThrows(BusinessBadRequestException.class,
                () -> validate(group("Milk", SelectionType.SINGLE, Boolean.FALSE, null, 5),
                        options(1, 1)));

        assertEquals("exception.customizationPrice.singleSelection", e.getMessage());
    }

    @Test
    void validate_shouldNotCountUnitsTowardsTheBounds() {
        // Five units of one topping fill one option slot against a maximum of two.
        assertDoesNotThrow(() -> validate(
                group("Extras", SelectionType.MULTIPLE, Boolean.FALSE, null, 2), options(5)));
    }

    @Test
    void validate_shouldNotLetUnitsSatisfyTheMinimum() {
        // Five units of one topping still leave a minimum of two distinct options unmet.
        BusinessBadRequestException e = assertThrows(BusinessBadRequestException.class,
                () -> validate(group("Extras", SelectionType.MULTIPLE, Boolean.FALSE, 2, null),
                        options(5)));

        assertEquals("exception.customizationPrice.belowMinimum", e.getMessage());
    }

    @Test
    void validate_shouldAcceptAProductWithNoCustomizationsAtAll() {
        assertDoesNotThrow(() -> validator.validate(List.of(), new ArrayList<>()));
    }

    @Test
    void validate_shouldNotCountSelectionsMadeUnderAnotherGroup() {
        ProductCustomizationResponseDto milk = group("Milk", SelectionType.SINGLE, Boolean.FALSE, null, 1);
        milk.setCustomizationId(1);
        ProductCustomizationResponseDto ice = group("Ice", SelectionType.SINGLE, Boolean.FALSE, null, 1);
        ice.setCustomizationId(2);

        assertDoesNotThrow(() -> validator.validate(List.of(milk, ice), List.of(
                new CustomizationSelectionValidator.SelectedOption(1, 1))));
    }
}
