package com.harmoni.pos.http.controller.product;

import com.harmoni.pos.business.service.product.ProductService;
import com.harmoni.pos.business.service.product.ProductSkuService;
import com.harmoni.pos.http.response.RestAPIResponse;
import com.harmoni.pos.menu.model.Product;
import com.harmoni.pos.menu.model.dto.add.ProductAddDto;
import com.harmoni.pos.menu.model.dto.edit.ProductEditDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing Product entities.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/product")
@Slf4j
@PreAuthorize("hasAnyAuthority('menu:view', 'menu:edit', 'catalog:read', 'catalog:write', 'rbac:manage')")
public class ProductController {

    private final ProductService productService;
    private final ProductSkuService productSkuService;

    /**
     * Creates a new Product.
     *
     * @param productDto the product data transfer object
     * @return ResponseEntity with creation status
     */
    @PostMapping("")
    @PreAuthorize("hasAnyAuthority('menu:edit', 'catalog:write', 'catalog:delete', 'rbac:manage')")
    public ResponseEntity<RestAPIResponse> create(@Valid @RequestBody ProductAddDto productDto) {
        Product product = productSkuService.create(productDto);
        log.debug("Product created {} ", ObjectUtils.getDisplayString(product));
        return new ResponseEntity<>(RestAPIResponse.builder().build(), HttpStatus.CREATED);
    }

    /**
     * Retrieves a Product by its ID.
     *
     * @param id the product ID
     * @return ResponseEntity containing the product data
     */
    @GetMapping("/{id:\\d+}")
    public ResponseEntity<RestAPIResponse> get(@PathVariable("id") Integer id) {

        RestAPIResponse restAPIResponse = RestAPIResponse.builder()
                .httpStatus(HttpStatus.OK.value())
                .data(this.productService.get(id))
                .error(null)
                .build();

        return new ResponseEntity<>(restAPIResponse, HttpStatus.OK);
    }

    /**
     * Retrieves Products by a list of IDs.
     *
     * @param username the username
     * @param ids the list of product IDs
     * @return ResponseEntity containing the products
     */
    @GetMapping("")
    public ResponseEntity<RestAPIResponse> getByIds(@RequestHeader("X-Username") String username,
                                                    @RequestParam List<Integer> ids) {

        RestAPIResponse restAPIResponse = RestAPIResponse.builder()
                .httpStatus(HttpStatus.OK.value())
                .data(this.productService.getByList(ids, username))
                .error(null)
                .build();

        return new ResponseEntity<>(restAPIResponse, HttpStatus.OK);
    }

    /**
     * Updates an existing Product.
     *
     * @param productEditDto the product edit data transfer object
     * @return ResponseEntity with update status
     */
    @PutMapping("")
    @PreAuthorize("hasAnyAuthority('menu:edit', 'catalog:write', 'catalog:delete', 'rbac:manage')")
    public ResponseEntity<RestAPIResponse> put(@Valid @RequestBody ProductEditDto productEditDto) {
        this.productSkuService.update(productEditDto);

        RestAPIResponse restAPIResponse = RestAPIResponse.builder()
                .httpStatus(HttpStatus.OK.value())
                .data(null)
                .error(null)
                .build();

        return new ResponseEntity<>(restAPIResponse, HttpStatus.OK);
    }

    /**
     * Deletes a Product by its ID.
     *
     * @param id the product ID
     * @return ResponseEntity with deletion status
     */
    @DeleteMapping("/{id:\\d+}")
    @PreAuthorize("hasAnyAuthority('menu:edit', 'catalog:delete', 'rbac:manage')")
    public ResponseEntity<RestAPIResponse> delete(@PathVariable Integer id) {

        this.productService.delete(id);
        RestAPIResponse restAPIResponse = RestAPIResponse.builder()
                .httpStatus(HttpStatus.OK.value())
                .data(null)
                .error(null)
                .build();

        return new ResponseEntity<>(restAPIResponse, HttpStatus.OK);
    }

    /**
     * Retrieves Products by Category ID.
     *
     * @param id the category ID
     * @return ResponseEntity containing products for the category
     */
    @GetMapping("/category/{id}")
    public ResponseEntity<RestAPIResponse> getByCategory(@PathVariable Integer id) {

        RestAPIResponse restAPIResponse = RestAPIResponse.builder()
                .httpStatus(HttpStatus.OK.value())
                .data(this.productService.selectByCategory(id))
                .error(null)
                .build();

        return new ResponseEntity<>(restAPIResponse, HttpStatus.OK);
    }

    /**
     * Retrieves Products by Category ID with price information.
     *
     * @param username the username
     * @param id the category ID
     * @return ResponseEntity containing products with price for the category
     */
    @GetMapping("/category/{id}/price")
    public ResponseEntity<RestAPIResponse> getByCategoryPrice(@RequestHeader("X-Username") String username,
                                                              @PathVariable Integer id) {

        RestAPIResponse restAPIResponse = RestAPIResponse.builder()
                .httpStatus(HttpStatus.OK.value())
                .data(this.productService.selectByCategoryPrice(username, id))
                .error(null)
                .build();

        return new ResponseEntity<>(restAPIResponse, HttpStatus.OK);
    }

    /**
     * Retrieves one cursor page of products by Category ID with price information.
     * <p>
     * Endpoint: GET /api/v1/product/category/{id}/price/page?limit=50&amp;beforeId=120
     * <p>
     * The response is {@code { products, nextCursor, hasMore }}. Pass {@code nextCursor}
     * back as {@code beforeId} to append the next page; {@code hasMore} is false on the
     * last one. The cursor is a product id rather than an offset so an insert cannot make
     * the next page repeat a row.
     *
     * @param username the username
     * @param id       the category id; {@code -1} means every category
     * @param limit    the most products to return; defaults to 50, capped at 200
     * @param beforeId return only products with an id lower than this; omit for the first page
     * @return ResponseEntity containing the product page
     */
    @GetMapping("/category/{id}/price/page")
    public ResponseEntity<RestAPIResponse> getByCategoryPricePage(
            @RequestHeader("X-Username") String username,
            @PathVariable Integer id,
            @RequestParam(value = "limit", required = false) Integer limit,
            @RequestParam(value = "beforeId", required = false) Integer beforeId) {

        RestAPIResponse restAPIResponse = RestAPIResponse.builder()
                .httpStatus(HttpStatus.OK.value())
                .data(this.productService.selectByCategoryPricePage(username, id, beforeId, limit))
                .error(null)
                .build();

        return new ResponseEntity<>(restAPIResponse, HttpStatus.OK);
    }

    /**
     * Retrieves one cursor page of products by Brand and Tier.
     * <p>
     * Endpoint: GET /api/v1/product/brand/{brandId}/tier/{tierId}/cursor?limit=10&amp;beforeId=120&amp;search=latte
     * <p>
     * The response is {@code { products, nextCursor, hasMore }}. Pass {@code nextCursor}
     * back as {@code beforeId} to append the next page; {@code hasMore} is false on the
     * last one.
     *
     * @param brandId  the brand id
     * @param tierId   the tier id used to resolve prices
     * @param limit    the most products to return; defaults to 50, capped at 200
     * @param beforeId return only products with an id lower than this; omit for the first page
     * @param search   optional product-name search term
     * @return ResponseEntity containing the product page
     */
    @GetMapping("/brand/{brandId}/tier/{tierId}/cursor")
    public ResponseEntity<RestAPIResponse> getByBrandTierCursor(
            @PathVariable Integer brandId,
            @PathVariable Integer tierId,
            @RequestParam(value = "limit", required = false) Integer limit,
            @RequestParam(value = "beforeId", required = false) Integer beforeId,
            @RequestParam(value = "search", required = false) String search) {

        RestAPIResponse restAPIResponse = RestAPIResponse.builder()
                .httpStatus(HttpStatus.OK.value())
                .data(this.productService.selectByBrandPricePage(brandId, tierId, beforeId, search, limit))
                .error(null)
                .build();

        return new ResponseEntity<>(restAPIResponse, HttpStatus.OK);
    }

    /**
     * Search products by product name LIKE (for AI).
     * GET /api/v1/product/search?productName=latte
     */
    @GetMapping("/search")
    public ResponseEntity<RestAPIResponse> searchByProductName(@RequestParam String productName) {
        RestAPIResponse restAPIResponse = RestAPIResponse.builder()
                .httpStatus(HttpStatus.OK.value())
                .data(productService.searchByProductName(productName))
                .build();
        return new ResponseEntity<>(restAPIResponse, HttpStatus.OK);
    }

    /**
     * Retrieves Products by Category and Brand with pagination and search.
     *
     * @param categoryId the category ID
     * @param brandId the brand ID
     * @param page the page number
     * @param size the page size
     * @param search the search term
     * @return ResponseEntity containing products for the category and brand
     */
    @GetMapping("/category/{categoryId}/{brandId}")
    public ResponseEntity<RestAPIResponse> getByCategoryBrand(@PathVariable Integer categoryId,
                                                              @PathVariable Integer brandId,
                                                              @RequestParam(name = "page") int page,
                                                              @RequestParam(name = "size") int size,
                                                              @RequestParam(name = "search") String search) {

        RestAPIResponse restAPIResponse = RestAPIResponse.builder()
                .httpStatus(HttpStatus.OK.value())
                .data(this.productService.selectByCategoryBrand(categoryId, brandId, page, size, search))
                .error(null)
                .build();

        return new ResponseEntity<>(restAPIResponse, HttpStatus.OK);
    }

}
