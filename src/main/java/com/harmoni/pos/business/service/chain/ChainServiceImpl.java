package com.harmoni.pos.business.service.chain;

import com.harmoni.pos.exception.BusinessBadRequestException;
import com.harmoni.pos.exception.BusinessNoContentRequestException;
import com.harmoni.pos.exception.BusinessNotFoundRequestException;
import com.harmoni.pos.menu.mapper.ChainMapper;
import com.harmoni.pos.menu.model.Chain;
import com.harmoni.pos.menu.model.dto.ChainDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

/**
 * Implementation of the {@link ChainService} for managing chains.
 * Provides operations to create, update, retrieve, delete, and list chain records.
 */
@RequiredArgsConstructor
@Service("chainService")
@Slf4j
public class ChainServiceImpl implements ChainService {

    private final ChainMapper chainMapper;

    /**
     * Creates a new chain from the provided data.
     *
     * @param chainDto the DTO containing chain details
     * @return number of rows inserted
     * @throws BusinessBadRequestException if a chain with the same name already exists
     * @throws BusinessNoContentRequestException if insertion fails
     */
    @Override
    public int create(ChainDto chainDto) {
        if (!ObjectUtils.isEmpty(chainMapper.selectByName(chainDto.getName()))) {
            throw new BusinessBadRequestException("exception.chain.name.badRequest.duplicate", null);
        }

        int inserted = chainMapper.insert(chainDto.toChain());
        if (inserted < 1) {
            throw new BusinessNoContentRequestException(BusinessNoContentRequestException.NO_CONTENT, null);
        }

        return inserted;
    }

    /**
     * Updates an existing chain with the given ID.
     *
     * @param chainDto the DTO containing updated data
     * @param id       the ID of the chain to update
     * @return true if update is successful, false otherwise
     * @throws BusinessBadRequestException if the chain name is duplicate or the ID is not found
     */
    @Override
    public boolean update(ChainDto chainDto, Integer id) {
        if (!ObjectUtils.isEmpty(chainMapper.selectByName(chainDto.getName()))) {
            throw new BusinessBadRequestException("exception.chain.name.badRequest.duplicate", null);
        }

        if (ObjectUtils.isEmpty(chainMapper.selectByPrimaryKey(id))) {
            throw new BusinessBadRequestException("exception.chain.id.badRequest.notFound", null);
        }

        chainMapper.updateByPrimaryKey(
                new Chain().setName(chainDto.getName()).setId(id).setBrandId(chainDto.getBrandId()));

        return true;
    }

    /**
     * Deletes the chain with the specified ID.
     *
     * @param id the chain ID
     * @return number of rows deleted
     */
    @Override
    public int delete(Integer id) {
        Chain chain = chainMapper.selectByPrimaryKey(id);
        return chainMapper.deleteByPrimaryKey(chain.getId());
    }

    /**
     * Retrieves the chain by its ID.
     *
     * @param id the chain ID
     * @return the chain entity
     * @throws BusinessNotFoundRequestException if the chain is not found
     */
    @Override
    public Chain get(Integer id) {
        Chain chain = chainMapper.selectByPrimaryKey(id);
        if (ObjectUtils.isEmpty(chain)) {
            throw new BusinessNotFoundRequestException("exception.chain.id.badRequest.notFound", null);
        }
        return chain;
    }

    /**
     * Retrieves all chains.
     *
     * @return list of chains
     */
    @Override
    public List<Chain> list() {
        return chainMapper.selectAll();
    }

    /**
     * Retrieves all chains associated with a given brand ID.
     *
     * @param brandId the brand ID
     * @return list of chains
     */
    @Override
    public List<Chain> listByBrandId(Integer brandId) {
        return chainMapper.selectByBrandId(brandId);
    }
}
