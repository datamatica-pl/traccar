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
package org.traccar.protocol;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.handler.codec.string.StringEncoder;
import org.traccar.BaseProtocol;
import org.traccar.TrackerServer;

import java.util.List;
import org.traccar.model.Command;

public class H02Protocol extends BaseProtocol {

    public H02Protocol() {
        super("h02");
        setSupportedCommands(
                Command.TYPE_ALARM_ARM,
                Command.TYPE_ALARM_DISARM,
                Command.TYPE_POSITION_PERIODIC,
                Command.TYPE_POSITION_STOP,
                Command.TYPE_SET_CENTER_NUMBER,
                Command.TYPE_REBOOT_DEVICE,
                Command.TYPE_FACTORY_SETTINGS,
                Command.TYPE_SET_SOS_NUMBERS,
                Command.TYPE_GET_PARAMS,
                Command.TYPE_SET_TIMEZONE,
                Command.TYPE_AUTO_ALARM_DISARM,
                Command.TYPE_AUTO_ALARM_ARM,
                Command.TYPE_GET_STATUS,
                Command.TYPE_POSITION_PERIODIC_ALT,
                Command.TYPE_CUSTOM,
                Command.TYPE_EXTENDED_CUSTOM
        );
    }

    @Override
    public void initTrackerServers(List<TrackerServer> serverList) {
        serverList.add(new TrackerServer(new ServerBootstrap(), this.getName()) {
            @Override
            protected void addSpecificHandlers(ChannelPipeline pipeline) {
                pipeline.addLast("frameDecoder", new H02FrameDecoder());
                pipeline.addLast("stringEncoder", new StringEncoder());
                pipeline.addLast("objectEncoder", new H02ProtocolEncoder());
                pipeline.addLast("objectDecoder", new H02ProtocolDecoder(H02Protocol.this));
            }
        });
    }
}
