package com.bn.web.api;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target(METHOD)
public @interface ApiRateLimiter {
    boolean userBase() default false;

    boolean apiBase() default true;

    long rate();

    long rateInterval();

    TimeUnit intervalUnit() default TimeUnit.SECONDS;
}
