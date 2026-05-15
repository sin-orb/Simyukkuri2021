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
import org.simyukkuri.event.impl.ShitExercisesEvent;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.simyukkuri.SimYukkuri;
import org.simyukkuri.engine.World;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.enums.AgeState;
import org.simyukkuri.enums.Attitude;
import org.simyukkuri.enums.Happiness;
import org.simyukkuri.enums.ImageCode;
import org.simyukkuri.event.EventPacket.EventPriority;
import org.simyukkuri.util.WorldTestHelper;

class FuneralEventTest {

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
        FuneralEvent event = new FuneralEvent();
        assertNotNull(event);
        assertEquals(-1, event.getFrom());
        assertEquals(-1, event.getTo());
    }

    @Test
    void testParameterizedConstructor_setsPriorityHigh() {
        Yukkuri from = createBody();
        Yukkuri to = createBody();
        FuneralEvent event = new FuneralEvent(from, to, null, 10);
        assertNotNull(event);
        assertEquals(from.getUniqueID(), event.getFrom());
        assertEquals(to.getUniqueID(), event.getTo());
        assertEquals(EventPriority.HIGH, event.getPriority());
    }

    @Test
    void testSimpleEventAction_returnsTrueWhenFromIsNull() {
        Yukkuri b = createBody();
        FuneralEvent event = new FuneralEvent();
        event.setFrom(-1);
        // from is null -> returns true
        assertTrue(event.simpleEventAction(b));
    }

    @Test
    void testSimpleEventAction_returnsTrueWhenFromEqualsB() {
        Yukkuri from = createBody();
        FuneralEvent event = new FuneralEvent(from, null, null, 10);
        // from == b -> returns true
        assertTrue(event.simpleEventAction(from));
    }

    @Test
    void testSimpleEventAction_returnsFalseWhenFromIsNotBAndNotShutmouth() {
        Yukkuri from = createBody();
        Yukkuri other = createBody();
        FuneralEvent event = new FuneralEvent(from, null, null, 10);
        // from != other and from is not shutmouth -> returns false
        assertFalse(event.simpleEventAction(other));
    }

    @Test
    void testCheckEventResponse_returnsTrueWhenFromUniqueIdEqualsB() {
        Yukkuri from = createBody();
        FuneralEvent event = new FuneralEvent(from, null, null, 10);
        // from.getUniqueID() == b.getUniqueID() -> returns true
        assertTrue(event.checkEventResponse(from));
    }

    @Test
    void testCheckEventResponse_returnsFalseForUnunSlave() {
        Yukkuri from = createBody();
        Yukkuri responder = createBody();
        responder.setPublicRank(org.simyukkuri.enums.PublicRank.UNUN_SLAVE);
        FuneralEvent event = new FuneralEvent(from, null, null, 10);
        assertFalse(event.checkEventResponse(responder));
    }

    @Test
    void testCheckEventResponse_returnsFalseWhenSourceBodyIsAlreadyInAnotherEvent() {
        Yukkuri from = createBody();
        Yukkuri child = createBody();
        WorldTestHelper.setParents(child, -1, from.getUniqueID());
        from.setCurrentEvent(new ShitExercisesEvent(from, null, null, 10));
        FuneralEvent event = new FuneralEvent(from, null, null, 10);
        assertFalse(event.checkEventResponse(child));
    }

    @Test
    void testExecute_returnsFalse() {
        Yukkuri b = createBody();
        FuneralEvent event = new FuneralEvent(b, null, null, 10);
        assertFalse(event.execute(b));
    }

    @Test
    void testEnd_setsCurrentEventToNull() {
        Yukkuri b = createBody();
        FuneralEvent event = new FuneralEvent(b, null, null, 10);
        b.setCurrentEvent(event);
        assertNotNull(b.getCurrentEvent());
        event.end(b);
        assertNull(b.getCurrentEvent());
    }

    @Test
    void testGetState_defaultIsGO() {
        FuneralEvent event = new FuneralEvent();
        assertEquals(FuneralEvent.STATE.GO, event.getState());
    }

    @Test
    void testSetState() {
        FuneralEvent event = new FuneralEvent();
        event.setState(FuneralEvent.STATE.SING);
        assertEquals(FuneralEvent.STATE.SING, event.getState());

        event.setState(FuneralEvent.STATE.END);
        assertEquals(FuneralEvent.STATE.END, event.getState());
    }

    // --- STATE enum ---
    @Test
    void testStateEnum_allValues() {
        FuneralEvent.STATE[] states = FuneralEvent.STATE.values();
        assertEquals(8, states.length);
        for (FuneralEvent.STATE s : states) {
            assertNotNull(s.name());
        }
    }

    // --- start ---
    @Test
    void testStart_setsCurrentEvent() {
        Yukkuri b = createBody();
        FuneralEvent event = new FuneralEvent(b, null, null, 10);
        event.start(b);
        assertEquals(event, b.getCurrentEvent());
    }

    // --- toString ---
    @Test
    void testToString_doesNotThrow() {
        FuneralEvent event = new FuneralEvent();
        assertDoesNotThrow(() -> event.toString());
    }

    // --- checkEventResponse ---
    @Test
    void testCheckEventResponse_noParents_returnsFalse() {
        Yukkuri from = createBody();
        Yukkuri other = createBody();
        // other has no parents → false
        FuneralEvent event = new FuneralEvent(from, null, null, 10);
        assertFalse(event.checkEventResponse(other));
    }

    // --- update ---
    @Test
    void testUpdate_fromNull_returnsAbort() {
        Yukkuri b = createBody();
        FuneralEvent event = new FuneralEvent();
        assertEquals(org.simyukkuri.event.EventPacket.UpdateState.ABORT, event.update(b));
    }

    @Test
    void testUpdate_bodyNYD_returnsAbort() {
        Yukkuri from = createBody();
        Yukkuri b = createBody();
        b.setCoreAnkoState(org.simyukkuri.enums.CoreAnkoState.NON_YUKKURI_DISEASE);
        FuneralEvent event = new FuneralEvent(from, null, null, 10);
        assertEquals(org.simyukkuri.event.EventPacket.UpdateState.ABORT, event.update(b));
    }

    @Test
    void testUpdate_fromRemoved_returnsAbort() {
        Yukkuri from = createBody();
        Yukkuri b = createBody();
        from.setRemoved(true);
        FuneralEvent event = new FuneralEvent(from, null, null, 10);
        assertEquals(org.simyukkuri.event.EventPacket.UpdateState.ABORT, event.update(b));
    }

    @Test
    void testUpdate_bEqualsFrom_noChildren_returnsAbort() {
        Yukkuri from = createBody();
        FuneralEvent event = new FuneralEvent(from, null, null, 10);
        from.setCurrentEvent(event);
        // b == from, no children → ABORT
        assertEquals(org.simyukkuri.event.EventPacket.UpdateState.ABORT, event.update(from));
    }

    @Test
    void testUpdate_partnerOfFrom_stateNotGO_returnsNull() {
        Yukkuri from = createBody();
        Yukkuri partner = createBody();
        from.setPartner(partner.getUniqueID());
        partner.setPartner(from.getUniqueID());
        FuneralEvent event = new FuneralEvent(from, null, null, 10);
        from.setCurrentEvent(event);
        event.setState(FuneralEvent.STATE.FIND); // not GO → b.stay()
        assertDoesNotThrow(() -> event.update(partner));
    }

    @Test
    void testUpdate_tickNotMultipleOf30_returnsNull() {
        Yukkuri from = createBody();
        Yukkuri b = createBody();
        FuneralEvent event = new FuneralEvent(from, null, null, 10);
        from.setCurrentEvent(event);
        event.tick = 1; // 1 % 30 != 0 → return null immediately
        assertNull(event.update(b));
    }

    @Test
    void testUpdate_bIsPartnerOfFrom_stateGO_doesNotThrow() {
        Yukkuri from = createBody();
        Yukkuri partner = createBody();
        from.setPartner(partner.getUniqueID());
        partner.setPartner(from.getUniqueID());
        FuneralEvent event = new FuneralEvent(from, null, null, 10);
        from.setCurrentEvent(event);
        // state=GO → calcCollisionX (needs rateX initialized)
        assertDoesNotThrow(() -> event.update(partner));
    }

    @Test
    void testUpdate_nFromWaitCountOver2000_returnsAbort() {
        Yukkuri from = createBody();
        FuneralEvent event = new FuneralEvent(from, null, null, 10);
        from.setCurrentEvent(event);
        event.fromWaitCount = 2001;
        assertEquals(org.simyukkuri.event.EventPacket.UpdateState.ABORT, event.update(from));
    }

    // --- update: child path (b != from && !partner) ---

    @Test
    void testUpdate_childBody_stateGO_doesNotThrow() {
        Yukkuri from = createBody();
        Yukkuri child = createBody();
        FuneralEvent event = new FuneralEvent(from, null, null, 10);
        from.setCurrentEvent(event);
        child.setCurrentEvent(event);
        // child is not from and not partner → child path, state=GO
        assertDoesNotThrow(() -> event.update(child));
    }

    @Test
    void testUpdate_childBody_stateFIND_doesNotThrow() {
        Yukkuri from = createBody();
        Yukkuri child = createBody();
        FuneralEvent event = new FuneralEvent(from, null, null, 10);
        from.setCurrentEvent(event);
        child.setCurrentEvent(event);
        event.setState(FuneralEvent.STATE.FIND);
        assertDoesNotThrow(() -> event.update(child));
    }

    @Test
    void testUpdate_childBody_stateSTART_bActionFlagTrue_doesNotThrow() {
        Yukkuri from = createBody();
        Yukkuri child = createBody();
        FuneralEvent event = new FuneralEvent(from, null, null, 10);
        event.actionFlag = true;
        from.setCurrentEvent(event);
        child.setCurrentEvent(event);
        event.setState(FuneralEvent.STATE.START);
        assertDoesNotThrow(() -> event.update(child));
    }

    @Test
    void testUpdate_childBody_stateINTRODUCE_bActionFlagTrue_doesNotThrow() {
        Yukkuri from = createBody();
        Yukkuri child = createBody();
        FuneralEvent event = new FuneralEvent(from, null, null, 10);
        event.actionFlag = true;
        from.setCurrentEvent(event);
        child.setCurrentEvent(event);
        event.setState(FuneralEvent.STATE.INTRODUCE);
        assertDoesNotThrow(() -> event.update(child));
    }

    @Test
    void testUpdate_childBody_stateSING_bActionFlagFalse_doesNotThrow() {
        Yukkuri from = createBody();
        Yukkuri child = createBody();
        FuneralEvent event = new FuneralEvent(from, null, null, 10);
        event.actionFlag = false; // !actionFlag in child SING case
        from.setCurrentEvent(event);
        child.setCurrentEvent(event);
        event.setState(FuneralEvent.STATE.SING);
        assertDoesNotThrow(() -> event.update(child));
    }

    @Test
    void testUpdate_childBody_stateTALK_bActionFlagTrue_doesNotThrow() {
        Yukkuri from = createBody();
        Yukkuri child = createBody();
        FuneralEvent event = new FuneralEvent(from, null, null, 10);
        event.actionFlag = true;
        from.setCurrentEvent(event);
        child.setCurrentEvent(event);
        event.setState(FuneralEvent.STATE.TALK);
        assertDoesNotThrow(() -> event.update(child));
    }

    @Test
    void testUpdate_childBody_stateGOODBYE_bActionFlagTrue_doesNotThrow() {
        Yukkuri from = createBody();
        Yukkuri child = createBody();
        FuneralEvent event = new FuneralEvent(from, null, null, 10);
        event.actionFlag = true;
        from.setCurrentEvent(event);
        child.setCurrentEvent(event);
        event.setState(FuneralEvent.STATE.GOODBYE);
        assertDoesNotThrow(() -> event.update(child));
    }

    @Test
    void testUpdate_childBody_stateEND_doesNotThrow() {
        Yukkuri from = createBody();
        Yukkuri child = createBody();
        FuneralEvent event = new FuneralEvent(from, null, null, 10);
        from.setCurrentEvent(event);
        child.setCurrentEvent(event);
        event.setState(FuneralEvent.STATE.END);
        assertDoesNotThrow(() -> event.update(child));
    }

    // --- update: partner path state != GO → stays ---
    @Test
    void testUpdate_partnerOfFrom_stateNotGO_stays() {
        Yukkuri from = createBody();
        Yukkuri partner = createBody();
        from.setPartner(partner.getUniqueID());
        partner.setPartner(from.getUniqueID());
        FuneralEvent event = new FuneralEvent(from, null, null, 10);
        from.setCurrentEvent(event);
        partner.setCurrentEvent(event);
        event.setState(FuneralEvent.STATE.FIND);
        assertNull(event.update(partner));
    }

    // --- checkEventResponse: partner of from (has parents) → true ---
    @Test
    void testCheckEventResponse_partnerOfFrom_hasParents_returnsTrue() {
        Yukkuri from = createBody();
        Yukkuri partner = createBody();
        Yukkuri grandparent = createBody(); // give partner a parent so line 85 passes
        from.setPartner(partner.getUniqueID());
        partner.setPartner(from.getUniqueID());
        partner.setParents(new int[] { grandparent.getUniqueID(), -1 });
        FuneralEvent event = new FuneralEvent(from, null, null, 10);
        assertTrue(event.checkEventResponse(partner));
    }

    // --- checkEventResponse: non-child of from, has parents → false ---
    @Test
    void testCheckEventResponse_notChildOfFrom_hasParents_returnsFalse() {
        Yukkuri from = createBody();
        Yukkuri grandparent = createBody();
        Yukkuri other = createBody();
        other.setParents(new int[] { grandparent.getUniqueID(), -1 }); // has parents but NOT from's child
        FuneralEvent event = new FuneralEvent(from, null, null, 10);
        assertFalse(event.checkEventResponse(other));
    }

    // --- checkEventResponse: adult child of from → false ---
    @Test
    void testCheckEventResponse_isChildOfFrom_adult_returnsFalse() {
        Yukkuri from = createBody();
        Yukkuri child = createBody(); // ADULT by default
        child.setParents(new int[] { from.getUniqueID(), -1 });
        FuneralEvent event = new FuneralEvent(from, null, null, 10);
        assertFalse(event.checkEventResponse(child));
    }

    // --- checkEventResponse: baby child of from → true ---
    @Test
    void testCheckEventResponse_isChildOfFrom_baby_returnsTrue() {
        Yukkuri from = createBody();
        Yukkuri child = createBody();
        child.setParents(new int[] { from.getUniqueID(), -1 });
        child.setAgeState(AgeState.BABY);
        FuneralEvent event = new FuneralEvent(from, null, null, 10);
        assertTrue(event.checkEventResponse(child));
    }

    @Nested
    class RegressionScenarios {

        @Test
        void testScenario_ChildParticipationMarksHappyAndClearsActions() {
            Yukkuri from = createBody();
            Yukkuri child = createBody();
            child.setParents(new int[] { from.getUniqueID(), -1 });
            child.setAgeState(AgeState.BABY);
            child.setHappiness(Happiness.SAD);
            child.setToFood(true);
            child.setMoveTargetId(from.getObjId());

            FuneralEvent event = new FuneralEvent(from, null, null, 10);

            assertTrue(event.checkEventResponse(child));
            assertEquals(Happiness.HAPPY, child.getHappiness());
            assertFalse(child.isToFood());
        }

        @Test
        void testScenario_ChildFindForElderSisterSetsVerySadAndCryingFace() {
            Yukkuri from = createBody();
            Yukkuri elderSister = createBody();
            Yukkuri child = createBody();
            elderSister.setParents(new int[] { from.getUniqueID(), -1 });
            child.setParents(new int[] { from.getUniqueID(), -1 });
            elderSister.setAge(200);
            child.setAge(100);

            FuneralEvent event = new FuneralEvent(from, elderSister, null, 10);
            from.setCurrentEvent(event);
            child.setCurrentEvent(event);
            event.setState(FuneralEvent.STATE.FIND);
            child.setLastActionTime(0);
            int memoriesBefore = child.getMemories();

            assertNull(event.update(child));
            assertEquals(Happiness.VERY_SAD, child.getHappiness());
            assertEquals(ImageCode.CRYING.ordinal(), child.getForceFace());
            assertTrue(child.getMemories() > memoriesBefore,
                    "child find branch should increase memories after reacting to the deceased elder sister");
        }

        @Test
        void testScenario_FromGoodbyeRemovesDeceasedOkazariAndAddsMemories() {
            Yukkuri from = createBody();
            Yukkuri deceased = createBody();
            Yukkuri child = createBody();
            child.setParents(new int[] { from.getUniqueID(), -1 });
            child.setAgeState(AgeState.BABY);
            from.getChildren().add(child.getUniqueID());

            FuneralEvent event = new FuneralEvent(from, deceased, null, 10);
            from.setCurrentEvent(event);
            child.setCurrentEvent(event);
            event.setState(FuneralEvent.STATE.GOODBYE);
            event.actionFlag = false;
            from.setLastActionTime(0);
            int memoriesBefore = from.getMemories();

            assertTrue(deceased.hasOkazari());
            assertNull(event.update(from));
            assertFalse(deceased.hasOkazari());
            assertTrue(event.actionFlag);
            assertTrue(from.getMemories() > memoriesBefore,
                    "from goodbye branch should increase memories after removing the deceased okazari");
        }

        @Test
        void testScenario_RudeChildGoodbyeCanEnterFurifuriPath() {
            Yukkuri from = createBody();
            Yukkuri child = createBody();
            child.setAttitude(Attitude.SUPER_SHITHEAD);

            FuneralEvent event = new FuneralEvent(from, null, null, 10);
            from.setCurrentEvent(event);
            child.setCurrentEvent(event);
            event.setState(FuneralEvent.STATE.GOODBYE);
            event.actionFlag = true;
            child.setLastActionTime(0);
            int memoriesBefore = child.getMemories();

            java.util.Random original = SimYukkuri.RND;
            SimYukkuri.RND = new java.util.Random() {
                @Override
                public boolean nextBoolean() {
                    return true;
                }
            };
            try {
                assertNull(event.update(child));
            } finally {
                SimYukkuri.RND = original;
            }

            assertTrue(child.isFurifuri());
            assertTrue(child.getMemories() > memoriesBefore,
                    "rude child goodbye branch should increase memories after entering the furifuri path");
        }
    }

    // --- update: child GO with isDontMove → ABORT ---
    @Test
    void testUpdate_childBody_GO_isDontMove_returnsAbort() {
        Yukkuri from = createBody();
        Yukkuri child = createBody();
        child.setGrabbed(true); // isDontMove() = true
        FuneralEvent event = new FuneralEvent(from, null, null, 10);
        from.setCurrentEvent(event);
        child.setCurrentEvent(event);
        assertEquals(org.simyukkuri.event.EventPacket.UpdateState.ABORT, event.update(child));
    }

    // --- update: b==from, with baby child present, state=GO → does not throw ---
    @Test
    void testUpdate_bEqualsFrom_withBabyChild_stateGO_doesNotThrow() {
        Yukkuri from = createBody();
        Yukkuri child = createBody();
        child.setParents(new int[] { from.getUniqueID(), -1 });
        child.setAgeState(AgeState.BABY);
        child.setSpriteSet(from.getSpriteSet()); // ensure sprites set
        FuneralEvent event = new FuneralEvent(from, null, null, 10);
        from.setCurrentEvent(event);
        child.setCurrentEvent(event);
        SimYukkuri.RND = new org.simyukkuri.ConstState(1);
        try {
            assertDoesNotThrow(() -> event.update(from));
        } finally {
            SimYukkuri.RND = new java.util.Random();
        }
    }

    // --- update: from.getZ() >= 5, b != from → sets message, returns null ---
    @Test
    void testUpdate_fromHighZ_bNotFrom_returnsNull() {
        Yukkuri from = createBody();
        Yukkuri child = createBody();
        from.setZ(10); // from.getZ() >= 5, !canflyCheck() true for Reimu
        FuneralEvent event = new FuneralEvent(from, null, null, 10);
        from.setCurrentEvent(event);
        child.setCurrentEvent(event);
        SimYukkuri.RND = new org.simyukkuri.ConstState(1); // nextInt(50)=1 → no ABORT
        try {
            assertNull(event.update(child));
        } finally {
            SimYukkuri.RND = new java.util.Random();
        }
    }

    // --- update: from.getZ() >= 5, b == from (with baby child) → does not throw
    // ---
    @Test
    void testUpdate_fromHighZ_bEqualsFrom_withChild_doesNotThrow() {
        Yukkuri from = createBody();
        Yukkuri child = createBody();
        child.setParents(new int[] { from.getUniqueID(), -1 });
        child.setAgeState(AgeState.BABY);
        from.setZ(10); // elevated
        FuneralEvent event = new FuneralEvent(from, null, null, 10);
        from.setCurrentEvent(event);
        child.setCurrentEvent(event);
        SimYukkuri.RND = new org.simyukkuri.ConstState(1);
        try {
            assertDoesNotThrow(() -> event.update(from));
        } finally {
            SimYukkuri.RND = new java.util.Random();
        }
    }

    // --- update: child FIND, to not null → sets message, stays ---
    @Test
    void testUpdate_childBody_FIND_withTo_doesNotThrow() {
        Yukkuri from = createBody();
        Yukkuri to = createBody(); // the deceased
        Yukkuri child = createBody();
        FuneralEvent event = new FuneralEvent(from, to, null, 10);
        from.setCurrentEvent(event);
        child.setCurrentEvent(event);
        event.setState(FuneralEvent.STATE.FIND);
        assertDoesNotThrow(() -> event.update(child));
    }

    // --- update: child GOODBYE, rude body → furifuri path ---
    @Test
    void testUpdate_childBody_GOODBYE_rude_doesNotThrow() {
        Yukkuri from = createBody();
        Yukkuri child = createBody();
        child.setAttitude(Attitude.SUPER_SHITHEAD); // isRude() = true
        FuneralEvent event = new FuneralEvent(from, null, null, 10);
        event.actionFlag = true;
        from.setCurrentEvent(event);
        child.setCurrentEvent(event);
        event.setState(FuneralEvent.STATE.GOODBYE);
        SimYukkuri.RND = new org.simyukkuri.ConstState(1);
        try {
            assertDoesNotThrow(() -> event.update(child));
        } finally {
            SimYukkuri.RND = new java.util.Random();
        }
    }

    // --- update: child END, rude body → sets message ---
    @Test
    void testUpdate_childBody_END_rude_doesNotThrow() {
        Yukkuri from = createBody();
        Yukkuri child = createBody();
        child.setAttitude(Attitude.SUPER_SHITHEAD); // isRude() = true
        FuneralEvent event = new FuneralEvent(from, null, null, 10);
        from.setCurrentEvent(event);
        child.setCurrentEvent(event);
        event.setState(FuneralEvent.STATE.END);
        assertDoesNotThrow(() -> event.update(child));
    }
}
