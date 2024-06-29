package com.harmoni.pos.business.service.subservice;

import com.harmoni.pos.exception.BusinessBadRequestException;
import com.harmoni.pos.exception.BusinessNoContentRequestException;
import com.harmoni.pos.menu.mapper.SubServiceMapper;
import com.harmoni.pos.menu.model.dto.SubServiceDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@RequiredArgsConstructor
@Service("subServiceService")
@Slf4j
public class SubServiceServiceImpl implements SubServiceService {

    private final SubServiceMapper subServiceMapper;

    @Override
    public int create(SubServiceDto subServiceDto) {
        if (!ObjectUtils.isEmpty(subServiceMapper.selectByNameServiceId(subServiceDto.getName(),
                subServiceDto.getServiceId()))) {
            throw new BusinessBadRequestException("exception.subService.badRequest.duplicate", null);
        }

        int inserted = subServiceMapper.insert(subServiceDto.toSubService());
        if (inserted<1) {
            throw new BusinessNoContentRequestException(
                    BusinessNoContentRequestException.NO_CONTENT, null);
        }
        return inserted;
    }
}
