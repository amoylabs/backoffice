package com.bn.web.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@Component
@Profile("dev")
@Slf4j
public class ApiRateLimiterInspector implements ApplicationListener<ApplicationReadyEvent> {
    @Override
    public void onApplicationEvent(final ApplicationReadyEvent event) {
        ConfigurableApplicationContext applicationContext = event.getApplicationContext();
        RequestMappingHandlerMapping configuredRequestMapping = applicationContext.getBean(RequestMappingHandlerMapping.class);
        boolean hasRateLimitSettingIssue = false;
        for (HandlerMethod method : configuredRequestMapping.getHandlerMethods().values()) {
            ApiRateLimiter apiRateLimiter = method.getMethodAnnotation(ApiRateLimiter.class);
            if (apiRateLimiter == null) {
                continue;
            }

            if (apiRateLimiter.userBase() && apiRateLimiter.ipAddressBase()) {
                hasRateLimitSettingIssue = true;
                log.error("Should have only one rate limit base on {}.{}", method.getBeanType().getSimpleName(), method.getMethod().getName());
            }
        }

        if (hasRateLimitSettingIssue) {
            log.warn("Application quit due to the setting issue");
            applicationContext.close();
        }
    }
}