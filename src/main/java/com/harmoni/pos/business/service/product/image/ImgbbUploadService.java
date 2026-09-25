package com.harmoni.pos.business.service.product.image;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.harmoni.pos.config.ImgbbProperties;
import com.harmoni.pos.exception.BusinessBadRequestException;
import com.harmoni.pos.menu.model.dto.ImgbbResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Base64;

/**
 * Uploads product images to the ImgBB image-hosting service and returns the
 * hosted URL, so product image bytes are never persisted by the menu service.
 *
 * <p>Sends the image bytes base64-encoded in an
 * {@code application/x-www-form-urlencoded} POST and authenticates with the
 * ImgBB API key.</p>
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class ImgbbUploadService {

    public static final String EXCEPTION_IMAGE_UPLOAD_FAILED = "exception.product.image.upload.failed";

    private static final String IMGBB_HOST = "https://i.ibb.co/";
    private static final String IMGBB_HOST_WORKING = "https://i.ibb.co.com/";

    private final WebClient webClient = WebClient.builder().build();
    private final ImgbbProperties imgbbProperties;
    private final ObjectMapper objectMapper;

    /**
     * Uploads image bytes to ImgBB.
     *
     * @param imageBytes the image bytes to host
     * @param fileName   an optional file name sent to ImgBB
     * @return the hosted image URL
     * @throws BusinessBadRequestException when ImgBB rejects or fails the upload
     */
    public String upload(byte[] imageBytes, String fileName) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("image", Base64.getEncoder().encodeToString(imageBytes));
        form.add("key", imgbbProperties.getApiKey());
        form.add("name", fileName);

        log.debug("Uploading product image to ImgBB {}", imgbbProperties.getUrl());
        String rawBody = webClient.post()
                .uri(imgbbProperties.getUrl())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(form))
                .exchangeToMono(clientResponse -> clientResponse.bodyToMono(String.class)
                        .defaultIfEmpty(""))
                .block();

        log.debug("ImgBB upload raw response: {}", rawBody);

        ImgbbResponse response;
        try {
            response = objectMapper.readValue(rawBody, ImgbbResponse.class);
        } catch (JsonProcessingException e) {
            log.warn("ImgBB upload returned an unparsable response body", e);
            throw new BusinessBadRequestException(EXCEPTION_IMAGE_UPLOAD_FAILED, null);
        }

        if (response == null || !response.isSuccess() || response.getData() == null
                || response.getData().getUrl() == null || response.getData().getUrl().isBlank()) {
            log.warn("ImgBB upload failed response={}", rawBody);
            throw new BusinessBadRequestException(EXCEPTION_IMAGE_UPLOAD_FAILED, null);
        }
        log.debug("ImgBB upload succeeded url={}", response.getData().getUrl());
        return toWorkingLink(response.getData().getUrl());
    }

    /**
     * Rewrites the returned ImgBB host to the currently serving CDN host.
     *
     * @param url the direct link returned by ImgBB
     * @return the direct link on a host that resolves and serves the image
     */
    private String toWorkingLink(String url) {
        if (url == null || !url.startsWith(IMGBB_HOST)) {
            return url;
        }
        String workingUrl = IMGBB_HOST_WORKING + url.substring(IMGBB_HOST.length());
        log.debug("Rewrote ImgBB host {} to {}", url, workingUrl);
        return workingUrl;
    }
}