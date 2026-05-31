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
import org.simyukkuri.entity.core.living.yukkuri.impl.Meirin;
import org.simyukkuri.entity.core.living.yukkuri.impl.Reimu;
import org.simyukkuri.entity.core.living.yukkuri.impl.Remirya;
import org.simyukkuri.enums.AgeState;

public class MeirinTest {

    @Test
    public void testMeirinIdentity() {
        Meirin meirin = new Meirin();
        assertEquals(Meirin.type, meirin.getType());
        assertEquals("めーりん", meirin.getNameJ());
        assertEquals("Merin", meirin.getNameE());
    }

    @Test
    public void testMeirinNames() {
        Meirin meirin = new Meirin();
        assertEquals("めーりん", meirin.getMyName());
        assertEquals("めーりん", meirin.getMyNameD());
        assertEquals("", meirin.getNameJ2());
        assertEquals("", meirin.getNameE2());
    }

    @Test
    public void testMeirinHybridType() {
        Meirin meirin = new Meirin();
        assertEquals(Meirin.type, meirin.getHybridType(Reimu.type));
        assertEquals(Meirin.type, meirin.getHybridType(Marisa.type));
    }

    @Test
    public void testMeirinIsHybrid() {
        Meirin meirin = new Meirin();
        assertFalse(meirin.isHybrid());
    }

    @Test
    public void testMeirinIsServantOfPredatorMasters() {
        Meirin meirin = new Meirin();
        assertTrue(meirin.isServantOf(Remirya.type));
        assertTrue(meirin.isServantOf(Fran.type));
        assertFalse(meirin.isServantOf(Marisa.type));
    }

    @Test
    public void testMeirinParameterizedConstructor() {
        Meirin parent1 = new Meirin();
        Meirin parent2 = new Meirin();

        Meirin obj = new Meirin(100, 200, 0, AgeState.ADULT, parent1, parent2);

        assertNotNull(obj);
        assertEquals(Meirin.type, obj.getType());
    }

    @Test
    public void testMeirinGetMountPoint() {
        Meirin obj = new Meirin();
        // getMountPoint returns attachment offset from map
        // Most classes return null for unknown keys
        obj.getMountPoint("unknown_key");
        // Result can be null or an array depending on initialization
        // Just verify the method doesn't crash
        assertNotNull(obj);
    }

    @Test
    public void testMeirinCheckTransform() {
        Meirin obj = new Meirin();
        // checkTransform() checks transformation conditions
        // Without proper World setup, will likely return null
        obj.checkTransform();
        // Just verify the method executes without crashing
        // Just verify the method executes without crashing
        assertNull(obj.checkTransform());
    }
}
