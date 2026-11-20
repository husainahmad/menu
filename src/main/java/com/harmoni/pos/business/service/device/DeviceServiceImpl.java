package com.harmoni.pos.business.service.device;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.harmoni.pos.exception.BusinessBadRequestException;
import com.harmoni.pos.menu.mapper.DeviceMapper;
import com.harmoni.pos.menu.mapper.StoreMapper;
import com.harmoni.pos.menu.mapper.TableMapper;
import com.harmoni.pos.menu.model.Device;
import com.harmoni.pos.menu.model.Store;
import com.harmoni.pos.menu.model.Table;
import com.harmoni.pos.menu.model.dto.DeviceDto;
import com.harmoni.pos.menu.model.enums.DeviceMode;
import com.harmoni.pos.menu.model.enums.DeviceStatus;
import com.harmoni.pos.menu.model.enums.DeviceType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * Default {@link DeviceService}.
 *
 * <p>Store devices are pure configuration: there is no credential or runtime
 * identity attached. The mode decides the shape of the configuration JSON, and
 * every payload is validated here, so a typo is rejected while the operator is
 * still on the form instead of surfacing later at the terminal.</p>
 */
@RequiredArgsConstructor
@Service("deviceService")
@Slf4j
public class DeviceServiceImpl implements DeviceService {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    /** Payment methods understood by the order service. */
    private static final Set<String> PAYMENTS = Set.of("CASH", "CARD", "QR");

    /** Order types understood by the store settings. */
    private static final Set<String> ORDER_TYPES = Set.of("DINE_IN", "TAKEAWAY", "DELIVERY");

    private final DeviceMapper deviceMapper;
    private final StoreMapper storeMapper;
    private final TableMapper tableMapper;

    @Override
    @Transactional(transactionManager = "menuSQLTransactionManager")
    public Device create(Integer storeId, DeviceDto dto) {
        requireStore(storeId);
        String name = dto.getName().trim();
        if (deviceMapper.selectByStoreIdAndName(storeId, name) != null) {
            throw new BusinessBadRequestException("exception.device.name.duplicate", null);
        }
        String address = dto.getAddress().trim();
        if (deviceMapper.selectByStoreIdAndAddress(storeId, address) != null) {
            throw new BusinessBadRequestException("exception.device.address.duplicate", null);
        }

        DeviceMode mode = parseMode(dto.getDeviceMode());
        String configuration = validatedConfiguration(mode, dto.getConfiguration(), storeId);

        Date now = new Date();
        Device device = dto.toModel()
                .setStoreId(storeId)
                .setName(name)
                .setAddress(address)
                .setDeviceType(parseType(dto.getDeviceType()).name())
                .setDeviceMode(mode.name())
                .setConfiguration(configuration)
                .setStatus(DeviceStatus.from(dto.getStatus()).name())
                .setCreatedAt(now)
                .setUpdatedAt(now);

        if (deviceMapper.insert(device) < 1) {
            throw new BusinessBadRequestException("exception.device.create.failed", null);
        }
        return device;
    }

    @Override
    @Transactional(transactionManager = "menuSQLTransactionManager")
    public Device update(Integer id, DeviceDto dto) {
        Device existing = require(id);
        String name = dto.getName().trim();

        Device sameName = deviceMapper.selectByStoreIdAndName(existing.getStoreId(), name);
        if (sameName != null && !Objects.equals(sameName.getId(), id)) {
            throw new BusinessBadRequestException("exception.device.name.duplicate", null);
        }
        String address = dto.getAddress().trim();
        Device sameAddress = deviceMapper.selectByStoreIdAndAddress(existing.getStoreId(), address);
        if (sameAddress != null && !Objects.equals(sameAddress.getId(), id)) {
            throw new BusinessBadRequestException("exception.device.address.duplicate", null);
        }

        DeviceMode mode = parseMode(dto.getDeviceMode());
        String configuration = validatedConfiguration(mode, dto.getConfiguration(), existing.getStoreId());

        existing.setName(name)
                .setAddress(address)
                .setDeviceType(parseType(dto.getDeviceType()).name())
                .setDeviceMode(mode.name())
                .setConfiguration(configuration)
                .setStatus(dto.getStatus() == null
                        ? existing.getStatus() : DeviceStatus.from(dto.getStatus()).name())
                .setUpdatedAt(new Date());

        if (deviceMapper.updateByPrimaryKey(existing) < 1) {
            throw new BusinessBadRequestException("exception.device.update.failed", null);
        }
        return existing;
    }

    @Override
    @Transactional(transactionManager = "menuSQLTransactionManager")
    public void delete(Integer id) {
        require(id);
        deviceMapper.deleteByPrimaryKey(id, new Date());
    }

    @Override
    public Device get(Integer id) {
        return require(id);
    }

    @Override
    public Device get(Integer storeId, Integer id) {
        Device device = deviceMapper.selectByPrimaryKey(id);
        if (device == null || !Objects.equals(device.getStoreId(), storeId)) {
            throw new BusinessBadRequestException("exception.device.id.notFound", null);
        }
        return device;
    }

    @Override
    public List<Device> listByStore(Integer storeId) {
        requireStore(storeId);
        return deviceMapper.selectByStoreId(storeId);
    }

    private Device require(Integer id) {
        Device device = deviceMapper.selectByPrimaryKey(id);
        if (device == null) {
            throw new BusinessBadRequestException("exception.device.id.notFound", null);
        }
        return device;
    }

    private void requireStore(Integer storeId) {
        if (storeId == null) {
            throw new BusinessBadRequestException("exception.device.store.notFound", null);
        }
        Store store = storeMapper.selectByPrimaryKey(storeId);
        if (store == null) {
            throw new BusinessBadRequestException("exception.device.store.notFound", null);
        }
    }

    private static DeviceType parseType(String value) {
        try {
            return DeviceType.from(value);
        } catch (IllegalArgumentException e) {
            throw new BusinessBadRequestException("exception.device.type.invalid", null);
        }
    }

    private static DeviceMode parseMode(String value) {
        try {
            return DeviceMode.from(value);
        } catch (IllegalArgumentException e) {
            throw new BusinessBadRequestException("exception.device.mode.invalid", null);
        }
    }

    /**
     * Validates and normalises the mode-specific JSON configuration.
     *
     * @param mode     the device mode
     * @param raw      the submitted JSON
     * @param storeId  the device's store, used to check a table reference
     * @return the normalised JSON string that is stored
     */
    private String validatedConfiguration(DeviceMode mode, String raw, Integer storeId) {
        JsonNode node = readObject(raw);
        return switch (mode) {
            case CASHIER_POS -> normalisePayments(node);
            case TABLE_ORDER -> normaliseTables(node, storeId);
            case SELF_ORDER_KIOSK -> normaliseOrderTypes(node);
            case QUICK_ORDER -> normaliseLabel(node);
        };
    }

    private static JsonNode readObject(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new BusinessBadRequestException("exception.device.config.invalid", null);
        }
        try {
            JsonNode node = MAPPER.readTree(raw);
            if (node == null || !node.isObject()) {
                throw new BusinessBadRequestException("exception.device.config.invalid", null);
            }
            return node;
        } catch (BusinessBadRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessBadRequestException("exception.device.config.invalid", null);
        }
    }

    private String normalisePayments(JsonNode node) {
        Set<String> payments = readCodes(node.get("payments"), PAYMENTS);
        if (payments.isEmpty()) {
            throw new BusinessBadRequestException("exception.device.config.payments", null);
        }
        return write("payments", payments);
    }

    private String normaliseOrderTypes(JsonNode node) {
        Set<String> orderTypes = readCodes(node.get("orderTypes"), ORDER_TYPES);
        if (orderTypes.isEmpty()) {
            throw new BusinessBadRequestException("exception.device.config.orderTypes", null);
        }
        return write("orderTypes", orderTypes);
    }

    private String normaliseTables(JsonNode node, Integer storeId) {
        Set<Integer> tableIds = readTableIds(node);
        Set<Integer> storeTableIds = tableMapper.selectByStoreId(storeId).stream()
                .map(Table::getId)
                .collect(Collectors.toSet());
        if (!storeTableIds.containsAll(tableIds)) {
            throw new BusinessBadRequestException("exception.device.config.table", null);
        }
        return writeTableIds(tableIds);
    }

    private static Set<Integer> readTableIds(JsonNode node) {
        Set<Integer> tableIds = new TreeSet<>();
        JsonNode array = node.get("tableIds");
        if (array == null || !array.isArray()) {
            throw new BusinessBadRequestException("exception.device.config.table", null);
        }
        for (JsonNode item : (ArrayNode) array) {
            if (!item.canConvertToInt() || item.asInt() <= 0) {
                throw new BusinessBadRequestException("exception.device.config.table", null);
            }
            tableIds.add(item.asInt());
        }
        if (tableIds.isEmpty()) {
            throw new BusinessBadRequestException("exception.device.config.table", null);
        }
        return tableIds;
    }

    private static String writeTableIds(Set<Integer> tableIds) {
        try {
            return MAPPER.writeValueAsString(MAPPER.createObjectNode()
                    .set("tableIds", MAPPER.valueToTree(tableIds)));
        } catch (Exception e) {
            throw new BusinessBadRequestException("exception.device.config.invalid", null);
        }
    }

    private String normaliseLabel(JsonNode node) {
        JsonNode label = node.get("label");
        if (label == null || !label.isTextual() || label.asText().isBlank()) {
            throw new BusinessBadRequestException("exception.device.config.label", null);
        }
        try {
            return MAPPER.writeValueAsString(
                    MAPPER.createObjectNode().put("label", label.asText().trim()));
        } catch (Exception e) {
            throw new BusinessBadRequestException("exception.device.config.invalid", null);
        }
    }

    private static Set<String> readCodes(JsonNode array, Set<String> allowed) {
        Set<String> values = new LinkedHashSet<>();
        if (array == null || !array.isArray()) {
            return values;
        }
        for (JsonNode item : (ArrayNode) array) {
            if (!item.isTextual()) {
                continue;
            }
            String code = item.asText().trim().toUpperCase(Locale.ROOT);
            if (!allowed.contains(code)) {
                throw new BusinessBadRequestException("exception.device.config.unknown", null);
            }
            values.add(code);
        }
        return values;
    }

    private static String write(String field, Set<String> values) {
        try {
            return MAPPER.writeValueAsString(MAPPER.createObjectNode()
                    .set(field, MAPPER.valueToTree(values)));
        } catch (Exception e) {
            throw new BusinessBadRequestException("exception.device.config.invalid", null);
        }
    }
}
