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
 * Interface defining how clients can check to see whether they should proceed before doing a request.
 * 
 * @author jabley
 * 
 */
public interface RateLimiter {

    /**
     * Method called by clients to check whether they should service the current request or not. Returns a non-null
     * {@link Token} which clients can then call {@link Token#isUsable()} to determine whether to proceed or not.
     * 
     * @param key
     *            the {@link Key}, which should have a good implementation of {@link #equals(Object)} and
     *            {@link #hashCode()} to ensure that types of request can be differentiated.
     * @return a non-null {@link Token}
     */
    Token getToken(Key key);

}
