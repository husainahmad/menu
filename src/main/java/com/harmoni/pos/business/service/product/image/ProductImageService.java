package com.harmoni.pos.business.service.product.image;

import com.harmoni.pos.menu.model.ProductImage;
import com.harmoni.pos.menu.model.dto.ProductImageDto;
import com.harmoni.pos.menu.model.dto.edit.ProductImageEditDto;

import java.io.IOException;

public interface ProductImageService {
    int deleteByPrimaryKey(Integer id);
    ProductImage insert(ProductImageDto productImageDto) throws IOException;
    ProductImage selectByPrimaryKey(Integer id);
    ProductImage selectByProductId(Integer productId);
    int updateByProductId(Integer productId, ProductImageEditDto productImageEditDto);
    ProductImage updateImageByProductId(Integer productId, ProductImageEditDto productImageEditDto) throws IOException;
    int updateByPrimaryKey(Integer id, ProductImageDto productImageDto) throws IOException;
}
