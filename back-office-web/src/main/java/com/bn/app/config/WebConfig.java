package com.bn.app.config;

import com.bn.authorization.UserAuthorizationInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private UserAuthorizationInterceptor userAuthorizationInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userAuthorizationInterceptor);
    }

    @Autowired
    public void setUserAuthorizationInterceptor(UserAuthorizationInterceptor userAuthorizationInterceptor) {
        this.userAuthorizationInterceptor = userAuthorizationInterceptor;
    }
}
