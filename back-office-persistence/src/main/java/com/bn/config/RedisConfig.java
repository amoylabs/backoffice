package com.bn.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * @author beckl
 */
@Configuration
public class RedisConfig {
    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(new RedisStandaloneConfiguration());
    }

    // @Bean
    // public LettuceConnectionFactory redisConnectionFactory() {
    //     LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder().readFrom(REPLICA_PREFERRED).build();
    //     RedisStandaloneConfiguration serverConfig = new RedisStandaloneConfiguration("localhost", 6379);
    //     return new LettuceConnectionFactory(serverConfig, clientConfig);
    // }

    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        return new StringRedisTemplate(redisConnectionFactory);
    }
}