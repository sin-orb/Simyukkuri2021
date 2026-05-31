package org.simyukkuri.system;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Test class for FrameRate.
 * Tests frame rate calculation and timing logic.
 */
public class FrameRateTest {

    @Test
    public void testConstructor() {
        FrameRate frameRate = new FrameRate();
        assertNotNull(frameRate);
        // コンストラクタ直後のフレームレートは 0.0f であること
        assertEquals(0.0f, frameRate.getFrameRate(), 0.001f, "初期フレームレートは 0.0f であること");
    }

    @Test
    public void testGetFrameRateInitial() {
        FrameRate frameRate = new FrameRate();

        // 初期フレームレートは 0
        assertEquals(0.0f, frameRate.getFrameRate(), 0.001f, "初期フレームレートは 0.0f であること");

        // 1秒未満のカウントではフレームレートは更新されないこと
        for (int i = 0; i < 10; i++) {
            frameRate.count();
        }
        assertEquals(0.0f, frameRate.getFrameRate(), 0.001f, "1秒未満のカウントではフレームレートは 0.0f のまま");
    }

    @Test
    public void testCountFrames() {
        FrameRate frameRate = new FrameRate();

        // 1秒未満でカウントしてもフレームレートは更新されないこと
        for (int i = 0; i < 10; i++) {
            frameRate.count();
        }
        assertEquals(0.0f, frameRate.getFrameRate(), 0.001f,
                "1秒未満のカウントではフレームレートが 0.0f のまま（時間契約の確認）");
    }

    @Test
    public void testFrameRateCalculation() throws InterruptedException {
        FrameRate frameRate = new FrameRate();

        // 1秒より少し長い間フレームをカウント（約10ms間隔≈100FPS）
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < 1100) {
            frameRate.count();
            Thread.sleep(10);
        }

        // 1秒後にフレームレートが計算・更新されること
        float fps = frameRate.getFrameRate();
        assertTrue(fps > 0, "1秒後にフレームレートが 0 より大きく更新されること");
        // 10ms sleep → 約100FPS。50〜150の範囲で収まること
        assertTrue(fps >= 50, "10ms sleep に相当する FPS(>=50) であること");
        assertTrue(fps <= 150, "10ms sleep に相当する FPS(<=150) であること");
    }
}
