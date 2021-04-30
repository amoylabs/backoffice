package com.bn.queue;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * A {@link Queue} that it's actually a unbound blocking queue and additionally supports operations
 * that it has a scheduled executor to consume the data in the queue periodically.
 * <p/>
 * Note that this queue won't block anything as it's using unbound {@link LinkedBlockingQueue}
 */
public class FixedRateScheduledQueue<T> implements ScheduledBlockingQueue<T> {
    private final BlockingQueue<T> queue = new LinkedBlockingQueue<>();
    private final Duration duration;
    private final Consumer<List<T>> consumer;
    private ScheduledExecutorService scheduledExecutorService;
    // count the times that executor continuously has nothing to give to consume
    private final AtomicInteger hasNothingToConsume4ExecutorCount = new AtomicInteger(0);
    // the threshold that executor will be shutdown to reduce resource cost
    private int executorShutdownThreshold = 10;

    public FixedRateScheduledQueue(Duration duration, Consumer<List<T>> consumer) {
        this.duration = duration;
        this.consumer = consumer;
    }

    private boolean isExecutorServiceRunning() {
        return scheduledExecutorService != null && !scheduledExecutorService.isTerminated();
    }

    private void scheduledExecutorAtFixedRate() {
        if (isExecutorServiceRunning()) {
            return;
        }

        long period = duration.getSeconds();
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(() -> consumeBy(consumer), period, period, TimeUnit.SECONDS);
    }

    private boolean isExceedExecutorShutdownThreshold() {
        // adding the comparison between the current count and the threshold to prevent infinite increment
        if (hasNothingToConsume4ExecutorCount.get() >= executorShutdownThreshold) {
            return true;
        }

        return hasNothingToConsume4ExecutorCount.incrementAndGet() >= executorShutdownThreshold;
    }

    /**
     * Wrapped queue consumer with try-catch to prevent blocking subsequent executions.
     */
    private void consumeBy(Consumer<List<T>> consumer) {
        List<T> dataList = new ArrayList<>(queue);
        if (dataList.isEmpty()) {
            if (isExceedExecutorShutdownThreshold() && isExecutorServiceRunning()) {
                scheduledExecutorService.shutdown();
            }
            return;
        }

        // reset the count from the beginning if there is the data to be consumed
        hasNothingToConsume4ExecutorCount.set(0);

        try {
            consumer.accept(dataList);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        } finally {
            queue.clear();
        }
    }

    @Override
    public boolean add(T e) {
        scheduledExecutorAtFixedRate();
        return queue.add(e);
    }

    @Override
    public boolean offer(T e) {
        scheduledExecutorAtFixedRate();
        return queue.offer(e);
    }

    @Override
    public T remove() {
        return queue.remove();
    }

    @Override
    public T poll() {
        return queue.poll();
    }

    @Override
    public T element() {
        return queue.element();
    }

    @Override
    public T peek() {
        return queue.peek();
    }

    @Override
    public void put(T e) throws InterruptedException {
        scheduledExecutorAtFixedRate();
        queue.put(e);
    }

    @Override
    public boolean offer(T e, long timeout, TimeUnit unit) throws InterruptedException {
        scheduledExecutorAtFixedRate();
        return queue.offer(e, timeout, unit);
    }

    @Override
    public T take() throws InterruptedException {
        return queue.take();
    }

    @Override
    public T poll(long timeout, TimeUnit unit) throws InterruptedException {
        return queue.poll(timeout, unit);
    }

    @Override
    public int remainingCapacity() {
        return queue.remainingCapacity();
    }

    @Override
    public boolean remove(Object o) {
        return queue.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return queue.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        scheduledExecutorAtFixedRate();
        return queue.addAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return queue.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return queue.retainAll(c);
    }

    @Override
    public void clear() {
        queue.clear();
    }

    @Override
    public int size() {
        return queue.size();
    }

    @Override
    public boolean isEmpty() {
        return queue.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return queue.contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        return queue.iterator();
    }

    @Override
    public Object[] toArray() {
        return queue.toArray();
    }

    @Override
    @SuppressWarnings("all")
    public <E> E[] toArray(E[] a) {
        return queue.toArray(a);
    }

    @Override
    public int drainTo(Collection<? super T> c) {
        return queue.drainTo(c);
    }

    @Override
    public int drainTo(Collection<? super T> c, int maxElements) {
        return queue.drainTo(c, maxElements);
    }

    @Override
    public void setExecutorShutdownThreshold(int threshold) {
        if (threshold <= 0) {
            throw new IllegalArgumentException("Executor Shutdown Threshold should be greater than zero");
        }

        this.executorShutdownThreshold = threshold;
    }
}
