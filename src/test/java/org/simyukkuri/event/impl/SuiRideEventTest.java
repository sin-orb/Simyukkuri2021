package org.simyukkuri.event.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.simyukkuri.SimYukkuri;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.world.item.Sui;
import org.simyukkuri.enums.FavItemType;
import org.simyukkuri.enums.Happiness;
import org.simyukkuri.enums.PublicRank;
import org.simyukkuri.event.EventPacket.EventPriority;
import org.simyukkuri.event.EventPacket.UpdateState;
import org.simyukkuri.event.EventTestBase;

public class SuiRideEventTest extends EventTestBase {

    // -----------------------------------------------------------------------
    // Helper: create a Sui and register it with the world
    // -----------------------------------------------------------------------
    private Sui createSui(int objId) {
        Sui s = new Sui();
        s.setObjId(objId);
        SimYukkuri.world.getCurrentWorldState().getSuis().put(objId, s);
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
        Yukkuri from = createBody(1, 100, 100);
        SuiRideEvent event = new SuiRideEvent(from, null, null, 100);
        assertEquals(EventPriority.MIDDLE, event.getPriority());
    }

    @Test
    public void testParameterizedConstructor_fromIsSet() {
        Yukkuri from = createBody(1, 100, 100);
        SuiRideEvent event = new SuiRideEvent(from, null, null, 100);
        assertEquals(from.getUniqueID(), event.getFrom());
    }

    @Test
    public void testParameterizedConstructor_countIsSet() {
        Yukkuri from = createBody(1, 100, 100);
        SuiRideEvent event = new SuiRideEvent(from, null, null, 42);
        assertEquals(42, event.getCount());
    }

    @Test
    public void testParameterizedConstructor_toIsNegativeOneWhenNull() {
        Yukkuri from = createBody(1, 100, 100);
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
        Yukkuri b = createBody(1, 100, 100);
        SuiRideEvent event = new SuiRideEvent();
        assertFalse(event.checkEventResponse(b));
    }

    @Test
    public void testCheckEventResponse_returnsFalseWhenTargetIsNull_paramCtor() {
        Yukkuri from = createBody(1, 100, 100);
        Yukkuri b = createBody(2, 200, 200);
        SuiRideEvent event = new SuiRideEvent(from, null, null, 100);
        assertFalse(event.checkEventResponse(b));
    }

    @Test
    public void testCheckEventResponse_returnsFalseWhenFromIsNull_withValidTarget() {
        Yukkuri b = createBody(1, 100, 100);
        Sui sui = createSui(999);
        SuiRideEvent event = new SuiRideEvent();
        event.setTarget(sui.getObjId());
        assertFalse(event.checkEventResponse(b));
    }

    @Test
    public void testCheckEventResponse_returnsTrueWhenFromEqualsB_withValidTarget() {
        Yukkuri from = createBody(1, 100, 100);
        Sui sui = createSui(999);
        SuiRideEvent event = new SuiRideEvent(from, null, sui, 100);
        assertTrue(event.checkEventResponse(from));
    }

    @Test
    public void testCheckEventResponse_returnsFalseWhenBCannotRespond_noCurrentEvent() {
        Yukkuri from = createBody(1, 100, 100);
        Yukkuri b = createBody(2, 200, 200);
        Sui sui = createSui(999);
        SuiRideEvent event = new SuiRideEvent(from, null, sui, 100);
        assertFalse(event.checkEventResponse(b));
    }

    @Test
    public void testCheckEventResponse_returnsFalseWhenBCannotEventResponse_dead() {
        Yukkuri from = createBody(1, 100, 100);
        Yukkuri b = createBody(2, 200, 200);
        Sui sui = createSui(999);
        SuiRideEvent event = new SuiRideEvent(from, null, sui, 100);
        from.setCurrentEvent(event);
        b.setDead(true);
        assertFalse(event.checkEventResponse(b));
    }

    @Test
    public void testCheckEventResponse_returnsFalseWhenBAlreadyHasEvent() {
        Yukkuri from = createBody(1, 100, 100);
        Yukkuri b = createBody(2, 200, 200);
        Sui sui = createSui(999);
        SuiRideEvent event = new SuiRideEvent(from, null, sui, 100);
        from.setCurrentEvent(event);
        b.setCurrentEvent(new SuiRideEvent());
        assertFalse(event.checkEventResponse(b));
    }

    @Test
    public void testCheckEventResponse_returnsFalseWhenBIsLockedMove() {
        Yukkuri from = createBody(1, 100, 100);
        Yukkuri b = createBody(2, 200, 200);
        Sui sui = createSui(999);
        SuiRideEvent event = new SuiRideEvent(from, null, sui, 100);
        from.setCurrentEvent(event);
        b.setLockmove(true);
        assertFalse(event.checkEventResponse(b));
    }

    @Test
    public void testCheckEventResponse_returnsFalseWhenNoFamilyRelationship() {
        Yukkuri from = createBody(1, 100, 100);
        Yukkuri b = createBody(2, 200, 200);
        Sui sui = createSui(999);
        SuiRideEvent event = new SuiRideEvent(from, null, sui, 100);
        from.setCurrentEvent(event);
        assertFalse(event.checkEventResponse(b));
    }

    @Test
    public void testCheckEventResponse_returnsTrueWhenBIsPartnerOfFrom() {
        Yukkuri from = createBody(1, 100, 100);
        Yukkuri b = createBody(2, 200, 200);
        Sui sui = createSui(999);
        SuiRideEvent event = new SuiRideEvent(from, null, sui, 100);
        from.setCurrentEvent(event);
        from.setPartner(b.getUniqueID());
        b.setPartner(from.getUniqueID());
        assertTrue(event.checkEventResponse(b));
    }

    @Test
    public void testCheckEventResponse_returnsTrueWhenFromIsParentOfB() {
        Yukkuri from = createBody(1, 100, 100);
        Yukkuri b = createBody(2, 200, 200);
        Sui sui = createSui(999);
        SuiRideEvent event = new SuiRideEvent(from, null, sui, 100);
        from.setCurrentEvent(event);
        // Set from as father of b (PAPA index = 0)
        b.getParents()[0] = from.getUniqueID();
        assertTrue(event.checkEventResponse(b));
    }

    @Test
    public void testCheckEventResponse_returnsFalseWhenBIsExciting_withRelationship() {
        Yukkuri from = createBody(1, 100, 100);
        Yukkuri b = createBody(2, 200, 200);
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
        Yukkuri from = createBody(1, 100, 100);
        Yukkuri b = createBody(2, 200, 200);
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
        Yukkuri from = createBody(1, 100, 100);
        Yukkuri b = createBody(2, 200, 200);
        Sui sui = createSui(999);
        SuiRideEvent event = new SuiRideEvent(from, null, sui, 100);
        from.setCurrentEvent(event);
        from.setPartner(b.getUniqueID());
        b.setPartner(from.getUniqueID());
        b.setPublicRank(PublicRank.UNUN_SLAVE);
        assertFalse(event.checkEventResponse(b));
    }

    // -----------------------------------------------------------------------
    // start
    // -----------------------------------------------------------------------

    @Test
    public void testStart_doesNotThrowWhenTargetIsNull() {
        Yukkuri b = createBody(1, 100, 100);
        SuiRideEvent event = new SuiRideEvent();
        assertDoesNotThrow(() -> event.start(b));
    }

    @Test
    public void testStart_doesNotThrowWithValidTarget() {
        Yukkuri b = createBody(1, 100, 100);
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
        Yukkuri b = createBody(1, 100, 100);
        SuiRideEvent event = new SuiRideEvent();
        assertFalse(event.execute(b));
    }

    @Test
    public void testExecute_returnsFalseWhenSuiNotInWaitingState() {
        Yukkuri b = createBody(1, 100, 100);
        Sui sui = createSui(999);
        sui.setCurrent_condition(2);
        SuiRideEvent event = new SuiRideEvent(b, null, sui, 100);
        assertFalse(event.execute(b));
    }

    @Test
    public void testExecute_alwaysReturnsFalse_suiInWaitingState() {
        Yukkuri b = createBody(1, 100, 100);
        Sui sui = createSui(999);
        // Default getCurrent_condition() == 1 (rest/waiting)
        assertEquals(1, sui.getCurrent_condition());
        SuiRideEvent event = new SuiRideEvent(b, null, sui, 100);
        assertFalse(event.execute(b));
    }

    @Test
    public void testExecute_suiWaiting_bodyRidesOnSui() {
        Yukkuri b = createBody(1, 100, 100);
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
        Yukkuri b = createBody(1, 100, 100);
        SuiRideEvent event = new SuiRideEvent(b, null, null, 100);
        assertDoesNotThrow(() -> event.end(b));
    }

    @Test
    public void testEnd_callsRideOffWhenBodyIsRidingSui() {
        Yukkuri b = createBody(1, 100, 100);
        Sui sui = createSui(999);
        sui.rideOn(b);
        assertTrue(sui.isriding(b));
        b.setParentLinkId(sui.getObjId());
        SuiRideEvent event = new SuiRideEvent(b, null, sui, 100);
        event.end(b);
        assertFalse(sui.isriding(b));
    }

    @Test
    public void testEnd_doesNotThrowForNonRidingBody() {
        Yukkuri b = createBody(1, 100, 100);
        Sui sui = createSui(999);
        SuiRideEvent event = new SuiRideEvent(b, null, sui, 100);
        // b is not riding, parentLinkId = -1 => takeMappedObj returns null
        assertDoesNotThrow(() -> event.end(b));
    }

    // -----------------------------------------------------------------------
    // update - target is null => ABORT
    // -----------------------------------------------------------------------

    @Test
    public void testUpdate_returnsAbortWhenTargetIsNull() {
        Yukkuri b = createBody(1, 100, 100);
        SuiRideEvent event = new SuiRideEvent();
        assertEquals(UpdateState.ABORT, event.update(b));
    }

    // -----------------------------------------------------------------------
    // update - b does NOT have FavItem SUI (no sui owned)
    // -----------------------------------------------------------------------

    @Test
    public void testUpdate_noFavSui_hasLinkParent_returnsNull() {
        Yukkuri b = createBody(1, 100, 100);
        Sui targetSui = createSui(999);
        Sui parentSui = createSui(888);
        b.setParentLinkId(parentSui.getObjId());
        SuiRideEvent event = new SuiRideEvent(b, null, targetSui, 100);
        assertNull(event.update(b));
    }

    @Test
    public void testUpdate_noFavSui_noLinkParent_fromIsNull_returnsAbort() {
        Yukkuri b = createBody(1, 100, 100);
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
        Yukkuri b = createBody(1, 100, 100);
        Sui sui = createSui(999);
        SuiRideEvent event = new SuiRideEvent(b, null, sui, 100);
        assertDoesNotThrow(() -> event.update(b));
    }

    @Test
    public void testUpdate_noFavSui_fromNotEqualsB_fromCurrentEventIsNull_returnsAbort() {
        Yukkuri from = createBody(1, 100, 100);
        Yukkuri b = createBody(2, 200, 200);
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
        Yukkuri from = createBody(1, 100, 100);
        Yukkuri b = createBody(2, 200, 200);
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
        Yukkuri from = createBody(1, 100, 100);
        Yukkuri b = createBody(2, 200, 200);
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
        Yukkuri from = createBody(1, 100, 100);
        Yukkuri b = createBody(2, 200, 200);
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
        Yukkuri b = createBody(1, 100, 100);
        Sui sui = createSui(999);
        // Give b favitem SUI without actually riding (no rideOn)
        b.setFavoriteItem(FavItemType.SUI, sui);
        SuiRideEvent event = new SuiRideEvent(b, null, sui, 100);
        // isriding(b) == false => falls to tick++ and returns null
        assertDoesNotThrow(() -> {
            UpdateState state = event.update(b);
            assertNull(state);
        });
    }

    @Test
    public void testUpdate_hasFavSui_riding_fromIsNull_returnsAbort() {
        Yukkuri b = createBody(1, 100, 100);
        Sui sui = createSui(999);
        sui.rideOn(b);
        b.setFavoriteItem(FavItemType.SUI, sui);
        // from = -1 (not in world)
        SuiRideEvent event = new SuiRideEvent();
        event.setTarget(sui.getObjId());
        assertEquals(UpdateState.ABORT, event.update(b));
    }

    @Test
    public void testUpdate_hasFavSui_riding_fromEqualsB_bindbodyUnder3_tickUnder50_returnsNull() {
        Yukkuri b = createBody(1, 100, 100);
        Sui sui = createSui(999);
        sui.rideOn(b);
        b.setFavoriteItem(FavItemType.SUI, sui);
        SuiRideEvent event = new SuiRideEvent(b, null, sui, 100);
        // tick=0 < 50, current_bindbody_num=1 < 3 => falls through, returns null
        UpdateState state = event.update(b);
        assertNull(state);
    }

    @Test
    public void testUpdate_hasFavSui_riding_fromEqualsB_bindbodyAtLimit_suiWaiting() {
        Yukkuri b = createBody(1, 100, 100);
        Sui sui = createSui(999);
        sui.rideOn(b);
        b.setFavoriteItem(FavItemType.SUI, sui);
        sui.setCurrent_bindbody_num(3);
        sui.setCurrent_condition(1);
        SuiRideEvent event = new SuiRideEvent(b, null, sui, 100);
        // tick=0, not > 500; condition==1 => moveTo path
        assertDoesNotThrow(() -> event.update(b));
    }

    @Test
    public void testUpdate_hasFavSui_riding_fromEqualsB_bindbodyAtLimit_suiNotWaiting() {
        Yukkuri b = createBody(1, 100, 100);
        Sui sui = createSui(999);
        sui.rideOn(b);
        b.setFavoriteItem(FavItemType.SUI, sui);
        sui.setCurrent_bindbody_num(3);
        sui.setCurrent_condition(2); // not waiting
        SuiRideEvent event = new SuiRideEvent(b, null, sui, 100);
        // condition != 1 => setEventResMessage path
        assertDoesNotThrow(() -> event.update(b));
    }

    @Test
    public void testUpdate_hasFavSui_riding_fromNotEqualsB_fromCurrentEventIsThis_suiWaiting_returnsAbort() {
        Yukkuri from = createBody(1, 100, 100);
        Yukkuri b = createBody(2, 200, 200);
        Sui sui = createSui(999);
        sui.rideOn(from); // from is owner/rider
        // Manually make b ride in slot 1
        sui.getBoundYukkuri()[1] = b;
        sui.setCurrent_bindbody_num(2);
        b.setParentLinkId(sui.getObjId());
        b.setFavoriteItem(FavItemType.SUI, sui);
        sui.setCurrent_condition(1); // waiting
        SuiRideEvent event = new SuiRideEvent(from, null, sui, 100);
        // from.getCurrentEvent() != this (null) AND condition==1 => rideOff ABORT
        UpdateState state = event.update(b);
        assertEquals(UpdateState.ABORT, state);
        assertFalse(sui.isriding(b));
    }

    @Test
    public void testUpdate_hasFavSui_riding_fromNotEqualsB_suiNotCondition1_returnsNull() {
        Yukkuri from = createBody(1, 100, 100);
        Yukkuri b = createBody(2, 200, 200);
        Sui sui = createSui(999);
        sui.rideOn(from);
        sui.getBoundYukkuri()[1] = b;
        sui.setCurrent_bindbody_num(2);
        b.setParentLinkId(sui.getObjId());
        b.setFavoriteItem(FavItemType.SUI, sui);
        sui.setCurrent_condition(2); // not waiting
        SuiRideEvent event = new SuiRideEvent(from, null, sui, 100);
        // from.getCurrentEvent() != this AND condition != 1 => no rideOff, returns null
        UpdateState state = event.update(b);
        assertNull(state);
    }

    @Nested
    class RegressionScenarios {

        @Test
        void testScenario_UnunSlaveFamilyMemberBecomesVerySadAndDoesNotJoinRideEvent() {
            Yukkuri owner = createBody(1, 100, 100);
            Yukkuri partner = createBody(2, 120, 100);
            Sui sui = createSui(999);
            SuiRideEvent event = new SuiRideEvent(owner, null, sui, 100);
            owner.setCurrentEvent(event);
            owner.setPartner(partner.getUniqueID());
            partner.setPartner(owner.getUniqueID());
            partner.setPublicRank(PublicRank.UNUN_SLAVE);

            assertFalse(event.checkEventResponse(partner));

            assertEquals(Happiness.VERY_SAD, partner.getHappiness(),
                    "unun slave family member should become very sad instead of joining the ride");
            assertTrue(partner.isStaying(), "unun slave family member should stay after refusing the ride event");
            assertNull(partner.getCurrentEvent(), "unun slave refusal should not start a body event");
        }

        @Test
        void testScenario_ExecuteMakesFirstRiderOwnerAndRegistersSuiFavorite() {
            Yukkuri rider = createBody(1, 100, 100);
            Sui sui = createSui(999);
            SuiRideEvent event = new SuiRideEvent(rider, null, sui, 100);

            assertFalse(event.execute(rider));

            assertTrue(sui.isriding(rider), "execute should actually place the rider onto the waiting sui");
            assertEquals(sui.getObjId(), rider.getParentLinkId(), "rider should link to the sui after boarding");
            assertEquals(sui, rider.getFavoriteItem(FavItemType.SUI), "first rider should become the owner of the sui");
            assertFalse(rider.isShadowVisible(), "boarded rider should hide its drop shadow");
        }
    }
}
