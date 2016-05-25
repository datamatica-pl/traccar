/*
 * Copyright 2015 Anton Tananaev (anton.tananaev@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.traccar.database;

import java.lang.reflect.InvocationTargetException;
import java.net.SocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jboss.netty.channel.Channel;
import org.traccar.Protocol;
import org.traccar.helper.Log;
import org.traccar.model.Command;

public class ActiveDevice {
    
    private final long deviceId;
    private final Protocol protocol;
    private final Channel channel;
    private final SocketAddress remoteAddress;
    private Object handler;
    private boolean isBusy;

    public ActiveDevice(long deviceId, Protocol protocol, Channel channel, SocketAddress remoteAddress) {
        this.deviceId = deviceId;
        this.protocol = protocol;
        this.channel = channel;
        this.remoteAddress = remoteAddress;
        this.isBusy = false;
    }

    public Channel getChannel() {
        return channel;
    }

    public long getDeviceId() {
        return deviceId;
    }
    
    public void sendCommand(Command command) {
        try {
            sendCommand(command, null);
        } catch (InterruptedException ex) {
            Logger.getLogger(ActiveDevice.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void sendCommand(Command command, final Object handler) throws InterruptedException {
        if(isBusy)
            Log.debug("busy!");
        lockChannel();
        Log.debug("sendCommand enter, handler == null?"+(this.handler==null));
        this.handler = handler;
        Log.debug("sendCommand enter, new handler == null?"+(this.handler==null));
        protocol.sendCommand(this, command);
        Log.debug("sendCommand exit, handler == null?"+(this.handler == null));
    }

    public void write(Object message) {
        Log.debug("write eneter, handler == null?"+(this.handler == null));
        getChannel().write(message, remoteAddress);
        Log.debug("write exit, handler == null?"+(this.handler == null));
    }
    
    public void write(Object message, Object handler) throws InterruptedException {
        lockChannel();
        this.handler = handler;
        getChannel().write(message, remoteAddress);
    }
    
    public void lockChannel() throws InterruptedException {
        synchronized(channel) {
            while(isBusy)
                channel.wait();
            isBusy = true;
        }
    }

    public void onCommandResponse(String message) {
        Log.debug("================Device answer received==============");
        Log.debug("handler == null?"+(handler == null));

        if(handler != null){
            try {
                handler.getClass().getDeclaredMethod("success", String.class)
                        .invoke(handler, message);
            } catch (IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | IllegalAccessException ex) {
                Logger.getLogger(ActiveDevice.class.getName()).log(Level.SEVERE, null, ex);
            }
            Log.debug("Handler called with message:");
            Log.debug(message);
        }
        synchronized(ActiveDevice.this.channel) {
            handler = null;
            isBusy = false;
            channel.notify();
        }
    }
}
