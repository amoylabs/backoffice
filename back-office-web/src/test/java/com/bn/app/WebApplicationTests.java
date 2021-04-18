package com.bn.app;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@SpringBootTest
class WebApplicationTests {

    @Test
    void contextLoads() {
    }

    @Configuration
    static class Config {
        @Bean
        public RedisProperties mockRedisProperties() {
            return new RedisProperties();
        }
    }
}
