package com.harmoni.pos.http.controller.tier.menu;

import com.harmoni.pos.business.service.tier.tiermenu.TierMenuService;
import com.harmoni.pos.http.response.RestAPIResponse;
import com.harmoni.pos.menu.model.dto.add.TierMenuEditDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/api/v1")
public class TierMenuController {

    private final TierMenuService tierMenuService;

    @PutMapping("/tier/{tierId}/menu")
    public ResponseEntity<RestAPIResponse> update(@PathVariable @Pattern(regexp = "\\d+", message = "{validation.tier.id.NotNull}") Integer tierId,
                                                  @Valid @RequestBody TierMenuEditDto tierMenuAddDto) {

        tierMenuService.create(tierId, tierMenuAddDto);
        RestAPIResponse restAPIResponse = RestAPIResponse.builder().build();

        return new ResponseEntity<>(restAPIResponse, HttpStatus.CREATED);
    }

}
