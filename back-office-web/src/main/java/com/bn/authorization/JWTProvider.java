package com.bn.authorization;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.json.JSONUtil;
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
import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.util.UUID;

@Slf4j
public class JWTProvider {
    private static final String JWT_SECURE_KEY = SecureUtil.md5("sth.private.as.the.key");
    private static final long JWT_MAX_AGE = 60 * 60 * 8 * 1000; // 8h

    public static String generateToken(UserAuthorization auth) {
        JWSHeader jwsHeader = new JWSHeader.Builder(JWSAlgorithm.HS256).type(JOSEObjectType.JWT).build();
        // create payload with user info
        Payload payload = new Payload(JSONUtil.toJsonStr(userToPayload(auth)));
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

    private static JWTPayload userToPayload(UserAuthorization auth) {
        long currentTime = System.currentTimeMillis();
        return JWTPayload.builder()
            .sub(StrUtil.format("JWS - {}", auth.getUserName()))
            .iat(currentTime)
            .exp(currentTime + JWT_MAX_AGE)
            .jti(UUID.randomUUID().toString())
            .auth(auth)
            .build();
    }

    public static JWTVerificationResult verifyToken(String token) {
        String payloadText;
        try {
            // Parse token to JWT object
            JWSObject jwsObject = JWSObject.parse(token);
            // verify JWT object
            JWSVerifier jwsVerifier = new MACVerifier(JWT_SECURE_KEY);
            if (!jwsObject.verify(jwsVerifier)) {
                return JWTVerificationResult.fail("Token verification failure");
            }

            payloadText = jwsObject.getPayload().toString();
        } catch (ParseException | JOSEException ex) {
            log.error("JWT Verification Error", ex);
            return JWTVerificationResult.fail("Token verification failure");
        }

        JWTPayload payload = JSONUtil.toBean(payloadText, JWTPayload.class);
        if (payload.getExp() < System.currentTimeMillis()) {
            return JWTVerificationResult.fail("Token is expired");
        }

        return JWTVerificationResult.pass(payload.getAuth());
    }
}
