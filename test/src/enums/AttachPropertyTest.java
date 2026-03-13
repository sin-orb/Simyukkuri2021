package src.enums;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class AttachPropertyTest {

    @Test
    void testValues() {
        AttachProperty[] values = AttachProperty.values();
        assertNotNull(values);
        assertTrue(values.length > 0);

        for (AttachProperty value : values) {
            assertNotNull(value);
            assertEquals(value, Enum.valueOf(AttachProperty.class, value.name()));
        }
    }

    private void assertTrue(boolean condition) {
        if (!condition)
            throw new AssertionError("Condition failed");
    }
}
