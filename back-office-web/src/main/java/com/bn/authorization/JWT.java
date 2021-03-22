package com.bn.authorization;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.json.JSONUtil;
import com.bn.domain.User;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;

import java.text.ParseException;
import java.util.List;
import java.util.UUID;

public class JWT {
    private static final String JWT_SECURE_KEY = SecureUtil.md5("sth.private.as.the.key");
    private static final long JWT_MAX_AGE = 60 * 60 * 8 * 1000; // 8h

    public static String generateUserToken(User user) {
        JWSHeader jwsHeader = new JWSHeader.Builder(JWSAlgorithm.HS256).type(JOSEObjectType.JWT).build();
        // create payload with user info
        Payload payload = new Payload(JSONUtil.toJsonStr(userToPayload(user)));
        // create JWS object
        JWSObject jwsObject = new JWSObject(jwsHeader, payload);
        try {
            // sign with HMAC
            JWSSigner jwsSigner = new MACSigner(JWT_SECURE_KEY);
            jwsObject.sign(jwsSigner);
        } catch (JOSEException ex) {
            throw new JWTException("JWT_USER_TOKEN_FAILURE", ex.getLocalizedMessage(), ex);
        }

        return jwsObject.serialize();
    }

    private static JWTPayload userToPayload(User user) {
        long currentTime = System.currentTimeMillis();
        return JWTPayload.builder()
            .sub(user.getName())
            .iat(currentTime)
            .exp(currentTime + JWT_MAX_AGE)
            .jti(UUID.randomUUID().toString())
            .userId(user.getName())
            .authorities(List.of()) // TODO
            .build();
    }

    public static JWTPayload verifyToken(String token) {
        String payloadText;
        try {
            // Parse token to JWT object
            JWSObject jwsObject = JWSObject.parse(token);
            // verify JWT object
            JWSVerifier jwsVerifier = new MACVerifier(JWT_SECURE_KEY);
            if (!jwsObject.verify(jwsVerifier)) {
                throw new AuthorizationException("JWT token is illegal by verified"); // FIXME
            }

            payloadText = jwsObject.getPayload().toString();
        } catch (ParseException | JOSEException ex) {
            throw new JWTException("JWT_VERIFY_TOKEN_ERROR", ex.getLocalizedMessage(), ex); // FIXME
        }

        JWTPayload payload = JSONUtil.toBean(payloadText, JWTPayload.class);
        if (payload.getExp() < System.currentTimeMillis()) {
            throw new AuthorizationException("JWT token has been expired"); // FIXME
        }

        return payload;
    }
}
