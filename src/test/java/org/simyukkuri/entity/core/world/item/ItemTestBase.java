package org.simyukkuri.entity.core.world.item;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.simyukkuri.SimYukkuri;
import org.simyukkuri.engine.World;
import org.simyukkuri.entity.core.world.WorldEntity;
import org.simyukkuri.util.WorldTestHelper;

public abstract class ItemTestBase {

    @BeforeEach
    public void setUp() {
        WorldTestHelper.resetWorld();
        SimYukkuri.world = new World();
        WorldTestHelper.initializeStandardTranslate200();
    }

    @AfterEach
    public void tearDown() {
    }

    /**
     * Helper to verify common WorldEntity properties
     */
    protected void verifyCommonProperties(WorldEntity item) {
        assertNotNull(item, "Item should not be null");
        assertTrue(item.getObjId() > 0, "Item should have a valid ID");
        assertNotNull(SimYukkuri.world.getCurrentWorldState(), "Map should exist");
        // Verify it's in the world (subclasses might need to specify WHICH map it goes
        // into,
        // but generally items put themselves in a specific map)
    }
}
