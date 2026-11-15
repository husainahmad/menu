package com.harmoni.pos.business.service.kitchen;

import com.harmoni.pos.exception.BusinessBadRequestException;
import com.harmoni.pos.menu.mapper.KitchenPrinterMapper;
import com.harmoni.pos.menu.mapper.KitchenStationMapper;
import com.harmoni.pos.menu.mapper.StoreMapper;
import com.harmoni.pos.menu.model.KitchenStation;
import com.harmoni.pos.menu.model.dto.kitchen.KitchenStationDto;
import com.harmoni.pos.menu.model.enums.KitchenStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Default {@link KitchenStationService}. Store, category and product data all live in
 * this service's database, so station configuration needs no cross-service calls.
 */
@RequiredArgsConstructor
@Service("kitchenStationService")
@Slf4j
public class KitchenStationServiceImpl implements KitchenStationService {

    private final KitchenStationMapper kitchenStationMapper;
    private final KitchenPrinterMapper kitchenPrinterMapper;
    private final StoreMapper storeMapper;

    @Override
    @Transactional(transactionManager = "menuSQLTransactionManager")
    public KitchenStation create(Integer storeId, KitchenStationDto dto) {
        requireStore(storeId);
        String code = dto.getCode().trim();
        if (kitchenStationMapper.selectByStoreIdAndCode(storeId, code) != null) {
            throw new BusinessBadRequestException("exception.kitchenStation.code.duplicate", null);
        }

        Date now = new Date();
        KitchenStation station = dto.toModel()
                .setStoreId(storeId)
                .setCode(code)
                .setStatus(KitchenStatus.from(dto.getStatus()).name())
                .setCreatedAt(now)
                .setUpdatedAt(now);

        if (kitchenStationMapper.insert(station) < 1) {
            throw new BusinessBadRequestException("exception.kitchenStation.create.failed", null);
        }
        return station;
    }

    @Override
    @Transactional(transactionManager = "menuSQLTransactionManager")
    public KitchenStation update(Integer id, KitchenStationDto dto) {
        KitchenStation existing = require(id);
        String code = dto.getCode().trim();

        KitchenStation sameCode = kitchenStationMapper.selectByStoreIdAndCode(existing.getStoreId(), code);
        if (sameCode != null && !Objects.equals(sameCode.getId(), id)) {
            throw new BusinessBadRequestException("exception.kitchenStation.code.duplicate", null);
        }

        existing.setName(dto.getName())
                .setCode(code)
                .setDescription(dto.getDescription())
                .setStatus(dto.getStatus() == null
                        ? existing.getStatus() : KitchenStatus.from(dto.getStatus()).name())
                .setUpdatedAt(new Date());

        if (kitchenStationMapper.updateByPrimaryKey(existing) < 1) {
            throw new BusinessBadRequestException("exception.kitchenStation.update.failed", null);
        }
        return existing;
    }

    @Override
    @Transactional(transactionManager = "menuSQLTransactionManager")
    public void delete(Integer id) {
        require(id);
        kitchenStationMapper.deleteByPrimaryKey(id, new Date());
    }

    @Override
    public KitchenStation get(Integer id) {
        KitchenStation station = require(id);
        station.setPrinters(kitchenPrinterMapper.selectByStationId(id));
        return station;
    }

    @Override
    public KitchenStation get(Integer storeId, Integer id) {
        KitchenStation station = kitchenStationMapper.selectByIdAndStore(id, storeId);
        if (station == null) {
            throw new BusinessBadRequestException("exception.kitchenStation.id.notFound", null);
        }
        station.setPrinters(kitchenPrinterMapper.selectByStationId(id));
        return station;
    }

    @Override
    public List<KitchenStation> listByStore(Integer storeId) {
        requireStore(storeId);
        List<KitchenStation> stations = kitchenStationMapper.selectByStoreId(storeId);
        stations.forEach(station -> station.setPrinters(
                kitchenPrinterMapper.selectByStationId(station.getId())));
        return stations;
    }

    private KitchenStation require(Integer id) {
        KitchenStation station = kitchenStationMapper.selectByPrimaryKey(id);
        if (station == null) {
            throw new BusinessBadRequestException("exception.kitchenStation.id.notFound", null);
        }
        return station;
    }

    private void requireStore(Integer storeId) {
        if (storeId == null || storeMapper.selectByPrimaryKey(storeId) == null) {
            throw new BusinessBadRequestException("exception.kitchenStation.store.notFound", null);
        }
    }
}
