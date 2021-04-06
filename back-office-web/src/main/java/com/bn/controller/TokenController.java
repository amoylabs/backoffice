package com.bn.controller;

import com.bn.service.TokenService;
import com.bn.web.token.ApiIdempotence;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("v1/token")
@RestController
@Slf4j
public class TokenController {
    private TokenService tokenService;

    @GetMapping
    public String generate() {
        return tokenService.generateIdempotentApiToken();
    }

    @PostMapping
    @ApiIdempotence
    public void submit() {
        log.info("Submit Successfully");
    }

    @Autowired
    public void setTokenService(TokenService tokenService) {
        this.tokenService = tokenService;
    }
}
