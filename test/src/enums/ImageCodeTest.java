package src.enums;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import org.junit.jupiter.api.Test;

class ImageCodeTest {

    @Test
    void testBurned() {
        // BURNED("front", "burned1", false)
        ImageCode code = ImageCode.BURNED;
        assertFalse(code.hasSecondary());

        assertEquals("front/burned1", code.getJarPath(true));
        assertEquals("front/burned1", code.getJarPath(false));

        String expectedPath = "front" + File.separator + "burned1";
        assertEquals(expectedPath, code.getFilePath(true));
        assertEquals(expectedPath, code.getFilePath(false));
    }

    @Test
    void testBody() {
        // BODY("body", null, true)
        ImageCode code = ImageCode.BODY;
        assertTrue(code.hasSecondary());

        assertEquals("left/body", code.getJarPath(false));
        assertEquals("right/body", code.getJarPath(true));

        String expectedLeft = "left" + File.separator + "body";
        String expectedRight = "right" + File.separator + "body";

        assertEquals(expectedLeft, code.getFilePath(false));
        assertEquals(expectedRight, code.getFilePath(true));
    }

    @Test
    void testBraid() {
        // BRAID("braid", "braid", true)
        ImageCode code = ImageCode.BRAID;
        assertTrue(code.hasSecondary());

        assertEquals("left/braid/braid", code.getJarPath(false));
        assertEquals("right/braid/braid", code.getJarPath(true));

        String expectedLeft = "left" + File.separator + "braid" + File.separator + "braid";
        String expectedRight = "right" + File.separator + "braid" + File.separator + "braid";

        assertEquals(expectedLeft, code.getFilePath(false));
        assertEquals(expectedRight, code.getFilePath(true));
    }
}
