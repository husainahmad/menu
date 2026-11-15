package com.harmoni.pos.menu.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Nested view of the brand → chain → store hierarchy used by the unified
 * organization page. Each level carries the settings that can be inherited or
 * overridden, plus the counts shown in the tree.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationTreeDto {

    /** All brands, each with its chains and stores. */
    @Builder.Default
    private List<BrandNode> brands = new ArrayList<>();

    /** A brand and its children. */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BrandNode {
        private Integer id;
        private String name;
        private String logoUrl;
        private String timeZone;
        private String currency;
        private String adminEmail;
        private String initialMenuMode;
        private Integer sourceBrandId;
        private int chainCount;
        private int storeCount;
        @Builder.Default
        private List<ChainNode> chains = new ArrayList<>();
    }

    /** A chain and its stores. */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChainNode {
        private Integer id;
        private String name;
        private Integer brandId;
        private String type;
        private String managerEmail;
        private String taxServiceMode;
        private BigDecimal taxPercent;
        private BigDecimal serviceChargePercent;
        private int storeCount;
        @Builder.Default
        private List<StoreNode> stores = new ArrayList<>();
    }

    /** A store with its settings and table count. */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StoreNode {
        private Integer id;
        private String name;
        private Integer chainId;
        private String address;
        private String orderTypes;
        private String taxServiceMode;
        private BigDecimal taxPercent;
        private BigDecimal serviceChargePercent;
        private String receiptLogoMode;
        private String logoUrl;
        private String menuPriceMode;
        private String orderTypeMode;
        private String timeZone;
        private Integer tierMenuId;
        private Integer tierPriceId;
        private Integer tierServiceId;
        private int tableCount;
        private int terminalCount;
    }
}
