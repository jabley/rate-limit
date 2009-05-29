/*
 * Copyright 2009 James Abley
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 */
package com.eternus.ratelimit.circuitbreaker;

/**
 * <p>
 * Interface defining the operations supported by a circuit breaker. Inspired by the Michael T Nygard book Release It!.
 * </p>
 * 
 * <p>
 * Circuit breakers are intended for use around integration points, such as making HTTP calls to third-party services
 * and to allow throttling of access to resources.
 * </p>
 * 
 * @author jabley
 * 
 */
public interface CircuitBreaker {

    /**
     * Allows clients to register interest in being notified of {@link CircuitBreaker} state changes.
     * 
     * @param listener
     *            a non-null {@link CircuitBreakerListener}
     */
    public void addListener(CircuitBreakerListener listener);

    /**
     * Clients should call this method after doing any potentially problematic Integration Point calls, if there weren't
     * any problems.
     */
    public void after();

    /**
     * Called to signal to this {@link CircuitBreaker} that it should attempt to reset and see if the guarded operation
     * is successful.
     */
    public void attemptReset();

    /**
     * Clients should call this method before doing any potentially problematic Integration Point calls.
     * 
     * @throws CircuitBreakerException
     *             if there was a problem. Implementations should throw {@link CircuitBreakerOpenException} to allow
     *             clients to differentiate between failure due to {@link CircuitBreaker} state versus an integration
     *             point failure
     */
    public void before() throws CircuitBreakerException;

    /**
     * Returns a human-readable representation of the current state of this {@link CircuitBreaker}.
     * 
     * @return a non-null String
     */
    public String getCurrentState();

    /**
     * Returns the number of permitted failures until this {@link CircuitBreaker} will trip.
     * 
     * @return a positive value
     */
    public int getThreshold();

    /**
     * Returns the time in milliseconds until this {@link CircuitBreaker} will reset. A negative value implies that this
     * {@link CircuitBreaker} is closed and working normally.
     * 
     * @return the time in milliseconds
     */
    public long getTimeToResetInMillis();

    /**
     * Returns the total number of times that this {@link CircuitBreaker} has been tripped.
     * 
     * @return a non-negative value
     */
    public long getTripCount();

    /**
     * Clients should call this method after doing any potentially problematic Integration Point calls, if there was a
     * failure.
     */
    public void handleFailure();

    /**
     * Called to signal to this {@link CircuitBreaker} that it should reset to fully closed and let the guarded
     * operation attempts happen normally.
     */
    public void reset();

    /**
     * Called to transition this {@link CircuitBreaker} from closed (operating normally) to open (something failed).
     */
    public void tripBreaker();

}
