/*
 * Copyright 2016 Anton Tananaev (anton@traccar.org)
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

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.traccar.BaseProtocolEncoder;
import org.traccar.helper.Checksum;
import org.traccar.helper.Log;
import org.traccar.model.Command;

import java.nio.charset.StandardCharsets;

public class TeltonikaProtocolEncoder extends BaseProtocolEncoder {

    private static final String SET_PARAM_FORMAT = "setparam %s %s";

    /**
     * Teltonika protocol command types taken from "FM1000 ST User Manual V2.8" and "FMA 120 User Manual V1.03"
     * Comments like "FM1000: 9.5.1.3" means Teltonika FM1000 manual
     * Comments like "FM120: ..." means Teltonika FMA120 manual
     */
    private static class TeltonikaCommand {
        public static final String HOME_NET_SEND_PERIOD_RUN = "1554"; // FM1000 manual: 9.5.2.5
        public static final String HOME_NET_SEND_PERIOD_STOP = "1544"; // FM1000: 9.5.1.3
        public static final String GET_GPS = "getgps";
        public static final String CPU_RESET = "cpureset";
        public static final String GET_STATUS = "getstatus";
        public static final String GET_INFO = "getinfo";
        public static final String FACTORY_RESET = "resetprof";
        public static final String TOWING_DETECTION = "1291"; // FM1000: 9.6.15
        public static final String TOWING_DISABLED = "0"; // FM1000: 9.6.15
        public static final String TOWING_HIGH_PRIORITY_EVENT = "2"; // FM1000: 9.6.15
        public static final String AUTO_GEOFENCING = "1101"; // FMA120: 8.6.11
        public static final String PARAM_DISABLE = "0";
        public static final String PARAM_ENABLE = "1";
        public static final String SET_AUTHORIZED_NUMBER_1 = "1260"; // FM1000: 9.4.14
    }

    private ChannelBuffer encodeContent(String content) {

        ChannelBuffer buf = ChannelBuffers.dynamicBuffer();

        buf.writeInt(0);
        buf.writeInt(content.length() + 10);
        buf.writeByte(TeltonikaProtocolDecoder.CODEC_12);
        buf.writeByte(1); // quantity
        buf.writeByte(5); // type
        buf.writeInt(content.length() + 2);
        buf.writeBytes(content.getBytes(StandardCharsets.US_ASCII));
        buf.writeByte('\r');
        buf.writeByte('\n');
        buf.writeByte(1); // quantity
        buf.writeInt(Checksum.crc16(Checksum.CRC16_IBM, buf.toByteBuffer(8, buf.writerIndex() - 8)));

        return buf;
    }

    @Override
    protected Object encodeCommand(Command command) {

        switch (command.getType()) {
            case Command.TYPE_AUTO_ALARM_ARM:
                return encodeContent(
                    String.format(SET_PARAM_FORMAT, TeltonikaCommand.AUTO_GEOFENCING,
                            TeltonikaCommand.PARAM_ENABLE)
                );
            case Command.TYPE_AUTO_ALARM_DISARM:
                return encodeContent(
                    String.format(SET_PARAM_FORMAT, TeltonikaCommand.AUTO_GEOFENCING,
                            TeltonikaCommand.PARAM_DISABLE)
                );
            case Command.TYPE_POSITION_SINGLE:
                return encodeContent(TeltonikaCommand.GET_GPS);
            case Command.TYPE_POSITION_PERIODIC:
                final String frequency = command.getAttributes().get(Command.KEY_FREQUENCY).toString();
                return encodeContent(
                    String.format(SET_PARAM_FORMAT, TeltonikaCommand.HOME_NET_SEND_PERIOD_RUN, frequency)
                );
            case Command.TYPE_POSITION_STOP:
                final String stopFrequency = command.getAttributes().get(Command.KEY_FREQUENCY).toString();
                return encodeContent(
                    String.format(SET_PARAM_FORMAT, TeltonikaCommand.HOME_NET_SEND_PERIOD_STOP, stopFrequency)
                );
            case Command.TYPE_SET_CENTER_NUMBER:
                String centerNumber = "";
                if (command.getAttributes().get(Command.KEY_CENTER_NUMBER) != null) {
                    centerNumber = command.getAttributes().get(Command.KEY_CENTER_NUMBER).toString();
                }
                return encodeContent(
                    String.format(SET_PARAM_FORMAT, TeltonikaCommand.SET_AUTHORIZED_NUMBER_1, centerNumber)
                );
            case Command.TYPE_REBOOT_DEVICE:
                return encodeContent(TeltonikaCommand.CPU_RESET);
            case Command.TYPE_FACTORY_SETTINGS:
                return encodeContent(TeltonikaCommand.FACTORY_RESET);
            case Command.TYPE_GET_STATUS:
                return encodeContent(TeltonikaCommand.GET_STATUS);
            case Command.TYPE_GET_PARAMS:
                return encodeContent(TeltonikaCommand.GET_INFO);
            case Command.TYPE_EXTENDED_CUSTOM:
                String customCommand = command.getAttributes().get(Command.KEY_MESSAGE).toString();
                return encodeContent(customCommand);
            default:
                Log.warning(new UnsupportedOperationException(command.getType()));
                break;
        }

        return null;
    }

}
