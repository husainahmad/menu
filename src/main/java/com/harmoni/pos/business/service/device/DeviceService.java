package com.harmoni.pos.business.service.device;

import com.harmoni.pos.menu.model.Device;
import com.harmoni.pos.menu.model.dto.DeviceDto;

import java.util.List;

/**
 * CRUD and lookup operations for store devices.
 */
public interface DeviceService {

    Device create(Integer storeId, DeviceDto dto);

    Device update(Integer id, DeviceDto dto);

    void delete(Integer id);

    Device get(Integer id);

    Device get(Integer storeId, Integer id);

    List<Device> listByStore(Integer storeId);
}
