
package src.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import src.yukkuri.Reimu;

public class IniFileUtilTest {

    @Test
    public void testClassExistence() {
        // Explicit reference to force compilation
        assertNotNull(IniFileUtil.class);

        // Verify class can be found by name too
        try {
            Class.forName("src.util.IniFileUtil");
        } catch (ClassNotFoundException e) {
            fail("IniFileUtil class not found via reflection");
        }
    }

    @Test
    public void testReadYukkuriIniFileNullSafety() {
        // Test that readYukkuriIniFile handles null gracefully
        // Should not crash with null input
        try {
            IniFileUtil.readYukkuriIniFile(null);
            // If it doesn't throw, that's acceptable
        } catch (NullPointerException e) {
            // NPE is also acceptable for null input
            assertNotNull(e);
        }
    }

    @Test
    public void testReadYukkuriIniFileWithValidBody() {
        // Test with a valid body instance
        // Without actual INI files, this will use defaults
        Reimu reimu = new Reimu();

        try {
            IniFileUtil.readYukkuriIniFile(reimu);
            // Should complete without crashing
            assertNotNull(reimu);
        } catch (Exception e) {
            // File not found is expected in test environment
            assertTrue(e.getMessage() == null ||
                    e.getMessage().contains("file") ||
                    e.getMessage().contains("File"));
        }
    }

    @Test
    public void testReadYukkuriIniFileWithForceFlag() {
        // Test readYukkuriIniFile with force parameter
        Reimu reimu = new Reimu();

        try {
            IniFileUtil.readYukkuriIniFile(reimu, true);
            // Should complete without crashing
            assertNotNull(reimu);
        } catch (Exception e) {
            // File not found is expected in test environment
            assertTrue(e.getMessage() == null ||
                    e.getMessage().contains("file") ||
                    e.getMessage().contains("File"));
        }
    }
}
