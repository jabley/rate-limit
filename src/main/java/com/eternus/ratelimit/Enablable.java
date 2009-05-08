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
 * Interface defining whether a service can be enabled or not.
 * 
 * @author jabley
 *
 */
public interface Enablable {

    /**
     * Returns true if this service is enabled, otherwise false.
     * 
     * @return true if this service is enabled, otherwise false
     */
    boolean isEnabled();

    /**
     * Sets the enabled state of this service.
     * 
     * @param enabled
     *            the enabled state
     */
    void setEnabled(boolean enabled);

}