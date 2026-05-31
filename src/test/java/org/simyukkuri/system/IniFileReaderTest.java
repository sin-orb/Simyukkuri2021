package org.simyukkuri.system;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Test class for IniFileReader.
 * IniFileReader requires file I/O - limited testability.
 */
public class IniFileReaderTest {

    @Test
    public void testOpenNonExistent() {
        File nonExistent = new File("nonexistent_file_12345.ini");
        IniFileReader reader = new IniFileReader(nonExistent, null);
        assertNotNull(reader, "存在しないファイルでも IniFileReader インスタンスが生成されること");

        boolean opened = reader.open(getClass().getClassLoader());
        assertFalse(opened, "存在しないファイルを open すると false を返すこと");
    }

    @Test
    public void testReadNextWithoutOpen() {
        File testFile = new File("test.ini");
        IniFileReader reader = new IniFileReader(testFile, null);
        // open() 前に readNext() を呼ぶと NPE になること（設計上の制限）
        assertThrows(NullPointerException.class, reader::readNext,
                "open() 前に readNext() を呼ぶと NullPointerException が発生すること");
    }

    @Test
    public void testClose() {
        File testFile = new File("test.ini");
        IniFileReader reader = new IniFileReader(testFile, null);
        // open() 前に close() を呼ぶと NPE になること（設計上の制限）
        assertThrows(NullPointerException.class, reader::close,
                "open() 前に close() を呼ぶと NullPointerException が発生すること");
    }

    @Test
    public void testConstants() {
        // 定数の具体的な値確認
        assertEquals("Section", IniFileReader.INI_SECTION, "INI_SECTION は 'Section' であること");
        assertEquals("Key",     IniFileReader.INI_KEY,     "INI_KEY は 'Key' であること");
        assertEquals("Value",   IniFileReader.INI_VALUE,   "INI_VALUE は 'Value' であること");
        // 3つの定数がすべて異なること
        assertFalse(IniFileReader.INI_SECTION.equals(IniFileReader.INI_KEY),
                "INI_SECTION と INI_KEY は異なること");
        assertFalse(IniFileReader.INI_KEY.equals(IniFileReader.INI_VALUE),
                "INI_KEY と INI_VALUE は異なること");
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
        // コメント行はスキップされて最初のkeyが返ること
        HashMap<String, String> result = reader.readNext();
        assertNotNull(result, "コメント行はスキップされること");
        assertEquals("Section1", result.get(IniFileReader.INI_SECTION), "Section1 が読めること");
        assertEquals("key", result.get(IniFileReader.INI_KEY), "key=value の key が読めること");
        assertEquals("value", result.get(IniFileReader.INI_VALUE), "key=value の value が読めること");
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
        // 空行はスキップされて最初のkeyが返ること
        HashMap<String, String> result = reader.readNext();
        assertNotNull(result, "空行はスキップされること");
        assertEquals("Sect", result.get(IniFileReader.INI_SECTION), "Sect が読めること");
        assertEquals("k", result.get(IniFileReader.INI_KEY), "k=v の k が読めること");
        assertEquals("v", result.get(IniFileReader.INI_VALUE), "k=v の v が読めること");
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
        // 1回目: key=val が読める
        HashMap<String, String> result1 = reader.readNext();
        assertNotNull(result1, "1回目は null でないこと");
        assertEquals("key", result1.get(IniFileReader.INI_KEY), "1回目は key=val の key が読めること");
        // 2回目: EOF → null を返すこと
        HashMap<String, String> result2 = reader.readNext();
        assertNull(result2, "EOF 後は null を返すこと");
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
        // 閉じ括弧なしのセクション行はスキップされて次の有効なsectionが返ること
        HashMap<String, String> result = reader.readNext();
        assertNotNull(result, "閉じ括弧なしのセクションはスキップされること");
        assertEquals("ValidSect", result.get(IniFileReader.INI_SECTION), "ValidSect が読めること");
        assertEquals("k", result.get(IniFileReader.INI_KEY), "k=v の k が読めること");
        reader.close();
    }

    @Nested
    class RegressionScenarios {
        @Test
        public void testScenario_ReadNextKeepsCurrentSectionAcrossMultipleKeys() throws IOException {
            File tmp = File.createTempFile("test", ".ini");
            tmp.deleteOnExit();
            try (Writer fw = new OutputStreamWriter(new FileOutputStream(tmp), StandardCharsets.UTF_8)) {
                fw.write("[Sect]\n");
                fw.write("first=1\n");
                fw.write("second=2\n");
            }
            IniFileReader reader = new IniFileReader(tmp, null);
            reader.open(getClass().getClassLoader());

            HashMap<String, String> first = reader.readNext();
            final HashMap<String, String> second = reader.readNext();

            assertNotNull(first);
            assertEquals("Sect", first.get(IniFileReader.INI_SECTION));
            assertEquals("first", first.get(IniFileReader.INI_KEY));
            assertEquals("1", first.get(IniFileReader.INI_VALUE));

            assertNotNull(second);
            assertEquals("Sect", second.get(IniFileReader.INI_SECTION));
            assertEquals("second", second.get(IniFileReader.INI_KEY));
            assertEquals("2", second.get(IniFileReader.INI_VALUE));
            reader.close();
        }

        @Test
        public void testScenario_ReadNextSwitchesToLaterSectionBeforeReturningNextKey() throws IOException {
            File tmp = File.createTempFile("test", ".ini");
            tmp.deleteOnExit();
            try (Writer fw = new OutputStreamWriter(new FileOutputStream(tmp), StandardCharsets.UTF_8)) {
                fw.write("[First]\n");
                fw.write("a=10\n");
                fw.write("[Second]\n");
                fw.write("b=20\n");
            }
            IniFileReader reader = new IniFileReader(tmp, null);
            reader.open(getClass().getClassLoader());

            HashMap<String, String> first = reader.readNext();
            final HashMap<String, String> second = reader.readNext();

            assertNotNull(first);
            assertEquals("First", first.get(IniFileReader.INI_SECTION));
            assertEquals("a", first.get(IniFileReader.INI_KEY));
            assertEquals("10", first.get(IniFileReader.INI_VALUE));

            assertNotNull(second);
            assertEquals("Second", second.get(IniFileReader.INI_SECTION));
            assertEquals("b", second.get(IniFileReader.INI_KEY));
            assertEquals("20", second.get(IniFileReader.INI_VALUE));
            reader.close();
        }
    }
}
