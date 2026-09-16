package com.harmoni.pos.business.service.table;

import com.harmoni.pos.menu.model.Table;
import com.harmoni.pos.menu.model.dto.TableDto;

import java.util.List;

/**
 * Service interface for managing Table entities.
 *
 * <p>This interface defines the contract for operations related to creating,
 * updating, retrieving, deleting, and listing table data in the POS system.</p>
 */
public interface TableService {

    /**
     * Creates a new table based on the provided data transfer object.
     *
     * @param tableDto the data object containing table information
     * @return the number of rows inserted into the database
     * @throws com.harmoni.pos.exception.BusinessBadRequestException if the store is not found
     * @throws com.harmoni.pos.exception.BusinessNoContentRequestException if the table could not be created
     */
    int create(TableDto tableDto);

    /**
     * Updates an existing table based on the provided data transfer object.
     *
     * @param id       the unique identifier of the table to be updated
     * @param tableDto the data object containing updated table information
     * @return the number of rows updated
     * @throws com.harmoni.pos.exception.BusinessNotFoundRequestException if the table is not found
     */
    int update(Integer id, TableDto tableDto);

    /**
     * Deletes a table by its ID.
     *
     * @param id the unique identifier of the table to be deleted
     * @return the number of rows deleted
     * @throws com.harmoni.pos.exception.BusinessNotFoundRequestException if the table is not found
     */
    int delete(Integer id);

    /**
     * Retrieves a table by its ID.
     *
     * @param id the unique identifier of the table
     * @return the table entity
     * @throws com.harmoni.pos.exception.BusinessNotFoundRequestException if the table does not exist
     */
    Table get(Integer id);

    /**
     * Retrieves a list of all available tables.
     *
     * @return a list of table entities
     */
    List<Table> list();

    /**
     * Retrieves a list of tables associated with a specific store.
     *
     * @param storeId the unique identifier of the store
     * @return a list of table entities under the store
     */
    List<Table> listByStoreId(Integer storeId);
}