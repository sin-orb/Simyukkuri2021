package org.simyukkuri.item;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.simyukkuri.SimYukkuri;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.world.item.Food;
import org.simyukkuri.entity.core.world.item.Food.FoodType;
import org.simyukkuri.entity.core.world.item.FoodMaker;
import org.simyukkuri.entity.core.world.item.ItemTestBase;
import org.simyukkuri.entity.core.world.mobile.Shit;
import org.simyukkuri.entity.core.world.mobile.Vomit;
import org.simyukkuri.enums.AgeState;
import org.simyukkuri.enums.Type;
import org.simyukkuri.enums.WorldEntityKind;
import org.simyukkuri.util.WorldTestHelper;

class FoodMakerTest extends ItemTestBase {

    @Test
    void testConstructor_Default() {
        FoodMaker item = new FoodMaker();
        item.setObjId(1);
        SimYukkuri.world.getCurrentWorldState().getFoodMakers().put(item.getObjId(), item);
        verifyCommonProperties(item);
        assertTrue(
                SimYukkuri.world
                        .getCurrentWorldState()
                        .getFoodMakers()
                        .containsKey(item.getObjId()));
    }

    // --- Static accessors ---

    @Test
    void testGetBounding_notNull() {
        assertNotNull(FoodMaker.getBounding());
        assertSame(FoodMaker.getBounding(), FoodMaker.getBounding());
    }

    @Test
    void testGetHitCheckObjType() {
        FoodMaker item = new FoodMaker();
        assertEquals(FoodMaker.hitCheckObjType, item.getHitCheckObjType());
    }

    @Test
    void testGetShadowImage_null() {
        FoodMaker item = new FoodMaker();
        assertNull(item.getShadowImage());
    }

    // --- processReady ---

    @Test
    void testGetSetProcessReady() {
        FoodMaker item = new FoodMaker();
        assertTrue(item.isProcessReady()); // default true
        item.setProcessReady(false);
        assertFalse(item.isProcessReady());
        item.setProcessReady(true);
        assertTrue(item.isProcessReady());
    }

    // --- stockFood ---

    @Test
    void testGetSetStockFood() {
        FoodMaker item = new FoodMaker();
        assertEquals(-1, item.getStockFood()); // default -1
        item.setStockFood(5);
        assertEquals(5, item.getStockFood());
    }

    // --- foodAmount ---

    @Test
    void testGetSetFoodAmount() {
        FoodMaker item = new FoodMaker();
        assertEquals(0, item.getFoodAmount());
        item.setFoodAmount(10);
        assertEquals(10, item.getFoodAmount());
    }

    // --- removeListData ---

    @Test
    void testRemoveListData() {
        FoodMaker item = new FoodMaker();
        item.setObjId(50);
        SimYukkuri.world.getCurrentWorldState().getFoodMakers().put(50, item);
        assertTrue(SimYukkuri.world.getCurrentWorldState().getFoodMakers().containsKey(50));
        item.removeFromWorld();
        assertFalse(SimYukkuri.world.getCurrentWorldState().getFoodMakers().containsKey(50));
    }

    // --- upDate ---

    @Test
    void testUpDate_processReady_doesNothing() {
        FoodMaker item = new FoodMaker();
        item.setProcessReady(true);
        item.setAge(4); // age%4==0
        assertDoesNotThrow(() -> item.upDate());
        assertTrue(item.isProcessReady()); // still true
    }

    @Test
    void testUpDate_notReadyAge0_setsReady() {
        FoodMaker item = new FoodMaker();
        item.setProcessReady(false);
        item.setAge(0); // 0 % 4 == 0
        item.upDate();
        assertTrue(item.isProcessReady());
    }

    @Test
    void testUpDate_notReadyAge1_staysNotReady() {
        FoodMaker item = new FoodMaker();
        item.setProcessReady(false);
        item.setAge(1); // 1 % 4 != 0
        item.upDate();
        assertFalse(item.isProcessReady());
    }

    // --- objHitProcess: processReady=false ---

    @Test
    void testObjHitProcess_notReady_returns0() {
        FoodMaker item = new FoodMaker();
        item.setProcessReady(false);
        Yukkuri body = WorldTestHelper.createBody();
        assertEquals(0, item.objHitProcess(body));
    }

    // --- objHitProcess: stockFood=-1, Shit ---

    @Test
    void testObjHitProcess_stockNegative_Shit_consumesInputAndLeavesStockEmpty() {
        FoodMaker item = new FoodMaker();
        item.setProcessReady(true);
        Shit shit = new Shit();
        shit.setObjType(Type.SHIT);
        shit.setX(100);
        shit.setY(100);
        SimYukkuri.world.getCurrentWorldState().getShit().put(shit.getObjId(), shit);

        assertEquals(0, item.objHitProcess(shit));
        assertEquals(-1, item.getStockFood());
        assertTrue(shit.isRemoved());
    }

    // --- objHitProcess: stockFood=-1, Vomit ---

    @Test
    void testObjHitProcess_stockNegative_Vomit_consumesInputAndLeavesStockEmpty() {
        FoodMaker item = new FoodMaker();
        item.setProcessReady(true);
        Vomit vomit = new Vomit();
        vomit.setObjType(Type.VOMIT);
        SimYukkuri.world.getCurrentWorldState().getVomit().put(vomit.getObjId(), vomit);

        assertEquals(0, item.objHitProcess(vomit));
        assertEquals(-1, item.getStockFood());
        assertTrue(vomit.isRemoved());
    }

    // --- objHitProcess: stockFood=-1, Food(FOOD) ---

    @Test
    void testObjHitProcess_stockNegative_FoodNormal_consumesInputAndLeavesStockEmpty() {
        FoodMaker item = new FoodMaker();
        item.setProcessReady(true);
        Food food = new Food();
        food.setObjType(Type.OBJECT);
        food.setFoodType(FoodType.FOOD);
        food.setObjId(99);
        SimYukkuri.world.getCurrentWorldState().getFoods().put(food.getObjId(), food);

        assertEquals(0, item.objHitProcess(food));
        assertEquals(-1, item.getStockFood());
        assertTrue(food.isRemoved());
    }

    // --- objHitProcess: stockFood=-1, Food(BITTER) ---

    @Test
    void testObjHitProcess_stockNegative_FoodBitter_consumesInputAndLeavesStockEmpty() {
        FoodMaker item = new FoodMaker();
        item.setProcessReady(true);
        Food food = new Food();
        food.setObjType(Type.OBJECT);
        food.setFoodType(FoodType.BITTER);
        food.setObjId(100);
        SimYukkuri.world.getCurrentWorldState().getFoods().put(food.getObjId(), food);

        assertEquals(0, item.objHitProcess(food));
        assertEquals(-1, item.getStockFood());
        assertTrue(food.isRemoved());
    }

    // --- objHitProcess: stockFood=-1, Food(LEMONPOP) ---

    @Test
    void testObjHitProcess_stockNegative_FoodLemonpop_consumesInputAndLeavesStockEmpty() {
        FoodMaker item = new FoodMaker();
        item.setProcessReady(true);
        Food food = new Food();
        food.setObjType(Type.OBJECT);
        food.setFoodType(FoodType.LEMONPOP);
        food.setObjId(101);
        SimYukkuri.world.getCurrentWorldState().getFoods().put(food.getObjId(), food);

        assertEquals(0, item.objHitProcess(food));
        assertEquals(-1, item.getStockFood());
        assertTrue(food.isRemoved());
    }

    // --- objHitProcess: stockFood=-1, Food(HOT) ---

    @Test
    void testObjHitProcess_stockNegative_FoodHot_consumesInputAndLeavesStockEmpty() {
        FoodMaker item = new FoodMaker();
        item.setProcessReady(true);
        Food food = new Food();
        food.setObjType(Type.OBJECT);
        food.setFoodType(FoodType.HOT);
        food.setObjId(102);
        SimYukkuri.world.getCurrentWorldState().getFoods().put(food.getObjId(), food);

        assertEquals(0, item.objHitProcess(food));
        assertEquals(-1, item.getStockFood());
        assertTrue(food.isRemoved());
    }

    // --- objHitProcess: stockFood=-1, Food(VIYUGRA) ---

    @Test
    void testObjHitProcess_stockNegative_FoodViyugra_consumesInputAndLeavesStockEmpty() {
        FoodMaker item = new FoodMaker();
        item.setProcessReady(true);
        Food food = new Food();
        food.setObjType(Type.OBJECT);
        food.setFoodType(FoodType.VIYUGRA);
        food.setObjId(103);
        SimYukkuri.world.getCurrentWorldState().getFoods().put(food.getObjId(), food);

        assertEquals(0, item.objHitProcess(food));
        assertEquals(-1, item.getStockFood());
        assertTrue(food.isRemoved());
    }

    // --- objHitProcess: stockFood=-1, Food(WASTE) ---

    @Test
    void testObjHitProcess_stockNegative_FoodWaste_consumesInputAndLeavesStockEmpty() {
        FoodMaker item = new FoodMaker();
        item.setProcessReady(true);
        Food food = new Food();
        food.setObjType(Type.OBJECT);
        food.setFoodType(FoodType.WASTE);
        food.setObjId(104);
        SimYukkuri.world.getCurrentWorldState().getFoods().put(food.getObjId(), food);

        assertEquals(0, item.objHitProcess(food));
        assertEquals(-1, item.getStockFood());
        assertTrue(food.isRemoved());
    }

    // --- getImageLayer (enabled=true) ---

    @Test
    void testGetImageLayer_enabled() {
        FoodMaker item = new FoodMaker();
        item.setEnabled(true);
        item.setAge(0);
        java.awt.image.BufferedImage[] layer = new java.awt.image.BufferedImage[1];
        int count = item.getImageLayer(layer);
        assertEquals(1, count);
    }

    // --- getImageLayer (enabled=false) ---

    @Test
    void testGetImageLayer_disabled() {
        FoodMaker item = new FoodMaker();
        item.setEnabled(false);
        java.awt.image.BufferedImage[] layer = new java.awt.image.BufferedImage[1];
        int count = item.getImageLayer(layer);
        assertEquals(1, count);
    }

    // --- Constructor(int, int, int): executes code path ---

    @Test
    void testConstructor_WithCoords_registersFoodMakerInWorld() {
        final FoodMaker item = new FoodMaker(100, 100, 0);
        assertNotNull(item);
        assertEquals(100, item.getX());
        assertEquals(100, item.getY());
        assertEquals(WorldEntityKind.FOODMAKER, item.getWorldEntityType());
        assertTrue(
                SimYukkuri.world
                        .getCurrentWorldState()
                        .getFoodMakers()
                        .containsKey(item.getObjId()));
    }

    @Test
    void testLoadImages_initializesBoundingAndLayers() {
        assertDoesNotThrow(() -> FoodMaker.loadImages(FoodMaker.class.getClassLoader(), null));
        assertNotNull(FoodMaker.getBounding());
        assertTrue(FoodMaker.getBounding().getWidth() > 0);
        assertTrue(FoodMaker.getBounding().getHeight() > 0);
    }

    // --- objHitProcess: stockFood >= 0 (second branch) ---

    @Test
    void testObjHitProcess_stockPositive_FoodFOOD_consumesInputAndProducesFood() {
        FoodMaker item = new FoodMaker();
        item.setProcessReady(true);
        item.setStockFood(5); // FOOD stock type
        item.setFoodAmount(4);

        Food food = new Food();
        food.setObjType(Type.OBJECT);
        food.setFoodType(FoodType.FOOD);
        food.setObjId(200);
        SimYukkuri.world.getCurrentWorldState().getFoods().put(food.getObjId(), food);

        long beforeCash = SimYukkuri.world.getPlayer().getCash();
        assertEquals(0, item.objHitProcess(food));

        assertEquals(beforeCash - item.getCost(), SimYukkuri.world.getPlayer().getCash());
        assertTrue(food.isRemoved());
        assertEquals(-1, item.getStockFood());
        assertEquals(0, item.getFoodAmount());
    }

    @Test
    void testObjHitProcess_stockPositive_FoodBITTER_executesCode() {
        FoodMaker item = new FoodMaker();
        item.setProcessReady(true);
        item.setStockFood(5);

        Food food = new Food();
        food.setObjType(Type.OBJECT);
        food.setFoodType(FoodType.BITTER);
        food.setObjId(201);
        SimYukkuri.world.getCurrentWorldState().getFoods().put(food.getObjId(), food);
        int result = item.objHitProcess(food);
        assertEquals(0, result);
        assertTrue(food.isRemoved());
    }

    @Test
    void testObjHitProcess_stockPositive_FoodLEMONPOP_executesCode() {
        FoodMaker item = new FoodMaker();
        item.setProcessReady(true);
        item.setStockFood(5);

        Food food = new Food();
        food.setObjType(Type.OBJECT);
        food.setFoodType(FoodType.LEMONPOP);
        food.setObjId(202);
        SimYukkuri.world.getCurrentWorldState().getFoods().put(food.getObjId(), food);
        int result = item.objHitProcess(food);
        assertEquals(0, result);
        assertTrue(food.isRemoved());
    }

    @Test
    void testObjHitProcess_stockPositive_FoodHOT_executesCode() {
        FoodMaker item = new FoodMaker();
        item.setProcessReady(true);
        item.setStockFood(5);

        Food food = new Food();
        food.setObjType(Type.OBJECT);
        food.setFoodType(FoodType.HOT);
        food.setObjId(203);
        SimYukkuri.world.getCurrentWorldState().getFoods().put(food.getObjId(), food);
        int result = item.objHitProcess(food);
        assertEquals(0, result);
        assertTrue(food.isRemoved());
    }

    @Test
    void testObjHitProcess_stockPositive_Shit_consumesInputAndCreatesOutputFood() {
        FoodMaker item = new FoodMaker();
        item.setProcessReady(true);
        item.setStockFood(12);
        item.setFoodAmount(4); // foodAmount >> 1 = 2 > 0

        Shit shit = new Shit();
        shit.setObjType(Type.SHIT);
        shit.setObjId(204);

        long beforeCash = SimYukkuri.world.getPlayer().getCash();
        final int beforeFoodCount = SimYukkuri.world.getCurrentWorldState().getFoods().size();
        assertEquals(0, item.objHitProcess(shit));

        final long outputFoodCount =
                SimYukkuri.world.getCurrentWorldState().getFoods().values().stream()
                        .filter(food -> food.getFoodType() == FoodType.SWEETS1)
                        .count();

        assertEquals(beforeCash - item.getCost(), SimYukkuri.world.getPlayer().getCash());
        assertTrue(shit.isRemoved());
        assertEquals(beforeFoodCount + 2, SimYukkuri.world.getCurrentWorldState().getFoods().size());
        assertEquals(2, outputFoodCount);
        assertEquals(-1, item.getStockFood());
        assertEquals(0, item.getFoodAmount());
    }

    @Test
    void testObjHitProcess_stockPositive_Vomit_consumesInputAndCreatesOutputFood() {
        FoodMaker item = new FoodMaker();
        item.setProcessReady(true);
        item.setStockFood(13);
        item.setFoodAmount(4);

        Vomit vomit = new Vomit();
        vomit.setObjType(Type.VOMIT);
        vomit.setObjId(205);

        long beforeCash = SimYukkuri.world.getPlayer().getCash();
        final int beforeFoodCount = SimYukkuri.world.getCurrentWorldState().getFoods().size();
        assertEquals(0, item.objHitProcess(vomit));

        final long outputFoodCount =
                SimYukkuri.world.getCurrentWorldState().getFoods().values().stream()
                        .filter(food -> food.getFoodType() == FoodType.SWEETS2)
                        .count();

        assertEquals(beforeCash - item.getCost(), SimYukkuri.world.getPlayer().getCash());
        assertTrue(vomit.isRemoved());
        assertEquals(beforeFoodCount + 2, SimYukkuri.world.getCurrentWorldState().getFoods().size());
        assertEquals(2, outputFoodCount);
        assertEquals(-1, item.getStockFood());
        assertEquals(0, item.getFoodAmount());
    }

    @Test
    void testObjHitProcess_stockPositive_Body_Reimu_consumesBodyAndConsumesCash() {
        FoodMaker item = new FoodMaker();
        item.setProcessReady(true);
        item.setStockFood(0); // FOOD stock

        org.simyukkuri.entity.core.living.yukkuri.impl.Reimu body =
                new org.simyukkuri.entity.core.living.yukkuri.impl.Reimu();
        body.setObjType(Type.YUKKURI);
        body.setCrushed(true); // isCrushed=true → enters branch
        body.setAgeState(org.simyukkuri.enums.AgeState.BABY);
        body.setObjId(206);
        SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(body.getUniqueId(), body);

        long beforeCash = SimYukkuri.world.getPlayer().getCash();
        assertEquals(0, item.objHitProcess(body));
        assertEquals(beforeCash - item.getCost(), SimYukkuri.world.getPlayer().getCash());
        assertEquals(0, item.getFoodAmount());
        assertEquals(-1, item.getStockFood());
        assertTrue(body.isRemoved());
    }

    // --- upDate: age % 4 == 0 and processReady=false → sets processReady=true ---

    @Test
    void testUpDate_processReadyFalse_setsTrue() {
        FoodMaker item = new FoodMaker();
        item.setProcessReady(false);
        item.setAge(0); // 0 % 4 == 0
        item.upDate();
        assertTrue(item.isProcessReady());
    }

    @Nested
    class RegressionScenarios {
        @Test
        void testScenario_CrushedBabyBodyContributesFoodAmountAndIsRemoved() {
            final FoodMaker item = new FoodMaker();
            Yukkuri body = WorldTestHelper.createBody();
            body.setObjType(Type.YUKKURI);
            body.setCrushed(true);
            body.setAgeState(AgeState.BABY);
            SimYukkuri.world
                    .getCurrentWorldState()
                    .getYukkuriRegistry()
                    .put(body.getUniqueId(), body);

            assertEquals(0, item.objHitProcess(body));
            assertEquals(1, item.getFoodAmount());
            assertEquals(-1, item.getStockFood());
            assertTrue(body.isRemoved());
        }

        @Test
        void testScenario_StoredFoodProcessesInputIntoOutputFoodAndConsumesCash() {
            FoodMaker item = new FoodMaker();
            item.setStockFood(5);
            item.setFoodAmount(4);
            item.setCost(30);
            item.setX(100);
            item.setY(100);

            Food bitterFood = new Food();
            bitterFood.setObjType(Type.OBJECT);
            bitterFood.setFoodType(FoodType.BITTER);
            bitterFood.setObjId(300);
            SimYukkuri.world
                    .getCurrentWorldState()
                    .getFoods()
                    .put(bitterFood.getObjId(), bitterFood);

            long beforeCash = SimYukkuri.world.getPlayer().getCash();
            final int beforeFoodCount = SimYukkuri.world.getCurrentWorldState().getFoods().size();

            assertEquals(0, item.objHitProcess(bitterFood));

            final long outputFoodCount =
                    SimYukkuri.world.getCurrentWorldState().getFoods().values().stream()
                            .filter(food -> food.getObjId() != bitterFood.getObjId())
                            .filter(food -> food.getFoodType() == FoodType.FOOD)
                            .count();

            assertEquals(beforeCash - item.getCost(), SimYukkuri.world.getPlayer().getCash());
            assertTrue(bitterFood.isRemoved());
            assertEquals(
                    beforeFoodCount + 2, SimYukkuri.world.getCurrentWorldState().getFoods().size());
            assertEquals(2, outputFoodCount);
            assertEquals(-1, item.getStockFood());
            assertEquals(0, item.getFoodAmount());
        }
    }
}
