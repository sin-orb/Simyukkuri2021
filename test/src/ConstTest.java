package src;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.awt.Color;

import org.junit.jupiter.api.Test;

import src.system.BasicStrokeEX;

class ConstTest {

    @Test
    void testDirections() {
        assertEquals(0, Const.LEFT);
        assertEquals(1, Const.RIGHT);
    }

    @Test
    void testWindowColors() {
        assertNotNull(Const.WINDOW_COLOR);
        assertEquals(5, Const.WINDOW_COLOR.length);

        for (Color[] colors : Const.WINDOW_COLOR) {
            assertEquals(3, colors.length); // Outline, Fill, Text
        }
    }

    @Test
    void testNegiWindowColor() {
        assertNotNull(Const.NEGI_WINDOW_COLOR);
        assertEquals(3, Const.NEGI_WINDOW_COLOR.length);
    }

    @Test
    void testDamageValues() {
        assertEquals(100, Const.NEEDLE);
        assertEquals(100 * 24 * 2, Const.HAMMER);
    }

    @Test
    void testTimeConstants() {
        assertEquals(20, Const.HOLDMESSAGE);
        assertEquals(20, Const.STAYLIMIT);
        assertEquals(100, Const.SHITSTAY);
    }

    @Test
    void testBodySize() {
        assertEquals(3, Const.BODY_SIZE.length);
        assertEquals(0.25f, Const.BODY_SIZE[0]);
        assertEquals(0.5f, Const.BODY_SIZE[1]);
        assertEquals(1.0f, Const.BODY_SIZE[2]);
    }

    @Test
    void testStalkOffsets() {
        assertEquals(8, Const.STALK_OF_S_X.length);
        assertEquals(8, Const.STALK_OF_S_Y.length);
    }

    @Test
    void testLimits() {
        assertEquals(3, Const.EXT_FORCE_PULL_LIMIT.length);
        assertEquals(3, Const.EXT_FORCE_PUSH_LIMIT.length);
    }

    @Test
    void testIndices() {
        assertEquals(0, Const.BABY_INDEX);
        assertEquals(1, Const.CHILD_INDEX);
        assertEquals(2, Const.ADULT_INDEX);
    }

    @Test
    void testWindowStroke() {
        assertNotNull(Const.WINDOW_STROKE);
        assertEquals(5, Const.WINDOW_STROKE.length);
        assertTrue(Const.WINDOW_STROKE[0] instanceof BasicStrokeEX);
    }

    private void assertTrue(boolean b) {
        if (!b)
            throw new AssertionError();
    }
}
