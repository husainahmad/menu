package com.harmoni.pos.http.controller.tier.menu;

import com.harmoni.pos.business.service.tier.tiermenu.TierMenuService;
import com.harmoni.pos.http.response.RestAPIResponse;
import com.harmoni.pos.menu.model.dto.add.TierMenuEditDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/api/v1")
public class TierMenuController {

    private final TierMenuService tierMenuService;

    @PutMapping("/tier/{tierId}/menu")
    public ResponseEntity<RestAPIResponse> update(@PathVariable Integer tierId,
                                                  @Valid @RequestBody List<TierMenuEditDto> tierMenuEditDtos) {

        tierMenuService.create(tierId, tierMenuEditDtos);
        RestAPIResponse restAPIResponse = RestAPIResponse.builder().build();

        return new ResponseEntity<>(restAPIResponse, HttpStatus.CREATED);
    }

    @GetMapping("/tier/menu")
    public ResponseEntity<RestAPIResponse> getByBrandId(@RequestParam(name = "brandId") Integer brandId) {
        RestAPIResponse restAPIResponse = RestAPIResponse.builder()
                .httpStatus(HttpStatus.OK.value())
                .data(tierMenuService.getMenusByBrandId(brandId))
                .build();
        return new ResponseEntity<>(restAPIResponse, HttpStatus.OK);
    }

}
