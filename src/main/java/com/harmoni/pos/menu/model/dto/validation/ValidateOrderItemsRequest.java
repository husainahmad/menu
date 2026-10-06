package com.harmoni.pos.menu.model.dto.validation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * A basket the order service wants checked before it creates the order.
 * <p>
 * The store is named because the answer depends on it: prices are tier scoped, and what a
 * store is allowed to sell is decided by its menu tier. A basket validated against the
 * wrong store would be validated against the wrong menu.
 *
 * @author husainahmad
 */
@Data
public class ValidateOrderItemsRequest {

    /**
     * The store the sale is happening at.
     */
    @NotNull(message = "{validation.orderValidation.storeId.NotNull}")
    private Integer storeId;

    /**
     * The lines being ordered. A basket with no lines has nothing to confirm.
     */
    @NotEmpty(message = "{validation.orderValidation.items.NotEmpty}")
    @Valid
    private List<ValidateOrderItemRequest> items = new ArrayList<>();
}