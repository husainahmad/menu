package com.harmoni.pos.menu.model.dto.product;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@Data
@Accessors(chain = true)
public class CustomizationProductPage {

    private List<CustomizationProductDto> products = new ArrayList<>();

    private Integer nextCursor;

    private boolean hasMore;
}
