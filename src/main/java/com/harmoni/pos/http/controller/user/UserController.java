package com.harmoni.pos.http.controller.user;

import com.harmoni.pos.business.service.user.UserService;
import com.harmoni.pos.http.response.RestAPIResponse;
import com.harmoni.pos.menu.model.User;
import com.harmoni.pos.menu.model.dto.UserDto;
import com.harmoni.pos.menu.model.dto.edit.UserEditDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing User entities.
 */
@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;

    /**
     * Creates a new User.
     *
     * @param userDto the user data transfer object
     * @param username the username
     * @return ResponseEntity with creation status
     * @throws Exception if an error occurs during creation
     */
    @PostMapping("")
    public ResponseEntity<RestAPIResponse> createUser(@Valid @RequestBody UserDto userDto,
                                                      @RequestHeader("X-Username") String username) throws Exception {
        int id = this.userService.insert(username, userDto);
        log.debug("tier created {} ", id);
        return new ResponseEntity<>(RestAPIResponse.builder().build(), HttpStatus.CREATED);
    }

    /**
     * Retrieves Users by Chain ID with pagination and search.
     *
     * @param chainId the chain ID
     * @param page the page number
     * @param size the page size
     * @param search the search term
     * @return ResponseEntity containing users for the chain
     */
    @GetMapping("/chain/{chainId}")
    public ResponseEntity<RestAPIResponse> getByCategoryBrand(@PathVariable Integer chainId,
                                                              @RequestParam(name = "page") int page,
                                                              @RequestParam(name = "size") int size,
                                                              @RequestParam(name = "search") String search) {

        RestAPIResponse restAPIResponse = RestAPIResponse.builder()
                .httpStatus(HttpStatus.OK.value())
                .data(this.userService.selectByChainId(chainId, page, size, search))
                .error(null)
                .build();

        return new ResponseEntity<>(restAPIResponse, HttpStatus.OK);
    }

    /**
     * Deletes a User by its ID.
     *
     * @param id the user ID
     * @param username the username
     * @return ResponseEntity with deletion status
     * @throws Exception if an error occurs during deletion
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<RestAPIResponse> deleteUser(@PathVariable Integer id, @RequestHeader("X-Username") String username) throws Exception {
        int rowCount = this.userService.delete(username, id);
        log.debug("user deleted {} ", rowCount);
        RestAPIResponse restAPIResponse = RestAPIResponse.builder().build();
        return new ResponseEntity<>(restAPIResponse, HttpStatus.OK);
    }

    /**
     * Updates a User by its ID.
     *
     * @param id the user ID
     * @param username the username
     * @param userEditDto the user edit data transfer object
     * @return ResponseEntity with update status
     * @throws Exception if an error occurs during update
     */
    @PutMapping("/{id}")
    public ResponseEntity<RestAPIResponse> updateUser(@PathVariable Integer id, @RequestHeader("X-Username") String username,
                    @Valid @RequestBody UserEditDto userEditDto) throws Exception {
        int rowCount = this.userService.update(username, userEditDto);
        log.debug("user updated {} ", rowCount);
        RestAPIResponse restAPIResponse = RestAPIResponse.builder().build();
        return new ResponseEntity<>(restAPIResponse, HttpStatus.OK);
    }

    /**
     * Retrieves a User by username.
     *
     * @param username the username of the user
     * @return ResponseEntity containing the user data
     * @throws Exception if an error occurs during retrieval
     */
    @GetMapping("/{username}")
    public ResponseEntity<RestAPIResponse> detailUser(@PathVariable String username) throws Exception {
        User user = this.userService.selectByUsername(username);
        log.debug("user detail {} ", user);
        RestAPIResponse restAPIResponse = RestAPIResponse.builder()
                .data(user)
                .build();
        return new ResponseEntity<>(restAPIResponse, HttpStatus.OK);
    }
}
