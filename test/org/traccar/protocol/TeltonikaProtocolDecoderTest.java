package org.traccar.protocol;

import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import static org.mockito.Matchers.anyInt;
import org.mockito.Mockito;
import org.traccar.ProtocolTest;
import org.traccar.helper.LinearBatteryVoltageToPercentCalc;
import org.traccar.model.MessageCommandResponse;
import org.traccar.model.Position;
import org.traccar.helper.IBatteryVoltageToPercentCalc;
import org.traccar.helper.TeltonikaBatteryVoltageToPercentCalc;

public class TeltonikaProtocolDecoderTest extends ProtocolTest {

    private final TeltonikaProtocolDecoder decoder = new TeltonikaProtocolDecoder(new TeltonikaProtocol(),
            new TeltonikaBatteryVoltageToPercentCalc());

    @Test
    public void testDecode() throws Exception {

        verifyNothing(decoder, binary(
                "000F313233343536373839303132333435"));

        verifyPositions(decoder, false, binary(
                "0000000000000055070450aa14320201f00150aa17f3031f42332a4c4193d68c008d00020901f00150aa1b6a031f423383f54193624f009d00000a01f00150aa1c230fc01a0000552b040164f400dd00f0010143100c0105000000050400006846"));

        verifyPositions(decoder, binary(
                "000000000000003508010000014f8e016420002141bbaf0f4e96a7fffa0000120000000602010047030242669c92000002c7000000009100000000000100002df3"));

        verifyPositions(decoder, binary(
                "00000000000000A7080400000113fc208dff000f14f650209cca80006f00d60400040004030101150316030001460000015d0000000113fc17610b000f14ffe0209cc580006e00c00500010004030101150316010001460000015e0000000113fc284945000f150f00209cd200009501080400000004030101150016030001460000015d0000000113fc267c5b000f150a50209cccc0009300680400000004030101150016030001460000015b00040000"));

        verifyPositions(decoder, binary(
                "000000000000014708060000013e5a60a4cb003fa7b780fc424518004200000a000000090501010200b300b400f000034268a746011818000001c700000000000000013e5dc8ba28003fa7c080fc4246040001000005000000090501010200b300b400f001034268b44600ef18000001c700000000000000013e5dc90455003fa7b640fc424388003a0000070000f0090501010200b300b400f000034268dc4600f718000001c70000001d000000013e5dc9d368003fa7b800fc4244300049000004000000090501010200b300b400f001034267de46010718000001c700000000000000013e5dca311d003fa7b680fc4243cc00420000070000f0090501010200b300b400f0000342685346010b18000001c700000000000000013e5dcfafe9003fa7b600fc4242f0003d000008000000090501010200b300b400f0000342685246011918000001c700000000000600000275"));

        verifyPositions(decoder, binary(
                "000000000000002c08010000013eff8d6f9800173295002111f400008100ae0b0000000401010003090016432980422f7200000100007a5d"));

        verifyPositions(decoder, binary(
                "00000000000000c7070441bf9db00fff425adbd741ca6e1e009e1205070001030b160000601a02015e02000314006615000a160067010500000ce441bf9d920fff425adbb141ca6fc900a2b218070001030b160000601a02015e02000314006615000a160067010500000cc641bf9d740fff425adbee41ca739200b6c91e070001030b1f0000601a02015f02000314006615000a160066010500000ca841bf9cfc0fff425adba041ca70c100b93813070001030b1f0000601a02015f02000314002315000a160025010500000c3004000000"));

        verifyPositions(decoder, binary(
                "000000000000003107024c61410b013f4231c2c141d0beb9003d000005006483ff4c6140eb013f4231c2c141d0beb9003d000005006483ff02000041df"));

        verifyPositions(decoder, binary(
                "000000000000002b080100000140d4e3ec6e000cc661d01674a5e0fffc00000900000004020100f0000242322318000000000100007a04"));

        verifyCommandResponse(decoder, binary(
                "000000000000002d0c01060000002523464d323d3236323033323736313732313339362c32363230332c30372e30322e30350d0a0100009a2e"),
                new MessageCommandResponse(null, "#FM2=262032761721396,26203,07.02.05\r\n"));

        verifyPositions(decoder, binary(
                "00000000000000a608010000013f14a1d1ce000f0eb790209a778000ab010c0500000000000000000100003390"));

    }

    @Test
    public void testPostionWithIgnition() throws Exception {
        final String posHeaderAndLenght = "000000000000003f";
        final String posData = "080100000160e52f0098000c90bbfc1f17c25f00000000000000f00c06"
                + "ef01" // Ignition ON
                + "f00050011504c800450106b50013b6001142317a180000430ee94400d9000001";
        final String posCRC16 = "00006ef1";

        List<Position> list = (List)decoder.decode(null, null, binary(posHeaderAndLenght + posData + posCRC16));

        Assert.assertTrue( list.get(0).getIgnition() );
    }

    @Test
    public void testPostionWithoutIgnition() throws Exception {
        final String posHeaderAndLenght = "000000000000003f";
        final String posData = "080100000160e52f0098000c90bbfc1f17c25f00000000000000f00c06"
                + "ef00" // Ignition OFF
                + "f00050011504c800450106b50013b6001142317a180000430ee94400d9000001";
        final String posCRC16 = "0000ae9c";

        List<Position> list = (List)decoder.decode(null, null, binary(posHeaderAndLenght + posData + posCRC16));

        Assert.assertFalse( list.get(0).getIgnition() );
    }

    @Test
    public void testPostionWithBatteryPercent() throws Exception {
        List<Position> list = (List)decoder.decode(null, null, binary("000000000000003c"
                + "08010000016687661068000c90e8941f17bc1f00000000000000000b06f00150011504c8004502"
                + "7130" // Battery percent (IO 113). Battery level is 48
                + "05b50000b60000420014180000"
                + "430f0f" // Battery voltage, because of occurence of battery percent it should by ignored by decoder
                + "00000100009a29"));

        Assert.assertEquals(48, (int)list.get(0).getBatteryLevel());
    }

    @Test
    public void testPostionWithBatteryVoltage() throws Exception {
        IBatteryVoltageToPercentCalc batteryCalcMock = Mockito.mock(LinearBatteryVoltageToPercentCalc.class);
        Mockito.when(batteryCalcMock.voltsToPercent(anyInt())).thenReturn(50);

        final TeltonikaProtocolDecoder decoderMockedBatCalc = new TeltonikaProtocolDecoder(new TeltonikaProtocol(),
            batteryCalcMock);
        
        List<Position> list = (List)decoderMockedBatCalc.decode(null, null, binary("000000000000003A"
                + "0801000001668853E720000C8F3BBD1F179468005601640B0000000A05F00150011504C800450105B50006B6000542004D180000"
                + "4310C6" // battery key + real battery leven (voltage) but in test we overwrite it by mocked value
                + "0000010000DE42"));

        // Because battery percent is not available here, expected behaviour is get battery level from calculator mocked before
        Assert.assertEquals(50, (int)list.get(0).getBatteryLevel());
    }
}
