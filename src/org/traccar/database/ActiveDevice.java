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
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jboss.netty.channel.Channel;
import org.traccar.Protocol;
import org.traccar.model.Command;

public class ActiveDevice {
    
    private final long deviceId;
    private final Protocol protocol;
    private final Channel channel;
    private final SocketAddress remoteAddress;
    private Object handler;
    private final Semaphore semaphore;
    private Timer timer;
    
    public static final int COMMAND_TIMEOUT = 15*1000;

    public ActiveDevice(long deviceId, Protocol protocol, Channel channel, SocketAddress remoteAddress) {
        this.deviceId = deviceId;
        this.protocol = protocol;
        this.channel = channel;
        this.remoteAddress = remoteAddress;
        this.semaphore = new Semaphore(1);
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
        lockChannel();
        this.handler = handler;
        try{
            protocol.sendCommand(this, command);
        }catch(Exception e) {
            onCommandFail();
            throw e;
        }
    }

    public void write(Object message) {
        getChannel().write(message, remoteAddress);
    }
    
    public void write(Object message, Object handler) throws InterruptedException {
        lockChannel();
        this.handler = handler;
        getChannel().write(message, remoteAddress);
    }
    
    public void lockChannel() throws InterruptedException {
        semaphore.acquire();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                onCommandFail();
            }
        }, COMMAND_TIMEOUT);
    }
    
    public void onCommandResponse(String message) {
        timer.cancel();
        if(handler != null){
            try {
                handler.getClass().getDeclaredMethod("success", String.class)
                        .invoke(handler, message);
            } catch (IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | IllegalAccessException ex) {
                Logger.getLogger(ActiveDevice.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        handler = null;
        semaphore.release();
    }
    
    private void onCommandFail() {
        if(handler != null)
            try {
                handler.getClass().getDeclaredMethod("fail").invoke(handler);
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                Logger.getLogger(ActiveDevice.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                handler = null;
                semaphore.release();
            }
    }
}
