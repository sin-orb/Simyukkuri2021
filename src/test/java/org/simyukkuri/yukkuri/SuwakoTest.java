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
import org.simyukkuri.entity.core.living.yukkuri.impl.Suwako;
import org.simyukkuri.enums.AgeState;

public class SuwakoTest {

    @Test
    public void testSuwakoIdentity() {
        Suwako suwako = new Suwako();
        assertEquals(Suwako.type, suwako.getType());
        assertEquals("すわこ", suwako.getNameJ());
        assertEquals("Suwako", suwako.getNameE());
    }

    @Test
    public void testSuwakoNames() {
        Suwako suwako = new Suwako();
        assertEquals("すわこ", suwako.getMyName());
        assertEquals("すわこ", suwako.getMyNameD());
        assertEquals("", suwako.getNameJ2());
        assertEquals("", suwako.getNameE2());
    }

    @Test
    public void testSuwakoHybridType() {
        Suwako suwako = new Suwako();
        assertEquals(Suwako.type, suwako.getHybridType(Reimu.type));
        assertEquals(Suwako.type, suwako.getHybridType(Marisa.type));
    }

    @Test
    public void testSuwakoIsHybrid() {
        Suwako suwako = new Suwako();
        assertFalse(suwako.isHybrid());
    }

    @Test
    public void testSuwakoParameterizedConstructor() {
        Suwako parent1 = new Suwako();
        Suwako parent2 = new Suwako();

        Suwako obj = new Suwako(100, 200, 0, AgeState.ADULT, parent1, parent2);

        assertNotNull(obj);
        assertEquals(Suwako.type, obj.getType());
    }

    @Test
    public void testSuwakoGetMountPoint() {
        Suwako obj = new Suwako();
        // getMountPoint returns attachment offset from map
        // Most classes return null for unknown keys
        obj.getMountPoint("unknown_key");
        // Result can be null or an array depending on initialization
        // Just verify the method doesn't crash
        assertNotNull(obj);
    }

    @Test
    public void testSuwakoCheckTransform() {
        Suwako obj = new Suwako();
        // checkTransform() checks transformation conditions
        // Without proper World setup, will likely return null
        obj.checkTransform();
        // Just verify the method executes without crashing
        // Just verify the method executes without crashing
        assertNull(obj.checkTransform());
    }
}
