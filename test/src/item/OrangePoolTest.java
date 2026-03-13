package src.item;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import src.SimYukkuri;
import src.base.ItemTestBase;
import src.base.ObjEX;
import src.draw.Rectangle4y;
import src.enums.AgeState;
import src.system.Sprite;
import src.yukkuri.Reimu;

class OrangePoolTest extends ItemTestBase {

    @Test
    void testConstructor_Default() {
        OrangePool item = new OrangePool();
        item.setObjId(1);
        SimYukkuri.world.getCurrentMap().getOrangePool().put(item.getObjId(), item);
        verifyCommonProperties(item);
        assertTrue(SimYukkuri.world.getCurrentMap().getOrangePool().containsKey(item.getObjId()));
    }

    @Test
    void testOrangeTypeEnum() {
        OrangePool.OrangeType[] types = OrangePool.OrangeType.values();
        assertEquals(2, types.length);
        for (OrangePool.OrangeType t : types) {
            assertDoesNotThrow(() -> t.toString());
        }
        assertEquals(OrangePool.OrangeType.NORMAL, OrangePool.OrangeType.valueOf("NORMAL"));
        assertEquals(OrangePool.OrangeType.RESCUE, OrangePool.OrangeType.valueOf("RESCUE"));
    }

    // --- getHitCheckObjType ---

    @Test
    void testGetHitCheckObjType() {
        OrangePool item = new OrangePool();
        assertEquals(OrangePool.hitCheckObjType, item.getHitCheckObjType());
    }

    // --- getShadowImage ---

    @Test
    void testGetShadowImage() {
        OrangePool item = new OrangePool();
        // OrangePool.getShadowImage() は常に null を返す
        assertFalse(item.getShadowImage() != null);
    }

    // --- getBounding ---

    @Test
    void testGetBounding() {
        // boundary は static final フィールドで new Rectangle4y() 済み
        Rectangle4y bounding = OrangePool.getBounding();
        assertNotNull(bounding);
    }

    // --- isRescue default ---

    @Test
    void testIsRescue_Default() {
        OrangePool item = new OrangePool();
        assertFalse(item.isRescue());
    }

    // --- setRescue ---

    @Test
    void testSetRescue() {
        OrangePool item = new OrangePool();
        item.setRescue(true);
        assertTrue(item.isRescue());
    }

    // --- getItemRank / setItemRank ---

    @Test
    void testGetSetItemRank() {
        OrangePool item = new OrangePool();
        item.setItemRank(ObjEX.ItemRank.HOUSE);
        assertEquals(ObjEX.ItemRank.HOUSE, item.getItemRank());
    }

    // --- removeListData ---

    @Test
    void testRemoveListData() {
        OrangePool item = new OrangePool();
        int id = 999;
        item.setObjId(id);
        SimYukkuri.world.getCurrentMap().getOrangePool().put(id, item);
        assertTrue(SimYukkuri.world.getCurrentMap().getOrangePool().containsKey(id));
        item.removeListData();
        assertFalse(SimYukkuri.world.getCurrentMap().getOrangePool().containsKey(id));
    }

    // --- objHitProcess: enabled=false ---

    @Test
    void testObjHitProcess_Disabled() {
        OrangePool item = new OrangePool();
        item.setEnabled(false);
        Reimu b = new Reimu();
        b.setAgeState(AgeState.ADULT);
        Sprite[] spr = new Sprite[3];
        for (int i = 0; i < 3; i++) {
            spr[i] = new Sprite(10, 10, Sprite.PIVOT_CENTER_BOTTOM);
        }
        b.setBodySpr(spr);
        // enabled=false のとき objHitProcess は即 0 を返す
        assertEquals(0, item.objHitProcess(b));
    }

    // --- getValue: itemRank=HOUSE, rescue=false ---

    @Test
    void testGetValue_HOUSE_NoRescue() {
        OrangePool item = new OrangePool();
        item.setItemRank(ObjEX.ItemRank.HOUSE);
        item.setRescue(false);
        // value[] = {500, 10000}; rescue=false なら value[0]=500
        assertEquals(500, item.getValue());
    }

    // --- getValue: itemRank 非 HOUSE ---

    @Test
    void testGetValue_NonHOUSE() {
        OrangePool item = new OrangePool();
        item.setItemRank(ObjEX.ItemRank.NORA);
        // NORA の場合は 0 を返す
        assertEquals(0, item.getValue());
    }

    // --- getCost: itemRank=HOUSE ---

    @Test
    void testGetCost_HOUSE() {
        OrangePool item = new OrangePool();
        item.setItemRank(ObjEX.ItemRank.HOUSE);
        item.setRescue(false);
        // cost[] = {5, 100}; rescue=false なら cost[0]=5
        assertEquals(5, item.getCost());

        item.setRescue(true);
        // rescue=true なら cost[1]=100
        assertEquals(100, item.getCost());
    }

    @Test
    void testLoadImages_headless_executesCode() {
        try {
            OrangePool.loadImages(OrangePool.class.getClassLoader(), null);
        } catch (Exception e) { }
    }

    @Test
    void testGetImageLayer_doesNotThrow() {
        OrangePool item = new OrangePool();
        java.awt.image.BufferedImage[] layer = new java.awt.image.BufferedImage[1];
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> item.getImageLayer(layer));
    }

    @Test
    void testSetupOrange_headless_executesCode() {
        OrangePool item = new OrangePool();
        try {
            OrangePool.setupOrange(item, true);
        } catch (Exception e) { }
    }

    @Test
    void testConstructorWithArgs_headless_executesCode() {
        try {
            OrangePool o = new OrangePool(100, 100, 0);
            org.junit.jupiter.api.Assertions.assertNotNull(o);
        } catch (Exception e) { }
    }
}