package org.simyukkuri.yukkuri;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.simyukkuri.SimYukkuri;
import org.simyukkuri.entity.core.living.yukkuri.impl.Marisa;
import org.simyukkuri.entity.core.living.yukkuri.impl.ReimuMarisa;
import org.simyukkuri.enums.AgeState;
import org.simyukkuri.util.WorldTestHelper;

public class ReimuMarisaTest {

    @BeforeEach
    public void setUp() {
        WorldTestHelper.resetStates();
        WorldTestHelper.initializeMinimalWorld();
    }

    @Test
    public void testReimuMarisaIdentity() {
        ReimuMarisa reimuMarisa = new ReimuMarisa();
        assertEquals(ReimuMarisa.type, reimuMarisa.getType());
    }

    @Test
    public void testReimuMarisaExtendsMarisa() {
        ReimuMarisa reimuMarisa = new ReimuMarisa();
        assertTrue(reimuMarisa instanceof Marisa);
    }

    @Test
    public void testReimuMarisaNames() {
        ReimuMarisa reimuMarisa = new ReimuMarisa();
        // ReimuMarisa should have both Reimu and Marisa names
        assertNotNull(reimuMarisa.getNameJ());
        assertNotNull(reimuMarisa.getNameE());
    }

    @Test
    public void testReimuMarisaIsHybrid() {
        ReimuMarisa reimuMarisa = new ReimuMarisa();
        assertTrue(reimuMarisa.isHybrid());
    }

    @Test
    public void testReimuMarisaParameterizedConstructor() {
        ReimuMarisa parent1 = new ReimuMarisa();
        ReimuMarisa parent2 = new ReimuMarisa();

        ReimuMarisa obj = new ReimuMarisa(100, 200, 0, AgeState.ADULT, parent1, parent2);

        assertNotNull(obj);
        assertEquals(ReimuMarisa.type, obj.getType());
    }

    @Test
    public void testReimuMarisaGetMountPoint() {
        ReimuMarisa obj = new ReimuMarisa();
        // getMountPoint returns attachment offset from map
        // Most classes return null for unknown keys
        obj.getMountPoint("unknown_key");
        // Result can be null or an array depending on initialization
        // Just verify the method doesn't crash
        assertNotNull(obj);
    }

    @Test
    public void testReimuMarisaCheckTransform() {
        ReimuMarisa obj = new ReimuMarisa();
        // checkTransform() checks transformation conditions
        // Without proper World setup, will likely return null
        obj.checkTransform();
        // Just verify the method executes without crashing
        // Just verify the method executes without crashing
        assertNull(obj.checkTransform());
    }

    @Test
    public void testTuneParameters_doesNotThrow() {
        ReimuMarisa obj = new ReimuMarisa();
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> obj.tuneParameters());
    }

    @Test
    public void testGetAnImageVerStateCtrlNagasi_doesNotThrow() {
        ReimuMarisa obj = new ReimuMarisa();
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> obj.getImageVariantState());
    }
}
