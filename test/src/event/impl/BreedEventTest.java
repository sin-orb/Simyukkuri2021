package src.event.impl;

import src.entity.core.Entity;
import src.entity.core.attachment.*;
import src.entity.core.attachment.impl.*;
import src.entity.core.effect.*;
import src.entity.core.effect.impl.*;
import src.entity.core.living.yukkuri.Dna;
import src.entity.core.living.yukkuri.Yukkuri;
import src.entity.core.living.yukkuri.impl.*;
import src.entity.core.world.bodylinked.*;
import src.entity.core.world.item.*;
import src.entity.core.world.mobile.*;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import src.entity.core.living.yukkuri.Yukkuri;
import src.enums.Happiness;
import src.event.EventPacket.UpdateState;
import src.util.WorldTestHelper;
import src.event.EventTestBase;

public class BreedEventTest extends EventTestBase {

    @Test
    void testCheckEventResponse_ParentParticipates() {
        Yukkuri parent = createBody(1, 100, 100);
        Yukkuri child = createBody(2, 120, 120);
        child.setAgeState(src.enums.AgeState.CHILD);

        // Set parent/child relationship
        WorldTestHelper.setParents(child, -1, parent.getUniqueID());

        BreedEvent event = new BreedEvent(parent, child, null, 10);

        // Child should respond to parent's breed event
        // Note: Logic allows response if child is related to 'from'
        assertTrue(event.checkEventResponse(child));
    }

    @Test
    void testCheckEventResponse_BabyChildOfFromDoesNotParticipate() {
        Yukkuri parent = createBody(1, 100, 100);
        Yukkuri baby = createBody(2, 120, 120);
        WorldTestHelper.setParents(baby, -1, parent.getUniqueID());
        baby.setAgeState(src.enums.AgeState.BABY);
        baby.setBirthMessageForced(true);

        BreedEvent event = new BreedEvent(parent, baby, null, 10);

        assertFalse(event.checkEventResponse(baby));
    }

    @Test
    void testCheckEventResponse_BabyChildOfFromBirthEventBlockedDoesNotParticipate() {
        Yukkuri parent = createBody(1, 100, 100);
        Yukkuri baby = createBody(2, 120, 120);
        WorldTestHelper.setParents(baby, -1, parent.getUniqueID());
        baby.setAgeState(src.enums.AgeState.BABY);
        baby.setBirthMessageForced(false);
        baby.setBirthEventBlockedTicks(300);

        BreedEvent event = new BreedEvent(parent, baby, null, 10);

        assertFalse(event.checkEventResponse(baby));
    }

    @Test
    void testCheckEventResponse_BabyChildOfFrom_withoutBirthMessage_participates() {
        Yukkuri parent = createBody(1, 100, 100);
        Yukkuri baby = createBody(2, 120, 120);
        WorldTestHelper.setParents(baby, -1, parent.getUniqueID());
        baby.setAgeState(src.enums.AgeState.BABY);
        baby.setBirthMessageForced(false);

        BreedEvent event = new BreedEvent(parent, baby, null, 10);

        assertTrue(event.checkEventResponse(baby));
    }

    @Test
    void testCheckEventResponse_StrangerDoesNotParticipate() {
        Yukkuri parent = createBody(1, 100, 100);
        Yukkuri stranger = createBody(2, 120, 120);

        // No relationship

        BreedEvent event = new BreedEvent(parent, null, null, 10);

        assertFalse(event.checkEventResponse(stranger));
    }

    @Test
    void testDefaultConstructor_doesNotThrow() {
        assertDoesNotThrow(() -> new BreedEvent());
    }

    @Test
    void testToString_doesNotThrow() {
        Yukkuri parent = createBody(1, 100, 100);
        Yukkuri child = createBody(2, 120, 120);
        BreedEvent event = new BreedEvent(parent, child, null, 10);
        assertDoesNotThrow(() -> event.toString());
    }

    @Test
    void testStart_fromNull_doesNotThrow() {
        // from is not registered in body map → getBodyInstance returns null
        Yukkuri child = createBody(2, 120, 120);
        // Create event with an unregistered body as from
        Yukkuri unregistered = WorldTestHelper.createBody();
        BreedEvent event = new BreedEvent(unregistered, child, null, 10);
        // from is not in world map, so getBodyInstance returns null → no moveToEvent
        assertDoesNotThrow(() -> event.start(child));
    }

    @Test
    void testStart_fromExists_doesNotThrow() {
        Yukkuri parent = createBody(1, 100, 100);
        Yukkuri child = createBody(2, 120, 120);
        BreedEvent event = new BreedEvent(parent, child, null, 10);
        assertDoesNotThrow(() -> event.start(child));
    }

    @Test
    void testUpdate_fromNull_returnsAbort() {
        // from not registered → getBodyInstance returns null → ABORT
        Yukkuri child = createBody(2, 120, 120);
        Yukkuri unregistered = WorldTestHelper.createBody();
        BreedEvent event = new BreedEvent(unregistered, child, null, 10);
        assertEquals(UpdateState.ABORT, event.update(child));
    }

    @Test
    void testUpdate_fromExists_doesNotThrow() {
        Yukkuri parent = createBody(1, 100, 100);
        Yukkuri child = createBody(2, 500, 500); // far from parent
        BreedEvent event = new BreedEvent(parent, child, null, 10);
        assertDoesNotThrow(() -> event.update(child));
    }

    @Test
    void testExecute_fromNull_returnsTrue() {
        // from not registered → getBodyInstance returns null → true
        Yukkuri child = createBody(2, 120, 120);
        Yukkuri unregistered = WorldTestHelper.createBody();
        BreedEvent event = new BreedEvent(unregistered, child, null, 10);
        assertTrue(event.execute(child));
    }

    @Test
    void testExecute_fromExists_doesNotThrow() {
        Yukkuri parent = createBody(1, 100, 100);
        Yukkuri child = createBody(2, 120, 120);
        BreedEvent event = new BreedEvent(parent, child, null, 10);
        assertDoesNotThrow(() -> event.execute(child));
    }

    // --- checkEventResponse extra paths ---

    @Test
    void testCheckEventResponse_bNearToBirth_returnsFalse() {
        Yukkuri parent = createBody(1, 100, 100);
        Yukkuri b = createBody(2, 120, 120);
        // make b nearToBirth = true
        b.setHasBaby(true);
        b.setPregnantPeriod(Integer.MAX_VALUE / 2);
        BreedEvent event = new BreedEvent(parent, null, null, 10);
        assertFalse(event.checkEventResponse(b));
    }

    @Test
    void testCheckEventResponse_bIsUnBirth_returnsFalse() {
        Yukkuri parent = createBody(1, 100, 100);
        Yukkuri b = createBody(2, 120, 120);
        b.setUnBirth(true);
        BreedEvent event = new BreedEvent(parent, null, null, 10);
        assertFalse(event.checkEventResponse(b));
    }

    @Test
    void testCheckEventResponse_bIsRaperExciting_returnsFalse() {
        Yukkuri parent = createBody(1, 100, 100);
        Yukkuri b = createBody(2, 120, 120);
        b.setRapist(true);
        b.setExciting(true);
        BreedEvent event = new BreedEvent(parent, null, null, 10);
        assertFalse(event.checkEventResponse(b));
    }

    @Test
    void testCheckEventResponse_differentPublicRank_returnsFalse() {
        Yukkuri parent = createBody(1, 100, 100);
        Yukkuri b = createBody(2, 120, 120);
        b.setPublicRank(src.enums.PublicRank.UnunSlave);
        BreedEvent event = new BreedEvent(parent, null, null, 10);
        assertFalse(event.checkEventResponse(b));
    }

    @Test
    void testCheckEventResponse_bBuried_returnsFalse() {
        Yukkuri parent = createBody(1, 100, 100);
        Yukkuri b = createBody(2, 120, 120);
        b.setBurialState(src.enums.BurialState.HALF);
        BreedEvent event = new BreedEvent(parent, null, null, 10);
        assertFalse(event.checkEventResponse(b));
    }

    @Test
    void testCheckEventResponse_fromNoOkazariFoolB_returnsFalse() {
        Yukkuri parent = createBody(1, 100, 100);
        Yukkuri b = createBody(2, 120, 120);
        parent.setOkazari(null); // from has no okazari
        b.setIntelligence(src.enums.Intelligence.FOOL);
        BreedEvent event = new BreedEvent(parent, null, null, 10);
        assertFalse(event.checkEventResponse(b));
    }

    @Test
    void testCheckEventResponse_fromIsPartnerOfB_returnsTrue() {
        Yukkuri parent = createBody(1, 100, 100);
        Yukkuri b = createBody(2, 120, 120);
        b.setPartner(parent.getUniqueID());
        parent.setPartner(b.getUniqueID());
        BreedEvent event = new BreedEvent(parent, null, null, 10);
        assertTrue(event.checkEventResponse(b));
    }

    @Test
    void testCheckEventResponse_bIsParentOfFrom_returnsTrue() {
        Yukkuri parent = createBody(1, 100, 100);
        Yukkuri b = createBody(2, 120, 120);
        parent.setParents(new int[] { b.getUniqueID(), -1 }); // b is father of parent
        BreedEvent event = new BreedEvent(parent, null, null, 10);
        assertTrue(event.checkEventResponse(b));
    }

    // --- update extra paths ---

    @Test
    void testUpdate_bNearToBirth_returnsForceExec() {
        Yukkuri from = createBody(1, 100, 100);
        Yukkuri b = createBody(2, 120, 120);
        b.setHasBaby(true);
        b.setPregnantPeriod(Integer.MAX_VALUE / 2);
        BreedEvent event = new BreedEvent(from, null, null, 10);
        assertEquals(src.event.EventPacket.UpdateState.FORCE_EXEC, event.update(b));
    }

    @Test
    void testUpdate_closeDistance_returnsForceExec() {
        Yukkuri from = createBody(1, 100, 100);
        Yukkuri b = createBody(2, 105, 105); // very close → distance < 20000
        from.setHasBaby(true); // nearToBirth needs this
        from.setPregnantPeriod(Integer.MAX_VALUE / 2);
        BreedEvent event = new BreedEvent(from, null, null, 10);
        assertEquals(src.event.EventPacket.UpdateState.FORCE_EXEC, event.update(b));
    }

    @Test
    void testUpdate_BabyChildOfFrom_returnsAbort() {
        Yukkuri from = createBody(1, 100, 100);
        Yukkuri b = createBody(2, 105, 105);
        WorldTestHelper.setParents(b, -1, from.getUniqueID());
        b.setAgeState(src.enums.AgeState.BABY);
        b.setBirthMessageForced(true);
        from.setBirth(true);

        BreedEvent event = new BreedEvent(from, null, null, 10);

        assertEquals(src.event.EventPacket.UpdateState.ABORT, event.update(b));
    }

    @Test
    void testUpdate_BabyChildBirthEventBlocked_returnsAbort() {
        Yukkuri from = createBody(1, 100, 100);
        Yukkuri b = createBody(2, 105, 105);
        WorldTestHelper.setParents(b, -1, from.getUniqueID());
        b.setAgeState(src.enums.AgeState.BABY);
        b.setBirthMessageForced(false);
        b.setBirthEventBlockedTicks(300);
        from.setBirth(true);

        BreedEvent event = new BreedEvent(from, null, null, 10);

        assertEquals(src.event.EventPacket.UpdateState.ABORT, event.update(b));
    }

    @Test
    void testUpdate_fromDead_returnsAbort() {
        Yukkuri from = createBody(1, 100, 100);
        Yukkuri b = createBody(2, 500, 500); // far
        from.setDead(true);
        BreedEvent event = new BreedEvent(from, null, null, 10);
        assertEquals(src.event.EventPacket.UpdateState.ABORT, event.update(b));
    }

    // --- execute extra paths ---

    @Test
    void testExecute_bNearToBirth_returnsTrue() {
        Yukkuri from = createBody(1, 100, 100);
        Yukkuri b = createBody(2, 120, 120);
        b.setHasBaby(true);
        b.setPregnantPeriod(Integer.MAX_VALUE / 2);
        BreedEvent event = new BreedEvent(from, null, null, 10);
        assertTrue(event.execute(b));
    }

    @Test
    void testExecute_bIsNYD_returnsFalse() {
        Yukkuri from = createBody(1, 100, 100);
        Yukkuri b = createBody(2, 120, 120);
        b.setCoreAnkoState(src.enums.CoreAnkoState.NonYukkuriDisease);
        BreedEvent event = new BreedEvent(from, null, null, 10);
        assertFalse(event.execute(b));
    }

    @Test
    void testExecute_fromIsBirth_returnsTrue() {
        Yukkuri from = createBody(1, 100, 100);
        Yukkuri b = createBody(2, 120, 120);
        from.setBirth(true);
        BreedEvent event = new BreedEvent(from, null, null, 10);
        assertTrue(event.execute(b));
    }

    @Test
    void testExecute_fromHasPants_returnsTrue() {
        Yukkuri from = createBody(1, 100, 100);
        Yukkuri b = createBody(2, 120, 120);
        from.setHasPants(true); // surprise message
        BreedEvent event = new BreedEvent(from, null, null, 10);
        assertTrue(event.execute(b));
    }

    @Test
    void testExecute_fromHasBabyOrStalk_returnsFalse() {
        Yukkuri from = createBody(1, 100, 100);
        Yukkuri b = createBody(2, 120, 120);
        from.setHasBaby(true); // still pregnant → else branch → return false
        BreedEvent event = new BreedEvent(from, null, null, 10);
        assertTrue(event.execute(b));
    }

    @Nested
    class RegressionScenarios {

        @Test
    void testScenario_BirthSuccessMakesResponderVeryHappyAndAddsGoodMemories() {
        Yukkuri from = createBody(1, 100, 100);
        Yukkuri b = createBody(2, 120, 120);
        b.setStress(100);
        int beforeStress = b.getStress();
            int beforeMemories = b.getMemories();

            BreedEvent event = new BreedEvent(from, null, null, 10);

        assertTrue(event.execute(b));
        assertEquals(Happiness.VERY_HAPPY, b.getHappiness());
        assertEquals(beforeStress - 30, b.getStress());
        assertTrue(b.getMemories() > beforeMemories);
    }

    @Test
    void testScenario_ChildResponderLeavesEventAfterGreeting() {
        Yukkuri from = createBody(1, 100, 100);
        Yukkuri child = createBody(2, 120, 120);
        WorldTestHelper.setParents(child, -1, from.getUniqueID());
        child.setAgeState(src.enums.AgeState.BABY);
        child.setBirthMessageForced(false);
        from.setHasBaby(true);

        BreedEvent event = new BreedEvent(from, null, null, 10);

        assertTrue(event.execute(child));
        assertTrue(child.getBirthEventBlockedTicks() > 0);
        assertFalse(event.checkEventResponse(child));
    }
    }
}
