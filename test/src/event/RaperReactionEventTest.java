package src.event;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import src.SimYukkuri;
import src.base.Body;
import src.base.EventPacket.EventPriority;
import src.draw.Translate;
import src.draw.World;
import src.enums.ActionState;
import src.enums.AgeState;
import src.system.Sprite;
import src.yukkuri.Reimu;

public class RaperReactionEventTest {

    @BeforeEach
    public void setUp() {
        SimYukkuri.world = new World();
        Translate.setMapSize(1000, 1000, 500);
        Translate.setCanvasSize(800, 600, 100, 100, new float[]{1.0f});
        Translate.createTransTable(false);
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
        Body from = createBody();
        Body to = createBody();
        RaperReactionEvent event = new RaperReactionEvent(from, to, null, 1);
        assertEquals(from.getUniqueID(), event.getFrom());
        assertEquals(to.getUniqueID(), event.getTo());
        assertEquals(1, event.getCount());
    }

    // --- checkEventResponse sets priority HIGH ---

    @Test
    public void testCheckEventResponse_setsPriorityHigh() {
        Body b = createBody();
        Body raper = createBody();
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
        Body b = createBody();
        RaperReactionEvent event = new RaperReactionEvent();
        // from=-1 → null → ABORT
        assertEquals(src.base.EventPacket.UpdateState.ABORT, event.update(b));
    }

    @Test
    public void testUpdate_fromDead_doesNotThrow() {
        Body from = createBody();
        Body b = createBody();
        from.setDead(true);
        RaperReactionEvent event = new RaperReactionEvent(from, null, null, 1);
        // from is dead → searches next target (null) → continues, returns null or ABORT
        assertDoesNotThrow(() -> event.update(b));
    }

    @Test
    public void testUpdate_fromRemoved_doesNotThrow() {
        Body from = createBody();
        Body b = createBody();
        from.setRemoved(true);
        RaperReactionEvent event = new RaperReactionEvent(from, null, null, 1);
        assertDoesNotThrow(() -> event.update(b));
    }

    // --- start ---
    @Test
    public void testStart_doesNotThrow() {
        Body from = createBody();
        Body b = createBody();
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
        Body b = createBody();
        RaperReactionEvent event = new RaperReactionEvent();
        assertDoesNotThrow(() -> event.checkEventResponse(b));
    }

    // --- execute ---
    @Test
    public void testExecute_fromNull_returnsTrue() {
        Body b = createBody();
        RaperReactionEvent event = new RaperReactionEvent();
        assertTrue(event.execute(b));
    }

    // --- end ---
    @Test
    public void testEnd_doesNotThrow() {
        Body b = createBody();
        RaperReactionEvent event = new RaperReactionEvent(b, null, null, 1);
        assertDoesNotThrow(() -> event.end(b));
    }

    // --- Helper ---

    private static Body createBody() {
        Body b = new Reimu();
        b.setAgeState(AgeState.ADULT);
        Sprite[] spr = new Sprite[3];
        for (int i = 0; i < 3; i++) {
            spr[i] = new Sprite(10, 10, Sprite.PIVOT_CENTER_BOTTOM);
        }
        b.setBodySpr(spr);
        SimYukkuri.world.getCurrentMap().getBody().put(b.getUniqueID(), b);
        return b;
    }

    @Test
    public void testSetScareWorldEventMessage_doesNotThrow() {
        Body from = createBody();
        Body b = createBody();
        RaperReactionEvent event = new RaperReactionEvent(from, null, null, 10);
        assertDoesNotThrow(() -> event.setScareWorldEventMessage(b));
    }

    @Test
    public void testSetCounterWorldEventMessage_doesNotThrow() {
        Body from = createBody();
        Body b = createBody();
        RaperReactionEvent event = new RaperReactionEvent(from, null, null, 10);
        assertDoesNotThrow(() -> event.setCounterWorldEventMessage(b));
    }

    @Test
    public void testCheckConditionOfTarget_fromNull_returnsFalse() {
        // unregistered from → getBodyInstance returns null → returns false
        Body unregistered = new Reimu();
        RaperReactionEvent event = new RaperReactionEvent(unregistered, null, null, 10);
        assertFalse(event.checkConditionOfTarget());
    }

    @Test
    public void testCheckConditionOfTarget_fromRegistered_doesNotThrow() {
        Body from = createBody();
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
        Body b = createBody();
        Body unregistered = new Reimu();
        RaperReactionEvent event = new RaperReactionEvent(unregistered, null, null, 10);
        // from not registered → getBodyInstance returns null → early return
        assertDoesNotThrow(() -> event.moveTarget(b));
    }

    @Test
    public void testMoveTarget_fromExists_doesNotThrow() {
        Body from = createBody();
        Body b = createBody();
        RaperReactionEvent event = new RaperReactionEvent(from, null, null, 10);
        assertDoesNotThrow(() -> event.moveTarget(b));
    }

    // --- checkEventResponse: raper nearby, normal body → state=ESCAPE, return true ---
    @Test
    public void testCheckEventResponse_raperNearby_normalBody_stateEscape_returnsTrue() {
        Body b = createBody();
        Body raper = createBody();
        raper.setRapist(true); // isRaper() = true → not skipped
        RaperReactionEvent event = new RaperReactionEvent(raper, null, null, 1);
        boolean result = event.checkEventResponse(b);
        assertTrue(result);
        assertEquals(ActionState.ESCAPE, event.getState());
    }

    // --- checkEventResponse: raper nearby, UnunSlave → state=ESCAPE, return true ---
    @Test
    public void testCheckEventResponse_raperNearby_UnunSlave_stateEscape_returnsTrue() {
        Body b = createBody();
        b.setPublicRank(src.enums.PublicRank.UnunSlave);
        Body raper = createBody();
        raper.setRapist(true);
        RaperReactionEvent event = new RaperReactionEvent(raper, null, null, 1);
        boolean result = event.checkEventResponse(b);
        assertTrue(result);
        assertEquals(ActionState.ESCAPE, event.getState());
    }

    // --- start: NYD → early return ---
    @Test
    public void testStart_NYD_earlyReturn() {
        Body from = createBody();
        Body b = createBody();
        b.seteCoreAnkoState(src.enums.CoreAnkoState.NonYukkuriDisease); // isNYD() = true
        RaperReactionEvent event = new RaperReactionEvent(from, null, null, 1);
        event.setState(ActionState.ATTACK);
        assertDoesNotThrow(() -> event.start(b)); // returns early, no throw
    }

    // --- start: ATTACK state → moveTarget + setAngry ---
    @Test
    public void testStart_attackState_doesNotThrow() {
        Body from = createBody();
        Body b = createBody();
        RaperReactionEvent event = new RaperReactionEvent(from, null, null, 1);
        event.setState(ActionState.ATTACK);
        assertDoesNotThrow(() -> event.start(b));
    }

    // --- start: ESCAPE state → escapeTarget + VERY_SAD ---
    @Test
    public void testStart_escapeState_doesNotThrow() {
        Body from = createBody();
        Body b = createBody();
        RaperReactionEvent event = new RaperReactionEvent(from, null, null, 1);
        event.setState(ActionState.ESCAPE);
        assertDoesNotThrow(() -> event.start(b));
    }

    // --- update: from is alive raper, state=ATTACK → ATTACK path, returns null ---
    @Test
    public void testUpdate_state_ATTACK_raperFrom_returnsNull() {
        Body from = createBody();
        from.setRapist(true); // isRaper() = true
        Body b = createBody();
        RaperReactionEvent event = new RaperReactionEvent(from, null, null, 1);
        event.setState(ActionState.ATTACK);
        SimYukkuri.RND = new src.ConstState(1); // avoid nextInt(500)=0 and nextInt(20)=0
        try {
            assertNull(event.update(b));
        } finally {
            SimYukkuri.RND = new java.util.Random();
        }
    }

    // --- update: state=ESCAPE, age=1 (1%10 != 0) → CRYING path ---
    @Test
    public void testUpdate_state_ESCAPE_age1_doesNotThrow() {
        Body from = createBody();
        from.setRapist(true);
        Body b = createBody();
        RaperReactionEvent event = new RaperReactionEvent(from, null, null, 1);
        event.setState(ActionState.ESCAPE);
        // Set age=1 via reflection so age%10 != 0 → enters else branch (CRYING path)
        try {
            java.lang.reflect.Field f = RaperReactionEvent.class.getDeclaredField("age");
            f.setAccessible(true);
            f.setInt(event, 1);
        } catch (Exception e) { /* ignore */ }
        SimYukkuri.RND = new src.ConstState(1);
        try {
            assertDoesNotThrow(() -> event.update(b));
        } finally {
            SimYukkuri.RND = new java.util.Random();
        }
    }

    // --- execute: from alive, state=ESCAPE → escapeTarget, returns false ---
    @Test
    public void testExecute_fromAlive_stateEscape_returnsFalse() {
        Body from = createBody();
        Body b = createBody();
        RaperReactionEvent event = new RaperReactionEvent(from, null, null, 1);
        event.setState(ActionState.ESCAPE);
        SimYukkuri.RND = new src.ConstState(1); // avoid nextInt(20)=0
        try {
            assertFalse(event.execute(b));
        } finally {
            SimYukkuri.RND = new java.util.Random();
        }
    }

    // --- execute: from alive, state=ATTACK, isDontMove → escape path, returns false ---
    @Test
    public void testExecute_fromAlive_stateAttack_isDontMove_returnsFalse() {
        Body from = createBody();
        Body b = createBody();
        b.setGrabbed(true); // isDontMove() = true → else branch (escape)
        RaperReactionEvent event = new RaperReactionEvent(from, null, null, 1);
        event.setState(ActionState.ATTACK);
        SimYukkuri.RND = new src.ConstState(1);
        try {
            assertFalse(event.execute(b));
        } finally {
            SimYukkuri.RND = new java.util.Random();
        }
    }

    // --- execute: from dead, new raper found → returns false ---
    @Test
    public void testExecute_fromDead_newRaperFound_returnsFalse() {
        Body from = createBody();
        from.setDead(true); // from is dead
        Body newRaper = createBody();
        newRaper.setRapist(true);
        newRaper.setExciting(true); // searchNextTarget finds this
        Body b = createBody();
        RaperReactionEvent event = new RaperReactionEvent(from, null, null, 1);
        event.setState(ActionState.ESCAPE);
        assertFalse(event.execute(b));
    }

    // --- searchNextTarget: raper+exciting body in map → returns it ---
    @Test
    public void testSearchNextTarget_raperExists_returnsBody() {
        Body raper = createBody();
        raper.setRapist(true);
        raper.setExciting(true);
        RaperReactionEvent event = new RaperReactionEvent();
        Body result = event.searchNextTarget();
        assertNotNull(result);
        assertEquals(raper.getUniqueID(), result.getUniqueID());
    }

    // --- escapeTarget: from null → early return ---
    @Test
    public void testEscapeTarget_fromNull_doesNotThrow() {
        Body b = createBody();
        Body unregistered = new src.yukkuri.Reimu(); // NOT in world map
        RaperReactionEvent event = new RaperReactionEvent(unregistered, null, null, 1);
        assertDoesNotThrow(() -> event.escapeTarget(b));
    }

    // --- escapeTarget: from exists → computes escape direction ---
    @Test
    public void testEscapeTarget_fromExists_doesNotThrow() {
        Body from = createBody();
        Body b = createBody();
        b.setX(500); b.setY(500); // middle of map
        from.setX(100); from.setY(100);
        RaperReactionEvent event = new RaperReactionEvent(from, null, null, 1);
        assertDoesNotThrow(() -> event.escapeTarget(b));
    }

    // --- checkConditionOfTarget: from is exciting → returns true ---
    @Test
    public void testCheckConditionOfTarget_fromExciting_returnsTrue() {
        Body from = createBody();
        from.setExciting(true);
        RaperReactionEvent event = new RaperReactionEvent(from, null, null, 10);
        assertTrue(event.checkConditionOfTarget());
    }
}
