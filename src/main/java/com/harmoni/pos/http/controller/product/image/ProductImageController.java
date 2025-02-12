package com.harmoni.pos.http.controller.product.image;

import com.harmoni.pos.business.service.product.image.ProductImageService;
import com.harmoni.pos.http.response.RestAPIResponse;
import com.harmoni.pos.menu.model.ProductImage;
import com.harmoni.pos.menu.model.dto.ProductImageDto;
import com.harmoni.pos.menu.model.dto.edit.ProductImageEditDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/product/image")
@Slf4j
public class ProductImageController {

    private final ProductImageService productImageService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<RestAPIResponse> create(@RequestParam("file") MultipartFile file) throws IOException {

        ProductImageDto productImageDto = new ProductImageDto();
        productImageDto.setFileName(file.getOriginalFilename());
        productImageDto.setMimeType(file.getContentType());
        productImageDto.setImageBlob(file.getBytes());

        ProductImage productImage = productImageService.insert(productImageDto);

        log.debug("Product image created {} ", ObjectUtils.getDisplayString(productImage));
        RestAPIResponse restAPIResponse = RestAPIResponse.builder()
                .data(productImage)
                .build();

        return new ResponseEntity<>(restAPIResponse, HttpStatus.CREATED);
    }

    @PutMapping(value = "/{productId}/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<RestAPIResponse> update(@PathVariable Integer productId,
                                                  @RequestParam("file") MultipartFile file) throws IOException {

        ProductImageEditDto productImageDto = new ProductImageEditDto();
        productImageDto.setProductId(productId);
        productImageDto.setFileName(file.getOriginalFilename());
        productImageDto.setMimeType(file.getContentType());
        productImageDto.setImageBlob(file.getBytes());

        ProductImage productImage = productImageService.updateImageByProductId(productId, productImageDto);

        log.debug("Product image updated {} ", ObjectUtils.getDisplayString(productImage));
        RestAPIResponse restAPIResponse = RestAPIResponse.builder()
                .data(productImage)
                .build();

        return new ResponseEntity<>(restAPIResponse, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestAPIResponse> get(@PathVariable Integer id) {

        RestAPIResponse restAPIResponse = RestAPIResponse.builder()
                .httpStatus(HttpStatus.OK.value())
                .data(this.productImageService.selectByPrimaryKey(id))
                .error(null)
                .build();

        return new ResponseEntity<>(restAPIResponse, HttpStatus.OK);
    }


}
