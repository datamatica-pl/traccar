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

    public CommandResponse(ActiveDevice activeDevice, String data) {
        this.activeDevice = activeDevice;
        this.data = data;
    }
    
    
    public ActiveDevice getActiveDevice() {
        return activeDevice;
    }   
    
    @Override
    public String toString() {
        return data;
    }
}
