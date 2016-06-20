/*
 * Copyright 2016 Anton Tananaev (anton.tananaev@gmail.com)
 *
 * CastelProtocolEncoder author: Jan Usarek
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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.traccar.BaseProtocolEncoder;
import org.traccar.helper.Checksum;
import org.traccar.helper.Log;
import org.traccar.model.Command;

public class CastelProtocolEncoder extends BaseProtocolEncoder {
    
    public static final byte[] SEQUENCE_NUMBER = { 0x00, 0x00 }; // we don't use seq numbers, so let's keep them at 0
    public static final short SERIAL_LENGTH = 20;
    public static final short SOS_NUM_LENGTH = 21;
    public static final short SOS_NUM_REQUEST_LENGTH = 66;
    public static final byte PROTOCOL_VERSION = 0x05;
    public static final byte[] PROTOCOL_HEAD = { 0x40, 0x40 };
    public static final byte[] PROTOCOL_TAIL = { 0x0D, 0x0A };

    private static class CastelProtocolCommandType {
        public static final byte[] ENGINE_ON_OFF = { 0x45, (byte)0x83 };
        public static final byte[] GET_PARAMS = { 0x52, 0x01 };
        public static final byte[] SET_PARAMS = { 0x51, 0x01 };
    }
    
    private static class CastelProtocolParamType {
        public static final byte[] SMS_CENTER_NUMBER = { 0x08, 0x50 };
        public static final byte[] AUTHORIZED_NUMBER = { 0x01, 0x10 };
        public static final byte[] INTERVAL = { 0x01, 0x40 };
        public static final byte[] WORK_MODE = { 0x01, 0x70 };
    }

    private short getPacketLength(int protocolDataLength) {
        byte protocolVersionLength = 1;
        byte protocolLength = 2;
        byte CRCLength = 2;
        byte commandTypeLength = 2;
        int commandLength = PROTOCOL_HEAD.length + protocolLength + protocolVersionLength +
                SERIAL_LENGTH + commandTypeLength + protocolDataLength + CRCLength + PROTOCOL_TAIL.length;

        return (short)commandLength;
    }
    
    private byte[] fillWithZeros(String str, short length) {
        byte[] strBytes = str.getBytes();
        byte[] arrayFilled = new byte[length];
        
        for (int i = 0; i < length; i++) {
            if (i < strBytes.length) {
                arrayFilled[i] = strBytes[i];
            } else {
                arrayFilled[i] = 0x00;
            }
        }
        
        return arrayFilled;
    }
    
    private byte[] getShortAsByteArray(Short numToConvert) {
        ByteBuffer numBuffer = ByteBuffer.allocate(2);
        numBuffer.order(ByteOrder.LITTLE_ENDIAN);
        numBuffer.putShort(numToConvert);
        return numBuffer.array();
    }

    private ChannelBuffer encodeContent(String deviceUniqueId, byte[] commandType, byte[] commandContent) {
        short packetLength = getPacketLength(commandContent.length);
        ChannelBuffer buf = ChannelBuffers.buffer(ByteOrder.LITTLE_ENDIAN, packetLength);

        buf.writeBytes(PROTOCOL_HEAD);
        buf.writeShort(packetLength);
        buf.writeByte(PROTOCOL_VERSION);
        buf.writeBytes(fillWithZeros(deviceUniqueId, SERIAL_LENGTH));
        buf.writeBytes(commandType);
        buf.writeBytes(commandContent);
        Integer contentChecksum = Checksum.crc16(Checksum.CRC16_X25, buf.toByteBuffer());
        buf.writeShort(contentChecksum);
        buf.writeBytes(PROTOCOL_TAIL);

        return buf;
    }
    
    private ChannelBuffer encodeContent(String deviceUniqueId, byte[] commandType, byte[][] commandContent) {
        short commandContentLength = 0;
        for (byte[] command : commandContent) {
            commandContentLength += command.length;
        }
        short packetLength = getPacketLength(commandContentLength);
        ChannelBuffer buf = ChannelBuffers.buffer(ByteOrder.LITTLE_ENDIAN, packetLength);

        buf.writeBytes(PROTOCOL_HEAD);
        buf.writeShort(packetLength);
        buf.writeByte(PROTOCOL_VERSION);
        buf.writeBytes(fillWithZeros(deviceUniqueId, SERIAL_LENGTH));
        buf.writeBytes(commandType);
        for (byte[] command : commandContent) {
            buf.writeBytes(command);
        }
        Integer contentChecksum = Checksum.crc16(Checksum.CRC16_X25, buf.toByteBuffer());
        buf.writeShort(contentChecksum);
        buf.writeBytes(PROTOCOL_TAIL); 

        return buf;
    }

    @Override
    protected Object encodeCommand(Command command) {
        String deviceUniqueId = getUniqueId(command.getDeviceId());
        byte[][] commandContent;
        switch (command.getType()) {
            case Command.TYPE_ENGINE_STOP:
                return encodeContent(deviceUniqueId, CastelProtocolEncoder.CastelProtocolCommandType.ENGINE_ON_OFF,
                                new byte[]{0x01});
            case Command.TYPE_POSITION_PERIODIC:
                // Get frequency param and prepare buffer to change it into an array of bytes
                Short frequency = ((Number) command.getAttributes().get(Command.KEY_FREQUENCY)).shortValue();
                ByteBuffer frequencyBuffer = ByteBuffer.allocate(2);
                frequencyBuffer.order(ByteOrder.LITTLE_ENDIAN);
                frequencyBuffer.putShort(frequency);
                
                commandContent = new byte[5][];
                commandContent[0] = SEQUENCE_NUMBER;
                commandContent[1] = new byte[] { 0x01 }; // number of set TLV, 1
                commandContent[2] = CastelProtocolEncoder.CastelProtocolParamType.INTERVAL;
                commandContent[3] = new byte[] { 0x02, 0x00 }; // INTERVAL param length
                commandContent[4] = frequencyBuffer.array();
                
                return encodeContent(deviceUniqueId, CastelProtocolEncoder.CastelProtocolCommandType.SET_PARAMS,
                                commandContent);
            case Command.TYPE_GET_PARAMS:
                commandContent = new byte[5][];
                commandContent[0] = SEQUENCE_NUMBER;
                // 3 - number of parameters to return (AUTHORIZED_NUMBER, SMS_CENTER_NUMBER, INTERVAL)
                commandContent[1] = new byte[] { 0x03 };
                commandContent[2] = CastelProtocolEncoder.CastelProtocolParamType.AUTHORIZED_NUMBER;
                commandContent[3] = CastelProtocolEncoder.CastelProtocolParamType.SMS_CENTER_NUMBER;
                commandContent[4] = CastelProtocolEncoder.CastelProtocolParamType.INTERVAL;
                
                return encodeContent(deviceUniqueId, CastelProtocolEncoder.CastelProtocolCommandType.GET_PARAMS,
                                commandContent);
            case Command.TYPE_SET_CENTER_NUMBER:
                String centerNumber = "";
                if (command.getAttributes().get(Command.KEY_CENTER_NUMBER) != null) {
                    centerNumber = command.getAttributes().get(Command.KEY_CENTER_NUMBER).toString();
                }
                
                byte[] centerNumberArr;
                if ("".equals(centerNumber)) {
                    centerNumberArr = new byte[] { 0x00 };
                } else {
                    centerNumberArr = centerNumber.getBytes();
                }
                
                commandContent = new byte[5][];
                commandContent[0] = SEQUENCE_NUMBER;
                commandContent[1] = new byte[] { 0x01 }; // number of set TLV, 1
                commandContent[2] = CastelProtocolEncoder.CastelProtocolParamType.SMS_CENTER_NUMBER;
                if ("".equals(centerNumber)) {
                    // To delete SMS center number two zeros are set after param type, first here,
                    // next when centerNumberArr with value 0x00 is added to commandContent
                    commandContent[3] = new byte[] { 0x00 };
                } else {
                    commandContent[3] = getShortAsByteArray((short)centerNumberArr.length);
                }
                commandContent[4] = centerNumberArr;
                
                return encodeContent(deviceUniqueId, CastelProtocolEncoder.CastelProtocolCommandType.SET_PARAMS,
                                commandContent);
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
                commandContent = new byte[10][];
                commandContent[0] = SEQUENCE_NUMBER;
                commandContent[1] = new byte[] { 0x01 }; // number of set TLV, 1
                commandContent[2] = CastelProtocolEncoder.CastelProtocolParamType.AUTHORIZED_NUMBER;
                commandContent[3] = getShortAsByteArray(SOS_NUM_REQUEST_LENGTH);
                commandContent[4] = new byte[] { 0x00 }; // order number of first SOS number
                commandContent[5] = fillWithZeros(SOSNumber1, SOS_NUM_LENGTH);
                commandContent[6] = new byte[] { 0x01 }; // order number of second SOS number
                commandContent[7] = fillWithZeros(SOSNumber2, SOS_NUM_LENGTH);
                commandContent[8] = new byte[] { 0x02 }; // order number of third SOS number
                commandContent[9] = fillWithZeros(SOSNumber3, SOS_NUM_LENGTH);
                
                return encodeContent(deviceUniqueId, CastelProtocolEncoder.CastelProtocolCommandType.SET_PARAMS,
                                commandContent);
            default:
                Log.warning(new UnsupportedOperationException(command.getType()));
                break;
        }

        return null;
    }

}
