package org.traccar.protocol;

import org.junit.Test;
import org.traccar.ProtocolTest;

public class Gl200ProtocolDecoderTest extends ProtocolTest {

    @Test
    public void testDecode() throws Exception {

        Gl200ProtocolDecoder decoder = new Gl200ProtocolDecoder(new Gl200Protocol());

        verifyPosition(decoder, text(
                "+RESP:GTFRI,110100,A5868800000015,,0,0,1,1,4.3,92,70.0,121.354335,31.222073,20110214013254,0460,0000,18d8,6141,00,80,20110214013254,000C"));

        verifyNothing(decoder, text(
                "+RESP:GTFRI,210102,A10000458356CE,,0,1,1,15,1.4,0,190.6,-85.765763,42.894896,20160208164505,4126,210,0,18673,00,92,20160208164507,00A6"));

        verifyPosition(decoder, text(
                "+BUFF:GTFRI,060402,862894021808798,,,10,1,1,0.0,349,394.3,-63.287717,-17.662410,20160116234031,0736,0003,6ABA,8305,00,3326.8,,,,94,220100,,,,20160116194035,4D83"));

        verifyPosition(decoder, text(
                "+RESP:GTIDA,06020A,862170013895931,,,D2C4FBC5,1,1,1,0.8,0,22.2,117.198630,31.845229,20120802121626,0460,0000,5663,2BB9,00,0.0,,,,,20120802121627,008E$"));

        verifyAttributes(decoder, text(
                "+RESP:GTINF,1F0101,135790246811220,1G1JC5444R7252367,,16,898600810906F8048812,16,0,1,12000,,4.2,0,0,,,20090214013254,,,,,,+0800,0,20090214093254,11F0$"));

        verifyAttributes(decoder, text(
                "+RESP:GTFRI,120113,555564055560555,,1,1,1,,,,,,,,0282,0380,f080,cabf,6900,79,20140824165629,0001$"));

        verifyPosition(decoder, text(
                "+RESP:GTFRI,0F0106,862193020451183,,,10,1,1,0.0,163,,-57.513617,-25.368191,20150918182145,,,,,,21235.0,,,,0,210100,,,,20150918182149,00B8$"));

        verifyPosition(decoder, text(
                "+RESP:GTOBD,1F0109,864251020135483,,gv500,0,78FFFF,,1,12613,,,,,,,,,,,,,,1286,0,0.0,0,17.1,3.379630,6.529701,20150813074639,0621,0030,51C0,A2B3,00,0.0,20150813074641,A7E6$"));

        verifyPosition(decoder, text(
                "+RESP:GTOBD,1F0109,864251020135483,4T1BE46KX7U018210,gv500,0,78FFFF,4T1BE46KX7U018210,1,13411,981B81C0,787,3,43,,921,463,1,10,0300030103030304001200310351035203530354,20,55,,1286,0,6.5,74,21.6,3.379710,6.529714,20150813074824,0621,0030,51C0,A2B3,00,0.0,20150813074828,A7E9$"));

        verifyPosition(decoder, text(
                "+RESP:GTSTT,1A0401,860599000508846,,41,0,0.0,84,107.5,-76.657998,39.497203,20150623160622,0310,0260,B435,3B81,,20150623160622,0F54$"));

        verifyPosition(decoder, text(
                "+RESP:GTFRI,1A0401,860599000508846,,0,0,1,1,134.8,154,278.7,-76.671089,39.778885,20150623154301,0310,0260,043F,7761,,99,20150623154314,0F24$"));

        verifyPosition(decoder, text(
                "+RESP:GTFRI,1A0200,860599000165464,CRI001,0,0,1,2,,41,,-71.153137,42.301634,20150328020301,,,,,280.3,55,20150327220351,320C"));
        
        verifyPosition(decoder, text(
                "+RESP:GTFRI,02010D,867844001675407,,0,0,1,2,0.0,0,28.9,8.591011,56.476397,20140915213209,0238,0001,03CB,2871,,97,20140915213459,009A"));

        verifyNothing(decoder, text(
                "+RESP:GTINF,359464030073766,8938003990320469804f,18,99,100,1,0,+2.00,0,20131018084015,00EE,0103090402"));

        verifyPosition(decoder, text(
                "+RESP:GTFRI,04040C,359231038939904,,,10,1,2,0.0,117,346.0,8.924243,50.798077,20130618122040,0262,0002,0299,109C,00,0.0,,,,,,,,,20130618122045,00F6"));
        
        verifyPosition(decoder, text(
                "+RESP:GTSTT,04040C,359231038939904,,42,0,0.0,117,346.0,8.924243,50.798077,20130618125152,0262,0002,0299,109C,00,20130618125154,017A"));
        
        verifyPosition(decoder, text(
                "+RESP:GTFRI,020102,000035988863964,,0,0,1,1,4.3,92,70.0,121.354335,31.222073,20090214013254,0460,0000,18d8,6141,00,,20090214093254,11F0"));

        verifyPosition(decoder, text(
                "+RESP:GTFRI,020102,135790246811220,,0,0,1,1,4.3,92,70.0,121.354335,31.222073,20090214013254,0460,0000,18d8,6141,00,,20090214093254,11F0"));

        verifyPosition(decoder, text(
                "+RESP:GTFRI,020102,135790246811220,,0,0,2,1,4.3,92,70.0,121.354335,31.222073,20090214013254,0460,0000,18d8,6141,00,0,4.3,92,70.0,121.354335,31.222073,20090101000000,0460,0000,18d8,6141,00,,20090214093254,11F0"));

        verifyPosition(decoder, text(
                "+RESP:GTDOG,020102,135790246811220,,0,0,1,1,4.3,92,70.0,121.354335,31.222073,20090214013254,0460,0000,18d8,6141,00,2000.0,20090214093254,11F0"));

        verifyPosition(decoder, text(
                "+RESP:GTLBC,020102,135790246811220,,+8613800000000,1,4.3,92,70.0,121.354335,31.222073,20090214013254,0460,0000,18d8,6141,00,,20090214093254,11F0"));

        verifyPosition(decoder, text(
                "+RESP:GTGCR,020102,135790246811220,,3,50,180,2,0.4,296,-5.4,121.391055,31.164473,20100714104934,0460,0000,1878,0873,00,,20100714104934,000C"));

        verifyPosition(decoder, text(
                "+RESP:GTFRI,07000D,868487001005941,,0,0,1,1,0.0,0,46.3,-77.039627,38.907573,20120731175232,0310,0260,B44B,EBC9,0015e96913a7,-58,,100,20120731175244,0114"));
        
        verifyPosition(decoder, text(
                "+RESP:GTTOW,0F0100,135790246811220,,,10,1,1,4.3,92,70.0,121.354335,31.222073,20090214013254,0460,0000,18d8,6141,00,2000.0,20090214093254,11F0$"));

        verifyPosition(decoder, text(
                "+RESP:GTDIS,0F0100,135790246811220,,,20,1,1,4.3,92,70.0,121.354335,31.222073,20090214013254,0460,0000,18d8,6141,00,2000.0,20090214093254,11F0$"));

        verifyPosition(decoder, text(
                "+RESP:GTIOB,0F0100,135790246811220,,,10,1,1,4.3,92,70.0,121.354335,31.222073,20090214013254,0460,0000,18d8,6141,00,2000.0,20090214093254,11F0$"));

        verifyPosition(decoder, text(
                "+RESP:GTGEO,0F0100,135790246811220,,,00,1,1,4.3,92,70.0,121.354335,31.222073,20090214013254,0460,0000,18d8,6141,00,2000.0,20090214093254,11F0$"));

        verifyPosition(decoder, text(
                "+RESP:GTSPD,0F0100,135790246811220,,,00,1,1,4.3,92,70.0,121.354335,31.222073,20090214013254,0460,0000,18d8,6141,00,2000.0,20090214093254,11F0$"));

        verifyPosition(decoder, text(
                "+RESP:GTSOS,0F0100,135790246811220,,,00,1,1,4.3,92,70.0,121.354335,31.222073,20090214013254,0460,0000,18d8,6141,00,2000.0,20090214093254,11F0$"));

        verifyPosition(decoder, text(
                "+RESP:GTRTL,0F0100,135790246811220,,,00,1,1,4.3,92,70.0,121.354335,31.222073,20090214013254,0460,0000,18d8,6141,00,2000.0,20090214093254,11F0$"));

        verifyPosition(decoder, text(
                "+RESP:GTDOG,0F0100,135790246811220,,,01,1,1,4.3,92,70.0,121.354335,31.222073,20090214013254,0460,0000,18d8,6141,00,2000.0,20090214093254,11F0$"));

        verifyPosition(decoder, text(
                "+RESP:GTIGL,0F0100,135790246811220,,,00,1,1,4.3,92,70.0,121.354335,31.222073,20090214013254,0460,0000,18d8,6141,00,2000.0,20090214093254,11F0$"));

        verifyPosition(decoder, text(
                "+RESP:GTHBM,0F0100,135790246811220,,,10,1,1,4.3,92,70.0,121.354335,31.222073,20090214013254,0460,0000,18d8,6141,00,2000.0,20090214093254,11F0$"));

        verifyPosition(decoder, text(
                "+RESP:GTHBM,0F0100,135790246811220,,,11,1,1,24.3,92,70.0,121.354335,31.222073,20090214013254,0460,0000,18d8,6141,00,2000.0,20090214093254,11F0$"));
        
        verifyPosition(decoder, text(
                "+RESP:GTFRI,02010C,867844001274144,,0,0,1,1,18.0,233,118.1,7.615551,51.515600,20140106130516,0262,0007,79E6,B956,,72,20140106140524,09CE$"));
        
        verifyPosition(decoder, text(
                "+RESP:GTFRI,02010C,867844001274649,,0,0,1,1,0.0,0,122.5,7.684216,51.524512,20140106233722,0262,0007,79EE,1D22,,93,20140107003805,03C4$"));

        verifyPosition(decoder, text(
                "+BUFF:GTFRI,210101,863286020016706,,,10,1,1,,,,49.903915,40.391669,20140818105815,,,,,,,,,,,210100,,,,,000C$"));

        verifyPosition(decoder, text(
                "+RESP:GTFRI,240100,135790246811220,,,10,1,1,4.3,92,70.0,121.354335,31.222073,20090214013254,0460,0000,18d8,6141,00,2000.0,12345:12:34,,80,,,,,,20090214093254,11F0$"));

        verifyPosition(decoder, text(
                "+RESP:GTFRI,240100,135790246811220,,,10,2,1,4.3,92,70.0,121.354335,31.222073,20090214013254,0460,0000,18d8,6141,00,0,4.3,92,70.0,121.354335,31.222073,20090101000000,0460,0000,18d8,6141,00,2000.0,12345:12:34,,,80,,,,,,20090214093254,11F0$"));

        verifyPosition(decoder, text(
                "\u0000\u0004,005F,0,GTFRI,020100,135790246811220,,0,0,1,1,4.3,92,70.0,121.354335,31.222073,20090214013254,0460,0000,18d8,6141,90,20090214093254,11F0$"));
        
        verifyPosition(decoder, text(
                "\u0000\u0004,005F,0,GTGEO,020100,135790246811220,,0,0,1,1,4.3,92,70.0,121.354335,31.222073,20090214013254,0460,0000,18d8,6141,90,20090214093254,11F0$"));

        verifyPosition(decoder, text(
                "\u0000\u0004,005F,0,GTNMR,020100,135790246811220,,0,0,1,1,4.3,92,70.0,121.354335,31.222073,20090214013254,0460,0000,18d8,6141,90,20090214093254,11F0$"));

        verifyPosition(decoder, text(
                "\u0000\u0004,0017,0,GTNMR,,867844000400914,,0,41,1,2,0.0,0,1504.2,-75.569202,6.242850,20150404162835,,,,,97,20150404162836,05EF$"));

        verifyNothing(decoder, text(
                "\u0000\u0004,0017,0,GTPNA,,867844000400914,,0,0,1,0,,,,0,0,,,,,,99,20150404190153,0601$"));

        verifyPosition(decoder, text(
                "\u0000\u0004,0017,0,GTEPN,,867844000400914,,0,0,1,0,0.0,0,1717.4,-75.598445,6.278578,20150405003116,,,,,95,20150405003358,0607$"));

        verifyPosition(decoder, text(
                "+RESP:GTSTT,280100,A1000043D20139,,42,0,0.1,321,228.6,-76.660884,39.832552,20150615120628,0310,0484,00600019,0A52,,20150615085741,0320$"));

        verifyPosition(decoder, text(
                "+RESP:GTRTL,280100,A1000043D20139,,0,0,1,1,0.1,321,239.1,-76.661047,39.832501,20150615114455,0310,0484,00600019,0A52,,87,20150615074456,031E$"));

        verifyNothing(decoder, text(
                "+ACK:GTHBD,1A0401,135790246811220,,20100214093254,11F0"));

    }

}
