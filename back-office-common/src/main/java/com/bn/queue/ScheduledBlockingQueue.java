package com.bn.queue;

import java.util.Queue;
import java.util.concurrent.BlockingQueue;

/**
 * A {@link Queue} that it's actually a unbound blocking queue and additionally supports operations
 * that it has a scheduled executor to consume the data in the queue periodically.
 * <p/>
 * Use this to set threshold to shutdown the executor if it constantly has empty data to consume.
 */
public interface ScheduledBlockingQueue<T> extends BlockingQueue<T> {
    void setExecutorShutdownThreshold(int threshold);
}
