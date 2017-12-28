/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.traccar.protocol;

import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.joda.time.DateTime;
import org.traccar.BaseProtocolDecoder;
import org.traccar.helper.Log;
import org.traccar.model.MessageCommandResponse;
import org.traccar.model.Position;

/**
 *
 * @author piotrkrzeszewski
 */
public class GrooProtocolDecoder extends BaseProtocolDecoder {

    public GrooProtocolDecoder(GrooProtocol protocol) {
        super(protocol);
    }

    @Override
    protected Object decode(Channel channel, SocketAddress remoteAddress, Object msg) throws Exception {
        Log.debug("Groo-test");

        ChannelBuffer buf = (ChannelBuffer) msg;
        String content = buf.toString(StandardCharsets.US_ASCII);
        
        // @G#@,V01,1,108101709000421,9260060084432078,@R#@
        String[] contentParts = content.split(",");
        Log.debug(Arrays.toString(contentParts));
        
        // contentParts[0] == "@G#@" - constatnt start of message
        // contentParts[1] == "V01" - protocol name
        
        String imei = contentParts[3];
        Log.debug("IMEI: " + imei);        
        if (!identify(imei, channel, remoteAddress)) {
            return null;
        }
        
        int messageType = Integer.parseInt(contentParts[2]);
        switch (messageType) {
            case 1: {
                // time-sync message
                // device requires response from server with current time
                handleTimeSyncMessage(contentParts, channel);
                break;
            }
            case 6: {
                // gps location update message
                return handleLocationUpdateMessage(contentParts, channel, remoteAddress);
            }
            case 9: {
                // gps location update message
                handleIsOnHandMessage(contentParts, channel);
                break;
            }
            case 13: {
                // gps location update message
                handlePedometerUpdateMessage(contentParts, channel);
                break;
            }
            case 14: {
                // gps location update message
                handleHeartRateUpdateMessage(contentParts, channel);
                break;
            }
            case 19:
            case 25: {
                // NOT DOCUMENTED - it looks like it's response for command
                // Device sends it when receives command with cmd = 25
                // Let's try to read this in this way
                return new MessageCommandResponse(getActiveDevice(), Arrays.toString(contentParts));
            }
            case 44: {
                // heartbeat message
                // device require response or it will disconnect from server (after 3 tries)
                handleHeartbeatMessage(contentParts, channel);
                break;
            }
        }
        
        return null;
    }
    
    private void sendResponseToDevice(String response, Channel channel) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(response.getBytes(Charset.forName("US-ASCII")));
        channel.write(ChannelBuffers.wrappedBuffer(byteBuffer));
    }
    
    private void handleTimeSyncMessage(String[] contentParts, Channel channel) {
        
        String response = String.format("@G#@,V01,38,%s,@R#@", buildGrooTimestamp());
        Log.debug(response);
        sendResponseToDevice(response, channel);
    }
    
    private Position handleLocationUpdateMessage(String[] contentParts, Channel channel, SocketAddress remoteAddress) {
        DateTime time = parseGrooTimestamp(contentParts[5]);
        String[] coordinates = contentParts[6].split(";");
        
        Position position = new Position();
        position.setProtocol(getProtocolName());
        
        Log.debug("TIME: " + time.toString());
        
        Log.debug("DeviceID: " + getDeviceId());
        position.setDeviceId(getDeviceId());
        
        position.setTime(time.toDate());
        position.setLatitude(Double.parseDouble(coordinates[0]));
        position.setLongitude(Double.parseDouble(coordinates[1]));
        
        Log.debug("COORD: " + Arrays.toString(coordinates));
        
        return position;
    }
    
    private void handleIsOnHandMessage(String[] contentParts, Channel channel) {
        DateTime time = parseGrooTimestamp(contentParts[5]);
        
        Log.debug("IS ON HAND: " + ("1".equals(contentParts[6]) ? "FALSE" : "TRUE"));
    }
    
    private void handlePedometerUpdateMessage(String[] contentParts, Channel channel) {
        DateTime time = parseGrooTimestamp(contentParts[5]);
        
        Log.debug("PEDOMETER: " + contentParts[6] + " steps today at " + time.toString());
    }
    
    private void handleHeartRateUpdateMessage(String[] contentParts, Channel channel) {
        DateTime time = parseGrooTimestamp(contentParts[5]);
        
        Log.debug("HEART RATE: " + contentParts[6] + " at " + time.toString() + ". Battery: " + contentParts[7]);
    }
    
    
    private void handleHeartbeatMessage(String[] contentParts, Channel channel) {
        String response = "@G#@,V01,21,@R#@"; //const response
        sendResponseToDevice(response, channel);
    }
    
    private String buildGrooTimestamp() {
        // Groo uses dates with following format:
        // Year, month, day, minute, second - 20150313180820
        
        DateTime now = DateTime.now();
        StringBuilder sb = new StringBuilder();
        sb.append(now.getYear());
        sb.append(String.format("%02d", now.getMonthOfYear()));
        sb.append(String.format("%02d", now.getDayOfMonth()));
        sb.append(String.format("%02d", now.getHourOfDay()));
        sb.append(String.format("%02d", now.getMinuteOfHour()));
        sb.append(String.format("%02d", now.getSecondOfMinute()));
        
        return sb.toString();
    }
    
    private DateTime parseGrooTimestamp(String timestamp) {
        int year = Integer.parseInt(timestamp.substring(0, 4));
        int month = Integer.parseInt(timestamp.substring(4, 6));
        int day = Integer.parseInt(timestamp.substring(6, 8));
        
        int hour = Integer.parseInt(timestamp.substring(8, 10));
        int minute = Integer.parseInt(timestamp.substring(10, 12));
        int second = Integer.parseInt(timestamp.substring(12, 14));
        
        return new DateTime(year, month, day, hour, minute, second);
    }
    
}
