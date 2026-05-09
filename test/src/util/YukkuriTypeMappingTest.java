package src.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import src.enums.YukkuriType;

public class YukkuriTypeMappingTest {

    @Test
    public void testTypeClassNameRoundTrip() {
        for (YukkuriType type : YukkuriType.values()) {
            assertEquals(type, YukkuriType.fromClassName(type.getClassName()));
            assertEquals(type, YukkuriType.fromTypeID(type.getTypeID()));
            assertEquals(type.getClassName(), YukkuriType.fromTypeID(type.getTypeID()).getClassName());
        }
    }
}
