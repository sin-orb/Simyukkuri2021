package org.simyukkuri.draw;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Vector;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.simyukkuri.engine.ModLoader;
import org.simyukkuri.util.WorldTestHelper;

public class ModLoaderTest {

    @TempDir
    Path tempDir;

    private static String originalJarPath;
    private static String originalDevelopRoot;

    @BeforeEach
    public void setUp() throws Exception {
        WorldTestHelper.resetStates();
        // Save original static fields if not already saved
        if (originalJarPath == null) {
            originalJarPath = ModLoader.getJarPath();
            Field devField = ModLoader.class.getDeclaredField("developRoot");
            devField.setAccessible(true);
            originalDevelopRoot = (String) devField.get(null);
        }
        // Ensure headless
        System.setProperty("java.awt.headless", "true");
    }

    @Test
    public void testSetAndGetJarPath() {
        ModLoader.setJarPath();
        String path = ModLoader.getJarPath();
        assertNotNull(path);
        // On Linux/Mac, it should end with / or be empty if lastIndexOf failed
        assertTrue(path.endsWith(File.separator) || path.isEmpty());
    }

    @Test
    public void testGetDefaultDirs() {
        assertNotNull(ModLoader.getDefaultImgRootDir());
        assertNotNull(ModLoader.getDefaultDataDir());
        assertNotNull(ModLoader.getModRootDir());
        assertNotNull(ModLoader.getDataMsgDir());
    }

    @Test
    public void testThemePaths() {
        ModLoader.setBackThemePath("test_back");
        assertNotNull(ModLoader.getBackThemePath());
        assertTrue(ModLoader.getBackThemePath().contains("test_back"));

        ModLoader.setItemThemePath("test_item");
        assertNotNull(ModLoader.getItemThemePath());
        assertTrue(ModLoader.getItemThemePath().contains("test_item"));

        ModLoader.setYukkuriThemePath("test_body");
        assertNotNull(ModLoader.getYukkuriThemePath());
        assertTrue(ModLoader.getYukkuriThemePath().contains("test_body"));

        ModLoader.setBackThemePath(null);
        assertNull(ModLoader.getBackThemePath());
    }

    @Test
    public void testGetThemeList() throws Exception {
        // Redirect jarPath to tempDir
        Field jarPathField = ModLoader.class.getDeclaredField("jarPath");
        jarPathField.setAccessible(true);
        jarPathField.set(null, tempDir.toAbsolutePath().toString() + File.separator);

        // Create dummy mod structure
        // ModLoader constants: MOD_ROOT_DIR="mod", MOD_BACK_DIR="back",
        // MOD_ITEM_DIR="item", MOD_BODY_DIR="yukkuri"
        Path modDir = tempDir.resolve("mod");
        Path backDir = modDir.resolve("back");
        Path itemDir = modDir.resolve("item");
        Path yukkuriDir = modDir.resolve("yukkuri");

        Files.createDirectories(backDir);
        Files.createDirectories(itemDir);
        Files.createDirectories(yukkuriDir);

        Files.createDirectory(backDir.resolve("back_theme"));
        Files.createDirectory(itemDir.resolve("item_theme"));
        Files.createDirectory(yukkuriDir.resolve("body_theme"));

        Vector<String> backList = ModLoader.getBackThemes();
        assertTrue(backList.contains("back_theme"));

        Vector<String> itemList = ModLoader.getItemThemes();
        assertTrue(itemList.contains("item_theme"));

        Vector<String> bodyList = ModLoader.getYukkuriThemes();
        assertTrue(bodyList.contains("body_theme"));

        // Restore jarPath
        jarPathField.set(null, originalJarPath);
    }

    @Test
    public void testOpenMessageFile_NonExistent() {
        BufferedReader br = ModLoader.openMessageFile(this.getClass().getClassLoader(), "nonexistent", "file.txt",
                false);
        assertNull(br);
    }

    @Test
    public void testOpenMessageFile_DevelopmentMode() throws Exception {
        // Test developmentRoot check
        Field jarPathField = ModLoader.class.getDeclaredField("jarPath");
        jarPathField.setAccessible(true);
        jarPathField.set(null, tempDir.toAbsolutePath().toString() + File.separator);

        Field developRootField = ModLoader.class.getDeclaredField("developRoot");
        developRootField.setAccessible(true);
        // setJarPath reloads developRoot but in tests we want to point it to tempDir
        developRootField.set(null,
                tempDir.toAbsolutePath().toString() + File.separator + "mod" + File.separator + "develop");

        Path devDir = tempDir.resolve("mod").resolve("develop");
        Path msgDir = devDir.resolve("msg_path");
        Files.createDirectories(msgDir);
        Path msgFile = msgDir.resolve("test.txt");
        Files.write(msgFile, "Hello World".getBytes("UTF-8"));

        BufferedReader br = ModLoader.openMessageFile(this.getClass().getClassLoader(), "msg_path", "test.txt", true);
        assertNotNull(br, "Buffered reader should not be null for existing development file");
        assertEquals("Hello World", br.readLine());
        br.close();

        // Restore original values
        jarPathField.set(null, originalJarPath);
        developRootField.set(null, originalDevelopRoot);
    }
}
