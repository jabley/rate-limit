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
package com.eternus.ratelimit;

/**
 * <p>
 * {@link RateLimiter} implementation which provides a fixed-bucket algorithm for rate-limiting access to services.
 * </p>
 * 
 * <p>
 * For a fixed-bucket algorithm, we define a duration and an acceptable number of requests to be serviced in that time.
 * Each time a request comes in, we look up the number of calls made in the current period. If it is at or above the
 * limit, then abort with a rate-limiting error, otherwise increment the counter and service the request.
 * </p>
 * 
 * @author jabley
 * 
 */
public class FixedBucket implements RateLimiter {

    /**
     * Flag which enables / disables this {@link RateLimiter} implementation.
     */
    private volatile boolean enabled = true;

    /**
     * The time between each service slot, in seconds.
     */
    private int timeToLive = 1;

    /**
     * The positive maximum number of requests allowed per duration.
     */
    private int allowedRequests = 1;

    /**
     * The non-null {@link TokenStore}.
     */
    private TokenStore cache;

    /**
     * Sets the non-null {@link TokenStore} implementation used.
     * 
     * @param cache
     *            a non-null {@link TokenStore}
     */
    public void setTokenStore(TokenStore cache) {
        this.cache = cache;
    }

    /**
     * {@inheritDoc}
     */
    public int getAllowedRequests() {
        return this.allowedRequests;
    }

    /**
     * {@inheritDoc}
     */
    public void setAllowedRequests(int allowedRequests) {
        if (allowedRequests > 0) {
            this.allowedRequests = allowedRequests;
        }
    }

    /**
     * {@inheritDoc}
     */
    public Token getToken(Key key) {
        Token result = TokenInstance.UNUSABLE;

        if (!enabled) {
            result = TokenInstance.USABLE;
        } else {

            StoreEntry entry = cache.get(key);

            if (entry == null) {

                /* Populate the entry, thus unlocking any underlying mutex */
                entry = cache.create(key, timeToLive);
            }

            /* Increment the client count and see whether we have hit the maximum allowed clients yet. */
            int current = entry.incrementAndGet();

            if (current <= allowedRequests) {
                result = TokenInstance.USABLE;
            }
        }

        return result;
    }

    /**
     * Initializes the {@link FixedBucket} for use.
     */
    public void init() {

    }

    /**
     * {@inheritDoc}
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * {@inheritDoc}
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * {@inheritDoc}
     */
    public void setDuration(int durationInSeconds) {
        if (durationInSeconds > 0) {
            this.timeToLive = durationInSeconds;
        }
    }

    /**
     * {@inheritDoc}
     */
    public int getDuration() {
        return this.timeToLive;
    }

}
