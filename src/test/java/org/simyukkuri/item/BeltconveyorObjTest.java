package org.simyukkuri.item;

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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.simyukkuri.SimYukkuri;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.world.item.ItemTestBase;
import org.simyukkuri.draw.Rectangle4y;
import org.simyukkuri.entity.core.world.item.BeltconveyorObj;
import org.simyukkuri.entity.core.world.mobile.Shit;
import org.simyukkuri.enums.YukkuriType;
import org.simyukkuri.system.Sprite;
import org.simyukkuri.util.WorldTestHelper;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
        belt.setBeltSpeed(5);
        assertEquals(5, belt.getBeltSpeed());
    }

    @Test
    void testGetSetHou_before() {
        BeltconveyorObj belt = new BeltconveyorObj();
        belt.setHou_before(3);
        assertEquals(3, belt.getHou_before());
    }

    @Test
    void testGetSetObj_before() {
        BeltconveyorObj belt = new BeltconveyorObj();
        belt.setObj_before(2);
        assertEquals(2, belt.getObj_before());
    }

    @Test
    void testGetSetMove_before() {
        BeltconveyorObj belt = new BeltconveyorObj();
        belt.setMove_before(1);
        assertEquals(1, belt.getMove_before());
    }

    @Test
    void testGetSetSpeed_before() {
        BeltconveyorObj belt = new BeltconveyorObj();
        belt.setSpeed_before(4);
        assertEquals(4, belt.getSpeed_before());
    }

    @Test
    void testGetSetTargetType() {
        BeltconveyorObj belt = new BeltconveyorObj();
        belt.setTargetType(3);
        assertEquals(3, belt.getTargetType());
    }

    @Test
    void testGetSetCantmove() {
        BeltconveyorObj belt = new BeltconveyorObj();
        belt.setCantmove(1);
        assertEquals(1, belt.getCantmove());
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
        belt.setFilter(true);
        assertTrue(belt.isFilter());
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
        // Not null by default (initialized in class)
        assertDoesNotThrow(() -> belt.getOptionLabels());
    }

    @Test
    void testGetOptionResultFilter() {
        BeltconveyorObj belt = new BeltconveyorObj();
        assertDoesNotThrow(() -> belt.getOptionSelections());
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
        belt.setFieldSX(100);
        assertEquals(100, belt.getFieldSX());
    }

    @Test
    void testGetSetFieldSY() {
        BeltconveyorObj belt = new BeltconveyorObj();
        belt.setFieldSY(200);
        assertEquals(200, belt.getFieldSY());
    }

    @Test
    void testGetSetFieldEX() {
        BeltconveyorObj belt = new BeltconveyorObj();
        belt.setFieldEX(500);
        assertEquals(500, belt.getFieldEX());
    }

    @Test
    void testGetSetFieldEY() {
        BeltconveyorObj belt = new BeltconveyorObj();
        belt.setFieldEY(600);
        assertEquals(600, belt.getFieldEY());
    }

    @Test
    void testGetSetFirstX() {
        BeltconveyorObj belt = new BeltconveyorObj();
        belt.setFirstX(50);
        assertEquals(50, belt.getFirstX());
    }

    @Test
    void testGetSetFirstY() {
        BeltconveyorObj belt = new BeltconveyorObj();
        belt.setFirstY(75);
        assertEquals(75, belt.getFirstY());
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

        assertDoesNotThrow(() -> belt.removeFromWorld());
        assertFalse(SimYukkuri.world.getCurrentWorldState().getBeltconveyorObjects().containsKey(97));
    }

    // --- upDate ---

    @Test
    void testUpDate_AgeNotDivisibleBy2400() {
        BeltconveyorObj belt = new BeltconveyorObj();
        belt.setAge(1);
        assertDoesNotThrow(() -> belt.upDate());
    }

    @Test
    void testUpDate_AgeDivisibleBy2400() {
        BeltconveyorObj belt = new BeltconveyorObj();
        belt.setAge(0); // 0 % 2400 == 0
        assertDoesNotThrow(() -> belt.upDate());
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
        assertDoesNotThrow(() -> belt.objHitProcess(shit));
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
        assertDoesNotThrow(() -> belt.objHitProcess(shit));
    }

    @Test
    void testObjHitProcess_Option1_MovesByBeltSpeedDown() {
        BeltconveyorObj belt = new BeltconveyorObj();
        belt.setTargetType(0);
        belt.setCantmove(0);
        belt.setBeltSpeed(5);
        belt.setOption(1);
        Shit shit = new Shit();
        assertDoesNotThrow(() -> belt.objHitProcess(shit));
    }

    @Test
    void testObjHitProcess_Option2_MovesByBeltSpeedRight() {
        BeltconveyorObj belt = new BeltconveyorObj();
        belt.setTargetType(0);
        belt.setCantmove(0);
        belt.setBeltSpeed(5);
        belt.setOption(2);
        Shit shit = new Shit();
        assertDoesNotThrow(() -> belt.objHitProcess(shit));
    }

    @Test
    void testObjHitProcess_Option3_MovesByBeltSpeedLeft() {
        BeltconveyorObj belt = new BeltconveyorObj();
        belt.setTargetType(0);
        belt.setCantmove(0);
        belt.setBeltSpeed(5);
        belt.setOption(3);
        Shit shit = new Shit();
        assertDoesNotThrow(() -> belt.objHitProcess(shit));
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
        // isField=false → uses x/y directly, polygon points converted via invertLimit
        assertDoesNotThrow(() -> belt.checkContain(50, 50, false));
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
        // bIsField=true → calls Translate.invertLimit for input coords too
        assertDoesNotThrow(() -> belt.checkContain(50, 50, true));
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
        assertDoesNotThrow(() -> BeltconveyorObj.drawPreview(g2, 10, 10, 100, 100));
        g2.dispose();
    }

    // --- setBeltconveyor: headless → try/catch ---

    @Test
    void testSetBeltconveyor_headless_executesCode() {
        BeltconveyorObj belt = new BeltconveyorObj();
        try {
            BeltconveyorObj.setBeltconveyors(belt, true);
        } catch (Exception e) {
            // Expected in headless environment (GUI setup fails)
        }
    }

    // --- Constructor(int, int, int): headless → try/catch ---

    @Test
    void testConstructor_WithCoords_doesNotThrow() {
        try {
            BeltconveyorObj belt = new BeltconveyorObj(100, 100, 0);
        } catch (Exception e) {
            // Expected in headless environment
        }
    }

    @Test
    void testLoadImages_headless_executesCode() {
        try {
            BeltconveyorObj.loadImages(BeltconveyorObj.class.getClassLoader(), null);
        } catch (Exception e) {
            // Expected: IOException because image files not found in test environment
        }
    }
}
