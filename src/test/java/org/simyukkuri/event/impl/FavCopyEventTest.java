package org.simyukkuri.event.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.simyukkuri.SimYukkuri;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.world.item.Bed;
import org.simyukkuri.enums.FavItemType;
import org.simyukkuri.event.EventTestBase;
import org.simyukkuri.util.WorldTestHelper;

public class FavCopyEventTest extends EventTestBase {

    @Test
    void testSimpleEventAction_ReturnsTrue() {
        Yukkuri parent = createBody(1, 100, 100);
        Yukkuri child = createBody(2, 120, 120);
        WorldTestHelper.setParents(child, -1, parent.getObjId());

        FavCopyEvent event = new FavCopyEvent(parent, child, null, 1);

        // simpleEventAction should execute and return true
        assertTrue(event.simpleEventAction(child));
    }

    @Test
    void testSimpleEventAction_FromIsB_returnsFalse() {
        Yukkuri parent = createBody(1, 100, 100);
        Yukkuri child = createBody(2, 120, 120);

        FavCopyEvent event = new FavCopyEvent(parent, child, null, 1);

        // when b == from, should return false
        assertFalse(event.simpleEventAction(parent));
    }

    @Test
    void testDefaultConstructor_doesNotThrow() {
        assertDoesNotThrow(() -> new FavCopyEvent());
    }

    @Test
    void testCheckEventResponse_returnsFalse() {
        Yukkuri parent = createBody(1, 100, 100);
        Yukkuri child = createBody(2, 120, 120);
        FavCopyEvent event = new FavCopyEvent(parent, child, null, 1);
        assertFalse(event.checkEventResponse(child));
    }

    @Test
    void testStart_doesNotThrow() {
        Yukkuri parent = createBody(1, 100, 100);
        Yukkuri child = createBody(2, 120, 120);
        FavCopyEvent event = new FavCopyEvent(parent, child, null, 1);
        assertDoesNotThrow(() -> event.start(child));
    }

    @Test
    void testExecute_returnsTrue() {
        Yukkuri parent = createBody(1, 100, 100);
        Yukkuri child = createBody(2, 120, 120);
        FavCopyEvent event = new FavCopyEvent(parent, child, null, 1);
        assertTrue(event.execute(child));
    }

    @Test
    void testToString_doesNotThrow() {
        Yukkuri parent = createBody(1, 100, 100);
        Yukkuri child = createBody(2, 120, 120);
        FavCopyEvent event = new FavCopyEvent(parent, child, null, 1);
        assertDoesNotThrow(() -> event.toString());
    }

    // --- from == null → return false ---
    @Test
    void testSimpleEventAction_fromNull_returnsFalse() {
        Yukkuri b = createBody(1, 100, 100);
        FavCopyEvent event = new FavCopyEvent(); // from=-1 → null
        assertFalse(event.simpleEventAction(b));
    }

    // --- b.isParent(from) → true → enters family block ---
    @Test
    void testSimpleEventAction_bIsParentOfFrom_returnsTrue() {
        Yukkuri b = createBody(1, 100, 100);
        Yukkuri from = createBody(2, 110, 110);
        from.setParents(new int[] { b.getUniqueID(), -1 }); // b is father of from
        FavCopyEvent event = new FavCopyEvent(from, null, null, 1);
        assertTrue(event.simpleEventAction(b));
    }

    // --- b.isPartner(from) → true → enters family block ---
    @Test
    void testSimpleEventAction_bIsPartnerOfFrom_returnsTrue() {
        Yukkuri b = createBody(1, 100, 100);
        Yukkuri from = createBody(2, 110, 110);
        b.setPartner(from.getUniqueID());
        from.setPartner(b.getUniqueID());
        FavCopyEvent event = new FavCopyEvent(from, null, null, 1);
        assertTrue(event.simpleEventAction(b));
    }

    // --- both UnunSlave → setFavoriteItem ---
    @Test
    void testSimpleEventAction_bothUnunSlave_returnsTrue() {
        Yukkuri b = createBody(1, 100, 100);
        Yukkuri from = createBody(2, 110, 110);
        b.setPublicRank(org.simyukkuri.enums.PublicRank.UNUN_SLAVE);
        from.setPublicRank(org.simyukkuri.enums.PublicRank.UNUN_SLAVE);
        b.setPartner(from.getUniqueID());
        from.setPartner(b.getUniqueID());
        FavCopyEvent event = new FavCopyEvent(from, null, null, 1);
        assertTrue(event.simpleEventAction(b));
    }

    // --- one UnunSlave, other not → no setFavoriteItem ---
    @Test
    void testSimpleEventAction_oneUnunSlave_returnsTrue() {
        Yukkuri b = createBody(1, 100, 100);
        Yukkuri from = createBody(2, 110, 110);
        b.setPublicRank(org.simyukkuri.enums.PublicRank.UNUN_SLAVE);
        // from stays at default (not UnunSlave)
        b.setPartner(from.getUniqueID());
        from.setPartner(b.getUniqueID());
        FavCopyEvent event = new FavCopyEvent(from, null, null, 1);
        assertTrue(event.simpleEventAction(b));
    }

    @Nested
    class RegressionScenarios {

        @Test
        void testScenario_FamilyCopiesFavoriteBedAcrossEvent() {
            Yukkuri parent = createBody(1, 100, 100);
            Yukkuri child = createBody(2, 120, 120);
            child.setPartner(parent.getUniqueID());
            parent.setPartner(child.getUniqueID());
            Bed bed = new Bed();
            SimYukkuri.world.getCurrentWorldState().getBeds().put(bed.getObjId(), bed);
            parent.setFavoriteItem(FavItemType.BED, bed);
            child.setFavoriteItem(FavItemType.BED, null);

            FavCopyEvent event = new FavCopyEvent(parent, child, null, 1);

            assertTrue(event.simpleEventAction(child));
            assertEquals(bed.getObjId(), child.getFavoriteItem(FavItemType.BED).getObjId());
        }
    }
}
