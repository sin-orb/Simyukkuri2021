package src.event;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
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
import src.system.Sprite;
import src.util.YukkuriUtil;
import src.yukkuri.Reimu;

public class YukkuriRideEventTest {

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
        YukkuriRideEvent event = new YukkuriRideEvent();
        assertEquals(-1, event.getFrom());
        assertEquals(-1, event.getTo());
        assertEquals(EventPriority.LOW, event.getPriority());
    }

    // --- Parameterized constructor ---

    @Test
    public void testParameterizedConstructor_setsPriorityMiddle() {
        Body from = createBody();
        Body to = createBody();
        YukkuriRideEvent event = new YukkuriRideEvent(from, to, null, 100);
        assertEquals(EventPriority.MIDDLE, event.getPriority());
        assertEquals(from.getUniqueID(), event.getFrom());
        assertEquals(to.getUniqueID(), event.getTo());
        assertEquals(100, event.getCount());
    }

    // --- checkEventResponse ---

    @Test
    public void testCheckEventResponse_returnsFalseWhenToIsNull() {
        Body b = createBody();
        YukkuriRideEvent event = new YukkuriRideEvent();
        // to is -1 (null lookup) => returns false
        assertFalse(event.checkEventResponse(b));
    }

    @Test
    public void testCheckEventResponse_returnsTrueWhenFromEqualsB() {
        Body from = createBody();
        Body to = createBody();
        YukkuriRideEvent event = new YukkuriRideEvent(from, to, null, 100);
        // from == b => returns true
        assertTrue(event.checkEventResponse(from));
    }

    @Test
    public void testCheckEventResponse_returnsTrueWhenToEqualsB() {
        Body from = createBody();
        Body to = createBody();
        YukkuriRideEvent event = new YukkuriRideEvent(from, to, null, 100);
        // to == b => returns true
        assertTrue(event.checkEventResponse(to));
    }

    @Test
    public void testCheckEventResponse_returnsFalseForUnrelatedBody() {
        Body from = createBody();
        Body to = createBody();
        Body other = createBody();
        YukkuriRideEvent event = new YukkuriRideEvent(from, to, null, 100);
        // other is neither from nor to => returns false
        assertFalse(event.checkEventResponse(other));
    }

    // --- execute ---

    @Test
    public void testExecute_returnsFalse() {
        Body b = createBody();
        YukkuriRideEvent event = new YukkuriRideEvent(b, null, null, 100);
        assertFalse(event.execute(b));
    }

    // --- end ---

    @Test
    public void testEnd_setsLinkParentToNegativeOneOnTo() {
        Body from = createBody();
        Body to = createBody();
        to.setLinkParent(from.objId);
        YukkuriRideEvent event = new YukkuriRideEvent(from, to, null, 100);
        event.end(from);
        assertEquals(-1, to.getLinkParent());
    }

    // --- update ---
    @Test
    public void testUpdate_fromNull_returnsAbort() {
        Body b = createBody();
        YukkuriRideEvent event = new YukkuriRideEvent();
        // from=-1 → null → ABORT
        assertEquals(src.base.EventPacket.UpdateState.ABORT, event.update(b));
    }

    @Test
    public void testUpdate_toNull_returnsAbort() {
        Body from = createBody();
        Body b = createBody();
        YukkuriRideEvent event = new YukkuriRideEvent(from, null, null, 100);
        from.setCurrentEvent(event);
        // to=-1 → null → ABORT
        assertEquals(src.base.EventPacket.UpdateState.ABORT, event.update(b));
    }

    @Test
    public void testUpdate_toDead_returnsAbort() {
        Body from = createBody();
        Body to = createBody();
        Body b = createBody();
        to.setDead(true);
        YukkuriRideEvent event = new YukkuriRideEvent(from, to, null, 100);
        from.setCurrentEvent(event);
        assertEquals(src.base.EventPacket.UpdateState.ABORT, event.update(b));
    }

    @Test
    public void testUpdate_fromCurrentEventNotThis_returnsAbort() {
        Body from = createBody();
        Body to = createBody();
        YukkuriRideEvent event = new YukkuriRideEvent(from, to, null, 100);
        // from.getCurrentEvent() != this → ABORT
        assertEquals(src.base.EventPacket.UpdateState.ABORT, event.update(from));
    }

    // --- start ---
    @Test
    public void testStart_doesNotThrow() {
        Body from = createBody();
        Body to = createBody();
        YukkuriRideEvent event = new YukkuriRideEvent(from, to, null, 100);
        assertDoesNotThrow(() -> event.start(from));
    }

    // --- toString ---
    @Test
    public void testToString_doesNotThrow() {
        YukkuriRideEvent event = new YukkuriRideEvent();
        assertDoesNotThrow(() -> event.toString());
    }

    // --- update: b == from, basic flow ---

    @Test
    public void testUpdate_bEqualsFrom_tick10001_returnsAbort() {
        Body from = createBody();
        Body to = createBody();
        YukkuriRideEvent event = new YukkuriRideEvent(from, to, null, 100);
        from.setCurrentEvent(event);
        // Set tick > 10000 via reflection
        try {
            java.lang.reflect.Field f = YukkuriRideEvent.class.getDeclaredField("tick");
            f.setAccessible(true);
            f.setInt(event, 10000); // becomes 10001 after tick++ in update
        } catch (Exception e) { }
        assertEquals(src.base.EventPacket.UpdateState.ABORT, event.update(from));
    }

    @Test
    public void testUpdate_bEqualsFrom_linkParentNull_doesNotThrow() {
        Body from = createBody();
        Body to = createBody();
        to.setLinkParent(-1); // no parent
        YukkuriRideEvent event = new YukkuriRideEvent(from, to, null, 100);
        from.setCurrentEvent(event);
        // Set tick so tick%20 == 0 → enters the move logic
        try {
            java.lang.reflect.Field f = YukkuriRideEvent.class.getDeclaredField("tick");
            f.setAccessible(true);
            f.setInt(event, -1); // becomes 0 after tick++, 0%20==0
        } catch (Exception e) { }
        assertDoesNotThrow(() -> event.update(from));
    }

    @Test
    public void testUpdate_bEqualsTo_notLinked_doesNotThrow() {
        Body from = createBody();
        Body to = createBody();
        to.setLinkParent(-1); // not on parent
        YukkuriRideEvent event = new YukkuriRideEvent(from, to, null, 100);
        from.setCurrentEvent(event);
        to.setCurrentEvent(event);
        assertDoesNotThrow(() -> event.update(to));
    }

    @Test
    public void testUpdate_fromRemoved_returnsAbort() {
        Body from = createBody();
        Body to = createBody();
        from.setRemoved(true);
        YukkuriRideEvent event = new YukkuriRideEvent(from, to, null, 100);
        from.setCurrentEvent(event);
        assertEquals(src.base.EventPacket.UpdateState.ABORT, event.update(from));
    }

    @Test
    public void testUpdate_toRemoved_returnsAbort() {
        Body from = createBody();
        Body to = createBody();
        to.setRemoved(true);
        YukkuriRideEvent event = new YukkuriRideEvent(from, to, null, 100);
        from.setCurrentEvent(event);
        assertEquals(src.base.EventPacket.UpdateState.ABORT, event.update(from));
    }

    // --- simpleEventAction: default EventPacket returns false ---
    @Test
    public void testSimpleEventAction_defaultReturnsFalse() {
        Body b = createBody();
        YukkuriRideEvent event = new YukkuriRideEvent();
        assertFalse(event.simpleEventAction(b));
    }

    // --- update: to.isNormalDirty() → ABORT ---
    @Test
    public void testUpdate_toNormalDirty_returnsAbort() {
        Body from = createBody();
        Body to = createBody();
        to.setDirty(true); // isNormalDirty() = !dead && dirty → true
        YukkuriRideEvent event = new YukkuriRideEvent(from, to, null, 100);
        from.setCurrentEvent(event);
        assertEquals(src.base.EventPacket.UpdateState.ABORT, event.update(from));
    }

    // --- update: from.findSick(to) → ABORT ---
    @Test
    public void testUpdate_findSick_returnsAbort() {
        Body from = createBody();
        Body to = createBody();
        // Body() constructor randomly sets intelligence; ensure AVERAGE (not FOOL) so findSick checks isSick()
        from.setIntelligence(src.enums.Intelligence.AVERAGE);
        to.setSickPeriod(2000); // isSick() = sickPeriod > INCUBATIONPERIODorg(1200) → true
        YukkuriRideEvent event = new YukkuriRideEvent(from, to, null, 100);
        from.setCurrentEvent(event);
        assertEquals(src.base.EventPacket.UpdateState.ABORT, event.update(from));
    }

    // --- update: b == from, to on head (carrying logic) → null ---
    @Test
    public void testUpdate_bEqualsFrom_toOnHead_doesNotThrow() {
        Body from = createBody();
        Body to = createBody();
        to.setLinkParent(from.objId); // takeMappedObj(to.getLinkParent()) finds from → not null
        YukkuriRideEvent event = new YukkuriRideEvent(from, to, null, 100);
        from.setCurrentEvent(event);
        assertDoesNotThrow(() -> event.update(from));
    }

    // --- update: b == to (child), to on head (riding on parent) ---
    @Test
    public void testUpdate_bEqualsTo_onHead_doesNotThrow() {
        Body from = createBody();
        Body to = createBody();
        to.setLinkParent(from.objId); // takeMappedObj(to.getLinkParent()) finds from → not null
        YukkuriRideEvent event = new YukkuriRideEvent(from, to, null, 100);
        from.setCurrentEvent(event);
        to.setCurrentEvent(event);
        assertDoesNotThrow(() -> event.update(to));
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
