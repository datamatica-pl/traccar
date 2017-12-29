/*
 *  Copyright (C) 2017  Datamatica (dev@datamatica.pl)
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published
 *  by the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 * 
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.traccar.protocol;

import java.util.List;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.handler.codec.string.StringDecoder;
import org.jboss.netty.handler.codec.string.StringEncoder;
import org.traccar.BaseProtocol;
import org.traccar.CharacterDelimiterFrameDecoder;
import org.traccar.TrackerServer;
import org.traccar.model.Command;
import org.traccar.helper.Log;

/**
 *
 * @author piotrkrzeszewski
 */
public class GrooProtocol extends BaseProtocol {
    
    public GrooProtocol() {
        super("groo");
        setSupportedCommands(
                Command.TYPE_POSITION_PERIODIC,
                Command.TYPE_ACTIVE_POSITIONING,
                Command.TYPE_ACTIVE_HEART_RATE,
                Command.TYPE_ACTIVE_BLOOD_PRESSURE
//                Command.TYPE_ALARM_ARM,
//                Command.TYPE_ALARM_DISARM,
//                Command.TYPE_POSITION_STOP,
//                Command.TYPE_SET_CENTER_NUMBER,
//                Command.TYPE_REBOOT_DEVICE,
//                Command.TYPE_FACTORY_SETTINGS,
//                Command.TYPE_SET_SOS_NUMBERS,
//                Command.TYPE_GET_PARAMS,
//                Command.TYPE_SET_TIMEZONE,
//                Command.TYPE_AUTO_ALARM_DISARM,
//                Command.TYPE_AUTO_ALARM_ARM,
//                Command.TYPE_GET_STATUS,
//                Command.TYPE_POSITION_PERIODIC_ALT,
//                Command.TYPE_CUSTOM
        );
    }
    
    @Override
    public void initTrackerServers(List<TrackerServer> serverList) {
        serverList.add(new TrackerServer(new ServerBootstrap(), this.getName()) {
            @Override
            protected void addSpecificHandlers(ChannelPipeline pipeline) {
                pipeline.addLast("objectDecoder", new GrooProtocolDecoder(GrooProtocol.this));
                pipeline.addLast("stringEncoder", new StringEncoder());
                pipeline.addLast("objectEncoder", new GrooProtocolEncoder());
            }
        });
    }
    
}
