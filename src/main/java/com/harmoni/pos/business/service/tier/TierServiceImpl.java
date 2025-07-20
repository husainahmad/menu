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

/**
 * Implementation of {@link TierService} for managing Tier entities.
 * Provides business logic for creating, updating, deleting, and retrieving tiers.
 */
@RequiredArgsConstructor
@Service("tierService")
@Slf4j
public class TierServiceImpl implements TierService {

    private final TierMapper tierMapper;

    /**
     * Creates a new Tier.
     *
     * @param tierDto the data transfer object containing tier details
     * @return the number of records inserted
     * @throws BusinessBadRequestException if a tier with the same name and brand already exists
     * @throws BusinessNoContentRequestException if the insert operation fails
     */
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

    /**
     * Updates an existing Tier.
     *
     * @param tierDto the data transfer object containing updated tier details
     * @param id the ID of the Tier to update
     * @return true if the update was successful
     * @throws BusinessNoContentRequestException if the update operation fails
     */
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

    /**
     * Deletes a Tier by its ID.
     *
     * @param id the ID of the Tier to delete
     * @return the number of records deleted
     */
    @Override
    public int delete(Integer id) {
        Tier tier = get(id);
        return tierMapper.deleteByPrimaryKey(tier.getId());
    }

    /**
     * Retrieves a Tier by its ID.
     *
     * @param id the ID of the Tier to retrieve
     * @return the Tier object
     * @throws BusinessNotFoundRequestException if the Tier is not found
     */
    @Override
    public Tier get(Integer id) {
        Tier tier = tierMapper.selectByPrimaryKey(id);
        if (ObjectUtils.isEmpty(tier)) {
            throw new BusinessNotFoundRequestException(BusinessNotFoundRequestException.NOT_FOUND_TIER, null);
        }
        return tier;
    }

    /**
     * Retrieves all Tiers for a given brand ID.
     *
     * @param id the brand ID
     * @return a list of Tiers associated with the brand
     */
    @Override
    public List<Tier> getByBrandId(Integer id) {
        return tierMapper.selectByBrandId(id);
    }

    /**
     * Retrieves all Tiers for a given brand ID and tier type.
     *
     * @param id the brand ID
     * @param tierType the type of Tier
     * @return a list of Tiers matching the criteria
     */
    @Override
    public List<Tier> getByBrandIdAndTierType(Integer id, TierType tierType) {
        return tierMapper.selectByBrandIdTierType(id, tierType);
    }

    /**
     * Validates Tiers by their IDs.
     *
     * @param ids the list of Tier IDs to validate
     * @return a list of valid Tiers
     * @throws BusinessNotFoundRequestException if any Tier is not found
     */
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
