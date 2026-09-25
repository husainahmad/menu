package com.harmoni.pos.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration of the ImgBB image-hosting integration: the upload endpoint
 * and the API key used to authenticate the {@code /1/upload} request.
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "imgbb")
public class ImgbbProperties {

    /** The ImgBB upload endpoint. */
    private String url;

    /** The ImgBB API key used to authenticate uploads. */
    private String apiKey;
}