package com.harmoni.pos.menu.model.dto.validation;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * The answer to {@link ValidateOrderItemsRequest}: the same lines, confirmed, with the
 * catalogue data attached.
 * <p>
 * {@code valid} is always {@code true} when a response is returned at all. It is carried
 * because a caller reading the body rather than the status code should not have to infer
 * it, and it leaves room for a future partial answer. A basket that cannot be confirmed is
 * refused outright with the reason, not answered with a flag and a hole in the list: the
 * order service would otherwise have to work out which line was missing.
 *
 * @author husainahmad
 */
@Data
public class ValidateOrderItemsResponseDto {

    /**
     * Whether every line was confirmed.
     */
    private Boolean valid;

    /**
     * The confirmed lines, in the order they were sent.
     */
    private List<ValidatedOrderItemDto> items = new ArrayList<>();
}