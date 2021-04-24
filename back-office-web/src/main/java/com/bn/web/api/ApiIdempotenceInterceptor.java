package com.bn.web.api;

import cn.hutool.core.util.StrUtil;
import com.bn.service.TokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Slf4j
public class ApiIdempotenceInterceptor implements HandlerInterceptor {
    private static final String IDEMPOTENT_API_TOKEN = "Idempotent-Api-Token";

    private TokenService tokenService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        if (!isMarkAsIdempotentAPI((HandlerMethod) handler)) {
            return true;
        }

        String token = request.getHeader(IDEMPOTENT_API_TOKEN);
        if (StrUtil.isNotBlank(token) && tokenService.isIdempotentApiTokenConsumed(token)) {
            return true;
        }

        sendUnauthorizedError(response);
        return false;
    }

    private boolean isMarkAsIdempotentAPI(HandlerMethod method) {
        return method.getMethodAnnotation(ApiIdempotence.class) != null;
    }

    private void sendUnauthorizedError(HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.getWriter().write("Duplicated request on the idempotent API");
    }

    @Autowired
    public void setTokenService(TokenService tokenService) {
        this.tokenService = tokenService;
    }
}
