/*
 * Copyright 2015 - 2016 Anton Tananaev (anton.tananaev@gmail.com)
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
import org.jboss.netty.handler.codec.string.StringDecoder;
import org.jboss.netty.handler.codec.string.StringEncoder;
import org.traccar.BaseProtocol;
import org.traccar.CharacterDelimiterFrameDecoder;
import org.traccar.TrackerServer;
import org.traccar.model.Command;

import java.util.List;

public class MiniFinderProtocol extends BaseProtocol {

    public MiniFinderProtocol() {
        super("minifinder");
        setSupportedCommands(
            Command.TYPE_POSITION_SINGLE,
            Command.TYPE_POSITION_PERIODIC,
            Command.TYPE_SET_TIMEZONE,
            Command.TYPE_GET_STATUS,
            Command.TYPE_REBOOT_DEVICE,
            Command.TYPE_SET_SOS_NUMBER,
            Command.TYPE_DELETE_SOS_NUMBER,
            Command.TYPE_SET_SECOND_NUMBER,
            Command.TYPE_DELETE_SECOND_NUMBER,
            Command.TYPE_SET_THIRD_NUMBER,
            Command.TYPE_DELETE_THIRD_NUMBER,
            Command.TYPE_LISTEN_MODE,
            Command.TYPE_VOICE_CALL_MODE,
            Command.TYPE_SLEEP_MODE,
            Command.TYPE_EXIT_SLEEP_MODE,
            Command.TYPE_SET_AGPS_ON,
            Command.TYPE_SET_AGPS_OFF,
            Command.TYPE_CUSTOM,
            Command.TYPE_EXTENDED_CUSTOM
        );
    }

    @Override
    public void initTrackerServers(List<TrackerServer> serverList) {
        serverList.add(new TrackerServer(new ServerBootstrap(), this.getName()) {
            @Override
            protected void addSpecificHandlers(ChannelPipeline pipeline) {
                pipeline.addLast("frameDecoder", new CharacterDelimiterFrameDecoder(1024, ';'));
                pipeline.addLast("stringEncoder", new StringEncoder());
                pipeline.addLast("stringDecoder", new StringDecoder());
                pipeline.addLast("objectEncoder", new MiniFinderProtocolEncoder());
                pipeline.addLast("objectDecoder", new MiniFinderProtocolDecoder(MiniFinderProtocol.this));
            }
        });
    }

}
