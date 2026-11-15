package com.harmoni.pos.business.service.floor;

import com.harmoni.pos.menu.model.Floor;
import com.harmoni.pos.menu.model.dto.FloorDto;

import java.util.List;

/**
 * Service interface for managing Floor entities: the floors of a store, each of
 * which groups a set of tables.
 */
public interface FloorService {

    /**
     * Creates a new floor.
     *
     * @param floorDto the floor data
     * @return the number of rows inserted
     */
    int create(FloorDto floorDto);

    /**
     * Updates an existing floor.
     *
     * @param id       the floor id
     * @param floorDto the updated floor data
     * @return the number of rows updated
     */
    int update(Integer id, FloorDto floorDto);

    /**
     * Soft deletes a floor. Refused while the floor still has tables.
     *
     * @param id the floor id
     * @return the number of rows updated
     */
    int delete(Integer id);

    /**
     * Retrieves a floor by id.
     *
     * @param id the floor id
     * @return the floor
     */
    Floor get(Integer id);

    /**
     * Lists the floors of a store, ordered by sort order.
     *
     * @param storeId the store id
     * @return the floors of the store
     */
    List<Floor> listByStoreId(Integer storeId);

    /**
     * Applies a new order to the given floors.
     *
     * @param floors floors carrying id and the new sort order
     * @return the number of rows updated
     */
    int reorder(List<FloorDto> floors);
}
