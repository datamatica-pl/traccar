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
package org.traccar;

import java.util.Map;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;
import org.traccar.helper.Log;
import org.traccar.model.Command;
import org.traccar.model.Device;

public abstract class BaseProtocolEncoder extends OneToOneEncoder {

    protected String getUniqueId(long deviceId) {
        return Context.getIdentityManager().getDeviceById(deviceId).getUniqueId();
    }
    
    protected Device getDevice(long deviceId) {
        return Context.getIdentityManager().getDeviceById(deviceId);
    }

    @Override
    protected Object encode(ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception {

        if (msg instanceof Command) {
            Command command = (Command) msg;
            Object encodedCommand = encodeCommand(command);

            // Log command
            StringBuilder s = new StringBuilder();
            s.append(String.format("[%08X] ", channel.getId()));
            s.append("COMMAND deviceId:").append(command.getDeviceId()).append(", ");
            s.append("type: ").append(command.getType()).append(", ");
            s.append("params: [");
            for(Map.Entry<String, Object> param : command.getAttributes().entrySet())
                s.append(param.getKey()).append(": ").append(param.getValue()).append(", ");
            s.append("] ");
            if (encodedCommand != null) {
                s.append("sent");
            } else {
                s.append("not sent");
            }
            Log.info(s.toString());

            return encodedCommand;
        }

        return msg;
    }

    protected abstract Object encodeCommand(Command command);

}
