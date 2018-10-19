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

import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Jan Usarek
 */
public class BatteryVoltageToPercentageCalculatorTest {

    @Test
    public void testBatteryVoltageToPercentage() {
        Map<Integer, Integer> dataAndResult = new HashMap<>();
        LinearBatteryVoltageToPercentCalc calc = new LinearBatteryVoltageToPercentCalc(2750, 4100);
        dataAndResult.put(2500, 0);
        dataAndResult.put(2750, 0);
        dataAndResult.put(2885, 10);
        dataAndResult.put(3020, 20);
        dataAndResult.put(3088, 25);
        dataAndResult.put(3425, 50);
        dataAndResult.put(3763, 75);
        dataAndResult.put(3830, 80);
        dataAndResult.put(3965, 90);
        dataAndResult.put(4100, 100);
        dataAndResult.put(4200, 100);

        for (Map.Entry<Integer, Integer> pair : dataAndResult.entrySet()) {
            int voltage = pair.getKey();
            int expectedResult = pair.getValue();
            int result = calc.voltsToPercent(voltage);
            if (result != expectedResult) {
                Assert.fail(this.getClass().getSimpleName() + " AssertionError for data: "
                        + voltage + " expected " + expectedResult + ", result: " + result);
            }
        }
    }

    @Test
    public void testTeltonikaBatteryVoltageToPercentage() {
        Map<Integer, Integer> dataAndResult = new HashMap<>();
        TeltonikaBatteryVoltageToPercentCalc calc = new TeltonikaBatteryVoltageToPercentCalc();
        dataAndResult.put(2500, 0);
        dataAndResult.put(3500, 0);
        dataAndResult.put(3600, 0);
        dataAndResult.put(3750, 25);
        dataAndResult.put(3900, 50);
        dataAndResult.put(4050, 75);
        dataAndResult.put(4200, 100);
        dataAndResult.put(4300, 100);

        for (Map.Entry<Integer, Integer> pair : dataAndResult.entrySet()) {
            int voltage = pair.getKey();
            int expectedResult = pair.getValue();
            int result = calc.voltsToPercent(voltage);
            if (result != expectedResult) {
                Assert.fail(this.getClass().getSimpleName() + " AssertionError for data: "
                        + voltage + " expected " + expectedResult + ", result: " + result);
            }
        }
    }
}
