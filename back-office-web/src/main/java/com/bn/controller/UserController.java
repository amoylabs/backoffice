package com.bn.controller;

import com.bn.authorization.UserAuthorizationRequired;
import com.bn.authorization.JWTProvider;
import com.bn.authorization.UserAuthorization;
import com.bn.controller.request.CreateUserRequest;
import com.bn.domain.User;
import com.bn.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@RequestMapping("v1/users")
@RestController
@Slf4j
public class UserController {
    private UserService userService;

    @GetMapping("{id}")
    public User getUser(@NotNull @PathVariable Long id) {
        log.info("Get user - {}", id);
        return userService.get(id);
    }

    @PostMapping
    public Long createUser(@Valid @RequestBody CreateUserRequest request) {
        log.info("Create user - {}", request.getMobilePhone());
        User user = User.builder()
            .name(request.getName())
            .mobilePhone(request.getMobilePhone())
            .email(request.getEmail())
            .password(request.getPassword())
            .build();
        return userService.create(user);
    }

    @PostMapping("auth")
    public String authorize() {
        UserAuthorization auth = UserAuthorization.builder()
            .userId(String.valueOf(ThreadLocalRandom.current().nextLong()))
            .userName("HACK")
            .authorities(List.of(UserAuthorization.ADMINISTER_AUTH))
            .build(); // FIXME hack user
        return JWTProvider.generateToken(auth);
    }

    @GetMapping("auth/test")
    @UserAuthorizationRequired("test")
    public void testAuthorization() {
        log.info("Authorization is working nicely!");
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}
