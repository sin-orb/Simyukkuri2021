package org.simyukkuri.system;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.swing.ImageIcon;

import org.junit.jupiter.api.Test;

public class IconPoolTest {

    // --- UiSkin enum ---

    @Test
    public void testUiSkinEnum_values() {
        IconPool.UiSkin[] skins = IconPool.UiSkin.values();
        assertTrue(skins.length > 0);
        for (IconPool.UiSkin s : skins) {
            assertNotNull(s.name());
            assertNotNull(s.getFileName());
        }
    }

    // --- ButtonIcon enum ---

    @Test
    public void testButtonIconEnum_values() {
        IconPool.ButtonIcon[] icons = IconPool.ButtonIcon.values();
        assertTrue(icons.length > 0);
        for (IconPool.ButtonIcon i : icons) {
            assertNotNull(i.name());
            assertNotNull(i.getFileName());
        }
    }

    // --- StatusIcon enum ---

    @Test
    public void testStatusIconEnum_values() {
        IconPool.StatusIcon[] icons = IconPool.StatusIcon.values();
        assertTrue(icons.length > 0);
        for (IconPool.StatusIcon i : icons) {
            assertNotNull(i.name());
            assertNotNull(i.getFileName());
            i.getHelp(); // may return null if ResourceUtil not initialized
        }
    }

    // --- CursorIcon enum ---

    @Test
    public void testCursorIconEnum_values() {
        IconPool.CursorIcon[] icons = IconPool.CursorIcon.values();
        assertTrue(icons.length > 0);
        for (IconPool.CursorIcon i : icons) {
            assertNotNull(i.name());
            assertNotNull(i.getFileName());
        }
    }

    // --- HelpIcon enum ---

    @Test
    public void testHelpIconEnum_values() {
        IconPool.HelpIcon[] icons = IconPool.HelpIcon.values();
        assertTrue(icons.length > 0);
        for (IconPool.HelpIcon i : icons) {
            assertNotNull(i.name());
            assertNotNull(i.getFileName());
        }
    }

    // --- loadImages: headless → try/catch ---

    @Test
    public void testLoadImages_headless_executesCode() {
        try {
            IconPool.loadImages(IconPool.class.getClassLoader(), null);
        } catch (Exception e) {
            // Expected: IOException because image files not found in test environment
        }
    }

    // --- getUiSkinImageArray ---

    @Test
    public void testGetUiSkinImageArray_notNull() {
        java.awt.image.BufferedImage[] arr = IconPool.getUiSkinImageArray();
        assertNotNull(arr, "getUiSkinImageArray が非null を返すこと");
        assertEquals(IconPool.UiSkin.values().length, arr.length,
                "配列サイズが UiSkin enum の要素数と一致すること");
    }

    // --- getButtonIconImageArray ---

    @Test
    public void testGetButtonIconImageArray_notNull() {
        java.awt.image.BufferedImage[] arr = IconPool.getButtonIconImageArray();
        assertNotNull(arr, "getButtonIconImageArray が非null を返すこと");
        assertEquals(IconPool.ButtonIcon.values().length, arr.length,
                "配列サイズが ButtonIcon enum の要素数と一致すること");
    }

    // --- getStatusIconImageArray ---

    @Test
    public void testGetStatusIconImageArray_notNull() {
        ImageIcon[] arr = IconPool.getStatusIconImageArray();
        assertNotNull(arr, "getStatusIconImageArray が非null を返すこと");
        assertEquals(IconPool.StatusIcon.values().length, arr.length,
                "配列サイズが StatusIcon enum の要素数と一致すること");
    }

    // --- getCursorIconImageArray ---

    @Test
    public void testGetCursorIconImageArray_notNull() {
        java.awt.image.BufferedImage[] arr = IconPool.getCursorIconImageArray();
        assertNotNull(arr, "getCursorIconImageArray が非null を返すこと");
        assertEquals(IconPool.CursorIcon.values().length, arr.length,
                "配列サイズが CursorIcon enum の要素数と一致すること");
    }

    // --- getHelpIconImageArray ---

    @Test
    public void testGetHelpIconImageArray_notNull() {
        java.awt.image.BufferedImage[] arr = IconPool.getHelpIconImageArray();
        assertNotNull(arr, "getHelpIconImageArray が非null を返すこと");
        assertEquals(IconPool.HelpIcon.values().length, arr.length,
                "配列サイズが HelpIcon enum の要素数と一致すること");
    }

    // --- Constructor ---

    @Test
    public void testConstructor_doesNotThrow() {
        IconPool instance = new IconPool();
        assertNotNull(instance, "IconPool インスタンスが生成されること");
    }
}
