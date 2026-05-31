package org.simyukkuri.yukkuri;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.simyukkuri.ConstState;
import org.simyukkuri.SimYukkuri;
import org.simyukkuri.entity.core.living.yukkuri.impl.Marisa;
import org.simyukkuri.entity.core.living.yukkuri.impl.Reimu;
import org.simyukkuri.entity.core.living.yukkuri.impl.Remirya;
import org.simyukkuri.enums.AgeState;

public class RemiryaTest {

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
    public void testRemiryaIdentity() {
        Remirya remirya = new Remirya();
        // Verify the Remirya was created with correct type
        assertEquals(Remirya.type, remirya.getType());
        assertEquals("れみりゃ", remirya.getNameJ());
        assertEquals("Remirya", remirya.getNameE());
    }

    @Test
    public void testRemiryaParameterizedConstructor() {
        Remirya parent1 = new Remirya();
        Remirya parent2 = new Remirya();

        Remirya remirya = new Remirya(160, 260, 0, AgeState.ADULT, parent1, parent2);

        assertNotNull(remirya);
        assertEquals(Remirya.type, remirya.getType());
    }

    @Test
    public void testRemiryaNames() {
        Remirya remirya = new Remirya();
        assertEquals("れみりゃ", remirya.getMyName());
        assertEquals("れみりゃ", remirya.getMyNameD());
        assertEquals("", remirya.getNameJ2());
        assertEquals("", remirya.getNameE2());
    }

    @Test
    public void testRemiryaHybridType() {
        Remirya remirya = new Remirya();
        assertEquals(Remirya.type, remirya.getHybridType(Reimu.type));
        assertEquals(Remirya.type, remirya.getHybridType(Marisa.type));
    }

    @Test
    public void testRemiryaTuneParameters() {
        SimYukkuri.RND = new ConstState(6);

        Remirya remirya = new Remirya();
        remirya.tuneParameters();

        // Remirya should be flying type
        assertTrue(remirya.isFlyingType());
        // Remirya should be a predator
        assertNotNull(remirya.getPredatorType());

        // Robustness should be: nextInt(10) + 1 = min(6, 9) + 1 = 6 + 1 = 7
        assertEquals(7, remirya.getImmunityStrength());
    }

    @Test
    public void testRemiryaNagasiMethods() {
        Remirya remirya = new Remirya();
        assertNotNull(remirya.getImageVariantState());

        int[][] testArray = new int[10][2];
        remirya.setImageVariantState(testArray);
        assertSame(testArray, remirya.getImageVariantState());
    }

    @Test
    public void testRemiryaIsHybrid() {
        Remirya remirya = new Remirya();
        assertFalse(remirya.isHybrid());
    }

    @Test
    public void testRemiryaGetMountPoint() {
        Remirya obj = new Remirya();
        // getMountPoint returns attachment offset from map
        // Most classes return null for unknown keys
        obj.getMountPoint("unknown_key");
        // Result can be null or an array depending on initialization
        // Just verify the method doesn't crash
        assertNotNull(obj);
    }

    @Test
    public void testRemiryaCheckTransform() {
        Remirya obj = new Remirya();
        // checkTransform() checks transformation conditions
        // Without proper World setup, will likely return null
        obj.checkTransform();
        // Just verify the method executes without crashing
        // Just verify the method executes without crashing
        assertNull(obj.checkTransform());
    }
}
