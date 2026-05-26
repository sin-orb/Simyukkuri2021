package org.simyukkuri.event.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.simyukkuri.ConstState;
import org.simyukkuri.SimYukkuri;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.living.yukkuri.impl.Reimu;
import org.simyukkuri.event.EventPacket;
import org.simyukkuri.event.EventTestBase;
import org.simyukkuri.system.Sprite;

public class ProposeEventTest extends EventTestBase {

    /** createBody with sprites for tests that need calcCollisionX */
    private Yukkuri createSprBody(int x, int y) {
        Yukkuri b = new Reimu();
        b.setX(x);
        b.setY(y);
        Sprite[] spr = new Sprite[3];
        for (int i = 0; i < 3; i++) {
            spr[i] = new Sprite(10, 10, Sprite.PIVOT_CENTER_BOTTOM);
        }
        b.setSpriteSet(spr);
        SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(b.getUniqueId(), b);
        return b;
    }

    @Test
    void testCheckEventResponse_Participants() {
        Yukkuri from = createBody(1, 100, 100);
        Yukkuri to = createBody(2, 120, 120);

        ProposeEvent event = new ProposeEvent(from, to, null, 10);

        assertTrue(event.checkEventResponse(from));
        assertTrue(event.checkEventResponse(to));
    }

    @Test
    void testDefaultConstructor() {
        ProposeEvent event = new ProposeEvent();
        assertNotNull(event);
        assertEquals(-1, event.getFrom());
        assertEquals(-1, event.getTo());
    }

    @Test
    void testParameterizedConstructor_setsPriorityHigh() {
        Yukkuri from = createBody(1, 100, 100);
        Yukkuri to = createBody(2, 120, 120);
        ProposeEvent event = new ProposeEvent(from, to, null, 10);
        assertEquals(EventPacket.EventPriority.HIGH, event.getPriority());
        assertEquals(from.getUniqueId(), event.getFrom());
        assertEquals(to.getUniqueId(), event.getTo());
        assertEquals(10, event.getCount());
    }

    @Test
    void testCheckEventResponse_Stranger() {
        Yukkuri from = createBody(1, 100, 100);
        Yukkuri to = createBody(2, 120, 120);
        Yukkuri stranger = createBody(3, 200, 200);
        ProposeEvent event = new ProposeEvent(from, to, null, 10);
        assertFalse(event.checkEventResponse(stranger));
    }

    @Test
    void testCheckEventResponse_FromNull() {
        Yukkuri b = createBody(1, 100, 100);
        ProposeEvent event = new ProposeEvent();
        // from=-1 (null lookup) → b != null and b != null → false
        assertFalse(event.checkEventResponse(b));
    }

    @Test
    void testExecute_BothNull() {
        Yukkuri b = createBody(1, 100, 100);
        ProposeEvent event = new ProposeEvent();
        // from and to both null → returns true
        assertTrue(event.execute(b));
    }

    @Test
    void testExecute_ToGrabbed() {
        Yukkuri from = createBody(1, 100, 100);
        Yukkuri to = createBody(2, 120, 120);
        ProposeEvent event = new ProposeEvent(from, to, null, 10);
        to.setGrabbed(true);
        // to.isGrabbed() → return false
        assertFalse(event.execute(from));
    }

    @Test
    void testEnd_BothNull() {
        Yukkuri b = createBody(1, 100, 100);
        ProposeEvent event = new ProposeEvent();
        // from null → return early
        assertDoesNotThrow(() -> event.end(b));
    }

    @Test
    void testEnd_WithBodies() {
        Yukkuri from = createBody(1, 100, 100);
        Yukkuri to = createBody(2, 120, 120);
        ProposeEvent event = new ProposeEvent(from, to, null, 10);
        from.setLockmove(true);
        to.setLockmove(true);
        from.setCurrentEvent(event);
        to.setCurrentEvent(event);
        event.end(from);
        assertFalse(from.isLockmove());
        assertFalse(to.isLockmove());
        assertNull(from.getCurrentEvent());
        assertNull(to.getCurrentEvent());
    }

    @Test
    void testStart_ToAndFromBothNull() {
        Yukkuri b = createBody(1, 100, 100);
        ProposeEvent event = new ProposeEvent();
        // from and to both null → returns without doing anything
        assertDoesNotThrow(() -> event.start(b));
    }

    @Test
    void testUpdate_FromNull() {
        Yukkuri b = createBody(1, 100, 100);
        ProposeEvent event = new ProposeEvent();
        // from null → ABORT
        assertEquals(EventPacket.UpdateState.ABORT, event.update(b));
    }

    @Test
    void testUpdate_ToDead() {
        Yukkuri from = createBody(1, 100, 100);
        Yukkuri to = createBody(2, 120, 120);
        ProposeEvent event = new ProposeEvent(from, to, null, 10);
        to.setDead(true);
        assertEquals(EventPacket.UpdateState.ABORT, event.update(from));
    }

    @Test
    void testAcceptPropose_AlreadyMarried() {
        Yukkuri from = createBody(1, 100, 100);
        Yukkuri to = createBody(2, 120, 120);
        // to already has a partner
        Yukkuri partner = createBody(3, 200, 200);
        to.setPartner(partner.getUniqueId());
        ProposeEvent event = new ProposeEvent(from, to, null, 10);
        assertFalse(event.acceptPropose(from, to));
    }

    @Test
    void testAcceptPropose_NoObstacles() {
        Yukkuri from = createBody(1, 100, 100);
        Yukkuri to = createBody(2, 120, 120);
        // no partner, no mold, no baby, no disorder
        ProposeEvent event = new ProposeEvent(from, to, null, 10);
        // from has no baby/stalk, no disorder → true
        assertTrue(event.acceptPropose(from, to));
    }

    @Test
    void testToString() {
        ProposeEvent event = new ProposeEvent();
        assertDoesNotThrow(() -> event.toString());
    }

    // --- update() deeper paths (require sprites for calcCollisionX) ---

    @Test
    void testUpdate_ToGrabbed_returnsNull() {
        Yukkuri from = createSprBody(100, 100);
        Yukkuri to = createSprBody(120, 120);
        ProposeEvent event = new ProposeEvent(from, to, null, 10);
        to.setGrabbed(true);
        SimYukkuri.RND = new ConstState(1);
        // Reaches calcCollisionX, then to.isGrabbed()=true branch → null
        assertNull(event.update(from));
    }

    @Test
    void testUpdate_Started_returnsForceExec() {
        Yukkuri from = createSprBody(100, 100);
        Yukkuri to = createSprBody(120, 120);
        ProposeEvent event = new ProposeEvent(from, to, null, 10);
        event.started = true;
        assertEquals(EventPacket.UpdateState.FORCE_EXEC, event.update(from));
    }

    @Test
    void testUpdate_NormalCase_returnsNull() {
        Yukkuri from = createSprBody(100, 100);
        Yukkuri to = createSprBody(120, 120);
        ProposeEvent event = new ProposeEvent(from, to, null, 10);
        // started=false, to not grabbed → normal update path → null
        assertNull(event.update(from));
    }

    @Test
    void testUpdate_ToRemoved_returnsAbort() {
        Yukkuri from = createSprBody(100, 100);
        Yukkuri to = createSprBody(120, 120);
        to.setRemoved(true);
        ProposeEvent event = new ProposeEvent(from, to, null, 10);
        assertEquals(EventPacket.UpdateState.ABORT, event.update(from));
    }

    @Test
    void testUpdate_ToNYD_returnsAbort() {
        Yukkuri from = createSprBody(100, 100);
        Yukkuri to = createSprBody(120, 120);
        to.setCoreAnkoState(org.simyukkuri.enums.CoreAnkoState.NON_YUKKURI_DISEASE);
        ProposeEvent event = new ProposeEvent(from, to, null, 10);
        assertEquals(EventPacket.UpdateState.ABORT, event.update(from));
    }

    @Test
    void testStart_WithBodies_doesNotThrow() {
        Yukkuri from = createSprBody(100, 100);
        Yukkuri to = createSprBody(120, 120);
        ProposeEvent event = new ProposeEvent(from, to, null, 10);
        assertDoesNotThrow(() -> event.start(from));
    }

    // --- execute() tick-based paths ---

    @Test
    void testExecute_tick0_setsStarted() {
        Yukkuri from = createSprBody(100, 100);
        Yukkuri to = createSprBody(120, 120);
        ProposeEvent event = new ProposeEvent(from, to, null, 10);
        event.tick = 0;
        event.execute(from);
        assertTrue(event.started);
    }

    @Test
    void testExecute_tick5_doesNotThrow() {
        Yukkuri from = createSprBody(100, 100);
        Yukkuri to = createSprBody(120, 120);
        ProposeEvent event = new ProposeEvent(from, to, null, 10);
        event.tick = 5;
        assertDoesNotThrow(() -> event.execute(from));
    }

    @Test
    void testExecute_tick20_doesNotThrow() {
        Yukkuri from = createSprBody(100, 100);
        Yukkuri to = createSprBody(120, 120);
        ProposeEvent event = new ProposeEvent(from, to, null, 10);
        event.tick = 20;
        assertDoesNotThrow(() -> event.execute(from));
    }

    @Test
    void testExecute_tick40_success_doesNotThrow() {
        Yukkuri from = createSprBody(100, 100);
        Yukkuri to = createSprBody(120, 120);
        ProposeEvent event = new ProposeEvent(from, to, null, 10);
        event.tick = 40;
        // no partner, no mold, no baby → success
        assertDoesNotThrow(() -> event.execute(from));
    }

    @Test
    void testExecute_tick40_failure_alreadyMarried() {
        Yukkuri from = createSprBody(100, 100);
        Yukkuri to = createSprBody(120, 120);
        Yukkuri otherPartner = createSprBody(200, 200);
        to.setPartner(otherPartner.getUniqueId());
        ProposeEvent event = new ProposeEvent(from, to, null, 10);
        event.tick = 40;
        // to already married → acceptPropose=false → failure path
        assertDoesNotThrow(() -> event.execute(from));
    }

    @Test
    void testExecute_tick60_success_doesNotThrow() {
        Yukkuri from = createSprBody(100, 100);
        Yukkuri to = createSprBody(120, 120);
        ProposeEvent event = new ProposeEvent(from, to, null, 10);
        event.tick = 60;
        // set from.partner = to (success case)
        from.setPartner(to.getUniqueId());
        assertDoesNotThrow(() -> event.execute(from));
    }

    // --- acceptPropose: hasBabyOrStalk → false ---

    @Test
    void testAcceptPropose_FromHasBaby_returnsFalse() {
        Yukkuri from = createSprBody(100, 100);
        Yukkuri to = createSprBody(120, 120);
        from.setHasBaby(true); // hasBabyOrStalk() returns true
        ProposeEvent event = new ProposeEvent(from, to, null, 10);
        assertFalse(event.acceptPropose(from, to));
    }

    // --- acceptPropose: t.findSick(f) → false ---
    @Test
    void testAcceptPropose_findSick_returnsFalse() {
        Yukkuri from = createSprBody(100, 100);
        Yukkuri to = createSprBody(120, 120);
        from.forceSetSick(); // from.isSick() = true
        to.setIntelligence(org.simyukkuri.enums.Intelligence.WISE); // to.findSick(from) = true
        ProposeEvent event = new ProposeEvent(from, to, null, 10);
        assertFalse(event.acceptPropose(from, to));
    }

    // --- acceptPropose: f.hasDisorder() → false ---
    @Test
    void testAcceptPropose_fromHasDisorder_returnsFalse() {
        Yukkuri from = createSprBody(100, 100);
        Yukkuri to = createSprBody(120, 120);
        from.setBlind(true); // hasDisorder() = true
        ProposeEvent event = new ProposeEvent(from, to, null, 10);
        assertFalse(event.acceptPropose(from, to));
    }

    // --- update: to.isTaken() → ABORT ---
    @Test
    void testUpdate_toTaken_returnsAbort() {
        Yukkuri from = createSprBody(100, 100);
        Yukkuri to = createSprBody(120, 120);
        to.setTaken(true);
        ProposeEvent event = new ProposeEvent(from, to, null, 10);
        assertEquals(EventPacket.UpdateState.ABORT, event.update(from));
    }

    // --- execute: from.findSick(to) = true → returns true ---
    @Test
    void testExecute_fromFindSick_returnsTrue() {
        Yukkuri from = createSprBody(100, 100);
        Yukkuri to = createSprBody(120, 120);
        to.forceSetSick(); // to.isSick() = true
        from.setIntelligence(org.simyukkuri.enums.Intelligence.WISE); // from.findSick(to) = true
        ProposeEvent event = new ProposeEvent(from, to, null, 10);
        assertTrue(event.execute(from));
    }

    // --- execute: to.hasDisorder() = true → returns true ---
    @Test
    void testExecute_toHasDisorder_returnsTrue() {
        Yukkuri from = createSprBody(100, 100);
        Yukkuri to = createSprBody(120, 120);
        to.setBlind(true); // hasDisorder() = true
        ProposeEvent event = new ProposeEvent(from, to, null, 10);
        assertTrue(event.execute(from));
    }

    // --- execute: tick=0, from.isRude() = true → VAIN face ---
    @Test
    void testExecute_tick0_fromRude_setsStarted() {
        Yukkuri from = createSprBody(100, 100);
        Yukkuri to = createSprBody(120, 120);
        from.setAttitude(org.simyukkuri.enums.Attitude.SHITHEAD); // isRude() = true
        ProposeEvent event = new ProposeEvent(from, to, null, 10);
        event.tick = 0;
        assertDoesNotThrow(() -> event.execute(from));
        assertTrue(event.started);
    }

    // --- execute: tick=20, from.isRude() = true → VAIN face ---
    @Test
    void testExecute_tick20_fromRude_doesNotThrow() {
        Yukkuri from = createSprBody(100, 100);
        Yukkuri to = createSprBody(120, 120);
        from.setAttitude(org.simyukkuri.enums.Attitude.SHITHEAD); // isRude() = true
        ProposeEvent event = new ProposeEvent(from, to, null, 10);
        event.tick = 20;
        assertDoesNotThrow(() -> event.execute(from));
    }

    // --- execute: tick=40 success, VERY_NICE attitude ---
    @Test
    void testExecute_tick40_success_veryNice_doesNotThrow() {
        Yukkuri from = createSprBody(100, 100);
        Yukkuri to = createSprBody(120, 120);
        from.setAttitude(org.simyukkuri.enums.Attitude.VERY_NICE);
        to.setAttitude(org.simyukkuri.enums.Attitude.VERY_NICE);
        ProposeEvent event = new ProposeEvent(from, to, null, 10);
        event.tick = 40;
        assertDoesNotThrow(() -> event.execute(from));
    }

    // --- execute: tick=40 success, NICE attitude ---
    @Test
    void testExecute_tick40_success_nice_doesNotThrow() {
        Yukkuri from = createSprBody(100, 100);
        Yukkuri to = createSprBody(120, 120);
        from.setAttitude(org.simyukkuri.enums.Attitude.NICE);
        to.setAttitude(org.simyukkuri.enums.Attitude.NICE);
        ProposeEvent event = new ProposeEvent(from, to, null, 10);
        event.tick = 40;
        assertDoesNotThrow(() -> event.execute(from));
    }

    // --- execute: tick=40 success, SHITHEAD + SUPER_SHITHEAD attitude ---
    @Test
    void testExecute_tick40_success_shithead_doesNotThrow() {
        Yukkuri from = createSprBody(100, 100);
        Yukkuri to = createSprBody(120, 120);
        from.setAttitude(org.simyukkuri.enums.Attitude.SHITHEAD);
        to.setAttitude(org.simyukkuri.enums.Attitude.SUPER_SHITHEAD);
        ProposeEvent event = new ProposeEvent(from, to, null, 10);
        event.tick = 40;
        assertDoesNotThrow(() -> event.execute(from));
    }

    // --- execute: tick=40 failure, to.findSick(from) = true → HateMoldyYukkuri ---
    @Test
    void testExecute_tick40_failure_toFindSick_doesNotThrow() {
        Yukkuri from = createSprBody(100, 100);
        Yukkuri to = createSprBody(120, 120);
        from.forceSetSick(); // from.isSick() = true
        to.setIntelligence(org.simyukkuri.enums.Intelligence.WISE); // to.findSick(from) = true
        // Make acceptPropose fail first (to already married)
        Yukkuri partner = createSprBody(200, 200);
        to.setPartner(partner.getUniqueId());
        ProposeEvent event = new ProposeEvent(from, to, null, 10);
        event.tick = 40;
        assertDoesNotThrow(() -> event.execute(from));
    }

    // --- execute: tick=40 failure, to.isRude() = true → RUDE face ---
    @Test
    void testExecute_tick40_failure_toIsRude_doesNotThrow() {
        Yukkuri from = createSprBody(100, 100);
        Yukkuri to = createSprBody(120, 120);
        to.setAttitude(org.simyukkuri.enums.Attitude.SHITHEAD); // isRude() = true
        Yukkuri partner = createSprBody(200, 200);
        to.setPartner(partner.getUniqueId()); // acceptPropose = false (already married)
        ProposeEvent event = new ProposeEvent(from, to, null, 10);
        event.tick = 40;
        assertDoesNotThrow(() -> event.execute(from));
    }

    // --- execute: tick=40 failure, from VERY_NICE / to VERY_NICE ---
    @Test
    void testExecute_tick40_failure_veryNice_doesNotThrow() {
        Yukkuri from = createSprBody(100, 100);
        Yukkuri to = createSprBody(120, 120);
        from.setAttitude(org.simyukkuri.enums.Attitude.VERY_NICE);
        to.setAttitude(org.simyukkuri.enums.Attitude.VERY_NICE);
        Yukkuri partner = createSprBody(200, 200);
        to.setPartner(partner.getUniqueId());
        ProposeEvent event = new ProposeEvent(from, to, null, 10);
        event.tick = 40;
        assertDoesNotThrow(() -> event.execute(from));
    }

    // --- execute: tick=40 failure, from NICE / to NICE ---
    @Test
    void testExecute_tick40_failure_nice_doesNotThrow() {
        Yukkuri from = createSprBody(100, 100);
        Yukkuri to = createSprBody(120, 120);
        from.setAttitude(org.simyukkuri.enums.Attitude.NICE);
        to.setAttitude(org.simyukkuri.enums.Attitude.NICE);
        Yukkuri partner = createSprBody(200, 200);
        to.setPartner(partner.getUniqueId());
        ProposeEvent event = new ProposeEvent(from, to, null, 10);
        event.tick = 40;
        assertDoesNotThrow(() -> event.execute(from));
    }

    // --- execute: tick=40 failure, from SHITHEAD / to SUPER_SHITHEAD ---
    @Test
    void testExecute_tick40_failure_shithead_doesNotThrow() {
        Yukkuri from = createSprBody(100, 100);
        Yukkuri to = createSprBody(120, 120);
        from.setAttitude(org.simyukkuri.enums.Attitude.SHITHEAD);
        to.setAttitude(org.simyukkuri.enums.Attitude.SUPER_SHITHEAD);
        Yukkuri partner = createSprBody(200, 200);
        to.setPartner(partner.getUniqueId());
        ProposeEvent event = new ProposeEvent(from, to, null, 10);
        event.tick = 40;
        assertDoesNotThrow(() -> event.execute(from));
    }

    // --- execute: tick=40 success, check stress/memory ---
    @Test
    void testExecute_tick40_Success_Semantic() {
        Yukkuri from = createSprBody(100, 100);
        Yukkuri to = createSprBody(120, 120);
        from.setAttitude(org.simyukkuri.enums.Attitude.VERY_NICE);
        to.setAttitude(org.simyukkuri.enums.Attitude.VERY_NICE);

        // Set initial values so we can verify decreases
        from.setStress(500);
        to.setStress(500);
        from.addMemories(100);
        to.addMemories(100);

        int fromInitialStress = from.getStress();
        int toInitialStress = to.getStress();
        int fromInitialMem = from.getMemories();
        int toInitialMem = to.getMemories();

        ProposeEvent event = new ProposeEvent(from, to, null, 10);
        event.tick = 20;
        event.execute(from); // set from.partner

        event.tick = 40;
        event.execute(from); // set to.partner and stress/mem

        assertEquals(to.getUniqueId(), from.getPartner());
        assertEquals(from.getUniqueId(), to.getPartner());
        assertTrue(from.getStress() < fromInitialStress, "Stress should decrease on success");
        assertTrue(to.getStress() < toInitialStress, "Stress should decrease on success");
        assertTrue(from.getMemories() > fromInitialMem, "Memories should increase on success");
        assertTrue(to.getMemories() > toInitialMem, "Memories should increase on success");
    }

    // --- execute: tick=40 failure, check stress/memory ---
    @Test
    void testExecute_tick40_Failure_Semantic() {
        Yukkuri from = createSprBody(100, 100);
        Yukkuri to = createSprBody(120, 120);
        from.setAttitude(org.simyukkuri.enums.Attitude.AVERAGE);
        to.setAttitude(org.simyukkuri.enums.Attitude.AVERAGE);

        // Set initial values so we can verify decreases
        from.setStress(100);
        to.setStress(500);
        from.addMemories(500);

        // Make to already married so acceptPropose fails
        Yukkuri partner = createSprBody(200, 200);
        to.setPartner(partner.getUniqueId());

        int fromInitialStress = from.getStress();
        int toInitialStress = to.getStress();
        int fromInitialMem = from.getMemories();

        ProposeEvent event = new ProposeEvent(from, to, null, 10);
        event.tick = 20;
        event.execute(from); // set from.partner

        event.tick = 40;
        event.execute(from); // should reset from.partner to -1

        assertEquals(-1, from.getPartner(), "from partner should be reset to -1");
        assertTrue(from.getStress() > fromInitialStress, "Stress should increase on failure");
        assertTrue(to.getStress() < toInitialStress, "to Stress should decrease on being proposed (even if rejected)");
        assertTrue(from.getMemories() < fromInitialMem, "Memories should decrease on failure");
    }

    @Test
    void testAcceptPropose_FromHasStalk_returnsFalse() {
        Yukkuri from = createSprBody(100, 100);
        Yukkuri to = createSprBody(120, 120);
        from.setHasStalk(true); // hasBabyOrStalk() returns true
        ProposeEvent event = new ProposeEvent(from, to, null, 10);
        assertFalse(event.acceptPropose(from, to));
    }

    @Test
    void testUpdate_ToTaken_ReturnsAbort() {
        Yukkuri from = createSprBody(100, 100);
        Yukkuri to = createSprBody(120, 120);
        to.setTaken(true);
        ProposeEvent event = new ProposeEvent(from, to, null, 10);
        assertEquals(EventPacket.UpdateState.ABORT, event.update(from));
        assertEquals(org.simyukkuri.enums.Happiness.VERY_SAD, from.getHappiness());
    }

    @Test
    void testExecute_ToSick_fromFindSick_ReturnsTrue() {
        Yukkuri from = createSprBody(100, 100);
        Yukkuri to = createSprBody(120, 120);
        to.forceSetSick();
        from.setIntelligence(org.simyukkuri.enums.Intelligence.WISE); // will find sick

        ProposeEvent event = new ProposeEvent(from, to, null, 10);
        assertTrue(event.execute(from));
        assertEquals(org.simyukkuri.enums.Happiness.VERY_SAD, from.getHappiness());
        assertEquals(-1, from.getPartner());
    }

    @Test
    void testUpdate_ToGrabbed_ResetsStarted() {
        Yukkuri from = createSprBody(100, 100);
        Yukkuri to = createSprBody(120, 120);
        from.setIntelligence(org.simyukkuri.enums.Intelligence.FOOL);
        ProposeEvent event = new ProposeEvent(from, to, null, 10);
        event.started = true;
        to.setGrabbed(true);

        assertNull(event.update(from));
        assertFalse(event.started, "started should be reset to false if target is grabbed");
    }

    @Test
    void testExecute_tick60_Failure_Semantic() {
        Yukkuri from = createSprBody(100, 100);
        Yukkuri to = createSprBody(120, 120);
        ProposeEvent event = new ProposeEvent(from, to, null, 10);
        event.tick = 60;
        from.setPartner(-1);

        assertTrue(event.execute(from));
        assertEquals(org.simyukkuri.enums.Happiness.VERY_SAD, from.getHappiness());
    }
}
