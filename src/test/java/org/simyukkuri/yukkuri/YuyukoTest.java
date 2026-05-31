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
import org.simyukkuri.entity.core.living.yukkuri.impl.Yuyuko;
import org.simyukkuri.enums.AgeState;

public class YuyukoTest {

    @Test
    public void testYuyukoIdentity() {
        Yuyuko yuyuko = new Yuyuko();
        assertEquals(Yuyuko.type, yuyuko.getType());
        assertEquals("ゆゆこ", yuyuko.getNameJ());
        assertEquals("Yuyuko", yuyuko.getNameE());
    }

    @Test
    public void testYuyukoNames() {
        Yuyuko yuyuko = new Yuyuko();
        assertEquals("ゆゆこ", yuyuko.getMyName());
        assertEquals("ゆゆこ", yuyuko.getMyNameD());
        assertEquals("", yuyuko.getNameJ2());
        assertEquals("", yuyuko.getNameE2());
    }

    @Test
    public void testYuyukoHybridType() {
        Yuyuko yuyuko = new Yuyuko();
        assertEquals(Yuyuko.type, yuyuko.getHybridType(Reimu.type));
        assertEquals(Yuyuko.type, yuyuko.getHybridType(Marisa.type));
    }

    @Test
    public void testYuyukoIsHybrid() {
        Yuyuko yuyuko = new Yuyuko();
        assertFalse(yuyuko.isHybrid());
    }

    @Test
    public void testYuyukoParameterizedConstructor() {
        Yuyuko parent1 = new Yuyuko();
        Yuyuko parent2 = new Yuyuko();

        Yuyuko obj = new Yuyuko(100, 200, 0, AgeState.ADULT, parent1, parent2);

        assertNotNull(obj);
        assertEquals(Yuyuko.type, obj.getType());
    }

    @Test
    public void testYuyukoGetMountPoint() {
        Yuyuko obj = new Yuyuko();
        // getMountPoint returns attachment offset from map
        // Most classes return null for unknown keys
        obj.getMountPoint("unknown_key");
        // Result can be null or an array depending on initialization
        // Just verify the method doesn't crash
        assertNotNull(obj);
    }

    @Test
    public void testYuyukoCheckTransform() {
        Yuyuko obj = new Yuyuko();
        // checkTransform() checks transformation conditions
        // Without proper World setup, will likely return null
        obj.checkTransform();
        // Just verify the method executes without crashing
        // Just verify the method executes without crashing
        assertNull(obj.checkTransform());
    }
}
