package com.bn.redis;

import cn.hutool.core.util.StrUtil;
import com.bn.exception.BadRequestException;
import com.bn.exception.ConflictException;
import com.bn.exception.DistributedLockError;
import com.bn.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.Callable;

/**
 * Providing a way to lock resource
 *
 * @author beckl
 */
@Component
@Slf4j
public class DistributedLockImpl implements DistributedLock {
    private static final int MAX_LOCK_CHECK_TIMES = 10;
    private static final int MIN_CHECK_INTERVAL_MILLISECONDS = 500;
    private static final int MAX_CHECK_INTERVAL_MILLISECONDS = 5_000;

    private StringRedisTemplate redisTemplate;

    /**
     * @param key      Lock key
     * @param value    Lock value
     * @param lockTime Time to lock the key in redis
     * @param waitTime Time to wait for current thread to acquire the lock
     */
    private void tryLock(String key, String value, Duration lockTime, Duration waitTime) {
        long startTime = System.nanoTime();
        Boolean acquired;
        do {
            acquired = redisTemplate.opsForValue().setIfAbsent(key, value, lockTime);
            if (acquired != null && !acquired) {
                if (waitTime == null || waitTime.isNegative() || waitTime.isZero()) {
                    throw new BadRequestException("Lock failed without any waiting time");
                }
                if (System.nanoTime() - startTime >= waitTime.toNanos()) {
                    throw new ConflictException("Lock failed until the waiting time is ended");
                }
            }

            // Assuming it will take 10 times to check the lock at maximum
            // This will prevent too many requests to redis server
            // The checking interval is set to 0.5s at minimum and 5s at maximum
            long checkInterval = Math.max(Math.min(waitTime.toMillis() / MAX_LOCK_CHECK_TIMES, MAX_CHECK_INTERVAL_MILLISECONDS), MIN_CHECK_INTERVAL_MILLISECONDS);
            try {
                Thread.sleep(checkInterval);
            } catch (InterruptedException ex) {
                throw new DistributedLockError("Lock failed as the check interval is interrupted", ex);
            }
        } while (acquired == null || !acquired);
    }

    /**
     * @param key      Lock key
     * @param value    Lock value
     * @param lockTime Time to lock the key in redis
     * @param waitTime Time to wait for current thread to acquire the lock
     * @param callable The callable to execute while current thread acquire the lock
     * @return result returned by callback
     */
    @Override
    public <T> T tryLock(String key, String value, Duration lockTime, Duration waitTime, Callable<T> callable) {
        tryLock(key, value, lockTime, waitTime);
        try {
            return callable.call();
        } catch (BadRequestException | ConflictException | NotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new DistributedLockError(ex);
        } finally {
            cleanup(key, value);
        }
    }

    /**
     * @param key      Lock key
     * @param value    Lock value
     * @param lockTime Time to lock the key in redis
     * @param waitTime Time to wait for current thread to acquire the lock
     * @param runnable The runnable to execute while current thread acquire the lock
     */
    @Override
    public void tryLock(String key, String value, Duration lockTime, Duration waitTime, Runnable runnable) {
        tryLock(key, value, lockTime, waitTime);
        try {
            runnable.run();
        } finally {
            cleanup(key, value);
        }
    }

    /**
     * @param key   Lock key
     * @param value Lock value
     */
    private void cleanup(String key, String value) {
        String remoteValue = redisTemplate.opsForValue().get(key);
        if (StrUtil.equals(value, remoteValue)) {
            redisTemplate.delete(key);
            return;
        }

        log.warn("Lock expired before loading complete, key = {}", key);
    }

    @Autowired
    public void setRedisTemplate(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
}
