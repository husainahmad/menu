package com.harmoni.pos.menu.model.dto;

import com.harmoni.pos.menu.model.Service;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Data transfer object for Service.
 * Used for transferring service data between layers.
 */
@Data
public class ServiceDto {

    /**
     * The name of the service.
     */
    @NotBlank(message = "{validation.service.name.NotBlank}")
    @Size(min = 2, max = 45)
    private String name;

    /**
     * Converts this DTO to a Service entity.
     *
     * @return a Service entity with the name set
     */
    public Service toService() {
        return new Service()
                .setName(name);
    }
}
