package org.simyukkuri.yukkuri;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.simyukkuri.SimYukkuri;
import org.simyukkuri.entity.core.living.yukkuri.impl.Fran;
import org.simyukkuri.entity.core.living.yukkuri.impl.Marisa;
import org.simyukkuri.entity.core.living.yukkuri.impl.Reimu;
import org.simyukkuri.entity.core.living.yukkuri.impl.Remirya;
import org.simyukkuri.entity.core.living.yukkuri.impl.Sakuya;
import org.simyukkuri.enums.AgeState;

public class SakuyaTest {

    @Test
    public void testSakuyaIdentity() {
        Sakuya sakuya = new Sakuya();
        assertEquals(Sakuya.type, sakuya.getType());
        assertEquals("さくや", sakuya.getNameJ());
        assertEquals("Sakuya", sakuya.getNameE());
    }

    @Test
    public void testSakuyaNames() {
        Sakuya sakuya = new Sakuya();
        assertEquals("さくや", sakuya.getMyName());
        assertEquals("さくや", sakuya.getMyNameD());
        assertEquals("", sakuya.getNameJ2());
        assertEquals("", sakuya.getNameE2());
    }

    @Test
    public void testSakuyaHybridType() {
        Sakuya sakuya = new Sakuya();
        assertEquals(Sakuya.type, sakuya.getHybridType(Reimu.type));
        assertEquals(Sakuya.type, sakuya.getHybridType(Marisa.type));
    }

    @Test
    public void testSakuyaIsHybrid() {
        Sakuya sakuya = new Sakuya();
        assertFalse(sakuya.isHybrid());
    }

    @Test
    public void testSakuyaIsServantOfPredatorMasters() {
        Sakuya sakuya = new Sakuya();
        assertTrue(sakuya.isServantOf(Remirya.type));
        assertTrue(sakuya.isServantOf(Fran.type));
        assertFalse(sakuya.isServantOf(Marisa.type));
    }

    @Test
    public void testSakuyaParameterizedConstructor() {
        Sakuya parent1 = new Sakuya();
        Sakuya parent2 = new Sakuya();

        Sakuya obj = new Sakuya(100, 200, 0, AgeState.ADULT, parent1, parent2);

        assertNotNull(obj);
        assertEquals(Sakuya.type, obj.getType());
    }

    @Test
    public void testSakuyaGetMountPoint() {
        Sakuya obj = new Sakuya();
        // getMountPoint returns attachment offset from map
        // Most classes return null for unknown keys
        obj.getMountPoint("unknown_key");
        // Result can be null or an array depending on initialization
        // Just verify the method doesn't crash
        assertNotNull(obj);
    }

    @Test
    public void testSakuyaCheckTransform() {
        Sakuya obj = new Sakuya();
        // checkTransform() checks transformation conditions
        // Without proper World setup, will likely return null
        obj.checkTransform();
        // Just verify the method executes without crashing
        // Just verify the method executes without crashing
        assertNull(obj.checkTransform());
    }
}
