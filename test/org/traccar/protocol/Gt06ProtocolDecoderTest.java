package org.traccar.protocol;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import org.jboss.netty.buffer.ChannelBuffer;
import org.junit.Before;
import org.junit.Test;
import org.traccar.ProtocolTest;
import org.traccar.model.MessageCommandResponse;
import org.traccar.model.ObdInfo;

public class Gt06ProtocolDecoderTest extends ProtocolTest {

    private Gt06ProtocolDecoder decoder;
    
    @Before
    public void testInit() {
        decoder = new Gt06ProtocolDecoder(new Gt06Protocol());
    }   
    
    @Test
    public void testDecode() throws Exception {

        verifyNothing(decoder, binary(
                "787805120099abec0d0a"));

        verifyNothing(decoder, binary(
                "78780D01086471700328358100093F040D0A"));

        verifyNothing(decoder, binary(
                "78780D01012345678901234500018CDD0D0A"));

        verifyNothing(decoder, binary(
                "78780d0103534190360660610003c3df0d0a"));

        verifyAttributes(decoder, binary(
                "78780a13440604000201baaf540d0a"));

        verifyPosition(decoder, binary(
                "78781f120f0a140e150bc505e51e780293a9e800540000f601006e0055da00035f240d0a"),
                position("2015-10-20 14:21:11.000", true, 54.94535, 24.01762));

        verifyPosition(decoder, binary(
                "787823120f081b121d37cb01c8e2cc08afd3c020d50201940701d600a1190041ee100576d1470d0a"));

        verifyPosition(decoder, binary(
                "78781F120B081D112E10CC027AC7EB0C46584900148F01CC00287D001FB8000380810D0A"));

        verifyPosition(decoder, binary(
                "787819100B031A0B1B31CC027AC7FD0C4657BF0115210001001CC6070D0A"));

        verifyPosition(decoder, binary(
                "787821120C010C0F151FCF027AC8840C4657EC00140001CC00287D001F720001000F53A00D0A"));

        verifyPosition(decoder, binary(
                "787825160B051B093523CF027AC8360C4657B30014000901CC00266A001E1740050400020008D7B10D0A"));

        verifyPosition(decoder, binary(
                "787819100e010903230ec803ae32a60653cded00180000020072feb70d0a"));

        verifyPosition(decoder, binary(
                "7878471e0e03110b0511c501c664fd074db73f0218a602e003433a002fed40433a0056e14e433a0056104e433a0056fd53433a002eed55433a007e4b57433a002ee25aff00020120f6720d0a"));

        verifyNothing(decoder, binary(
                "7979005bfd0358899050927725004c0020bf984358df603b2ea3a339e54335013a5b56455253494f4e5d47543036445f32305f3630444d325f423235455f5631355f574d5b4255494c445d323031332f31322f32382031353a3234002a3b240d0a7979005bfd0358899050927725004c0020bf984358df603b2ea3a339e54335013a5b56455253494f4e5d47543036445f32305f3630444d325f423235455f5631355f574d5b4255494c445d323031332f31322f32382031353a3234002d4f9b0d0a7979005bfd0358899050927725004c0020bf984358df603b2ea3a339e54335013a5b56455253494f4e5d47543036445f32305f3630444d325f423235455f5631355f574d5b4255494c445d323031332f31322f32382031353a3234003084ff0d0a"));

        verifyPosition(decoder, binary(
                "787822220e0914160f07c9021a362805090a7800d8b802d402c30e00a98a0105010213f4bb0d0a"));

        verifyNothing(decoder, binary(
                "787811010864717003664467100f190a0002c6d20d0a"));

        verifyNothing(decoder, binary(
                "787811010123456789012345100B3201000171930D0A"));
    }
    
    @Test
    public void login() throws Exception{
        verifyNothing(decoder, binary("7878","0d","01","0123456789012345","0001","8cdd","0d0a"));
    }
    
    @Test
    public void testObd() throws Exception{ 
        login();
        
        ObdInfo info = new ObdInfo();
        info.addProp(0x17, 0); //absErr
        verifyObd(decoder, binary(obdPacket("3137333d30")), info);
        
        info = new ObdInfo();
        info.addProp(0x17, 1);
        info.addProp(0x2a, 1220);
        verifyObd(decoder, binary(obdPacket("3137333d312c3261333d346334")), info);
    }
    
    @Test
    public void commandResponse_gt06_empty() throws Exception {
        String response = "";
        verifyGt06CommandResponse(response, StandardCharsets.US_ASCII);
    }
    
    @Test
    public void commandResponse_gt06_ascii() throws Exception {
        String response = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nunc sit amet purus ultricies, ultrices odio at, suscipit lectus.";
        verifyGt06CommandResponse(response, StandardCharsets.US_ASCII);
    }
    
    @Test
    public void commandResponse_gt06_utf16() throws Exception {
        String response = "Zażółć gęślą jaźń.";
        verifyGt06CommandResponse(response, StandardCharsets.UTF_16BE);
    }
    
    @Test
    public void commandResponse_gt03() throws Exception {
        login();
        verifyCommandResponse(decoder, binary("78780f8107000000004f4b210002007208e70d0a"),
                new MessageCommandResponse(null, "OK!"));
    }
    
    private void verifyGt06CommandResponse(String response, Charset encoding) throws Exception {
        login();
        verifyCommandResponse(decoder, commandResponse78(response, encoding),
                new MessageCommandResponse(null, response));
    }
    
    private ChannelBuffer commandResponse78(String response, Charset encoding) {
        String encodingCode = "0002";
        int length = response.length();
        if(encoding != StandardCharsets.US_ASCII) {
            encodingCode = "0001";
            length *= 2;
        }
        return binary("7878",String.format("%02x", length+12),"15",
                String.format("%02x", 2+length),"12345678",stringToHex(response, encoding),encodingCode,
                "00df","0000","0d0a");
    }
    
    @Test
    public void commandResponse_gt230_empty() throws Exception {
        verifyGt230CommandResponse("", StandardCharsets.US_ASCII);
    }
    
    @Test
    public void commandResponse_gt230_ascii() throws Exception {
        String response = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nunc sit amet purus ultricies, ultrices odio at, suscipit lectus.";
        verifyGt230CommandResponse(response, StandardCharsets.US_ASCII);
    }
    
    @Test
    public void commandResponse_gt230_utf16() throws Exception {
        verifyGt230CommandResponse("Zażółć gęślą jaźń", StandardCharsets.UTF_16BE);
    }

    private void verifyGt230CommandResponse(String response, Charset encoding) throws Exception {
        login();
        verifyCommandResponse(decoder, commandResponse79(response, encoding),
                new MessageCommandResponse(null, response));
    }

    private ChannelBuffer commandResponse79(String response, Charset encoding) {
        String encodingCode = "01";
        int length = response.length();
        if(encoding != StandardCharsets.US_ASCII) {
            encodingCode = "02";
            length *= 2;
        }
        return binary("7979",String.format("%04x",length + 10),"21",
                "00000000",encodingCode,stringToHex(response, encoding),
                "0001","0000","0d0a");
    }
    
    private String obdPacket(String obdData) {
        String head = String.format("7979%04x8c10011a17310301",obdData.length()/2+24);
        String tail = "c60598b6380243bfe03114d2002991600d0a"; 
        return head+obdData+tail;
    }

}
