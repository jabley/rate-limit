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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Default implementation of {@link CircuitBreaker}.
 * 
 * @author jabley
 * 
 */
class CircuitBreakerImpl implements CircuitBreaker {

    /**
     * The positive number of failed attempts allowed before this {@link CircuitBreaker} will trip.
     */
    private final int threshold;

    /**
     * The timeout in milliseconds after which the open circuit breaker will attempt to reset.
     */
    private final int timeout;

    /**
     * Count of the number of times this {@link CircuitBreaker} has been tripped.
     */
    private final AtomicLong tripCount;

    /**
     * The non-null current {@link CircuitBreakerState}.
     */
    private final AtomicReference<CircuitBreakerState> state;

    /**
     * The non-null list of {@link CircuitBreakerListener}s that wish to be notified of state changes.
     */
    private final List<CircuitBreakerListener> listeners;

    /**
     * Creates a new {@link CircuitBreakerImpl} with the specified threshold and timeout.
     * 
     * @param threshold
     *            a positive number of failures allowed before this {@link CircuitBreaker} will trip
     * @param timeout
     *            the time in milliseconds needed for this tripped {@link CircuitBreaker} to attempt a reset
     */
    public CircuitBreakerImpl(int threshold, int timeout) {
        this.threshold = threshold;
        this.timeout = timeout;
        this.tripCount = new AtomicLong();
        this.listeners = new ArrayList<CircuitBreakerListener>();
        this.state = new AtomicReference<CircuitBreakerState>(new ClosedState(threshold));
    }

    /**
     * {@inheritDoc}
     */
    public void addListener(CircuitBreakerListener listener) {
        this.listeners.add(listener);
    }

    /**
     * {@inheritDoc}
     */
    public void after() {
        getState().after(this);
    }

    /**
     * {@inheritDoc}
     */
    public void attemptReset() {
        setState(new HalfOpenState());

        notifyListeners(Notifications.ATTEMPT_RESET);
    }

    /**
     * {@inheritDoc}
     */
    public void before() throws CircuitBreakerException {
        getState().before(this);
    }

    /**
     * {@inheritDoc}
     */
    public void handleFailure() {
        getState().handleFailure(this);
    }

    /**
     * {@inheritDoc}
     */
    public String getCurrentState() {
        return getState().toString();
    }

    /**
     * {@inheritDoc}
     */
    public int getThreshold() {
        return this.threshold;
    }

    /**
     * {@inheritDoc}
     */
    public long getTimeToResetInMillis() {
        return getState().getTimeToReset();
    }

    /**
     * {@inheritDoc}
     */
    public long getTripCount() {
        return this.tripCount.get();
    }

    /**
     * {@inheritDoc}
     */
    public void tripBreaker() {
        tripCount.incrementAndGet();
        setState(new OpenState(this.timeout));

        notifyListeners(Notifications.TRIPPED);
    }

    /**
     * {@inheritDoc}
     */
    public void reset() {
        setState(new ClosedState(threshold));

        notifyListeners(Notifications.RESET);
    }

    /**
     * Returns the non-null current state.
     * 
     * @return a non-null {@link CircuitBreakerState}
     */
    private CircuitBreakerState getState() {
        return this.state.get();
    }

    /**
     * Notify {@link CircuitBreakerListener}s with the appropriate {@link Notifications} function.
     * 
     * @param notifications
     *            a non-null {@link Notifications}
     */
    private void notifyListeners(Notifications notifications) {
        for (CircuitBreakerListener listener : this.listeners) {
            try {
                notifications.notifyListener(listener);
            } catch (RuntimeException e) {

                /* ignore and carry on processing the others */
            }
        }
    }

    /**
     * Sets the non-null new state.
     * 
     * @param newState
     *            the non-null new {@link CircuitBreakerState}
     */
    private void setState(CircuitBreakerState newState) {
        this.state.set(newState);
    }

    /**
     * Simple interface defining a Functor for notifying listeners.
     * 
     * @author jabley
     * 
     */
    interface NotifyListener {

        /**
         * Method called to notify {@link CircuitBreakerListener}s of a state change in this {@link CircuitBreaker}.
         * 
         * @param listener
         *            a non-null {@link CircuitBreakerListener}
         */
        void notifyListener(CircuitBreakerListener listener);
    }

    /**
     * Enumeration defining the possible notifications that we can pass to {@link CircuitBreakerListener}s.
     * 
     * @author jabley
     * 
     */
    private static enum Notifications implements NotifyListener {

        /**
         * {@link NotifyListener} implementation for when {@link CircuitBreaker#attemptReset()} has been called.
         */
        ATTEMPT_RESET() {

            /**
             * {@inheritDoc}
             */
            public void notifyListener(CircuitBreakerListener listener) {
                listener.attemptReset();
            }
        },

        /**
         * {@link NotifyListener} implementation for when {@link CircuitBreaker#reset()} has been called.
         */
        RESET() {

            /**
             * {@inheritDoc}
             */
            public void notifyListener(CircuitBreakerListener listener) {
                listener.reset();
            }
        },

        /**
         * {@link NotifyListener} implementation for when {@link CircuitBreaker#tripBreaker()} has been called.
         */
        TRIPPED() {

            /**
             * {@inheritDoc}
             */
            public void notifyListener(CircuitBreakerListener listener) {
                listener.tripped();
            }
        };

        /**
         * {@inheritDoc}
         */
        public abstract void notifyListener(CircuitBreakerListener listener);
        
    }

}
