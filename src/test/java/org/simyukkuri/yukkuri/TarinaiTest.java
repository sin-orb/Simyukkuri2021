package org.simyukkuri.yukkuri;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.simyukkuri.ConstState;
import org.simyukkuri.SimYukkuri;
import org.simyukkuri.entity.core.living.yukkuri.impl.Marisa;
import org.simyukkuri.entity.core.living.yukkuri.impl.MarisaReimu;
import org.simyukkuri.entity.core.living.yukkuri.impl.Tarinai;
import org.simyukkuri.enums.AgeState;
import org.simyukkuri.enums.Attitude;

public class TarinaiTest {

    private java.util.Random originalRnd;

    @BeforeEach
    public void setUp() {
        originalRnd = SimYukkuri.RND;
    }

    @AfterEach
    public void tearDown() {
        SimYukkuri.RND = originalRnd;
    }

    @Test
    public void testTarinaiIdentity() {
        Tarinai tarinai = new Tarinai();
        assertEquals(Tarinai.type, tarinai.getType());
        assertEquals("たりないゆ", tarinai.getNameJ());
        assertEquals("Tarinaiyu", tarinai.getNameE());
    }

    @Test
    public void testTarinaiNames() {
        Tarinai tarinai = new Tarinai();
        assertEquals("たりないゆ", tarinai.getMyName());
        assertEquals("たりないゆ", tarinai.getMyNameD());
        assertEquals("", tarinai.getNameJ2());
        assertEquals("", tarinai.getNameE2());
    }

    @Test
    public void testTarinaiHybridType() {
        Tarinai tarinai = new Tarinai();
        // Tarinai + Marisa = MarisaReimu
        assertEquals(MarisaReimu.type, tarinai.getHybridType(Marisa.type));
        // Tarinai + other = Tarinai
        assertEquals(Tarinai.type, tarinai.getHybridType(org.simyukkuri.enums.YukkuriType.ALICE));
    }

    @Test
    public void testTarinaiIsIdiot() {
        Tarinai tarinai = new Tarinai();
        assertTrue(tarinai.isIdiot());
    }

    @Test
    public void testTarinaiTuneParameters() {
        SimYukkuri.RND = new ConstState(10);

        Tarinai tarinai = new Tarinai();
        tarinai.tuneParameters();

        // Tarinai should have no okazari
        assertNull(tarinai.getOkazaris());
        // Tarinai should have SUPER_SHITHEAD attitude
        assertEquals(Attitude.SUPER_SHITHEAD, tarinai.getAttitude());
        // Tarinai should have no braid
        assertFalse(tarinai.isBraidType());

        // Robustness should be: nextInt(5) + 1 = min(10, 4) + 1 = 4 + 1 = 5
        assertEquals(5, tarinai.getImmunityStrength());
    }

    @Test
    public void testTarinaiDefaultConstructor() {
        Tarinai tarinai = new Tarinai();
        assertNotNull(tarinai);
        assertEquals(Tarinai.type, tarinai.getType());
    }

    @Test
    public void testTarinaiIsNotHybrid() {
        Tarinai tarinai = new Tarinai();
        assertFalse(tarinai.isHybrid());
    }

    @Test
    public void testTarinaiParameterizedConstructor() {
        Tarinai parent1 = new Tarinai();
        Tarinai parent2 = new Tarinai();

        Tarinai obj = new Tarinai(100, 200, 0, AgeState.ADULT, parent1, parent2);

        assertNotNull(obj);
        assertEquals(Tarinai.type, obj.getType());
    }

    @Test
    public void testTarinaiGetMountPoint() {
        Tarinai obj = new Tarinai();
        // getMountPoint returns attachment offset from map
        // Most classes return null for unknown keys
        obj.getMountPoint("unknown_key");
        // Result can be null or an array depending on initialization
        // Just verify the method doesn't crash
        assertNotNull(obj);
    }

    @Test
    public void testTarinaiCheckTransform() {
        Tarinai obj = new Tarinai();
        // checkTransform() checks transformation conditions
        // Without proper World setup, will likely return null
        obj.checkTransform();
        // Just verify the method executes without crashing
        // Just verify the method executes without crashing
        assertNull(obj.checkTransform());
    }
}
