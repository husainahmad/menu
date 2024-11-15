package com.harmoni.pos.business.service.tier;

import com.harmoni.pos.exception.BusinessBadRequestException;
import com.harmoni.pos.exception.BusinessNoContentRequestException;
import com.harmoni.pos.exception.BusinessNotFoundRequestException;
import com.harmoni.pos.menu.mapper.TierMapper;
import com.harmoni.pos.menu.model.Tier;
import com.harmoni.pos.menu.model.TierType;
import com.harmoni.pos.menu.model.dto.TierDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
@RequiredArgsConstructor
@Service("tierService")
@Slf4j
public class TierServiceImpl implements TierService {

    private final TierMapper tierMapper;

    @Override
    public int create(TierDto tierDto) {

        if (!ObjectUtils.isEmpty(tierMapper.selectByNameAndBrandId(tierDto.getName(),
                tierDto.getBrandId()))) {
            throw new BusinessBadRequestException(BusinessBadRequestException.DUPLICATION_TIER, null);
        }

        int inserted = tierMapper.insert(tierDto.toTear());

        if (inserted<1) {
            throw new BusinessNoContentRequestException(
                    BusinessNoContentRequestException.NO_CONTENT, null);
        }

        return inserted;
    }

    @Override
    public boolean update(TierDto tierDto, Integer id) {
        Tier tier = get(id);
        tier.setType(tierDto.getType());
        tier.setName(tierDto.getName());
        tier.setBrandId(tierDto.getBrandId());
        int updated = tierMapper.updateByPrimaryKey(tier);
        if (updated<1) {
            throw new BusinessNoContentRequestException(
                    BusinessNoContentRequestException.NO_CONTENT, null);
        }
        return true;
    }

    @Override
    public int delete(Integer id) {
        Tier tier = get(id);
        return tierMapper.deleteByPrimaryKey(tier.getId());
    }

    @Override
    public Tier get(Integer id) {
        Tier tier = tierMapper.selectByPrimaryKey(id);
        if (ObjectUtils.isEmpty(tier)) {
            throw new BusinessNotFoundRequestException(BusinessNotFoundRequestException.NOT_FOUND_TIER, null);
        }
        return tier;
    }

    @Override
    public List<Tier> getByBrandId(Integer id) {
        return tierMapper.selectByBrandId(id);
    }

    @Override
    public List<Tier> getByBrandIdAndTierType(Integer id, TierType tierType) {
        return tierMapper.selectByBrandIdTierType(id, tierType);
    }

    @Override
    public List<Tier> validateTierByIds(List<Integer> ids) {
        List<Tier> tiers = this.tierMapper.selectByIds(ids);
        tiers.forEach(tier -> {
            AtomicBoolean isFound = new AtomicBoolean(false);
            ids.forEach(id -> {
                if (tier.getId().equals(id)) {
                    isFound.set(true);
                }
            });
            if (!isFound.get()) {
                throw new BusinessNotFoundRequestException(BusinessNotFoundRequestException.NOT_FOUND_TIER, null);
            }
        });
        return tiers;
    }

}
