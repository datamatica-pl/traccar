/*
 * Copyright 2015 Anton Tananaev (anton.tananaev@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.traccar.protocol;

import org.junit.Test;
import org.traccar.ProtocolTest;
import org.traccar.model.Command;

/**
 *
 * @author Jan Usarek
 */
public class CastelProtocolEncoderTest extends ProtocolTest {

    @Test
    public void testEncode() throws Exception {

        CastelProtocolEncoder encoder = new CastelProtocolEncoder();

        Command command = new Command();
        command.setDeviceId(1);
        

        command.setType(Command.TYPE_GET_PARAMS);
        verifyCommand(encoder, command, binary(""
                + "4040"                                        // Protocol head
                + "2800"                                        // Length
                + "05"                                          // Protocol version
                + "3132333435363738393031323334350000000000"    // Device serial number
                + "5201"                                        // Protocol command type
                    // protocol data section
                    + "0000"                                    // Sequence number, we don't use it so its always 0x0000
                    + "03"                                      // Number of set TLV, 3
                    + "0110"                                    // Get authorized numbers param
                    + "0850"                                    // Get sms center number param
                    + "0140"                                    // Get updates interval param
                    // end: protocol data section
                + "6474"                                        // Checksum
                + "0d0a"                                        // Protocol head
        ));
        
        command.setType(Command.TYPE_SET_SOS_NUMBERS);

        command.set(Command.KEY_SOS_NUMBER_1, 111222333);
        verifyCommand(encoder, command, binary(""
                + "4040"                                        // Protocol head
                + "6800"                                        // Length
                + "05"                                          // Protocol version
                + "3132333435363738393031323334350000000000"    // Device serial number
                + "5101"                                        // Command type 5101 represents it is parameters setting command
                    // Protocol data section starts here
                    + "0000"                                        // Sequence number, we don't use it so its always 0x0000
                    + "01"                                          // number of set TLV, 1
                    + "0110"                                        // Code for setting SOS numbers
                    + "4200"                                        // Length of SOS numbers section - 66
                    + "00"                                          // Order num of first SOS number
                    + "313131323232333333000000000000000000000000"  // First SOS number
                    + "01"                                          // Order num of second SOS number
                    + "000000000000000000000000000000000000000000"  // Second SOS number
                    + "02"                                          // Order num of third SOS number
                    + "000000000000000000000000000000000000000000"  // Third SOS number
                    // Protocol data section ends here
                + "54b9"                                        // Checsum
                + "0d0a"                                        // Protocol tail
        ));

        command.set(Command.KEY_SOS_NUMBER_1, 111222333);
        command.set(Command.KEY_SOS_NUMBER_2, 111222444);
        verifyCommand(encoder, command, binary(""
                + "4040"
                + "6800"
                + "05"
                + "3132333435363738393031323334350000000000"
                + "5101"
                    + "0000"
                    + "01"
                    + "0110"
                    + "4200"
                    + "00"
                    + "313131323232333333000000000000000000000000"
                    + "01"
                    + "313131323232343434000000000000000000000000"
                    + "02"
                    + "000000000000000000000000000000000000000000"
                + "781a"
                + "0d0a"
        ));
        
        command.setType(Command.TYPE_POSITION_PERIODIC);
        command.set(Command.KEY_FREQUENCY, 30);
        verifyCommand(encoder, command, binary(""
                + "4040"
                + "2800"
                + "05"
                + "3132333435363738393031323334350000000000"
                + "5101"
                + "0000"
                    + "01"
                    + "0140"            // Set interval tag
                    + "0200"            // Length of interval value below
                    + "1e00"            // Interval, 30 seconds
                + "1ec3"
                + "0d0a"
        ));
        
        command.setType(Command.TYPE_SET_CENTER_NUMBER);
        
        // Delete SMS center number, potential problems may occur here, because I assmume that command tag
        // "0850 0000" will delete the center number. Unfortunately documentation does not tell it explicitly
        // it just gives an example of request with "0850 0000", comment it as "set center number" and does
        // not provide any sample number. When I ask device about parameters after delete, it looks like it has
        // center number set to "00" and length to "0100" which is in conflict in other part of documentation.
        // Further verification of this test, as well as delete center number method may be required.
        command.set(Command.KEY_CENTER_NUMBER, "");
        verifyCommand(encoder, command, binary(""
                + "4040"
                + "2600"
                + "05"
                + "3132333435363738393031323334350000000000"
                + "5101"
                    + "0000"
                    + "01"
                    + "0850"
                    + "0000"            // Probably length of center number "0x0000". Should delete SMS center number.
                + "7395"
                + "0d0a"
        ));
        
        command.set(Command.KEY_CENTER_NUMBER, 111222333);
        verifyCommand(encoder, command, binary(""
                + "4040"
                + "2f00"
                + "05"
                + "3132333435363738393031323334350000000000"
                + "5101"
                    + "0000"
                    + "01"
                    + "0850"
                    + "0900"
                    + "313131323232333333"      // Center number
                + "7ec1"
                + "0d0a"
        ));
    }
}
