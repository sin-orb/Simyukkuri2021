package src.item;

import org.junit.jupiter.api.Test;
import src.base.ItemTestBase;
import src.SimYukkuri;
import src.enums.ObjEXType;
import src.item.Food.FoodType;
import src.item.Food.EmptyImage;

import static org.junit.jupiter.api.Assertions.*;

class FoodTest extends ItemTestBase {

    // Constructor tests

    @Test
    void testConstructor_Default() {
        Food food = new Food();
        assertNotNull(food);
    }

    @Test
    void testConstructor_WithCoordinates() {
        Food food = new Food(100, 200, FoodType.FOOD.ordinal());
        assertNotNull(food);
        assertEquals(100, food.getX());
        assertEquals(200, food.getY());
    }

    @Test
    void testConstructor_RegistersInWorldFoodMap() {
        int before = SimYukkuri.world.getCurrentMap().getFood().size();
        Food food = new Food(50, 50, FoodType.FOOD.ordinal());
        int after = SimYukkuri.world.getCurrentMap().getFood().size();
        assertEquals(before + 1, after);
        assertTrue(SimYukkuri.world.getCurrentMap().getFood().containsKey(food.getObjId()));
    }

    @Test
    void testConstructor_SetsObjEXTypeToFood() {
        Food food = new Food(0, 0, FoodType.FOOD.ordinal());
        assertEquals(ObjEXType.FOOD, food.getObjEXType());
    }

    @Test
    void testConstructor_SetsRemovedFalse() {
        Food food = new Food(0, 0, FoodType.FOOD.ordinal());
        assertFalse(food.isRemoved());
    }

    @Test
    void testConstructor_SWEETS1() {
        Food food = new Food(10, 20, FoodType.SWEETS1.ordinal());
        assertNotNull(food);
        assertEquals(FoodType.SWEETS1, food.getFoodType());
    }

    @Test
    void testConstructor_SWEETS2() {
        Food food = new Food(10, 20, FoodType.SWEETS2.ordinal());
        assertEquals(FoodType.SWEETS2, food.getFoodType());
    }

    @Test
    void testConstructor_STALK() {
        Food food = new Food(10, 20, FoodType.STALK.ordinal());
        assertEquals(FoodType.STALK, food.getFoodType());
    }

    @Test
    void testConstructor_BITTER() {
        Food food = new Food(10, 20, FoodType.BITTER.ordinal());
        assertEquals(FoodType.BITTER, food.getFoodType());
    }

    @Test
    void testConstructor_LEMONPOP() {
        Food food = new Food(10, 20, FoodType.LEMONPOP.ordinal());
        assertEquals(FoodType.LEMONPOP, food.getFoodType());
    }

    @Test
    void testConstructor_HOT() {
        Food food = new Food(10, 20, FoodType.HOT.ordinal());
        assertEquals(FoodType.HOT, food.getFoodType());
    }

    @Test
    void testConstructor_VIYUGRA() {
        Food food = new Food(10, 20, FoodType.VIYUGRA.ordinal());
        assertEquals(FoodType.VIYUGRA, food.getFoodType());
    }

    @Test
    void testConstructor_WASTE() {
        Food food = new Food(10, 20, FoodType.WASTE.ordinal());
        assertEquals(FoodType.WASTE, food.getFoodType());
    }

    @Test
    void testConstructor_InitializesAmountFromFoodType() {
        Food food = new Food(0, 0, FoodType.FOOD.ordinal());
        assertEquals(FoodType.FOOD.getAmount(), food.getAmount());
    }

    // FoodType enum tests

    @Test
    void testFoodTypeEnum() {
        assertNotNull(Food.FoodType.FOOD);
        assertEquals(3, Food.FoodType.FOOD.ordinal());
    }

    @Test
    void testFoodTypeEnum_SWEETS1_Ordinal() {
        assertEquals(0, FoodType.SWEETS1.ordinal());
    }

    @Test
    void testFoodTypeEnum_SWEETS2_Ordinal() {
        assertEquals(1, FoodType.SWEETS2.ordinal());
    }

    @Test
    void testFoodTypeEnum_STALK_Ordinal() {
        assertEquals(2, FoodType.STALK.ordinal());
    }

    @Test
    void testFoodTypeEnum_getValue_FOOD() {
        assertEquals(250, FoodType.FOOD.getValue());
    }

    @Test
    void testFoodTypeEnum_getValue_SWEETS1() {
        assertEquals(500, FoodType.SWEETS1.getValue());
    }

    @Test
    void testFoodTypeEnum_getValue_SWEETS2() {
        assertEquals(1000, FoodType.SWEETS2.getValue());
    }

    @Test
    void testFoodTypeEnum_getValue_STALK() {
        assertEquals(0, FoodType.STALK.getValue());
    }

    @Test
    void testFoodTypeEnum_getValue_WASTE() {
        assertEquals(0, FoodType.WASTE.getValue());
    }

    @Test
    void testFoodTypeEnum_getValue_VOMIT() {
        assertEquals(0, FoodType.VOMIT.getValue());
    }

    @Test
    void testFoodTypeEnum_getValue_SHIT() {
        assertEquals(0, FoodType.SHIT.getValue());
    }

    @Test
    void testFoodTypeEnum_getValue_BITTER() {
        assertEquals(400, FoodType.BITTER.getValue());
    }

    @Test
    void testFoodTypeEnum_getValue_LEMONPOP() {
        assertEquals(300, FoodType.LEMONPOP.getValue());
    }

    @Test
    void testFoodTypeEnum_getValue_HOT() {
        assertEquals(400, FoodType.HOT.getValue());
    }

    @Test
    void testFoodTypeEnum_getValue_VIYUGRA() {
        assertEquals(1000, FoodType.VIYUGRA.getValue());
    }

    @Test
    void testFoodTypeEnum_getLooks_FOOD() {
        assertEquals(400, FoodType.FOOD.getLooks());
    }

    @Test
    void testFoodTypeEnum_getLooks_SWEETS1() {
        assertEquals(999, FoodType.SWEETS1.getLooks());
    }

    @Test
    void testFoodTypeEnum_getLooks_SWEETS2() {
        assertEquals(999, FoodType.SWEETS2.getLooks());
    }

    @Test
    void testFoodTypeEnum_getLooks_STALK() {
        assertEquals(500, FoodType.STALK.getLooks());
    }

    @Test
    void testFoodTypeEnum_getLooks_BITTER() {
        assertEquals(300, FoodType.BITTER.getLooks());
    }

    @Test
    void testFoodTypeEnum_getLooks_LEMONPOP() {
        assertEquals(300, FoodType.LEMONPOP.getLooks());
    }

    @Test
    void testFoodTypeEnum_getLooks_HOT() {
        assertEquals(150, FoodType.HOT.getLooks());
    }

    @Test
    void testFoodTypeEnum_getLooks_VIYUGRA() {
        assertEquals(150, FoodType.VIYUGRA.getLooks());
    }

    @Test
    void testFoodTypeEnum_getLooks_WASTE() {
        assertEquals(-50, FoodType.WASTE.getLooks());
    }

    @Test
    void testFoodTypeEnum_getLooks_SHIT() {
        assertEquals(-500, FoodType.SHIT.getLooks());
    }

    @Test
    void testFoodTypeEnum_getLooks_BODY() {
        assertEquals(-999, FoodType.BODY.getLooks());
    }

    @Test
    void testFoodTypeEnum_getLooks_VOMIT() {
        assertEquals(-100, FoodType.VOMIT.getLooks());
    }

    @Test
    void testFoodTypeEnum_getAmount_FOOD() {
        assertEquals(100 * 24 * 24, FoodType.FOOD.getAmount());
    }

    @Test
    void testFoodTypeEnum_getAmount_SWEETS1() {
        assertEquals(100 * 24 * 2, FoodType.SWEETS1.getAmount());
    }

    @Test
    void testFoodTypeEnum_getAmount_STALK() {
        assertEquals(100 * 24 * 4, FoodType.STALK.getAmount());
    }

    @Test
    void testFoodTypeEnum_getAmount_VOMIT() {
        assertEquals(0, FoodType.VOMIT.getAmount());
    }

    @Test
    void testFoodTypeEnum_getAmount_SHIT() {
        assertEquals(0, FoodType.SHIT.getAmount());
    }

    @Test
    void testFoodTypeEnum_getAmount_BODY() {
        assertEquals(0, FoodType.BODY.getAmount());
    }

    @Test
    void testFoodTypeEnum_getAmount_WASTE() {
        assertEquals(100 * 24 * 32, FoodType.WASTE.getAmount());
    }

    @Test
    void testFoodTypeEnum_getFileName_FOOD() {
        assertEquals("gohan1.png", FoodType.FOOD.getFileName());
    }

    @Test
    void testFoodTypeEnum_getFileName_SWEETS1() {
        assertEquals("sweets1.png", FoodType.SWEETS1.getFileName());
    }

    @Test
    void testFoodTypeEnum_getFileName_SWEETS2() {
        assertEquals("sweets2.png", FoodType.SWEETS2.getFileName());
    }

    @Test
    void testFoodTypeEnum_getFileName_STALK() {
        assertEquals("stalk_food.png", FoodType.STALK.getFileName());
    }

    @Test
    void testFoodTypeEnum_getFileName_VOMIT() {
        assertNull(FoodType.VOMIT.getFileName());
    }

    @Test
    void testFoodTypeEnum_getFileName_SHIT() {
        assertNull(FoodType.SHIT.getFileName());
    }

    @Test
    void testFoodTypeEnum_getFileName_BODY() {
        assertNull(FoodType.BODY.getFileName());
    }

    @Test
    void testFoodTypeEnum_getEmptyImg_FOOD() {
        assertEquals(EmptyImage.DISH, FoodType.FOOD.getEmptyImg());
    }

    @Test
    void testFoodTypeEnum_getEmptyImg_SWEETS1() {
        assertEquals(EmptyImage.SWEETS, FoodType.SWEETS1.getEmptyImg());
    }

    @Test
    void testFoodTypeEnum_getEmptyImg_STALK() {
        assertEquals(EmptyImage.STALK, FoodType.STALK.getEmptyImg());
    }

    @Test
    void testFoodTypeEnum_getEmptyImg_VOMIT() {
        assertNull(FoodType.VOMIT.getEmptyImg());
    }

    @Test
    void testFoodTypeEnum_getEmptyImg_WASTE() {
        assertEquals(EmptyImage.WASTE, FoodType.WASTE.getEmptyImg());
    }

    @Test
    void testFoodTypeEnum_hasShadow_FOOD() {
        assertTrue(FoodType.FOOD.hasShadow());
    }

    @Test
    void testFoodTypeEnum_hasShadow_SWEETS1() {
        assertTrue(FoodType.SWEETS1.hasShadow());
    }

    @Test
    void testFoodTypeEnum_hasShadow_STALK() {
        assertTrue(FoodType.STALK.hasShadow());
    }

    @Test
    void testFoodTypeEnum_hasShadow_BITTER() {
        assertTrue(FoodType.BITTER.hasShadow());
    }

    @Test
    void testFoodTypeEnum_hasShadow_WASTE() {
        assertFalse(FoodType.WASTE.hasShadow());
    }

    @Test
    void testFoodTypeEnum_hasShadow_VOMIT() {
        assertFalse(FoodType.VOMIT.hasShadow());
    }

    @Test
    void testFoodTypeEnum_hasShadow_SHIT() {
        assertFalse(FoodType.SHIT.hasShadow());
    }

    @Test
    void testFoodTypeEnum_hasShadow_BODY() {
        assertFalse(FoodType.BODY.hasShadow());
    }

    // Nora variants

    @Test
    void testFoodTypeEnum_FOOD_NORA_getLooks() {
        assertEquals(0, FoodType.FOOD_NORA.getLooks());
    }

    @Test
    void testFoodTypeEnum_WASTE_NORA_getLooks() {
        assertEquals(-300, FoodType.WASTE_NORA.getLooks());
    }

    @Test
    void testFoodTypeEnum_SWEETS_NORA1_hasShadow() {
        assertTrue(FoodType.SWEETS_NORA1.hasShadow());
    }

    @Test
    void testFoodTypeEnum_WASTE_NORA_hasShadow() {
        assertFalse(FoodType.WASTE_NORA.hasShadow());
    }

    @Test
    void testFoodTypeEnum_BITTER_NORA_getLooks() {
        assertEquals(-200, FoodType.BITTER_NORA.getLooks());
    }

    @Test
    void testFoodTypeEnum_LEMONPOP_NORA_getLooks() {
        assertEquals(-100, FoodType.LEMONPOP_NORA.getLooks());
    }

    @Test
    void testFoodTypeEnum_HOT_NORA_getLooks() {
        assertEquals(-200, FoodType.HOT_NORA.getLooks());
    }

    @Test
    void testFoodTypeEnum_VIYUGRA_NORA_getLooks() {
        assertEquals(-200, FoodType.VIYUGRA_NORA.getLooks());
    }

    // Yasei variants

    @Test
    void testFoodTypeEnum_FOOD_YASEI_hasShadow() {
        assertTrue(FoodType.FOOD_YASEI.hasShadow());
    }

    @Test
    void testFoodTypeEnum_WASTE_YASEI_hasShadow() {
        assertFalse(FoodType.WASTE_YASEI.hasShadow());
    }

    @Test
    void testFoodTypeEnum_BITTER_YASEI_getLooks() {
        assertEquals(-200, FoodType.BITTER_YASEI.getLooks());
    }

    @Test
    void testFoodTypeEnum_LEMONPOP_YASEI_getLooks() {
        assertEquals(-100, FoodType.LEMONPOP_YASEI.getLooks());
    }

    @Test
    void testFoodTypeEnum_HOT_YASEI_getLooks() {
        assertEquals(-200, FoodType.HOT_YASEI.getLooks());
    }

    @Test
    void testFoodTypeEnum_VIYUGRA_YASEI_getLooks() {
        assertEquals(-200, FoodType.VIYUGRA_YASEI.getLooks());
    }

    @Test
    void testFoodTypeEnum_WASTE_YASEI_getLooks() {
        assertEquals(-300, FoodType.WASTE_YASEI.getLooks());
    }

    @Test
    void testFoodTypeEnum_SWEETS_YASEI1_hasShadow() {
        assertTrue(FoodType.SWEETS_YASEI1.hasShadow());
    }

    // EmptyImage enum tests

    @Test
    void testEmptyImage_DISH_FileName() {
        assertEquals("empty.png", EmptyImage.DISH.getFileName());
    }

    @Test
    void testEmptyImage_SWEETS_FileName() {
        assertEquals("sweets_empty.png", EmptyImage.SWEETS.getFileName());
    }

    @Test
    void testEmptyImage_WASTE_FileName() {
        assertEquals("waste_empty.png", EmptyImage.WASTE.getFileName());
    }

    @Test
    void testEmptyImage_STALK_FileName_IsNull() {
        assertNull(EmptyImage.STALK.getFileName());
    }

    @Test
    void testEmptyImage_FUEL_FileName_IsNull() {
        assertNull(EmptyImage.FUEL.getFileName());
    }

    @Test
    void testEmptyImage_DISH_NORA_FileName() {
        assertEquals("empty_nora.png", EmptyImage.DISH_NORA.getFileName());
    }

    @Test
    void testEmptyImage_SWEETS_NORA_FileName() {
        assertEquals("sweets_nora_empty.png", EmptyImage.SWEETS_NORA.getFileName());
    }

    @Test
    void testEmptyImage_WASTE_NORA_FileName() {
        assertEquals("waste_nora_empty.png", EmptyImage.WASTE_NORA.getFileName());
    }

    @Test
    void testEmptyImage_DISH_YASEI_FileName() {
        assertEquals("empty_yasei.png", EmptyImage.DISH_YASEI.getFileName());
    }

    @Test
    void testEmptyImage_SWEETS_YASEI_FileName() {
        assertEquals("sweets_yasei_empty.png", EmptyImage.SWEETS_YASEI.getFileName());
    }

    @Test
    void testEmptyImage_WASTE_YASEI_FileName() {
        assertEquals("waste_yasei_empty.png", EmptyImage.WASTE_YASEI.getFileName());
    }

    @Test
    void testEmptyImage_Values_Count() {
        assertEquals(11, EmptyImage.values().length);
    }

    // Getter / Setter tests

    @Test
    void testGetFoodType_Default() {
        Food food = new Food();
        food.setFoodType(FoodType.BITTER);
        assertEquals(FoodType.BITTER, food.getFoodType());
    }

    @Test
    void testSetFoodType_AllTypes() {
        Food food = new Food();
        for (FoodType type : FoodType.values()) {
            food.setFoodType(type);
            assertEquals(type, food.getFoodType());
        }
    }

    @Test
    void testGetAmount_DefaultFood() {
        Food food = new Food(0, 0, FoodType.FOOD.ordinal());
        assertEquals(FoodType.FOOD.getAmount(), food.getAmount());
    }

    @Test
    void testSetAmount_Positive() {
        Food food = new Food(0, 0, FoodType.FOOD.ordinal());
        food.setAmount(12345);
        assertEquals(12345, food.getAmount());
    }

    @Test
    void testSetAmount_Zero() {
        Food food = new Food(0, 0, FoodType.FOOD.ordinal());
        food.setAmount(0);
        assertEquals(0, food.getAmount());
    }

    @Test
    void testSetAmount_Negative() {
        Food food = new Food(0, 0, FoodType.FOOD.ordinal());
        food.setAmount(-1);
        assertEquals(-1, food.getAmount());
    }

    @Test
    void testGetValue_MatchesFoodType() {
        Food food = new Food(0, 0, FoodType.FOOD.ordinal());
        assertEquals(FoodType.FOOD.getValue(), food.getValue());
    }

    @Test
    void testGetValue_SWEETS1() {
        Food food = new Food(0, 0, FoodType.SWEETS1.ordinal());
        assertEquals(500, food.getValue());
    }

    @Test
    void testGetValue_SWEETS2() {
        Food food = new Food(0, 0, FoodType.SWEETS2.ordinal());
        assertEquals(1000, food.getValue());
    }

    @Test
    void testGetLooks_MatchesFoodType() {
        Food food = new Food(0, 0, FoodType.FOOD.ordinal());
        assertEquals(FoodType.FOOD.getLooks(), food.getLooks());
    }

    @Test
    void testGetLooks_SWEETS1() {
        Food food = new Food(0, 0, FoodType.SWEETS1.ordinal());
        assertEquals(999, food.getLooks());
    }

    @Test
    void testGetLooks_WASTE() {
        Food food = new Food(0, 0, FoodType.WASTE.ordinal());
        assertEquals(-50, food.getLooks());
    }

    // isEmpty and eatFood tests

    @Test
    void testIsEmpty_WhenAmountIsZero() {
        Food food = new Food(0, 0, FoodType.FOOD.ordinal());
        food.setAmount(0);
        assertTrue(food.isEmpty());
    }

    @Test
    void testIsEmpty_WhenAmountIsPositive() {
        Food food = new Food(0, 0, FoodType.FOOD.ordinal());
        food.setAmount(100);
        assertFalse(food.isEmpty());
    }

    @Test
    void testIsEmpty_WhenNewlyCreated_NonZeroAmount() {
        Food food = new Food(0, 0, FoodType.FOOD.ordinal());
        assertFalse(food.isEmpty());
    }

    @Test
    void testEatFood_ReducesAmount() {
        Food food = new Food(0, 0, FoodType.FOOD.ordinal());
        int initial = food.getAmount();
        food.eatFood(100);
        assertEquals(initial - 100, food.getAmount());
    }

    @Test
    void testEatFood_DoesNothingWhenAlreadyEmpty() {
        Food food = new Food(0, 0, FoodType.FOOD.ordinal());
        food.setAmount(0);
        food.eatFood(100);
        assertEquals(0, food.getAmount());
    }

    @Test
    void testEatFood_ClampedToZero_WhenOvereaten() {
        Food food = new Food(0, 0, FoodType.FOOD.ordinal());
        food.setAmount(50);
        food.eatFood(200);
        assertEquals(0, food.getAmount());
    }

    @Test
    void testEatFood_ExactlyDepletes() {
        Food food = new Food(0, 0, FoodType.FOOD.ordinal());
        int initial = food.getAmount();
        food.eatFood(initial);
        assertEquals(0, food.getAmount());
        assertTrue(food.isEmpty());
    }

    @Test
    void testEatFood_MultipleEats() {
        Food food = new Food(0, 0, FoodType.FOOD.ordinal());
        food.setAmount(300);
        food.eatFood(100);
        food.eatFood(100);
        assertEquals(100, food.getAmount());
    }

    @Test
    void testEatFood_ZeroEat_NoChange() {
        Food food = new Food(0, 0, FoodType.FOOD.ordinal());
        food.setAmount(500);
        food.eatFood(0);
        assertEquals(500, food.getAmount());
    }

    // kick tests

    @Test
    void testKick_DoesNotThrow() {
        Food food = new Food(0, 0, FoodType.FOOD.ordinal());
        assertDoesNotThrow(() -> food.kick());
    }

    // getShadowImage tests

    @Test
    void testGetShadowImage_DoesNotThrow() {
        Food food = new Food(0, 0, FoodType.FOOD.ordinal());
        assertDoesNotThrow(() -> food.getShadowImage());
    }

    // removeListData tests

    @Test
    void testRemoveListData_RemovesFoodFromWorldMap() {
        Food food = new Food(50, 50, FoodType.FOOD.ordinal());
        int id = food.getObjId();
        assertTrue(SimYukkuri.world.getCurrentMap().getFood().containsKey(id));
        food.removeListData();
        assertFalse(SimYukkuri.world.getCurrentMap().getFood().containsKey(id));
    }

    @Test
    void testRemoveListData_CalledTwiceDoesNotThrow() {
        Food food = new Food(50, 50, FoodType.FOOD.ordinal());
        food.removeListData();
        assertDoesNotThrow(() -> food.removeListData());
    }

    // getBounding / getFoodBounding tests

    @Test
    void testGetBounding_DoesNotThrow() {
        assertDoesNotThrow(() -> Food.getBounding());
    }

    @Test
    void testGetFoodBounding_DoesNotThrow() {
        assertDoesNotThrow(() -> Food.getFoodBounding(FoodType.FOOD));
    }

    @Test
    void testGetFoodBounding_AllTypes_NoThrow() {
        for (FoodType type : FoodType.values()) {
            assertDoesNotThrow(() -> Food.getFoodBounding(type));
        }
    }

    // enabled / setEnabled / getEnabled tests

    @Test
    void testGetEnabled_DefaultTrue() {
        Food food = new Food(0, 0, FoodType.FOOD.ordinal());
        assertTrue(food.getEnabled());
    }

    @Test
    void testSetEnabled_False() {
        Food food = new Food(0, 0, FoodType.FOOD.ordinal());
        food.setEnabled(false);
        assertFalse(food.getEnabled());
    }

    @Test
    void testSetEnabled_True() {
        Food food = new Food(0, 0, FoodType.FOOD.ordinal());
        food.setEnabled(false);
        food.setEnabled(true);
        assertTrue(food.getEnabled());
    }

    // isRemoved / setRemoved tests

    @Test
    void testIsRemoved_DefaultFalse() {
        Food food = new Food(0, 0, FoodType.FOOD.ordinal());
        assertFalse(food.isRemoved());
    }

    @Test
    void testSetRemoved_True() {
        Food food = new Food(0, 0, FoodType.FOOD.ordinal());
        food.setRemoved(true);
        assertTrue(food.isRemoved());
    }

    @Test
    void testSetRemoved_FalseAgain() {
        Food food = new Food(0, 0, FoodType.FOOD.ordinal());
        food.setRemoved(true);
        food.setRemoved(false);
        assertFalse(food.isRemoved());
    }

    // objHitProcess tests

    @Test
    void testObjHitProcess_ReturnsZero() {
        Food food = new Food(0, 0, FoodType.FOOD.ordinal());
        int result = food.objHitProcess(null);
        assertEquals(0, result);
    }

    // getObjId tests

    @Test
    void testGetObjId_NonZero() {
        Food food = new Food(0, 0, FoodType.FOOD.ordinal());
        assertTrue(food.getObjId() > 0);
    }

    @Test
    void testGetObjId_UniquePerInstance() {
        Food food1 = new Food(0, 0, FoodType.FOOD.ordinal());
        Food food2 = new Food(10, 10, FoodType.BITTER.ordinal());
        assertNotEquals(food1.getObjId(), food2.getObjId());
    }

    // getObjEXType tests

    @Test
    void testGetObjEXType_IsFood() {
        Food food = new Food(0, 0, FoodType.FOOD.ordinal());
        assertEquals(ObjEXType.FOOD, food.getObjEXType());
    }

    // verifyCommonProperties

    @Test
    void testVerifyCommonProperties() {
        Food food = new Food(100, 100, FoodType.FOOD.ordinal());
        verifyCommonProperties(food);
    }

    // Multiple food creation

    @Test
    void testMultipleFoods_AllRegisteredInWorldMap() {
        int before = SimYukkuri.world.getCurrentMap().getFood().size();
        Food f1 = new Food(10, 10, FoodType.FOOD.ordinal());
        Food f2 = new Food(20, 20, FoodType.BITTER.ordinal());
        Food f3 = new Food(30, 30, FoodType.SWEETS1.ordinal());
        int after = SimYukkuri.world.getCurrentMap().getFood().size();
        assertEquals(before + 3, after);
        assertTrue(SimYukkuri.world.getCurrentMap().getFood().containsKey(f1.getObjId()));
        assertTrue(SimYukkuri.world.getCurrentMap().getFood().containsKey(f2.getObjId()));
        assertTrue(SimYukkuri.world.getCurrentMap().getFood().containsKey(f3.getObjId()));
    }

    @Test
    void testFoodTypeEnum_Values_NonEmpty() {
        FoodType[] types = FoodType.values();
        assertTrue(types.length > 0);
    }

    @Test
    void testEatFood_BecomeEmpty_ThenDoNothing() {
        Food food = new Food(0, 0, FoodType.FOOD.ordinal());
        food.setAmount(10);
        food.eatFood(10);
        assertTrue(food.isEmpty());
        food.eatFood(999);
        assertEquals(0, food.getAmount());
    }

    // getImageLayer tests

    @Test
    void testGetImageLayer_notEmpty_doesNotThrow() {
        Food food = new Food(0, 0, FoodType.FOOD.ordinal());
        food.setAmount(100);
        java.awt.image.BufferedImage[] layer = new java.awt.image.BufferedImage[1];
        assertDoesNotThrow(() -> food.getImageLayer(layer));
    }

    @Test
    void testGetImageLayer_empty_doesNotThrow() {
        Food food = new Food(0, 0, FoodType.FOOD.ordinal());
        food.setAmount(0);
        java.awt.image.BufferedImage[] layer = new java.awt.image.BufferedImage[1];
        assertDoesNotThrow(() -> food.getImageLayer(layer));
    }
}
