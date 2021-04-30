package com.bn.map;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * A {@link Map} that additionally supports operations that wait for
 * the map to become non-empty and the element is put into it when retrieving an element,
 * and wait for space to become available in the map when storing an element.
 *
 * <p>{@code BlockingMap} methods come in four forms, with different ways
 * of handling operations that cannot be satisfied immediately, but may be
 * satisfied at some point in the future:
 * one throws an exception, the second returns a special value (either
 * {@code null} or {@code false}, depending on the operation), the third
 * blocks the current thread indefinitely until the operation can succeed,
 * and the fourth blocks for only a given maximum time limit before giving
 * up.  These methods are summarized in the following table:
 *
 * <table class="plain">
 * <caption>Summary of BlockingMap methods</caption>
 *  <tr>
 *    <td></td>
 *    <th scope="col" style="font-weight:normal; font-style:italic">Throws exception</th>
 *    <th scope="col" style="font-weight:normal; font-style:italic">Special value</th>
 *    <th scope="col" style="font-weight:normal; font-style:italic">Blocks</th>
 *    <th scope="col" style="font-weight:normal; font-style:italic">Times out</th>
 *  </tr>
 *  <tr>
 *    <th scope="row" style="text-align:left">Insert</th>
 *    <td>{@link #add(Object, Object)} add(K, V)}</td>
 *    <td>{@link #offer(Object, Object)} offer(K, V)}</td>
 *    <td>{@link #put(Object, Object)} put(K, V)}</td>
 *    <td>{@link #offer(Object, Object, long, TimeUnit)} offer(K, V, time, unit)}</td>
 *  </tr>
 *  <tr>
 *    <th scope="row" style="text-align:left">Remove</th>
 *    <td>{@link #remove(Object)} remove(K)}</td>
 *    <td>{@link #poll(Object)} poll(K)}</td>
 *    <td>{@link #take(Object)} take(K)}</td>
 *    <td>{@link #poll(Object, long, TimeUnit)} poll(K, time, unit)}</td>
 *  </tr>
 * </table>
 */
public interface BlockingMap<K, V> {
    void put(K key, V value) throws InterruptedException;

    boolean offer(K key, V value, long timeout, TimeUnit unit) throws InterruptedException;

    boolean offer(K key, V value);

    void add(K key, V value);

    V take(K key) throws InterruptedException;

    V poll(K key, long timeout, TimeUnit unit) throws InterruptedException;

    V poll(K key);

    V remove(K key);

    int size();

    boolean isEmpty();

    boolean containsKey(K key);

    boolean containsValue(V value);

    void clear();

    Set<K> keySet();

    Collection<V> values();

    Set<Map.Entry<K, V>> entrySet();
}
