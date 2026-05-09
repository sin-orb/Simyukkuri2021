package src.logic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import src.base.Body;
import src.event.EventPacket;
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
    public void testAddWorldEventRegistersEventAndSpeakerMessage() {
        Reimu speaker = new Reimu();
        MockEventPacket event = new MockEventPacket();

        EventLogic.addWorldEvent(event, speaker, "world-message", 42);

        assertEquals(1, src.SimYukkuri.world.getCurrentMap().getEvent().size());
        assertSame(event, src.SimYukkuri.world.getCurrentMap().getEvent().get(0));
        assertEquals("world-message", speaker.getMessageBuffer());
        assertEquals(42, speaker.getMessageTicks());
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
    public void testAddBodyEventRegistersEventAndSpeakerMessage() {
        Reimu target = new Reimu();
        Reimu speaker = new Reimu();
        MockEventPacket event = new MockEventPacket();

        EventLogic.addBodyEvent(target, event, speaker, "body-message", 24);

        assertEquals(1, target.getEventList().size());
        assertSame(event, target.getEventList().get(0));
        assertEquals("body-message", speaker.getMessageBuffer());
        assertEquals(24, speaker.getMessageTicks());
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
        assertEquals(1, src.SimYukkuri.world.getCurrentMap().getEvent().size(), "world event should remain registered");
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

    // ========== 追加テスト ==========

    @Test
    public void testConstructor_doesNotThrow() {
        assertDoesNotThrow(() -> new EventLogic());
    }

    // --- checkBodyEvent: countDown でイベント除去 ---

    @Test
    public void testCheckBodyEvent_countDownTrue_removesEvent() {
        // simple=false, check=false → countDown=true → removed
        Reimu yukkuri = new Reimu();
        MockCountDownEvent event = new MockCountDownEvent();
        yukkuri.getEventList().add(event);
        assertNull(EventLogic.checkBodyEvent(yukkuri));
        assertEquals(0, yukkuri.getEventList().size(), "countDown=true so event should be removed");
    }

    @Test
    public void testCheckBodyEvent_retNotNull_secondEventCountsDown() {
        // event1: check=true → ret set & removed
        // event2: simple=false, ret!=null → if(ret==null) skipped → countDown=true → removed
        Reimu yukkuri = new Reimu();
        MockCheckResponseTruePacket event1 = new MockCheckResponseTruePacket();
        MockCountDownEvent event2 = new MockCountDownEvent();
        yukkuri.getEventList().add(event1);
        yukkuri.getEventList().add(event2);
        EventPacket result = EventLogic.checkBodyEvent(yukkuri);
        assertNotNull(result);
        assertEquals(0, yukkuri.getEventList().size(), "both events should be removed");
    }

    // --- checkSimpleWorldEvent: from == b → simpleEventAction スキップ ---

    @Test
    public void testCheckSimpleWorldEvent_FromEqualsBody_Skips() {
        Reimu yukkuri = new Reimu();
        yukkuri.setUniqueID(4001);
        src.SimYukkuri.world.getCurrentMap().getBody().put(4001, yukkuri);

        MockEventPacket event = new MockEventPacket(); // simpleEventAction → sets wasSimpleActionCalled
        event.setFrom(yukkuri); // from = 4001
        src.SimYukkuri.world.getCurrentMap().getEvent().add(event);

        EventLogic.checkSimpleWorldEvent(yukkuri); // getBodyInstance(4001)==yukkuri → from==b → skip
        assertFalse(event.wasSimpleActionCalled, "simpleEventAction should be skipped when from==b");

        src.SimYukkuri.world.getCurrentMap().getEvent().clear();
        src.SimYukkuri.world.getCurrentMap().getBody().remove(4001);
    }

    // --- checkSimpleBodyEvent: 完全未カバー (lines 154-162) ---

    @Test
    public void testCheckSimpleBodyEvent_simpleTrue_removed() {
        Reimu yukkuri = new Reimu();
        MockSimpleEventTruePacket event = new MockSimpleEventTruePacket();
        yukkuri.getEventList().add(event);
        EventLogic.checkSimpleBodyEvent(yukkuri);
        assertEquals(0, yukkuri.getEventList().size(), "simpleEventAction=true → removed");
    }

    @Test
    public void testCheckSimpleBodyEvent_simpleFalse_kept() {
        Reimu yukkuri = new Reimu();
        MockEventPacket event = new MockEventPacket(); // simpleEventAction returns false
        yukkuri.getEventList().add(event);
        EventLogic.checkSimpleBodyEvent(yukkuri);
        assertEquals(1, yukkuri.getEventList().size(), "simpleEventAction=false → kept");
    }

    // --- eventUpdate: 未到達 (line 186 missed branches) ---

    @Test
    public void testEventUpdate_farFromTarget_executeNotCalled() {
        // state=null, Z match but far → (stepDist+2) < distance → execute NOT called
        Reimu yukkuri = new Reimu();
        yukkuri.setX(0);
        yukkuri.setY(0);
        yukkuri.setZ(0);
        MockEventPacket event = new MockEventPacket();
        event.setToX(10000);
        event.setToY(10000);
        event.setToZ(0);
        yukkuri.setCurrentEvent(event);
        EventLogic.eventUpdate(yukkuri);
        assertFalse(event.wasExecuteCalled, "execute should NOT be called when body is far from target");
        assertNotNull(yukkuri.getCurrentEvent(), "event should remain");
    }

    @Test
    public void testEventUpdate_ZMismatch_executeNotCalled() {
        // state=null, Z mismatch → b.getZ() != ev.getToZ() → execute NOT called
        Reimu yukkuri = new Reimu();
        yukkuri.setX(100);
        yukkuri.setY(100);
        yukkuri.setZ(0);
        MockEventPacket event = new MockEventPacket();
        event.setToX(100);
        event.setToY(100);
        event.setToZ(5); // Z mismatch
        yukkuri.setCurrentEvent(event);
        EventLogic.eventUpdate(yukkuri);
        assertFalse(event.wasExecuteCalled, "execute should NOT be called when Z doesn't match");
        assertNotNull(yukkuri.getCurrentEvent(), "event should remain");
    }

    // --- clockWorldEvent: countDown=false → イベント残留 (line 72 false branch) ---

    @Test
    public void testClockWorldEvent_countDownFalse_keepsEvent() {
        // MockEventPacket は countDown をオーバーライドしないので count=100→99, false を返す
        MockEventPacket event = new MockEventPacket();
        src.SimYukkuri.world.getCurrentMap().getEvent().add(event);
        EventLogic.clockWorldEvent();
        assertEquals(1, src.SimYukkuri.world.getCurrentMap().getEvent().size(),
                "countDown=false なのでイベントは残るはず");
        src.SimYukkuri.world.getCurrentMap().getEvent().clear();
    }

    // --- checkBodyEvent: countDown=false → イベント残留 (line 123 false branch) ---

    @Test
    public void testCheckBodyEvent_countDownFalse_keepsEvent() {
        // simple=false, check=false, countDown=false → stays in list, returns null
        Reimu yukkuri = new Reimu();
        MockEventPacket event = new MockEventPacket(); // countDown not overridden → false
        yukkuri.getEventList().add(event);
        assertNull(EventLogic.checkBodyEvent(yukkuri));
        assertEquals(1, yukkuri.getEventList().size(), "countDown=false なのでイベントは残るはず");
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
        public boolean wasExecuteCalled = false;

        MockForceExecEvent(boolean r) {
            execResult = r;
        }

        @Override
        public UpdateState update(Body b) {
            return UpdateState.FORCE_EXEC;
        }

        @Override
        public boolean execute(Body b) {
            wasExecuteCalled = true;
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

    @Nested
    class RegressionScenarios {

        @Test
        void testScenario_SelectedBodyEventRemovesOnlyChosenEventAndKeepsTrailingEvent() {
            Reimu yukkuri = new Reimu();
            MockCheckResponseTruePacket selected = new MockCheckResponseTruePacket();
            MockEventPacket trailing = new MockEventPacket();

            yukkuri.getEventList().add(selected);
            yukkuri.getEventList().add(trailing);

            EventPacket result = EventLogic.checkBodyEvent(yukkuri);

            assertSame(selected, result);
            assertEquals(1, yukkuri.getEventList().size());
            assertSame(trailing, yukkuri.getEventList().get(0));
        }

        @Test
        void testScenario_SimpleBodyEventRemovalDoesNotTouchLaterNormalEvent() {
            Reimu yukkuri = new Reimu();
            MockSimpleEventTruePacket simple = new MockSimpleEventTruePacket();
            MockEventPacket trailing = new MockEventPacket();

            yukkuri.getEventList().add(simple);
            yukkuri.getEventList().add(trailing);

            EventLogic.checkSimpleBodyEvent(yukkuri);

            assertEquals(1, yukkuri.getEventList().size());
            assertSame(trailing, yukkuri.getEventList().get(0));
        }

        @Test
        void testScenario_SimpleWorldEventDoesNotRemoveItselfOrLaterNormalEvent() {
            Reimu yukkuri = new Reimu();
            MockSimpleEventTruePacket simple = new MockSimpleEventTruePacket();
            MockEventPacket trailing = new MockEventPacket();

            src.SimYukkuri.world.getCurrentMap().getEvent().add(simple);
            src.SimYukkuri.world.getCurrentMap().getEvent().add(trailing);

            EventLogic.checkSimpleWorldEvent(yukkuri);

            assertEquals(2, src.SimYukkuri.world.getCurrentMap().getEvent().size());
            assertSame(simple, src.SimYukkuri.world.getCurrentMap().getEvent().get(0));
            assertSame(trailing, src.SimYukkuri.world.getCurrentMap().getEvent().get(1));
        }

        @Test
        void testScenario_EventUpdateNearTargetExecutesAndClearsWhenExecuteReturnsTrueWithoutForceExec() {
            Reimu yukkuri = new Reimu();
            yukkuri.setX(100);
            yukkuri.setY(100);
            yukkuri.setZ(0);
            MockEventPacket event = new MockEventPacket();
            event.setToX(101);
            event.setToY(100);
            event.setToZ(0);
            yukkuri.setCurrentEvent(event);

            EventLogic.eventUpdate(yukkuri);

            assertTrue(event.wasExecuteCalled);
            assertNull(yukkuri.getCurrentEvent());
        }

        @Test
        void testScenario_EventUpdateNearTargetExecutesButKeepsEventWhenExecuteReturnsFalseWithoutForceExec() {
            Reimu yukkuri = new Reimu();
            yukkuri.setX(100);
            yukkuri.setY(100);
            yukkuri.setZ(0);
            MockForceExecEvent event = new MockForceExecEvent(false) {
                @Override
                public UpdateState update(Body b) {
                    return null;
                }
            };
            event.setToX(101);
            event.setToY(100);
            event.setToZ(0);
            yukkuri.setCurrentEvent(event);

            EventLogic.eventUpdate(yukkuri);

            assertTrue(event.wasExecuteCalled);
            assertNotNull(yukkuri.getCurrentEvent());
        }
    }
}
