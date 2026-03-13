package src.event;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import src.base.Body;
import src.base.EventPacket.UpdateState;
import src.base.EventTestBase;
import src.util.WorldTestHelper;

public class BreedEventTest extends EventTestBase {

    @Test
    void testCheckEventResponse_ParentParticipates() {
        Body parent = createBody(1, 100, 100);
        Body child = createBody(2, 120, 120);

        // Set parent/child relationship
        WorldTestHelper.setParents(child, -1, parent.getUniqueID());

        BreedEvent event = new BreedEvent(parent, child, null, 10);

        // Child should respond to parent's breed event
        // Note: Logic allows response if child is related to 'from'
        assertTrue(event.checkEventResponse(child));
    }

    @Test
    void testCheckEventResponse_StrangerDoesNotParticipate() {
        Body parent = createBody(1, 100, 100);
        Body stranger = createBody(2, 120, 120);

        // No relationship

        BreedEvent event = new BreedEvent(parent, null, null, 10);

        assertFalse(event.checkEventResponse(stranger));
    }

    @Test
    void testDefaultConstructor_doesNotThrow() {
        assertDoesNotThrow(() -> new BreedEvent());
    }

    @Test
    void testToString_doesNotThrow() {
        Body parent = createBody(1, 100, 100);
        Body child = createBody(2, 120, 120);
        BreedEvent event = new BreedEvent(parent, child, null, 10);
        assertDoesNotThrow(() -> event.toString());
    }

    @Test
    void testStart_fromNull_doesNotThrow() {
        // from is not registered in body map → getBodyInstance returns null
        Body child = createBody(2, 120, 120);
        // Create event with an unregistered body as from
        Body unregistered = WorldTestHelper.createBody();
        BreedEvent event = new BreedEvent(unregistered, child, null, 10);
        // from is not in world map, so getBodyInstance returns null → no moveToEvent
        assertDoesNotThrow(() -> event.start(child));
    }

    @Test
    void testStart_fromExists_doesNotThrow() {
        Body parent = createBody(1, 100, 100);
        Body child = createBody(2, 120, 120);
        BreedEvent event = new BreedEvent(parent, child, null, 10);
        assertDoesNotThrow(() -> event.start(child));
    }

    @Test
    void testUpdate_fromNull_returnsAbort() {
        // from not registered → getBodyInstance returns null → ABORT
        Body child = createBody(2, 120, 120);
        Body unregistered = WorldTestHelper.createBody();
        BreedEvent event = new BreedEvent(unregistered, child, null, 10);
        assertEquals(UpdateState.ABORT, event.update(child));
    }

    @Test
    void testUpdate_fromExists_doesNotThrow() {
        Body parent = createBody(1, 100, 100);
        Body child = createBody(2, 500, 500); // far from parent
        BreedEvent event = new BreedEvent(parent, child, null, 10);
        assertDoesNotThrow(() -> event.update(child));
    }

    @Test
    void testExecute_fromNull_returnsTrue() {
        // from not registered → getBodyInstance returns null → true
        Body child = createBody(2, 120, 120);
        Body unregistered = WorldTestHelper.createBody();
        BreedEvent event = new BreedEvent(unregistered, child, null, 10);
        assertTrue(event.execute(child));
    }

    @Test
    void testExecute_fromExists_doesNotThrow() {
        Body parent = createBody(1, 100, 100);
        Body child = createBody(2, 120, 120);
        BreedEvent event = new BreedEvent(parent, child, null, 10);
        assertDoesNotThrow(() -> event.execute(child));
    }

    // --- checkEventResponse extra paths ---

    @Test
    void testCheckEventResponse_bNearToBirth_returnsFalse() {
        Body parent = createBody(1, 100, 100);
        Body b = createBody(2, 120, 120);
        // make b nearToBirth = true
        b.setHasBaby(true);
        b.setPregnantPeriod(Integer.MAX_VALUE / 2);
        BreedEvent event = new BreedEvent(parent, null, null, 10);
        assertFalse(event.checkEventResponse(b));
    }

    @Test
    void testCheckEventResponse_bIsUnBirth_returnsFalse() {
        Body parent = createBody(1, 100, 100);
        Body b = createBody(2, 120, 120);
        b.setUnBirth(true);
        BreedEvent event = new BreedEvent(parent, null, null, 10);
        assertFalse(event.checkEventResponse(b));
    }

    @Test
    void testCheckEventResponse_bIsRaperExciting_returnsFalse() {
        Body parent = createBody(1, 100, 100);
        Body b = createBody(2, 120, 120);
        b.setRapist(true);
        b.setExciting(true);
        BreedEvent event = new BreedEvent(parent, null, null, 10);
        assertFalse(event.checkEventResponse(b));
    }

    @Test
    void testCheckEventResponse_differentPublicRank_returnsFalse() {
        Body parent = createBody(1, 100, 100);
        Body b = createBody(2, 120, 120);
        b.setPublicRank(src.enums.PublicRank.UnunSlave);
        BreedEvent event = new BreedEvent(parent, null, null, 10);
        assertFalse(event.checkEventResponse(b));
    }

    @Test
    void testCheckEventResponse_bBuried_returnsFalse() {
        Body parent = createBody(1, 100, 100);
        Body b = createBody(2, 120, 120);
        b.setBaryState(src.enums.BaryInUGState.HALF);
        BreedEvent event = new BreedEvent(parent, null, null, 10);
        assertFalse(event.checkEventResponse(b));
    }

    @Test
    void testCheckEventResponse_fromNoOkazariFoolB_returnsFalse() {
        Body parent = createBody(1, 100, 100);
        Body b = createBody(2, 120, 120);
        parent.setOkazari(null); // from has no okazari
        b.setIntelligence(src.enums.Intelligence.FOOL);
        BreedEvent event = new BreedEvent(parent, null, null, 10);
        assertFalse(event.checkEventResponse(b));
    }

    @Test
    void testCheckEventResponse_fromIsPartnerOfB_returnsTrue() {
        Body parent = createBody(1, 100, 100);
        Body b = createBody(2, 120, 120);
        b.setPartner(parent.getUniqueID());
        parent.setPartner(b.getUniqueID());
        BreedEvent event = new BreedEvent(parent, null, null, 10);
        assertTrue(event.checkEventResponse(b));
    }

    @Test
    void testCheckEventResponse_bIsParentOfFrom_returnsTrue() {
        Body parent = createBody(1, 100, 100);
        Body b = createBody(2, 120, 120);
        parent.setParents(new int[]{b.getUniqueID(), -1}); // b is father of parent
        BreedEvent event = new BreedEvent(parent, null, null, 10);
        assertTrue(event.checkEventResponse(b));
    }

    // --- update extra paths ---

    @Test
    void testUpdate_bNearToBirth_returnsForceExec() {
        Body from = createBody(1, 100, 100);
        Body b = createBody(2, 120, 120);
        b.setHasBaby(true);
        b.setPregnantPeriod(Integer.MAX_VALUE / 2);
        BreedEvent event = new BreedEvent(from, null, null, 10);
        assertEquals(src.base.EventPacket.UpdateState.FORCE_EXEC, event.update(b));
    }

    @Test
    void testUpdate_closeDistance_returnsForceExec() {
        Body from = createBody(1, 100, 100);
        Body b = createBody(2, 105, 105); // very close → distance < 20000
        from.setHasBaby(true); // nearToBirth needs this
        from.setPregnantPeriod(Integer.MAX_VALUE / 2);
        BreedEvent event = new BreedEvent(from, null, null, 10);
        assertEquals(src.base.EventPacket.UpdateState.FORCE_EXEC, event.update(b));
    }

    @Test
    void testUpdate_fromDead_returnsAbort() {
        Body from = createBody(1, 100, 100);
        Body b = createBody(2, 500, 500); // far
        from.setDead(true);
        BreedEvent event = new BreedEvent(from, null, null, 10);
        assertEquals(src.base.EventPacket.UpdateState.ABORT, event.update(b));
    }

    // --- execute extra paths ---

    @Test
    void testExecute_bNearToBirth_returnsTrue() {
        Body from = createBody(1, 100, 100);
        Body b = createBody(2, 120, 120);
        b.setHasBaby(true);
        b.setPregnantPeriod(Integer.MAX_VALUE / 2);
        BreedEvent event = new BreedEvent(from, null, null, 10);
        assertTrue(event.execute(b));
    }

    @Test
    void testExecute_bIsNYD_returnsFalse() {
        Body from = createBody(1, 100, 100);
        Body b = createBody(2, 120, 120);
        b.seteCoreAnkoState(src.enums.CoreAnkoState.NonYukkuriDisease);
        BreedEvent event = new BreedEvent(from, null, null, 10);
        assertFalse(event.execute(b));
    }

    @Test
    void testExecute_fromIsBirth_returnsFalse() {
        Body from = createBody(1, 100, 100);
        Body b = createBody(2, 120, 120);
        from.setBirth(true);
        BreedEvent event = new BreedEvent(from, null, null, 10);
        assertFalse(event.execute(b));
    }

    @Test
    void testExecute_fromHasPants_returnsTrue() {
        Body from = createBody(1, 100, 100);
        Body b = createBody(2, 120, 120);
        from.setHasPants(true); // surprise message
        BreedEvent event = new BreedEvent(from, null, null, 10);
        assertTrue(event.execute(b));
    }

    @Test
    void testExecute_fromHasBabyOrStalk_returnsFalse() {
        Body from = createBody(1, 100, 100);
        Body b = createBody(2, 120, 120);
        from.setHasBaby(true); // still pregnant → else branch → return false
        BreedEvent event = new BreedEvent(from, null, null, 10);
        assertFalse(event.execute(b));
    }
}
