package com.lucho.chmv8;

import com.google.common.base.Function;

import java.util.concurrent.ConcurrentMap;

public interface ConcurrentMapV8<K, V> extends ConcurrentMap<K, V> {

  /**
   * If the specified key is not already associated with a value,
   * computes its value using the given mappingFunction and
   * enters it into the map.  This is equivalent to
   * <pre> {@code
   * if (map.containsKey(key))
   *   return map.get(key);
   * value = mappingFunction.map(key);
   * map.put(key, value);
   * return value;}</pre>
   *
   * except that the action is performed atomically.  If the
   * function returns {@code null} (in which case a {@code
   * NullPointerException} is thrown), or the function itself throws
   * an (unchecked) exception, the exception is rethrown to its
   * caller, and no mapping is recorded.  Some attempted update
   * operations on this map by other threads may be blocked while
   * computation is in progress, so the computation should be short
   * and simple, and must not attempt to update any other mappings
   * of this Map. The most appropriate usage is to construct a new
   * object serving as an initial mapped value, or memoized result,
   * as in:
   *
   *  <pre> {@code
   * map.computeIfAbsent(key, new Function<K, V>() {
   *   public V apply(K k) { return new Value(f(k)); }});}</pre>
   *
   * @param key key with which the specified value is to be associated
   * @param mappingFunction the function to compute a value
   * @return the current (existing or computed) value associated with
   *         the specified key.
   * @throws NullPointerException if the specified key, mappingFunction,
   *         or computed value is null
   * @throws IllegalStateException if the computation detectably
   *         attempts a recursive update to this map that would
   *         otherwise never complete
   * @throws RuntimeException or Error if the mappingFunction does so,
   *         in which case the mapping is left unestablished
   */
  V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction);

  /**
   * Computes and enters a new mapping value given a key and
   * its current mapped value (or {@code null} if there is no current
   * mapping). This is equivalent to
   *  <pre> {@code
   *  map.put(key, remappingFunction.remap(key, map.get(key));
   * }</pre>
   *
   * except that the action is performed atomically.  If the
   * function returns {@code null} (in which case a {@code
   * NullPointerException} is thrown), or the function itself throws
   * an (unchecked) exception, the exception is rethrown to its
   * caller, and current mapping is left unchanged.  Some attempted
   * update operations on this map by other threads may be blocked
   * while computation is in progress, so the computation should be
   * short and simple, and must not attempt to update any other
   * mappings of this Map. For example, to either create or
   * append new messages to a value mapping:
   *
   * <pre> {@code
   * Map<Key, String> map = ...;
   * final String msg = ...;
   * map.compute(key, new Function2<Key, String, String>() {
   *   public String apply(Key k, String v) {
   *    return (v == null) ? msg : v + msg;});}}</pre>
   *
   * @param key key with which the specified value is to be associated
   * @param remappingFunction the function to compute a value
   * @return the new value associated with
   *         the specified key.
   * @throws NullPointerException if the specified key or remappingFunction
   *         or computed value is null
   * @throws IllegalStateException if the computation detectably
   *         attempts a recursive update to this map that would
   *         otherwise never complete
   * @throws RuntimeException or Error if the remappingFunction does so,
   *         in which case the mapping is unchanged
   */
  V compute(K key, Function2<? super K, V, V> remappingFunction);
}
