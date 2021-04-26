package com.bn.redis;

import java.util.concurrent.TimeUnit;

public interface RateLimiter {
    boolean trySetRate(String target, long rate, long rateInterval, TimeUnit intervalUnit);

    boolean tryAcquire(String target);
}
