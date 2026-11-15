package com.harmoni.pos.business.service.kitchen;

import com.harmoni.pos.menu.model.KitchenPrinter;
import com.harmoni.pos.menu.model.dto.kitchen.KitchenPrinterDto;

import java.util.List;

/**
 * CRUD operations for kitchen printers. Printing itself is not part of this phase;
 * a printer is configuration until the printing engine is added.
 */
public interface KitchenPrinterService {

    KitchenPrinter create(Integer stationId, KitchenPrinterDto dto);

    KitchenPrinter update(Integer id, KitchenPrinterDto dto);

    void delete(Integer id);

    KitchenPrinter get(Integer id);

    List<KitchenPrinter> listByStation(Integer stationId);
}
