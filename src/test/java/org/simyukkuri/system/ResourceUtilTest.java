package org.simyukkuri.system;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.Locale;
import org.junit.jupiter.api.Test;
import org.simyukkuri.util.GameLocale;

/**
 * Test class for ResourceUtil.
 */
public class ResourceUtilTest {

    @Test
    public void testGetInstance() {
        ResourceUtil instance1 = ResourceUtil.getInstance();
        ResourceUtil instance2 = ResourceUtil.getInstance();
        assertNotNull(instance1);
        assertSame(instance1, instance2, "Should be a singleton");
    }

    @Test
    public void testReadProperty() {
        ResourceUtil ru = ResourceUtil.getInstance();

        // title and version are core properties
        String title = ru.read("title");
        assertNotNull(title);
        assertNotEquals("title", title); // should not return the key itself

        String version = ru.read("version");
        assertNotNull(version);
        assertFalse(version.isEmpty());
    }

    @Test
    public void testReadNonExistentProperty() {
        ResourceUtil ru = ResourceUtil.getInstance();
        // Since the implementation returns ja/en defaults if not found,
        // it might return null if the key doesn't exist in any file.
        String value = ru.read("non.existent.key.12345");
        // Looking at ResourceUtil.java:141, it calls ja.get(property) or
        // en.get(property)
        // If not in HashMaps, it returns null.
        assertNull(value);
    }

    @Test
    public void testGameLocaleIsJapanese() {
        boolean isJp = GameLocale.isJapanese();
        assertEquals(Locale.JAPANESE.getLanguage().equals(GameLocale.getLocale().getLanguage()), isJp);
    }
}
