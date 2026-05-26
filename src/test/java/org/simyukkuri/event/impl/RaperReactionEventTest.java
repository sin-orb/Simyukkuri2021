package org.simyukkuri.event.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.simyukkuri.SimYukkuri;
import org.simyukkuri.engine.World;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.living.yukkuri.impl.Reimu;
import org.simyukkuri.enums.ActionState;
import org.simyukkuri.enums.AgeState;
import org.simyukkuri.event.EventPacket.EventPriority;
import org.simyukkuri.system.Sprite;
import org.simyukkuri.util.WorldTestHelper;

public class RaperReactionEventTest {

    @BeforeEach
    public void setUp() {
        WorldTestHelper.resetWorld();
        SimYukkuri.world = new World();
        WorldTestHelper.initializeStandardTranslate500();
    }

    // --- Default constructor ---

    @Test
    public void testDefaultConstructor() {
        RaperReactionEvent event = new RaperReactionEvent();
        assertEquals(-1, event.getFrom());
        assertEquals(-1, event.getTo());
        assertEquals(EventPriority.LOW, event.getPriority());
    }

    // --- Parameterized constructor ---

    @Test
    public void testParameterizedConstructor() {
        Yukkuri from = createBody();
        Yukkuri to = createBody();
        RaperReactionEvent event = new RaperReactionEvent(from, to, null, 1);
        assertEquals(from.getUniqueId(), event.getFrom());
        assertEquals(to.getUniqueId(), event.getTo());
        assertEquals(1, event.getCount());
    }

    // --- checkEventResponse sets priority HIGH ---

    @Test
    public void testCheckEventResponse_setsPriorityHigh() {
        Yukkuri b = createBody();
        Yukkuri raper = createBody();
        // Set up raper as exciting raper in the map so the check finds one
        raper.setRapist(true);
        raper.setExciting(true);
        RaperReactionEvent event = new RaperReactionEvent(raper, null, null, 1);
        event.checkEventResponse(b);
        assertEquals(EventPriority.HIGH, event.getPriority());
    }

    // --- getState / setState ---

    @Test
    public void testGetState_defaultIsNull() {
        RaperReactionEvent event = new RaperReactionEvent();
        assertNull(event.getState());
    }

    @Test
    public void testSetState() {
        RaperReactionEvent event = new RaperReactionEvent();
        event.setState(ActionState.ESCAPE);
        assertEquals(ActionState.ESCAPE, event.getState());

        event.setState(ActionState.ATTACK);
        assertEquals(ActionState.ATTACK, event.getState());
    }

    // --- update ---
    @Test
    public void testUpdate_fromNull_returnsAbort() {
        Yukkuri b = createBody();
        RaperReactionEvent event = new RaperReactionEvent();
        // from=-1 → null → ABORT
        assertEquals(org.simyukkuri.event.EventPacket.UpdateState.ABORT, event.update(b));
    }

    @Test
    public void testUpdate_fromDead_doesNotThrow() {
        Yukkuri from = createBody();
        Yukkuri b = createBody();
        from.setDead(true);
        RaperReactionEvent event = new RaperReactionEvent(from, null, null, 1);
        // from is dead → searches next target (null) → continues, returns null or ABORT
        assertDoesNotThrow(() -> event.update(b));
    }

    @Test
    public void testUpdate_fromRemoved_doesNotThrow() {
        Yukkuri from = createBody();
        Yukkuri b = createBody();
        from.setRemoved(true);
        RaperReactionEvent event = new RaperReactionEvent(from, null, null, 1);
        assertDoesNotThrow(() -> event.update(b));
    }

    // --- start ---
    @Test
    public void testStart_doesNotThrow() {
        Yukkuri from = createBody();
        Yukkuri b = createBody();
        RaperReactionEvent event = new RaperReactionEvent(from, null, null, 1);
        assertDoesNotThrow(() -> event.start(b));
    }

    // --- toString ---
    @Test
    public void testToString_doesNotThrow() {
        RaperReactionEvent event = new RaperReactionEvent();
        assertDoesNotThrow(() -> event.toString());
    }

    // --- checkEventResponse (from=null) ---
    @Test
    public void testCheckEventResponse_fromNull_doesNotThrow() {
        Yukkuri b = createBody();
        RaperReactionEvent event = new RaperReactionEvent();
        assertDoesNotThrow(() -> event.checkEventResponse(b));
    }

    // --- execute ---
    @Test
    public void testExecute_fromNull_returnsTrue() {
        Yukkuri b = createBody();
        RaperReactionEvent event = new RaperReactionEvent();
        assertTrue(event.execute(b));
    }

    // --- end ---
    @Test
    public void testEnd_doesNotThrow() {
        Yukkuri b = createBody();
        RaperReactionEvent event = new RaperReactionEvent(b, null, null, 1);
        assertDoesNotThrow(() -> event.end(b));
    }

    // --- Helper ---

    private static Yukkuri createBody() {
        Yukkuri b = new Reimu();
        b.setAgeState(AgeState.ADULT);
        Sprite[] spr = new Sprite[3];
        for (int i = 0; i < 3; i++) {
            spr[i] = new Sprite(10, 10, Sprite.PIVOT_CENTER_BOTTOM);
        }
        b.setSpriteSet(spr);
        SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(b.getUniqueId(), b);
        return b;
    }

    @Test
    public void testSetScareWorldEventMessage_doesNotThrow() {
        Yukkuri from = createBody();
        Yukkuri b = createBody();
        RaperReactionEvent event = new RaperReactionEvent(from, null, null, 10);
        assertDoesNotThrow(() -> event.setScareWorldEventMessage(b));
    }

    @Test
    public void testSetCounterWorldEventMessage_doesNotThrow() {
        Yukkuri from = createBody();
        Yukkuri b = createBody();
        RaperReactionEvent event = new RaperReactionEvent(from, null, null, 10);
        assertDoesNotThrow(() -> event.setCounterWorldEventMessage(b));
    }

    @Test
    public void testCheckConditionOfTarget_fromNull_returnsFalse() {
        // unregistered from → getBodyMap returns null → returns false
        Yukkuri unregistered = new Reimu();
        RaperReactionEvent event = new RaperReactionEvent(unregistered, null, null, 10);
        assertFalse(event.checkConditionOfTarget());
    }

    @Test
    public void testCheckConditionOfTarget_fromRegistered_doesNotThrow() {
        Yukkuri from = createBody();
        RaperReactionEvent event = new RaperReactionEvent(from, null, null, 10);
        assertDoesNotThrow(() -> event.checkConditionOfTarget());
    }

    @Test
    public void testSearchAttackTarget_emptyWorld_returnsNull() {
        RaperReactionEvent event = new RaperReactionEvent();
        // No body with raper+exciting+sukkiri in world → returns null
        assertNull(event.searchAttackTarget());
    }

    @Test
    public void testMoveTarget_fromNull_doesNotThrow() {
        Yukkuri b = createBody();
        Yukkuri unregistered = new Reimu();
        RaperReactionEvent event = new RaperReactionEvent(unregistered, null, null, 10);
        // from not registered → getBodyMap returns null → early return
        assertDoesNotThrow(() -> event.moveTargetId(b));
    }

    @Test
    public void testMoveTarget_fromExists_doesNotThrow() {
        Yukkuri from = createBody();
        Yukkuri b = createBody();
        RaperReactionEvent event = new RaperReactionEvent(from, null, null, 10);
        assertDoesNotThrow(() -> event.moveTargetId(b));
    }

    // --- checkEventResponse: raper nearby, normal body → state=ESCAPE, return true
    // ---
    @Test
    public void testCheckEventResponse_raperNearby_normalBody_stateEscape_returnsTrue() {
        Yukkuri b = createBody();
        Yukkuri raper = createBody();
        raper.setRapist(true); // isRaper() = true → not skipped
        RaperReactionEvent event = new RaperReactionEvent(raper, null, null, 1);
        boolean result = event.checkEventResponse(b);
        assertTrue(result);
        assertEquals(ActionState.ESCAPE, event.getState());
    }

    // --- checkEventResponse: raper nearby, UnunSlave → state=ESCAPE, return true
    // ---
    @Test
    public void testCheckEventResponse_raperNearby_UnunSlave_stateEscape_returnsTrue() {
        Yukkuri b = createBody();
        b.setPublicRank(org.simyukkuri.enums.PublicRank.UNUN_SLAVE);
        Yukkuri raper = createBody();
        raper.setRapist(true);
        RaperReactionEvent event = new RaperReactionEvent(raper, null, null, 1);
        boolean result = event.checkEventResponse(b);
        assertTrue(result);
        assertEquals(ActionState.ESCAPE, event.getState());
    }

    // --- start: NYD → early return ---
    @Test
    public void testStart_NYD_earlyReturn() {
        Yukkuri from = createBody();
        Yukkuri b = createBody();
        b.setCoreAnkoState(
                org.simyukkuri.enums.CoreAnkoState.NON_YUKKURI_DISEASE); // isNyd() = true
        RaperReactionEvent event = new RaperReactionEvent(from, null, null, 1);
        event.setState(ActionState.ATTACK);
        assertDoesNotThrow(() -> event.start(b)); // returns early, no throw
    }

    // --- start: ATTACK state → moveTargetId + setAngry ---
    @Test
    public void testStart_attackState_doesNotThrow() {
        Yukkuri from = createBody();
        Yukkuri b = createBody();
        RaperReactionEvent event = new RaperReactionEvent(from, null, null, 1);
        event.setState(ActionState.ATTACK);
        assertDoesNotThrow(() -> event.start(b));
    }

    // --- start: ESCAPE state → escapeTarget + VERY_SAD ---
    @Test
    public void testStart_escapeState_doesNotThrow() {
        Yukkuri from = createBody();
        Yukkuri b = createBody();
        RaperReactionEvent event = new RaperReactionEvent(from, null, null, 1);
        event.setState(ActionState.ESCAPE);
        assertDoesNotThrow(() -> event.start(b));
    }

    // --- update: from is alive raper, state=ATTACK → ATTACK path, returns null ---
    @Test
    public void testUpdate_state_ATTACK_raperFrom_returnsNull() {
        Yukkuri from = createBody();
        from.setRapist(true); // isRaper() = true
        Yukkuri b = createBody();
        RaperReactionEvent event = new RaperReactionEvent(from, null, null, 1);
        event.setState(ActionState.ATTACK);
        SimYukkuri.RND = new org.simyukkuri.ConstState(1); // avoid nextInt(500)=0 and nextInt(20)=0
        try {
            assertNull(event.update(b));
        } finally {
            SimYukkuri.RND = new java.util.Random();
        }
    }

    // --- update: state=ESCAPE, age=1 (1%10 != 0) → CRYING path ---
    @Test
    public void testUpdate_state_ESCAPE_age1_doesNotThrow() {
        Yukkuri from = createBody();
        from.setRapist(true);
        Yukkuri b = createBody();
        RaperReactionEvent event = new RaperReactionEvent(from, null, null, 1);
        event.setState(ActionState.ESCAPE);
        // Set age=1 via reflection so age%10 != 0 → enters else branch (CRYING path)
        try {
            java.lang.reflect.Field f = RaperReactionEvent.class.getDeclaredField("age");
            f.setAccessible(true);
            f.setInt(event, 1);
        } catch (Exception e) {
            /* ignore */
        }
        SimYukkuri.RND = new org.simyukkuri.ConstState(1);
        try {
            assertDoesNotThrow(() -> event.update(b));
        } finally {
            SimYukkuri.RND = new java.util.Random();
        }
    }

    // --- execute: from alive, state=ESCAPE → escapeTarget, returns false ---
    @Test
    public void testExecute_fromAlive_stateEscape_returnsFalse() {
        Yukkuri from = createBody();
        Yukkuri b = createBody();
        RaperReactionEvent event = new RaperReactionEvent(from, null, null, 1);
        event.setState(ActionState.ESCAPE);
        SimYukkuri.RND = new org.simyukkuri.ConstState(1); // avoid nextInt(20)=0
        try {
            assertFalse(event.execute(b));
        } finally {
            SimYukkuri.RND = new java.util.Random();
        }
    }

    // --- execute: from alive, state=ATTACK, isDontMove → escape path, returns
    // false ---
    @Test
    public void testExecute_fromAlive_stateAttack_isDontMove_returnsFalse() {
        Yukkuri from = createBody();
        Yukkuri b = createBody();
        b.setGrabbed(true); // isDontMove() = true → else branch (escape)
        RaperReactionEvent event = new RaperReactionEvent(from, null, null, 1);
        event.setState(ActionState.ATTACK);
        SimYukkuri.RND = new org.simyukkuri.ConstState(1);
        try {
            assertFalse(event.execute(b));
        } finally {
            SimYukkuri.RND = new java.util.Random();
        }
    }

    // --- execute: from dead, new raper found → returns false ---
    @Test
    public void testExecute_fromDead_newRaperFound_returnsFalse() {
        Yukkuri from = createBody();
        from.setDead(true); // from is dead
        Yukkuri newRaper = createBody();
        newRaper.setRapist(true);
        newRaper.setExciting(true); // searchNextTarget finds this
        Yukkuri b = createBody();
        RaperReactionEvent event = new RaperReactionEvent(from, null, null, 1);
        event.setState(ActionState.ESCAPE);
        assertFalse(event.execute(b));
    }

    // --- searchNextTarget: raper+exciting body in map → returns it ---
    @Test
    public void testSearchNextTarget_raperExists_returnsBody() {
        Yukkuri raper = createBody();
        raper.setRapist(true);
        raper.setExciting(true);
        RaperReactionEvent event = new RaperReactionEvent();
        Yukkuri result = event.searchNextTarget();
        assertNotNull(result);
        assertEquals(raper.getUniqueId(), result.getUniqueId());
    }

    // --- escapeTarget: from null → early return ---
    @Test
    public void testEscapeTarget_fromNull_doesNotThrow() {
        Yukkuri b = createBody();
        Yukkuri unregistered = new Reimu(); // NOT in world map
        RaperReactionEvent event = new RaperReactionEvent(unregistered, null, null, 1);
        assertDoesNotThrow(() -> event.escapeTarget(b));
    }

    // --- escapeTarget: from exists → computes escape direction ---
    @Test
    public void testEscapeTarget_fromExists_doesNotThrow() {
        Yukkuri from = createBody();
        Yukkuri b = createBody();
        b.setX(500);
        b.setY(500); // middle of map
        from.setX(100);
        from.setY(100);
        RaperReactionEvent event = new RaperReactionEvent(from, null, null, 1);
        assertDoesNotThrow(() -> event.escapeTarget(b));
    }

    // --- checkConditionOfTarget: from is exciting → returns true ---
    @Test
    public void testCheckConditionOfTarget_fromExciting_returnsTrue() {
        Yukkuri from = createBody();
        from.setExciting(true);
        RaperReactionEvent event = new RaperReactionEvent(from, null, null, 10);
        assertTrue(event.checkConditionOfTarget());
    }
}
