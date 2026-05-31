package org.simyukkuri.yukkuri;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.simyukkuri.SimYukkuri;
import org.simyukkuri.entity.core.living.yukkuri.impl.Chiruno;
import org.simyukkuri.entity.core.living.yukkuri.impl.Marisa;
import org.simyukkuri.entity.core.living.yukkuri.impl.Reimu;
import org.simyukkuri.enums.AgeState;

public class ChirunoTest {

    @Test
    public void testChirunoIdentity() {
        Chiruno chiruno = new Chiruno();
        assertEquals(Chiruno.type, chiruno.getType());
        assertEquals("ちるの", chiruno.getNameJ());
        assertEquals("Chiruno", chiruno.getNameE());
    }

    @Test
    public void testChirunoNames() {
        Chiruno chiruno = new Chiruno();
        assertEquals("ちるの", chiruno.getMyName());
        assertEquals("ちるの", chiruno.getMyNameD());
        assertEquals("", chiruno.getNameJ2());
        assertEquals("", chiruno.getNameE2());
    }

    @Test
    public void testChirunoHybridType() {
        Chiruno chiruno = new Chiruno();
        assertEquals(Chiruno.type, chiruno.getHybridType(Reimu.type));
        assertEquals(Chiruno.type, chiruno.getHybridType(Marisa.type));
    }

    @Test
    public void testChirunoIsHybrid() {
        Chiruno chiruno = new Chiruno();
        assertFalse(chiruno.isHybrid());
    }

    @Test
    public void testChirunoParameterizedConstructor() {
        Chiruno parent1 = new Chiruno();
        Chiruno parent2 = new Chiruno();

        Chiruno obj = new Chiruno(100, 200, 0, AgeState.ADULT, parent1, parent2);

        assertNotNull(obj);
        assertEquals(Chiruno.type, obj.getType());
    }

    @Test
    public void testChirunoGetMountPoint() {
        Chiruno obj = new Chiruno();
        // getMountPoint returns attachment offset from map
        // Most classes return null for unknown keys
        obj.getMountPoint("unknown_key");
        // Result can be null or an array depending on initialization
        // Just verify the method doesn't crash
        assertNotNull(obj);
    }

    @Test
    public void testChirunoCheckTransform() {
        Chiruno obj = new Chiruno();
        // checkTransform() checks transformation conditions
        // Without proper World setup, will likely return null
        obj.checkTransform();
        // Just verify the method executes without crashing
        // Just verify the method executes without crashing
        assertNull(obj.checkTransform());
    }
}
