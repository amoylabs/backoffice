package com.bn.web.filter;

import cn.hutool.core.util.StrUtil;
import com.bn.authorization.JWTProvider;
import com.bn.authorization.JWTVerificationResult;
import com.bn.authorization.UserAuthorization;
import com.bn.authorization.UserAuthorizationContextHolder;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

@Component
@Order(1)
public class UserAuthorizationFilter implements Filter {
    private static final String AUTHORIZATION_HEADER_NAME = "Authorization";
    private static final String AUTHORIZATION_TOKEN_PREFIX = "Bear ";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String authorizationString = httpServletRequest.getHeader(AUTHORIZATION_HEADER_NAME);
        if (StrUtil.isBlankIfStr(authorizationString)) {
            chain.doFilter(request, response);
            return;
        }

        if (authorizationString.indexOf(AUTHORIZATION_TOKEN_PREFIX) != 0) { // Should be "Bear xxx.xxx.xxx"
            sendError(response, HttpStatus.PRECONDITION_FAILED.value(), "Incorrect token");
            return;
        }

        String token = authorizationString.replace(AUTHORIZATION_TOKEN_PREFIX, ""); // Remove prefix
        JWTVerificationResult verificationResult = JWTProvider.verifyToken(token);
        if (!verificationResult.isPassed()) {
            sendError(response, HttpStatus.UNAUTHORIZED.value(), verificationResult.getFailureMessage());
            return;
        }

        UserAuthorization auth = Objects.requireNonNull(verificationResult.getAuth(), "User authorization should NOT be null");
        UserAuthorizationContextHolder.set(auth);

        try {
            chain.doFilter(request, response);
        } finally {
            UserAuthorizationContextHolder.clear(); // !!! SUPER IMPORTANT to clear context
        }
    }

    private void sendError(ServletResponse response, int code, String message) throws IOException {
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        httpServletResponse.setStatus(code);
        httpServletResponse.getWriter().write(message);
    }
}
