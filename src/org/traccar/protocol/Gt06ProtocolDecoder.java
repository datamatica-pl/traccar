/*
 * Copyright 2012 - 2015 Anton Tananaev (anton.tananaev@gmail.com)
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
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.TimeZone;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.traccar.BaseProtocolDecoder;
import org.traccar.Context;
import org.traccar.helper.BitUtil;
import org.traccar.helper.Checksum;
import org.traccar.helper.DateBuilder;
import org.traccar.helper.Log;
import org.traccar.helper.UnitsConverter;
import org.traccar.model.CommandResponse;
import org.traccar.model.Event;
import org.traccar.model.KeyValueCommandResponse;
import static org.traccar.model.KeyValueCommandResponse.*;
import org.traccar.model.MessageCommandResponse;
import org.traccar.model.ObdInfo;
import org.traccar.model.Position;

public class Gt06ProtocolDecoder extends BaseProtocolDecoder {

    private boolean forceTimeZone = false;
    private final TimeZone timeZone = TimeZone.getTimeZone("UTC");

    public Gt06ProtocolDecoder(Gt06Protocol protocol) {
        super(protocol);

        if (Context.getConfig().hasKey(getProtocolName() + ".timezone")) {
            forceTimeZone = true;
            timeZone.setRawOffset(Context.getConfig().getInteger(getProtocolName() + ".timezone") * 1000);
        }
    }

    public static final int MSG_LOGIN = 0x01;
    public static final int MSG_GPS = 0x10;
    public static final int MSG_LBS = 0x11;
    public static final int MSG_GPS_LBS_1 = 0x12;
    public static final int MSG_GPS_LBS_2 = 0x22;
    public static final int MSG_STATUS = 0x13;
    public static final int MSG_SATELLITE = 0x14;
    public static final int MSG_COMMAND_RESPONSE78 = 0x15;
    public static final int MSG_GPS_LBS_STATUS_1 = 0x16;
    public static final int MSG_GPS_LBS_STATUS_2 = 0x26;
    public static final int MSG_GPS_LBS_STATUS_3 = 0x27;
    public static final int MSG_LBS_PHONE = 0x17;
    public static final int MSG_LBS_EXTEND = 0x18;
    public static final int MSG_LBS_STATUS = 0x19;
    public static final int MSG_GPS_PHONE = 0x1A;
    public static final int MSG_GPS_LBS_EXTEND = 0x1E;
    public static final int MSG_COMMAND_0 = 0x80;
    public static final int MSG_COMMAND_1 = 0x81;
    public static final int MSG_COMMAND_2 = 0x82;
    
    public static final int MSG_OBD_PACKET = 0x8C;
    public static final int MSG_COMMAND_RESPONSE79 = 0x21;

    private static boolean isSupported(int type) {
        return hasGps(type) || hasLbs(type) || hasStatus(type);
    }

    private static boolean hasGps(int type) {
        return type == MSG_GPS || type == MSG_GPS_LBS_1 || type == MSG_GPS_LBS_2
                || type == MSG_GPS_LBS_STATUS_1 || type == MSG_GPS_LBS_STATUS_2 || type == MSG_GPS_LBS_STATUS_3
                || type == MSG_GPS_PHONE || type == MSG_GPS_LBS_EXTEND;
    }

    private static boolean hasLbs(int type) {
        return type == MSG_LBS || type == MSG_LBS_STATUS || type == MSG_GPS_LBS_1 || type == MSG_GPS_LBS_2
                || type == MSG_GPS_LBS_STATUS_1 || type ==  MSG_GPS_LBS_STATUS_2 || type == MSG_GPS_LBS_STATUS_3;
    }

    private static boolean hasStatus(int type) {
        return type == MSG_STATUS || type == MSG_LBS_STATUS
                || type == MSG_GPS_LBS_STATUS_1 || type == MSG_GPS_LBS_STATUS_2 || type == MSG_GPS_LBS_STATUS_3;
    }

    private static void sendResponse(Channel channel, int type, int index) {
        if (channel != null) {
            ChannelBuffer response = ChannelBuffers.directBuffer(10);
            response.writeByte(0x78); response.writeByte(0x78); // header
            response.writeByte(0x05); // size
            response.writeByte(type);
            response.writeShort(index);
            response.writeShort(Checksum.crc16(Checksum.CRC16_X25, response.toByteBuffer(2, 4)));
            response.writeByte(0x0D); response.writeByte(0x0A); // ending
            channel.write(response);
        }
    }

    private void decodeGps(Position position, ChannelBuffer buf) {

        readDate(buf, position);

        int length = buf.readUnsignedByte();
        position.set(Event.KEY_SATELLITES, BitUtil.to(length, 4));
        length = BitUtil.from(length, 4);

        readLatLon(buf, position);
        readSpeed(position, buf);

        readCourseStatus(buf, position);

        buf.skipBytes(length - 12); // skip reserved
    }

    private void decodeLbs(Position position, ChannelBuffer buf, boolean hasLength) {

        int lbsLength = 0;
        if (hasLength) {
            lbsLength = buf.readUnsignedByte();
        }

        position.set(Event.KEY_MCC, buf.readUnsignedShort());
        position.set(Event.KEY_MNC, buf.readUnsignedByte());
        position.set(Event.KEY_LAC, buf.readUnsignedShort());
        position.set(Event.KEY_CID, buf.readUnsignedMedium());

        if (lbsLength > 0) {
            buf.skipBytes(lbsLength - 9);
        }
    }

    private void decodeStatus(Position position, ChannelBuffer buf) {

        position.set(Event.KEY_ALARM, true);

        int flags = buf.readUnsignedByte();

        position.set(Event.KEY_IGNITION, BitUtil.check(flags, 1));
        // decode other flags

        short power = buf.readUnsignedByte();
        position.set(Event.KEY_POWER, power);
        position.set(Event.KEY_BATTERY, Math.round(power*100F/6F));
        position.set(Event.KEY_GSM, buf.readUnsignedByte());
    }

    @Override
    protected Object decode(
            Channel channel, SocketAddress remoteAddress, Object msg) throws Exception {

        ChannelBuffer buf = (ChannelBuffer) msg;

        byte header = buf.readByte();
        if(header != buf.readByte())
            return null;
        if(header != 0x78 && header != 0x79)
            return null;
        
        if (header == 0x79) {
            int length = buf.readUnsignedShort();
            int type = buf.readUnsignedByte();
            if(!hasDeviceId())
                return null;
            
            switch(type) {
                case MSG_OBD_PACKET:
                    return decodeObd(buf, length - 8 - 16);
                case MSG_COMMAND_RESPONSE79:
                    buf.skipBytes(4);
                    byte encoding = buf.readByte();
                    Charset charset = encoding == 0x01 ? StandardCharsets.US_ASCII
                            : StandardCharsets.UTF_16BE;
                    return decodeCmdResponse(buf, length - 6 - 4, charset);
                default:
                    return null;
            }
        }

        int length = buf.readUnsignedByte(); // size
        int dataLength = length - 5;

        int type = buf.readUnsignedByte();

        if (type == MSG_LOGIN) {

            String imei = ChannelBuffers.hexDump(buf.readBytes(8)).substring(1);
            buf.readUnsignedShort(); // type

            // Timezone offset
            if (dataLength > 10) {
                int extensionBits = buf.readUnsignedShort();
                int hours = (extensionBits >> 4) / 100;
                int minutes = (extensionBits >> 4) % 100;
                int offset = (hours * 60 + minutes) * 60;
                if ((extensionBits & 0x8) != 0) {
                    offset = -offset;
                }
                if (!forceTimeZone) {
                    timeZone.setRawOffset(offset * 1000);
                }
            }

            if (identify(imei, channel, remoteAddress)) {
                buf.skipBytes(buf.readableBytes() - 6);
                sendResponse(channel, type, buf.readUnsignedShort());
            }

        } else if (hasDeviceId()) {
            
            if (type == MSG_COMMAND_RESPONSE78) {
                buf.skipBytes(5);
                byte[] encoding = buf.copy(dataLength+2, 2).array();
                if(encoding[0] == 0x00) {
                    Charset charset = encoding[1] == 0x01? StandardCharsets.UTF_16BE
                            : StandardCharsets.US_ASCII;
                    return decodeCmdResponse(buf, dataLength-7, charset);
                } else {
                    Log.warning(String.format("Unknown encoding, %02X%02X", encoding[0], encoding[1]));
                    return decodeCmdResponse(buf, dataLength-7, Charset.forName("ASCII"));
                }
            } else if(type == MSG_COMMAND_1) {
                buf.skipBytes(5);
                return decodeCmdResponse(buf, dataLength - 7, Charset.forName("ASCII"));
            } else if (isSupported(type)) {

                Position position = new Position();
                position.setDeviceId(getDeviceId());
                position.setProtocol(getProtocolName());

                if (hasGps(type)) {
                    decodeGps(position, buf);
                } else {
                    getLastLocation(position, null);
                }

                if (hasLbs(type)) {
                    decodeLbs(position, buf, hasStatus(type));
                }

                if (hasStatus(type)) {
                    decodeStatus(position, buf);
                }

                if (type == MSG_GPS_LBS_1 && buf.readableBytes() == 4 + 6) {
                    position.set(Event.KEY_ODOMETER, buf.readUnsignedInt());
                }

                if (buf.readableBytes() > 6) {
                    buf.skipBytes(buf.readableBytes() - 6);
                }
                int index = buf.readUnsignedShort();
                position.set(Event.KEY_INDEX, index);
                sendResponse(channel, type, index);

                return position;

            } else {

                buf.skipBytes(dataLength);
                if (type != MSG_COMMAND_0 && type != MSG_COMMAND_1 && type != MSG_COMMAND_2) {
                    sendResponse(channel, type, buf.readUnsignedShort());
                }

            }

        }

        return null;
    }

    private Position decodeObd(ChannelBuffer buf, int dataLength) throws Exception {
        Position position = new Position();
        
        readDate(buf, position);
        position.setTime(utcToOurs(position.getDeviceTime()));
        buf.skipBytes(1); //ACC
        readObd(dataLength, buf, position);
        buf.skipBytes(1); //quantity of positioning
        readLatLon(buf, position);
        readSpeed(position, buf);
        readCourseStatus(buf, position);
        
        position.setDeviceId(getDeviceId());
        return position;
    }
    

    private void readSpeed(Position position, ChannelBuffer buf) {
        position.setSpeed(UnitsConverter.knotsFromKph(buf.readUnsignedByte()));
    }

    private void readObd(int dataLength, ChannelBuffer buf, Position position) throws Exception {
        byte[] bytes = new byte[dataLength];
        buf.readBytes(bytes,0,bytes.length);
        String content = new String(bytes);
        ObdInfo info = new ObdInfo();
        
        String[] props = content.split(",");
        for(int i=0;i<props.length;++i) {
            String[] parts = props[i].split("=");
            if(parts.length != 2) {
                System.out.println(props[i]);
                continue;
            }
            int key = Integer.parseInt(parts[0].substring(0,parts[0].length()-1), 16);
            int val = Integer.parseInt(parts[1], 16);
            info.addProp(key, val);
        }
        position.setObdInfo(info);
    }
    
    private void readDate(ChannelBuffer buf, Position position) {
        DateBuilder dateBuilder = new DateBuilder(timeZone)
                .setDate(buf.readUnsignedByte(), buf.readUnsignedByte(), buf.readUnsignedByte())
                .setTime(buf.readUnsignedByte(), buf.readUnsignedByte(), buf.readUnsignedByte());
        position.setTime(dateBuilder.getDate());
    }
    
    private void readLatLon(ChannelBuffer buf, Position position) {
        double latitude = buf.readUnsignedInt() / 60.0 / 30000.0;
        double longitude = buf.readUnsignedInt() / 60.0 / 30000.0;
        position.setLatitude(latitude);
        position.setLongitude(longitude);
    }
    
    private void readCourseStatus(ChannelBuffer buf, Position position) {
        double latitude = position.getLatitude();
        double longitude = position.getLongitude();
        
        int flags = buf.readUnsignedShort();
        position.setCourse(BitUtil.to(flags, 10));
        position.setValid(BitUtil.check(flags, 12));

        if (!BitUtil.check(flags, 10)) {
            latitude = -latitude;
        }
        if (BitUtil.check(flags, 11)) {
            longitude = -longitude;
        }

        position.setLatitude(latitude);
        position.setLongitude(longitude);

        if (BitUtil.check(flags, 14)) {
            position.set(Event.KEY_IGNITION, BitUtil.check(flags, 15));
        }
    }

    private CommandResponse decodeCmdResponse(ChannelBuffer buf, int dataLength, Charset charset) {
        String response = buf.readBytes(dataLength).toString(charset);
        if(response.contains(";")) {
            KeyValueCommandResponse kvResp = new KeyValueCommandResponse(getActiveDevice());
            String[] pairs = response.split(";");
            for(int i=0; i<pairs.length;++i) {
                String[] parts = pairs[i].split(":");
                if(parts.length == 2)
                    kvResp.put(normalizeKey(parts[0]), parts[1]);
            }
            return kvResp;
        }
        return new MessageCommandResponse(Context.getConnectionManager().getActiveDevice(getDeviceId()),
                response);
    }
    
    private String normalizeKey(String dKey) {
        dKey = dKey.trim();
        if("ACC".equalsIgnoreCase(dKey)) {
            return KEY_ACC;
        } else if("DEFENSE".equalsIgnoreCase(dKey))
            return KEY_DEFENSE;
        else if("DEFENSE TIME".equalsIgnoreCase(dKey))
            return KEY_DEFENSE_TIME;
        else if("GPRS".equalsIgnoreCase(dKey))
            return KEY_GPRS;
        else if("GPS".equalsIgnoreCase(dKey))
            return KEY_GPS;
        else if("GSM SIGNAL LEVEL".equalsIgnoreCase(dKey))
            return KEY_GSM;
        else if("SENDS".equalsIgnoreCase(dKey))
            return KEY_SENDS;
        else if("SENSORSET".equalsIgnoreCase(dKey))
            return KEY_SENSORSET;
        else if("TIMER".equalsIgnoreCase(dKey))
            return KEY_POSITION_T;
        else if("DISTANCE".equalsIgnoreCase(dKey))
            return KEY_POSITION_D;
        else if("TIMEZONE".equalsIgnoreCase(dKey))
            return KEY_TIME_ZONE;
        else if("BATTERY".equalsIgnoreCase(dKey))
            return KEY_BATTERY;
        else if("IMEI".equalsIgnoreCase(dKey))
            return KEY_IMEI;
        return dKey;
    }

    private Date utcToOurs(Date deviceTime) {
        final int ONE_HOUR = 60*60*1000;
        return new Date(deviceTime.getTime() + ONE_HOUR);
    }
}
