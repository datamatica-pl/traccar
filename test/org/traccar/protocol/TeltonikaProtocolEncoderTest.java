package org.traccar.protocol;

import org.junit.Test;
import org.traccar.ProtocolTest;
import org.traccar.model.Command;

public class TeltonikaProtocolEncoderTest extends ProtocolTest {
    
    @Test
    public void testPositonSingleCommand() throws Exception {

        TeltonikaProtocolEncoder encoder = new TeltonikaProtocolEncoder();
        
        Command command = new Command();
        command.setDeviceId(1);
        command.setType(Command.TYPE_POSITION_SINGLE);
        
        verifyCommand(encoder, command, binary("00000000000000100c0105000000086765746770730d0a0100003339"));
    }
    
    @Test
    public void testExtCustomCommandObdinfo() throws Exception {

        TeltonikaProtocolEncoder encoder = new TeltonikaProtocolEncoder();
        
        Command command = new Command();
        command.setDeviceId(1);
        command.setType(Command.TYPE_EXTENDED_CUSTOM);
        
        command.set(Command.KEY_MESSAGE, "obdinfo");
        verifyCommand(encoder, command, binary("00000000000000110c0105000000096f6264696e666f0d0a0100000cd8"));
    }

}
