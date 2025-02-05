package com.harmoni.pos.menu.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.harmoni.pos.menu.model.ProductImage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.experimental.Accessors;


@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductImageDto {

    @NotNull(message = "{validation.product.id.NotNull}")
    private Integer productId;
    @NotBlank(message = "{validation.product.image.fileName.NotNull}")
    @Size(min = 13, max = 145)
    private String fileName;
    @NotNull(message = "{validation.product.image.byte.NotNull}")
    private byte[] imageBlob;
    @NotBlank(message = "{validation.product.name.NotBlank}")
    private String mimeType;

    public ProductImage toProductImage() {
        return new ProductImage()
                .setProductId(productId)
                .setImageBlob(imageBlob)
                .setFileName(fileName)
                .setMimeType(mimeType);
    }

}