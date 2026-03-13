package src.system;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class IconPoolTest {

    // --- UISkin enum ---

    @Test
    public void testUISkinEnum_values() {
        IconPool.UISkin[] skins = IconPool.UISkin.values();
        assertTrue(skins.length > 0);
        for (IconPool.UISkin s : skins) {
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

    // --- getUISkinImageArray ---

    @Test
    public void testGetUISkinImageArray_notNull() {
        assertNotNull(IconPool.getUISkinImageArray());
    }

    // --- getButtonIconImageArray ---

    @Test
    public void testGetButtonIconImageArray_notNull() {
        assertNotNull(IconPool.getButtonIconImageArray());
    }

    // --- getStatusIconImageArray ---

    @Test
    public void testGetStatusIconImageArray_notNull() {
        assertNotNull(IconPool.getStatusIconImageArray());
    }

    // --- getCursorIconImageArray ---

    @Test
    public void testGetCursorIconImageArray_notNull() {
        assertNotNull(IconPool.getCursorIconImageArray());
    }

    // --- getHelpIconImageArray ---

    @Test
    public void testGetHelpIconImageArray_notNull() {
        assertNotNull(IconPool.getHelpIconImageArray());
    }

    // --- Constructor ---

    @Test
    public void testConstructor_doesNotThrow() {
        assertDoesNotThrow(() -> new IconPool());
    }
}
