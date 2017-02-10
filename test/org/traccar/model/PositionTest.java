package org.traccar.model;

import java.text.SimpleDateFormat;
import java.time.Clock;
import java.time.ZoneId;
import java.util.Date;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Jan Usarek
 */
public class PositionTest {
    
    private SimpleDateFormat df;
    private Clock testClock;
    
    @Before
    public void initialize() throws Exception {
        String testDay = "2017-02-10 00:00:00";
        df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        testClock = Clock.fixed( df.parse( testDay ).toInstant(), ZoneId.systemDefault() );
    }
    
    @Test
    public void testLastValidTimeInFuture() throws Exception {
        Position position = new Position();
        position.setFixTime(new Date( df.parse("2017-02-10 14:00:00").getTime() ));
        
        assertTrue(position.isTimeInAllowedRange(testClock));
    }
    
    @Test
    public void testInvalidTimeInFuture() throws Exception {
        Position position = new Position();
        position.setFixTime(new Date( df.parse("2017-02-10 14:00:01").getTime() ));
        
        assertFalse(position.isTimeInAllowedRange(testClock));
    }
    
    @Test
    public void testLastValidTimeInPast() throws Exception {
        Position position = new Position();
        position.setFixTime(new Date( df.parse("2017-01-11 00:00:00").getTime() ));
        
        assertTrue(position.isTimeInAllowedRange(testClock));
    }
    
    @Test
    public void testInvalidTimeInPast() throws Exception {
        Position position = new Position();
        position.setFixTime(new Date( df.parse("2017-01-10 23:59:59").getTime() ));
        
        assertFalse(position.isTimeInAllowedRange(testClock));
    }
    
    @Test
    public void testIsAlarmAlarmTrue() throws Exception {
        Position position = new Position();
        position.set("alarm", true);
        assertTrue(position.isAlarm());
    }
    
    @Test
    public void testIsAlarmAlarmFalse() throws Exception {
        Position position = new Position();
        position.set("alarm", false);
        assertFalse(position.isAlarm());
    }
    
    @Test
    public void testIsAlarmAlarmNull() throws Exception {
        Position position = new Position();
        assertFalse(position.isAlarm());
    }
    
    @Test
    public void testValidStatusCorrect() throws Exception {
        Position position = new Position();
        position.setLatitude(52.00);
        position.setLongitude(21.00);
        position.set("alarm", false);
        position.setFixTime(new Date( df.parse("2017-02-10 00:01:00").getTime() ));
        position.checkAndSetValidStatus(testClock);
        
        assertEquals((Integer)Position.VALID_STATUS_CORRECT_POSITION, (Integer)position.getValidStatus());
    }
    
    @Test
    public void testValidStatusAlarm() throws Exception {
        Position position = new Position();
        position.setLatitude(52.00);
        position.setLongitude(21.00);
        position.set("alarm", true);
        position.setFixTime(new Date( df.parse("2017-02-10 00:01:00").getTime() ));
        position.checkAndSetValidStatus(testClock);
        
        assertEquals((Integer)Position.VALID_STATUS_ALARM, (Integer)position.getValidStatus());
    }
    
    @Test
    public void testValidStatusTimeOutOfRange() throws Exception {
        Position position = new Position();
        position.setLatitude(52.00);
        position.setLongitude(21.00);
        position.set("alarm", false);
        position.setFixTime(new Date( df.parse("2017-02-10 14:01:00").getTime() ));
        position.checkAndSetValidStatus(testClock);
        
        assertEquals((Integer)Position.VALID_STATUS_TIME_OUT_OF_RANGE, (Integer)position.getValidStatus());
    }
    
    @Test
    public void testValidStatusAlarmAndTimeOutOfRange() throws Exception {
        Position position = new Position();
        position.setLatitude(52.00);
        position.setLongitude(21.00);
        position.set("alarm", true);
        position.setFixTime(new Date( df.parse("2017-01-10 23:59:59").getTime() ));
        position.checkAndSetValidStatus(testClock);
        
        assertEquals((Integer)Position.VALID_STATUS_ALARM_AND_TIME_OUT_OF_RANGE, (Integer)position.getValidStatus());
    }
}
