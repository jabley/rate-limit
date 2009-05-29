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
 * Interface defining the behaviour of the state of a {@link CircuitBreaker}.
 * 
 * @author jabley
 * 
 */
interface CircuitBreakerState {

    /**
     * 
     * @param circuitBreaker
     *            the non-null {@link CircuitBreaker} that this {@link CircuitBreakerState} refers to
     */
    void after(CircuitBreaker circuitBreaker);

    /**
     * 
     * @param circuitBreaker
     *            the non-null {@link CircuitBreaker} that this {@link CircuitBreakerState} refers to
     * @throws CircuitBreakerException
     *             if there was a problem
     */
    void before(CircuitBreaker circuitBreaker) throws CircuitBreakerException;

    /**
     * 
     * @param circuitBreaker
     *            the non-null {@link CircuitBreaker} that this {@link CircuitBreakerState} refers to
     */
    void handleFailure(CircuitBreaker circuitBreaker);

    /**
     * Returns the time to next reset in milliseconds. A negative value means that the state is closed.
     * 
     * @return the time to next reset in milliseconds
     */
    long getTimeToReset();

}
