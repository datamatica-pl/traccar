/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.traccar.protocol;

import org.traccar.StringProtocolEncoder;
import org.traccar.helper.Log;
import org.traccar.model.Command;

/**
 *
 * @author piotrkrzeszewski
 */
public class GrooProtocolEncoder extends StringProtocolEncoder {
    
    @Override
    protected Object encodeCommand(Command command) {
        Log.debug("Sending command " + command.getType() + command.getAttributes());
        
        switch (command.getType()) {
            case Command.TYPE_POSITION_PERIODIC: {
                final String frequency = command.getAttributes().get(Command.KEY_FREQUENCY).toString();
                return "@G#@,V01,19," + Integer.parseInt(frequency) / 60 + ",@R#@";
            }
            case Command.TYPE_ACTIVE_POSITIONING: {
                return "@G#@,V01,25,@R#@";
            }
        }
        return null;
    }
}
