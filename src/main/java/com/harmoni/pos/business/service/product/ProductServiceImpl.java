package com.harmoni.pos.business.service.product;

import com.github.pagehelper.PageInfo;
import com.harmoni.pos.business.service.category.CategoryService;
import com.harmoni.pos.business.service.product.image.ProductImageService;
import com.harmoni.pos.business.service.sku.SkuService;
import com.harmoni.pos.business.service.skutierprice.SkuTierPriceService;
import com.harmoni.pos.business.service.store.tier.StoreTierService;
import com.harmoni.pos.business.service.tier.TierService;
import com.harmoni.pos.business.service.user.UserService;
import com.harmoni.pos.exception.BusinessBadRequestException;
import com.harmoni.pos.http.utils.PaginationUtils;
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
import java.util.*;
import java.util.stream.Collectors;
/**
 * Implementation of the {@link ProductService} that handles product management operations.
 * <p>
 * This class is responsible for creating, updating, retrieving, and deleting products,
 * as well as managing related entities like SKUs and tier-based pricing. It coordinates
 * with various service layers to ensure consistent business logic and data integrity.
 * </p>
 *
 * @author husainahmad
 */
@RequiredArgsConstructor
@Service("productService")
@Slf4j
public class ProductServiceImpl implements ProductService {

    // Injected dependencies for data access and related services
    private final ProductMapper productMapper;
    private final SkuService skuService;
    private final TierService tierService;
    private final SkuTierPriceService skuTierPriceService;
    private final CategoryService categoryService;
    private final ProductImageService productImageService;
    private final UserService userService;
    private final StoreTierService storeTierService;

    /**
     * Creates a new product.
     *
     * @param productDto the DTO containing product information
     * @return the created {@link Product}
     */
    @Override
    public Product create(ProductAddDto productDto) {
        this.selectByNameCategoryId(null, productDto.getName(), productDto.getCategoryId());
        Product product = productDto.toProduct();
        product.setCreatedAt(new Date(System.currentTimeMillis()));
        productMapper.insert(product);
        return product;
    }

    /**
     * Retrieves a list of products in a specific category with pricing based on user's store tier.
     *
     * @param username the username
     * @param categoryId the ID of the category
     * @return list of {@link Product} with pricing information
     */
    @Override
    public List<Product> selectByCategoryPrice(String username, Integer categoryId) {
        User user = userService.selectByUsername(username);
        StoreTier storeTier = storeTierService.selectByStoreId(user.getStoreId());
        return productMapper.selectByCategoryIdPrice(categoryId, storeTier.getTierPriceId());
    }

    /**
     * Retrieves all products in a specific category.
     *
     * @param categoryId the ID of the category
     * @return list of {@link Product}
     */
    @Override
    public List<Product> selectByCategory(Integer categoryId) {
        com.github.pagehelper.PageHelper.clearPage();
        return productMapper.selectByCategoryId(categoryId);
    }

    /**
     * Retrieves paginated list of products filtered by category, brand, and optional search keyword.
     *
     * @param categoryId the ID of the category
     * @param brandId    the ID of the brand
     * @param page       the current page number
     * @param size       the number of records per page
     * @param search     optional search keyword
     * @return paginated map with product data and metadata
     */
    @Override
    public Map<String, Object> selectByCategoryBrand(Integer categoryId, Integer brandId, int page, int size, String search) {
        PaginationUtils.applyPagination(page, size);
        Map<String, Object> paginationData = new HashMap<>();
        List<Product> products = getProducts(categoryId, brandId, search);
        PageInfo<Product> productPageInfo = new PageInfo<>(products);

        productPageInfo.setList(populateSkus(products));

        paginationData.put("page", productPageInfo.getPages());
        paginationData.put("size", productPageInfo.getSize());
        paginationData.put("total", productPageInfo.getTotal());
        paginationData.put("data", productPageInfo.getList());
        paginationData.put("navigate", productPageInfo.getNavigatepageNums());

        return paginationData;
    }

    /**
     * Helper method to get products by category and brand with optional search keyword.
     *
     * @param categoryId the category ID
     * @param brandId    the brand ID
     * @param search     search keyword
     * @return list of products
     */
    private List<Product> getProducts(Integer categoryId, Integer brandId, String search) {
        return productMapper.selectByCategoryIdBrandId(categoryId, brandId, search);
    }

    /**
     * Batch-fetches SKUs for the given products and attaches them.
     *
     * @param products list of products
     * @return list of products with SKUs populated
     */
    private List<Product> populateSkus(List<Product> products) {
        if (ObjectUtils.isEmpty(products)) {
            return products;
        }

        List<Integer> productIds = products.stream()
                .map(Product::getId)
                .filter(id -> !ObjectUtils.isEmpty(id))
                .collect(Collectors.toList());

        if (ObjectUtils.isEmpty(productIds)) {
            return products;
        }

        List<Sku> skus = skuService.selectByProductIds(productIds);
        Map<Integer, List<Sku>> skusByProductId = skus.stream()
                .collect(Collectors.groupingBy(Sku::getProductId));

        products.forEach(product -> product.setSkus(
                skusByProductId.getOrDefault(product.getId(), Collections.emptyList())));

        return products;
    }

    /**
     * Retrieves a single product by its ID.
     *
     * @param id the product ID
     * @return the {@link Product}
     * @throws BusinessBadRequestException if product not found
     */
    @Override
    public Product get(Integer id) {
        Product product = productMapper.selectByPrimaryKey(id);
        if (ObjectUtils.isEmpty(product)) {
            throw new BusinessBadRequestException("exception.product.id.badRequest.notFound", null);
        }
        product.setProductImage(this.productImageService.selectByProductId(id));
        return product;
    }

    /**
     * Retrieves multiple products by a list of IDs and JWT token to validate brand access.
     *
     * @param ids      list of product IDs
     * @param jwtToken JWT token
     * @return list of {@link Product}
     */
    @Override
    public List<Product> getByList(List<Integer> ids, String username) {
        User user = this.userService.selectByUsername(username);
        return productMapper.selectByIds(ids, user.getStore().getChain().getBrandId());
    }

    /**
     * Validates if a product with the given name exists in a category.
     *
     * @param id         optional product ID to exclude from check
     * @param name       the product name
     * @param categoryId the category ID
     * @throws BusinessBadRequestException if a duplicate exists
     */
    @Override
    public void selectByNameCategoryId(Integer id, String name, Integer categoryId) {
        Product product = productMapper.selectByNameCategoryId(name, categoryId);
        if (!ObjectUtils.isEmpty(product) && id == null) {
            throw new BusinessBadRequestException("exception.product.badRequest.duplicate", null);
        }
    }

    /**
     * Updates product SKUs and their corresponding tier-based pricing.
     *
     * @param productId     the product ID
     * @param productSkuDto the DTO containing SKU and tier pricing info
     */
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
        this.skuService.validateSkuName(this.skuService.selectByProductId(product.getId()), skus);
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

    /**
     * Updates product information such as name and category.
     *
     * @param productEditDto DTO containing updated product info
     * @return the updated {@link Product}
     */
    @Override
    public Product update(ProductEditDto productEditDto) {
        this.selectByNameCategoryId(productEditDto.getId(), productEditDto.getName(), productEditDto.getCategoryId());
        Product product = productEditDto.toProduct();
        product.setUpdatedAt(new Date(System.currentTimeMillis()));
        productMapper.updateByPrimaryKey(product);
        return product;
    }

    /**
     * Deletes a product and its related SKUs.
     *
     * @param id the product ID
     * @return number of deleted records
     */
    @Override
    public int delete(Integer id) {
        skuService.deleteSkuByProductId(id);
        return productMapper.deleteByPrimaryKey(id, true, new Date(System.currentTimeMillis()));
    }

    @Override
    public List<Product> searchByProductName(String productName) {
        if (productName == null || productName.isBlank()) return List.of();
        return productMapper.searchByProductName(productName.trim());
    }

    /**
     * Constructs a {@link Sku} object.
     *
     * @param skuId     the SKU ID
     * @param skuName   the name of the SKU
     * @param productId the associated product ID
     * @return constructed {@link Sku}
     */
    private Sku setSku(Integer skuId, String skuName, Integer productId) {
        Sku sku = new Sku();
        sku.setId(skuId <= 0 ? null : skuId);
        sku.setName(skuName);
        sku.setProductId(productId);
        sku.setUpdatedAt(new Date(System.currentTimeMillis()));
        return sku;
    }

    /**
     * Constructs a {@link SkuTierPrice} object.
     *
     * @param skuId       the SKU ID
     * @param tierPriceId the Tier ID
     * @param price       the price for the tier
     * @return constructed {@link SkuTierPrice}
     */
    private SkuTierPrice setTierPrice(Integer skuId, Integer tierPriceId, BigDecimal price) {
        SkuTierPrice skuTierPrice = new SkuTierPrice();
        skuTierPrice.setSkuId(skuId);
        skuTierPrice.setTierId(tierPriceId);
        skuTierPrice.setPrice(price);
        return skuTierPrice;
    }
}
