package org.simyukkuri.enums;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.simyukkuri.entity.core.world.bodylinked.Stalk;
import org.simyukkuri.entity.core.world.item.Food;

class ObjEXTypeTest {

    @Test
    void testGetClassPack() {
        // Test a few specific values to ensure they map to correct classes
        assertEquals(Food.class, WorldEntityKind.FOOD.getClassPack());
        assertEquals(Stalk.class, WorldEntityKind.STALK.getClassPack());

        // Ensure all values have a non-null class pack
        for (WorldEntityKind type : WorldEntityKind.values()) {
            assertNotNull(type.getClassPack(), "Class pack should not be null for " + type);
        }
    }
}
