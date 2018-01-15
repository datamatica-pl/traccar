/*
 * Copyright 2013 - 2015 Anton Tananaev (anton.tananaev@gmail.com)
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

import java.io.StringReader;
import java.net.SocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.traccar.BaseProtocolDecoder;
import org.traccar.Context;
import org.traccar.helper.BatteryVoltageToPercentageCalculator;
import org.traccar.helper.BitUtil;
import org.traccar.helper.UnitsConverter;
import org.traccar.model.CommandResponse;
import org.traccar.model.Event;
import org.traccar.model.KeyValueCommandResponse;
import static org.traccar.model.KeyValueCommandResponse.*;
import org.traccar.model.MessageCommandResponse;
import org.traccar.model.Position;

public class TeltonikaProtocolDecoder extends BaseProtocolDecoder {
    private final int IGNITION_IO_KEY = 239;
    private final int BATTERY_VOLTAGE_IO_KEY = 67;

    public TeltonikaProtocolDecoder(TeltonikaProtocol protocol) {
        super(protocol);
    }

    private void parseIdentification(Channel channel, SocketAddress remoteAddress, ChannelBuffer buf) {

        int length = buf.readUnsignedShort();
        String imei = buf.toString(buf.readerIndex(), length, Charset.defaultCharset());
        boolean result =  identify(imei, channel, remoteAddress);

        if (channel != null) {
            ChannelBuffer response = ChannelBuffers.directBuffer(1);
            if (result) {
                response.writeByte(1);
            } else {
                response.writeByte(0);
            }
            channel.write(response);
        }
    }

    public static final int CODEC_GH3000 = 0x07;
    public static final int CODEC_FM4X00 = 0x08;
    public static final int CODEC_12 = 0x0C;

    private List<Position> parseLocation(Channel channel, ChannelBuffer buf) {
        List<Position> positions = new LinkedList<>();

        buf.skipBytes(4); // marker
        buf.readUnsignedInt(); // data length
        int codec = buf.readUnsignedByte(); // codec

        int count = buf.readUnsignedByte();

        for (int i = 0; i < count; i++) {
            Position position = new Position();
            position.setProtocol(getProtocolName());

            position.setDeviceId(getDeviceId());

            int globalMask = 0x0f;

            if (codec == CODEC_GH3000) {

                long time = buf.readUnsignedInt() & 0x3fffffff;
                time += 1167609600; // 2007-01-01 00:00:00

                globalMask = buf.readUnsignedByte();
                if (BitUtil.check(globalMask, 0)) {

                    position.setTime(new Date(time * 1000));

                    int locationMask = buf.readUnsignedByte();

                    if (BitUtil.check(locationMask, 0)) {
                        position.setLatitude(buf.readFloat());
                        position.setLongitude(buf.readFloat());
                    }

                    if (BitUtil.check(locationMask, 1)) {
                        position.setAltitude(buf.readUnsignedShort());
                    }

                    if (BitUtil.check(locationMask, 2)) {
                        position.setCourse(buf.readUnsignedByte() * 360.0 / 256);
                    }

                    if (BitUtil.check(locationMask, 3)) {
                        position.setSpeed(UnitsConverter.knotsFromKph(buf.readUnsignedByte()));
                    }

                    if (BitUtil.check(locationMask, 4)) {
                        int satellites = buf.readUnsignedByte();
                        position.set(Event.KEY_SATELLITES, satellites);
                        position.setValid(satellites >= 3);
                    }

                    if (BitUtil.check(locationMask, 5)) {
                        position.set(Event.KEY_LAC, buf.readUnsignedShort());
                        position.set(Event.KEY_CID, buf.readUnsignedShort());
                    }

                    if (BitUtil.check(locationMask, 6)) {
                        position.set(Event.KEY_GSM, buf.readUnsignedByte());
                    }

                    if (BitUtil.check(locationMask, 7)) {
                        position.set("operator", buf.readUnsignedInt());
                    }

                } else {

                    getLastLocation(position, new Date(time * 1000));

                }

            } else {

                position.setTime(new Date(buf.readLong()));

                position.set("priority", buf.readUnsignedByte());

                position.setLongitude(buf.readInt() / 10000000.0);
                position.setLatitude(buf.readInt() / 10000000.0);
                position.setAltitude(buf.readShort());
                position.setCourse(buf.readUnsignedShort());

                int satellites = buf.readUnsignedByte();
                position.set(Event.KEY_SATELLITES, satellites);

                position.setValid(satellites != 0);

                position.setSpeed(UnitsConverter.knotsFromKph(buf.readUnsignedShort()));

                position.set(Event.KEY_EVENT, buf.readUnsignedByte());

                buf.readUnsignedByte(); // total IO data records

            }

            // Read all 1 byte IO elements
            if (BitUtil.check(globalMask, 1)) {
                int cnt = buf.readUnsignedByte();
                for (int j = 0; j < cnt; j++) {
                    int id = buf.readUnsignedByte();
                    int oneByteValue = buf.readUnsignedByte();
                    if (id == 1) {
                        position.set(Event.KEY_POWER, oneByteValue);
                    } else {
                        if (id == IGNITION_IO_KEY) {
                            if (oneByteValue == 0) {
                                position.set(Event.KEY_IGNITION, false);
                            } else if (oneByteValue == 1) {
                                position.set(Event.KEY_IGNITION, true);
                            }
                        }
                        position.set(Event.PREFIX_IO + id, oneByteValue);
                    }
                }
            }
            
            // Read all 2 byte IO elements
            if (BitUtil.check(globalMask, 2)) {
                int cnt = buf.readUnsignedByte();
                for (int j = 0; j < cnt; j++) {
                    int id = buf.readUnsignedByte();
                    int val = buf.readUnsignedShort();
                    position.set(Event.PREFIX_IO + id, val);
                    if (id == BATTERY_VOLTAGE_IO_KEY) {
                        int minBatteryVoltageInMV = 2700;
                        int maxBatteryVoltageInMV = 4100;
                        BatteryVoltageToPercentageCalculator batCalc = new BatteryVoltageToPercentageCalculator(
                                minBatteryVoltageInMV, maxBatteryVoltageInMV);
                        position.set(Event.KEY_BATTERY, batCalc.voltsToPercent(val));
                    }
                }
            }

            // Read all 4 byte IO elements
            if (BitUtil.check(globalMask, 3)) {
                int cnt = buf.readUnsignedByte();
                for (int j = 0; j < cnt; j++) {
                    position.set(Event.PREFIX_IO + buf.readUnsignedByte(), buf.readUnsignedInt());
                }
            }

            // Read all 8 byte IO elements
            if (codec == CODEC_FM4X00) {
                int cnt = buf.readUnsignedByte();
                for (int j = 0; j < cnt; j++) {
                    position.set(Event.PREFIX_IO + buf.readUnsignedByte(), buf.readLong());
                }
            }
            
            positions.add(position);
        }

        if (channel != null) {
            ChannelBuffer response = ChannelBuffers.directBuffer(4);
            response.writeInt(count);
            channel.write(response);
        }

        return positions;
    }

    @Override
    protected Object decode(
            Channel channel, SocketAddress remoteAddress, Object msg) throws Exception {

        ChannelBuffer buf = (ChannelBuffer) msg;

        if (buf.getUnsignedShort(0) > 0) {
            parseIdentification(channel, remoteAddress, buf);
        } else {
            if(buf.getUnsignedByte(8) == CODEC_12) {
                return parseCmdResponse(channel, buf);
            } else {
                return parseLocation(channel, buf);
            }   
        }
        
        return null;
    }

    private CommandResponse parseCmdResponse(Channel channel, ChannelBuffer buf) {
        buf.skipBytes(9); //preamble, data size, 0x0C
        buf.skipBytes(1); //quantity - assuming 1
        buf.skipBytes(1); //constant 0x06
        int size = (int)buf.readUnsignedInt();
        String response = buf.readBytes(size).toString(StandardCharsets.US_ASCII);
        if(response.startsWith("Data Link:")) {
            return parseStatus(response);
        } else if(response.startsWith("INI:")) {
            return parseParams(response);
        }
        return new MessageCommandResponse(Context.getConnectionManager().getActiveDevice(getDeviceId()),
                response);
    }

    private CommandResponse parseStatus(String response) {
        KeyValueCommandResponse kvResp = new KeyValueCommandResponse(
            Context.getConnectionManager().getActiveDevice(getDeviceId()));
        String[] r = response.split(":");
        String key = r[0];
        for(int i=1;i < r.length-1;++i) {
            r[i] = r[i].trim();
            int valEnd = r[i].indexOf(" ");
            kvResp.put(normalizeKey(key), r[i].substring(0, valEnd));
            key = r[i].substring(valEnd+1, r[i].length());
        }
        kvResp.put(normalizeKey(key), r[r.length-1]);
        return kvResp;
    }
    
    private String normalizeKey(String dKey) {
        dKey = dKey.trim();
        if("Data Link".equalsIgnoreCase(dKey)) {
            return KEY_DATA_LINK;
        } else if("Roaming".equalsIgnoreCase(dKey)) {
            return KEY_ROAMING;
        } else if("GPRS".equalsIgnoreCase(dKey)) {
            return KEY_GPRS;
        } else if("GPS".equalsIgnoreCase(dKey)) {
            return KEY_GPS;
        } else if("INI".equalsIgnoreCase(dKey)) {
            return KEY_INIT_TIME;
        } else if("RTC".equalsIgnoreCase(dKey)) {
            return KEY_RTC_TIME;
        }
        return dKey;
    }

    private CommandResponse parseParams(String response) {
        KeyValueCommandResponse kvResp = new KeyValueCommandResponse(
            Context.getConnectionManager().getActiveDevice(getDeviceId()));
        Scanner s = new Scanner(new StringReader(response));
        s.skip("INI:");
        s.useDelimiter("RTC:");
        kvResp.put(normalizeKey("INI"), s.next().trim());
        s.skip("RTC:");
        s.useDelimiter("RST:");
        kvResp.put(normalizeKey("RTC"), s.next().trim());
        return kvResp;
    }
}
