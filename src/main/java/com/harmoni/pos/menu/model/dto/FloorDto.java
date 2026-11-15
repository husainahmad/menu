package com.harmoni.pos.menu.model.dto;

import com.harmoni.pos.menu.model.Floor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Data transfer object for Floor. Used for transferring floor data between
 * layers.
 */
@Data
public class FloorDto {

    /**
     * The floor id. Set only on reorder requests; the CRUD endpoints take the
     * id from the path.
     */
    private Integer id;

    /**
     * The store ID associated with the floor.
     */
    @NotNull(message = "{validation.floor.storeId.NotNull}")
    private Integer storeId;

    /**
     * The name of the floor.
     */
    @NotBlank(message = "{validation.floor.name.NotBlank}")
    @Size(min = 2, max = 60)
    private String name;

    /**
     * Order in which the floor is listed; defaults to 0.
     */
    private Integer sortOrder;

    /**
     * Whether the floor is active; defaults to true.
     */
    private Boolean active;

    /**
     * Converts this DTO to a Floor entity.
     *
     * @return a Floor entity with storeId, name, sortOrder and active set
     */
    public Floor toFloor() {
        return new Floor()
                .setStoreId(storeId)
                .setName(name)
                .setSortOrder(sortOrder == null ? 0 : sortOrder)
                .setActive(active == null || active);
    }
}
