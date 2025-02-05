package com.harmoni.pos.business.service.product.image;

import com.harmoni.pos.exception.BusinessBadRequestException;
import com.harmoni.pos.http.utils.ImageUtils;
import com.harmoni.pos.menu.mapper.ProductImageMapper;
import com.harmoni.pos.menu.model.ProductImage;
import com.harmoni.pos.menu.model.dto.ProductImageDto;
import com.harmoni.pos.menu.model.dto.edit.ProductImageEditDto;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;

@RequiredArgsConstructor
@Service("productImageService")
public class ProductImageServiceImpl implements ProductImageService {

    private final ProductImageMapper productImageMapper;

    @Override
    public int deleteByPrimaryKey(Integer id) {
        return productImageMapper.deleteByPrimaryKey(id);
    }

    @Override
    public ProductImage insert(ProductImageDto productImageDto) throws IOException {
        ProductImage productImage = productImageDto.toProductImage();
        byte[] compressedImage = ImageUtils.compressImage(productImage.getImageBlob());
        productImage.setImageBlob(compressedImage);
        productImageMapper.insert(productImage);
        return productImage;
    }

    @Override
    public ProductImage selectByPrimaryKey(Integer id) {
        ProductImage productImage = productImageMapper.selectByPrimaryKey(id);
        if (ObjectUtils.isEmpty(productImage)) {
            throw new BusinessBadRequestException("exception.product.image.id.badRequest.notFound", null);
        }
        return productImage;
    }

    @Override
    public ProductImage selectByProductId(Integer productId) {
        return productImageMapper.selectByProductKey(productId);
    }

    @Override
    public int updateByProductId(Integer productId, ProductImageEditDto productImageEditDto) {
        if (ObjectUtils.isEmpty(productImageEditDto)) {
            return 0;
        }
        productImageEditDto.setProductId(productId);
        ProductImage productImage = productImageEditDto.toProductImage();
        productImage.setId(productImageEditDto.getId());
        productImage.setUpdatedAt(new Date(System.currentTimeMillis()));
        productImage.setCreatedAt(new Date(System.currentTimeMillis()));

        int row = productImageMapper.updateProductIdByPrimaryKey(productImage);
        if (row==0) {
            return productImageMapper.insert(productImage);
        }

        return row;
    }

    @Override
    public ProductImage updateImageByProductId(Integer productId, ProductImageEditDto productImageEditDto) throws IOException {
        ProductImage productImage = productImageEditDto.toProductImage();
        productImage.setProductId(productId);

        if (ObjectUtils.isNotEmpty(productImageMapper.selectByProductKey(productId))) {
            productImageMapper.updateImageByProductKey(productImage);
            return productImage;
        }

        return insert(productImageEditDto);

    }

    @Override
    public int updateByPrimaryKey(Integer id, ProductImageDto productImageDto) throws IOException {
        ProductImage productImage = productImageDto.toProductImage();
        productImage.setId(id);
        byte[] compressedImage = ImageUtils.compressImage(productImage.getImageBlob());
        productImage.setImageBlob(compressedImage);
        return productImageMapper.updateByPrimaryKey(productImage);
    }
}
