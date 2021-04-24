package com.bn.web.api;

import com.bn.redis.RateLimiter;
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

        // TODO add api base and user base implementations
        String apiRateLimiterKey = "api:rate:limit:" + apiPath.get();
        boolean isAlreadySet = rateLimiter.trySetRate(apiRateLimiterKey, apiRateLimiter.rate(), apiRateLimiter.rateInterval(), apiRateLimiter.intervalUnit());
        if (isAlreadySet) {
            log.info("Api rate limiter is set - {}", apiPath.get());
        }

        boolean tryAcquireApiPermit = rateLimiter.tryAcquire(apiRateLimiterKey);
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
        response.setStatus(status.value());
        response.getWriter().write(status.getReasonPhrase());
    }

    @Autowired
    public void setRateLimiter(RateLimiter rateLimiter) {
        this.rateLimiter = rateLimiter;
    }
}