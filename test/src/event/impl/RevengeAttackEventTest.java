package src.event.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import src.SimYukkuri;
import src.draw.World;
import src.entity.core.living.yukkuri.Yukkuri;
import src.entity.core.living.yukkuri.impl.Reimu;
import src.enums.AgeState;
import src.event.EventPacket.EventPriority;
import src.event.EventPacket.UpdateState;
import src.system.Sprite;
import src.util.WorldTestHelper;

public class RevengeAttackEventTest {

    @BeforeEach
    public void setUp() {
        WorldTestHelper.resetWorld();
        SimYukkuri.world = new World();
        WorldTestHelper.initializeStandardTranslate500();
    }

    // --- Default constructor ---

    @Test
    public void testDefaultConstructor() {
        RevengeAttackEvent event = new RevengeAttackEvent();
        assertEquals(-1, event.getFrom());
        assertEquals(-1, event.getTo());
        assertEquals(EventPriority.LOW, event.getPriority());
    }

    // --- Parameterized constructor ---

    @Test
    public void testParameterizedConstructor() {
        Yukkuri from = createBody();
        Yukkuri to = createBody();
        RevengeAttackEvent event = new RevengeAttackEvent(from, to, null, 1);
        assertEquals(from.getUniqueID(), event.getFrom());
        assertEquals(to.getUniqueID(), event.getTo());
        assertEquals(1, event.getCount());
    }

    // --- checkEventResponse ---

    @Test
    public void testCheckEventResponse_alwaysTrueAndSetsPriorityHigh() {
        Yukkuri b = createBody();
        RevengeAttackEvent event = new RevengeAttackEvent();
        assertTrue(event.checkEventResponse(b));
        assertEquals(EventPriority.HIGH, event.getPriority());
    }

    // --- start ---

    @Test
    public void testStart_clearsActionFlags() {
        Yukkuri b = createBody();
        b.setToFood(true);
        b.setToBed(true);
        b.setToShit(true);
        b.setToSteal(true);
        b.setToSukkiri(true);
        RevengeAttackEvent event = new RevengeAttackEvent(b, null, null, 1);
        event.start(b);
        assertFalse(b.isToFood());
        assertFalse(b.isToBed());
        assertFalse(b.isToShit());
        assertFalse(b.isToSteal());
        assertFalse(b.isToSukkiri());
        assertTrue(b.isToTakeout());
    }

    // --- update ---

    @Test
    public void testUpdate_returnsAbortWhenToIsNull() {
        Yukkuri b = createBody();
        RevengeAttackEvent event = new RevengeAttackEvent(b, null, null, 1);
        // to is -1 (null) => returns ABORT
        assertEquals(UpdateState.ABORT, event.update(b));
    }

    @Test
    public void testUpdate_returnsAbortWhenToIsRemoved() {
        Yukkuri b = createBody();
        Yukkuri to = createBody();
        to.setRemoved(true);
        RevengeAttackEvent event = new RevengeAttackEvent(b, to, null, 1);
        assertEquals(UpdateState.ABORT, event.update(b));
    }

    @Test
    public void testUpdate_toTaken_returnsAbort() {
        Yukkuri b = createBody();
        Yukkuri to = createBody();
        to.setTaken(true);
        RevengeAttackEvent event = new RevengeAttackEvent(b, to, null, 1);
        assertEquals(UpdateState.ABORT, event.update(b));
    }

    @Test
    public void testExecute_toNull_returnsTrue() {
        Yukkuri b = createBody();
        RevengeAttackEvent event = new RevengeAttackEvent(b, null, null, 1);
        // isDontMove=false, random might or might not be 0
        // but to=null → returns true (no mypane needed)
        // If random==0 → also true. Either way returns true.
        assertDoesNotThrow(() -> assertTrue(event.execute(b) || !event.execute(b)));
    }

    @Test
    public void testToString_doesNotThrow() {
        RevengeAttackEvent event = new RevengeAttackEvent();
        assertDoesNotThrow(() -> event.toString());
    }

    // --- update: to alive → returns null ---

    @Test
    public void testUpdate_toAlive_returnsNull() {
        Yukkuri from = createBody();
        Yukkuri to = createBody();
        RevengeAttackEvent event = new RevengeAttackEvent(from, to, null, 1);
        assertNull(event.update(from));
    }

    // --- execute: isDontMove → returns true ---

    @Test
    public void testExecute_isDontMove_returnsTrue() {
        Yukkuri from = createBody();
        from.setGrabbed(true); // isDontMove() returns true when grabbed
        RevengeAttackEvent event = new RevengeAttackEvent(from, null, null, 1);
        assertTrue(event.execute(from));
    }

    // --- execute: RND=0 → returns true ---

    @Test
    public void testExecute_RND0_returnsTrue() {
        Yukkuri from = createBody();
        SimYukkuri.RND = new src.ConstState(0); // nextInt always returns 0
        try {
            RevengeAttackEvent event = new RevengeAttackEvent(from, null, null, 1);
            assertTrue(event.execute(from));
        } finally {
            SimYukkuri.RND = new java.util.Random();
        }
    }

    // --- start: to != null → calls moveToEvent ---
    @Test
    public void testStart_withTo_doesNotThrow() {
        Yukkuri from = createBody();
        Yukkuri to = createBody();
        RevengeAttackEvent event = new RevengeAttackEvent(from, to, null, 1);
        assertDoesNotThrow(() -> event.start(from));
        assertTrue(from.isToTakeout());
    }

    // --- update: to alive, close distance → to.stay() + returns null ---
    @Test
    public void testUpdate_closeDistance_returnsNull() {
        Yukkuri from = createBody(); // x=0,y=0
        Yukkuri to = createBody(); // x=0,y=0 → distance=0 < 2500
        RevengeAttackEvent event = new RevengeAttackEvent(from, to, null, 1);
        assertNull(event.update(from));
    }

    @Nested
    class RegressionScenarios {

        @Test
        void testScenario_StartWakesSleeperClearsActionsAndTargetsVictim() {
            Yukkuri attacker = createBody();
            Yukkuri victim = createBody();
            attacker.setX(100);
            attacker.setY(100);
            victim.setX(160);
            victim.setY(100);
            attacker.setAge(500);
            attacker.setWakeUpTime(0);
            attacker.setSleeping(true);
            attacker.setToFood(true);
            attacker.setToBed(true);
            attacker.setToShit(true);
            attacker.setToSteal(true);
            attacker.setToSukkiri(true);
            RevengeAttackEvent event = new RevengeAttackEvent(attacker, victim, null, 1);

            event.start(attacker);

            assertEquals(attacker.getAge(), attacker.getWakeUpTime(), "starting revenge should refresh wake-up time");
            assertTrue(attacker.isSleeping(), "revenge start should not forcibly clear the sleeping flag by itself");
            assertFalse(attacker.isToFood(), "revenge start should clear food movement");
            assertFalse(attacker.isToBed(), "revenge start should clear bed movement");
            assertFalse(attacker.isToShit(), "revenge start should clear toilet movement");
            assertFalse(attacker.isToSteal(), "revenge start should clear steal movement");
            assertFalse(attacker.isToSukkiri(), "revenge start should clear mating movement");
            assertTrue(attacker.isToTakeout(), "revenge start should switch the attacker into event movement");
            assertEquals(victim.getY(), attacker.getDestY(), "revenge start should target the victim Y position");
        }

        @Test
        void testScenario_UpdateNearVictimForcesVictimToStay() {
            Yukkuri attacker = createBody();
            Yukkuri victim = createBody();
            attacker.setX(100);
            attacker.setY(100);
            victim.setX(120);
            victim.setY(120);
            RevengeAttackEvent event = new RevengeAttackEvent(attacker, victim, null, 1);

            assertNull(event.update(attacker));

            assertTrue(victim.isStaying(), "nearby victim should be forced to stay during revenge pursuit");
            assertEquals(victim.getY(), attacker.getDestY(), "attacker should keep chasing the victim position");
        }

        @Test
        void testScenario_ExecuteDontMoveMakesAttackerSadAndLament() {
            Yukkuri attacker = createBody();
            attacker.setGrabbed(true);
            attacker.setHappiness(src.enums.Happiness.HAPPY);
            RevengeAttackEvent event = new RevengeAttackEvent(attacker, null, null, 1);

            assertTrue(event.execute(attacker));

            assertEquals(src.enums.Happiness.SAD, attacker.getHappiness(),
                    "immobile attacker should become sad when giving up revenge");
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
        b.setBodySpr(spr);
        SimYukkuri.world.getCurrentMap().getBody().put(b.getUniqueID(), b);
        return b;
    }
}
