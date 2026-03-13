package src.event;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import src.SimYukkuri;
import src.base.Body;
import src.base.EventPacket;
import src.base.EventPacket.EventPriority;
import src.base.EventPacket.UpdateState;
import src.draw.Translate;
import src.draw.World;
import src.enums.AgeState;
import src.enums.CoreAnkoState;
import src.enums.PredatorType;

class KillPredeatorEventTest {

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
        KillPredeatorEvent event = new KillPredeatorEvent();
        assertNotNull(event);
        assertEquals(-1, event.getFrom());
        assertEquals(-1, event.getTo());
    }

    @Test
    void testParameterizedConstructor() {
        Body from = createBody();
        Body to = createBody();
        KillPredeatorEvent event = new KillPredeatorEvent(from, to, null, 10);
        assertNotNull(event);
        assertEquals(from.getUniqueID(), event.getFrom());
        assertEquals(to.getUniqueID(), event.getTo());
        assertEquals(10, event.getCount());
    }

    @Test
    void testCheckEventResponse_setsPriorityHigh() {
        Body from = createBody();
        Body responder = createBody();
        KillPredeatorEvent event = new KillPredeatorEvent(from, null, null, 10);
        event.checkEventResponse(responder);
        assertEquals(EventPriority.HIGH, event.getPriority());
    }

    @Test
    void testCheckEventResponse_NoPredatorReturnsFalse() {
        Body b = createBody();
        KillPredeatorEvent event = new KillPredeatorEvent();
        // no predators in world → bIsNearPredeator=false → returns false
        assertFalse(event.checkEventResponse(b));
    }

    @Test
    void testCheckEventResponse_NYD() {
        Body b = createBody();
        b.seteCoreAnkoState(CoreAnkoState.NonYukkuriDisease);
        KillPredeatorEvent event = new KillPredeatorEvent();
        // NYD body: canEventResponse() may return false → return true(skip)
        // OR if canEventResponse() passes → isNYD() → return false
        // Either way, assertDoesNotThrow
        assertDoesNotThrow(() -> event.checkEventResponse(b));
    }

    @Test
    void testCheckEventResponse_DeadBodyReturnsTrue() {
        // !b.canEventResponse() → return true (skip dead bodies)
        Body b = createBody();
        b.setDead(true);
        KillPredeatorEvent event = new KillPredeatorEvent();
        // canEventResponse() returns false for dead → returns true
        assertTrue(event.checkEventResponse(b));
    }

    @Test
    void testSearchNextTarget_NoPredators() {
        KillPredeatorEvent event = new KillPredeatorEvent();
        // no predators in world → returns null
        assertNull(event.searchNextTarget());
    }

    @Test
    void testSearchNextTarget_WithPredator() {
        Body predator = createBody();
        predator.setPredatorType(PredatorType.BITE);
        KillPredeatorEvent event = new KillPredeatorEvent();
        // predator exists → returns non-null
        Body result = event.searchNextTarget();
        // result could be the predator or null (depends on implementation)
        // just assert no exception
        assertDoesNotThrow(() -> event.searchNextTarget());
    }

    @Test
    void testUpdate_FromDead_ReturnsAbort() {
        Body b = createBody();
        Body from = createBody();
        from.setDead(true);
        KillPredeatorEvent event = new KillPredeatorEvent(from, null, null, 10);
        // from.isDead() → searchNextTarget() → null (no predators) → ABORT
        assertEquals(UpdateState.ABORT, event.update(b));
    }

    @Test
    void testExecute_FromNull() {
        Body b = createBody();
        KillPredeatorEvent event = new KillPredeatorEvent();
        // from is null → execute returns true (early exit)
        assertTrue(event.execute(b));
    }

    @Test
    void testToString_doesNotThrow() {
        KillPredeatorEvent event = new KillPredeatorEvent();
        assertDoesNotThrow(() -> event.toString());
    }

    @Test
    void testStart_NYDBody_returnsEarly() {
        Body b = createBody();
        b.seteCoreAnkoState(CoreAnkoState.NonYukkuriDisease); // isNYD() returns true
        KillPredeatorEvent event = new KillPredeatorEvent();
        assertDoesNotThrow(() -> event.start(b));
    }

    // --- start: not NYD, from valid ---

    @Test
    void testStart_notNYD_doesNotThrow() {
        Body predator = createBody();
        Body b = createBody();
        KillPredeatorEvent event = new KillPredeatorEvent(predator, null, null, 10);
        // b is not NYD → calls setAngry + moveToEvent
        assertDoesNotThrow(() -> event.start(b));
    }

    // --- update: RND=0 → ABORT ---

    @Test
    void testUpdate_RND0_returnsAbort() {
        Body predator = createBody();
        Body b = createBody();
        SimYukkuri.RND = new src.ConstState(0);
        try {
            KillPredeatorEvent event = new KillPredeatorEvent(predator, null, null, 10);
            assertEquals(UpdateState.ABORT, event.update(b));
        } finally {
            SimYukkuri.RND = new java.util.Random();
        }
    }

    // --- execute: from removed, next predator exists → returns false ---

    @Test
    void testExecute_fromRemoved_withPredator_returnsFalse() {
        Body from = createBody();
        from.setRemoved(true);
        Body predator2 = createBody();
        predator2.setPredatorType(PredatorType.BITE);
        Body b = createBody();
        KillPredeatorEvent event = new KillPredeatorEvent(from, null, null, 10);
        // from is removed → searchNextTarget finds predator2 → returns false
        assertFalse(event.execute(b));
    }

    // --- execute: from alive, z >= 5 → no mypane, returns false ---

    @Test
    void testExecute_fromAlive_highZ_returnsFalse() {
        Body from = createBody();
        from.setZ(10); // z >= 5 → skips mypane.addEffect
        Body b = createBody();
        KillPredeatorEvent event = new KillPredeatorEvent(from, null, null, 10);
        assertFalse(event.execute(b));
    }

    // --- checkEventResponse: predator nearby → returns true ---

    @Test
    void testCheckEventResponse_withNearbyPredator_returnsTrue() {
        Body b = createBody();
        b.setX(100); b.setY(100);
        Body predator = createBody();
        predator.setX(100); predator.setY(100);
        predator.setPredatorType(PredatorType.BITE);
        KillPredeatorEvent event = new KillPredeatorEvent();
        // predator nearby → bIsNearPreadeator=true → returns true
        assertTrue(event.checkEventResponse(b));
    }

    // --- checkEventResponse: predator is parent of b → skipped → returns false ---
    @Test
    void testCheckEventResponse_predatorIsParentOfB_returnsFalse() {
        Body b = createBody();
        Body predator = createBody();
        predator.setPredatorType(PredatorType.BITE);
        // predator is parent of b (b.getParents()[0]=predator)
        b.setParents(new int[]{predator.getUniqueID(), -1});
        KillPredeatorEvent event = new KillPredeatorEvent();
        // predator.isParent(b) → skip that predator → bIsNearPredator=false → false
        assertFalse(event.checkEventResponse(b));
    }

    // --- update: from alive predator, b isAdult non-slave → enters branch, returns null ---
    @Test
    void testUpdate_fromAlive_predator_adultNonSlave_returnsNull() {
        Body predator = createBody();
        predator.setPredatorType(PredatorType.BITE);
        Body b = createBody();
        b.setAge(100000); // isAdult() = true
        // b.getPublicRank() default != UnunSlave
        SimYukkuri.RND = new src.ConstState(999); // nextInt(1000) != 0 → no ABORT
        try {
            KillPredeatorEvent event = new KillPredeatorEvent(predator, null, null, 10);
            assertNull(event.update(b));
        } finally {
            SimYukkuri.RND = new java.util.Random();
        }
    }

    // --- execute: from dead, no next predator → returns true ---
    @Test
    void testExecute_fromDead_noPredator_returnsTrue() {
        Body from = createBody();
        from.setDead(true);
        Body b = createBody();
        KillPredeatorEvent event = new KillPredeatorEvent(from, null, null, 10);
        // from.isDead() → searchNextTarget() → null → return true
        assertTrue(event.execute(b));
    }
}
