package org.simyukkuri.yukkuri;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.simyukkuri.SimYukkuri;
import org.simyukkuri.entity.core.living.yukkuri.impl.Marisa;
import org.simyukkuri.entity.core.living.yukkuri.impl.Myon;
import org.simyukkuri.entity.core.living.yukkuri.impl.Reimu;
import org.simyukkuri.enums.AgeState;

public class MyonTest {

    @Test
    public void testMyonIdentity() {
        Myon myon = new Myon();
        assertEquals(Myon.type, myon.getType());
        assertEquals("みょん", myon.getNameJ());
        assertEquals("Myon", myon.getNameE());
    }

    @Test
    public void testMyonNames() {
        Myon myon = new Myon();
        assertEquals("みょん", myon.getMyName());
        assertEquals("みょん", myon.getMyNameD());
        assertEquals("", myon.getNameJ2());
        assertEquals("", myon.getNameE2());
    }

    @Test
    public void testMyonHybridType() {
        Myon myon = new Myon();
        assertEquals(Myon.type, myon.getHybridType(Reimu.type));
        assertEquals(Myon.type, myon.getHybridType(Marisa.type));
    }

    @Test
    public void testMyonIsHybrid() {
        Myon myon = new Myon();
        assertFalse(myon.isHybrid());
    }

    @Test
    public void testMyonDefaultConstructor() {
        Myon myon = new Myon();
        assertNotNull(myon);
        assertEquals(Myon.type, myon.getType());
    }

    @Test
    public void testMyonParameterizedConstructor() {
        Myon parent1 = new Myon();
        Myon parent2 = new Myon();

        Myon obj = new Myon(100, 200, 0, AgeState.ADULT, parent1, parent2);

        assertNotNull(obj);
        assertEquals(Myon.type, obj.getType());
    }

    @Test
    public void testMyonGetMountPoint() {
        Myon obj = new Myon();
        // getMountPoint returns attachment offset from map
        // Most classes return null for unknown keys
        obj.getMountPoint("unknown_key");
        // Result can be null or an array depending on initialization
        // Just verify the method doesn't crash
        assertNotNull(obj);
    }

    @Test
    public void testMyonCheckTransform() {
        Myon obj = new Myon();
        // checkTransform() checks transformation conditions
        // Without proper World setup, will likely return null
        obj.checkTransform();
        // Just verify the method executes without crashing
        // Just verify the method executes without crashing
        assertNull(obj.checkTransform());
    }
}
