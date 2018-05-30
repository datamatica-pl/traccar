/*
 * Copyright (C) 2018  Datamatica (dev@datamatica.pl)
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
 * @author ŁŁ
 */
public class AnalogInputToFuelLevelCalculator {
    private final int[] vol;
    private final double[] lvl;
    private final double capacity;
    
    public AnalogInputToFuelLevelCalculator(int[] vol, double[] lvl, double capacity) {
        this.vol = vol;
        this.lvl = lvl;
        this.capacity = capacity;
    }
    
    public double calculate(int volt) {
        int i;
        for(i=0;i<vol.length;++i)
            if(volt < vol[i])
                break;
        if(i == 0)
            return 0;
        if(i == vol.length)
            return capacity;
        return ((volt - vol[i-1])/(vol[i]-vol[i-1])*(lvl[i]-lvl[i-1]) + lvl[i-1])
                *capacity;
    }
}
