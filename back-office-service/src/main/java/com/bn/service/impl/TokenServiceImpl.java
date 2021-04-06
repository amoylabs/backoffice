package com.bn.service.impl;

import cn.hutool.core.util.StrUtil;
import com.bn.redis.RedisCache;
import com.bn.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
public class TokenServiceImpl implements TokenService {
    private RedisCache redisCache;

    @Override
    public String generateIdempotentApiToken() {
        String token = UUID.randomUUID().toString();
        // setting duration to clear this sort of data in cache
        redisCache.set(token, "idempotent:api", Duration.ofDays(1));
        return token;
    }

    @Override
    public boolean isIdempotentApiTokenConsumed(String token) {
        if (StrUtil.isBlank(token)) {
            return false;
        }

        // successful DEL operation is considered as the token is consumed
        return redisCache.exists(token) && redisCache.del(token) == 1L;
    }

    @Autowired
    public void setRedisCache(RedisCache redisCache) {
        this.redisCache = redisCache;
    }
}
