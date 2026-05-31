package org.simyukkuri.item;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.simyukkuri.SimYukkuri;
import org.simyukkuri.entity.core.world.item.ItemTestBase;
import org.simyukkuri.field.FieldShape;
import org.simyukkuri.field.impl.Barrier;

public class BarrierTest extends ItemTestBase {

    @Test
    void testConstructorDefault() {
        Barrier item = new Barrier();
        assertNull(item.getColor(),        "デフォルトコンストラクタでは color が null");
        assertEquals(0, item.getAttribute(), "デフォルトコンストラクタでは attribute が 0");
        assertEquals(1, item.getMinimumSize(), "最小サイズは常に 1");
    }

    @Test
    void testGetColorDefaultIsNull() {
        // BARRIER_WALL の色は (128,128,128,255) であること
        Barrier wall = new Barrier(0, 0, 100, 100, FieldShape.BARRIER_WALL);
        assertNotNull(wall.getColor());
        assertEquals(128, wall.getColor().getRed());
        assertEquals(128, wall.getColor().getGreen());
        assertEquals(128, wall.getColor().getBlue());
        assertEquals(255, wall.getColor().getAlpha());
    }

    @Test
    void testGetAttributeDefaultIsZero() {
        // 各 type が対応する attribute 値に設定されること
        Barrier wall    = new Barrier(0, 0, 10, 10, FieldShape.BARRIER_WALL);
        Barrier gapMini = new Barrier(0, 0, 10, 10, FieldShape.BARRIER_GAP_MINI);
        assertEquals(FieldShape.BARRIER_WALL,     wall.getAttribute());
        assertEquals(FieldShape.BARRIER_GAP_MINI, gapMini.getAttribute());
    }

    @Test
    void testGetMinimumSizeReturns1() {
        Barrier item = new Barrier();
        assertEquals(1, item.getMinimumSize());
    }

    @Test
    void testConstructorWithArgsBarrierWallDoesNotThrow() {
        Barrier b = new Barrier(0, 0, 100, 100, FieldShape.BARRIER_WALL);
        assertEquals(FieldShape.BARRIER_WALL, b.getAttribute());
        assertEquals(128, b.getColor().getRed());
        assertEquals(128, b.getColor().getGreen());
        assertEquals(128, b.getColor().getBlue());
        // フィールド座標はコンストラクタ引数がそのまま設定される
        assertEquals(0,   b.getFieldSx());
        assertEquals(0,   b.getFieldSy());
        assertEquals(100, b.getFieldEx());
        assertEquals(100, b.getFieldEy());
    }

    @Test
    void testConstructorWithArgsBarrierGapMiniDoesNotThrow() {
        Barrier b = new Barrier(0, 0, 50, 50, FieldShape.BARRIER_GAP_MINI);
        assertEquals(FieldShape.BARRIER_GAP_MINI, b.getAttribute());
        assertNotNull(b.getColor());
        // BARRIER_GAP_MINI の色は黄色 (255,255,0,255)
        assertEquals(255, b.getColor().getRed());
        assertEquals(255, b.getColor().getGreen());
        assertEquals(0,   b.getColor().getBlue());
        assertEquals(255, b.getColor().getAlpha());
    }

    @Test
    void testDrawPreviewDoesNotThrow() {
        // drawPreview が実際にピクセルへ描画することを確認
        BufferedImage img = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = img.createGraphics();
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, 200, 200);
        g2.setColor(Color.BLACK);
        Barrier.drawPreview(g2, 10, 10, 100, 10);
        g2.dispose();
        // 水平線上 (50, 10) が黒になっていること
        assertEquals(Color.BLACK.getRGB(), img.getRGB(50, 10), "drawPreview は線上のピクセルを描画すること");
    }

    @Test
    void testDrawShapeDoesNotThrow() {
        // drawShape が BARRIER_WALL のグレー色でピクセルへ描画することを確認
        Barrier item = new Barrier(10, 10, 100, 10, FieldShape.BARRIER_WALL);
        BufferedImage img = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = img.createGraphics();
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, 200, 200);
        item.drawShape(g2);
        g2.dispose();
        // 水平線上 (50, 10) がグレー(128,128,128) になっていること
        Color drawnColor = new Color(img.getRGB(50, 10));
        assertEquals(128, drawnColor.getRed(),   "drawShape は barrier color のグレーで描画すること");
        assertEquals(128, drawnColor.getGreen(), "drawShape は barrier color のグレーで描画すること");
        assertEquals(128, drawnColor.getBlue(),  "drawShape は barrier color のグレーで描画すること");
    }

    @Test
    void testClearBarrierDoesNotThrow() {
        Barrier item = new Barrier(0, 0, 50, 10, FieldShape.BARRIER_WALL);
        int midX = (item.getStartX() + item.getEndX()) / 2;
        int midY = (item.getStartY() + item.getEndY()) / 2;
        assertTrue(SimYukkuri.world.getCurrentWorldState().getBarriers().contains(item));
        assertTrue(Barrier.onBarrier(midX, midY, 4, 4, FieldShape.BARRIER_WALL),
                "clearBarrier 前は壁マップに存在すること");

        Barrier.clearBarrier(item);

        assertFalse(SimYukkuri.world.getCurrentWorldState().getBarriers().contains(item),
                "clearBarrier 後はリストから除去されること");
        assertFalse(Barrier.onBarrier(midX, midY, 4, 4, FieldShape.BARRIER_WALL),
                "clearBarrier 後は壁マップからも消えること");
    }

    @Test
    void testOnBarrierNoWallsReturnsFalse() {
        boolean result = Barrier.onBarrier(100, 100, 10, 10, FieldShape.BARRIER_WALL);
        assertFalse(result);
    }

    @Test
    void testGetBarrierEmptyListReturnsNull() {
        SimYukkuri.world.getCurrentWorldState().getBarriers().clear();
        assertNull(Barrier.getBarrier(100, 100, 5), "バリアリストが空のときは null を返すこと");
        // バリアを追加しても全く別の座標では null のまま
        new Barrier(0, 0, 10, 0, FieldShape.BARRIER_WALL);
        assertNull(Barrier.getBarrier(500, 500, 2), "バリア範囲外の座標では null を返すこと");
    }

    @Test
    void testGetBarrierWithBarrierReturnsBarrier() {
        Barrier b = new Barrier(50, 50, 100, 50, FieldShape.BARRIER_WALL);
        int midX = (b.getStartX() + b.getEndX()) / 2;
        int midY = (b.getStartY() + b.getEndY()) / 2;
        assertSame(b, Barrier.getBarrier(midX, midY, 2), "バリア線上の座標で getBarrier が当該バリアを返すこと");
    }

    @Test
    void testAcrossBarrierNoWallsReturnsFalse() {
        boolean result = Barrier.acrossBarrier(0, 0, 100, 100, FieldShape.BARRIER_WALL);
        assertFalse(result);
    }

    @Nested
    class RegressionScenarios {
        @Test
        void testScenario_ConstructedWallMarksWallMapAndMakesOnBarrierTrue() {
            Barrier barrier = new Barrier(10, 10, 30, 10, FieldShape.BARRIER_WALL);
            int midX = (barrier.getStartX() + barrier.getEndX()) / 2;
            int midY = (barrier.getStartY() + barrier.getEndY()) / 2;

            assertTrue(SimYukkuri.world.getCurrentWorldState().getBarriers().contains(barrier));
            assertTrue(Barrier.onBarrier(midX, midY, 4, 4, FieldShape.BARRIER_WALL));
            assertTrue(Barrier.acrossBarrier(barrier.getStartX(), barrier.getStartY(), barrier.getEndX(),
                    barrier.getEndY(),
                    FieldShape.BARRIER_WALL));
        }

        @Test
        void testScenario_GetBarrierFindsExactBarrierOnItsLine() {
            Barrier barrier = new Barrier(15, 12, 35, 12, FieldShape.BARRIER_WALL);
            int midX = (barrier.getStartX() + barrier.getEndX()) / 2;
            int midY = (barrier.getStartY() + barrier.getEndY()) / 2;

            Barrier found = Barrier.getBarrier(midX, midY, 2);

            assertSame(barrier, found);
        }

        @Test
        void testScenario_ClearBarrierRemovesWallMapPresenceAndLineBlocking() {
            Barrier barrier = new Barrier(12, 16, 32, 16, FieldShape.BARRIER_WALL);
            int midX = (barrier.getStartX() + barrier.getEndX()) / 2;
            int midY = (barrier.getStartY() + barrier.getEndY()) / 2;
            assertTrue(Barrier.onBarrier(midX, midY, 4, 4, FieldShape.BARRIER_WALL));
            assertTrue(Barrier.acrossBarrier(barrier.getStartX(), barrier.getStartY(), barrier.getEndX(),
                    barrier.getEndY(),
                    FieldShape.BARRIER_WALL));

            Barrier.clearBarrier(barrier);

            assertFalse(SimYukkuri.world.getCurrentWorldState().getBarriers().contains(barrier));
            assertFalse(Barrier.onBarrier(midX, midY, 4, 4, FieldShape.BARRIER_WALL));
            assertFalse(Barrier.acrossBarrier(barrier.getStartX(), barrier.getStartY(), barrier.getEndX(),
                    barrier.getEndY(),
                    FieldShape.BARRIER_WALL));
        }
    }
}
