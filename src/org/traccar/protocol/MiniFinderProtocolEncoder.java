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

import java.text.DecimalFormat;
import java.text.NumberFormat;
import org.traccar.StringProtocolEncoder;
import org.traccar.helper.Log;
import org.traccar.model.Command;
import org.traccar.model.Device;

public class MiniFinderProtocolEncoder extends StringProtocolEncoder {

    private final String DEFAULT_PASSWORD = "123456";

    @Override
    protected Object encodeCommand(Command command) {
        Device device = getDevice(command.getDeviceId());
        String commandPassword = device.getCommandPassword();
        if ("".equals(commandPassword)) {
            commandPassword = this.DEFAULT_PASSWORD;
        }

        switch (command.getType()) {
            case Command.TYPE_POSITION_SINGLE:
                return commandPassword + "LOC";
            case Command.TYPE_POSITION_PERIODIC:
                int frequency = Integer.parseInt(command.getAttributes().get(Command.KEY_FREQUENCY).toString());
                int tenSecondsIntervals = frequency / 10;
                if (tenSecondsIntervals < 1 || tenSecondsIntervals > 999) {
                    tenSecondsIntervals = 6;
                }
                return String.format("%sM,%03d", commandPassword, tenSecondsIntervals);
            case Command.TYPE_SET_TIMEZONE:
                Long offset = (Long)command.getAttributes().get(Command.KEY_TIMEZONE);
                Integer oneHourSeconds = 3600;
                Long offsetHours = offset / oneHourSeconds;
                String sign = (offsetHours >= 0) ? "+" : "";
                NumberFormat formatter = new DecimalFormat("00");
                String timeZone = sign + formatter.format(offsetHours);
                return String.format("%sL%s", commandPassword, timeZone);
            case Command.TYPE_GET_STATUS:
                return commandPassword + "G";
            case Command.TYPE_REBOOT_DEVICE:
                return commandPassword + "T";
            case Command.TYPE_SET_SOS_NUMBER:
                if (command.getAttributes().get(Command.KEY_SOS_NUMBER_1) != null) {
                    String SOSNumber = command.getAttributes().get(Command.KEY_SOS_NUMBER_1).toString();
                    return String.format("%sA1,%s", commandPassword, SOSNumber);
                } else {
                    return String.format("%sA0", commandPassword);
                }
            case Command.TYPE_DELETE_SOS_NUMBER:
                return String.format("%sA0", commandPassword);
            case Command.TYPE_SET_SECOND_NUMBER:
                if (command.getAttributes().get(Command.KEY_SOS_NUMBER_2) != null) {
                    String SOSNumber = command.getAttributes().get(Command.KEY_SOS_NUMBER_2).toString();
                    return String.format("%sB1,%s", commandPassword, SOSNumber);
                } else {
                    return String.format("%sB0", commandPassword);
                }
            case Command.TYPE_DELETE_SECOND_NUMBER:
                return String.format("%sB0", commandPassword);
            case Command.TYPE_SET_THIRD_NUMBER:
                if (command.getAttributes().get(Command.KEY_SOS_NUMBER_3) != null) {
                    String SOSNumber = command.getAttributes().get(Command.KEY_SOS_NUMBER_3).toString();
                    return String.format("%sC1,%s", commandPassword, SOSNumber);
                } else {
                    return String.format("%sC0", commandPassword);
                }
            case Command.TYPE_DELETE_THIRD_NUMBER:
                return String.format("%sC0", commandPassword);
            case Command.TYPE_LISTEN_MODE:
                return String.format("%sP1", commandPassword);
            case Command.TYPE_VOICE_CALL_MODE:
                return String.format("%sP0", commandPassword);
            case Command.TYPE_SLEEP_MODE:
                return String.format("%sSP1", commandPassword);
            case Command.TYPE_EXIT_SLEEP_MODE:
                return String.format("%sSP0", commandPassword);
            case Command.TYPE_SET_AGPS_ON:
                return String.format("%sAGPS1", commandPassword);
            case Command.TYPE_SET_AGPS_OFF:
                return String.format("%sAGPS0", commandPassword);
            case Command.TYPE_CUSTOM:
                return command.getAttributes().get("raw");
            case Command.TYPE_EXTENDED_CUSTOM:
                return command.getAttributes().get("message");
            default:
                Log.warning(new UnsupportedOperationException(command.getType()));
                break;
        }

        return null;
    }

}
