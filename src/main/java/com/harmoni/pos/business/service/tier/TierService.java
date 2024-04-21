package com.harmoni.pos.business.service.tier;

import com.harmoni.pos.menu.model.Tier;
import com.harmoni.pos.menu.model.dto.TierDto;

import java.util.List;

public interface TierService {

    int create(TierDto tierDto);

    Tier get(Long id);

    List<Tier> getByBrandId(Long id);
    List<Tier> list();

}
