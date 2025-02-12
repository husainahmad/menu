package com.harmoni.pos.business.service.tier.tierservice;

import com.harmoni.pos.exception.BusinessBadRequestException;
import com.harmoni.pos.exception.BusinessNoContentRequestException;
import com.harmoni.pos.menu.mapper.TierMapper;
import com.harmoni.pos.menu.mapper.TierServiceMapper;
import com.harmoni.pos.menu.model.TierService;
import com.harmoni.pos.menu.model.dto.TierServiceDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service("tierServiceService")
@Slf4j
public class TierServiceServiceImpl implements TierServiceService {

    private final TierServiceMapper tierServiceMapper;
    private final TierMapper tierMapper;

    @Override
    public int create(TierServiceDto tierServiceDto) {
        if (!ObjectUtils.isEmpty(tierMapper.selectByNameAndBrandId(tierServiceDto.getTierDto().getName(),
                tierServiceDto.getTierDto().getBrandId()))) {
            throw new BusinessBadRequestException(BusinessBadRequestException.DUPLICATION_TIER, null);
        }

        int inserted = tierMapper.insert(tierServiceDto.getTierDto().toTear());

        if (inserted<1) {
            throw new BusinessNoContentRequestException(
                    BusinessNoContentRequestException.NO_CONTENT, null);
        }

        return inserted;
    }

    @Override
    public int updateTierServices(List<TierServiceDto> tierServiceDtos, Integer tierId) {

        List<TierService> tierServices = new ArrayList<>();
        tierServiceDtos.forEach(tierServiceDto -> tierServices.add(tierServiceDto.toTierService()));

        return tierServiceMapper.updateTierServicesBulk(tierServices);
    }

    @Override
    public List<TierService> getByBrandId(Integer id) {
        return this.tierServiceMapper.selectByBrandId(id);
    }
}
