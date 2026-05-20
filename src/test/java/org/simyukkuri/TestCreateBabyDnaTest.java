package org.simyukkuri;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.simyukkuri.util.WorldTestHelper;

public class TestCreateBabyDnaTest {

    @BeforeEach
    public void setUp() {
        WorldTestHelper.initializeMinimalWorld();
    }

    @Test
    public void testMainDoesNotThrow() {
        assertDoesNotThrow(() -> TestCreateBabyDna.main(new String[0]));
    }
}
