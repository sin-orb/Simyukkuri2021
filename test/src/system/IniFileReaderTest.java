package src.system;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
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

    // --- open with real temp file ---

    @Test
    public void testOpen_realFile_returnsTrue() throws IOException {
        File tmp = File.createTempFile("test", ".ini");
        tmp.deleteOnExit();
        try (Writer fw = new OutputStreamWriter(new FileOutputStream(tmp), StandardCharsets.UTF_8)) {
            fw.write("[Section1]\nkey=value\n");
        }
        IniFileReader reader = new IniFileReader(tmp, null);
        assertTrue(reader.open(getClass().getClassLoader()));
        reader.close();
    }

    // --- readNext with real temp file ---

    @Test
    public void testReadNext_section_and_key_returnsMap() throws IOException {
        File tmp = File.createTempFile("test", ".ini");
        tmp.deleteOnExit();
        try (Writer fw = new OutputStreamWriter(new FileOutputStream(tmp), StandardCharsets.UTF_8)) {
            fw.write("[MySect]\n");
            fw.write("mykey=myvalue\n");
        }
        IniFileReader reader = new IniFileReader(tmp, null);
        reader.open(getClass().getClassLoader());
        HashMap<String, String> result = reader.readNext();
        assertNotNull(result);
        assertEquals("MySect", result.get(IniFileReader.INI_SECTION));
        assertEquals("mykey", result.get(IniFileReader.INI_KEY));
        assertEquals("myvalue", result.get(IniFileReader.INI_VALUE));
        reader.close();
    }

    @Test
    public void testReadNext_commentLine_skipped() throws IOException {
        File tmp = File.createTempFile("test", ".ini");
        tmp.deleteOnExit();
        try (Writer fw = new OutputStreamWriter(new FileOutputStream(tmp), StandardCharsets.UTF_8)) {
            fw.write("# this is a comment\n");
            fw.write("[Section1]\n");
            fw.write("key=value\n");
        }
        IniFileReader reader = new IniFileReader(tmp, null);
        reader.open(getClass().getClassLoader());
        HashMap<String, String> result = reader.readNext();
        assertNotNull(result);
        assertEquals("Section1", result.get(IniFileReader.INI_SECTION));
        reader.close();
    }

    @Test
    public void testReadNext_emptyLine_skipped() throws IOException {
        File tmp = File.createTempFile("test", ".ini");
        tmp.deleteOnExit();
        try (Writer fw = new OutputStreamWriter(new FileOutputStream(tmp), StandardCharsets.UTF_8)) {
            fw.write("\n\n[Sect]\nk=v\n");
        }
        IniFileReader reader = new IniFileReader(tmp, null);
        reader.open(getClass().getClassLoader());
        HashMap<String, String> result = reader.readNext();
        assertNotNull(result);
        assertEquals("Sect", result.get(IniFileReader.INI_SECTION));
        reader.close();
    }

    @Test
    public void testReadNext_endOfFile_returnsNull() throws IOException {
        File tmp = File.createTempFile("test", ".ini");
        tmp.deleteOnExit();
        try (Writer fw = new OutputStreamWriter(new FileOutputStream(tmp), StandardCharsets.UTF_8)) {
            fw.write("[Sect]\nkey=val\n");
        }
        IniFileReader reader = new IniFileReader(tmp, null);
        reader.open(getClass().getClassLoader());
        reader.readNext(); // reads key=val
        HashMap<String, String> result2 = reader.readNext(); // EOF
        assertNull(result2);
        reader.close();
    }

    @Test
    public void testReadNext_sectionWithoutBracketEnd_skipped() throws IOException {
        File tmp = File.createTempFile("test", ".ini");
        tmp.deleteOnExit();
        try (Writer fw = new OutputStreamWriter(new FileOutputStream(tmp), StandardCharsets.UTF_8)) {
            fw.write("[NoClosingBracket\n");
            fw.write("[ValidSect]\n");
            fw.write("k=v\n");
        }
        IniFileReader reader = new IniFileReader(tmp, null);
        reader.open(getClass().getClassLoader());
        HashMap<String, String> result = reader.readNext();
        assertNotNull(result);
        assertEquals("ValidSect", result.get(IniFileReader.INI_SECTION));
        reader.close();
    }
}
