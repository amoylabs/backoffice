package com.bn.app;

import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestBeans {

    @Bean
    public RedisProperties mockRedisProperties() {
        return new RedisProperties();
    }
}
