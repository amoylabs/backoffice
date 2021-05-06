package com.bn.map;

import java.util.Collection;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A bounded {@linkplain BlockingMap blocking map} backed by a concurrent
 * map.
 *
 * <p>This class supports an optional fairness policy for ordering
 * waiting producer and consumer threads.  By default, this ordering
 * is not guaranteed. However, a queue constructed with fairness set
 * to {@code true} grants threads access in FIFO order. Fairness
 * generally decreases throughput but reduces variability and avoids
 * starvation.
 */
public class BlockingHashMap<K, V> implements BlockingMap<K, V> {
    private final int capacity;
    private final ConcurrentMap<K, V> map;

    // Main lock guarding all access
    private final ReentrantLock lock;
    // Condition for waiting takes
    private final Condition notEmpty;
    // Condition for waiting puts
    private final Condition notFull;
    // Condition for waiting operation on each keys
    private final ConcurrentMap<K, Condition> lockMap;

    public BlockingHashMap(int capacity) {
        this(capacity, false);
    }

    public BlockingHashMap(int capacity, boolean fair) {
        if (capacity <= 0) {
            throw new IllegalArgumentException();
        }

        this.capacity = capacity;
        this.map = new ConcurrentHashMap<>(capacity);
        this.lockMap = new ConcurrentHashMap<>(capacity);
        this.lock = new ReentrantLock(fair);
        this.notEmpty = this.lock.newCondition();
        this.notFull = this.lock.newCondition();
    }

    private Condition keyToCondition(K key) {
        final ReentrantLock lock = this.lock;
        lockMap.putIfAbsent(key, lock.newCondition());
        return lockMap.get(key);
    }

    @Override
    public void put(K key, V value) throws InterruptedException {
        Objects.requireNonNull(key);
        Objects.requireNonNull(value);
        final ReentrantLock lock = this.lock;
        lock.lockInterruptibly();
        try {
            while (map.size() == capacity) {
                notFull.await();
            }
            map.put(key, value);
            keyToCondition(key).signal();
            notEmpty.signal();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean offer(K key, V value, long timeout, TimeUnit unit) throws InterruptedException {
        Objects.requireNonNull(key);
        Objects.requireNonNull(value);
        long nanos = unit.toNanos(timeout);
        final ReentrantLock lock = this.lock;
        lock.lockInterruptibly();
        try {
            while (map.size() == capacity) {
                if (nanos <= 0L) {
                    return false;
                }
                nanos = notFull.awaitNanos(nanos);
            }
            map.put(key, value);
            keyToCondition(key).signal();
            notEmpty.signal();
            return true;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean offer(K key, V value) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(value);
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            if (map.size() == capacity) {
                return false;
            }
            map.put(key, value);
            keyToCondition(key).signal();
            notEmpty.signal();
            return true;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void add(K key, V value) {
        if (offer(key, value)) {
            throw new IllegalStateException("Map is full");
        }
    }

    /**
     * CAUTION: Need to be 100% sure that the requested key will be put into the map somewhen,
     * otherwise, this method will be blocked the whole time.
     */
    @Override
    public V take(K key) throws InterruptedException {
        Objects.requireNonNull(key);
        final ReentrantLock lock = this.lock;
        lock.lockInterruptibly();
        try {
            while (map.size() == 0) {
                notEmpty.await();
            }
            V val;
            while ((val = map.get(key)) == null) {
                keyToCondition(key).await();
            }
            map.remove(key);
            lockMap.remove(key);
            notFull.signal();
            return val;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public V poll(K key, long timeout, TimeUnit unit) throws InterruptedException {
        Objects.requireNonNull(key);
        long nanos = unit.toNanos(timeout);
        final ReentrantLock lock = this.lock;
        lock.lockInterruptibly();
        try {
            while (map.size() == 0) {
                if (nanos <= 0L) {
                    return null;
                }
                nanos = notEmpty.awaitNanos(nanos);
            }
            V val;
            while ((val = map.get(key)) == null) {
                if (nanos <= 0L) {
                    return null;
                }
                nanos = keyToCondition(key).awaitNanos(nanos);
            }
            map.remove(key);
            lockMap.remove(key);
            notFull.signal();
            return val;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public V poll(K key) {
        Objects.requireNonNull(key);
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            if (map.size() == 0) {
                return null;
            }
            V val = map.get(key);
            if (val == null) {
                return null;
            }
            map.remove(key);
            lockMap.remove(key);
            notFull.signal();
            return val;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public V remove(K key) {
        if (key == null) return null;
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            if (map.size() > 0 && map.containsKey(key)) {
                V val = map.remove(key);
                lockMap.remove(key);
                return val;
            }
            throw new NoSuchElementException("Map is empty");
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int size() {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            return map.size();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean isEmpty() {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            return map.isEmpty();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean containsKey(K key) {
        if (key == null) return false;
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            return map.size() > 0 && map.containsKey(key);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean containsValue(V value) {
        if (value == null) return false;
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            return map.size() > 0 && map.containsValue(value);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void clear() {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            int k;
            if ((k = map.size()) > 0) {
                map.clear();
                for (; k > 0 && lock.hasWaiters(notFull); k--) {
                    notFull.signal();
                }
                for (Condition c : lockMap.values()) {
                    if (lock.hasWaiters(c)) {
                        c.signal();
                    }
                }
                lockMap.clear();
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Set<K> keySet() {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            return map.keySet();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Collection<V> values() {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            return map.values();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            return map.entrySet();
        } finally {
            lock.unlock();
        }
    }
}
