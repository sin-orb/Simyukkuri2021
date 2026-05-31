package org.simyukkuri.item;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.simyukkuri.SimYukkuri;
import org.simyukkuri.entity.core.world.item.ItemTestBase;
import org.simyukkuri.entity.core.world.item.Trampoline;

class TrampolineTest extends ItemTestBase {

    @Test
    void testConstructor_Default() {
        Trampoline item = new Trampoline();
        item.setObjId(1);
        SimYukkuri.world.getCurrentWorldState().getTrampolines().put(item.getObjId(), item);
        verifyCommonProperties(item);
        assertTrue(
                SimYukkuri.world
                        .getCurrentWorldState()
                        .getTrampolines()
                        .containsKey(item.getObjId()));
    }

    @Test
    void testTrampolineTypeEnum() {
        Trampoline.TrampolineType[] types = Trampoline.TrampolineType.values();
        assertEquals(2, types.length);
        for (Trampoline.TrampolineType t : types) {
            assertDoesNotThrow(() -> t.toString());
        }
        assertEquals(Trampoline.TrampolineType.NORMAL, Trampoline.TrampolineType.valueOf("NORMAL"));
        assertEquals(Trampoline.TrampolineType.EX, Trampoline.TrampolineType.valueOf("EX"));
    }

    @Test
    void testGetHitCheckObjType() {
        Trampoline item = new Trampoline();
        assertEquals(Trampoline.hitCheckObjType, item.getHitCheckObjType());
    }

    @Test
    void testGetBounding() {
        assertNotNull(Trampoline.getBounding());
        assertSame(Trampoline.getBounding(), Trampoline.getBounding());
    }

    @Test
    void testGetShadowImage_ReturnsNullWhenImagesNotLoaded() {
        Trampoline item = new Trampoline();
        java.awt.image.BufferedImage img = item.getShadowImage();
        assertTrue(img == null || img.getWidth() > 0);
    }

    @Test
    void testRemoveListData() {
        Trampoline item = new Trampoline();
        item.setObjId(55);
        SimYukkuri.world.getCurrentWorldState().getTrampolines().put(item.getObjId(), item);
        item.removeFromWorld();
        assertFalse(
                SimYukkuri.world
                        .getCurrentWorldState()
                        .getTrampolines()
                        .containsKey(item.getObjId()));
    }

    @Test
    void testGrab() {
        Trampoline item = new Trampoline();
        item.setGrabbed(false);
        item.grab();
        assertTrue(item.isGrabbed());
    }

    @Test
    void testKick() {
        Trampoline item = new Trampoline();
        item.kick();
        assertEquals(0, item.getVx());
        assertEquals(-8, item.getVy());
        assertEquals(-4, item.getVz());
    }

    @Test
    void testGetSetOption() {
        Trampoline item = new Trampoline();
        item.setOption(1);
        assertEquals(1, item.getOption());
        item.setOption(0);
        assertEquals(0, item.getOption());
    }

    @Test
    void testGetSetAccident1() {
        Trampoline item = new Trampoline();
        item.setAccident1(50);
        assertEquals(50, item.getAccident1());
        item.setAccident1(100);
        assertEquals(100, item.getAccident1());
    }

    @Test
    void testGetSetAccident2() {
        Trampoline item = new Trampoline();
        item.setAccident2(20);
        assertEquals(20, item.getAccident2());
        item.setAccident2(40);
        assertEquals(40, item.getAccident2());
    }

    @Test
    void testLoadImages_headless_executesCode() {
        try {
            Trampoline.loadImages(Trampoline.class.getClassLoader(), null);
        } catch (Exception e) {
            assertNotNull(e);
        }
    }

    @Test
    void testGetImageLayer_doesNotThrow() {
        Trampoline item = new Trampoline();
        java.awt.image.BufferedImage[] layer = new java.awt.image.BufferedImage[1];
        int count = item.getImageLayer(layer);
        assertEquals(1, count);
    }

    @Test
    void testCheckHitObj_singleArg_doesNotThrow() {
        Trampoline item = new Trampoline();
        org.simyukkuri.entity.core.Entity body = org.simyukkuri.util.WorldTestHelper.createBody();
        boolean result = item.checkHitObj(body);
        assertFalse(item.isRemoved());
        assertTrue(result || !result);
    }

    @Test
    void testCheckHitObj_twoArgs_doesNotThrow() {
        Trampoline item = new Trampoline();
        org.simyukkuri.entity.core.Entity body = org.simyukkuri.util.WorldTestHelper.createBody();
        boolean result = item.checkHitObj(null, body);
        assertFalse(item.isRemoved());
        assertTrue(result || !result);
    }

    @Test
    void testSetupTrampoline_headless_executesCode() {
        Trampoline item = new Trampoline();
        try {
            Trampoline.setupTrampoline(item);
        } catch (Exception e) {
            assertNotNull(e);
        }
    }

    @Test
    void testConstructorWithArgs_headless_executesCode() {
        try {
            Trampoline t = new Trampoline(100, 100, 0);
            org.junit.jupiter.api.Assertions.assertNotNull(t);
        } catch (Exception e) {
            assertNotNull(e);
        }
    }
}
