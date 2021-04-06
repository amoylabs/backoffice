package com.bn.app.config;

import com.bn.web.authorization.UserAuthorizationInterceptor;
import com.bn.web.token.ApiIdempotenceInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private UserAuthorizationInterceptor userAuthorizationInterceptor;
    private ApiIdempotenceInterceptor apiIdempotenceInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userAuthorizationInterceptor);
        registry.addInterceptor(apiIdempotenceInterceptor);
    }

    @Autowired
    public void setUserAuthorizationInterceptor(UserAuthorizationInterceptor userAuthorizationInterceptor) {
        this.userAuthorizationInterceptor = userAuthorizationInterceptor;
    }

    @Autowired
    public void setApiIdempotenceInterceptor(ApiIdempotenceInterceptor apiIdempotenceInterceptor) {
        this.apiIdempotenceInterceptor = apiIdempotenceInterceptor;
    }
}
