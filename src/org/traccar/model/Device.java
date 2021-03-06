/*
 * Copyright 2012 - 2016 Anton Tananaev (anton.tananaev@gmail.com)
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

import java.util.Date;

public class Device {

    private long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String uniqueId;

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public static final String STATUS_UNKNOWN = "unknown";
    public static final String STATUS_ONLINE = "online";
    public static final String STATUS_OFFLINE = "offline";

    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    private Date lastUpdate;

    public Date getLastUpdate() {
        if (lastUpdate != null) {
            return new Date(lastUpdate.getTime());
        } else {
            return null;
        }
    }

    public void setLastUpdate(Date lastUpdate) {
        if (lastUpdate != null) {
            this.lastUpdate = new Date(lastUpdate.getTime());
        } else {
            this.lastUpdate = null;
        }
    }

    private long positionId;

    public long getPositionId() {
        return positionId;
    }

    public void setPositionId(long positionId) {
        this.positionId = positionId;
    }

    private long groupId;

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }
    
    private Integer timezoneOffset;

    public int getTimezoneOffset() {
        return timezoneOffset == null ? 0 : timezoneOffset;
    }

    public void setTimezoneOffset(int timezoneOffset) {
        this.timezoneOffset = timezoneOffset;
    }
    
    private Double speedLimit;
    private static final double UNREACHABLE_SPEED = 1e11;

    public double getSpeedLimit() {
        return speedLimit == null ? UNREACHABLE_SPEED : speedLimit;
    }

    public void setSpeedLimit(Double speedLimit) {
        this.speedLimit = speedLimit;
    }
    
    private String commandPassword;
    
    public String getCommandPassword() {
        return commandPassword == null ? "" : commandPassword;
    }
    
    public void setCommandPassword(String commandPassword) {
        this.commandPassword = commandPassword;
    }
    
    private final double DEFAULT_FUEL_CAPACITY = 60;
    private Double fuelCapacity;
    
    public double getFuelCapacity() {
        if(fuelCapacity == null)
            return DEFAULT_FUEL_CAPACITY;
        return fuelCapacity;
    }

    private double fuelLevel;
    private double fuelUsed;
    
    public void setFuelLevel(double level) {
        this.fuelLevel = level;
    }
    
    public void setFuelUsed(double used) {
        this.fuelUsed = used;
    }
    
    public void updateFuelLevel(Double fuelLevel) {
        double used = 0;
        used = this.fuelLevel - fuelLevel;
        if(used < 0 && used > -5)
            return;
        this.fuelLevel = fuelLevel;
        if(used > 0) {
            fuelUsed += used; 
        }
    }
    
    public double getFuelLevel() {
        return fuelLevel;
    }
    
    public double getFuelUsed() {
        return fuelUsed;
    }
}
