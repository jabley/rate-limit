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

import java.util.concurrent.atomic.AtomicInteger;

/**
 * {@link CircuitBreakerState} implementation for when a {@link CircuitBreaker} is closed and requests to integration
 * points are working normally.
 * 
 * @author jabley
 * 
 */
class ClosedState implements CircuitBreakerState {

    /**
     * The number of failures seen by this {@link CircuitBreaker}.
     */
    private final AtomicInteger failureCount;

    /**
     * The number of failures permitted for the {@link CircuitBreaker}, after which the {@link CircuitBreaker} will
     * trip.
     */
    private final int threshold;

    /**
     * Creates a new {@link ClosedState}.
     * 
     * @param threshold
     *            the positive threshold permitted number of failures after which any subsequent failures will cause the
     *            {@link CircuitBreaker} to trip open
     */
    ClosedState(int threshold) {
        this.threshold = threshold;
        this.failureCount = new AtomicInteger();
    }

    /**
     * {@inheritDoc}
     */
    public void after(CircuitBreaker circuitBreakerImpl) {
        this.failureCount.set(0);
    }

    /**
     * {@inheritDoc}
     */
    public void before(CircuitBreaker circuitBreakerImpl) {
        // no-op
    }

    /**
     * {@inheritDoc}
     */
    public long getTimeToReset() {
        return -1;
    }

    /**
     * {@inheritDoc}
     */
    public void handleFailure(CircuitBreaker circuitBreakerImpl) {
        int count = this.failureCount.incrementAndGet();

        if (count > threshold) {
            circuitBreakerImpl.tripBreaker();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "CLOSED";
    }
    
}
