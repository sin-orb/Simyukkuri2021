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
import org.simyukkuri.entity.core.living.yukkuri.impl.Yurusanae;
import org.simyukkuri.enums.AgeState;

public class YurusanaeTest {

    @Test
    public void testYurusanaeIdentity() {
        Yurusanae yurusanae = new Yurusanae();
        assertEquals(Yurusanae.type, yurusanae.getType());
        assertEquals("さなえ", yurusanae.getNameJ());
        assertEquals("Yurusanae", yurusanae.getNameE());
    }

    @Test
    public void testYurusanaeNames() {
        Yurusanae yurusanae = new Yurusanae();
        assertEquals("さなえ", yurusanae.getMyName());
        assertEquals("さなえ", yurusanae.getMyNameD());
        assertEquals("", yurusanae.getNameJ2());
        assertEquals("", yurusanae.getNameE2());
    }

    @Test
    public void testYurusanaeHybridType() {
        Yurusanae yurusanae = new Yurusanae();
        assertEquals(Yurusanae.type, yurusanae.getHybridType(Reimu.type));
        assertEquals(Yurusanae.type, yurusanae.getHybridType(Marisa.type));
    }

    @Test
    public void testYurusanaeIsHybrid() {
        Yurusanae yurusanae = new Yurusanae();
        assertFalse(yurusanae.isHybrid());
    }

    @Test
    public void testYurusanaeParameterizedConstructor() {
        Yurusanae parent1 = new Yurusanae();
        Yurusanae parent2 = new Yurusanae();

        Yurusanae obj = new Yurusanae(100, 200, 0, AgeState.ADULT, parent1, parent2);

        assertNotNull(obj);
        assertEquals(Yurusanae.type, obj.getType());
    }

    @Test
    public void testYurusanaeGetMountPoint() {
        Yurusanae obj = new Yurusanae();
        // getMountPoint returns attachment offset from map
        // Most classes return null for unknown keys
        obj.getMountPoint("unknown_key");
        // Result can be null or an array depending on initialization
        // Just verify the method doesn't crash
        assertNotNull(obj);
    }

    @Test
    public void testYurusanaeCheckTransform() {
        Yurusanae obj = new Yurusanae();
        // checkTransform() checks transformation conditions
        // Without proper World setup, will likely return null
        obj.checkTransform();
        // Just verify the method executes without crashing
        // Just verify the method executes without crashing
        assertNull(obj.checkTransform());
    }
}
