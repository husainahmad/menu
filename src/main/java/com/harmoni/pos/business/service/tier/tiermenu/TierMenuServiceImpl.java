package com.harmoni.pos.business.service.tier.tiermenu;

import com.harmoni.pos.business.service.tier.TierService;
import com.harmoni.pos.menu.mapper.TierMenuMapper;
import com.harmoni.pos.menu.model.Tier;
import com.harmoni.pos.menu.model.TierMenu;
import com.harmoni.pos.menu.model.dto.add.TierMenuEditDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RequiredArgsConstructor
@Service("tierMenuService")
public class TierMenuServiceImpl implements TierMenuService {

    private final TierService tierService;
    private final TierMenuMapper tierMenuMapper;

    @Override
    public int create(Integer tierId, List<TierMenuEditDto> tierMenuEditDtos) {
        Tier tier = tierService.get(tierId);

        List<TierMenu> tierMenuList = new ArrayList<>();

        tierMenuEditDtos.forEach(categoryEditDto -> {
            TierMenu tierMenu = new TierMenu();
            tierMenu.setTierId(tier.getId());
            tierMenu.setCategoryId(categoryEditDto.getCategoryDto().getId());
            tierMenu.setActive(categoryEditDto.getActive());
            tierMenu.setUpdatedAt(new Date(System.currentTimeMillis()));
            tierMenu.setCreatedAt(new Date(System.currentTimeMillis()));

            tierMenuList.add(tierMenu);
        });

        return tierMenuMapper.updateTierMenuBulk(tierMenuList);
    }

    @Override
    public List<TierMenu> getMenusByBrandId(Integer brandId) {
        return tierMenuMapper.selectByBrandId(brandId);
    }

    @Override
    public List<TierMenu> getMenusByTierId(Integer tierId) {
        return tierMenuMapper.selectByTierId(tierId);
    }
}
