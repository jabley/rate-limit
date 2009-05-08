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

import java.util.concurrent.atomic.AtomicInteger;

/**
 * {@link StoreEntry} implementation.
 * 
 * @author jabley
 * 
 */
class StoreEntryImpl implements StoreEntry {

    /**
     * The expiry time from the epoch.
     */
    private final long expiry;

    /**
     * The counter used to keep track of how many times the service has been used for the current period.
     */
    private final AtomicInteger counter;

    /**
     * Creates a new {@link StoreEntryImpl} which will expire in {@code timeToLive} seconds.
     * 
     * @param timeToLive
     *            the time to live in seconds
     */
    StoreEntryImpl(int timeToLive) {
        this.expiry = System.currentTimeMillis() + timeToLive * 1000;
        this.counter = new AtomicInteger(0);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isExpired() {
        return System.currentTimeMillis() > expiry;
    }

    /**
     * {@inheritDoc}
     */
    public int incrementAndGet() {
        return this.counter.incrementAndGet();
    }

}
