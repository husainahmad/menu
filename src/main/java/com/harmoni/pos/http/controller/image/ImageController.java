package com.harmoni.pos.http.controller.image;

import org.springframework.security.access.prepost.PreAuthorize;

import com.harmoni.pos.business.service.product.image.ImgbbUploadService;
import com.harmoni.pos.http.response.RestAPIResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

/**
 * Generic image upload endpoint used by brand and store logos. The image is
 * hosted on ImgBB and only the URL is returned.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/image")
public class ImageController {

    private final ImgbbUploadService imgbbUploadService;

    /**
     * Uploads an image and returns its hosted URL.
     *
     * @param file the image file
     * @return the hosted URL under the {@code url} key
     * @throws IOException when the file cannot be read
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyAuthority('menu:edit', 'catalog:write', 'organization:write', 'rbac:manage')")
    public ResponseEntity<RestAPIResponse> upload(@RequestParam("file") MultipartFile file) throws IOException {
        String url = imgbbUploadService.upload(file.getBytes(), file.getOriginalFilename());
        RestAPIResponse response = RestAPIResponse.builder()
                .data(Map.of("url", url))
                .httpStatus(HttpStatus.CREATED.value())
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
