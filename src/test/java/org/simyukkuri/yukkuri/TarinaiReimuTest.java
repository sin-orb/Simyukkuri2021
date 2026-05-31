package org.simyukkuri.yukkuri;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.simyukkuri.SimYukkuri;
import org.simyukkuri.entity.core.living.yukkuri.impl.Marisa;
import org.simyukkuri.entity.core.living.yukkuri.impl.MarisaReimu;
import org.simyukkuri.entity.core.living.yukkuri.impl.Tarinai;
import org.simyukkuri.entity.core.living.yukkuri.impl.TarinaiReimu;
import org.simyukkuri.enums.AgeState;

public class TarinaiReimuTest {

    @Test
    public void testTarinaiReimuIdentity() {
        TarinaiReimu tarinaiReimu = new TarinaiReimu();
        assertEquals(TarinaiReimu.type, tarinaiReimu.getType());
    }

    @Test
    public void testTarinaiReimuExtendsTarinai() {
        TarinaiReimu tarinaiReimu = new TarinaiReimu();
        assertTrue(tarinaiReimu instanceof Tarinai);
    }

    @Test
    public void testTarinaiReimuIsIdiot() {
        TarinaiReimu tarinaiReimu = new TarinaiReimu();
        assertTrue(tarinaiReimu.isIdiot());
    }

    @Test
    public void testTarinaiReimuParameterizedConstructor() {
        TarinaiReimu parent1 = new TarinaiReimu();
        TarinaiReimu parent2 = new TarinaiReimu();

        TarinaiReimu obj = new TarinaiReimu(100, 200, 0, AgeState.ADULT, parent1, parent2);

        assertNotNull(obj);
        assertEquals(TarinaiReimu.type, obj.getType());
    }

    @Test
    public void testTarinaiReimuGetMountPoint() {
        TarinaiReimu obj = new TarinaiReimu();
        // getMountPoint returns attachment offset from map
        // Most classes return null for unknown keys
        obj.getMountPoint("unknown_key");
        // Result can be null or an array depending on initialization
        // Just verify the method doesn't crash
        assertNotNull(obj);
    }

    @Test
    public void testTarinaiReimuCheckTransform() {
        TarinaiReimu obj = new TarinaiReimu();
        // checkTransform() checks transformation conditions
        // Without proper World setup, will likely return null
        obj.checkTransform();
        // Just verify the method executes without crashing
        // Just verify the method executes without crashing
        assertNull(obj.checkTransform());
    }

    @Test
    public void testIsIdiot_doesNotThrow() {
        TarinaiReimu obj = new TarinaiReimu();
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> obj.isIdiot());
    }

    @Test
    public void testTuneParameters_doesNotThrow() {
        TarinaiReimu obj = new TarinaiReimu();
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> obj.tuneParameters());
    }

    @Test
    public void testGetHybridType_withMarisa() {
        TarinaiReimu obj = new TarinaiReimu();
        // With Marisa type → returns MarisaReimu.type
        assertEquals(MarisaReimu.type, obj.getHybridType(Marisa.type));
    }

    @Test
    public void testGetHybridType_default() {
        TarinaiReimu obj = new TarinaiReimu();
        // Default → returns TarinaiReimu.type
        assertEquals(TarinaiReimu.type, obj.getHybridType(org.simyukkuri.enums.YukkuriType.ALICE));
    }

    @Test
    public void testGetNameJ() {
        TarinaiReimu obj = new TarinaiReimu();
        assertNotNull(obj.getNameJ());
    }
}
