package com.bn.authorization;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class AuthorizationInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        if (handler instanceof HandlerMethod) {
            HandlerMethod method = (HandlerMethod) handler;
            AuthorizationRequired authRequired = method.getMethodAnnotation(AuthorizationRequired.class);
            if (authRequired == null) {
                return true;
            }

            String authorizationString = request.getHeader("Authorization");
            log.info("Authorization token - {}", authorizationString);
            String[] authorizationElements = authorizationString.split(" ");
            if (authorizationElements.length != 2 && StrUtil.isEmptyIfStr(authorizationElements[1])) {
                log.warn("Authorization token mismatched");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED); // TODO FIXME
                return false;
            }

            String token = authorizationElements[1];
            JWTPayload payload = JWT.verifyToken(token);
            log.info("Authorized user - {}", payload.getUserId());
            // TODO verify user authorities
        }

        return true;
    }
}
