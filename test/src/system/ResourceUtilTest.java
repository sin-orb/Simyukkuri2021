package src.system;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.Locale;

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
    public void testIS_JP() {
        // Just verify it doesn't crash accessing the static field
        boolean isJp = ResourceUtil.IS_JP;
        // Result depends on environment, so we just check it matches Locale
        assertEquals(Locale.getDefault().getLanguage().equals(new Locale("ja").getLanguage()), isJp);
    }
}
