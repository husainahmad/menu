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
import java.util.List;
import java.util.ArrayList;

/**
 * Implementation of the {@link ServiceService} interface
 * that handles business logic for managing services.
 * <p>
 * Provides methods to create a new service and
 * to retrieve all services along with their sub-services.
 * </p>
 */
@RequiredArgsConstructor
@Service("serviceService")
@Slf4j
public class ServiceServiceImpl implements ServiceService {

    private final ServiceMapper serviceMapper;

    /**
     * Creates a new service record.
     * <p>
     * Validates if the service name already exists and
     * throws a {@link BusinessBadRequestException} if it does.
     * Attempts to insert the new service and throws
     * {@link BusinessNoContentRequestException} if insertion fails.
     * </p>
     *
     * @param serviceDto the data transfer object containing service information
     * @return number of records inserted (expected to be 1)
     * @throws BusinessBadRequestException        if a service with the same name already exists
     * @throws BusinessNoContentRequestException if the insert operation affects no rows
     */
    @Override
    public int create(ServiceDto serviceDto) {

        if (!ObjectUtils.isEmpty(serviceMapper.selectByName(serviceDto.getName()))) {
            throw new BusinessBadRequestException("exception.service.badRequest.duplicate",
                    PosObjectUtils.appendValue(new ArrayList<>().toArray(), serviceDto.getName()));
        }

        int inserted = serviceMapper.insert(serviceDto.toService());
        if (inserted < 1) {
            throw new BusinessNoContentRequestException(
                    BusinessNoContentRequestException.NO_CONTENT, null);
        }
        return inserted;
    }

    /**
     * Retrieves all services including their associated sub-services.
     *
     * @return list of services with sub-services loaded
     */
    @Override
    public List<com.harmoni.pos.menu.model.Service> getAllWithSub() {
        return serviceMapper.selectAllAndSubService();
    }
}
