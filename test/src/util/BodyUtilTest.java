package src.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.awt.Graphics2D;
import java.awt.image.ImageObserver;
import src.base.Body;

/**
 * Test class for BodyUtil.
 * Note: BodyUtil contains only graphics rendering methods which are not
 * unit-testable.
 * This test file exists for completeness and to verify method signatures.
 */
public class BodyUtilTest {

    @Test
    public void testClassExistence() {
        // Verify BodyUtil class exists and can be loaded
        assertNotNull(BodyUtil.class);
    }

    @Test
    public void testDrawBodyMethodExists() throws NoSuchMethodException {
        // Verify the main drawBody method signature exists
        assertNotNull(BodyUtil.class.getMethod("drawBody",
                Graphics2D.class, ImageObserver.class, Body.class));
    }

    @Test
    public void testDrawBodyOverloadExists() throws NoSuchMethodException {
        // Verify the overloaded drawBody method signature exists
        assertNotNull(BodyUtil.class.getMethod("drawBody",
                Graphics2D.class, int.class, int.class,
                java.awt.image.BufferedImage.class,
                int.class, int.class, int.class, int.class,
                int.class, int.class, ImageObserver.class));
    }
}
