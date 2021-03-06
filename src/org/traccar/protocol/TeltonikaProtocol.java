/*
 * Copyright 2015 - 2016 Anton Tananaev (anton@traccar.org)
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

import org.jboss.netty.bootstrap.ConnectionlessBootstrap;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelPipeline;
import org.traccar.BaseProtocol;
import org.traccar.TrackerServer;
import org.traccar.model.Command;

import java.util.List;
import org.traccar.helper.TeltonikaBatteryVoltageToPercentCalc;

public class TeltonikaProtocol extends BaseProtocol {

    public TeltonikaProtocol() {
        super("teltonika");
        setSupportedCommands(
                Command.TYPE_AUTO_ALARM_ARM,
                Command.TYPE_AUTO_ALARM_DISARM,
                Command.TYPE_POSITION_SINGLE,
                Command.TYPE_POSITION_PERIODIC,
                Command.TYPE_POSITION_STOP,
                Command.TYPE_SET_CENTER_NUMBER,
                Command.TYPE_REBOOT_DEVICE,
                Command.TYPE_FACTORY_SETTINGS,
                Command.TYPE_GET_PARAMS,
                Command.TYPE_GET_STATUS,
                Command.TYPE_DEL_AUTHORIZED_NUMS_FIRST_100_FMB,
                Command.TYPE_DEL_AUTHORIZED_NUMS_SECOND_100_FMB,
                Command.TYPE_EXTENDED_CUSTOM);
    }

    @Override
    public void initTrackerServers(List<TrackerServer> serverList) {


        serverList.add(new TrackerServer(new ServerBootstrap(), getName()) {
            @Override
            protected void addSpecificHandlers(ChannelPipeline pipeline) {
                pipeline.addLast("frameDecoder", new TeltonikaFrameDecoder());
                pipeline.addLast("objectEncoder", new TeltonikaProtocolEncoder());
                pipeline.addLast("objectDecoder", new TeltonikaProtocolDecoder(TeltonikaProtocol.this,
                        new TeltonikaBatteryVoltageToPercentCalc()));
            }
        });
        serverList.add(new TrackerServer(new ConnectionlessBootstrap(), getName()) {
            @Override
            protected void addSpecificHandlers(ChannelPipeline pipeline) {
                pipeline.addLast("objectEncoder", new TeltonikaProtocolEncoder());
                pipeline.addLast("objectDecoder", new TeltonikaProtocolDecoder(TeltonikaProtocol.this,
                        new TeltonikaBatteryVoltageToPercentCalc()));
            }
        });
    }

}
