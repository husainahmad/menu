package com.harmoni.pos.business.service.product;

import com.harmoni.pos.business.service.category.CategoryService;
import com.harmoni.pos.business.service.sku.SkuService;
import com.harmoni.pos.business.service.skutierprice.SkuTierPriceService;
import com.harmoni.pos.business.service.tier.TierService;
import com.harmoni.pos.exception.BusinessBadRequestException;
import com.harmoni.pos.menu.mapper.*;
import com.harmoni.pos.menu.model.*;
import com.harmoni.pos.menu.model.dto.add.ProductAddDto;
import com.harmoni.pos.menu.model.dto.ProductSkuDto;
import com.harmoni.pos.menu.model.dto.ProductSkuTierDto;
import com.harmoni.pos.menu.model.dto.edit.ProductEditDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RequiredArgsConstructor
@Service("productService")
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductMapper productMapper;
    private final SkuService skuService;
    private final TierService tierService;
    private final SkuTierPriceService skuTierPriceService;
    private final CategoryService categoryService;

    @Override
    public Product create(ProductAddDto productDto) {

        this.selectByNameCategoryId(null, productDto.getName(), productDto.getCategoryId());
        Product product = productDto.toProduct();
        product.setCreatedAt(new Date(System.currentTimeMillis()));
        productMapper.insert(product);

        return product;
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

        Product product = productMapper.selectByPrimaryKey(id);
        if (ObjectUtils.isEmpty(product)) {
            throw new BusinessBadRequestException("exception.product.id.badRequest.notFound", null);
        }

        return product;
    }

    @Override
    public void selectByNameCategoryId(Integer id, String name, Integer categoryId) {
        Product product = productMapper.selectByNameCategoryId(name, categoryId);

        if (!ObjectUtils.isEmpty(product) && id==null) {
            throw new BusinessBadRequestException("exception.product.badRequest.duplicate", null);
        }
    }

    @Override
    public void updateProductSku(Integer productId, ProductSkuDto productSkuDto) {
        Product product = this.get(productId);
        boolean isIgnoreUpdateProduct = false;

        Category category = this.categoryService.get(productSkuDto.getCategoryId());

        if (product.getName().equals(productSkuDto.getName()) &&
            product.getCategoryId().equals(productSkuDto.getCategoryId())) {
            isIgnoreUpdateProduct = true;
        }

        if (!isIgnoreUpdateProduct) {
            product.setName(productSkuDto.getName());
            product.setCategoryId(category.getId());
            product.setUpdatedAt(new Date(System.currentTimeMillis()));
            this.productMapper.updateByPrimaryKey(product);
        }

        List<Integer> skuIds = new ArrayList<>();
        List<Sku> skus = new ArrayList<>();
        List<Integer> tierIds = new ArrayList<>();

        Sku sku;
        for (ProductSkuTierDto skuDto : productSkuDto.getSkus()) {
            skuIds.add(skuDto.getId());
            sku = setSku(skuDto.getId(), skuDto.getName(), productSkuDto.getId());
            skus.add(sku);
            tierIds.add(skuDto.getTierPrice().getId());
        }

        this.skuService.compareListSkus(skus, skuIds);
        this.skuService.validateSkuName(
                this.skuService.selectByProductId(product.getId()), skus);

        this.skuService.updateBulk(skus);
        this.skuService.setSkuIdInListSkus(skus, this.skuService.selectByProductId(productId));
        this.tierService.validateTierByIds(tierIds);

        List<SkuTierPrice> skuTierPrices = new ArrayList<>();
        for (ProductSkuTierDto skuDto : productSkuDto.getSkus()) {
            sku = skus.stream().filter(s -> s.getName().equals(skuDto.getName())).findAny().orElse(null);
            if (sku != null) {
                skuTierPrices.add(setTierPrice(sku.getId(), skuDto.getTierPrice().getId(),
                        skuDto.getTierPrice().getPrice()));
            }
        }

        this.skuTierPriceService.insetOrUpdateBulk(skuTierPrices);

    }

    @Override
    public Product update(ProductEditDto productEditDto) {
        this.selectByNameCategoryId(productEditDto.getId(), productEditDto.getName(), productEditDto.getCategoryId());
        Product product = productEditDto.toProduct();
        product.setUpdatedAt(new Date(System.currentTimeMillis()));
        productMapper.updateByPrimaryKey(product);
        return product;
    }

    @Override
    public int delete(Integer id) {
        skuService.deleteSkuByProductId(id);
        return productMapper.deleteByPrimaryKey(id, true, new Date(System.currentTimeMillis()));
    }

    private Sku setSku(Integer skuId, String skuName, Integer productId) {
        Sku sku = new Sku();
        sku.setId(skuId<=0 ? null : skuId);
        sku.setName(skuName);
        sku.setProductId(productId);
        sku.setUpdatedAt(new Date(System.currentTimeMillis()));
        return sku;
    }

    private SkuTierPrice setTierPrice(Integer skuId, Integer tierPriceId, BigDecimal price) {
        SkuTierPrice skuTierPrice = new SkuTierPrice();
        skuTierPrice.setSkuId(skuId);
        skuTierPrice.setTierId(tierPriceId);
        skuTierPrice.setPrice(price);
        return skuTierPrice;
    }

}
