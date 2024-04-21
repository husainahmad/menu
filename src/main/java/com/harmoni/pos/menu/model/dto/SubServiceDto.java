package com.harmoni.pos.menu.model.dto;

import com.harmoni.pos.menu.model.SubService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Date;

@Data
public class SubServiceDto {

    @NotNull(message = "{validation.subService.serviceId.NotNull}")
    private Integer serviceId;
    @NotBlank(message = "{validation.subService.name.NotBlank}")
    @Size(min = 2, max = 45)
    private String name;

    public SubService toSubService() {
        return new SubService()
                .setName(name)
                .setServiceId(serviceId);
    }
}