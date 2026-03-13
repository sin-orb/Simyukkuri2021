package src.event;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import src.SimYukkuri;
import src.base.Body;
import src.base.EventPacket.EventPriority;
import src.base.EventPacket.UpdateState;
import src.draw.Translate;
import src.draw.World;
import src.enums.AgeState;

class FlyingEatEventTest {

    @BeforeEach
    void setUp() {
        SimYukkuri.world = new World();
        Translate.setMapSize(1000, 1000, 500);
        Translate.setCanvasSize(800, 600, 100, 100, new float[]{1.0f});
        Translate.createTransTable(false);
    }

    private static Body createBody() {
        Body b = new src.yukkuri.Reimu();
        b.setAgeState(AgeState.ADULT);
        src.system.Sprite[] spr = new src.system.Sprite[3];
        for (int i = 0; i < 3; i++) {
            spr[i] = new src.system.Sprite(10, 10, src.system.Sprite.PIVOT_CENTER_BOTTOM);
        }
        b.setBodySpr(spr);
        SimYukkuri.world.getCurrentMap().getBody().put(b.getUniqueID(), b);
        return b;
    }

    @Test
    void testDefaultConstructor() {
        FlyingEatEvent event = new FlyingEatEvent();
        assertNotNull(event);
        assertEquals(-1, event.getFrom());
        assertEquals(-1, event.getTo());
    }

    @Test
    void testParameterizedConstructor() {
        Body from = createBody();
        Body to = createBody();
        FlyingEatEvent event = new FlyingEatEvent(from, to, null, 1);
        assertNotNull(event);
        assertEquals(from.getUniqueID(), event.getFrom());
        assertEquals(to.getUniqueID(), event.getTo());
        assertEquals(1, event.getCount());
    }

    @Test
    void testCheckEventResponse_setsPriorityHighAndReturnsTrue() {
        Body b = createBody();
        FlyingEatEvent event = new FlyingEatEvent();
        assertTrue(event.checkEventResponse(b));
        assertEquals(EventPriority.HIGH, event.getPriority());
    }

    @Test
    void testUpdate_returnsAbortWhenToIsNull() {
        Body b = createBody();
        FlyingEatEvent event = new FlyingEatEvent();
        event.setFrom(b.getUniqueID());
        event.setTo(-1);
        UpdateState result = event.update(b);
        assertEquals(UpdateState.ABORT, result);
    }

    @Test
    void testUpdate_returnsAbortWhenToIsRemoved() {
        Body from = createBody();
        Body to = createBody();
        to.setRemoved(true);
        FlyingEatEvent event = new FlyingEatEvent(from, to, null, 1);
        UpdateState result = event.update(from);
        assertEquals(UpdateState.ABORT, result);
    }

    @Test
    void testUpdate_returnsAbortWhenToIsGrabbed() {
        Body from = createBody();
        Body to = createBody();
        to.setGrabbed(true);
        FlyingEatEvent event = new FlyingEatEvent(from, to, null, 1);
        UpdateState result = event.update(from);
        assertEquals(UpdateState.ABORT, result);
    }

    @Test
    void testStart_toNull_doesNotThrow() {
        Body from = createBody();
        FlyingEatEvent event = new FlyingEatEvent(from, null, null, 1);
        // to=null → early return
        assertDoesNotThrow(() -> event.start(from));
    }

    @Test
    void testExecute_toNull_returnsTrue() {
        Body from = createBody();
        FlyingEatEvent event = new FlyingEatEvent(from, null, null, 1);
        assertTrue(event.execute(from));
    }

    @Test
    void testExecute_toRemoved_returnsTrue() {
        Body from = createBody();
        Body to = createBody();
        to.setRemoved(true);
        FlyingEatEvent event = new FlyingEatEvent(from, to, null, 1);
        assertTrue(event.execute(from));
    }

    @Test
    void testToString_doesNotThrow() {
        FlyingEatEvent event = new FlyingEatEvent();
        assertDoesNotThrow(() -> event.toString());
    }

    @Test
    void testEnd_setsLinkParentToMinusOne() {
        Body from = createBody();
        Body to = createBody();
        to.setLinkParent(from.objId);
        FlyingEatEvent event = new FlyingEatEvent(from, to, null, 1);
        event.end(from);
        assertEquals(-1, to.getLinkParent());
    }

    // --- start: to != null ---

    @Test
    void testStart_toNotNull_doesNotThrow() {
        Body from = createBody();
        Body to = createBody();
        FlyingEatEvent event = new FlyingEatEvent(from, to, null, 1);
        assertDoesNotThrow(() -> event.start(from));
    }

    // --- update: to alive, b not at fly height → null ---

    @Test
    void testUpdate_toAlive_notAtFlyHeight_returnsNull() {
        Body from = createBody();
        Body to = createBody();
        from.setZ(0); // far from getFlyHeightLimit (87)
        FlyingEatEvent event = new FlyingEatEvent(from, to, null, 1);
        assertNull(event.update(from));
    }

    // --- update: to alive, b at fly height → FORCE_EXEC ---

    @Test
    void testUpdate_toAlive_atFlyHeight_returnsForceExec() {
        Body from = createBody();
        Body to = createBody();
        int flyH = Translate.getFlyHeightLimit();
        from.setZ(flyH); // at fly height limit
        FlyingEatEvent event = new FlyingEatEvent(from, to, null, 1);
        assertEquals(UpdateState.FORCE_EXEC, event.update(from));
    }

    // --- execute: to grabbed → true ---

    @Test
    void testExecute_toGrabbed_returnsTrue() {
        Body from = createBody();
        Body to = createBody();
        to.setGrabbed(true);
        FlyingEatEvent event = new FlyingEatEvent(from, to, null, 1);
        assertTrue(event.execute(from));
    }

    // --- execute: tick < 20 → returns false ---

    @Test
    void testExecute_tickLessThan20_returnsFalse() {
        Body from = createBody();
        Body to = createBody();
        FlyingEatEvent event = new FlyingEatEvent(from, to, null, 1);
        event.tick = 0; // starts at 0, becomes 1 after tick++ (still < 20)
        assertFalse(event.execute(from));
    }

    // --- execute: tick accumulates but < 20 multiple times → always false ---

    @Test
    void testExecute_multipleCallsBelow20_alwaysReturnsFalse() {
        Body from = createBody();
        Body to = createBody();
        FlyingEatEvent event = new FlyingEatEvent(from, to, null, 1);
        event.tick = 0;
        // 5 calls: tick goes 1,2,3,4,5 (all < 20) → all return false
        for (int i = 0; i < 5; i++) {
            assertFalse(event.execute(from));
        }
    }

    // --- execute: tick=19→20, to dead+crushed, eater rude → returns true ---
    @Test
    void testExecute_tick19_toCrushed_returnsTrue() {
        Body from = createBody();
        Body to = createBody();
        to.setDead(true);
        to.setCrushed(true);
        to.setBodyAmount(10000);
        from.setAttitude(src.enums.Attitude.SHITHEAD);
        FlyingEatEvent event = new FlyingEatEvent(from, to, null, 1);
        event.tick = 19;
        assertTrue(event.execute(from));
    }

    // --- execute: tick=19→20, to dead, eater rude → returns true ---
    @Test
    void testExecute_tick19_toDead_rudeEater_returnsTrue() {
        Body from = createBody();
        Body to = createBody();
        to.setDead(true);
        to.setBodyAmount(10000);
        from.setAttitude(src.enums.Attitude.SHITHEAD); // isRude() = true
        FlyingEatEvent event = new FlyingEatEvent(from, to, null, 1);
        event.tick = 19;
        assertTrue(event.execute(from));
    }

    // --- execute: tick=19→20, to dead, eater KAIYU not rude → returns true (eatBody triggers bodyCut path) ---
    @Test
    void testExecute_tick19_toDead_kaiyuEater_returnsTrue() {
        Body from = createBody();
        Body to = createBody();
        to.setDead(true);
        to.setBodyAmount(10000);
        FlyingEatEvent event = new FlyingEatEvent(from, to, null, 1);
        event.tick = 19;
        assertTrue(event.execute(from));
    }

    // --- execute: tick=19→20, alive to, eatAmount=0+bodyAmount=10000 → no bodyCut, to.isNotNYD → false ---
    @Test
    void testExecute_tick19_zeroEat_toNotNYD_returnsFalse() {
        Body from = createBody();
        Body to = createBody();
        from.setEATAMOUNTorg(new int[]{0, 0, 0}); // eatAmount=0, no bodyCut
        to.setBodyAmount(10000); // avoid bodyCut NPE when alive
        FlyingEatEvent event = new FlyingEatEvent(from, to, null, 1);
        event.tick = 19;
        assertFalse(event.execute(from));
    }

    // --- execute: tick=19→20, alive to, eatAmount=0, b is full → returns true ---
    @Test
    void testExecute_tick19_zeroEat_bFull_returnsTrue() {
        Body from = createBody();
        Body to = createBody();
        from.setEATAMOUNTorg(new int[]{0, 0, 0}); // eatAmount=0, no bodyCut
        to.setBodyAmount(10000);
        from.setHungry(99999); // isFull() regardless of HUNGRYLIMIT
        FlyingEatEvent event = new FlyingEatEvent(from, to, null, 1);
        event.tick = 19;
        assertTrue(event.execute(from));
    }
}
