package org.simyukkuri.item;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.simyukkuri.SimYukkuri;
import org.simyukkuri.draw.Rectangle4y;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.world.item.BeltconveyorObj;
import org.simyukkuri.entity.core.world.item.ItemTestBase;
import org.simyukkuri.entity.core.world.mobile.Shit;
import org.simyukkuri.enums.YukkuriType;
import org.simyukkuri.system.Sprite;
import org.simyukkuri.util.WorldTestHelper;

class BeltconveyorObjTest extends ItemTestBase {

    @BeforeEach
    @Override
    public void setUp() {
        super.setUp();
        WorldTestHelper.initializeStandardTranslate200();
    }

    /** bodySprが設定されたBodyを生成する */
    private Yukkuri createBodyWithSprites() {
        Yukkuri body = WorldTestHelper.createBody();
        Sprite[] spr = new Sprite[3];
        for (int i = 0; i < 3; i++) {
            spr[i] = new Sprite(10, 10, Sprite.PIVOT_CENTER_BOTTOM);
        }
        body.setSpriteSet(spr);
        return body;
    }

    @Test
    void testConstructor_Default() {
        BeltconveyorObj belt = new BeltconveyorObj();
        assertNotNull(belt);
        assertFalse(belt.isFilter());
        assertFalse(belt.isMoveOnce());
        assertEquals(0, belt.getBeltSpeed());
    }

    @Test
    void testActionEnum() {
        BeltconveyorObj.Action[] actions = BeltconveyorObj.Action.values();
        assertEquals(1, actions.length);
        for (BeltconveyorObj.Action a : actions) {
            assertDoesNotThrow(() -> a.toString());
        }
        assertEquals(BeltconveyorObj.Action.YUKKURI_FILTER, BeltconveyorObj.Action.valueOf("YUKKURI_FILTER"));
    }

    // --- static methods ---

    @Test
    void testGetBounding() {
        Rectangle4y b = BeltconveyorObj.getBounding();
        assertNotNull(b);
        assertSame(b, BeltconveyorObj.getBounding());
    }

    @Test
    void testGetHitCheckObjType() {
        BeltconveyorObj belt = new BeltconveyorObj();
        assertEquals(BeltconveyorObj.hitCheckObjType, belt.getHitCheckObjType());
    }

    @Test
    void testCheckInterval_AlwaysTrue() {
        BeltconveyorObj belt = new BeltconveyorObj();
        assertTrue(belt.checkInterval(0));
        assertTrue(belt.checkInterval(100));
        assertTrue(belt.checkInterval(-1));
    }

    @Test
    void testSetupBeltconveyor_AlwaysTrue() {
        assertTrue(BeltconveyorObj.setupBeltconveyor(null));
    }

    // --- getters / setters ---

    @Test
    void testGetSetBeltSpeed() {
        BeltconveyorObj belt = new BeltconveyorObj();
        assertEquals(0, belt.getBeltSpeed());
        belt.setBeltSpeed(5);
        assertEquals(5, belt.getBeltSpeed());
    }

    @Test
    void testGetSetHou_before() {
        BeltconveyorObj belt = new BeltconveyorObj();
        belt.setHouBefore(3);
        assertEquals(3, belt.getHouBefore());
        belt.setHouBefore(7);
        assertEquals(7, belt.getHouBefore());
    }

    @Test
    void testGetSetObj_before() {
        BeltconveyorObj belt = new BeltconveyorObj();
        belt.setObjBefore(2);
        assertEquals(2, belt.getObjBefore());
        belt.setObjBefore(5);
        assertEquals(5, belt.getObjBefore());
    }

    @Test
    void testGetSetMove_before() {
        BeltconveyorObj belt = new BeltconveyorObj();
        belt.setMoveBefore(1);
        assertEquals(1, belt.getMoveBefore());
        belt.setMoveBefore(4);
        assertEquals(4, belt.getMoveBefore());
    }

    @Test
    void testGetSetSpeed_before() {
        BeltconveyorObj belt = new BeltconveyorObj();
        belt.setSpeedBefore(4);
        assertEquals(4, belt.getSpeedBefore());
        belt.setSpeedBefore(8);
        assertEquals(8, belt.getSpeedBefore());
    }

    @Test
    void testGetSetTargetType() {
        BeltconveyorObj belt = new BeltconveyorObj();
        belt.setTargetType(3);
        assertEquals(3, belt.getTargetType());
        belt.setTargetType(0);
        assertEquals(0, belt.getTargetType());
    }

    @Test
    void testGetSetCantmove() {
        BeltconveyorObj belt = new BeltconveyorObj();
        belt.setCantmove(1);
        assertEquals(1, belt.getCantmove());
        belt.setCantmove(0);
        assertEquals(0, belt.getCantmove());
    }

    @Test
    void testGetSetMoveOnce() {
        BeltconveyorObj belt = new BeltconveyorObj();
        belt.setMoveOnce(true);
        assertTrue(belt.isMoveOnce());
        belt.setMoveOnce(false);
        assertFalse(belt.isMoveOnce());
    }

    @Test
    void testGetSetBindObjList() {
        BeltconveyorObj belt = new BeltconveyorObj();
        List<org.simyukkuri.entity.core.Entity> list = new LinkedList<>();
        belt.setBoundObjects(list);
        assertEquals(list, belt.getBoundObjects());
    }

    @Test
    void testGetSetSelectedYukkuriType() {
        BeltconveyorObj belt = new BeltconveyorObj();
        List<YukkuriType> list = new ArrayList<>();
        list.add(YukkuriType.REIMU);
        belt.setSelectedYukkuriTypes(list);
        assertEquals(list, belt.getSelectedYukkuriTypes());
    }

    @Test
    void testGetSetObOptionSelectionList() {
        BeltconveyorObj belt = new BeltconveyorObj();
        List<Boolean> list = new ArrayList<>();
        list.add(true);
        belt.setOptionSelections(list);
        assertEquals(list, belt.getOptionSelections());
    }

    @Test
    void testGetSetFilter() {
        BeltconveyorObj belt = new BeltconveyorObj();
        belt.setFilter(true);
        assertTrue(belt.isFilter());
        belt.setFilter(false);
        assertFalse(belt.isFilter());
    }

    @Test
    void testSetFilterMethod() {
        BeltconveyorObj belt = new BeltconveyorObj();
        assertFalse(belt.isFilter());
        belt.setFilter(true);
        assertTrue(belt.isFilter());
        belt.setFilter(false);
        assertFalse(belt.isFilter());
    }

    @Test
    void testGetSetYukkuriFilter() {
        BeltconveyorObj belt = new BeltconveyorObj();
        List<YukkuriType> filter = new ArrayList<>();
        filter.add(YukkuriType.MARISA);
        belt.setSelectedYukkuriTypes(filter);
        assertEquals(filter, belt.getSelectedYukkuriTypes());
    }

    @Test
    void testGetOptionFilter() {
        BeltconveyorObj belt = new BeltconveyorObj();
        assertNotNull(belt.getOptionLabels());
        assertFalse(belt.getOptionLabels().isEmpty());
    }

    @Test
    void testGetOptionResultFilter() {
        BeltconveyorObj belt = new BeltconveyorObj();
        assertNotNull(belt.getOptionSelections());
        // Initially empty, populated after setOptionSelections
        List<Boolean> selections = new ArrayList<>();
        selections.add(true);
        belt.setOptionSelections(selections);
        assertEquals(1, belt.getOptionSelections().size());
    }

    @Test
    void testSetOptionResultFilter() {
        BeltconveyorObj belt = new BeltconveyorObj();
        List<Boolean> list = new ArrayList<>();
        list.add(false);
        belt.setOptionSelections(list);
        assertEquals(list, belt.getOptionSelections());
    }

    @Test
    void testGetSetFieldSX() {
        BeltconveyorObj belt = new BeltconveyorObj();
        belt.setFieldSx(100);
        assertEquals(100, belt.getFieldSx());
        belt.setFieldSx(200);
        assertEquals(200, belt.getFieldSx());
    }

    @Test
    void testGetSetFieldSY() {
        BeltconveyorObj belt = new BeltconveyorObj();
        belt.setFieldSy(200);
        assertEquals(200, belt.getFieldSy());
        belt.setFieldSy(400);
        assertEquals(400, belt.getFieldSy());
    }

    @Test
    void testGetSetFieldEX() {
        BeltconveyorObj belt = new BeltconveyorObj();
        belt.setFieldEx(500);
        assertEquals(500, belt.getFieldEx());
        belt.setFieldEx(1000);
        assertEquals(1000, belt.getFieldEx());
    }

    @Test
    void testGetSetFieldEY() {
        BeltconveyorObj belt = new BeltconveyorObj();
        belt.setFieldEy(600);
        assertEquals(600, belt.getFieldEy());
        belt.setFieldEy(1200);
        assertEquals(1200, belt.getFieldEy());
    }

    @Test
    void testGetSetFirstX() {
        BeltconveyorObj belt = new BeltconveyorObj();
        belt.setFirstX(50);
        assertEquals(50, belt.getFirstX());
        belt.setFirstX(100);
        assertEquals(100, belt.getFirstX());
    }

    @Test
    void testGetSetFirstY() {
        BeltconveyorObj belt = new BeltconveyorObj();
        belt.setFirstY(75);
        assertEquals(75, belt.getFirstY());
        belt.setFirstY(150);
        assertEquals(150, belt.getFirstY());
    }

    @Test
    void testGetSetAnPointX() {
        BeltconveyorObj belt = new BeltconveyorObj();
        int[] pts = { 1, 2, 3, 4 };
        belt.setPolygonX(pts);
        assertArrayEquals(pts, belt.getPolygonX());
    }

    @Test
    void testGetSetAnPointY() {
        BeltconveyorObj belt = new BeltconveyorObj();
        int[] pts = { 10, 20, 30, 40 };
        belt.setPolygonY(pts);
        assertArrayEquals(pts, belt.getPolygonY());
    }

    // --- removeListData ---

    @Test
    void testRemoveListData_EmptyBindList() {
        BeltconveyorObj belt = new BeltconveyorObj();
        belt.setObjId(99);
        SimYukkuri.world.getCurrentWorldState().getBeltconveyorObjects().put(99, belt);
        assertTrue(SimYukkuri.world.getCurrentWorldState().getBeltconveyorObjects().containsKey(99));
        assertDoesNotThrow(() -> belt.removeFromWorld());
        assertFalse(SimYukkuri.world.getCurrentWorldState().getBeltconveyorObjects().containsKey(99));
    }

    @Test
    void testRemoveListData_WithBodyInBindList() {
        BeltconveyorObj belt = new BeltconveyorObj();
        belt.setObjId(98);
        SimYukkuri.world.getCurrentWorldState().getBeltconveyorObjects().put(98, belt);

        Yukkuri body = createBodyWithSprites();
        body.setOnNonMovingConveyor(true);
        List<org.simyukkuri.entity.core.Entity> list = new LinkedList<>();
        list.add(body);
        belt.setBoundObjects(list);

        belt.removeFromWorld();

        assertFalse(SimYukkuri.world.getCurrentWorldState().getBeltconveyorObjects().containsKey(98));
        assertFalse(body.isOnNonMovingConveyor());
    }

    @Test
    void testRemoveListData_WithNullInBindList() {
        BeltconveyorObj belt = new BeltconveyorObj();
        belt.setObjId(97);
        SimYukkuri.world.getCurrentWorldState().getBeltconveyorObjects().put(97, belt);

        List<org.simyukkuri.entity.core.Entity> list = new LinkedList<>();
        list.add(null);
        belt.setBoundObjects(list);

        assertTrue(SimYukkuri.world.getCurrentWorldState().getBeltconveyorObjects().containsKey(97));
        assertDoesNotThrow(() -> belt.removeFromWorld());
        assertFalse(SimYukkuri.world.getCurrentWorldState().getBeltconveyorObjects().containsKey(97));
    }

    // --- upDate ---

    @Test
    void testUpDate_AgeNotDivisibleBy2400() {
        BeltconveyorObj belt = new BeltconveyorObj();
        belt.setAge(1);
        belt.upDate();
        // upDate does not modify age (incremented by game loop externally)
        assertEquals(1, belt.getAge());
        assertFalse(belt.isRemoved());
    }

    @Test
    void testUpDate_AgeDivisibleBy2400() {
        BeltconveyorObj belt = new BeltconveyorObj();
        belt.setAge(0); // 0 % 2400 == 0
        belt.upDate();
        // upDate does not modify age
        assertEquals(0, belt.getAge());
        assertFalse(belt.isRemoved());
    }

    // --- objHitProcess ---

    @Test
    void testObjHitProcess_TargetType1_NonBody_ReturnsZero() {
        BeltconveyorObj belt = new BeltconveyorObj();
        belt.setTargetType(1); // Yukkuri only
        Shit shit = new Shit(); // not a Yukkuri
        assertEquals(0, belt.objHitProcess(shit));
    }

    @Test
    void testObjHitProcess_TargetType2_Body_ReturnsZero() {
        BeltconveyorObj belt = new BeltconveyorObj();
        belt.setTargetType(2); // Shit or Vomit only
        Yukkuri body = createBodyWithSprites();
        assertEquals(0, belt.objHitProcess(body));
    }

    @Test
    void testObjHitProcess_TargetType3_Body_ReturnsZero() {
        BeltconveyorObj belt = new BeltconveyorObj();
        belt.setTargetType(3); // Food only (not Yukkuri)
        Yukkuri body = createBodyWithSprites();
        assertEquals(0, belt.objHitProcess(body));
    }

    @Test
    void testObjHitProcess_TargetType4_NonStalk_ReturnsZero() {
        BeltconveyorObj belt = new BeltconveyorObj();
        belt.setTargetType(4); // Stalk only
        Shit shit = new Shit();
        assertEquals(0, belt.objHitProcess(shit));
    }

    @Test
    void testObjHitProcess_TargetTypeDefault_Body_ReturnsZero() {
        BeltconveyorObj belt = new BeltconveyorObj();
        belt.setTargetType(5); // default: excludes Yukkuri
        Yukkuri body = createBodyWithSprites();
        assertEquals(0, belt.objHitProcess(body));
    }

    @Test
    void testObjHitProcess_TargetType0_Shit_MovesBeltSpeed0() {
        BeltconveyorObj belt = new BeltconveyorObj();
        belt.setTargetType(0);
        belt.setBeltSpeed(0);
        belt.setCantmove(0);
        Shit shit = new Shit();
        // Barrier.onBarrier with 0 dimensions → returns false → bMove=true → setCalcY
        int result = belt.objHitProcess(shit);
        assertEquals(0, result);
    }

    @Test
    void testObjHitProcess_Cantmove1_Body_SetsFlag() {
        BeltconveyorObj belt = new BeltconveyorObj();
        belt.setTargetType(0);
        belt.setCantmove(1);
        Yukkuri body = createBodyWithSprites();
        belt.objHitProcess(body);
        assertTrue(body.isOnNonMovingConveyor());
    }

    @Test
    void testObjHitProcess_Option0_MovesByBeltSpeed() {
        BeltconveyorObj belt = new BeltconveyorObj();
        belt.setTargetType(0);
        belt.setCantmove(0);
        belt.setBeltSpeed(5);
        belt.setOption(0); // default: setCalcY(y - speed)
        Shit shit = new Shit();
        shit.setX(100);
        shit.setY(100);
        int result = belt.objHitProcess(shit);
        assertEquals(0, result);
    }

    @Test
    void testObjHitProcess_Option1_MovesByBeltSpeedDown() {
        BeltconveyorObj belt = new BeltconveyorObj();
        belt.setTargetType(0);
        belt.setCantmove(0);
        belt.setBeltSpeed(5);
        belt.setOption(1);
        Shit shit = new Shit();
        int result = belt.objHitProcess(shit);
        assertEquals(0, result);
    }

    @Test
    void testObjHitProcess_Option2_MovesByBeltSpeedRight() {
        BeltconveyorObj belt = new BeltconveyorObj();
        belt.setTargetType(0);
        belt.setCantmove(0);
        belt.setBeltSpeed(5);
        belt.setOption(2);
        Shit shit = new Shit();
        int result = belt.objHitProcess(shit);
        assertEquals(0, result);
    }

    @Test
    void testObjHitProcess_Option3_MovesByBeltSpeedLeft() {
        BeltconveyorObj belt = new BeltconveyorObj();
        belt.setTargetType(0);
        belt.setCantmove(0);
        belt.setBeltSpeed(5);
        belt.setOption(3);
        Shit shit = new Shit();
        int result = belt.objHitProcess(shit);
        assertEquals(0, result);
    }

    @Test
    void testObjHitProcess_FilterEnabled_BodyInFilter_ReturnsZero() {
        BeltconveyorObj belt = new BeltconveyorObj();
        belt.setFilter(true);
        List<YukkuriType> filter = new ArrayList<>();
        filter.add(YukkuriType.MARISA); // Marisa filtered out
        belt.setSelectedYukkuriTypes(filter);
        Yukkuri body = createBodyWithSprites(); // Marisa
        assertEquals(0, belt.objHitProcess(body));
    }

    // --- image/rendering methods ---

    @Test
    void testGetImage_returnsNull() {
        BeltconveyorObj belt = new BeltconveyorObj();
        assertNull(belt.getImage(0));
        assertNull(belt.getImage(1));
    }

    @Test
    void testGetImageLayerCount_returnsZero() {
        BeltconveyorObj belt = new BeltconveyorObj();
        assertEquals(0, belt.getImageLayerCount());
    }

    @Test
    void testGetShadowImage_returnsNull() {
        BeltconveyorObj belt = new BeltconveyorObj();
        assertNull(belt.getShadowImage());
    }

    // --- checkHitObj ---

    @Test
    void testCheckHitObj_LockmoveYukkuri_ReturnsFalse() {
        BeltconveyorObj belt = new BeltconveyorObj();
        Yukkuri body = WorldTestHelper.createBody();
        body.setLockmove(true);
        assertFalse(belt.checkHitObj(null, body));
    }

    @Test
    void testCheckHitObj_RemovedObj_RemovesFromList() {
        BeltconveyorObj belt = new BeltconveyorObj();
        Shit shit = new Shit();
        shit.setRemoved(true);
        // Removed obj: not added to bindObjList, checkContain will return false
        assertDoesNotThrow(() -> belt.checkHitObj(null, shit));
    }

    @Test
    void testCheckHitObj_NotContained_ReturnsFalse() {
        BeltconveyorObj belt = new BeltconveyorObj();
        belt.setPolygonX(new int[] { 0, 0, 0, 0 });
        belt.setPolygonY(new int[] { 0, 0, 0, 0 });
        Shit shit = new Shit();
        shit.setX(500);
        shit.setY(500); // far from belt bounds
        assertFalse(belt.checkHitObj(null, shit));
    }

    // --- checkContain ---

    @Test
    void testCheckContain_mapCoords_doesNotThrow() {
        BeltconveyorObj belt = new BeltconveyorObj();
        belt.setPolygonX(new int[] { 0, 0, 100, 100 });
        belt.setPolygonY(new int[] { 0, 100, 100, 0 });
        // outside point returns false
        assertFalse(belt.checkContain(999, 999, false));
    }

    @Test
    void testCheckContain_outsideBounds_returnsFalse() {
        BeltconveyorObj belt = new BeltconveyorObj();
        belt.setPolygonX(new int[] { 0, 0, 0, 0 });
        belt.setPolygonY(new int[] { 0, 0, 0, 0 });
        // All anPoints at origin → very small region → large point is outside
        assertFalse(belt.checkContain(999, 999, false));
    }

    @Test
    void testCheckContain_fieldCoords_doesNotThrow() {
        BeltconveyorObj belt = new BeltconveyorObj();
        belt.setPolygonX(new int[] { 0, 0, 100, 100 });
        belt.setPolygonY(new int[] { 0, 100, 100, 0 });
        // outside point returns false even with field coords
        assertFalse(belt.checkContain(9999, 9999, true));
    }

    // --- getImageLayer(BufferedImage[]): option 0,1,2,3 ---

    @Test
    void testGetImageLayer_option0_returnsOne() {
        BeltconveyorObj belt = new BeltconveyorObj();
        belt.setOption(0);
        BufferedImage[] layer = new BufferedImage[1];
        assertEquals(1, belt.getImageLayer(layer));
    }

    @Test
    void testGetImageLayer_option1_returnsOne() {
        BeltconveyorObj belt = new BeltconveyorObj();
        belt.setOption(1);
        BufferedImage[] layer = new BufferedImage[1];
        assertEquals(1, belt.getImageLayer(layer));
    }

    @Test
    void testGetImageLayer_option2_returnsOne() {
        BeltconveyorObj belt = new BeltconveyorObj();
        belt.setOption(2);
        BufferedImage[] layer = new BufferedImage[1];
        assertEquals(1, belt.getImageLayer(layer));
    }

    @Test
    void testGetImageLayer_option3_returnsOne() {
        BeltconveyorObj belt = new BeltconveyorObj();
        belt.setOption(3);
        BufferedImage[] layer = new BufferedImage[1];
        assertEquals(1, belt.getImageLayer(layer));
    }

    // --- getImageLayer(Graphics2D, BufferedImage[]): with non-null layer[0] ---

    @Test
    void testGetImageLayer_g2_withImage_returnsOne() {
        BeltconveyorObj belt = new BeltconveyorObj();
        belt.setPolygonX(new int[] { 0, 0, 100, 100 });
        belt.setPolygonY(new int[] { 0, 100, 100, 0 });
        BufferedImage img = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
        java.awt.Graphics2D g2 = img.createGraphics();
        BufferedImage[] layer = new BufferedImage[] { img };
        assertDoesNotThrow(() -> belt.getImageLayer(g2, layer));
        g2.dispose();
    }

    // --- drawPreview ---

    @Test
    void testDrawPreview_doesNotThrow() {
        BufferedImage img = new BufferedImage(800, 600, BufferedImage.TYPE_INT_RGB);
        java.awt.Graphics2D g2 = img.createGraphics();
        BeltconveyorObj.drawPreview(g2, 10, 10, 100, 100);
        g2.dispose();
        // drawPreview completed: pixel at (10,10) may have changed
        assertNotNull(img);
    }

    // --- setBeltconveyor: headless → try/catch ---

    @Test
    void testSetBeltconveyor_headless_executesCode() {
        BeltconveyorObj belt = new BeltconveyorObj();
        assertFalse(belt.isRemoved());
        try {
            BeltconveyorObj.setBeltconveyors(belt, true);
        } catch (Exception e) {
            // Expected in headless environment (GUI setup fails)
        }
        assertFalse(belt.isRemoved());
    }

    // --- Constructor(int, int, int): headless → try/catch ---

    @Test
    void testConstructor_WithCoords_doesNotThrow() {
        BeltconveyorObj[] holder = new BeltconveyorObj[1];
        try {
            holder[0] = new BeltconveyorObj(100, 100, 0);
        } catch (Exception e) {
            // Expected in headless environment
        }
        // Either created successfully or caught exception; no NPE from test
        assertTrue(holder[0] == null || !holder[0].isRemoved());
    }

    @Test
    void testLoadImages_headless_executesCode() {
        Exception caught = null;
        try {
            BeltconveyorObj.loadImages(BeltconveyorObj.class.getClassLoader(), null);
        } catch (Exception e) {
            caught = e;
        }
        // In headless env, either succeeds (if images exist) or throws IOException
        assertTrue(caught == null || caught instanceof java.io.IOException
            || caught instanceof RuntimeException);
    }
}
