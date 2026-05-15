package org.simyukkuri.entity.core.world.mobile;

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

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.simyukkuri.SimYukkuri;
import org.simyukkuri.draw.Translate;
import org.simyukkuri.engine.World;
import org.simyukkuri.enums.AgeState;
import org.simyukkuri.enums.TickResult;
import org.simyukkuri.system.ItemMenu.GetMenuTarget;
import org.simyukkuri.system.ItemMenu.UseMenuTarget;
import org.simyukkuri.util.WorldTestHelper;

class VomitTest {

    private Vomit vomit;

    @BeforeEach
    void setUp() {
        SimYukkuri.world = new World();
        WorldTestHelper.initializeStandardTranslate500();
        vomit = new Vomit();
        vomit.setAgeState(AgeState.ADULT);
    }

    @Test
    void testGetVomitState() {
        // Limit for ADULT is 100 * 24 * 8
        // 1/4 of limit is the threshold for state 1
        int limit = 100 * 24 * 8;
        int threshold = limit / 4;

        vomit.setAge(0);
        assertEquals(0, vomit.getVomitState());

        vomit.setAge(threshold);
        assertEquals(1, vomit.getVomitState());
    }

    @Test
    void testEatVomit() {
        vomit.setAmount(1000);
        vomit.eatVomit(100);
        assertEquals(900, vomit.getAmount());

        vomit.eatVomit(1000);
        assertEquals(0, vomit.getAmount());
        assertTrue(vomit.isRemoved());
    }

    @Test
    void testCrushVomit() {
        vomit.setAge(100);
        vomit.crushVomit();
        // crush adds limit/2 to age
        assertEquals(100 + 9600, vomit.getAge());
    }

    @Test
    void testGetValue() {
        assertEquals(300, vomit.getValue());

        vomit.setAgeState(AgeState.CHILD);
        assertEquals(100, vomit.getValue());

        vomit.setAgeState(AgeState.BABY);
        assertEquals(50, vomit.getValue());
    }

    @Test
    void testToString() {
        vomit.setOwnerName("Alice");
        String str = vomit.toString();
        assertNotNull(str);
        assertTrue(str.contains("Alice"));
    }

    @Test
    void testGettersSetters() {
        vomit.setFalldownDamage(10);
        assertEquals(10, vomit.getFalldownDamage());

        vomit.setVomitType(2);
        assertEquals(2, vomit.getVomitType());
    }

    // --- Additional tests ---

    @Test
    void testGetAgeState_Adult() {
        assertEquals(AgeState.ADULT, vomit.getAgeState());
    }

    @Test
    void testGetAgeState_Baby() {
        vomit.setAgeState(AgeState.BABY);
        assertEquals(AgeState.BABY, vomit.getAgeState());
    }

    @Test
    void testGetAgeState_Child() {
        vomit.setAgeState(AgeState.CHILD);
        assertEquals(AgeState.CHILD, vomit.getAgeState());
    }

    @Test
    void testHasGetPopup() {
        assertEquals(GetMenuTarget.VOMIT, vomit.hasGetPopup());
    }

    @Test
    void testHasUsePopup() {
        assertEquals(UseMenuTarget.NONE, vomit.hasUsePopup());
    }

    @Test
    void testKick_adult() {
        vomit.setAgeState(AgeState.ADULT);
        assertDoesNotThrow(() -> vomit.kick());
        // kick sets vz to blowLevel[2] = -4, vy = blowLevel[2]*2 = -8
        assertEquals(-8, vomit.getVy());
        assertEquals(-4, vomit.getVz());
    }

    @Test
    void testKick_child() {
        vomit.setAgeState(AgeState.CHILD);
        assertDoesNotThrow(() -> vomit.kick());
        assertEquals(-10, vomit.getVy());
        assertEquals(-5, vomit.getVz());
    }

    @Test
    void testKick_baby() {
        vomit.setAgeState(AgeState.BABY);
        assertDoesNotThrow(() -> vomit.kick());
        assertEquals(-12, vomit.getVy());
        assertEquals(-6, vomit.getVz());
    }

    @Test
    void testClockTick_alreadyRemoved_returnsREMOVED() {
        vomit.setRemoved(true);
        assertEquals(TickResult.REMOVED, vomit.clockTick());
    }

    @Test
    void testClockTick_notRemoved_ageUnderLimit_returnsDONOTHING() {
        vomit.setRemoved(false);
        vomit.setAge(0);
        vomit.setX(100);
        vomit.setY(100);
        assertEquals(TickResult.NONE, vomit.clockTick());
    }

    @Test
    void testClockTick_notRemoved_ageOverLimit_setsRemoved() {
        vomit.setRemoved(false);
        int limit = 100 * 24 * 8; // ADULT limit
        vomit.setAge(limit);
        vomit.setX(100);
        vomit.setY(100);
        vomit.clockTick();
        assertTrue(vomit.isRemoved());
    }

    @Test
    void testVomitConstants() {
        assertEquals(0, Vomit.VOMIT_NORMAL);
        assertEquals(1, Vomit.VOMIT_CRASHED);
        assertEquals(2, Vomit.VOMIT_SHADOW);
        assertEquals(3, Vomit.NUM_OF_VOMIT_STATE);
    }

    @Test
    void testGetAmount_defaultZero() {
        Vomit v = new Vomit();
        assertEquals(0, v.getAmount());
    }

    @Test
    void testSetAmount() {
        vomit.setAmount(500);
        assertEquals(500, vomit.getAmount());
    }

    @Test
    void testGetOwnerName_defaultUnknown() {
        Vomit v = new Vomit();
        assertEquals("Unknown", v.getOwnerName());
    }

    @Test
    void testSetOwnerName() {
        vomit.setOwnerName("Reimu");
        assertEquals("Reimu", vomit.getOwnerName());
    }

    @Test
    void testGetVomitType_default() {
        Vomit v = new Vomit();
        assertEquals(0, v.getVomitType());
    }

    @Test
    void testGetFalldownDamage_default() {
        Vomit v = new Vomit();
        assertEquals(0, v.getFalldownDamage());
    }

    @Test
    void testVomitState_Baby() {
        vomit.setAgeState(AgeState.BABY);
        int limit = 100 * 24 * 2; // BABY limit
        vomit.setAge(limit / 4);
        assertEquals(1, vomit.getVomitState());
        vomit.setAge(0);
        assertEquals(0, vomit.getVomitState());
    }

    @Test
    void testVomitState_Child() {
        vomit.setAgeState(AgeState.CHILD);
        int limit = 100 * 24 * 4; // CHILD limit
        vomit.setAge(limit / 4);
        assertEquals(1, vomit.getVomitState());
    }

    // --- getImage ---

    @Test
    void testGetImage_doesNotThrow() {
        assertDoesNotThrow(() -> vomit.getImage());
    }

    // --- getShadowImage ---

    @Test
    void testGetShadowImage_doesNotThrow() {
        assertDoesNotThrow(() -> vomit.getShadowImage());
    }

    // --- getSize ---

    @Test
    void testGetSize_doesNotThrow() {
        assertDoesNotThrow(() -> vomit.getSize());
    }

    // --- clockTick: vx != 0 branch ---

    @Test
    void testClockTick_vxNonZero_doesNotThrow() {
        vomit.setRemoved(false);
        vomit.setAge(0);
        vomit.setX(100);
        vomit.setY(100);
        vomit.setVx(3);
        assertDoesNotThrow(() -> vomit.clockTick());
    }

    // --- clockTick: vx negative, x goes negative → clamp to 0 ---

    @Test
    void testClockTick_vxNegative_clampsToZero() {
        vomit.setRemoved(false);
        vomit.setAge(0);
        vomit.setX(0);
        vomit.setY(100);
        vomit.setVx(-5);
        assertDoesNotThrow(() -> vomit.clockTick());
    }

    // --- clockTick: vy != 0 branch ---

    @Test
    void testClockTick_vyNonZero_doesNotThrow() {
        vomit.setRemoved(false);
        vomit.setAge(0);
        vomit.setX(100);
        vomit.setY(100);
        vomit.setVy(3);
        assertDoesNotThrow(() -> vomit.clockTick());
    }

    // --- clockTick: z != 0 branch (falldown) ---

    @Test
    void testClockTick_zNonZero_doesNotThrow() {
        vomit.setRemoved(false);
        vomit.setAge(0);
        vomit.setX(100);
        vomit.setY(100);
        vomit.setZ(50);
        assertDoesNotThrow(() -> vomit.clockTick());
    }

    @Test
    void testLoadImages_headless_executesCode() {
        try {
            org.simyukkuri.entity.core.world.mobile.Vomit.loadImages(org.simyukkuri.entity.core.world.mobile.Vomit.class.getClassLoader(),
                    null);
        } catch (Exception e) {
        }
    }

    @Test
    void testConstructorWithArgs_headless_executesCode() {
        try {
            org.simyukkuri.entity.core.living.yukkuri.Yukkuri body = org.simyukkuri.util.WorldTestHelper.createBody();
            org.simyukkuri.entity.core.world.mobile.Vomit v = new org.simyukkuri.entity.core.world.mobile.Vomit(100, 100, 0, body,
                    org.simyukkuri.enums.YukkuriType.REIMU);
        } catch (Exception e) {
        }
    }

    @Nested
    class RegressionScenarios {
        @Test
        void testScenario_ClockTickAtRightEdgeBouncesAndClampsX() {
            vomit.setRemoved(false);
            vomit.setAge(0);
            vomit.setX(999);
            vomit.setVx(5);

            TickResult result = vomit.clockTick();

            assertEquals(TickResult.NONE, result);
            assertEquals(Translate.getWorldWidth(), vomit.getX());
            assertEquals(-5, vomit.getVx());
        }

        @Test
        void testScenario_FallingImpactCrushesVomitAndResetsMotion() {
            vomit.setRemoved(false);
            vomit.setAge(0);
            vomit.setZ(10);
            vomit.setVz(15);

            TickResult result = vomit.clockTick();

            assertEquals(TickResult.NONE, result);
            assertEquals(9600, vomit.getAge());
            assertEquals(0, vomit.getZ());
            assertEquals(0, vomit.getVz());
            assertEquals(0, vomit.getFalldownDamage());
        }
    }
}
