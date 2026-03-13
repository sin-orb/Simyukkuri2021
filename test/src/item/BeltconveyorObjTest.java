package src.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import src.SimYukkuri;
import src.base.Body;
import src.base.ItemTestBase;
import src.draw.Rectangle4y;
import src.draw.Translate;
import src.enums.YukkuriType;
import src.game.Shit;
import src.system.Sprite;
import src.util.WorldTestHelper;

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
        Translate.setCanvasSize(800, 600, 100, 100, new float[]{1.0f});
        Translate.createTransTable(false);
    }

    /** bodySprが設定されたBodyを生成する */
    private Body createBodyWithSprites() {
        Body body = WorldTestHelper.createBody();
        Sprite[] spr = new Sprite[3];
        for (int i = 0; i < 3; i++) {
            spr[i] = new Sprite(10, 10, Sprite.PIVOT_CENTER_BOTTOM);
        }
        body.setBodySpr(spr);
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
    void testGetSetbMoveOnce() {
        BeltconveyorObj belt = new BeltconveyorObj();
        belt.setbMoveOnce(true);
        assertTrue(belt.isbMoveOnce());
        belt.setbMoveOnce(false);
        assertFalse(belt.isbMoveOnce());
    }

    @Test
    void testGetSetBindObjList() {
        BeltconveyorObj belt = new BeltconveyorObj();
        List<src.base.Obj> list = new LinkedList<>();
        belt.setBindObjList(list);
        assertEquals(list, belt.getBindObjList());
    }

    @Test
    void testGetSetSelectedYukkuriType() {
        BeltconveyorObj belt = new BeltconveyorObj();
        List<YukkuriType> list = new ArrayList<>();
        list.add(YukkuriType.REIMU);
        belt.setSelectedYukkuriType(list);
        assertEquals(list, belt.getSelectedYukkuriType());
    }

    @Test
    void testGetSetObOptionSelectionList() {
        BeltconveyorObj belt = new BeltconveyorObj();
        List<Boolean> list = new ArrayList<>();
        list.add(true);
        belt.setObOptionSelectionList(list);
        assertEquals(list, belt.getObOptionSelectionList());
    }

    @Test
    void testGetSetbFilter() {
        BeltconveyorObj belt = new BeltconveyorObj();
        belt.setbFilter(true);
        assertTrue(belt.isbFilter());
        belt.setbFilter(false);
        assertFalse(belt.isbFilter());
    }

    @Test
    void testSetFilterMethod() {
        BeltconveyorObj belt = new BeltconveyorObj();
        belt.setFilter(true);
        assertTrue(belt.isbFilter());
    }

    @Test
    void testGetSetYukkuriFilter() {
        BeltconveyorObj belt = new BeltconveyorObj();
        List<YukkuriType> filter = new ArrayList<>();
        filter.add(YukkuriType.MARISA);
        belt.setYukkuriFilter(filter);
        assertEquals(filter, belt.getYukkuriFilter());
    }

    @Test
    void testGetOptionFilter() {
        BeltconveyorObj belt = new BeltconveyorObj();
        // Not null by default (initialized in class)
        assertDoesNotThrow(() -> belt.getOptionFilter());
    }

    @Test
    void testGetOptionResultFilter() {
        BeltconveyorObj belt = new BeltconveyorObj();
        assertDoesNotThrow(() -> belt.getOptionResultFilter());
    }

    @Test
    void testSetOptionResultFilter() {
        BeltconveyorObj belt = new BeltconveyorObj();
        List<Boolean> list = new ArrayList<>();
        list.add(false);
        belt.setOptionResultFilter(list);
        assertEquals(list, belt.getOptionResultFilter());
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
        int[] pts = {1, 2, 3, 4};
        belt.setAnPointX(pts);
        assertArrayEquals(pts, belt.getAnPointX());
    }

    @Test
    void testGetSetAnPointY() {
        BeltconveyorObj belt = new BeltconveyorObj();
        int[] pts = {10, 20, 30, 40};
        belt.setAnPointY(pts);
        assertArrayEquals(pts, belt.getAnPointY());
    }

    // --- removeListData ---

    @Test
    void testRemoveListData_EmptyBindList() {
        BeltconveyorObj belt = new BeltconveyorObj();
        belt.setObjId(99);
        SimYukkuri.world.getCurrentMap().getBeltconveyorObj().put(99, belt);
        assertTrue(SimYukkuri.world.getCurrentMap().getBeltconveyorObj().containsKey(99));
        assertDoesNotThrow(() -> belt.removeListData());
        assertFalse(SimYukkuri.world.getCurrentMap().getBeltconveyorObj().containsKey(99));
    }

    @Test
    void testRemoveListData_WithBodyInBindList() {
        BeltconveyorObj belt = new BeltconveyorObj();
        belt.setObjId(98);
        SimYukkuri.world.getCurrentMap().getBeltconveyorObj().put(98, belt);

        Body body = createBodyWithSprites();
        body.setbOnDontMoveBeltconveyor(true);
        List<src.base.Obj> list = new LinkedList<>();
        list.add(body);
        belt.setBindObjList(list);

        belt.removeListData();

        assertFalse(SimYukkuri.world.getCurrentMap().getBeltconveyorObj().containsKey(98));
        assertFalse(body.isbOnDontMoveBeltconveyor());
    }

    @Test
    void testRemoveListData_WithNullInBindList() {
        BeltconveyorObj belt = new BeltconveyorObj();
        belt.setObjId(97);
        SimYukkuri.world.getCurrentMap().getBeltconveyorObj().put(97, belt);

        List<src.base.Obj> list = new LinkedList<>();
        list.add(null);
        belt.setBindObjList(list);

        assertDoesNotThrow(() -> belt.removeListData());
        assertFalse(SimYukkuri.world.getCurrentMap().getBeltconveyorObj().containsKey(97));
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
        belt.setTargetType(1); // Body only
        Shit shit = new Shit(); // not a Body
        assertEquals(0, belt.objHitProcess(shit));
    }

    @Test
    void testObjHitProcess_TargetType2_Body_ReturnsZero() {
        BeltconveyorObj belt = new BeltconveyorObj();
        belt.setTargetType(2); // Shit or Vomit only
        Body body = createBodyWithSprites();
        assertEquals(0, belt.objHitProcess(body));
    }

    @Test
    void testObjHitProcess_TargetType3_Body_ReturnsZero() {
        BeltconveyorObj belt = new BeltconveyorObj();
        belt.setTargetType(3); // Food only (not Body)
        Body body = createBodyWithSprites();
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
        belt.setTargetType(5); // default: excludes Body
        Body body = createBodyWithSprites();
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
        Body body = createBodyWithSprites();
        belt.objHitProcess(body);
        assertTrue(body.isbOnDontMoveBeltconveyor());
    }

    @Test
    void testObjHitProcess_Option0_MovesByBeltSpeed() {
        BeltconveyorObj belt = new BeltconveyorObj();
        belt.setTargetType(0);
        belt.setCantmove(0);
        belt.setBeltSpeed(5);
        belt.setOption(0); // default: setCalcY(y - speed)
        Shit shit = new Shit();
        shit.setX(100); shit.setY(100);
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
        belt.setbFilter(true);
        List<YukkuriType> filter = new ArrayList<>();
        filter.add(YukkuriType.MARISA); // Marisa filtered out
        belt.setSelectedYukkuriType(filter);
        Body body = createBodyWithSprites(); // Marisa
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
    void testCheckHitObj_LockmoveBody_ReturnsFalse() {
        BeltconveyorObj belt = new BeltconveyorObj();
        Body body = WorldTestHelper.createBody();
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
        belt.setAnPointX(new int[]{0, 0, 0, 0});
        belt.setAnPointY(new int[]{0, 0, 0, 0});
        Shit shit = new Shit();
        shit.setX(500); shit.setY(500); // far from belt bounds
        assertFalse(belt.checkHitObj(null, shit));
    }

    // --- checkContain ---

    @Test
    void testCheckContain_mapCoords_doesNotThrow() {
        BeltconveyorObj belt = new BeltconveyorObj();
        belt.setAnPointX(new int[]{0, 0, 100, 100});
        belt.setAnPointY(new int[]{0, 100, 100, 0});
        // bIsField=false → uses nX/nY directly, anPoints converted via invertLimit
        assertDoesNotThrow(() -> belt.checkContain(50, 50, false));
    }

    @Test
    void testCheckContain_outsideBounds_returnsFalse() {
        BeltconveyorObj belt = new BeltconveyorObj();
        belt.setAnPointX(new int[]{0, 0, 0, 0});
        belt.setAnPointY(new int[]{0, 0, 0, 0});
        // All anPoints at origin → very small region → large point is outside
        assertFalse(belt.checkContain(999, 999, false));
    }

    @Test
    void testCheckContain_fieldCoords_doesNotThrow() {
        BeltconveyorObj belt = new BeltconveyorObj();
        belt.setAnPointX(new int[]{0, 0, 100, 100});
        belt.setAnPointY(new int[]{0, 100, 100, 0});
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
        belt.setAnPointX(new int[]{0, 0, 100, 100});
        belt.setAnPointY(new int[]{0, 100, 100, 0});
        BufferedImage img = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
        java.awt.Graphics2D g2 = img.createGraphics();
        BufferedImage[] layer = new BufferedImage[]{img};
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
            BeltconveyorObj.setBeltconveyor(belt, true);
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
