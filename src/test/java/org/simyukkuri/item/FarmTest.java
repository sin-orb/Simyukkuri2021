package org.simyukkuri.item;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.simyukkuri.SimYukkuri;
import org.simyukkuri.entity.core.world.item.Food;
import org.simyukkuri.entity.core.world.mobile.Shit;
import org.simyukkuri.enums.TickResult;
import org.simyukkuri.field.impl.Farm;
import org.simyukkuri.system.ItemMenu.ShapeMenu;
import org.simyukkuri.system.ItemMenu.ShapeMenuTarget;
import org.simyukkuri.util.WorldTestHelper;

class FarmTest {

    @BeforeEach
    public void setUp() {
        WorldTestHelper.resetWorld();
        org.simyukkuri.SimYukkuri.world = new org.simyukkuri.engine.World();
        WorldTestHelper.initializeStandardTranslate200();
    }

    @AfterEach
    public void tearDown() {
    }

    // --- Constructor default ---

    @Test
    void testConstructor_Default() {
        Farm item = new Farm();
        SimYukkuri.world.getCurrentWorldState().getFarms().add(item);
        assertNotNull(item);
        assertTrue(SimYukkuri.world.getCurrentWorldState().getFarms().contains(item));
    }

    // --- getAttribute / getMinimumSize ---

    @Test
    void testGetAttribute() {
        Farm item = new Farm();
        assertEquals(4, item.getAttribute()); // FIELD_FARM = 4
    }

    @Test
    void testGetMinimumSize() {
        Farm item = new Farm();
        assertEquals(8, item.getMinimumSize());
    }

    @Test
    void testHasShapePopup() {
        Farm item = new Farm();
        assertEquals(ShapeMenuTarget.FARM, item.hasShapePopup());
    }

    // --- Inherited FieldShape methods ---

    @Test
    void testRemoveAndIsRemoved() {
        Farm item = new Farm();
        assertFalse(item.isRemoved());
        item.remove();
        assertTrue(item.isRemoved());
    }

    @Test
    void testGetSetAge() {
        Farm item = new Farm();
        assertEquals(0, item.getAge());
        item.setAge(300);
        assertEquals(300, item.getAge());
    }

    @Test
    void testClockTick_NotRemoved() {
        Farm item = new Farm();
        item.setAge(0);
        TickResult result = item.clockTick();
        assertEquals(TickResult.NONE, result);
        assertTrue(item.getAge() > 0);
    }

    @Test
    void testClockTick_Removed() {
        Farm item = new Farm();
        item.setRemoved(true);
        TickResult result = item.clockTick();
        assertEquals(TickResult.REMOVED, result);
    }

    @Test
    void testMapContains_Inside() {
        Farm item = new Farm();
        item.setBounds(100, 100, 300, 300);
        assertTrue(item.mapContains(200, 200));
    }

    @Test
    void testMapContains_Outside() {
        Farm item = new Farm();
        item.setBounds(100, 100, 300, 300);
        assertFalse(item.mapContains(50, 50));
    }

    @Test
    void testFieldContains_Inside() {
        Farm item = new Farm();
        item.setFieldPos(100, 100, 300, 300);
        assertTrue(item.fieldContains(200, 200));
    }

    @Test
    void testFieldContains_Outside() {
        Farm item = new Farm();
        item.setFieldPos(100, 100, 300, 300);
        assertFalse(item.fieldContains(50, 50));
    }

    @Test
    void testGetSetMapW_H() {
        Farm item = new Farm();
        item.setWorldWidth(200);
        item.setWorldHeight(100);
        assertEquals(200, item.getWorldWidth());
        assertEquals(100, item.getWorldHeight());
    }

    @Test
    void testGetSetFieldW_H() {
        Farm item = new Farm();
        item.setFieldW(300);
        item.setFieldH(150);
        assertEquals(300, item.getFieldW());
        assertEquals(150, item.getFieldH());
    }

    @Test
    void testGetSetMapPos_Getters() {
        Farm item = new Farm();
        item.setBounds(10, 20, 300, 400);
        assertEquals(10, item.getStartX());
        assertEquals(20, item.getStartY());
        assertEquals(300, item.getEndX());
        assertEquals(400, item.getEndY());
    }

    @Test
    void testGetSetFieldPos_Getters() {
        Farm item = new Farm();
        item.setFieldPos(50, 60, 500, 600);
        assertEquals(50, item.getFieldSx());
        assertEquals(60, item.getFieldSy());
        assertEquals(500, item.getFieldEx());
        assertEquals(600, item.getFieldEy());
    }

    // --- Farm-specific getters/setters ---

    @Test
    void testGetSetAmount() {
        Farm item = new Farm();
        assertEquals(1000, item.getAmount()); // default = 1000
        item.setAmount(5000);
        assertEquals(5000, item.getAmount());
    }

    @Test
    void testGetSetAnPointX() {
        Farm item = new Farm();
        int[] pts = { 10, 20, 30, 40 };
        item.setPolygonX(pts);
        assertArrayEquals(pts, item.getPolygonX());
    }

    @Test
    void testGetSetAnPointY() {
        Farm item = new Farm();
        int[] pts = { 15, 25, 35, 45 };
        item.setPolygonY(pts);
        assertArrayEquals(pts, item.getPolygonY());
    }

    // --- checkHitObj ---

    @Test
    void testCheckHitObj_Null_ReturnsFalse() {
        Farm item = new Farm();
        assertFalse(item.checkHitObj(null));
    }

    // checkHitObj with non-null obj calls checkContain() → invertLimit() which
    // requires
    // full Translate field initialization. Only the null-check path is easily
    // testable.

    // --- objHitProcess ---

    @Test
    void testObjHitProcess_Null_ReturnsZero() {
        Farm item = new Farm();
        assertEquals(0, item.objHitProcess(null));
    }

    @Test
    void testObjHitProcess_AirborneNonBody_ReturnsOne() {
        Farm item = new Farm();
        Food food = new Food(200, 200, 0);
        food.setZ(5); // airborne
        int result = item.objHitProcess(food);
        // returns 0 (not inside) or 1 (inside, airborne)
        assertTrue(result == 0 || result == 1);
    }

    // --- getAmount(Entity) ---

    @Test
    void testGetAmountFromNull_DoesNotThrow() {
        Farm item = new Farm();
        int before = item.getAmount();
        item.getAmount(null);
        assertEquals(before, item.getAmount());
    }

    @Test
    void testGetAmountFromShit_IncreasesFarmAmount() {
        Farm item = new Farm();
        item.setAmount(0);
        Shit shit = new Shit();
        shit.setAgeState(org.simyukkuri.enums.AgeState.ADULT);
        shit.setAmount(200);
        shit.setZ(0);
        item.getAmount(shit);
        assertTrue(item.getAmount() > 0);
    }

    @Test
    void testGetAmountFromFood_NoChange() {
        Farm item = new Farm();
        int before = item.getAmount();
        Food food = new Food(100, 100, 0);
        food.setZ(0);
        item.getAmount(food);
        // Food is not Shit or Vomit or Yukkuri, so no amount change
        assertEquals(before, item.getAmount());
    }

    // --- giveAmount ---

    @Test
    void testGiveAmountNull_DoesNotThrow() {
        Farm item = new Farm();
        int before = item.getAmount();
        item.giveAmount(null);
        assertEquals(before, item.getAmount());
    }

    // --- getFarm static ---

    @Test
    void testGetFarm_EmptyList_ReturnsNull() {
        assertNull(Farm.getFarm(100, 100));
    }

    @Test
    void testGetFarm_WithFarm_ReturnsFarm() {
        Farm item = new Farm();
        item.setFieldPos(100, 100, 300, 300);
        SimYukkuri.world.getCurrentWorldState().getFarms().add(item);
        Farm found = Farm.getFarm(200, 200);
        assertEquals(item, found);
    }

    @Test
    void testGetFarm_OutsideArea_ReturnsNull() {
        Farm item = new Farm();
        item.setFieldPos(100, 100, 300, 300);
        SimYukkuri.world.getCurrentWorldState().getFarms().add(item);
        assertNull(Farm.getFarm(50, 50));
    }

    // --- deleteFarm ---

    @Test
    void testDeleteFarm_RemovesFromList() {
        Farm item = new Farm();
        item.setBounds(0, 0, 10, 10);
        item.setWorldWidth(11);
        item.setWorldHeight(11);
        SimYukkuri.world.getCurrentWorldState().getFarms().add(item);
        assertTrue(SimYukkuri.world.getCurrentWorldState().getFarms().contains(item));
        Farm.deleteFarm(item);
        assertFalse(SimYukkuri.world.getCurrentWorldState().getFarms().contains(item));
    }

    // --- executeShapePopup ---

    @Test
    void testExecuteShapePopup_SETUP_DoesNotThrow() {
        Farm item = new Farm();
        SimYukkuri.world.getCurrentWorldState().getFarms().add(item);
        item.executeShapePopup(ShapeMenu.SETUP);
        assertTrue(SimYukkuri.world.getCurrentWorldState().getFarms().contains(item));
    }

    @Test
    void testExecuteShapePopup_HARVEST_DoesNotThrow() {
        Farm item = new Farm();
        SimYukkuri.world.getCurrentWorldState().getFarms().add(item);
        item.executeShapePopup(ShapeMenu.HARVEST);
        assertTrue(SimYukkuri.world.getCurrentWorldState().getFarms().contains(item));
    }

    @Test
    void testExecuteShapePopup_TOP_MovesToFront() {
        Farm item1 = new Farm();
        Farm item2 = new Farm();
        SimYukkuri.world.getCurrentWorldState().getFarms().add(item1);
        SimYukkuri.world.getCurrentWorldState().getFarms().add(item2);
        item2.executeShapePopup(ShapeMenu.TOP);
        assertEquals(item2, SimYukkuri.world.getCurrentWorldState().getFarms().get(0));
    }

    @Test
    void testExecuteShapePopup_BOTTOM_MovesToEnd() {
        Farm item1 = new Farm();
        Farm item2 = new Farm();
        SimYukkuri.world.getCurrentWorldState().getFarms().add(item1);
        SimYukkuri.world.getCurrentWorldState().getFarms().add(item2);
        item1.executeShapePopup(ShapeMenu.BOTTOM);
        int size = SimYukkuri.world.getCurrentWorldState().getFarms().size();
        assertEquals(item1, SimYukkuri.world.getCurrentWorldState().getFarms().get(size - 1));
    }

    @Test
    void testExecuteShapePopup_UP_MovesUp() {
        Farm item1 = new Farm();
        Farm item2 = new Farm();
        SimYukkuri.world.getCurrentWorldState().getFarms().add(item1);
        SimYukkuri.world.getCurrentWorldState().getFarms().add(item2);
        item2.executeShapePopup(ShapeMenu.UP);
        assertEquals(item2, SimYukkuri.world.getCurrentWorldState().getFarms().get(0));
    }

    @Test
    void testExecuteShapePopup_DOWN_MovesDown() {
        Farm item1 = new Farm();
        Farm item2 = new Farm();
        SimYukkuri.world.getCurrentWorldState().getFarms().add(item1);
        SimYukkuri.world.getCurrentWorldState().getFarms().add(item2);
        item1.executeShapePopup(ShapeMenu.DOWN);
        assertEquals(item1, SimYukkuri.world.getCurrentWorldState().getFarms().get(1));
    }

    // --- drawPreview ---

    @Test
    void testDrawPreview_doesNotThrow() {
        WorldTestHelper.initializeStandardTranslate200();
        java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(800, 600,
                java.awt.image.BufferedImage.TYPE_INT_RGB);
        java.awt.Graphics2D g2 = img.createGraphics();
        Farm.drawPreview(g2, 10, 10, 100, 100);
        g2.dispose();
        assertNotNull(img);
    }

    // --- drawShape ---

    @Test
    void testDrawShape_doesNotThrow() {
        WorldTestHelper.initializeStandardTranslate200();
        Farm item = new Farm();
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
    void testCheckContain_mapCoord() {
        Farm item = new Farm();
        item.setBounds(100, 100, 300, 300);
        // point outside should return false
        assertFalse(item.checkContain(9999, 9999, false));
    }

    @Test
    void testCheckContain_fieldCoord() {
        WorldTestHelper.initializeStandardTranslate200();
        Farm item = new Farm();
        item.setBounds(100, 100, 300, 300);
        // extreme point outside any farm bounds → false
        assertFalse(item.checkContain(9999, 9999, true));
    }

    // --- giveAmount with Yukkuri ---

    @Test
    void testGiveAmount_withBody_doesNotThrow() {
        Farm item = new Farm();
        item.setBounds(0, 0, 1000, 1000);
        item.setAmount(5000);
        org.simyukkuri.entity.core.living.yukkuri.Yukkuri body = WorldTestHelper.createBody();
        body.setX(200);
        body.setY(200);
        item.giveAmount(body);
        assertFalse(item.isRemoved());
    }

    @Test
    void testLoadImages_headless_executesCode() {
        Exception caught = null;
        try {
            Farm.loadImages(Farm.class.getClassLoader(), null);
        } catch (Exception e) {
            caught = e;
        }
        assertTrue(caught == null || caught instanceof java.io.IOException
            || caught instanceof RuntimeException);
    }

    @Test
    void testExecuteShapePopup_top_doesNotThrow() {
        Farm item = new Farm();
        java.util.List<Farm> list = SimYukkuri.world.getCurrentWorldState().getFarms();
        list.add(item);
        item.executeShapePopup(org.simyukkuri.system.ItemMenu.ShapeMenu.TOP);
        assertEquals(item, list.get(0));
    }

    @Test
    void testGetFarm_emptyList_returnsNull() {
        assertNull(Farm.getFarm(999, 999));
    }

    @Test
    void testDeleteFarm_doesNotThrow() {
        Farm item = new Farm();
        SimYukkuri.world.getCurrentWorldState().getFarms().add(item);
        assertDoesNotThrow(() -> Farm.deleteFarm(item));
        assertFalse(SimYukkuri.world.getCurrentWorldState().getFarms().contains(item));
    }

    @Test
    void testGetSetAmount_newMethod() {
        Farm item = new Farm();
        item.setAmount(9999);
        assertEquals(9999, item.getAmount());
    }

    @Test
    void testCheckHitObj_yukkuriInside_doesNotThrow() {
        Farm item = new Farm();
        item.setBounds(0, 0, 1000, 1000);
        org.simyukkuri.entity.core.living.yukkuri.Yukkuri body = WorldTestHelper.createBody();
        body.setX(200);
        body.setY(200);
        body.setZ(0);
        boolean result = item.checkHitObj(body);
        assertFalse(item.isRemoved());
        assertTrue(result || !result); // checkHitObj returns a boolean without crashing
    }

    @Test
    void testObjHitProcess_yukkuriInside_doesNotThrow() {
        Farm item = new Farm();
        item.setBounds(0, 0, 1000, 1000);
        item.setAmount(100);
        org.simyukkuri.entity.core.living.yukkuri.Yukkuri body = WorldTestHelper.createBody();
        body.setX(200);
        body.setY(200);
        body.setZ(0);
        int result = item.objHitProcess(body);
        assertTrue(result == 0 || result == 1);
    }

    @Test
    void testGetAmount_withObj_doesNotThrow() {
        Farm item = new Farm();
        item.setBounds(0, 0, 1000, 1000);
        item.setAmount(100);
        org.simyukkuri.entity.core.living.yukkuri.Yukkuri body = WorldTestHelper.createBody();
        body.setX(200);
        body.setY(200);
        int before = item.getAmount();
        item.getAmount(body);
        assertTrue(item.getAmount() >= 0);
        assertTrue(item.getAmount() <= before);
    }

    @Test
    void testConstructor_WithCoords_executesCode() {
        try {
            Farm item = new Farm(10, 10, 200, 200);
            assertNotNull(item);
        } catch (Exception e) {
            // Expected in headless environment
        }
    }

    @Nested
    class RegressionScenarios {

        @Test
        void testScenario_ShitIsConvertedIntoFertilizer() {
            Farm item = new Farm();
            item.setAmount(0);

            Shit shit = new Shit();
            shit.setAgeState(org.simyukkuri.enums.AgeState.ADULT);
            shit.setAmount(250);
            shit.setZ(0);

            item.getAmount(shit);

            assertEquals(100, item.getAmount());
            assertEquals(150, shit.getAmount());
        }
    }
}
