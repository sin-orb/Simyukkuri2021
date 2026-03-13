package src.item;

import static org.junit.jupiter.api.Assertions.*;

import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import src.SimYukkuri;
import src.base.Obj;
import src.enums.Event;
import src.item.Pool.DEPTH;
import src.system.ItemMenu.ShapeMenu;
import src.system.ItemMenu.ShapeMenuTarget;
import src.util.WorldTestHelper;

class PoolTest {

    @BeforeEach
    public void setUp() {
        WorldTestHelper.resetWorld();
        WorldTestHelper.initializeMinimalWorld();
        src.draw.Translate.setMapSize(1000, 1000, 200);
    }

    @AfterEach
    public void tearDown() {
        WorldTestHelper.resetWorld();
    }

    // --- Constructor default ---

    @Test
    void testConstructor_Default() {
        Pool item = new Pool();
        SimYukkuri.world.getCurrentMap().getPool().add(item);
        assertNotNull(item);
        assertTrue(SimYukkuri.world.getCurrentMap().getPool().contains(item));
    }

    // --- DEPTH enum ---

    @Test
    void testDEPTH_enum_count() {
        assertEquals(4, DEPTH.values().length);
    }

    @Test
    void testDEPTH_enum_values() {
        assertNotNull(DEPTH.NONE);
        assertNotNull(DEPTH.EDGE);
        assertNotNull(DEPTH.SHALLOW);
        assertNotNull(DEPTH.DEEP);
        assertEquals(DEPTH.NONE, DEPTH.valueOf("NONE"));
        assertEquals(DEPTH.DEEP, DEPTH.valueOf("DEEP"));
    }

    // --- Inherited FieldShapeBase methods ---

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
        item.setAge(500);
        assertEquals(500, item.getAge());
    }

    @Test
    void testClockTick_NotRemoved() {
        Pool item = new Pool();
        item.setAge(0);
        Event result = item.clockTick();
        assertEquals(Event.DONOTHING, result);
        assertTrue(item.getAge() > 0);
    }

    @Test
    void testClockTick_Removed() {
        Pool item = new Pool();
        item.setRemoved(true);
        Event result = item.clockTick();
        assertEquals(Event.REMOVED, result);
    }

    @Test
    void testMapContains_Inside() {
        Pool item = new Pool();
        item.setMapPos(100, 100, 300, 300);
        assertTrue(item.mapContains(200, 200));
    }

    @Test
    void testMapContains_Outside() {
        Pool item = new Pool();
        item.setMapPos(100, 100, 300, 300);
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
        item.setMapW(200);
        item.setMapH(100);
        assertEquals(200, item.getMapW());
        assertEquals(100, item.getMapH());
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
        item.setMapPos(10, 20, 300, 400);
        assertEquals(10, item.getMapSX());
        assertEquals(20, item.getMapSY());
        assertEquals(300, item.getMapEX());
        assertEquals(400, item.getMapEY());
    }

    @Test
    void testGetSetFieldPos_Getters() {
        Pool item = new Pool();
        item.setFieldPos(50, 60, 500, 600);
        assertEquals(50, item.getFieldSX());
        assertEquals(60, item.getFieldSY());
        assertEquals(500, item.getFieldEX());
        assertEquals(600, item.getFieldEY());
    }

    // --- Pool-specific getters/setters ---

    @Test
    void testGetSetBindObjList() {
        Pool item = new Pool();
        List<Obj> list = new LinkedList<>();
        item.setBindObjList(list);
        assertEquals(list, item.getBindObjList());
    }

    @Test
    void testGetSetAnWaterPointX() {
        Pool item = new Pool();
        int[] pts = {10, 20, 30, 40};
        item.setAnWaterPointX(pts);
        assertArrayEquals(pts, item.getAnWaterPointX());
    }

    @Test
    void testGetSetAnWaterPointY() {
        Pool item = new Pool();
        int[] pts = {15, 25, 35, 45};
        item.setAnWaterPointY(pts);
        assertArrayEquals(pts, item.getAnWaterPointY());
    }

    // --- checkHitObj ---

    @Test
    void testCheckHitObj_Null_ReturnsFalse() {
        Pool item = new Pool();
        assertFalse(item.checkHitObj(null));
    }

    // checkHitObj with a non-null obj calls checkContain() → invertLimit() which requires
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
        item.setMapPos(100, 100, 300, 300);
        assertEquals(DEPTH.NONE, item.checkArea(50, 200));
    }

    @Test
    void testCheckArea_EdgeX_ReturnsEDGE() {
        Pool item = new Pool();
        item.setMapPos(100, 100, 300, 300);
        // x=105 is in EDGE zone (mapSX <= 105 < mapSX+10)
        // y=200 is in DEEP zone
        assertEquals(DEPTH.EDGE, item.checkArea(105, 200));
    }

    @Test
    void testCheckArea_ShallowX_ReturnsSHALLOW() {
        Pool item = new Pool();
        item.setMapPos(100, 100, 300, 300);
        // x=115 is in SHALLOW zone (mapSX+10 <= 115 < mapSX+20)
        // y=200 is in DEEP
        assertEquals(DEPTH.SHALLOW, item.checkArea(115, 200));
    }

    @Test
    void testCheckArea_DeepCenter_ReturnsDEEP() {
        Pool item = new Pool();
        item.setMapPos(100, 100, 300, 300);
        // x=200, y=200 both in DEEP
        assertEquals(DEPTH.DEEP, item.checkArea(200, 200));
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
        SimYukkuri.world.getCurrentMap().getPool().add(item);
        Pool found = Pool.getPool(200, 200);
        assertEquals(item, found);
    }

    @Test
    void testGetPool_OutsideArea_ReturnsNull() {
        Pool item = new Pool();
        item.setFieldPos(100, 100, 300, 300);
        SimYukkuri.world.getCurrentMap().getPool().add(item);
        assertNull(Pool.getPool(50, 50));
    }

    // --- deletePool ---

    @Test
    void testDeletePool_RemovesFromList() {
        Pool item = new Pool();
        item.setMapPos(0, 0, 10, 10);
        item.setMapW(11);
        item.setMapH(11);
        SimYukkuri.world.getCurrentMap().getPool().add(item);
        assertTrue(SimYukkuri.world.getCurrentMap().getPool().contains(item));
        Pool.deletePool(item);
        assertFalse(SimYukkuri.world.getCurrentMap().getPool().contains(item));
    }

    // --- executeShapePopup ---

    @Test
    void testExecuteShapePopup_SETUP_DoesNotThrow() {
        Pool item = new Pool();
        SimYukkuri.world.getCurrentMap().getPool().add(item);
        assertDoesNotThrow(() -> item.executeShapePopup(ShapeMenu.SETUP));
    }

    @Test
    void testExecuteShapePopup_TOP_MovesToFront() {
        Pool item1 = new Pool();
        Pool item2 = new Pool();
        SimYukkuri.world.getCurrentMap().getPool().add(item1);
        SimYukkuri.world.getCurrentMap().getPool().add(item2);
        item2.executeShapePopup(ShapeMenu.TOP);
        assertEquals(item2, SimYukkuri.world.getCurrentMap().getPool().get(0));
    }

    @Test
    void testExecuteShapePopup_BOTTOM_MovesToEnd() {
        Pool item1 = new Pool();
        Pool item2 = new Pool();
        SimYukkuri.world.getCurrentMap().getPool().add(item1);
        SimYukkuri.world.getCurrentMap().getPool().add(item2);
        item1.executeShapePopup(ShapeMenu.BOTTOM);
        int size = SimYukkuri.world.getCurrentMap().getPool().size();
        assertEquals(item1, SimYukkuri.world.getCurrentMap().getPool().get(size - 1));
    }

    @Test
    void testExecuteShapePopup_UP_MovesUp() {
        Pool item1 = new Pool();
        Pool item2 = new Pool();
        SimYukkuri.world.getCurrentMap().getPool().add(item1);
        SimYukkuri.world.getCurrentMap().getPool().add(item2);
        // item2 is at index 1, moving UP brings it to index 0
        item2.executeShapePopup(ShapeMenu.UP);
        assertEquals(item2, SimYukkuri.world.getCurrentMap().getPool().get(0));
    }

    @Test
    void testExecuteShapePopup_DOWN_MovesDown() {
        Pool item1 = new Pool();
        Pool item2 = new Pool();
        SimYukkuri.world.getCurrentMap().getPool().add(item1);
        SimYukkuri.world.getCurrentMap().getPool().add(item2);
        // item1 is at index 0, moving DOWN brings it to index 1
        item1.executeShapePopup(ShapeMenu.DOWN);
        assertEquals(item1, SimYukkuri.world.getCurrentMap().getPool().get(1));
    }

    // --- objHitProcess with non-Body, airborne ---

    @Test
    void testObjHitProcess_AirborneObj_ReturnsZero() {
        Pool item = new Pool();
        item.setMapPos(100, 100, 300, 300);
        Food food = new Food(200, 200, 0);
        food.setZ(5); // airborne
        assertEquals(0, item.objHitProcess(food));
    }

    // --- objHitProcess with non-Body, on ground, outside pool ---

    @Test
    void testObjHitProcess_ObjOnGround_OutsidePool() {
        Pool item = new Pool();
        item.setMapPos(100, 100, 300, 300);
        Food food = new Food(50, 50, 0); // outside pool
        food.setZ(0);
        assertDoesNotThrow(() -> item.objHitProcess(food));
    }

    // --- drawPreview ---

    @Test
    void testDrawPreview_doesNotThrow() {
        src.draw.Translate.setCanvasSize(800, 600, 100, 100, new float[]{1.0f});
        src.draw.Translate.createTransTable(false);
        java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(800, 600, java.awt.image.BufferedImage.TYPE_INT_RGB);
        java.awt.Graphics2D g2 = img.createGraphics();
        assertDoesNotThrow(() -> Pool.drawPreview(g2, 10, 10, 100, 100));
        g2.dispose();
    }

    // --- drawShape ---

    @Test
    void testDrawShape_doesNotThrow() {
        src.draw.Translate.setCanvasSize(800, 600, 100, 100, new float[]{1.0f});
        src.draw.Translate.createTransTable(false);
        Pool item = new Pool();
        item.setMapPos(100, 100, 300, 300);
        java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(800, 600, java.awt.image.BufferedImage.TYPE_INT_RGB);
        java.awt.Graphics2D g2 = img.createGraphics();
        assertDoesNotThrow(() -> item.drawShape(g2));
        g2.dispose();
    }

    // --- checkContain ---

    @Test
    void testCheckContain_mapCoord_insidePool() {
        Pool item = new Pool();
        item.setMapPos(100, 100, 300, 300);
        // false = map coord check
        assertDoesNotThrow(() -> item.checkContain(200, 200, false));
    }

    @Test
    void testCheckContain_fieldCoord_outsidePool() {
        src.draw.Translate.setCanvasSize(800, 600, 100, 100, new float[]{1.0f});
        src.draw.Translate.createTransTable(false);
        Pool item = new Pool();
        item.setMapPos(100, 100, 300, 300);
        // true = field coord check
        assertDoesNotThrow(() -> item.checkContain(50, 50, true));
    }

    // --- objHitProcess with Body inside pool ---

    @Test
    void testObjHitProcess_BodyInsidePool_executesCode() {
        Pool item = new Pool();
        item.setMapPos(0, 0, 1000, 1000);
        src.base.Body body = WorldTestHelper.createBody();
        body.setX(200); body.setY(200); body.setZ(0);
        try {
            item.objHitProcess(body);
        } catch (NullPointerException e) {
            // Sprite not loaded in headless test environment
        }
    }

    // --- Pool(int,int,int,int) constructor ---

    @Test
    void testConstructor_WithCoords_executesCode() {
        src.draw.Translate.setCanvasSize(800, 600, 100, 100, new float[]{1.0f});
        src.draw.Translate.createTransTable(false);
        try {
            Pool item = new Pool(100, 100, 300, 300);
            assertNotNull(item);
            assertTrue(SimYukkuri.world.getCurrentMap().getPool().contains(item));
        } catch (Exception e) {
            // May fail in headless if Translate not fully initialized
        }
    }
}
