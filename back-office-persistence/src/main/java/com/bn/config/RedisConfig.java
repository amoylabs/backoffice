package com.bn.config;

import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;

/**
 * @author beckl
 */
@Configuration
public class RedisConfig {

    @Bean
    public LettuceConnectionFactory redisConnectionFactory(RedisProperties redisProperties) {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        if (StringUtils.hasText(redisProperties.getHost())) {
            redisStandaloneConfiguration.setHostName(redisProperties.getHost());
        }
        if (redisProperties.getPort() != 0) {
            redisStandaloneConfiguration.setPort(redisProperties.getPort());
        }
        if (redisProperties.getPort() != 0) {
            redisStandaloneConfiguration.setDatabase(redisProperties.getDatabase());
        }
        if (StringUtils.hasText(redisProperties.getPassword())) {
            redisStandaloneConfiguration.setPassword(redisProperties.getPassword());
        }
        return new LettuceConnectionFactory(redisStandaloneConfiguration);
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
