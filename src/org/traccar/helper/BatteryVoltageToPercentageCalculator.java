/*
 * Copyright (C) 2016  Datamatica (dev@datamatica.pl)
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

package org.traccar.helper;

/**
 *
 * @author Jan Usarek
 */
public class BatteryVoltageToPercentageCalculator {
    private final int minVoltage;
    private final int maxVoltage;
    private final int voltageDiff;
    
    public BatteryVoltageToPercentageCalculator(int minVoltageMV, int maxVoltageMV) {
        minVoltage = minVoltageMV;
        maxVoltage = maxVoltageMV;
        voltageDiff = maxVoltage - minVoltage;
    }
    
    public int voltsToPercent(int currentVoltage) {
        if (currentVoltage <= minVoltage) {
            return 0;
        } else if (currentVoltage >= maxVoltage) {
            return 100;
        } else {
            int voltageBase = currentVoltage - minVoltage;
            double voltageRatio = voltageBase / (double)voltageDiff;
            return (int)Math.round(voltageRatio * 100);
        }
    }
}
