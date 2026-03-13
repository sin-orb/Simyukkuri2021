package src.base;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import src.SimYukkuri;
import src.draw.Translate;
import src.draw.World;
import src.util.WorldTestHelper;

public abstract class ItemTestBase {

    @BeforeEach
    public void setUp() {
        SimYukkuri.world = new World();
        Translate.setMapSize(1000, 1000, 200);
        Translate.setCanvasSize(800, 600, 100, 100, new float[]{1.0f});
        Translate.createTransTable(false);
    }

    @AfterEach
    public void tearDown() {
    }

    /**
     * Helper to verify common ObjEX properties
     */
    protected void verifyCommonProperties(ObjEX item) {
        assertNotNull(item, "Item should not be null");
        assertTrue(item.getObjId() > 0, "Item should have a valid ID");
        assertNotNull(SimYukkuri.world.getCurrentMap(), "Map should exist");
        // Verify it's in the world (subclasses might need to specify WHICH map it goes
        // into,
        // but generally items put themselves in a specific map)
    }
}
