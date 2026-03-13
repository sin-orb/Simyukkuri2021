package src.enums;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;

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
        assertTrue(reimu.getTypeID() >= 0);

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
            boolean added = ids.add(type.getTypeID());
            assertTrue(added, "Duplicate typeID found: " + type.getTypeID() + " for " + type.name());
        }
    }
}
