package com.harmoni.pos.business.service.floor;

import com.harmoni.pos.business.service.store.StoreService;
import com.harmoni.pos.exception.BusinessBadRequestException;
import com.harmoni.pos.exception.BusinessNoContentRequestException;
import com.harmoni.pos.exception.BusinessNotFoundRequestException;
import com.harmoni.pos.menu.mapper.FloorMapper;
import com.harmoni.pos.menu.mapper.TableMapper;
import com.harmoni.pos.menu.model.Floor;
import com.harmoni.pos.menu.model.dto.FloorDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Date;
import java.util.List;

/**
 * Implementation of {@link FloorService}. Enforces that a floor belongs to an
 * existing store, that its name is unique within the store, and that a floor
 * with tables cannot be deleted.
 */
@RequiredArgsConstructor
@Service("floorService")
@Slf4j
public class FloorServiceImpl implements FloorService {

    private final FloorMapper floorMapper;
    private final TableMapper tableMapper;
    private final StoreService storeService;

    @Override
    public int create(FloorDto floorDto) {
        storeService.get(floorDto.getStoreId());

        if (!ObjectUtils.isEmpty(floorMapper.selectByNameStoreId(floorDto.getName(), floorDto.getStoreId()))) {
            throw new BusinessBadRequestException("exception.floor.name.badRequest.duplicate", null);
        }

        Floor floor = floorDto.toFloor();
        floor.setCreatedAt(new Date(System.currentTimeMillis()));

        int inserted = floorMapper.insert(floor);
        if (inserted < 1) {
            throw new BusinessNoContentRequestException(
                    BusinessNoContentRequestException.NO_CONTENT, null);
        }
        return inserted;
    }

    @Override
    public int update(Integer id, FloorDto floorDto) {
        Floor existing = this.get(id);
        storeService.get(floorDto.getStoreId());

        Floor duplicate = floorMapper.selectByNameStoreId(floorDto.getName(), floorDto.getStoreId());
        if (!ObjectUtils.isEmpty(duplicate) && !duplicate.getId().equals(existing.getId())) {
            throw new BusinessBadRequestException("exception.floor.name.badRequest.duplicate", null);
        }

        Floor floor = floorDto.toFloor();
        floor.setId(existing.getId());
        floor.setUpdatedAt(new Date(System.currentTimeMillis()));

        int updated = floorMapper.updateByPrimaryKey(floor);
        if (updated < 1) {
            throw new BusinessNoContentRequestException(
                    BusinessNoContentRequestException.NO_CONTENT, null);
        }
        return updated;
    }

    @Override
    public int delete(Integer id) {
        Floor floor = this.get(id);
        if (tableMapper.countByFloorId(id) > 0) {
            throw new BusinessBadRequestException("exception.floor.delete.badRequest.hasTables", null);
        }
        floor.setDeletedAt(new Date(System.currentTimeMillis()));
        return floorMapper.deleteByPrimaryKey(floor);
    }

    @Override
    public Floor get(Integer id) {
        Floor floor = floorMapper.selectByPrimaryKey(id);
        if (ObjectUtils.isEmpty(floor)) {
            throw new BusinessNotFoundRequestException(
                    "exception.floor.id.badRequest.notFound", null);
        }
        return floor;
    }

    @Override
    public List<Floor> listByStoreId(Integer storeId) {
        return floorMapper.selectByStoreId(storeId);
    }

    @Override
    public int reorder(List<FloorDto> floors) {
        int updated = 0;
        for (FloorDto dto : floors) {
            Floor floor = this.get(dto.getId());
            floor.setSortOrder(dto.getSortOrder() == null ? 0 : dto.getSortOrder());
            floor.setUpdatedAt(new Date(System.currentTimeMillis()));
            updated += floorMapper.updateByPrimaryKey(floor);
        }
        return updated;
    }
}
