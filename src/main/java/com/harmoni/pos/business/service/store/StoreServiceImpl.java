package com.harmoni.pos.business.service.store;

import com.harmoni.pos.exception.BusinessBadRequestException;
import com.harmoni.pos.exception.BusinessNoContentRequestException;
import com.harmoni.pos.http.utils.PosObjectUtils;
import com.harmoni.pos.menu.mapper.StoreMapper;
import com.harmoni.pos.menu.model.Store;
import com.harmoni.pos.menu.model.dto.StoreDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service("storeService")
@Slf4j
public class StoreServiceImpl implements StoreService {
    private final StoreMapper storeMapper;

    @Override
    public int create(StoreDto storeDto) {

        if (!ObjectUtils.isEmpty(storeMapper.selectByNameTierIdChainId(storeDto.getName(),
                storeDto.getTierId(), storeDto.getChainId()))) {
            throw new BusinessBadRequestException("exception.store.badRequest.duplicate",
                    PosObjectUtils.appendValue(new ArrayList<>().toArray(), storeDto.getName()));
        }

        int inserted = storeMapper.insert(storeDto.toStore());
        if (inserted<1) {
            throw new BusinessNoContentRequestException(
                    BusinessNoContentRequestException.NO_CONTENT, null);
        }

        return inserted;
    }

    @Override
    public List<Store> list() {
        return storeMapper.selectAll();
    }
}
