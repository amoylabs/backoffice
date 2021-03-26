package com.bn.authorization;

import cn.hutool.core.util.ArrayUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

@Slf4j
public class AuthorizationInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        if (handler instanceof HandlerMethod) {
            final AuthorizationRequired authRequired = getRequiredAuthorization((HandlerMethod) handler);
            if (authRequired == null || ArrayUtil.isEmpty(authRequired.value())) {
                return true;
            }

            Object userAuthContext = RequestContextHolder.currentRequestAttributes().getAttribute(UserAuthorization.USER_AUTH_CONTEXT_NAME, RequestAttributes.SCOPE_REQUEST);
            if (!(userAuthContext instanceof UserAuthorization)) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Lack of user authority");
                return false;
            }

            UserAuthorization auth = (UserAuthorization) userAuthContext;
            log.info("Authorized user - {}:{}", auth.getUserId(), auth.getUserName());
            if (auth.getAuthorities() == null || auth.getAuthorities().isEmpty()) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Lack of user authority");
                return false;
            }

            if (auth.getAuthorities().contains(UserAuthorization.ADMINISTER_AUTH)) {
                return true;
            }

            // User authorities should contains all of the required authorities
            if (Arrays.stream(authRequired.value()).anyMatch(requiredAuth -> !auth.getAuthorities().contains(requiredAuth))) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Lack of user authority");
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

        return method.getBean().getClass().getDeclaredAnnotation(AuthorizationRequired.class);
    }
}
