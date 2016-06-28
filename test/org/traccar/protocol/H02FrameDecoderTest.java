package org.traccar.protocol;

import org.jboss.netty.buffer.ChannelBuffer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.traccar.ProtocolTest;

public class H02FrameDecoderTest extends ProtocolTest {

    private H02FrameDecoder decoder;
    
    private class Message {
        String hexContent;
        
        public Message(String hexContent) {
            this.hexContent = hexContent;
        }
        
        public String inputHex() {
            return hexContent;
        }
        
        public String outputHex() {
            return hexContent;
        }
    }
    
    private class StringMessage extends Message {
        public StringMessage(String content) {
            super(stringToHex(content));
        }
    }
    
    private class Position24 extends Message {
        private static final String TAIL = "00000000000000000000000000";
        String tail;
        
        public Position24(String hexContent) {
            this(hexContent, TAIL);
        }
        public Position24(String hexContent, String tail) {
            super(hexContent);
            this.tail = tail;
        }

        @Override
        public String inputHex() {
            return super.inputHex() + tail;
        }
    }
    
    @Before
    public void testInit() {
        decoder = new H02FrameDecoder();
    }
    
    @Test
    public void position2a() throws Exception {
        verifySequence(new Message("2a48512c3335333538383036303031353536382c56312c3139333530352c412c3830392e303031302c532c333435342e383939372c572c302e30302c302e30302c3239313031332c65666666666266662c3030303264342c3030303030622c3030353338352c3030353261612c323523"));
    }
    
    @Test
    public void position24() throws Exception {
        verifySequence(new Position24("24430025645511183817091319355128000465632432000100ffe7fbffff0000"));
    }
    
    @Test
    public void justLetters() throws Exception {
        verifySequence(new StringMessage("OK!"));
    }
    
    @Test
    public void lettersThen2a() throws Exception {
        verifySequence(new StringMessage("OK! Over speed alarm: 110 km/h"),
                new Message("2a48512c3335333538383036303031353536382c56312c3139333530352c412c3830392e303031302c532c333435342e383939372c572c302e30302c302e30302c3239313031332c65666666666266662c3030303264342c3030303030622c3030353338352c3030353261612c323523"));
    }
    
    @Test
    public void lettersThen24() throws Exception {
        verifySequence(new StringMessage("OK! Over speed alarm: off"), 
                new Position24("24430025645511183817091319355128000465632432000100ffe7fbffff0000"));
    }
    
    @Test
    public void lettersThen24And2a() throws Exception {
        verifySequence(new StringMessage("Lorem ipsum dolor sit amet."),
                new Position24("24430025645511183817091319355128000465632432000100ffe7fbffff0000"),
                new Message("2a48512c3335333538383036303031353536382c56312c3139333530352c412c3830392e303031302c532c333435342e383939372c572c302e30302c302e30302c3239313031332c65666666666266662c3030303264342c3030303030622c3030353338352c3030353261612c323523"));
    }
    
    @Test
    public void position24_consumingTail() throws Exception {
        verifySequence(new Position24("24430025645511183817091319355128000465632432000100ffe7fbffff0000", stringToHex("asdfghjklqwer")),
                new StringMessage("Fail!"));
    }
    
    private void verifySequence(Message... messages) throws Exception {
        ChannelBuffer buffer = binary(messages);
        for(Message message : messages) {
            Assert.assertEquals(binary(message.outputHex()), decoder.decode(null, null, buffer));
        }
    }
    
    private ChannelBuffer binary(Message... messages) {
        String[] hex = new String[messages.length];
        for(int i=0;i < messages.length; ++i)
            hex[i] = messages[i].inputHex();
        return binary(hex);
    }
}
