package src.event;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import src.SimYukkuri;
import src.base.Body;
import src.base.EventPacket.EventPriority;
import src.draw.Translate;
import src.draw.World;
import src.enums.AgeState;
import src.item.Food;
import src.system.Sprite;
import src.yukkuri.Reimu;

public class SuperEatingTimeEventTest {

    @BeforeEach
    public void setUp() {
        SimYukkuri.world = new World();
        Translate.setMapSize(1000, 1000, 500);
        Translate.setCanvasSize(800, 600, 100, 100, new float[] { 1.0f });
        Translate.createTransTable(false);
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
        Body from = createBody();
        Body to = createBody();
        SuperEatingTimeEvent event = new SuperEatingTimeEvent(from, to, null, 10);
        assertEquals(EventPriority.HIGH, event.getPriority());
        assertEquals(from.getUniqueID(), event.getFrom());
        assertEquals(to.getUniqueID(), event.getTo());
        assertEquals(10, event.getCount());
    }

    // --- simpleEventAction ---

    @Test
    public void testSimpleEventAction_returnsTrueWhenFromIsNull() {
        Body b = createBody();
        SuperEatingTimeEvent event = new SuperEatingTimeEvent();
        // from is -1 (null lookup) => returns true
        assertTrue(event.simpleEventAction(b));
    }

    // --- checkEventResponse ---

    @Test
    public void testCheckEventResponse_returnsFalseWhenFromIsNull() {
        Body b = createBody();
        SuperEatingTimeEvent event = new SuperEatingTimeEvent();
        // default constructor: from is null => returns false
        assertFalse(event.checkEventResponse(b));
    }

    @Test
    public void testCheckEventResponse_returnsTrueWhenFromEqualsBAndNotShutmouth() {
        Body b = createBody();
        SuperEatingTimeEvent event = new SuperEatingTimeEvent(b, null, null, 10);
        // from == b and b is not shutmouth => returns true
        assertTrue(event.checkEventResponse(b));
    }

    @Test
    public void testCheckEventResponse_returnsFalseWhenDead() {
        Body from = createBody();
        Body b = createBody();
        b.setDead(true);
        SuperEatingTimeEvent event = new SuperEatingTimeEvent(from, null, null, 10);
        // b is dead => returns false (after checking from != b, isShutmouth/isDead
        // check)
        assertFalse(event.checkEventResponse(b));
    }

    // --- execute ---

    @Test
    public void testExecute_returnsFalse() {
        Body b = createBody();
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

    // --- getLowestStep ---

    @Test
    public void testGetLowestStep_defaultIsZero() {
        SuperEatingTimeEvent event = new SuperEatingTimeEvent();
        assertEquals(0, event.getLowestStep());
    }

    // --- simpleEventAction (from not null, not shutmouth) ---
    @Test
    public void testSimpleEventAction_fromNotNull_returnsFalse() {
        Body from = createBody();
        SuperEatingTimeEvent event = new SuperEatingTimeEvent(from, null, null, 10);
        assertFalse(event.simpleEventAction(from));
    }

    // --- checkEventResponse (different publicRank) ---
    @Test
    public void testCheckEventResponse_differentPublicRank_returnsFalse() {
        Body from = createBody();
        Body b = createBody();
        b.setPublicRank(src.enums.PublicRank.UnunSlave);
        SuperEatingTimeEvent event = new SuperEatingTimeEvent(from, null, null, 10);
        assertFalse(event.checkEventResponse(b));
    }

    @Test
    public void testCheckEventResponse_partner_returnsTrue() {
        Body from = createBody();
        Body partner = createBody();
        from.setPartner(partner.getUniqueID());
        partner.setPartner(from.getUniqueID());
        SuperEatingTimeEvent event = new SuperEatingTimeEvent(from, null, null, 10);
        assertTrue(event.checkEventResponse(partner));
    }

    // --- start ---
    @Test
    public void testStart_setsCurrentEvent() {
        Body b = createBody();
        SuperEatingTimeEvent event = new SuperEatingTimeEvent(b, null, null, 10);
        event.start(b);
        assertEquals(event, b.getCurrentEvent());
    }

    // --- update ---
    @Test
    public void testUpdate_fromNull_returnsAbort() {
        Body b = createBody();
        SuperEatingTimeEvent event = new SuperEatingTimeEvent();
        assertEquals(src.base.EventPacket.UpdateState.ABORT, event.update(b));
    }

    @Test
    public void testUpdate_bodyNYD_returnsAbort() {
        Body from = createBody();
        Body b = createBody();
        b.seteCoreAnkoState(src.enums.CoreAnkoState.NonYukkuriDisease);
        SuperEatingTimeEvent event = new SuperEatingTimeEvent(from, null, null, 10);
        assertEquals(src.base.EventPacket.UpdateState.ABORT, event.update(b));
    }

    @Test
    public void testUpdate_fromRemoved_returnsAbort() {
        Body from = createBody();
        Body b = createBody();
        from.setRemoved(true);
        SuperEatingTimeEvent event = new SuperEatingTimeEvent(from, null, null, 10);
        assertEquals(src.base.EventPacket.UpdateState.ABORT, event.update(b));
    }

    @Test
    public void testUpdate_targetNull_returnsAbort() {
        Body from = createBody();
        Body b = createBody();
        // target=-1 → takeMappedObj returns null → ABORT
        SuperEatingTimeEvent event = new SuperEatingTimeEvent(from, null, null, 10);
        from.setCurrentEvent(event);
        assertEquals(src.base.EventPacket.UpdateState.ABORT, event.update(b));
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
        Body from = createBody();
        Food food = createFood();
        SuperEatingTimeEvent event = new SuperEatingTimeEvent(from, null, food, 10);
        from.setCurrentEvent(event);
        // b == from → parent branch, no children → ABORT
        assertEquals(src.base.EventPacket.UpdateState.ABORT, event.update(from));
    }

    @Test
    public void testUpdate_tickNotMultipleOf20_returnsNull() {
        Body from = createBody();
        Body b = createBody();
        Food food = createFood();
        SuperEatingTimeEvent event = new SuperEatingTimeEvent(from, null, food, 10);
        from.setCurrentEvent(event);
        event.tick = 1; // 1 % 20 != 0 → return null
        assertNull(event.update(b));
    }

    @Test
    public void testUpdate_childBranch_defaultState_returnsNull() {
        Body from = createBody();
        Body b = createBody(); // b != from, no partner relationship
        Food food = createFood();
        SuperEatingTimeEvent event = new SuperEatingTimeEvent(from, null, food, 10);
        from.setCurrentEvent(event);
        // tick=0 (0%20==0), b != from, not partner → child switch default (WAIT) case →
        // null
        assertNull(event.update(b));
    }

    @Test
    public void testUpdate_childBranch_partnerOfFrom_returnsNull() {
        Body from = createBody();
        Body partner = createBody();
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
        Body from = createBody();
        Body b = createBody();
        Food food = createFood();
        SuperEatingTimeEvent event = new SuperEatingTimeEvent(from, null, food, 10);
        // from.getCurrentEvent() == null (not set), nFromWaitCount=11 → line 166 ABORT
        event.nFromWaitCount = 11;
        assertEquals(src.base.EventPacket.UpdateState.ABORT, event.update(b));
    }

    @Test
    public void testUpdate_parentBranch_withAdultChild_returnsAbort() {
        Body from = createBody();
        Body child = createBody(); // createBody() sets ADULT age → filtered from activeChildList
        from.addChildrenList(child);
        Food food = createFood();
        SuperEatingTimeEvent event = new SuperEatingTimeEvent(from, null, food, 10);
        from.setCurrentEvent(event);
        // child is ADULT → createActiveChildList returns empty list → ABORT
        assertEquals(src.base.EventPacket.UpdateState.ABORT, event.update(from));
    }

    @Test
    public void testUpdate_childBranch_GOState_returnsNull() {
        Body from = createBody();
        Body b = createBody();
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
        Body from = createBody();
        Body child = createBody();
        Food food = createFood();
        SuperEatingTimeEvent event = new SuperEatingTimeEvent(from, null, food, 10);
        event.setState(SuperEatingTimeEvent.STATE.START_BEFORE);
        from.setCurrentEvent(event);
        child.setCurrentEvent(event);
        assertDoesNotThrow(() -> event.update(child));
    }

    @Test
    public void testUpdate_childBranch_START_State_doesNotThrow() {
        Body from = createBody();
        Body child = createBody();
        Food food = createFood();
        SuperEatingTimeEvent event = new SuperEatingTimeEvent(from, null, food, 10);
        event.setState(SuperEatingTimeEvent.STATE.START);
        from.setCurrentEvent(event);
        child.setCurrentEvent(event);
        assertDoesNotThrow(() -> event.update(child));
    }

    @Test
    public void testUpdate_childBranch_WAIT_State_doesNotThrow() {
        Body from = createBody();
        Body child = createBody();
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
        Body from = createBody();
        Body child = createBody();
        child.setAgeState(AgeState.BABY);
        // Add child as from's child so createActiveChildList returns it
        from.addChildrenList(child);
        SimYukkuri.world.getCurrentMap().getBody().put(child.getUniqueID(), child);
        Food food = createFood();
        SuperEatingTimeEvent event = new SuperEatingTimeEvent(from, null, food, 10);
        event.setState(SuperEatingTimeEvent.STATE.WAIT);
        from.setCurrentEvent(event);
        assertDoesNotThrow(() -> event.update(from));
    }

    @Test
    public void testUpdate_parentBranch_GO_state_withBabyChild_doesNotThrow() {
        Body from = createBody();
        Body child = createBody();
        child.setAgeState(AgeState.BABY);
        from.addChildrenList(child);
        SimYukkuri.world.getCurrentMap().getBody().put(child.getUniqueID(), child);
        Food food = createFood();
        SuperEatingTimeEvent event = new SuperEatingTimeEvent(from, null, food, 10);
        event.setState(SuperEatingTimeEvent.STATE.GO);
        from.setCurrentEvent(event);
        assertDoesNotThrow(() -> event.update(from));
    }

    // --- checkEventResponse: isDontMove → false ---
    @Test
    public void testCheckEventResponse_isDontMove_returnsFalse() {
        Body from = createBody();
        Body b = createBody();
        b.setGrabbed(true); // isDontMove() = true
        SuperEatingTimeEvent event = new SuperEatingTimeEvent(from, null, null, 10);
        assertFalse(event.checkEventResponse(b));
    }

    // --- checkEventResponse: isNYD → false ---
    @Test
    public void testCheckEventResponse_isNYD_returnsFalse() {
        Body from = createBody();
        Body b = createBody();
        b.seteCoreAnkoState(src.enums.CoreAnkoState.NonYukkuriDisease);
        SuperEatingTimeEvent event = new SuperEatingTimeEvent(from, null, null, 10);
        assertFalse(event.checkEventResponse(b));
    }

    // --- checkEventResponse: not child of from → false ---
    @Test
    public void testCheckEventResponse_notChildOfFrom_returnsFalse() {
        Body from = createBody();
        Body other = createBody(); // no parent relationship
        SuperEatingTimeEvent event = new SuperEatingTimeEvent(from, null, null, 10);
        assertFalse(event.checkEventResponse(other));
    }

    // --- checkEventResponse: child of from but adult → false ---
    @Test
    public void testCheckEventResponse_isChild_adult_returnsFalse() {
        Body from = createBody();
        Body child = createBody(); // ADULT by default
        child.setParents(new int[] { from.getUniqueID(), -1 });
        SuperEatingTimeEvent event = new SuperEatingTimeEvent(from, null, null, 10);
        assertFalse(event.checkEventResponse(child));
    }

    // --- checkEventResponse: baby child of from → true ---
    @Test
    public void testCheckEventResponse_babyChild_returnsTrue() {
        Body from = createBody();
        Body child = createBody();
        child.setAgeState(AgeState.BABY);
        child.setParents(new int[] { from.getUniqueID(), -1 });
        SuperEatingTimeEvent event = new SuperEatingTimeEvent(from, null, null, 10);
        assertTrue(event.checkEventResponse(child));
    }

    // --- update: nFromWaitCount > 5000 → ABORT ---
    @Test
    public void testUpdate_nFromWaitCountOver5000_returnsAbort() {
        Body from = createBody();
        Food food = createFood();
        SuperEatingTimeEvent event = new SuperEatingTimeEvent(from, null, food, 10);
        from.setCurrentEvent(event);
        event.nFromWaitCount = 5001;
        assertEquals(src.base.EventPacket.UpdateState.ABORT, event.update(from));
    }

    // --- simpleEventAction: from.isShutmouth() → true ---
    @Test
    public void testSimpleEventAction_fromShutmouth_returnsTrue() {
        Body from = createBody();
        from.setShutmouth(true);
        SuperEatingTimeEvent event = new SuperEatingTimeEvent(from, null, null, 10);
        assertTrue(event.simpleEventAction(from));
    }

    // --- update: parent START_BEFORE state with baby child → does not throw ---
    @Test
    public void testUpdate_parentBranch_START_BEFORE_withBabyChild_doesNotThrow() {
        Body from = createBody();
        Body child = createBody();
        child.setAgeState(AgeState.BABY);
        from.addChildrenList(child);
        SimYukkuri.world.getCurrentMap().getBody().put(child.getUniqueID(), child);
        Food food = createFood();
        SuperEatingTimeEvent event = new SuperEatingTimeEvent(from, null, food, 10);
        event.setState(SuperEatingTimeEvent.STATE.START_BEFORE);
        from.setCurrentEvent(event);
        SimYukkuri.RND = new src.ConstState(1);
        try {
            assertDoesNotThrow(() -> event.update(from));
        } finally {
            SimYukkuri.RND = new java.util.Random();
        }
    }

    // --- update: parent START state with baby child → does not throw ---
    @Test
    public void testUpdate_parentBranch_START_withBabyChild_doesNotThrow() {
        Body from = createBody();
        Body child = createBody();
        child.setAgeState(AgeState.BABY);
        from.addChildrenList(child);
        SimYukkuri.world.getCurrentMap().getBody().put(child.getUniqueID(), child);
        Food food = createFood();
        SuperEatingTimeEvent event = new SuperEatingTimeEvent(from, null, food, 10);
        event.setState(SuperEatingTimeEvent.STATE.START);
        from.setCurrentEvent(event);
        from.setHungry(from.getHungryLimit()); // not very hungry
        SimYukkuri.RND = new src.ConstState(1);
        try {
            assertDoesNotThrow(() -> event.update(from));
        } finally {
            SimYukkuri.RND = new java.util.Random();
        }
    }

    @Test
    public void testUpdate_foodEmpty_returnsAbort() {
        Body from = createBody();
        Body child = createBody();
        child.setAgeState(AgeState.BABY);
        from.addChildrenList(child);
        Food food = createFood();
        food.setAmount(0); // empty food

        SuperEatingTimeEvent event = new SuperEatingTimeEvent(from, null, food, 10);
        event.setState(SuperEatingTimeEvent.STATE.START);
        from.setCurrentEvent(event);
        child.setCurrentEvent(event);

        // food.isEmpty() check in update is only in parent branch (b == from)
        assertEquals(src.base.EventPacket.UpdateState.ABORT, event.update(from));
        assertEquals(src.enums.Happiness.VERY_SAD, from.getHappiness());
    }

    // --- Helper ---

    private static Body createBody() {
        Body b = new Reimu();
        b.setAgeState(AgeState.ADULT);
        Sprite[] spr = new Sprite[3];
        for (int i = 0; i < 3; i++) {
            spr[i] = new Sprite(10, 10, Sprite.PIVOT_CENTER_BOTTOM);
        }
        b.setBodySpr(spr);
        SimYukkuri.world.getCurrentMap().getBody().put(b.getUniqueID(), b);
        return b;
    }

    private Food createFood() {
        Food food = new Food(100, 100, Food.FoodType.SWEETS1.ordinal());
        SimYukkuri.world.getCurrentMap().getFood().put(food.getObjId(), food);
        return food;
    }
}
