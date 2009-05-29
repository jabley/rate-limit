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
 * {@link CircuitBreakerState} implementation for when the {@link CircuitBreaker} is half-open and testing to see if the
 * guarded operation / integration point is usable yet.
 * 
 * @author jabley
 * 
 */
class HalfOpenState implements CircuitBreakerState {

    /**
     * {@inheritDoc}
     */
    public void after(CircuitBreaker circuitBreakerImpl) {
        circuitBreakerImpl.reset();
    }

    /**
     * {@inheritDoc}
     */
    public void before(CircuitBreaker circuitBreakerImpl) throws CircuitBreakerException {
        // no-op
    }

    /**
     * {@inheritDoc}
     */
    public long getTimeToReset() {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    public void handleFailure(CircuitBreaker circuitBreakerImpl) {
        circuitBreakerImpl.tripBreaker();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "HALF_OPEN";
    }
    
}
