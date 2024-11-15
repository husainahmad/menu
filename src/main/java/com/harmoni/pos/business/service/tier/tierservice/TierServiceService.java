package com.harmoni.pos.business.service.tier.tierservice;

import com.harmoni.pos.menu.model.Tier;
import com.harmoni.pos.menu.model.TierService;
import com.harmoni.pos.menu.model.TierType;
import com.harmoni.pos.menu.model.dto.TierServiceDto;

import java.util.List;

public interface TierServiceService {

    int create(TierServiceDto tierServiceDto);

    boolean update(TierServiceDto tierServiceDto, Integer id);

    int delete(Integer id);

    Tier get(Integer id);

    List<TierService> getByBrandId(Integer id);

    List<TierService> getByBrandIdAndTierType(Integer id, TierType tierType);

    List<TierService> validateTierByIds(List<Integer> ids);

}
