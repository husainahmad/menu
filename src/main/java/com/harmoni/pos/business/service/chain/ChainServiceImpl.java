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

@RequiredArgsConstructor
@Service("chainService")
@Slf4j
public class ChainServiceImpl implements ChainService {
    private final ChainMapper chainMapper;

    @Override
    public int create(ChainDto chainDto) {

        if (!ObjectUtils.isEmpty(chainMapper.selectByName(chainDto.getName()))) {
            throw new BusinessBadRequestException(
                    "exception.chain.name.badRequest.duplicate", null);
        }

        int inserted = chainMapper.insert(chainDto.toChain());
        if (inserted<1) {
            throw new BusinessNoContentRequestException(
                    BusinessNoContentRequestException.NO_CONTENT, null);
        }

        return inserted;
    }

    @Override
    public boolean update(ChainDto chainDto, Integer id) {

        if (!ObjectUtils.isEmpty(chainMapper.selectByName(chainDto.getName()))) {
            throw new BusinessBadRequestException(
                    "exception.chain.name.badRequest.duplicate", null);
        }

        if (ObjectUtils.isEmpty(chainMapper.selectByPrimaryKey(id))) {
            throw new BusinessBadRequestException(
                    "exception.chain.id.badRequest.notFound", null);
        }

        chainMapper.updateByPrimaryKey(
                new Chain().setName(chainDto.getName()).setId(id).setBrandId(chainDto.getBrandId()));

        return false;
    }

    @Override
    public int delete(Integer id) {
        Chain chain = chainMapper.selectByPrimaryKey(id);
        return chainMapper.deleteByPrimaryKey(chain.getId());
    }

    @Override
    public Chain get(Integer id) {
        Chain chain = chainMapper.selectByPrimaryKey(id);
        if (ObjectUtils.isEmpty(chain)) {
            throw new BusinessNotFoundRequestException(
                    "exception.chain.id.badRequest.notFound", null);
        }
        return chain;
    }

    @Override
    public List<Chain> list() {
        return chainMapper.selectAll();
    }

    @Override
    public List<Chain> listByBrandId(Integer brandId) {
        return chainMapper.selectByBrandId(brandId);
    }
}
