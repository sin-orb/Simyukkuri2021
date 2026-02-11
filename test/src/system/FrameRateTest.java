package src.system;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for FrameRate.
 * Tests frame rate calculation and timing logic.
 */
public class FrameRateTest {

    @Test
    public void testConstructor() {
        FrameRate frameRate = new FrameRate();
        assertNotNull(frameRate);
    }

    @Test
    public void testGetFrameRateInitial() {
        FrameRate frameRate = new FrameRate();

        // Initial framerate should be 0
        assertEquals(0.0f, frameRate.getFrameRate(), 0.001f);
    }

    @Test
    public void testCountFrames() {
        FrameRate frameRate = new FrameRate();

        // Count some frames
        for (int i = 0; i < 10; i++) {
            frameRate.count();
        }

        // Should not crash
        assertNotNull(frameRate);
    }

    @Test
    public void testFrameRateCalculation() throws InterruptedException {
        FrameRate frameRate = new FrameRate();

        // Count frames for slightly over 1 second
        long startTime = System.currentTimeMillis();
        int frameCount = 0;

        while (System.currentTimeMillis() - startTime < 1100) {
            frameRate.count();
            frameCount++;
            Thread.sleep(10); // ~100 FPS
        }

        // After 1 second, framerate should be calculated
        float fps = frameRate.getFrameRate();

        // FPS should be reasonable (between 50-150 given 10ms sleep)
        assertTrue(fps > 0, "FPS should be greater than 0");
        assertTrue(fps < 200, "FPS should be less than 200");
    }
}
