package com.bn.web.filter;

import cn.hutool.core.util.StrUtil;
import com.bn.authorization.JWTProvider;
import com.bn.authorization.JWTVerificationResult;
import com.bn.authorization.UserAuthorization;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Order(1)
@Slf4j
public class UserAuthorizationFilter implements Filter {
    private static final String AUTHORIZATION_TOKEN_PREFIX = "Bear ";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String authorizationString = httpServletRequest.getHeader("Authorization");
        if (StrUtil.isBlankIfStr(authorizationString)) {
            chain.doFilter(request, response);
            return;
        }

        log.info("Authorization token - {}", authorizationString);
        if (authorizationString.indexOf(AUTHORIZATION_TOKEN_PREFIX) != 0) { // Should be "Bear xxx.xxx.xxx"
            sendError(response, HttpServletResponse.SC_PRECONDITION_FAILED, "Incorrect token");
            return;
        }

        String token = authorizationString.replace(AUTHORIZATION_TOKEN_PREFIX, ""); // Remove prefix
        JWTVerificationResult verificationResult = JWTProvider.verifyToken(token);
        if (!verificationResult.isPassed()) {
            sendError(response, HttpServletResponse.SC_UNAUTHORIZED, verificationResult.getFailureMessage());
            return;
        }

        UserAuthorization auth = verificationResult.getAuth();
        RequestContextHolder.currentRequestAttributes().setAttribute(UserAuthorization.USER_AUTH_CONTEXT_NAME, auth, RequestAttributes.SCOPE_REQUEST);

        chain.doFilter(request, response);
    }

    private void sendError(ServletResponse response, int code, String message) throws IOException {
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        httpServletResponse.setContentType(MimeTypeUtils.APPLICATION_JSON_VALUE);
        httpServletResponse.sendError(code, message);
    }
}
