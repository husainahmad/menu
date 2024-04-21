package com.harmoni.pos.business.service.brand;

import com.harmoni.pos.menu.model.Brand;
import com.harmoni.pos.menu.model.dto.BrandDto;

import java.util.List;

public interface BrandService {

    int create(BrandDto brandDto);

    Brand get(Long id);
    List<Brand> list();
}
