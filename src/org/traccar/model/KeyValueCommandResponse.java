package org.traccar.model;

import java.util.HashMap;
import java.util.Map;
import org.traccar.database.ActiveDevice;

public class KeyValueCommandResponse extends CommandResponse{
    
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
