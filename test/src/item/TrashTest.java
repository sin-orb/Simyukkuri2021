package src.item;

import org.junit.jupiter.api.Test;
import src.base.ItemTestBase;
import src.base.ObjEX;
import src.SimYukkuri;
import src.draw.Translate;
import src.enums.ObjEXType;
import src.enums.Type;
import static org.junit.jupiter.api.Assertions.*;

class TrashTest extends ItemTestBase {

    // ---------------------------------------------------------------
    // コンストラクタ（デフォルト）
    // ---------------------------------------------------------------
    @Test
    void testDefaultConstructor() {
        Trash trash = new Trash();
        assertNotNull(trash);
    }

    // ---------------------------------------------------------------
    // コンストラクタ（パラメータ付き）
    // ---------------------------------------------------------------
    @Test
    void testParameterizedConstructor_Basic() {
        Trash trash = new Trash(100, 200, 0);
        assertNotNull(trash);
        assertEquals(Type.OBJECT, trash.getObjType());
        assertEquals(ObjEXType.TRASH, trash.getObjEXType());
        assertEquals(0, trash.getValue());
        assertEquals(0, trash.getCost());
        assertTrue(trash.getObjId() > 0);
    }

    @Test
    void testParameterizedConstructor_Position() {
        Trash trash = new Trash(150, 250, 1);
        assertEquals(150, trash.getX());
        assertEquals(250, trash.getY());
    }

    // ---------------------------------------------------------------
    // ワールドのゴミマップに登録されること
    // ---------------------------------------------------------------
    @Test
    void testConstructor_RegisteredInWorld() {
        Trash trash = new Trash(300, 400, 0);
        assertTrue(SimYukkuri.world.getCurrentMap().getTrash().containsKey(trash.getObjId()));
    }

    @Test
    void testMultipleTrashHaveUniqueIds() {
        Trash t1 = new Trash(100, 100, 0);
        Trash t2 = new Trash(200, 200, 0);
        assertNotEquals(t1.getObjId(), t2.getObjId());
        assertTrue(SimYukkuri.world.getCurrentMap().getTrash().containsKey(t1.getObjId()));
        assertTrue(SimYukkuri.world.getCurrentMap().getTrash().containsKey(t2.getObjId()));
    }

    // ---------------------------------------------------------------
    // removeListData
    // ---------------------------------------------------------------
    @Test
    void testRemoveListData_RemovesFromWorld() {
        Trash trash = new Trash(100, 100, 0);
        int id = trash.getObjId();
        assertTrue(SimYukkuri.world.getCurrentMap().getTrash().containsKey(id));
        trash.removeListData();
        assertFalse(SimYukkuri.world.getCurrentMap().getTrash().containsKey(id));
    }

    // ---------------------------------------------------------------
    // kick
    // ---------------------------------------------------------------
    @Test
    void testKick_SetsVelocity() {
        Trash trash = new Trash(100, 100, 0);
        trash.kick();
        // kick() calls kick(0, -8, -4)
        assertEquals(0,  trash.getVx());
        assertEquals(-8, trash.getVy());
        assertEquals(-4, trash.getVz());
    }

    @Test
    void testKickXYZ() {
        Trash trash = new Trash(100, 100, 0);
        trash.kick(3, 5, -2);
        assertEquals(3, trash.getVx());
        assertEquals(5, trash.getVy());
        assertEquals(-2, trash.getVz());
    }

    // ---------------------------------------------------------------
    // getBounding
    // ---------------------------------------------------------------
    @Test
    void testGetBounding_NotNull() {
        assertNotNull(Trash.getBounding());
    }

    // ---------------------------------------------------------------
    // getShadowImage - loadImages 未呼び出し時は null
    // ---------------------------------------------------------------
    @Test
    void testGetShadowImage_NullWhenNotLoaded() {
        Trash trash = new Trash();
        // images が null のとき NullPointerException か null が返る
        try {
            assertNull(trash.getShadowImage());
        } catch (NullPointerException e) {
            // images 配列が null の場合は受け入れる
        }
    }

    // ---------------------------------------------------------------
    // getHitCheckObjType（ObjEX のデフォルト = 0）
    // ---------------------------------------------------------------
    @Test
    void testGetHitCheckObjType_DefaultZero() {
        Trash trash = new Trash(100, 100, 0);
        assertEquals(0, trash.getHitCheckObjType());
    }

    // ---------------------------------------------------------------
    // objHitProcess のデフォルト（Trash は override していないので 0 を返す）
    // ---------------------------------------------------------------
    @Test
    void testObjHitProcess_Default() {
        Trash trash = new Trash(100, 100, 0);
        Trash other = new Trash(200, 200, 0);
        int result = trash.objHitProcess(other);
        assertEquals(0, result);
    }

    // ---------------------------------------------------------------
    // ObjEX 共通メソッド群
    // ---------------------------------------------------------------
    @Test
    void testGetObjEXType() {
        Trash trash = new Trash(100, 100, 0);
        assertEquals(ObjEXType.TRASH, trash.getObjEXType());
    }

    @Test
    void testGetObjType() {
        Trash trash = new Trash(100, 100, 0);
        assertEquals(Type.OBJECT, trash.getObjType());
    }

    @Test
    void testCheckInterval() {
        Trash trash = new Trash(100, 100, 0);
        // interval=1（ObjEX デフォルト）
        assertTrue(trash.checkInterval(0));
        assertTrue(trash.checkInterval(1));
        assertTrue(trash.checkInterval(100));
    }

    @Test
    void testGetEnabled_DefaultTrue() {
        Trash trash = new Trash(100, 100, 0);
        assertTrue(trash.getEnabled());
    }

    @Test
    void testSetEnabled() {
        Trash trash = new Trash(100, 100, 0);
        trash.setEnabled(false);
        assertFalse(trash.getEnabled());
        trash.setEnabled(true);
        assertTrue(trash.getEnabled());
    }

    @Test
    void testInvertEnabled() {
        Trash trash = new Trash(100, 100, 0);
        assertTrue(trash.getEnabled());
        trash.invertEnabled();
        assertFalse(trash.getEnabled());
        trash.invertEnabled();
        assertTrue(trash.getEnabled());
    }

    @Test
    void testHasSetupMenu_DefaultFalse() {
        Trash trash = new Trash(100, 100, 0);
        assertFalse(trash.hasSetupMenu());
    }

    @Test
    void testEnableHitCheck_DefaultTrue() {
        Trash trash = new Trash(100, 100, 0);
        assertTrue(trash.enableHitCheck());
    }

    @Test
    void testGetOption() {
        Trash trash = new Trash(100, 200, 1);
        assertEquals(1, trash.getOption());
    }

    @Test
    void testSetOption() {
        Trash trash = new Trash(100, 200, 0);
        trash.setOption(3);
        assertEquals(3, trash.getOption());
    }

    @Test
    void testGetLooks_Default() {
        Trash trash = new Trash(100, 100, 0);
        assertEquals(0, trash.getLooks());
    }

    @Test
    void testSetLooks() {
        Trash trash = new Trash(100, 100, 0);
        trash.setLooks(2);
        assertEquals(2, trash.getLooks());
    }

    @Test
    void testGetLinkParent_Default() {
        Trash trash = new Trash(100, 100, 0);
        assertEquals(-1, trash.getLinkParent());
    }

    @Test
    void testSetLinkParent() {
        Trash trash = new Trash(100, 100, 0);
        trash.setLinkParent(55);
        assertEquals(55, trash.getLinkParent());
    }

    @Test
    void testGetSetColW() {
        Trash trash = new Trash(100, 100, 0);
        trash.setColW(12);
        assertEquals(12, trash.getColW());
    }

    @Test
    void testGetSetColH() {
        Trash trash = new Trash(100, 100, 0);
        trash.setColH(18);
        assertEquals(18, trash.getColH());
    }

    // ---------------------------------------------------------------
    // Obj 継承メソッド群
    // ---------------------------------------------------------------
    @Test
    void testGetX_GetY() {
        Trash trash = new Trash(111, 222, 0);
        assertEquals(111, trash.getX());
        assertEquals(222, trash.getY());
    }

    @Test
    void testSetX_SetY() {
        Trash trash = new Trash(0, 0, 0);
        trash.setX(500);
        trash.setY(600);
        assertEquals(500, trash.getX());
        assertEquals(600, trash.getY());
    }

    @Test
    void testGetZ_Default() {
        Trash trash = new Trash(100, 100, 0);
        assertEquals(0, trash.getZ());
    }

    @Test
    void testIsRemoved_Default() {
        Trash trash = new Trash(100, 100, 0);
        assertFalse(trash.isRemoved());
    }

    @Test
    void testRemove() {
        Trash trash = new Trash(100, 100, 0);
        trash.remove();
        assertTrue(trash.isRemoved());
    }

    @Test
    void testSetRemoved() {
        Trash trash = new Trash(100, 100, 0);
        trash.setRemoved(true);
        assertTrue(trash.isRemoved());
        trash.setRemoved(false);
        assertFalse(trash.isRemoved());
    }

    @Test
    void testIsCanGrab_Default() {
        Trash trash = new Trash(100, 100, 0);
        assertTrue(trash.isCanGrab());
    }

    @Test
    void testSetCanGrab() {
        Trash trash = new Trash(100, 100, 0);
        trash.setCanGrab(false);
        assertFalse(trash.isCanGrab());
    }

    @Test
    void testGrab() {
        Trash trash = new Trash(100, 100, 0);
        assertFalse(trash.isGrabbed());
        trash.grab();
        assertTrue(trash.isGrabbed());
    }

    @Test
    void testRelease() {
        Trash trash = new Trash(100, 100, 0);
        trash.grab();
        assertTrue(trash.isGrabbed());
        trash.release();
        assertFalse(trash.isGrabbed());
    }

    @Test
    void testSetGrabbed() {
        Trash trash = new Trash(100, 100, 0);
        trash.setGrabbed(true);
        assertTrue(trash.isGrabbed());
    }

    @Test
    void testGetValue_Default() {
        Trash trash = new Trash(100, 100, 0);
        assertEquals(0, trash.getValue());
    }

    @Test
    void testSetValue() {
        Trash trash = new Trash(100, 100, 0);
        trash.setValue(100);
        assertEquals(100, trash.getValue());
    }

    @Test
    void testGetCost_Default() {
        Trash trash = new Trash(100, 100, 0);
        assertEquals(0, trash.getCost());
    }

    @Test
    void testSetCost() {
        Trash trash = new Trash(100, 100, 0);
        trash.setCost(50);
        assertEquals(50, trash.getCost());
    }

    @Test
    void testGetAge_Default() {
        Trash trash = new Trash(100, 100, 0);
        assertEquals(0, trash.getAge());
    }

    @Test
    void testSetAge() {
        Trash trash = new Trash(100, 100, 0);
        trash.setAge(9999L);
        assertEquals(9999L, trash.getAge());
    }

    @Test
    void testAddAge() {
        Trash trash = new Trash(100, 100, 0);
        trash.setAge(500);
        trash.addAge(300);
        assertEquals(800, trash.getAge());
    }

    @Test
    void testSetVx_Vy_Vz() {
        Trash trash = new Trash(100, 100, 0);
        trash.setVx(7);
        trash.setVy(-3);
        trash.setVz(1);
        assertEquals(7, trash.getVx());
        assertEquals(-3, trash.getVy());
        assertEquals(1, trash.getVz());
    }

    @Test
    void testGetVxyz() {
        Trash trash = new Trash(100, 100, 0);
        trash.setVx(2);
        trash.setVy(3);
        trash.setVz(4);
        int[] vxyz = trash.getVxyz();
        assertEquals(2, vxyz[0]);
        assertEquals(3, vxyz[1]);
        assertEquals(4, vxyz[2]);
    }

    @Test
    void testGetSetOfsX_OfsY() {
        Trash trash = new Trash(100, 100, 0);
        trash.setOfsX(4);
        trash.setOfsY(8);
        assertEquals(4, trash.getOfsX());
        assertEquals(8, trash.getOfsY());
    }

    @Test
    void testGetDrawOfsX_DrawOfsY() {
        Trash trash = new Trash(100, 100, 0);
        trash.setOfsX(5);
        trash.setOfsY(10);
        assertEquals(105, trash.getDrawOfsX());
        assertEquals(110, trash.getDrawOfsY());
    }

    @Test
    void testSetOfsXY() {
        Trash trash = new Trash(100, 100, 0);
        trash.setOfsXY(11, 22);
        assertEquals(11, trash.getOfsX());
        assertEquals(22, trash.getOfsY());
    }

    @Test
    void testGetScreenPivot_NotNull() {
        Trash trash = new Trash(100, 100, 0);
        assertNotNull(trash.getScreenPivot());
    }

    @Test
    void testSetScreenPivot() {
        Trash trash = new Trash(100, 100, 0);
        trash.setScreenPivot(30, 40);
        assertEquals(30, trash.getScreenPivot().getX());
        assertEquals(40, trash.getScreenPivot().getY());
    }

    @Test
    void testGetScreenRect_NotNull() {
        Trash trash = new Trash(100, 100, 0);
        assertNotNull(trash.getScreenRect());
    }

    @Test
    void testSetBxyz_ResetBPos() {
        Trash trash = new Trash(100, 100, 0);
        trash.setBx(1);
        trash.setBy(2);
        trash.setBz(3);
        assertEquals(1, trash.getBx());
        assertEquals(2, trash.getBy());
        assertEquals(3, trash.getBz());
        trash.resetBPos();
        assertEquals(0, trash.getBx());
        assertEquals(0, trash.getBy());
        assertEquals(0, trash.getBz());
    }

    @Test
    void testAddBxyz() {
        Trash trash = new Trash(100, 100, 0);
        trash.addBxyz(5, 6, 7);
        assertEquals(5, trash.getBx());
        assertEquals(6, trash.getBy());
        assertEquals(7, trash.getBz());
    }

    @Test
    void testIsEnableWall_Default() {
        Trash trash = new Trash(100, 100, 0);
        assertTrue(trash.isEnableWall());
    }

    @Test
    void testSetEnableWall() {
        Trash trash = new Trash(100, 100, 0);
        trash.setEnableWall(false);
        assertFalse(trash.isEnableWall());
    }

    @Test
    void testIsbFallingUnderGround_Default() {
        Trash trash = new Trash(100, 100, 0);
        assertFalse(trash.isbFallingUnderGround());
    }

    @Test
    void testSetbFallingUnderGround() {
        Trash trash = new Trash(100, 100, 0);
        trash.setbFallingUnderGround(true);
        assertTrue(trash.isbFallingUnderGround());
    }

    @Test
    void testIsbInPool_Default() {
        Trash trash = new Trash(100, 100, 0);
        assertFalse(trash.isbInPool());
    }

    @Test
    void testSetbInPool() {
        Trash trash = new Trash(100, 100, 0);
        trash.setbInPool(true);
        assertTrue(trash.isbInPool());
    }

    @Test
    void testGetnMostDepth_Default() {
        Trash trash = new Trash(100, 100, 0);
        assertEquals(0, trash.getnMostDepth());
    }

    @Test
    void testSetnMostDepth() {
        Trash trash = new Trash(100, 100, 0);
        trash.setnMostDepth(-20);
        assertEquals(-20, trash.getnMostDepth());
    }

    @Test
    void testGetSetBindObj() {
        Trash trash = new Trash(100, 100, 0);
        trash.setBindObj(33);
        assertEquals(33, trash.getBindObj());
    }

    @Test
    void testCompareTo() {
        Trash t1 = new Trash(100, 100, 0);
        Trash t2 = new Trash(200, 200, 0);
        assertEquals(0, t1.compareTo(t2));
    }

    @Test
    void testCalcPos_ClampBoundary() {
        Trash trash = new Trash(0, 0, 0);
        trash.setX(-100);
        trash.setY(-100);
        trash.calcPos();
        assertEquals(0, trash.getX());
        assertEquals(0, trash.getY());
    }

    @Test
    void testCalcPos_ClampUpperBoundary() {
        Trash trash = new Trash(0, 0, 0);
        int maxX = Translate.getMapW();
        int maxY = Translate.getMapH();
        trash.setX(maxX + 500);
        trash.setY(maxY + 500);
        trash.calcPos();
        assertEquals(maxX, trash.getX());
        assertEquals(maxY, trash.getY());
    }

    @Test
    void testGetBoundaryShape() {
        Trash trash = new Trash(100, 100, 0);
        src.draw.Rectangle4y r = new src.draw.Rectangle4y();
        trash.getBoundaryShape(r);
        assertNotNull(r);
    }

    @Test
    void testGetSetImgW_ImgH() {
        Trash trash = new Trash(100, 100, 0);
        trash.setImgW(48);
        trash.setImgH(24);
        assertEquals(48, trash.getImgW());
        assertEquals(24, trash.getImgH());
    }

    @Test
    void testGetSetPivX_PivY() {
        Trash trash = new Trash(100, 100, 0);
        trash.setPivX(8);
        trash.setPivY(16);
        assertEquals(8, trash.getPivX());
        assertEquals(16, trash.getPivY());
    }

    @Test
    void testSetInterval() {
        Trash trash = new Trash(100, 100, 0);
        trash.setInterval(7);
        assertEquals(7, trash.getInterval());
    }

    @Test
    void testSetObjEXType() {
        Trash trash = new Trash();
        trash.setObjEXType(ObjEXType.TRASH);
        assertEquals(ObjEXType.TRASH, trash.getObjEXType());
    }

    @Test
    void testGetTmpPos_NotNull() {
        Trash trash = new Trash(100, 100, 0);
        assertNotNull(trash.getTmpPos());
    }
}
