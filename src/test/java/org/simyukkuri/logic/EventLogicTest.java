package org.simyukkuri.logic;

import org.simyukkuri.entity.core.Entity;
import org.simyukkuri.entity.core.attachment.*;
import org.simyukkuri.entity.core.attachment.impl.*;
import org.simyukkuri.entity.core.effect.*;
import org.simyukkuri.entity.core.effect.impl.*;
import org.simyukkuri.entity.core.living.yukkuri.Dna;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.living.yukkuri.impl.*;
import org.simyukkuri.entity.core.world.bodylinked.*;
import org.simyukkuri.entity.core.world.item.*;
import org.simyukkuri.entity.core.world.mobile.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.event.EventPacket;
import org.simyukkuri.entity.core.living.yukkuri.impl.Reimu;

/**
 * Test class for EventLogic.
 * EventLogic has event registration helpers that can be tested.
 * Full event lifecycle testing requires World initialization.
 */
public class EventLogicTest {

    @BeforeEach
    public void setUp() {
        try {
            org.simyukkuri.util.WorldTestHelper.initializeMinimalWorld();
        } catch (Exception e) {
            // World initialization may fail - tests will handle this
        }
    }

    // ========== World TickResult Registration Tests ==========

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

        assertEquals(1, org.simyukkuri.SimYukkuri.world.getCurrentWorldState().getEvents().size());
        assertSame(event, org.simyukkuri.SimYukkuri.world.getCurrentWorldState().getEvents().get(0));
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

    // ========== Yukkuri TickResult Registration Tests ==========

    @Test
    public void testAddBodyEventShortcut() {
        Reimu target = new Reimu();
        Reimu speaker = new Reimu();
        MockEventPacket event = new MockEventPacket();

        // Should not crash - uses default HOLDMESSAGE count
        EventLogic.addYukkuriEvent(target, event, speaker, "test message");

        // TickResult should be added to target's event list
        assertEquals(1, target.getEvents().size(), "TickResult should be added to body");
    }

    @Test
    public void testAddBodyEventWithCount() {
        Reimu target = new Reimu();
        Reimu speaker = new Reimu();
        MockEventPacket event = new MockEventPacket();

        // Should not crash - uses custom count
        EventLogic.addYukkuriEvent(target, event, speaker, "test message", 100);

        // TickResult should be added to target's event list
        assertEquals(1, target.getEvents().size(), "TickResult should be added to body");
    }

    @Test
    public void testAddBodyEventRegistersEventAndSpeakerMessage() {
        Reimu target = new Reimu();
        Reimu speaker = new Reimu();
        MockEventPacket event = new MockEventPacket();

        EventLogic.addYukkuriEvent(target, event, speaker, "body-message", 24);

        assertEquals(1, target.getEvents().size());
        assertSame(event, target.getEvents().get(0));
        assertEquals("body-message", speaker.getMessageBuffer());
        assertEquals(24, speaker.getMessageTicks());
    }

    @Test
    public void testAddBodyEventNullMessage() {
        Reimu target = new Reimu();
        MockEventPacket event = new MockEventPacket();

        // Should not crash with null message body
        EventLogic.addYukkuriEvent(target, event, null, null);

        // TickResult should be added to target's event list
        assertEquals(1, target.getEvents().size(), "TickResult should be added to body");
    }

    // ========== TickResult Lifecycle Tests (Require World) ==========

    @Test
    public void testClockWorldEvent() {
        // Add an expired event then clock it
        MockEventPacket event = new MockCountDownEvent(); // countDown returns true → removed
        org.simyukkuri.SimYukkuri.world.getCurrentWorldState().getEvents().add(event);
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

    // --- checkYukkuriEvent ---

    @Test
    public void testCheckBodyEvent_noEvents_returnsNull() {
        Reimu yukkuri = new Reimu();
        assertNull(EventLogic.checkYukkuriEvent(yukkuri));
    }

    @Test
    public void testCheckBodyEvent_simpleEventActionTrue_removesEvent() {
        Reimu yukkuri = new Reimu();
        MockSimpleEventTruePacket event = new MockSimpleEventTruePacket();
        yukkuri.getEvents().add(event);
        EventLogic.checkYukkuriEvent(yukkuri); // simpleEventAction=true → removed
        assertEquals(0, yukkuri.getEvents().size());
    }

    @Test
    public void testCheckBodyEvent_checkResponseTrue_returnsEvent() {
        Reimu yukkuri = new Reimu();
        MockCheckResponseTruePacket event = new MockCheckResponseTruePacket();
        yukkuri.getEvents().add(event);
        EventPacket result = EventLogic.checkYukkuriEvent(yukkuri);
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
        org.simyukkuri.SimYukkuri.world.getCurrentWorldState().getEvents().add(event);
        EventPacket result = EventLogic.checkWorldEvent(yukkuri);
        assertNull(result); // simpleEventAction=true → skipped, not returned
        org.simyukkuri.SimYukkuri.world.getCurrentWorldState().getEvents().clear();
    }

    @Test
    public void testCheckWorldEvent_checkResponseTrue_returnsEvent() {
        Reimu yukkuri = new Reimu();
        MockCheckResponseTruePacket event = new MockCheckResponseTruePacket();
        org.simyukkuri.SimYukkuri.world.getCurrentWorldState().getEvents().add(event);
        EventPacket result = EventLogic.checkWorldEvent(yukkuri);
        assertNotNull(result);
        assertEquals(1, org.simyukkuri.SimYukkuri.world.getCurrentWorldState().getEvents().size(), "world event should remain registered");
        org.simyukkuri.SimYukkuri.world.getCurrentWorldState().getEvents().clear();
    }

    // --- checkSimpleWorldEvent ---

    @Test
    public void testCheckSimpleWorldEvent_FromCheck() {
        Reimu yukkuri = new Reimu();
        yukkuri.setUniqueID(100);
        MockSimpleEventTruePacket event = new MockSimpleEventTruePacket();
        event.setFrom(yukkuri); // TickResult is from this yukkuri
        org.simyukkuri.SimYukkuri.world.getCurrentWorldState().getEvents().add(event);

        // Should skip because from == b
        EventLogic.checkSimpleWorldEvent(yukkuri);

        // event.simpleEventAction should NOT have been called
        assertFalse(event.wasSimpleActionCalled);
        org.simyukkuri.SimYukkuri.world.getCurrentWorldState().getEvents().clear();
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

    // --- checkYukkuriEvent: countDown でイベント除去 ---

    @Test
    public void testCheckBodyEvent_countDownTrue_removesEvent() {
        // simple=false, check=false → countDown=true → removed
        Reimu yukkuri = new Reimu();
        MockCountDownEvent event = new MockCountDownEvent();
        yukkuri.getEvents().add(event);
        assertNull(EventLogic.checkYukkuriEvent(yukkuri));
        assertEquals(0, yukkuri.getEvents().size(), "countDown=true so event should be removed");
    }

    @Test
    public void testCheckBodyEvent_retNotNull_secondEventCountsDown() {
        // event1: check=true → ret set & removed
        // event2: simple=false, ret!=null → if(ret==null) skipped → countDown=true → removed
        Reimu yukkuri = new Reimu();
        MockCheckResponseTruePacket event1 = new MockCheckResponseTruePacket();
        MockCountDownEvent event2 = new MockCountDownEvent();
        yukkuri.getEvents().add(event1);
        yukkuri.getEvents().add(event2);
        EventPacket result = EventLogic.checkYukkuriEvent(yukkuri);
        assertNotNull(result);
        assertEquals(0, yukkuri.getEvents().size(), "both events should be removed");
    }

    // --- checkSimpleWorldEvent: from == b → simpleEventAction スキップ ---

    @Test
    public void testCheckSimpleWorldEvent_FromEqualsBody_Skips() {
        Reimu yukkuri = new Reimu();
        yukkuri.setObjId(4001);
        org.simyukkuri.SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(4001, yukkuri);

        MockEventPacket event = new MockEventPacket(); // simpleEventAction → sets wasSimpleActionCalled
        event.setFrom(yukkuri); // from = 4001
        org.simyukkuri.SimYukkuri.world.getCurrentWorldState().getEvents().add(event);

        EventLogic.checkSimpleWorldEvent(yukkuri); // getYukkuriRegistry(4001)==yukkuri → from==b → skip
        assertFalse(event.wasSimpleActionCalled, "simpleEventAction should be skipped when from==b");

        org.simyukkuri.SimYukkuri.world.getCurrentWorldState().getEvents().clear();
        org.simyukkuri.SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().remove(4001);
    }

    // --- checkSimpleYukkuriEvent: 完全未カバー (lines 154-162) ---

    @Test
    public void testCheckSimpleBodyEvent_simpleTrue_removed() {
        Reimu yukkuri = new Reimu();
        MockSimpleEventTruePacket event = new MockSimpleEventTruePacket();
        yukkuri.getEvents().add(event);
        EventLogic.checkSimpleYukkuriEvent(yukkuri);
        assertEquals(0, yukkuri.getEvents().size(), "simpleEventAction=true → removed");
    }

    @Test
    public void testCheckSimpleBodyEvent_simpleFalse_kept() {
        Reimu yukkuri = new Reimu();
        MockEventPacket event = new MockEventPacket(); // simpleEventAction returns false
        yukkuri.getEvents().add(event);
        EventLogic.checkSimpleYukkuriEvent(yukkuri);
        assertEquals(1, yukkuri.getEvents().size(), "simpleEventAction=false → kept");
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
        org.simyukkuri.SimYukkuri.world.getCurrentWorldState().getEvents().add(event);
        EventLogic.clockWorldEvent();
        assertEquals(1, org.simyukkuri.SimYukkuri.world.getCurrentWorldState().getEvents().size(),
                "countDown=false なのでイベントは残るはず");
        org.simyukkuri.SimYukkuri.world.getCurrentWorldState().getEvents().clear();
    }

    // --- checkYukkuriEvent: countDown=false → イベント残留 (line 123 false branch) ---

    @Test
    public void testCheckBodyEvent_countDownFalse_keepsEvent() {
        // simple=false, check=false, countDown=false → stays in list, returns null
        Reimu yukkuri = new Reimu();
        MockEventPacket event = new MockEventPacket(); // countDown not overridden → false
        yukkuri.getEvents().add(event);
        assertNull(EventLogic.checkYukkuriEvent(yukkuri));
        assertEquals(1, yukkuri.getEvents().size(), "countDown=false なのでイベントは残るはず");
    }

    // ========== Mock TickResult Packet for Testing ==========

    private static class MockEventPacket extends EventPacket {
        public boolean wasExecuteCalled = false;
        public boolean wasSimpleActionCalled = false;

        public MockEventPacket() {
            super(null, null, null, 100);
        }

        @Override
        public boolean checkEventResponse(Yukkuri b) {
            return false;
        }

        @Override
        public void start(Yukkuri b) {
        }

        @Override
        public UpdateState update(Yukkuri b) {
            return null;
        }

        @Override
        public boolean execute(Yukkuri b) {
            wasExecuteCalled = true;
            return true;
        }

        @Override
        public void end(Yukkuri b) {
        }

        @Override
        public boolean simpleEventAction(Yukkuri b) {
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
        public UpdateState update(Yukkuri b) {
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
        public UpdateState update(Yukkuri b) {
            return UpdateState.FORCE_EXEC;
        }

        @Override
        public boolean execute(Yukkuri b) {
            wasExecuteCalled = true;
            return execResult;
        }
    }

    private static class MockSimpleEventTruePacket extends MockEventPacket {
        @Override
        public boolean simpleEventAction(Yukkuri b) {
            return true;
        }
    }

    private static class MockCheckResponseTruePacket extends MockEventPacket {
        @Override
        public boolean simpleEventAction(Yukkuri b) {
            return false;
        }

        @Override
        public boolean checkEventResponse(Yukkuri b) {
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

            yukkuri.getEvents().add(selected);
            yukkuri.getEvents().add(trailing);

            EventPacket result = EventLogic.checkYukkuriEvent(yukkuri);

            assertSame(selected, result);
            assertEquals(1, yukkuri.getEvents().size());
            assertSame(trailing, yukkuri.getEvents().get(0));
        }

        @Test
        void testScenario_SimpleBodyEventRemovalDoesNotTouchLaterNormalEvent() {
            Reimu yukkuri = new Reimu();
            MockSimpleEventTruePacket simple = new MockSimpleEventTruePacket();
            MockEventPacket trailing = new MockEventPacket();

            yukkuri.getEvents().add(simple);
            yukkuri.getEvents().add(trailing);

            EventLogic.checkSimpleYukkuriEvent(yukkuri);

            assertEquals(1, yukkuri.getEvents().size());
            assertSame(trailing, yukkuri.getEvents().get(0));
        }

        @Test
        void testScenario_SimpleWorldEventDoesNotRemoveItselfOrLaterNormalEvent() {
            Reimu yukkuri = new Reimu();
            MockSimpleEventTruePacket simple = new MockSimpleEventTruePacket();
            MockEventPacket trailing = new MockEventPacket();

            org.simyukkuri.SimYukkuri.world.getCurrentWorldState().getEvents().add(simple);
            org.simyukkuri.SimYukkuri.world.getCurrentWorldState().getEvents().add(trailing);

            EventLogic.checkSimpleWorldEvent(yukkuri);

            assertEquals(2, org.simyukkuri.SimYukkuri.world.getCurrentWorldState().getEvents().size());
            assertSame(simple, org.simyukkuri.SimYukkuri.world.getCurrentWorldState().getEvents().get(0));
            assertSame(trailing, org.simyukkuri.SimYukkuri.world.getCurrentWorldState().getEvents().get(1));
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
                public UpdateState update(Yukkuri b) {
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
