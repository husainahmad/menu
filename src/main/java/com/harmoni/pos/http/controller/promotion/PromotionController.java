package com.harmoni.pos.http.controller.promotion;

import com.harmoni.pos.business.service.promotion.PromotionService;
import com.harmoni.pos.http.response.RestAPIResponse;
import com.harmoni.pos.menu.model.Promotion;
import com.harmoni.pos.menu.model.PromotionStatus;
import com.harmoni.pos.menu.model.PromotionType;
import com.harmoni.pos.menu.model.dto.add.PromotionAddDto;
import com.harmoni.pos.menu.model.dto.edit.PromotionEditDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing Promotion entities.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/promotion")
@Slf4j
public class PromotionController {

    private final PromotionService promotionService;

    /**
     * Creates a promotion along with its schedules, targets, rules and special prices.
     *
     * @param promotionAddDto the aggregate to create
     * @return the created promotion
     */
    @PostMapping("")
    public ResponseEntity<RestAPIResponse> create(@Valid @RequestBody PromotionAddDto promotionAddDto) {
        Promotion promotion = promotionService.create(promotionAddDto);
        log.debug("Promotion created {} ", promotion.getId());
        return new ResponseEntity<>(RestAPIResponse.builder()
                .httpStatus(HttpStatus.CREATED.value())
                .data(promotion)
                .build(), HttpStatus.CREATED);
    }

    /**
     * Retrieves a paginated, optionally filtered list of promotions.
     *
     * @param status        optional lifecycle state filter
     * @param promotionType optional mechanism filter
     * @param search        optional keyword matched against code and name
     * @param page          the 1-based page number
     * @param size          the page size
     * @return a page of promotions
     */
    @GetMapping("")
    public ResponseEntity<RestAPIResponse> list(@RequestParam(name = "status", required = false) PromotionStatus status,
                                                @RequestParam(name = "promotionType", required = false) PromotionType promotionType,
                                                @RequestParam(name = "search", required = false) String search,
                                                @RequestParam(name = "page") int page,
                                                @RequestParam(name = "size") int size) {
        RestAPIResponse restAPIResponse = RestAPIResponse.builder()
                .httpStatus(HttpStatus.OK.value())
                .data(promotionService.listPaginated(status, promotionType, search, page, size))
                .build();
        return new ResponseEntity<>(restAPIResponse, HttpStatus.OK);
    }

    /**
     * Retrieves a promotion with all child collections populated.
     *
     * @param id the promotion ID
     * @return the promotion
     */
    @GetMapping("/{id:\\d+}")
    public ResponseEntity<RestAPIResponse> get(@PathVariable("id") Long id) {
        RestAPIResponse restAPIResponse = RestAPIResponse.builder()
                .httpStatus(HttpStatus.OK.value())
                .data(promotionService.get(id))
                .error(null)
                .build();
        return new ResponseEntity<>(restAPIResponse, HttpStatus.OK);
    }

    /**
     * Retrieves a promotion by its unique code, without child collections.
     *
     * @param code the promotion code
     * @return the promotion
     */
    @GetMapping("/code/{code}")
    public ResponseEntity<RestAPIResponse> getByCode(@PathVariable("code") String code) {
        RestAPIResponse restAPIResponse = RestAPIResponse.builder()
                .httpStatus(HttpStatus.OK.value())
                .data(promotionService.getByCode(code))
                .error(null)
                .build();
        return new ResponseEntity<>(restAPIResponse, HttpStatus.OK);
    }

    /**
     * Retrieves the promotions that may be redeemed right now.
     *
     * @return list of promotions ordered by priority
     */
    @GetMapping("/redeemable")
    public ResponseEntity<RestAPIResponse> listRedeemable() {
        RestAPIResponse restAPIResponse = RestAPIResponse.builder()
                .httpStatus(HttpStatus.OK.value())
                .data(promotionService.listRedeemable())
                .build();
        return new ResponseEntity<>(restAPIResponse, HttpStatus.OK);
    }

    /**
     * Updates a promotion. An omitted child collection is left untouched, an empty
     * one is cleared.
     *
     * @param promotionEditDto the aggregate to update
     * @return the updated promotion
     */
    @PutMapping("")
    public ResponseEntity<RestAPIResponse> put(@Valid @RequestBody PromotionEditDto promotionEditDto) {
        RestAPIResponse restAPIResponse = RestAPIResponse.builder()
                .httpStatus(HttpStatus.OK.value())
                .data(promotionService.update(promotionEditDto))
                .error(null)
                .build();
        return new ResponseEntity<>(restAPIResponse, HttpStatus.OK);
    }

    /**
     * Moves a promotion to a new lifecycle state without touching its configuration.
     *
     * @param id     the promotion ID
     * @param status the new lifecycle state
     * @return the number of updated rows
     */
    @PatchMapping("/{id:\\d+}/status")
    public ResponseEntity<RestAPIResponse> patchStatus(@PathVariable("id") Long id,
                                                       @RequestParam("status") PromotionStatus status) {
        RestAPIResponse restAPIResponse = RestAPIResponse.builder()
                .httpStatus(HttpStatus.OK.value())
                .data(promotionService.updateStatus(id, status))
                .error(null)
                .build();
        return new ResponseEntity<>(restAPIResponse, HttpStatus.OK);
    }

    /**
     * Deletes a promotion and its child collections.
     *
     * @param id the promotion ID
     * @return the number of deleted rows
     */
    @DeleteMapping("/{id:\\d+}")
    public ResponseEntity<RestAPIResponse> delete(@PathVariable("id") Long id) {
        RestAPIResponse restAPIResponse = RestAPIResponse.builder()
                .httpStatus(HttpStatus.OK.value())
                .data(promotionService.delete(id))
                .error(null)
                .build();
        return new ResponseEntity<>(restAPIResponse, HttpStatus.OK);
    }

    /**
     * Deletes every promotion matching the given filter.
     *
     * @param status        optional lifecycle state filter
     * @param promotionType optional mechanism filter
     * @param search        optional keyword matched against code and name
     * @return the number of deleted rows
     */
    @DeleteMapping("")
    public ResponseEntity<RestAPIResponse> deleteByFilter(@RequestParam(name = "status", required = false) PromotionStatus status,
                                                          @RequestParam(name = "promotionType", required = false) PromotionType promotionType,
                                                          @RequestParam(name = "search", required = false) String search) {
        RestAPIResponse restAPIResponse = RestAPIResponse.builder()
                .httpStatus(HttpStatus.OK.value())
                .data(promotionService.deleteByFilter(status, promotionType, search))
                .error(null)
                .build();
        return new ResponseEntity<>(restAPIResponse, HttpStatus.OK);
    }
}
