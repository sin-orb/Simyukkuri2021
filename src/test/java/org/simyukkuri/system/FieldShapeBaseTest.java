package org.simyukkuri.system;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Graphics2D;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.simyukkuri.enums.TickResult;
import org.simyukkuri.field.FieldShape;

class FieldShapeBaseTest {

    static class ConcreteFieldShape extends FieldShape {
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
        shape.setBounds(10, 10, 20, 20);
        assertEquals(10, shape.getStartX());
        assertEquals(10, shape.getStartY());
        assertEquals(20, shape.getEndX());
        assertEquals(20, shape.getEndY());

        assertTrue(shape.mapContains(15, 15));
        assertTrue(shape.mapContains(10, 10)); // Boundary
        assertTrue(shape.mapContains(20, 20)); // Boundary

        assertFalse(shape.mapContains(9, 15));
        assertFalse(shape.mapContains(21, 15));
    }

    @Test
    void testFieldPosAndContains() {
        shape.setFieldPos(100, 100, 200, 200);
        assertEquals(100, shape.getFieldSx());
        assertEquals(100, shape.getFieldSy());
        assertEquals(200, shape.getFieldEx());
        assertEquals(200, shape.getFieldEy());

        assertTrue(shape.fieldContains(150, 150));
        assertFalse(shape.fieldContains(50, 150));
    }

    @Test
    void testRemoveAndClockTick() {
        assertFalse(shape.isRemoved());
        long ageBefore = shape.getAge();
        assertEquals(TickResult.NONE, shape.clockTick());
        assertEquals(ageBefore + FieldShape.TICK, shape.getAge());

        shape.remove();
        assertTrue(shape.isRemoved());
        assertEquals(TickResult.REMOVED, shape.clockTick());
        assertEquals(ageBefore + FieldShape.TICK * 2L, shape.getAge());
    }

    @Test
    void testDimensions() {
        shape.setWorldWidth(50);
        assertEquals(50, shape.getWorldWidth());

        shape.setWorldHeight(60);
        assertEquals(60, shape.getWorldHeight());

        shape.setFieldW(500);
        assertEquals(500, shape.getFieldW());

        shape.setFieldH(600);
        assertEquals(600, shape.getFieldH());
    }

    @Test
    void testHasShapePopup_defaultReturnsNONE() {
        assertEquals(org.simyukkuri.system.ItemMenu.ShapeMenuTarget.NONE, shape.hasShapePopup());
    }
}
