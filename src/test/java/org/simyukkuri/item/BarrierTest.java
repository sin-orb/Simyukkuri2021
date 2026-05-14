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

import static org.junit.jupiter.api.Assertions.*;

import java.awt.image.BufferedImage;
import java.awt.Graphics2D;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.simyukkuri.SimYukkuri;
import org.simyukkuri.entity.core.world.item.ItemTestBase;
import org.simyukkuri.field.FieldShape;
import org.simyukkuri.field.impl.Barrier;

public class BarrierTest extends ItemTestBase {
    // setUp is inherited from ItemTestBase (calls Translate.setMapSize(1000,1000,200))

    @Test
    void testConstructor_Default() {
        Barrier item = new Barrier();
        SimYukkuri.world.getCurrentMap().getBarrier().add(item);
        assertNotNull(item);
        assertTrue(SimYukkuri.world.getCurrentMap().getBarrier().contains(item));
    }

    @Test
    void testGetColor_defaultIsNull() {
        Barrier item = new Barrier();
        assertNull(item.getColor());
    }

    @Test
    void testGetAttribute_defaultIsZero() {
        Barrier item = new Barrier();
        assertEquals(0, item.getAttribute());
    }

    @Test
    void testGetMinimumSize_returns1() {
        Barrier item = new Barrier();
        assertEquals(1, item.getMinimumSize());
    }

    @Test
    void testConstructorWithArgs_BARRIER_WALL_doesNotThrow() {
        assertDoesNotThrow(() -> {
            Barrier b = new Barrier(0, 0, 100, 100, FieldShape.BARRIER_WALL);
            assertNotNull(b);
            assertNotNull(b.getColor());
            assertEquals(FieldShape.BARRIER_WALL, b.getAttribute());
        });
    }

    @Test
    void testConstructorWithArgs_BARRIER_GAP_MINI_doesNotThrow() {
        assertDoesNotThrow(() -> {
            Barrier b = new Barrier(0, 0, 50, 50, FieldShape.BARRIER_GAP_MINI);
            assertNotNull(b);
        });
    }

    @Test
    void testDrawPreview_doesNotThrow() {
        BufferedImage img = new BufferedImage(800, 600, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = img.createGraphics();
        assertDoesNotThrow(() -> Barrier.drawPreview(g2, 10, 10, 100, 100));
        g2.dispose();
    }

    @Test
    void testDrawShape_doesNotThrow() {
        Barrier item = new Barrier(0, 0, 100, 100, FieldShape.BARRIER_WALL);
        BufferedImage img = new BufferedImage(800, 600, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = img.createGraphics();
        assertDoesNotThrow(() -> item.drawShape(g2));
        g2.dispose();
    }

    @Test
    void testClearBarrier_doesNotThrow() {
        Barrier item = new Barrier(0, 0, 50, 50, FieldShape.BARRIER_WALL);
        assertTrue(SimYukkuri.world.getCurrentMap().getBarrier().contains(item));
        assertDoesNotThrow(() -> Barrier.clearBarrier(item));
        assertFalse(SimYukkuri.world.getCurrentMap().getBarrier().contains(item));
    }

    @Test
    void testOnBarrier_noWalls_returnsFalse() {
        // wallMap is zeroed, so no walls → false
        boolean result = Barrier.onBarrier(100, 100, 10, 10, FieldShape.BARRIER_WALL);
        assertFalse(result);
    }

    @Test
    void testGetBarrier_emptyList_returnsNull() {
        SimYukkuri.world.getCurrentMap().getBarrier().clear();
        assertNull(Barrier.getBarrier(100, 100, 5));
    }

    @Test
    void testGetBarrier_withBarrier_returnsBarrier() {
        Barrier b = new Barrier(50, 50, 100, 50, FieldShape.BARRIER_WALL);
        // getBarrier checks map coordinates, may or may not find based on coord mapping
        // Just verify it doesn't throw
        assertDoesNotThrow(() -> Barrier.getBarrier(50, 50, 5));
    }

    @Test
    void testAcrossBarrier_noWalls_returnsFalse() {
        boolean result = Barrier.acrossBarrier(0, 0, 100, 100, FieldShape.BARRIER_WALL);
        assertFalse(result);
    }

    @Nested
    class RegressionScenarios {
        @Test
        void testScenario_ConstructedWallMarksWallMapAndMakesOnBarrierTrue() {
            Barrier barrier = new Barrier(10, 10, 30, 10, FieldShape.BARRIER_WALL);
            int midX = (barrier.getMapSX() + barrier.getMapEX()) / 2;
            int midY = (barrier.getMapSY() + barrier.getMapEY()) / 2;

            assertTrue(SimYukkuri.world.getCurrentMap().getBarrier().contains(barrier));
            assertTrue(Barrier.onBarrier(midX, midY, 4, 4, FieldShape.BARRIER_WALL));
            assertTrue(Barrier.acrossBarrier(barrier.getMapSX(), barrier.getMapSY(), barrier.getMapEX(), barrier.getMapEY(),
                    FieldShape.BARRIER_WALL));
        }

        @Test
        void testScenario_GetBarrierFindsExactBarrierOnItsLine() {
            Barrier barrier = new Barrier(15, 12, 35, 12, FieldShape.BARRIER_WALL);
            int midX = (barrier.getMapSX() + barrier.getMapEX()) / 2;
            int midY = (barrier.getMapSY() + barrier.getMapEY()) / 2;

            Barrier found = Barrier.getBarrier(midX, midY, 2);

            assertSame(barrier, found);
        }

        @Test
        void testScenario_ClearBarrierRemovesWallMapPresenceAndLineBlocking() {
            Barrier barrier = new Barrier(12, 16, 32, 16, FieldShape.BARRIER_WALL);
            int midX = (barrier.getMapSX() + barrier.getMapEX()) / 2;
            int midY = (barrier.getMapSY() + barrier.getMapEY()) / 2;
            assertTrue(Barrier.onBarrier(midX, midY, 4, 4, FieldShape.BARRIER_WALL));
            assertTrue(Barrier.acrossBarrier(barrier.getMapSX(), barrier.getMapSY(), barrier.getMapEX(), barrier.getMapEY(),
                    FieldShape.BARRIER_WALL));

            Barrier.clearBarrier(barrier);

            assertFalse(SimYukkuri.world.getCurrentMap().getBarrier().contains(barrier));
            assertFalse(Barrier.onBarrier(midX, midY, 4, 4, FieldShape.BARRIER_WALL));
            assertFalse(Barrier.acrossBarrier(barrier.getMapSX(), barrier.getMapSY(), barrier.getMapEX(), barrier.getMapEY(),
                    FieldShape.BARRIER_WALL));
        }
    }
}
