package com.harmoni.pos.menu.model.dto;

import com.harmoni.pos.menu.model.Device;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Create/update payload for a store device.
 */
@Data
public class DeviceDto {

    @NotBlank(message = "{validation.device.name.NotBlank}")
    @Size(max = 125, message = "{validation.device.name.Size}")
    private String name;

    @NotBlank(message = "{validation.device.address.NotBlank}")
    @Size(max = 125, message = "{validation.device.address.Size}")
    private String address;

    @NotBlank(message = "{validation.device.type.NotBlank}")
    private String deviceType;

    @NotBlank(message = "{validation.device.mode.NotBlank}")
    private String deviceMode;

    @NotBlank(message = "{validation.device.configuration.NotBlank}")
    private String configuration;

    private String status;

    public Device toModel() {
        return new Device()
                .setName(name)
                .setAddress(address)
                .setDeviceType(deviceType)
                .setDeviceMode(deviceMode)
                .setConfiguration(configuration)
                .setStatus(status);
    }
}
