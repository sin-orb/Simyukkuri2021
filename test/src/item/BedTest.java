package src.item;

import org.junit.jupiter.api.Test;
import src.base.ItemTestBase;
import src.base.ObjEX.ItemRank;
import src.SimYukkuri;
import src.draw.Translate;
import src.enums.ObjEXType;
import src.enums.Type;
import static org.junit.jupiter.api.Assertions.*;

import java.awt.Rectangle;

class BedTest extends ItemTestBase {

    // ---------------------------------------------------------------
    // コンストラクタ（デフォルト）
    // ---------------------------------------------------------------
    @Test
    void testDefaultConstructor() {
        Bed bed = new Bed();
        assertNotNull(bed);
    }

    // ---------------------------------------------------------------
    // コンストラクタ（パラメータ付き）- option=0 -> HOUSE
    // ---------------------------------------------------------------
    @Test
    void testParameterizedConstructor_House() {
        Bed bed = new Bed(100, 200, 0);
        assertNotNull(bed);
        assertEquals(ItemRank.HOUSE, bed.getItemRank());
        assertEquals(ObjEXType.BED, bed.getObjEXType());
        assertEquals(Type.PLATFORM, bed.getObjType());
        assertEquals(3000, bed.getValue());
        assertEquals(0, bed.getCost());
        assertTrue(bed.getObjId() > 0);
    }

    // ---------------------------------------------------------------
    // コンストラクタ（パラメータ付き）- option=1 -> NORA
    // ---------------------------------------------------------------
    @Test
    void testParameterizedConstructor_Nora() {
        Bed bed = new Bed(50, 50, 1);
        assertNotNull(bed);
        assertEquals(ItemRank.NORA, bed.getItemRank());
        assertEquals(0, bed.getValue());
        assertEquals(0, bed.getCost());
    }

    // ---------------------------------------------------------------
    // コンストラクタ（パラメータ付き）- option=2 -> YASEI
    // ---------------------------------------------------------------
    @Test
    void testParameterizedConstructor_Yasei() {
        Bed bed = new Bed(50, 50, 2);
        assertNotNull(bed);
        assertEquals(ItemRank.YASEI, bed.getItemRank());
        assertEquals(0, bed.getValue());
        assertEquals(0, bed.getCost());
    }

    // ---------------------------------------------------------------
    // MapIndex=5: HOUSE -> YASEI に上書き
    // ---------------------------------------------------------------
    @Test
    void testConstructor_MapIndex5_HouseBecomesYasei() {
        SimYukkuri.world.setCurrentMapIdx(5);
        Bed bed = new Bed(100, 200, 0); // option=HOUSE
        assertEquals(ItemRank.YASEI, bed.getItemRank());
        // YASEI なので value=0
        assertEquals(0, bed.getValue());
        SimYukkuri.world.setCurrentMapIdx(0);
    }

    // ---------------------------------------------------------------
    // MapIndex=6: HOUSE -> YASEI に上書き
    // ---------------------------------------------------------------
    @Test
    void testConstructor_MapIndex6_HouseBecomesYasei() {
        SimYukkuri.world.setCurrentMapIdx(6);
        Bed bed = new Bed(100, 200, 0);
        assertEquals(ItemRank.YASEI, bed.getItemRank());
        SimYukkuri.world.setCurrentMapIdx(0);
    }

    // ---------------------------------------------------------------
    // MapIndex=5: NORA はそのまま（YASEIへの上書きはHOUSEのみ）
    // ---------------------------------------------------------------
    @Test
    void testConstructor_MapIndex5_NoraStaysNora() {
        SimYukkuri.world.setCurrentMapIdx(5);
        Bed bed = new Bed(100, 200, 1); // option=NORA
        assertEquals(ItemRank.NORA, bed.getItemRank());
        SimYukkuri.world.setCurrentMapIdx(0);
    }

    // ---------------------------------------------------------------
    // ワールドのベッドマップに登録されること
    // ---------------------------------------------------------------
    @Test
    void testConstructor_RegisteredInWorld() {
        Bed bed = new Bed(300, 400, 0);
        assertTrue(SimYukkuri.world.getCurrentMap().getBed().containsKey(bed.getObjId()));
    }

    // ---------------------------------------------------------------
    // interval の初期値
    // ---------------------------------------------------------------
    @Test
    void testInterval_Default() {
        Bed bed = new Bed(100, 100, 0);
        assertEquals(5000, bed.getInterval());
    }

    // ---------------------------------------------------------------
    // getItemRank / setItemRank
    // ---------------------------------------------------------------
    @Test
    void testSetItemRank_House() {
        Bed bed = new Bed();
        bed.setItemRank(ItemRank.HOUSE);
        assertEquals(ItemRank.HOUSE, bed.getItemRank());
    }

    @Test
    void testSetItemRank_Nora() {
        Bed bed = new Bed();
        bed.setItemRank(ItemRank.NORA);
        assertEquals(ItemRank.NORA, bed.getItemRank());
    }

    @Test
    void testSetItemRank_Yasei() {
        Bed bed = new Bed();
        bed.setItemRank(ItemRank.YASEI);
        assertEquals(ItemRank.YASEI, bed.getItemRank());
    }

    // ---------------------------------------------------------------
    // getValue（オーバーライド）
    // ---------------------------------------------------------------
    @Test
    void testGetValue_House() {
        Bed bed = new Bed(100, 100, 0);
        assertEquals(3000, bed.getValue());
    }

    @Test
    void testGetValue_Nora() {
        Bed bed = new Bed(100, 100, 1);
        assertEquals(0, bed.getValue());
    }

    // ---------------------------------------------------------------
    // getShadowImage は null を返す
    // ---------------------------------------------------------------
    @Test
    void testGetShadowImage_ReturnsNull() {
        Bed bed = new Bed();
        assertNull(bed.getShadowImage());
    }

    // ---------------------------------------------------------------
    // getBounding
    // ---------------------------------------------------------------
    @Test
    void testGetBounding_NotNull() {
        assertNotNull(Bed.getBounding());
    }

    // ---------------------------------------------------------------
    // takeScreenRect
    // ---------------------------------------------------------------
    @Test
    void testTakeScreenRect_NotNull() {
        Bed bed = new Bed(100, 100, 0);
        Rectangle r = bed.takeScreenRect();
        assertNotNull(r);
    }

    @Test
    void testTakeScreenRect_ContainsBoundaryValues() {
        Bed bed = new Bed(100, 100, 0);
        Rectangle r = bed.takeScreenRect();
        src.draw.Rectangle4y bounding = Bed.getBounding();
        assertEquals(bounding.getX(), r.x);
        assertEquals(bounding.getY(), r.y);
        assertEquals(bounding.getWidth(), r.width);
        assertEquals(bounding.getHeight(), r.height);
    }

    // ---------------------------------------------------------------
    // removeListData
    // ---------------------------------------------------------------
    @Test
    void testRemoveListData_RemovesFromWorld() {
        Bed bed = new Bed(100, 100, 0);
        int id = bed.getObjId();
        assertTrue(SimYukkuri.world.getCurrentMap().getBed().containsKey(id));
        bed.removeListData();
        assertFalse(SimYukkuri.world.getCurrentMap().getBed().containsKey(id));
    }

    // ---------------------------------------------------------------
    // ObjEX 共通メソッド群
    // ---------------------------------------------------------------
    @Test
    void testGetObjEXType() {
        Bed bed = new Bed(100, 100, 0);
        assertEquals(ObjEXType.BED, bed.getObjEXType());
    }

    @Test
    void testGetObjType() {
        Bed bed = new Bed(100, 100, 0);
        assertEquals(Type.PLATFORM, bed.getObjType());
    }

    @Test
    void testCheckInterval() {
        Bed bed = new Bed(100, 100, 0);
        // interval=5000
        assertTrue(bed.checkInterval(0));
        assertTrue(bed.checkInterval(5000));
        assertFalse(bed.checkInterval(1));
        assertFalse(bed.checkInterval(2500));
    }

    @Test
    void testGetEnabled_DefaultTrue() {
        Bed bed = new Bed(100, 100, 0);
        assertTrue(bed.getEnabled());
    }

    @Test
    void testSetEnabled() {
        Bed bed = new Bed(100, 100, 0);
        bed.setEnabled(false);
        assertFalse(bed.getEnabled());
        bed.setEnabled(true);
        assertTrue(bed.getEnabled());
    }

    @Test
    void testInvertEnabled() {
        Bed bed = new Bed(100, 100, 0);
        assertTrue(bed.getEnabled());
        bed.invertEnabled();
        assertFalse(bed.getEnabled());
        bed.invertEnabled();
        assertTrue(bed.getEnabled());
    }

    @Test
    void testHasSetupMenu_DefaultFalse() {
        Bed bed = new Bed(100, 100, 0);
        assertFalse(bed.hasSetupMenu());
    }

    @Test
    void testEnableHitCheck_DefaultTrue() {
        Bed bed = new Bed(100, 100, 0);
        assertTrue(bed.enableHitCheck());
    }

    @Test
    void testGetOption() {
        Bed bed = new Bed(100, 200, 1);
        assertEquals(1, bed.getOption());
    }

    @Test
    void testSetOption() {
        Bed bed = new Bed(100, 200, 0);
        bed.setOption(2);
        assertEquals(2, bed.getOption());
    }

    @Test
    void testGetLooks_Default() {
        Bed bed = new Bed(100, 100, 0);
        assertEquals(0, bed.getLooks());
    }

    @Test
    void testSetLooks() {
        Bed bed = new Bed(100, 100, 0);
        bed.setLooks(5);
        assertEquals(5, bed.getLooks());
    }

    @Test
    void testGetLinkParent_Default() {
        Bed bed = new Bed(100, 100, 0);
        assertEquals(-1, bed.getLinkParent());
    }

    @Test
    void testSetLinkParent() {
        Bed bed = new Bed(100, 100, 0);
        bed.setLinkParent(99);
        assertEquals(99, bed.getLinkParent());
    }

    @Test
    void testGetSetColW() {
        Bed bed = new Bed(100, 100, 0);
        bed.setColW(15);
        assertEquals(15, bed.getColW());
    }

    @Test
    void testGetSetColH() {
        Bed bed = new Bed(100, 100, 0);
        bed.setColH(25);
        assertEquals(25, bed.getColH());
    }

    // ---------------------------------------------------------------
    // Obj 継承メソッド群
    // ---------------------------------------------------------------
    @Test
    void testGetX_GetY() {
        Bed bed = new Bed(123, 456, 0);
        assertEquals(123, bed.getX());
        assertEquals(456, bed.getY());
    }

    @Test
    void testSetX_SetY() {
        Bed bed = new Bed(0, 0, 0);
        bed.setX(300);
        bed.setY(400);
        assertEquals(300, bed.getX());
        assertEquals(400, bed.getY());
    }

    @Test
    void testGetZ_Default() {
        Bed bed = new Bed(100, 100, 0);
        assertEquals(0, bed.getZ());
    }

    @Test
    void testIsRemoved_Default() {
        Bed bed = new Bed(100, 100, 0);
        assertFalse(bed.isRemoved());
    }

    @Test
    void testRemove() {
        Bed bed = new Bed(100, 100, 0);
        bed.remove();
        assertTrue(bed.isRemoved());
    }

    @Test
    void testIsCanGrab_Default() {
        Bed bed = new Bed(100, 100, 0);
        assertTrue(bed.isCanGrab());
    }

    @Test
    void testGrab() {
        Bed bed = new Bed(100, 100, 0);
        assertFalse(bed.isGrabbed());
        bed.grab();
        assertTrue(bed.isGrabbed());
    }

    @Test
    void testRelease() {
        Bed bed = new Bed(100, 100, 0);
        bed.grab();
        assertTrue(bed.isGrabbed());
        bed.release();
        assertFalse(bed.isGrabbed());
    }

    @Test
    void testGetAge_Default() {
        Bed bed = new Bed(100, 100, 0);
        assertEquals(0, bed.getAge());
    }

    @Test
    void testSetAge() {
        Bed bed = new Bed(100, 100, 0);
        bed.setAge(5000L);
        assertEquals(5000L, bed.getAge());
    }

    @Test
    void testAddAge() {
        Bed bed = new Bed(100, 100, 0);
        bed.setAge(100);
        bed.addAge(200);
        assertEquals(300, bed.getAge());
    }

    @Test
    void testSetVx_Vy_Vz() {
        Bed bed = new Bed(100, 100, 0);
        bed.setVx(3);
        bed.setVy(5);
        bed.setVz(-2);
        assertEquals(3, bed.getVx());
        assertEquals(5, bed.getVy());
        assertEquals(-2, bed.getVz());
    }

    @Test
    void testKick() {
        Bed bed = new Bed(100, 100, 0);
        bed.kick(2, 4, -1);
        assertEquals(2, bed.getVx());
        assertEquals(4, bed.getVy());
        assertEquals(-1, bed.getVz());
    }

    @Test
    void testGetCost() {
        Bed bed = new Bed(100, 100, 0);
        assertEquals(0, bed.getCost());
    }

    @Test
    void testSetCost() {
        Bed bed = new Bed(100, 100, 0);
        bed.setCost(500);
        assertEquals(500, bed.getCost());
    }

    @Test
    void testSetValue() {
        Bed bed = new Bed(100, 100, 0);
        bed.setValue(1000);
        assertEquals(1000, bed.getValue());
    }

    @Test
    void testSetCanGrab() {
        Bed bed = new Bed(100, 100, 0);
        bed.setCanGrab(false);
        assertFalse(bed.isCanGrab());
    }

    @Test
    void testSetGrabbed() {
        Bed bed = new Bed(100, 100, 0);
        bed.setGrabbed(true);
        assertTrue(bed.isGrabbed());
    }

    @Test
    void testSetRemoved() {
        Bed bed = new Bed(100, 100, 0);
        bed.setRemoved(true);
        assertTrue(bed.isRemoved());
    }

    @Test
    void testIsEnableWall_Default() {
        Bed bed = new Bed(100, 100, 0);
        assertTrue(bed.isEnableWall());
    }

    @Test
    void testSetEnableWall() {
        Bed bed = new Bed(100, 100, 0);
        bed.setEnableWall(false);
        assertFalse(bed.isEnableWall());
    }

    @Test
    void testGetSetOfsX_OfsY() {
        Bed bed = new Bed(100, 100, 0);
        bed.setOfsX(5);
        bed.setOfsY(10);
        assertEquals(5, bed.getOfsX());
        assertEquals(10, bed.getOfsY());
    }

    @Test
    void testGetDrawOfsX_DrawOfsY() {
        Bed bed = new Bed(100, 100, 0);
        bed.setOfsX(10);
        bed.setOfsY(20);
        assertEquals(110, bed.getDrawOfsX());
        assertEquals(120, bed.getDrawOfsY());
    }

    @Test
    void testGetScreenPivot_NotNull() {
        Bed bed = new Bed(100, 100, 0);
        assertNotNull(bed.getScreenPivot());
    }

    @Test
    void testSetScreenPivot() {
        Bed bed = new Bed(100, 100, 0);
        bed.setScreenPivot(50, 60);
        assertEquals(50, bed.getScreenPivot().getX());
        assertEquals(60, bed.getScreenPivot().getY());
    }

    @Test
    void testGetScreenRect_NotNull() {
        Bed bed = new Bed(100, 100, 0);
        assertNotNull(bed.getScreenRect());
    }

    @Test
    void testSetBxyz_ResetBPos() {
        Bed bed = new Bed(100, 100, 0);
        bed.setBx(3);
        bed.setBy(4);
        bed.setBz(5);
        assertEquals(3, bed.getBx());
        assertEquals(4, bed.getBy());
        assertEquals(5, bed.getBz());
        bed.resetBPos();
        assertEquals(0, bed.getBx());
        assertEquals(0, bed.getBy());
        assertEquals(0, bed.getBz());
    }

    @Test
    void testAddBxyz() {
        Bed bed = new Bed(100, 100, 0);
        bed.addBxyz(2, 3, 4);
        assertEquals(2, bed.getBx());
        assertEquals(3, bed.getBy());
        assertEquals(4, bed.getBz());
    }

    @Test
    void testIsbFallingUnderGround_Default() {
        Bed bed = new Bed(100, 100, 0);
        assertFalse(bed.isbFallingUnderGround());
    }

    @Test
    void testSetbFallingUnderGround() {
        Bed bed = new Bed(100, 100, 0);
        bed.setbFallingUnderGround(true);
        assertTrue(bed.isbFallingUnderGround());
    }

    @Test
    void testIsbInPool_Default() {
        Bed bed = new Bed(100, 100, 0);
        assertFalse(bed.isbInPool());
    }

    @Test
    void testSetbInPool() {
        Bed bed = new Bed(100, 100, 0);
        bed.setbInPool(true);
        assertTrue(bed.isbInPool());
    }

    @Test
    void testGetnMostDepth_Default() {
        Bed bed = new Bed(100, 100, 0);
        assertEquals(0, bed.getnMostDepth());
    }

    @Test
    void testSetnMostDepth() {
        Bed bed = new Bed(100, 100, 0);
        bed.setnMostDepth(-5);
        assertEquals(-5, bed.getnMostDepth());
    }

    @Test
    void testGetSetBindObj() {
        Bed bed = new Bed(100, 100, 0);
        bed.setBindObj(77);
        assertEquals(77, bed.getBindObj());
    }

    @Test
    void testCompareTo() {
        Bed b1 = new Bed(100, 100, 0);
        Bed b2 = new Bed(200, 200, 0);
        assertEquals(0, b1.compareTo(b2));
    }

    @Test
    void testGetVxyz() {
        Bed bed = new Bed(100, 100, 0);
        bed.setVx(1);
        bed.setVy(2);
        bed.setVz(3);
        int[] vxyz = bed.getVxyz();
        assertEquals(1, vxyz[0]);
        assertEquals(2, vxyz[1]);
        assertEquals(3, vxyz[2]);
    }

    @Test
    void testCalcPos_ClampBoundary() {
        Bed bed = new Bed(0, 0, 0);
        bed.setX(-100);
        bed.setY(-100);
        bed.calcPos();
        assertEquals(0, bed.getX());
        assertEquals(0, bed.getY());
    }

    @Test
    void testCalcPos_ClampUpperBoundary() {
        Bed bed = new Bed(0, 0, 0);
        int maxX = Translate.getMapW();
        int maxY = Translate.getMapH();
        bed.setX(maxX + 500);
        bed.setY(maxY + 500);
        bed.calcPos();
        assertEquals(maxX, bed.getX());
        assertEquals(maxY, bed.getY());
    }

    @Test
    void testGetBoundaryShape() {
        Bed bed = new Bed(100, 100, 0);
        src.draw.Rectangle4y r = new src.draw.Rectangle4y();
        bed.getBoundaryShape(r);
        assertNotNull(r);
    }

    @Test
    void testGetSetImgW_ImgH() {
        Bed bed = new Bed(100, 100, 0);
        bed.setImgW(128);
        bed.setImgH(64);
        assertEquals(128, bed.getImgW());
        assertEquals(64, bed.getImgH());
    }

    @Test
    void testGetSetPivX_PivY() {
        Bed bed = new Bed(100, 100, 0);
        bed.setPivX(16);
        bed.setPivY(32);
        assertEquals(16, bed.getPivX());
        assertEquals(32, bed.getPivY());
    }

    @Test
    void testSetInterval() {
        Bed bed = new Bed(100, 100, 0);
        bed.setInterval(100);
        assertEquals(100, bed.getInterval());
    }

    @Test
    void testSetObjEXType() {
        Bed bed = new Bed();
        bed.setObjEXType(ObjEXType.BED);
        assertEquals(ObjEXType.BED, bed.getObjEXType());
    }

    @Test
    void testSetOfsXY() {
        Bed bed = new Bed(100, 100, 0);
        bed.setOfsXY(7, 8);
        assertEquals(7, bed.getOfsX());
        assertEquals(8, bed.getOfsY());
    }

    @Test
    void testGetTmpPos_NotNull() {
        Bed bed = new Bed(100, 100, 0);
        assertNotNull(bed.getTmpPos());
    }

    @Test
    void testGetHitCheckObjType_DefaultZero() {
        Bed bed = new Bed(100, 100, 0);
        // ObjEX のデフォルト hitCheckObjType = 0
        assertEquals(0, bed.getHitCheckObjType());
    }

    // ---------------------------------------------------------------
    // 複数ベッドが個別ID で登録される
    // ---------------------------------------------------------------
    @Test
    void testMultipleBedsHaveUniqueIds() {
        Bed bed1 = new Bed(100, 100, 0);
        Bed bed2 = new Bed(200, 200, 0);
        assertNotEquals(bed1.getObjId(), bed2.getObjId());
        assertTrue(SimYukkuri.world.getCurrentMap().getBed().containsKey(bed1.getObjId()));
        assertTrue(SimYukkuri.world.getCurrentMap().getBed().containsKey(bed2.getObjId()));
    }

    // ---------------------------------------------------------------
    // objHitProcess のデフォルト（Bed は override していないので 0 を返す）
    // ---------------------------------------------------------------
    @Test
    void testObjHitProcess_Default() {
        Bed bed = new Bed(100, 100, 0);
        Bed other = new Bed(200, 200, 0);
        int result = bed.objHitProcess(other);
        assertEquals(0, result);
    }
}
