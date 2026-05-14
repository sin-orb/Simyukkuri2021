package org.simyukkuri.enums;

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
