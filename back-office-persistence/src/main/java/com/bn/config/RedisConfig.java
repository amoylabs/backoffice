package com.bn.config;

import cn.hutool.core.util.StrUtil;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;

/**
 * @author beckl
 */
@Configuration
@Lazy
public class RedisConfig {
    private static final String REDIS_CLIENT_NAME = "backoffice";

    @Bean
    public LettuceConnectionFactory redisConnectionFactory(RedisProperties redisProperties) {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        if (StringUtils.hasText(redisProperties.getHost())) {
            redisStandaloneConfiguration.setHostName(redisProperties.getHost());
        }
        if (redisProperties.getPort() > 0) {
            redisStandaloneConfiguration.setPort(redisProperties.getPort());
        }
        if (redisProperties.getDatabase() >= 0) {
            redisStandaloneConfiguration.setDatabase(redisProperties.getDatabase());
        }
        if (StringUtils.hasText(redisProperties.getPassword())) {
            redisStandaloneConfiguration.setPassword(redisProperties.getPassword());
        }

        return new LettuceConnectionFactory(redisStandaloneConfiguration);
    }

    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        return new StringRedisTemplate(redisConnectionFactory);
    }

    @Bean(destroyMethod = "shutdown")
    public RedissonClient redissonClient(RedisProperties redisProperties) {
        Config config = new Config();
        config.setCodec(JsonJacksonCodec.INSTANCE);
        config.setNettyThreads(Runtime.getRuntime().availableProcessors());
        config.setThreads(0); // No suppose to use external ExecutorService

        SingleServerConfig serverConfig = config.useSingleServer();
        serverConfig.setClientName(REDIS_CLIENT_NAME);
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(); // use this to get default redis host & port
        String host = StringUtils.hasText(redisProperties.getHost()) ? redisProperties.getHost() : redisStandaloneConfiguration.getHostName();
        int port = redisProperties.getPort() > 0 ? redisProperties.getPort() : redisStandaloneConfiguration.getPort();
        serverConfig.setAddress(StrUtil.format("redis://{}:{}", host, port));
        if (redisProperties.getDatabase() >= 0) {
            serverConfig.setDatabase(redisProperties.getDatabase());
        }
        if (StringUtils.hasText(redisProperties.getPassword())) {
            serverConfig.setPassword(redisProperties.getPassword());
        }

        return Redisson.create(config);
    }
}
