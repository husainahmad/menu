package com.harmoni.pos.menu.model.dto.add;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.harmoni.pos.menu.model.dto.edit.CategoryEditDto;
import com.harmoni.pos.menu.model.dto.edit.TierEditDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class TierMenuEditDto extends TierEditDto {

    @JsonProperty("categories")
    private List<CategoryEditDto> categoryEditDtos;

}
