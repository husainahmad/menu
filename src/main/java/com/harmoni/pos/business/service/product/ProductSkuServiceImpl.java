package com.harmoni.pos.business.service.product;

import com.harmoni.pos.business.service.sku.SkuService;
import com.harmoni.pos.business.service.skutierprice.SkuTierPriceService;
import com.harmoni.pos.menu.model.Product;
import com.harmoni.pos.menu.model.Sku;
import com.harmoni.pos.menu.model.SkuTierPrice;
import com.harmoni.pos.menu.model.dto.add.ProductAddDto;
import com.harmoni.pos.menu.model.dto.SkuDto;
import com.harmoni.pos.menu.model.dto.add.SkuAddDto;
import com.harmoni.pos.menu.model.dto.edit.ProductEditDto;
import com.harmoni.pos.menu.model.dto.edit.SkuEditDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RequiredArgsConstructor
@Service("productSkuService")
@Slf4j
public class ProductSkuServiceImpl implements ProductSkuService {

    private final ProductService productService;
    private final SkuService skuService;
    private final SkuTierPriceService skuTierPriceService;

    @Override
    public Product create(ProductAddDto productDto) {

        Product product = productService.create(productDto);
        List<Sku> skus = new ArrayList<>();
        productDto.getSkuDtos().forEach(skuDto -> extractSkuAndSave(skuDto, product, skus));

        List<SkuTierPrice> skuTierPrices = new ArrayList<>();
        skus.forEach(sku -> extractedSkuTierPrice(sku, skuTierPrices));
        skuTierPriceService.insetOrUpdateBulk(skuTierPrices);
        product.setSkus(skus);
        return product;
    }

    @Override
    public Product update(ProductEditDto productEditDto) {
        Product product = productService.update(productEditDto);
        List<Sku> skus = new ArrayList<>();
        productEditDto.getSkuDtos().forEach(skuEditDto -> extractSku(skuEditDto, product, skus));

        skuService.updateByIdBulk(skus);
        List<SkuTierPrice> skuTierPrices = new ArrayList<>();
        skus.forEach(sku -> extractedSkuTierPrice(sku, skuTierPrices));
        skuTierPriceService.insetOrUpdateBulk(skuTierPrices);
        product.setSkus(skus);
        return product;
    }

    private void extractedSkuTierPrice(Sku sku, List<SkuTierPrice> skuTierPrices) {
        sku.getSkuTierPrices().forEach(skuTierPrice -> {
            skuTierPrice.setSkuId(sku.getId());
            skuTierPrices.add(skuTierPrice);
        });
    }

    private void extractSkuAndSave(SkuDto skuDto, Product product, List<Sku> skus) {
        Sku sku = getSku(skuDto, product);
        skuService.insertOrUpdate(sku);
        skus.add(sku);
    }

    private void extractSku(SkuDto skuDto, Product product, List<Sku> skus) {
        Sku sku = getSku(skuDto, product);
        skus.add(sku);
    }

    private static Sku getSku(SkuDto skuDto, Product product) {
        Sku sku = getSkuAddOrEdit(skuDto);
        if (sku == null) {
            throw new IllegalArgumentException("Unable to create SKU from SkuDto");
        }
        sku.setActive(true);
        sku.setProductId(product.getId());
        List<SkuTierPrice> skuTierPrices = getPriceTier(skuDto);

        sku.setSkuTierPrices(skuTierPrices);
        return sku;
    }

    private static Sku getSkuAddOrEdit(SkuDto skuDto) {
        if (skuDto instanceof SkuEditDto skuEditDto) {
            return skuEditDto.toSku();
        }
        return ((SkuAddDto) skuDto).toSku();
    }

    private static List<SkuTierPrice> getPriceTier(SkuDto skuDto) {
        if (skuDto instanceof SkuAddDto skuAddDto) {
            return getPriceTier(skuAddDto);
        }

        if (skuDto instanceof SkuEditDto skuEditDto) {
            return getPriceTier(skuEditDto);
        }
        return List.of();
    }

    private static List<SkuTierPrice> getPriceTier(SkuAddDto skuDto) {
        List<SkuTierPrice> skuTierPrices = new ArrayList<>();

        skuDto.getSkuTierPriceDtos().forEach(skuTierPriceDto -> {
            SkuTierPrice skuTierPrice = skuTierPriceDto.toSkuTierPrice();
            skuTierPrice.setCreatedAt(new Date(System.currentTimeMillis()));
            skuTierPrices.add(skuTierPrice);
        });

        return skuTierPrices;
    }

    private static List<SkuTierPrice> getPriceTier(SkuEditDto skuDto) {
        List<SkuTierPrice> skuTierPrices = new ArrayList<>();

        skuDto.getSkuTierPriceDtos().forEach(skuTierPriceEditDto -> {
            SkuTierPrice skuTierPrice = skuTierPriceEditDto.toSkuTierPrice();
            skuTierPrice.setCreatedAt(new Date(System.currentTimeMillis()));
            skuTierPrices.add(skuTierPrice);
        });

        return skuTierPrices;
    }

}
