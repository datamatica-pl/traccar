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

import java.net.SocketAddress;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.traccar.BaseProtocolDecoder;
import org.traccar.helper.Checksum;
import org.traccar.helper.DateBuilder;
import org.traccar.helper.UnitsConverter;
import org.traccar.model.CommandResponse;
import org.traccar.model.Event;
import org.traccar.model.KeyValueCommandResponse;
import org.traccar.model.Position;

public class CastelProtocolDecoder extends BaseProtocolDecoder {
    
    static class TaggedValueReader implements Iterable<Map.Entry<String, String>>, 
            Iterator<Map.Entry<String, String>> {
        enum Tag {
            AUTHORIZED_NUMBER((short)0x0110),
            SMS_CENTER_NUMBER((short)0x0850),
            FIXED_UPLOAD_INTERVAL((short)0x0140);
            
            private static final Map<Short, Tag> map = new HashMap<>();
            static {
                for(Tag tag: Tag.values())
                    map.put(tag.tagCode, tag);
            }
            
            private short tagCode;
            Tag(short tagCode) {
                this.tagCode = tagCode;
            }
            
            public static Tag valueOf(short code) {
                return map.get(code);
            }
        }
        
        private static final int AUTHORIZED_NUMBER_COUNT = 3;
        private static final int AUTHORIZED_NUMBER_LENGTH = 22;
        private static final int AUTHORIZED_NUMBER_ID_LENGTH = 1;
        
        private ChannelBuffer buf;
        private short fails;
        private short successes;
        private byte readerIndex;

        public TaggedValueReader(ChannelBuffer buf) {
            this.buf = buf;
            fails = buf.readUnsignedByte();
            successes = buf.readUnsignedByte();
            readerIndex = 0;
        }

        private String readValue(Tag tag, ChannelBuffer buffer) {
            StringBuilder sb = new StringBuilder();
            switch(tag) {
                case AUTHORIZED_NUMBER:
                    sb.append("[");
                    for(int i=0;i<AUTHORIZED_NUMBER_COUNT;++i)
                        sb.append(readAuthorizedNumber(buffer.readBytes(AUTHORIZED_NUMBER_LENGTH))).append(", ");
                    sb.append("]");
                    break;
                case SMS_CENTER_NUMBER:
                    sb.append(readSMSCenterNumber(buffer));
                    break;
                case FIXED_UPLOAD_INTERVAL:
                    sb.append(readFixedUpload(buffer)).append(" s");
                    break;
            }
            return sb.toString();
        }
        
        private String readAuthorizedNumber(ChannelBuffer buffer) {
            buffer.skipBytes(AUTHORIZED_NUMBER_ID_LENGTH);
            return buffer.toString(StandardCharsets.US_ASCII).replace("\0", "");
        }
        
        private String readSMSCenterNumber(ChannelBuffer buffer) {
            return buffer.toString(StandardCharsets.US_ASCII).replace("\0", "");
        }
        
        private short readFixedUpload(ChannelBuffer buffer) {
            return buffer.readShort();
        }
        
        @Override
        public Iterator<Map.Entry<String, String>> iterator() {
            return this;
        }
        
        @Override
        public boolean hasNext() {
            return readerIndex < fails + successes;
        }

        @Override
        public Map.Entry<String, String> next() {
            Tag tag = Tag.valueOf(ChannelBuffers.swapShort(buf.readShort()));
            int valueLength = buf.readUnsignedShort();
            String value = readValue(tag, buf.readBytes(valueLength));
            readerIndex++;
            return new AbstractMap.SimpleEntry<>(tag.toString(), value);
        }
    }

    public CastelProtocolDecoder(CastelProtocol protocol) {
        super(protocol);
    }
    
    private static final int RESPONSE_PACKET_COUNT_LENGTH = 1;
    private static final int SEQUENCE_NUMBER_LENGTH = 2;
    private static final int PACKET_NUMBER_LENGTH = 1;

    private static final short MSG_SC_LOGIN = 0x1001;
    private static final short MSG_SC_LOGIN_RESPONSE = (short) 0x9001;
    private static final short MSG_SC_LOGOUT = 0x1002;
    private static final short MSG_SC_HEARTBEAT = 0x1003;
    private static final short MSG_SC_HEARTBEAT_RESPONSE = (short) 0x9003;
    private static final short MSG_SC_GPS = 0x4001;
    private static final short MSG_SC_ALARM = 0x4007;
    private static final short MSG_SC_GPS_SLEEP = 0x4009;
    private static final short MSG_SC_AGPS_REQUEST = 0x5101;
    private static final short MSG_SC_CURRENT_LOCATION = (short) 0xB001;

    private static final short MSG_CC_LOGIN = 0x4001;
    private static final short MSG_CC_LOGIN_RESPONSE = (short) 0x8001;
    private static final short MSG_CC_HEARTBEAT = 0x4206;
    private static final short MSG_CC_HEARTBEAT_RESPONSE = (short) 0x8206;
    private static final short MSG_CC_SET = 0x5101;
    private static final short MSG_CC_GPS_UPLOAD_INTERVAL = 0x4001;
    private static final short MSG_PT_HEARTBEAT = 0x4003;
    private static final short MSG_PT_HEARTBEAT_RESPONSE = (short)0x8003;
    private static final short MSG_COMMAND_RESPONSE = (short)0x9101;
    private static final short MSG_GET_PARAMS_RESPONSE = (short)0x9201;
    private static final short MSG_ENGINE_ON_OFF_RESPONSE = (short) 0x8583;

    private Position readPosition(ChannelBuffer buf) {

        Position position = new Position();
        position.setProtocol(getProtocolName());
        position.setDeviceId(getDeviceId());

        DateBuilder dateBuilder = new DateBuilder()
                .setDateReverse(buf.readUnsignedByte(), buf.readUnsignedByte(), buf.readUnsignedByte())
                .setTime(buf.readUnsignedByte(), buf.readUnsignedByte(), buf.readUnsignedByte());
        position.setTime(dateBuilder.getDate());

        double lat = buf.readUnsignedInt() / 3600000.0;
        double lon = buf.readUnsignedInt() / 3600000.0;
        position.setSpeed(UnitsConverter.knotsFromCps(buf.readUnsignedShort()));
        position.setCourse(buf.readUnsignedShort() % 360);

        int flags = buf.readUnsignedByte();
        if ((flags & 0x02) == 0) {
            lat = -lat;
        }
        if ((flags & 0x01) == 0) {
            lon = -lon;
        }
        position.setLatitude(lat);
        position.setLongitude(lon);
        position.setValid((flags & 0x0C) > 0);
        position.set(Event.KEY_SATELLITES, flags >> 4);

        return position;
    }

    private void sendResponse(
            Channel channel, SocketAddress remoteAddress,
            int version, ChannelBuffer id, short type, ChannelBuffer content) {

        if (channel != null) {
            int length = 2 + 2 + 1 + id.readableBytes() + 2 + 2 + 2;
            if (content != null) {
                length += content.readableBytes();
            }

            ChannelBuffer response = ChannelBuffers.directBuffer(ByteOrder.LITTLE_ENDIAN, length);
            response.writeByte('@'); response.writeByte('@');
            response.writeShort(length);
            response.writeByte(version);
            response.writeBytes(id);
            response.writeShort(ChannelBuffers.swapShort(type));
            if (content != null) {
                response.writeBytes(content);
            }
            response.writeShort(
                    Checksum.crc16(Checksum.CRC16_X25, response.toByteBuffer(0, response.writerIndex())));
            response.writeByte(0x0D); response.writeByte(0x0A);
            channel.write(response, remoteAddress);
        }
    }

    private void sendResponse(
            Channel channel, SocketAddress remoteAddress, ChannelBuffer id, short type) {

        if (channel != null) {
            int length = 2 + 2 + id.readableBytes() + 2 + 4 + 8 + 2 + 2;

            ChannelBuffer response = ChannelBuffers.directBuffer(ByteOrder.LITTLE_ENDIAN, length);
            response.writeByte('@'); response.writeByte('@');
            response.writeShort(length);
            response.writeBytes(id);
            response.writeShort(ChannelBuffers.swapShort(type));
            response.writeInt(0);
            for (int i = 0; i < 8; i++) {
                response.writeByte(0xff);
            }
            response.writeShort(
                    Checksum.crc16(Checksum.CRC16_X25, response.toByteBuffer(0, response.writerIndex())));
            response.writeByte(0x0D); response.writeByte(0x0A);
            channel.write(response, remoteAddress);
        }
    }

    @Override
    protected Object decode(
            Channel channel, SocketAddress remoteAddress, Object msg) throws Exception {

        ChannelBuffer buf = (ChannelBuffer) msg;

        int header = buf.readUnsignedShort();
        buf.readUnsignedShort(); // length

        int version = -1;
        if (header == 0x4040) {
            version = buf.readUnsignedByte();
        }

        ChannelBuffer id = buf.readBytes(20);
        int type = ChannelBuffers.swapShort(buf.readShort());

        if (!identify(id.toString(Charset.defaultCharset()).trim(), channel, remoteAddress)) {
            return null;
        }
        
        if(type == MSG_COMMAND_RESPONSE) {
            return handleCommandResponse(buf);
        } else if(type == MSG_GET_PARAMS_RESPONSE) {
            return handleGetParamsResponse(buf);
        } else if(type == MSG_ENGINE_ON_OFF_RESPONSE) {
            return handleEngineOnOffResponse(buf);
        }

        if (version == -1) {

            if (type == 0x2001) {

                sendResponse(channel, remoteAddress, id, (short) 0x1001);

                buf.readUnsignedInt(); // index
                buf.readUnsignedInt(); // unix time
                buf.readUnsignedByte();

                return readPosition(buf);

            }

        } else if (version == 4) {

            if (type == MSG_SC_HEARTBEAT) {

                sendResponse(channel, remoteAddress, version, id, MSG_SC_HEARTBEAT_RESPONSE, null);

            } else if (type == MSG_SC_LOGIN || type == MSG_SC_LOGOUT || type == MSG_SC_GPS
                    || type == MSG_SC_ALARM || type == MSG_SC_CURRENT_LOCATION) {

                if (type == MSG_SC_LOGIN) {
                    ChannelBuffer response = ChannelBuffers.directBuffer(ByteOrder.LITTLE_ENDIAN, 10);
                    response.writeInt(0xFFFFFFFF);
                    response.writeShort(0);
                    response.writeInt((int) (System.currentTimeMillis() / 1000));
                    sendResponse(channel, remoteAddress, version, id, MSG_SC_LOGIN_RESPONSE, response);
                }

                if (type == MSG_SC_GPS) {
                    buf.readUnsignedByte(); // historical
                } else if (type == MSG_SC_ALARM) {
                    buf.readUnsignedInt(); // alarm
                } else if (type == MSG_SC_CURRENT_LOCATION) {
                    buf.readUnsignedShort();
                }

                buf.readUnsignedInt(); // ACC ON time
                buf.readUnsignedInt(); // UTC time
                long odometer = buf.readUnsignedInt();
                buf.readUnsignedInt(); // trip odometer
                buf.readUnsignedInt(); // total fuel consumption
                buf.readUnsignedShort(); // current fuel consumption
                long status = buf.readUnsignedInt();
                buf.skipBytes(8);

                int count = buf.readUnsignedByte();

                List<Position> positions = new LinkedList<>();

                for (int i = 0; i < count; i++) {
                    Position position = readPosition(buf);
                    position.set(Event.KEY_ODOMETER, odometer);
                    position.set(Event.KEY_STATUS, status);
                    positions.add(position);
                }

                if (!positions.isEmpty()) {
                    return positions;
                }

            } else if (type == MSG_SC_GPS_SLEEP || type == MSG_SC_AGPS_REQUEST) {

                return readPosition(buf);

            }

        } else {

            if (version == 17 && type == MSG_PT_HEARTBEAT) 
                sendResponse(channel, remoteAddress, version, id, MSG_PT_HEARTBEAT_RESPONSE, null);
            else if (type == MSG_CC_HEARTBEAT) {

                sendResponse(channel, remoteAddress, version, id, MSG_CC_HEARTBEAT_RESPONSE, null);

                buf.readUnsignedByte(); // 0x01 for history
                int count = buf.readUnsignedByte();

                List<Position> positions = new LinkedList<>();

                for (int i = 0; i < count; i++) {
                    Position position = readPosition(buf);

                    position.set(Event.KEY_STATUS, buf.readUnsignedInt());
                    position.set(Event.KEY_BATTERY, buf.readUnsignedByte());
                    position.set(Event.KEY_ODOMETER, buf.readUnsignedInt());

                    buf.readUnsignedByte(); // geo-fencing id
                    buf.readUnsignedByte(); // geo-fencing flags
                    buf.readUnsignedByte(); // additional flags

                    position.set(Event.KEY_LAC, buf.readUnsignedShort());
                    position.set(Event.KEY_CID, buf.readUnsignedShort());

                    positions.add(position);
                }

                return positions;

            } else if (type == MSG_CC_LOGIN) {

                sendResponse(channel, remoteAddress, version, id, MSG_CC_LOGIN_RESPONSE, null);

                Position position = readPosition(buf);

                position.set(Event.KEY_STATUS, buf.readUnsignedInt());
                position.set(Event.KEY_BATTERY, buf.readUnsignedByte());
                position.set(Event.KEY_ODOMETER, buf.readUnsignedInt());

                buf.readUnsignedByte(); // geo-fencing id
                buf.readUnsignedByte(); // geo-fencing flags
                buf.readUnsignedByte(); // additional flags

                // GSM_CELL_CODE
                // STR_Z - firmware version
                // STR_Z - hardware version

                return position;

            }

        }

        return null;
    }

    private ChannelBuffer buildSetPackage(short cmd_id, short value) {
        ChannelBuffer buffer = ChannelBuffers.directBuffer(ByteOrder.LITTLE_ENDIAN, 10);
        buffer.writeShort(ChannelBuffers.swapShort(cmd_id));
        buffer.writeByte(1);
        buffer.writeShort(cmd_id);
        buffer.writeShort(2);
        buffer.writeShort(value);
        return buffer;
    }

    private CommandResponse handleCommandResponse(ChannelBuffer buf) {
        buf.skipBytes(SEQUENCE_NUMBER_LENGTH);
        if(readCommandResponse(buf))
            return new CommandResponse(getActiveDevice(), true);
        else
            return new CommandResponse(getActiveDevice(), false);        
    }
    
    private boolean readCommandResponse(ChannelBuffer buf) {
        byte successCount = buf.readByte();
        return successCount > 0;
    }

    private CommandResponse handleGetParamsResponse(ChannelBuffer buf) {
        buf.skipBytes(SEQUENCE_NUMBER_LENGTH + RESPONSE_PACKET_COUNT_LENGTH + PACKET_NUMBER_LENGTH);
        KeyValueCommandResponse response = new KeyValueCommandResponse(getActiveDevice());
        TaggedValueReader reader = new TaggedValueReader(buf);
        for(Map.Entry<String, String> taggedValue : reader)
            response.put(taggedValue.getKey(), taggedValue.getValue());
        return response;
    }
    
    private CommandResponse handleEngineOnOffResponse(ChannelBuffer buf) {
        byte action = buf.readByte();
        byte result = buf.readByte();
        if(result == 0x01)
            return new CommandResponse(getActiveDevice(), true);
        else
            return new CommandResponse(getActiveDevice(), false);
    }
}
