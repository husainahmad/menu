package com.harmoni.pos.business.service.category;

import com.harmoni.pos.exception.BusinessBadRequestException;
import com.harmoni.pos.exception.BusinessNoContentRequestException;
import com.harmoni.pos.menu.model.Category;
import com.harmoni.pos.menu.model.dto.CategoryDto;
import com.harmoni.pos.menu.mapper.CategoryMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

@RequiredArgsConstructor
@Service("categoryService")
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;

    @Override
    public int create(CategoryDto categoryDto) {
        if (!ObjectUtils.isEmpty(categoryMapper.selectByNameBrandId(categoryDto.getName(),
                categoryDto.getBrandId()))) {
            throw new BusinessBadRequestException("exception.category.badRequest.duplicate", null);
        }

        int inserted = categoryMapper.insert(categoryDto.toCategory());
        if (inserted<1) {
            throw new BusinessNoContentRequestException(
                    BusinessNoContentRequestException.NO_CONTENT, null);
        }

        return inserted;
    }

    @Override
    public List<Category> list() {
        return categoryMapper.selectAll();
    }

    @Override
    public List<Category> selectByBrandId(Integer brandId) {
        return categoryMapper.selectByBrandId(brandId);
    }


    @Override
    public Category get(Integer id) {
        Category category = categoryMapper.selectByPrimaryKey(id);
        if (ObjectUtils.isEmpty(category)) {
            throw new BusinessBadRequestException("exception.category.id.badRequest.notFound", null);
        }
        return category;
    }
}
