package com.harmoni.pos.business.service.tier.tiermenu;

import com.harmoni.pos.menu.model.Tier;
import com.harmoni.pos.menu.model.dto.add.TierMenuEditDto;

public interface TierMenuService {

    int create(Integer tierId, TierMenuEditDto tierMenuAddDto);

    boolean update(com.harmoni.pos.menu.model.dto.edit.TierMenuEditDto tierMenuEditDto, Integer tierId);

    int delete(Integer id);

    Tier get(Integer id);

}
