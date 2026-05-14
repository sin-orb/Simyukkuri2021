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
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.simyukkuri.SimYukkuri;
import org.simyukkuri.draw.World;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.living.yukkuri.impl.Reimu;
import org.simyukkuri.entity.core.world.item.Food;
import org.simyukkuri.enums.AgeState;
import org.simyukkuri.enums.Happiness;
import org.simyukkuri.event.EventPacket.EventPriority;
import org.simyukkuri.system.Sprite;
import org.simyukkuri.util.WorldTestHelper;

public class SuperEatingTimeEventTest {

    @BeforeEach
    public void setUp() {
        WorldTestHelper.resetWorld();
        SimYukkuri.world = new World();
        WorldTestHelper.initializeStandardTranslate500();
    }

    // --- Default constructor ---

    @Test
    public void testDefaultConstructor() {
        SuperEatingTimeEvent event = new SuperEatingTimeEvent();
        assertEquals(-1, event.getFrom());
        assertEquals(-1, event.getTo());
        assertEquals(EventPriority.LOW, event.getPriority());
    }

    // --- Parameterized constructor ---

    @Test
    public void testParameterizedConstructor_setsPriorityHigh() {
        Yukkuri from = createBody();
        Yukkuri to = createBody();
        SuperEatingTimeEvent event = new SuperEatingTimeEvent(from, to, null, 10);
        assertEquals(EventPriority.HIGH, event.getPriority());
        assertEquals(from.getUniqueID(), event.getFrom());
        assertEquals(to.getUniqueID(), event.getTo());
        assertEquals(10, event.getCount());
    }

    // --- simpleEventAction ---

    @Test
    public void testSimpleEventAction_returnsTrueWhenFromIsNull() {
        Yukkuri b = createBody();
        SuperEatingTimeEvent event = new SuperEatingTimeEvent();
        // from is -1 (null lookup) => returns true
        assertTrue(event.simpleEventAction(b));
    }

    // --- checkEventResponse ---

    @Test
    public void testCheckEventResponse_returnsFalseWhenFromIsNull() {
        Yukkuri b = createBody();
        SuperEatingTimeEvent event = new SuperEatingTimeEvent();
        // default constructor: from is null => returns false
        assertFalse(event.checkEventResponse(b));
    }

    @Test
    public void testCheckEventResponse_returnsTrueWhenFromEqualsBAndNotShutmouth() {
        Yukkuri b = createBody();
        SuperEatingTimeEvent event = new SuperEatingTimeEvent(b, null, null, 10);
        // from == b and b is not shutmouth => returns true
        assertTrue(event.checkEventResponse(b));
    }

    @Test
    public void testCheckEventResponse_returnsFalseWhenDead() {
        Yukkuri from = createBody();
        Yukkuri b = createBody();
        b.setDead(true);
        SuperEatingTimeEvent event = new SuperEatingTimeEvent(from, null, null, 10);
        // b is dead => returns false (after checking from != b, isShutmouth/isDead
        // check)
        assertFalse(event.checkEventResponse(b));
    }

    // --- execute ---

    @Test
    public void testExecute_returnsFalse() {
        Yukkuri b = createBody();
        SuperEatingTimeEvent event = new SuperEatingTimeEvent(b, null, null, 10);
        assertFalse(event.execute(b));
    }

    // --- getState / setState ---

    @Test
    public void testGetState_defaultIsWAIT() {
        SuperEatingTimeEvent event = new SuperEatingTimeEvent();
        assertEquals(SuperEatingTimeEvent.STATE.WAIT, event.getState());
    }

    @Test
    public void testSetState() {
        SuperEatingTimeEvent event = new SuperEatingTimeEvent();
        event.setState(SuperEatingTimeEvent.STATE.GO);
        assertEquals(SuperEatingTimeEvent.STATE.GO, event.getState());

        event.setState(SuperEatingTimeEvent.STATE.START);
        assertEquals(SuperEatingTimeEvent.STATE.START, event.getState());

        event.setState(SuperEatingTimeEvent.STATE.END);
        assertEquals(SuperEatingTimeEvent.STATE.END, event.getState());
    }

    // --- getMinimumStep ---

    @Test
    public void testGetMinimumStep_defaultIsZero() {
        SuperEatingTimeEvent event = new SuperEatingTimeEvent();
        assertEquals(0, event.getMinimumStep());
    }

    // --- simpleEventAction (from not null, not shutmouth) ---
    @Test
    public void testSimpleEventAction_fromNotNull_returnsFalse() {
        Yukkuri from = createBody();
        SuperEatingTimeEvent event = new SuperEatingTimeEvent(from, null, null, 10);
        assertFalse(event.simpleEventAction(from));
    }

    // --- checkEventResponse (different publicRank) ---
    @Test
    public void testCheckEventResponse_differentPublicRank_returnsFalse() {
        Yukkuri from = createBody();
        Yukkuri b = createBody();
        b.setPublicRank(org.simyukkuri.enums.PublicRank.UnunSlave);
        SuperEatingTimeEvent event = new SuperEatingTimeEvent(from, null, null, 10);
        assertFalse(event.checkEventResponse(b));
    }

    @Test
    public void testCheckEventResponse_partner_returnsTrue() {
        Yukkuri from = createBody();
        Yukkuri partner = createBody();
        from.setPartner(partner.getUniqueID());
        partner.setPartner(from.getUniqueID());
        SuperEatingTimeEvent event = new SuperEatingTimeEvent(from, null, null, 10);
        assertTrue(event.checkEventResponse(partner));
    }

    // --- start ---
    @Test
    public void testStart_setsCurrentEvent() {
        Yukkuri b = createBody();
        SuperEatingTimeEvent event = new SuperEatingTimeEvent(b, null, null, 10);
        event.start(b);
        assertEquals(event, b.getCurrentEvent());
    }

    // --- update ---
    @Test
    public void testUpdate_fromNull_returnsAbort() {
        Yukkuri b = createBody();
        SuperEatingTimeEvent event = new SuperEatingTimeEvent();
        assertEquals(org.simyukkuri.event.EventPacket.UpdateState.ABORT, event.update(b));
    }

    @Test
    public void testUpdate_bodyNYD_returnsAbort() {
        Yukkuri from = createBody();
        Yukkuri b = createBody();
        b.setCoreAnkoState(org.simyukkuri.enums.CoreAnkoState.NonYukkuriDisease);
        SuperEatingTimeEvent event = new SuperEatingTimeEvent(from, null, null, 10);
        assertEquals(org.simyukkuri.event.EventPacket.UpdateState.ABORT, event.update(b));
    }

    @Test
    public void testUpdate_fromRemoved_returnsAbort() {
        Yukkuri from = createBody();
        Yukkuri b = createBody();
        from.setRemoved(true);
        SuperEatingTimeEvent event = new SuperEatingTimeEvent(from, null, null, 10);
        assertEquals(org.simyukkuri.event.EventPacket.UpdateState.ABORT, event.update(b));
    }

    @Test
    public void testUpdate_targetNull_returnsAbort() {
        Yukkuri from = createBody();
        Yukkuri b = createBody();
        // target=-1 → takeMappedObj returns null → ABORT
        SuperEatingTimeEvent event = new SuperEatingTimeEvent(from, null, null, 10);
        from.setCurrentEvent(event);
        assertEquals(org.simyukkuri.event.EventPacket.UpdateState.ABORT, event.update(b));
    }

    // --- toString ---
    @Test
    public void testToString_doesNotThrow() {
        SuperEatingTimeEvent event = new SuperEatingTimeEvent();
        // ResourceUtil may not be loaded in headless test env → result may be null, but
        // should not throw
        assertDoesNotThrow(() -> event.toString());
    }

    // --- STATE enum ---
    @Test
    public void testStateEnum_allValues() {
        SuperEatingTimeEvent.STATE[] states = SuperEatingTimeEvent.STATE.values();
        assertEquals(5, states.length);
        for (SuperEatingTimeEvent.STATE s : states) {
            assertNotNull(s.name());
        }
    }

    // --- update() deeper path tests ---

    @Test
    public void testUpdate_parentBranch_noChildren_returnsAbort() {
        Yukkuri from = createBody();
        Food food = createFood();
        SuperEatingTimeEvent event = new SuperEatingTimeEvent(from, null, food, 10);
        from.setCurrentEvent(event);
        // b == from → parent branch, no children → ABORT
        assertEquals(org.simyukkuri.event.EventPacket.UpdateState.ABORT, event.update(from));
    }

    @Test
    public void testUpdate_tickNotMultipleOf20_returnsNull() {
        Yukkuri from = createBody();
        Yukkuri b = createBody();
        Food food = createFood();
        SuperEatingTimeEvent event = new SuperEatingTimeEvent(from, null, food, 10);
        from.setCurrentEvent(event);
        event.tick = 1; // 1 % 20 != 0 → return null
        assertNull(event.update(b));
    }

    @Test
    public void testUpdate_childBranch_defaultState_returnsNull() {
        Yukkuri from = createBody();
        Yukkuri b = createBody(); // b != from, no partner relationship
        Food food = createFood();
        SuperEatingTimeEvent event = new SuperEatingTimeEvent(from, null, food, 10);
        from.setCurrentEvent(event);
        // tick=0 (0%20==0), b != from, not partner → child switch default (WAIT) case →
        // null
        assertNull(event.update(b));
    }

    @Test
    public void testUpdate_childBranch_partnerOfFrom_returnsNull() {
        Yukkuri from = createBody();
        Yukkuri partner = createBody();
        from.setPartner(partner.getUniqueID());
        partner.setPartner(from.getUniqueID());
        Food food = createFood();
        SuperEatingTimeEvent event = new SuperEatingTimeEvent(from, null, food, 10);
        from.setCurrentEvent(event);
        // b == partner (not from), isPartner(from) == true → partner branch → null
        assertNull(event.update(partner));
    }

    @Test
    public void testUpdate_nFromWaitCountOver10_fromNoEvent_returnsAbort() {
        Yukkuri from = createBody();
        Yukkuri b = createBody();
        Food food = createFood();
        SuperEatingTimeEvent event = new SuperEatingTimeEvent(from, null, food, 10);
        // from.getCurrentEvent() == null (not set), waitTicks=11 → line 166 ABORT
        event.waitTicks = 11;
        assertEquals(org.simyukkuri.event.EventPacket.UpdateState.ABORT, event.update(b));
    }

    @Test
    public void testUpdate_parentBranch_withAdultChild_returnsAbort() {
        Yukkuri from = createBody();
        Yukkuri child = createBody(); // createBody() sets ADULT age → filtered from activeChildList
        from.addChildrenList(child);
        Food food = createFood();
        SuperEatingTimeEvent event = new SuperEatingTimeEvent(from, null, food, 10);
        from.setCurrentEvent(event);
        // child is ADULT → createActiveChildList returns empty list → ABORT
        assertEquals(org.simyukkuri.event.EventPacket.UpdateState.ABORT, event.update(from));
    }

    @Test
    public void testUpdate_childBranch_GOState_returnsNull() {
        Yukkuri from = createBody();
        Yukkuri b = createBody();
        Food food = createFood();
        SuperEatingTimeEvent event = new SuperEatingTimeEvent(from, null, food, 10);
        event.setState(SuperEatingTimeEvent.STATE.GO);
        from.setCurrentEvent(event);
        // child GO branch: Barrier.onBarrier check, then setHappiness, then null
        assertNull(event.update(b));
    }

    // --- update: child branch various states ---

    @Test
    public void testUpdate_childBranch_START_BEFORE_State_doesNotThrow() {
        Yukkuri from = createBody();
        Yukkuri child = createBody();
        Food food = createFood();
        SuperEatingTimeEvent event = new SuperEatingTimeEvent(from, null, food, 10);
        event.setState(SuperEatingTimeEvent.STATE.START_BEFORE);
        from.setCurrentEvent(event);
        child.setCurrentEvent(event);
        assertDoesNotThrow(() -> event.update(child));
    }

    @Test
    public void testUpdate_childBranch_START_State_doesNotThrow() {
        Yukkuri from = createBody();
        Yukkuri child = createBody();
        Food food = createFood();
        SuperEatingTimeEvent event = new SuperEatingTimeEvent(from, null, food, 10);
        event.setState(SuperEatingTimeEvent.STATE.START);
        from.setCurrentEvent(event);
        child.setCurrentEvent(event);
        assertDoesNotThrow(() -> event.update(child));
    }

    @Test
    public void testUpdate_childBranch_WAIT_State_doesNotThrow() {
        Yukkuri from = createBody();
        Yukkuri child = createBody();
        Food food = createFood();
        SuperEatingTimeEvent event = new SuperEatingTimeEvent(from, null, food, 10);
        event.setState(SuperEatingTimeEvent.STATE.WAIT);
        from.setCurrentEvent(event);
        child.setCurrentEvent(event);
        assertDoesNotThrow(() -> event.update(child));
    }

    // --- update: parent branch WAIT state ---

    @Test
    public void testUpdate_parentBranch_WAIT_state_withBabyChild_doesNotThrow() {
        Yukkuri from = createBody();
        Yukkuri child = createBody();
        child.setAgeState(AgeState.BABY);
        // Add child as from's child so createActiveChildList returns it
        from.addChildrenList(child);
        SimYukkuri.world.getCurrentMap().getYukkuriMap().put(child.getUniqueID(), child);
        Food food = createFood();
        SuperEatingTimeEvent event = new SuperEatingTimeEvent(from, null, food, 10);
        event.setState(SuperEatingTimeEvent.STATE.WAIT);
        from.setCurrentEvent(event);
        assertDoesNotThrow(() -> event.update(from));
    }

    @Test
    public void testUpdate_parentBranch_GO_state_withBabyChild_doesNotThrow() {
        Yukkuri from = createBody();
        Yukkuri child = createBody();
        child.setAgeState(AgeState.BABY);
        from.addChildrenList(child);
        SimYukkuri.world.getCurrentMap().getYukkuriMap().put(child.getUniqueID(), child);
        Food food = createFood();
        SuperEatingTimeEvent event = new SuperEatingTimeEvent(from, null, food, 10);
        event.setState(SuperEatingTimeEvent.STATE.GO);
        from.setCurrentEvent(event);
        assertDoesNotThrow(() -> event.update(from));
    }

    // --- checkEventResponse: isDontMove → false ---
    @Test
    public void testCheckEventResponse_isDontMove_returnsFalse() {
        Yukkuri from = createBody();
        Yukkuri b = createBody();
        b.setGrabbed(true); // isDontMove() = true
        SuperEatingTimeEvent event = new SuperEatingTimeEvent(from, null, null, 10);
        assertFalse(event.checkEventResponse(b));
    }

    // --- checkEventResponse: isNYD → false ---
    @Test
    public void testCheckEventResponse_isNYD_returnsFalse() {
        Yukkuri from = createBody();
        Yukkuri b = createBody();
        b.setCoreAnkoState(org.simyukkuri.enums.CoreAnkoState.NonYukkuriDisease);
        SuperEatingTimeEvent event = new SuperEatingTimeEvent(from, null, null, 10);
        assertFalse(event.checkEventResponse(b));
    }

    // --- checkEventResponse: not child of from → false ---
    @Test
    public void testCheckEventResponse_notChildOfFrom_returnsFalse() {
        Yukkuri from = createBody();
        Yukkuri other = createBody(); // no parent relationship
        SuperEatingTimeEvent event = new SuperEatingTimeEvent(from, null, null, 10);
        assertFalse(event.checkEventResponse(other));
    }

    // --- checkEventResponse: child of from but adult → false ---
    @Test
    public void testCheckEventResponse_isChild_adult_returnsFalse() {
        Yukkuri from = createBody();
        Yukkuri child = createBody(); // ADULT by default
        child.setParents(new int[] { from.getUniqueID(), -1 });
        SuperEatingTimeEvent event = new SuperEatingTimeEvent(from, null, null, 10);
        assertFalse(event.checkEventResponse(child));
    }

    // --- checkEventResponse: baby child of from → true ---
    @Test
    public void testCheckEventResponse_babyChild_returnsTrue() {
        Yukkuri from = createBody();
        Yukkuri child = createBody();
        child.setAgeState(AgeState.BABY);
        child.setParents(new int[] { from.getUniqueID(), -1 });
        SuperEatingTimeEvent event = new SuperEatingTimeEvent(from, null, null, 10);
        assertTrue(event.checkEventResponse(child));
    }

    // --- update: waitTicks > 5000 → ABORT ---
    @Test
    public void testUpdate_nFromWaitCountOver5000_returnsAbort() {
        Yukkuri from = createBody();
        Food food = createFood();
        SuperEatingTimeEvent event = new SuperEatingTimeEvent(from, null, food, 10);
        from.setCurrentEvent(event);
        event.waitTicks = 5001;
        assertEquals(org.simyukkuri.event.EventPacket.UpdateState.ABORT, event.update(from));
    }

    // --- simpleEventAction: from.isShutmouth() → true ---
    @Test
    public void testSimpleEventAction_fromShutmouth_returnsTrue() {
        Yukkuri from = createBody();
        from.setShutmouth(true);
        SuperEatingTimeEvent event = new SuperEatingTimeEvent(from, null, null, 10);
        assertTrue(event.simpleEventAction(from));
    }

    // --- update: parent START_BEFORE state with baby child → does not throw ---
    @Test
    public void testUpdate_parentBranch_START_BEFORE_withBabyChild_doesNotThrow() {
        Yukkuri from = createBody();
        Yukkuri child = createBody();
        child.setAgeState(AgeState.BABY);
        from.addChildrenList(child);
        SimYukkuri.world.getCurrentMap().getYukkuriMap().put(child.getUniqueID(), child);
        Food food = createFood();
        SuperEatingTimeEvent event = new SuperEatingTimeEvent(from, null, food, 10);
        event.setState(SuperEatingTimeEvent.STATE.START_BEFORE);
        from.setCurrentEvent(event);
        SimYukkuri.RND = new org.simyukkuri.ConstState(1);
        try {
            assertDoesNotThrow(() -> event.update(from));
        } finally {
            SimYukkuri.RND = new java.util.Random();
        }
    }

    // --- update: parent START state with baby child → does not throw ---
    @Test
    public void testUpdate_parentBranch_START_withBabyChild_doesNotThrow() {
        Yukkuri from = createBody();
        Yukkuri child = createBody();
        child.setAgeState(AgeState.BABY);
        from.addChildrenList(child);
        SimYukkuri.world.getCurrentMap().getYukkuriMap().put(child.getUniqueID(), child);
        Food food = createFood();
        SuperEatingTimeEvent event = new SuperEatingTimeEvent(from, null, food, 10);
        event.setState(SuperEatingTimeEvent.STATE.START);
        from.setCurrentEvent(event);
        from.setHungry(from.getHungryLimit()); // not very hungry
        SimYukkuri.RND = new org.simyukkuri.ConstState(1);
        try {
            assertDoesNotThrow(() -> event.update(from));
        } finally {
            SimYukkuri.RND = new java.util.Random();
        }
    }

    @Test
    public void testUpdate_foodEmpty_returnsAbort() {
        Yukkuri from = createBody();
        Yukkuri child = createBody();
        child.setAgeState(AgeState.BABY);
        from.addChildrenList(child);
        Food food = createFood();
        food.setAmount(0); // empty food

        SuperEatingTimeEvent event = new SuperEatingTimeEvent(from, null, food, 10);
        event.setState(SuperEatingTimeEvent.STATE.START);
        from.setCurrentEvent(event);
        child.setCurrentEvent(event);

        // food.isEmpty() check in update is only in parent branch (b == from)
        assertEquals(org.simyukkuri.event.EventPacket.UpdateState.ABORT, event.update(from));
        assertEquals(org.simyukkuri.enums.Happiness.VERY_SAD, from.getHappiness());
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

    private Food createFood() {
        Food food = new Food(100, 100, Food.FoodType.SWEETS1.ordinal());
        SimYukkuri.world.getCurrentMap().getFood().put(food.getObjId(), food);
        return food;
    }

    @Nested
    class RegressionScenarios {

        @Test
        void testScenario_RemovedTargetMakesParentVerySadAndAborts() {
            Yukkuri from = createBody();
            Yukkuri child = createBody();
            Food food = createFood();
            food.remove();
            from.setHappiness(Happiness.HAPPY);

            SuperEatingTimeEvent event = new SuperEatingTimeEvent(from, null, food, 10);
            from.setCurrentEvent(event);

            assertEquals(org.simyukkuri.event.EventPacket.UpdateState.ABORT, event.update(child));
            assertEquals(Happiness.VERY_SAD, from.getHappiness());
        }

        @Test
        void testScenario_ParentStartWithSatiatedChildTargetsFoodAndGetsNoHungryPeriod() {
            Yukkuri from = createBody();
            Yukkuri child = createBody();
            child.setAgeState(AgeState.BABY);
            child.setHungry(child.getHungryLimit());
            from.addChildrenList(child);
            SimYukkuri.world.getCurrentMap().getYukkuriMap().put(child.getUniqueID(), child);
            Food food = createFood();

            SuperEatingTimeEvent event = new SuperEatingTimeEvent(from, null, food, 10);
            event.setState(SuperEatingTimeEvent.STATE.START);
            from.setCurrentEvent(event);

            assertNull(event.update(from));
            assertTrue(from.isToFood());
            assertEquals(food.getObjId(), from.getMoveTargetId());
            assertEquals(500, from.getSuperEatingNoHungryPeriod());
        }

        @Test
        void testScenario_ChildStartNearFoodActuallyEatsAndClearsActions() {
            Yukkuri from = createBody();
            Yukkuri child = createBody();
            child.setAgeState(AgeState.BABY);
            Food food = createFood();
            child.setX(food.getX());
            child.setY(food.getY());
            int foodBefore = food.getAmount();
            int memoriesBefore = child.getMemories();

            SuperEatingTimeEvent event = new SuperEatingTimeEvent(from, null, food, 10);
            event.setState(SuperEatingTimeEvent.STATE.START);
            from.setCurrentEvent(event);
            child.setCurrentEvent(event);

            assertNull(event.update(child));
            assertTrue(food.getAmount() < foodBefore);
            assertTrue(child.getMemories() > memoriesBefore);
            assertFalse(child.isToFood());
            assertEquals(-1, child.getMoveTargetId());
        }
    }
}
