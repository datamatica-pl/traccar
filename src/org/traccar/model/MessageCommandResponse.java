package org.traccar.model;

import org.traccar.database.ActiveDevice;

public class MessageCommandResponse extends CommandResponse {
    private final String data;

    public MessageCommandResponse(ActiveDevice activeDevice, String data) {
        this(activeDevice, data, true);
    }

    public MessageCommandResponse(ActiveDevice activeDevice, String data, boolean success) {
        super(activeDevice, success);
        this.data = data;
    }

    @Override
    public String toString() {
        return data;
    }
}
