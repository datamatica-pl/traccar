/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.traccar.model;

import org.traccar.database.ActiveDevice;

public class CommandResponse {
    private static final String COMMAND_RESULT_OK = "Ok";
    private static final String COMMAND_RESULT_FAIL = "Device error";
    
    private final ActiveDevice activeDevice;
    private final boolean success;

    public CommandResponse(ActiveDevice activeDevice, boolean success) {
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
        return success ? COMMAND_RESULT_OK : COMMAND_RESULT_FAIL;
    }
}
