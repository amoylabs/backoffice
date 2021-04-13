package com.bn.controller;

import cn.hutool.core.codec.Base64;
import com.bn.controller.request.CreateUserRequest;
import com.bn.controller.response.GetUserResponse;
import com.bn.domain.User;
import com.bn.service.UserService;
import com.bn.web.authorization.UserAuthorizationRequired;
import com.bn.web.authorization.UserRealm;
import com.bn.web.authorization.UserRealmContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@RequestMapping("v1/users")
@RestController
@Slf4j
public class UserController {
    private UserService userService;

    @GetMapping("{id}")
    public GetUserResponse getUser(@PathVariable @NotNull Long id) {
        log.info("Get user - {}", id);
        User user = userService.get(id);
        return GetUserResponse.builder()
            .id(user.getId())
            .name(user.getName())
            .email(user.getEmail())
            .mobilePhone(user.getMobilePhone())
            .status(user.getStatus().name())
            .build();
    }

    @PostMapping
    @UserAuthorizationRequired("ADMIN")
    @ResponseStatus(HttpStatus.CREATED)
    public Long createUser(@Valid @RequestBody CreateUserRequest request) {
        log.info("Create user - {}", request.getMobilePhone());
        User user = User.builder()
            .name(request.getName())
            .mobilePhone(request.getMobilePhone())
            .email(request.getEmail())
            .password(Base64.decodeStr(request.getPassword()))
            .build();
        UserRealm context = Objects.requireNonNull(UserRealmContextHolder.get());
        return userService.create(user, context.getUserName());
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}
