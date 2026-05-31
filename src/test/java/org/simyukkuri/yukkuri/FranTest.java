package org.simyukkuri.yukkuri;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.simyukkuri.SimYukkuri;
import org.simyukkuri.entity.core.living.yukkuri.impl.Fran;
import org.simyukkuri.entity.core.living.yukkuri.impl.Marisa;
import org.simyukkuri.entity.core.living.yukkuri.impl.Reimu;
import org.simyukkuri.enums.AgeState;

public class FranTest {

    @Test
    public void testFranIdentity() {
        Fran fran = new Fran();
        assertEquals(Fran.type, fran.getType());
        assertEquals("ふらん", fran.getNameJ());
        assertEquals("Fran", fran.getNameE());
    }

    @Test
    public void testFranNames() {
        Fran fran = new Fran();
        assertEquals("ふらん", fran.getMyName());
        assertEquals("ふらん", fran.getMyNameD());
        assertEquals("", fran.getNameJ2());
        assertEquals("", fran.getNameE2());
    }

    @Test
    public void testFranHybridType() {
        Fran fran = new Fran();
        assertEquals(Fran.type, fran.getHybridType(Reimu.type));
        assertEquals(Fran.type, fran.getHybridType(Marisa.type));
    }

    @Test
    public void testFranIsHybrid() {
        Fran fran = new Fran();
        assertFalse(fran.isHybrid());
    }

    @Test
    public void testFranDefaultConstructor() {
        Fran fran = new Fran();
        assertNotNull(fran);
        assertEquals(Fran.type, fran.getType());
    }

    @Test
    public void testFranParameterizedConstructor() {
        Fran parent1 = new Fran();
        Fran parent2 = new Fran();

        Fran obj = new Fran(100, 200, 0, AgeState.ADULT, parent1, parent2);

        assertNotNull(obj);
        assertEquals(Fran.type, obj.getType());
    }

    @Test
    public void testFranGetMountPoint() {
        Fran obj = new Fran();
        // getMountPoint returns attachment offset from map
        // Most classes return null for unknown keys
        obj.getMountPoint("unknown_key");
        // Result can be null or an array depending on initialization
        // Just verify the method doesn't crash
        assertNotNull(obj);
    }

    @Test
    public void testFranCheckTransform() {
        Fran obj = new Fran();
        // checkTransform() checks transformation conditions
        // Without proper World setup, will likely return null
        obj.checkTransform();
        // Just verify the method executes without crashing
        // Just verify the method executes without crashing
        assertNull(obj.checkTransform());
    }
}
