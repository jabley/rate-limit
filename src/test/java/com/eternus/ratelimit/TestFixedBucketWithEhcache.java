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

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;

import org.junit.After;
import org.junit.Before;


public class TestFixedBucketWithEhcache extends FixedBucketTests {

    private CacheManager cacheManager;
    
    private Ehcache cache;
    
    @Before
    public void setup() {
        this.cache = new Cache("test-token-store", 100, false, false, 100, 10);
        this.cacheManager = CacheManager.create();
        this.cacheManager.addCache(this.cache);
    }
    
    @After
    public void teardown() {
        cacheManager.shutdown();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected TokenStore createTokenStore() {
        EhcacheTokenStore tokenStore = new EhcacheTokenStore();
        tokenStore.setCache(this.cache);
        return tokenStore;
    }

}
