package org.simyukkuri.yukkuri;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.simyukkuri.ConstState;
import org.simyukkuri.SimYukkuri;
import org.simyukkuri.entity.core.living.yukkuri.impl.DosMarisa;
import org.simyukkuri.entity.core.living.yukkuri.impl.Marisa;
import org.simyukkuri.entity.core.living.yukkuri.impl.Reimu;
import org.simyukkuri.entity.core.living.yukkuri.impl.ReimuMarisa;
import org.simyukkuri.entity.core.living.yukkuri.impl.WasaReimu;
import org.simyukkuri.enums.AgeState;

public class DosMarisaTest {

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
    public void testDosMarisaIdentity() {
        DosMarisa dosMarisa = new DosMarisa();
        assertEquals(DosMarisa.type, dosMarisa.getType());
        assertEquals("ドスまりさ", dosMarisa.getNameJ());
        assertEquals("DosMarisa", dosMarisa.getNameE());
    }

    @Test
    public void testDosMarisaNames() {
        DosMarisa dosMarisa = new DosMarisa();
        assertEquals("ドスまりさ", dosMarisa.getMyName());
        assertEquals("ドスまりさ", dosMarisa.getMyNameD());
        assertEquals("", dosMarisa.getNameJ2());
        assertEquals("", dosMarisa.getNameE2());
    }

    @Test
    public void testDosMarisaHybridType() {
        DosMarisa dosMarisa = new DosMarisa();
        // DosMarisa + Reimu = ReimuMarisa
        assertEquals(ReimuMarisa.type, dosMarisa.getHybridType(Reimu.type));
        // DosMarisa + WasaReimu = ReimuMarisa
        assertEquals(ReimuMarisa.type, dosMarisa.getHybridType(WasaReimu.type));
        // DosMarisa + other = DosMarisa
        assertEquals(
                DosMarisa.type, dosMarisa.getHybridType(org.simyukkuri.enums.YukkuriType.ALICE));
    }

    @Test
    public void testDosMarisaExtendsMarisa() {
        DosMarisa dosMarisa = new DosMarisa();
        assertTrue(dosMarisa instanceof Marisa);
    }

    @Test
    public void testDosMarisaTuneParameters() {
        SimYukkuri.RND = new ConstState(7);

        DosMarisa dosMarisa = new DosMarisa();
        dosMarisa.tuneParameters();

        // DosMarisa has extreme multipliers in tuneParameters
        // Robustness should be: nextInt(10) + 1 = min(7, 9) + 1 = 7 + 1 = 8
        assertEquals(8, dosMarisa.getImmunityStrength());

        // sameDirectionFactor should be: nextInt(10) + 10 = min(7, 9) + 10 = 7 + 10 =
        // 17
        assertEquals(17, dosMarisa.getSameDirectionFactor());
    }

    @Test
    public void testDosMarisaParameterizedConstructor() {
        DosMarisa parent1 = new DosMarisa();
        DosMarisa parent2 = new DosMarisa();

        DosMarisa dosMarisa = new DosMarisa(130, 230, 0, AgeState.ADULT, parent1, parent2);

        assertNotNull(dosMarisa);
        assertEquals(DosMarisa.type, dosMarisa.getType());
    }

    @Test
    public void testDosMarisaGetMountPoint() {
        DosMarisa obj = new DosMarisa();
        // getMountPoint returns attachment offset from map
        // Most classes return null for unknown keys
        obj.getMountPoint("unknown_key");
        // Result can be null or an array depending on initialization
        // Just verify the method doesn't crash
        assertNotNull(obj);
    }

    @Test
    public void testDosMarisaCheckTransform() {
        org.simyukkuri.util.WorldTestHelper.resetWorld();
        org.simyukkuri.util.WorldTestHelper.initializeMinimalWorld();
        DosMarisa obj = new DosMarisa();
        assertNull(obj.checkTransform());
    }

    @Test
    public void testDosMarisaHybridTypeWithReimu() {
        DosMarisa obj = new DosMarisa();
        assertEquals(ReimuMarisa.type, obj.getHybridType(Reimu.type));
    }

    @Test
    public void testDosMarisaHybridTypeWithWasaReimu() {
        DosMarisa obj = new DosMarisa();
        assertEquals(ReimuMarisa.type, obj.getHybridType(WasaReimu.type));
    }

    @Test
    public void testDosMarisaHybridTypeWithOther() {
        DosMarisa obj = new DosMarisa();
        // Test with a type not specifically handled - should return own type
        assertEquals(DosMarisa.type, obj.getHybridType(org.simyukkuri.enums.YukkuriType.ALICE));
    }

}
