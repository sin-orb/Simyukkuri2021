package src.enums;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import src.game.Stalk;
import src.item.Food;

class ObjEXTypeTest {

    @Test
    void testGetClassPack() {
        // Test a few specific values to ensure they map to correct classes
        assertEquals(Food.class, ObjEXType.FOOD.getClassPack());
        assertEquals(Stalk.class, ObjEXType.STALK.getClassPack());

        // Ensure all values have a non-null class pack
        for (ObjEXType type : ObjEXType.values()) {
            assertNotNull(type.getClassPack(), "Class pack should not be null for " + type);
        }
    }
}
