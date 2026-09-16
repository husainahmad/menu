package com.harmoni.pos.menu.mapper;
import com.harmoni.pos.menu.model.CustomizationOption;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * MyBatis mapper for {@link CustomizationOption}.
 */
@Mapper
public interface CustomizationOptionMapper {

    int insert(CustomizationOption option);

    int insertOrUpdateBulk(List<CustomizationOption> customizationOptions, Integer customizationId);

    int updateByPrimaryKey(CustomizationOption option);

    int deleteByPrimaryKey(Integer id);

    CustomizationOption selectByPrimaryKey(Integer id);

    List<CustomizationOption> selectByCustomizationId(Integer customizationId);

    List<CustomizationOption> selectByCustomizationIds(List<Integer> customizationIds);

}

