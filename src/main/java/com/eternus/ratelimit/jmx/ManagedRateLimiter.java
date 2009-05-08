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
package com.eternus.ratelimit.jmx;

import javax.management.MBeanNotificationInfo;
import javax.management.Notification;
import javax.management.NotificationBroadcasterSupport;
import javax.management.monitor.MonitorNotification;

import com.eternus.ratelimit.Key;
import com.eternus.ratelimit.RateLimiter;
import com.eternus.ratelimit.Token;

/**
 * JMX MBean which will send out notifications around a {@link RateLimiter} implementation.
 * 
 * @author jabley
 * 
 */
public class ManagedRateLimiter extends NotificationBroadcasterSupport implements RateLimiter, ManagedRateLimiterMBean {

    /**
     * The name of the JXM notification that will be sent for successfully serviced requests.
     */
    private static final String JMX_MONITOR_RATE_LIMIT_SERVICE_TYPE = "jmx.monitor.rate-limit.service";

    /**
     * The non-null delegate.
     */
    private final RateLimiter delegate;

    /**
     * The JMX notification sequence number.
     */
    private long sequenceNumber;

    /**
     * Creates a new {@link ManagedRateLimiter} which will delegate the implementation to the specified non-null
     * {@link RateLimiter}.
     * 
     * @param delegate
     *            a non-null {@link RateLimiter} around which this MBean will send notifications
     */
    public ManagedRateLimiter(RateLimiter delegate) {
        if (delegate == null) {
            throw new IllegalArgumentException("delegate cannot be null");
        }
        this.delegate = delegate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MBeanNotificationInfo[] getNotificationInfo() {
        String[] types = new String[] { JMX_MONITOR_RATE_LIMIT_SERVICE_TYPE,
                MonitorNotification.THRESHOLD_VALUE_EXCEEDED };
        MBeanNotificationInfo info = new MBeanNotificationInfo(types, Notification.class.getName(),
                "rate-limited request processed");
        return new MBeanNotificationInfo[] { info };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Token getToken(Key key) {
        Token token = delegate.getToken(key);

        if (token.isUsable()) {
            sendNotification(new Notification(JMX_MONITOR_RATE_LIMIT_SERVICE_TYPE, this, getSequenceNumber(),
                    "allowed request " + key));
        } else {
            sendNotification(new Notification(MonitorNotification.THRESHOLD_VALUE_EXCEEDED, this, getSequenceNumber(),
                    "denied request " + key));
        }

        return token;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isEnabled() {
        return this.delegate.isEnabled();
    }

    /**
     * {@inheritDoc}
     */
    public void setEnabled(boolean enabled) {
        this.delegate.setEnabled(enabled);
    }

    /**
     * Returns the next sequence number for the JMX notification.
     * 
     * @return the next positive sequence number
     */
    private synchronized long getSequenceNumber() {
        return ++this.sequenceNumber;
    }

}
