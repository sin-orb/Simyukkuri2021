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

import java.util.Random;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.simyukkuri.SimYukkuri;
import org.simyukkuri.draw.World;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.living.yukkuri.impl.Reimu;
import org.simyukkuri.enums.AgeState;
import org.simyukkuri.enums.Happiness;
import org.simyukkuri.enums.Intelligence;
import org.simyukkuri.enums.PanicType;
import org.simyukkuri.enums.PublicRank;
import org.simyukkuri.event.EventPacket;
import org.simyukkuri.event.EventPacket.EventPriority;
import org.simyukkuri.system.Sprite;
import org.simyukkuri.util.GameRandom;
import org.simyukkuri.util.RandomSource;
import org.simyukkuri.util.WorldTestHelper;

public class ProudChildEventTest {

    private Random originalRnd;

    @BeforeEach
    public void setUp() {
        originalRnd = SimYukkuri.RND;
        SimYukkuri.RND = new Random(0);
        WorldTestHelper.resetWorld();
        SimYukkuri.world = new World();
        WorldTestHelper.initializeStandardTranslate500();
    }

    @AfterEach
    public void tearDown() {
        GameRandom.clearOverride();
        SimYukkuri.RND = originalRnd;
        WorldTestHelper.resetWorld();
    }

    // --- Default constructor ---

    @Test
    public void testDefaultConstructor() {
        ProudChildEvent event = new ProudChildEvent();
        assertEquals(-1, event.getFrom());
        assertEquals(-1, event.getTo());
        assertEquals(EventPriority.LOW, event.getPriority());
    }

    // --- Parameterized constructor ---

    @Test
    public void testParameterizedConstructor_setsPriorityMiddle() {
        Yukkuri from = createBody();
        Yukkuri to = createBody();
        ProudChildEvent event = new ProudChildEvent(from, to, null, 10);
        assertEquals(EventPriority.MIDDLE, event.getPriority());
        assertEquals(from.getUniqueID(), event.getFrom());
        assertEquals(to.getUniqueID(), event.getTo());
        assertEquals(10, event.getCount());
    }

    // --- simpleEventAction ---

    @Test
    public void testSimpleEventAction_returnsTrueWhenFromIsNull() {
        Yukkuri b = createBody();
        ProudChildEvent event = new ProudChildEvent();
        // from is -1 (null lookup), so YukkuriLookup lookup returns null
        assertTrue(event.simpleEventAction(b));
    }

    @Test
    public void testSimpleEventAction_returnsTrueWhenFromEqualsB() {
        Yukkuri b = createBody();
        ProudChildEvent event = new ProudChildEvent(b, null, null, 10);
        // from == b, returns true
        assertTrue(event.simpleEventAction(b));
    }

    @Test
    public void testSimpleEventAction_returnsFalseWhenFromNotBAndNotShutmouth() {
        Yukkuri from = createBody();
        Yukkuri b = createBody();
        ProudChildEvent event = new ProudChildEvent(from, null, null, 10);
        // from != b and from is not shutmouth => returns false
        assertFalse(event.simpleEventAction(b));
    }

    // --- checkEventResponse ---

    @Test
    public void testCheckEventResponse_returnsFalseForUnunSlave() {
        Yukkuri from = createBody();
        Yukkuri b = createBody();
        b.setPublicRank(PublicRank.UnunSlave);
        ProudChildEvent event = new ProudChildEvent(from, null, null, 10);
        assertFalse(event.checkEventResponse(b));
    }

    @Test
    public void testCheckEventResponse_returnsFalseForBirthMessageForcedBaby() {
        Yukkuri from = createBody();
        Yukkuri baby = createBody();
        baby.setAgeState(AgeState.BABY);
        baby.setBirthMessageForced(true);
        WorldTestHelper.setParents(baby, -1, from.getUniqueID());
        ProudChildEvent event = new ProudChildEvent(from, null, null, 10);
        assertFalse(event.checkEventResponse(baby));
    }

    @Test
    public void testCheckEventResponse_returnsFalseWhenFromIsNull() {
        Yukkuri b = createBody();
        ProudChildEvent event = new ProudChildEvent();
        // from is null (default constructor), returns false
        assertFalse(event.checkEventResponse(b));
    }

    @Test
    public void testCheckEventResponse_returnsFalseForNewbornBaby() {
        Yukkuri from = createBody();
        Yukkuri baby = createBody();
        WorldTestHelper.setParents(baby, -1, from.getUniqueID());
        baby.setAgeState(AgeState.BABY);
        baby.setBirthAge(baby.getAge());
        baby.setFirstGround(false);
        ProudChildEvent event = new ProudChildEvent(from, null, null, 10);
        assertFalse(event.checkEventResponse(baby));
    }

    @Test
    public void testCheckEventResponse_returnsFalseWhileFalling() {
        Yukkuri from = createBody();
        Yukkuri baby = createBody();
        WorldTestHelper.setParents(baby, -1, from.getUniqueID());
        baby.setAgeState(AgeState.BABY);
        baby.setBirthAge(0);
        baby.setFirstGround(false);
        baby.setZ(10);
        baby.setMostDepth(0);
        ProudChildEvent event = new ProudChildEvent(from, null, null, 10);
        assertFalse(event.checkEventResponse(baby));
    }

    // --- execute ---

    @Test
    public void testExecute_returnsFalse() {
        Yukkuri b = createBody();
        ProudChildEvent event = new ProudChildEvent(b, null, null, 10);
        assertFalse(event.execute(b));
    }

    // --- end ---

    @Test
    public void testEnd_setsCurrentEventToNull() {
        Yukkuri b = createBody();
        ProudChildEvent event = new ProudChildEvent(b, null, null, 10);
        b.setCurrentEvent(event);
        event.end(b);
        assertNull(b.getCurrentEvent());
    }

    // --- getState / setState ---

    @Test
    public void testGetState_defaultIsGO() {
        ProudChildEvent event = new ProudChildEvent();
        assertEquals(ProudChildEvent.STATE.GO, event.getState());
    }

    @Test
    public void testSetState() {
        ProudChildEvent event = new ProudChildEvent();
        event.setState(ProudChildEvent.STATE.SING);
        assertEquals(ProudChildEvent.STATE.SING, event.getState());

        event.setState(ProudChildEvent.STATE.END);
        assertEquals(ProudChildEvent.STATE.END, event.getState());
    }

    // --- start ---
    @Test
    public void testStart_setsCurrentEvent() {
        Yukkuri b = createBody();
        ProudChildEvent event = new ProudChildEvent(b, null, null, 10);
        event.start(b);
        assertEquals(event, b.getCurrentEvent());
    }

    // --- toString ---
    @Test
    public void testToString_doesNotThrow() {
        ProudChildEvent event = new ProudChildEvent();
        assertDoesNotThrow(() -> event.toString());
    }

    // --- STATE enum ---
    @Test
    public void testStateEnum_allValues() {
        ProudChildEvent.STATE[] states = ProudChildEvent.STATE.values();
        assertTrue(states.length > 0);
        for (ProudChildEvent.STATE s : states) {
            assertNotNull(s.name());
        }
    }

    // --- update ---
    @Test
    public void testUpdate_fromNull_returnsAbort() {
        Yukkuri b = createBody();
        ProudChildEvent event = new ProudChildEvent();
        assertEquals(EventPacket.UpdateState.ABORT, event.update(b));
    }

    @Test
    public void testUpdate_bodyNYD_returnsAbort() {
        Yukkuri from = createBody();
        Yukkuri b = createBody();
        b.setCoreAnkoState(org.simyukkuri.enums.CoreAnkoState.NonYukkuriDisease);
        ProudChildEvent event = new ProudChildEvent(from, null, null, 10);
        from.setCurrentEvent(event);
        assertEquals(EventPacket.UpdateState.ABORT, event.update(b));
    }

    @Test
    public void testUpdate_newbornBaby_returnsAbort() {
        Yukkuri from = createBody();
        Yukkuri baby = createBody();
        WorldTestHelper.setParents(baby, -1, from.getUniqueID());
        baby.setAgeState(AgeState.BABY);
        baby.setBirthAge(baby.getAge());
        baby.setFirstGround(false);
        ProudChildEvent event = new ProudChildEvent(from, null, null, 10);
        from.setCurrentEvent(event);
        assertEquals(EventPacket.UpdateState.ABORT, event.update(baby));
    }

    @Test
    public void testCheckEventResponse_birthEventBlockedBaby_returnsFalse() {
        Yukkuri from = createBody();
        Yukkuri baby = createBody();
        WorldTestHelper.setParents(baby, -1, from.getUniqueID());
        baby.setAgeState(AgeState.BABY);
        baby.setFirstGround(false);
        baby.setBirthMessageForced(false);
        baby.setBirthEventBlockedTicks(300);
        ProudChildEvent event = new ProudChildEvent(from, null, null, 10);
        from.setCurrentEvent(event);
        assertFalse(event.checkEventResponse(baby));
    }

    @Test
    public void testUpdate_fromRemoved_returnsAbort() {
        Yukkuri from = createBody();
        Yukkuri b = createBody();
        from.setRemoved(true);
        ProudChildEvent event = new ProudChildEvent(from, null, null, 10);
        from.setCurrentEvent(event);
        assertEquals(EventPacket.UpdateState.ABORT, event.update(b));
    }

    @Test
    public void testUpdate_fromCurrentEventNull_returnsAbort() {
        Yukkuri from = createBody();
        Yukkuri b = createBody();
        ProudChildEvent event = new ProudChildEvent(from, null, null, 10);
        // from.getCurrentEvent() == null → ABORT
        assertEquals(EventPacket.UpdateState.ABORT, event.update(b));
    }

    @Test
    public void testUpdate_bEqualsFrom_noChildren_returnsAbort() {
        Yukkuri from = createBody();
        ProudChildEvent event = new ProudChildEvent(from, null, null, 10);
        from.setCurrentEvent(event);
        // b == from, no children → ABORT
        assertEquals(EventPacket.UpdateState.ABORT, event.update(from));
    }

    // --- update: child path (b != from && !partner) ---

    @Test
    public void testUpdate_childBody_stateGO_doesNotThrow() {
        Yukkuri from = createBody();
        Yukkuri child = createBody();
        ProudChildEvent event = new ProudChildEvent(from, null, null, 10);
        from.setCurrentEvent(event);
        child.setCurrentEvent(event);
        assertDoesNotThrow(() -> event.update(child));
    }

    @Test
    public void testUpdate_childBody_stateWAIT_doesNotThrow() {
        Yukkuri from = createBody();
        Yukkuri child = createBody();
        ProudChildEvent event = new ProudChildEvent(from, null, null, 10);
        event.setState(ProudChildEvent.STATE.WAIT);
        from.setCurrentEvent(event);
        child.setCurrentEvent(event);
        assertDoesNotThrow(() -> event.update(child));
    }

    @Test
    public void testUpdate_childBody_stateSTART_bActionFlagTrue_doesNotThrow() {
        Yukkuri from = createBody();
        Yukkuri child = createBody();
        ProudChildEvent event = new ProudChildEvent(from, null, null, 10);
        event.setActionFlag(true);
        event.setState(ProudChildEvent.STATE.START);
        from.setCurrentEvent(event);
        child.setCurrentEvent(event);
        assertDoesNotThrow(() -> event.update(child));
    }

    @Test
    public void testUpdate_childBody_stateSING_bActionFlagTrue_doesNotThrow() {
        Yukkuri from = createBody();
        Yukkuri child = createBody();
        ProudChildEvent event = new ProudChildEvent(from, null, null, 10);
        event.setActionFlag(true);
        event.setState(ProudChildEvent.STATE.SING);
        from.setCurrentEvent(event);
        child.setCurrentEvent(event);
        assertDoesNotThrow(() -> event.update(child));
    }

    @Test
    public void testUpdate_childBody_statePROUD_bActionFlagTrue_doesNotThrow() {
        Yukkuri from = createBody();
        Yukkuri child = createBody();
        ProudChildEvent event = new ProudChildEvent(from, null, null, 10);
        event.setActionFlag(true);
        event.setState(ProudChildEvent.STATE.PROUD);
        from.setCurrentEvent(event);
        child.setCurrentEvent(event);
        assertDoesNotThrow(() -> event.update(child));
    }

    @Test
    public void testUpdate_childBody_stateEND_doesNotThrow() {
        Yukkuri from = createBody();
        Yukkuri child = createBody();
        ProudChildEvent event = new ProudChildEvent(from, null, null, 10);
        event.setState(ProudChildEvent.STATE.END);
        from.setCurrentEvent(event);
        child.setCurrentEvent(event);
        assertEquals(EventPacket.UpdateState.ABORT, event.update(child));
    }

    @Test
    public void testUpdate_parentBody_stateEND_returnsAbort() {
        Yukkuri from = createBody();
        Yukkuri child = createBody();
        child.setParents(new int[] { from.getUniqueID(), -1 });
        child.setAgeState(AgeState.BABY);
        from.getChildrenList().add(child.getUniqueID());
        ProudChildEvent event = new ProudChildEvent(from, null, null, 10);
        event.setState(ProudChildEvent.STATE.END);
        event.setActionFlag(false);
        from.setCurrentEvent(event);
        child.setCurrentEvent(event);
        assertEquals(EventPacket.UpdateState.ABORT, event.update(from));
    }

    @Test
    public void testUpdate_parentBody_allSleepingChildren_returnsAbort() {
        Yukkuri from = createBody();
        Yukkuri child = createBody();
        child.setParents(new int[] { from.getUniqueID(), -1 });
        child.setAgeState(AgeState.BABY);
        child.setSleeping(true);
        from.getChildrenList().add(child.getUniqueID());
        ProudChildEvent event = new ProudChildEvent(from, null, null, 10);
        from.setCurrentEvent(event);
        child.setCurrentEvent(event);

        assertEquals(EventPacket.UpdateState.ABORT, event.update(from));
    }

    // --- update: partner path ---

    @Test
    public void testUpdate_partnerOfFrom_stateGO_doesNotThrow() {
        Yukkuri from = createBody();
        Yukkuri partner = createBody();
        from.setPartner(partner.getUniqueID());
        partner.setPartner(from.getUniqueID());
        ProudChildEvent event = new ProudChildEvent(from, null, null, 10);
        from.setCurrentEvent(event);
        partner.setCurrentEvent(event);
        event.setState(ProudChildEvent.STATE.GO);
        assertDoesNotThrow(() -> event.update(partner));
    }

    @Test
    public void testUpdate_partnerOfFrom_stateNotGO_stays() {
        Yukkuri from = createBody();
        Yukkuri partner = createBody();
        from.setPartner(partner.getUniqueID());
        partner.setPartner(from.getUniqueID());
        ProudChildEvent event = new ProudChildEvent(from, null, null, 10);
        from.setCurrentEvent(event);
        partner.setCurrentEvent(event);
        event.setState(ProudChildEvent.STATE.WAIT);
        assertNull(event.update(partner));
    }

    // --- update: more ABORT paths ---

    @Test
    public void testUpdate_panicType_returnsAbort() {
        Yukkuri from = createBody();
        Yukkuri b = createBody();
        b.setPanicType(PanicType.FEAR); // b.getPanicType() != null → ABORT
        ProudChildEvent event = new ProudChildEvent(from, null, null, 10);
        from.setCurrentEvent(event);
        b.setCurrentEvent(event);
        assertEquals(EventPacket.UpdateState.ABORT, event.update(b));
    }

    @Test
    public void testUpdate_fromUnhappy_returnsAbort() {
        Yukkuri from = createBody();
        Yukkuri b = createBody();
        from.setHappiness(Happiness.SAD); // isUnhappy() = true → ABORT
        ProudChildEvent event = new ProudChildEvent(from, null, null, 10);
        from.setCurrentEvent(event);
        b.setCurrentEvent(event);
        assertEquals(EventPacket.UpdateState.ABORT, event.update(b));
    }

    @Test
    public void testUpdate_childDamaged_returnsAbort() {
        Yukkuri from = createBody();
        Yukkuri baby = createBody();
        WorldTestHelper.setParents(baby, -1, from.getUniqueID());
        baby.setAgeState(AgeState.BABY);
        baby.setBirthAge(0);
        baby.setFirstGround(false);
        baby.setDamage(baby.getDamageLimit() / 2 + 1);
        ProudChildEvent event = new ProudChildEvent(from, null, null, 10);
        from.setCurrentEvent(event);
        baby.setCurrentEvent(event);
        assertEquals(EventPacket.UpdateState.ABORT, event.update(baby));
    }

    // --- update: tick % 30 != 0 → returns null ---
    @Test
    public void testUpdate_tick1_returnsNull() {
        Yukkuri from = createBody();
        Yukkuri b = createBody();
        ProudChildEvent event = new ProudChildEvent(from, null, null, 10);
        event.setTick(1); // 1 % 30 = 1 != 0 → returns null
        from.setCurrentEvent(event);
        b.setCurrentEvent(event);
        assertNull(event.update(b));
    }

    // --- update: child GO, isDontMove → ABORT ---
    @Test
    public void testUpdate_childBody_GO_isDontMove_returnsAbort() {
        Yukkuri from = createBody();
        Yukkuri b = createBody();
        b.setGrabbed(true); // isDontMove() returns true
        ProudChildEvent event = new ProudChildEvent(from, null, null, 10);
        from.setCurrentEvent(event);
        b.setCurrentEvent(event);
        assertEquals(EventPacket.UpdateState.ABORT, event.update(b));
    }

    // --- update: child SING, actionFlag=false (inner path) ---
    @Test
    public void testUpdate_childBody_SING_bActionFlagFalse_doesNotThrow() {
        Yukkuri from = createBody();
        Yukkuri child = createBody();
        ProudChildEvent event = new ProudChildEvent(from, null, null, 10);
        event.setActionFlag(false);
        event.setState(ProudChildEvent.STATE.SING);
        from.setCurrentEvent(event);
        child.setCurrentEvent(event);
        assertDoesNotThrow(() -> event.update(child));
    }

    // --- checkEventResponse: b is child of from (baby age) → returns true ---
    @Test
    public void testCheckEventResponse_isChildOfFrom_baby_returnsTrue() {
        Yukkuri from = createBody();
        Yukkuri child = createBody();
        // Set child's parents so that from is parent of child
        child.setParents(new int[] { from.getUniqueID(), -1 });
        // Make child BABY (not adult)
        child.setAgeState(AgeState.BABY);
        ProudChildEvent event = new ProudChildEvent(from, null, null, 10);
        // from is in world map, child.getFather() = from.uniqueID
        assertTrue(event.checkEventResponse(child));
    }

    @Nested
    class RegressionScenarios {

        @Test
        void testScenario_ChildParticipationBecomesHappy() {
            Yukkuri from = createBody();
            Yukkuri child = createBody();
            child.setParents(new int[] { from.getUniqueID(), -1 });
            child.setAgeState(AgeState.BABY);
            child.setHappiness(Happiness.SAD);

            ProudChildEvent event = new ProudChildEvent(from, null, null, 10);

            assertTrue(event.checkEventResponse(child));
            assertEquals(Happiness.HAPPY, child.getHappiness());
        }

        @Test
        void testScenario_ChildGoRandomHitMakesVeryHappyAndAddsMemory() {
            Yukkuri from = createBody();
            Yukkuri child = createBody();
            child.setIntelligence(Intelligence.FOOL);
            ProudChildEvent event = new ProudChildEvent(from, null, null, 10);
            from.setCurrentEvent(event);
            child.setCurrentEvent(event);
            int memoriesBefore = child.getMemories();

            GameRandom.setOverride(new RandomSource() {
                @Override
                public int nextInt(int bound) {
                    return 0;
                }

                @Override
                public boolean nextBoolean() {
                    return false;
                }
            });

            assertNull(event.update(child));

            assertEquals(Happiness.VERY_HAPPY, child.getHappiness());
            assertEquals(memoriesBefore + 10, child.getMemories());
        }

        @Test
        void testScenario_FromProudStateBecomesVeryHappyAndAddsMemories() {
            Yukkuri from = createBody();
            Yukkuri child = createBody();
            child.setParents(new int[] { from.getUniqueID(), -1 });
            child.setAgeState(AgeState.BABY);
            from.setIntelligence(Intelligence.FOOL);
            from.getChildrenList().add(child.getUniqueID());

            ProudChildEvent event = new ProudChildEvent(from, null, null, 10);
            from.setCurrentEvent(event);
            child.setCurrentEvent(event);
            event.setState(ProudChildEvent.STATE.PROUD);
            event.setActionFlag(false);
            from.setLastActionTime(0);
            int memoriesBefore = from.getMemories();

            assertNull(event.update(from));
            assertTrue(event.isActionFlag());
            assertEquals(Happiness.VERY_HAPPY, from.getHappiness());
            assertEquals(memoriesBefore + 20, from.getMemories());
        }

        @Test
        void testScenario_RudeChildProudCanEnterFurifuriPath() {
            Yukkuri from = createBody();
            Yukkuri child = createBody();
            child.setAgeState(AgeState.BABY);
            child.setIntelligence(Intelligence.FOOL);
            child.setAttitude(org.simyukkuri.enums.Attitude.SUPER_SHITHEAD);

            ProudChildEvent event = new ProudChildEvent(from, null, null, 10);
            from.setCurrentEvent(event);
            child.setCurrentEvent(event);
            event.setState(ProudChildEvent.STATE.PROUD);
            event.setActionFlag(true);
            child.setLastActionTime(0);
            int memoriesBefore = child.getMemories();

            GameRandom.setOverride(new RandomSource() {
                @Override
                public int nextInt(int bound) {
                    return 0;
                }

                @Override
                public boolean nextBoolean() {
                    return true;
                }
            });

            assertNull(event.update(child));

            assertTrue(child.isFurifuri());
            assertEquals(memoriesBefore + 20, child.getMemories());
        }
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
        SimYukkuri.world.getCurrentMap().getYukkuriMap().put(b.getUniqueID(), b);
        return b;
    }
}
