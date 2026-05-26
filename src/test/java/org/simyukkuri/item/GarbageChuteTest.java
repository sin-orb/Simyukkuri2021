package org.simyukkuri.item;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.LinkedList;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.simyukkuri.SimYukkuri;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.living.yukkuri.impl.Reimu;
import org.simyukkuri.entity.core.world.WorldEntity.ItemRank;
import org.simyukkuri.entity.core.world.item.Diffuser;
import org.simyukkuri.entity.core.world.item.Food;
import org.simyukkuri.entity.core.world.item.GarbageChute;
import org.simyukkuri.entity.core.world.item.ItemTestBase;
import org.simyukkuri.enums.AgeState;
import org.simyukkuri.enums.Happiness;
import org.simyukkuri.enums.WorldEntityKind;
import org.simyukkuri.system.Sprite;

class GarbageChuteTest extends ItemTestBase {

    private static Yukkuri createBody() {
        Yukkuri b = new Reimu();
        b.setAgeState(AgeState.ADULT);
        Sprite[] spr = new Sprite[3];
        for (int i = 0; i < 3; i++) {
            spr[i] = new Sprite(10, 10, Sprite.PIVOT_CENTER_BOTTOM);
        }
        b.setSpriteSet(spr);
        SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(b.getUniqueId(), b);
        return b;
    }

    // --- Default constructor ---

    @Test
    void testConstructor_Default() {
        GarbageChute item = new GarbageChute();
        item.setObjId(1);
        SimYukkuri.world.getCurrentWorldState().getGarbageChutes().put(item.getObjId(), item);
        verifyCommonProperties(item);
        assertTrue(SimYukkuri.world.getCurrentWorldState().getGarbageChutes().containsKey(item.getObjId()));
    }

    // --- Parameterized constructor: HOUSE rank (option=0) ---

    @Test
    void testConstructor_HouseRank() {
        GarbageChute item = new GarbageChute(100, 100, 0);
        assertEquals(ItemRank.HOUSE, item.getItemRank());
        assertEquals(5000, item.getValue());
        assertEquals(5, item.getCost());
        assertTrue(SimYukkuri.world.getCurrentWorldState().getGarbageChutes().containsKey(item.getObjId()));
    }

    // --- Parameterized constructor: NORA rank (option=1) ---

    @Test
    void testConstructor_NoraRank() {
        GarbageChute item = new GarbageChute(100, 100, 1);
        assertEquals(ItemRank.NORA, item.getItemRank());
        assertEquals(0, item.getValue());
        assertEquals(0, item.getCost());
    }

    // --- WorldEntityKind ---

    @Test
    void testObjEXType_GARBAGECHUTE() {
        GarbageChute item = new GarbageChute(100, 100, 0);
        assertEquals(WorldEntityKind.GARBAGECHUTE, item.getWorldEntityType());
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
        SimYukkuri.world.getCurrentWorldState().getGarbageChutes().put(99, item);
        assertTrue(SimYukkuri.world.getCurrentWorldState().getGarbageChutes().containsKey(99));
        item.removeFromWorld();
        assertFalse(SimYukkuri.world.getCurrentWorldState().getGarbageChutes().containsKey(99));
    }

    // --- getters/setters ---

    @Test
    void testGetSetBindObjList() {
        GarbageChute item = new GarbageChute();
        List<org.simyukkuri.entity.core.Entity> list = new LinkedList<>();
        item.setBoundObjects(list);
        assertEquals(list, item.getBoundObjects());
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
        Yukkuri b = createBody();
        item.setBoundYukkuri(b);
        assertEquals(b, item.getBoundYukkuri());
        item.setBoundYukkuri(null);
        assertNull(item.getBoundYukkuri());
    }

    // --- upDate: empty bindObjList ---

    @Test
    void testUpDate_emptyList_doesNothing() {
        GarbageChute item = new GarbageChute();
        item.setBoundObjects(new LinkedList<>());
        assertDoesNotThrow(() -> item.upDate());
    }

    // --- upDate: null bindObjList ---

    @Test
    void testUpDate_nullList_doesNothing() {
        GarbageChute item = new GarbageChute();
        item.setBoundObjects(null);
        assertDoesNotThrow(() -> item.upDate());
    }

    // --- upDate: list with a removed object ---

    @Test
    void testUpDate_removedObj_clearsFromList() {
        GarbageChute item = new GarbageChute(100, 100, 0);
        Food food = new Food(50, 50, 0);
        food.setRemoved(true);
        List<org.simyukkuri.entity.core.Entity> list = new LinkedList<>();
        list.add(food);
        item.setBoundObjects(list);
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
        SimYukkuri.world.getCurrentWorldState().getFoods().put(food.getObjId(), food);
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
        SimYukkuri.world.getCurrentWorldState().getFoods().put(food.getObjId(), food);
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

    @Nested
    class RegressionScenarios {
        @Test
        void testScenario_LiveBodyStartsFallingAndCostsCash() {
            GarbageChute item = new GarbageChute(100, 100, 0);
            Yukkuri body = createBody();
            long beforeCash = SimYukkuri.world.getPlayer().getCash();

            assertEquals(0, item.objHitProcess(body));

            assertSame(body, item.getBoundYukkuri());
            assertTrue(item.getBoundObjects().contains(body));
            assertEquals(Happiness.VERY_SAD, body.getHappiness());
            assertTrue(body.isFallingUnderGround());
            assertEquals(beforeCash - item.getCost(), SimYukkuri.world.getPlayer().getCash());
        }

        @Test
        void testScenario_DeepFallingBodyIsRemovedFromChuteOnUpdate() {
            GarbageChute item = new GarbageChute(100, 100, 0);
            Yukkuri body = createBody();
            body.setZ(-1000);
            item.getBoundObjects().add(body);

            item.upDate();

            assertTrue(body.isRemoved());
            assertFalse(item.getBoundObjects().contains(body));
        }
    }
}
