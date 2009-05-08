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
 * Interface defining an extensible enumeration for return values from {@link RateLimiter#getToken(Key)}
 * 
 * @author jabley
 */
public interface Token {

    /**
     * Returns true if this Token means that the client should be safe to proceed, otherwise false.
     * 
     * @return true if the client should proceed, otherwise false
     */
    boolean isUsable();
}