package com.harmoni.pos.business.service.kitchen;

import com.harmoni.pos.exception.BusinessBadRequestException;
import com.harmoni.pos.menu.mapper.CategoryMapper;
import com.harmoni.pos.menu.mapper.KitchenCategoryRoutingMapper;
import com.harmoni.pos.menu.mapper.KitchenProductRoutingMapper;
import com.harmoni.pos.menu.mapper.KitchenStationMapper;
import com.harmoni.pos.menu.mapper.ProductMapper;
import com.harmoni.pos.menu.mapper.StoreMapper;
import com.harmoni.pos.menu.model.KitchenCategoryRouting;
import com.harmoni.pos.menu.model.KitchenProductRouting;
import com.harmoni.pos.menu.model.KitchenStation;
import com.harmoni.pos.menu.model.dto.kitchen.KitchenCategoryRoutingDto;
import com.harmoni.pos.menu.model.dto.kitchen.KitchenProductRoutingDto;
import com.harmoni.pos.menu.model.enums.KitchenStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Default {@link KitchenRoutingService}.
 */
@RequiredArgsConstructor
@Service("kitchenRoutingService")
@Slf4j
public class KitchenRoutingServiceImpl implements KitchenRoutingService {

    private final KitchenCategoryRoutingMapper categoryRoutingMapper;
    private final KitchenProductRoutingMapper productRoutingMapper;
    private final KitchenStationMapper kitchenStationMapper;
    private final StoreMapper storeMapper;
    private final CategoryMapper categoryMapper;
    private final ProductMapper productMapper;

    @Override
    public Map<String, Object> listByStore(Integer storeId) {
        requireStore(storeId);
        Map<String, Object> result = new HashMap<>();
        result.put("categories", categoryRoutingMapper.selectByStoreId(storeId));
        result.put("products", productRoutingMapper.selectByStoreId(storeId));
        return result;
    }

    @Override
    @Transactional(transactionManager = "menuSQLTransactionManager")
    public KitchenCategoryRouting routeCategory(Integer storeId, KitchenCategoryRoutingDto dto) {
        requireStore(storeId);
        if (categoryMapper.selectByPrimaryKey(dto.getCategoryId()) == null) {
            throw new BusinessBadRequestException("exception.kitchenRouting.category.notFound", null);
        }
        requireStation(storeId, dto.getKitchenStationId());

        Date now = new Date();
        KitchenCategoryRouting routing = new KitchenCategoryRouting()
                .setStoreId(storeId)
                .setCategoryId(dto.getCategoryId())
                .setKitchenStationId(dto.getKitchenStationId())
                .setCreatedAt(now)
                .setUpdatedAt(now);

        categoryRoutingMapper.insertOrUpdate(routing);
        return categoryRoutingMapper.selectByStoreIdAndCategoryId(storeId, dto.getCategoryId());
    }

    @Override
    @Transactional(transactionManager = "menuSQLTransactionManager")
    public KitchenProductRouting routeProduct(Integer storeId, KitchenProductRoutingDto dto) {
        requireStore(storeId);
        if (productMapper.selectByPrimaryKey(dto.getProductId()) == null) {
            throw new BusinessBadRequestException("exception.kitchenRouting.product.notFound", null);
        }
        requireStation(storeId, dto.getKitchenStationId());

        Date now = new Date();
        KitchenProductRouting routing = new KitchenProductRouting()
                .setStoreId(storeId)
                .setProductId(dto.getProductId())
                .setKitchenStationId(dto.getKitchenStationId())
                .setCreatedAt(now)
                .setUpdatedAt(now);

        productRoutingMapper.insertOrUpdate(routing);
        return productRoutingMapper.selectByStoreIdAndProductId(storeId, dto.getProductId());
    }

    @Override
    @Transactional(transactionManager = "menuSQLTransactionManager")
    public void deleteCategoryRouting(Integer storeId, Integer categoryId) {
        requireStore(storeId);
        categoryRoutingMapper.deleteByStoreIdAndCategoryId(storeId, categoryId);
    }

    @Override
    @Transactional(transactionManager = "menuSQLTransactionManager")
    public void deleteProductRouting(Integer storeId, Integer productId) {
        requireStore(storeId);
        productRoutingMapper.deleteByStoreIdAndProductId(storeId, productId);
    }

    @Override
    public List<KitchenCategoryRouting> categoryRoutings(Integer storeId) {
        requireStore(storeId);
        return categoryRoutingMapper.selectByStoreId(storeId);
    }

    @Override
    public List<KitchenProductRouting> productRoutings(Integer storeId) {
        requireStore(storeId);
        return productRoutingMapper.selectByStoreId(storeId);
    }

    @Override
    public RoutingTable loadRoutingTable(Integer storeId) {
        Map<Integer, Integer> productStations = productRoutingMapper.selectRoutingByStoreId(storeId)
                .stream()
                .filter(routing -> routing.getProductId() != null && routing.getKitchenStationId() != null)
                .collect(Collectors.toMap(KitchenProductRouting::getProductId,
                        KitchenProductRouting::getKitchenStationId, (first, second) -> second));

        Map<Integer, Integer> categoryStations = categoryRoutingMapper.selectRoutingByStoreId(storeId)
                .stream()
                .filter(routing -> routing.getCategoryId() != null && routing.getKitchenStationId() != null)
                .collect(Collectors.toMap(KitchenCategoryRouting::getCategoryId,
                        KitchenCategoryRouting::getKitchenStationId, (first, second) -> second));

        return new RoutingTable(productStations, categoryStations);
    }

    private void requireStore(Integer storeId) {
        if (storeId == null || storeMapper.selectByPrimaryKey(storeId) == null) {
            throw new BusinessBadRequestException("exception.kitchenStation.store.notFound", null);
        }
    }

    private KitchenStation requireStation(Integer storeId, Integer stationId) {
        KitchenStation station = kitchenStationMapper.selectByIdAndStore(stationId, storeId);
        if (station == null) {
            throw new BusinessBadRequestException("exception.kitchenRouting.station.notFound", null);
        }
        if (!KitchenStatus.ACTIVE.name().equalsIgnoreCase(station.getStatus())) {
            throw new BusinessBadRequestException("exception.kitchenStation.inactive", null);
        }
        return station;
    }
}
