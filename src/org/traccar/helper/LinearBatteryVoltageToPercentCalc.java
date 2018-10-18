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

// Remeber that battery voltage can't be so easy converted to percentage values. Really it's not a linear function.
// This function varies from battery to battery, depending on battery type, temperature and maybe more, sample charts are visible here:
// https://devzone.nordicsemi.com/f/nordic-q-a/28101/how-to-calculate-battery-voltage-into-percentage-for-aa-2-batteries-without-fluctuations/110796#110796
// Values minVoltage and maxVoltage given to this class should have cut edge values, especially low voltage end.
// For example: when device lose all power on 3450mV use rather higher value for minVoltage. I use 3600mV (for Teltonika TMT-250 it's a little more than 20%).

/**
 *
 * @author Jan Usarek
 */
public class LinearBatteryVoltageToPercentCalc {
    private final int minVoltage;
    private final int maxVoltage;
    private final int voltageDiff;
    
    public LinearBatteryVoltageToPercentCalc(int minVoltageMV, int maxVoltageMV) {
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
