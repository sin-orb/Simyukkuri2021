package src.system;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Graphics2D;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import src.enums.Event;

class FieldShapeBaseTest {

    static class ConcreteFieldShape extends FieldShapeBase {
        private static final long serialVersionUID = 1L;

        @Override
        public int getAttribute() {
            return 0;
        }

        @Override
        public int getMinimumSize() {
            return 10;
        }

        @Override
        public void drawShape(Graphics2D g2) {
            // No-op for testing
        }
    }

    private ConcreteFieldShape shape;

    @BeforeEach
    void setUp() {
        shape = new ConcreteFieldShape();
    }

    @Test
    void testMapPosAndContains() {
        shape.setMapPos(10, 10, 20, 20);
        assertEquals(10, shape.getMapSX());
        assertEquals(10, shape.getMapSY());
        assertEquals(20, shape.getMapEX());
        assertEquals(20, shape.getMapEY());

        assertTrue(shape.mapContains(15, 15));
        assertTrue(shape.mapContains(10, 10)); // Boundary
        assertTrue(shape.mapContains(20, 20)); // Boundary

        assertFalse(shape.mapContains(9, 15));
        assertFalse(shape.mapContains(21, 15));
    }

    @Test
    void testFieldPosAndContains() {
        shape.setFieldPos(100, 100, 200, 200);
        assertEquals(100, shape.getFieldSX());
        assertEquals(100, shape.getFieldSY());
        assertEquals(200, shape.getFieldEX());
        assertEquals(200, shape.getFieldEY());

        assertTrue(shape.fieldContains(150, 150));
        assertFalse(shape.fieldContains(50, 150));
    }

    @Test
    void testRemoveAndClockTick() {
        assertFalse(shape.isRemoved());
        assertEquals(Event.DONOTHING, shape.clockTick());

        shape.remove();
        assertTrue(shape.isRemoved());
        assertEquals(Event.REMOVED, shape.clockTick());
    }

    @Test
    void testDimensions() {
        shape.setMapW(50);
        assertEquals(50, shape.getMapW());

        shape.setMapH(60);
        assertEquals(60, shape.getMapH());

        shape.setFieldW(500);
        assertEquals(500, shape.getFieldW());

        shape.setFieldH(600);
        assertEquals(600, shape.getFieldH());
    }

    // --- Individual map coordinate setters ---

    @Test
    void testIndividualMapSetters() {
        shape.setMapSX(11);
        assertEquals(11, shape.getMapSX());
        shape.setMapSY(22);
        assertEquals(22, shape.getMapSY());
        shape.setMapEX(33);
        assertEquals(33, shape.getMapEX());
        shape.setMapEY(44);
        assertEquals(44, shape.getMapEY());
    }

    // --- Individual field coordinate setters ---

    @Test
    void testIndividualFieldSetters() {
        shape.setFieldSX(111);
        assertEquals(111, shape.getFieldSX());
        shape.setFieldSY(222);
        assertEquals(222, shape.getFieldSY());
        shape.setFieldEX(333);
        assertEquals(333, shape.getFieldEX());
        shape.setFieldEY(444);
        assertEquals(444, shape.getFieldEY());
    }

    // --- hasShapePopup / executeShapePopup (default implementations) ---

    @Test
    void testHasShapePopup_defaultReturnsNONE() {
        // Default FieldShapeBase.hasShapePopup() returns ShapeMenuTarget.NONE (not null)
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> shape.hasShapePopup());
        org.junit.jupiter.api.Assertions.assertNotNull(shape.hasShapePopup());
    }

    @Test
    void testExecuteShapePopup_defaultDoesNotThrow() {
        // Default implementation is empty body
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(
            () -> shape.executeShapePopup(src.system.ItemMenu.ShapeMenu.TOP));
    }
}
