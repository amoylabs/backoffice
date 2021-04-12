package com.bn.controller;

import cn.hutool.core.codec.Base64;
import cn.hutool.crypto.digest.HMac;
import cn.hutool.crypto.digest.HmacAlgorithm;
import com.bn.controller.request.AuthorizeUserRequest;
import com.bn.domain.App;
import com.bn.domain.User;
import com.bn.exception.BadRequestException;
import com.bn.service.AppService;
import com.bn.service.UserService;
import com.bn.util.PasswordUtils;
import com.bn.web.authorization.JWTProvider;
import com.bn.web.authorization.UserRealm;
import com.bn.web.validation.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@RequestMapping("v1/auth")
@RestController
@Validated
@Slf4j
public class AuthorizationController {
    private AppService appService;
    private UserService userService;

    @PostMapping("hacker")
    public String authorizeHacker() {
        UserRealm auth = UserRealm.builder()
            .userId(String.valueOf(ThreadLocalRandom.current().nextLong()))
            .userName("Hacker")
            .realms(List.of(UserRealm.SUPER))
            .build(); // TODO should be removed
        return JWTProvider.generateToken(auth);
    }

    @PostMapping("app/{appId}")
    public String signingApp(@PathVariable @UUID String appId, @RequestHeader("hmac") @NotEmpty String hmac) {
        App app = appService.get(appId);
        byte[] key = app.getSigningKey().getBytes(StandardCharsets.UTF_8);
        HMac mac = new HMac(HmacAlgorithm.HmacSHA256, key);
        String macHex = Base64.encode(mac.digest(appId));
        if (!hmac.equals(macHex)) {
            log.warn("API signing - {} : {}", hmac, macHex);
            throw new BadRequestException("API Signing failure");
        }

        UserRealm auth = UserRealm.builder()
            .userId(appId)
            .userName(app.getName())
            .realms(List.of(UserRealm.SUPER))
            .build();
        return JWTProvider.generateToken(auth);
    }

    @PostMapping("user")
    public String authorizeUser(@Valid @RequestBody AuthorizeUserRequest request) {
        User user = userService.get(request.getUn());
        String password = Base64.decodeStr(request.getPd());
        if (!PasswordUtils.verifyPassword(password, user.getPassword(), user.getPasswordSalt())) {
            throw new BadRequestException("User authorization failure");
        }

        UserRealm auth = UserRealm.builder()
            .userId(String.valueOf(user.getId()))
            .userName(user.getName())
            .realms(List.of()) // TODO to load user realms and set here
            .build();
        return JWTProvider.generateToken(auth);
    }

    @Autowired
    public void setAppService(AppService appService) {
        this.appService = appService;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}
