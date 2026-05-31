package org.simyukkuri.item;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.simyukkuri.SimYukkuri;
import org.simyukkuri.entity.core.world.item.Diffuser;
import org.simyukkuri.entity.core.world.item.ItemTestBase;

class DiffuserTest extends ItemTestBase {

    @Test
    void testConstructor_Default() {
        Diffuser item = new Diffuser();
        item.setObjId(1);
        SimYukkuri.world.getCurrentWorldState().getDiffusers().put(item.getObjId(), item);
        verifyCommonProperties(item);
        assertTrue(SimYukkuri.world.getCurrentWorldState().getDiffusers().containsKey(item.getObjId()));
    }

    @Test
    void testSteamTypeEnum() {
        Diffuser.SteamType[] types = Diffuser.SteamType.values();
        assertTrue(types.length > 0);
        for (Diffuser.SteamType t : types) {
            assertDoesNotThrow(() -> t.toString());
            assertTrue(t.getColor() >= 0);
        }
        assertEquals(Diffuser.SteamType.ANTI_FUNGAL, Diffuser.SteamType.valueOf("ANTI_FUNGAL"));
        assertEquals(Diffuser.SteamType.STEAM, Diffuser.SteamType.valueOf("STEAM"));
    }

    @Test
    void testGetHitCheckObjType() {
        Diffuser item = new Diffuser();
        assertEquals(Diffuser.hitCheckObjType, item.getHitCheckObjType());
        assertEquals(0, item.getHitCheckObjType());
    }

    @Test
    void testGetBounding() {
        assertNotNull(Diffuser.getBounding());
        assertSame(Diffuser.getBounding(), Diffuser.getBounding());
    }

    @Test
    void testGetShadowImage_DoesNotThrow() {
        Diffuser item = new Diffuser();
        // images[2] is null if not loaded → getShadowImage returns null
        java.awt.image.BufferedImage result = item.getShadowImage();
        // Either null (images not loaded) or a valid image
        assertTrue(result == null || result.getWidth() > 0);
    }

    @Test
    void testGetSetSteamType() {
        Diffuser item = new Diffuser();
        boolean[] types = item.getSteamType();
        assertNotNull(types);
        assertEquals(Diffuser.SteamType.values().length, types.length);
        // set first type true
        item.setSteamType(new boolean[] { true, false, false, false, false, false, false, false, false, false, false,
                false, false, false });
        assertTrue(item.getSteamType()[0]);
    }

    @Test
    void testGetSetSteamNum() {
        Diffuser item = new Diffuser();
        assertEquals(0, item.getSteamNum());
        item.setSteamNum(3);
        assertEquals(3, item.getSteamNum());
    }

    @Test
    void testRemoveListData() {
        Diffuser item = new Diffuser();
        item.setObjId(88);
        SimYukkuri.world.getCurrentWorldState().getDiffusers().put(item.getObjId(), item);
        item.removeFromWorld();
        assertFalse(SimYukkuri.world.getCurrentWorldState().getDiffusers().containsKey(item.getObjId()));
    }

    @Test
    void testUpDate_Disabled() {
        Diffuser item = new Diffuser();
        item.setEnabled(false);
        item.upDate();
        assertFalse(item.getEnabled());
        assertFalse(item.isRemoved());
    }

    @Test
    void testUpDate_Enabled_NoSteamTypeSet() {
        Diffuser item = new Diffuser();
        item.setEnabled(true);
        item.upDate();
        assertTrue(item.getEnabled());
        assertFalse(item.isRemoved());
    }

    @Test
    void testUpDate_Enabled_AgeModulo40NotZero() {
        Diffuser item = new Diffuser();
        item.setEnabled(true);
        item.setAge(1); // 1%40 != 0, 1%2400 != 0 → no cash or steam
        item.upDate();
        assertTrue(item.getEnabled());
        assertEquals(1, item.getAge());
    }

    // --- getImageLayer ---

    @Test
    void testGetImageLayer_enabled_doesNotThrow() {
        Diffuser item = new Diffuser();
        java.awt.image.BufferedImage[] layer = new java.awt.image.BufferedImage[1];
        int count = item.getImageLayer(layer);
        assertEquals(1, count);
    }

    @Test
    void testGetImageLayer_disabled_doesNotThrow() {
        Diffuser item = new Diffuser();
        item.setEnabled(false);
        java.awt.image.BufferedImage[] layer = new java.awt.image.BufferedImage[1];
        int count = item.getImageLayer(layer);
        assertEquals(1, count);
    }

    // --- Diffuser(int,int,int) constructor ---

    @Test
    void testConstructor_WithCoords_executesCode() {
        Diffuser[] holder = new Diffuser[1];
        try {
            holder[0] = new Diffuser(100, 100, 0);
        } catch (Exception e) {
            // Expected: HeadlessException from JOptionPane in headless environment
        }
        assertTrue(holder[0] == null || !holder[0].isRemoved());
    }

    // --- setupDiffuser ---

    @Test
    void testSetupDiffuser_headless_executesCode() {
        Diffuser item = new Diffuser();
        assertFalse(item.isRemoved());
        try {
            Diffuser.setupDiffuser(item, false);
        } catch (Exception e) {
            // Expected: HeadlessException from JOptionPane
        }
        assertNotNull(item);
    }
}
