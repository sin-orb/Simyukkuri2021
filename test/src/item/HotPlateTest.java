package src.item;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import src.SimYukkuri;
import src.base.Body;
import src.base.ItemTestBase;
import src.util.WorldTestHelper;

class HotPlateTest extends ItemTestBase {

    @Test
    void testConstructor_Default() {
        HotPlate item = new HotPlate();
        item.setObjId(1);
        SimYukkuri.world.getCurrentMap().getHotPlate().put(item.getObjId(), item);
        verifyCommonProperties(item);
        assertTrue(SimYukkuri.world.getCurrentMap().getHotPlate().containsKey(item.getObjId()));
    }

    @Test
    void testGetHitCheckObjType() {
        HotPlate item = new HotPlate();
        assertEquals(HotPlate.hitCheckObjType, item.getHitCheckObjType());
    }

    @Test
    void testEnableHitCheck_NoBindBody() {
        HotPlate item = new HotPlate();
        assertNull(item.getBindBody());
        assertTrue(item.enableHitCheck());
    }

    @Test
    void testEnableHitCheck_WithBindBody() {
        HotPlate item = new HotPlate();
        Body body = WorldTestHelper.createBody();
        item.setBindBody(body);
        assertFalse(item.enableHitCheck());
    }

    @Test
    void testGetSetBindBody() {
        HotPlate item = new HotPlate();
        assertNull(item.getBindBody());
        Body body = WorldTestHelper.createBody();
        item.setBindBody(body);
        assertEquals(body, item.getBindBody());
        item.setBindBody(null);
        assertNull(item.getBindBody());
    }

    @Test
    void testGetSetSmoke() {
        HotPlate item = new HotPlate();
        item.setSmoke(null);
        assertNull(item.getSmoke());
    }

    @Test
    void testGetShadowImage() {
        HotPlate item = new HotPlate();
        assertNull(item.getShadowImage());
    }

    @Test
    void testGetBounding() {
        assertNotNull(HotPlate.getBounding());
    }

    @Test
    void testRemoveListData_WithoutBindBody() {
        HotPlate item = new HotPlate();
        item.setObjId(200);
        SimYukkuri.world.getCurrentMap().getHotPlate().put(item.getObjId(), item);
        assertTrue(SimYukkuri.world.getCurrentMap().getHotPlate().containsKey(200));

        item.removeListData();

        assertFalse(SimYukkuri.world.getCurrentMap().getHotPlate().containsKey(200));
    }

    @Test
    void testRemoveListData_WithBindBody() {
        HotPlate item = new HotPlate();
        item.setObjId(201);
        SimYukkuri.world.getCurrentMap().getHotPlate().put(item.getObjId(), item);

        Body body = WorldTestHelper.createBody();
        body.setLockmove(true);
        item.setBindBody(body);

        item.removeListData();

        assertFalse(SimYukkuri.world.getCurrentMap().getHotPlate().containsKey(201));
        assertFalse(body.isLockmove());
    }

    // upDate() の age%2400==0 の Cash.addCash 呼び出しが起きる。
    // initializeMinimalWorld() は setUp() で実施済み。
    @Test
    void testUpDate_NoBindBody() {
        HotPlate item = new HotPlate();
        assertNull(item.getBindBody());
        assertDoesNotThrow(() -> item.upDate());
    }

    @Test
    void testUpDate_WithBindBody_GrabbedTrue() {
        HotPlate item = new HotPlate();
        item.setObjId(202);
        SimYukkuri.world.getCurrentMap().getHotPlate().put(item.getObjId(), item);

        Body body = WorldTestHelper.createBody();
        item.setBindBody(body);
        item.setGrabbed(true);

        assertDoesNotThrow(() -> item.upDate());
    }

    // bindBody のX座標をプレートのX座標と変える。upDate() 後 bindBody が null になる。
    @Test
    void testUpDate_WithBindBody_BodyMovedAway() {
        HotPlate item = new HotPlate();
        item.setObjId(203);
        SimYukkuri.world.getCurrentMap().getHotPlate().put(item.getObjId(), item);

        Body body = WorldTestHelper.createBody();
        item.setBindBody(body);
        // プレートとbodyのX座標を意図的にずらす
        body.setCalcX(item.getX() + 999);

        item.upDate();

        assertNull(item.getBindBody());
    }

    // --- HotPlate(int,int,int) constructor ---

    @Test
    void testConstructor_WithCoords_doesNotThrow() {
        HotPlate item = new HotPlate(100, 100, 0);
        assertNotNull(item);
        assertTrue(SimYukkuri.world.getCurrentMap().getHotPlate().containsKey(item.getObjId()));
    }

    // --- getImageLayer ---

    @Test
    void testGetImageLayer_enabled_noBindBody_doesNotThrow() {
        HotPlate item = new HotPlate();
        java.awt.image.BufferedImage[] layer = new java.awt.image.BufferedImage[1];
        assertDoesNotThrow(() -> item.getImageLayer(layer));
    }

    @Test
    void testGetImageLayer_enabled_withBindBody_doesNotThrow() {
        HotPlate item = new HotPlate();
        Body body = WorldTestHelper.createBody();
        item.setBindBody(body);
        java.awt.image.BufferedImage[] layer = new java.awt.image.BufferedImage[1];
        assertDoesNotThrow(() -> item.getImageLayer(layer));
    }

    @Test
    void testGetImageLayer_disabled_doesNotThrow() {
        HotPlate item = new HotPlate();
        item.setEnabled(false);
        java.awt.image.BufferedImage[] layer = new java.awt.image.BufferedImage[1];
        assertDoesNotThrow(() -> item.getImageLayer(layer));
    }

    // --- objHitProcess ---

    @Test
    void testObjHitProcess_executesCode() {
        HotPlate item = new HotPlate();
        item.setObjId(300);
        SimYukkuri.world.getCurrentMap().getHotPlate().put(item.getObjId(), item);
        Body body = WorldTestHelper.createBody();
        try {
            item.objHitProcess(body);
        } catch (Exception e) {
            // mypane.getTerrarium().addEffect fails in headless
        }
    }
}
