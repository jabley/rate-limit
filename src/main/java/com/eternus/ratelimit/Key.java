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
 * Marker interface for objects passed into the {@link RateLimiter#getToken(Key)} method. Implementations should ensure
 * that good implementations of {@link #equals(Object)} and {@link #hashCode()} are provided, to ensure that the
 * {@link RateLimiter} implementations can portion out {@link Token}s based on differentiating the {@link Key}
 * instances.
 * 
 * @author jabley
 * 
 */
public interface Key {

}
