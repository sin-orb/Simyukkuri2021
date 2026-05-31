package org.simyukkuri.yukkuri;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.simyukkuri.util.WorldTestHelper;

public class SerializationTestTest {

    @BeforeEach
    public void setUp() {
        WorldTestHelper.initializeMinimalWorld();
    }

    @AfterEach
    public void tearDown() {
        // SerializationTest.main() creates test_save.dat in current directory
        new File("test_save.dat").delete();
        WorldTestHelper.resetWorld();
    }

    @Test
    public void testMainDoesNotThrow() {
        assertDoesNotThrow(() -> SerializationTest.main(new String[0]));
        // main() はシリアライズしてファイルに保存するため、test_save.dat が作成されること
        assertTrue(new File("test_save.dat").exists(),
                "SerializationTest.main() 実行後に test_save.dat が作成されること");
    }
}
