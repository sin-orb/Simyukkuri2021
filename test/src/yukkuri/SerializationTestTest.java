package src.yukkuri;

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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import src.util.WorldTestHelper;

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
        // mainはSystem.out/errに出力するだけで例外を投げない
        assertDoesNotThrow(() -> SerializationTest.main(new String[0]));
    }
}
