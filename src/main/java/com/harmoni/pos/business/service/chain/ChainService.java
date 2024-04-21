package com.harmoni.pos.business.service.chain;

import com.harmoni.pos.menu.model.Chain;
import com.harmoni.pos.menu.model.dto.ChainDto;

import java.util.List;

public interface ChainService {

    int create(ChainDto chainDto);

    boolean update(ChainDto chainDto, Long id);

    Chain get(Long id);
    List<Chain> list();
}
