package com.harmoni.pos.business.service.product;

import com.harmoni.pos.business.service.sku.SkuService;
import com.harmoni.pos.business.service.skutierprice.SkuTierPriceService;
import com.harmoni.pos.menu.model.Product;
import com.harmoni.pos.menu.model.Sku;
import com.harmoni.pos.menu.model.SkuTierPrice;
import com.harmoni.pos.menu.model.dto.ProductDto;
import com.harmoni.pos.menu.model.dto.SkuDto;
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
    public Product create(ProductDto productDto) {

        Product product = productService.create(productDto);
        List<Sku> skus = new ArrayList<>();
        productDto.getSkuDtos().forEach(skuDto -> extractSkuAndSave(skuDto, product, skus));

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
        Sku sku = skuDto.toSku();
        sku.setProductId(product.getId());
        sku.setActive(true);
        sku.setCreatedAt(new Date(System.currentTimeMillis()));
        List<SkuTierPrice> skuTierPrices = new ArrayList<>();

        skuDto.getSkuTierPriceDtos().forEach(skuTierPriceDto -> {
            SkuTierPrice skuTierPrice = skuTierPriceDto.toSkuTierPrice();
            skuTierPrice.setCreatedAt(new Date(System.currentTimeMillis()));
            skuTierPrices.add(skuTierPrice);
        });
        sku.setSkuTierPrices(skuTierPrices);

        skuService.insertOrUpdate(sku);
        skus.add(sku);
    }
}
