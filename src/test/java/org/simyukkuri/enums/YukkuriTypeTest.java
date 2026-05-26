package org.simyukkuri.enums;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * YukkuriType enum のテスト.
 */
public class YukkuriTypeTest {

    @BeforeAll
    public static void setUpClass() {
        System.setProperty("java.awt.headless", "true");
    }

    @Test
    public void testEnumProperties() {
        // 代表的なタイプをいくつかチェック
        YukkuriType reimu = YukkuriType.REIMU;
        assertEquals("Reimu", reimu.getClassName());
        assertEquals("reimu", reimu.getMessageFileName());
        assertEquals("reimu", reimu.getImageDirName());
        assertTrue(reimu.getTypeId() >= 0);

        YukkuriType marisa = YukkuriType.MARISA;
        assertEquals("Marisa", marisa.getClassName());
        assertEquals("marisa", marisa.getMessageFileName());
        assertEquals("marisa", marisa.getImageDirName());

        YukkuriType deibu = YukkuriType.DEIBU;
        assertEquals("Deibu", deibu.getClassName());
    }

    @Test
    public void testAllTypesHaveName() {
        for (YukkuriType type : YukkuriType.values()) {
            assertNotNull(type.getNameJ(), "NameJ should not be null for " + type.name());
            assertFalse(type.getNameJ().isEmpty(), "NameJ should not be empty for " + type.name());
        }
    }

    @Test
    public void testTypeIDsAreUnique() {
        java.util.Set<Integer> ids = new java.util.HashSet<>();
        for (YukkuriType type : YukkuriType.values()) {
            boolean added = ids.add(type.getTypeId());
            assertTrue(added, "Duplicate typeID found: " + type.getTypeId() + " for " + type.name());
        }
    }

    @Test
    public void testLookupByClassNameAndTypeID() {
        assertEquals(YukkuriType.REIMU, YukkuriType.fromClassName("Reimu"));
        assertEquals(YukkuriType.MARISA, YukkuriType.fromTypeId(0));
        assertNull(YukkuriType.fromClassName(null));
        assertNull(YukkuriType.fromClassName("UnknownClass"));
        assertNull(YukkuriType.fromTypeId(-999));
    }

    @Test
    public void testNormalizeOffspringType() {
        assertEquals(YukkuriType.MARISA, YukkuriType.normalizeOffspringType(YukkuriType.DOSMARISA));
        assertEquals(YukkuriType.REIMU, YukkuriType.normalizeOffspringType(YukkuriType.DEIBU));
        assertEquals(YukkuriType.ALICE, YukkuriType.normalizeOffspringType(YukkuriType.ALICE));
    }
}
