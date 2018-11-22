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
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TeltonikaProtocolEncoder extends BaseProtocolEncoder {

    private static class TeltonikaFMBCommand {
        public static final String SET_PARAM_FORMAT = "setparam %s:%s";
        public static final String POSITION_PERIODIC = "setparam 10050:%s;10054:1;10055:%s";
        public static final String POSITION_STOP = "'setparam 10000:%s;10005:120'";
        public static final String TOWING_DETECTION_ON = "setparam 11600:3;11601:1;11602:5;11603:30;11604:0;7035:1";
        public static final String TOWING_DETECTION_OFF = "setparam 11600:0";
        public static final String SET_AUTHORIZED_NUMBER_1 = "4000";
        public static final String GET_GPS = "getgps";
        public static final String CPU_RESET = "cpureset";
        public static final String GET_STATUS = "getstatus";
        public static final String GET_INFO = "getinfo";
        public static final String FACTORY_RESET = "defaultcfg";
        public static final int AUTHORIZED_NUMS_BEGIN_INDEX = 4000;
        public static final int AUTHORIZED_NUMS_END_INDEX = 4199;
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

    private String getClearIdsRangeCommand(int begin, int end) {
        final String cmdIdsAndVals = IntStream
                .rangeClosed(begin, end)
                .mapToObj(Integer::toString)
                .collect(Collectors.joining(":;"));

        return "setparam " + cmdIdsAndVals + ":";
    }

    @Override
    protected Object encodeCommand(Command command) {

        switch (command.getType()) {
            case Command.TYPE_AUTO_ALARM_ARM:
                return encodeContent(TeltonikaFMBCommand.TOWING_DETECTION_ON);
            case Command.TYPE_AUTO_ALARM_DISARM:
                return encodeContent(TeltonikaFMBCommand.TOWING_DETECTION_OFF);
            case Command.TYPE_POSITION_SINGLE:
                return encodeContent(TeltonikaFMBCommand.GET_GPS);
            case Command.TYPE_POSITION_PERIODIC:
                final String frequency = command.getAttributes().get(Command.KEY_FREQUENCY).toString();
                return encodeContent(String.format(TeltonikaFMBCommand.POSITION_PERIODIC, frequency, frequency));
            case Command.TYPE_POSITION_STOP:
                final String stopFrequency = command.getAttributes().get(Command.KEY_FREQUENCY).toString();
                return encodeContent(String.format(TeltonikaFMBCommand.POSITION_STOP, stopFrequency));
            case Command.TYPE_SET_CENTER_NUMBER:
                String centerNumber = "";
                if (command.getAttributes().get(Command.KEY_CENTER_NUMBER) != null) {
                    centerNumber = command.getAttributes().get(Command.KEY_CENTER_NUMBER).toString();
                }
                return encodeContent(
                    String.format(TeltonikaFMBCommand.SET_PARAM_FORMAT,
                            TeltonikaFMBCommand.SET_AUTHORIZED_NUMBER_1, centerNumber)
                );
            case Command.TYPE_REBOOT_DEVICE:
                return encodeContent(TeltonikaFMBCommand.CPU_RESET);
            case Command.TYPE_FACTORY_SETTINGS:
                return encodeContent(TeltonikaFMBCommand.FACTORY_RESET);
            case Command.TYPE_GET_STATUS:
                return encodeContent(TeltonikaFMBCommand.GET_STATUS);
            case Command.TYPE_GET_PARAMS:
                return encodeContent(TeltonikaFMBCommand.GET_INFO);
            case Command.TYPE_EXTENDED_CUSTOM:
                String customCommand = command.getAttributes().get(Command.KEY_MESSAGE).toString();
                return encodeContent(customCommand);
            case Command.TYPE_DEL_AUTHORIZED_NUMS_FIRST_100_FMB:
                return encodeContent(
                        getClearIdsRangeCommand(TeltonikaFMBCommand.AUTHORIZED_NUMS_BEGIN_INDEX, 4099)
                );
            case Command.TYPE_DEL_AUTHORIZED_NUMS_SECOND_100_FMB:
                return encodeContent(
                        getClearIdsRangeCommand(4100, TeltonikaFMBCommand.AUTHORIZED_NUMS_END_INDEX)
                );
            default:
                Log.warning(new UnsupportedOperationException(command.getType()));
                break;
        }

        return null;
    }

}
