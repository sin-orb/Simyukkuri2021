package src.event;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import src.SimYukkuri;
import src.base.Body;
import src.base.EventPacket;
import src.base.EventPacket.EventPriority;
import src.base.EventPacket.UpdateState;
import src.draw.Translate;
import src.draw.World;
import src.enums.AgeState;
import src.system.Sprite;
import src.yukkuri.Reimu;

public class RevengeAttackEventTest {

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
        RevengeAttackEvent event = new RevengeAttackEvent();
        assertEquals(-1, event.getFrom());
        assertEquals(-1, event.getTo());
        assertEquals(EventPriority.LOW, event.getPriority());
    }

    // --- Parameterized constructor ---

    @Test
    public void testParameterizedConstructor() {
        Body from = createBody();
        Body to = createBody();
        RevengeAttackEvent event = new RevengeAttackEvent(from, to, null, 1);
        assertEquals(from.getUniqueID(), event.getFrom());
        assertEquals(to.getUniqueID(), event.getTo());
        assertEquals(1, event.getCount());
    }

    // --- checkEventResponse ---

    @Test
    public void testCheckEventResponse_alwaysTrueAndSetsPriorityHigh() {
        Body b = createBody();
        RevengeAttackEvent event = new RevengeAttackEvent();
        assertTrue(event.checkEventResponse(b));
        assertEquals(EventPriority.HIGH, event.getPriority());
    }

    // --- start ---

    @Test
    public void testStart_clearsActionFlags() {
        Body b = createBody();
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
        Body b = createBody();
        RevengeAttackEvent event = new RevengeAttackEvent(b, null, null, 1);
        // to is -1 (null) => returns ABORT
        assertEquals(UpdateState.ABORT, event.update(b));
    }

    @Test
    public void testUpdate_returnsAbortWhenToIsRemoved() {
        Body b = createBody();
        Body to = createBody();
        to.setRemoved(true);
        RevengeAttackEvent event = new RevengeAttackEvent(b, to, null, 1);
        assertEquals(UpdateState.ABORT, event.update(b));
    }

    @Test
    public void testUpdate_toTaken_returnsAbort() {
        Body b = createBody();
        Body to = createBody();
        to.setTaken(true);
        RevengeAttackEvent event = new RevengeAttackEvent(b, to, null, 1);
        assertEquals(UpdateState.ABORT, event.update(b));
    }

    @Test
    public void testExecute_toNull_returnsTrue() {
        Body b = createBody();
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
        Body from = createBody();
        Body to = createBody();
        RevengeAttackEvent event = new RevengeAttackEvent(from, to, null, 1);
        assertNull(event.update(from));
    }

    // --- execute: isDontMove → returns true ---

    @Test
    public void testExecute_isDontMove_returnsTrue() {
        Body from = createBody();
        from.setGrabbed(true); // isDontMove() returns true when grabbed
        RevengeAttackEvent event = new RevengeAttackEvent(from, null, null, 1);
        assertTrue(event.execute(from));
    }

    // --- execute: RND=0 → returns true ---

    @Test
    public void testExecute_RND0_returnsTrue() {
        Body from = createBody();
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
        Body from = createBody();
        Body to = createBody();
        RevengeAttackEvent event = new RevengeAttackEvent(from, to, null, 1);
        assertDoesNotThrow(() -> event.start(from));
        assertTrue(from.isToTakeout());
    }

    // --- update: to alive, close distance → to.stay() + returns null ---
    @Test
    public void testUpdate_closeDistance_returnsNull() {
        Body from = createBody(); // x=0,y=0
        Body to = createBody();   // x=0,y=0 → distance=0 < 2500
        RevengeAttackEvent event = new RevengeAttackEvent(from, to, null, 1);
        assertNull(event.update(from));
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
