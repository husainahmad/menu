package com.harmoni.pos.menu.model.dto.kitchen;

import com.harmoni.pos.menu.model.KitchenPrinter;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Create/update payload for a kitchen printer.
 */
@Data
public class KitchenPrinterDto {

    @NotBlank(message = "{validation.kitchenPrinter.name.NotBlank}")
    @Size(max = 125, message = "{validation.kitchenPrinter.name.Size}")
    private String name;

    @NotBlank(message = "{validation.kitchenPrinter.type.NotBlank}")
    private String printerType;

    @NotBlank(message = "{validation.kitchenPrinter.connection.NotBlank}")
    private String connectionConfig;

    @NotBlank(message = "{validation.kitchenPrinter.paper.NotBlank}")
    private String paperWidth;

    private Boolean isPrimary;

    private String status;

    public KitchenPrinter toModel() {
        return new KitchenPrinter()
                .setName(name)
                .setPrinterType(printerType)
                .setConnectionConfig(connectionConfig)
                .setPaperWidth(paperWidth)
                .setIsPrimary(Boolean.TRUE.equals(isPrimary))
                .setStatus(status);
    }
}
