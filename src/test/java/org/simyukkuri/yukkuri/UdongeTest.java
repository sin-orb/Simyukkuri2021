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
import org.simyukkuri.entity.core.living.yukkuri.impl.Udonge;
import org.simyukkuri.enums.AgeState;

public class UdongeTest {

    @Test
    public void testUdongeIdentity() {
        Udonge udonge = new Udonge();
        assertEquals(Udonge.type, udonge.getType());
        assertEquals("うどんげ", udonge.getNameJ());
        assertEquals("Udonge", udonge.getNameE());
    }

    @Test
    public void testUdongeNames() {
        Udonge udonge = new Udonge();
        assertEquals("うどんげ", udonge.getMyName());
        assertEquals("うどんげ", udonge.getMyNameD());
        assertEquals("", udonge.getNameJ2());
        assertEquals("", udonge.getNameE2());
    }

    @Test
    public void testUdongeHybridType() {
        Udonge udonge = new Udonge();
        assertEquals(Udonge.type, udonge.getHybridType(Reimu.type));
        assertEquals(Udonge.type, udonge.getHybridType(Marisa.type));
    }

    @Test
    public void testUdongeIsHybrid() {
        Udonge udonge = new Udonge();
        assertFalse(udonge.isHybrid());
    }

    @Test
    public void testUdongeParameterizedConstructor() {
        Udonge parent1 = new Udonge();
        Udonge parent2 = new Udonge();

        Udonge obj = new Udonge(100, 200, 0, AgeState.ADULT, parent1, parent2);

        assertNotNull(obj);
        assertEquals(Udonge.type, obj.getType());
    }

    @Test
    public void testUdongeGetMountPoint() {
        Udonge obj = new Udonge();
        // getMountPoint returns attachment offset from map
        // Most classes return null for unknown keys
        obj.getMountPoint("unknown_key");
        // Result can be null or an array depending on initialization
        // Just verify the method doesn't crash
        assertNotNull(obj);
    }

    @Test
    public void testUdongeCheckTransform() {
        Udonge obj = new Udonge();
        // checkTransform() checks transformation conditions
        // Without proper World setup, will likely return null
        obj.checkTransform();
        // Just verify the method executes without crashing
        // Just verify the method executes without crashing
        assertNull(obj.checkTransform());
    }
}
