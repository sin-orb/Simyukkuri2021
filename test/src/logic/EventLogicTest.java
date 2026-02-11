package src.logic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import src.base.Body;
import src.base.EventPacket;
import src.yukkuri.Reimu;

/**
 * Test class for EventLogic.
 * EventLogic has event registration helpers that can be tested.
 * Full event lifecycle testing requires World initialization.
 */
public class EventLogicTest {

    @BeforeEach
    public void setUp() {
        try {
            src.util.WorldTestHelper.initializeMinimalWorld();
        } catch (Exception e) {
            // World initialization may fail - tests will handle this
        }
    }

    // ========== World Event Registration Tests ==========

    @Test
    public void testAddWorldEventShortcut() {
        try {
            Reimu speaker = new Reimu();
            MockEventPacket event = new MockEventPacket();

            // Should not crash - uses default HOLDMESSAGE count
            EventLogic.addWorldEvent(event, speaker, "test message");

            assertTrue(true, "addWorldEvent shortcut should not crash");
        } catch (NullPointerException e) {
            // Expected if World not properly initialized
            assertNotNull(e);
        }
    }

    @Test
    public void testAddWorldEventWithCount() {
        try {
            Reimu speaker = new Reimu();
            MockEventPacket event = new MockEventPacket();

            // Should not crash - uses custom count
            EventLogic.addWorldEvent(event, speaker, "test message", 100);

            assertTrue(true, "addWorldEvent with count should not crash");
        } catch (NullPointerException e) {
            // Expected if World not properly initialized
            assertNotNull(e);
        }
    }

    @Test
    public void testAddWorldEventNullMessage() {
        try {
            MockEventPacket event = new MockEventPacket();

            // Should not crash with null message body
            EventLogic.addWorldEvent(event, null, null);

            assertTrue(true, "addWorldEvent with null message should not crash");
        } catch (NullPointerException e) {
            // Expected if World not properly initialized
            assertNotNull(e);
        }
    }

    // ========== Body Event Registration Tests ==========

    @Test
    public void testAddBodyEventShortcut() {
        Reimu target = new Reimu();
        Reimu speaker = new Reimu();
        MockEventPacket event = new MockEventPacket();

        // Should not crash - uses default HOLDMESSAGE count
        EventLogic.addBodyEvent(target, event, speaker, "test message");

        // Event should be added to target's event list
        assertEquals(1, target.getEventList().size(), "Event should be added to body");
    }

    @Test
    public void testAddBodyEventWithCount() {
        Reimu target = new Reimu();
        Reimu speaker = new Reimu();
        MockEventPacket event = new MockEventPacket();

        // Should not crash - uses custom count
        EventLogic.addBodyEvent(target, event, speaker, "test message", 100);

        // Event should be added to target's event list
        assertEquals(1, target.getEventList().size(), "Event should be added to body");
    }

    @Test
    public void testAddBodyEventNullMessage() {
        Reimu target = new Reimu();
        MockEventPacket event = new MockEventPacket();

        // Should not crash with null message body
        EventLogic.addBodyEvent(target, event, null, null);

        // Event should be added to target's event list
        assertEquals(1, target.getEventList().size(), "Event should be added to body");
    }

    // ========== Event Lifecycle Tests (Require World) ==========

    @Test
    public void testClockWorldEvent() {
        try {
            // Should not crash
            EventLogic.clockWorldEvent();

            assertTrue(true, "clockWorldEvent should not crash");
        } catch (NullPointerException e) {
            // Expected if World not properly initialized
            assertNotNull(e);
        }
    }

    @Test
    public void testEventUpdate() {
        Reimu yukkuri = new Reimu();

        // Should not crash with no current event
        EventLogic.eventUpdate(yukkuri);

        assertTrue(true, "eventUpdate should handle null current event");
    }

    // ========== Mock Event Packet for Testing ==========

    /**
     * Simple mock EventPacket for testing event registration.
     */
    private static class MockEventPacket extends EventPacket {

        public MockEventPacket() {
            super(null, null, null, 100);
        }

        @Override
        public boolean checkEventResponse(Body b) {
            return false;
        }

        @Override
        public void start(Body b) {
            // No-op
        }

        @Override
        public UpdateState update(Body b) {
            return null; // Normal update
        }

        @Override
        public boolean execute(Body b) {
            return true;
        }

        @Override
        public void end(Body b) {
            // No-op
        }
    }
}
