package com.harmoni.pos.business.service.storeservicetype;

import com.harmoni.pos.exception.BusinessBadRequestException;
import com.harmoni.pos.exception.BusinessNoContentRequestException;
import com.harmoni.pos.menu.mapper.StoreServiceTypeMapper;
import com.harmoni.pos.menu.model.dto.StoreServiceTypeDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

/**
 * Implementation of {@link StoreServiceTypeService} to handle business logic
 * related to Store Service Types.
 */
@RequiredArgsConstructor
@Service("storeServiceTypeService")
@Slf4j
public class StoreServiceTypeServiceImpl implements StoreServiceTypeService {

    private final StoreServiceTypeMapper storeServiceTypeMapper;

    /**
     * Creates a new Store Service Type record.
     *
     * @param storeServiceTypeDto Data transfer object containing details of the store service type to create.
     * @return the number of records inserted (usually 1 if successful).
     * @throws BusinessBadRequestException if a duplicate store service type exists for the given store and sub-service.
     * @throws BusinessNoContentRequestException if the insertion failed and no records were created.
     */
    @Override
    public int create(StoreServiceTypeDto storeServiceTypeDto) {

        if (!ObjectUtils.isEmpty(storeServiceTypeMapper.selectByStoreIdSubServiceId(
                storeServiceTypeDto.getStoreId(), storeServiceTypeDto.getSubServiceId()
        ))) {
            throw new BusinessBadRequestException("exception.storeServiceType.badRequest.duplicate", null);
        }

        int inserted = storeServiceTypeMapper.insert(storeServiceTypeDto.toStoreServiceType());
        if (inserted < 1) {
            throw new BusinessNoContentRequestException(
                    BusinessNoContentRequestException.NO_CONTENT, null);
        }

        return inserted;
    }
}
