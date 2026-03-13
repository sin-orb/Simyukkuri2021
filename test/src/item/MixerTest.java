package src.item;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import src.SimYukkuri;
import src.base.Body;
import src.base.ItemTestBase;
import src.util.WorldTestHelper;

class MixerTest extends ItemTestBase {

    @Test
    void testConstructor_Default() {
        Mixer item = new Mixer();
        item.setObjId(1);
        SimYukkuri.world.getCurrentMap().getMixer().put(item.getObjId(), item);
        verifyCommonProperties(item);
        assertTrue(SimYukkuri.world.getCurrentMap().getMixer().containsKey(item.getObjId()));
    }

    @Test
    void testGetHitCheckObjType() {
        Mixer item = new Mixer();
        assertEquals(Mixer.hitCheckObjType, item.getHitCheckObjType());
    }

    @Test
    void testEnableHitCheck_NoBind() {
        Mixer item = new Mixer();
        item.setBind(-1);
        // bind=-1 → getBodyInstance returns null → true
        assertTrue(item.enableHitCheck());
    }

    @Test
    void testGetSetBind() {
        Mixer item = new Mixer();
        item.setBind(42);
        assertEquals(42, item.getBind());
    }

    @Test
    void testGetSetMix() {
        Mixer item = new Mixer();
        item.setMix(null);
        assertNull(item.getMix());
    }

    @Test
    void testGetSetCounter() {
        Mixer item = new Mixer();
        item.setCounter(100);
        assertEquals(100, item.getCounter());
    }

    @Test
    void testGetSetAmount() {
        Mixer item = new Mixer();
        item.setAmount(500);
        assertEquals(500, item.getAmount());
    }

    @Test
    void testGetSetSweet() {
        Mixer item = new Mixer();
        item.setSweet(300);
        assertEquals(300, item.getSweet());
    }

    @Test
    void testGetSetSick() {
        Mixer item = new Mixer();
        assertFalse(item.isSick());
        item.setSick(true);
        assertTrue(item.isSick());
    }

    @Test
    void testGetShadowImage() {
        Mixer item = new Mixer();
        assertNull(item.getShadowImage());
    }

    @Test
    void testGetBounding() {
        assertNotNull(Mixer.getBounding());
    }

    @Test
    void testRemoveListData_NoBind() {
        Mixer item = new Mixer();
        item.setObjId(99);
        SimYukkuri.world.getCurrentMap().getMixer().put(item.getObjId(), item);
        item.setBind(-1);
        item.removeListData();
        assertFalse(SimYukkuri.world.getCurrentMap().getMixer().containsKey(item.getObjId()));
    }

    @Test
    void testRemoveListData_WithBind() {
        Mixer item = new Mixer();
        item.setObjId(98);
        SimYukkuri.world.getCurrentMap().getMixer().put(item.getObjId(), item);
        Body body = WorldTestHelper.createBody();
        body.setLockmove(true);
        SimYukkuri.world.getCurrentMap().getBody().put(body.getUniqueID(), body);
        item.setBind(body.getUniqueID());
        item.removeListData();
        assertFalse(body.isLockmove());
        assertEquals(-1, item.getBind());
    }

    @Test
    void testObjHitProcess_Disabled() {
        Mixer item = new Mixer();
        item.setEnabled(false);
        Body body = WorldTestHelper.createBody();
        assertEquals(0, item.objHitProcess(body));
    }

    @Test
    void testObjHitProcess_Enabled() {
        Mixer item = new Mixer();
        item.setEnabled(true);
        item.setX(100);
        item.setY(100);
        Body body = WorldTestHelper.createBody();
        SimYukkuri.world.getCurrentMap().getBody().put(body.getUniqueID(), body);
        assertEquals(1, item.objHitProcess(body));
        assertEquals(body.getUniqueID(), item.getBind());
        assertTrue(body.isLockmove());
    }

    @Test
    void testUpDate_NoBind_NoMix() {
        Mixer item = new Mixer();
        item.setBind(-1);
        item.setMix(null);
        // Should not throw even if age%2400==0 (calls Cash.addCash)
        assertDoesNotThrow(() -> item.upDate());
    }

    @Test
    void testUpDate_Disabled_WithBind() {
        Mixer item = new Mixer();
        Body body = WorldTestHelper.createBody();
        body.setLockmove(true);
        SimYukkuri.world.getCurrentMap().getBody().put(body.getUniqueID(), body);
        item.setBind(body.getUniqueID());
        item.setEnabled(false);
        // enabled=false → mix=null branch
        assertDoesNotThrow(() -> item.upDate());
    }

    @Test
    void testUpDate_Grabbed_WithBind() {
        Mixer item = new Mixer();
        item.setX(100);
        item.setY(100);
        item.setEnabled(true);
        item.setGrabbed(true);
        Body body = WorldTestHelper.createBody();
        body.setX(100);
        body.setY(100);
        body.setLockmove(true);
        SimYukkuri.world.getCurrentMap().getBody().put(body.getUniqueID(), body);
        item.setBind(body.getUniqueID());
        assertDoesNotThrow(() -> item.upDate());
    }

    @Test
    void testUpDate_BodyMovedAway() {
        Mixer item = new Mixer();
        item.setX(100);
        item.setY(100);
        item.setEnabled(true);
        item.setGrabbed(false);
        Body body = WorldTestHelper.createBody();
        // body at different position
        body.setX(200);
        body.setY(200);
        body.setLockmove(true);
        SimYukkuri.world.getCurrentMap().getBody().put(body.getUniqueID(), body);
        item.setBind(body.getUniqueID());
        item.upDate();
        // bind should be reset to -1
        assertEquals(-1, item.getBind());
    }

    @Test
    void testLoadImages_headless_executesCode() {
        try {
            Mixer.loadImages(Mixer.class.getClassLoader(), null);
        } catch (Exception e) { }
    }

    @Test
    void testGetImageLayer_doesNotThrow() {
        Mixer item = new Mixer();
        java.awt.image.BufferedImage[] layer = new java.awt.image.BufferedImage[1];
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> item.getImageLayer(layer));
    }

    @Test
    void testConstructorWithArgs_doesNotThrow() {
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> new Mixer(100, 100, 0));
    }
}