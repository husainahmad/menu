package com.harmoni.pos.menu.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.harmoni.pos.menu.model.ProductImage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Data transfer object for Product Image.
 * Used for transferring product image data between layers.
 */
@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductImageDto {

    /**
     * The ID of the product associated with the image.
     */
    @NotNull(message = "{validation.product.id.NotNull}")
    private Integer productId;

    /**
     * The file name of the product image.
     */
    @NotBlank(message = "{validation.product.image.fileName.NotNull}")
    @Size(min = 13, max = 145)
    private String fileName;

    /**
     * The image data as a byte array.
     */
    @NotNull(message = "{validation.product.image.byte.NotNull}")
    private byte[] imageBlob;

    /**
     * The MIME type of the image.
     */
    @NotBlank(message = "{validation.product.name.NotBlank}")
    private String mimeType;

    /**
     * Converts this DTO to a ProductImage entity.
     *
     * @return a ProductImage entity with fields set from this DTO
     */
    public ProductImage toProductImage() {
        return new ProductImage()
                .setProductId(productId)
                .setImageBlob(imageBlob)
                .setFileName(fileName)
                .setMimeType(mimeType);
    }

}