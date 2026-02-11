package src.base;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import src.SimYukkuri;
import src.draw.World;
import src.enums.AgeState;
import src.system.Sprite;
import src.yukkuri.Reimu;

import org.junit.jupiter.api.Nested;

import src.base.EventPacket.EventPriority;
import src.base.EventPacket.UpdateState;
import src.draw.Translate;
import src.enums.Attitude;
import src.enums.BaryInUGState;
import src.enums.Happiness;
import src.enums.PublicRank;
import src.event.BreedEvent;
import src.event.CutPenipeniEvent;
import src.event.EatBodyEvent;
import src.event.FavCopyEvent;
import src.event.GetTrashOkazariEvent;
import src.event.ProposeEvent;
import src.event.RevengeAttackEvent;

public class EventPacketTest {

    static class TestEventPacket extends EventPacket {
        private static final long serialVersionUID = 1L;

        public TestEventPacket() {
            super();
        }

        public TestEventPacket(Body f, Body t, Obj tgt, int cnt) {
            super(f, t, tgt, cnt);
        }

        @Override
        public boolean checkEventResponse(Body b) {
            return true;
        }

        @Override
        public void start(Body b) {
        }

        @Override
        public boolean execute(Body b) {
            return true;
        }
    }

    @BeforeEach
    public void setUp() {
        SimYukkuri.world = new World();
        Translate.setMapSize(1000, 1000, 500);
    }

    // --- コンストラクタ ---

    @Test
    public void testDefaultConstructor() {
        TestEventPacket packet = new TestEventPacket();
        assertEquals(-1, packet.getFrom());
        assertEquals(-1, packet.getTo());
        assertEquals(EventPacket.EventPriority.LOW, packet.getPriority());
    }

    @Test
    public void testConstructorWithBothBodies() {
        Body from = createBody();
        Body to = createBody();
        Obj target = new Obj();
        target.objId = 42;

        TestEventPacket packet = new TestEventPacket(from, to, target, 10);

        assertEquals(from.getUniqueID(), packet.getFrom());
        assertEquals(to.getUniqueID(), packet.getTo());
        assertEquals(42, packet.getTarget());
        assertEquals(10, packet.getCount());
    }

    @Test
    public void testConstructorWithNullTo() {
        Body from = createBody();

        TestEventPacket packet = new TestEventPacket(from, null, null, 5);

        assertEquals(from.getUniqueID(), packet.getFrom());
        assertEquals(-1, packet.getTo()); // t == null → -1
        assertEquals(-1, packet.getTarget()); // tgt == null → -1
    }

    @Test
    public void testConstructorWithNullFrom() {
        TestEventPacket packet = new TestEventPacket(null, null, null, 3);

        assertEquals(-1, packet.getFrom()); // setFrom(null) → -1
        assertEquals(-1, packet.getTo());
    }

    // --- countDown ---

    @Test
    public void testCountDown() {
        EventPacket packet = new TestEventPacket();
        packet.setCount(2);

        assertFalse(packet.countDown()); // 2 -> 1
        assertEquals(1, packet.getCount());

        assertTrue(packet.countDown()); // 1 -> 0
        assertEquals(0, packet.getCount());

        assertFalse(packet.countDown()); // 0 -> -1
        assertEquals(-1, packet.getCount());
    }

    // --- setFrom(Body) ---

    @Test
    public void testSetFromWithBody() {
        EventPacket packet = new TestEventPacket();
        Body b = createBody();

        packet.setFrom(b);
        assertEquals(b.getUniqueID(), packet.getFrom());
    }

    @Test
    public void testSetFromWithNull() {
        EventPacket packet = new TestEventPacket();
        packet.setFrom(10);

        packet.setFrom((Body) null);
        assertEquals(-1, packet.getFrom());
    }

    // --- setTo(Body) ---

    @Test
    public void testSetToWithBody() {
        EventPacket packet = new TestEventPacket();
        Body b = createBody();

        packet.setTo(b);
        assertEquals(b.getUniqueID(), packet.getTo());
    }

    // --- setTarget(Obj) ---

    @Test
    public void testSetTargetWithObj() {
        EventPacket packet = new TestEventPacket();
        Obj obj = new Obj();
        obj.objId = 99;

        packet.setTarget(obj);
        assertEquals(99, packet.getTarget());
    }

    // --- simpleEventAction ---

    @Test
    public void testSimpleEventActionReturnsFalse() {
        EventPacket packet = new TestEventPacket();
        assertFalse(packet.simpleEventAction(null));
    }

    // --- update ---

    @Test
    public void testUpdateReturnsNull() {
        EventPacket packet = new TestEventPacket();
        assertNull(packet.update(null));
    }

    // --- end ---

    @Test
    public void testEndDoesNotThrow() {
        EventPacket packet = new TestEventPacket();
        packet.end(null); // should not throw
    }

    // --- priorities ---

    @Test
    public void testPriorities() {
        EventPacket packet = new TestEventPacket();
        assertEquals(EventPacket.EventPriority.LOW, packet.getPriority());

        packet.setPriority(EventPacket.EventPriority.HIGH);
        assertEquals(EventPacket.EventPriority.HIGH, packet.getPriority());

        packet.setPriority(EventPacket.EventPriority.MIDDLE);
        assertEquals(EventPacket.EventPriority.MIDDLE, packet.getPriority());
    }

    // --- coordinate setters ---

    @Test
    public void testCoordinateSetters() {
        EventPacket packet = new TestEventPacket();

        packet.setToX(100);
        assertEquals(100, packet.getToX());

        packet.setToY(200);
        assertEquals(200, packet.getToY());

        packet.setToZ(300);
        assertEquals(300, packet.getToZ());
    }

    // --- int setters ---

    @Test
    public void testIntSetters() {
        EventPacket packet = new TestEventPacket();

        packet.setFrom(10);
        packet.setTo(20);
        packet.setTarget(30);
        packet.setCount(40);

        assertEquals(10, packet.getFrom());
        assertEquals(20, packet.getTo());
        assertEquals(30, packet.getTarget());
        assertEquals(40, packet.getCount());
    }

    // --- EventPriority enum ---

    @Test
    public void testEventPriorityEnum() {
        assertEquals(3, EventPacket.EventPriority.values().length);
        assertEquals(EventPacket.EventPriority.LOW, EventPacket.EventPriority.valueOf("LOW"));
        assertEquals(EventPacket.EventPriority.MIDDLE, EventPacket.EventPriority.valueOf("MIDDLE"));
        assertEquals(EventPacket.EventPriority.HIGH, EventPacket.EventPriority.valueOf("HIGH"));
    }

    // --- UpdateState enum ---

    @Test
    public void testUpdateStateEnum() {
        assertEquals(2, EventPacket.UpdateState.values().length);
        assertEquals(EventPacket.UpdateState.FORCE_EXEC, EventPacket.UpdateState.valueOf("FORCE_EXEC"));
        assertEquals(EventPacket.UpdateState.ABORT, EventPacket.UpdateState.valueOf("ABORT"));
    }

    // ========== CutPenipeniEvent Tests ==========

    @Nested
    class CutPenipeniEventTests {
        @Test
        public void testDefaultConstructor() {
            CutPenipeniEvent event = new CutPenipeniEvent();
            assertEquals(-1, event.getFrom());
            assertEquals(-1, event.getTo());
        }

        @Test
        public void testParameterizedConstructor() {
            Body from = createBody();
            Body to = createBody();
            CutPenipeniEvent event = new CutPenipeniEvent(from, to, null, 10);
            assertEquals(from.getUniqueID(), event.getFrom());
            assertEquals(to.getUniqueID(), event.getTo());
            assertEquals(10, event.getCount());
        }

        @Test
        public void testCheckEventResponse_setsPriorityHigh() {
            Body from = createBody();
            CutPenipeniEvent event = new CutPenipeniEvent(from, null, null, 10);
            event.checkEventResponse(from);
            assertEquals(EventPriority.HIGH, event.getPriority());
        }

        @Test
        public void testCheckEventResponse_trueForFromBody() {
            Body from = createBody();
            CutPenipeniEvent event = new CutPenipeniEvent(from, null, null, 10);
            assertTrue(event.checkEventResponse(from));
        }

        @Test
        public void testCheckEventResponse_falseForOtherBody() {
            Body from = createBody();
            Body other = createBody();
            CutPenipeniEvent event = new CutPenipeniEvent(from, null, null, 10);
            assertFalse(event.checkEventResponse(other));
        }

        @Test
        public void testUpdate_tick0_setsPenipeniCutAndLockmove() {
            Body b = createBody();
            CutPenipeniEvent event = new CutPenipeniEvent(b, null, null, 10);
            event.update(b);
            assertTrue(b.isbPenipeniCutted());
            assertTrue(b.isLockmove());
        }

        @Test
        public void testEnd_setsExpectedState() {
            Body b = createBody();
            CutPenipeniEvent event = new CutPenipeniEvent(b, null, null, 10);
            b.setLockmove(true);
            event.end(b);
            assertTrue(b.isbPenipeniCutted());
            assertEquals(Happiness.VERY_SAD, b.getHappiness());
            assertFalse(b.isLockmove());
        }

        @Test
        public void testExecute_returnsTrue() {
            Body b = createBody();
            CutPenipeniEvent event = new CutPenipeniEvent(b, null, null, 10);
            assertTrue(event.execute(b));
        }
    }

    // ========== EatBodyEvent Tests ==========

    @Nested
    class EatBodyEventTests {
        @Test
        public void testDefaultConstructor() {
            EatBodyEvent event = new EatBodyEvent();
            assertEquals(-1, event.getFrom());
            assertEquals(-1, event.getTo());
        }

        @Test
        public void testParameterizedConstructor() {
            Body from = createBody();
            Body to = createBody();
            EatBodyEvent event = new EatBodyEvent(from, to, null, 30);
            assertEquals(from.getUniqueID(), event.getFrom());
            assertEquals(to.getUniqueID(), event.getTo());
            assertEquals(30, event.getCount());
        }

        @Test
        public void testCheckEventResponse_trueWhenFromMatches() {
            Body b = createBody();
            EatBodyEvent event = new EatBodyEvent(b, null, null, 30);
            assertTrue(event.checkEventResponse(b));
        }

        @Test
        public void testCheckEventResponse_falseWhenNotFrom() {
            Body from = createBody();
            Body other = createBody();
            EatBodyEvent event = new EatBodyEvent(from, null, null, 30);
            assertFalse(event.checkEventResponse(other));
        }

        @Test
        public void testCheckEventResponse_falseWhenSuperShithead() {
            Body b = createBody();
            b.setAttitude(Attitude.SUPER_SHITHEAD);
            EatBodyEvent event = new EatBodyEvent(b, null, null, 30);
            assertFalse(event.checkEventResponse(b));
        }

        @Test
        public void testCheckEventResponse_falseWhenDead() {
            Body b = createBody();
            b.setDead(true);
            EatBodyEvent event = new EatBodyEvent(b, null, null, 30);
            assertFalse(event.checkEventResponse(b));
        }

        @Test
        public void testEnd_clearsLockmove() {
            Body b = createBody();
            b.setLockmove(true);
            EatBodyEvent event = new EatBodyEvent(b, null, null, 30);
            event.end(b);
            assertFalse(b.isLockmove());
        }
    }

    // ========== BreedEvent Tests ==========

    @Nested
    class BreedEventTests {
        @Test
        public void testDefaultConstructor() {
            BreedEvent event = new BreedEvent();
            assertEquals(-1, event.getFrom());
            assertEquals(-1, event.getTo());
        }

        @Test
        public void testParameterizedConstructor() {
            Body from = createBody();
            Body to = createBody();
            BreedEvent event = new BreedEvent(from, to, null, 2);
            assertEquals(from.getUniqueID(), event.getFrom());
            assertEquals(to.getUniqueID(), event.getTo());
            assertEquals(2, event.getCount());
        }

        @Test
        public void testCheckEventResponse_setsPriorityMiddle() {
            Body from = createBody();
            BreedEvent event = new BreedEvent(from, null, null, 2);
            event.checkEventResponse(from);
            assertEquals(EventPriority.MIDDLE, event.getPriority());
        }

        @Test
        public void testCheckEventResponse_falseWhenFromNull() {
            Body b = createBody();
            BreedEvent event = new BreedEvent();
            assertFalse(event.checkEventResponse(b));
        }

        @Test
        public void testCheckEventResponse_falseWhenFromEqualsB() {
            Body b = createBody();
            BreedEvent event = new BreedEvent(b, null, null, 2);
            assertFalse(event.checkEventResponse(b));
        }

        @Test
        public void testCheckEventResponse_falseWhenDead() {
            Body from = createBody();
            Body b = createBody();
            b.setDead(true);
            BreedEvent event = new BreedEvent(from, null, null, 2);
            assertFalse(event.checkEventResponse(b));
        }

        @Test
        public void testCheckEventResponse_falseWhenBaryStateNotNone() {
            Body from = createBody();
            Body b = createBody();
            b.setBaryState(BaryInUGState.HALF);
            BreedEvent event = new BreedEvent(from, null, null, 2);
            assertFalse(event.checkEventResponse(b));
        }

        @Test
        public void testCheckEventResponse_falseWhenPublicRankMismatch() {
            Body from = createBody();
            Body b = createBody();
            b.setPublicRank(PublicRank.UnunSlave);
            BreedEvent event = new BreedEvent(from, null, null, 2);
            assertFalse(event.checkEventResponse(b));
        }

        @Test
        public void testCheckEventResponse_trueWhenPartner() {
            Body from = createBody();
            Body b = createBody();
            b.setPartner(from.getUniqueID());
            BreedEvent event = new BreedEvent(from, null, null, 2);
            assertTrue(event.checkEventResponse(b));
        }

        @Test
        public void testUpdate_abortWhenFromNull() {
            Body b = createBody();
            BreedEvent event = new BreedEvent();
            assertEquals(UpdateState.ABORT, event.update(b));
        }
    }

    // ========== ProposeEvent Tests ==========

    @Nested
    class ProposeEventTests {
        @Test
        public void testDefaultConstructor() {
            ProposeEvent event = new ProposeEvent();
            assertEquals(EventPriority.LOW, event.getPriority());
        }

        @Test
        public void testParameterizedConstructor_setsPriorityHigh() {
            Body from = createBody();
            Body to = createBody();
            ProposeEvent event = new ProposeEvent(from, to, null, 1);
            assertEquals(EventPriority.HIGH, event.getPriority());
        }

        @Test
        public void testCheckEventResponse_trueForFrom() {
            Body from = createBody();
            Body to = createBody();
            ProposeEvent event = new ProposeEvent(from, to, null, 1);
            assertTrue(event.checkEventResponse(from));
        }

        @Test
        public void testCheckEventResponse_trueForTo() {
            Body from = createBody();
            Body to = createBody();
            ProposeEvent event = new ProposeEvent(from, to, null, 1);
            assertTrue(event.checkEventResponse(to));
        }

        @Test
        public void testCheckEventResponse_falseForOther() {
            Body from = createBody();
            Body to = createBody();
            Body other = createBody();
            ProposeEvent event = new ProposeEvent(from, to, null, 1);
            assertFalse(event.checkEventResponse(other));
        }

        @Test
        public void testAcceptPropose_trueWhenEligible() {
            Body f = createBodyWithOkazari();
            Body t = createBody();
            ProposeEvent event = new ProposeEvent(f, t, null, 1);
            assertTrue(event.acceptPropose(f, t));
        }

        @Test
        public void testAcceptPropose_falseWhenToHasPartner() {
            Body f = createBodyWithOkazari();
            Body t = createBody();
            Body partner = createBody();
            t.setPartner(partner.getUniqueID());
            ProposeEvent event = new ProposeEvent(f, t, null, 1);
            assertFalse(event.acceptPropose(f, t));
        }

        @Test
        public void testAcceptPropose_falseWhenFromHasDisorder() {
            Body f = createBody();
            f.setOkazari(null); // remove okazari → hasDisorder=true
            Body t = createBody();
            ProposeEvent event = new ProposeEvent(f, t, null, 1);
            assertFalse(event.acceptPropose(f, t));
        }

        @Test
        public void testAcceptPropose_falseWhenFromHasBabyOrStalk() {
            Body f = createBodyWithOkazari();
            f.setHasBaby(true);
            Body t = createBody();
            ProposeEvent event = new ProposeEvent(f, t, null, 1);
            assertFalse(event.acceptPropose(f, t));
        }
    }

    // ========== RevengeAttackEvent Tests ==========

    @Nested
    class RevengeAttackEventTests {
        @Test
        public void testDefaultConstructor() {
            RevengeAttackEvent event = new RevengeAttackEvent();
            assertEquals(-1, event.getFrom());
            assertEquals(-1, event.getTo());
        }

        @Test
        public void testCheckEventResponse_alwaysTrue() {
            Body b = createBody();
            RevengeAttackEvent event = new RevengeAttackEvent();
            assertTrue(event.checkEventResponse(b));
        }

        @Test
        public void testCheckEventResponse_setsPriorityHigh() {
            Body b = createBody();
            RevengeAttackEvent event = new RevengeAttackEvent();
            event.checkEventResponse(b);
            assertEquals(EventPriority.HIGH, event.getPriority());
        }

        @Test
        public void testStart_clearsActionFlags() {
            Body b = createBody();
            b.setToFood(true);
            RevengeAttackEvent event = new RevengeAttackEvent(b, null, null, 1);
            event.start(b);
            assertFalse(b.isToFood());
            assertFalse(b.isToBed());
            assertFalse(b.isToShit());
            assertFalse(b.isToSteal());
            assertFalse(b.isToSukkiri());
            assertTrue(b.isToTakeout());
        }

        @Test
        public void testUpdate_abortWhenToNull() {
            Body b = createBody();
            RevengeAttackEvent event = new RevengeAttackEvent(b, null, null, 1);
            assertEquals(UpdateState.ABORT, event.update(b));
        }

        @Test
        public void testUpdate_abortWhenToRemoved() {
            Body b = createBody();
            Body to = createBody();
            to.setRemoved(true);
            RevengeAttackEvent event = new RevengeAttackEvent(b, to, null, 1);
            assertEquals(UpdateState.ABORT, event.update(b));
        }
    }

    // ========== FavCopyEvent Tests ==========

    @Nested
    class FavCopyEventTests {
        @Test
        public void testDefaultConstructor() {
            FavCopyEvent event = new FavCopyEvent();
            assertEquals(-1, event.getFrom());
            assertEquals(-1, event.getTo());
        }

        @Test
        public void testCheckEventResponse_alwaysFalse() {
            Body b = createBody();
            FavCopyEvent event = new FavCopyEvent();
            assertFalse(event.checkEventResponse(b));
        }

        @Test
        public void testSimpleEventAction_falseWhenFromIsB() {
            Body b = createBody();
            FavCopyEvent event = new FavCopyEvent(b, null, null, 1);
            assertFalse(event.simpleEventAction(b));
        }

        @Test
        public void testSimpleEventAction_falseWhenFromIsNull() {
            Body b = createBody();
            FavCopyEvent event = new FavCopyEvent();
            assertFalse(event.simpleEventAction(b));
        }

        @Test
        public void testExecute_returnsTrue() {
            Body b = createBody();
            FavCopyEvent event = new FavCopyEvent();
            assertTrue(event.execute(b));
        }
    }

    // ========== GetTrashOkazariEvent Tests ==========

    @Nested
    class GetTrashOkazariEventTests {
        @Test
        public void testDefaultConstructor() {
            GetTrashOkazariEvent event = new GetTrashOkazariEvent();
            assertEquals(-1, event.getFrom());
            assertEquals(-1, event.getTo());
        }

        @Test
        public void testCheckEventResponse_trueAndSetsPriorityMiddle() {
            Body b = createBody();
            GetTrashOkazariEvent event = new GetTrashOkazariEvent();
            assertTrue(event.checkEventResponse(b));
            assertEquals(EventPriority.MIDDLE, event.getPriority());
        }
    }

    // ========== Helper methods ==========

    private static Body createBodyWithOkazari() {
        Body b = createBody();
        b.setOkazari(new Okazari());
        return b;
    }

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
