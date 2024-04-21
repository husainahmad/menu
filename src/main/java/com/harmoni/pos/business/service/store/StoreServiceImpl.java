package com.harmoni.pos.business.service.store;

import com.harmoni.pos.exception.BusinessBadRequestException;
import com.harmoni.pos.exception.BusinessNoContentRequestException;
import com.harmoni.pos.menu.mapper.StoreMapper;
import com.harmoni.pos.menu.model.Store;
import com.harmoni.pos.menu.model.dto.StoreDto;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

@RequiredArgsConstructor
@Service("storeService")
public class StoreServiceImpl implements StoreService {
    private final Logger log = LoggerFactory.getLogger(StoreServiceImpl.class);
    private final StoreMapper storeMapper;

    @Override
    public int create(StoreDto storeDto) {

        if (!ObjectUtils.isEmpty(storeMapper.selectByNameTierIdBrandId(storeDto.getName(),
                storeDto.getTierId(), storeDto.getBrandId()))) {
            throw new BusinessBadRequestException("exception.store.badRequest.duplicate", null);
        }

        int record = storeMapper.insert(storeDto.toStore());
        if (record<1) {
            throw new BusinessNoContentRequestException("exception.noContent", null);
        }

        return record;
    }

    @Override
    public List<Store> list() {
        return storeMapper.selectAll();
    }
}
