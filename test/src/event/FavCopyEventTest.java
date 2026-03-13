package src.event;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import src.base.Body;
import src.base.EventTestBase;
import src.util.WorldTestHelper;

public class FavCopyEventTest extends EventTestBase {

    @Test
    void testSimpleEventAction_ReturnsTrue() {
        Body parent = createBody(1, 100, 100);
        Body child = createBody(2, 120, 120);
        WorldTestHelper.setParents(child, -1, parent.getObjId());

        FavCopyEvent event = new FavCopyEvent(parent, child, null, 1);

        // simpleEventAction should execute and return true
        assertTrue(event.simpleEventAction(child));
    }

    @Test
    void testSimpleEventAction_FromIsB_returnsFalse() {
        Body parent = createBody(1, 100, 100);
        Body child = createBody(2, 120, 120);

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
        Body parent = createBody(1, 100, 100);
        Body child = createBody(2, 120, 120);
        FavCopyEvent event = new FavCopyEvent(parent, child, null, 1);
        assertFalse(event.checkEventResponse(child));
    }

    @Test
    void testStart_doesNotThrow() {
        Body parent = createBody(1, 100, 100);
        Body child = createBody(2, 120, 120);
        FavCopyEvent event = new FavCopyEvent(parent, child, null, 1);
        assertDoesNotThrow(() -> event.start(child));
    }

    @Test
    void testExecute_returnsTrue() {
        Body parent = createBody(1, 100, 100);
        Body child = createBody(2, 120, 120);
        FavCopyEvent event = new FavCopyEvent(parent, child, null, 1);
        assertTrue(event.execute(child));
    }

    @Test
    void testToString_doesNotThrow() {
        Body parent = createBody(1, 100, 100);
        Body child = createBody(2, 120, 120);
        FavCopyEvent event = new FavCopyEvent(parent, child, null, 1);
        assertDoesNotThrow(() -> event.toString());
    }

    // --- from == null → return false ---
    @Test
    void testSimpleEventAction_fromNull_returnsFalse() {
        Body b = createBody(1, 100, 100);
        FavCopyEvent event = new FavCopyEvent(); // from=-1 → null
        assertFalse(event.simpleEventAction(b));
    }

    // --- b.isParent(from) → true → enters family block ---
    @Test
    void testSimpleEventAction_bIsParentOfFrom_returnsTrue() {
        Body b = createBody(1, 100, 100);
        Body from = createBody(2, 110, 110);
        from.setParents(new int[]{b.getUniqueID(), -1}); // b is father of from
        FavCopyEvent event = new FavCopyEvent(from, null, null, 1);
        assertTrue(event.simpleEventAction(b));
    }

    // --- b.isPartner(from) → true → enters family block ---
    @Test
    void testSimpleEventAction_bIsPartnerOfFrom_returnsTrue() {
        Body b = createBody(1, 100, 100);
        Body from = createBody(2, 110, 110);
        b.setPartner(from.getUniqueID());
        from.setPartner(b.getUniqueID());
        FavCopyEvent event = new FavCopyEvent(from, null, null, 1);
        assertTrue(event.simpleEventAction(b));
    }

    // --- both UnunSlave → setFavItem ---
    @Test
    void testSimpleEventAction_bothUnunSlave_returnsTrue() {
        Body b = createBody(1, 100, 100);
        Body from = createBody(2, 110, 110);
        b.setPublicRank(src.enums.PublicRank.UnunSlave);
        from.setPublicRank(src.enums.PublicRank.UnunSlave);
        b.setPartner(from.getUniqueID());
        from.setPartner(b.getUniqueID());
        FavCopyEvent event = new FavCopyEvent(from, null, null, 1);
        assertTrue(event.simpleEventAction(b));
    }

    // --- one UnunSlave, other not → no setFavItem ---
    @Test
    void testSimpleEventAction_oneUnunSlave_returnsTrue() {
        Body b = createBody(1, 100, 100);
        Body from = createBody(2, 110, 110);
        b.setPublicRank(src.enums.PublicRank.UnunSlave);
        // from stays at default (not UnunSlave)
        b.setPartner(from.getUniqueID());
        from.setPartner(b.getUniqueID());
        FavCopyEvent event = new FavCopyEvent(from, null, null, 1);
        assertTrue(event.simpleEventAction(b));
    }
}
