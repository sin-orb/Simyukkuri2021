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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import src.entity.core.world.bodylinked.Stalk;
import src.entity.core.world.item.Food;

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
