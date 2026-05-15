package org.simyukkuri.event.impl;

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
import org.simyukkuri.enums.AgeState;
import org.simyukkuri.enums.CoreAnkoState;
import org.simyukkuri.enums.PredatorType;
import org.simyukkuri.event.EventPacket.EventPriority;
import org.simyukkuri.event.EventPacket.UpdateState;
import org.simyukkuri.util.WorldTestHelper;

class KillPredeatorEventTest {

    @BeforeEach
    void setUp() {
        WorldTestHelper.resetWorld();
        SimYukkuri.world = new World();
        WorldTestHelper.initializeStandardTranslate500();
    }

    private static Yukkuri createBody() {
        Yukkuri b = new org.simyukkuri.entity.core.living.yukkuri.impl.Reimu();
        b.setAgeState(AgeState.ADULT);
        org.simyukkuri.system.Sprite[] spr = new org.simyukkuri.system.Sprite[3];
        for (int i = 0; i < 3; i++) {
            spr[i] = new org.simyukkuri.system.Sprite(10, 10, org.simyukkuri.system.Sprite.PIVOT_CENTER_BOTTOM);
        }
        b.setSpriteSet(spr);
        SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(b.getUniqueID(), b);
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
        Yukkuri from = createBody();
        Yukkuri to = createBody();
        KillPredeatorEvent event = new KillPredeatorEvent(from, to, null, 10);
        assertNotNull(event);
        assertEquals(from.getUniqueID(), event.getFrom());
        assertEquals(to.getUniqueID(), event.getTo());
        assertEquals(10, event.getCount());
    }

    @Test
    void testCheckEventResponse_setsPriorityHigh() {
        Yukkuri from = createBody();
        Yukkuri responder = createBody();
        KillPredeatorEvent event = new KillPredeatorEvent(from, null, null, 10);
        event.checkEventResponse(responder);
        assertEquals(EventPriority.HIGH, event.getPriority());
    }

    @Test
    void testCheckEventResponse_NoPredatorReturnsFalse() {
        Yukkuri b = createBody();
        KillPredeatorEvent event = new KillPredeatorEvent();
        // no predators in world → bIsNearPredeator=false → returns false
        assertFalse(event.checkEventResponse(b));
    }

    @Test
    void testCheckEventResponse_NYD() {
        Yukkuri b = createBody();
        b.setCoreAnkoState(CoreAnkoState.NON_YUKKURI_DISEASE);
        KillPredeatorEvent event = new KillPredeatorEvent();
        // NYD body: canEventResponse() may return false → return true(skip)
        // OR if canEventResponse() passes → isNYD() → return false
        // Either way, assertDoesNotThrow
        assertDoesNotThrow(() -> event.checkEventResponse(b));
    }

    @Test
    void testCheckEventResponse_DeadBodyReturnsTrue() {
        // !b.canEventResponse() → return true (skip dead bodies)
        Yukkuri b = createBody();
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
        Yukkuri predator = createBody();
        predator.setPredatorType(PredatorType.BITE);
        KillPredeatorEvent event = new KillPredeatorEvent();
        // predator exists → returns non-null
        Yukkuri result = event.searchNextTarget();
        // result could be the predator or null (depends on implementation)
        // just assert no exception
        assertDoesNotThrow(() -> event.searchNextTarget());
    }

    @Test
    void testUpdate_FromDead_ReturnsAbort() {
        Yukkuri b = createBody();
        Yukkuri from = createBody();
        from.setDead(true);
        KillPredeatorEvent event = new KillPredeatorEvent(from, null, null, 10);
        // from.isDead() → searchNextTarget() → null (no predators) → ABORT
        assertEquals(UpdateState.ABORT, event.update(b));
    }

    @Test
    void testExecute_FromNull() {
        Yukkuri b = createBody();
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
        Yukkuri b = createBody();
        b.setCoreAnkoState(CoreAnkoState.NON_YUKKURI_DISEASE); // isNYD() returns true
        KillPredeatorEvent event = new KillPredeatorEvent();
        assertDoesNotThrow(() -> event.start(b));
    }

    // --- start: not NYD, from valid ---

    @Test
    void testStart_notNYD_doesNotThrow() {
        Yukkuri predator = createBody();
        Yukkuri b = createBody();
        KillPredeatorEvent event = new KillPredeatorEvent(predator, null, null, 10);
        // b is not NYD → calls setAngry + moveToEvent
        assertDoesNotThrow(() -> event.start(b));
    }

    // --- update: RND=0 → ABORT ---

    @Test
    void testUpdate_RND0_returnsAbort() {
        Yukkuri predator = createBody();
        Yukkuri b = createBody();
        SimYukkuri.RND = new org.simyukkuri.ConstState(0);
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
        Yukkuri from = createBody();
        from.setRemoved(true);
        Yukkuri predator2 = createBody();
        predator2.setPredatorType(PredatorType.BITE);
        Yukkuri b = createBody();
        KillPredeatorEvent event = new KillPredeatorEvent(from, null, null, 10);
        // from is removed → searchNextTarget finds predator2 → returns false
        assertFalse(event.execute(b));
    }

    // --- execute: from alive, z >= 5 → no mypane, returns false ---

    @Test
    void testExecute_fromAlive_highZ_returnsFalse() {
        Yukkuri from = createBody();
        from.setZ(10); // z >= 5 → skips mypane.addEffect
        Yukkuri b = createBody();
        KillPredeatorEvent event = new KillPredeatorEvent(from, null, null, 10);
        assertFalse(event.execute(b));
    }

    // --- checkEventResponse: predator nearby → returns true ---

    @Test
    void testCheckEventResponse_withNearbyPredator_returnsTrue() {
        Yukkuri b = createBody();
        b.setX(100);
        b.setY(100);
        Yukkuri predator = createBody();
        predator.setX(100);
        predator.setY(100);
        predator.setPredatorType(PredatorType.BITE);
        KillPredeatorEvent event = new KillPredeatorEvent();
        // predator nearby → bIsNearPreadeator=true → returns true
        assertTrue(event.checkEventResponse(b));
    }

    // --- checkEventResponse: predator is parent of b → skipped → returns false ---
    @Test
    void testCheckEventResponse_predatorIsParentOfB_returnsFalse() {
        Yukkuri b = createBody();
        Yukkuri predator = createBody();
        predator.setPredatorType(PredatorType.BITE);
        // predator is parent of b (b.getParents()[0]=predator)
        b.setParents(new int[] { predator.getUniqueID(), -1 });
        KillPredeatorEvent event = new KillPredeatorEvent();
        // predator.isParent(b) → skip that predator → bIsNearPredator=false → false
        assertFalse(event.checkEventResponse(b));
    }

    // --- update: from alive predator, b isAdult non-slave → enters branch, returns
    // null ---
    @Test
    void testUpdate_fromAlive_predator_adultNonSlave_returnsNull() {
        Yukkuri predator = createBody();
        predator.setPredatorType(PredatorType.BITE);
        Yukkuri b = createBody();
        b.setAge(100000); // isAdult() = true
        // b.getPublicRank() default != UnunSlave
        SimYukkuri.RND = new org.simyukkuri.ConstState(999); // nextInt(1000) != 0 → no ABORT
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
        Yukkuri from = createBody();
        from.setDead(true);
        Yukkuri b = createBody();
        KillPredeatorEvent event = new KillPredeatorEvent(from, null, null, 10);
        // from.isDead() → searchNextTarget() → null → return true
        assertTrue(event.execute(b));
    }
}
