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
package org.traccar.model;

import java.time.Clock;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Position extends Event {
    public static long MAX_LATITUDE = 90;
    public static long MIN_LATITUDE = -90;
    public static long MAX_LONGITUDE = 180;
    public static long MIN_LONGITUDE = -180;

    public static int VALID_STATUS_CORRECT_POSITION = 0;
    public static int VALID_STATUS_ALARM = 1;
    public static int VALID_STATUS_TIME_OUT_OF_RANGE = 2;
    public static int VALID_STATUS_ALARM_AND_TIME_OUT_OF_RANGE = 3;
    public static long FUTURE_TIME_ACCEPTANCE_LIMIT = TimeUnit.HOURS.toMillis(14); // Max '+' timezone is 14 hours
    public static long PAST_TIME_ACCEPTANCE_LIMIT = TimeUnit.DAYS.toMillis(30);

    private Date fixTime;

    public Date getFixTime() {
        if (fixTime != null) {
            return new Date(fixTime.getTime());
        } else {
            return null;
        }
    }

    public void setFixTime(Date fixTime) {
        if (fixTime != null) {
            this.fixTime = new Date(fixTime.getTime());
        } else {
            this.fixTime = null;
        }
    }

    public void setTime(Date time) {
        setDeviceTime(time);
        setFixTime(time);
    }

    private boolean outdated;

    public boolean getOutdated() {
        return outdated;
    }

    public void setOutdated(boolean outdated) {
        this.outdated = outdated;
    }

    private boolean valid;

    public boolean getValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    private double latitude;

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    private double longitude;

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    private double altitude;

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    private double speed; // value in knots

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    private double course;

    public double getCourse() {
        return course;
    }

    public void setCourse(double course) {
        this.course = course;
    }

    private String address;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    private ObdInfo obdInfo;
    private Long obdId;

    public ObdInfo getObdInfo(){
        return obdInfo;
    }

    public void setObdInfo(ObdInfo obdInfo) {
        this.obdInfo = obdInfo;
    }

    public Long getObdId() {
        return obdId;
    }

    public void setObdId(Long obdId) {
        this.obdId = obdId;
    }

    public boolean hasObd() {
        return obdInfo != null;
    }
    
    public Integer getBatteryLevel() {
        Number lvlObj = (Number)getAttributes().get(KEY_BATTERY);
        if(lvlObj == null)
            return null;
        return lvlObj.intValue();
    }
    
    public Boolean getIgnition() {
        return (Boolean)getAttributes().get(KEY_IGNITION);
    }

    public boolean isAlarm() {
        Boolean isAlarm = (Boolean)this.getAttributes().get(Event.KEY_ALARM);
        if (isAlarm == null) {
            return false;
        } else {
            return isAlarm;
        }
    }
    
    public boolean isTimeInAllowedRange(Clock clock) {
        long currentTime = clock.millis();
        long maxAllowedTime = currentTime + FUTURE_TIME_ACCEPTANCE_LIMIT;
        long minAllowedTime = currentTime - PAST_TIME_ACCEPTANCE_LIMIT;
        long positionTime = this.getFixTime().getTime();
        
        return positionTime >= minAllowedTime && positionTime <= maxAllowedTime;
    }

    private Integer validStatus;

    public Integer getValidStatus() {
        return validStatus;
    }

    public void setValidStatus(Integer validStatus) {
        this.validStatus = validStatus;
    }

    public void checkAndSetValidStatus(Clock clock) {
        if (isAlarm()) {
            if (isTimeInAllowedRange(clock)) {
                validStatus = VALID_STATUS_ALARM;
            } else {
                validStatus = VALID_STATUS_ALARM_AND_TIME_OUT_OF_RANGE;
            }
        } else if (!isTimeInAllowedRange(clock)) {
            validStatus = VALID_STATUS_TIME_OUT_OF_RANGE;
        } else {
            validStatus = VALID_STATUS_CORRECT_POSITION;
        }
    }

    public boolean hasProperValidStatus() {
        return validStatus == null || validStatus == VALID_STATUS_CORRECT_POSITION;
    }

    @Override
    public String toString() {
        return "Position{" + "fixTime=" + fixTime + ", latitude=" + latitude +
                ", longitude=" + longitude + ", altitude=" + altitude +
                ", speed=" + speed + ", course=" + course + '}';
    }

}
