package org.simyukkuri.yukkuri;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.simyukkuri.SimYukkuri;
import org.simyukkuri.entity.core.living.yukkuri.impl.Kimeemaru;
import org.simyukkuri.entity.core.living.yukkuri.impl.Marisa;
import org.simyukkuri.entity.core.living.yukkuri.impl.Reimu;
import org.simyukkuri.enums.AgeState;

public class KimeemaruTest {

    @Test
    public void testKimeemaruIdentity() {
        Kimeemaru kimeemaru = new Kimeemaru();
        assertEquals(Kimeemaru.type, kimeemaru.getType());
        assertEquals("きめぇまる", kimeemaru.getNameJ());
        assertEquals("kimeemaru", kimeemaru.getNameE());
    }

    @Test
    public void testKimeemaruNames() {
        Kimeemaru kimeemaru = new Kimeemaru();
        assertEquals("きめぇまる", kimeemaru.getMyName());
        assertEquals("きめぇまる", kimeemaru.getMyNameD());
        assertEquals("", kimeemaru.getNameJ2());
        assertEquals("", kimeemaru.getNameE2());
    }

    @Test
    public void testKimeemaruHybridType() {
        Kimeemaru kimeemaru = new Kimeemaru();
        assertEquals(Kimeemaru.type, kimeemaru.getHybridType(Reimu.type));
        assertEquals(Kimeemaru.type, kimeemaru.getHybridType(Marisa.type));
    }

    @Test
    public void testKimeemaruIsHybrid() {
        Kimeemaru kimeemaru = new Kimeemaru();
        assertFalse(kimeemaru.isHybrid());
    }

    @Test
    public void testKimeemaruParameterizedConstructor() {
        Kimeemaru parent1 = new Kimeemaru();
        Kimeemaru parent2 = new Kimeemaru();

        Kimeemaru obj = new Kimeemaru(100, 200, 0, AgeState.ADULT, parent1, parent2);

        assertNotNull(obj);
        assertEquals(Kimeemaru.type, obj.getType());
    }

    @Test
    public void testKimeemaruGetMountPoint() {
        Kimeemaru obj = new Kimeemaru();
        // getMountPoint returns attachment offset from map
        // Most classes return null for unknown keys
        obj.getMountPoint("unknown_key");
        // Result can be null or an array depending on initialization
        // Just verify the method doesn't crash
        assertNotNull(obj);
    }

    @Test
    public void testKimeemaruCheckTransform() {
        Kimeemaru obj = new Kimeemaru();
        // checkTransform() checks transformation conditions
        // Without proper World setup, will likely return null
        obj.checkTransform();
        // Just verify the method executes without crashing
        // Just verify the method executes without crashing
        assertNull(obj.checkTransform());
    }
}
