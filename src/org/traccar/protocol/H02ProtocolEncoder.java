/*
 * Copyright 2016 Gabor Somogyi (gabor.g.somogyi@gmail.com)
 *           2016 Anton Tananaev (anton.tananaev@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.traccar.protocol;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.traccar.StringProtocolEncoder;
import org.traccar.helper.Log;
import org.traccar.model.Command;
import org.traccar.model.Device;

public class H02ProtocolEncoder extends StringProtocolEncoder {

    private static final String MARKER = "HQ";
    private final String DEFAULT_PASSWORD = "123456";

    private Object formatCommand(DateTime time, String uniqueId, String type, String... params) {

        StringBuilder result = new StringBuilder(String.format("*%s,%s,%s,%02d%02d%02d",
                MARKER, uniqueId, type, time.getHourOfDay(), time.getMinuteOfHour(), time.getSecondOfMinute()));

        for (String param : params) {
            result.append(",").append(param);
        }

        result.append("#");

        return result.toString();
    }

    protected Object encodeCommand(Command command, DateTime time) {
        String uniqueId = getUniqueId(command.getDeviceId());
        String commandToSend = "";
        Device device = getDevice(command.getDeviceId());
        String commandPassword = device.getCommandPassword();
        if ("".equals(commandPassword)) {
            commandPassword = this.DEFAULT_PASSWORD;
        }

        switch (command.getType()) {
            case Command.TYPE_ALARM_ARM:
                return formatCommand(time, uniqueId, "SCF", "0", "0");
            case Command.TYPE_ALARM_DISARM:
                return formatCommand(time, uniqueId, "SCF", "1", "1");
            case Command.TYPE_ENGINE_STOP:
                return formatCommand(
                        time, uniqueId, "S20", "1", "3", "10", "3", "5", "5", "3", "5", "3", "5", "3", "5");
            case Command.TYPE_ENGINE_RESUME:
                return formatCommand(time, uniqueId, "S20", "0", "0");
            case Command.TYPE_POSITION_PERIODIC:
                String frequency = command.getAttributes().get(Command.KEY_FREQUENCY).toString();
                return "run," + frequency;
            case Command.TYPE_SET_CENTER_NUMBER:
                String centerNumber = "";
                commandToSend = "adm" + commandPassword;
                if (command.getAttributes().get(Command.KEY_CENTER_NUMBER) != null) {
                    centerNumber = command.getAttributes().get(Command.KEY_CENTER_NUMBER).toString();
                }
                if ("".equals(centerNumber)) {
                    return commandToSend;
                } else {
                    return commandToSend + "," + centerNumber;
                }
            case Command.TYPE_FACTORY_SETTINGS:
                return "format" + commandPassword;
            case Command.TYPE_REBOOT_DEVICE:
                return "reset" + commandPassword;
            case Command.TYPE_SET_SOS_NUMBERS:
                String SOSNumber1 = "";
                String SOSNumber2 = "";
                commandToSend = "sos";
                if (command.getAttributes().get(Command.KEY_SOS_NUMBER_1) != null) {
                    SOSNumber1 = command.getAttributes().get(Command.KEY_SOS_NUMBER_1).toString();
                }
                if (command.getAttributes().get(Command.KEY_SOS_NUMBER_2) != null) {
                    SOSNumber2 = command.getAttributes().get(Command.KEY_SOS_NUMBER_2).toString();
                }
                commandToSend += "," + SOSNumber1 + "," + SOSNumber2;
                return commandToSend;
            case Command.TYPE_GET_PARAMS:
                return "param2";
            case Command.TYPE_SET_TIMEZONE:
                Long offset = (Long)command.getAttributes().get(Command.KEY_TIMEZONE);
                Integer oneHourSeconds = 3600;
                Long offsetHours = offset / oneHourSeconds;
                return "timezone" + commandPassword + " " + offsetHours.toString();
            case Command.TYPE_CUSTOM:
                return command.toString();
            default:
                Log.warning(new UnsupportedOperationException(command.getType()));
                break;
        }

        return null;
    }

    @Override
    protected Object encodeCommand(Command command) {
        return encodeCommand(command, new DateTime(DateTimeZone.UTC));
    }

}
