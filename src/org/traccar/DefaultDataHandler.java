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
            Context.getDataManager().addPosition(position);
            Position lastPosition = Context.getConnectionManager().getLastPosition(position.getDeviceId());
            Device device = Context.getDataManager().getDeviceById(position.getDeviceId());
            if(!device.isSpeedAlarm() && position.getSpeed() > device.getSpeedLimit()) {
                device.setSpeedAlarm(true);
                Context.getDataManager().updateSpeedAlarm(device.getId(), true);
            }
            Boolean ignition = (Boolean)position.getAttributes().get(Event.KEY_IGNITION);
            if(device.isSpeedAlarm() && ignition != null && !ignition) {
                device.setSpeedAlarm(false);
                Context.getDataManager().updateSpeedAlarm(device.getId(), false);
            }
            
            position.setTime(new Date(position.getFixTime().getTime() + device.getTimezoneOffset()*60*1000));
            if (lastPosition == null || position.getFixTime().compareTo(lastPosition.getFixTime()) > 0) {
                if (position.hasProperValidStatus()) {
                    Context.getDataManager().updateLatestPosition(position);
                }
            }
            
        } catch (Exception error) {
            Log.warning(error);
            try {
                Log.warning("Position can't be handled: " + position.toString());
            } catch (Exception e) {
                Log.warning("Not handled position could not be logge: " + e.getMessage());
            }
        }

        return position;
    }

}
