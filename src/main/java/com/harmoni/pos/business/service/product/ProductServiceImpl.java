package com.harmoni.pos.business.service.product;

import com.harmoni.pos.exception.BusinessBadRequestException;
import com.harmoni.pos.exception.BusinessNoContentRequestException;
import com.harmoni.pos.menu.mapper.ProductMapper;
import com.harmoni.pos.menu.model.Product;
import com.harmoni.pos.menu.model.dto.ProductDto;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

@RequiredArgsConstructor
@Service("productService")
public class ProductServiceImpl implements ProductService {
    private final Logger log = LoggerFactory.getLogger(ProductServiceImpl.class);
    private final ProductMapper productMapper;

    @Override
    public int create(ProductDto productDto) {

        if (!ObjectUtils.isEmpty(productMapper.selectByNameCategoryId(productDto.getName(),
                productDto.getCategoryId()))) {
            throw new BusinessBadRequestException("exception.product.badRequest.duplicate", null);
        }

        int record = productMapper.insert(productDto.toProduct());
        if (record<1) {
            throw new BusinessNoContentRequestException("exception.noContent", null);
        }

        return record;
    }

    @Override
    public List<Product> seelctAll() {
        return productMapper.selectAll();
    }

    @Override
    public List<Product> selectByCategory(Integer categoryId) {
        return productMapper.selectByCategoryId(categoryId);
    }

    @Override
    public List<Product> selectByCategoryBrand(Integer categoryId, Integer brandId) {
        return productMapper.selectByCategoryIdBrandId(categoryId, brandId);
    }

    @Override
    public Product get(Integer id) {

        Product product = productMapper.selectByPrimaryKey(id.intValue());
        if (ObjectUtils.isEmpty(product)) {
            throw new BusinessBadRequestException("exception.product.id.badRequest.notFound", null);
        }

        return product;
    }
}
