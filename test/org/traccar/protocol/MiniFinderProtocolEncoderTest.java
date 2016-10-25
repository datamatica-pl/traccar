package org.traccar.protocol;

import org.junit.Before;
import org.junit.Test;
import org.traccar.ProtocolTest;
import org.traccar.model.Command;

public class MiniFinderProtocolEncoderTest extends ProtocolTest {

    private String prefix = "123456";
    private MiniFinderProtocolEncoder encoder;

    @Before
    public void setup() {
        encoder = new MiniFinderProtocolEncoder();
    }

    @Test
    public void testEncodeCustom() throws Exception {
        String expected = String.format("%sM,700", prefix);
        Command command = new Command();
        command.setType(Command.TYPE_CUSTOM);
        command.set("raw", expected);
        Object encoded = encoder.encodeCommand(command);
        assert expected.equals(encoded);
    }
    
    @Test
    public void testEncodePositionSingle() throws Exception {
        String expected = String.format("%sLOC", prefix);
        Command command = new Command();
        command.setType(Command.TYPE_POSITION_SINGLE);
        Object encoded = encoder.encodeCommand(command);
        assert expected.equals(encoded);
    }
    
    @Test
    public void testEncodePositionPeriodic90sec() throws Exception {
        String expected = String.format("%sM,009", prefix);
        Command command = new Command();
        command.setType(Command.TYPE_POSITION_PERIODIC);
        command.set("frequency", 90);
        Object encoded = encoder.encodeCommand(command);
        assert expected.equals(encoded);
    }
    
    @Test
    public void testEncodePositionPeriodic185sec() throws Exception {
        String expected = String.format("%sM,018", prefix);
        Command command = new Command();
        command.setType(Command.TYPE_POSITION_PERIODIC);
        command.set("frequency", 185);
        Object encoded = encoder.encodeCommand(command);
        assert expected.equals(encoded);
    }
    
    @Test
    public void testEncodePositionPeriodic1200sec() throws Exception {
        String expected = String.format("%sM,120", prefix);
        Command command = new Command();
        command.setType(Command.TYPE_POSITION_PERIODIC);
        command.set("frequency", 1200);
        Object encoded = encoder.encodeCommand(command);
        assert expected.equals(encoded);
    }
    
    @Test
    public void testEncodePositionPeriodic5sec() throws Exception {
        // 5 sec is too small value, it's not supported, set default value then to 60s (6*10s)
        String expected = String.format("%sM,006", prefix);
        Command command = new Command();
        command.setType(Command.TYPE_POSITION_PERIODIC);
        command.set("frequency", 5);
        Object encoded = encoder.encodeCommand(command);
        assert expected.equals(encoded);
    }
    
    @Test
    public void testEncodePositionPeriodic10805sec() throws Exception {
        // 10805 sec is big value, it's not supported, set default value then to 60s (6*10s)
        String expected = String.format("%sM,006", prefix);
        Command command = new Command();
        command.setType(Command.TYPE_POSITION_PERIODIC);
        command.set("frequency", 10805);
        Object encoded = encoder.encodeCommand(command);
        assert expected.equals(encoded);
    }
    
    @Test
    public void testEncodeGetStatus() throws Exception {
        String expected = String.format("%sG", prefix);
        Command command = new Command();
        command.setType(Command.TYPE_GET_STATUS);
        Object encoded = encoder.encodeCommand(command);
        assert expected.equals(encoded);
    }
    
    @Test
    public void testEncodeRebootDevice() throws Exception {
        String expected = String.format("%sT", prefix);
        Command command = new Command();
        command.setType(Command.TYPE_REBOOT_DEVICE);
        Object encoded = encoder.encodeCommand(command);
        assert expected.equals(encoded);
    }
    
    @Test
    public void testListenMode() throws Exception {
        String expected = String.format("%sP1", prefix);
        Command command = new Command();
        command.setType(Command.TYPE_LISTEN_MODE);
        Object encoded = encoder.encodeCommand(command);
        assert expected.equals(encoded);
    }
    
    @Test
    public void testVoiceCallMode() throws Exception {
        String expected = String.format("%sP0", prefix);
        Command command = new Command();
        command.setType(Command.TYPE_VOICE_CALL_MODE);
        Object encoded = encoder.encodeCommand(command);
        assert expected.equals(encoded);
    }
    
    @Test
    public void testSleepMode() throws Exception {
        String expected = String.format("%sSP1", prefix);
        Command command = new Command();
        command.setType(Command.TYPE_SLEEP_MODE);
        Object encoded = encoder.encodeCommand(command);
        assert expected.equals(encoded);
    }
    
    @Test
    public void testExitSleepMode() throws Exception {
        String expected = String.format("%sSP0", prefix);
        Command command = new Command();
        command.setType(Command.TYPE_EXIT_SLEEP_MODE);
        Object encoded = encoder.encodeCommand(command);
        assert expected.equals(encoded);
    }
    
    @Test
    public void testTZUtc() throws Exception {
        String expected = String.format("%sL+00", prefix);
        Command command = new Command();
        command.setType(Command.TYPE_SET_TIMEZONE);
        command.set(Command.KEY_TIMEZONE, 0L);
        Object encoded = encoder.encodeCommand(command);
        assert expected.equals(encoded);
    }
    
    @Test
    public void testTZPlus7h() throws Exception {
        String expected = String.format("%sL+07", prefix);
        Command command = new Command();
        command.setType(Command.TYPE_SET_TIMEZONE);
        command.set(Command.KEY_TIMEZONE, 25200L);
        Object encoded = encoder.encodeCommand(command);
        assert expected.equals(encoded);
    }
    
    @Test
    public void testTZPlus11h() throws Exception {
        String expected = String.format("%sL+11", prefix);
        Command command = new Command();
        command.setType(Command.TYPE_SET_TIMEZONE);
        command.set(Command.KEY_TIMEZONE, 39600L);
        Object encoded = encoder.encodeCommand(command);
        assert expected.equals(encoded);
    }
    
    @Test
    public void testTZPlus8h45min() throws Exception {
        String expected = String.format("%sL+08", prefix);
        Command command = new Command();
        command.setType(Command.TYPE_SET_TIMEZONE);
        command.set(Command.KEY_TIMEZONE, 30420L);
        Object encoded = encoder.encodeCommand(command);
        assert expected.equals(encoded);
    }
    
    @Test
    public void testTZMinus6h() throws Exception {
        String expected = String.format("%sL-06", prefix);
        Command command = new Command();
        command.setType(Command.TYPE_SET_TIMEZONE);
        command.set(Command.KEY_TIMEZONE, -21600L);
        Object encoded = encoder.encodeCommand(command);
        assert expected.equals(encoded);
    }
    
    @Test
    public void testTZMinus9h30m() throws Exception {
        String expected = String.format("%sL-09", prefix);
        Command command = new Command();
        command.setType(Command.TYPE_SET_TIMEZONE);
        command.set(Command.KEY_TIMEZONE, -34200L);
        Object encoded = encoder.encodeCommand(command);
        assert expected.equals(encoded);
    }
    
    @Test
    public void testTZMinus11h() throws Exception {
        String expected = String.format("%sL-11", prefix);
        Command command = new Command();
        command.setType(Command.TYPE_SET_TIMEZONE);
        command.set(Command.KEY_TIMEZONE, -39600L);
        Object encoded = encoder.encodeCommand(command);
        assert expected.equals(encoded);
    }

    @Test
    public void testEncodeUnsupportedCommand() throws Exception {
        Command command = new Command();
        command.setType("UNSUPPORTED");
        Object o = encoder.encodeCommand(command);
        assert o == null;
    }

}
