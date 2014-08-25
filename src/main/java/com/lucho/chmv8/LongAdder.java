package com.lucho.chmv8;

/*
 * Written by Doug Lea with assistance from members of JCP JSR-166
 * Expert Group and released to the public domain, as explained at
 * http://creativecommons.org/publicdomain/zero/1.0/
 */

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

/**
 * One or more variables that together maintain an initially zero
 * {@code long} sum.  When updates (method {@link #add}) are contended
 * across threads, the set of variables may grow dynamically to reduce
 * contention. Method {@link #sum} (or, equivalently, {@link
 * #longValue}) returns the current total combined across the
 * variables maintaining the sum.
 *
 * <p> This class is usually preferable to {@link java.util.concurrent.atomic.AtomicLong} when
 * multiple threads update a common sum that is used for purposes such
 * as collecting statistics, not for fine-grained synchronization
 * control.  Under low update contention, the two classes have similar
 * characteristics. But under high contention, expected throughput of
 * this class is significantly higher, at the expense of higher space
 * consumption.
 *
 * <p>This class extends {@link Number}, but does <em>not</em> define
 * methods such as {@code hashCode} and {@code compareTo} because
 * instances are expected to be mutated, and so are not useful as
 * collection keys.
 *
 * <p><em>jsr166e note: This class is targeted to be placed in
 * java.util.concurrent.atomic<em>
 *
 * @since 1.8
 * @author Doug Lea
 */
final class LongAdder extends Striped64 implements Serializable {
  private static final long serialVersionUID = 7249069246863182397L;

  /**
   * Version of plus for use in retryUpdate
   */
  final long fn(long v, long x) { return v + x; }

  /**
   * Creates a new adder with initial sum of zero.
   */
  public LongAdder() {
  }

  /**
   * Adds the given value.
   *
   * @param x the value to add
   */
  public void add(long x) {
    Cell[] as; long b, v; HashCode hc; Cell a; int n;
    if ((as = cells) != null || !casBase(b = base, b + x)) {
      boolean uncontended = true;
      int h = (hc = threadHashCode.get()).code;
      if (as == null || (n = as.length) < 1 ||
          (a = as[(n - 1) & h]) == null ||
          !(uncontended = a.cas(v = a.value, v + x)))
        retryUpdate(x, hc, uncontended);
    }
  }

  /**
   * Returns the current sum.  The returned value is <em>NOT</em> an
   * atomic snapshot: Invocation in the absence of concurrent
   * updates returns an accurate result, but concurrent updates that
   * occur while the sum is being calculated might not be
   * incorporated.
   *
   * @return the sum
   */
  public long sum() {
    long sum = base;
    Cell[] as = cells;
    if (as != null) {
      for (Cell a : as) {
        if (a != null)
          sum += a.value;
      }
    }
    return sum;
  }

  /**
   * Returns the String representation of the {@link #sum}.
   * @return the String representation of the {@link #sum}
   */
  public String toString() {
    return Long.toString(sum());
  }

  /**
   * Equivalent to {@link #sum}.
   *
   * @return the sum
   */
  public long longValue() {
    return sum();
  }

  /**
   * Returns the {@link #sum} as an {@code int} after a narrowing
   * primitive conversion.
   */
  public int intValue() {
    return (int)sum();
  }

  /**
   * Returns the {@link #sum} as a {@code float}
   * after a widening primitive conversion.
   */
  public float floatValue() {
    return (float)sum();
  }

  /**
   * Returns the {@link #sum} as a {@code double} after a widening
   * primitive conversion.
   */
  public double doubleValue() {
    return (double)sum();
  }

  private void writeObject(java.io.ObjectOutputStream s)
      throws IOException {
    s.defaultWriteObject();
    s.writeLong(sum());
  }

  private void readObject(ObjectInputStream s)
      throws IOException, ClassNotFoundException {
    s.defaultReadObject();
    busy = 0;
    cells = null;
    base = s.readLong();
  }

}