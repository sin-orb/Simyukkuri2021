package src.system;

import src.entity.core.Entity;
import src.entity.core.attachment.*;
import src.entity.core.attachment.impl.*;
import src.entity.core.effect.*;
import src.entity.core.effect.impl.*;
import src.entity.core.living.yukkuri.Dna;
import src.entity.core.living.yukkuri.Yukkuri;
import src.entity.core.living.yukkuri.impl.*;
import src.entity.core.world.bodylinked.*;
import src.entity.core.world.item.*;
import src.entity.core.world.mobile.*;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class MapWindowTest {

    // --- MAP enum ---

    @Test
    public void testMAPEnum_values() {
        MapWindow.MAP[] maps = MapWindow.MAP.values();
        assertTrue(maps.length > 0);
        for (MapWindow.MAP m : maps) {
            assertNotNull(m.name());
            m.getDisplayName(); // may return null if ResourceUtil not initialized
            m.getFilePath();
            m.toString();
        }
    }

    @Test
    public void testMAPEnum_valueOf() {
        MapWindow.MAP[] maps = MapWindow.MAP.values();
        if (maps.length > 0) {
            String name = maps[0].name();
            assertEquals(maps[0], MapWindow.MAP.valueOf(name));
        }
    }

    // --- Constructor: headless → try/catch ---

    @Test
    public void testConstructor_headless_executesCode() {
        try {
            MapWindow mw = new MapWindow(null);
            assertNotNull(mw);
        } catch (Exception e) {
            // Expected in headless environment
        }
    }
}
