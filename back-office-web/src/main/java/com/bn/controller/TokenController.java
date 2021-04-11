package com.bn.controller;

import com.bn.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("v1/token")
@RestController
public class TokenController {
    private TokenService tokenService;

    @GetMapping
    public String generate() {
        return tokenService.generateIdempotentApiToken();
    }

    @Autowired
    public void setTokenService(TokenService tokenService) {
        this.tokenService = tokenService;
    }
}
