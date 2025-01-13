package com.harmoni.pos.business.service.tier.tiermenu;

import com.harmoni.pos.menu.model.TierMenu;
import com.harmoni.pos.menu.model.dto.add.TierMenuEditDto;

import java.util.List;

public interface TierMenuService {

    int create(Integer tierId, List<TierMenuEditDto> tierMenuEditDtos);

    List<TierMenu> getMenusByBrandId(Integer brandId);

}
