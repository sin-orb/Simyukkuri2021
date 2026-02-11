package src.base;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import src.SimYukkuri;
import src.draw.Point4y;
import src.draw.Rectangle4y;
import src.draw.Translate;
import src.draw.World;
import src.enums.Event;
import src.enums.Type;
import src.enums.Where;
import src.system.ItemMenu.GetMenuTarget;
import src.system.ItemMenu.UseMenuTarget;
import java.awt.image.BufferedImage;

public class ObjTest {

    private static class DummyEffect extends Effect {
        private static final long serialVersionUID = 1L;

        DummyEffect() {
            super();
        }

        @Override
        public BufferedImage getImage() {
            return null;
        }
    }

    @BeforeEach
    public void setUp() {
        SimYukkuri.world = new World();
        Translate.setMapSize(999, 999, 499); // mapW=1000, mapH=1000, mapZ=500
        int[][] wallMap = new int[1001][1001];
        SimYukkuri.world.getCurrentMap().setWallMap(wallMap);
    }

    // --- age ---

    @Test
    public void testAge() {
        Obj obj = new Obj();
        obj.setAge(10);
        assertEquals(10, obj.getAge());

        obj.addAge(5);
        assertEquals(15, obj.getAge());

        obj.addAge(-20); // clamped to 0
        assertEquals(0, obj.getAge());
    }

    // --- coordinates ---

    @Test
    public void testCoordinates() {
        Obj obj = new Obj();
        obj.setX(100);
        obj.setY(200);
        obj.setZ(5);

        assertEquals(100, obj.getX());
        assertEquals(200, obj.getY());
        assertEquals(5, obj.getZ());
    }

    // --- setCalcX ---

    @Test
    public void testSetCalcXNormal() {
        Obj obj = new Obj();
        obj.setCalcX(500);
        assertEquals(500, obj.getX());
    }

    @Test
    public void testSetCalcXMin() {
        Obj obj = new Obj();
        obj.setCalcX(-10);
        assertEquals(0, obj.getX());
    }

    @Test
    public void testSetCalcXMax() {
        Obj obj = new Obj();
        obj.setCalcX(Translate.getMapW() + 100);
        assertEquals(Translate.getMapW(), obj.getX());
    }

    @Test
    public void testSetCalcXNoWall() {
        Obj obj = new Obj();
        obj.setEnableWall(false);
        obj.setCalcX(-100);
        assertEquals(-100, obj.getX()); // wall無効なのでそのまま
    }

    // --- setCalcY ---

    @Test
    public void testSetCalcYNormal() {
        Obj obj = new Obj();
        obj.setCalcY(500);
        assertEquals(500, obj.getY());
    }

    @Test
    public void testSetCalcYMin() {
        Obj obj = new Obj();
        obj.setCalcY(-10);
        assertEquals(0, obj.getY());
    }

    @Test
    public void testSetCalcYMax() {
        Obj obj = new Obj();
        obj.setCalcY(Translate.getMapH() + 100);
        assertEquals(Translate.getMapH(), obj.getY());
    }

    @Test
    public void testSetCalcYNoWall() {
        Obj obj = new Obj();
        obj.setEnableWall(false);
        obj.setCalcY(-100);
        assertEquals(-100, obj.getY());
    }

    // --- setCalcZ ---

    @Test
    public void testSetCalcZNormal() {
        Obj obj = new Obj();
        obj.setCalcZ(100);
        assertEquals(100, obj.getZ());
    }

    @Test
    public void testSetCalcZAboveMax() {
        Obj obj = new Obj();
        obj.setCalcZ(Translate.getMapZ() + 100);
        assertEquals(Translate.getMapZ(), obj.getZ());
    }

    @Test
    public void testSetCalcZBelowMostDepthNotFalling() {
        Obj obj = new Obj();
        obj.setnMostDepth(0);
        obj.setZ(-10); // z < nMostDepth を事前に設定
        obj.setbFallingUnderGround(false);

        obj.setCalcZ(-5);
        // z < nMostDepth → !bFallingUnderGround → z = nMostDepth = 0
        // しかし次の if (Z > mapZ) else z = Z → z = -5
        assertEquals(-5, obj.getZ());
    }

    @Test
    public void testSetCalcZBelowMostDepthFalling() {
        Obj obj = new Obj();
        obj.setnMostDepth(0);
        obj.setZ(-10);
        obj.setbFallingUnderGround(true);

        obj.setCalcZ(-5);
        // z < nMostDepth → bFallingUnderGround → z = Z
        assertEquals(-5, obj.getZ());
    }

    @Test
    public void testSetCalcZNoWall() {
        Obj obj = new Obj();
        obj.setEnableWall(false);
        obj.setCalcZ(9999);
        assertEquals(9999, obj.getZ());
    }

    // --- vectors ---

    @Test
    public void testVectors() {
        Obj obj = new Obj();
        obj.kick(10, 20, 30);
        assertEquals(10, obj.getVx());
        assertEquals(20, obj.getVy());
        assertEquals(30, obj.getVz());

        int[] vxyz = obj.getVxyz();
        assertArrayEquals(new int[] { 10, 20, 30 }, vxyz);
    }

    // --- bxyz ---

    @Test
    public void testSetBxyz() {
        Obj obj = new Obj();
        obj.setBxyz(1, 2, 3);
        assertEquals(1, obj.getBx());
        assertEquals(2, obj.getBy());
        assertEquals(3, obj.getBz());
    }

    @Test
    public void testAddBxyz() {
        Obj obj = new Obj();
        obj.setBxyz(1, 2, 3);
        obj.addBxyz(10, 20, 30);
        assertEquals(11, obj.getBx());
        assertEquals(22, obj.getBy());
        assertEquals(33, obj.getBz());
    }

    @Test
    public void testResetBPos() {
        Obj obj = new Obj();
        obj.setBxyz(1, 2, 3);
        obj.resetBPos();
        assertEquals(0, obj.getBx());
        assertEquals(0, obj.getBy());
        assertEquals(0, obj.getBz());
    }

    // --- offset ---

    @Test
    public void testOfsXY() {
        Obj obj = new Obj();
        obj.setX(100);
        obj.setY(200);
        obj.setOfsXY(5, 10);

        assertEquals(105, obj.getDrawOfsX());
        assertEquals(210, obj.getDrawOfsY());
    }

    // --- setBoundary ---

    @Test
    public void testSetBoundaryDirect() {
        Obj obj = new Obj();
        obj.setPivotX(10);
        obj.setPivotY(20);
        obj.setW(30);
        obj.setH(40);

        assertEquals(10, obj.getPivotX());
        assertEquals(20, obj.getPivotY());
        assertEquals(30, obj.getW());
        assertEquals(40, obj.getH());
    }

    @Test
    public void testGetBoundaryShape() {
        Obj obj = new Obj();
        obj.setPivotX(10);
        obj.setPivotY(20);
        obj.setW(30);
        obj.setH(40);

        Rectangle4y r = new Rectangle4y();
        obj.getBoundaryShape(r);

        assertEquals(10, r.getX());
        assertEquals(20, r.getY());
        assertEquals(30, r.getWidth());
        assertEquals(40, r.getHeight());
    }

    // --- screen pivot/rect ---

    @Test
    public void testScreenPivotDirect() {
        Obj obj = new Obj();
        obj.setScreenPivot(100, 200);
        assertEquals(100, obj.getScreenPivot().getX());
        assertEquals(200, obj.getScreenPivot().getY());
    }

    @Test
    public void testScreenPivotPoint() {
        Obj obj = new Obj();
        Point4y p = new Point4y();
        p.setX(50);
        p.setY(60);
        obj.setScreenPivot(p);
        assertEquals(50, obj.getScreenPivot().getX());
        assertEquals(60, obj.getScreenPivot().getY());
    }

    @Test
    public void testScreenRectDirect() {
        Obj obj = new Obj();
        obj.setScreenRect(10, 20, 30, 40);
        Rectangle4y r = obj.getScreenRect();
        assertEquals(10, r.getX());
        assertEquals(20, r.getY());
        assertEquals(30, r.getWidth());
        assertEquals(40, r.getHeight());
    }

    @Test
    public void testScreenRectWithRect() {
        Obj obj = new Obj();
        Rectangle4y r = new Rectangle4y();
        r.setX(1);
        r.setY(2);
        r.setWidth(3);
        r.setHeight(4);
        obj.setScreenRect(r);

        Rectangle4y result = obj.getScreenRect();
        assertEquals(1, result.getX());
        assertEquals(2, result.getY());
        assertEquals(3, result.getWidth());
        assertEquals(4, result.getHeight());
    }

    // --- grab/release ---

    @Test
    public void testGrabRelease() {
        Obj obj = new Obj();
        assertFalse(obj.isGrabbed());
        assertTrue(obj.isCanGrab());

        obj.grab();
        assertTrue(obj.isGrabbed());

        obj.release();
        assertFalse(obj.isGrabbed());
    }

    // --- remove ---

    @Test
    public void testRemove() {
        Obj obj = new Obj();
        assertFalse(obj.isRemoved());

        obj.remove();
        assertTrue(obj.isRemoved());
        assertEquals(-1, obj.getBindObj());
    }

    // --- type/value/cost ---

    @Test
    public void testObjType() {
        Obj obj = new Obj();
        obj.setObjType(Type.YUKKURI);
        assertEquals(Type.YUKKURI, obj.getObjType());
    }

    @Test
    public void testValueCost() {
        Obj obj = new Obj();
        obj.setValue(100);
        obj.setCost(50);
        assertEquals(100, obj.getValue());
        assertEquals(50, obj.getCost());
    }

    // --- where ---

    @Test
    public void testWhere() {
        Obj obj = new Obj();
        assertEquals(Where.ON_FLOOR, obj.getWhere());
        assertEquals(Where.ON_FLOOR, obj.geteWhere());

        obj.setWhere(Where.ON_YUKKURI);
        assertEquals(Where.ON_YUKKURI, obj.getWhere());

        obj.seteWhere(Where.IN_YUKKURI);
        assertEquals(Where.IN_YUKKURI, obj.geteWhere());
    }

    // --- fallingUnderGround / inPool ---

    @Test
    public void testFallingUnderGround() {
        Obj obj = new Obj();
        assertFalse(obj.getFallingUnderGround());
        obj.setFallingUnderGround(true);
        assertTrue(obj.getFallingUnderGround());
    }

    @Test
    public void testInPool() {
        Obj obj = new Obj();
        assertFalse(obj.getInPool());
        obj.setInPool(true);
        assertTrue(obj.getInPool());
    }

    // --- mostDepth ---

    @Test
    public void testMostDepth() {
        Obj obj = new Obj();
        obj.setMostDepth(-10);
        assertEquals(-10, obj.getMostDepth());
    }

    // --- bindObj ---

    @Test
    public void testBindObj() {
        Obj obj = new Obj();
        obj.setBindObj(42);
        assertEquals(42, obj.getBindObj());
    }

    // --- compareTo ---

    @Test
    public void testCompareTo() {
        Obj obj = new Obj();
        assertEquals(0, obj.compareTo(new Obj()));
    }

    // --- hasPopup ---

    @Test
    public void testHasGetPopup() {
        Obj obj = new Obj();
        assertEquals(GetMenuTarget.NONE, obj.hasGetPopup());
    }

    @Test
    public void testHasUsePopup() {
        Obj obj = new Obj();
        assertEquals(UseMenuTarget.NONE, obj.hasUsePopup());
    }

    // --- forceXY ---

    @Test
    public void testForceXY() {
        Obj obj = new Obj();
        obj.setForceX(-100);
        assertEquals(-100, obj.getX());

        obj.setForceY(-200);
        assertEquals(-200, obj.getY());
    }

    // --- kick no args ---

    @Test
    public void testKickNoArgs() {
        Obj obj = new Obj();
        obj.kick(); // does nothing, should not throw
    }

    // --- calcPos ---

    @Test
    public void testCalcPosClampXLow() {
        Obj obj = new Obj();
        obj.setX(-10);
        obj.setY(500);
        obj.calcPos();
        assertEquals(0, obj.getX());
    }

    @Test
    public void testCalcPosClampYLow() {
        Obj obj = new Obj();
        obj.setX(500);
        obj.setY(-10);
        obj.calcPos();
        assertEquals(0, obj.getY());
    }

    @Test
    public void testCalcPosClampXHigh() {
        Obj obj = new Obj();
        obj.setX(Translate.getMapW() + 100);
        obj.setY(500);
        obj.calcPos();
        assertEquals(Translate.getMapW(), obj.getX());
    }

    @Test
    public void testCalcPosClampYHigh() {
        Obj obj = new Obj();
        obj.setX(500);
        obj.setY(Translate.getMapH() + 100);
        obj.calcPos();
        assertEquals(Translate.getMapH(), obj.getY());
    }

    @Test
    public void testCalcPosNoClamp() {
        Obj obj = new Obj();
        obj.setX(500);
        obj.setY(500);
        obj.calcPos();
        assertEquals(500, obj.getX());
        assertEquals(500, obj.getY());
    }

    // --- clockTick ---

    @Test
    public void testClockTickRemovedReturnsRemoved() {
        Obj obj = new Obj();
        obj.setRemoved(true);

        assertEquals(Event.REMOVED, obj.clockTick());
    }

    @Test
    public void testClockTickGrabbedNoMovement() {
        Obj obj = new Obj();
        obj.setX(500);
        obj.setY(500);
        obj.setGrabbed(true);
        obj.setVx(10);
        obj.setVy(10);

        Event result = obj.clockTick();

        assertEquals(Event.DONOTHING, result);
        assertEquals(0, obj.getBx());
        assertEquals(0, obj.getBy());
    }

    @Test
    public void testClockTickMoveX() {
        Obj obj = new Obj();
        obj.setX(500);
        obj.setY(500);
        obj.setVx(10);

        obj.clockTick();

        assertEquals(510, obj.getX());
    }

    @Test
    public void testClockTickXBoundsLow() {
        Obj obj = new Obj();
        obj.setX(5);
        obj.setY(500);
        obj.setVx(-100);

        obj.clockTick();

        // x=5+(-100)=-95 → x<0 → x=0, vx=100
        // 末尾の if(x<0)x=5 は x=0 なので通らない
        assertEquals(0, obj.getX());
        assertEquals(100, obj.getVx());
    }

    @Test
    public void testClockTickXBoundsHigh() {
        Obj obj = new Obj();
        obj.setX(990);
        obj.setY(500);
        obj.setVx(100);

        obj.clockTick();

        assertEquals(Translate.getMapW(), obj.getX());
        assertEquals(-100, obj.getVx());
    }

    @Test
    public void testClockTickMoveY() {
        Obj obj = new Obj();
        obj.setX(500);
        obj.setY(500);
        obj.setVy(10);

        obj.clockTick();

        assertEquals(510, obj.getY());
    }

    @Test
    public void testClockTickYBoundsLow() {
        Obj obj = new Obj();
        obj.setX(500);
        obj.setY(5);
        obj.setVy(-100);

        obj.clockTick();

        // y=5+(-100)=-95 → y<0 → y=0, vy=100
        assertEquals(0, obj.getY());
        assertEquals(100, obj.getVy());
    }

    @Test
    public void testClockTickYBoundsHigh() {
        Obj obj = new Obj();
        obj.setX(500);
        obj.setY(990);
        obj.setVy(100);

        obj.clockTick();

        assertEquals(Translate.getMapH(), obj.getY());
        assertEquals(-100, obj.getVy());
    }

    @Test
    public void testClockTickZGravity() {
        Obj obj = new Obj();
        obj.setX(500);
        obj.setY(500);
        obj.setZ(100);
        obj.setVz(0);

        obj.clockTick();

        // z != 0 → mz=0+1=1, vz=0+1=1, z=100-1=99
        assertEquals(99, obj.getZ());
    }

    @Test
    public void testClockTickZLandsAtMostDepth() {
        Obj obj = new Obj();
        obj.setX(500);
        obj.setY(500);
        obj.setZ(1);
        obj.setVz(5);
        obj.setnMostDepth(0);
        obj.setbFallingUnderGround(false);

        obj.clockTick();

        assertEquals(0, obj.getZ());
        assertEquals(0, obj.getVx());
        assertEquals(0, obj.getVy());
        assertEquals(0, obj.getVz());
    }

    @Test
    public void testClockTickZFallingUnderGround() {
        Obj obj = new Obj();
        obj.setX(500);
        obj.setY(500);
        obj.setZ(1);
        obj.setVz(5);
        obj.setnMostDepth(0);
        obj.setbFallingUnderGround(true);

        obj.clockTick();

        // bFallingUnderGround=true → z != nMostDepth
        // vx, vy は 0 にセットされる（z <= nMostDepth の外側）
        assertEquals(0, obj.getVx());
        assertEquals(0, obj.getVy());
    }

    @Test
    public void testClockTickXBarrierHit() {
        Obj obj = new Obj();
        obj.setX(500);
        obj.setY(500);
        obj.setVx(10);

        // wallMapにバリアを設置 (MAP_ITEM=8, 固定16x16チェック)
        int[][] wallMap = SimYukkuri.world.getCurrentMap().getWallMap();
        for (int xx = 502; xx < 520; xx++) {
            for (int yy = 492; yy < 510; yy++) {
                if (xx < wallMap.length && yy < wallMap[0].length) {
                    wallMap[xx][yy] = 8;
                }
            }
        }

        obj.clockTick();

        assertEquals(500, obj.getX());
        assertEquals(0, obj.getVx());
    }

    @Test
    public void testClockTickYBarrierHit() {
        Obj obj = new Obj();
        obj.setX(500);
        obj.setY(500);
        obj.setVy(10);

        int[][] wallMap = SimYukkuri.world.getCurrentMap().getWallMap();
        for (int xx = 492; xx < 510; xx++) {
            for (int yy = 502; yy < 520; yy++) {
                if (xx < wallMap.length && yy < wallMap[0].length) {
                    wallMap[xx][yy] = 8;
                }
            }
        }

        obj.clockTick();

        assertEquals(500, obj.getY());
        assertEquals(0, obj.getVy());
    }

    @Test
    public void testClockTickNoMovement() {
        Obj obj = new Obj();
        obj.setX(500);
        obj.setY(500);

        Event result = obj.clockTick();

        assertEquals(Event.DONOTHING, result);
        assertEquals(500, obj.getX());
        assertEquals(500, obj.getY());
    }

    @Test
    public void testClockTickNegativeXYClamp() {
        Obj obj = new Obj();
        obj.setX(-10);
        obj.setY(-20);

        obj.clockTick();

        // x < 0 → x = 5, y < 0 → y = 5
        assertEquals(5, obj.getX());
        assertEquals(5, obj.getY());
    }

    // --- imgW/H getters/setters ---

    @Test
    public void testImgWH() {
        Obj obj = new Obj();
        obj.setImgW(100);
        obj.setImgH(200);
        assertEquals(100, obj.getImgW());
        assertEquals(200, obj.getImgH());
    }

    // --- pivXY getters/setters ---

    @Test
    public void testPivXY() {
        Obj obj = new Obj();
        obj.setPivX(10);
        obj.setPivY(20);
        assertEquals(10, obj.getPivX());
        assertEquals(20, obj.getPivY());
    }

    // --- ofsXY getters/setters ---

    @Test
    public void testOfsXYGetSet() {
        Obj obj = new Obj();
        obj.setOfsX(5);
        obj.setOfsY(10);
        assertEquals(5, obj.getOfsX());
        assertEquals(10, obj.getOfsY());
    }

    // --- objId ---

    @Test
    public void testObjId() {
        Obj obj = new Obj();
        obj.setObjId(42);
        assertEquals(42, obj.getObjId());
    }

    // --- takeMappedObj: 存在しないキー ---

    @Test
    public void testTakeMappedObjReturnsNull() {
        Obj obj = new Obj();
        assertNull(obj.takeMappedObj(99999));
    }

    @Test
    public void testTakeMappedObjFromFrontEffect() {
        Obj obj = new Obj();
        DummyEffect effect = new DummyEffect();
        effect.objId = 12345;
        SimYukkuri.world.getCurrentMap().getFrontEffect().put(effect.objId, effect);

        assertEquals(effect, obj.takeMappedObj(12345));
    }

    @Test
    public void testTakeMappedObjFromBodyMap() {
        Obj obj = new Obj();
        StubBody body = new StubBody();
        body.objId = 2222;
        SimYukkuri.world.getCurrentMap().getBody().put(body.getUniqueID(), body);

        assertEquals(body, obj.takeMappedObj(2222));
    }
}
