package com.bn.web.api;

import com.bn.redis.RateLimiter;
import com.bn.util.WebUtils;
import com.bn.web.authorization.UserRealm;
import com.bn.web.authorization.UserRealmContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Locale;
import java.util.Optional;

@Component
@Slf4j
public class ApiRateLimitInterceptor implements HandlerInterceptor {
    private RateLimiter rateLimiter;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod method = (HandlerMethod) handler;
        ApiRateLimiter apiRateLimiter = method.getMethodAnnotation(ApiRateLimiter.class);
        if (apiRateLimiter == null) {
            return true;
        }

        Optional<String> apiPath = getApiPath(request);
        if (apiPath.isEmpty()) {
            log.warn("Failed to get best matching pattern of api path - {}", request.getRequestURI());
            sendError(response, HttpStatus.NOT_ACCEPTABLE);
            return false;
        }

        StringBuilder apiRateLimiterKey = new StringBuilder("api:rate:limit:");
        apiRateLimiterKey.append(apiPath.get());
        if (apiRateLimiter.ipAddressBase()) {
            apiRateLimiterKey.append('#').append(WebUtils.getClientIpAddress(request));
        } else if (apiRateLimiter.userBase()) {
            UserRealm user = UserRealmContextHolder.get();
            if (user.isAnonymous()) {
                log.warn("Unable to recognize the user as it's anonymous");
                sendError(response, HttpStatus.PRECONDITION_REQUIRED, "Unable to recognize the user");
                return false;
            }
            apiRateLimiterKey.append('#').append(user.getUserId());
        }

        boolean isAlreadySet = rateLimiter.trySetRate(apiRateLimiterKey.toString(), apiRateLimiter.rate(), apiRateLimiter.rateInterval(), apiRateLimiter.intervalUnit());
        if (isAlreadySet) {
            log.info("Api rate limiter is set - {}", apiPath.get());
        }

        boolean tryAcquireApiPermit = rateLimiter.tryAcquire(apiRateLimiterKey.toString());
        if (tryAcquireApiPermit) {
            return true;
        }

        log.warn("Api rate limit exceed - {}", apiPath.get());
        sendError(response, HttpStatus.TOO_MANY_REQUESTS);
        return false;
    }

    /**
     * RequestMappingHandlerMapping or SimpleUrlHandlerMapping would set matched pattern into request
     * It might rarely be null
     */
    private Optional<String> getApiPath(HttpServletRequest request) {
        Object bestMatchPattern = request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        if (!(bestMatchPattern instanceof String)) {
            return Optional.empty();
        }

        return Optional.of(request.getMethod().toUpperCase(Locale.getDefault()) + "#" + bestMatchPattern);
    }

    private void sendError(HttpServletResponse response, HttpStatus status) throws IOException {
        sendError(response, status, status.getReasonPhrase());
    }

    private void sendError(HttpServletResponse response, HttpStatus status, String message) throws IOException {
        response.setStatus(status.value());
        response.getWriter().write(status.getReasonPhrase());
    }

    @Autowired
    public void setRateLimiter(RateLimiter rateLimiter) {
        this.rateLimiter = rateLimiter;
    }
}