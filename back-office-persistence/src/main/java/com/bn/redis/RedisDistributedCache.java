package com.bn.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@SuppressWarnings("PMD.LinguisticNaming")
public class RedisDistributedCache implements DistributedCache {
    private StringRedisTemplate redisTemplate;

    @Override
    public String get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public void set(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    @Override
    public void set(String key, String value, Duration expiration) {
        redisTemplate.opsForValue().set(key, value, expiration);
    }

    @Override
    public boolean setIfAbsent(String key, String value) {
        Boolean acquired = redisTemplate.opsForValue().setIfAbsent(key, value);
        return acquired != null && acquired;
    }

    @Override
    public boolean setIfAbsent(String key, String value, Duration expiration) {
        Boolean acquired = redisTemplate.opsForValue().setIfAbsent(key, value, expiration);
        return acquired != null && acquired;
    }

    @Override
    public void expire(String key, Duration duration) {
        redisTemplate.expire(key, duration);
    }

    @Override
    public long del(String... keys) {
        Long opsResult = redisTemplate.delete(List.of(keys));
        return opsResult == null ? 0 : opsResult;
    }

    @Override
    public long increaseBy(String key, long increment) {
        Long opsResult = redisTemplate.opsForValue().increment(key, increment);
        return opsResult == null ? 0 : opsResult;
    }

    @Override
    public Map<String, String> multiGet(String... keys) {
        List<String> values = redisTemplate.opsForValue().multiGet(List.of(keys));
        if (values == null) {
            return Map.of();
        }

        Map<String, String> map = new HashMap<>(keys.length);
        for (int i = 0; i < keys.length; i++) {
            map.put(keys[i], values.get(i));
        }
        return map;
    }

    @Override
    public void multiSet(Map<String, String> values) {
        redisTemplate.opsForValue().multiSet(values);
    }

    @Autowired
    public void setRedisTemplate(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
}
