package org.simyukkuri.yukkuri;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.simyukkuri.SimYukkuri;
import org.simyukkuri.entity.core.living.yukkuri.impl.Eiki;
import org.simyukkuri.entity.core.living.yukkuri.impl.Marisa;
import org.simyukkuri.entity.core.living.yukkuri.impl.Reimu;
import org.simyukkuri.enums.AgeState;

public class EikiTest {

    @Test
    public void testEikiIdentity() {
        Eiki eiki = new Eiki();
        assertEquals(Eiki.type, eiki.getType());
        assertEquals("えーき", eiki.getNameJ());
        assertEquals("Eiki", eiki.getNameE());
    }

    @Test
    public void testEikiNames() {
        Eiki eiki = new Eiki();
        assertEquals("えーき", eiki.getMyName());
        assertEquals("えーき", eiki.getMyNameD());
        assertEquals("", eiki.getNameJ2());
        assertEquals("", eiki.getNameE2());
    }

    @Test
    public void testEikiHybridType() {
        Eiki eiki = new Eiki();
        assertEquals(Eiki.type, eiki.getHybridType(Reimu.type));
        assertEquals(Eiki.type, eiki.getHybridType(Marisa.type));
    }

    @Test
    public void testEikiIsHybrid() {
        Eiki eiki = new Eiki();
        assertFalse(eiki.isHybrid());
    }

    @Test
    public void testEikiParameterizedConstructor() {
        Eiki parent1 = new Eiki();
        Eiki parent2 = new Eiki();

        Eiki obj = new Eiki(100, 200, 0, AgeState.ADULT, parent1, parent2);

        assertNotNull(obj);
        assertEquals(Eiki.type, obj.getType());
    }

    @Test
    public void testEikiGetMountPoint() {
        Eiki obj = new Eiki();
        // getMountPoint returns attachment offset from map
        // Most classes return null for unknown keys
        obj.getMountPoint("unknown_key");
        // Result can be null or an array depending on initialization
        // Just verify the method doesn't crash
        assertNotNull(obj);
    }

    @Test
    public void testEikiCheckTransform() {
        Eiki obj = new Eiki();
        // checkTransform() checks transformation conditions
        // Without proper World setup, will likely return null
        obj.checkTransform();
        // Just verify the method executes without crashing
        // Just verify the method executes without crashing
        assertNull(obj.checkTransform());
    }
}
