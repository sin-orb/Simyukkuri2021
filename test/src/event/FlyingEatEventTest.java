package src.event;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import src.SimYukkuri;
import src.base.Yukkuri;
import src.event.EventPacket.EventPriority;
import src.event.EventPacket.UpdateState;
import src.draw.Translate;
import src.draw.World;
import src.enums.AgeState;
import src.enums.Happiness;
import src.enums.ImageCode;
import src.util.WorldTestHelper;

class FlyingEatEventTest {

    @BeforeEach
    void setUp() {
        WorldTestHelper.resetWorld();
        SimYukkuri.world = new World();
        WorldTestHelper.initializeStandardTranslate500();
    }

    private static Yukkuri createBody() {
        Yukkuri b = new src.yukkuri.Reimu();
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
        Yukkuri from = createBody();
        Yukkuri to = createBody();
        FlyingEatEvent event = new FlyingEatEvent(from, to, null, 1);
        assertNotNull(event);
        assertEquals(from.getUniqueID(), event.getFrom());
        assertEquals(to.getUniqueID(), event.getTo());
        assertEquals(1, event.getCount());
    }

    @Test
    void testCheckEventResponse_setsPriorityHighAndReturnsTrue() {
        Yukkuri b = createBody();
        FlyingEatEvent event = new FlyingEatEvent();
        assertTrue(event.checkEventResponse(b));
        assertEquals(EventPriority.HIGH, event.getPriority());
    }

    @Test
    void testUpdate_returnsAbortWhenToIsNull() {
        Yukkuri b = createBody();
        FlyingEatEvent event = new FlyingEatEvent();
        event.setFrom(b.getUniqueID());
        event.setTo(-1);
        UpdateState result = event.update(b);
        assertEquals(UpdateState.ABORT, result);
    }

    @Test
    void testUpdate_returnsAbortWhenToIsRemoved() {
        Yukkuri from = createBody();
        Yukkuri to = createBody();
        to.setRemoved(true);
        FlyingEatEvent event = new FlyingEatEvent(from, to, null, 1);
        UpdateState result = event.update(from);
        assertEquals(UpdateState.ABORT, result);
    }

    @Test
    void testUpdate_returnsAbortWhenToIsGrabbed() {
        Yukkuri from = createBody();
        Yukkuri to = createBody();
        to.setGrabbed(true);
        FlyingEatEvent event = new FlyingEatEvent(from, to, null, 1);
        UpdateState result = event.update(from);
        assertEquals(UpdateState.ABORT, result);
    }

    @Test
    void testStart_toNull_doesNotThrow() {
        Yukkuri from = createBody();
        FlyingEatEvent event = new FlyingEatEvent(from, null, null, 1);
        // to=null → early return
        assertDoesNotThrow(() -> event.start(from));
    }

    @Test
    void testExecute_toNull_returnsTrue() {
        Yukkuri from = createBody();
        FlyingEatEvent event = new FlyingEatEvent(from, null, null, 1);
        assertTrue(event.execute(from));
    }

    @Test
    void testExecute_toRemoved_returnsTrue() {
        Yukkuri from = createBody();
        Yukkuri to = createBody();
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
        Yukkuri from = createBody();
        Yukkuri to = createBody();
        to.setParentLinkId(from.objId);
        FlyingEatEvent event = new FlyingEatEvent(from, to, null, 1);
        event.end(from);
        assertEquals(-1, to.getParentLinkId());
    }

    // --- start: to != null ---

    @Test
    void testStart_toNotNull_doesNotThrow() {
        Yukkuri from = createBody();
        Yukkuri to = createBody();
        FlyingEatEvent event = new FlyingEatEvent(from, to, null, 1);
        assertDoesNotThrow(() -> event.start(from));
    }

    // --- update: to alive, b not at fly height → null ---

    @Test
    void testUpdate_toAlive_notAtFlyHeight_returnsNull() {
        Yukkuri from = createBody();
        Yukkuri to = createBody();
        from.setZ(0); // far from getFlyHeightLimit (87)
        FlyingEatEvent event = new FlyingEatEvent(from, to, null, 1);
        assertNull(event.update(from));
    }

    // --- update: to alive, b at fly height → FORCE_EXEC ---

    @Test
    void testUpdate_toAlive_atFlyHeight_returnsForceExec() {
        Yukkuri from = createBody();
        Yukkuri to = createBody();
        int flyH = Translate.getFlyHeightLimit();
        from.setZ(flyH); // at fly height limit
        FlyingEatEvent event = new FlyingEatEvent(from, to, null, 1);
        assertEquals(UpdateState.FORCE_EXEC, event.update(from));
    }

    // --- execute: to grabbed → true ---

    @Test
    void testExecute_toGrabbed_returnsTrue() {
        Yukkuri from = createBody();
        Yukkuri to = createBody();
        to.setGrabbed(true);
        FlyingEatEvent event = new FlyingEatEvent(from, to, null, 1);
        assertTrue(event.execute(from));
    }

    // --- execute: tick < 20 → returns false ---

    @Test
    void testExecute_tickLessThan20_returnsFalse() {
        Yukkuri from = createBody();
        Yukkuri to = createBody();
        FlyingEatEvent event = new FlyingEatEvent(from, to, null, 1);
        event.tick = 0; // starts at 0, becomes 1 after tick++ (still < 20)
        assertFalse(event.execute(from));
    }

    // --- execute: tick accumulates but < 20 multiple times → always false ---

    @Test
    void testExecute_multipleCallsBelow20_alwaysReturnsFalse() {
        Yukkuri from = createBody();
        Yukkuri to = createBody();
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
        Yukkuri from = createBody();
        Yukkuri to = createBody();
        to.setDead(true);
        to.setCrushed(true);
        to.setAnkoAmount(10000);
        from.setAttitude(src.enums.Attitude.SHITHEAD);
        FlyingEatEvent event = new FlyingEatEvent(from, to, null, 1);
        event.tick = 19;
        assertTrue(event.execute(from));
    }

    // --- execute: tick=19→20, to dead, eater rude → returns true ---
    @Test
    void testExecute_tick19_toDead_rudeEater_returnsTrue() {
        Yukkuri from = createBody();
        Yukkuri to = createBody();
        to.setDead(true);
        to.setAnkoAmount(10000);
        from.setAttitude(src.enums.Attitude.SHITHEAD); // isRude() = true
        FlyingEatEvent event = new FlyingEatEvent(from, to, null, 1);
        event.tick = 19;
        assertTrue(event.execute(from));
    }

    // --- execute: tick=19→20, to dead, eater KAIYU not rude → returns true (eatBody triggers bodyCut path) ---
    @Test
    void testExecute_tick19_toDead_kaiyuEater_returnsTrue() {
        Yukkuri from = createBody();
        Yukkuri to = createBody();
        to.setDead(true);
        to.setAnkoAmount(10000);
        FlyingEatEvent event = new FlyingEatEvent(from, to, null, 1);
        event.tick = 19;
        assertTrue(event.execute(from));
    }

    // --- execute: tick=19→20, alive to, eatAmount=0+ankoAmount=10000 → no bodyCut, to.isNotNYD → false ---
    @Test
    void testExecute_tick19_zeroEat_toNotNYD_returnsFalse() {
        Yukkuri from = createBody();
        Yukkuri to = createBody();
        from.setEatAmountBase(new int[]{0, 0, 0}); // eatAmount=0, no bodyCut
        to.setAnkoAmount(10000); // avoid bodyCut NPE when alive
        FlyingEatEvent event = new FlyingEatEvent(from, to, null, 1);
        event.tick = 19;
        assertFalse(event.execute(from));
    }

    // --- execute: tick=19→20, alive to, eatAmount=0, b is full → returns true ---
    @Test
    void testExecute_tick19_zeroEat_bFull_returnsTrue() {
        Yukkuri from = createBody();
        Yukkuri to = createBody();
        from.setEatAmountBase(new int[]{0, 0, 0}); // eatAmount=0, no bodyCut
        to.setAnkoAmount(10000);
        from.setHungry(99999); // isFull() regardless of HUNGRYLIMIT
        FlyingEatEvent event = new FlyingEatEvent(from, to, null, 1);
        event.tick = 19;
        assertTrue(event.execute(from));
    }

    @Nested
    class RegressionScenarios {

        @Test
        void testScenario_AlivePreyBecomesVerySadAndPainFacedAtEatTick() {
            Yukkuri from = createBody();
            Yukkuri to = createBody();
            from.setEatAmountBase(new int[] { 0, 0, 0 });
            from.setHungry(0);
            to.setAnkoAmount(10000);
            to.setHappiness(Happiness.HAPPY);
            to.setForceFace(-1);

            FlyingEatEvent event = new FlyingEatEvent(from, to, null, 1);
            event.tick = 19;

            assertFalse(event.execute(from));
            assertEquals(Happiness.VERY_SAD, to.getHappiness());
            assertEquals(ImageCode.PAIN.ordinal(), to.getForceFace());
        }
    }
}
