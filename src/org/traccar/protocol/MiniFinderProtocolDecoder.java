/*
 * Copyright 2014 - 2015 Anton Tananaev (anton.tananaev@gmail.com)
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
import java.util.regex.Pattern;
import org.jboss.netty.channel.Channel;
import org.traccar.BaseProtocolDecoder;
import org.traccar.helper.BitUtil;
import org.traccar.helper.DateBuilder;
import org.traccar.helper.Parser;
import org.traccar.helper.PatternBuilder;
import org.traccar.helper.UnitsConverter;
import org.traccar.model.Event;
import org.traccar.model.KeyValueCommandResponse;
import static org.traccar.model.KeyValueCommandResponse.*;
import org.traccar.model.MessageCommandResponse;
import org.traccar.model.Position;

public class MiniFinderProtocolDecoder extends BaseProtocolDecoder {

    public MiniFinderProtocolDecoder(MiniFinderProtocol protocol) {
        super(protocol);
    }

    private static final Pattern PATTERN = new PatternBuilder()
            .expression("![A-D],")
            .number("(d+)/(d+)/(d+),")           // date
            .number("(d+):(d+):(d+),")           // time
            .number("(-?d+.d+),")                // latitude
            .number("(-?d+.d+),")                // longitude
            .number("(d+.?d*),")                 // speed
            .number("(d+.?d*),")                 // course
            .groupBegin()
            .number("(x+),")                     // flags
            .number("(-?d+.d+),")                // altitude
            .number("(d+),")                     // battery
            .number("(d+),")                     // satellites in use
            .number("(d+),")                     // satellites in view
            .text("0")
            .or()
            .any()
            .groupEnd()
            .compile();

    @Override
    protected Object decode(
            Channel channel, SocketAddress remoteAddress, Object msg) throws Exception {

        String sentence = (String) msg;

        if (sentence.startsWith("!1")) {

            identify(sentence.substring(3, sentence.length()), channel, remoteAddress);

        } else if(sentence.startsWith("!3")) {
            return new MessageCommandResponse(getActiveDevice(), sentence.substring(3));
        } else if(sentence.startsWith("!4")) {
            KeyValueCommandResponse kvResp = new KeyValueCommandResponse(getActiveDevice());
            String[] vals = sentence.substring(3).split(",");
            String[] keys = new String[] {"_tinterval", KEY_NUMBER_A,
                KEY_NUMBER_B, KEY_NUMBER_C, KEY_TIME_ZONE, KEY_OVERSPEED_THRESHOLD,
                KEY_MOVEMENT_ALARM, KEY_VIBRATION_ALARM, "_others"
            };
            for(int i=0;i<keys.length && i<vals.length;++i)
                kvResp.put(keys[i], vals[i]);
            return kvResp;
        }else if (sentence.matches("![A-D].*") && hasDeviceId()) {

            Parser parser = new Parser(PATTERN, sentence);
            if (!parser.matches()) {
                return null;
            }

            Position position = new Position();
            position.setProtocol(getProtocolName());
            position.setDeviceId(getDeviceId());

            DateBuilder dateBuilder = new DateBuilder()
                    .setDateReverse(parser.nextInt(), parser.nextInt(), parser.nextInt())
                    .setTime(parser.nextInt(), parser.nextInt(), parser.nextInt());
            position.setTime(dateBuilder.getDate());

            position.setLatitude(parser.nextDouble());
            position.setLongitude(parser.nextDouble());
            position.setSpeed(UnitsConverter.knotsFromKph(parser.nextDouble()));

            position.setCourse(parser.nextDouble());
            if (position.getCourse() > 360) {
                position.setCourse(0);
            }

            if (parser.hasNext(5)) {

                int flags = parser.nextInt(16);
                position.set(Event.KEY_FLAGS, flags);
                position.setValid(BitUtil.check(flags, 0));

                position.setAltitude(parser.nextDouble());

                position.set(Event.KEY_BATTERY, Integer.parseInt(parser.next()));
                position.set(Event.KEY_SATELLITES, parser.next());

            }

            return position;

        }

        return null;
    }

}
