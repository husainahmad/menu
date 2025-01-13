package com.harmoni.pos.menu.model.dto.add;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.harmoni.pos.menu.model.dto.edit.CategoryEditDto;
import com.harmoni.pos.menu.model.dto.edit.TierEditDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TierMenuEditDto {
    @NotNull(message = "{validation.tier.NotNull}")
    @JsonProperty("tier")
    private @Valid TierEditDto tierDto;

    @NotNull(message = "{validation.category.NotNull}")
    @JsonProperty("category")
    private CategoryEditDto categoryDto;
    private Boolean active;
}
