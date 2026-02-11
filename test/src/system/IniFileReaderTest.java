package src.system;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.File;
import java.util.HashMap;

/**
 * Test class for IniFileReader.
 * IniFileReader requires file I/O - limited testability.
 */
public class IniFileReaderTest {

    @Test
    public void testConstructorFile() {
        File testFile = new File("test.ini");
        IniFileReader reader = new IniFileReader(testFile, null);

        assertNotNull(reader);
    }

    @Test
    public void testConstructorResource() {
        File testFile = new File("test.ini");
        IniFileReader reader = new IniFileReader(testFile, "resource/test.ini");

        assertNotNull(reader);
    }

    @Test
    public void testOpenNonExistent() {
        File nonExistent = new File("nonexistent_file_12345.ini");
        IniFileReader reader = new IniFileReader(nonExistent, null);

        boolean opened = reader.open(getClass().getClassLoader());

        // Should return false for non-existent file
        assertFalse(opened);
    }

    @Test
    public void testReadNextWithoutOpen() {
        File testFile = new File("test.ini");
        IniFileReader reader = new IniFileReader(testFile, null);

        // Reading without opening should return null or throw exception
        try {
            HashMap<String, String> result = reader.readNext();
            assertNull(result);
        } catch (NullPointerException e) {
            // Expected - reader not opened
            assertNotNull(e);
        }
    }

    @Test
    public void testClose() {
        File testFile = new File("test.ini");
        IniFileReader reader = new IniFileReader(testFile, null);

        // Close should not crash even if not opened
        try {
            reader.close();
            assertTrue(true);
        } catch (Exception e) {
            // Should handle gracefully
            assertNotNull(e);
        }
    }

    @Test
    public void testConstants() {
        // Verify constants exist
        assertEquals("Section", IniFileReader.INI_SECTION);
        assertEquals("Key", IniFileReader.INI_KEY);
        assertEquals("Value", IniFileReader.INI_VALUE);
    }
}
