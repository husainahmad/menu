package com.harmoni.pos.business.service.service;

import com.harmoni.pos.exception.BusinessBadRequestException;
import com.harmoni.pos.exception.BusinessNoContentRequestException;
import com.harmoni.pos.menu.mapper.ServiceMapper;
import com.harmoni.pos.menu.model.dto.ServiceDto;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@RequiredArgsConstructor
@Service("serviceService")
public class ServiceServiceImpl implements ServiceService {

    private final Logger log = LoggerFactory.getLogger(ServiceServiceImpl.class);
    private final ServiceMapper serviceMapper;

    @Override
    public int create(ServiceDto serviceDto) {

        if (!ObjectUtils.isEmpty(serviceMapper.selectByName(serviceDto.getName()))) {
            throw new BusinessBadRequestException("exception.service.badRequest.duplicate", null);
        }

        int record = serviceMapper.insert(serviceDto.toService());
        if (record<1) {
            throw new BusinessNoContentRequestException("exception.noContent", null);
        }
        return record;
    }
}
