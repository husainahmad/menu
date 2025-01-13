package com.harmoni.pos.business.service.tier.tierservice;

import com.harmoni.pos.menu.model.TierService;
import com.harmoni.pos.menu.model.dto.TierServiceDto;

import java.util.List;

public interface TierServiceService {

    int create(TierServiceDto tierServiceDto);

    int updateTierServices(List<TierServiceDto> tierServiceDtos, Integer tierId);

    List<TierService> getByBrandId(Integer id);

}
