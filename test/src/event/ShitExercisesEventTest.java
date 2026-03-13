package src.event;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import src.base.EventPacket;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import src.SimYukkuri;
import src.base.Body;
import src.base.EventPacket.EventPriority;
import src.draw.Translate;
import src.draw.World;
import src.enums.AgeState;
import src.enums.PublicRank;
import src.system.Sprite;
import src.yukkuri.Reimu;

public class ShitExercisesEventTest {

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
        ShitExercisesEvent event = new ShitExercisesEvent();
        assertEquals(-1, event.getFrom());
        assertEquals(-1, event.getTo());
        assertEquals(EventPriority.LOW, event.getPriority());
    }

    // --- Parameterized constructor ---

    @Test
    public void testParameterizedConstructor_setsPriorityHigh() {
        Body from = createBody();
        Body to = createBody();
        ShitExercisesEvent event = new ShitExercisesEvent(from, to, null, 10);
        assertEquals(EventPriority.HIGH, event.getPriority());
        assertEquals(from.getUniqueID(), event.getFrom());
        assertEquals(to.getUniqueID(), event.getTo());
        assertEquals(10, event.getCount());
    }

    // --- simpleEventAction ---

    @Test
    public void testSimpleEventAction_returnsTrueWhenFromIsNull() {
        Body b = createBody();
        ShitExercisesEvent event = new ShitExercisesEvent();
        // from is -1 (null lookup) => returns true
        assertTrue(event.simpleEventAction(b));
    }

    // --- checkEventResponse ---

    @Test
    public void testCheckEventResponse_returnsFalseWhenFromIsNull() {
        Body b = createBody();
        ShitExercisesEvent event = new ShitExercisesEvent();
        // default constructor: from is null => returns false
        assertFalse(event.checkEventResponse(b));
    }

    @Test
    public void testCheckEventResponse_returnsTrueWhenFromEqualsB() {
        Body b = createBody();
        ShitExercisesEvent event = new ShitExercisesEvent(b, null, null, 10);
        // from == b => returns true
        assertTrue(event.checkEventResponse(b));
    }

    @Test
    public void testCheckEventResponse_returnsFalseForUnunSlave() {
        Body from = createBody();
        Body b = createBody();
        b.setPublicRank(PublicRank.UnunSlave);
        ShitExercisesEvent event = new ShitExercisesEvent(from, null, null, 10);
        // b is UnunSlave, from != b, and from is not partner => false
        assertFalse(event.checkEventResponse(b));
    }

    // --- execute ---

    @Test
    public void testExecute_returnsFalse() {
        Body b = createBody();
        ShitExercisesEvent event = new ShitExercisesEvent(b, null, null, 10);
        assertFalse(event.execute(b));
    }

    // --- getState / setState ---

    @Test
    public void testGetState_defaultIsGO() {
        ShitExercisesEvent event = new ShitExercisesEvent();
        assertEquals(ShitExercisesEvent.STATE.GO, event.getState());
    }

    @Test
    public void testSetState() {
        ShitExercisesEvent event = new ShitExercisesEvent();
        event.setState(ShitExercisesEvent.STATE.YURAYURA);
        assertEquals(ShitExercisesEvent.STATE.YURAYURA, event.getState());

        event.setState(ShitExercisesEvent.STATE.END);
        assertEquals(ShitExercisesEvent.STATE.END, event.getState());
    }

    // --- simpleEventAction (from not null) ---

    @Test
    public void testSimpleEventAction_fromNotNull_notShutmouth_returnsFalse() {
        Body from = createBody();
        ShitExercisesEvent event = new ShitExercisesEvent(from, null, null, 10);
        // from != null and not shutmouth → false
        assertFalse(event.simpleEventAction(from));
    }

    // --- checkEventResponse (partner) ---

    @Test
    public void testCheckEventResponse_partner_returnsTrue() {
        Body from = createBody();
        Body partner = createBody();
        from.setPartner(partner.getUniqueID());
        partner.setPartner(from.getUniqueID());
        ShitExercisesEvent event = new ShitExercisesEvent(from, null, null, 10);
        assertTrue(event.checkEventResponse(partner));
    }

    @Test
    public void testCheckEventResponse_childBaby_canEventResponse_doesNotThrow() {
        Body from = createBody();
        Body baby = createBody();
        baby.setAgeState(AgeState.BABY);
        int[] parents = new int[] { from.getUniqueID(), -1 };
        baby.setParents(parents);
        ShitExercisesEvent event = new ShitExercisesEvent(from, null, null, 10);
        assertDoesNotThrow(() -> event.checkEventResponse(baby));
    }

    // --- start ---

    @Test
    public void testStart_setsCurrentEvent() {
        Body b = createBody();
        ShitExercisesEvent event = new ShitExercisesEvent(b, null, null, 10);
        event.start(b);
        assertEquals(event, b.getCurrentEvent());
    }

    // --- update ---

    @Test
    public void testUpdate_fromNull_returnsAbort() {
        Body b = createBody();
        ShitExercisesEvent event = new ShitExercisesEvent();
        // from is null → ABORT
        assertEquals(EventPacket.UpdateState.ABORT, event.update(b));
    }

    @Test
    public void testUpdate_bodyNYD_returnsAbort() {
        Body from = createBody();
        Body b = createBody();
        b.seteCoreAnkoState(src.enums.CoreAnkoState.NonYukkuriDisease);
        ShitExercisesEvent event = new ShitExercisesEvent(from, null, null, 10);
        from.setCurrentEvent(event);
        assertEquals(EventPacket.UpdateState.ABORT, event.update(b));
    }

    @Test
    public void testUpdate_fromRemoved_returnsAbort() {
        Body from = createBody();
        Body b = createBody();
        from.setCurrentEvent(new ShitExercisesEvent());
        from.setRemoved(true);
        ShitExercisesEvent event = new ShitExercisesEvent(from, null, null, 10);
        assertEquals(EventPacket.UpdateState.ABORT, event.update(b));
    }

    @Test
    public void testUpdate_fromCurrentEventNull_returnsAbort() {
        Body from = createBody();
        Body b = createBody();
        // from.getCurrentEvent() == null → ABORT
        ShitExercisesEvent event = new ShitExercisesEvent(from, null, null, 10);
        assertEquals(EventPacket.UpdateState.ABORT, event.update(b));
    }

    @Test
    public void testUpdate_bEqualsFrom_noChildren_returnsAbort() {
        Body from = createBody();
        ShitExercisesEvent event = new ShitExercisesEvent(from, null, null, 10);
        from.setCurrentEvent(event);
        // b == from, nFromWaitCount < 2000, children empty → ABORT at childrenList
        assertEquals(EventPacket.UpdateState.ABORT, event.update(from));
    }

    @Test
    public void testUpdate_bIsPartnerOfFrom_stateGO_doesNotThrow() {
        Body from = createBody();
        Body partner = createBody();
        from.setPartner(partner.getUniqueID());
        partner.setPartner(from.getUniqueID());
        ShitExercisesEvent event = new ShitExercisesEvent(from, null, null, 10);
        from.setCurrentEvent(event);
        event.setState(ShitExercisesEvent.STATE.WAIT); // avoid moveTo in GO state
        // partner of from → skips, returns null
        assertDoesNotThrow(() -> event.update(partner));
    }

    // --- update: tick not multiple of 20 ---
    @Test
    public void testUpdate_tickNotMultipleOf20_returnsNull() {
        Body from = createBody();
        Body b = createBody();
        ShitExercisesEvent event = new ShitExercisesEvent(from, null, null, 10);
        from.setCurrentEvent(event);
        event.tick = 1; // 1 % 20 != 0 → return null
        assertNull(event.update(b));
    }

    // --- update: nFromWaitCount > 2000 → ABORT ---
    @Test
    public void testUpdate_nFromWaitCountOver2000_returnsAbort() {
        Body from = createBody();
        ShitExercisesEvent event = new ShitExercisesEvent(from, null, null, 10);
        from.setCurrentEvent(event);
        event.nFromWaitCount = 2001;
        assertEquals(EventPacket.UpdateState.ABORT, event.update(from));
    }

    // --- update: b is partner of from, state=GO, has Translate table ---
    @Test
    public void testUpdate_bIsPartnerOfFrom_stateActualGO_doesNotThrow() {
        Body from = createBody();
        Body partner = createBody();
        from.setPartner(partner.getUniqueID());
        partner.setPartner(from.getUniqueID());
        ShitExercisesEvent event = new ShitExercisesEvent(from, null, null, 10);
        from.setCurrentEvent(event);
        // state=GO → calcCollisionX called (needs rateX initialized via setCanvasSize)
        assertDoesNotThrow(() -> event.update(partner));
    }

    // --- update: child path (b != from, not partner) ---

    @Test
    public void testUpdate_childBody_stateGO_doesNotThrow() {
        Body from = createBody();
        Body child = createBody();
        ShitExercisesEvent event = new ShitExercisesEvent(from, null, null, 10);
        from.setCurrentEvent(event);
        child.setCurrentEvent(event);
        assertDoesNotThrow(() -> event.update(child));
    }

    @Test
    public void testUpdate_childBody_stateWAIT_doesNotThrow() {
        Body from = createBody();
        Body child = createBody();
        ShitExercisesEvent event = new ShitExercisesEvent(from, null, null, 10);
        event.setState(ShitExercisesEvent.STATE.WAIT);
        from.setCurrentEvent(event);
        child.setCurrentEvent(event);
        assertDoesNotThrow(() -> event.update(child));
    }

    @Test
    public void testUpdate_childBody_stateSTART_bActionFlagTrue_doesNotThrow() {
        Body from = createBody();
        Body child = createBody();
        ShitExercisesEvent event = new ShitExercisesEvent(from, null, null, 10);
        event.bActionFlag = true;
        event.setState(ShitExercisesEvent.STATE.START);
        from.setCurrentEvent(event);
        child.setCurrentEvent(event);
        assertDoesNotThrow(() -> event.update(child));
    }

    @Test
    public void testUpdate_childBody_stateYURAYURA_bActionFlagTrue_doesNotThrow() {
        Body from = createBody();
        Body child = createBody();
        ShitExercisesEvent event = new ShitExercisesEvent(from, null, null, 10);
        event.bActionFlag = true;
        event.setState(ShitExercisesEvent.STATE.YURAYURA);
        from.setCurrentEvent(event);
        child.setCurrentEvent(event);
        assertDoesNotThrow(() -> event.update(child));
    }

    @Test
    public void testUpdate_childBody_stateNOBINOBI_bActionFlagTrue_doesNotThrow() {
        Body from = createBody();
        Body child = createBody();
        ShitExercisesEvent event = new ShitExercisesEvent(from, null, null, 10);
        event.bActionFlag = true;
        event.setState(ShitExercisesEvent.STATE.NOBINOBI);
        from.setCurrentEvent(event);
        child.setCurrentEvent(event);
        assertDoesNotThrow(() -> event.update(child));
    }

    @Test
    public void testUpdate_childBody_statePOKAPOKA_bActionFlagTrue_doesNotThrow() {
        Body from = createBody();
        Body child = createBody();
        ShitExercisesEvent event = new ShitExercisesEvent(from, null, null, 10);
        event.bActionFlag = true;
        event.setState(ShitExercisesEvent.STATE.POKAPOKA);
        from.setCurrentEvent(event);
        child.setCurrentEvent(event);
        assertDoesNotThrow(() -> event.update(child));
    }

    @Test
    public void testUpdate_childBody_stateUNUN_bActionFlagTrue_doesNotThrow() {
        Body from = createBody();
        Body child = createBody();
        ShitExercisesEvent event = new ShitExercisesEvent(from, null, null, 10);
        event.bActionFlag = true;
        event.setState(ShitExercisesEvent.STATE.UNUN);
        from.setCurrentEvent(event);
        child.setCurrentEvent(event);
        assertDoesNotThrow(() -> event.update(child));
    }

    @Test
    public void testUpdate_childBody_stateEND_doesNotThrow() {
        Body from = createBody();
        Body child = createBody();
        ShitExercisesEvent event = new ShitExercisesEvent(from, null, null, 10);
        event.setState(ShitExercisesEvent.STATE.END);
        from.setCurrentEvent(event);
        child.setCurrentEvent(event);
        assertDoesNotThrow(() -> event.update(child));
    }

    // --- checkEventResponse: !canEventResponse → false ---
    @Test
    public void testCheckEventResponse_canEventResponseFalse_returnsFalse() {
        Body from = createBody();
        Body b = createBody();
        b.setDead(true); // canEventResponse() = false
        ShitExercisesEvent event = new ShitExercisesEvent(from, null, null, 10);
        assertFalse(event.checkEventResponse(b));
    }

    // --- checkEventResponse: not child of from → false ---
    @Test
    public void testCheckEventResponse_notChildOfFrom_returnsFalse() {
        Body from = createBody();
        Body other = createBody(); // fresh body, no parent relationship to from
        ShitExercisesEvent event = new ShitExercisesEvent(from, null, null, 10);
        assertFalse(event.checkEventResponse(other));
    }

    // --- checkEventResponse: child of from but not baby (CHILD age) → false ---
    @Test
    public void testCheckEventResponse_isChild_notBaby_returnsFalse() {
        Body from = createBody();
        Body child = createBody();
        child.setAgeState(AgeState.CHILD); // CHILD, not BABY → isBaby() = false
        child.setParents(new int[] { from.getUniqueID(), -1 });
        ShitExercisesEvent event = new ShitExercisesEvent(from, null, null, 10);
        assertFalse(event.checkEventResponse(child));
    }

    // --- checkEventResponse: baby child of from → true ---
    @Test
    public void testCheckEventResponse_babyChildOfFrom_returnsTrue() {
        Body from = createBody();
        Body baby = createBody();
        baby.setAgeState(AgeState.BABY);
        baby.setParents(new int[] { from.getUniqueID(), -1 });
        ShitExercisesEvent event = new ShitExercisesEvent(from, null, null, 10);
        assertTrue(event.checkEventResponse(baby));
    }

    // --- update: child GO, isDontMove → ABORT ---
    @Test
    public void testUpdate_childBody_GO_isDontMove_returnsAbort() {
        Body from = createBody();
        Body child = createBody();
        child.setGrabbed(true); // isDontMove() = true
        ShitExercisesEvent event = new ShitExercisesEvent(from, null, null, 10);
        from.setCurrentEvent(event);
        child.setCurrentEvent(event);
        assertEquals(EventPacket.UpdateState.ABORT, event.update(child));
    }

    // --- update: b==from with baby child, state=GO → does not throw ---
    @Test
    public void testUpdate_bEqualsFrom_withBabyChild_stateGO_doesNotThrow() {
        Body from = createBody();
        Body baby = createBody();
        baby.setParents(new int[] { from.getUniqueID(), -1 });
        baby.setAgeState(AgeState.BABY);
        ShitExercisesEvent event = new ShitExercisesEvent(from, null, null, 10);
        from.setCurrentEvent(event);
        baby.setCurrentEvent(event);
        SimYukkuri.RND = new src.ConstState(1);
        try {
            assertDoesNotThrow(() -> event.update(from));
        } finally {
            SimYukkuri.RND = new java.util.Random();
        }
    }

    // --- update: b==from with baby child, state=WAIT → does not throw ---
    @Test
    public void testUpdate_bEqualsFrom_withBabyChild_stateWAIT_doesNotThrow() {
        Body from = createBody();
        Body baby = createBody();
        baby.setParents(new int[] { from.getUniqueID(), -1 });
        baby.setAgeState(AgeState.BABY);
        ShitExercisesEvent event = new ShitExercisesEvent(from, null, null, 10);
        event.setState(ShitExercisesEvent.STATE.WAIT);
        from.setCurrentEvent(event);
        baby.setCurrentEvent(event);
        assertDoesNotThrow(() -> event.update(from));
    }

    // --- update: from.getZ() >= 2 (lifted), b != from → sets message, returns null
    // ---
    @Test
    public void testUpdate_fromHighZ_childNotFrom_returnsNull() {
        Body from = createBody();
        Body child = createBody();
        from.setZ(5); // from.getZ() >= 2, !canflyCheck() = true for Reimu
        ShitExercisesEvent event = new ShitExercisesEvent(from, null, null, 10);
        from.setCurrentEvent(event);
        child.setCurrentEvent(event);
        SimYukkuri.RND = new src.ConstState(1); // nextInt(50)=1 → no ABORT
        try {
            assertNull(event.update(child));
        } finally {
            SimYukkuri.RND = new java.util.Random();
        }
    }

    // --- update: child UNUN, bActionFlag=false, bUnunActionFlag=true → state=END
    // ---
    @Test
    public void testUpdate_childBody_UNUN_bActionFlagFalse_transitionToEnd() {
        Body from = createBody();
        Body child = createBody();
        ShitExercisesEvent event = new ShitExercisesEvent(from, null, null, 10);
        event.bActionFlag = false;
        event.bUnunActionFlag = true;
        event.setState(ShitExercisesEvent.STATE.UNUN);
        from.setCurrentEvent(event);
        child.setCurrentEvent(event);
        assertDoesNotThrow(() -> event.update(child));
        assertEquals(ShitExercisesEvent.STATE.END, event.getState());
    }

    // --- update: b==from with baby, STATE.START, bActionFlag=false → does not
    // throw ---
    @Test
    public void testUpdate_bEqualsFrom_withBaby_stateSTART_bActionFlagFalse_doesNotThrow() {
        Body from = createBody();
        Body baby = createBody();
        baby.setParents(new int[] { from.getUniqueID(), -1 });
        baby.setAgeState(AgeState.BABY);
        ShitExercisesEvent event = new ShitExercisesEvent(from, null, null, 10);
        event.setState(ShitExercisesEvent.STATE.START);
        event.bActionFlag = false;
        from.setCurrentEvent(event);
        baby.setCurrentEvent(event);
        assertDoesNotThrow(() -> event.update(from));
    }

    // --- toString ---

    @Test
    public void testToString_doesNotThrow() {
        ShitExercisesEvent event = new ShitExercisesEvent();
        assertDoesNotThrow(() -> event.toString());
    }

    // --- STATE enum ---

    @Test
    public void testStateEnum_allValues() {
        ShitExercisesEvent.STATE[] states = ShitExercisesEvent.STATE.values();
        assertEquals(8, states.length);
        for (ShitExercisesEvent.STATE s : states) {
            assertNotNull(s.name());
        }
    }

    // --- update: hungry relief ---
    @Test
    public void testUpdate_hungryRelief() {
        Body from = createBody();
        Body b = createBody();
        b.setHungry(1); // very low hungry
        ShitExercisesEvent event = new ShitExercisesEvent(from, null, null, 10);
        from.setCurrentEvent(event);
        b.setCurrentEvent(event);
        event.update(b);
        // (100 * 1 / limit) < 2 → limit * 2 / 100
        assertTrue(b.getHungry() >= b.getHungryLimit() * 2 / 100);
    }

    // --- update: nearToBirth ---
    @Test
    public void testUpdate_nearToBirth_returnsAbort() {
        Body from = createBody();
        Body b = createBody();
        b.setHasBaby(true);
        b.setPregnantPeriod(2400); // Trigger nearToBirth()

        ShitExercisesEvent event = new ShitExercisesEvent(from, null, null, 10);
        from.setCurrentEvent(event);
        b.setCurrentEvent(event);
        assertEquals(EventPacket.UpdateState.ABORT, event.update(b));
    }

    // --- update: parental pickup nextInt(50)=0 ---
    @Test
    public void testUpdate_fromHighZ_nextInt0_returnsAbort() {
        Body from = createBody();
        Body child = createBody();
        from.setZ(5); // lifted
        ShitExercisesEvent event = new ShitExercisesEvent(from, null, null, 10);
        from.setCurrentEvent(event);
        child.setCurrentEvent(event);
        SimYukkuri.RND = new src.ConstState(0);
        try {
            assertEquals(EventPacket.UpdateState.ABORT, event.update(child));
        } finally {
            SimYukkuri.RND = new java.util.Random();
        }
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
}
