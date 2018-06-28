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

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Jan Usarek
 */
public class AnalogInputToFuelLevelCalculatorTest {
    private final double[] FUEL_ANALOG_VAL = new double[] {
        0, 0.125, 0.25, 0.375, 0.5, 0.625, 0.75, 0.875, 1
    };
    private final int[] FUEL_ANALOG_VOL = new int[] {
        2505, 2811, 3121, 3428, 3692, 3956, 4163, 4378, 4701
    };
    private final double fuelCapacity = 60.0;
    private AnalogInputToFuelLevelCalculator fuelCalc = new AnalogInputToFuelLevelCalculator(
                                                            FUEL_ANALOG_VOL, FUEL_ANALOG_VAL, fuelCapacity);
    
    @Test
    public void testFuelBelowZero() throws Exception {
        assertEquals(0.0, fuelCalc.calculate(2000), 0.001);
    }
    
    @Test
    public void testFuelZero() throws Exception {
        assertEquals(0.0, fuelCalc.calculate(2505), 0.001);
    }
    
    @Test
    public void testFuelOver100Percent() throws Exception {
        assertEquals(fuelCapacity, fuelCalc.calculate(7000), 0.001);
    }
    
    @Test
    public void testFuelExactlyFull() throws Exception {
        assertEquals(fuelCapacity, fuelCalc.calculate(4701), 0.001);
    }
    
    @Test
    public void testFuelHalf() throws Exception {
        assertEquals(fuelCapacity / 2, fuelCalc.calculate(3692), 0.001);
    }
    
    @Test
    public void testFuelNearlyFull() throws Exception {
        // TODO: Verify later this behaviour. Shouldn't a value between steps return lineraly counted value somewhere between steps?
        // For now it's OK, returning value as multipier of lower threshold can be accepted.
        assertEquals(FUEL_ANALOG_VAL[FUEL_ANALOG_VAL.length - 2] * fuelCapacity, fuelCalc.calculate(4700), 0.001);
    }
}
