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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.simyukkuri.SimYukkuri;
import org.simyukkuri.engine.World;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.living.yukkuri.impl.Reimu;
import org.simyukkuri.enums.AgeState;
import org.simyukkuri.event.EventPacket.EventPriority;
import org.simyukkuri.system.Sprite;
import org.simyukkuri.util.WorldTestHelper;

public class YukkuriRideEventTest {

    @BeforeEach
    public void setUp() {
        WorldTestHelper.resetWorld();
        SimYukkuri.world = new World();
        WorldTestHelper.initializeStandardTranslate500();
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
        Yukkuri from = createBody();
        Yukkuri to = createBody();
        YukkuriRideEvent event = new YukkuriRideEvent(from, to, null, 100);
        assertEquals(EventPriority.MIDDLE, event.getPriority());
        assertEquals(from.getUniqueID(), event.getFrom());
        assertEquals(to.getUniqueID(), event.getTo());
        assertEquals(100, event.getCount());
    }

    // --- checkEventResponse ---

    @Test
    public void testCheckEventResponse_returnsFalseWhenToIsNull() {
        Yukkuri b = createBody();
        YukkuriRideEvent event = new YukkuriRideEvent();
        // to is -1 (null lookup) => returns false
        assertFalse(event.checkEventResponse(b));
    }

    @Test
    public void testCheckEventResponse_returnsTrueWhenFromEqualsB() {
        Yukkuri from = createBody();
        Yukkuri to = createBody();
        YukkuriRideEvent event = new YukkuriRideEvent(from, to, null, 100);
        // from == b => returns true
        assertTrue(event.checkEventResponse(from));
    }

    @Test
    public void testCheckEventResponse_returnsTrueWhenToEqualsB() {
        Yukkuri from = createBody();
        Yukkuri to = createBody();
        YukkuriRideEvent event = new YukkuriRideEvent(from, to, null, 100);
        // to == b => returns true
        assertTrue(event.checkEventResponse(to));
    }

    @Test
    public void testCheckEventResponse_returnsFalseForUnrelatedBody() {
        Yukkuri from = createBody();
        Yukkuri to = createBody();
        Yukkuri other = createBody();
        YukkuriRideEvent event = new YukkuriRideEvent(from, to, null, 100);
        // other is neither from nor to => returns false
        assertFalse(event.checkEventResponse(other));
    }

    // --- execute ---

    @Test
    public void testExecute_returnsFalse() {
        Yukkuri b = createBody();
        YukkuriRideEvent event = new YukkuriRideEvent(b, null, null, 100);
        assertFalse(event.execute(b));
    }

    // --- end ---

    @Test
    public void testEnd_setsLinkParentToNegativeOneOnTo() {
        Yukkuri from = createBody();
        Yukkuri to = createBody();
        to.setParentLinkId(from.objId);
        YukkuriRideEvent event = new YukkuriRideEvent(from, to, null, 100);
        event.end(from);
        assertEquals(-1, to.getParentLinkId());
    }

    // --- update ---
    @Test
    public void testUpdate_fromNull_returnsAbort() {
        Yukkuri b = createBody();
        YukkuriRideEvent event = new YukkuriRideEvent();
        // from=-1 → null → ABORT
        assertEquals(org.simyukkuri.event.EventPacket.UpdateState.ABORT, event.update(b));
    }

    @Test
    public void testUpdate_toNull_returnsAbort() {
        Yukkuri from = createBody();
        Yukkuri b = createBody();
        YukkuriRideEvent event = new YukkuriRideEvent(from, null, null, 100);
        from.setCurrentEvent(event);
        // to=-1 → null → ABORT
        assertEquals(org.simyukkuri.event.EventPacket.UpdateState.ABORT, event.update(b));
    }

    @Test
    public void testUpdate_toDead_returnsAbort() {
        Yukkuri from = createBody();
        Yukkuri to = createBody();
        Yukkuri b = createBody();
        to.setDead(true);
        YukkuriRideEvent event = new YukkuriRideEvent(from, to, null, 100);
        from.setCurrentEvent(event);
        assertEquals(org.simyukkuri.event.EventPacket.UpdateState.ABORT, event.update(b));
    }

    @Test
    public void testUpdate_fromCurrentEventNotThis_returnsAbort() {
        Yukkuri from = createBody();
        Yukkuri to = createBody();
        YukkuriRideEvent event = new YukkuriRideEvent(from, to, null, 100);
        // from.getCurrentEvent() != this → ABORT
        assertEquals(org.simyukkuri.event.EventPacket.UpdateState.ABORT, event.update(from));
    }

    // --- start ---
    @Test
    public void testStart_doesNotThrow() {
        Yukkuri from = createBody();
        Yukkuri to = createBody();
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
        Yukkuri from = createBody();
        Yukkuri to = createBody();
        YukkuriRideEvent event = new YukkuriRideEvent(from, to, null, 100);
        from.setCurrentEvent(event);
        // Set tick > 10000 via reflection
        try {
            java.lang.reflect.Field f = YukkuriRideEvent.class.getDeclaredField("tick");
            f.setAccessible(true);
            f.setInt(event, 10000); // becomes 10001 after tick++ in update
        } catch (Exception e) {
        }
        assertEquals(org.simyukkuri.event.EventPacket.UpdateState.ABORT, event.update(from));
    }

    @Test
    public void testUpdate_bEqualsFrom_parentLinkIdNull_doesNotThrow() {
        Yukkuri from = createBody();
        Yukkuri to = createBody();
        to.setParentLinkId(-1); // no parent
        YukkuriRideEvent event = new YukkuriRideEvent(from, to, null, 100);
        from.setCurrentEvent(event);
        // Set tick so tick%20 == 0 → enters the move logic
        try {
            java.lang.reflect.Field f = YukkuriRideEvent.class.getDeclaredField("tick");
            f.setAccessible(true);
            f.setInt(event, -1); // becomes 0 after tick++, 0%20==0
        } catch (Exception e) {
        }
        assertDoesNotThrow(() -> event.update(from));
    }

    @Test
    public void testUpdate_bEqualsTo_notLinked_doesNotThrow() {
        Yukkuri from = createBody();
        Yukkuri to = createBody();
        to.setParentLinkId(-1); // not on parent
        YukkuriRideEvent event = new YukkuriRideEvent(from, to, null, 100);
        from.setCurrentEvent(event);
        to.setCurrentEvent(event);
        assertDoesNotThrow(() -> event.update(to));
    }

    @Test
    public void testUpdate_fromRemoved_returnsAbort() {
        Yukkuri from = createBody();
        Yukkuri to = createBody();
        from.setRemoved(true);
        YukkuriRideEvent event = new YukkuriRideEvent(from, to, null, 100);
        from.setCurrentEvent(event);
        assertEquals(org.simyukkuri.event.EventPacket.UpdateState.ABORT, event.update(from));
    }

    @Test
    public void testUpdate_toRemoved_returnsAbort() {
        Yukkuri from = createBody();
        Yukkuri to = createBody();
        to.setRemoved(true);
        YukkuriRideEvent event = new YukkuriRideEvent(from, to, null, 100);
        from.setCurrentEvent(event);
        assertEquals(org.simyukkuri.event.EventPacket.UpdateState.ABORT, event.update(from));
    }

    // --- simpleEventAction: default EventPacket returns false ---
    @Test
    public void testSimpleEventAction_defaultReturnsFalse() {
        Yukkuri b = createBody();
        YukkuriRideEvent event = new YukkuriRideEvent();
        assertFalse(event.simpleEventAction(b));
    }

    // --- update: to.isNormalDirty() → ABORT ---
    @Test
    public void testUpdate_toNormalDirty_returnsAbort() {
        Yukkuri from = createBody();
        Yukkuri to = createBody();
        to.setDirty(true); // isNormalDirty() = !dead && dirty → true
        YukkuriRideEvent event = new YukkuriRideEvent(from, to, null, 100);
        from.setCurrentEvent(event);
        assertEquals(org.simyukkuri.event.EventPacket.UpdateState.ABORT, event.update(from));
    }

    // --- update: from.findSick(to) → ABORT ---
    @Test
    public void testUpdate_findSick_returnsAbort() {
        Yukkuri from = createBody();
        Yukkuri to = createBody();
        // Yukkuri() constructor randomly sets intelligence; ensure AVERAGE (not FOOL)
        // so findSick checks isSick()
        from.setIntelligence(org.simyukkuri.enums.Intelligence.AVERAGE);
        to.setSickPeriod(2000); // isSick() = sickPeriod > incubationPeriodBase(1200) → true
        YukkuriRideEvent event = new YukkuriRideEvent(from, to, null, 100);
        from.setCurrentEvent(event);
        assertEquals(org.simyukkuri.event.EventPacket.UpdateState.ABORT, event.update(from));
    }

    // --- update: b == from, to on head (carrying logic) → null ---
    @Test
    public void testUpdate_bEqualsFrom_toOnHead_doesNotThrow() {
        Yukkuri from = createBody();
        Yukkuri to = createBody();
        to.setParentLinkId(from.objId); // takeMappedObj(to.getParentLinkId()) finds from → not null
        YukkuriRideEvent event = new YukkuriRideEvent(from, to, null, 100);
        from.setCurrentEvent(event);
        assertDoesNotThrow(() -> event.update(from));
    }

    // --- update: b == to (child), to on head (riding on parent) ---
    @Test
    public void testUpdate_bEqualsTo_onHead_doesNotThrow() {
        Yukkuri from = createBody();
        Yukkuri to = createBody();
        to.setParentLinkId(from.objId); // takeMappedObj(to.getParentLinkId()) finds from → not null
        YukkuriRideEvent event = new YukkuriRideEvent(from, to, null, 100);
        from.setCurrentEvent(event);
        to.setCurrentEvent(event);
        assertDoesNotThrow(() -> event.update(to));
    }

    @Nested
    class RegressionScenarios {

        @Test
        public void testScenario_CloseChildGetsLinkedOntoParent() throws Exception {
            Yukkuri from = createBody();
            Yukkuri to = createBody();
            from.setX(100);
            from.setY(100);
            to.setX(101);
            to.setY(100);
            to.setParentLinkId(-1);

            YukkuriRideEvent event = new YukkuriRideEvent(from, to, null, 100);
            from.setCurrentEvent(event);

            java.lang.reflect.Field f = YukkuriRideEvent.class.getDeclaredField("tick");
            f.setAccessible(true);
            f.setInt(event, -1); // update後に0になり、親ロジックに入る

            assertNull(event.update(from));
            assertEquals(from.objId, to.getParentLinkId());
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
        SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(b.getUniqueID(), b);
        return b;
    }
}
