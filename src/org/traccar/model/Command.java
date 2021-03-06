/*
 * Copyright 2015 - 2016 Anton Tananaev (anton.tananaev@gmail.com)
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
package org.traccar.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Command extends Extensible {

    public static final String TYPE_CUSTOM = "custom";
    public static final String TYPE_POSITION_SINGLE = "positionSingle";
    public static final String TYPE_POSITION_PERIODIC = "positionPeriodic";
    public static final String TYPE_POSITION_STOP = "positionStop";
    public static final String TYPE_ENGINE_STOP = "engineStop";
    public static final String TYPE_ENGINE_RESUME = "engineResume";
    public static final String TYPE_ALARM_ARM = "alarmArm";
    public static final String TYPE_ALARM_DISARM = "alarmDisarm";
    public static final String TYPE_AUTO_ALARM_ARM = "autoAlarmArm";
    public static final String TYPE_AUTO_ALARM_DISARM = "autoAlarmDisarm";
    public static final String TYPE_SET_TIMEZONE = "setTimezone";
    public static final String TYPE_REQUEST_PHOTO = "requestPhoto";
    public static final String TYPE_REBOOT_DEVICE = "rebootDevice";
    public static final String TYPE_MOVEMENT_ALARM = "movementAlarm";
    public static final String TYPE_MOVEMENT_ALARM_OFF = "movementAlarmOff";
    public static final String TYPE_SEND_SMS = "sendSms";
    public static final String TYPE_SET_DEFENSE_TIME = "setDefenseTime";
    public static final String TYPE_GET_PARAMS = "getParams";
    public static final String TYPE_GET_STATUS = "getStatus";
    public static final String TYPE_SET_SOS_NUMBER = "setSOSNumber";
    public static final String TYPE_SET_SOS_NUMBERS = "setSOSNumbers";
    public static final String TYPE_DELETE_SOS_NUMBER = "deleteSOSNumber";
    public static final String TYPE_SET_CENTER_NUMBER = "setCenterNumber";
    public static final String TYPE_SET_SECOND_NUMBER = "setSecondNumber";
    public static final String TYPE_DELETE_SECOND_NUMBER = "deleteSecondNumber";
    public static final String TYPE_SET_THIRD_NUMBER = "setThirdNumber";
    public static final String TYPE_DELETE_THIRD_NUMBER = "deleteThirdNumber";
    public static final String TYPE_FACTORY_SETTINGS = "factorySettings";
    public static final String TYPE_LISTEN_MODE = "listenMode";
    public static final String TYPE_VOICE_CALL_MODE = "voiceCallMode";
    public static final String TYPE_EXTENDED_CUSTOM = "extendedCustom";
    public static final String TYPE_SLEEP_MODE = "sleepMode";
    public static final String TYPE_EXIT_SLEEP_MODE = "exitSleepMode";
    public static final String TYPE_SET_AGPS_ON = "setAgpsOn";
    public static final String TYPE_SET_AGPS_OFF = "setAgpsOff";
    public static final String TYPE_DEL_AUTHORIZED_NUMS_FIRST_100_FMB = "delAuthorizedNumsFirst100FMB";
    public static final String TYPE_DEL_AUTHORIZED_NUMS_SECOND_100_FMB = "delAuthorizedNumsSecond100FMB";
    
    // Alternative command formats
    public static final String TYPE_POSITION_PERIODIC_ALT = "positionPeriodicAlt";

    public static final String KEY_UNIQUE_ID = "uniqueId";
    public static final String KEY_FREQUENCY = "frequency";
    public static final String KEY_FREQUENCY_STOP = "frequencyStop";
    public static final String KEY_TIMEZONE = "timezone";
    public static final String KEY_DEVICE_PASSWORD = "devicePassword";
    public static final String KEY_RADIUS = "radius";
    public static final String KEY_PHONE_NUMBER = "phoneNumber";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_DEFENSE_TIME = "defenseTime";
    public static final String KEY_SOS_NUMBER_1 = "SOSNumber1";
    public static final String KEY_SOS_NUMBER_2 = "SOSNumber2";
    public static final String KEY_SOS_NUMBER_3 = "SOSNumber3";
    public static final String KEY_SOS_NUMBER = "SOSNumber";
    public static final String KEY_CENTER_NUMBER = "centerNumber";
    public static final String KEY_USER_ID = "userId";
}
