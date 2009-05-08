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
 * Simple {@link Key} implementation used for tests. It relies on the default {@link #equals(Object)} and
 * {@link #hashCode()} implementations, which are sufficient for test purposes, since we treat each instance as the same
 * {@link Key} per test.
 * 
 * @author jabley
 * 
 */
class RateLimiterKey implements Key {

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "test-key-" + System.identityHashCode(this);
    }

}