package org.simyukkuri.system;

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
        assertTrue(result.contains("INFO"));
        assertTrue(result.contains("TestLogger"));
        assertTrue(result.contains("Test message"));

        // Test other levels
        record.setLevel(Level.SEVERE);
        result = formatter.format(record);
        assertTrue(result.contains("SEVERE"));

        record.setLevel(Level.FINE);
        result = formatter.format(record);
        assertTrue(result.contains("FINE"));
    }
}
