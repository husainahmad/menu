package com.harmoni.pos.business.service.table;

import com.harmoni.pos.business.service.store.StoreService;
import com.harmoni.pos.exception.BusinessBadRequestException;
import com.harmoni.pos.exception.BusinessNoContentRequestException;
import com.harmoni.pos.exception.BusinessNotFoundRequestException;
import com.harmoni.pos.menu.mapper.TableMapper;
import com.harmoni.pos.menu.model.Table;
import com.harmoni.pos.menu.model.dto.TableDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Date;
import java.util.List;

/**
 * Implementation of the {@link TableService} interface that handles business logic
 * related to table management.
 *
 * <p>This service provides functionality for creating, updating, deleting, retrieving,
 * and listing tables, and ensures that domain-level validations are enforced.</p>
 */
@RequiredArgsConstructor
@Service("tableService")
@Slf4j
public class TableServiceImpl implements TableService {

    private final TableMapper tableMapper;
    private final StoreService storeService;

    /**
     * Creates a new table.
     *
     * @param tableDto the DTO containing the table data to create
     * @return number of rows affected in the database
     * @throws BusinessBadRequestException if the store is not found or the table name already exists in the store
     * @throws BusinessNoContentRequestException if insert operation fails
     */
    @Override
    public int create(TableDto tableDto) {
        storeService.get(tableDto.getStoreId());

        if (!ObjectUtils.isEmpty(tableMapper.selectByNameStoreId(tableDto.getName(), tableDto.getStoreId()))) {
            throw new BusinessBadRequestException("exception.table.name.badRequest.duplicate", null);
        }

        Table table = tableDto.toTable();
        table.setCreatedAt(new Date(System.currentTimeMillis()));

        int inserted = tableMapper.insert(table);
        if (inserted < 1) {
            throw new BusinessNoContentRequestException(
                    BusinessNoContentRequestException.NO_CONTENT, null);
        }

        return inserted;
    }

    /**
     * Updates an existing table.
     *
     * @param id       the ID of the table to update
     * @param tableDto the DTO containing the updated table data
     * @return number of rows affected in the database
     * @throws BusinessNotFoundRequestException if the table is not found
     * @throws BusinessBadRequestException if the store is not found or the table name already exists in the store
     */
    @Override
    public int update(Integer id, TableDto tableDto) {
        Table existing = this.get(id);
        storeService.get(tableDto.getStoreId());

        Table duplicate = tableMapper.selectByNameStoreId(tableDto.getName(), tableDto.getStoreId());
        if (!ObjectUtils.isEmpty(duplicate) && !duplicate.getId().equals(existing.getId())) {
            throw new BusinessBadRequestException("exception.table.name.badRequest.duplicate", null);
        }

        Table table = tableDto.toTable();
        table.setId(existing.getId());
        table.setUpdatedAt(new Date(System.currentTimeMillis()));

        int updated = tableMapper.updateByPrimaryKey(table);
        if (updated < 1) {
            throw new BusinessNoContentRequestException(
                    BusinessNoContentRequestException.NO_CONTENT, null);
        }

        return updated;
    }

    /**
     * Soft deletes a table by ID.
     *
     * @param id the table ID to delete
     * @return number of rows affected
     * @throws BusinessNotFoundRequestException if the table is not found
     */
    @Override
    public int delete(Integer id) {
        Table table = this.get(id);
        table.setDeletedAt(new Date(System.currentTimeMillis()));
        return tableMapper.deleteByPrimaryKey(table);
    }

    /**
     * Retrieves a table by ID.
     *
     * @param id the table ID
     * @return the table entity
     * @throws BusinessNotFoundRequestException if the table is not found
     */
    @Override
    public Table get(Integer id) {
        Table table = tableMapper.selectByPrimaryKey(id);
        if (ObjectUtils.isEmpty(table)) {
            throw new BusinessNotFoundRequestException(
                    "exception.table.id.badRequest.notFound", null);
        }
        return table;
    }

    /**
     * Retrieves a list of all tables.
     *
     * @return a list of {@link Table} entities
     */
    @Override
    public List<Table> list() {
        return tableMapper.selectAll();
    }

    /**
     * Retrieves a list of tables associated with a specific store.
     *
     * @param storeId the store ID
     * @return a list of {@link Table} entities under the store
     */
    @Override
    public List<Table> listByStoreId(Integer storeId) {
        return tableMapper.selectByStoreId(storeId);
    }
}