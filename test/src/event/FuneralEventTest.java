package src.event;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import src.SimYukkuri;
import src.base.Body;
import src.base.EventPacket.EventPriority;
import src.draw.Translate;
import src.draw.World;
import src.enums.AgeState;
import src.enums.Attitude;

class FuneralEventTest {

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
        FuneralEvent event = new FuneralEvent();
        assertNotNull(event);
        assertEquals(-1, event.getFrom());
        assertEquals(-1, event.getTo());
    }

    @Test
    void testParameterizedConstructor_setsPriorityHigh() {
        Body from = createBody();
        Body to = createBody();
        FuneralEvent event = new FuneralEvent(from, to, null, 10);
        assertNotNull(event);
        assertEquals(from.getUniqueID(), event.getFrom());
        assertEquals(to.getUniqueID(), event.getTo());
        assertEquals(EventPriority.HIGH, event.getPriority());
    }

    @Test
    void testSimpleEventAction_returnsTrueWhenFromIsNull() {
        Body b = createBody();
        FuneralEvent event = new FuneralEvent();
        event.setFrom(-1);
        // from is null -> returns true
        assertTrue(event.simpleEventAction(b));
    }

    @Test
    void testSimpleEventAction_returnsTrueWhenFromEqualsB() {
        Body from = createBody();
        FuneralEvent event = new FuneralEvent(from, null, null, 10);
        // from == b -> returns true
        assertTrue(event.simpleEventAction(from));
    }

    @Test
    void testSimpleEventAction_returnsFalseWhenFromIsNotBAndNotShutmouth() {
        Body from = createBody();
        Body other = createBody();
        FuneralEvent event = new FuneralEvent(from, null, null, 10);
        // from != other and from is not shutmouth -> returns false
        assertFalse(event.simpleEventAction(other));
    }

    @Test
    void testCheckEventResponse_returnsTrueWhenFromUniqueIdEqualsB() {
        Body from = createBody();
        FuneralEvent event = new FuneralEvent(from, null, null, 10);
        // from.getUniqueID() == b.getUniqueID() -> returns true
        assertTrue(event.checkEventResponse(from));
    }

    @Test
    void testCheckEventResponse_returnsFalseForUnunSlave() {
        Body from = createBody();
        Body responder = createBody();
        responder.setPublicRank(src.enums.PublicRank.UnunSlave);
        FuneralEvent event = new FuneralEvent(from, null, null, 10);
        assertFalse(event.checkEventResponse(responder));
    }

    @Test
    void testExecute_returnsFalse() {
        Body b = createBody();
        FuneralEvent event = new FuneralEvent(b, null, null, 10);
        assertFalse(event.execute(b));
    }

    @Test
    void testEnd_setsCurrentEventToNull() {
        Body b = createBody();
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
        Body b = createBody();
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
        Body from = createBody();
        Body other = createBody();
        // other has no parents → false
        FuneralEvent event = new FuneralEvent(from, null, null, 10);
        assertFalse(event.checkEventResponse(other));
    }

    // --- update ---
    @Test
    void testUpdate_fromNull_returnsAbort() {
        Body b = createBody();
        FuneralEvent event = new FuneralEvent();
        assertEquals(src.base.EventPacket.UpdateState.ABORT, event.update(b));
    }

    @Test
    void testUpdate_bodyNYD_returnsAbort() {
        Body from = createBody();
        Body b = createBody();
        b.seteCoreAnkoState(src.enums.CoreAnkoState.NonYukkuriDisease);
        FuneralEvent event = new FuneralEvent(from, null, null, 10);
        assertEquals(src.base.EventPacket.UpdateState.ABORT, event.update(b));
    }

    @Test
    void testUpdate_fromRemoved_returnsAbort() {
        Body from = createBody();
        Body b = createBody();
        from.setRemoved(true);
        FuneralEvent event = new FuneralEvent(from, null, null, 10);
        assertEquals(src.base.EventPacket.UpdateState.ABORT, event.update(b));
    }

    @Test
    void testUpdate_bEqualsFrom_noChildren_returnsAbort() {
        Body from = createBody();
        FuneralEvent event = new FuneralEvent(from, null, null, 10);
        from.setCurrentEvent(event);
        // b == from, no children → ABORT
        assertEquals(src.base.EventPacket.UpdateState.ABORT, event.update(from));
    }

    @Test
    void testUpdate_partnerOfFrom_stateNotGO_returnsNull() {
        Body from = createBody();
        Body partner = createBody();
        from.setPartner(partner.getUniqueID());
        partner.setPartner(from.getUniqueID());
        FuneralEvent event = new FuneralEvent(from, null, null, 10);
        from.setCurrentEvent(event);
        event.setState(FuneralEvent.STATE.FIND); // not GO → b.stay()
        assertDoesNotThrow(() -> event.update(partner));
    }

    @Test
    void testUpdate_tickNotMultipleOf30_returnsNull() {
        Body from = createBody();
        Body b = createBody();
        FuneralEvent event = new FuneralEvent(from, null, null, 10);
        from.setCurrentEvent(event);
        event.tick = 1; // 1 % 30 != 0 → return null immediately
        assertNull(event.update(b));
    }

    @Test
    void testUpdate_bIsPartnerOfFrom_stateGO_doesNotThrow() {
        Body from = createBody();
        Body partner = createBody();
        from.setPartner(partner.getUniqueID());
        partner.setPartner(from.getUniqueID());
        FuneralEvent event = new FuneralEvent(from, null, null, 10);
        from.setCurrentEvent(event);
        // state=GO → calcCollisionX (needs rateX initialized)
        assertDoesNotThrow(() -> event.update(partner));
    }

    @Test
    void testUpdate_nFromWaitCountOver2000_returnsAbort() {
        Body from = createBody();
        FuneralEvent event = new FuneralEvent(from, null, null, 10);
        from.setCurrentEvent(event);
        event.nFromWaitCount = 2001;
        assertEquals(src.base.EventPacket.UpdateState.ABORT, event.update(from));
    }

    // --- update: child path (b != from && !partner) ---

    @Test
    void testUpdate_childBody_stateGO_doesNotThrow() {
        Body from = createBody();
        Body child = createBody();
        FuneralEvent event = new FuneralEvent(from, null, null, 10);
        from.setCurrentEvent(event);
        child.setCurrentEvent(event);
        // child is not from and not partner → child path, state=GO
        assertDoesNotThrow(() -> event.update(child));
    }

    @Test
    void testUpdate_childBody_stateFIND_doesNotThrow() {
        Body from = createBody();
        Body child = createBody();
        FuneralEvent event = new FuneralEvent(from, null, null, 10);
        from.setCurrentEvent(event);
        child.setCurrentEvent(event);
        event.setState(FuneralEvent.STATE.FIND);
        assertDoesNotThrow(() -> event.update(child));
    }

    @Test
    void testUpdate_childBody_stateSTART_bActionFlagTrue_doesNotThrow() {
        Body from = createBody();
        Body child = createBody();
        FuneralEvent event = new FuneralEvent(from, null, null, 10);
        event.bActionFlag = true;
        from.setCurrentEvent(event);
        child.setCurrentEvent(event);
        event.setState(FuneralEvent.STATE.START);
        assertDoesNotThrow(() -> event.update(child));
    }

    @Test
    void testUpdate_childBody_stateINTRODUCE_bActionFlagTrue_doesNotThrow() {
        Body from = createBody();
        Body child = createBody();
        FuneralEvent event = new FuneralEvent(from, null, null, 10);
        event.bActionFlag = true;
        from.setCurrentEvent(event);
        child.setCurrentEvent(event);
        event.setState(FuneralEvent.STATE.INTRODUCE);
        assertDoesNotThrow(() -> event.update(child));
    }

    @Test
    void testUpdate_childBody_stateSING_bActionFlagFalse_doesNotThrow() {
        Body from = createBody();
        Body child = createBody();
        FuneralEvent event = new FuneralEvent(from, null, null, 10);
        event.bActionFlag = false; // !bActionFlag in child SING case
        from.setCurrentEvent(event);
        child.setCurrentEvent(event);
        event.setState(FuneralEvent.STATE.SING);
        assertDoesNotThrow(() -> event.update(child));
    }

    @Test
    void testUpdate_childBody_stateTALK_bActionFlagTrue_doesNotThrow() {
        Body from = createBody();
        Body child = createBody();
        FuneralEvent event = new FuneralEvent(from, null, null, 10);
        event.bActionFlag = true;
        from.setCurrentEvent(event);
        child.setCurrentEvent(event);
        event.setState(FuneralEvent.STATE.TALK);
        assertDoesNotThrow(() -> event.update(child));
    }

    @Test
    void testUpdate_childBody_stateGOODBYE_bActionFlagTrue_doesNotThrow() {
        Body from = createBody();
        Body child = createBody();
        FuneralEvent event = new FuneralEvent(from, null, null, 10);
        event.bActionFlag = true;
        from.setCurrentEvent(event);
        child.setCurrentEvent(event);
        event.setState(FuneralEvent.STATE.GOODBYE);
        assertDoesNotThrow(() -> event.update(child));
    }

    @Test
    void testUpdate_childBody_stateEND_doesNotThrow() {
        Body from = createBody();
        Body child = createBody();
        FuneralEvent event = new FuneralEvent(from, null, null, 10);
        from.setCurrentEvent(event);
        child.setCurrentEvent(event);
        event.setState(FuneralEvent.STATE.END);
        assertDoesNotThrow(() -> event.update(child));
    }

    // --- update: partner path state != GO → stays ---
    @Test
    void testUpdate_partnerOfFrom_stateNotGO_stays() {
        Body from = createBody();
        Body partner = createBody();
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
        Body from = createBody();
        Body partner = createBody();
        Body grandparent = createBody(); // give partner a parent so line 85 passes
        from.setPartner(partner.getUniqueID());
        partner.setPartner(from.getUniqueID());
        partner.setParents(new int[]{grandparent.getUniqueID(), -1});
        FuneralEvent event = new FuneralEvent(from, null, null, 10);
        assertTrue(event.checkEventResponse(partner));
    }

    // --- checkEventResponse: non-child of from, has parents → false ---
    @Test
    void testCheckEventResponse_notChildOfFrom_hasParents_returnsFalse() {
        Body from = createBody();
        Body grandparent = createBody();
        Body other = createBody();
        other.setParents(new int[]{grandparent.getUniqueID(), -1}); // has parents but NOT from's child
        FuneralEvent event = new FuneralEvent(from, null, null, 10);
        assertFalse(event.checkEventResponse(other));
    }

    // --- checkEventResponse: adult child of from → false ---
    @Test
    void testCheckEventResponse_isChildOfFrom_adult_returnsFalse() {
        Body from = createBody();
        Body child = createBody(); // ADULT by default
        child.setParents(new int[]{from.getUniqueID(), -1});
        FuneralEvent event = new FuneralEvent(from, null, null, 10);
        assertFalse(event.checkEventResponse(child));
    }

    // --- checkEventResponse: baby child of from → true ---
    @Test
    void testCheckEventResponse_isChildOfFrom_baby_returnsTrue() {
        Body from = createBody();
        Body child = createBody();
        child.setParents(new int[]{from.getUniqueID(), -1});
        child.setAgeState(AgeState.BABY);
        FuneralEvent event = new FuneralEvent(from, null, null, 10);
        assertTrue(event.checkEventResponse(child));
    }

    // --- update: child GO with isDontMove → ABORT ---
    @Test
    void testUpdate_childBody_GO_isDontMove_returnsAbort() {
        Body from = createBody();
        Body child = createBody();
        child.setGrabbed(true); // isDontMove() = true
        FuneralEvent event = new FuneralEvent(from, null, null, 10);
        from.setCurrentEvent(event);
        child.setCurrentEvent(event);
        assertEquals(src.base.EventPacket.UpdateState.ABORT, event.update(child));
    }

    // --- update: b==from, with baby child present, state=GO → does not throw ---
    @Test
    void testUpdate_bEqualsFrom_withBabyChild_stateGO_doesNotThrow() {
        Body from = createBody();
        Body child = createBody();
        child.setParents(new int[]{from.getUniqueID(), -1});
        child.setAgeState(AgeState.BABY);
        child.setBodySpr(from.getBodySpr()); // ensure sprites set
        FuneralEvent event = new FuneralEvent(from, null, null, 10);
        from.setCurrentEvent(event);
        child.setCurrentEvent(event);
        SimYukkuri.RND = new src.ConstState(1);
        try {
            assertDoesNotThrow(() -> event.update(from));
        } finally {
            SimYukkuri.RND = new java.util.Random();
        }
    }

    // --- update: from.getZ() >= 5, b != from → sets message, returns null ---
    @Test
    void testUpdate_fromHighZ_bNotFrom_returnsNull() {
        Body from = createBody();
        Body child = createBody();
        from.setZ(10); // from.getZ() >= 5, !canflyCheck() true for Reimu
        FuneralEvent event = new FuneralEvent(from, null, null, 10);
        from.setCurrentEvent(event);
        child.setCurrentEvent(event);
        SimYukkuri.RND = new src.ConstState(1); // nextInt(50)=1 → no ABORT
        try {
            assertNull(event.update(child));
        } finally {
            SimYukkuri.RND = new java.util.Random();
        }
    }

    // --- update: from.getZ() >= 5, b == from (with baby child) → does not throw ---
    @Test
    void testUpdate_fromHighZ_bEqualsFrom_withChild_doesNotThrow() {
        Body from = createBody();
        Body child = createBody();
        child.setParents(new int[]{from.getUniqueID(), -1});
        child.setAgeState(AgeState.BABY);
        from.setZ(10); // elevated
        FuneralEvent event = new FuneralEvent(from, null, null, 10);
        from.setCurrentEvent(event);
        child.setCurrentEvent(event);
        SimYukkuri.RND = new src.ConstState(1);
        try {
            assertDoesNotThrow(() -> event.update(from));
        } finally {
            SimYukkuri.RND = new java.util.Random();
        }
    }

    // --- update: child FIND, to not null → sets message, stays ---
    @Test
    void testUpdate_childBody_FIND_withTo_doesNotThrow() {
        Body from = createBody();
        Body to = createBody(); // the deceased
        Body child = createBody();
        FuneralEvent event = new FuneralEvent(from, to, null, 10);
        from.setCurrentEvent(event);
        child.setCurrentEvent(event);
        event.setState(FuneralEvent.STATE.FIND);
        assertDoesNotThrow(() -> event.update(child));
    }

    // --- update: child GOODBYE, rude body → furifuri path ---
    @Test
    void testUpdate_childBody_GOODBYE_rude_doesNotThrow() {
        Body from = createBody();
        Body child = createBody();
        child.setAttitude(Attitude.SUPER_SHITHEAD); // isRude() = true
        FuneralEvent event = new FuneralEvent(from, null, null, 10);
        event.bActionFlag = true;
        from.setCurrentEvent(event);
        child.setCurrentEvent(event);
        event.setState(FuneralEvent.STATE.GOODBYE);
        SimYukkuri.RND = new src.ConstState(1);
        try {
            assertDoesNotThrow(() -> event.update(child));
        } finally {
            SimYukkuri.RND = new java.util.Random();
        }
    }

    // --- update: child END, rude body → sets message ---
    @Test
    void testUpdate_childBody_END_rude_doesNotThrow() {
        Body from = createBody();
        Body child = createBody();
        child.setAttitude(Attitude.SUPER_SHITHEAD); // isRude() = true
        FuneralEvent event = new FuneralEvent(from, null, null, 10);
        from.setCurrentEvent(event);
        child.setCurrentEvent(event);
        event.setState(FuneralEvent.STATE.END);
        assertDoesNotThrow(() -> event.update(child));
    }
}
