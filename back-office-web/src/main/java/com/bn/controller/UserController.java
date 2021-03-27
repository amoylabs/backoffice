package com.bn.controller;

import com.bn.authorization.JWTProvider;
import com.bn.authorization.UserAuthorizationRequired;
import com.bn.authorization.UserRealm;
import com.bn.authorization.UserRealmContextHolder;
import com.bn.controller.request.CreateUserRequest;
import com.bn.controller.response.UserVO;
import com.bn.domain.User;
import com.bn.service.UserService;
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
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

@RequestMapping("v1/users")
@RestController
@Slf4j
public class UserController {
    private UserService userService;

    @GetMapping("{id}")
    public UserVO getUser(@NotNull @PathVariable Long id) {
        log.info("Get user - {}", id);
        User user = userService.get(id);
        return UserVO.builder()
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
            .password(request.getPassword())
            .build();
        UserRealm context = Objects.requireNonNull(UserRealmContextHolder.get());
        return userService.create(user, context.getUserName());
    }

    @PostMapping("auth/hack")
    public String authorize() {
        UserRealm auth = UserRealm.builder()
            .userId(String.valueOf(ThreadLocalRandom.current().nextLong()))
            .userName("HACK")
            .realms(List.of(UserRealm.ADMINISTER_AUTH))
            .build(); // FIXME hack user
        return JWTProvider.generateToken(auth);
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}
