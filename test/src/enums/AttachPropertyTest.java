package src.enums;

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
