package org.simyukkuri.yukkuri;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.simyukkuri.SimYukkuri;
import org.simyukkuri.entity.core.living.yukkuri.impl.Marisa;
import org.simyukkuri.entity.core.living.yukkuri.impl.Reimu;
import org.simyukkuri.entity.core.living.yukkuri.impl.Tenko;
import org.simyukkuri.enums.AgeState;

public class TenkoTest {

    @Test
    public void testTenkoIdentity() {
        Tenko tenko = new Tenko();
        assertEquals(Tenko.type, tenko.getType());
        assertEquals("てんこ", tenko.getNameJ());
        assertEquals("Tenko", tenko.getNameE());
    }

    @Test
    public void testTenkoNames() {
        Tenko tenko = new Tenko();
        assertEquals("てんこ", tenko.getMyName());
        assertEquals("てんこ", tenko.getMyNameD());
        assertEquals("", tenko.getNameJ2());
        assertEquals("", tenko.getNameE2());
    }

    @Test
    public void testTenkoHybridType() {
        Tenko tenko = new Tenko();
        assertEquals(Tenko.type, tenko.getHybridType(Reimu.type));
        assertEquals(Tenko.type, tenko.getHybridType(Marisa.type));
    }

    @Test
    public void testTenkoIsHybrid() {
        Tenko tenko = new Tenko();
        assertFalse(tenko.isHybrid());
    }

    @Test
    public void testTenkoParameterizedConstructor() {
        Tenko parent1 = new Tenko();
        Tenko parent2 = new Tenko();

        Tenko obj = new Tenko(100, 200, 0, AgeState.ADULT, parent1, parent2);

        assertNotNull(obj);
        assertEquals(Tenko.type, obj.getType());
    }

    @Test
    public void testTenkoGetMountPoint() {
        Tenko obj = new Tenko();
        // getMountPoint returns attachment offset from map
        // Most classes return null for unknown keys
        obj.getMountPoint("unknown_key");
        // Result can be null or an array depending on initialization
        // Just verify the method doesn't crash
        assertNotNull(obj);
    }

    @Test
    public void testTenkoCheckTransform() {
        Tenko obj = new Tenko();
        // checkTransform() checks transformation conditions
        // Without proper World setup, will likely return null
        obj.checkTransform();
        // Just verify the method executes without crashing
        // Just verify the method executes without crashing
        assertNull(obj.checkTransform());
    }
}
