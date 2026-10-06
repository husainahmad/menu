package com.harmoni.pos.menu.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * Entity representing a scope a {@link Promotion} applies to at the tenant,
 * brand, chain or store level.
 * <p>
 * The scope_type determines the meaning of scope_id:
 * - TENANT: scope_id references a tenant
 * - BRAND: scope_id references a brand
 * - CHAIN: scope_id references a chain
 * - STORE: scope_id references a store
 *
 * @author husainahmad
 */
@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PromotionScope {

    /**
     * The unique identifier of the scope.
     */
    private Long id;

    /**
     * The promotion this scope belongs to.
     */
    private Long promotionId;

    /**
     * The scope level, see {@link PromotionScopeType}.
     */
    private PromotionScopeType scopeType;

    /**
     * The scoped entity ID. The meaning depends on {@link #scopeType}.
     */
    private Long scopeId;

    /**
     * The creation timestamp of the scope.
     */
    private Date createdAt;
}