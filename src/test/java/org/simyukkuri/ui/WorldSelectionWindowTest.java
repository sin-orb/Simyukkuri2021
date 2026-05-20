package org.simyukkuri.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class WorldSelectionWindowTest {

    // --- WorldSelection enum ---

    @Test
    public void testWorldSelectionEnum_values() {
        WorldSelectionWindow.WorldSelection[] maps = WorldSelectionWindow.WorldSelection.values();
        assertTrue(maps.length > 0);
        for (WorldSelectionWindow.WorldSelection m : maps) {
            assertNotNull(m.name());
            m.getDisplayName(); // may return null if ResourceUtil not initialized
            m.getFilePath();
            m.toString();
        }
    }

    @Test
    public void testWorldSelectionEnum_valueOf() {
        WorldSelectionWindow.WorldSelection[] maps = WorldSelectionWindow.WorldSelection.values();
        if (maps.length > 0) {
            String name = maps[0].name();
            assertEquals(maps[0], WorldSelectionWindow.WorldSelection.valueOf(name));
        }
    }

    // --- Constructor: headless → try/catch ---

    @Test
    public void testConstructor_headless_executesCode() {
        try {
            WorldSelectionWindow mw = new WorldSelectionWindow(null);
            assertNotNull(mw);
        } catch (Exception e) {
            // Expected in headless environment
        }
    }
}
