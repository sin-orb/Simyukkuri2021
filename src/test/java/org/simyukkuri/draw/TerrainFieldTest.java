package org.simyukkuri.draw;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.LinearGradientPaint;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.simyukkuri.engine.ModLoader;
import org.simyukkuri.ui.WorldSelectionWindow;
import org.simyukkuri.util.WorldTestHelper;
import org.simyukkuri.visual.TerrainBillboard;

public class TerrainFieldTest {

    @TempDir
    Path tempDir;

    private static String originalJarPath;

    @BeforeEach
    public void setUp() throws Exception {
        WorldTestHelper.resetStates();
        System.setProperty("java.awt.headless", "true");
        WorldTestHelper.initializeTranslate(1000, 1000, 500, 900, 700, 100, 100, new float[] { 1.0f });

        // Save original static fields if not already saved
        if (originalJarPath == null) {
            originalJarPath = ModLoader.getJarPath();
            Field devField = ModLoader.class.getDeclaredField("developRoot");
            devField.setAccessible(true);
            // originalDevelopRoot = (String) devField.get(null);
            Field backField = ModLoader.class.getDeclaredField("backTheme");
            backField.setAccessible(true);
            // originalBackTheme = (String) backField.get(null);
        }

        // Setup ModLoader paths to tempDir
        Field jarPathField = ModLoader.class.getDeclaredField("jarPath");
        jarPathField.setAccessible(true);
        jarPathField.set(null, tempDir.toAbsolutePath().toString() + File.separator);

        Field developRootField = ModLoader.class.getDeclaredField("developRoot");
        developRootField.setAccessible(true);
        developRootField.set(null, tempDir.toAbsolutePath().toString() + "/mod/develop/");

        // Reset backTheme for each test
        Field backThemeField = ModLoader.class.getDeclaredField("backTheme");
        backThemeField.setAccessible(true);
        backThemeField.set(null, null);

        // Reset TerrainField static fields to ensure test isolation
        Field isPersField = TerrainField.class.getDeclaredField("isPers");
        isPersField.setAccessible(true);
        isPersField.set(null, false);

        Field ownerTypeField = TerrainField.class.getDeclaredField("ownerType");
        ownerTypeField.setAccessible(true);
        ownerTypeField.set(null, 0);

        Field skyColorField = TerrainField.class.getDeclaredField("skyColor");
        skyColorField.setAccessible(true);
        skyColorField.set(null, null);
    }

    @Test
    public void testLoadTerrainOldFormat() throws Exception {
        // Set up mock back.jpg in backTheme path
        String mapPath = WorldSelectionWindow.WorldSelection.values()[0].getFilePath();
        Path backDir = tempDir.resolve("mod").resolve("back").resolve(mapPath);
        Files.createDirectories(backDir);
        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        ImageIO.write(img, "jpg", backDir.resolve("back.jpg").toFile());

        Field backThemeField = ModLoader.class.getDeclaredField("backTheme");
        backThemeField.setAccessible(true);
        backThemeField.set(null, tempDir.toAbsolutePath().toString() + "/mod/back/");

        TerrainField.loadTerrain(0, this.getClass().getClassLoader(), null);

        // Check defaults for old format
        assertTrue(TerrainField.isPers());
        assertNotNull(TerrainField.getBillboards());

        // morning sky should have default gradient
        assertNotNull(TerrainField.getSkyGrad(0));
    }

    @Test
    public void testLoadTerrainNewFormat() throws Exception {
        String mapPath = WorldSelectionWindow.WorldSelection.values()[0].getFilePath();
        // Modern format is preferred in backTheme too if bg.ini exists there
        Path backDir = tempDir.resolve("mod").resolve("back").resolve(mapPath);
        Files.createDirectories(backDir);

        Path bgIni = backDir.resolve("bg.ini");
        String content = "[Asset]\n" +
                "img=test_bg.png\n" +
                "img=obj1.png\n" +
                "[Environment]\n" +
                "base=test_bg.png\n" +
                "morning_top_rgba=255,0,0,255\n" +
                "morning_bottom_rgba=0,0,255,255\n" +
                "[Object]\n" +
                "obj1.png=0.5,0.5,0.0\n" +
                "[Floor]\n" +
                "obj1.png=0.1,0.1,0.0\n" +
                "[Ceiling]\n" +
                "obj1.png=0.9,0.9,0.0\n" +
                "[Owner]\n" +
                "perspective=false\n" +
                "owner=1\n";
        Files.write(bgIni, content.getBytes("UTF-8"));

        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        ImageIO.write(img, "png", backDir.resolve("test_bg.png").toFile());
        ImageIO.write(img, "png", backDir.resolve("obj1.png").toFile());

        Field backThemeField = ModLoader.class.getDeclaredField("backTheme");
        backThemeField.setAccessible(true);
        backThemeField.set(null, tempDir.toAbsolutePath().toString() + "/mod/back/");

        TerrainField.loadTerrain(0, this.getClass().getClassLoader(), null);

        // Check custom format results
        assertFalse(TerrainField.isPers(), "New format should set perspective to false as specified in INI");

        // morning sky should be set from RGBA
        LinearGradientPaint morning = TerrainField.getSkyGrad(0);
        assertNotNull(morning);

        // Struct list should have 1 item
        List<TerrainBillboard> structList = TerrainField.getBillboards();
        assertEquals(1, structList.size());
    }
}
