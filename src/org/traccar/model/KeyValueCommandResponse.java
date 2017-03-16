package org.traccar.model;

import java.util.HashMap;
import java.util.Map;
import org.traccar.database.ActiveDevice;

public class KeyValueCommandResponse extends CommandResponse{
    
    public static final String KEY_BATTERY = "param_battery";
    public static final String KEY_GPRS = "param_gprs";
    public static final String KEY_GSM = "param_gsm";
    public static final String KEY_POWER = "param_power";
    public static final String KEY_GPS = "param_gps";
    public static final String KEY_ACC = "param_acc";
    public static final String KEY_OIL = "param_oil";
    public static final String KEY_POSITION_T = "param_position_t";
    public static final String KEY_NUMBER_A = "param_number_a";
    public static final String KEY_NUMBER_B = "param_number_b";
    public static final String KEY_NUMBER_C = "param_number_c";
    public static final String KEY_TIME_ZONE = "param_time_zone";
    public static final String KEY_OVERSPEED_THRESHOLD = "param_overspeed_threshold";
    public static final String KEY_MOVEMENT_ALARM = "param_movement_alarm";
    public static final String KEY_VIBRATION_ALARM = "param_vibration_alarm";
    public static final String KEY_DEFENSE = "param_defense";
    public static final String KEY_DEFENSE_TIME = "param_defense_time";
    public static final String KEY_SENDS = "param_sends";
    public static final String KEY_SENSORSET = "param_sensorset";
    public static final String KEY_POSITION_D = "param_position_d";
    public static final String KEY_IMEI = "param_imei";
    
    private Map<String, Object> map;
    
    public KeyValueCommandResponse(ActiveDevice activeDevice) {
        this(activeDevice, true);
    }

    public KeyValueCommandResponse(ActiveDevice activeDevice, boolean success) {
        super(activeDevice, success);
        map = new HashMap<>();
    }
    
    public void put(String key, Object value) {
        map.put(key, value);
    }
    
    @Override
    public String toString() {
        return MiscFormatter.toJsonString(map);
    }
}
