package src.event;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import src.SimYukkuri;
import src.base.Body;
import src.base.EventPacket.EventPriority;
import src.draw.Translate;
import src.draw.World;
import src.enums.AgeState;

class BegForLifeEventTest {

    @BeforeEach
    void setUp() {
        SimYukkuri.world = new World();
        Translate.setMapSize(1000, 1000, 500);
        Translate.setCanvasSize(800, 600, 100, 100, new float[]{1.0f});
        Translate.createTransTable(false);
    }

    private static Body createBody() {
        Body b = new src.yukkuri.Reimu();
        b.setAgeState(AgeState.ADULT);
        src.system.Sprite[] spr = new src.system.Sprite[3];
        for (int i = 0; i < 3; i++) {
            spr[i] = new src.system.Sprite(10, 10, src.system.Sprite.PIVOT_CENTER_BOTTOM);
        }
        b.setBodySpr(spr);
        SimYukkuri.world.getCurrentMap().getBody().put(b.getUniqueID(), b);
        return b;
    }

    @Test
    void testDefaultConstructor() {
        BegForLifeEvent event = new BegForLifeEvent();
        assertNotNull(event);
        assertEquals(-1, event.getFrom());
        assertEquals(-1, event.getTo());
    }

    @Test
    void testParameterizedConstructor() {
        Body from = createBody();
        Body to = createBody();
        BegForLifeEvent event = new BegForLifeEvent(from, to, null, 10);
        assertNotNull(event);
        assertEquals(from.getUniqueID(), event.getFrom());
        assertEquals(to.getUniqueID(), event.getTo());
        assertEquals(10, event.getCount());
    }

    @Test
    void testCheckEventResponse_setsPriorityHigh() {
        Body from = createBody();
        BegForLifeEvent event = new BegForLifeEvent(from, null, null, 10);
        // checkEventResponse checks b == from and !b.isUnBirth()
        event.checkEventResponse(from);
        assertEquals(EventPriority.HIGH, event.getPriority());
    }

    @Test
    void testCheckEventResponse_returnsTrueWhenBEqualsFromAndNotUnBirth() {
        Body from = createBody();
        // from is not unBirth by default
        BegForLifeEvent event = new BegForLifeEvent(from, null, null, 10);
        assertTrue(event.checkEventResponse(from));
    }

    @Test
    void testCheckEventResponse_returnsFalseWhenBIsNotFrom() {
        Body from = createBody();
        Body other = createBody();
        BegForLifeEvent event = new BegForLifeEvent(from, null, null, 10);
        assertFalse(event.checkEventResponse(other));
    }

    @Test
    void testCheckEventResponse_returnsFalseWhenBIsUnBirth() {
        Body from = createBody();
        // Set unBirth via reflection
        try {
            java.lang.reflect.Field f = findField(from.getClass(), "unBirth");
            f.setAccessible(true);
            f.setBoolean(from, true);
        } catch (Exception e) {
            fail("Could not set unBirth via reflection: " + e.getMessage());
        }
        BegForLifeEvent event = new BegForLifeEvent(from, null, null, 10);
        assertFalse(event.checkEventResponse(from));
    }

    @Test
    void testExecute_returnsTrue() {
        Body b = createBody();
        BegForLifeEvent event = new BegForLifeEvent(b, null, null, 10);
        assertTrue(event.execute(b));
    }

    @Test
    void testEnd_setsBeggingToFalse() {
        Body b = createBody();
        b.setBegging(true);
        BegForLifeEvent event = new BegForLifeEvent(b, null, null, 10);
        event.end(b);
        assertFalse(b.isBegging());
    }

    @Test
    void testStart_doesNotThrow() {
        Body b = createBody();
        BegForLifeEvent event = new BegForLifeEvent(b, null, null, 10);
        assertDoesNotThrow(() -> event.start(b));
    }

    @Test
    void testUpdate_tick0_doesNotThrow() {
        Body b = createBody();
        BegForLifeEvent event = new BegForLifeEvent(b, null, null, 10);
        // tick=0, sets body state
        assertDoesNotThrow(() -> event.update(b));
    }

    @Test
    void testUpdate_bTalking_returnsNull() {
        Body b = createBody();
        b.setMessageCount(1); // isTalking() = true
        BegForLifeEvent event = new BegForLifeEvent(b, null, null, 10);
        assertNull(event.update(b));
    }

    @Test
    void testToString_doesNotThrow() {
        BegForLifeEvent event = new BegForLifeEvent();
        assertDoesNotThrow(() -> event.toString());
    }

    // --- update: tick >= 7 and roop != 0 → begging path ---

    @Test
    void testUpdate_tick7_roopNotZero_setsBegging() throws Exception {
        Body b = createBody();
        // Must set damage > 0 so getDamage() != 0, preventing roop reset
        src.util.WorldTestHelper.setDamage(b, 1);
        BegForLifeEvent event = new BegForLifeEvent(b, null, null, 10);
        // Set roop, roop2, roop3 non-zero, tick=7
        setField(event, "roop", 5);
        setField(event, "roop2", 8);
        setField(event, "roop3", 1);
        setField(event, "tick", 7);
        assertNull(event.update(b));
        assertTrue(b.isBegging());
    }

    // --- update: roop == 0, roop2 != 0, roop3 != 0 → BegForLife path ---

    @Test
    void testUpdate_roop0_roop2NotZero_begForLife() throws Exception {
        Body b = createBody();
        BegForLifeEvent event = new BegForLifeEvent(b, null, null, 10);
        setField(event, "roop", 0);
        setField(event, "roop2", 5);
        setField(event, "roop3", 1);
        setField(event, "tick", 8);
        assertNull(event.update(b));
    }

    // --- update: wait == 30, all roop == 0 → Monologue → ABORT ---

    @Test
    void testUpdate_wait30_allRoopZero_returnsAbort() throws Exception {
        Body b = createBody();
        BegForLifeEvent event = new BegForLifeEvent(b, null, null, 10);
        setField(event, "roop", 0);
        setField(event, "roop2", 0);
        setField(event, "roop3", 0);
        setField(event, "tick", 8);
        setField(event, "wait", 30);
        assertEquals(src.base.EventPacket.UpdateState.ABORT, event.update(b));
    }

    // --- update: wait == 50, roop=0, roop2=0, roop3 != 0, isDamaged ---

    @Test
    void testUpdate_wait50_damaged_setsMessage() throws Exception {
        Body b = createBody();
        src.util.WorldTestHelper.setDamage(b, b.getDamageLimit() + 100);
        BegForLifeEvent event = new BegForLifeEvent(b, null, null, 10);
        setField(event, "roop", 0);
        setField(event, "roop2", 0);
        setField(event, "roop3", 1);
        setField(event, "tick", 8);
        setField(event, "wait", 50);
        assertNull(event.update(b));
    }

    // --- update: wait == 50, roop=0, roop2=0, roop3 != 0, not damaged → thanks path ---

    @Test
    void testUpdate_wait50_notDamaged_thanksPath() throws Exception {
        Body b = createBody();
        // not damaged
        src.util.WorldTestHelper.setDamage(b, 0);
        BegForLifeEvent event = new BegForLifeEvent(b, null, null, 10);
        setField(event, "roop", 0);
        setField(event, "roop2", 0);
        setField(event, "roop3", 1);
        setField(event, "tick", 8);
        setField(event, "wait", 50);
        assertNull(event.update(b));
    }

    private static void setField(Object obj, String name, int value) throws Exception {
        java.lang.reflect.Field f = findField(obj.getClass(), name);
        f.setAccessible(true);
        f.setInt(obj, value);
    }

    private static java.lang.reflect.Field findField(Class<?> clazz, String fieldName) {
        while (clazz != null) {
            try {
                return clazz.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            }
        }
        return null;
    }
}
