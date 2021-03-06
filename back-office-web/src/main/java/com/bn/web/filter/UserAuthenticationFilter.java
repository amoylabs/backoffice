package com.bn.web.filter;

import cn.hutool.core.util.StrUtil;
import com.bn.web.authorization.JWTProvider;
import com.bn.web.authorization.UserAuthorization;
import com.bn.web.authorization.UserRealm;
import com.bn.web.authorization.UserRealmContextHolder;
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
public class UserAuthenticationFilter implements Filter {
    private static final String AUTHORIZATION_HEADER_NAME = "Authorization";
    private static final String AUTHORIZATION_TOKEN_PREFIX = "Bear ";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String authorizationString = httpServletRequest.getHeader(AUTHORIZATION_HEADER_NAME);
        if (StrUtil.isNotBlank(authorizationString)) {
            HttpServletResponse httpServletResponse = (HttpServletResponse) response;
            if (authorizationString.indexOf(AUTHORIZATION_TOKEN_PREFIX) != 0) { // Should be "Bear xxx.xxx.xxx"
                httpServletResponse.setStatus(HttpStatus.PRECONDITION_FAILED.value());
                httpServletResponse.getWriter().write("Incorrect token");
                return;
            }

            String token = authorizationString.replace(AUTHORIZATION_TOKEN_PREFIX, ""); // Remove prefix
            UserAuthorization verificationResult = JWTProvider.verifyToken(token);
            if (!verificationResult.isValid()) {
                httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
                httpServletResponse.getWriter().write(verificationResult.getFailureMessage());
                return;
            }

            UserRealm userRealm = Objects.requireNonNull(verificationResult.getUserRealm(), "User realm should NOT be null");
            UserRealmContextHolder.set(userRealm);
        } else {
            UserRealmContextHolder.set(UserRealm.anonymous()); // ensure safely access to User Realm Context
        }

        try {
            chain.doFilter(request, response);
        } finally {
            UserRealmContextHolder.clear(); // !!! SUPER IMPORTANT to clear context
        }
    }
}
