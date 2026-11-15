package com.harmoni.pos.business.service.kitchen;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.harmoni.pos.exception.BusinessBadRequestException;
import com.harmoni.pos.menu.mapper.KitchenPrinterMapper;
import com.harmoni.pos.menu.mapper.KitchenStationMapper;
import com.harmoni.pos.menu.model.KitchenPrinter;
import com.harmoni.pos.menu.model.KitchenStation;
import com.harmoni.pos.menu.model.dto.kitchen.KitchenPrinterDto;
import com.harmoni.pos.menu.model.enums.KitchenStatus;
import com.harmoni.pos.menu.model.enums.PaperWidth;
import com.harmoni.pos.menu.model.enums.PrinterType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Default {@link KitchenPrinterService}. Enforces the one-primary-printer rule and
 * validates the transport configuration shape before it is stored for the future
 * printing engine.
 */
@RequiredArgsConstructor
@Service("kitchenPrinterService")
@Slf4j
public class KitchenPrinterServiceImpl implements KitchenPrinterService {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final KitchenPrinterMapper kitchenPrinterMapper;
    private final KitchenStationMapper kitchenStationMapper;

    @Override
    @Transactional(transactionManager = "menuSQLTransactionManager")
    public KitchenPrinter create(Integer stationId, KitchenPrinterDto dto) {
        requireStation(stationId);

        if (kitchenPrinterMapper.selectByStationIdAndName(stationId, dto.getName().trim()) != null) {
            throw new BusinessBadRequestException("exception.kitchenPrinter.name.duplicate", null);
        }

        validate(dto);
        List<KitchenPrinter> existing = kitchenPrinterMapper.selectByStationId(stationId);
        boolean primary = Boolean.TRUE.equals(dto.getIsPrimary()) || existing.isEmpty();

        if (primary) {
            kitchenPrinterMapper.clearPrimaryByStationId(stationId, null);
        }

        Date now = new Date();
        KitchenPrinter printer = dto.toModel()
                .setKitchenStationId(stationId)
                .setName(dto.getName().trim())
                .setPrinterType(PrinterType.from(dto.getPrinterType()).name())
                .setPaperWidth(PaperWidth.fromCode(dto.getPaperWidth()).getCode())
                .setIsPrimary(primary)
                .setStatus(KitchenStatus.from(dto.getStatus()).name())
                .setCreatedAt(now)
                .setUpdatedAt(now);

        if (kitchenPrinterMapper.insert(printer) < 1) {
            throw new BusinessBadRequestException("exception.kitchenPrinter.create.failed", null);
        }
        return printer;
    }

    @Override
    @Transactional(transactionManager = "menuSQLTransactionManager")
    public KitchenPrinter update(Integer id, KitchenPrinterDto dto) {
        KitchenPrinter existing = get(id);
        validate(dto);

        KitchenPrinter sameName = kitchenPrinterMapper.selectByStationIdAndName(
                existing.getKitchenStationId(), dto.getName().trim());
        if (sameName != null && !Objects.equals(sameName.getId(), id)) {
            throw new BusinessBadRequestException("exception.kitchenPrinter.name.duplicate", null);
        }

        boolean primary = dto.getIsPrimary() == null
                ? Boolean.TRUE.equals(existing.getIsPrimary())
                : dto.getIsPrimary();
        if (primary) {
            kitchenPrinterMapper.clearPrimaryByStationId(existing.getKitchenStationId(), id);
        }

        existing.setName(dto.getName().trim())
                .setPrinterType(PrinterType.from(dto.getPrinterType()).name())
                .setConnectionConfig(dto.getConnectionConfig())
                .setPaperWidth(PaperWidth.fromCode(dto.getPaperWidth()).getCode())
                .setIsPrimary(primary)
                .setStatus(dto.getStatus() == null
                        ? existing.getStatus() : KitchenStatus.from(dto.getStatus()).name())
                .setUpdatedAt(new Date());

        if (kitchenPrinterMapper.updateByPrimaryKey(existing) < 1) {
            throw new BusinessBadRequestException("exception.kitchenPrinter.update.failed", null);
        }
        return existing;
    }

    @Override
    @Transactional(transactionManager = "menuSQLTransactionManager")
    public void delete(Integer id) {
        KitchenPrinter printer = get(id);
        kitchenPrinterMapper.deleteByPrimaryKey(id, new Date());

        if (Boolean.TRUE.equals(printer.getIsPrimary())) {
            KitchenPrinter replacement = kitchenPrinterMapper.selectFirstActiveByStationId(
                    printer.getKitchenStationId());
            if (replacement != null) {
                replacement.setIsPrimary(true).setUpdatedAt(new Date());
                kitchenPrinterMapper.updateByPrimaryKey(replacement);
            }
        }
    }

    @Override
    public KitchenPrinter get(Integer id) {
        KitchenPrinter printer = kitchenPrinterMapper.selectByPrimaryKey(id);
        if (printer == null) {
            throw new BusinessBadRequestException("exception.kitchenPrinter.id.notFound", null);
        }
        return printer;
    }

    @Override
    public List<KitchenPrinter> listByStation(Integer stationId) {
        requireStation(stationId);
        return kitchenPrinterMapper.selectByStationId(stationId);
    }

    private void validate(KitchenPrinterDto dto) {
        PrinterType.from(dto.getPrinterType());
        PaperWidth.fromCode(dto.getPaperWidth());

        // The connection payload is stored for the printing engine. It must be a JSON
        // object so a typo is caught while the operator is still on the form.
        try {
            JsonNode node = MAPPER.readTree(dto.getConnectionConfig());
            if (node == null || !node.isObject()) {
                throw new BusinessBadRequestException("exception.kitchenPrinter.config.invalid", null);
            }
        } catch (BusinessBadRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessBadRequestException("exception.kitchenPrinter.config.invalid", null);
        }
    }

    private KitchenStation requireStation(Integer stationId) {
        KitchenStation station = kitchenStationMapper.selectByPrimaryKey(stationId);
        if (station == null) {
            throw new BusinessBadRequestException("exception.kitchenStation.id.notFound", null);
        }
        return station;
    }
}
