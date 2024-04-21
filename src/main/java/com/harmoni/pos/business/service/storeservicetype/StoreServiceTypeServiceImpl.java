package com.harmoni.pos.business.service.storeservicetype;

import com.harmoni.pos.exception.BusinessBadRequestException;
import com.harmoni.pos.exception.BusinessNoContentRequestException;
import com.harmoni.pos.menu.mapper.StoreServiceTypeMapper;
import com.harmoni.pos.menu.model.dto.StoreServiceTypeDto;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@RequiredArgsConstructor
@Service("storeServiceTypeService")
public class StoreServiceTypeServiceImpl implements StoreServiceTypeService {

    private final Logger log = LoggerFactory.getLogger(StoreServiceTypeServiceImpl.class);
    private final StoreServiceTypeMapper storeServiceTypeMapper;

    @Override
    public int create(StoreServiceTypeDto storeServiceTypeDto) {

        if (!ObjectUtils.isEmpty(storeServiceTypeMapper.selectByStoreIdSubServiceId(
                storeServiceTypeDto.getStoreId(), storeServiceTypeDto.getSubServiceId()
        ))) {
            throw new BusinessBadRequestException("exception.storeServiceType.badRequest.duplicate", null);
        }

        int record = storeServiceTypeMapper.insert(storeServiceTypeDto.toStoreServiceType());
        if (record<1) {
            throw new BusinessNoContentRequestException("exception.noContent", null);
        }

        return record;
    }
}
