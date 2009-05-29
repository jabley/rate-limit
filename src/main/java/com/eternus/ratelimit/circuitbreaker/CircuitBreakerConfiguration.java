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
 * Configuration bean for a {@link CircuitBreaker} that controls the {@link CircuitBreaker} behaviour in terms of how
 * sensitive it is to tripping and how long it will stay open, etc. This is designed to be injected by Spring, etc.
 * 
 * @author jabley
 * 
 */
public class CircuitBreakerConfiguration {

    /*
     * An option considered was to implement an escalating timeout, a la TCP/IP. If it fails, wait 1 second, try again.
     * If it fails, wait 2 seconds, try again. If it fails, wait 4 seconds, etc. Seemed like needless complexity at the
     * time.
     */

    /**
     * The number of times that a {@link CircuitBreaker} will fail before it trips.
     */
    private int threshold = 3;

    /**
     * The time in milliseconds that a {@link CircuitBreaker} will stay open until it attempts a reset.
     */
    private int timeout = 10000;

    /**
     * Factory Method to return a new {@link CircuitBreaker} ready for use.
     * 
     * @return a non-null {@link CircuitBreaker}
     */
    public CircuitBreaker createCircuitBreaker() {
        return new CircuitBreakerImpl(threshold, timeout);
    }

    /**
     * Returns the number of times that a closed {@link CircuitBreaker} can fail before it will trip.
     * 
     * @return the threshold
     */
    public int getThreshold() {
        return threshold;
    }

    /**
     * Sets the number of times that a closed {@link CircuitBreaker} can fail before it will trip.
     * 
     * @param threshold
     *            the threshold to set - positive integer
     */
    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    /**
     * Returns the time in milliseconds that a {@link CircuitBreaker} will take to try to reset itself.
     * 
     * @return the timeout
     */
    public int getTimeoutInMillis() {
        return timeout;
    }

    /**
     * Sets the time that a {@link CircuitBreaker} will be in the open state before attempting to reset itself.
     * 
     * @param timeout
     *            the timeout to set
     */
    public void setTimeoutInMillis(int timeout) {
        this.timeout = timeout;
    }
}
