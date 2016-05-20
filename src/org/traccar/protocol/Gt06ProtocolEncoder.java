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

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.traccar.BaseProtocolEncoder;
import org.traccar.helper.Checksum;
import org.traccar.helper.Log;
import org.traccar.model.Command;

import java.nio.charset.Charset;

public class Gt06ProtocolEncoder extends BaseProtocolEncoder {

    private ChannelBuffer encodeContent(String content) {

        ChannelBuffer buf = ChannelBuffers.dynamicBuffer();

        buf.writeByte(0x78);
        buf.writeByte(0x78);

        buf.writeByte(1 + 1 + 4 + content.length() + 2 + 2); // message length

        buf.writeByte(0x80); // message type

        buf.writeByte(4 + content.length()); // command length
        buf.writeInt(0);
        buf.writeBytes(content.getBytes(Charset.defaultCharset())); // command

        buf.writeShort(0); // message index

        buf.writeShort(Checksum.crc16(Checksum.CRC16_X25, buf.toByteBuffer(2, buf.writerIndex() - 2)));

        buf.writeByte('\r');
        buf.writeByte('\n');

        return buf;
    }

    @Override
    protected Object encodeCommand(Command command) {

        switch (command.getType()) {
            case Command.TYPE_ENGINE_STOP:
                return encodeContent("Relay,1#");
            case Command.TYPE_ENGINE_RESUME:
                return encodeContent("Relay,0#");
            case Command.TYPE_SET_DEFENSE_TIME:
                String defenseTime = command.getAttributes().get(Command.KEY_DEFENSE_TIME).toString();
                return encodeContent("DEFENSE," + defenseTime + "#");
            case Command.TYPE_GET_PARAMS:
                return encodeContent("PARAM#");
            case Command.TYPE_SET_TIMEZONE:
                Long offset = (Long)command.getAttributes().get(Command.KEY_TIMEZONE);
                return encodeContent(this.timeZoneCommand(offset));
            case Command.TYPE_SET_SOS_NUMBERS:
                String SOSNumber1 = "";
                String SOSNumber2 = "";
                String SOSNumber3 = "";
                if (command.getAttributes().get(Command.KEY_SOS_NUMBER_1) != null) {
                    SOSNumber1 = command.getAttributes().get(Command.KEY_SOS_NUMBER_1).toString();
                }
                if (command.getAttributes().get(Command.KEY_SOS_NUMBER_2) != null) {
                    SOSNumber2 = command.getAttributes().get(Command.KEY_SOS_NUMBER_2).toString();
                }
                if (command.getAttributes().get(Command.KEY_SOS_NUMBER_3) != null) {
                    SOSNumber3 = command.getAttributes().get(Command.KEY_SOS_NUMBER_3).toString();
                }
                return encodeContent("SOS,A," + SOSNumber1 + "," + SOSNumber2 + "," + SOSNumber3 + "#");
            case Command.TYPE_DELETE_SOS_NUMBER:
                String phoneNumber = command.getAttributes().get(Command.KEY_SOS_NUMBER).toString();
                return encodeContent("SOS,D," + phoneNumber + "#");
            case Command.TYPE_SET_CENTER_NUMBER:
                String centerNumber = "";
                if (command.getAttributes().get(Command.KEY_CENTER_NUMBER) != null) {
                    centerNumber = command.getAttributes().get(Command.KEY_CENTER_NUMBER).toString();
                }
                if ("".equals(centerNumber)) {
                    return encodeContent("CENTER,D#"); // Delete center number
                } else {
                    return encodeContent("CENTER,A," + centerNumber + "#"); // Set center number
                }
            case Command.TYPE_REBOOT_DEVICE:
                return encodeContent("RESET#");
            case Command.TYPE_POSITION_PERIODIC:
                String frequency = command.getAttributes().get(Command.KEY_FREQUENCY).toString();
                return encodeContent("TIMER," + frequency + "," + frequency + "#");
            case Command.TYPE_FACTORY_SETTINGS:
                return encodeContent("FACTORY#");
            default:
                Log.warning(new UnsupportedOperationException(command.getType()));
                break;
        }

        return null;
    }
    
    // Convert tz offset in seconds to form acceptable by GT06 device and return whole command
    private String timeZoneCommand(Long offset) {
        char timezoneDirection;
        if (offset < 0) {
            timezoneDirection = 'W';
        } else {
            timezoneDirection = 'E';
        }
        
        Long offsetABS = Math.abs(offset);
        Integer oneHourSeconds = 3600;
        Integer quarterSeconds = 900;
        Long offsetHours = offsetABS / oneHourSeconds;
        Long offsetRemainder = offsetABS % oneHourSeconds;
        Long quartersNum = offsetRemainder / quarterSeconds;
        Long offsetMinutes = quartersNum * 15;
        
        return "GMT," + timezoneDirection + "," + offsetHours + "," + offsetMinutes + "#";
    }

}
