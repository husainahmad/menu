package com.harmoni.pos.menu.mapper;

import com.harmoni.pos.menu.model.KitchenPrinter;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * Mapper for {@code kitchen_printers}.
 */
@Mapper
public interface KitchenPrinterMapper {

    int insert(KitchenPrinter row);

    int updateByPrimaryKey(KitchenPrinter row);

    int deleteByPrimaryKey(@Param("id") Integer id, @Param("deletedAt") Date deletedAt);

    KitchenPrinter selectByPrimaryKey(@Param("id") Integer id);

    List<KitchenPrinter> selectByStationId(@Param("stationId") Integer stationId);

    KitchenPrinter selectPrimaryByStationId(@Param("stationId") Integer stationId);

    KitchenPrinter selectFirstActiveByStationId(@Param("stationId") Integer stationId);

    int clearPrimaryByStationId(@Param("stationId") Integer stationId,
                                @Param("exceptId") Integer exceptId);

    KitchenPrinter selectByStationIdAndName(@Param("stationId") Integer stationId,
                                            @Param("name") String name);
}
