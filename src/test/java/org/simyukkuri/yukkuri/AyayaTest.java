package org.simyukkuri.yukkuri;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.simyukkuri.SimYukkuri;
import org.simyukkuri.entity.core.living.yukkuri.impl.Ayaya;
import org.simyukkuri.entity.core.living.yukkuri.impl.Marisa;
import org.simyukkuri.entity.core.living.yukkuri.impl.Reimu;
import org.simyukkuri.enums.AgeState;

public class AyayaTest {

    @Test
    public void testAyayaIdentity() {
        Ayaya ayaya = new Ayaya();
        assertEquals(Ayaya.type, ayaya.getType());
        assertEquals("あや", ayaya.getNameJ());
        assertEquals("Ayaya", ayaya.getNameE());
    }

    @Test
    public void testAyayaNames() {
        Ayaya ayaya = new Ayaya();
        assertEquals("あや", ayaya.getMyName());
        assertEquals("あや", ayaya.getMyNameD());
        assertEquals("", ayaya.getNameJ2());
        assertEquals("", ayaya.getNameE2());
    }

    @Test
    public void testAyayaHybridType() {
        Ayaya ayaya = new Ayaya();
        // Ayaya always returns Ayaya type
        assertEquals(Ayaya.type, ayaya.getHybridType(Reimu.type));
        assertEquals(Ayaya.type, ayaya.getHybridType(Marisa.type));
    }

    @Test
    public void testAyayaIsHybrid() {
        Ayaya ayaya = new Ayaya();
        assertFalse(ayaya.isHybrid());
    }

    @Test
    public void testAyayaDefaultConstructor() {
        Ayaya ayaya = new Ayaya();
        assertNotNull(ayaya);
        assertEquals(Ayaya.type, ayaya.getType());
    }

    @Test
    public void testAyayaParameterizedConstructor() {
        Ayaya parent1 = new Ayaya();
        Ayaya parent2 = new Ayaya();

        Ayaya obj = new Ayaya(100, 200, 0, AgeState.ADULT, parent1, parent2);

        assertNotNull(obj);
        assertEquals(Ayaya.type, obj.getType());
    }

    @Test
    public void testAyayaGetMountPoint() {
        Ayaya obj = new Ayaya();
        // getMountPoint returns attachment offset from map
        // Most classes return null for unknown keys
        obj.getMountPoint("unknown_key");
        // Result can be null or an array depending on initialization
        // Just verify the method doesn't crash
        assertNotNull(obj);
    }

    @Test
    public void testAyayaCheckTransform() {
        Ayaya obj = new Ayaya();
        // checkTransform() checks transformation conditions
        // Without proper World setup, will likely return null
        obj.checkTransform();
        // Just verify the method executes without crashing
        // Just verify the method executes without crashing
        assertNull(obj.checkTransform());
    }
}
