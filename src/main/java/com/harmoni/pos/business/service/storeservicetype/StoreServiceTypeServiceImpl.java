package com.harmoni.pos.business.service.storeservicetype;

import com.harmoni.pos.exception.BusinessBadRequestException;
import com.harmoni.pos.exception.BusinessNoContentRequestException;
import com.harmoni.pos.menu.mapper.StoreServiceTypeMapper;
import com.harmoni.pos.menu.model.dto.StoreServiceTypeDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@RequiredArgsConstructor
@Service("storeServiceTypeService")
@Slf4j
public class StoreServiceTypeServiceImpl implements StoreServiceTypeService {

    private final StoreServiceTypeMapper storeServiceTypeMapper;

    @Override
    public int create(StoreServiceTypeDto storeServiceTypeDto) {

        if (!ObjectUtils.isEmpty(storeServiceTypeMapper.selectByStoreIdSubServiceId(
                storeServiceTypeDto.getStoreId(), storeServiceTypeDto.getSubServiceId()
        ))) {
            throw new BusinessBadRequestException("exception.storeServiceType.badRequest.duplicate", null);
        }

        int inserted = storeServiceTypeMapper.insert(storeServiceTypeDto.toStoreServiceType());
        if (inserted<1) {
            throw new BusinessNoContentRequestException(
                    BusinessNoContentRequestException.NO_CONTENT, null);
        }

        return inserted;
    }
}
