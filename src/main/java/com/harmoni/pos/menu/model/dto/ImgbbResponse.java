package com.harmoni.pos.menu.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * Response payload of the ImgBB {@code /1/upload} endpoint. Only the fields the
 * menu service consumes are mapped; unknown fields are ignored.
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ImgbbResponse {

    private ImgbbData data;
    private boolean success;
    private int status;

    /**
     * The uploaded image details returned by ImgBB.
     */
    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ImgbbData {

        private String id;
        private String title;

        /** Direct link to the hosted image. */
        @JsonProperty("url")
        private String url;

        /** ImgBB viewer page for the hosted image. */
        @JsonProperty("display_url")
        private String displayUrl;

        /** Deletion link, valid while the image has not expired. */
        @JsonProperty("delete_url")
        private String deleteUrl;

        /** Seconds until the image is removed; {@code 0} when it never expires. */
        private int expiration;
    }
}