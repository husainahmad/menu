package com.harmoni.pos.menu.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * One page of products plus the cursor that asks for the next one.
 * <p>
 * The cursor is the id of the last product on this page rather than an offset. An offset
 * would repeat a row whenever a product was inserted between two requests, because every
 * later row shifts down by one; an id cannot.
 * <p>
 * {@code nextCursor} is {@code null} on the last page. It is only sent when the page came
 * back full: a short page means there is nothing behind it.
 */
@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductPage {

    /**
     * The products on this page, newest id first.
     */
    private List<Product> products;

    /**
     * Pass back as {@code beforeId} to get the next page; {@code null} when this is the last.
     */
    private Integer nextCursor;

    /**
     * Whether another page exists.
     */
    private boolean hasMore;
}
