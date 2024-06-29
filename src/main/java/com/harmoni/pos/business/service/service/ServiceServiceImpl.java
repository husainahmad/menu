package com.harmoni.pos.business.service.service;

import com.harmoni.pos.exception.BusinessBadRequestException;
import com.harmoni.pos.exception.BusinessNoContentRequestException;
import com.harmoni.pos.http.utils.PosObjectUtils;
import com.harmoni.pos.menu.mapper.ServiceMapper;
import com.harmoni.pos.menu.model.dto.ServiceDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;

@RequiredArgsConstructor
@Service("serviceService")
@Slf4j
public class ServiceServiceImpl implements ServiceService {

    private final ServiceMapper serviceMapper;

    @Override
    public int create(ServiceDto serviceDto) {

        if (!ObjectUtils.isEmpty(serviceMapper.selectByName(serviceDto.getName()))) {
            throw new BusinessBadRequestException("exception.service.badRequest.duplicate",
                    PosObjectUtils.appendValue(new ArrayList<>().toArray(), serviceDto.getName()));
        }

        int inserted = serviceMapper.insert(serviceDto.toService());
        if (inserted<1) {
            throw new BusinessNoContentRequestException("exception.noContent", null);
        }
        return inserted;
    }
}
