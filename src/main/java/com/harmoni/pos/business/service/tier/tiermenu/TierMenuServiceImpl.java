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
    public int create(Integer tierId, TierMenuEditDto tierMenuAddDto) {
        Tier tier = tierService.get(tierId);

        List<TierMenu> tierMenuList = new ArrayList<>();

        tierMenuAddDto.getCategoryEditDtos().forEach(categoryEditDto -> {
            TierMenu tierMenu = new TierMenu();
            tierMenu.setTierId(tier.getId());
            tierMenu.setCategoryId(categoryEditDto.getId());
            tierMenu.setActive(categoryEditDto.getActive());
            tierMenu.setCreatedAt(new Date(System.currentTimeMillis()));

            tierMenuList.add(tierMenu);
        });

        return tierMenuMapper.updateTierMenuBulk(tierMenuList);
    }

    @Override
    public boolean update(com.harmoni.pos.menu.model.dto.edit.TierMenuEditDto tierMenuEditDto, Integer tierId) {
        return false;
    }

    @Override
    public int delete(Integer id) {
        return 0;
    }

    @Override
    public Tier get(Integer id) {
        return null;
    }
}
