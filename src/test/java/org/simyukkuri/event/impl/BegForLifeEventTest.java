package org.simyukkuri.event.impl;

import org.simyukkuri.entity.core.Entity;
import org.simyukkuri.entity.core.attachment.*;
import org.simyukkuri.entity.core.attachment.impl.*;
import org.simyukkuri.entity.core.effect.*;
import org.simyukkuri.entity.core.effect.impl.*;
import org.simyukkuri.entity.core.living.yukkuri.Dna;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.living.yukkuri.impl.*;
import org.simyukkuri.entity.core.world.bodylinked.*;
import org.simyukkuri.entity.core.world.item.*;
import org.simyukkuri.entity.core.world.mobile.*;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.simyukkuri.SimYukkuri;
import org.simyukkuri.engine.World;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.living.yukkuri.impl.Reimu;
import org.simyukkuri.enums.AgeState;
import org.simyukkuri.enums.Attitude;
import org.simyukkuri.enums.Happiness;
import org.simyukkuri.enums.ImageCode;
import org.simyukkuri.event.EventPacket.EventPriority;
import org.simyukkuri.util.WorldTestHelper;

class BegForLifeEventTest {

    @BeforeEach
    void setUp() {
        WorldTestHelper.resetWorld();
        SimYukkuri.world = new World();
        WorldTestHelper.initializeStandardTranslate500();
    }

    private static Yukkuri createBody() {
        Yukkuri b = new Reimu();
        b.setAgeState(AgeState.ADULT);
        org.simyukkuri.system.Sprite[] spr = new org.simyukkuri.system.Sprite[3];
        for (int i = 0; i < 3; i++) {
            spr[i] = new org.simyukkuri.system.Sprite(10, 10, org.simyukkuri.system.Sprite.PIVOT_CENTER_BOTTOM);
        }
        b.setSpriteSet(spr);
        SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(b.getUniqueID(), b);
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
        Yukkuri from = createBody();
        Yukkuri to = createBody();
        BegForLifeEvent event = new BegForLifeEvent(from, to, null, 10);
        assertNotNull(event);
        assertEquals(from.getUniqueID(), event.getFrom());
        assertEquals(to.getUniqueID(), event.getTo());
        assertEquals(10, event.getCount());
    }

    @Test
    void testCheckEventResponse_setsPriorityHigh() {
        Yukkuri from = createBody();
        BegForLifeEvent event = new BegForLifeEvent(from, null, null, 10);
        // checkEventResponse checks b == from and !b.isUnBirth()
        event.checkEventResponse(from);
        assertEquals(EventPriority.HIGH, event.getPriority());
    }

    @Test
    void testCheckEventResponse_returnsTrueWhenBEqualsFromAndNotUnBirth() {
        Yukkuri from = createBody();
        // from is not unBirth by default
        BegForLifeEvent event = new BegForLifeEvent(from, null, null, 10);
        assertTrue(event.checkEventResponse(from));
    }

    @Test
    void testCheckEventResponse_returnsFalseWhenBIsNotFrom() {
        Yukkuri from = createBody();
        Yukkuri other = createBody();
        BegForLifeEvent event = new BegForLifeEvent(from, null, null, 10);
        assertFalse(event.checkEventResponse(other));
    }

    @Test
    void testCheckEventResponse_returnsFalseWhenBIsUnBirth() {
        Yukkuri from = createBody();
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
        Yukkuri b = createBody();
        BegForLifeEvent event = new BegForLifeEvent(b, null, null, 10);
        assertTrue(event.execute(b));
    }

    @Test
    void testEnd_setsBeggingToFalse() {
        Yukkuri b = createBody();
        b.setBegging(true);
        BegForLifeEvent event = new BegForLifeEvent(b, null, null, 10);
        event.end(b);
        assertFalse(b.isBegging());
    }

    @Test
    void testStart_doesNotThrow() {
        Yukkuri b = createBody();
        BegForLifeEvent event = new BegForLifeEvent(b, null, null, 10);
        assertDoesNotThrow(() -> event.start(b));
    }

    @Test
    void testUpdate_tick0_doesNotThrow() {
        Yukkuri b = createBody();
        BegForLifeEvent event = new BegForLifeEvent(b, null, null, 10);
        // tick=0, sets body state
        assertDoesNotThrow(() -> event.update(b));
    }

    @Test
    void testUpdate_bTalking_returnsNull() {
        Yukkuri b = createBody();
        b.setMessageTicks(1); // isTalking() = true
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
        Yukkuri b = createBody();
        // Must set damage > 0 so getDamage() != 0, preventing roop reset
        org.simyukkuri.util.WorldTestHelper.setDamage(b, 1);
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
        Yukkuri b = createBody();
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
        Yukkuri b = createBody();
        BegForLifeEvent event = new BegForLifeEvent(b, null, null, 10);
        setField(event, "roop", 0);
        setField(event, "roop2", 0);
        setField(event, "roop3", 0);
        setField(event, "tick", 8);
        setField(event, "wait", 30);
        assertEquals(org.simyukkuri.event.EventPacket.UpdateState.ABORT, event.update(b));
    }

    // --- update: wait == 50, roop=0, roop2=0, roop3 != 0, isDamaged ---

    @Test
    void testUpdate_wait50_damaged_setsMessage() throws Exception {
        Yukkuri b = createBody();
        org.simyukkuri.util.WorldTestHelper.setDamage(b, b.getDamageLimit() + 100);
        BegForLifeEvent event = new BegForLifeEvent(b, null, null, 10);
        setField(event, "roop", 0);
        setField(event, "roop2", 0);
        setField(event, "roop3", 1);
        setField(event, "tick", 8);
        setField(event, "wait", 50);
        assertNull(event.update(b));
    }

    // --- update: wait == 50, roop=0, roop2=0, roop3 != 0, not damaged → thanks
    // path ---

    @Test
    void testUpdate_wait50_notDamaged_thanksPath() throws Exception {
        Yukkuri b = createBody();
        // not damaged
        org.simyukkuri.util.WorldTestHelper.setDamage(b, 0);
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

    @Nested
    class RegressionScenarios {

        @Test
        void testScenario_NotDamagedAverageBodyEndsBeggingVeryHappyAndSmiling() throws Exception {
            Yukkuri b = createBody();
            b.setAttitude(Attitude.AVERAGE);
            b.setBegging(true);
            WorldTestHelper.setDamage(b, 0);

            BegForLifeEvent event = new BegForLifeEvent(b, null, null, 10);
            setField(event, "roop", 0);
            setField(event, "roop2", 0);
            setField(event, "roop3", 1);
            setField(event, "tick", 8);
            setField(event, "wait", 50);

            assertNull(event.update(b));
            assertFalse(b.isBegging());
            assertEquals(Happiness.VERY_HAPPY, b.getHappiness());
            assertEquals(ImageCode.SMILE.ordinal(), b.getForceFace());
        }
    }
}
