package src.system;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import src.draw.Point4y;
import src.draw.Rectangle4y;

/**
 * Test class for Sprite.
 * Sprite is pure geometry logic with no dependencies - highly testable.
 */
public class SpriteTest {

    @Test
    public void testConstructorCenterCenter() {
        Sprite sprite = new Sprite(100, 50, Sprite.PIVOT_CENTER_CENTER);

        assertNotNull(sprite);
        assertEquals(100, sprite.getOriginalW());
        assertEquals(50, sprite.getOriginalH());
        assertEquals(100, sprite.getImageW());
        assertEquals(50, sprite.getImageH());
        assertEquals(Sprite.PIVOT_CENTER_CENTER, sprite.getPivotType());

        // Pivot should be at center
        assertEquals(50, sprite.getPivotX()); // 100 >> 1
        assertEquals(25, sprite.getPivotY()); // 50 >> 1
    }

    @Test
    public void testConstructorCenterBottom() {
        Sprite sprite = new Sprite(100, 50, Sprite.PIVOT_CENTER_BOTTOM);

        assertEquals(Sprite.PIVOT_CENTER_BOTTOM, sprite.getPivotType());

        // Pivot should be at center/bottom
        assertEquals(50, sprite.getPivotX()); // 100 >> 1
        assertEquals(49, sprite.getPivotY()); // 50 - 1
    }

    @Test
    public void testDefaultConstructor() {
        Sprite sprite = new Sprite();
        assertNotNull(sprite);
    }

    @Test
    public void testSetSpriteSize() {
        Sprite sprite = new Sprite(100, 50, Sprite.PIVOT_CENTER_CENTER);

        sprite.setSpriteSize(200, 100);

        assertEquals(200, sprite.getImageW());
        assertEquals(100, sprite.getImageH());
        // Pivot should be recalculated
        assertEquals(100, sprite.getPivotX()); // 200 >> 1
        assertEquals(50, sprite.getPivotY()); // 100 >> 1
    }

    @Test
    public void testAddSpriteSize() {
        Sprite sprite = new Sprite(100, 50, Sprite.PIVOT_CENTER_CENTER);

        sprite.addSpriteSize(20, 10);

        // Should add to original size
        assertEquals(120, sprite.getImageW()); // 100 + 20
        assertEquals(60, sprite.getImageH()); // 50 + 10
        // Pivot should be recalculated
        assertEquals(60, sprite.getPivotX()); // 120 >> 1
        assertEquals(30, sprite.getPivotY()); // 60 >> 1
    }

    @Test
    public void testCalcScreenRect() {
        Sprite sprite = new Sprite(100, 50, Sprite.PIVOT_CENTER_CENTER);
        Point4y origin = new Point4y(500, 300);

        sprite.calcScreenRect(origin, 10, 5, 100, 50);

        Rectangle4y[] rects = sprite.getScreenRect();
        assertNotNull(rects);
        assertEquals(2, rects.length);

        // Left rect
        assertEquals(490, rects[0].getX()); // 500 - 10
        assertEquals(295, rects[0].getY()); // 300 - 5
        assertEquals(100, rects[0].getWidth());
        assertEquals(50, rects[0].getHeight());

        // Right rect
        assertEquals(510, rects[1].getX()); // 500 + 10
        assertEquals(295, rects[1].getY()); // 300 - 5
        assertEquals(-100, rects[1].getWidth()); // Negative for mirroring
        assertEquals(50, rects[1].getHeight());
    }

    @Test
    public void testSetPivotType() {
        Sprite sprite = new Sprite(100, 50, Sprite.PIVOT_CENTER_CENTER);

        sprite.setPivotType(Sprite.PIVOT_CENTER_BOTTOM);

        assertEquals(Sprite.PIVOT_CENTER_BOTTOM, sprite.getPivotType());
    }

    @Test
    public void testGettersSetters() {
        Sprite sprite = new Sprite();

        sprite.setOriginalW(150);
        assertEquals(150, sprite.getOriginalW());

        sprite.setOriginalH(75);
        assertEquals(75, sprite.getOriginalH());

        sprite.setImageW(200);
        assertEquals(200, sprite.getImageW());

        sprite.setImageH(100);
        assertEquals(100, sprite.getImageH());

        sprite.setPivotX(50);
        assertEquals(50, sprite.getPivotX());

        sprite.setPivotY(25);
        assertEquals(25, sprite.getPivotY());
    }

    @Test
    public void testPivotCalculationCenter() {
        Sprite sprite = new Sprite(100, 50, Sprite.PIVOT_CENTER_CENTER);

        // Test with different sizes
        sprite.setSpriteSize(200, 100);
        assertEquals(100, sprite.getPivotX());
        assertEquals(50, sprite.getPivotY());

        sprite.setSpriteSize(50, 25);
        assertEquals(25, sprite.getPivotX());
        assertEquals(12, sprite.getPivotY());
    }

    @Test
    public void testPivotCalculationBottom() {
        Sprite sprite = new Sprite(100, 50, Sprite.PIVOT_CENTER_BOTTOM);

        // Test with different sizes
        sprite.setSpriteSize(200, 100);
        assertEquals(100, sprite.getPivotX());
        assertEquals(99, sprite.getPivotY()); // height - 1

        sprite.setSpriteSize(50, 25);
        assertEquals(25, sprite.getPivotX());
        assertEquals(24, sprite.getPivotY()); // height - 1
    }

    @Test
    public void testScreenRectLeftRight() {
        Sprite sprite = new Sprite(100, 50, Sprite.PIVOT_CENTER_CENTER);
        Point4y origin = new Point4y(0, 0);

        sprite.calcScreenRect(origin, 0, 0, 50, 25);

        Rectangle4y[] rects = sprite.getScreenRect();

        // Both should be at origin with different widths
        assertEquals(0, rects[0].getX());
        assertEquals(0, rects[1].getX());
        assertEquals(50, rects[0].getWidth());
        assertEquals(-50, rects[1].getWidth()); // Negative for right side
    }

    @Test
    public void testOriginalVsImageSize() {
        Sprite sprite = new Sprite(100, 50, Sprite.PIVOT_CENTER_CENTER);

        // Original size should remain constant
        assertEquals(100, sprite.getOriginalW());
        assertEquals(50, sprite.getOriginalH());

        // Change image size
        sprite.setSpriteSize(200, 100);

        // Original should not change
        assertEquals(100, sprite.getOriginalW());
        assertEquals(50, sprite.getOriginalH());

        // Image size should change
        assertEquals(200, sprite.getImageW());
        assertEquals(100, sprite.getImageH());
    }
}
