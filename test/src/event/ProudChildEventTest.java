package src.event;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import src.SimYukkuri;
import src.base.Body;
import src.base.EventPacket;
import src.base.EventPacket.EventPriority;
import src.draw.Translate;
import src.draw.World;
import src.enums.AgeState;
import src.enums.Happiness;
import src.enums.PanicType;
import src.enums.Parent;
import src.enums.PublicRank;
import src.system.Sprite;
import src.yukkuri.Reimu;

public class ProudChildEventTest {

    @BeforeEach
    public void setUp() {
        SimYukkuri.world = new World();
        Translate.setMapSize(1000, 1000, 500);
        Translate.setCanvasSize(800, 600, 100, 100, new float[]{1.0f});
        Translate.createTransTable(false);
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
        Body from = createBody();
        Body to = createBody();
        ProudChildEvent event = new ProudChildEvent(from, to, null, 10);
        assertEquals(EventPriority.MIDDLE, event.getPriority());
        assertEquals(from.getUniqueID(), event.getFrom());
        assertEquals(to.getUniqueID(), event.getTo());
        assertEquals(10, event.getCount());
    }

    // --- simpleEventAction ---

    @Test
    public void testSimpleEventAction_returnsTrueWhenFromIsNull() {
        Body b = createBody();
        ProudChildEvent event = new ProudChildEvent();
        // from is -1 (null lookup), so YukkuriUtil.getBodyInstance returns null
        assertTrue(event.simpleEventAction(b));
    }

    @Test
    public void testSimpleEventAction_returnsTrueWhenFromEqualsB() {
        Body b = createBody();
        ProudChildEvent event = new ProudChildEvent(b, null, null, 10);
        // from == b, returns true
        assertTrue(event.simpleEventAction(b));
    }

    @Test
    public void testSimpleEventAction_returnsFalseWhenFromNotBAndNotShutmouth() {
        Body from = createBody();
        Body b = createBody();
        ProudChildEvent event = new ProudChildEvent(from, null, null, 10);
        // from != b and from is not shutmouth => returns false
        assertFalse(event.simpleEventAction(b));
    }

    // --- checkEventResponse ---

    @Test
    public void testCheckEventResponse_returnsFalseForUnunSlave() {
        Body from = createBody();
        Body b = createBody();
        b.setPublicRank(PublicRank.UnunSlave);
        ProudChildEvent event = new ProudChildEvent(from, null, null, 10);
        assertFalse(event.checkEventResponse(b));
    }

    @Test
    public void testCheckEventResponse_returnsFalseWhenFromIsNull() {
        Body b = createBody();
        ProudChildEvent event = new ProudChildEvent();
        // from is null (default constructor), returns false
        assertFalse(event.checkEventResponse(b));
    }

    // --- execute ---

    @Test
    public void testExecute_returnsFalse() {
        Body b = createBody();
        ProudChildEvent event = new ProudChildEvent(b, null, null, 10);
        assertFalse(event.execute(b));
    }

    // --- end ---

    @Test
    public void testEnd_setsCurrentEventToNull() {
        Body b = createBody();
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
        Body b = createBody();
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
        Body b = createBody();
        ProudChildEvent event = new ProudChildEvent();
        assertEquals(EventPacket.UpdateState.ABORT, event.update(b));
    }

    @Test
    public void testUpdate_bodyNYD_returnsAbort() {
        Body from = createBody();
        Body b = createBody();
        b.seteCoreAnkoState(src.enums.CoreAnkoState.NonYukkuriDisease);
        ProudChildEvent event = new ProudChildEvent(from, null, null, 10);
        from.setCurrentEvent(event);
        assertEquals(EventPacket.UpdateState.ABORT, event.update(b));
    }

    @Test
    public void testUpdate_fromRemoved_returnsAbort() {
        Body from = createBody();
        Body b = createBody();
        from.setRemoved(true);
        ProudChildEvent event = new ProudChildEvent(from, null, null, 10);
        from.setCurrentEvent(event);
        assertEquals(EventPacket.UpdateState.ABORT, event.update(b));
    }

    @Test
    public void testUpdate_fromCurrentEventNull_returnsAbort() {
        Body from = createBody();
        Body b = createBody();
        ProudChildEvent event = new ProudChildEvent(from, null, null, 10);
        // from.getCurrentEvent() == null → ABORT
        assertEquals(EventPacket.UpdateState.ABORT, event.update(b));
    }

    @Test
    public void testUpdate_bEqualsFrom_noChildren_returnsAbort() {
        Body from = createBody();
        ProudChildEvent event = new ProudChildEvent(from, null, null, 10);
        from.setCurrentEvent(event);
        // b == from, no children → ABORT
        assertEquals(EventPacket.UpdateState.ABORT, event.update(from));
    }

    // --- update: child path (b != from && !partner) ---

    @Test
    public void testUpdate_childBody_stateGO_doesNotThrow() {
        Body from = createBody();
        Body child = createBody();
        ProudChildEvent event = new ProudChildEvent(from, null, null, 10);
        from.setCurrentEvent(event);
        child.setCurrentEvent(event);
        assertDoesNotThrow(() -> event.update(child));
    }

    @Test
    public void testUpdate_childBody_stateWAIT_doesNotThrow() {
        Body from = createBody();
        Body child = createBody();
        ProudChildEvent event = new ProudChildEvent(from, null, null, 10);
        event.setState(ProudChildEvent.STATE.WAIT);
        from.setCurrentEvent(event);
        child.setCurrentEvent(event);
        assertDoesNotThrow(() -> event.update(child));
    }

    @Test
    public void testUpdate_childBody_stateSTART_bActionFlagTrue_doesNotThrow() {
        Body from = createBody();
        Body child = createBody();
        ProudChildEvent event = new ProudChildEvent(from, null, null, 10);
        event.bActionFlag = true;
        event.setState(ProudChildEvent.STATE.START);
        from.setCurrentEvent(event);
        child.setCurrentEvent(event);
        assertDoesNotThrow(() -> event.update(child));
    }

    @Test
    public void testUpdate_childBody_stateSING_bActionFlagTrue_doesNotThrow() {
        Body from = createBody();
        Body child = createBody();
        ProudChildEvent event = new ProudChildEvent(from, null, null, 10);
        event.bActionFlag = true;
        event.setState(ProudChildEvent.STATE.SING);
        from.setCurrentEvent(event);
        child.setCurrentEvent(event);
        assertDoesNotThrow(() -> event.update(child));
    }

    @Test
    public void testUpdate_childBody_statePROUD_bActionFlagTrue_doesNotThrow() {
        Body from = createBody();
        Body child = createBody();
        ProudChildEvent event = new ProudChildEvent(from, null, null, 10);
        event.bActionFlag = true;
        event.setState(ProudChildEvent.STATE.PROUD);
        from.setCurrentEvent(event);
        child.setCurrentEvent(event);
        assertDoesNotThrow(() -> event.update(child));
    }

    @Test
    public void testUpdate_childBody_stateEND_doesNotThrow() {
        Body from = createBody();
        Body child = createBody();
        ProudChildEvent event = new ProudChildEvent(from, null, null, 10);
        event.setState(ProudChildEvent.STATE.END);
        from.setCurrentEvent(event);
        child.setCurrentEvent(event);
        assertDoesNotThrow(() -> event.update(child));
    }

    // --- update: partner path ---

    @Test
    public void testUpdate_partnerOfFrom_stateGO_doesNotThrow() {
        Body from = createBody();
        Body partner = createBody();
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
        Body from = createBody();
        Body partner = createBody();
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
        Body from = createBody();
        Body b = createBody();
        b.setPanicType(PanicType.FEAR); // b.getPanicType() != null → ABORT
        ProudChildEvent event = new ProudChildEvent(from, null, null, 10);
        from.setCurrentEvent(event);
        b.setCurrentEvent(event);
        assertEquals(EventPacket.UpdateState.ABORT, event.update(b));
    }

    @Test
    public void testUpdate_fromUnhappy_returnsAbort() {
        Body from = createBody();
        Body b = createBody();
        from.setHappiness(Happiness.SAD); // isUnhappy() = true → ABORT
        ProudChildEvent event = new ProudChildEvent(from, null, null, 10);
        from.setCurrentEvent(event);
        b.setCurrentEvent(event);
        assertEquals(EventPacket.UpdateState.ABORT, event.update(b));
    }

    // --- update: tick % 30 != 0 → returns null ---
    @Test
    public void testUpdate_tick1_returnsNull() {
        Body from = createBody();
        Body b = createBody();
        ProudChildEvent event = new ProudChildEvent(from, null, null, 10);
        event.tick = 1; // 1 % 30 = 1 != 0 → returns null
        from.setCurrentEvent(event);
        b.setCurrentEvent(event);
        assertNull(event.update(b));
    }

    // --- update: child GO, isDontMove → ABORT ---
    @Test
    public void testUpdate_childBody_GO_isDontMove_returnsAbort() {
        Body from = createBody();
        Body b = createBody();
        b.setGrabbed(true); // isDontMove() returns true
        ProudChildEvent event = new ProudChildEvent(from, null, null, 10);
        from.setCurrentEvent(event);
        b.setCurrentEvent(event);
        assertEquals(EventPacket.UpdateState.ABORT, event.update(b));
    }

    // --- update: child SING, bActionFlag=false (inner path) ---
    @Test
    public void testUpdate_childBody_SING_bActionFlagFalse_doesNotThrow() {
        Body from = createBody();
        Body child = createBody();
        ProudChildEvent event = new ProudChildEvent(from, null, null, 10);
        event.bActionFlag = false;
        event.setState(ProudChildEvent.STATE.SING);
        from.setCurrentEvent(event);
        child.setCurrentEvent(event);
        assertDoesNotThrow(() -> event.update(child));
    }

    // --- checkEventResponse: b is child of from (baby age) → returns true ---
    @Test
    public void testCheckEventResponse_isChildOfFrom_baby_returnsTrue() {
        Body from = createBody();
        Body child = createBody();
        // Set child's parents so that from is parent of child
        child.setParents(new int[]{from.getUniqueID(), -1});
        // Make child BABY (not adult)
        child.setAgeState(AgeState.BABY);
        ProudChildEvent event = new ProudChildEvent(from, null, null, 10);
        // from is in world map, child.getFather() = from.uniqueID
        assertTrue(event.checkEventResponse(child));
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
