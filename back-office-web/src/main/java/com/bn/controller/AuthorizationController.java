package com.bn.controller;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.digest.HMac;
import cn.hutool.crypto.digest.HmacAlgorithm;
import com.bn.controller.request.AuthorizeUserRequest;
import com.bn.domain.App;
import com.bn.domain.User;
import com.bn.exception.BadRequestException;
import com.bn.redis.RedisCache;
import com.bn.service.AppService;
import com.bn.service.UserService;
import com.bn.util.PasswordUtils;
import com.bn.web.authorization.JWTProvider;
import com.bn.web.authorization.UserRealm;
import com.bn.web.validation.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@RequestMapping("v1/auth")
@RestController
@Validated
@Slf4j
public class AuthorizationController {
    private AppService appService;
    private UserService userService;
    private RedisCache redisCache;

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

    @GetMapping("rsa-key")
    public String getRsaKey() {
        KeyPair keyPair = SecureUtil.generateKeyPair("RSA");
        String publicKey = Base64.encode(keyPair.getPublic().getEncoded());
        String privateKey = Base64.encode(keyPair.getPrivate().getEncoded());
        redisCache.set(StrUtil.format("rsa:pk:{}", publicKey), privateKey, Duration.ofHours(1));
        return publicKey;
    }

    @PostMapping("user")
    public String authorizeUser(@Valid @RequestBody AuthorizeUserRequest request) {
        User user = userService.get(request.getUn());
        String privateKey = redisCache.get(StrUtil.format("rsa:pk:{}", request.getPk()));
        if (StrUtil.isBlank(privateKey)) {
            log.warn("Could not retrieve private key for {}", request.getPk());
            throw new BadRequestException("User authorization failure");
        }

        redisCache.del(request.getPk());
        String password = SecureUtil.rsa(privateKey, request.getPk()).decryptStr(request.getPd(), KeyType.PrivateKey);
        if (!PasswordUtils.verifyPassword(password, user.getPassword(), user.getPasswordSalt())) {
            log.warn("User authorization - {} : {}", password, user.getPassword()); // TODO Removed as security concerns
            throw new BadRequestException("User authorization failure");
        }

        UserRealm auth = UserRealm.builder()
            .userId(String.valueOf(user.getId()))
            .userName(user.getName())
            .realms(List.of())
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

    @Autowired
    public void setRedisCache(RedisCache redisCache) {
        this.redisCache = redisCache;
    }
}
