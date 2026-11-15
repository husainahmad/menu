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
import java.util.HashMap;
import java.util.Map;

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
     * Updates an existing service and rejects a rename that would collide with
     * another service.
     *
     * @param id         the service ID
     * @param serviceDto the new service details
     * @return number of rows updated
     */
    @Override
    public int update(Integer id, ServiceDto serviceDto) {
        com.harmoni.pos.menu.model.Service existing = serviceMapper.selectByPrimaryKey(id);
        if (ObjectUtils.isEmpty(existing)) {
            throw new BusinessBadRequestException("exception.service.id.notFound",
                    PosObjectUtils.appendValue(new ArrayList<>().toArray(), String.valueOf(id)));
        }
        com.harmoni.pos.menu.model.Service duplicate = serviceMapper.selectByName(serviceDto.getName());
        if (!ObjectUtils.isEmpty(duplicate) && !existing.getId().equals(duplicate.getId())) {
            throw new BusinessBadRequestException("exception.service.badRequest.duplicate",
                    PosObjectUtils.appendValue(new ArrayList<>().toArray(), serviceDto.getName()));
        }

        com.harmoni.pos.menu.model.Service service = serviceDto.toService();
        service.setId(existing.getId());
        service.setCreatedAt(existing.getCreatedAt());
        service.setUpdatedAt(new java.util.Date(System.currentTimeMillis()));
        service.setDeletedAt(existing.getDeletedAt());
        return serviceMapper.updateByPrimaryKey(service);
    }

    /**
     * Deletes a service by its ID.
     *
     * @param id the service ID
     * @return number of rows deleted
     */
    @Override
    public int delete(Integer id) {
        com.harmoni.pos.menu.model.Service existing = serviceMapper.selectByPrimaryKey(id);
        if (ObjectUtils.isEmpty(existing)) {
            throw new BusinessBadRequestException("exception.service.id.notFound",
                    PosObjectUtils.appendValue(new ArrayList<>().toArray(), String.valueOf(id)));
        }
        return serviceMapper.deleteByPrimaryKey(id);
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

    /**
     * Retrieves one page of services including their sub-services.
     *
     * <p>The service/sub-service join cannot be paginated directly: a limit on the
     * joined rows would cut a service's sub-services in half. The page is therefore
     * resolved in two steps, first the service ids and then the rows for those ids.</p>
     *
     * @param page the page number, 1-based
     * @param size the number of services per page
     * @return paginated map with service data and metadata
     */
    @Override
    public Map<String, Object> listPaginated(int page, int size) {
        int safePage = Math.max(page, 1);
        int safeSize = Math.max(size, 1);
        int total = serviceMapper.countAll();
        int pages = (int) Math.ceil(total / (double) safeSize);
        int offset = (safePage - 1) * safeSize;

        List<Integer> ids = serviceMapper.selectPagedIds(offset, safeSize);
        List<com.harmoni.pos.menu.model.Service> services = ids.isEmpty()
                ? new ArrayList<>()
                : serviceMapper.selectByIdsAndSubService(ids);

        Map<String, Object> paginationData = new HashMap<>();
        paginationData.put("page", pages);
        paginationData.put("size", safeSize);
        paginationData.put("total", total);
        paginationData.put("data", services);
        paginationData.put("navigate", new ArrayList<>());
        return paginationData;
    }
}
