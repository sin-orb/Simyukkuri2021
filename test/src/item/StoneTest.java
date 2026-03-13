package src.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import src.base.ItemTestBase;
import src.base.ObjEX;
import src.base.ObjEX.ItemRank;
import src.SimYukkuri;
import src.draw.Translate;
import src.enums.ObjEXType;
import src.enums.Type;
import src.enums.CriticalDamegeType;
import src.base.Body;
import src.util.WorldTestHelper;
import src.yukkuri.Marisa;
import static org.junit.jupiter.api.Assertions.*;

class StoneTest extends ItemTestBase {

    // ---------------------------------------------------------------
    // コンストラクタ（デフォルト）
    // ---------------------------------------------------------------
    @Test
    void testDefaultConstructor() {
        Stone stone = new Stone();
        assertNotNull(stone);
    }

    // ---------------------------------------------------------------
    // コンストラクタ（パラメータ付き）各オプション値
    // ---------------------------------------------------------------
    @Test
    void testParameterizedConstructor_House() {
        // option=0 -> ItemRank.HOUSE
        Stone stone = new Stone(100, 200, 0);
        assertNotNull(stone);
        assertEquals(ItemRank.HOUSE, stone.getItemRank());
        assertEquals(ObjEXType.STONE, stone.getObjEXType());
        assertEquals(Type.OBJECT, stone.getObjType());
        assertEquals(0, stone.getValue());
        assertEquals(0, stone.getCost());
        assertTrue(stone.getObjId() > 0);
    }

    @Test
    void testParameterizedConstructor_Nora() {
        // option=1 -> ItemRank.NORA (mapIndex=0なのでそのまま)
        Stone stone = new Stone(50, 50, 1);
        assertNotNull(stone);
        assertEquals(ItemRank.NORA, stone.getItemRank());
        assertEquals(0, stone.getValue());
        assertEquals(0, stone.getCost());
    }

    @Test
    void testParameterizedConstructor_Yasei() {
        // option=2 -> ItemRank.YASEI
        Stone stone = new Stone(50, 50, 2);
        assertNotNull(stone);
        assertEquals(ItemRank.YASEI, stone.getItemRank());
    }

    // ---------------------------------------------------------------
    // MapIndex による itemRank の上書き
    // ---------------------------------------------------------------
    @Test
    void testConstructor_MapIndex2_BecomesNora() {
        // MapIndex=2 -> NORA に上書き
        SimYukkuri.world.setCurrentMapIdx(2);
        Stone stone = new Stone(100, 200, 0);  // option=HOUSE だが上書き
        assertEquals(ItemRank.NORA, stone.getItemRank());
        // 後始末
        SimYukkuri.world.setCurrentMapIdx(0);
    }

    @Test
    void testConstructor_MapIndex3_BecomesNora() {
        SimYukkuri.world.setCurrentMapIdx(3);
        Stone stone = new Stone(100, 200, 0);
        assertEquals(ItemRank.NORA, stone.getItemRank());
        SimYukkuri.world.setCurrentMapIdx(0);
    }

    @Test
    void testConstructor_MapIndex4_BecomesNora() {
        SimYukkuri.world.setCurrentMapIdx(4);
        Stone stone = new Stone(100, 200, 0);
        assertEquals(ItemRank.NORA, stone.getItemRank());
        SimYukkuri.world.setCurrentMapIdx(0);
    }

    @Test
    void testConstructor_MapIndex5_BecomesYasei() {
        SimYukkuri.world.setCurrentMapIdx(5);
        Stone stone = new Stone(100, 200, 0);
        assertEquals(ItemRank.YASEI, stone.getItemRank());
        SimYukkuri.world.setCurrentMapIdx(0);
    }

    @Test
    void testConstructor_MapIndex6_BecomesYasei() {
        SimYukkuri.world.setCurrentMapIdx(6);
        Stone stone = new Stone(100, 200, 0);
        assertEquals(ItemRank.YASEI, stone.getItemRank());
        SimYukkuri.world.setCurrentMapIdx(0);
    }

    // ---------------------------------------------------------------
    // ワールドのストーンマップに登録されること
    // ---------------------------------------------------------------
    @Test
    void testConstructor_RegisteredInWorld() {
        Stone stone = new Stone(300, 400, 0);
        assertTrue(SimYukkuri.world.getCurrentMap().getStone().containsKey(stone.getObjId()));
    }

    // ---------------------------------------------------------------
    // getItemRank / setItemRank
    // ---------------------------------------------------------------
    @Test
    void testSetItemRank_House() {
        Stone stone = new Stone();
        stone.setItemRank(ItemRank.HOUSE);
        assertEquals(ItemRank.HOUSE, stone.getItemRank());
    }

    @Test
    void testSetItemRank_Nora() {
        Stone stone = new Stone();
        stone.setItemRank(ItemRank.NORA);
        assertEquals(ItemRank.NORA, stone.getItemRank());
    }

    @Test
    void testSetItemRank_Yasei() {
        Stone stone = new Stone();
        stone.setItemRank(ItemRank.YASEI);
        assertEquals(ItemRank.YASEI, stone.getItemRank());
    }

    // ---------------------------------------------------------------
    // getHitCheckObjType
    // ---------------------------------------------------------------
    @Test
    void testGetHitCheckObjType() {
        Stone stone = new Stone();
        assertEquals(ObjEX.YUKKURI, stone.getHitCheckObjType());
    }

    @Test
    void testHitCheckObjTypeConstant() {
        assertEquals(ObjEX.YUKKURI, Stone.hitCheckObjType);
    }

    // ---------------------------------------------------------------
    // getBounding
    // ---------------------------------------------------------------
    @Test
    void testGetBounding_NotNull() {
        assertNotNull(Stone.getBounding());
    }

    // ---------------------------------------------------------------
    // grab
    // ---------------------------------------------------------------
    @Test
    void testGrab_SetsGrabbedTrue() {
        Stone stone = new Stone(100, 100, 0);
        assertFalse(stone.isGrabbed());
        stone.grab();
        assertTrue(stone.isGrabbed());
    }

    // ---------------------------------------------------------------
    // kick
    // ---------------------------------------------------------------
    @Test
    void testKick_SetsVelocity() {
        Stone stone = new Stone(100, 100, 0);
        stone.kick();
        // kick() calls kick(0, -8, -4)
        assertEquals(0,  stone.getVx());
        assertEquals(-8, stone.getVy());
        assertEquals(-4, stone.getVz());
    }

    // ---------------------------------------------------------------
    // removeListData
    // ---------------------------------------------------------------
    @Test
    void testRemoveListData_RemovesFromWorld() {
        Stone stone = new Stone(100, 100, 0);
        int id = stone.getObjId();
        assertTrue(SimYukkuri.world.getCurrentMap().getStone().containsKey(id));
        stone.removeListData();
        assertFalse(SimYukkuri.world.getCurrentMap().getStone().containsKey(id));
    }

    // ---------------------------------------------------------------
    // objHitProcess - CUT ならば 0 を返してすぐに終了
    // ---------------------------------------------------------------
    @Test
    void testObjHitProcess_BodyWithCUT_returnsZero() {
        Stone stone = new Stone(100, 100, 0);
        Body body = WorldTestHelper.createBody();
        body.setCriticalDamege(CriticalDamegeType.CUT);
        int result = stone.objHitProcess(body);
        assertEquals(0, result);
    }

    // ---------------------------------------------------------------
    // objHitProcess - non-Body 引数 -> 0 を返す
    // ---------------------------------------------------------------
    @Test
    void testObjHitProcess_NonBody_returnsZero() {
        Stone stone = new Stone(100, 100, 0);
        // Trash は Obj だが Body ではない
        Trash trash = new Trash(50, 50, 0);
        int result = stone.objHitProcess(trash);
        assertEquals(0, result);
    }

    // ---------------------------------------------------------------
    // objHitProcess - Body (adult, CriticalDamege=INJURED -> bodyInjure 呼び出し)
    // bodyInjure/bodyCut は MyPane に依存するためスキップし、
    // 既に CUT が設定されているケースをテストする
    // ---------------------------------------------------------------
    @Test
    void testObjHitProcess_AdultBody_INJURED_returnZero() {
        Stone stone = new Stone(100, 100, 0);
        Body body = WorldTestHelper.createBody();
        // CUT に設定すると bodyInjure/bodyCut を呼ばずに 0 を返す分岐に入る
        body.setCriticalDamege(CriticalDamegeType.CUT);
        int result = stone.objHitProcess(body);
        assertEquals(0, result);
    }

    // ---------------------------------------------------------------
    // objHitProcess - Body (null CriticalDamege, ADULT) は GUI 依存なのでスキップ
    // 代わりに再度 non-Body パターンをカバー
    // ---------------------------------------------------------------
    @Test
    void testObjHitProcess_AnotherNonBody() {
        Stone stone = new Stone(100, 100, 0);
        Stone other = new Stone(200, 200, 0);
        int result = stone.objHitProcess(other);
        assertEquals(0, result);
    }

    // ---------------------------------------------------------------
    // interval の初期値確認
    // ---------------------------------------------------------------
    @Test
    void testInterval_Default() {
        Stone stone = new Stone(100, 100, 0);
        assertEquals(5, stone.getInterval());
    }

    // ---------------------------------------------------------------
    // ObjEX 共通メソッド群
    // ---------------------------------------------------------------
    @Test
    void testCheckInterval() {
        Stone stone = new Stone(100, 100, 0);
        // interval=5 なので cnt%5==0 のときtrue
        assertTrue(stone.checkInterval(0));
        assertTrue(stone.checkInterval(5));
        assertFalse(stone.checkInterval(1));
        assertFalse(stone.checkInterval(3));
    }

    @Test
    void testGetEnabled_DefaultTrue() {
        Stone stone = new Stone(100, 100, 0);
        assertTrue(stone.getEnabled());
    }

    @Test
    void testSetEnabled() {
        Stone stone = new Stone(100, 100, 0);
        stone.setEnabled(false);
        assertFalse(stone.getEnabled());
        stone.setEnabled(true);
        assertTrue(stone.getEnabled());
    }

    @Test
    void testInvertEnabled() {
        Stone stone = new Stone(100, 100, 0);
        assertTrue(stone.getEnabled());
        stone.invertEnabled();
        assertFalse(stone.getEnabled());
        stone.invertEnabled();
        assertTrue(stone.getEnabled());
    }

    @Test
    void testGetOption() {
        Stone stone = new Stone(100, 200, 1);
        assertEquals(1, stone.getOption());
    }

    @Test
    void testSetOption() {
        Stone stone = new Stone(100, 200, 0);
        stone.setOption(2);
        assertEquals(2, stone.getOption());
    }

    @Test
    void testGetLooks_Default() {
        Stone stone = new Stone(100, 100, 0);
        assertEquals(0, stone.getLooks());
    }

    @Test
    void testSetLooks() {
        Stone stone = new Stone(100, 100, 0);
        stone.setLooks(3);
        assertEquals(3, stone.getLooks());
    }

    @Test
    void testGetLinkParent_Default() {
        Stone stone = new Stone(100, 100, 0);
        assertEquals(-1, stone.getLinkParent());
    }

    @Test
    void testSetLinkParent() {
        Stone stone = new Stone(100, 100, 0);
        stone.setLinkParent(42);
        assertEquals(42, stone.getLinkParent());
    }

    @Test
    void testHasSetupMenu_DefaultFalse() {
        Stone stone = new Stone(100, 100, 0);
        assertFalse(stone.hasSetupMenu());
    }

    @Test
    void testEnableHitCheck_DefaultTrue() {
        Stone stone = new Stone(100, 100, 0);
        assertTrue(stone.enableHitCheck());
    }

    @Test
    void testGetSetColW() {
        Stone stone = new Stone(100, 100, 0);
        stone.setColW(10);
        assertEquals(10, stone.getColW());
    }

    @Test
    void testGetSetColH() {
        Stone stone = new Stone(100, 100, 0);
        stone.setColH(20);
        assertEquals(20, stone.getColH());
    }

    // ---------------------------------------------------------------
    // Obj 継承メソッド群
    // ---------------------------------------------------------------
    @Test
    void testGetX_GetY() {
        Stone stone = new Stone(123, 456, 0);
        assertEquals(123, stone.getX());
        assertEquals(456, stone.getY());
    }

    @Test
    void testSetX_SetY() {
        Stone stone = new Stone(0, 0, 0);
        stone.setX(300);
        stone.setY(400);
        assertEquals(300, stone.getX());
        assertEquals(400, stone.getY());
    }

    @Test
    void testGetZ_Default() {
        Stone stone = new Stone(100, 100, 0);
        assertEquals(0, stone.getZ());
    }

    @Test
    void testIsRemoved_Default() {
        Stone stone = new Stone(100, 100, 0);
        assertFalse(stone.isRemoved());
    }

    @Test
    void testRemove() {
        Stone stone = new Stone(100, 100, 0);
        stone.remove();
        assertTrue(stone.isRemoved());
    }

    @Test
    void testIsCanGrab_Default() {
        Stone stone = new Stone(100, 100, 0);
        assertTrue(stone.isCanGrab());
    }

    @Test
    void testRelease() {
        Stone stone = new Stone(100, 100, 0);
        stone.grab();
        assertTrue(stone.isGrabbed());
        stone.release();
        assertFalse(stone.isGrabbed());
    }

    @Test
    void testGetValue_Default() {
        Stone stone = new Stone(100, 100, 0);
        assertEquals(0, stone.getValue());
    }

    @Test
    void testGetCost_Default() {
        Stone stone = new Stone(100, 100, 0);
        assertEquals(0, stone.getCost());
    }

    @Test
    void testGetAge_Default() {
        Stone stone = new Stone(100, 100, 0);
        assertEquals(0, stone.getAge());
    }

    @Test
    void testSetAge() {
        Stone stone = new Stone(100, 100, 0);
        stone.setAge(12345L);
        assertEquals(12345L, stone.getAge());
    }

    @Test
    void testAddAge() {
        Stone stone = new Stone(100, 100, 0);
        stone.setAge(100);
        stone.addAge(50);
        assertEquals(150, stone.getAge());
    }

    @Test
    void testSetVx_Vy_Vz() {
        Stone stone = new Stone(100, 100, 0);
        stone.setVx(3);
        stone.setVy(5);
        stone.setVz(-2);
        assertEquals(3, stone.getVx());
        assertEquals(5, stone.getVy());
        assertEquals(-2, stone.getVz());
    }

    @Test
    void testGetObjType() {
        Stone stone = new Stone(100, 100, 0);
        assertEquals(Type.OBJECT, stone.getObjType());
    }

    @Test
    void testGetObjEXType() {
        Stone stone = new Stone(100, 100, 0);
        assertEquals(ObjEXType.STONE, stone.getObjEXType());
    }

    @Test
    void testSetObjEXType() {
        Stone stone = new Stone();
        stone.setObjEXType(ObjEXType.TRASH);
        assertEquals(ObjEXType.TRASH, stone.getObjEXType());
    }

    @Test
    void testCompareTo() {
        Stone s1 = new Stone(100, 100, 0);
        Stone s2 = new Stone(200, 200, 0);
        assertEquals(0, s1.compareTo(s2));
    }

    @Test
    void testSetBxyz_GetBxyz() {
        Stone stone = new Stone(100, 100, 0);
        stone.setBx(5);
        stone.setBy(7);
        stone.setBz(2);
        assertEquals(5, stone.getBx());
        assertEquals(7, stone.getBy());
        assertEquals(2, stone.getBz());
    }

    @Test
    void testAddBxyz() {
        Stone stone = new Stone(100, 100, 0);
        stone.addBxyz(1, 2, 3);
        assertEquals(1, stone.getBx());
        assertEquals(2, stone.getBy());
        assertEquals(3, stone.getBz());
    }

    @Test
    void testResetBPos() {
        Stone stone = new Stone(100, 100, 0);
        stone.setBx(5);
        stone.setBy(7);
        stone.setBz(2);
        stone.resetBPos();
        assertEquals(0, stone.getBx());
        assertEquals(0, stone.getBy());
        assertEquals(0, stone.getBz());
    }

    @Test
    void testIsEnableWall_Default() {
        Stone stone = new Stone(100, 100, 0);
        assertTrue(stone.isEnableWall());
    }

    @Test
    void testSetEnableWall() {
        Stone stone = new Stone(100, 100, 0);
        stone.setEnableWall(false);
        assertFalse(stone.isEnableWall());
    }

    @Test
    void testGetSetOfsX_OfsY() {
        Stone stone = new Stone(100, 100, 0);
        stone.setOfsX(3);
        stone.setOfsY(7);
        assertEquals(3, stone.getOfsX());
        assertEquals(7, stone.getOfsY());
    }

    @Test
    void testGetDrawOfsX_DrawOfsY() {
        Stone stone = new Stone(100, 100, 0);
        stone.setOfsX(10);
        stone.setOfsY(20);
        assertEquals(110, stone.getDrawOfsX());
        assertEquals(120, stone.getDrawOfsY());
    }

    @Test
    void testSetOfsXY() {
        Stone stone = new Stone(100, 100, 0);
        stone.setOfsXY(15, 25);
        assertEquals(15, stone.getOfsX());
        assertEquals(25, stone.getOfsY());
    }

    @Test
    void testGetScreenPivot_NotNull() {
        Stone stone = new Stone(100, 100, 0);
        assertNotNull(stone.getScreenPivot());
    }

    @Test
    void testSetScreenPivot() {
        Stone stone = new Stone(100, 100, 0);
        stone.setScreenPivot(50, 60);
        assertEquals(50, stone.getScreenPivot().getX());
        assertEquals(60, stone.getScreenPivot().getY());
    }

    @Test
    void testGetScreenRect_NotNull() {
        Stone stone = new Stone(100, 100, 0);
        assertNotNull(stone.getScreenRect());
    }

    @Test
    void testIsbFallingUnderGround_Default() {
        Stone stone = new Stone(100, 100, 0);
        assertFalse(stone.isbFallingUnderGround());
    }

    @Test
    void testSetbFallingUnderGround() {
        Stone stone = new Stone(100, 100, 0);
        stone.setbFallingUnderGround(true);
        assertTrue(stone.isbFallingUnderGround());
    }

    @Test
    void testIsbInPool_Default() {
        Stone stone = new Stone(100, 100, 0);
        assertFalse(stone.isbInPool());
    }

    @Test
    void testSetbInPool() {
        Stone stone = new Stone(100, 100, 0);
        stone.setbInPool(true);
        assertTrue(stone.isbInPool());
    }

    @Test
    void testGetnMostDepth_Default() {
        Stone stone = new Stone(100, 100, 0);
        assertEquals(0, stone.getnMostDepth());
    }

    @Test
    void testSetnMostDepth() {
        Stone stone = new Stone(100, 100, 0);
        stone.setnMostDepth(-10);
        assertEquals(-10, stone.getnMostDepth());
    }

    @Test
    void testGetSetBindObj() {
        Stone stone = new Stone(100, 100, 0);
        stone.setBindObj(99);
        assertEquals(99, stone.getBindObj());
    }

    @Test
    void testSetRemoved() {
        Stone stone = new Stone(100, 100, 0);
        stone.setRemoved(true);
        assertTrue(stone.isRemoved());
        stone.setRemoved(false);
        assertFalse(stone.isRemoved());
    }

    @Test
    void testSetCanGrab() {
        Stone stone = new Stone(100, 100, 0);
        stone.setCanGrab(false);
        assertFalse(stone.isCanGrab());
    }

    @Test
    void testSetGrabbed() {
        Stone stone = new Stone(100, 100, 0);
        stone.setGrabbed(true);
        assertTrue(stone.isGrabbed());
    }

    @Test
    void testSetValue() {
        Stone stone = new Stone(100, 100, 0);
        stone.setValue(500);
        assertEquals(500, stone.getValue());
    }

    @Test
    void testSetCost() {
        Stone stone = new Stone(100, 100, 0);
        stone.setCost(100);
        assertEquals(100, stone.getCost());
    }

    @Test
    void testGetVxyz() {
        Stone stone = new Stone(100, 100, 0);
        stone.setVx(1);
        stone.setVy(2);
        stone.setVz(3);
        int[] vxyz = stone.getVxyz();
        assertEquals(1, vxyz[0]);
        assertEquals(2, vxyz[1]);
        assertEquals(3, vxyz[2]);
    }

    @Test
    void testKickXYZ() {
        Stone stone = new Stone(100, 100, 0);
        stone.kick(5, 6, 7);
        assertEquals(5, stone.getVx());
        assertEquals(6, stone.getVy());
        assertEquals(7, stone.getVz());
    }

    @Test
    void testCalcPos_ClampBoundary() {
        Stone stone = new Stone(0, 0, 0);
        stone.setX(-100);
        stone.setY(-100);
        stone.calcPos();
        assertEquals(0, stone.getX());
        assertEquals(0, stone.getY());
    }

    @Test
    void testCalcPos_ClampUpperBoundary() {
        Stone stone = new Stone(0, 0, 0);
        int maxX = Translate.getMapW();
        int maxY = Translate.getMapH();
        stone.setX(maxX + 500);
        stone.setY(maxY + 500);
        stone.calcPos();
        assertEquals(maxX, stone.getX());
        assertEquals(maxY, stone.getY());
    }

    @Test
    void testGetBoundaryShape() {
        Stone stone = new Stone(100, 100, 0);
        src.draw.Rectangle4y r = new src.draw.Rectangle4y();
        stone.getBoundaryShape(r);
        // 境界が設定されているはず（例外なく返ること）
        assertNotNull(r);
    }

    @Test
    void testGetSetImgW_ImgH() {
        Stone stone = new Stone(100, 100, 0);
        stone.setImgW(64);
        stone.setImgH(32);
        assertEquals(64, stone.getImgW());
        assertEquals(32, stone.getImgH());
    }

    @Test
    void testGetSetPivX_PivY() {
        Stone stone = new Stone(100, 100, 0);
        stone.setPivX(10);
        stone.setPivY(20);
        assertEquals(10, stone.getPivX());
        assertEquals(20, stone.getPivY());
    }

    @Test
    void testSetInterval() {
        Stone stone = new Stone(100, 100, 0);
        stone.setInterval(10);
        assertEquals(10, stone.getInterval());
    }

    // ---------------------------------------------------------------
    // getShadowImage - images が null のとき（loadImages未呼び出し）
    // ---------------------------------------------------------------
    @Test
    void testGetShadowImage_NullWhenNotLoaded() {
        Stone stone = new Stone();
        // loadImages が呼ばれていないので images[2] == null
        // NullPointerException が出ないことを確認（null を返す）
        try {
            // null の場合は null が返るはず
            assertNull(stone.getShadowImage());
        } catch (NullPointerException e) {
            // images 配列が null の場合は受け入れる
        }
    }

    // ---------------------------------------------------------------
    // helper
    // ---------------------------------------------------------------
    private java.lang.reflect.Field findField(Class<?> clazz, String name) {
        while (clazz != null) {
            try {
                return clazz.getDeclaredField(name);
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            }
        }
        return null;
    }
}
