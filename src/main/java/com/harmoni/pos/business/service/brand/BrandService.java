package com.harmoni.pos.business.service.brand;

import com.harmoni.pos.menu.model.Brand;
import com.harmoni.pos.menu.model.dto.BrandDto;

import java.util.List;

/**
 * Service interface for managing Brand entities.
 *
 * <p>This interface defines the contract for operations related to creating,
 * retrieving, deleting, and listing brand data in the POS system.</p>
 */
public interface BrandService {

    /**
     * Creates a new brand based on the provided data transfer object.
     *
     * @param brandDto the data object containing brand information
     * @return the number of rows inserted into the database
     * @throws com.harmoni.pos.exception.BusinessBadRequestException if a duplicate brand name is found
     * @throws com.harmoni.pos.exception.BusinessNoContentRequestException if the brand could not be created
     */
    int create(BrandDto brandDto);

    /**
     * Deletes a brand by its ID.
     *
     * @param id the unique identifier of the brand to be deleted
     * @return the number of rows deleted
     * @throws com.harmoni.pos.exception.BusinessNotFoundRequestException if the brand is not found
     */
    int delete(Integer id);

    /**
     * Retrieves a brand by its ID.
     *
     * @param id the unique identifier of the brand
     * @return the brand entity
     * @throws com.harmoni.pos.exception.BusinessNotFoundRequestException if the brand does not exist
     */
    Brand get(Integer id);

    /**
     * Retrieves a list of all available brands.
     *
     * @return a list of brand entities
     */
    List<Brand> list();
}
