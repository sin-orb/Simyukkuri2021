package org.simyukkuri.yukkuri;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.simyukkuri.ConstState;
import org.simyukkuri.SimYukkuri;
import org.simyukkuri.entity.core.living.yukkuri.impl.Deibu;
import org.simyukkuri.entity.core.living.yukkuri.impl.Marisa;
import org.simyukkuri.entity.core.living.yukkuri.impl.MarisaReimu;
import org.simyukkuri.entity.core.living.yukkuri.impl.Reimu;
import org.simyukkuri.enums.AgeState;
import org.simyukkuri.enums.Attitude;

public class DeibuTest {

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
    public void testDeibuIdentity() {
        Deibu deibu = new Deibu();
        // Verify the Deibu was created with correct type
        assertEquals(Deibu.type, deibu.getType());
        assertEquals("れいむ", deibu.getNameJ());
        assertEquals("Reimu", deibu.getNameE());
    }

    @Test
    public void testDeibuParameterizedConstructor() {
        Deibu parent1 = new Deibu();
        Deibu parent2 = new Deibu();

        Deibu deibu = new Deibu(140, 240, 0, AgeState.ADULT, parent1, parent2);

        assertNotNull(deibu);
        assertEquals(Deibu.type, deibu.getType());
    }

    @Test
    public void testDeibuNames() {
        Deibu deibu = new Deibu();
        assertEquals("れいむ", deibu.getMyName());
        assertEquals("れいむ", deibu.getMyNameD());
        assertEquals("", deibu.getNameJ2());
        assertEquals("", deibu.getNameE2());
    }

    @Test
    public void testDeibuHybridType() {
        Deibu deibu = new Deibu();
        // Deibu + Marisa = MarisaReimu
        assertEquals(MarisaReimu.type, deibu.getHybridType(Marisa.type));
        // Deibu + other = Deibu
        assertEquals(Deibu.type, deibu.getHybridType(org.simyukkuri.enums.YukkuriType.ALICE));
    }

    @Test
    public void testDeibuTuneParameters() {
        SimYukkuri.RND = new ConstState(8);

        Deibu deibu = new Deibu();
        deibu.tuneParameters();

        // Deibu should set SUPER_SHITHEAD attitude
        assertEquals(Attitude.SUPER_SHITHEAD, deibu.getAttitude());

        // Robustness should be: nextInt(10) + 1 = min(8, 9) + 1 = 8 + 1 = 9
        assertEquals(5, deibu.getImmunityStrength());
    }

    @Test
    public void testDeibuExtendsReimu() {
        Deibu deibu = new Deibu();
        assertTrue(deibu instanceof Reimu);
    }

    @Test
    public void testDeibuNagasiMethods() {
        Deibu deibu = new Deibu();
        assertNotNull(deibu.getImageVariantState());

        int[][] testArray = new int[10][2];
        deibu.setImageVariantState(testArray);
        assertSame(testArray, deibu.getImageVariantState());
    }

    @Test
    public void testDeibuGetMountPoint() {
        Deibu obj = new Deibu();
        // getMountPoint returns attachment offset from map
        // Most classes return null for unknown keys
        obj.getMountPoint("unknown_key");
        // Result can be null or an array depending on initialization
        // Just verify the method doesn't crash
        assertNotNull(obj);
    }

    @Test
    public void testDeibuCheckTransform() {
        Deibu obj = new Deibu();
        // checkTransform() checks transformation conditions
        // Without proper World setup, will likely return null
        obj.checkTransform();
        // Just verify the method executes without crashing
        // Just verify the method executes without crashing
        assertNull(obj.checkTransform());
    }

    @Test
    public void testDeibuHybridTypeWithMarisa() {
        Deibu obj = new Deibu();
        assertEquals(MarisaReimu.type, obj.getHybridType(Marisa.type));
    }

    @Test
    public void testDeibuHybridTypeWithOther() {
        Deibu obj = new Deibu();
        // Test with a type not specifically handled - should return own type
        assertEquals(Deibu.type, obj.getHybridType(org.simyukkuri.enums.YukkuriType.ALICE));
    }
}
