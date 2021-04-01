package com.bn.redis;

import java.time.Duration;
import java.util.concurrent.Callable;

public interface DistributedLock {
    <T> T tryLock(String key, String value, Duration lockTime, Duration waitTime, Callable<T> callable);

    void tryLock(String key, String value, Duration lockTime, Duration waitTime, Runnable runnable);
}
