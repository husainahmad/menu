package com.harmoni.pos.business.service.brand;

import com.harmoni.pos.exception.BusinessBadRequestException;
import com.harmoni.pos.exception.BusinessNoContentRequestException;
import com.harmoni.pos.exception.BusinessNotFoundRequestException;
import com.harmoni.pos.menu.mapper.BrandMapper;
import com.harmoni.pos.menu.model.Brand;
import com.harmoni.pos.menu.model.dto.BrandDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

/**
 * Implementation of the {@link BrandService} interface that handles business logic
 * related to brand management.
 *
 * <p>This service provides functionality for creating, deleting, retrieving, and listing brands,
 * and ensures that domain-level validations are enforced (e.g., checking for duplicates).</p>
 */
@RequiredArgsConstructor
@Service("brandService")
@Slf4j
public class BrandServiceImpl implements BrandService {

    private final BrandMapper brandMapper;

    /**
     * Creates a new brand.
     *
     * @param brandDto the DTO containing the brand data to create
     * @return number of rows affected in the database
     * @throws BusinessBadRequestException if the brand name already exists
     * @throws BusinessNoContentRequestException if insert operation fails
     */
    @Override
    public int create(BrandDto brandDto) {
        if (!ObjectUtils.isEmpty(brandMapper.selectByName(brandDto.getName()))) {
            throw new BusinessBadRequestException(
                    "exception.brand.name.badRequest.duplicate", null);
        }

        int inserted = brandMapper.insert(brandDto.toBrand());
        if (inserted < 1) {
            throw new BusinessNoContentRequestException(
                    BusinessNoContentRequestException.NO_CONTENT, null);
        }

        return inserted;
    }

    /**
     * Deletes a brand by ID.
     *
     * @param id the brand ID to delete
     * @return number of rows affected
     * @throws BusinessNotFoundRequestException if the brand is not found
     */
    @Override
    public int delete(Integer id) {
        Brand brand = this.get(id); // Throws if not found
        return brandMapper.deleteByPrimaryKey(brand.getId());
    }

    /**
     * Retrieves a brand by ID.
     *
     * @param id the brand ID
     * @return the brand entity
     * @throws BusinessNotFoundRequestException if the brand is not found
     */
    @Override
    public Brand get(Integer id) {
        Brand brand = brandMapper.selectByPrimaryKey(id);
        if (ObjectUtils.isEmpty(brand)) {
            throw new BusinessNotFoundRequestException(
                    "exception.brand.id.badRequest.notFound", null);
        }
        return brand;
    }

    /**
     * Retrieves a list of all brands.
     *
     * @return a list of {@link Brand} entities
     */
    @Override
    public List<Brand> list() {
        return brandMapper.selectAll();
    }
}
