package org.traccar.protocol;

import org.jboss.netty.buffer.ChannelBuffers;
import org.junit.Assert;
import org.junit.Test;
import org.traccar.ProtocolTest;
import org.traccar.helper.ChannelBufferTools;
import org.traccar.model.Command;

import javax.xml.bind.DatatypeConverter;

public class Gt06ProtocolEncoderTest extends ProtocolTest {

    @Test
    public void testEncode() throws Exception {

        Gt06ProtocolEncoder encoder = new Gt06ProtocolEncoder();

        Command command = new Command();
        command.setDeviceId(1);

        command.setType(Command.TYPE_ENGINE_STOP);
        verifyCommand(encoder, command, binary("787812800c0000000052656c61792c312300009dee0d0a"));

        command.setType(Command.TYPE_ENGINE_RESUME);
        verifyCommand(encoder, command, binary("787812800c0000000052656c61792c3023000081550d0a"));

        command.setType(Command.TYPE_GET_PARAMS);
        verifyCommand(encoder, command, binary("787810800a00000000504152414d230000abe90d0a"));

        command.setType(Command.TYPE_SET_TIMEZONE);

        command.set(Command.KEY_TIMEZONE, 0L); // GMT
        verifyCommand(encoder, command, binary("787814800e00000000474d542c452c302c302300004f0d0d0a"));

        command.set(Command.KEY_TIMEZONE, -39600L); // GMT-11
        verifyCommand(encoder, command, binary("787815800f00000000474d542c572c31312c3023000049f40d0a"));

        command.set(Command.KEY_TIMEZONE, -34200L); // GMT-9.30
        verifyCommand(encoder, command, binary("787815800f00000000474d542c572c392c33302300003ecb0d0a"));

        command.set(Command.KEY_TIMEZONE, 3600L); // GMT+1
        verifyCommand(encoder, command, binary("787814800e00000000474d542c452c312c302300004b260d0a"));

        command.set(Command.KEY_TIMEZONE, 28800L); // GMT+8
        verifyCommand(encoder, command, binary("787814800e00000000474d542c452c382c302300006e550d0a"));

        command.set(Command.KEY_TIMEZONE, 31500L); // GMT+8:45
        verifyCommand(encoder, command, binary("787815800f00000000474d542c452c382c34352300009bfb0d0a"));

        command.setType(Command.TYPE_SET_SOS_NUMBERS);

        command.set(Command.KEY_SOS_NUMBER_1, 111222333);
        verifyCommand(encoder, command, binary("78781c801600000000534f532c412c3131313232323333332c2c2300003a8a0d0a"));

        command.set(Command.KEY_SOS_NUMBER_1, 111222333);
        command.set(Command.KEY_SOS_NUMBER_2, 111222444);
        verifyCommand(encoder, command, binary("787825801f00000000534f532c412c3131313232323333332c31313132323234" +
                                               "34342c23000042ed0d0a"));

        command.set(Command.KEY_SOS_NUMBER_1, 111222333);
        command.set(Command.KEY_SOS_NUMBER_2, 111222444);
        command.set(Command.KEY_SOS_NUMBER_3, 111222555);
        verifyCommand(encoder, command, binary("78782e802800000000534f532c412c3131313232323333332c313131323232" +
                                               "3434342c31313132323235353523000047000d0a"));

        command.setType(Command.TYPE_DELETE_SOS_NUMBER);

        command.set(Command.KEY_SOS_NUMBER, 111222333);
        verifyCommand(encoder, command, binary("78781a801400000000534f532c442c31313132323233333323000046ba0d0a"));

        command.set(Command.KEY_SOS_NUMBER, "1,2,3");
        verifyCommand(encoder, command, binary("787816801000000000534f532c442c312c322c33230000386a0d0a"));

        command.setType(Command.TYPE_SET_CENTER_NUMBER);

        command.set(Command.KEY_CENTER_NUMBER, "");
        verifyCommand(encoder, command, binary("787813800d0000000043454e5445522c4423000067f40d0a"));
        
        command.set(Command.KEY_CENTER_NUMBER, "111222333");
        verifyCommand(encoder, command, binary("78781d80170000000043454e5445522c412c313131323232333333230000ab170d0a"));
        
        command.setType(Command.TYPE_REBOOT_DEVICE);
        verifyCommand(encoder, command, binary("787810800a000000005245534554230000dc090d0a"));
        
        command.setType(Command.TYPE_POSITION_PERIODIC);
        command.set(Command.KEY_FREQUENCY, 30);
        verifyCommand(encoder, command, binary("78781680100000000054494d45522c33302c33302300003e910d0a"));
        
        command.setType(Command.TYPE_FACTORY_SETTINGS);
        verifyCommand(encoder, command, binary("787812800c00000000464143544f5259230000c10a0d0a"));
    }

}
