/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.traccar.model;

import java.util.HashMap;

public class ObdInfo extends HashMap<String, Object> {  
    
    private static class PropertyInfo {
        public String name;
        public Class type;
        
        public PropertyInfo(String name) {
            this(name, Integer.class);
        }
        public PropertyInfo(String name, Class type) {
            this.name = name;
            this.type = type;
        }
    }
    
    public static final HashMap<Integer, PropertyInfo> ids = new HashMap<Integer,PropertyInfo>();
    static {
        ids.put( 0x17, new PropertyInfo("ECMErr", Boolean.class));
        ids.put( 0x18, new PropertyInfo("ABSErr", Boolean.class));
        ids.put( 0x19, new PropertyInfo("SRSErr", Boolean.class));
        ids.put( 0x2a, new PropertyInfo("RemainOilL", Double.class));
        ids.put( 0x2b, new PropertyInfo("RemainOilPercent", Double.class));
        ids.put( 0x38, new PropertyInfo("InstFuelConsumptionKM", Double.class));
        ids.put( 0x39, new PropertyInfo("InstFuelConsumptionH", Double.class));
        ids.put( 0x3a, new PropertyInfo("OilLife", Double.class));
        ids.put( 0x3b, new PropertyInfo("OilPressure", Double.class));
        ids.put( 0x2d, new PropertyInfo("WaterTemperature", Double.class));
        ids.put( 0x3f, new PropertyInfo("RelPosOfAccPedal", Double.class));
        ids.put( 0x40, new PropertyInfo("AccPedalPressed", Boolean.class));
        
        ids.put(0x00, new PropertyInfo("HighBeam"));
        ids.put(0x01, new PropertyInfo("LowBeam"));
        ids.put(0x02, new PropertyInfo("widthLamp"));
        ids.put(0x03, new PropertyInfo("FogLamp"));
        ids.put(0x04, new PropertyInfo("LeftTurnLight"));
        ids.put(0x05, new PropertyInfo("RightTurnLight"));
        ids.put(0x06, new PropertyInfo("HazardLight"));
        ids.put(0x07, new PropertyInfo("LFDoor"));
        ids.put(0x08, new PropertyInfo("RFDoor"));
        ids.put(0x09, new PropertyInfo("LBDoor"));
        ids.put(0x0a, new PropertyInfo("RBDoor"));
        ids.put(0x0b, new PropertyInfo("TDoor"));
        ids.put(0x0c, new PropertyInfo("AllDoorLocked"));
        ids.put(0x0d, new PropertyInfo("LFDoorLocked"));
        ids.put(0x0e, new PropertyInfo("RFDoorLocked"));
        ids.put(0x0f, new PropertyInfo("LBDoorLocked"));
        ids.put(0x10, new PropertyInfo("RBDoorLocked"));
        ids.put(0x11, new PropertyInfo("TDoorLocked"));
        ids.put(0x12, new PropertyInfo("LFWindow"));
        ids.put(0x13, new PropertyInfo("RFWindow"));
        ids.put(0x14, new PropertyInfo("LBWindow"));
        ids.put(0x15, new PropertyInfo("RBWindow"));
        ids.put(0x16, new PropertyInfo("SWindow"));
        
        ids.put(0x1a, new PropertyInfo("OilAlrmSignal"));
        ids.put(0x1b, new PropertyInfo("TirePressAlrmSignal"));
        ids.put(0x1c, new PropertyInfo("MaintenanceAlrmSignal"));
        ids.put(0x1d, new PropertyInfo("AirbagStatus"));
        ids.put(0x1e, new PropertyInfo("HandbrakeStatus"));
        ids.put(0x1f, new PropertyInfo("BrakeStatus"));
        ids.put(0x20, new PropertyInfo("DriverSafetyBelt"));
        ids.put(0x21, new PropertyInfo("CoDriverSafetyBelt"));
        ids.put(0x22, new PropertyInfo("ACCSignal"));
        ids.put(0x23, new PropertyInfo("KeyStatus"));
        ids.put(0x24, new PropertyInfo("RemoteSignal"));
        ids.put(0x25, new PropertyInfo("RainWiperStatus"));
        ids.put(0x26, new PropertyInfo("AirCondition"));
        ids.put(0x27, new PropertyInfo("Gears"));
        ids.put(0x28, new PropertyInfo("TotalMileage"));
        ids.put(0x29, new PropertyInfo("ContinueCourse"));
        ids.put(0x2c, new PropertyInfo("SingleOilConsumption"));
        ids.put(0x2e, new PropertyInfo("EngineInletAirTemperature"));
        ids.put(0x2f, new PropertyInfo("AirConditionTemperature"));
        ids.put(0x30, new PropertyInfo("BatteryVoltage"));
        ids.put(0x31, new PropertyInfo("LFWheelSpeed"));
        ids.put(0x32, new PropertyInfo("RFWheelSpeed"));
        ids.put(0x33, new PropertyInfo("LBWheelSpeed"));
        ids.put(0x34, new PropertyInfo("RBWheelSpeed"));
        ids.put(0x35, new PropertyInfo("Speed"));
        ids.put(0x36, new PropertyInfo("RotationSpeed"));
        ids.put(0x37, new PropertyInfo("AverageFuelConsumption"));
        ids.put(0x3c, new PropertyInfo("AirFlow"));
        ids.put(0x3d, new PropertyInfo("IntakeManifoldAbsolutePressure"));
        ids.put(0x3e, new PropertyInfo("FuelInjectionPulseWidth"));
        ids.put(0x41, new PropertyInfo("SteeringAngle"));
        ids.put(0x42, new PropertyInfo("SteeringWheelAngleStatus"));
        ids.put(0x80, new PropertyInfo("CurMaxDrivingSpeed"));
        ids.put(0x81, new PropertyInfo("CurIdlingTime"));
        ids.put(0x82, new PropertyInfo("FastAcc"));
        ids.put(0x83, new PropertyInfo("QuickSlowdown"));
    }
    
    public ObdInfo() {
        put("ECMErr", null);
        put("ABSErr", null);
        put("SRSErr", null);
        put("RemainOilL", null);
        put("RemainOilPercent", null);
        put("InstFuelConsumptionKM", null);
        put("InstFuelConsumptionH", null);
        put("OilLife", null);
        put("OilPressure", null);
        put("WaterTemperature", null);
        put("RelPosOfAccPedal", null);
        put("AccPedalPressed", null);
    }

    public void addProp(int key, int val) throws Exception {
        if(!ids.containsKey(key))
            return;
        PropertyInfo info = ids.get(key);
        Object typedValue = decodeValue(info.type, val);
        if(containsKey(info.name))
            remove(info.name);
        put(info.name, typedValue);
    }

    private Object decodeValue(Class type, int val) {
        if(type == Double.class)
            return val/100.0;
        else if(type == Boolean.class) 
            return val != 0;
        else
            return type.cast(val);
    }
}