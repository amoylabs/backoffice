package com.bn.controller;

import com.bn.web.authorization.JWTProvider;
import com.bn.web.authorization.UserRealm;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@RequestMapping("v1/auth")
@RestController
public class AuthorizationController {
    @PostMapping("hacker")
    public String authorize() {
        UserRealm auth = UserRealm.builder()
            .userId(String.valueOf(ThreadLocalRandom.current().nextLong()))
            .userName("Hacker")
            .realms(List.of(UserRealm.SUPER))
            .build(); // TODO should be removed
        return JWTProvider.generateToken(auth);
    }
}
