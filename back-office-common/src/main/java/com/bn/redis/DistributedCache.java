package com.bn.redis;

import java.time.Duration;
import java.util.Map;

@SuppressWarnings("PMD.LinguisticNaming")
public interface DistributedCache {
    String get(String key);

    void set(String key, String value);

    void set(String key, String value, Duration expiration);

    boolean setIfAbsent(String key, String value);

    boolean setIfAbsent(String key, String value, Duration expiration);

    void expire(String key, Duration duration);

    long del(String... keys);

    long increaseBy(String key, long increment);

    Map<String, String> multiGet(String... keys);

    void multiSet(Map<String, String> values);
}
