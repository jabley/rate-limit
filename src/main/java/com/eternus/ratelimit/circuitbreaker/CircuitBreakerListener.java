/**
 * 
 */
package com.eternus.ratelimit.circuitbreaker;

/**
 * Simple listener interface to allow clients to register interest in CircuitBreaker state changes.
 * 
 * <strong>N.B. implementations of this interface should be thread-safe, since they can potentially get called by
 * multiple threads.</strong>
 * 
 * @author jabley
 * 
 */
public interface CircuitBreakerListener {

    /**
     * Called when a {@link CircuitBreaker} is attempting to reset.
     */
    void attemptReset();

    /**
     * Called when a {@link CircuitBreaker} has been reset.
     */
    void reset();

    /**
     * Called when a {@link CircuitBreaker} has been tripped.
     */
    void tripped();
}
