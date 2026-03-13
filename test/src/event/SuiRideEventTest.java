package src.event;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import src.SimYukkuri;
import src.base.Body;
import src.base.EventPacket.EventPriority;
import src.base.EventPacket.UpdateState;
import src.enums.FavItemType;
import src.enums.PublicRank;
import src.item.Sui;
import src.base.EventTestBase;

public class SuiRideEventTest extends EventTestBase {

    // -----------------------------------------------------------------------
    // Helper: create a Sui and register it with the world
    // -----------------------------------------------------------------------
    private Sui createSui(int objId) {
        Sui s = new Sui();
        s.setObjId(objId);
        SimYukkuri.world.getCurrentMap().getSui().put(objId, s);
        return s;
    }

    // -----------------------------------------------------------------------
    // Default constructor
    // -----------------------------------------------------------------------

    @Test
    public void testDefaultConstructor_fromAndToAreNegativeOne() {
        SuiRideEvent event = new SuiRideEvent();
        assertEquals(-1, event.getFrom());
        assertEquals(-1, event.getTo());
    }

    @Test
    public void testDefaultConstructor_priorityIsLow() {
        SuiRideEvent event = new SuiRideEvent();
        assertEquals(EventPriority.LOW, event.getPriority());
    }

    // -----------------------------------------------------------------------
    // Parameterized constructor
    // -----------------------------------------------------------------------

    @Test
    public void testParameterizedConstructor_priorityIsMiddle() {
        Body from = createBody(1, 100, 100);
        SuiRideEvent event = new SuiRideEvent(from, null, null, 100);
        assertEquals(EventPriority.MIDDLE, event.getPriority());
    }

    @Test
    public void testParameterizedConstructor_fromIsSet() {
        Body from = createBody(1, 100, 100);
        SuiRideEvent event = new SuiRideEvent(from, null, null, 100);
        assertEquals(from.getUniqueID(), event.getFrom());
    }

    @Test
    public void testParameterizedConstructor_countIsSet() {
        Body from = createBody(1, 100, 100);
        SuiRideEvent event = new SuiRideEvent(from, null, null, 42);
        assertEquals(42, event.getCount());
    }

    @Test
    public void testParameterizedConstructor_toIsNegativeOneWhenNull() {
        Body from = createBody(1, 100, 100);
        SuiRideEvent event = new SuiRideEvent(from, null, null, 100);
        assertEquals(-1, event.getTo());
    }

    // -----------------------------------------------------------------------
    // toString
    // -----------------------------------------------------------------------

    @Test
    public void testToString_returnsNonNull() {
        SuiRideEvent event = new SuiRideEvent();
        assertDoesNotThrow(() -> event.toString());
    }

    @Test
    public void testToString_returnsNonEmpty() {
        SuiRideEvent event = new SuiRideEvent();
        assertDoesNotThrow(() -> event.toString());
    }

    // -----------------------------------------------------------------------
    // checkEventResponse - early-return paths
    // -----------------------------------------------------------------------

    @Test
    public void testCheckEventResponse_returnsFalseWhenTargetIsNull_defaultCtor() {
        Body b = createBody(1, 100, 100);
        SuiRideEvent event = new SuiRideEvent();
        assertFalse(event.checkEventResponse(b));
    }

    @Test
    public void testCheckEventResponse_returnsFalseWhenTargetIsNull_paramCtor() {
        Body from = createBody(1, 100, 100);
        Body b = createBody(2, 200, 200);
        SuiRideEvent event = new SuiRideEvent(from, null, null, 100);
        assertFalse(event.checkEventResponse(b));
    }

    @Test
    public void testCheckEventResponse_returnsFalseWhenFromIsNull_withValidTarget() {
        Body b = createBody(1, 100, 100);
        Sui sui = createSui(999);
        SuiRideEvent event = new SuiRideEvent();
        event.setTarget(sui.getObjId());
        assertFalse(event.checkEventResponse(b));
    }

    @Test
    public void testCheckEventResponse_returnsTrueWhenFromEqualsB_withValidTarget() {
        Body from = createBody(1, 100, 100);
        Sui sui = createSui(999);
        SuiRideEvent event = new SuiRideEvent(from, null, sui, 100);
        assertTrue(event.checkEventResponse(from));
    }

    @Test
    public void testCheckEventResponse_returnsFalseWhenBCannotRespond_noCurrentEvent() {
        Body from = createBody(1, 100, 100);
        Body b = createBody(2, 200, 200);
        Sui sui = createSui(999);
        SuiRideEvent event = new SuiRideEvent(from, null, sui, 100);
        assertFalse(event.checkEventResponse(b));
    }

    @Test
    public void testCheckEventResponse_returnsFalseWhenBCannotEventResponse_dead() {
        Body from = createBody(1, 100, 100);
        Body b = createBody(2, 200, 200);
        Sui sui = createSui(999);
        SuiRideEvent event = new SuiRideEvent(from, null, sui, 100);
        from.setCurrentEvent(event);
        b.setDead(true);
        assertFalse(event.checkEventResponse(b));
    }

    @Test
    public void testCheckEventResponse_returnsFalseWhenBAlreadyHasEvent() {
        Body from = createBody(1, 100, 100);
        Body b = createBody(2, 200, 200);
        Sui sui = createSui(999);
        SuiRideEvent event = new SuiRideEvent(from, null, sui, 100);
        from.setCurrentEvent(event);
        b.setCurrentEvent(new SuiRideEvent());
        assertFalse(event.checkEventResponse(b));
    }

    @Test
    public void testCheckEventResponse_returnsFalseWhenBIsLockedMove() {
        Body from = createBody(1, 100, 100);
        Body b = createBody(2, 200, 200);
        Sui sui = createSui(999);
        SuiRideEvent event = new SuiRideEvent(from, null, sui, 100);
        from.setCurrentEvent(event);
        b.setLockmove(true);
        assertFalse(event.checkEventResponse(b));
    }

    @Test
    public void testCheckEventResponse_returnsFalseWhenNoFamilyRelationship() {
        Body from = createBody(1, 100, 100);
        Body b = createBody(2, 200, 200);
        Sui sui = createSui(999);
        SuiRideEvent event = new SuiRideEvent(from, null, sui, 100);
        from.setCurrentEvent(event);
        assertFalse(event.checkEventResponse(b));
    }

    @Test
    public void testCheckEventResponse_returnsTrueWhenBIsPartnerOfFrom() {
        Body from = createBody(1, 100, 100);
        Body b = createBody(2, 200, 200);
        Sui sui = createSui(999);
        SuiRideEvent event = new SuiRideEvent(from, null, sui, 100);
        from.setCurrentEvent(event);
        from.setPartner(b.getUniqueID());
        b.setPartner(from.getUniqueID());
        assertTrue(event.checkEventResponse(b));
    }

    @Test
    public void testCheckEventResponse_returnsTrueWhenFromIsParentOfB() {
        Body from = createBody(1, 100, 100);
        Body b = createBody(2, 200, 200);
        Sui sui = createSui(999);
        SuiRideEvent event = new SuiRideEvent(from, null, sui, 100);
        from.setCurrentEvent(event);
        // Set from as father of b (PAPA index = 0)
        b.getParents()[0] = from.getUniqueID();
        assertTrue(event.checkEventResponse(b));
    }

    @Test
    public void testCheckEventResponse_returnsFalseWhenBIsExciting_withRelationship() {
        Body from = createBody(1, 100, 100);
        Body b = createBody(2, 200, 200);
        Sui sui = createSui(999);
        SuiRideEvent event = new SuiRideEvent(from, null, sui, 100);
        from.setCurrentEvent(event);
        from.setPartner(b.getUniqueID());
        b.setPartner(from.getUniqueID());
        b.setExciting(true);
        assertFalse(event.checkEventResponse(b));
    }

    @Test
    public void testCheckEventResponse_returnsFalseWhenBIsScare_withRelationship() {
        Body from = createBody(1, 100, 100);
        Body b = createBody(2, 200, 200);
        Sui sui = createSui(999);
        SuiRideEvent event = new SuiRideEvent(from, null, sui, 100);
        from.setCurrentEvent(event);
        from.setPartner(b.getUniqueID());
        b.setPartner(from.getUniqueID());
        b.setScare(true);
        assertFalse(event.checkEventResponse(b));
    }

    @Test
    public void testCheckEventResponse_returnsFalseWhenBIsUnunSlave_withRelationship() {
        Body from = createBody(1, 100, 100);
        Body b = createBody(2, 200, 200);
        Sui sui = createSui(999);
        SuiRideEvent event = new SuiRideEvent(from, null, sui, 100);
        from.setCurrentEvent(event);
        from.setPartner(b.getUniqueID());
        b.setPartner(from.getUniqueID());
        b.setPublicRank(PublicRank.UnunSlave);
        assertFalse(event.checkEventResponse(b));
    }

    // -----------------------------------------------------------------------
    // start
    // -----------------------------------------------------------------------

    @Test
    public void testStart_doesNotThrowWhenTargetIsNull() {
        Body b = createBody(1, 100, 100);
        SuiRideEvent event = new SuiRideEvent();
        assertDoesNotThrow(() -> event.start(b));
    }

    @Test
    public void testStart_doesNotThrowWithValidTarget() {
        Body b = createBody(1, 100, 100);
        Sui sui = createSui(999);
        sui.setX(200);
        sui.setY(200);
        SuiRideEvent event = new SuiRideEvent(b, null, sui, 100);
        assertDoesNotThrow(() -> event.start(b));
    }

    // -----------------------------------------------------------------------
    // execute
    // -----------------------------------------------------------------------

    @Test
    public void testExecute_returnsFalseWhenTargetIsNull() {
        Body b = createBody(1, 100, 100);
        SuiRideEvent event = new SuiRideEvent();
        assertFalse(event.execute(b));
    }

    @Test
    public void testExecute_returnsFalseWhenSuiNotInWaitingState() {
        Body b = createBody(1, 100, 100);
        Sui sui = createSui(999);
        sui.setCurrent_condition(2);
        SuiRideEvent event = new SuiRideEvent(b, null, sui, 100);
        assertFalse(event.execute(b));
    }

    @Test
    public void testExecute_alwaysReturnsFalse_suiInWaitingState() {
        Body b = createBody(1, 100, 100);
        Sui sui = createSui(999);
        // Default getCurrent_condition() == 1 (rest/waiting)
        assertEquals(1, sui.getCurrent_condition());
        SuiRideEvent event = new SuiRideEvent(b, null, sui, 100);
        assertFalse(event.execute(b));
    }

    @Test
    public void testExecute_suiWaiting_bodyRidesOnSui() {
        Body b = createBody(1, 100, 100);
        Sui sui = createSui(999);
        assertEquals(1, sui.getCurrent_condition());
        SuiRideEvent event = new SuiRideEvent(b, null, sui, 100);
        event.execute(b);
        assertTrue(sui.isriding(b));
    }

    // -----------------------------------------------------------------------
    // end
    // -----------------------------------------------------------------------

    @Test
    public void testEnd_doesNotThrowWhenLinkParentIsNegativeOne() {
        Body b = createBody(1, 100, 100);
        SuiRideEvent event = new SuiRideEvent(b, null, null, 100);
        assertDoesNotThrow(() -> event.end(b));
    }

    @Test
    public void testEnd_callsRideOffWhenBodyIsRidingSui() {
        Body b = createBody(1, 100, 100);
        Sui sui = createSui(999);
        sui.rideOn(b);
        assertTrue(sui.isriding(b));
        b.setLinkParent(sui.getObjId());
        SuiRideEvent event = new SuiRideEvent(b, null, sui, 100);
        event.end(b);
        assertFalse(sui.isriding(b));
    }

    @Test
    public void testEnd_doesNotThrowForNonRidingBody() {
        Body b = createBody(1, 100, 100);
        Sui sui = createSui(999);
        SuiRideEvent event = new SuiRideEvent(b, null, sui, 100);
        // b is not riding, linkParent = -1 => takeMappedObj returns null
        assertDoesNotThrow(() -> event.end(b));
    }

    // -----------------------------------------------------------------------
    // update - target is null => ABORT
    // -----------------------------------------------------------------------

    @Test
    public void testUpdate_returnsAbortWhenTargetIsNull() {
        Body b = createBody(1, 100, 100);
        SuiRideEvent event = new SuiRideEvent();
        assertEquals(UpdateState.ABORT, event.update(b));
    }

    // -----------------------------------------------------------------------
    // update - b does NOT have FavItem SUI (no sui owned)
    // -----------------------------------------------------------------------

    @Test
    public void testUpdate_noFavSui_hasLinkParent_returnsNull() {
        Body b = createBody(1, 100, 100);
        Sui targetSui = createSui(999);
        Sui parentSui = createSui(888);
        b.setLinkParent(parentSui.getObjId());
        SuiRideEvent event = new SuiRideEvent(b, null, targetSui, 100);
        assertNull(event.update(b));
    }

    @Test
    public void testUpdate_noFavSui_noLinkParent_fromIsNull_returnsAbort() {
        Body b = createBody(1, 100, 100);
        Sui sui = createSui(999);
        SuiRideEvent event = new SuiRideEvent();
        event.setTarget(sui.getObjId());
        assertDoesNotThrow(() -> {
            UpdateState state = event.update(b);
            assertEquals(UpdateState.ABORT, state);
        });
    }

    @Test
    public void testUpdate_noFavSui_fromEqualsB_suiCannotRide_doesNotThrow() {
        Body b = createBody(1, 100, 100);
        Sui sui = createSui(999);
        SuiRideEvent event = new SuiRideEvent(b, null, sui, 100);
        assertDoesNotThrow(() -> event.update(b));
    }

    @Test
    public void testUpdate_noFavSui_fromNotEqualsB_fromCurrentEventIsNull_returnsAbort() {
        Body from = createBody(1, 100, 100);
        Body b = createBody(2, 200, 200);
        Sui sui = createSui(999);
        SuiRideEvent event = new SuiRideEvent(from, null, sui, 100);
        // from.getCurrentEvent() == null => ABORT
        assertDoesNotThrow(() -> {
            UpdateState state = event.update(b);
            assertEquals(UpdateState.ABORT, state);
        });
    }

    @Test
    public void testUpdate_noFavSui_bIsDontMove_returnsAbort() {
        Body from = createBody(1, 100, 100);
        Body b = createBody(2, 200, 200);
        Sui sui = createSui(999);
        SuiRideEvent event = new SuiRideEvent(from, null, sui, 100);
        from.setCurrentEvent(event);
        // isDontMove returns true when lockmove is set
        b.setLockmove(true);
        assertDoesNotThrow(() -> {
            UpdateState state = event.update(b);
            assertEquals(UpdateState.ABORT, state);
        });
    }

    @Test
    public void testUpdate_noFavSui_bIsExciting_returnsAbort() {
        Body from = createBody(1, 100, 100);
        Body b = createBody(2, 200, 200);
        Sui sui = createSui(999);
        SuiRideEvent event = new SuiRideEvent(from, null, sui, 100);
        from.setCurrentEvent(event);
        b.setExciting(true);
        assertDoesNotThrow(() -> {
            UpdateState state = event.update(b);
            assertEquals(UpdateState.ABORT, state);
        });
    }

    @Test
    public void testUpdate_noFavSui_bIsScare_returnsAbort() {
        Body from = createBody(1, 100, 100);
        Body b = createBody(2, 200, 200);
        Sui sui = createSui(999);
        SuiRideEvent event = new SuiRideEvent(from, null, sui, 100);
        from.setCurrentEvent(event);
        b.setScare(true);
        assertDoesNotThrow(() -> {
            UpdateState state = event.update(b);
            assertEquals(UpdateState.ABORT, state);
        });
    }

    // -----------------------------------------------------------------------
    // update - b HAS FavItem SUI
    // -----------------------------------------------------------------------

    @Test
    public void testUpdate_hasFavSui_notRiding_fromEqualsB_returnsNull() {
        Body b = createBody(1, 100, 100);
        Sui sui = createSui(999);
        // Give b favitem SUI without actually riding (no rideOn)
        b.setFavItem(FavItemType.SUI, sui);
        SuiRideEvent event = new SuiRideEvent(b, null, sui, 100);
        // isriding(b) == false => falls to tick++ and returns null
        assertDoesNotThrow(() -> {
            UpdateState state = event.update(b);
            assertNull(state);
        });
    }

    @Test
    public void testUpdate_hasFavSui_riding_fromIsNull_returnsAbort() {
        Body b = createBody(1, 100, 100);
        Sui sui = createSui(999);
        sui.rideOn(b);
        b.setFavItem(FavItemType.SUI, sui);
        // from = -1 (not in world)
        SuiRideEvent event = new SuiRideEvent();
        event.setTarget(sui.getObjId());
        assertEquals(UpdateState.ABORT, event.update(b));
    }

    @Test
    public void testUpdate_hasFavSui_riding_fromEqualsB_bindbodyUnder3_tickUnder50_returnsNull() {
        Body b = createBody(1, 100, 100);
        Sui sui = createSui(999);
        sui.rideOn(b);
        b.setFavItem(FavItemType.SUI, sui);
        SuiRideEvent event = new SuiRideEvent(b, null, sui, 100);
        // tick=0 < 50, current_bindbody_num=1 < 3 => falls through, returns null
        UpdateState state = event.update(b);
        assertNull(state);
    }

    @Test
    public void testUpdate_hasFavSui_riding_fromEqualsB_bindbodyAtLimit_suiWaiting() {
        Body b = createBody(1, 100, 100);
        Sui sui = createSui(999);
        sui.rideOn(b);
        b.setFavItem(FavItemType.SUI, sui);
        sui.setCurrent_bindbody_num(3);
        sui.setCurrent_condition(1);
        SuiRideEvent event = new SuiRideEvent(b, null, sui, 100);
        // tick=0, not > 500; condition==1 => moveTo path
        assertDoesNotThrow(() -> event.update(b));
    }

    @Test
    public void testUpdate_hasFavSui_riding_fromEqualsB_bindbodyAtLimit_suiNotWaiting() {
        Body b = createBody(1, 100, 100);
        Sui sui = createSui(999);
        sui.rideOn(b);
        b.setFavItem(FavItemType.SUI, sui);
        sui.setCurrent_bindbody_num(3);
        sui.setCurrent_condition(2); // not waiting
        SuiRideEvent event = new SuiRideEvent(b, null, sui, 100);
        // condition != 1 => setBodyEventResMessage path
        assertDoesNotThrow(() -> event.update(b));
    }

    @Test
    public void testUpdate_hasFavSui_riding_fromNotEqualsB_fromCurrentEventIsThis_suiWaiting_returnsAbort() {
        Body from = createBody(1, 100, 100);
        Body b = createBody(2, 200, 200);
        Sui sui = createSui(999);
        sui.rideOn(from); // from is owner/rider
        // Manually make b ride in slot 1
        sui.getBindBody()[1] = b;
        sui.setCurrent_bindbody_num(2);
        b.setLinkParent(sui.getObjId());
        b.setFavItem(FavItemType.SUI, sui);
        sui.setCurrent_condition(1); // waiting
        SuiRideEvent event = new SuiRideEvent(from, null, sui, 100);
        // from.getCurrentEvent() != this (null) AND condition==1 => rideOff ABORT
        UpdateState state = event.update(b);
        assertEquals(UpdateState.ABORT, state);
        assertFalse(sui.isriding(b));
    }

    @Test
    public void testUpdate_hasFavSui_riding_fromNotEqualsB_suiNotCondition1_returnsNull() {
        Body from = createBody(1, 100, 100);
        Body b = createBody(2, 200, 200);
        Sui sui = createSui(999);
        sui.rideOn(from);
        sui.getBindBody()[1] = b;
        sui.setCurrent_bindbody_num(2);
        b.setLinkParent(sui.getObjId());
        b.setFavItem(FavItemType.SUI, sui);
        sui.setCurrent_condition(2); // not waiting
        SuiRideEvent event = new SuiRideEvent(from, null, sui, 100);
        // from.getCurrentEvent() != this AND condition != 1 => no rideOff, returns null
        UpdateState state = event.update(b);
        assertNull(state);
    }
}
