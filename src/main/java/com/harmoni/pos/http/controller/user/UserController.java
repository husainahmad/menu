package com.harmoni.pos.http.controller.user;

import com.harmoni.pos.business.service.user.UserService;
import com.harmoni.pos.component.JwtUtil;
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

@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @PostMapping("")
    public ResponseEntity<RestAPIResponse> createUser(@Valid @RequestBody UserDto userDto,
                                                      @RequestHeader("Authorization") String authHeader) throws Exception {
        int id = this.userService.insert(jwtUtil.getTokenFromBearer(authHeader), userDto);
        log.debug("tier created {} ", id);
        return new ResponseEntity<>(RestAPIResponse.builder().build(), HttpStatus.CREATED);
    }

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

    @DeleteMapping("/{id}")
    public ResponseEntity<RestAPIResponse> deleteUser(@PathVariable Integer id, @RequestHeader("Authorization") String authHeader) throws Exception {
        int rowCount = this.userService.delete(jwtUtil.getTokenFromBearer(authHeader), id);
        log.debug("user deleted {} ", rowCount);
        RestAPIResponse restAPIResponse = RestAPIResponse.builder().build();
        return new ResponseEntity<>(restAPIResponse, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RestAPIResponse> updateUser(@PathVariable Integer id, @RequestHeader("Authorization") String authHeader,
                    @Valid @RequestBody UserEditDto userEditDto) throws Exception {
        int rowCount = this.userService.update(jwtUtil.getTokenFromBearer(authHeader), userEditDto);
        log.debug("user updated {} ", rowCount);
        RestAPIResponse restAPIResponse = RestAPIResponse.builder().build();
        return new ResponseEntity<>(restAPIResponse, HttpStatus.OK);
    }

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
