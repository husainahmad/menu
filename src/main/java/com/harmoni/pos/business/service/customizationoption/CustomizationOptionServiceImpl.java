package com.harmoni.pos.business.service.customizationoption;

import com.harmoni.pos.menu.mapper.CustomizationOptionMapper;
import com.harmoni.pos.menu.model.CustomizationOption;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of {@link CustomizationOptionService}.
 */
@Service
public class CustomizationOptionServiceImpl implements CustomizationOptionService {

    private final CustomizationOptionMapper optionMapper;

    public CustomizationOptionServiceImpl(CustomizationOptionMapper optionMapper) {
        this.optionMapper = optionMapper;
    }

    @Override
    public Optional<CustomizationOption> getById(Integer id) {
        return Optional.ofNullable(optionMapper.selectByPrimaryKey(id));
    }

    @Override
    public List<CustomizationOption> getByCustomizationId(Integer customizationId) {
        return optionMapper.selectByCustomizationId(customizationId);
    }

    @Override
    public List<CustomizationOption> getByCustomizationIds(List<Integer> customizationIds) {
        if (customizationIds == null || customizationIds.isEmpty()) {
            return List.of();
        }
        return optionMapper.selectByCustomizationIds(customizationIds);
    }

    @Override
    public boolean create(CustomizationOption option) {
        return optionMapper.insert(option) > 0;
    }

    @Override
    public int createBulk(List<CustomizationOption> options, Integer customizationId) {
        return optionMapper.insertOrUpdateBulk(options, customizationId);
    }

    @Override
    public boolean update(CustomizationOption option) {
        return optionMapper.updateByPrimaryKey(option) > 0;
    }

    @Override
    public boolean delete(Integer id) {
        return optionMapper.deleteByPrimaryKey(id) > 0;
    }
}
