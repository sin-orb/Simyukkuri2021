package org.simyukkuri.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.simyukkuri.enums.YukkuriType;

public class YukkuriTypeMappingTest {

    @Test
    public void testTypeClassNameRoundTrip() {
        for (YukkuriType type : YukkuriType.values()) {
            assertEquals(type, YukkuriType.fromClassName(type.getClassName()));
            assertEquals(type, YukkuriType.fromTypeId(type.getTypeId()));
            assertEquals(type.getClassName(), YukkuriType.fromTypeId(type.getTypeId()).getClassName());
        }
    }
}
