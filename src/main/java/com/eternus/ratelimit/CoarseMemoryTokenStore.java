/**
 * 
 */
package com.eternus.ratelimit;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * {@link TokenStore} that uses a coarse-grained lock to manage access to the internal StoreEntry items.
 * 
 * @author jabley
 *
 */
public class CoarseMemoryTokenStore implements TokenStore {

    /**
     * The Map used to keep track of {@link StoreEntry} instances.
     */
    private final Map<Key, StoreEntry> cache;
    
    /**
     * The lock used to synchronize on.
     */
    private final Lock lock;

    /**
     * Creates a new {@link CoarseMemoryTokenStore}.
     */
    public CoarseMemoryTokenStore() {
        this.cache = new HashMap<Key, StoreEntry>();
        this.lock = new ReentrantLock();
    }
    
    /**
     * {@inheritDoc}
     */
    public StoreEntry create(Key key, int timeToLiveInSecs) {
        try {
            StoreEntryImpl result = new StoreEntryImpl(timeToLiveInSecs);
            cache.put(key, result);
            return result;
        } finally {
            lock.unlock();
        }
    }

    /**
     * {@inheritDoc}
     */
    public StoreEntry get(Key key) {
        lock.lock();
        
        StoreEntry result = cache.get(key);
        
        if (!(result == null || result.isExpired())) {
            
            /* cache hit with good entry - use it. */
            lock.unlock();
            return result;
        }  else {
            
            /* cache miss or expired. keep the lock and the client will call #create(Key, int) */
            result = null;
            cache.put(key, result);
        }
        
        return result;
    }

}
