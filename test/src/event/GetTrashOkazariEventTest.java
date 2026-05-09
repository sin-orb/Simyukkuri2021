package src.event;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import src.SimYukkuri;
import src.base.Yukkuri;
import src.event.EventPacket;
import src.base.EventTestBase;
import src.base.Entity;
import src.item.GarbageChute;
import src.item.Stone;
import src.item.Trash;

public class GetTrashOkazariEventTest extends EventTestBase {

    @Test
    void testCheckEventResponse_AlwaysReturnsTrue() {
        Yukkuri body = createBody(1, 100, 100);
        Entity trash = new GarbageChute(120, 120, 0);
        trash.setObjId(10);

        GetTrashOkazariEvent event = new GetTrashOkazariEvent(body, null, trash, 1);

        assertTrue(event.checkEventResponse(body));
    }

    @Test
    void testDefaultConstructor() {
        GetTrashOkazariEvent event = new GetTrashOkazariEvent();
        assertNotNull(event);
    }

    @Test
    void testCheckEventResponsePriorityIsMiddle() {
        Yukkuri body = createBody(1, 100, 100);
        Entity trash = new Stone(120, 120, 0);

        GetTrashOkazariEvent event = new GetTrashOkazariEvent(body, null, trash, 1);
        event.checkEventResponse(body);
        assertTrue(event.getPriority() == EventPacket.EventPriority.MIDDLE);
    }

    @Test
    void testStartDoesNotThrow() {
        Yukkuri body = createBody(1, 100, 100);
        Stone stone = new Stone(120, 120, 0);
        // takeMappedObj(stoneId)がStoneを返すようにワールドに登録
        SimYukkuri.world.getCurrentMap().getStone().put(stone.getObjId(), stone);

        GetTrashOkazariEvent event = new GetTrashOkazariEvent(body, null, stone, 1);
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> event.start(body));
    }

    @Test
    void testUpdateWithRemovedTargetAbortsEarly() {
        Yukkuri body = createBody(1, 100, 100);
        Stone stone = new Stone(120, 120, 0);
        SimYukkuri.world.getCurrentMap().getStone().put(stone.getObjId(), stone);
        stone.remove();

        GetTrashOkazariEvent event = new GetTrashOkazariEvent(body, null, stone, 1);
        EventPacket.UpdateState state = event.update(body);
        assertTrue(state == EventPacket.UpdateState.ABORT);
    }

    @Test
    void testExecuteWithRemovedTargetReturnsTrue() {
        Yukkuri body = createBody(1, 100, 100);
        Stone stone = new Stone(120, 120, 0);
        SimYukkuri.world.getCurrentMap().getStone().put(stone.getObjId(), stone);
        stone.remove();

        GetTrashOkazariEvent event = new GetTrashOkazariEvent(body, null, stone, 1);
        boolean done = event.execute(body);
        assertTrue(done);
    }

    @Test
    void testUpdateWithNonRemovedAndNoOkazari() {
        Yukkuri body = createBody(1, 100, 100);
        Stone stone = new Stone(120, 120, 0);
        SimYukkuri.world.getCurrentMap().getStone().put(stone.getObjId(), stone);

        GetTrashOkazariEvent event = new GetTrashOkazariEvent(body, null, stone, 1);
        // not removed, no okazari → moveToEvent呼び出し後 null を返す
        EventPacket.UpdateState state = event.update(body);
        // null (継続) か ABORT が返る（どちらも許容）
        assertTrue(state == null || state == EventPacket.UpdateState.ABORT);
    }

    @Test
    void testToString_doesNotThrow() {
        Yukkuri body = createBody(1, 100, 100);
        Stone stone = new Stone(120, 120, 0);
        GetTrashOkazariEvent event = new GetTrashOkazariEvent(body, null, stone, 1);
        assertDoesNotThrow(() -> event.toString());
    }

    // --- update: b.hasOkazari() = true → ABORT ---
    @Test
    void testUpdate_bodyHasOkazari_returnsAbort() {
        Yukkuri body = createBody(1, 100, 100);
        Stone stone = new Stone(120, 120, 0);
        SimYukkuri.world.getCurrentMap().getStone().put(stone.getObjId(), stone);
        // body already has an okazari (set in Yukkuri constructor: setOkazari(new Okazari(this, OkazariType.DEFAULT)))
        // so hasOkazari() = true
        GetTrashOkazariEvent event = new GetTrashOkazariEvent(body, null, stone, 1);
        EventPacket.UpdateState state = event.update(body);
        assertEquals(EventPacket.UpdateState.ABORT, state);
    }

    @Nested
    class RegressionScenarios {

        @Test
        void testScenario_BareBodyKeepsChasingTrashUntilExecution() {
            Yukkuri body = createBody(1, 100, 100);
            body.setOkazari(null);
            Stone stone = new Stone(120, 120, 0);
            SimYukkuri.world.getCurrentMap().getStone().put(stone.getObjId(), stone);

            GetTrashOkazariEvent event = new GetTrashOkazariEvent(body, null, stone, 1);

            assertFalse(body.hasOkazari());
            assertNull(event.update(body));
        }
    }

}
