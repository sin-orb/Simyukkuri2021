package org.simyukkuri.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.simyukkuri.ConstState;
import org.simyukkuri.SimYukkuri;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.living.yukkuri.impl.Remirya;
import org.simyukkuri.entity.core.living.yukkuri.impl.TarinaiReimu;
import org.simyukkuri.entity.core.world.item.Bed;
import org.simyukkuri.entity.core.world.item.Food;
import org.simyukkuri.entity.core.world.item.Stone;
import org.simyukkuri.entity.core.world.mobile.Shit;
import org.simyukkuri.enums.AgeState;
import org.simyukkuri.enums.BurialState;
import org.simyukkuri.enums.CoreAnkoState;
import org.simyukkuri.enums.FavItemType;
import org.simyukkuri.enums.Intelligence;
import org.simyukkuri.enums.PredatorType;
import org.simyukkuri.enums.PublicRank;
import org.simyukkuri.util.WorldTestHelper;

class FoodLogicTest {

    private Yukkuri body;

    @BeforeEach
    void setUp() {
        WorldTestHelper.resetWorld();
        WorldTestHelper.initializeMinimalWorld();
        WorldTestHelper.initializeStandardTranslate200();
        SimYukkuri.RND = new ConstState(1);

        body = WorldTestHelper.createBody();
        body.setX(100);
        body.setY(100);
        SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(body.getUniqueId(), body);
    }

    @AfterEach
    void tearDown() {
        WorldTestHelper.resetWorld();
    }

    @Test
    void testCheckFood_NotHungry() {
        body.setHungry(0);

        assertFalse(FoodLogic.checkFood(body));
    }

    @Test
    void testCheckFood_Hungry_NoFood() {
        body.setHungry(body.getHungryLimit() / 2);

        assertFalse(FoodLogic.checkFood(body));
    }

    @Test
    void testCheckFood_Hungry_FoundFood() {
        body.setHungry(body.getHungryLimit() / 2);
        Food food = new Food(150, 150, Food.FoodType.SWEETS1.ordinal());
        SimYukkuri.world.getCurrentWorldState().getFoods().put(food.getObjId(), food);

        assertTrue(FoodLogic.checkFood(body));
    }

    @Test
    void testCheckFood_IsSleepyAndFull() {
        body.setAge(body.getActivePeriodBase() + 10000);
        body.setHungry(body.getHungryLimit());

        assertFalse(FoodLogic.checkFood(body));
    }

    @Test
    void testCheckFood_NonYukkuriDiseaseState() {
        body.setCoreAnkoState(CoreAnkoState.NON_YUKKURI_DISEASE);

        assertFalse(FoodLogic.checkFood(body));
    }

    @Test
    void testCheckFood_Buried_ReturnsFalse() {
        body.setBurialState(BurialState.HALF);

        assertFalse(FoodLogic.checkFood(body));
    }

    @Test
    void testCheckFood_Sleeping_ReturnsFalse() {
        WorldTestHelper.setSleeping(body, true);

        assertFalse(FoodLogic.checkFood(body));
    }

    @Test
    void testCheckFood_Exciting_NotRaper_NotSoHungry_ReturnsFalse() {
        body.setExciting(true);

        assertFalse(FoodLogic.checkFood(body));
    }

    @Test
    void testCheckFood_Raper_Exciting_NotStarving_ReturnsFalse() {
        body.setRaper(true);
        body.setExciting(true);

        assertFalse(FoodLogic.checkFood(body));
    }

    @Test
    void testCheckFood_FoodTargetRemoved_ReturnsFalse() {
        body.setHungry(0);
        Food food = new Food(110, 110, Food.FoodType.SWEETS1.ordinal());
        food.setAmount(100);
        SimYukkuri.world.getCurrentWorldState().getFoods().put(food.getObjId(), food);
        body.setToFood(true);
        body.setMoveTargetId(food.getObjId());
        food.setRemoved(true);

        assertFalse(FoodLogic.checkFood(body));
    }

    @Test
    void testCheckFood_StandardSearch_EmptyWorld_ReturnsFalse() {
        body.setHungry(5000);
        SimYukkuri.RND = new ConstState(0);

        assertFalse(FoodLogic.checkFood(body));
    }

    @Test
    void testCheckFood_StandardSearch_IdiotBody_UsesNearlestSearch() {
        body.setHungry(5000);
        body.setIntelligence(Intelligence.FOOL);
        SimYukkuri.RND = new ConstState(0);

        assertFalse(FoodLogic.checkFood(body));
    }

    @Test
    void testCheckFood_PredatorSearch_EmptyWorld_ReturnsFalse() {
        body.setHungry(5000);
        body.setPredatorType(PredatorType.BITE);
        SimYukkuri.RND = new ConstState(0);

        assertFalse(FoodLogic.checkFood(body));
    }

    @Test
    void testCheckFood_UnunSlave_EmptyWorld_ReturnsFalse() {
        body.setHungry(5000);
        body.setPublicRank(PublicRank.UNUN_SLAVE);
        SimYukkuri.RND = new ConstState(0);

        assertFalse(FoodLogic.checkFood(body));
    }

    @Test
    void testCheckFood_WantToShit_NotSoHungry_ReturnsFalse() {
        body.setHungry(5000);
        body.setShit(body.getShitLimit());
        SimYukkuri.RND = new ConstState(1);

        assertFalse(FoodLogic.checkFood(body));
    }

    @Test
    void testCheckFood_ToShit_Wise_NotVeryHungry_ReturnsFalse() {
        body.setHungry(body.getHungryLimit());
        body.setIntelligence(Intelligence.WISE);
        body.setToShit(true);

        assertFalse(FoodLogic.checkFood(body));
    }

    @Test
    void testCheckFood_Adult_ToSukkiri_NotVeryHungry_ReturnsFalse() {
        body.setHungry(body.getHungryLimit());
        body.setAgeState(AgeState.ADULT);
        body.setToSukkiri(true);

        assertFalse(FoodLogic.checkFood(body));
    }

    @Test
    void testCheckTakeout_NullBodyReturnsFalse() {
        assertFalse(FoodLogic.checkTakeout(null, null));
    }

    @Test
    void testCheckTakeout_NullObjReturnsFalse() {
        assertFalse(FoodLogic.checkTakeout(body, null));
    }

    @Test
    void testCheckTakeout_VeryHungryReturnsFalse() {
        body.setHungry(0);
        Food food = new Food(150, 150, Food.FoodType.SWEETS1.ordinal());

        assertFalse(FoodLogic.checkTakeout(body, food));
    }

    @Test
    void testCheckTakeout_WithNonFoodObj() {
        Stone stone = new Stone(150, 150, 0);

        assertFalse(FoodLogic.checkTakeout(body, stone));
    }

    @Test
    void testCheckTakeout_NormalBodyWithFoodNoFavBed() {
        Food food = new Food(150, 150, Food.FoodType.SWEETS1.ordinal());
        SimYukkuri.world.getCurrentWorldState().getFoods().put(food.getObjId(), food);

        assertFalse(FoodLogic.checkTakeout(body, food));
    }

    @Test
    void testCheckTakeout_Success() throws Exception {
        body.setHungry(body.getHungryLimit() / 2);
        Food food = new Food(150, 150, Food.FoodType.SWEETS1.ordinal());
        food.setAmount(100);

        Bed bed = new Bed(800, 800, 0);
        SimYukkuri.world.getCurrentWorldState().getBeds().put(bed.getObjId(), bed);
        body.setFavoriteItem(FavItemType.BED, bed);
        WorldTestHelper.addChild(body, 999);

        assertTrue(FoodLogic.checkTakeout(body, food));
    }

    @Test
    void testCheckCanEatBody_PredatorTypeReturnsTrue() {
        body.setPredatorType(PredatorType.BITE);
        Yukkuri prey = WorldTestHelper.createBody();
        prey.setX(120);
        prey.setY(120);
        SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(prey.getUniqueId(), prey);

        assertTrue(FoodLogic.checkCanEatYukkuri(body, prey));
    }

    @Test
    void testCheckCanEatBody_NonPredatorVsLiving() {
        Yukkuri prey = WorldTestHelper.createBody();
        prey.setX(120);
        prey.setY(120);
        SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(prey.getUniqueId(), prey);

        assertFalse(FoodLogic.checkCanEatYukkuri(body, prey));
    }

    @Test
    void testCheckCanEatBody_NonPredatorVsDead() {
        Yukkuri prey = WorldTestHelper.createBody();
        prey.setX(120);
        prey.setY(120);
        prey.setDead(true);
        SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(prey.getUniqueId(), prey);

        assertFalse(FoodLogic.checkCanEatYukkuri(body, prey));
    }

    @Test
    void testEatFood_DeadBodyReturnsEarly() {
        body.setDead(true);
        int hungryBefore = body.getHungry();

        FoodLogic.eatFood(body, Food.FoodType.SWEETS1, 100);

        assertEquals(hungryBefore, body.getHungry());
    }

    @Test
    void testEatFood_NormalBody_Sweets() {
        int hungryBefore = body.getHungry();

        FoodLogic.eatFood(body, Food.FoodType.SWEETS1, 100);

        assertTrue(body.getHungry() > hungryBefore);
    }

    @Test
    void testEatFood_NormalBody_Food() {
        int hungryBefore = body.getHungry();

        FoodLogic.eatFood(body, Food.FoodType.FOOD, 100);

        assertTrue(body.getHungry() > hungryBefore);
    }

    @Test
    void testEatFood_NormalBody_Bitter() {
        int hungryBefore = body.getHungry();

        FoodLogic.eatFood(body, Food.FoodType.BITTER, 100);

        assertTrue(body.getHungry() > hungryBefore);
    }

    @Test
    void testSearchFoodStandard_EmptyWorld_ReturnsNull() {
        body.setHungry(5000);

        assertNull(FoodLogic.searchFoodStandard(body, new boolean[] { false }));
    }

    @Test
    void testSearchFoodPredetor_EmptyWorld_ReturnsNull() {
        body.setHungry(5000);
        body.setPredatorType(PredatorType.BITE);

        assertNull(FoodLogic.searchFoodPredetor(body, new boolean[] { false }));
    }

    @Test
    void testSearchFoodPredetor_WithPreyNearby_FindsPrey() {
        Remirya hunter = new Remirya();
        hunter.setX(100);
        hunter.setY(100);
        hunter.setHungry(0);
        SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(hunter.getUniqueId(), hunter);

        Yukkuri prey = new TarinaiReimu();
        prey.setX(110);
        prey.setY(110);
        prey.setAgeState(AgeState.BABY);
        SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(prey.getUniqueId(), prey);

        assertNotNull(FoodLogic.searchFoodPredetor(hunter, new boolean[] { false }));
    }

    @Test
    void testSearchFoodForUnunSlave_WithShit_SearchesForUnunSlave() {
        WorldTestHelper.resetStates();
        WorldTestHelper.initializeStandardTranslate200();
        WorldTestHelper.initializeMinimalWorld();

        Yukkuri ununSlave = WorldTestHelper.createBody();
        ununSlave.setHungry(ununSlave.getHungryLimit() / 4);
        ununSlave.setPublicRank(PublicRank.UNUN_SLAVE);
        ununSlave.setEyesightBase(1000000);
        ununSlave.setX(50);
        ununSlave.setY(50);

        Shit shit = new Shit();
        shit.setX(100);
        shit.setY(100);
        SimYukkuri.world.getCurrentWorldState().getShit().put(shit.getObjId(), shit);
        SimYukkuri.world.getCurrentWorldState().getToilets().clear();

        assertNotNull(FoodUnunSlaveSearchPolicy.searchFoodForUnunSlave(ununSlave, new boolean[] { false }));
    }

    @Test
    void testSearchFoodNearest_EmptyWorld_ReturnsNull() {
        body.setHungry(5000);

        assertNull(FoodNearestSearchPolicy.searchFoodNearest(body, new boolean[] { false }));
    }

    @Test
    void testSearchFoodNearest_FullBody_ReturnsNull() {
        body.setHungry(body.getHungryLimit());
        body.setIntelligence(Intelligence.FOOL);

        assertNull(FoodNearestSearchPolicy.searchFoodNearest(body, new boolean[] { false }));
    }
}
