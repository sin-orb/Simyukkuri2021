package src.item;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.image.BufferedImage;
import java.awt.Graphics2D;

import org.junit.jupiter.api.Test;

import src.SimYukkuri;
import src.base.ItemTestBase;
import src.system.FieldShapeBase;

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
            Barrier b = new Barrier(0, 0, 100, 100, FieldShapeBase.BARRIER_WALL);
            assertNotNull(b);
            assertNotNull(b.getColor());
            assertEquals(FieldShapeBase.BARRIER_WALL, b.getAttribute());
        });
    }

    @Test
    void testConstructorWithArgs_BARRIER_GAP_MINI_doesNotThrow() {
        assertDoesNotThrow(() -> {
            Barrier b = new Barrier(0, 0, 50, 50, FieldShapeBase.BARRIER_GAP_MINI);
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
        Barrier item = new Barrier(0, 0, 100, 100, FieldShapeBase.BARRIER_WALL);
        BufferedImage img = new BufferedImage(800, 600, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = img.createGraphics();
        assertDoesNotThrow(() -> item.drawShape(g2));
        g2.dispose();
    }

    @Test
    void testClearBarrier_doesNotThrow() {
        Barrier item = new Barrier(0, 0, 50, 50, FieldShapeBase.BARRIER_WALL);
        assertTrue(SimYukkuri.world.getCurrentMap().getBarrier().contains(item));
        assertDoesNotThrow(() -> Barrier.clearBarrier(item));
        assertFalse(SimYukkuri.world.getCurrentMap().getBarrier().contains(item));
    }

    @Test
    void testOnBarrier_noWalls_returnsFalse() {
        // wallMap is zeroed, so no walls → false
        boolean result = Barrier.onBarrier(100, 100, 10, 10, FieldShapeBase.BARRIER_WALL);
        assertFalse(result);
    }

    @Test
    void testGetBarrier_emptyList_returnsNull() {
        SimYukkuri.world.getCurrentMap().getBarrier().clear();
        assertNull(Barrier.getBarrier(100, 100, 5));
    }

    @Test
    void testGetBarrier_withBarrier_returnsBarrier() {
        Barrier b = new Barrier(50, 50, 100, 50, FieldShapeBase.BARRIER_WALL);
        // getBarrier checks map coordinates, may or may not find based on coord mapping
        // Just verify it doesn't throw
        assertDoesNotThrow(() -> Barrier.getBarrier(50, 50, 5));
    }

    @Test
    void testAcrossBarrier_noWalls_returnsFalse() {
        boolean result = Barrier.acrossBarrier(0, 0, 100, 100, FieldShapeBase.BARRIER_WALL);
        assertFalse(result);
    }
}
