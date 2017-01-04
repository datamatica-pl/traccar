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
     * Teltonica protocol command types taken from "FM1000 ST User Manual V2.8"
     * Manual's file name: FM1000-ST-User-Manual-v-2.8.pdf
     * Phrazes in comments like "FM1000 manual: 9.5.2.4" or "9.5.2.4" means chapter 9.5.2.4 in this document
     */
    private static class TeltonikaCommand {
        public static final String HOME_NET_SEND_PERIOD_RUN = "1554"; // FM1000 manual: 9.5.2.5
        public static final String HOME_NET_SEND_PERIOD_STOP = "1544"; // 9.5.1.3
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
            case Command.TYPE_POSITION_SINGLE:
                return encodeContent("getgps");
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
