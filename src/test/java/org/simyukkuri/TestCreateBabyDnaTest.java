package org.simyukkuri;

import org.simyukkuri.entity.core.Entity;
import org.simyukkuri.entity.core.attachment.*;
import org.simyukkuri.entity.core.attachment.impl.*;
import org.simyukkuri.entity.core.effect.*;
import org.simyukkuri.entity.core.effect.impl.*;
import org.simyukkuri.entity.core.living.yukkuri.Dna;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.living.yukkuri.impl.*;
import org.simyukkuri.entity.core.world.bodylinked.*;
import org.simyukkuri.entity.core.world.item.*;
import org.simyukkuri.entity.core.world.mobile.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

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
