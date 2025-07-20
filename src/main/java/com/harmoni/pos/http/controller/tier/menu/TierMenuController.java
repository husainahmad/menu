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

/**
 * REST controller for managing Tier Menu entities.
 */
@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/api/v1/tier")
public class TierMenuController {

    private final TierMenuService tierMenuService;

    /**
     * Updates Tier Menus for a Tier.
     *
     * @param tierId the tier ID
     * @param tierMenuEditDtos the list of tier menu edit data transfer objects
     * @return ResponseEntity with update status
     */
    @PutMapping("/{tierId}/menu")
    public ResponseEntity<RestAPIResponse> update(@PathVariable Integer tierId,
                                                  @Valid @RequestBody List<TierMenuEditDto> tierMenuEditDtos) {
        tierMenuService.create(tierId, tierMenuEditDtos);
        return new ResponseEntity<>(RestAPIResponse.builder().build(), HttpStatus.CREATED);
    }

    /**
     * Retrieves Tier Menus by Brand ID.
     *
     * @param brandId the brand ID
     * @return ResponseEntity containing tier menus for the brand
     */
    @GetMapping("/menu")
    public ResponseEntity<RestAPIResponse> getByBrandId(@RequestParam(name = "brandId") Integer brandId) {
        RestAPIResponse restAPIResponse = RestAPIResponse.builder()
                .httpStatus(HttpStatus.OK.value())
                .data(tierMenuService.getMenusByBrandId(brandId))
                .build();
        return new ResponseEntity<>(restAPIResponse, HttpStatus.OK);
    }

}
