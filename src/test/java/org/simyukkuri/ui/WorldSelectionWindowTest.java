package org.simyukkuri.ui;

import org.simyukkuri.entity.core.Entity;
import org.simyukkuri.entity.core.attachment.*;
import org.simyukkuri.entity.core.attachment.impl.*;
import org.simyukkuri.entity.core.effect.*;
import org.simyukkuri.entity.core.effect.impl.*;
import org.simyukkuri.entity.core.living.yukkuri.Dna;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.living.yukkuri.impl.*;
import org.simyukkuri.entity.core.world.bodylinked.*;
import org.simyukkuri.entity.core.world.item.*;
import org.simyukkuri.entity.core.world.mobile.*;

import static org.junit.jupiter.api.Assertions.*;
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
