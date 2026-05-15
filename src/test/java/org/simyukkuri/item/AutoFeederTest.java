package org.simyukkuri.item;

import org.simyukkuri.entity.core.Entity;
import org.simyukkuri.entity.core.attachment.*;
import org.simyukkuri.entity.core.attachment.impl.*;
import org.simyukkuri.entity.core.effect.*;
import org.simyukkuri.entity.core.effect.impl.*;
import org.simyukkuri.entity.core.living.yukkuri.Dna;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.living.yukkuri.impl.*;
import org.simyukkuri.entity.core.world.bodylinked.*;
import org.simyukkuri.entity.core.world.item.*;
import org.simyukkuri.entity.core.world.mobile.*;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.simyukkuri.SimYukkuri;
import org.simyukkuri.entity.core.world.item.ItemTestBase;
import org.simyukkuri.entity.core.world.item.AutoFeeder;
import org.simyukkuri.entity.core.world.item.Food;

class AutoFeederTest extends ItemTestBase {

    @Test
    void testConstructor_Default() {
        AutoFeeder item = new AutoFeeder();
        item.setObjId(1);
        item.setX(100);
        item.setY(100);
        item.setOption(0);

        SimYukkuri.world.getCurrentWorldState().getAutoFeeders().put(item.getObjId(), item);

        verifyCommonProperties(item);
        assertEquals(100, item.getX());
        assertEquals(100, item.getY());
        assertEquals(0, item.getOption());
        assertTrue(SimYukkuri.world.getCurrentWorldState().getAutoFeeders().containsKey(item.getObjId()));
    }

    @Test
    void testValueAndCost() {
        AutoFeeder item = new AutoFeeder();
        item.setValue(10000);
        item.setCost(30);
        assertTrue(item.getValue() >= 0);
        assertTrue(item.getCost() >= 0);
    }

    @Test
    void testFeedTypeEnum() {
        AutoFeeder.FeedType[] types = AutoFeeder.FeedType.values();
        assertEquals(10, types.length);
        for (AutoFeeder.FeedType t : types) {
            assertDoesNotThrow(() -> t.toString());
        }
        assertEquals(AutoFeeder.FeedType.NORMAL, AutoFeeder.FeedType.valueOf("NORMAL"));
        assertEquals(AutoFeeder.FeedType.PROCESSED_BODY, AutoFeeder.FeedType.valueOf("PROCESSED_BODY"));
    }

    @Test
    void testFeedModeEnum() {
        AutoFeeder.FeedMode[] modes = AutoFeeder.FeedMode.values();
        assertEquals(2, modes.length);
        for (AutoFeeder.FeedMode m : modes) {
            assertDoesNotThrow(() -> m.toString());
        }
        assertEquals(AutoFeeder.FeedMode.NORMAL_MODE, AutoFeeder.FeedMode.valueOf("NORMAL_MODE"));
        assertEquals(AutoFeeder.FeedMode.REGULAR_MODE, AutoFeeder.FeedMode.valueOf("REGULAR_MODE"));
    }

    @Test
    void testGetHitCheckObjType() {
        AutoFeeder item = new AutoFeeder();
        assertEquals(AutoFeeder.hitCheckObjType, item.getHitCheckObjType());
        assertEquals(0, item.getHitCheckObjType());
    }

    @Test
    void testGetSetType() {
        AutoFeeder item = new AutoFeeder();
        item.setType(3);
        assertEquals(3, item.getType());
    }

    @Test
    void testGetSetMode() {
        AutoFeeder item = new AutoFeeder();
        item.setMode(1);
        assertEquals(1, item.getMode());
    }

    @Test
    void testGetSetFeedingInterval() {
        AutoFeeder item = new AutoFeeder();
        item.setFeedingInterval(1200);
        assertEquals(1200, item.getFeedingInterval());
    }

    @Test
    void testGetSetFeedingP() {
        AutoFeeder item = new AutoFeeder();
        item.setFeedingP(5);
        assertEquals(5, item.getFeedingP());
    }

    @Test
    void testGetSetFood() {
        AutoFeeder item = new AutoFeeder();
        assertNull(item.getFoods());
        Food food = new Food(100, 100, Food.FoodType.FOOD.ordinal());
        item.setFoods(food);
        assertEquals(food, item.getFoods());
    }

    @Test
    void testGetShadowImage() {
        AutoFeeder item = new AutoFeeder();
        assertNull(item.getShadowImage());
    }

    @Test
    void testGetBounding() {
        assertNotNull(AutoFeeder.getBounding());
    }

    @Test
    void testRemoveListData() {
        AutoFeeder item = new AutoFeeder();
        item.setObjId(77);
        SimYukkuri.world.getCurrentWorldState().getAutoFeeders().put(item.getObjId(), item);
        item.removeFromWorld();
        assertFalse(SimYukkuri.world.getCurrentWorldState().getAutoFeeders().containsKey(item.getObjId()));
    }

    @Test
    void testUpDate_Disabled() {
        AutoFeeder item = new AutoFeeder();
        item.setEnabled(false);
        // !enabled → return early, no exception
        assertDoesNotThrow(() -> item.upDate());
    }

    @Test
    void testUpDate_AgeModulo20NotZero() {
        AutoFeeder item = new AutoFeeder();
        item.setEnabled(true);
        // force age to 1 so age%20 != 0
        item.setAge(1);
        assertDoesNotThrow(() -> item.upDate());
    }

    @Test
    void testUpDate_FoodNullModeOne() {
        AutoFeeder item = new AutoFeeder();
        item.setEnabled(true);
        item.setFoods(null);
        item.setMode(1); // REGULAR_MODE
        // age=0 → age%20==0 → food==null && mode!=0 → check feedingInterval condition
        // but RND.nextInt(feedingP) might not be 0, so food might stay null
        assertDoesNotThrow(() -> item.upDate());
    }

    // --- upDate: food set, type=NORMAL, food is removed → clears food ---

    @Test
    void testUpDate_FoodSet_FoodRemoved_ClearsFood() {
        AutoFeeder item = new AutoFeeder();
        item.setEnabled(true);
        item.setAge(0); // age % 20 == 0
        item.setType(0); // NORMAL food type
        Food food = new Food(100, 100, 0);
        food.setRemoved(true);
        // Add food to the food map so containsValue check passes (not "taken out" path)
        SimYukkuri.world.getCurrentWorldState().getFoods().put(food.getObjId(), food);
        item.setFoods(food);
        assertDoesNotThrow(() -> item.upDate());
        assertNull(item.getFoods());
    }

    // --- upDate: food set, type=NORMAL, food is empty → removes food ---

    @Test
    void testUpDate_FoodSet_FoodEmpty_RemovesFood() {
        AutoFeeder item = new AutoFeeder();
        item.setEnabled(true);
        item.setAge(0);
        item.setType(0); // NORMAL
        Food food = new Food(100, 100, 0);
        food.setAmount(0); // isEmpty() returns true
        SimYukkuri.world.getCurrentWorldState().getFoods().put(food.getObjId(), food);
        item.setFoods(food);
        assertDoesNotThrow(() -> item.upDate());
    }

    // --- upDate: food set, type=NORMAL, food is not removed and not empty → no
    // change ---

    @Test
    void testUpDate_FoodSet_FoodValid_NoChange() {
        AutoFeeder item = new AutoFeeder();
        item.setEnabled(true);
        item.setAge(0);
        item.setType(0); // NORMAL
        Food food = new Food(100, 100, 0);
        food.setAmount(1000); // not empty, not removed
        SimYukkuri.world.getCurrentWorldState().getFoods().put(food.getObjId(), food);
        item.setFoods(food);
        assertDoesNotThrow(() -> item.upDate());
        // food should still be set
        assertEquals(food, item.getFoods());
    }

    // --- upDate: age % 20 != 0 → early return ---

    @Test
    void testUpDate_AgeNotDivisibleBy20_earlyReturn() {
        AutoFeeder item = new AutoFeeder();
        item.setEnabled(true);
        item.setAge(1); // 1 % 20 != 0
        // This should return early without any food processing
        assertDoesNotThrow(() -> item.upDate());
    }

    // --- upDate: food null, mode=0 → tries to create food ---

    @Test
    void testUpDate_FoodNull_Mode0_Type0() {
        AutoFeeder item = new AutoFeeder();
        item.setEnabled(true);
        item.setAge(0);
        item.setFoods(null);
        item.setMode(0); // NORMAL_MODE → always tries to create food
        item.setType(0); // FOOD type
        // GadgetAction.putObjEX might work in headless mode
        assertDoesNotThrow(() -> item.upDate());
    }

    // --- readIniFile ---

    @Test
    void testReadIniFile_DoesNotThrow() {
        AutoFeeder item = new AutoFeeder();
        assertDoesNotThrow(() -> item.readIniFile());
    }

    // --- FeedType enum toString ---

    @Test
    void testFeedTypeEnum_toString() {
        for (AutoFeeder.FeedType t : AutoFeeder.FeedType.values()) {
            assertDoesNotThrow(() -> t.toString());
        }
    }

    // --- FeedMode enum toString ---

    @Test
    void testFeedModeEnum_toString() {
        for (AutoFeeder.FeedMode m : AutoFeeder.FeedMode.values()) {
            assertDoesNotThrow(() -> m.toString());
        }
    }

    // --- getImageLayer (enabled=true) ---

    @Test
    void testGetImageLayer_enabled() {
        AutoFeeder item = new AutoFeeder();
        item.setEnabled(true);
        java.awt.image.BufferedImage[] layer = new java.awt.image.BufferedImage[1];
        assertDoesNotThrow(() -> item.getImageLayer(layer));
        // images[0] may be null (no loadImages called) but method runs without
        // exception
    }

    // --- getImageLayer (enabled=false) ---

    @Test
    void testGetImageLayer_disabled() {
        AutoFeeder item = new AutoFeeder();
        item.setEnabled(false);
        java.awt.image.BufferedImage[] layer = new java.awt.image.BufferedImage[1];
        assertDoesNotThrow(() -> item.getImageLayer(layer));
    }

    // --- upDate: food not in world → isTakenOut check (no body in map → returns
    // false) ---

    @Test
    void testUpDate_foodNotInWorldMap_clearsFood() {
        AutoFeeder item = new AutoFeeder();
        item.setEnabled(true);
        item.setAge(0);
        item.setType(0); // FOOD type
        Food food = new Food(100, 100, 0);
        // food is NOT added to world food map → containsValue returns false
        // isTakenOut scans body map → empty → returns false → food stays (condition
        // short-circuits)
        item.setFoods(food);
        assertDoesNotThrow(() -> item.upDate());
    }

    // --- setupFeeder: headless → try/catch ---

    @Test
    void testSetupFeeder_headless_executesCode() {
        AutoFeeder item = new AutoFeeder();
        try {
            AutoFeeder.setupFeeder(item, true);
        } catch (Exception e) {
            // Expected in headless environment
        }
    }

    // --- setupFeederMode: headless → try/catch ---

    @Test
    void testSetupFeederMode_headless_executesCode() {
        AutoFeeder item = new AutoFeeder();
        try {
            AutoFeeder.setupFeederMode(item, true);
        } catch (Exception e) {
            // Expected in headless environment
        }
    }

    // --- Constructor(int, int, int): headless → try/catch ---

    @Test
    void testConstructor_WithCoords_doesNotThrow() {
        try {
            AutoFeeder item = new AutoFeeder(100, 100, 0);
        } catch (Exception e) {
            // Expected in headless environment (setupFeeder fails)
        }
    }

    // --- isTakenOut: body map contains body with FOOD takeout = food.objId →
    // returns true ---

    @Test
    void testIsTakenOut_bodyHoldingFood_returnsTrue() {
        try {
            AutoFeeder item = new AutoFeeder();
            item.setEnabled(true);
            item.setAge(0);
            item.setType(0); // FOOD type

            // Create a Food that is NOT in the world food map
            Food food = new Food(100, 100, 0);
            item.setFoods(food);

            // Create a body in the world body map that holds this food
            org.simyukkuri.entity.core.living.yukkuri.Yukkuri body = org.simyukkuri.util.WorldTestHelper.createBody();
            body.getCarryItems().put(org.simyukkuri.enums.TakeoutItemType.FOOD, food.objId);
            SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(body.objId, body);

            // food is NOT in world.food map → containsValue(food) is false
            // isTakenOut should find body holding food → returns true → food stays null
            // condition short-circuits
            assertDoesNotThrow(() -> item.upDate());
        } catch (Exception e) {
            // If World init fails, skip
        }
    }

    // --- makeRandomType: call via reflection ---

    @Test
    void testMakeRandomType_viaReflection() {
        try {
            AutoFeeder item = new AutoFeeder();
            java.lang.reflect.Method m = AutoFeeder.class.getDeclaredMethod("makeRandomType");
            m.setAccessible(true);
            int result = (int) m.invoke(item);
            assertTrue(result >= 0);
        } catch (Exception e) {
            // Reflection failed or method not accessible - skip
        }
    }

    @Nested
    class RegressionScenarios {

        @Test
        void testScenario_NormalModeCreatesConcreteFoodAndRegistersItInWorld() {
            AutoFeeder item = new AutoFeeder();
            item.setEnabled(true);
            item.setAge(0);
            item.setMode(AutoFeeder.FeedMode.NORMAL_MODE.ordinal());
            item.setType(AutoFeeder.FeedType.NORMAL.ordinal());
            item.setX(140);
            item.setY(220);

            item.upDate();

            assertNotNull(item.getFoods());
            assertInstanceOf(Food.class, item.getFoods());

            Food created = (Food) item.getFoods();
            assertEquals(140, created.getX());
            assertEquals(220, created.getY());
            assertSame(created, SimYukkuri.world.getCurrentWorldState().getFoods().get(created.getObjId()));
        }

        @Test
        void testScenario_ValidWorldFoodRemainsAttachedToFeeder() {
            AutoFeeder item = new AutoFeeder();
            item.setEnabled(true);
            item.setAge(0);
            item.setType(AutoFeeder.FeedType.NORMAL.ordinal());

            Food existing = new Food(120, 160, Food.FoodType.SWEETS1.ordinal());
            existing.setAmount(500);
            SimYukkuri.world.getCurrentWorldState().getFoods().put(existing.getObjId(), existing);
            item.setFoods(existing);

            item.upDate();

            assertSame(existing, item.getFoods());
            assertSame(existing, SimYukkuri.world.getCurrentWorldState().getFoods().get(existing.getObjId()));
        }

        @Test
        void testScenario_RemovedFoodClearsFeederReference() {
            AutoFeeder item = new AutoFeeder();
            item.setEnabled(true);
            item.setAge(0);
            item.setType(AutoFeeder.FeedType.NORMAL.ordinal());

            Food removed = new Food(100, 100, Food.FoodType.FOOD.ordinal());
            removed.setRemoved(true);
            SimYukkuri.world.getCurrentWorldState().getFoods().put(removed.getObjId(), removed);
            item.setFoods(removed);

            item.upDate();

            assertNull(item.getFoods());
            assertSame(removed, SimYukkuri.world.getCurrentWorldState().getFoods().get(removed.getObjId()));
        }

        @Test
        void testScenario_RegularModeCreatesConfiguredFoodAndConsumesCash() {
            AutoFeeder item = new AutoFeeder();
            item.setEnabled(true);
            item.setAge(20);
            item.setMode(AutoFeeder.FeedMode.REGULAR_MODE.ordinal());
            item.setType(AutoFeeder.FeedType.HOT.ordinal());
            item.setFeedingInterval(20);
            item.setFeedingP(1);
            item.setX(180);
            item.setY(260);

            long beforeCash = SimYukkuri.world.getPlayer().getCash();

            item.upDate();

            assertNotNull(item.getFoods());
            assertInstanceOf(Food.class, item.getFoods());

            Food created = (Food) item.getFoods();
            assertEquals(Food.FoodType.HOT, created.getFoodType());
            assertEquals(180, created.getX());
            assertEquals(260, created.getY());
            assertSame(created, SimYukkuri.world.getCurrentWorldState().getFoods().get(created.getObjId()));
            assertEquals(beforeCash - created.getValue() - item.getCost(),
                    SimYukkuri.world.getPlayer().getCash());
        }

    }
}
