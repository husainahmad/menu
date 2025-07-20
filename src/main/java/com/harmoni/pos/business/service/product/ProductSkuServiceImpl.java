package com.harmoni.pos.business.service.product;

import com.harmoni.pos.business.service.product.image.ProductImageService;
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
/**
 * Implementation of {@link ProductSkuService} that handles creation and update of
 * products along with their SKUs and tier pricing information.
 */
@RequiredArgsConstructor
@Service("productSkuService")
@Slf4j
public class ProductSkuServiceImpl implements ProductSkuService {

    private final ProductService productService;
    private final SkuService skuService;
    private final SkuTierPriceService skuTierPriceService;
    private final ProductImageService productImageService;

    /**
     * Creates a product with its SKUs and tier pricing.
     *
     * @param productDto the DTO containing product, SKU, and tier pricing data
     * @return the created product with associated SKUs
     */
    @Override
    public Product create(ProductAddDto productDto) {
        Product product = productService.create(productDto);
        List<Sku> skus = new ArrayList<>();
        productDto.getSkuDtos().forEach(skuDto -> extractSkuAndSave(skuDto, product, skus));

        List<SkuTierPrice> skuTierPrices = new ArrayList<>();
        skus.forEach(sku -> extractedSkuTierPrice(sku, skuTierPrices));

        skuTierPriceService.insetOrUpdateBulk(skuTierPrices);
        product.setSkus(skus);

        productImageService.updateByProductId(product.getId(), productDto.getProductImageEditDto());
        return product;
    }

    /**
     * Updates a product along with its SKUs and tier pricing.
     *
     * @param productEditDto the DTO containing updated product, SKU, and pricing data
     * @return the updated product with SKUs
     */
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

        productImageService.updateByProductId(product.getId(), productEditDto.getProductImageEditDto());
        return product;
    }

    /**
     * Extracts tier price info from SKU and populates the target list.
     *
     * @param sku            the SKU containing tier prices
     * @param skuTierPrices  the list to populate
     */
    private void extractedSkuTierPrice(Sku sku, List<SkuTierPrice> skuTierPrices) {
        sku.getSkuTierPrices().forEach(skuTierPrice -> {
            skuTierPrice.setSkuId(sku.getId());
            skuTierPrices.add(skuTierPrice);
        });
    }

    /**
     * Converts and saves SKU from DTO, and adds it to the SKU list.
     *
     * @param skuDto the SKU DTO
     * @param product the product entity
     * @param skus the list to populate
     */
    private void extractSkuAndSave(SkuDto skuDto, Product product, List<Sku> skus) {
        Sku sku = getSku(skuDto, product);
        skuService.insertOrUpdate(sku);
        skus.add(sku);
    }

    /**
     * Converts SKU from DTO and adds it to the list (no DB operation).
     *
     * @param skuDto the SKU DTO
     * @param product the product entity
     * @param skus the list to populate
     */
    private void extractSku(SkuDto skuDto, Product product, List<Sku> skus) {
        Sku sku = getSku(skuDto, product);
        skus.add(sku);
    }

    /**
     * Converts a SKU DTO to a SKU entity.
     *
     * @param skuDto the SKU DTO
     * @param product the parent product
     * @return the SKU entity
     */
    private static Sku getSku(SkuDto skuDto, Product product) {
        Sku sku = getSkuAddOrEdit(skuDto);
        if (sku == null) {
            throw new IllegalArgumentException("Unable to create SKU from SkuDto");
        }
        sku.setActive(true);
        sku.setProductId(product.getId());
        sku.setSkuTierPrices(getPriceTier(skuDto));
        return sku;
    }

    /**
     * Converts a generic SKU DTO to a SKU based on its actual type.
     *
     * @param skuDto the SKU DTO (add/edit)
     * @return the SKU entity
     */
    private static Sku getSkuAddOrEdit(SkuDto skuDto) {
        if (skuDto instanceof SkuEditDto skuEditDto) {
            return skuEditDto.toSku();
        }
        return ((SkuAddDto) skuDto).toSku();
    }

    /**
     * Retrieves the list of tier prices from a generic SKU DTO.
     *
     * @param skuDto the SKU DTO
     * @return the list of tier prices
     */
    private static List<SkuTierPrice> getPriceTier(SkuDto skuDto) {
        if (skuDto instanceof SkuAddDto skuAddDto) {
            return getPriceTier(skuAddDto);
        }

        if (skuDto instanceof SkuEditDto skuEditDto) {
            return getPriceTier(skuEditDto);
        }

        return List.of();
    }

    /**
     * Extracts tier pricing from a SKU add DTO.
     *
     * @param skuDto the SKU add DTO
     * @return the list of tier prices
     */
    private static List<SkuTierPrice> getPriceTier(SkuAddDto skuDto) {
        List<SkuTierPrice> skuTierPrices = new ArrayList<>();
        skuDto.getSkuTierPriceDtos().forEach(skuTierPriceDto -> {
            SkuTierPrice skuTierPrice = skuTierPriceDto.toSkuTierPrice();
            skuTierPrice.setCreatedAt(new Date(System.currentTimeMillis()));
            skuTierPrices.add(skuTierPrice);
        });
        return skuTierPrices;
    }

    /**
     * Extracts tier pricing from a SKU edit DTO.
     *
     * @param skuDto the SKU edit DTO
     * @return the list of tier prices
     */
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
