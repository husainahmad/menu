package com.harmoni.pos.menu.model.dto.edit;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class TierMenuEditDto extends TierEditDto {

    @JsonProperty("categories")
    private List<CategoryEditDto> categoryDto;
}
