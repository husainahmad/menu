package com.harmoni.pos.business.service.device;

import com.fasterxml.jackson.databind.JsonNode;
import com.harmoni.pos.exception.BusinessBadRequestException;
import com.harmoni.pos.menu.mapper.DeviceMapper;
import com.harmoni.pos.menu.mapper.StoreMapper;
import com.harmoni.pos.menu.mapper.TableMapper;
import com.harmoni.pos.menu.model.Device;
import com.harmoni.pos.menu.model.Store;
import com.harmoni.pos.menu.model.Table;
import com.harmoni.pos.menu.model.dto.DeviceDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeviceServiceImplTest {

    @Mock
    private DeviceMapper deviceMapper;
    @Mock
    private StoreMapper storeMapper;
    @Mock
    private TableMapper tableMapper;
    @InjectMocks
    private DeviceServiceImpl service;

    private Store store;
    private DeviceDto dto;

    @BeforeEach
    void setUp() {
        store = new Store().setId(1);
        dto = new DeviceDto();
        dto.setName("Front POS");
        dto.setAddress("POS-01");
        dto.setDeviceType("TABLET");
        dto.setDeviceMode("CASHIER_POS");
        dto.setConfiguration("{\"payments\":[\"CASH\"]}");
        dto.setStatus("ACTIVE");
    }

    @Test
    void createPersistsTrimmedAddress() {
        dto.setAddress("  POS-02  ");
        when(storeMapper.selectByPrimaryKey(1)).thenReturn(store);
        when(deviceMapper.selectByStoreIdAndName(1, "Front POS")).thenReturn(null);
        when(deviceMapper.selectByAddress("POS-02")).thenReturn(null);
        when(deviceMapper.insert(any(Device.class))).thenReturn(1);

        Device created = service.create(1, dto);

        assertEquals("POS-02", created.getAddress());
        assertEquals("{\"payments\":[\"CASH\"]}", created.getConfiguration());
    }

    @Test
    void createRejectsDuplicateAddressAcrossStores() {
        when(storeMapper.selectByPrimaryKey(1)).thenReturn(store);
        when(deviceMapper.selectByStoreIdAndName(1, "Front POS")).thenReturn(null);
        when(deviceMapper.selectByAddress("POS-01"))
                .thenReturn(new Device().setId(99).setStoreId(99));

        BusinessBadRequestException error = assertThrows(BusinessBadRequestException.class,
                () -> service.create(1, dto));

        assertEquals("exception.device.address.duplicate", error.getMessage());
        verify(deviceMapper, never()).insert(any(Device.class));
    }

    @Test
    void updateAllowsOwnAddress() {
        Device existing = new Device()
                .setId(5)
                .setStoreId(1)
                .setName("Front POS")
                .setAddress("POS-01")
                .setStatus("ACTIVE");
        when(deviceMapper.selectByPrimaryKey(5)).thenReturn(existing);
        when(deviceMapper.selectByStoreIdAndName(1, "Front POS")).thenReturn(existing);
        when(deviceMapper.selectByAddress("POS-01")).thenReturn(existing);
        when(deviceMapper.updateByPrimaryKey(any(Device.class))).thenReturn(1);

        Device updated = service.update(5, dto);

        assertEquals("POS-01", updated.getAddress());
    }

    @Test
    void updateRejectsAnotherDevicesAddress() {
        Device existing = new Device()
                .setId(5)
                .setStoreId(1)
                .setName("Front POS")
                .setAddress("POS-01")
                .setStatus("ACTIVE");
        when(deviceMapper.selectByPrimaryKey(5)).thenReturn(existing);
        when(deviceMapper.selectByStoreIdAndName(1, "Front POS")).thenReturn(existing);
        when(deviceMapper.selectByAddress("POS-01"))
                .thenReturn(new Device().setId(99).setStoreId(99));

        BusinessBadRequestException error = assertThrows(BusinessBadRequestException.class,
                () -> service.update(5, dto));

        assertEquals("exception.device.address.duplicate", error.getMessage());
        verify(deviceMapper, never()).updateByPrimaryKey(any(Device.class));
    }

    @Test
    void tableOrderNormalisesMultipleTables() {
        stubCreate();
        when(tableMapper.selectByStoreId(1)).thenReturn(List.of(
                new Table().setId(1).setStoreId(1),
                new Table().setId(2).setStoreId(1),
                new Table().setId(3).setStoreId(1)));
        when(deviceMapper.insert(any(Device.class))).thenReturn(1);
        dto.setDeviceMode("TABLE_ORDER");
        dto.setConfiguration("{\"tableIds\":[3,1,3]}");

        Device created = service.create(1, dto);

        assertEquals("{\"tableIds\":[1,3]}", created.getConfiguration());
    }

    @Test
    void tableOrderRejectsEmptySelection() {
        stubCreate();
        dto.setDeviceMode("TABLE_ORDER");
        dto.setConfiguration("{\"tableIds\":[]}");

        BusinessBadRequestException error = assertThrows(BusinessBadRequestException.class,
                () -> service.create(1, dto));

        assertEquals("exception.device.config.table", error.getMessage());
    }

    @Test
    void tableOrderRejectsTableFromAnotherStore() {
        stubCreate();
        when(tableMapper.selectByStoreId(1)).thenReturn(List.of(
                new Table().setId(1).setStoreId(1)));
        dto.setDeviceMode("TABLE_ORDER");
        dto.setConfiguration("{\"tableIds\":[1,4]}");

        BusinessBadRequestException error = assertThrows(BusinessBadRequestException.class,
                () -> service.create(1, dto));

        assertEquals("exception.device.config.table", error.getMessage());
    }

    @Test
    void getConfigurationByAddressReturnsJson() {
        when(deviceMapper.selectByAddress("POS-02")).thenReturn(new Device()
                .setId(7)
                .setAddress("POS-02")
                .setDeviceMode("CASHIER_POS")
                .setConfiguration("{\"payments\":[\"CASH\"]}"));

        JsonNode configuration = service.getConfigurationByAddress(" POS-02 ");

        assertEquals("CASH", configuration.get("payments").get(0).asText());
        assertEquals("CASHIER_POS", configuration.get("deviceMode").asText());
    }

    @Test
    void getConfigurationByAddressRejectsBlankAddress() {
        BusinessBadRequestException error = assertThrows(BusinessBadRequestException.class,
                () -> service.getConfigurationByAddress("  "));

        assertEquals("exception.device.address.required", error.getMessage());
        verify(deviceMapper, never()).selectByAddress(anyString());
    }

    @Test
    void getConfigurationByAddressRejectsUnknownAddress() {
        when(deviceMapper.selectByAddress("POS-99")).thenReturn(null);

        BusinessBadRequestException error = assertThrows(BusinessBadRequestException.class,
                () -> service.getConfigurationByAddress("POS-99"));

        assertEquals("exception.device.address.notFound", error.getMessage());
    }

    private void stubCreate() {
        when(storeMapper.selectByPrimaryKey(1)).thenReturn(store);
        when(deviceMapper.selectByStoreIdAndName(1, "Front POS")).thenReturn(null);
        when(deviceMapper.selectByAddress("POS-01")).thenReturn(null);
    }
}
