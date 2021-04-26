package com.bn.app.config;

import com.bn.web.api.ApiIdempotenceInterceptor;
import com.bn.web.api.ApiRateLimitInterceptor;
import com.bn.web.authorization.UserAuthorizationInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private UserAuthorizationInterceptor userAuthorizationInterceptor;
    private ApiIdempotenceInterceptor apiIdempotenceInterceptor;
    private ApiRateLimitInterceptor apiRateLimitInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userAuthorizationInterceptor);
        registry.addInterceptor(apiIdempotenceInterceptor);
        registry.addInterceptor(apiRateLimitInterceptor);
    }

    @Autowired
    public void setUserAuthorizationInterceptor(UserAuthorizationInterceptor userAuthorizationInterceptor) {
        this.userAuthorizationInterceptor = userAuthorizationInterceptor;
    }

    @Autowired
    public void setApiIdempotenceInterceptor(ApiIdempotenceInterceptor apiIdempotenceInterceptor) {
        this.apiIdempotenceInterceptor = apiIdempotenceInterceptor;
    }

    @Autowired
    public void setApiRateLimitInterceptor(ApiRateLimitInterceptor apiRateLimitInterceptor) {
        this.apiRateLimitInterceptor = apiRateLimitInterceptor;
    }
}
