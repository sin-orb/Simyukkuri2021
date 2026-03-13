package src.event;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import src.SimYukkuri;
import src.base.Body;
import src.base.EventPacket;
import src.base.EventTestBase;
import src.system.Sprite;
import src.yukkuri.Reimu;

public class ProposeEventTest extends EventTestBase {

    /** createBody with sprites for tests that need calcCollisionX */
    private Body createSprBody(int x, int y) {
        Body b = new Reimu();
        b.setX(x);
        b.setY(y);
        Sprite[] spr = new Sprite[3];
        for (int i = 0; i < 3; i++) {
            spr[i] = new Sprite(10, 10, Sprite.PIVOT_CENTER_BOTTOM);
        }
        b.setBodySpr(spr);
        SimYukkuri.world.getCurrentMap().getBody().put(b.getUniqueID(), b);
        return b;
    }

    @Test
    void testCheckEventResponse_Participants() {
        Body from = createBody(1, 100, 100);
        Body to = createBody(2, 120, 120);

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
        Body from = createBody(1, 100, 100);
        Body to = createBody(2, 120, 120);
        ProposeEvent event = new ProposeEvent(from, to, null, 10);
        assertEquals(EventPacket.EventPriority.HIGH, event.getPriority());
        assertEquals(from.getUniqueID(), event.getFrom());
        assertEquals(to.getUniqueID(), event.getTo());
        assertEquals(10, event.getCount());
    }

    @Test
    void testCheckEventResponse_Stranger() {
        Body from = createBody(1, 100, 100);
        Body to = createBody(2, 120, 120);
        Body stranger = createBody(3, 200, 200);
        ProposeEvent event = new ProposeEvent(from, to, null, 10);
        assertFalse(event.checkEventResponse(stranger));
    }

    @Test
    void testCheckEventResponse_FromNull() {
        Body b = createBody(1, 100, 100);
        ProposeEvent event = new ProposeEvent();
        // from=-1 (null lookup) → b != null and b != null → false
        assertFalse(event.checkEventResponse(b));
    }

    @Test
    void testExecute_BothNull() {
        Body b = createBody(1, 100, 100);
        ProposeEvent event = new ProposeEvent();
        // from and to both null → returns true
        assertTrue(event.execute(b));
    }

    @Test
    void testExecute_ToGrabbed() {
        Body from = createBody(1, 100, 100);
        Body to = createBody(2, 120, 120);
        ProposeEvent event = new ProposeEvent(from, to, null, 10);
        to.setGrabbed(true);
        // to.isGrabbed() → return false
        assertFalse(event.execute(from));
    }

    @Test
    void testEnd_BothNull() {
        Body b = createBody(1, 100, 100);
        ProposeEvent event = new ProposeEvent();
        // from null → return early
        assertDoesNotThrow(() -> event.end(b));
    }

    @Test
    void testEnd_WithBodies() {
        Body from = createBody(1, 100, 100);
        Body to = createBody(2, 120, 120);
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
        Body b = createBody(1, 100, 100);
        ProposeEvent event = new ProposeEvent();
        // from and to both null → returns without doing anything
        assertDoesNotThrow(() -> event.start(b));
    }

    @Test
    void testUpdate_FromNull() {
        Body b = createBody(1, 100, 100);
        ProposeEvent event = new ProposeEvent();
        // from null → ABORT
        assertEquals(EventPacket.UpdateState.ABORT, event.update(b));
    }

    @Test
    void testUpdate_ToDead() {
        Body from = createBody(1, 100, 100);
        Body to = createBody(2, 120, 120);
        ProposeEvent event = new ProposeEvent(from, to, null, 10);
        to.setDead(true);
        assertEquals(EventPacket.UpdateState.ABORT, event.update(from));
    }

    @Test
    void testAcceptPropose_AlreadyMarried() {
        Body from = createBody(1, 100, 100);
        Body to = createBody(2, 120, 120);
        // to already has a partner
        Body partner = createBody(3, 200, 200);
        to.setPartner(partner.getUniqueID());
        ProposeEvent event = new ProposeEvent(from, to, null, 10);
        assertFalse(event.acceptPropose(from, to));
    }

    @Test
    void testAcceptPropose_NoObstacles() {
        Body from = createBody(1, 100, 100);
        Body to = createBody(2, 120, 120);
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
        Body from = createSprBody(100, 100);
        Body to = createSprBody(120, 120);
        ProposeEvent event = new ProposeEvent(from, to, null, 10);
        to.setGrabbed(true);
        // Reaches calcCollisionX, then to.isGrabbed()=true branch → null
        assertNull(event.update(from));
    }

    @Test
    void testUpdate_Started_returnsForceExec() {
        Body from = createSprBody(100, 100);
        Body to = createSprBody(120, 120);
        ProposeEvent event = new ProposeEvent(from, to, null, 10);
        event.started = true;
        assertEquals(EventPacket.UpdateState.FORCE_EXEC, event.update(from));
    }

    @Test
    void testUpdate_NormalCase_returnsNull() {
        Body from = createSprBody(100, 100);
        Body to = createSprBody(120, 120);
        ProposeEvent event = new ProposeEvent(from, to, null, 10);
        // started=false, to not grabbed → normal update path → null
        assertNull(event.update(from));
    }

    @Test
    void testUpdate_ToRemoved_returnsAbort() {
        Body from = createSprBody(100, 100);
        Body to = createSprBody(120, 120);
        to.setRemoved(true);
        ProposeEvent event = new ProposeEvent(from, to, null, 10);
        assertEquals(EventPacket.UpdateState.ABORT, event.update(from));
    }

    @Test
    void testUpdate_ToNYD_returnsAbort() {
        Body from = createSprBody(100, 100);
        Body to = createSprBody(120, 120);
        to.seteCoreAnkoState(src.enums.CoreAnkoState.NonYukkuriDisease);
        ProposeEvent event = new ProposeEvent(from, to, null, 10);
        assertEquals(EventPacket.UpdateState.ABORT, event.update(from));
    }

    @Test
    void testStart_WithBodies_doesNotThrow() {
        Body from = createSprBody(100, 100);
        Body to = createSprBody(120, 120);
        ProposeEvent event = new ProposeEvent(from, to, null, 10);
        assertDoesNotThrow(() -> event.start(from));
    }

    // --- execute() tick-based paths ---

    @Test
    void testExecute_tick0_setsStarted() {
        Body from = createSprBody(100, 100);
        Body to = createSprBody(120, 120);
        ProposeEvent event = new ProposeEvent(from, to, null, 10);
        event.tick = 0;
        event.execute(from);
        assertTrue(event.started);
    }

    @Test
    void testExecute_tick5_doesNotThrow() {
        Body from = createSprBody(100, 100);
        Body to = createSprBody(120, 120);
        ProposeEvent event = new ProposeEvent(from, to, null, 10);
        event.tick = 5;
        assertDoesNotThrow(() -> event.execute(from));
    }

    @Test
    void testExecute_tick20_doesNotThrow() {
        Body from = createSprBody(100, 100);
        Body to = createSprBody(120, 120);
        ProposeEvent event = new ProposeEvent(from, to, null, 10);
        event.tick = 20;
        assertDoesNotThrow(() -> event.execute(from));
    }

    @Test
    void testExecute_tick40_success_doesNotThrow() {
        Body from = createSprBody(100, 100);
        Body to = createSprBody(120, 120);
        ProposeEvent event = new ProposeEvent(from, to, null, 10);
        event.tick = 40;
        // no partner, no mold, no baby → success
        assertDoesNotThrow(() -> event.execute(from));
    }

    @Test
    void testExecute_tick40_failure_alreadyMarried() {
        Body from = createSprBody(100, 100);
        Body to = createSprBody(120, 120);
        Body otherPartner = createSprBody(200, 200);
        to.setPartner(otherPartner.getUniqueID());
        ProposeEvent event = new ProposeEvent(from, to, null, 10);
        event.tick = 40;
        // to already married → acceptPropose=false → failure path
        assertDoesNotThrow(() -> event.execute(from));
    }

    @Test
    void testExecute_tick60_success_doesNotThrow() {
        Body from = createSprBody(100, 100);
        Body to = createSprBody(120, 120);
        ProposeEvent event = new ProposeEvent(from, to, null, 10);
        event.tick = 60;
        // set from.partner = to (success case)
        from.setPartner(to.getUniqueID());
        assertDoesNotThrow(() -> event.execute(from));
    }

    // --- acceptPropose: hasBabyOrStalk → false ---

    @Test
    void testAcceptPropose_FromHasBaby_returnsFalse() {
        Body from = createSprBody(100, 100);
        Body to = createSprBody(120, 120);
        from.setHasBaby(true); // hasBabyOrStalk() returns true
        ProposeEvent event = new ProposeEvent(from, to, null, 10);
        assertFalse(event.acceptPropose(from, to));
    }

    // --- acceptPropose: t.findSick(f) → false ---
    @Test
    void testAcceptPropose_findSick_returnsFalse() {
        Body from = createSprBody(100, 100);
        Body to = createSprBody(120, 120);
        from.forceSetSick(); // from.isSick() = true
        to.setIntelligence(src.enums.Intelligence.WISE); // to.findSick(from) = true
        ProposeEvent event = new ProposeEvent(from, to, null, 10);
        assertFalse(event.acceptPropose(from, to));
    }

    // --- acceptPropose: f.hasDisorder() → false ---
    @Test
    void testAcceptPropose_fromHasDisorder_returnsFalse() {
        Body from = createSprBody(100, 100);
        Body to = createSprBody(120, 120);
        from.setBlind(true); // hasDisorder() = true
        ProposeEvent event = new ProposeEvent(from, to, null, 10);
        assertFalse(event.acceptPropose(from, to));
    }

    // --- update: to.isTaken() → ABORT ---
    @Test
    void testUpdate_toTaken_returnsAbort() {
        Body from = createSprBody(100, 100);
        Body to = createSprBody(120, 120);
        to.setTaken(true);
        ProposeEvent event = new ProposeEvent(from, to, null, 10);
        assertEquals(EventPacket.UpdateState.ABORT, event.update(from));
    }

    // --- execute: from.findSick(to) = true → returns true ---
    @Test
    void testExecute_fromFindSick_returnsTrue() {
        Body from = createSprBody(100, 100);
        Body to = createSprBody(120, 120);
        to.forceSetSick(); // to.isSick() = true
        from.setIntelligence(src.enums.Intelligence.WISE); // from.findSick(to) = true
        ProposeEvent event = new ProposeEvent(from, to, null, 10);
        assertTrue(event.execute(from));
    }

    // --- execute: to.hasDisorder() = true → returns true ---
    @Test
    void testExecute_toHasDisorder_returnsTrue() {
        Body from = createSprBody(100, 100);
        Body to = createSprBody(120, 120);
        to.setBlind(true); // hasDisorder() = true
        ProposeEvent event = new ProposeEvent(from, to, null, 10);
        assertTrue(event.execute(from));
    }

    // --- execute: tick=0, from.isRude() = true → VAIN face ---
    @Test
    void testExecute_tick0_fromRude_setsStarted() {
        Body from = createSprBody(100, 100);
        Body to = createSprBody(120, 120);
        from.setAttitude(src.enums.Attitude.SHITHEAD); // isRude() = true
        ProposeEvent event = new ProposeEvent(from, to, null, 10);
        event.tick = 0;
        assertDoesNotThrow(() -> event.execute(from));
        assertTrue(event.started);
    }

    // --- execute: tick=20, from.isRude() = true → VAIN face ---
    @Test
    void testExecute_tick20_fromRude_doesNotThrow() {
        Body from = createSprBody(100, 100);
        Body to = createSprBody(120, 120);
        from.setAttitude(src.enums.Attitude.SHITHEAD); // isRude() = true
        ProposeEvent event = new ProposeEvent(from, to, null, 10);
        event.tick = 20;
        assertDoesNotThrow(() -> event.execute(from));
    }

    // --- execute: tick=40 success, VERY_NICE attitude ---
    @Test
    void testExecute_tick40_success_veryNice_doesNotThrow() {
        Body from = createSprBody(100, 100);
        Body to = createSprBody(120, 120);
        from.setAttitude(src.enums.Attitude.VERY_NICE);
        to.setAttitude(src.enums.Attitude.VERY_NICE);
        ProposeEvent event = new ProposeEvent(from, to, null, 10);
        event.tick = 40;
        assertDoesNotThrow(() -> event.execute(from));
    }

    // --- execute: tick=40 success, NICE attitude ---
    @Test
    void testExecute_tick40_success_nice_doesNotThrow() {
        Body from = createSprBody(100, 100);
        Body to = createSprBody(120, 120);
        from.setAttitude(src.enums.Attitude.NICE);
        to.setAttitude(src.enums.Attitude.NICE);
        ProposeEvent event = new ProposeEvent(from, to, null, 10);
        event.tick = 40;
        assertDoesNotThrow(() -> event.execute(from));
    }

    // --- execute: tick=40 success, SHITHEAD + SUPER_SHITHEAD attitude ---
    @Test
    void testExecute_tick40_success_shithead_doesNotThrow() {
        Body from = createSprBody(100, 100);
        Body to = createSprBody(120, 120);
        from.setAttitude(src.enums.Attitude.SHITHEAD);
        to.setAttitude(src.enums.Attitude.SUPER_SHITHEAD);
        ProposeEvent event = new ProposeEvent(from, to, null, 10);
        event.tick = 40;
        assertDoesNotThrow(() -> event.execute(from));
    }

    // --- execute: tick=40 failure, to.findSick(from) = true → HateMoldyYukkuri ---
    @Test
    void testExecute_tick40_failure_toFindSick_doesNotThrow() {
        Body from = createSprBody(100, 100);
        Body to = createSprBody(120, 120);
        from.forceSetSick(); // from.isSick() = true
        to.setIntelligence(src.enums.Intelligence.WISE); // to.findSick(from) = true
        // Make acceptPropose fail first (to already married)
        Body partner = createSprBody(200, 200);
        to.setPartner(partner.getUniqueID());
        ProposeEvent event = new ProposeEvent(from, to, null, 10);
        event.tick = 40;
        assertDoesNotThrow(() -> event.execute(from));
    }

    // --- execute: tick=40 failure, to.isRude() = true → RUDE face ---
    @Test
    void testExecute_tick40_failure_toIsRude_doesNotThrow() {
        Body from = createSprBody(100, 100);
        Body to = createSprBody(120, 120);
        to.setAttitude(src.enums.Attitude.SHITHEAD); // isRude() = true
        Body partner = createSprBody(200, 200);
        to.setPartner(partner.getUniqueID()); // acceptPropose = false (already married)
        ProposeEvent event = new ProposeEvent(from, to, null, 10);
        event.tick = 40;
        assertDoesNotThrow(() -> event.execute(from));
    }

    // --- execute: tick=40 failure, from VERY_NICE / to VERY_NICE ---
    @Test
    void testExecute_tick40_failure_veryNice_doesNotThrow() {
        Body from = createSprBody(100, 100);
        Body to = createSprBody(120, 120);
        from.setAttitude(src.enums.Attitude.VERY_NICE);
        to.setAttitude(src.enums.Attitude.VERY_NICE);
        Body partner = createSprBody(200, 200);
        to.setPartner(partner.getUniqueID());
        ProposeEvent event = new ProposeEvent(from, to, null, 10);
        event.tick = 40;
        assertDoesNotThrow(() -> event.execute(from));
    }

    // --- execute: tick=40 failure, from NICE / to NICE ---
    @Test
    void testExecute_tick40_failure_nice_doesNotThrow() {
        Body from = createSprBody(100, 100);
        Body to = createSprBody(120, 120);
        from.setAttitude(src.enums.Attitude.NICE);
        to.setAttitude(src.enums.Attitude.NICE);
        Body partner = createSprBody(200, 200);
        to.setPartner(partner.getUniqueID());
        ProposeEvent event = new ProposeEvent(from, to, null, 10);
        event.tick = 40;
        assertDoesNotThrow(() -> event.execute(from));
    }

    // --- execute: tick=40 failure, from SHITHEAD / to SUPER_SHITHEAD ---
    @Test
    void testExecute_tick40_failure_shithead_doesNotThrow() {
        Body from = createSprBody(100, 100);
        Body to = createSprBody(120, 120);
        from.setAttitude(src.enums.Attitude.SHITHEAD);
        to.setAttitude(src.enums.Attitude.SUPER_SHITHEAD);
        Body partner = createSprBody(200, 200);
        to.setPartner(partner.getUniqueID());
        ProposeEvent event = new ProposeEvent(from, to, null, 10);
        event.tick = 40;
        assertDoesNotThrow(() -> event.execute(from));
    }

    // --- execute: tick=40 success, check stress/memory ---
    @Test
    void testExecute_tick40_Success_Semantic() {
        Body from = createSprBody(100, 100);
        Body to = createSprBody(120, 120);
        from.setAttitude(src.enums.Attitude.VERY_NICE);
        to.setAttitude(src.enums.Attitude.VERY_NICE);

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

        assertEquals(to.getUniqueID(), from.getPartner());
        assertEquals(from.getUniqueID(), to.getPartner());
        assertTrue(from.getStress() < fromInitialStress, "Stress should decrease on success");
        assertTrue(to.getStress() < toInitialStress, "Stress should decrease on success");
        assertTrue(from.getMemories() > fromInitialMem, "Memories should increase on success");
        assertTrue(to.getMemories() > toInitialMem, "Memories should increase on success");
    }

    // --- execute: tick=40 failure, check stress/memory ---
    @Test
    void testExecute_tick40_Failure_Semantic() {
        Body from = createSprBody(100, 100);
        Body to = createSprBody(120, 120);
        from.setAttitude(src.enums.Attitude.AVERAGE);
        to.setAttitude(src.enums.Attitude.AVERAGE);

        // Set initial values so we can verify decreases
        from.setStress(100);
        to.setStress(500);
        from.addMemories(500);

        // Make to already married so acceptPropose fails
        Body partner = createSprBody(200, 200);
        to.setPartner(partner.getUniqueID());

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
        Body from = createSprBody(100, 100);
        Body to = createSprBody(120, 120);
        from.setHasStalk(true); // hasBabyOrStalk() returns true
        ProposeEvent event = new ProposeEvent(from, to, null, 10);
        assertFalse(event.acceptPropose(from, to));
    }

    @Test
    void testUpdate_ToTaken_ReturnsAbort() {
        Body from = createSprBody(100, 100);
        Body to = createSprBody(120, 120);
        to.setTaken(true);
        ProposeEvent event = new ProposeEvent(from, to, null, 10);
        assertEquals(EventPacket.UpdateState.ABORT, event.update(from));
        assertEquals(src.enums.Happiness.VERY_SAD, from.getHappiness());
    }

    @Test
    void testExecute_ToSick_fromFindSick_ReturnsTrue() {
        Body from = createSprBody(100, 100);
        Body to = createSprBody(120, 120);
        to.forceSetSick();
        from.setIntelligence(src.enums.Intelligence.WISE); // will find sick

        ProposeEvent event = new ProposeEvent(from, to, null, 10);
        assertTrue(event.execute(from));
        assertEquals(src.enums.Happiness.VERY_SAD, from.getHappiness());
        assertEquals(-1, from.getPartner());
    }

    @Test
    void testUpdate_ToGrabbed_ResetsStarted() {
        Body from = createSprBody(100, 100);
        Body to = createSprBody(120, 120);
        ProposeEvent event = new ProposeEvent(from, to, null, 10);
        event.started = true;
        to.setGrabbed(true);

        assertNull(event.update(from));
        assertFalse(event.started, "started should be reset to false if target is grabbed");
    }

    @Test
    void testExecute_tick60_Failure_Semantic() {
        Body from = createSprBody(100, 100);
        Body to = createSprBody(120, 120);
        ProposeEvent event = new ProposeEvent(from, to, null, 10);
        event.tick = 60;
        from.setPartner(-1);

        assertTrue(event.execute(from));
        assertEquals(src.enums.Happiness.VERY_SAD, from.getHappiness());
    }
}
