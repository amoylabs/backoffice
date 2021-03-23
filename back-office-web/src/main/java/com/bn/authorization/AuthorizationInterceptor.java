package com.bn.authorization;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

@Slf4j
public class AuthorizationInterceptor implements HandlerInterceptor {
    private static final String AUTHORIZATION_TOKEN_PREFIX = "Bear ";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        if (handler instanceof HandlerMethod) {
            final AuthorizationRequired authRequired = getRequiredAuthorization((HandlerMethod) handler);
            if (authRequired == null || ArrayUtil.isEmpty(authRequired.value())) {
                return true;
            }

            final Optional<String> tokenOptional = getRequestedAuthorizationToken(request);
            if (tokenOptional.isEmpty()) {
                response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED);
                return false;
            }

            Optional<UserAuthorization> authOptional = JWTProvider.verifyToken(tokenOptional.get());
            if (authOptional.isEmpty()) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return false;
            }

            UserAuthorization auth = authOptional.get();
            log.info("Authorized user - {}:{}", auth.getUserId(), auth.getUserName());
            if (auth.getAuthorities() == null || auth.getAuthorities().isEmpty()) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return false;
            }

            if (auth.getAuthorities().contains(UserAuthorization.ADMINISTER_AUTH)) {
                return true;
            }

            // User authorities should contains all of the required authorities
            if (Arrays.stream(authRequired.value()).anyMatch(requiredAuth -> !auth.getAuthorities().contains(requiredAuth))) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return false;
            }
        }

        return true;
    }

    private AuthorizationRequired getRequiredAuthorization(HandlerMethod method) {
        AuthorizationRequired authRequiredOnMethod = method.getMethodAnnotation(AuthorizationRequired.class);
        if (authRequiredOnMethod != null) {
            return authRequiredOnMethod;
        }

        return method.getBean().getClass().getAnnotation(AuthorizationRequired.class);
    }

    private Optional<String> getRequestedAuthorizationToken(HttpServletRequest request) {
        String authorizationString = request.getHeader("Authorization");
        if (StrUtil.isBlankIfStr(authorizationString)) {
            return Optional.empty();
        }

        log.info("Authorization token - {}", authorizationString);
        if (authorizationString.indexOf(AUTHORIZATION_TOKEN_PREFIX) != 0) { // Should be "Bear xxx.xxx.xxx"
            return Optional.empty();
        }

        return Optional.of(authorizationString.replace(authorizationString, AUTHORIZATION_TOKEN_PREFIX));
    }
}
