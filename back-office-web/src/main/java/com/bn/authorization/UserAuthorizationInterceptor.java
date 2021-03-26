package com.bn.authorization;

import cn.hutool.core.util.ArrayUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

@Slf4j
public class UserAuthorizationInterceptor implements HandlerInterceptor {
    private static final String USER_AUTH_FAILURE_MESSAGE = "Lack of user authority";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        if (handler instanceof HandlerMethod) {
            final UserAuthorizationRequired authRequired = getRequiredAuthorization((HandlerMethod) handler);
            if (authRequired == null || ArrayUtil.isEmpty(authRequired.value())) {
                return true;
            }

            UserRealm userRealm = UserRealmContextHolder.get();
            if (userRealm == null) {
                sendUnauthorizedError(response);
                return false;
            }

            log.info("Authorized user - {}:{}", userRealm.getUserId(), userRealm.getUserName());
            if (userRealm.getRealms() == null || userRealm.getRealms().isEmpty()) {
                sendUnauthorizedError(response);
                return false;
            }

            if (userRealm.getRealms().contains(UserRealm.ADMINISTER_AUTH)) {
                return true;
            }

            // User authorities should contains all of the required authorities
            if (Arrays.stream(authRequired.value()).anyMatch(requiredAuth -> !userRealm.getRealms().contains(requiredAuth))) {
                sendUnauthorizedError(response);
                return false;
            }
        }

        return true;
    }

    private UserAuthorizationRequired getRequiredAuthorization(HandlerMethod method) {
        UserAuthorizationRequired authRequiredOnMethod = method.getMethodAnnotation(UserAuthorizationRequired.class);
        if (authRequiredOnMethod != null) {
            return authRequiredOnMethod;
        }

        return method.getBean().getClass().getDeclaredAnnotation(UserAuthorizationRequired.class);
    }

    private void sendUnauthorizedError(HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.getWriter().write(USER_AUTH_FAILURE_MESSAGE);
    }
}
