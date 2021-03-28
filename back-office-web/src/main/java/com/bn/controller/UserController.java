package com.bn.controller;

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
import java.util.Optional;

@RequestMapping("v1/users")
@RestController
@Slf4j
public class UserController {
    private UserService userService;

    @GetMapping("{id}")
    public User getUser(@NotNull @PathVariable Long id) {
        log.info("Get user - {}", id);
        Optional<User> user = userService.get(id);
        return user.get();
    }

    @PostMapping
    public Long createUser(@Valid @RequestBody CreateUserRequest request) {
        log.info("Create user - {}", request.getMobilePhone());
        User user = User.builder()
            .name(request.getName())
            .mobilePhone(request.getMobilePhone())
            .password(request.getPassword())
            .build();
        User savedUser = userService.create(user);
        return savedUser.getId();
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}
