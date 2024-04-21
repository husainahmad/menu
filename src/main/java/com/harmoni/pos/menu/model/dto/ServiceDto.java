package com.harmoni.pos.menu.model.dto;

import com.harmoni.pos.menu.model.Service;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ServiceDto {

    @NotBlank(message = "{validation.service.name.NotBlank}")
    @Size(min = 2, max = 45)
    private String name;

    public Service toService() {
        return new Service()
                .setName(name);
    }
}
