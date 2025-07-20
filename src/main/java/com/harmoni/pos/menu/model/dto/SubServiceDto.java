package com.harmoni.pos.menu.model.dto;

import com.harmoni.pos.menu.model.SubService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Data transfer object for SubService.
 * Used for transferring subservice data between layers.
 */
@Data
public class SubServiceDto {

    /**
     * The ID of the subservice.
     */
    private Integer id;

    /**
     * The service ID associated with the subservice.
     */
    @NotNull(message = "{validation.subService.serviceId.NotNull}")
    private Integer serviceId;

    /**
     * The name of the subservice.
     */
    @NotBlank(message = "{validation.subService.name.NotBlank}")
    @Size(min = 2, max = 45)
    private String name;

    /**
     * Converts this DTO to a SubService entity.
     *
     * @return a SubService entity with name and serviceId set
     */
    public SubService toSubService() {
        return new SubService()
                .setName(name)
                .setServiceId(serviceId);
    }
}