package com.harmoni.pos.business.service.table;

import com.harmoni.pos.business.service.floor.FloorService;
import com.harmoni.pos.business.service.store.StoreService;
import com.harmoni.pos.exception.BusinessBadRequestException;
import com.harmoni.pos.exception.BusinessNoContentRequestException;
import com.harmoni.pos.exception.BusinessNotFoundRequestException;
import com.harmoni.pos.menu.mapper.TableMapper;
import com.harmoni.pos.menu.model.Floor;
import com.harmoni.pos.menu.model.Table;
import com.harmoni.pos.menu.model.dto.TableDto;
import com.harmoni.pos.menu.model.dto.TableLayoutDto;
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
    private final FloorService floorService;

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
        requireFloorInStore(tableDto);

        if (!ObjectUtils.isEmpty(tableMapper.selectByNameFloorIdStoreId(
                tableDto.getName(), tableDto.getFloorId(), tableDto.getStoreId()))) {
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
        requireFloorInStore(tableDto);

        Table duplicate = tableMapper.selectByNameFloorIdStoreId(
                tableDto.getName(), tableDto.getFloorId(), tableDto.getStoreId());
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

    @Override
    public List<Table> listByFloorId(Integer floorId) {
        return tableMapper.selectByFloorId(floorId);
    }

    @Override
    public int updateLayout(List<TableLayoutDto> layouts) {
        int updated = 0;
        Date now = new Date(System.currentTimeMillis());
        for (TableLayoutDto layout : layouts) {
            Table table = this.get(layout.getId());
            if (layout.getName() != null && !layout.getName().trim().isEmpty()) {
                String name = layout.getName().trim();
                Table duplicate = tableMapper.selectByNameFloorIdStoreId(
                        name, table.getFloorId(), table.getStoreId());
                if (!ObjectUtils.isEmpty(duplicate) && !duplicate.getId().equals(table.getId())) {
                    throw new BusinessBadRequestException("exception.table.name.badRequest.duplicate", null);
                }
                table.setName(name);
            }
            if (layout.getCapacity() != null) {
                table.setCapacity(layout.getCapacity());
            }
            if (layout.getActive() != null) {
                table.setActive(layout.getActive());
            }
            if (layout.getShape() != null) {
                table.setShape(layout.getShape());
            }
            if (layout.getPosX() != null) {
                table.setPosX(layout.getPosX());
            }
            if (layout.getPosY() != null) {
                table.setPosY(layout.getPosY());
            }
            if (layout.getWidth() != null) {
                table.setWidth(layout.getWidth());
            }
            if (layout.getHeight() != null) {
                table.setHeight(layout.getHeight());
            }
            table.setUpdatedAt(now);
            updated += tableMapper.updateByPrimaryKey(table);
        }
        return updated;
    }

    /**
     * Ensures the target floor exists and belongs to the table's store, so a
     * table can never be attached to another store's floor.
     *
     * @param tableDto the table payload carrying storeId and floorId
     */
    private void requireFloorInStore(TableDto tableDto) {
        Floor floor = floorService.get(tableDto.getFloorId());
        if (!ObjectUtils.nullSafeEquals(floor.getStoreId(), tableDto.getStoreId())) {
            throw new BusinessBadRequestException("exception.table.floor.badRequest.notInStore", null);
        }
    }
}