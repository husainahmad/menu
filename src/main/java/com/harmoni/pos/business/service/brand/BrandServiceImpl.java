package com.harmoni.pos.business.service.brand;

import com.harmoni.pos.exception.BusinessBadRequestException;
import com.harmoni.pos.exception.BusinessNoContentRequestException;
import com.harmoni.pos.menu.mapper.BrandMapper;
import com.harmoni.pos.menu.model.Brand;
import com.harmoni.pos.menu.model.dto.BrandDto;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

@RequiredArgsConstructor
@Service("brandService")
public class BrandServiceImpl implements BrandService {

    private final Logger log = LoggerFactory.getLogger(BrandServiceImpl.class);
    private final BrandMapper brandMapper;

    @Override
    public int create(BrandDto brandDto) {

        if (!ObjectUtils.isEmpty(brandMapper.selectByNameAndChainId(brandDto.getChainId(),
                brandDto.getName()))) {
            throw new BusinessBadRequestException(
                    "exception.brand.name.badRequest.duplicate", null);
        }

        int record = brandMapper.insert(brandDto.toBrand());
        if (record<1) {
            throw new BusinessNoContentRequestException("exception.noContent", null);
        }

        return record;
    }

    @Override
    public Brand get(Long id) {
        Brand brand = brandMapper.selectByPrimaryKey(id.intValue());
        if (ObjectUtils.isEmpty(brand)) {
            throw new BusinessBadRequestException(
                    "exception.brand.id.badRequest.notFound", null);
        }
        return brand;
    }

    @Override
    public List<Brand> list() {
        return brandMapper.selectAll();
    }
}
