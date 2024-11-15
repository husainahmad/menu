package com.harmoni.pos.menu.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import lombok.Data;

import java.util.List;

@Data
public class TierServiceDto {

    @JsonProperty("tier")
    private @Valid TierDto tierDto;

    @JsonProperty("subServices")
    private List<SubServiceDto> subServiceDtos;

}