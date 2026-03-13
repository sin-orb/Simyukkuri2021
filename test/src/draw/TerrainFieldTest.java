package src.draw;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Files;
import java.lang.reflect.Field;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.awt.LinearGradientPaint;
import java.util.List;
import src.base.Obj;
import src.system.MapWindow;
import src.util.WorldTestHelper;

public class TerrainFieldTest {

    @TempDir
    Path tempDir;

    private static String originalJarPath;
    private static String originalDevelopRoot;
    private static String originalBackTheme;

    @BeforeEach
    public void setUp() throws Exception {
        WorldTestHelper.resetStates();
        System.setProperty("java.awt.headless", "true");
        // Initialize Translate with reasonable values
        Translate.setCanvasSize(900, 700, 100, 100, new float[] { 1.0f });

        // Save original static fields if not already saved
        if (originalJarPath == null) {
            originalJarPath = ModLoader.getJarPath();
            Field devField = ModLoader.class.getDeclaredField("developRoot");
            devField.setAccessible(true);
            originalDevelopRoot = (String) devField.get(null);
            Field backField = ModLoader.class.getDeclaredField("backTheme");
            backField.setAccessible(true);
            originalBackTheme = (String) backField.get(null);
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
        String mapPath = MapWindow.MAP.values()[0].getFilePath();
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
        assertNotNull(TerrainField.getStructList());

        // morning sky should have default gradient
        assertNotNull(TerrainField.getSkyGrad(0));
    }

    @Test
    public void testLoadTerrainNewFormat() throws Exception {
        String mapPath = MapWindow.MAP.values()[0].getFilePath();
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
        List<Obj> structList = TerrainField.getStructList();
        assertEquals(1, structList.size());
    }
}
