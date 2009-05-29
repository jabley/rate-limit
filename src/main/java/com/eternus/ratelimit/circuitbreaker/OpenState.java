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
 * {@link CircuitBreakerState} implementation for when a {@link CircuitBreaker} is open.
 * 
 * @author jabley
 * 
 */
class OpenState implements CircuitBreakerState {

    /**
     * The time when the {@link CircuitBreaker} was tripped.
     */
    private final long tripTime;

    /**
     * The time in milliseconds after which the tripped CircuitBreaker can attempt to reset.
     */
    private final int timeout;

    /**
     * Creates a new {@link OpenState} with the specified timeout in milliseconds.
     * 
     * @param timeout
     *            the positive time in milliseconds after which the {@link CircuitBreaker} will attempt to reset
     */
    OpenState(int timeout) {
        this.tripTime = System.currentTimeMillis();
        this.timeout = timeout;
    }

    /**
     * {@inheritDoc}
     */
    public void after(CircuitBreaker circuitBreakerImpl) {
        // no-op
    }

    /**
     * {@inheritDoc}
     */
    public void before(CircuitBreaker circuitBreakerImpl) throws CircuitBreakerException {
        long now = System.currentTimeMillis();
        long elapsed = now - this.tripTime;

        if (elapsed > this.timeout) {
            circuitBreakerImpl.attemptReset();
        } else {
            throw new CircuitBreakerOpenException("Open CircuitBreaker not yet ready for use.");
        }
    }

    /**
     * {@inheritDoc}
     */
    public long getTimeToReset() {
        long now = System.currentTimeMillis();
        long elapsed = now - this.tripTime;

        if (elapsed < this.timeout) {
            
            /* There is still some time to go. */
            return this.timeout - elapsed;
        }
        
        /* It will reset on the next client attempt. */
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    public void handleFailure(CircuitBreaker circuitBreakerImpl) {
        // no-op
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "OPEN";
    }

}
