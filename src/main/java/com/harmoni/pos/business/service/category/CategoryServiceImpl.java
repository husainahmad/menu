package com.harmoni.pos.business.service.category;

import com.harmoni.pos.exception.BusinessBadRequestException;
import com.harmoni.pos.exception.BusinessNoContentRequestException;
import com.harmoni.pos.menu.model.Category;
import com.harmoni.pos.menu.model.dto.CategoryDto;
import com.harmoni.pos.menu.mapper.CategoryMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

@RequiredArgsConstructor
@Service("categoryService")
public class CategoryServiceImpl implements CategoryService {

    private final Logger log = LoggerFactory.getLogger(CategoryServiceImpl.class);
    private final CategoryMapper categoryMapper;

    @Override
    public int create(CategoryDto categoryDto) {
        if (!ObjectUtils.isEmpty(categoryMapper.selectByNameBrandId(categoryDto.getName(),
                categoryDto.getBrandId()))) {
            throw new BusinessBadRequestException("exception.category.badRequest.duplicate", null);
        }

        int record = categoryMapper.insert(categoryDto.toCategory());
        if (record<1) {
            throw new BusinessNoContentRequestException("exception.noContent", null);
        }

        return record;
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
        Category category = categoryMapper.selectByPrimaryKey(id.intValue());
        if (ObjectUtils.isEmpty(category)) {
            throw new BusinessBadRequestException("exception.category.id.badRequest.notFound", null);
        }
        return category;
    }
}
