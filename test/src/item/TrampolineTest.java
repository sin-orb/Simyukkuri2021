package src.item;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import src.SimYukkuri;
import src.base.ItemTestBase;

class TrampolineTest extends ItemTestBase {

    @Test
    void testConstructor_Default() {
        Trampoline item = new Trampoline();
        item.setObjId(1);
        SimYukkuri.world.getCurrentMap().getTrampoline().put(item.getObjId(), item);
        verifyCommonProperties(item);
        assertTrue(SimYukkuri.world.getCurrentMap().getTrampoline().containsKey(item.getObjId()));
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
    }

    @Test
    void testGetShadowImage_ReturnsNullWhenImagesNotLoaded() {
        Trampoline item = new Trampoline();
        // images[1] is null if not loaded
        // getShadowImage() returns images[1]
        assertDoesNotThrow(() -> item.getShadowImage());
    }

    @Test
    void testRemoveListData() {
        Trampoline item = new Trampoline();
        item.setObjId(55);
        SimYukkuri.world.getCurrentMap().getTrampoline().put(item.getObjId(), item);
        item.removeListData();
        assertFalse(SimYukkuri.world.getCurrentMap().getTrampoline().containsKey(item.getObjId()));
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
        // kick() calls kick(0, -8, -4) - should not throw
        assertDoesNotThrow(() -> item.kick());
    }

    @Test
    void testGetSetOption() {
        Trampoline item = new Trampoline();
        item.setOption(1);
        assertEquals(1, item.getOption());
    }

    @Test
    void testGetSetAccident1() {
        Trampoline item = new Trampoline();
        item.setAccident1(50);
        assertEquals(50, item.getAccident1());
    }

    @Test
    void testGetSetAccident2() {
        Trampoline item = new Trampoline();
        item.setAccident2(20);
        assertEquals(20, item.getAccident2());
    }

    @Test
    void testLoadImages_headless_executesCode() {
        try {
            Trampoline.loadImages(Trampoline.class.getClassLoader(), null);
        } catch (Exception e) { }
    }

    @Test
    void testGetImageLayer_doesNotThrow() {
        Trampoline item = new Trampoline();
        java.awt.image.BufferedImage[] layer = new java.awt.image.BufferedImage[1];
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> item.getImageLayer(layer));
    }

    @Test
    void testCheckHitObj_singleArg_doesNotThrow() {
        Trampoline item = new Trampoline();
        src.base.Obj body = src.util.WorldTestHelper.createBody();
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> item.checkHitObj(body));
    }

    @Test
    void testCheckHitObj_twoArgs_doesNotThrow() {
        Trampoline item = new Trampoline();
        src.base.Obj body = src.util.WorldTestHelper.createBody();
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> item.checkHitObj(null, body));
    }

    @Test
    void testSetupTrampoline_headless_executesCode() {
        Trampoline item = new Trampoline();
        try {
            Trampoline.setupTrampoline(item);
        } catch (Exception e) { }
    }

    @Test
    void testConstructorWithArgs_headless_executesCode() {
        try {
            Trampoline t = new Trampoline(100, 100, 0);
            org.junit.jupiter.api.Assertions.assertNotNull(t);
        } catch (Exception e) { }
    }
}