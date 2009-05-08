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
 * Interface defining how {@link StoreEntry}s are managed.
 * 
 * @author jabley
 * 
 */
public interface TokenStore {

    /**
     * Returns a usable {@link StoreEntry} for the given {@link Key}. A value of {@code null} means that there is no
     * such {@link StoreEntry} and the calling client <strong>MUST</strong> call {@link #create(Key, int)} to avoid
     * other clients potentially being blocked without any hope of progressing. By usable, it is meant that the non-null
     * {@link StoreEntry} has not expired and can be used to determine whether the current client should be allowed to
     * proceed with the rate-limited action or not.
     * 
     * @param key
     *            the non-null {@link Key}
     * @return a {@link StoreEntry} or null
     */
    StoreEntry get(Key key);

    /**
     * Creates a new {@link StoreEntry}
     * 
     * @param key
     *            the non-null {@link Key}
     * @param timeToLiveInSecs
     *            the positive time-to-live in seconds
     * @return a non-null usable {@link StoreEntry}
     */
    StoreEntry create(Key key, int timeToLiveInSecs);

}
