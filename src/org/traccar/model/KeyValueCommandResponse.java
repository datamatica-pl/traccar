package org.traccar.model;

import java.util.HashMap;
import java.util.Map;
import org.traccar.database.ActiveDevice;

public class KeyValueCommandResponse extends CommandResponse{
    
    public static final String KEY_BATTERY = "cmd_param_battery";
    public static final String KEY_GPRS = "cmd_param_gprs";
    public static final String KEY_GSM = "cmd_param_gsm";
    public static final String KEY_POWER = "cmd_param_power";
    public static final String KEY_GPS = "cmd_param_gps";
    public static final String KEY_ACC = "cmd_param_acc";
    public static final String KEY_OIL = "cmd_param_oil";
    public static final String KEY_POSITION_T = "cmd_param_position_t";
    public static final String KEY_NUMBER_A = "cmd_param_number_a";
    public static final String KEY_NUMBER_B = "cmd_param_number_b";
    public static final String KEY_NUMBER_C = "cmd_param_number_c";
    public static final String KEY_TIME_ZONE = "cmd_param_time_zone";
    public static final String KEY_OVERSPEED_THRESHOLD = "cmd_param_overspeed_threshold";
    public static final String KEY_MOVEMENT_ALARM = "cmd_param_movement_alarm";
    public static final String KEY_VIBRATION_ALARM = "cmd_param_vibration_alarm";
    public static final String KEY_DEFENSE = "cmd_param_defense";
    public static final String KEY_DEFENSE_TIME = "cmd_param_defense_time";
    public static final String KEY_SENDS = "cmd_param_sends";
    public static final String KEY_SENSORSET = "cmd_param_sensorset";
    public static final String KEY_POSITION_D = "cmd_param_position_d";
    public static final String KEY_IMEI = "cmd_param_imei";
    public static final String KEY_DATA_LINK = "cmd_param_data_link";
    public static final String KEY_ROAMING = "cmd_param_roaming";
    public static final String KEY_INIT_TIME = "cmd_param_init_time";
    public static final String KEY_RTC_TIME = "cmd_param_rtc_time";
    
    
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
    
    public boolean isEmpty() {
        return map.isEmpty();
    }
    
    @Override
    public String toString() {
        return MiscFormatter.toJsonString(map);
    }
}
