package com.harmoni.pos.business.service.brand;

import com.harmoni.pos.exception.BusinessBadRequestException;
import com.harmoni.pos.exception.BusinessNoContentRequestException;
import com.harmoni.pos.menu.mapper.BrandMapper;
import com.harmoni.pos.menu.model.Brand;
import com.harmoni.pos.menu.model.dto.BrandDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

@RequiredArgsConstructor
@Service("brandService")
@Slf4j
public class BrandServiceImpl implements BrandService {

    private final BrandMapper brandMapper;

    @Override
    public int create(BrandDto brandDto) {

        if (!ObjectUtils.isEmpty(brandMapper.selectByNameAndChainId(brandDto.getChainId(),
                brandDto.getName()))) {
            throw new BusinessBadRequestException(
                    "exception.brand.name.badRequest.duplicate", null);
        }

        int inserted = brandMapper.insert(brandDto.toBrand());
        if (inserted<1) {
            throw new BusinessNoContentRequestException(
                    BusinessNoContentRequestException.NO_CONTENT, null);
        }

        return inserted;
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
