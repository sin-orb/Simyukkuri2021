package src.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import src.enums.YukkuriType;

public class YukkuriTypeMappingTest {

    @Test
    public void testTypeClassNameRoundTrip() {
        for (YukkuriType type : YukkuriType.values()) {
            assertEquals(type, YukkuriUtil.getYukkuriType(type.getClassName()));
            assertEquals(type.getClassName(), YukkuriUtil.getYukkuriClassName(type.getTypeID()));
        }
    }
}
