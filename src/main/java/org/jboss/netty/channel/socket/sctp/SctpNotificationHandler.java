/*
 * Copyright 2011 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package org.jboss.netty.channel.socket.sctp;

import com.sun.nio.sctp.*;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLoggerFactory;

/**
 * @author <a href="http://www.jboss.org/netty/">The Netty Project</a>
 * @author <a href="http://github.com/jestan">Jestan Nirojan</a>
 */

class SctpNotificationHandler extends AbstractNotificationHandler {

    private static final InternalLogger logger =
            InternalLoggerFactory.getInstance(SctpNotificationHandler.class);

    private final SctpChannelImpl channel;
    private final ChannelPipeline pipeline;

    public SctpNotificationHandler(SctpChannelImpl channel) {
        this.channel = channel;
        this.pipeline = channel.getPipeline();
    }

    @Override
    public HandlerResult handleNotification(AssociationChangeNotification notification, Object o) {
        fireNotificationReceived(notification, o);
        return HandlerResult.CONTINUE;
    }

    @Override
    public HandlerResult handleNotification(PeerAddressChangeNotification notification, Object o) {
        fireNotificationReceived(notification, o);
        return HandlerResult.CONTINUE;
    }

    @Override
    public HandlerResult handleNotification(SendFailedNotification notification, Object o) {
        fireNotificationReceived(notification, o);
        return HandlerResult.RETURN;
    }

    @Override
    public HandlerResult handleNotification(ShutdownNotification notification, Object o) {
        Channels.fireChannelDisconnected(channel);
        return HandlerResult.RETURN;
    }

    private void fireNotificationReceived(Notification notification, Object o) {
        pipeline.sendUpstream(new SctpNotificationEvent(channel, notification, o));
    }
}
