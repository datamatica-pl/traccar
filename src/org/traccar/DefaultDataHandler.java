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
package org.traccar;

import java.util.Date;
import org.traccar.helper.Log;
import org.traccar.model.Device;
import org.traccar.model.Event;
import org.traccar.model.Position;

public class DefaultDataHandler extends BaseDataHandler {

    @Override
    protected Position handlePosition(Position position) {

        try {
            Position lastPosition = Context.getConnectionManager().getLastPosition(position.getDeviceId());
            Device device = Context.getDataManager().getDeviceById(position.getDeviceId());
            position.setTime(new Date(position.getFixTime().getTime() + device.getTimezoneOffset() * 60 * 1000));
            if(position.getFuelLevel() != null) {
                device.updateFuelLevel(position.getFuelLevel());
                if(position.getFuelUsed() == null) {
                    position.set(Event.KEY_FUEL_USED, device.getFuelUsed());
                } else {
                    device.setFuelUsed(position.getFuelUsed());
                }
                Context.getDataManager().updateFuel(device);
            } else {
                position.set(Event.KEY_FUEL, -1.);
                position.set(Event.KEY_FUEL_USED, -1.);
            }
            Context.getDataManager().addPosition(position);
            Integer batteryLevel = position.getBatteryLevel();
            if (batteryLevel != null) {
                Context.getDataManager().updateBatteryLevel(device.getId(), batteryLevel);
            }
            if (position.getIgnition() != null) {
                Context.getDataManager().updateIgnition(device.getId(), position.getIgnition());
            }

            if ((lastPosition == null || position.getFixTime().compareTo(lastPosition.getFixTime()) > 0)
                    && position.hasProperValidStatus()) {
                Context.getDataManager().updateLatestPosition(position);
            }
        } catch (Exception error) {
            Log.warning(error);
            try {
                Log.warning("Position can't be handled: " + position.toString());
            } catch (Exception e) {
                Log.warning("Not handled position could not be logge: " + e.getMessage());
            }
            return null;
        }

        return position;
    }

}
