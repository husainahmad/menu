package com.harmoni.pos.business.service.tier;

import com.harmoni.pos.exception.BusinessBadRequestException;
import com.harmoni.pos.exception.BusinessNoContentRequestException;
import com.harmoni.pos.menu.mapper.TierMapper;
import com.harmoni.pos.menu.model.Tier;
import com.harmoni.pos.menu.model.dto.TierDto;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

@RequiredArgsConstructor
@Service("tierService")
public class TierServiceImpl implements TierService {

    private final Logger log = LoggerFactory.getLogger(TierServiceImpl.class);
    private final TierMapper tierMapper;

    @Override
    public int create(TierDto tierDto) {

        if (!ObjectUtils.isEmpty(tierMapper.selectByNameAndBrandId(tierDto.getName(),
                tierDto.getBrandId()))) {
            throw new BusinessBadRequestException("exception.tier.badRequest.duplicate", null);
        }

        int record = tierMapper.insert(tierDto.toTear());

        if (record<1) {
            throw new BusinessNoContentRequestException("exception.noContent", null);
        }

        return record;
    }

    @Override
    public Tier get(Long id) {
        Tier tier = tierMapper.selectByPrimaryKey(id.intValue());
        if (ObjectUtils.isEmpty(tier)) {
            throw new BusinessBadRequestException("exception.tier.id.badRequest.notFound", null);
        }
        return tier;
    }

    @Override
    public List<Tier> getByBrandId(Long id) {
        List<Tier> tiers = tierMapper.selectByBrandId(id.intValue());
        if (ObjectUtils.isEmpty(tiers)) {
            throw new BusinessBadRequestException("exception.tier.id.badRequest.notFound", null);
        }
        return tiers;
    }

    @Override
    public List<Tier> list() {
        return tierMapper.selectAll();
    }
}
