package org.simyukkuri.item;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.simyukkuri.SimYukkuri;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.world.item.HotPlate;
import org.simyukkuri.entity.core.world.item.ItemTestBase;
import org.simyukkuri.enums.Happiness;
import org.simyukkuri.enums.ImageCode;
import org.simyukkuri.enums.FootBake;
import org.simyukkuri.util.WorldTestHelper;

class HotPlateTest extends ItemTestBase {

    @Test
    void testConstructor_Default() {
        HotPlate item = new HotPlate();
        item.setObjId(1);
        SimYukkuri.world.getCurrentWorldState().getHotPlates().put(item.getObjId(), item);
        verifyCommonProperties(item);
        assertTrue(SimYukkuri.world.getCurrentWorldState().getHotPlates().containsKey(item.getObjId()));
    }

    @Test
    void testGetHitCheckObjType() {
        HotPlate item = new HotPlate();
        assertEquals(HotPlate.hitCheckObjType, item.getHitCheckObjType());
    }

    @Test
    void testEnableHitCheck_NoBindBody() {
        HotPlate item = new HotPlate();
        assertNull(item.getBoundYukkuri());
        assertTrue(item.enableHitCheck());
    }

    @Test
    void testEnableHitCheck_WithBindBody() {
        HotPlate item = new HotPlate();
        Yukkuri body = WorldTestHelper.createBody();
        item.setBoundYukkuri(body);
        assertFalse(item.enableHitCheck());
    }

    @Test
    void testGetSetBindBody() {
        HotPlate item = new HotPlate();
        assertNull(item.getBoundYukkuri());
        Yukkuri body = WorldTestHelper.createBody();
        item.setBoundYukkuri(body);
        assertEquals(body, item.getBoundYukkuri());
        item.setBoundYukkuri(null);
        assertNull(item.getBoundYukkuri());
    }

    @Test
    void testGetSetSmoke() {
        HotPlate item = new HotPlate();
        assertNull(item.getSmoke());
        item.setSmoke(null);
        assertNull(item.getSmoke());
    }

    @Test
    void testGetShadowImage() {
        HotPlate item = new HotPlate();
        assertNull(item.getShadowImage());
    }

    @Test
    void testGetBounding() {
        assertNotNull(HotPlate.getBounding());
        assertSame(HotPlate.getBounding(), HotPlate.getBounding());
    }

    @Test
    void testRemoveListData_WithoutBindBody() {
        HotPlate item = new HotPlate();
        item.setObjId(200);
        SimYukkuri.world.getCurrentWorldState().getHotPlates().put(item.getObjId(), item);
        assertTrue(SimYukkuri.world.getCurrentWorldState().getHotPlates().containsKey(200));

        item.removeFromWorld();

        assertFalse(SimYukkuri.world.getCurrentWorldState().getHotPlates().containsKey(200));
    }

    @Test
    void testRemoveListData_WithBindBody() {
        HotPlate item = new HotPlate();
        item.setObjId(201);
        SimYukkuri.world.getCurrentWorldState().getHotPlates().put(item.getObjId(), item);

        Yukkuri body = WorldTestHelper.createBody();
        body.setLockmove(true);
        item.setBoundYukkuri(body);

        item.removeFromWorld();

        assertFalse(SimYukkuri.world.getCurrentWorldState().getHotPlates().containsKey(201));
        assertFalse(body.isLockmove());
    }

    // upDate() の age%2400==0 の Cash.addCash 呼び出しが起きる。
    // initializeMinimalWorld() は setUp() で実施済み。
    @Test
    void testUpDate_NoBindBody() {
        HotPlate item = new HotPlate();
        assertNull(item.getBoundYukkuri());
        assertDoesNotThrow(() -> item.upDate());
    }

    @Test
    void testUpDate_WithBindBody_GrabbedTrue() {
        HotPlate item = new HotPlate();
        item.setObjId(202);
        SimYukkuri.world.getCurrentWorldState().getHotPlates().put(item.getObjId(), item);

        Yukkuri body = WorldTestHelper.createBody();
        item.setBoundYukkuri(body);
        item.setGrabbed(true);

        item.upDate();
        assertNotNull(item);
        assertFalse(item.isRemoved());
    }

    // bindBody のX座標をプレートのX座標と変える。upDate() 後 bindBody が null になる。
    @Test
    void testUpDate_WithBindBody_BodyMovedAway() {
        HotPlate item = new HotPlate();
        item.setObjId(203);
        SimYukkuri.world.getCurrentWorldState().getHotPlates().put(item.getObjId(), item);

        Yukkuri body = WorldTestHelper.createBody();
        item.setBoundYukkuri(body);
        // プレートとbodyのX座標を意図的にずらす
        body.setCalcX(item.getX() + 999);

        item.upDate();

        assertNull(item.getBoundYukkuri());
    }

    // --- HotPlate(int,int,int) constructor ---

    @Test
    void testConstructor_WithCoords_doesNotThrow() {
        HotPlate item = new HotPlate(100, 100, 0);
        assertNotNull(item);
        assertTrue(SimYukkuri.world.getCurrentWorldState().getHotPlates().containsKey(item.getObjId()));
    }

    // --- getImageLayer ---

    @Test
    void testGetImageLayer_enabled_noBindBody_doesNotThrow() {
        HotPlate item = new HotPlate();
        java.awt.image.BufferedImage[] layer = new java.awt.image.BufferedImage[1];
        int count = item.getImageLayer(layer);
        assertEquals(1, count);
    }

    @Test
    void testGetImageLayer_enabled_withBindBody_doesNotThrow() {
        HotPlate item = new HotPlate();
        Yukkuri body = WorldTestHelper.createBody();
        item.setBoundYukkuri(body);
        java.awt.image.BufferedImage[] layer = new java.awt.image.BufferedImage[1];
        int count = item.getImageLayer(layer);
        assertEquals(1, count);
    }

    @Test
    void testGetImageLayer_disabled_doesNotThrow() {
        HotPlate item = new HotPlate();
        item.setEnabled(false);
        java.awt.image.BufferedImage[] layer = new java.awt.image.BufferedImage[1];
        int count = item.getImageLayer(layer);
        assertEquals(1, count);
    }

    // --- objHitProcess ---

    @Test
    void testObjHitProcess_executesCode() {
        HotPlate item = new HotPlate();
        item.setObjId(300);
        SimYukkuri.world.getCurrentWorldState().getHotPlates().put(item.getObjId(), item);
        Yukkuri body = WorldTestHelper.createBody();
        try {
            item.objHitProcess(body);
        } catch (Exception e) {
            // mypane.getTerrarium().addEffect fails in headless
        }
        assertFalse(item.isRemoved());
    }

    @Nested
    class RegressionScenarios {

        @Test
        void testScenario_BoundBodyOnPlateAccumulatesDamageStressAndPainState() {
            HotPlate item = new HotPlate();
            item.setX(120);
            item.setY(140);

            Yukkuri body = WorldTestHelper.createBody();
            body.setCalcX(item.getX());
            body.setCalcY(item.getY());
            body.setCalcZ(item.getZ());
            body.setSleeping(true);
            body.setHappiness(Happiness.AVERAGE);

            item.setBoundYukkuri(body);

            int damageBefore = body.getDamage();
            int stressBefore = body.getStress();
            int footBakeBefore = body.getFootBakePeriod();

            item.upDate();

            assertFalse(body.isSleeping());
            assertEquals(damageBefore + 20, body.getDamage());
            assertEquals(stressBefore + 20, body.getStress());
            assertEquals(footBakeBefore + 50, body.getFootBakePeriod());
            assertEquals(Happiness.VERY_SAD, body.getHappiness());
            assertEquals(ImageCode.PAIN.ordinal(), body.getForceFace());
            assertEquals(body, item.getBoundYukkuri());
        }

        @Test
        void testScenario_CriticalBurnedBodyBecomesPullableWhileStillBound() {
            HotPlate item = new HotPlate();
            item.setX(160);
            item.setY(180);

            Yukkuri body = WorldTestHelper.createBody();
            body.setCalcX(item.getX());
            body.setCalcY(item.getY());
            body.setCalcZ(item.getZ());
            body.setFootBakePeriod(body.getDamageLimit() + 1);
            body.setCanPullOrPush(false);

            item.setBoundYukkuri(body);
            item.upDate();

            assertTrue(body.canPullOrPush());
            assertEquals(body, item.getBoundYukkuri());
        }

        @Test
        void footBakeAtMediumThresholdRemainsNone() {
            // threshold_medium = getDamageLimit() >> 1（現 ageState 基準）
            // 等値（== threshold_medium）は NONE のまま（条件が > のため）
            Yukkuri body = WorldTestHelper.createBody();
            body.setAgeState(org.simyukkuri.enums.AgeState.ADULT);
            int limit = body.getDamageLimit();
            body.setFootBakePeriod(limit >> 1);
            assertEquals(FootBake.NONE, body.getFootBakeLevel(),
                    "threshold_medium ちょうどは NONE のままであること（> ではないため）");
        }

        @Test
        void footBakeAtCriticalThresholdRemainsMedium() {
            // threshold_critical = getDamageLimit()。等値は MEDIUM のまま（> ではないため）
            Yukkuri body = WorldTestHelper.createBody();
            body.setAgeState(org.simyukkuri.enums.AgeState.ADULT);
            int limit = body.getDamageLimit();
            body.setFootBakePeriod(limit);
            assertEquals(FootBake.MEDIUM, body.getFootBakeLevel(),
                    "threshold_critical ちょうどは MEDIUM のままであること（> ではないため）");
        }

        @Test
        void upDateAccumulatesToMediumBurn() {
            // NONE 上限 (threshold_medium ちょうど) + upDate() 1回(+50) → MEDIUM に遷移
            HotPlate item = new HotPlate();
            item.setX(120);
            item.setY(140);
            Yukkuri body = WorldTestHelper.createBody();
            body.setAgeState(org.simyukkuri.enums.AgeState.ADULT);
            body.setCalcX(item.getX());
            body.setCalcY(item.getY());
            body.setCalcZ(item.getZ());
            int limit = body.getDamageLimit();
            body.setFootBakePeriod(limit >> 1);
            item.setBoundYukkuri(body);

            item.upDate();

            assertEquals(FootBake.MEDIUM, body.getFootBakeLevel(),
                    "upDate +50 で threshold_medium を超えて MEDIUM に遷移すること");
        }

        @Test
        void upDateAccumulatesToCriticalBurnAndEnablesPull() {
            // CRITICAL 手前 50 + upDate() 1回(+50) → CRITICAL に遷移 + canPullOrPush=true
            HotPlate item = new HotPlate();
            item.setX(160);
            item.setY(180);
            Yukkuri body = WorldTestHelper.createBody();
            body.setAgeState(org.simyukkuri.enums.AgeState.ADULT);
            body.setCalcX(item.getX());
            body.setCalcY(item.getY());
            body.setCalcZ(item.getZ());
            int limit = body.getDamageLimit();
            body.setFootBakePeriod(limit - 49);
            body.setCanPullOrPush(false);
            item.setBoundYukkuri(body);

            item.upDate();

            assertEquals(FootBake.CRITICAL, body.getFootBakeLevel(),
                    "upDate +50 で threshold_critical を超えて CRITICAL に遷移すること");
            assertTrue(body.canPullOrPush(),
                    "CRITICAL になると canPullOrPush が true になること");
        }

        @Test
        void testScenario_RemovingBoundBodyFromPlateRestoresMobilityAndShadow() {
            HotPlate item = new HotPlate();
            item.setX(200);
            item.setY(220);

            Yukkuri body = WorldTestHelper.createBody();
            body.setCalcX(item.getX());
            body.setCalcY(item.getY());
            body.setCalcZ(item.getZ());
            body.setLockmove(true);
            body.setShadowVisible(false);
            body.setCanPullOrPush(true);
            body.setForceFace(ImageCode.PAIN.ordinal());

            item.setBoundYukkuri(body);
            body.setCalcX(item.getX() + 1);

            item.upDate();

            assertNull(item.getBoundYukkuri());
            assertFalse(body.isLockmove());
            assertFalse(body.canPullOrPush());
            assertTrue(body.isShadowVisible());
            assertEquals(-1, body.getForceFace());
        }
    }
}
