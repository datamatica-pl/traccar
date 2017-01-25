package org.traccar;

import java.util.Date;
import org.junit.After;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import org.junit.Before;
import org.junit.Test;
import org.traccar.model.Position;

public class FilterHandlerTest {

    private FilterHandler filtingHandler;
    private FilterHandler passingHandler;

    @Before
    public void setUp() {
        filtingHandler = new FilterHandler(true, true, true, true, true, 10, 10);
        passingHandler = new FilterHandler(false, false, false, false, false, 0, 0);
    }

    @After
    public void tearDown() {
        filtingHandler = null;
        passingHandler = null;
    }

    private Position createPosition(
            long deviceId,
            Date time,
            boolean valid,
            double latitude,
            double longitude,
            double altitude,
            double speed,
            double course) {

        Position p = new Position();
        p.setDeviceId(deviceId);
        p.setTime(time);
        p.setValid(valid);
        p.setLatitude(latitude);
        p.setLongitude(longitude);
        p.setAltitude(altitude);
        p.setSpeed(speed);
        p.setCourse(course);
        return p;
    }

    @Test
    public void testFilterInvalid() throws Exception {

        Position position = createPosition(0, new Date(), true, 10, 10, 10, 10, 10);

        assertNotNull(filtingHandler.decode(null, null, position));
        assertNotNull(passingHandler.decode(null, null, position));

        position = createPosition(0, new Date(Long.MAX_VALUE), true, 10, 10, 10, 10, 10);

        assertNull(filtingHandler.decode(null, null, position));
        assertNotNull(passingHandler.decode(null, null, position));

        position = createPosition(0, new Date(), false, 10, 10, 10, 10, 10);

        assertNull(filtingHandler.decode(null, null, position));
        assertNotNull(passingHandler.decode(null, null, position));
    }

    @Test
    public void testFilterIncorrectCoordinates() throws Exception {
        final double max_longitude = 180.00;
        final double min_longitude = -180.00;
        final double too_big_longitude = 180.01;
        final double too_small_longitude = -180.01;
        final double max_latitude = 90.00;
        final double min_latitude = -90.00;
        final double too_big_latitude = 90.01;
        final double too_small_latitude = -90.01;
        final double warsaw_latitude = 52.228766;
        final double warsaw_longitude = 21.0033086;

        Position position;

        // Both filting and passing filters should behave the same in this test, because filtering
        // of position with incorrect coordinates is always turned on.

        position = createPosition(0, new Date(), true, too_big_latitude, max_longitude, 10, 10, 10);
        assertNull(filtingHandler.decode(null, null, position));
        assertNull(passingHandler.decode(null, null, position));

        position = createPosition(0, new Date(), true, too_small_latitude, min_longitude, 10, 10, 10);
        assertNull(filtingHandler.decode(null, null, position));
        assertNull(passingHandler.decode(null, null, position));

        position = createPosition(0, new Date(), true, max_latitude, too_big_longitude, 10, 10, 10);
        assertNull(filtingHandler.decode(null, null, position));
        assertNull(passingHandler.decode(null, null, position));

        position = createPosition(0, new Date(), true, min_latitude, too_small_longitude, 10, 10, 10);
        assertNull(filtingHandler.decode(null, null, position));
        assertNull(passingHandler.decode(null, null, position));

        position = createPosition(0, new Date(), true, warsaw_latitude, warsaw_longitude, 10, 10, 10);
        assertNotNull(filtingHandler.decode(null, null, position));
        assertNotNull(passingHandler.decode(null, null, position));

        position = createPosition(0, new Date(), true, max_latitude, max_longitude, 10, 10, 10);
        assertNotNull(filtingHandler.decode(null, null, position));
        assertNotNull(passingHandler.decode(null, null, position));

        position = createPosition(0, new Date(), true, min_latitude, min_longitude, 10, 10, 10);
        assertNotNull(filtingHandler.decode(null, null, position));
        assertNotNull(passingHandler.decode(null, null, position));
    }
}
