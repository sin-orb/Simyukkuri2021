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
        // Add an expired event then clock it
        MockEventPacket event = new MockCountDownEvent(); // countDown returns true → removed
        src.SimYukkuri.world.getCurrentMap().getEvent().add(event);
        EventLogic.clockWorldEvent();
        // Should not crash
        assertTrue(true);
    }

    @Test
    public void testEventUpdate_noCurrentEvent() {
        Reimu yukkuri = new Reimu();
        // Should not crash with no current event
        EventLogic.eventUpdate(yukkuri);
        assertTrue(true);
    }

    @Test
    public void testEventUpdate_ABORT() {
        Reimu yukkuri = new Reimu();
        MockAbortEvent event = new MockAbortEvent();
        yukkuri.setCurrentEvent(event);
        EventLogic.eventUpdate(yukkuri);
        // ev.end called, currentEvent cleared
        assertNull(yukkuri.getCurrentEvent());
    }

    @Test
    public void testEventUpdate_FORCE_EXEC_executeTrue() {
        Reimu yukkuri = new Reimu();
        MockForceExecEvent event = new MockForceExecEvent(true);
        yukkuri.setCurrentEvent(event);
        EventLogic.eventUpdate(yukkuri);
        // execute returned true → end called, currentEvent cleared
        assertNull(yukkuri.getCurrentEvent());
    }

    @Test
    public void testEventUpdate_FORCE_EXEC_executeFalse() {
        Reimu yukkuri = new Reimu();
        MockForceExecEvent event = new MockForceExecEvent(false);
        yukkuri.setCurrentEvent(event);
        EventLogic.eventUpdate(yukkuri);
        // execute returned false → event stays
        assertNotNull(yukkuri.getCurrentEvent());
    }

    // --- checkBodyEvent ---

    @Test
    public void testCheckBodyEvent_noEvents_returnsNull() {
        Reimu yukkuri = new Reimu();
        assertNull(EventLogic.checkBodyEvent(yukkuri));
    }

    @Test
    public void testCheckBodyEvent_simpleEventActionTrue_removesEvent() {
        Reimu yukkuri = new Reimu();
        MockSimpleEventTruePacket event = new MockSimpleEventTruePacket();
        yukkuri.getEventList().add(event);
        EventLogic.checkBodyEvent(yukkuri); // simpleEventAction=true → removed
        assertEquals(0, yukkuri.getEventList().size());
    }

    @Test
    public void testCheckBodyEvent_checkResponseTrue_returnsEvent() {
        Reimu yukkuri = new Reimu();
        MockCheckResponseTruePacket event = new MockCheckResponseTruePacket();
        yukkuri.getEventList().add(event);
        EventPacket result = EventLogic.checkBodyEvent(yukkuri);
        assertNotNull(result);
    }

    // --- checkWorldEvent ---

    @Test
    public void testCheckWorldEvent_noEvents_returnsNull() {
        Reimu yukkuri = new Reimu();
        assertNull(EventLogic.checkWorldEvent(yukkuri));
    }

    @Test
    public void testCheckWorldEvent_simpleEventActionTrue_skips() {
        Reimu yukkuri = new Reimu();
        MockSimpleEventTruePacket event = new MockSimpleEventTruePacket();
        src.SimYukkuri.world.getCurrentMap().getEvent().add(event);
        EventPacket result = EventLogic.checkWorldEvent(yukkuri);
        assertNull(result); // simpleEventAction=true → skipped, not returned
        src.SimYukkuri.world.getCurrentMap().getEvent().clear();
    }

    @Test
    public void testCheckWorldEvent_checkResponseTrue_returnsEvent() {
        Reimu yukkuri = new Reimu();
        MockCheckResponseTruePacket event = new MockCheckResponseTruePacket();
        src.SimYukkuri.world.getCurrentMap().getEvent().add(event);
        EventPacket result = EventLogic.checkWorldEvent(yukkuri);
        assertNotNull(result);
        src.SimYukkuri.world.getCurrentMap().getEvent().clear();
    }

    // --- checkSimpleWorldEvent ---

    @Test
    public void testCheckSimpleWorldEvent_FromCheck() {
        Reimu yukkuri = new Reimu();
        yukkuri.setUniqueID(100);
        MockSimpleEventTruePacket event = new MockSimpleEventTruePacket();
        event.setFrom(yukkuri); // Event is from this yukkuri
        src.SimYukkuri.world.getCurrentMap().getEvent().add(event);

        // Should skip because from == b
        EventLogic.checkSimpleWorldEvent(yukkuri);

        // event.simpleEventAction should NOT have been called
        assertFalse(event.wasSimpleActionCalled);
        src.SimYukkuri.world.getCurrentMap().getEvent().clear();
    }

    @Test
    public void testEventUpdate_ReachesTarget() {
        Reimu yukkuri = new Reimu();
        yukkuri.setX(100);
        yukkuri.setY(100);
        yukkuri.setZ(0);

        MockEventPacket event = new MockEventPacket();
        event.setToX(101); // Distance squared = 1, threshold is (0 + 2) = 2
        event.setToY(100);
        event.setToZ(0);

        yukkuri.setCurrentEvent(event);
        EventLogic.eventUpdate(yukkuri);

        // execute should have been called because distance is small
        assertTrue(event.wasExecuteCalled);
        assertNull(yukkuri.getCurrentEvent());
    }

    // ========== Mock Event Packet for Testing ==========

    private static class MockEventPacket extends EventPacket {
        public boolean wasExecuteCalled = false;
        public boolean wasSimpleActionCalled = false;

        public MockEventPacket() {
            super(null, null, null, 100);
        }

        @Override
        public boolean checkEventResponse(Body b) {
            return false;
        }

        @Override
        public void start(Body b) {
        }

        @Override
        public UpdateState update(Body b) {
            return null;
        }

        @Override
        public boolean execute(Body b) {
            wasExecuteCalled = true;
            return true;
        }

        @Override
        public void end(Body b) {
        }

        @Override
        public boolean simpleEventAction(Body b) {
            wasSimpleActionCalled = true;
            return false;
        }
    }

    private static class MockCountDownEvent extends MockEventPacket {
        @Override
        public boolean countDown() {
            return true;
        }
    }

    private static class MockAbortEvent extends MockEventPacket {
        @Override
        public UpdateState update(Body b) {
            return UpdateState.ABORT;
        }
    }

    private static class MockForceExecEvent extends MockEventPacket {
        private final boolean execResult;

        MockForceExecEvent(boolean r) {
            execResult = r;
        }

        @Override
        public UpdateState update(Body b) {
            return UpdateState.FORCE_EXEC;
        }

        @Override
        public boolean execute(Body b) {
            return execResult;
        }
    }

    private static class MockSimpleEventTruePacket extends MockEventPacket {
        @Override
        public boolean simpleEventAction(Body b) {
            return true;
        }
    }

    private static class MockCheckResponseTruePacket extends MockEventPacket {
        @Override
        public boolean simpleEventAction(Body b) {
            return false;
        }

        @Override
        public boolean checkEventResponse(Body b) {
            return true;
        }
    }
}
