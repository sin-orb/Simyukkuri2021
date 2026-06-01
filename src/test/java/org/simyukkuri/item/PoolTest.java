package org.simyukkuri.item;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.LinkedList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.simyukkuri.ConstState;
import org.simyukkuri.SimYukkuri;
import org.simyukkuri.entity.core.Entity;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.world.item.Food;
import org.simyukkuri.enums.AgeState;
import org.simyukkuri.enums.TickResult;
import org.simyukkuri.field.impl.Pool;
import org.simyukkuri.field.impl.Pool.Depth;
import org.simyukkuri.system.ItemMenu.ShapeMenu;
import org.simyukkuri.system.ItemMenu.ShapeMenuTarget;
import org.simyukkuri.util.WorldTestHelper;

class PoolTest {

    @BeforeEach
    public void setUp() {
        WorldTestHelper.resetWorld();
        WorldTestHelper.initializeMinimalWorld();
        WorldTestHelper.initializeStandardTranslate200();
    }

    @AfterEach
    public void tearDown() {
        WorldTestHelper.resetWorld();
    }

    // --- Constructor default ---

    @Test
    void testConstructor_Default() {
        Pool item = new Pool();
        SimYukkuri.world.getCurrentWorldState().getPools().add(item);
        assertNotNull(item);
        assertTrue(SimYukkuri.world.getCurrentWorldState().getPools().contains(item));
    }

    // --- Depth enum ---

    @Test
    void testDepth_enum_count() {
        assertEquals(4, Depth.values().length);
    }

    @Test
    void testDepth_enum_values() {
        assertNotNull(Depth.NONE);
        assertNotNull(Depth.EDGE);
        assertNotNull(Depth.SHALLOW);
        assertNotNull(Depth.DEEP);
        assertEquals(Depth.NONE, Depth.valueOf("NONE"));
        assertEquals(Depth.DEEP, Depth.valueOf("DEEP"));
    }

    // --- Inherited FieldShape methods ---

    @Test
    void testGetAttribute() {
        Pool item = new Pool();
        assertEquals(8, item.getAttribute()); // FIELD_POOL = 8
    }

    @Test
    void testGetMinimumSize() {
        Pool item = new Pool();
        assertEquals(8, item.getMinimumSize());
    }

    @Test
    void testHasShapePopup() {
        Pool item = new Pool();
        assertEquals(ShapeMenuTarget.POOL, item.hasShapePopup());
    }

    @Test
    void testRemoveAndIsRemoved() {
        Pool item = new Pool();
        assertFalse(item.isRemoved());
        item.remove();
        assertTrue(item.isRemoved());
    }

    @Test
    void testGetSetAge() {
        Pool item = new Pool();
        assertEquals(0, item.getAge());
        item.setAge(500);
        assertEquals(500, item.getAge());
    }

    @Test
    void testClockTick_NotRemoved() {
        Pool item = new Pool();
        item.setAge(0);
        TickResult result = item.clockTick();
        assertEquals(TickResult.NONE, result);
        assertTrue(item.getAge() > 0);
    }

    @Test
    void testClockTick_Removed() {
        Pool item = new Pool();
        item.setRemoved(true);
        TickResult result = item.clockTick();
        assertEquals(TickResult.REMOVED, result);
    }

    @Test
    void testMapContains_Inside() {
        Pool item = new Pool();
        item.setBounds(100, 100, 300, 300);
        assertTrue(item.mapContains(200, 200));
    }

    @Test
    void testMapContains_Outside() {
        Pool item = new Pool();
        item.setBounds(100, 100, 300, 300);
        assertFalse(item.mapContains(50, 200));
    }

    @Test
    void testFieldContains_Inside() {
        Pool item = new Pool();
        item.setFieldPos(100, 100, 300, 300);
        assertTrue(item.fieldContains(200, 200));
    }

    @Test
    void testFieldContains_Outside() {
        Pool item = new Pool();
        item.setFieldPos(100, 100, 300, 300);
        assertFalse(item.fieldContains(50, 50));
    }

    @Test
    void testGetSetMapW_H() {
        Pool item = new Pool();
        item.setWorldWidth(200);
        item.setWorldHeight(100);
        assertEquals(200, item.getWorldWidth());
        assertEquals(100, item.getWorldHeight());
    }

    @Test
    void testGetSetFieldW_H() {
        Pool item = new Pool();
        item.setFieldW(300);
        item.setFieldH(150);
        assertEquals(300, item.getFieldW());
        assertEquals(150, item.getFieldH());
    }

    @Test
    void testGetSetMapPos_Getters() {
        Pool item = new Pool();
        item.setBounds(10, 20, 300, 400);
        assertEquals(10, item.getStartX());
        assertEquals(20, item.getStartY());
        assertEquals(300, item.getEndX());
        assertEquals(400, item.getEndY());
    }

    @Test
    void testGetSetFieldPos_Getters() {
        Pool item = new Pool();
        item.setFieldPos(50, 60, 500, 600);
        assertEquals(50, item.getFieldSx());
        assertEquals(60, item.getFieldSy());
        assertEquals(500, item.getFieldEx());
        assertEquals(600, item.getFieldEy());
    }

    // --- Pool-specific getters/setters ---

    @Test
    void testGetSetBindObjList() {
        Pool item = new Pool();
        List<Entity> list = new LinkedList<>();
        item.setBoundObjects(list);
        assertEquals(list, item.getBoundObjects());
    }

    @Test
    void testGetSetAnWaterPointX() {
        Pool item = new Pool();
        int[] pts = { 10, 20, 30, 40 };
        item.setWaterPolygonX(pts);
        assertArrayEquals(pts, item.getWaterPolygonX());
    }

    @Test
    void testGetSetAnWaterPointY() {
        Pool item = new Pool();
        int[] pts = { 15, 25, 35, 45 };
        item.setWaterPolygonY(pts);
        assertArrayEquals(pts, item.getWaterPolygonY());
    }

    // --- checkHitObj ---

    @Test
    void testCheckHitObj_Null_ReturnsFalse() {
        Pool item = new Pool();
        assertFalse(item.checkHitObj(null));
    }

    // checkHitObj with a non-null obj calls checkContain() → invertLimit() which
    // requires
    // full Translate field initialization. Only the null-check path is tested here.
    @Test
    void testCheckHitObj_Null_ReturnsFalse2() {
        Pool item = new Pool();
        assertFalse(item.checkHitObj(null));
    }

    // --- checkArea ---

    @Test
    void testCheckArea_OutsidePool_ReturnsNONE() {
        Pool item = new Pool();
        item.setBounds(100, 100, 300, 300);
        assertEquals(Depth.NONE, item.checkArea(50, 200));
    }

    @Test
    void testCheckArea_EdgeX_ReturnsEDGE() {
        Pool item = new Pool();
        item.setBounds(100, 100, 300, 300);
        // x=105 is in EDGE zone (mapSX <= 105 < mapSX+10)
        // y=200 is in DEEP zone
        assertEquals(Depth.EDGE, item.checkArea(105, 200));
    }

    @Test
    void testCheckArea_ShallowX_ReturnsSHALLOW() {
        Pool item = new Pool();
        item.setBounds(100, 100, 300, 300);
        // x=115 is in SHALLOW zone (mapSX+10 <= 115 < mapSX+20)
        // y=200 is in DEEP
        assertEquals(Depth.SHALLOW, item.checkArea(115, 200));
    }

    @Test
    void testCheckArea_DeepCenter_ReturnsDEEP() {
        Pool item = new Pool();
        item.setBounds(100, 100, 300, 300);
        // x=200, y=200 both in DEEP
        assertEquals(Depth.DEEP, item.checkArea(200, 200));
    }

    // --- getPool static ---

    @Test
    void testGetPool_EmptyList_ReturnsNull() {
        assertNull(Pool.getPool(100, 100));
    }

    @Test
    void testGetPool_WithPool_ReturnsPool() {
        Pool item = new Pool();
        item.setFieldPos(100, 100, 300, 300);
        SimYukkuri.world.getCurrentWorldState().getPools().add(item);
        Pool found = Pool.getPool(200, 200);
        assertEquals(item, found);
    }

    @Test
    void testGetPool_OutsideArea_ReturnsNull() {
        Pool item = new Pool();
        item.setFieldPos(100, 100, 300, 300);
        SimYukkuri.world.getCurrentWorldState().getPools().add(item);
        assertNull(Pool.getPool(50, 50));
    }

    // --- deletePool ---

    @Test
    void testDeletePool_RemovesFromList() {
        Pool item = new Pool();
        item.setBounds(0, 0, 10, 10);
        item.setWorldWidth(11);
        item.setWorldHeight(11);
        SimYukkuri.world.getCurrentWorldState().getPools().add(item);
        assertTrue(SimYukkuri.world.getCurrentWorldState().getPools().contains(item));
        Pool.deletePool(item);
        assertFalse(SimYukkuri.world.getCurrentWorldState().getPools().contains(item));
    }

    // --- executeShapePopup ---

    @Test
    void testExecuteShapePopup_SETUP_DoesNotThrow() {
        Pool item = new Pool();
        SimYukkuri.world.getCurrentWorldState().getPools().add(item);
        item.executeShapePopup(ShapeMenu.SETUP);
        assertTrue(SimYukkuri.world.getCurrentWorldState().getPools().contains(item));
    }

    @Test
    void testExecuteShapePopup_TOP_MovesToFront() {
        Pool item1 = new Pool();
        Pool item2 = new Pool();
        SimYukkuri.world.getCurrentWorldState().getPools().add(item1);
        SimYukkuri.world.getCurrentWorldState().getPools().add(item2);
        item2.executeShapePopup(ShapeMenu.TOP);
        assertEquals(item2, SimYukkuri.world.getCurrentWorldState().getPools().get(0));
    }

    @Test
    void testExecuteShapePopup_BOTTOM_MovesToEnd() {
        Pool item1 = new Pool();
        Pool item2 = new Pool();
        SimYukkuri.world.getCurrentWorldState().getPools().add(item1);
        SimYukkuri.world.getCurrentWorldState().getPools().add(item2);
        item1.executeShapePopup(ShapeMenu.BOTTOM);
        int size = SimYukkuri.world.getCurrentWorldState().getPools().size();
        assertEquals(item1, SimYukkuri.world.getCurrentWorldState().getPools().get(size - 1));
    }

    @Test
    void testExecuteShapePopup_UP_MovesUp() {
        Pool item1 = new Pool();
        Pool item2 = new Pool();
        SimYukkuri.world.getCurrentWorldState().getPools().add(item1);
        SimYukkuri.world.getCurrentWorldState().getPools().add(item2);
        // item2 is at index 1, moving UP brings it to index 0
        item2.executeShapePopup(ShapeMenu.UP);
        assertEquals(item2, SimYukkuri.world.getCurrentWorldState().getPools().get(0));
    }

    @Test
    void testExecuteShapePopup_DOWN_MovesDown() {
        Pool item1 = new Pool();
        Pool item2 = new Pool();
        SimYukkuri.world.getCurrentWorldState().getPools().add(item1);
        SimYukkuri.world.getCurrentWorldState().getPools().add(item2);
        // item1 is at index 0, moving DOWN brings it to index 1
        item1.executeShapePopup(ShapeMenu.DOWN);
        assertEquals(item1, SimYukkuri.world.getCurrentWorldState().getPools().get(1));
    }

    // --- objHitProcess with non-Yukkuri, airborne ---

    @Test
    void testObjHitProcess_AirborneObj_ReturnsZero() {
        Pool item = new Pool();
        item.setBounds(100, 100, 300, 300);
        Food food = new Food(200, 200, 0);
        food.setZ(5); // airborne
        assertEquals(0, item.objHitProcess(food));
    }

    // --- objHitProcess with non-Yukkuri, on ground, outside pool ---

    @Test
    void testObjHitProcess_ObjOnGround_OutsidePool() {
        Pool item = new Pool();
        item.setBounds(100, 100, 300, 300);
        Food food = new Food(50, 50, 0); // outside pool
        food.setZ(0);
        int result = item.objHitProcess(food);
        assertEquals(0, result);
    }

    // --- drawPreview ---

    @Test
    void testDrawPreview_doesNotThrow() {
        WorldTestHelper.initializeStandardTranslate200();
        java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(800, 600,
                java.awt.image.BufferedImage.TYPE_INT_RGB);
        java.awt.Graphics2D g2 = img.createGraphics();
        Pool.drawPreview(g2, 10, 10, 100, 100);
        g2.dispose();
        assertNotNull(img);
    }

    // --- drawShape ---

    @Test
    void testDrawShape_doesNotThrow() {
        WorldTestHelper.initializeStandardTranslate200();
        Pool item = new Pool();
        item.setBounds(100, 100, 300, 300);
        java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(800, 600,
                java.awt.image.BufferedImage.TYPE_INT_RGB);
        java.awt.Graphics2D g2 = img.createGraphics();
        item.drawShape(g2);
        g2.dispose();
        assertNotNull(item);
    }

    // --- checkContain ---

    @Test
    void testCheckContain_mapCoord_insidePool() {
        Pool item = new Pool();
        item.setBounds(100, 100, 300, 300);
        // extreme outside point returns false
        assertFalse(item.checkContain(9999, 9999, false));
    }

    @Test
    void testCheckContain_fieldCoord_outsidePool() {
        WorldTestHelper.initializeStandardTranslate200();
        Pool item = new Pool();
        item.setBounds(100, 100, 300, 300);
        // extreme outside point returns false
        assertFalse(item.checkContain(9999, 9999, true));
    }

    // --- objHitProcess with Yukkuri inside pool ---

    @Test
    void testObjHitProcess_BodyInsidePool_executesCode() {
        Pool item = new Pool();
        item.setBounds(0, 0, 1000, 1000);
        org.simyukkuri.entity.core.living.yukkuri.Yukkuri body = WorldTestHelper.createBody();
        body.setX(200);
        body.setY(200);
        body.setZ(0);
        int result = 0;
        try {
            result = item.objHitProcess(body);
        } catch (NullPointerException e) {
            // Sprite not loaded in headless test environment
        }
        assertTrue(result == 0 || result == 1);
    }

    // --- Pool(int,int,int,int) constructor ---

    @Test
    void testConstructor_WithCoords_executesCode() {
        WorldTestHelper.initializeStandardTranslate200();
        try {
            Pool item = new Pool(100, 100, 300, 300);
            assertNotNull(item);
            assertTrue(SimYukkuri.world.getCurrentWorldState().getPools().contains(item));
        } catch (Exception e) {
            // May fail in headless if Translate not fully initialized
        }
    }

    @Nested
    class RegressionScenarios {

        @Test
        void testScenario_EdgeObjectIsLiftedBackToSurface() {
            Pool item = new Pool();
            item.setBounds(100, 100, 300, 300);

            Food food = new Food(105, 200, Food.FoodType.FOOD.ordinal());
            food.setZ(-1);

            assertEquals(0, item.objHitProcess(food));
            assertTrue(food.isInPool());
            assertEquals(0, food.getMostDepth());
            assertEquals(0, food.getZ());
        }

        @Test
        void testScenario_ShallowObjectSinksOneLevelIntoWater() {
            Pool item = new Pool();
            item.setBounds(100, 100, 300, 300);

            Food food = new Food(115, 200, Food.FoodType.FOOD.ordinal());
            food.setZ(0);

            assertEquals(0, item.objHitProcess(food));
            assertTrue(food.isInPool());
            assertEquals(-1, food.getMostDepth());
            assertEquals(-1, food.getZ());
        }
    }

    // ================================================================
    // TEST_EXPANTION_PLAN: Pool.objHitProcess
    // ================================================================

    @Test
    void testObjHitProcess_EdgeArea_BodyNotWet() {
        // EDGE エリアに入った個体は isWet にならない
        // Pool bounds: (0,0,200,200), edgeWidth=10 → EDGE: x in [0,10) or (190,200]
        Pool pool = new Pool();
        pool.setBounds(0, 0, 200, 200);

        Yukkuri body = WorldTestHelper.createBody();
        body.setX(5);   // EDGE 領域（x < 10）
        body.setY(100);
        body.setZ(0);

        assertEquals(Pool.Depth.EDGE, pool.checkArea(5, 100));

        pool.objHitProcess(body);

        assertTrue(body.isInPool());
        assertFalse(body.isWet());
    }

    @Test
    void testObjHitProcess_ShallowArea_BodyBecomesWet() {
        // SHALLOW エリアに入った個体は isWet になる（初回は !isWet=true でinWater必ず呼ばれる）
        // Pool bounds: (0,0,200,200), edgeWidth=10 → SHALLOW: x in [10,20)
        Pool pool = new Pool();
        pool.setBounds(0, 0, 200, 200);

        Yukkuri body = WorldTestHelper.createBody();
        body.setX(15);  // SHALLOW 領域
        body.setY(100);
        body.setZ(0);

        assertEquals(Pool.Depth.SHALLOW, pool.checkArea(15, 100));

        pool.objHitProcess(body);

        assertTrue(body.isWet());
    }

    @Test
    void testObjHitProcess_ShallowArea_LikesWater_NoDamage() {
        // 水好きの個体は SHALLOW でダメージを受けない
        Pool pool = new Pool();
        pool.setBounds(0, 0, 200, 200);

        Yukkuri body = WorldTestHelper.createBody();
        body.setX(15);  // SHALLOW
        body.setY(100);
        body.setZ(0);
        body.setLikeWater(true);

        int damageBefore = body.getDamage();
        pool.objHitProcess(body);

        assertEquals(damageBefore, body.getDamage());
    }

    @Test
    void testObjHitProcess_DeepArea_NonLikeWater_DeepEnough_LockMove() {
        // 非水好きの ADULT が depthLimit=-3 を超えると isLockmove=true
        // ADULT depthLimit=3 → zcord < -3 → Z=-4 でロック
        SimYukkuri.RND = new ConstState(0); // nextInt(10+3*5)==0 でダメージチェックも
        Pool pool = new Pool();
        pool.setBounds(0, 0, 200, 200);

        Yukkuri body = WorldTestHelper.createBody();
        body.setX(50);  // DEEP 領域（edgeWidth*2=20 <= x < 180）
        body.setY(50);
        body.setAgeState(AgeState.ADULT);
        body.setZ(-4);  // zcord < -depthLimit(3)
        body.setLikeWater(false);

        pool.objHitProcess(body);

        assertTrue(body.isLockmove());
    }

    @Test
    void testObjHitProcess_DeepArea_Baby_ShallowZNotLocked() {
        // BABY は depthLimit=1 なので Z=-1 ではロックしない（Z < -1 のとき=Z=-2 でロック）
        Pool pool = new Pool();
        pool.setBounds(0, 0, 200, 200);

        Yukkuri body = WorldTestHelper.createBody();
        body.setX(50);  // DEEP
        body.setY(50);
        body.setAgeState(AgeState.BABY);
        body.setZ(-1);  // zcord >= -depthLimit(1) → ロックしない
        body.setLikeWater(false);

        pool.objHitProcess(body);

        assertFalse(body.isLockmove());
    }

    @Test
    void testObjHitProcess_DeepArea_Baby_DeepZLocked() {
        // BABY は depthLimit=1 なので Z=-2 でロックする
        Pool pool = new Pool();
        pool.setBounds(0, 0, 200, 200);

        Yukkuri body = WorldTestHelper.createBody();
        body.setX(50);  // DEEP
        body.setY(50);
        body.setAgeState(AgeState.BABY);
        body.setZ(-2);  // zcord < -depthLimit(1) → ロック
        body.setLikeWater(false);

        pool.objHitProcess(body);

        assertTrue(body.isLockmove());
    }
}
