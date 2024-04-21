package com.harmoni.pos.business.service.chain;

import com.harmoni.pos.exception.BusinessBadRequestException;
import com.harmoni.pos.exception.BusinessNoContentRequestException;
import com.harmoni.pos.menu.mapper.ChainMapper;
import com.harmoni.pos.menu.model.Chain;
import com.harmoni.pos.menu.model.dto.ChainDto;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

@RequiredArgsConstructor
@Service("chainService")
public class ChainServiceImpl implements ChainService {
    private final Logger log = LoggerFactory.getLogger(ChainServiceImpl.class);
    private final ChainMapper chainMapper;

    @Override
    public int create(ChainDto chainDto) {

        if (!ObjectUtils.isEmpty(chainMapper.selectByName(chainDto.getName()))) {
            throw new BusinessBadRequestException(
                    "exception.chain.name.badRequest.duplicate", null);
        }

        int record = chainMapper.insert(chainDto.toChain());
        if (record<1) {
            throw new BusinessNoContentRequestException("exception.noContent", null);
        }

        return record;
    }

    @Override
    public boolean update(ChainDto chainDto, Long id) {

        if (!ObjectUtils.isEmpty(chainMapper.selectByName(chainDto.getName()))) {
            throw new BusinessBadRequestException(
                    "exception.chain.name.badRequest.duplicate", null);
        }

        if (ObjectUtils.isEmpty(chainMapper.selectByPrimaryKey(id.intValue()))) {
            throw new BusinessBadRequestException(
                    "exception.chain.id.badRequest.notFound", null);
        }

        chainMapper.updateByPrimaryKey(
                new Chain().setName(chainDto.getName()).setId(id.intValue()));

        return false;
    }

    @Override
    public Chain get(Long id) {
        Chain chain = chainMapper.selectByPrimaryKey(id.intValue());
        if (ObjectUtils.isEmpty(chain)) {
            throw new BusinessBadRequestException(
                    "exception.chain.id.badRequest.notFound", null);
        }
        return chain;
    }

    @Override
    public List<Chain> list() {
        return chainMapper.selectAll();
    }
}
