package src.base;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import src.SimYukkuri;
import src.draw.Translate;
import src.draw.World;
import src.enums.Event;
import src.enums.Type;
import src.draw.Point4y;

public class ObjEXTest {

    /** テスト用のObjEX具象サブクラス */
    static class StubObjEX extends ObjEX {
        boolean removeListDataCalled = false;
        boolean objHitProcessed = false;

        public StubObjEX(int initX, int initY, int initOption) {
            super(initX, initY, initOption);
        }

        public StubObjEX() {
            super();
        }

        @Override
        public int getImageLayer(BufferedImage[] layer) {
            return 0;
        }

        @Override
        public BufferedImage getShadowImage() {
            return null;
        }

        @Override
        public void removeListData() {
            removeListDataCalled = true;
        }

        @Override
        public int objHitProcess(Obj o) {
            objHitProcessed = true;
            return 0;
        }
    }

    @BeforeEach
    public void setUp() {
        SimYukkuri.world = new World();
        Translate.setMapSize(999, 999, 499); // mapW=1000, mapH=1000
        Translate.setCanvasSize(800, 600, 100, 100, new float[] {1.0f});
        Translate.createTransTable(false);
        // wallMapを初期化（Barrier.onBarrierで参照される）
        int[][] wallMap = new int[1001][1001];
        SimYukkuri.world.getCurrentMap().setWallMap(wallMap);
    }

    // --- コンストラクタ ---

    @Test
    public void testConstructorSetsFields() {
        StubObjEX obj = new StubObjEX(100, 200, 5);

        assertEquals(100, obj.getX());
        assertEquals(200, obj.getY());
        assertEquals(0, obj.getZ());
        assertEquals(5, obj.getOption());
        assertTrue(obj.getEnabled());
        assertEquals(Type.PLATFORM, obj.getObjType());
        assertTrue(obj.objId > 0);
    }

    @Test
    public void testDefaultConstructor() {
        StubObjEX obj = new StubObjEX();
        assertEquals(0, obj.getOption());
    }

    // --- clockTick: removed ---

    @Test
    public void testClockTickRemovedCallsRemoveListData() {
        StubObjEX obj = new StubObjEX(100, 100, 0);
        obj.setRemoved(true);

        Event result = obj.clockTick();

        assertEquals(Event.REMOVED, result);
        assertTrue(obj.removeListDataCalled);
    }

    // --- clockTick: grabbed → 移動しない ---

    @Test
    public void testClockTickGrabbedNoMovement() {
        StubObjEX obj = new StubObjEX(100, 100, 0);
        obj.setGrabbed(true);
        obj.setVx(10);
        obj.setVy(10);

        obj.clockTick();

        // grabbed=true → 移動処理スキップ, upDate()は呼ばれる
        // bx,by,bzがリセットされる
        assertEquals(0, obj.getBx());
        assertEquals(0, obj.getBy());
        assertEquals(0, obj.getBz());
    }

    // --- clockTick: x方向移動 ---

    @Test
    public void testClockTickMoveX() {
        StubObjEX obj = new StubObjEX(500, 500, 0);
        obj.setVx(10);

        obj.clockTick();

        assertEquals(510, obj.getX());
    }

    @Test
    public void testClockTickXBoundsLow() {
        StubObjEX obj = new StubObjEX(5, 500, 0);
        obj.setVx(-100);

        obj.clockTick();

        assertEquals(0, obj.getX());
        // vx *= -1 → 100
        assertEquals(100, obj.getVx());
    }

    @Test
    public void testClockTickXBoundsHigh() {
        StubObjEX obj = new StubObjEX(990, 500, 0);
        obj.setVx(100);

        obj.clockTick();

        assertEquals(Translate.getMapW(), obj.getX());
        assertEquals(-100, obj.getVx());
    }

    // --- clockTick: y方向移動 ---

    @Test
    public void testClockTickMoveY() {
        StubObjEX obj = new StubObjEX(500, 500, 0);
        obj.setVy(10);

        obj.clockTick();

        assertEquals(510, obj.getY());
    }

    @Test
    public void testClockTickYBoundsLow() {
        StubObjEX obj = new StubObjEX(500, 5, 0);
        obj.setVy(-100);

        obj.clockTick();

        assertEquals(0, obj.getY());
        assertEquals(100, obj.getVy());
    }

    @Test
    public void testClockTickYBoundsHigh() {
        StubObjEX obj = new StubObjEX(500, 990, 0);
        obj.setVy(100);

        obj.clockTick();

        assertEquals(Translate.getMapH(), obj.getY());
        assertEquals(-100, obj.getVy());
    }

    // --- clockTick: z方向（重力） ---

    @Test
    public void testClockTickZGravity() {
        StubObjEX obj = new StubObjEX(500, 500, 0);
        obj.setZ(100);
        obj.setVz(0);

        obj.clockTick();

        // z != 0 → mz = vz + bz = 0, mz += 1 → 1, vz += 1 → 1, z -= mz → 99
        assertTrue(obj.getZ() < 100);
    }

    @Test
    public void testClockTickZLandsAtMostDepth() {
        StubObjEX obj = new StubObjEX(500, 500, 0);
        obj.setZ(1);
        obj.setVz(5);
        obj.setnMostDepth(0);
        obj.setbFallingUnderGround(false);

        obj.clockTick();

        // z が nMostDepth以下 → z = nMostDepth, vx/vy/vz = 0
        assertEquals(0, obj.getZ());
        assertEquals(0, obj.getVx());
        assertEquals(0, obj.getVy());
        assertEquals(0, obj.getVz());
    }

    @Test
    public void testClockTickZFallingUnderGround() {
        StubObjEX obj = new StubObjEX(500, 500, 0);
        obj.setZ(1);
        obj.setVz(5);
        obj.setnMostDepth(0);
        obj.setbFallingUnderGround(true);
        obj.setObjType(Type.SHIT); // not PLATFORM

        obj.clockTick();

        // bFallingUnderGround=true → (!bFallingUnderGround || objType==PLATFORM) は false
        // z <= nMostDepth 条件に到達するが、内側分岐の条件不成立
    }

    @Test
    public void testClockTickZPlatformType() {
        StubObjEX obj = new StubObjEX(500, 500, 0);
        obj.setZ(1);
        obj.setVz(5);
        obj.setnMostDepth(0);
        obj.setbFallingUnderGround(true);
        obj.setObjType(Type.PLATFORM);

        obj.clockTick();

        // objType == PLATFORM → z = nMostDepth
        assertEquals(0, obj.getZ());
    }

    // --- clockTick: mx==0 && my==0 → 移動なし ---

    @Test
    public void testClockTickNoMovementWhenVelocityZero() {
        StubObjEX obj = new StubObjEX(500, 500, 0);
        obj.setVx(0);
        obj.setVy(0);

        obj.clockTick();

        assertEquals(500, obj.getX());
        assertEquals(500, obj.getY());
    }

    // --- clockTick: barrier ---

    @Test
    public void testClockTickXBarrierHit() {
        StubObjEX obj = new StubObjEX(500, 500, 0);
        obj.setVx(10);
        obj.setW(40); // getW()>>2 = 10
        obj.setH(40); // getH()>>2 = 10

        // 移動後 x=510, y=500, thx=10, thy=10
        // sx=510-5=505, sy=500-5=495, ex=510+5=515, ey=500+5=505
        int[][] wallMap = SimYukkuri.world.getCurrentMap().getWallMap();
        for (int xx = 505; xx < 516; xx++) {
            for (int yy = 495; yy < 506; yy++) {
                if (xx >= 0 && xx < wallMap.length && yy >= 0 && yy < wallMap[0].length) {
                    wallMap[xx][yy] = 8; // MAP_ITEM = 8
                }
            }
        }

        obj.clockTick();

        // バリアに衝突 → x -= vx, vx = 0
        assertEquals(500, obj.getX());
        assertEquals(0, obj.getVx());
    }

    @Test
    public void testClockTickYBarrierHit() {
        StubObjEX obj = new StubObjEX(500, 500, 0);
        obj.setVy(10);
        obj.setW(40);
        obj.setH(40);

        // 移動後 x=500, y=510, thx=10, thy=10
        int[][] wallMap = SimYukkuri.world.getCurrentMap().getWallMap();
        for (int xx = 495; xx < 506; xx++) {
            for (int yy = 505; yy < 516; yy++) {
                if (xx >= 0 && xx < wallMap.length && yy >= 0 && yy < wallMap[0].length) {
                    wallMap[xx][yy] = 8;
                }
            }
        }

        obj.clockTick();

        assertEquals(500, obj.getY());
        assertEquals(0, obj.getVy());
    }

    // --- clockTick: 通常動作 ---

    @Test
    public void testClockTickNormalReturnsDoNothing() {
        StubObjEX obj = new StubObjEX(500, 500, 0);

        assertEquals(Event.DONOTHING, obj.clockTick());
    }

    // --- checkInterval ---

    @Test
    public void testCheckIntervalTrue() {
        StubObjEX obj = new StubObjEX(0, 0, 0);
        obj.setInterval(5);

        assertTrue(obj.checkInterval(10)); // 10 % 5 == 0
        assertTrue(obj.checkInterval(0));  // 0 % 5 == 0
    }

    @Test
    public void testCheckIntervalFalse() {
        StubObjEX obj = new StubObjEX(0, 0, 0);
        obj.setInterval(5);

        assertFalse(obj.checkInterval(3)); // 3 % 5 == 3
    }

    // --- invertEnabled ---

    @Test
    public void testInvertEnabled() {
        StubObjEX obj = new StubObjEX(0, 0, 0);
        assertTrue(obj.getEnabled());

        obj.invertEnabled();
        assertFalse(obj.getEnabled());

        obj.invertEnabled();
        assertTrue(obj.getEnabled());
    }

    // --- hasSetupMenu ---

    @Test
    public void testHasSetupMenuReturnsFalse() {
        StubObjEX obj = new StubObjEX(0, 0, 0);
        assertFalse(obj.hasSetupMenu());
    }

    // --- enableHitCheck ---

    @Test
    public void testEnableHitCheckReturnsTrue() {
        StubObjEX obj = new StubObjEX(0, 0, 0);
        assertTrue(obj.enableHitCheck());
    }

    // --- getHitCheckObjType ---

    @Test
    public void testGetHitCheckObjTypeReturnsZero() {
        StubObjEX obj = new StubObjEX(0, 0, 0);
        assertEquals(0, obj.getHitCheckObjType());
    }

    // --- objHitProcess ---

    @Test
    public void testObjHitProcessReturnsZero() {
        StubObjEX obj = new StubObjEX(0, 0, 0);
        assertEquals(0, obj.objHitProcess(new Obj()));
    }

    // --- checkHitObj(Obj, boolean) ---

    @Test
    public void testCheckHitObjNullReturnsFalse() {
        StubObjEX obj = new StubObjEX(500, 500, 0);
        assertFalse(obj.checkHitObj(null, false));
    }

    @Test
    public void testCheckHitObjZNonZeroAndBCheckZFalse() {
        StubObjEX obj = new StubObjEX(500, 500, 0);
        obj.setColW(50);
        obj.setColH(50);

        Obj target = new Obj();
        target.setZ(10); // z != 0
        target.setX(500);
        target.setY(500);

        Point4y translated = new Point4y();
        Translate.translate(target.getX(), target.getY(), translated);
        obj.setScreenPivot(translated.getX(), translated.getY());

        assertTrue(obj.checkHitObj(target, false));
    }

    @Test
    public void testCheckHitObjInsideReturnsTrue() {
        StubObjEX obj = new StubObjEX(500, 500, 0);
        obj.setColW(50);
        obj.setColH(50);

        Obj target = new Obj();
        target.setZ(0);
        target.setX(500);
        target.setY(500);

        Point4y translated = new Point4y();
        Translate.translate(target.getX(), target.getY(), translated);
        obj.setScreenPivot(translated.getX(), translated.getY());

        assertTrue(obj.checkHitObj(target, true));
    }

    @Test
    public void testCheckHitObjOutsideReturnsFalse() {
        StubObjEX obj = new StubObjEX(500, 500, 0);
        obj.setColW(10);
        obj.setColH(10);

        Obj target = new Obj();
        target.setZ(0);
        target.setX(600);
        target.setY(600);

        Point4y translated = new Point4y();
        Translate.translate(500, 500, translated);
        obj.setScreenPivot(translated.getX(), translated.getY());

        assertFalse(obj.checkHitObj(target, true));
    }

    // --- checkHitObj(Rectangle, Obj) ---

    @Test
    public void testCheckHitObjRectNullReturnsFalse() {
        StubObjEX obj = new StubObjEX(500, 500, 0);
        assertFalse(obj.checkHitObj(new Rectangle(), null));
    }

    @Test
    public void testCheckHitObjRectObjZNonZeroReturnsTrue() {
        StubObjEX obj = new StubObjEX(500, 500, 0);

        Obj target = new Obj();
        target.setZ(10); // z != 0 → 空中 → 移動させない → return true

        assertTrue(obj.checkHitObj(new Rectangle(), target));
    }

    @Test
    public void testCheckHitObjRectInsideProcessesAndReturnsFalse() {
        StubObjEX obj = new StubObjEX(0, 0, 0);
        obj.setColW(10);
        obj.setColH(10);

        Obj target = new Obj();
        target.setZ(0);
        target.setX(50);
        target.setY(60);

        Point4y translated = new Point4y();
        Translate.translate(target.getX(), target.getY(), translated);
        obj.setScreenPivot(translated.getX(), translated.getY());
        Rectangle rect = new Rectangle();
        obj.getCollisionRect(rect);

        assertFalse(obj.checkHitObj(rect, target));
        assertTrue(obj.objHitProcessed);
    }

    @Test
    public void testGetCollisionRectUsesPivotAndSize() {
        StubObjEX obj = new StubObjEX(0, 0, 0);
        obj.setColW(12);
        obj.setColH(8);
        obj.setScreenPivot(100, 200);

        Rectangle r = new Rectangle();
        obj.getCollisionRect(r);

        assertEquals(100 - 12, r.x);
        assertEquals(200 - 8, r.y);
        assertEquals(24, r.width);
        assertEquals(16, r.height);
    }

    @Test
    public void testTmpPosGetterSetter() {
        StubObjEX obj = new StubObjEX(0, 0, 0);
        Point4y p = new Point4y();
        p.setX(7);
        p.setY(9);
        obj.setTmpPos(p);
        assertEquals(7, obj.getTmpPos().getX());
        assertEquals(9, obj.getTmpPos().getY());
    }

    // --- getters/setters ---

    @Test
    public void testGettersAndSetters() {
        StubObjEX obj = new StubObjEX(0, 0, 0);

        obj.setOption(42);
        assertEquals(42, obj.getOption());

        obj.setLinkParent(7);
        assertEquals(7, obj.getLinkParent());

        obj.setLooks(3);
        assertEquals(3, obj.getLooks());

        obj.setInterval(10);
        assertEquals(10, obj.getInterval());

        obj.setEnabled(false);
        assertFalse(obj.getEnabled());
        obj.setEnabled(true);
        assertTrue(obj.getEnabled());

        obj.setColW(25);
        assertEquals(25, obj.getColW());

        obj.setColH(30);
        assertEquals(30, obj.getColH());
    }

    // --- ItemRank enum ---

    @Test
    public void testItemRankValues() {
        assertEquals(3, ObjEX.ItemRank.values().length);
        assertEquals(ObjEX.ItemRank.HOUSE, ObjEX.ItemRank.valueOf("HOUSE"));
        assertEquals(ObjEX.ItemRank.NORA, ObjEX.ItemRank.valueOf("NORA"));
        assertEquals(ObjEX.ItemRank.YASEI, ObjEX.ItemRank.valueOf("YASEI"));
    }

    // --- 定数 ---

    @Test
    public void testConstants() {
        assertEquals(1, ObjEX.YUKKURI);
        assertEquals(2, ObjEX.SHIT);
        assertEquals(4, ObjEX.FOOD);
        assertEquals(8, ObjEX.TOILET);
        assertEquals(16, ObjEX.TOY);
        assertEquals(32, ObjEX.PLATFORM);
        assertEquals(64, ObjEX.FIX_OBJECT);
        assertEquals(128, ObjEX.OBJECT);
        assertEquals(256, ObjEX.VOMIT);
        assertEquals(512, ObjEX.STALK);
    }
}
