package com.harmoni.pos.business.service.tier;

import com.harmoni.pos.menu.model.Tier;
import com.harmoni.pos.menu.model.TierType;
import com.harmoni.pos.menu.model.dto.TierDto;

import java.util.List;

public interface TierService {

    int create(TierDto tierDto);

    boolean update(TierDto tierDto, Integer id);

    int delete(Integer id);

    Tier get(Integer id);

    List<Tier> getByBrandId(Integer id);

    List<Tier> getByBrandIdAndTierType(Integer id, TierType tierType);

    List<Tier> validateTierByIds(List<Integer> ids);

}
