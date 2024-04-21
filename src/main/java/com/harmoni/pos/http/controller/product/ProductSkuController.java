package com.harmoni.pos.http.controller.product;

import com.harmoni.pos.business.service.product.ProductService;
import com.harmoni.pos.business.service.sku.SkuService;
import com.harmoni.pos.http.response.RestAPIResponse;
import com.harmoni.pos.menu.model.dto.ProductDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class ProductSkuController {

    private final Logger log = LoggerFactory.getLogger(ProductSkuController.class);
    private final ProductService productService;
    private final SkuService skuService;


    @GetMapping("/product/{id}/sku")
    public ResponseEntity<RestAPIResponse> get(@PathVariable Long id) {

        RestAPIResponse restAPIResponse = RestAPIResponse.builder()
                .httpStatus(HttpStatus.OK.value())
                .data(this.skuService.selectByProductId(id))
                .error(null)
                .build();

        return new ResponseEntity<>(restAPIResponse, HttpStatus.OK);
    }
}
