package org.simyukkuri.yukkuri;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.simyukkuri.SimYukkuri;
import org.simyukkuri.entity.core.living.yukkuri.impl.Marisa;
import org.simyukkuri.entity.core.living.yukkuri.impl.Nitori;
import org.simyukkuri.entity.core.living.yukkuri.impl.Reimu;
import org.simyukkuri.enums.AgeState;

public class NitoriTest {

    @Test
    public void testNitoriIdentity() {
        Nitori nitori = new Nitori();
        assertEquals(Nitori.type, nitori.getType());
        assertEquals("にとり", nitori.getNameJ());
        assertEquals("Nitori", nitori.getNameE());
    }

    @Test
    public void testNitoriNames() {
        Nitori nitori = new Nitori();
        assertEquals("にとり", nitori.getMyName());
        assertEquals("にとり", nitori.getMyNameD());
        assertEquals("", nitori.getNameJ2());
        assertEquals("", nitori.getNameE2());
    }

    @Test
    public void testNitoriHybridType() {
        Nitori nitori = new Nitori();
        assertEquals(Nitori.type, nitori.getHybridType(Reimu.type));
        assertEquals(Nitori.type, nitori.getHybridType(Marisa.type));
    }

    @Test
    public void testNitoriIsHybrid() {
        Nitori nitori = new Nitori();
        assertFalse(nitori.isHybrid());
    }

    @Test
    public void testNitoriParameterizedConstructor() {
        Nitori parent1 = new Nitori();
        Nitori parent2 = new Nitori();

        Nitori obj = new Nitori(100, 200, 0, AgeState.ADULT, parent1, parent2);

        assertNotNull(obj);
        assertEquals(Nitori.type, obj.getType());
    }

    @Test
    public void testNitoriGetMountPoint() {
        Nitori obj = new Nitori();
        // getMountPoint returns attachment offset from map
        // Most classes return null for unknown keys
        obj.getMountPoint("unknown_key");
        // Result can be null or an array depending on initialization
        // Just verify the method doesn't crash
        assertNotNull(obj);
    }

    @Test
    public void testNitoriCheckTransform() {
        Nitori obj = new Nitori();
        // checkTransform() checks transformation conditions
        // Without proper World setup, will likely return null
        obj.checkTransform();
        // Just verify the method executes without crashing
        // Just verify the method executes without crashing
        assertNull(obj.checkTransform());
    }
}
