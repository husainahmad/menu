package com.harmoni.pos.business.service.category;

import com.harmoni.pos.menu.model.Category;
import com.harmoni.pos.menu.model.dto.CategoryDto;

import java.util.List;
import java.util.Map;

public interface CategoryService {
    int create(CategoryDto categoryDto);
    int delete(Integer id);
    List<Category> list();
    Map<String, Object> listPaginated(int page, int size);

    List<Category> selectByBrandId(Integer brandId);

    Category get(Integer id);

}
