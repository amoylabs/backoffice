package com.bn.controller;

import com.bn.service.UserService;
import com.bn.web.api.ApiIdempotence;
import com.bn.web.api.ApiRateLimiter;
import com.bn.web.authorization.UserAuthorizationRequired;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("v1/tests")
@RestController
@Slf4j
public class TestController {
    private UserService userService;

    @PostMapping("async")
    public void executeAsyncTask() {
        log.info("Execute Async Task : {}", System.currentTimeMillis());
        userService.doSthAsync();
        log.info("End immediately : {}", System.currentTimeMillis());
    }

    @PostMapping("api-idempotence")
    @ApiIdempotence
    public void submitWithApiIdempotence() {
        log.info("Submit Successfully");
    }

    @PostMapping("api-limit")
    @ApiRateLimiter(rate = 5, rateInterval = 10)
    public void testApiLimit() {
        log.info("Access Successfully");
    }

    @PostMapping("api-user-limit")
    @ApiRateLimiter(userBase = true, rate = 5, rateInterval = 10)
    public String testApiUserBaseLimit() {
        return "OK";
    }

    @GetMapping("a")
    public String testA() {
        return "You got A";
    }

    @GetMapping("b")
    @UserAuthorizationRequired("TEST")
    public String testB() {
        return "You got B";
    }

    @GetMapping("c")
    @UserAuthorizationRequired("STH-ELSE")
    public String testC() {
        return "You got C";
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}
