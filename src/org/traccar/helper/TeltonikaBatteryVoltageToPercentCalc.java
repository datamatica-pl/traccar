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
 * 
 * It's just a linear battery voltage to percentage calculator with MIN and MAX voltage adjusted for Teltonika devices
 */
public class TeltonikaBatteryVoltageToPercentCalc implements IBatteryVoltageToPercentCalc {
    private static final int MIN_VOLTAGE = 3600;
    private static final int MAX_VOLTAGE = 4200;
    private final LinearBatteryVoltageToPercentCalc calc = new LinearBatteryVoltageToPercentCalc(MIN_VOLTAGE, MAX_VOLTAGE);

    @Override
    public int voltsToPercent(int currentVoltage) {
        return calc.voltsToPercent(currentVoltage);
    }
}
