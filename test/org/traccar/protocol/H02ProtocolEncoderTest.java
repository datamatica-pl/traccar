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
public class H02ProtocolEncoderTest extends ProtocolTest {

    @Test
    public void testEncode() throws Exception {

        H02ProtocolEncoder encoder = new H02ProtocolEncoder();

        Command command = new Command();
        command.setDeviceId(1);

        command.setType(Command.TYPE_GET_PARAMS);
        verifyStringCommand(encoder, command, "param2");
        
        command.setType(Command.TYPE_GET_STATUS);
        verifyStringCommand(encoder, command, "status");
        
        command.setType(Command.TYPE_AUTO_ALARM_ARM);
        verifyStringCommand(encoder, command, "auto1");
        
        command.setType(Command.TYPE_AUTO_ALARM_DISARM);
        verifyStringCommand(encoder, command, "auto0");
        
        command.setType(Command.TYPE_FACTORY_SETTINGS);
        verifyStringCommand(encoder, command, "format123456");
        
        command.setType(Command.TYPE_REBOOT_DEVICE);
        verifyStringCommand(encoder, command, "reset123456");
        
        command.setType(Command.TYPE_POSITION_PERIODIC);
        command.set(Command.KEY_FREQUENCY, 90);
        verifyStringCommand(encoder, command, "run,90");
        
        command.setType(Command.TYPE_SET_CENTER_NUMBER);
        command.set(Command.KEY_CENTER_NUMBER, 499001001);
        verifyStringCommand(encoder, command, "adm123456,499001001");
        
        command.setType(Command.TYPE_SET_SOS_NUMBERS);
        
        command.set(Command.KEY_SOS_NUMBER_1, 499001001);
        verifyStringCommand(encoder, command, "sos,499001001,");
        
        command.set(Command.KEY_SOS_NUMBER_1, 499002002);
        command.set(Command.KEY_SOS_NUMBER_2, 499003003);
        verifyStringCommand(encoder, command, "sos,499002002,499003003");
    }
}
