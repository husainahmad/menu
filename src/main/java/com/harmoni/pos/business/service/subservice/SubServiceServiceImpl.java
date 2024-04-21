package com.harmoni.pos.business.service.subservice;

import com.harmoni.pos.exception.BusinessBadRequestException;
import com.harmoni.pos.exception.BusinessNoContentRequestException;
import com.harmoni.pos.menu.mapper.SubServiceMapper;
import com.harmoni.pos.menu.model.dto.SubServiceDto;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@RequiredArgsConstructor
@Service("subServiceService")
public class SubServiceServiceImpl implements SubServiceService {

    private final Logger log = LoggerFactory.getLogger(SubServiceServiceImpl.class);
    private final SubServiceMapper subServiceMapper;

    @Override
    public int create(SubServiceDto subServiceDto) {
        if (!ObjectUtils.isEmpty(subServiceMapper.selectByNameServiceId(subServiceDto.getName(),
                subServiceDto.getServiceId()))) {
            throw new BusinessBadRequestException("exception.subService.badRequest.duplicate", null);
        }

        int record = subServiceMapper.insert(subServiceDto.toSubService());
        if (record<1) {
            throw new BusinessNoContentRequestException("exception.noContent", null);
        }
        return record;
    }
}
