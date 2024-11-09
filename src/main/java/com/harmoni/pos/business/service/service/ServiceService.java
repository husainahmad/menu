package com.harmoni.pos.business.service.service;

import com.harmoni.pos.menu.model.Service;
import com.harmoni.pos.menu.model.dto.ServiceDto;

import java.util.List;

public interface ServiceService {

    int create(ServiceDto serviceDto);

    List<Service> getAllWithSub();
}
