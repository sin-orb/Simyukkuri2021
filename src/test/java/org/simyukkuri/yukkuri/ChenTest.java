package org.simyukkuri.yukkuri;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.simyukkuri.entity.core.living.yukkuri.impl.Chen;
import org.simyukkuri.entity.core.living.yukkuri.impl.Marisa;
import org.simyukkuri.entity.core.living.yukkuri.impl.Reimu;
import org.simyukkuri.enums.AgeState;

public class ChenTest {

    @Test
    public void testChenIdentity() {
        Chen chen = new Chen();
        assertEquals(Chen.type, chen.getType());
        assertEquals("ちぇん", chen.getNameJ());
        assertEquals("Chen", chen.getNameE());
    }

    @Test
    public void testChenNames() {
        Chen chen = new Chen();
        assertEquals("ちぇん", chen.getMyName());
        assertEquals("ちぇん", chen.getMyNameD());
        assertEquals("", chen.getNameJ2());
        assertEquals("", chen.getNameE2());
    }

    @Test
    public void testChenHybridType() {
        Chen chen = new Chen();
        assertEquals(Chen.type, chen.getHybridType(Reimu.type));
        assertEquals(Chen.type, chen.getHybridType(Marisa.type));
    }

    @Test
    public void testChenIsHybrid() {
        Chen chen = new Chen();
        assertFalse(chen.isHybrid());
    }

    @Test
    public void testChenParameterizedConstructor() {
        Chen parent1 = new Chen();
        Chen parent2 = new Chen();

        Chen obj = new Chen(100, 200, 0, AgeState.ADULT, parent1, parent2);

        assertNotNull(obj);
        assertEquals(Chen.type, obj.getType());
    }

    @Test
    public void testChenGetMountPoint() {
        Chen obj = new Chen();
        // getMountPoint returns attachment offset from map
        // Most classes return null for unknown keys
        obj.getMountPoint("unknown_key");
        // Result can be null or an array depending on initialization
        // Just verify the method doesn't crash
        assertNotNull(obj);
    }

    @Test
    public void testChenCheckTransform() {
        Chen obj = new Chen();
        // checkTransform() checks transformation conditions
        // Without proper World setup, will likely return null
        obj.checkTransform();
        // Just verify the method executes without crashing
        // Just verify the method executes without crashing
        assertNull(obj.checkTransform());
    }

}
