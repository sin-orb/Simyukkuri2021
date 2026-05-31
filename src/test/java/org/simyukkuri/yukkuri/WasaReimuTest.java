package org.simyukkuri.yukkuri;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.simyukkuri.SimYukkuri;
import org.simyukkuri.entity.core.living.yukkuri.impl.Marisa;
import org.simyukkuri.entity.core.living.yukkuri.impl.MarisaReimu;
import org.simyukkuri.entity.core.living.yukkuri.impl.Reimu;
import org.simyukkuri.entity.core.living.yukkuri.impl.WasaReimu;
import org.simyukkuri.enums.AgeState;

public class WasaReimuTest {

    @Test
    public void testWasaReimuIsHybrid() {
        WasaReimu wasaReimu = new WasaReimu();
        assertFalse(wasaReimu.isHybrid());
    }

    @Test
    public void testWasaReimuDefaultConstructor() {
        WasaReimu wasaReimu = new WasaReimu();
        assertNotNull(wasaReimu);
        assertEquals(WasaReimu.type, wasaReimu.getType());
    }

    @Test
    public void testWasaReimuNames() {
        WasaReimu wasaReimu = new WasaReimu();
        assertEquals("れいむ", wasaReimu.getMyName());
        assertEquals("れいむ", wasaReimu.getMyNameD());
        assertEquals("", wasaReimu.getNameJ2());
        assertEquals("", wasaReimu.getNameE2());
    }

    @Test
    public void testWasaReimuExtendsReimu() {
        WasaReimu wasaReimu = new WasaReimu();
        assertTrue(wasaReimu instanceof Reimu);
    }

    @Test
    public void testWasaReimuNagasiMethods() {
        WasaReimu wasaReimu = new WasaReimu();
        assertNotNull(wasaReimu.getImageVariantState());

        int[][] testArray = new int[10][2];
        wasaReimu.setImageVariantState(testArray);
        assertSame(testArray, wasaReimu.getImageVariantState());
    }

    @Test
    public void testWasaReimuParameterizedConstructor() {
        WasaReimu parent1 = new WasaReimu();
        WasaReimu parent2 = new WasaReimu();

        WasaReimu obj = new WasaReimu(100, 200, 0, AgeState.ADULT, parent1, parent2);

        assertNotNull(obj);
        assertEquals(WasaReimu.type, obj.getType());
    }

    @Test
    public void testWasaReimuGetMountPoint() {
        WasaReimu obj = new WasaReimu();
        // getMountPoint returns attachment offset from map
        // Most classes return null for unknown keys
        obj.getMountPoint("unknown_key");
        // Result can be null or an array depending on initialization
        // Just verify the method doesn't crash
        assertNotNull(obj);
    }

    @Test
    public void testWasaReimuCheckTransform() {
        WasaReimu obj = new WasaReimu();
        // checkTransform() checks transformation conditions
        // Without proper World setup, will likely return null
        obj.checkTransform();
        // Just verify the method executes without crashing
        // Just verify the method executes without crashing
        assertNull(obj.checkTransform());
    }

    @Test
    public void testWasaReimuHybridTypeWithMarisa() {
        WasaReimu obj = new WasaReimu();
        assertEquals(MarisaReimu.type, obj.getHybridType(Marisa.type));
    }

    @Test
    public void testWasaReimuHybridTypeWithOther() {
        WasaReimu obj = new WasaReimu();
        // Test with a type not specifically handled - should return Reimu type
        assertEquals(Reimu.type, obj.getHybridType(org.simyukkuri.enums.YukkuriType.ALICE));
    }
}
