package org.simyukkuri.entity.core;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.simyukkuri.SimYukkuri;
import org.simyukkuri.draw.Point4y;
import org.simyukkuri.draw.Rectangle4y;
import org.simyukkuri.draw.Translate;
import org.simyukkuri.engine.World;
import org.simyukkuri.entity.core.effect.Effect;
import org.simyukkuri.entity.core.living.yukkuri.StubBody;
import org.simyukkuri.enums.TickResult;
import org.simyukkuri.enums.Type;
import org.simyukkuri.enums.Where;
import org.simyukkuri.system.ItemMenu.GetMenuTarget;
import org.simyukkuri.system.ItemMenu.UseMenuTarget;
import org.simyukkuri.util.WorldTestHelper;

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
        WorldTestHelper.initializeTranslate(999, 999, 499, 800, 600, 100, 100, new float[] { 1.0f });
        int[][] wallMap = new int[1001][1001];
        SimYukkuri.world.getCurrentWorldState().setWallGrid(wallMap);
    }

    // --- age ---

    @Test
    public void testAge() {
        Entity obj = new Entity();
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
        Entity obj = new Entity();
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
        Entity obj = new Entity();
        obj.setCalcX(500);
        assertEquals(500, obj.getX());
    }

    @Test
    public void testSetCalcXMin() {
        Entity obj = new Entity();
        obj.setCalcX(-10);
        assertEquals(0, obj.getX());
    }

    @Test
    public void testSetCalcXMax() {
        Entity obj = new Entity();
        obj.setCalcX(Translate.getWorldWidth() + 100);
        assertEquals(Translate.getWorldWidth(), obj.getX());
    }

    @Test
    public void testSetCalcXNoWall() {
        Entity obj = new Entity();
        obj.setEnableWall(false);
        obj.setCalcX(-100);
        assertEquals(-100, obj.getX()); // wall無効なのでそのまま
    }

    // --- setCalcY ---

    @Test
    public void testSetCalcYNormal() {
        Entity obj = new Entity();
        obj.setCalcY(500);
        assertEquals(500, obj.getY());
    }

    @Test
    public void testSetCalcYMin() {
        Entity obj = new Entity();
        obj.setCalcY(-10);
        assertEquals(0, obj.getY());
    }

    @Test
    public void testSetCalcYMax() {
        Entity obj = new Entity();
        obj.setCalcY(Translate.getWorldHeight() + 100);
        assertEquals(Translate.getWorldHeight(), obj.getY());
    }

    @Test
    public void testSetCalcYNoWall() {
        Entity obj = new Entity();
        obj.setEnableWall(false);
        obj.setCalcY(-100);
        assertEquals(-100, obj.getY());
    }

    // --- setCalcZ ---

    @Test
    public void testSetCalcZNormal() {
        Entity obj = new Entity();
        obj.setCalcZ(100);
        assertEquals(100, obj.getZ());
    }

    @Test
    public void testSetCalcZAboveMax() {
        Entity obj = new Entity();
        obj.setCalcZ(Translate.getWorldDepth() + 100);
        assertEquals(Translate.getWorldDepth(), obj.getZ());
    }

    @Test
    public void testSetCalcZBelowMostDepthNotFalling() {
        Entity obj = new Entity();
        obj.setMostDepth(0);
        obj.setZ(-10); // z < nMostDepth を事前に設定
        obj.setFallingUnderGround(false);

        obj.setCalcZ(-5);
        // z < nMostDepth → !fallingUnderGround → z = nMostDepth = 0
        // しかし次の if (Z > mapZ) else z = Z → z = -5
        assertEquals(-5, obj.getZ());
    }

    @Test
    public void testSetCalcZBelowMostDepthFalling() {
        Entity obj = new Entity();
        obj.setMostDepth(0);
        obj.setZ(-10);
        obj.setFallingUnderGround(true);

        obj.setCalcZ(-5);
        // z < nMostDepth → fallingUnderGround → z = Z
        assertEquals(-5, obj.getZ());
    }

    @Test
    public void testSetCalcZNoWall() {
        Entity obj = new Entity();
        obj.setEnableWall(false);
        obj.setCalcZ(9999);
        assertEquals(9999, obj.getZ());
    }

    // --- vectors ---

    @Test
    public void testVectors() {
        Entity obj = new Entity();
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
        Entity obj = new Entity();
        obj.setMotion(1, 2, 3);
        assertEquals(1, obj.getMotionX());
        assertEquals(2, obj.getMotionY());
        assertEquals(3, obj.getMotionZ());
    }

    @Test
    public void testAddBxyz() {
        Entity obj = new Entity();
        obj.setMotion(1, 2, 3);
        obj.addMotion(10, 20, 30);
        assertEquals(11, obj.getMotionX());
        assertEquals(22, obj.getMotionY());
        assertEquals(33, obj.getMotionZ());
    }

    @Test
    public void testResetBPos() {
        Entity obj = new Entity();
        obj.setMotion(1, 2, 3);
        obj.resetMotion();
        assertEquals(0, obj.getMotionX());
        assertEquals(0, obj.getMotionY());
        assertEquals(0, obj.getMotionZ());
    }

    // --- offset ---

    @Test
    public void testOfsXY() {
        Entity obj = new Entity();
        obj.setX(100);
        obj.setY(200);
        obj.setOfsX(5);
        obj.setOfsY(10);

        assertEquals(105, obj.getDrawOfsX());
        assertEquals(210, obj.getDrawOfsY());
    }

    // --- setBoundary ---

    @Test
    public void testSetBoundaryDirect() {
        Entity obj = new Entity();
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
        Entity obj = new Entity();
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
        Entity obj = new Entity();
        obj.setScreenPivot(100, 200);
        assertEquals(100, obj.getScreenPivot().getX());
        assertEquals(200, obj.getScreenPivot().getY());
    }

    @Test
    public void testScreenPivotPoint() {
        Entity obj = new Entity();
        Point4y p = new Point4y();
        p.setX(50);
        p.setY(60);
        obj.setScreenPivot(p);
        assertEquals(50, obj.getScreenPivot().getX());
        assertEquals(60, obj.getScreenPivot().getY());
    }

    @Test
    public void testScreenRectDirect() {
        Entity obj = new Entity();
        obj.setScreenRect(10, 20, 30, 40);
        Rectangle4y r = obj.getScreenRect();
        assertEquals(10, r.getX());
        assertEquals(20, r.getY());
        assertEquals(30, r.getWidth());
        assertEquals(40, r.getHeight());
    }

    @Test
    public void testScreenRectWithRect() {
        Entity obj = new Entity();
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
        Entity obj = new Entity();
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
        Entity obj = new Entity();
        assertFalse(obj.isRemoved());

        obj.remove();
        assertTrue(obj.isRemoved());
        assertEquals(-1, obj.getBindObj());
    }

    // --- type/value/cost ---

    @Test
    public void testObjType() {
        Entity obj = new Entity();
        obj.setObjType(Type.YUKKURI);
        assertEquals(Type.YUKKURI, obj.getObjType());
    }

    @Test
    public void testValueCost() {
        Entity obj = new Entity();
        obj.setValue(100);
        obj.setCost(50);
        assertEquals(100, obj.getValue());
        assertEquals(50, obj.getCost());
    }

    // --- where ---

    @Test
    public void testWhere() {
        Entity obj = new Entity();
        assertEquals(Where.ON_FLOOR, obj.getWhere());
        assertEquals(Where.ON_FLOOR, obj.getWhere());

        obj.setWhere(Where.ON_YUKKURI);
        assertEquals(Where.ON_YUKKURI, obj.getWhere());

        obj.setWhere(Where.IN_YUKKURI);
        assertEquals(Where.IN_YUKKURI, obj.getWhere());
    }

    // --- fallingUnderGround / inPool ---

    @Test
    public void testFallingUnderGround() {
        Entity obj = new Entity();
        assertFalse(obj.isFallingUnderGround());
        obj.setFallingUnderGround(true);
        assertTrue(obj.isFallingUnderGround());
    }

    @Test
    public void testInPool() {
        Entity obj = new Entity();
        assertFalse(obj.isInPool());
        obj.setInPool(true);
        assertTrue(obj.isInPool());
    }

    // --- mostDepth ---

    @Test
    public void testMostDepth() {
        Entity obj = new Entity();
        obj.setMostDepth(-10);
        assertEquals(-10, obj.getMostDepth());
    }

    // --- bindObj ---

    @Test
    public void testBindObj() {
        Entity obj = new Entity();
        obj.setBindObj(42);
        assertEquals(42, obj.getBindObj());
    }

    // --- compareTo ---

    @Test
    public void testCompareTo() {
        Entity obj = new Entity();
        assertEquals(0, obj.compareTo(new Entity()));
    }

    // --- hasPopup ---

    @Test
    public void testHasGetPopup() {
        Entity obj = new Entity();
        assertEquals(GetMenuTarget.NONE, obj.hasGetPopup());
    }

    @Test
    public void testHasUsePopup() {
        Entity obj = new Entity();
        assertEquals(UseMenuTarget.NONE, obj.hasUsePopup());
    }

    // --- forceXY ---

    @Test
    public void testForceXY() {
        Entity obj = new Entity();
        obj.setForceX(-100);
        assertEquals(-100, obj.getX());

        obj.setForceY(-200);
        assertEquals(-200, obj.getY());
    }

    // --- kick no args ---

    @Test
    public void testKickNoArgs() {
        Entity obj = new Entity();
        obj.kick(5, 10, 15);
        obj.kick(); // no-op: velocity unchanged
        assertEquals(5, obj.getVx());
        assertEquals(10, obj.getVy());
        assertEquals(15, obj.getVz());
    }

    // --- calcPos ---

    @Test
    public void testCalcPosClampXLow() {
        Entity obj = new Entity();
        obj.setX(-10);
        obj.setY(500);
        obj.calcPos();
        assertEquals(0, obj.getX());
    }

    @Test
    public void testCalcPosClampYLow() {
        Entity obj = new Entity();
        obj.setX(500);
        obj.setY(-10);
        obj.calcPos();
        assertEquals(0, obj.getY());
    }

    @Test
    public void testCalcPosClampXHigh() {
        Entity obj = new Entity();
        obj.setX(Translate.getWorldWidth() + 100);
        obj.setY(500);
        obj.calcPos();
        assertEquals(Translate.getWorldWidth(), obj.getX());
    }

    @Test
    public void testCalcPosClampYHigh() {
        Entity obj = new Entity();
        obj.setX(500);
        obj.setY(Translate.getWorldHeight() + 100);
        obj.calcPos();
        assertEquals(Translate.getWorldHeight(), obj.getY());
    }

    @Test
    public void testCalcPosNoClamp() {
        Entity obj = new Entity();
        obj.setX(500);
        obj.setY(500);
        obj.calcPos();
        assertEquals(500, obj.getX());
        assertEquals(500, obj.getY());
    }

    // --- clockTick ---

    @Test
    public void testClockTickRemovedReturnsRemoved() {
        Entity obj = new Entity();
        obj.setRemoved(true);

        assertEquals(TickResult.REMOVED, obj.clockTick());
    }

    @Test
    public void testClockTickGrabbedNoMovement() {
        Entity obj = new Entity();
        obj.setX(500);
        obj.setY(500);
        obj.setGrabbed(true);
        obj.setVx(10);
        obj.setVy(10);

        TickResult result = obj.clockTick();

        assertEquals(TickResult.NONE, result);
        assertEquals(0, obj.getMotionX());
        assertEquals(0, obj.getMotionY());
    }

    @Test
    public void testClockTickMoveX() {
        Entity obj = new Entity();
        obj.setX(500);
        obj.setY(500);
        obj.setVx(10);

        obj.clockTick();

        assertEquals(510, obj.getX());
    }

    @Test
    public void testClockTickXBoundsLow() {
        Entity obj = new Entity();
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
        Entity obj = new Entity();
        obj.setX(990);
        obj.setY(500);
        obj.setVx(100);

        obj.clockTick();

        assertEquals(Translate.getWorldWidth(), obj.getX());
        assertEquals(-100, obj.getVx());
    }

    @Test
    public void testClockTickMoveY() {
        Entity obj = new Entity();
        obj.setX(500);
        obj.setY(500);
        obj.setVy(10);

        obj.clockTick();

        assertEquals(510, obj.getY());
    }

    @Test
    public void testClockTickYBoundsLow() {
        Entity obj = new Entity();
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
        Entity obj = new Entity();
        obj.setX(500);
        obj.setY(990);
        obj.setVy(100);

        obj.clockTick();

        assertEquals(Translate.getWorldHeight(), obj.getY());
        assertEquals(-100, obj.getVy());
    }

    @Test
    public void testClockTickZGravity() {
        Entity obj = new Entity();
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
        Entity obj = new Entity();
        obj.setX(500);
        obj.setY(500);
        obj.setZ(1);
        obj.setVz(5);
        obj.setMostDepth(0);
        obj.setFallingUnderGround(false);

        obj.clockTick();

        assertEquals(0, obj.getZ());
        assertEquals(0, obj.getVx());
        assertEquals(0, obj.getVy());
        assertEquals(0, obj.getVz());
    }

    @Test
    public void testClockTickZFallingUnderGround() {
        Entity obj = new Entity();
        obj.setX(500);
        obj.setY(500);
        obj.setZ(1);
        obj.setVz(5);
        obj.setMostDepth(0);
        obj.setFallingUnderGround(true);

        obj.clockTick();

        // fallingUnderGround=true → z != nMostDepth
        // vx, vy は 0 にセットされる（z <= nMostDepth の外側）
        assertEquals(0, obj.getVx());
        assertEquals(0, obj.getVy());
    }

    @Test
    public void testClockTickXBarrierHit() {
        Entity obj = new Entity();
        obj.setX(500);
        obj.setY(500);
        obj.setVx(10);

        // wallMapにバリアを設置 (ITEM_BLOCK_FLAG=8, 固定16x16チェック)
        int[][] wallMap = SimYukkuri.world.getCurrentWorldState().getWallGrid();
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
        Entity obj = new Entity();
        obj.setX(500);
        obj.setY(500);
        obj.setVy(10);

        int[][] wallMap = SimYukkuri.world.getCurrentWorldState().getWallGrid();
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
        Entity obj = new Entity();
        obj.setX(500);
        obj.setY(500);

        TickResult result = obj.clockTick();

        assertEquals(TickResult.NONE, result);
        assertEquals(500, obj.getX());
        assertEquals(500, obj.getY());
    }

    @Test
    public void testClockTickNegativeXYClamp() {
        Entity obj = new Entity();
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
        Entity obj = new Entity();
        obj.setImgW(100);
        obj.setImgH(200);
        assertEquals(100, obj.getImgW());
        assertEquals(200, obj.getImgH());
    }

    // --- pivXY getters/setters ---

    @Test
    public void testPivXY() {
        Entity obj = new Entity();
        obj.setPivX(10);
        obj.setPivY(20);
        assertEquals(10, obj.getPivX());
        assertEquals(20, obj.getPivY());
    }

    // --- ofsXY getters/setters ---

    @Test
    public void testOfsXYGetSet() {
        Entity obj = new Entity();
        obj.setOfsX(5);
        obj.setOfsY(10);
        assertEquals(5, obj.getOfsX());
        assertEquals(10, obj.getOfsY());
    }

    // --- objId ---

    @Test
    public void testObjId() {
        Entity obj = new Entity();
        obj.setObjId(42);
        assertEquals(42, obj.getObjId());
    }

    // --- takeMappedObj: 存在しないキー ---

    @Test
    public void testTakeMappedObjReturnsNull() {
        Entity obj = new Entity();
        assertNull(obj.takeMappedObj(99999));
    }

    @Test
    public void testTakeMappedObjFromFrontEffect() {
        Entity obj = new Entity();
        DummyEffect effect = new DummyEffect();
        effect.objId = 12345;
        SimYukkuri.world.getCurrentWorldState().getFrontEffects().put(effect.objId, effect);

        assertEquals(effect, obj.takeMappedObj(12345));
    }

    @Test
    public void testTakeMappedObjFromBodyMap() {
        Entity obj = new Entity();
        StubBody body = new StubBody();
        body.objId = 2222;
        SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(body.getUniqueId(), body);

        assertEquals(body, obj.takeMappedObj(2222));
    }

    @Nested
    class RegressionScenarios {

        @Test
        public void testScenario_ClockTickAppliesVelocityAndKnockbackThenResetsBVector() {
            Entity obj = new Entity();
            obj.setX(100);
            obj.setY(100);
            obj.setVx(5);
            obj.setVy(7);
            obj.setMotion(3, 4, 0);

            TickResult result = obj.clockTick();

            assertEquals(TickResult.NONE, result);
            assertEquals(108, obj.getX());
            assertEquals(111, obj.getY());
            assertEquals(0, obj.getMotionX());
            assertEquals(0, obj.getMotionY());
            assertEquals(0, obj.getMotionZ());
        }

        @Test
        public void testScenario_FallingUnderGroundKeepsNegativeZButStillZeroesXYVelocity() {
            Entity obj = new Entity();
            obj.setX(500);
            obj.setY(500);
            obj.setZ(1);
            obj.setVz(5);
            obj.setVx(9);
            obj.setVy(11);
            obj.setMostDepth(0);
            obj.setFallingUnderGround(true);

            TickResult result = obj.clockTick();

            assertEquals(TickResult.NONE, result);
            assertTrue(obj.getZ() < 0);
            assertEquals(0, obj.getVx());
            assertEquals(0, obj.getVy());
        }
    }
}
