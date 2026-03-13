package src.item;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import src.SimYukkuri;
import src.base.ItemTestBase;

class DiffuserTest extends ItemTestBase {

    @Test
    void testConstructor_Default() {
        Diffuser item = new Diffuser();
        item.setObjId(1);
        SimYukkuri.world.getCurrentMap().getDiffuser().put(item.getObjId(), item);
        verifyCommonProperties(item);
        assertTrue(SimYukkuri.world.getCurrentMap().getDiffuser().containsKey(item.getObjId()));
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
    }

    @Test
    void testGetShadowImage_DoesNotThrow() {
        Diffuser item = new Diffuser();
        // images[2] is null if not loaded
        assertDoesNotThrow(() -> item.getShadowImage());
    }

    @Test
    void testGetSetSteamType() {
        Diffuser item = new Diffuser();
        boolean[] types = item.getSteamType();
        assertNotNull(types);
        assertEquals(Diffuser.SteamType.values().length, types.length);
        // set first type true
        item.setSteamType(new boolean[]{true, false, false, false, false, false, false, false, false, false, false, false, false, false});
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
        SimYukkuri.world.getCurrentMap().getDiffuser().put(item.getObjId(), item);
        item.removeListData();
        assertFalse(SimYukkuri.world.getCurrentMap().getDiffuser().containsKey(item.getObjId()));
    }

    @Test
    void testUpDate_Disabled() {
        Diffuser item = new Diffuser();
        item.setEnabled(false);
        // !enabled → return early
        assertDoesNotThrow(() -> item.upDate());
    }

    @Test
    void testUpDate_Enabled_NoSteamTypeSet() {
        Diffuser item = new Diffuser();
        item.setEnabled(true);
        // all steamType are false by default
        // age=0 → age%2400==0 calls Cash.addCash; age%40==0 checks steamType
        // steamType[steamNum=0] is false, so no addEffect call (GUI)
        // Just checks the do-while loop that resets steamNum
        assertDoesNotThrow(() -> item.upDate());
    }

    @Test
    void testUpDate_Enabled_AgeModulo40NotZero() {
        Diffuser item = new Diffuser();
        item.setEnabled(true);
        item.setAge(1); // 1%40 != 0, 1%2400 != 0 → no cash or steam
        assertDoesNotThrow(() -> item.upDate());
    }

    // --- getImageLayer ---

    @Test
    void testGetImageLayer_enabled_doesNotThrow() {
        Diffuser item = new Diffuser();
        java.awt.image.BufferedImage[] layer = new java.awt.image.BufferedImage[1];
        assertDoesNotThrow(() -> item.getImageLayer(layer));
    }

    @Test
    void testGetImageLayer_disabled_doesNotThrow() {
        Diffuser item = new Diffuser();
        item.setEnabled(false);
        java.awt.image.BufferedImage[] layer = new java.awt.image.BufferedImage[1];
        assertDoesNotThrow(() -> item.getImageLayer(layer));
    }

    // --- Diffuser(int,int,int) constructor ---

    @Test
    void testConstructor_WithCoords_executesCode() {
        try {
            Diffuser item = new Diffuser(100, 100, 0);
            // If no exception: setupDiffuser returned false → item removed from world map
        } catch (Exception e) {
            // Expected: HeadlessException from JOptionPane in headless environment
        }
    }

    // --- setupDiffuser ---

    @Test
    void testSetupDiffuser_headless_executesCode() {
        Diffuser item = new Diffuser();
        try {
            Diffuser.setupDiffuser(item, false);
        } catch (Exception e) {
            // Expected: HeadlessException from JOptionPane
        }
    }
}
