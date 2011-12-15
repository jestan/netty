/*
 * Copyright 2011 Red Hat, Inc.
 *
 * Red Hat licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package org.jboss.netty.util.internal;

import java.util.Collection;
import java.util.concurrent.BlockingQueue;

import org.jboss.netty.util.UnsafeDetectUtil;

/**
 * This factory should be used to create the "optimal" {@link BlockingQueue} instance for the running JVM.
 *
 * @author <a href="http://www.jboss.org/netty/">The Netty Project</a>
 * @author <a href="http://www.murkycloud.com/">Norman Maurer</a>
 */
@SuppressWarnings("unchecked")
public class QueueFactory {
    private static final String JDK_LINK_TRANSFER_QUEUE = "java.util.concurrent.LinkedTransferQueue";
    private static final boolean useUnsafe = UnsafeDetectUtil.isUnsafeFound();
    private static final boolean useJdkLinkTransferQueue = UnsafeDetectUtil.isJdkLinkTransferQueueFound();

    private QueueFactory() {
        // only use static methods!
    }

    private static final <T> T createInstance(final String className, Class<T> aClass) {
        try {
            final Class<T> clazz = (Class<T>) Class.forName(className);
            return clazz.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Create a new unbound {@link BlockingQueue}
     *
     * @param itemClass the {@link Class} type which will be used as {@link BlockingQueue} items
     * @return queue     the {@link BlockingQueue} implementation
     */
    public static final <T> BlockingQueue<T> createQueue(Class<T> itemClass) {
        if (useJdkLinkTransferQueue) {
            return (BlockingQueue<T>) createInstance(JDK_LINK_TRANSFER_QUEUE, itemClass);
        } else if (useUnsafe) {
            return new LinkedTransferQueue<T>();
        } else {
            return new LegacyLinkedTransferQueue<T>();
        }
    }

    /**
     * Create a new unbound {@link BlockingQueue}
     *
     * @param collection the collection which should get copied to the newly created {@link BlockingQueue}
     * @param itemClass  the {@link Class} type which will be used as {@link BlockingQueue} items
     * @return queue      the {@link BlockingQueue} implementation
     */
    public static final <T> BlockingQueue<T> createQueue(Collection<? extends T> collection, Class<T> itemClass) {
        if (useJdkLinkTransferQueue) {
            return (BlockingQueue<T>) createInstance(JDK_LINK_TRANSFER_QUEUE, itemClass);
        } else if (useUnsafe) {
            return new LinkedTransferQueue<T>(collection);
        } else {
            return new LegacyLinkedTransferQueue<T>(collection);
        }
    }
}
