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

import java.lang.ref.SoftReference;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import com.google.common.collect.MapMaker;

/**
 * {@link TokenStore} implementation that is purely in-memory.
 * 
 * @author jabley
 * 
 */
public class MemoryTokenStore implements TokenStore {

    /**
     * The Map used to keep track of {@link StoreEntry} instances.
     */
    private final Map<Key, SoftReference<StoreEntry>> cache;

    /**
     * The {@link Lock} used to guard reads.
     */
    private final Lock r;

    /**
     * The {@link Lock} used to guard writes.
     */
    private final Lock w;

    /**
     * Creates a new {@link MemoryTokenStore}.
     */
    public MemoryTokenStore() {
        this.cache = new MapMaker().softValues().expiration(120, TimeUnit.SECONDS).makeMap();
        ReadWriteLock lock = new ReentrantReadWriteLock();
        this.r = lock.readLock();
        this.w = lock.writeLock();
    }

    /**
     * {@inheritDoc}
     */
    public StoreEntry get(Key key) {

        StoreEntry result = null;
        SoftReference<StoreEntry> ref = null;

        r.lock();

        try {
            ref = this.cache.get(key);
            if (ref != null) {
                result = ref.get();
            }
        } finally {
            r.unlock();
        }

        if (!(result == null || result.isExpired())) {

            /* Cache hit with a good entry - use it. */
            return result;
        }

        w.lock();

        result = checkPopulateThisPeriod(key);

        return result;
    }

    /**
     * {@inheritDoc}
     */
    public StoreEntry create(Key key, int timeToLive) {
        try {
            StoreEntryImpl entry = new StoreEntryImpl(timeToLive);
            cache.put(key, new SoftReference<StoreEntry>(entry));
            return entry;
        } finally {
            w.unlock();
        }
    }

    /**
     * If no usable entry in the cache, then we assume that the write lock is held prior to calling this method.
     * 
     * Returns null to indicate that the context client thread is safe to call {@link #create(Key, int)}, otherwise
     * returns a usable {@link StoreEntry}.
     * 
     * @param key
     *            the non-null {@link Key}
     * @return a {@link StoreEntry} - may be null
     */
    private StoreEntry checkPopulateThisPeriod(Key key) {

        /* Check the cache again in case it got updated by a different thread. */
        SoftReference<StoreEntry> ref = this.cache.get(key);
        StoreEntry result = (ref != null) ? ref.get() : null;

        if (result == null) {

            /* Keep the write lock and expect that the client will call create(Key, int) very soon. */
        } else if (result.isExpired()) {

            /*
             * Remove the expired lock and signal to the client that they are the first one in the new period. Keep the
             * write lock in the expectation that the client will call create(Key, int),
             */
            cache.put(key, null);
            result = null;
        } else {

            /*
             * A different thread won and populated it already. Release the write lock and return the good non-null
             * result.
             */
            w.unlock();
        }

        return result;
    }

}