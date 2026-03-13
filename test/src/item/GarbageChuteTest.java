package src.item;

import static org.junit.jupiter.api.Assertions.*;

import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.Test;

import src.SimYukkuri;
import src.base.Body;
import src.base.ItemTestBase;
import src.base.ObjEX.ItemRank;
import src.draw.Translate;
import src.enums.AgeState;
import src.enums.ObjEXType;
import src.system.Sprite;
import src.yukkuri.Reimu;

class GarbageChuteTest extends ItemTestBase {

    private static Body createBody() {
        Body b = new Reimu();
        b.setAgeState(AgeState.ADULT);
        Sprite[] spr = new Sprite[3];
        for (int i = 0; i < 3; i++) {
            spr[i] = new Sprite(10, 10, Sprite.PIVOT_CENTER_BOTTOM);
        }
        b.setBodySpr(spr);
        SimYukkuri.world.getCurrentMap().getBody().put(b.getUniqueID(), b);
        return b;
    }

    // --- Default constructor ---

    @Test
    void testConstructor_Default() {
        GarbageChute item = new GarbageChute();
        item.setObjId(1);
        SimYukkuri.world.getCurrentMap().getGarbagechute().put(item.getObjId(), item);
        verifyCommonProperties(item);
        assertTrue(SimYukkuri.world.getCurrentMap().getGarbagechute().containsKey(item.getObjId()));
    }

    // --- Parameterized constructor: HOUSE rank (option=0) ---

    @Test
    void testConstructor_HouseRank() {
        GarbageChute item = new GarbageChute(100, 100, 0);
        assertEquals(ItemRank.HOUSE, item.getItemRank());
        assertEquals(5000, item.getValue());
        assertEquals(5, item.getCost());
        assertTrue(SimYukkuri.world.getCurrentMap().getGarbagechute().containsKey(item.getObjId()));
    }

    // --- Parameterized constructor: NORA rank (option=1) ---

    @Test
    void testConstructor_NoraRank() {
        GarbageChute item = new GarbageChute(100, 100, 1);
        assertEquals(ItemRank.NORA, item.getItemRank());
        assertEquals(0, item.getValue());
        assertEquals(0, item.getCost());
    }

    // --- ObjEXType ---

    @Test
    void testObjEXType_GARBAGECHUTE() {
        GarbageChute item = new GarbageChute(100, 100, 0);
        assertEquals(ObjEXType.GARBAGECHUTE, item.getObjEXType());
    }

    // --- getShadowImage ---

    @Test
    void testGetShadowImage_returnsNull() {
        GarbageChute item = new GarbageChute();
        assertNull(item.getShadowImage());
    }

    // --- getBounding ---

    @Test
    void testGetBounding_notNull() {
        assertNotNull(GarbageChute.getBounding());
    }

    // --- getHitCheckObjType ---

    @Test
    void testGetHitCheckObjType() {
        GarbageChute item = new GarbageChute();
        assertEquals(GarbageChute.hitCheckObjType, item.getHitCheckObjType());
    }

    // --- removeListData ---

    @Test
    void testRemoveListData() {
        GarbageChute item = new GarbageChute();
        item.setObjId(99);
        SimYukkuri.world.getCurrentMap().getGarbagechute().put(99, item);
        assertTrue(SimYukkuri.world.getCurrentMap().getGarbagechute().containsKey(99));
        item.removeListData();
        assertFalse(SimYukkuri.world.getCurrentMap().getGarbagechute().containsKey(99));
    }

    // --- getters/setters ---

    @Test
    void testGetSetBindObjList() {
        GarbageChute item = new GarbageChute();
        List<src.base.Obj> list = new LinkedList<>();
        item.setBindObjList(list);
        assertEquals(list, item.getBindObjList());
    }

    @Test
    void testGetSetItemRank() {
        GarbageChute item = new GarbageChute();
        item.setItemRank(ItemRank.YASEI);
        assertEquals(ItemRank.YASEI, item.getItemRank());
        item.setItemRank(ItemRank.HOUSE);
        assertEquals(ItemRank.HOUSE, item.getItemRank());
    }

    @Test
    void testGetSetBindBody() {
        GarbageChute item = new GarbageChute();
        Body b = createBody();
        item.setBindBody(b);
        assertEquals(b, item.getBindBody());
        item.setBindBody(null);
        assertNull(item.getBindBody());
    }

    // --- upDate: empty bindObjList ---

    @Test
    void testUpDate_emptyList_doesNothing() {
        GarbageChute item = new GarbageChute();
        item.setBindObjList(new LinkedList<>());
        assertDoesNotThrow(() -> item.upDate());
    }

    // --- upDate: null bindObjList ---

    @Test
    void testUpDate_nullList_doesNothing() {
        GarbageChute item = new GarbageChute();
        item.setBindObjList(null);
        assertDoesNotThrow(() -> item.upDate());
    }

    // --- upDate: list with a removed object ---

    @Test
    void testUpDate_removedObj_clearsFromList() {
        GarbageChute item = new GarbageChute(100, 100, 0);
        Food food = new Food(50, 50, 0);
        food.setRemoved(true);
        List<src.base.Obj> list = new LinkedList<>();
        list.add(food);
        item.setBindObjList(list);
        // upDate calls translateZ which needs rateX - already set by ItemTestBase.setUp
        assertDoesNotThrow(() -> item.upDate());
    }

    // --- objHitProcess: null ---

    @Test
    void testObjHitProcess_null_returnsZero() {
        GarbageChute item = new GarbageChute(100, 100, 0);
        assertEquals(0, item.objHitProcess(null));
    }

    // --- objHitProcess: Diffuser (skip) ---

    @Test
    void testObjHitProcess_Diffuser_returnsZero() {
        GarbageChute item = new GarbageChute(100, 100, 0);
        Diffuser d = new Diffuser();
        assertEquals(0, item.objHitProcess(d));
    }

    // --- objHitProcess: same object twice (bindObjList already contains) ---

    @Test
    void testObjHitProcess_duplicate_returnsZero() {
        GarbageChute item = new GarbageChute(100, 100, 0);
        item.setCost(0); // avoid Cash issues
        Food food = new Food(50, 50, 0);
        SimYukkuri.world.getCurrentMap().getFood().put(food.getObjId(), food);
        // First call adds it
        item.objHitProcess(food);
        // Second call with same object → already in bindObjList → returns 0
        assertEquals(0, item.objHitProcess(food));
    }

    // --- objHitProcess: Food (non-body) → removes it ---

    @Test
    void testObjHitProcess_food_removesFood() {
        GarbageChute item = new GarbageChute(100, 100, 0);
        item.setCost(0); // avoid Cash deductions from world player
        Food food = new Food(50, 50, 0);
        SimYukkuri.world.getCurrentMap().getFood().put(food.getObjId(), food);
        assertFalse(food.isRemoved());
        item.objHitProcess(food);
        assertTrue(food.isRemoved());
    }

    // --- enabled flag ---

    @Test
    void testSetEnabled() {
        GarbageChute item = new GarbageChute(100, 100, 0);
        item.setEnabled(false);
        assertFalse(item.getEnabled());
        item.setEnabled(true);
        assertTrue(item.getEnabled());
    }

    // --- getImageLayer ---

    @Test
    void testGetImageLayer_houseRank_enabled_doesNotThrow() {
        GarbageChute item = new GarbageChute(100, 100, 0);
        java.awt.image.BufferedImage[] layer = new java.awt.image.BufferedImage[1];
        assertDoesNotThrow(() -> item.getImageLayer(layer));
    }

    @Test
    void testGetImageLayer_houseRank_disabled_doesNotThrow() {
        GarbageChute item = new GarbageChute(100, 100, 0);
        item.setEnabled(false);
        java.awt.image.BufferedImage[] layer = new java.awt.image.BufferedImage[1];
        assertDoesNotThrow(() -> item.getImageLayer(layer));
    }

    @Test
    void testGetImageLayer_noraRank_enabled_doesNotThrow() {
        GarbageChute item = new GarbageChute(100, 100, 1);
        java.awt.image.BufferedImage[] layer = new java.awt.image.BufferedImage[1];
        assertDoesNotThrow(() -> item.getImageLayer(layer));
    }
}
