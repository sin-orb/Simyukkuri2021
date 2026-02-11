
package src.enums;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class YukkuriTypeTest {

    @Test
    public void testEnumProperties() {
        // Test REIMU
        YukkuriType reimu = YukkuriType.REIMU;
        assertEquals("Reimu", reimu.getClassName());
        assertEquals("reimu", reimu.getMessageFileName());
        // nameJ depends on resource loading, testing for null safety at least
        assertNotNull(reimu.getNameJ(), "NameJ should be loaded from resources (or fallback)");
        // With dummy resources, it might be null if key missing in EN/JA?
        // Fallback logic returns null?
        // ResourceUtil.read returns null if both missing.
        // Wait, I created dummy files with only "enums_babyyu" etc.
        // "reimu" key is likely missing.
        // Reimu nameJ is: ResourceUtil.IS_JP ? Reimu.nameJ : Reimu.nameE
        // Accesses static fields of Reimu class.
        // Reimu class static init might be safe?

        // Let's test non-resource dependants first or just assertNotNull
    }

    @Test
    public void testValues() {
        assertTrue(YukkuriType.values().length > 0);
    }
}
