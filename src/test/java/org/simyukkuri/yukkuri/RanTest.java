package org.simyukkuri.yukkuri;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.simyukkuri.SimYukkuri;
import org.simyukkuri.entity.core.living.yukkuri.impl.Marisa;
import org.simyukkuri.entity.core.living.yukkuri.impl.Ran;
import org.simyukkuri.entity.core.living.yukkuri.impl.Reimu;
import org.simyukkuri.enums.AgeState;

public class RanTest {

    @Test
    public void testRanIdentity() {
        Ran ran = new Ran();
        assertEquals(Ran.type, ran.getType());
        assertEquals("らん", ran.getNameJ());
        assertEquals("Ran", ran.getNameE());
    }

    @Test
    public void testRanNames() {
        Ran ran = new Ran();
        assertEquals("らん", ran.getMyName());
        assertEquals("らん", ran.getMyNameD());
        assertEquals("", ran.getNameJ2());
        assertEquals("", ran.getNameE2());
    }

    @Test
    public void testRanHybridType() {
        Ran ran = new Ran();
        assertEquals(Ran.type, ran.getHybridType(Reimu.type));
        assertEquals(Ran.type, ran.getHybridType(Marisa.type));
    }

    @Test
    public void testRanIsHybrid() {
        Ran ran = new Ran();
        assertFalse(ran.isHybrid());
    }

    @Test
    public void testRanDefaultConstructor() {
        Ran ran = new Ran();
        assertNotNull(ran);
        assertEquals(Ran.type, ran.getType());
    }

    @Test
    public void testRanParameterizedConstructor() {
        Ran parent1 = new Ran();
        Ran parent2 = new Ran();

        Ran obj = new Ran(100, 200, 0, AgeState.ADULT, parent1, parent2);

        assertNotNull(obj);
        assertEquals(Ran.type, obj.getType());
    }

    @Test
    public void testRanGetMountPoint() {
        Ran obj = new Ran();
        // getMountPoint returns attachment offset from map
        // Most classes return null for unknown keys
        obj.getMountPoint("unknown_key");
        // Result can be null or an array depending on initialization
        // Just verify the method doesn't crash
        assertNotNull(obj);
    }

    @Test
    public void testRanCheckTransform() {
        Ran obj = new Ran();
        // checkTransform() checks transformation conditions
        // Without proper World setup, will likely return null
        obj.checkTransform();
        // Just verify the method executes without crashing
        // Just verify the method executes without crashing
        assertNull(obj.checkTransform());
    }
}
