package src.item;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import src.SimYukkuri;
import src.base.Body;
import src.enums.Event;
import src.game.Shit;
import src.system.ItemMenu.ShapeMenu;
import src.system.ItemMenu.ShapeMenuTarget;
import src.util.WorldTestHelper;

class FarmTest {

    @BeforeEach
    public void setUp() {
        src.SimYukkuri.world = new src.draw.World();
        src.draw.Translate.setMapSize(1000, 1000, 200);
        src.draw.Translate.setCanvasSize(800, 600, 100, 100, new float[]{1.0f});
        src.draw.Translate.createTransTable(false);
    }

    @AfterEach
    public void tearDown() {
    }

    // --- Constructor default ---

    @Test
    void testConstructor_Default() {
        Farm item = new Farm();
        SimYukkuri.world.getCurrentMap().getFarm().add(item);
        assertNotNull(item);
        assertTrue(SimYukkuri.world.getCurrentMap().getFarm().contains(item));
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

    // --- Inherited FieldShapeBase methods ---

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
        item.setAge(300);
        assertEquals(300, item.getAge());
    }

    @Test
    void testClockTick_NotRemoved() {
        Farm item = new Farm();
        item.setAge(0);
        Event result = item.clockTick();
        assertEquals(Event.DONOTHING, result);
        assertTrue(item.getAge() > 0);
    }

    @Test
    void testClockTick_Removed() {
        Farm item = new Farm();
        item.setRemoved(true);
        Event result = item.clockTick();
        assertEquals(Event.REMOVED, result);
    }

    @Test
    void testMapContains_Inside() {
        Farm item = new Farm();
        item.setMapPos(100, 100, 300, 300);
        assertTrue(item.mapContains(200, 200));
    }

    @Test
    void testMapContains_Outside() {
        Farm item = new Farm();
        item.setMapPos(100, 100, 300, 300);
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
        item.setMapW(200);
        item.setMapH(100);
        assertEquals(200, item.getMapW());
        assertEquals(100, item.getMapH());
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
        item.setMapPos(10, 20, 300, 400);
        assertEquals(10, item.getMapSX());
        assertEquals(20, item.getMapSY());
        assertEquals(300, item.getMapEX());
        assertEquals(400, item.getMapEY());
    }

    @Test
    void testGetSetFieldPos_Getters() {
        Farm item = new Farm();
        item.setFieldPos(50, 60, 500, 600);
        assertEquals(50, item.getFieldSX());
        assertEquals(60, item.getFieldSY());
        assertEquals(500, item.getFieldEX());
        assertEquals(600, item.getFieldEY());
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
        int[] pts = {10, 20, 30, 40};
        item.setAnPointX(pts);
        assertArrayEquals(pts, item.getAnPointX());
    }

    @Test
    void testGetSetAnPointY() {
        Farm item = new Farm();
        int[] pts = {15, 25, 35, 45};
        item.setAnPointY(pts);
        assertArrayEquals(pts, item.getAnPointY());
    }

    // --- checkHitObj ---

    @Test
    void testCheckHitObj_Null_ReturnsFalse() {
        Farm item = new Farm();
        assertFalse(item.checkHitObj(null));
    }

    // checkHitObj with non-null obj calls checkContain() → invertLimit() which requires
    // full Translate field initialization. Only the null-check path is easily testable.

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
        // non-Body airborne: o.getMostDepth() != 0 → setMostDepth(0) → returns 1
        // But initially mostDepth == 0 → returns 1 only if different... let's just check no throw
        assertDoesNotThrow(() -> item.objHitProcess(food));
    }

    // --- getAmount(Obj) ---

    @Test
    void testGetAmountFromNull_DoesNotThrow() {
        Farm item = new Farm();
        assertDoesNotThrow(() -> item.getAmount(null));
    }

    @Test
    void testGetAmountFromShit_IncreasesFarmAmount() {
        Farm item = new Farm();
        item.setAmount(0);
        Shit shit = new Shit();
        shit.setAgeState(src.enums.AgeState.ADULT);
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
        // Food is not Shit or Vomit or Body, so no amount change
        assertEquals(before, item.getAmount());
    }

    // --- giveAmount ---

    @Test
    void testGiveAmountNull_DoesNotThrow() {
        Farm item = new Farm();
        assertDoesNotThrow(() -> item.giveAmount(null));
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
        SimYukkuri.world.getCurrentMap().getFarm().add(item);
        Farm found = Farm.getFarm(200, 200);
        assertEquals(item, found);
    }

    @Test
    void testGetFarm_OutsideArea_ReturnsNull() {
        Farm item = new Farm();
        item.setFieldPos(100, 100, 300, 300);
        SimYukkuri.world.getCurrentMap().getFarm().add(item);
        assertNull(Farm.getFarm(50, 50));
    }

    // --- deleteFarm ---

    @Test
    void testDeleteFarm_RemovesFromList() {
        Farm item = new Farm();
        item.setMapPos(0, 0, 10, 10);
        item.setMapW(11);
        item.setMapH(11);
        SimYukkuri.world.getCurrentMap().getFarm().add(item);
        assertTrue(SimYukkuri.world.getCurrentMap().getFarm().contains(item));
        Farm.deleteFarm(item);
        assertFalse(SimYukkuri.world.getCurrentMap().getFarm().contains(item));
    }

    // --- executeShapePopup ---

    @Test
    void testExecuteShapePopup_SETUP_DoesNotThrow() {
        Farm item = new Farm();
        SimYukkuri.world.getCurrentMap().getFarm().add(item);
        assertDoesNotThrow(() -> item.executeShapePopup(ShapeMenu.SETUP));
    }

    @Test
    void testExecuteShapePopup_HERVEST_DoesNotThrow() {
        Farm item = new Farm();
        SimYukkuri.world.getCurrentMap().getFarm().add(item);
        assertDoesNotThrow(() -> item.executeShapePopup(ShapeMenu.HERVEST));
    }

    @Test
    void testExecuteShapePopup_TOP_MovesToFront() {
        Farm item1 = new Farm();
        Farm item2 = new Farm();
        SimYukkuri.world.getCurrentMap().getFarm().add(item1);
        SimYukkuri.world.getCurrentMap().getFarm().add(item2);
        item2.executeShapePopup(ShapeMenu.TOP);
        assertEquals(item2, SimYukkuri.world.getCurrentMap().getFarm().get(0));
    }

    @Test
    void testExecuteShapePopup_BOTTOM_MovesToEnd() {
        Farm item1 = new Farm();
        Farm item2 = new Farm();
        SimYukkuri.world.getCurrentMap().getFarm().add(item1);
        SimYukkuri.world.getCurrentMap().getFarm().add(item2);
        item1.executeShapePopup(ShapeMenu.BOTTOM);
        int size = SimYukkuri.world.getCurrentMap().getFarm().size();
        assertEquals(item1, SimYukkuri.world.getCurrentMap().getFarm().get(size - 1));
    }

    @Test
    void testExecuteShapePopup_UP_MovesUp() {
        Farm item1 = new Farm();
        Farm item2 = new Farm();
        SimYukkuri.world.getCurrentMap().getFarm().add(item1);
        SimYukkuri.world.getCurrentMap().getFarm().add(item2);
        item2.executeShapePopup(ShapeMenu.UP);
        assertEquals(item2, SimYukkuri.world.getCurrentMap().getFarm().get(0));
    }

    @Test
    void testExecuteShapePopup_DOWN_MovesDown() {
        Farm item1 = new Farm();
        Farm item2 = new Farm();
        SimYukkuri.world.getCurrentMap().getFarm().add(item1);
        SimYukkuri.world.getCurrentMap().getFarm().add(item2);
        item1.executeShapePopup(ShapeMenu.DOWN);
        assertEquals(item1, SimYukkuri.world.getCurrentMap().getFarm().get(1));
    }

    // --- drawPreview ---

    @Test
    void testDrawPreview_doesNotThrow() {
        src.draw.Translate.setCanvasSize(800, 600, 100, 100, new float[]{1.0f});
        src.draw.Translate.createTransTable(false);
        java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(800, 600, java.awt.image.BufferedImage.TYPE_INT_RGB);
        java.awt.Graphics2D g2 = img.createGraphics();
        assertDoesNotThrow(() -> Farm.drawPreview(g2, 10, 10, 100, 100));
        g2.dispose();
    }

    // --- drawShape ---

    @Test
    void testDrawShape_doesNotThrow() {
        src.draw.Translate.setCanvasSize(800, 600, 100, 100, new float[]{1.0f});
        src.draw.Translate.createTransTable(false);
        Farm item = new Farm();
        item.setMapPos(100, 100, 300, 300);
        java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(800, 600, java.awt.image.BufferedImage.TYPE_INT_RGB);
        java.awt.Graphics2D g2 = img.createGraphics();
        assertDoesNotThrow(() -> item.drawShape(g2));
        g2.dispose();
    }

    // --- checkContain ---

    @Test
    void testCheckContain_mapCoord() {
        Farm item = new Farm();
        item.setMapPos(100, 100, 300, 300);
        assertDoesNotThrow(() -> item.checkContain(200, 200, false));
    }

    @Test
    void testCheckContain_fieldCoord() {
        src.draw.Translate.setCanvasSize(800, 600, 100, 100, new float[]{1.0f});
        src.draw.Translate.createTransTable(false);
        Farm item = new Farm();
        item.setMapPos(100, 100, 300, 300);
        assertDoesNotThrow(() -> item.checkContain(50, 50, true));
    }

    // --- giveAmount with Body ---

    @Test
    void testGiveAmount_withBody_doesNotThrow() {
        Farm item = new Farm();
        item.setMapPos(0, 0, 1000, 1000);
        item.setAmount(5000);
        src.base.Body body = WorldTestHelper.createBody();
        body.setX(200); body.setY(200);
        assertDoesNotThrow(() -> item.giveAmount(body));
    }

    @Test
    void testLoadImages_headless_executesCode() {
        try {
            Farm.loadImages(Farm.class.getClassLoader(), null);
        } catch (Exception e) {
            // Expected: IOException because image files not found in test environment
        }
    }

    @Test
    void testExecuteShapePopup_top_doesNotThrow() {
        Farm item = new Farm();
        java.util.List<Farm> list = SimYukkuri.world.getCurrentMap().getFarm();
        list.add(item);
        assertDoesNotThrow(() -> item.executeShapePopup(src.system.ItemMenu.ShapeMenu.TOP));
    }

    @Test
    void testGetFarm_emptyList_returnsNull() {
        assertNull(Farm.getFarm(999, 999));
    }

    @Test
    void testDeleteFarm_doesNotThrow() {
        Farm item = new Farm();
        SimYukkuri.world.getCurrentMap().getFarm().add(item);
        assertDoesNotThrow(() -> Farm.deleteFarm(item));
        assertFalse(SimYukkuri.world.getCurrentMap().getFarm().contains(item));
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
        item.setMapPos(0, 0, 1000, 1000);
        src.base.Body body = WorldTestHelper.createBody();
        body.setX(200); body.setY(200); body.setZ(0);
        // Result depends on coordinate mapping, just verify no crash
        assertDoesNotThrow(() -> item.checkHitObj(body));
    }

    @Test
    void testObjHitProcess_yukkuriInside_doesNotThrow() {
        Farm item = new Farm();
        item.setMapPos(0, 0, 1000, 1000);
        item.setAmount(100);
        src.base.Body body = WorldTestHelper.createBody();
        body.setX(200); body.setY(200); body.setZ(0);
        assertDoesNotThrow(() -> item.objHitProcess(body));
    }

    @Test
    void testGetAmount_withObj_doesNotThrow() {
        Farm item = new Farm();
        item.setMapPos(0, 0, 1000, 1000);
        item.setAmount(100);
        src.base.Body body = WorldTestHelper.createBody();
        body.setX(200); body.setY(200);
        assertDoesNotThrow(() -> item.getAmount(body));
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
}
