package org.simyukkuri.system;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import org.junit.jupiter.api.Test;

class CustomLogFormatterTest {

    @Test
    void testFormat() {
        CustomLogFormatter formatter = new CustomLogFormatter();

        LogRecord record = new LogRecord(Level.INFO, "Test message");
        record.setLoggerName("TestLogger");

        String result = formatter.format(record);
        assertNotNull(result);
        // 日時フォーマット (yyyy-MM-dd HH:mm:ss.SSS) で始まること
        assertTrue(result.matches("(?s)\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d{3}.*"),
                "日時フォーマット yyyy-MM-dd HH:mm:ss.SSS で始まること");
        assertTrue(result.contains("INFO"),    "INFO レベルが出力に含まれること");
        assertTrue(result.contains("TestLogger"), "ロガー名が出力に含まれること");
        assertTrue(result.contains(" - "),     "ロガー名とメッセージの区切り ' - ' があること");
        assertTrue(result.contains("Test message"), "メッセージが出力に含まれること");
        assertTrue(result.endsWith("\n"),      "出力が改行で終わること");

        // WARNING は "WARN " に短縮されること
        record.setLevel(Level.WARNING);
        result = formatter.format(record);
        assertTrue(result.contains("WARN "), "WARNING は 'WARN ' に短縮されること");

        // SEVERE レベルの確認
        record.setLevel(Level.SEVERE);
        result = formatter.format(record);
        assertTrue(result.contains("SEVERE"), "SEVERE レベルが出力に含まれること");

        // FINE レベルの確認
        record.setLevel(Level.FINE);
        result = formatter.format(record);
        assertTrue(result.contains("FINE"), "FINE レベルが出力に含まれること");
    }
}
