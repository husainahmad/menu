package com.harmoni.pos.business.service.category;

import com.harmoni.pos.menu.model.Category;
import com.harmoni.pos.menu.model.dto.CategoryDto;

import java.util.List;

public interface CategoryService {
    int create(CategoryDto categoryDto);
    List<Category> list();

    List<Category> selectByBrandId(Integer brandId);

    Category get(Integer id);

}
