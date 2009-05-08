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

import static org.junit.Assert.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Test;

/**
 * Tests for {@link FixedBucket} which are defined to use a variety of {@link TokenStore} implementations.
 * 
 * @author jabley
 * 
 */
public abstract class FixedBucketTests {

    @Test
    public void sequentialFixedBucketAccess() {
        FixedBucket rateLimiter = new FixedBucket();
        int allowedRequests = 1;
        rateLimiter.setAllowedRequests(allowedRequests);
        rateLimiter.setTokenStore(createTokenStore());
        rateLimiter.init();

        RateLimiterKey key = new RateLimiterKey();

        Token token = rateLimiter.getToken(key);
        assertTrue("We have a usable token back for the first request", token.isUsable());

        token = rateLimiter.getToken(key);

        assertFalse("The second token is not usable, since we assume that the two token"
                + " accesses take less than a second to perform", token.isUsable());
    }

    @Test
    public void canDoReasonableNumberOfTokenChecksPerSecond() throws Exception {
        FixedBucket rateLimiter = new FixedBucket();
        int allowedRequests = 50000;
        rateLimiter.setAllowedRequests(allowedRequests);
        rateLimiter.setTokenStore(createTokenStore());
        rateLimiter.init();

        RateLimiterKey key = new RateLimiterKey();

        Token token;

        for (int i = 0, n = allowedRequests; i < n; ++i) {
            token = rateLimiter.getToken(key);
            assertTrue("We have a usable token back for the first request", token.isUsable());
        }

        token = rateLimiter.getToken(key);

        assertFalse("The current token is not usable, since we assume that the " + allowedRequests + " token"
                + " accesses take less than a second to perform", token.isUsable());
    }

    @Test
    public void multipleClientsCanAccessWithoutBlocking() throws Exception {
        final FixedBucket rateLimiter = new FixedBucket();
        int allowedRequests = 100;
        rateLimiter.setAllowedRequests(allowedRequests);
        rateLimiter.setTokenStore(createTokenStore());
        rateLimiter.init();

        final RateLimiterKey key = new RateLimiterKey();

        int clientCount = 10;
        Runnable[] clients = new Runnable[clientCount];
        final boolean[] isUsable = new boolean[clientCount];

        final CountDownLatch startGate = new CountDownLatch(1);

        final CountDownLatch endGate = new CountDownLatch(clientCount);

        
        for (int i = 0, n = isUsable.length; i < n; ++i) {
            final int j = i; 
            clients[j] = new Runnable() {

                /**
                 * {@inheritDoc}
                 */
                public void run() {
                    try {
                        startGate.await();

                        isUsable[j] = rateLimiter.getToken(key).isUsable();

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        endGate.countDown();
                    }
                }
            };
        }

        ExecutorService executor = Executors.newFixedThreadPool(clientCount);
        
        for (Runnable runnable : clients) {
            executor.execute(runnable);
        }
        
        startGate.countDown();
        
        endGate.await();
        
        for (boolean b : isUsable) {
            assertTrue("Token was usable", b);
        }
    }

    /**
     * Factory Method to return a {@link TokenStore} for test usage.
     * 
     * @return a non-null {@link TokenStore}
     */
    protected abstract TokenStore createTokenStore();

}
