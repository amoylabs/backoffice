package com.bn.redis;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class RateLimiterImpl implements RateLimiter {
    private RedissonClient redissonClient;

    @Override
    public boolean trySetRate(String target, long rate, long rateInterval, TimeUnit intervalUnit) {
        RRateLimiter limiter = redissonClient.getRateLimiter(target);
        return limiter.trySetRate(RateType.OVERALL, rate, rateInterval, RateIntervalUnit.valueOf(intervalUnit.name()));
    }

    @Override
    public boolean tryAcquire(String target) {
        RRateLimiter limiter = redissonClient.getRateLimiter(target);
        return limiter.tryAcquire();
    }

    @Autowired
    public void setRedissonClient(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }
}
