/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.traccar.model;

import org.traccar.database.ActiveDevice;

public class CommandResponse {
    private String data;
    private ActiveDevice activeDevice;
    private boolean success;

    public CommandResponse(ActiveDevice activeDevice, String data) {
        this(activeDevice, data, true);
    }

    public CommandResponse(ActiveDevice activeDevice, String data, boolean success) {
        this.data = data;
        this.activeDevice = activeDevice;
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }
    
    public ActiveDevice getActiveDevice() {
        return activeDevice;
    }   
    
    @Override
    public String toString() {
        return data;
    }
}
