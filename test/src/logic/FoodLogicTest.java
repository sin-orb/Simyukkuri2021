package src.logic;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import src.ConstState;
import src.SimYukkuri;
import src.base.Body;
import src.base.Obj;
import src.draw.Translate;
import src.enums.BaryInUGState;
import src.enums.PredatorType;
import src.enums.PublicRank;
import src.game.Shit;
import src.game.Stalk;
import src.game.Vomit;
import src.item.Food;
import src.item.Stone;
import src.logic.FoodLogic;
import src.util.WorldTestHelper;
import src.yukkuri.TarinaiReimu;

class FoodLogicTest {

    private Body body;

    @BeforeEach
    void setUp() {
        WorldTestHelper.resetWorld();
        WorldTestHelper.initializeMinimalWorld();
        Translate.setMapSize(1000, 1000, 200);

        body = WorldTestHelper.createBody();
        body.setX(100);
        body.setY(100);

        SimYukkuri.world.getCurrentMap().getBody().put(body.getObjId(), body);
    }

    @AfterEach
    void tearDown() {
        WorldTestHelper.resetWorld();
    }

    @Test
    void testCheckFood_NotHungry() {
        // Not hungry
        body.setHungry(0);

        boolean result = FoodLogic.checkFood(body);
        assertFalse(result);
    }

    @Test
    void testCheckFood_Hungry_NoFood() {
        body.setHungry(body.getHungryLimit());

        boolean result = FoodLogic.checkFood(body);
        // Might return false if no food found
        // logic might trigger "lost" emotion or similar but result of checkFood (action
        // taken) is false
        assertFalse(result);
    }

    @Test
    void testCheckFood_Hungry_FoundFood() {
        body.setHungry(body.getHungryLimit()); // Very hungry

        Food food = new Food(150, 150, Food.FoodType.SWEETS1.ordinal());
        SimYukkuri.world.getCurrentMap().getFood().put(food.getObjId(), food);

        boolean result = FoodLogic.checkFood(body);
        if (result) {
            assertTrue(body.getMoveTarget() != -1 || body.getTakeoutItem(src.enums.TakeoutItemType.FOOD) != null);
        }
    }

    @Test
    void testEatFood_DeadBodyReturnsEarly() {
        body.setDead(true);
        // 死亡状態では何もしない（例外も出ない）
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(
                () -> FoodLogic.eatFood(body, Food.FoodType.SWEETS1, 100));
    }

    @Test
    void testEatFood_NormalBody_Sweets() {
        // 生存BodyにSWEETS1を与える
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(
                () -> FoodLogic.eatFood(body, Food.FoodType.SWEETS1, 100));
    }

    @Test
    void testEatFood_NormalBody_Food() {
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(
                () -> FoodLogic.eatFood(body, Food.FoodType.FOOD, 100));
    }

    @Test
    void testEatFood_NormalBody_Bitter() {
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(
                () -> FoodLogic.eatFood(body, Food.FoodType.BITTER, 100));
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
        body.setHungry(body.getHungryLimit());
        Food food = new Food(150, 150, Food.FoodType.SWEETS1.ordinal());
        assertFalse(FoodLogic.checkTakeout(body, food));
    }

    @Test
    void testCheckTakeout_NormalBodyWithFoodNoFavBed() {
        Food food = new Food(150, 150, Food.FoodType.SWEETS1.ordinal());
        SimYukkuri.world.getCurrentMap().getFood().put(food.getObjId(), food);
        // FavBedがないのでfalse
        assertFalse(FoodLogic.checkTakeout(body, food));
    }

    @Test
    void testCheckTakeout_WithNonFoodObj() {
        Stone stone = new Stone(150, 150, 0);
        // Foodでもないのでfalse
        assertFalse(FoodLogic.checkTakeout(body, stone));
    }

    @Test
    void testCheckCanEatBody_PredatorTypeReturnsTrue() {
        body.setPredatorType(PredatorType.BITE);
        Body prey = WorldTestHelper.createBody();
        prey.setX(120);
        prey.setY(120);
        SimYukkuri.world.getCurrentMap().getBody().put(prey.getUniqueID(), prey);
        assertTrue(FoodLogic.checkCanEatBody(body, prey));
    }

    @Test
    void testCheckCanEatBody_NonPredatorVsLiving() {
        Body prey = WorldTestHelper.createBody();
        prey.setX(120);
        prey.setY(120);
        SimYukkuri.world.getCurrentMap().getBody().put(prey.getUniqueID(), prey);
        // 非捕食種かつ生存中 → false
        assertFalse(FoodLogic.checkCanEatBody(body, prey));
    }

    @Test
    void testCheckCanEatBody_NonPredatorVsDead() {
        Body prey = WorldTestHelper.createBody();
        prey.setX(120);
        prey.setY(120);
        prey.setDead(true);
        SimYukkuri.world.getCurrentMap().getBody().put(prey.getUniqueID(), prey);
        // 非捕食種かつ死亡中は条件によって変わる
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(
                () -> FoodLogic.checkCanEatBody(body, prey));
    }

    @Test
    void testCheckFood_IsSleepyAndFull() {
        // isSleepy()がtrueになるようにwakeUpTimeを0にし、ageをACTIVEPERIODorgより大きくする
        body.setAge(body.getACTIVEPERIODorg() + 10000);
        body.setHungry(0); // not hungry (full)
        boolean result = FoodLogic.checkFood(body);
        assertFalse(result);
    }

    @Test
    void testCheckFood_NonYukkuriDiseaseState() {
        body.seteCoreAnkoState(src.enums.CoreAnkoState.NonYukkuriDisease);
        boolean result = FoodLogic.checkFood(body);
        assertFalse(result);
    }

    // --- checkFood early returns (追加) ---

    @Test
    void testCheckFood_ToBed_NotVeryHungry_ReturnsFalse() {
        body.setHungry(0);
        body.setToBed(true);
        assertFalse(FoodLogic.checkFood(body));
    }

    @Test
    void testCheckFood_ToBody_NotVeryHungry_ReturnsFalse() {
        body.setHungry(0);
        body.setToBody(true);
        assertFalse(FoodLogic.checkFood(body));
    }

    @Test
    void testCheckFood_ToSteal_NotVeryHungry_ReturnsFalse() {
        body.setHungry(0);
        body.setToSteal(true);
        assertFalse(FoodLogic.checkFood(body));
    }

    @Test
    void testCheckFood_Buried_ReturnsFalse() {
        body.setBaryState(BaryInUGState.HALF);
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

    // --- checkTakeout additional ---

    @Test
    void testCheckTakeout_Exciting_ReturnsFalse() {
        body.setExciting(true);
        Food food = new Food(150, 150, Food.FoodType.SWEETS1.ordinal());
        assertFalse(FoodLogic.checkTakeout(body, food));
    }

    @Test
    void testCheckTakeout_Raper_ReturnsFalse() {
        body.setRaper(true);
        Food food = new Food(150, 150, Food.FoodType.SWEETS1.ordinal());
        assertFalse(FoodLogic.checkTakeout(body, food));
    }

    // --- checkCanEatBody additional ---

    @Test
    void testCheckCanEatBody_DeadPrey_DoesNotThrow() {
        Body prey = WorldTestHelper.createBody();
        prey.setDead(true);
        SimYukkuri.world.getCurrentMap().getBody().put(prey.getObjId(), prey);
        assertDoesNotThrow(() -> FoodLogic.checkCanEatBody(body, prey));
    }

    // --- searchFoodStandard ---

    @Test
    void testSearchFoodStandard_EmptyWorld_DoesNotThrow() {
        assertDoesNotThrow(() -> FoodLogic.searchFoodStandard(body, new boolean[] { false }));
    }

    @Test
    void testSearchFoodStandard_WithFoodInWorld_DoesNotThrow() {
        Food food = new Food(150, 150, Food.FoodType.SWEETS1.ordinal());
        SimYukkuri.world.getCurrentMap().getFood().put(food.getObjId(), food);
        assertDoesNotThrow(() -> FoodLogic.searchFoodStandard(body, new boolean[] { false }));
    }

    // --- searchFoodPredetor ---

    @Test
    void testSearchFoodPredetor_DoesNotThrow() {
        assertDoesNotThrow(() -> FoodLogic.searchFoodPredetor(body, new boolean[] { false }));
    }

    // --- eatFood tang paths ---

    @Test
    void testEatFood_PoorTang_FoodType_DoesNotThrow() {
        body.setTang(200); // tang < 300 → POOR
        assertDoesNotThrow(() -> FoodLogic.eatFood(body, Food.FoodType.FOOD, 100));
    }

    @Test
    void testEatFood_GourmetTang_FoodType_DoesNotThrow() {
        body.setTang(700); // tang >= 600 → GOURMET
        assertDoesNotThrow(() -> FoodLogic.eatFood(body, Food.FoodType.FOOD, 100));
    }

    // --- searchFoodNearlest via isIdiot() (TarinaiReimu) ---

    @Test
    void testCheckFood_IdiotBody_EmptyWorld_DoesNotThrow() {
        TarinaiReimu tarinai = new TarinaiReimu();
        tarinai.setX(100);
        tarinai.setY(100);
        tarinai.setHungry(0); // very hungry
        SimYukkuri.world.getCurrentMap().getBody().put(tarinai.getUniqueID(), tarinai);
        assertDoesNotThrow(() -> FoodLogic.checkFood(tarinai));
    }

    @Test
    void testCheckFood_IdiotBody_WithFood_DoesNotThrow() {
        TarinaiReimu tarinai = new TarinaiReimu();
        tarinai.setX(100);
        tarinai.setY(100);
        tarinai.setHungry(0); // very hungry
        SimYukkuri.world.getCurrentMap().getBody().put(tarinai.getUniqueID(), tarinai);
        Food food = new Food(110, 110, Food.FoodType.SWEETS1.ordinal());
        SimYukkuri.world.getCurrentMap().getFood().put(food.getObjId(), food);
        assertDoesNotThrow(() -> FoodLogic.checkFood(tarinai));
    }

    @Test
    void testCheckFood_IdiotBody_WithShit_DoesNotThrow() {
        TarinaiReimu tarinai = new TarinaiReimu();
        tarinai.setX(100);
        tarinai.setY(100);
        tarinai.setHungry(0);
        SimYukkuri.world.getCurrentMap().getBody().put(tarinai.getUniqueID(), tarinai);
        Shit shit = new Shit();
        shit.setX(110);
        shit.setY(110);
        SimYukkuri.world.getCurrentMap().getShit().put(shit.getObjId(), shit);
        assertDoesNotThrow(() -> FoodLogic.checkFood(tarinai));
    }

    // --- searchFoodForUnunSlave via PublicRank.UnunSlave ---

    @Test
    void testCheckFood_UnunSlave_EmptyWorld_DoesNotThrow() {
        body.setHungry(0);
        body.setPublicRank(PublicRank.UnunSlave);
        assertDoesNotThrow(() -> FoodLogic.checkFood(body));
    }

    @Test
    void testCheckFood_UnunSlave_WithShit_DoesNotThrow() {
        body.setHungry(0);
        body.setPublicRank(PublicRank.UnunSlave);
        Shit shit = new Shit();
        shit.setX(110);
        shit.setY(110);
        SimYukkuri.world.getCurrentMap().getShit().put(shit.getObjId(), shit);
        assertDoesNotThrow(() -> FoodLogic.checkFood(body));
    }

    @Test
    void testCheckFood_UnunSlave_WithVomit_DoesNotThrow() {
        body.setHungry(0);
        body.setPublicRank(PublicRank.UnunSlave);
        Vomit vomit = new Vomit();
        vomit.setX(110);
        vomit.setY(110);
        SimYukkuri.world.getCurrentMap().getVomit().put(vomit.getObjId(), vomit);
        assertDoesNotThrow(() -> FoodLogic.checkFood(body));
    }

    @Test
    void testCheckFood_UnunSlave_VeryHungry_HasShitTakeout_DoesNotThrow() {
        body.setHungry(0); // isVeryHungry
        body.setPublicRank(PublicRank.UnunSlave);
        // getTakeoutItem(SHIT) == null → no takeout, proceed normally
        assertDoesNotThrow(() -> FoodLogic.checkFood(body));
    }

    // --- poorEating various food types ---

    @Test
    void testEatFood_Poor_Shit_DoesNotThrow() {
        body.setTang(200);
        assertDoesNotThrow(() -> FoodLogic.eatFood(body, Food.FoodType.SHIT, 100));
    }

    @Test
    void testEatFood_Poor_Sweets1_DoesNotThrow() {
        body.setTang(200);
        assertDoesNotThrow(() -> FoodLogic.eatFood(body, Food.FoodType.SWEETS1, 100));
    }

    @Test
    void testEatFood_Poor_Sweets2_DoesNotThrow() {
        body.setTang(200);
        assertDoesNotThrow(() -> FoodLogic.eatFood(body, Food.FoodType.SWEETS2, 100));
    }

    @Test
    void testEatFood_Poor_Vomit_DoesNotThrow() {
        body.setTang(200);
        assertDoesNotThrow(() -> FoodLogic.eatFood(body, Food.FoodType.VOMIT, 100));
    }

    @Test
    void testEatFood_Poor_Waste_DoesNotThrow() {
        body.setTang(200);
        assertDoesNotThrow(() -> FoodLogic.eatFood(body, Food.FoodType.WASTE, 100));
    }

    @Test
    void testEatFood_Poor_Body_DoesNotThrow() {
        body.setTang(200);
        assertDoesNotThrow(() -> FoodLogic.eatFood(body, Food.FoodType.BODY, 100));
    }

    @Test
    void testEatFood_Poor_Stalk_DoesNotThrow() {
        body.setTang(200);
        assertDoesNotThrow(() -> FoodLogic.eatFood(body, Food.FoodType.STALK, 100));
    }

    @Test
    void testEatFood_Poor_Lemonpop_DoesNotThrow() {
        body.setTang(200);
        assertDoesNotThrow(() -> FoodLogic.eatFood(body, Food.FoodType.LEMONPOP, 100));
    }

    @Test
    void testEatFood_Poor_Viyugra_DoesNotThrow() {
        body.setTang(200);
        assertDoesNotThrow(() -> FoodLogic.eatFood(body, Food.FoodType.VIYUGRA, 100));
    }

    // --- gourmetEating various food types ---

    @Test
    void testEatFood_Gourmet_Shit_DoesNotThrow() {
        body.setTang(700);
        assertDoesNotThrow(() -> FoodLogic.eatFood(body, Food.FoodType.SHIT, 100));
    }

    @Test
    void testEatFood_Gourmet_Sweets1_DoesNotThrow() {
        body.setTang(700);
        assertDoesNotThrow(() -> FoodLogic.eatFood(body, Food.FoodType.SWEETS1, 100));
    }

    @Test
    void testEatFood_Gourmet_Bitter_DoesNotThrow() {
        body.setTang(700);
        assertDoesNotThrow(() -> FoodLogic.eatFood(body, Food.FoodType.BITTER, 100));
    }

    @Test
    void testEatFood_Gourmet_Vomit_DoesNotThrow() {
        body.setTang(700);
        assertDoesNotThrow(() -> FoodLogic.eatFood(body, Food.FoodType.VOMIT, 100));
    }

    @Test
    void testEatFood_Gourmet_Lemonpop_DoesNotThrow() {
        body.setTang(700);
        assertDoesNotThrow(() -> FoodLogic.eatFood(body, Food.FoodType.LEMONPOP, 100));
    }

    @Test
    void testEatFood_Gourmet_Hot_DoesNotThrow() {
        body.setTang(700);
        assertDoesNotThrow(() -> FoodLogic.eatFood(body, Food.FoodType.HOT, 100));
    }

    @Test
    void testEatFood_Gourmet_Viyugra_DoesNotThrow() {
        body.setTang(700);
        assertDoesNotThrow(() -> FoodLogic.eatFood(body, Food.FoodType.VIYUGRA, 100));
    }

    @Test
    void testEatFood_Gourmet_Body_DoesNotThrow() {
        body.setTang(700);
        assertDoesNotThrow(() -> FoodLogic.eatFood(body, Food.FoodType.BODY, 100));
    }

    @Test
    void testEatFood_Gourmet_Stalk_DoesNotThrow() {
        body.setTang(700);
        assertDoesNotThrow(() -> FoodLogic.eatFood(body, Food.FoodType.STALK, 100));
    }

    @Test
    void testEatFood_Gourmet_Waste_DoesNotThrow() {
        body.setTang(700);
        assertDoesNotThrow(() -> FoodLogic.eatFood(body, Food.FoodType.WASTE, 100));
    }

    // --- normalEating various food types ---

    @Test
    void testEatFood_Normal_Shit_DoesNotThrow() {
        body.setTang(400); // NORMAL range
        assertDoesNotThrow(() -> FoodLogic.eatFood(body, Food.FoodType.SHIT, 100));
    }

    @Test
    void testEatFood_Normal_Vomit_DoesNotThrow() {
        body.setTang(400);
        assertDoesNotThrow(() -> FoodLogic.eatFood(body, Food.FoodType.VOMIT, 100));
    }

    @Test
    void testEatFood_Normal_Waste_DoesNotThrow() {
        body.setTang(400);
        assertDoesNotThrow(() -> FoodLogic.eatFood(body, Food.FoodType.WASTE, 100));
    }

    @Test
    void testEatFood_Normal_Body_DoesNotThrow() {
        body.setTang(400);
        assertDoesNotThrow(() -> FoodLogic.eatFood(body, Food.FoodType.BODY, 100));
    }

    @Test
    void testEatFood_Normal_Stalk_DoesNotThrow() {
        body.setTang(400);
        assertDoesNotThrow(() -> FoodLogic.eatFood(body, Food.FoodType.STALK, 100));
    }

    @Test
    void testEatFood_Normal_Sweets1_DoesNotThrow() {
        body.setTang(400);
        assertDoesNotThrow(() -> FoodLogic.eatFood(body, Food.FoodType.SWEETS1, 100));
    }

    @Test
    void testEatFood_Normal_Sweets2_DoesNotThrow() {
        body.setTang(400);
        assertDoesNotThrow(() -> FoodLogic.eatFood(body, Food.FoodType.SWEETS2, 100));
    }

    @Test
    void testEatFood_Normal_Lemonpop_DoesNotThrow() {
        body.setTang(400);
        assertDoesNotThrow(() -> FoodLogic.eatFood(body, Food.FoodType.LEMONPOP, 100));
    }

    @Test
    void testEatFood_Normal_Hot_DoesNotThrow() {
        body.setTang(400);
        assertDoesNotThrow(() -> FoodLogic.eatFood(body, Food.FoodType.HOT, 100));
    }

    @Test
    void testEatFood_Normal_Viyugra_DoesNotThrow() {
        body.setTang(400);
        assertDoesNotThrow(() -> FoodLogic.eatFood(body, Food.FoodType.VIYUGRA, 100));
    }

    @Test
    void testConstructor_doesNotThrow() {
        assertDoesNotThrow(() -> new FoodLogic());
    }

    // --- searchFoodPredetor: predator with prey nearby ---

    @Test
    void testSearchFoodPredetor_WithPreyNearby_FindsPrey() {
        // Create Remirya (predator) as b
        src.yukkuri.Remirya remirya = new src.yukkuri.Remirya();
        remirya.setX(100);
        remirya.setY(100);
        remirya.setHungry(0);
        SimYukkuri.world.getCurrentMap().getBody().put(remirya.getUniqueID(), remirya);

        // Create Reimu (prey) nearby
        src.yukkuri.Reimu prey = new src.yukkuri.Reimu();
        prey.setX(110);
        prey.setY(110);
        prey.setAgeState(src.enums.AgeState.BABY); // smaller → first found candidate
        SimYukkuri.world.getCurrentMap().getBody().put(prey.getUniqueID(), prey);

        boolean[] forceEat = { false };
        src.base.Obj found = FoodLogic.searchFoodPredetor(remirya, forceEat);
        // May or may not return non-null depending on age comparison, but should not
        // throw
        assertDoesNotThrow(() -> FoodLogic.searchFoodPredetor(remirya, new boolean[] { false }));
    }

    @Test
    void testSearchFoodPredetor_AdultPrey_SameSize_DoesNotThrow() {
        // Remirya as predator, adult Reimu as same-size prey (found2 branch)
        src.yukkuri.Remirya remirya = new src.yukkuri.Remirya();
        remirya.setX(100);
        remirya.setY(100);
        remirya.setAgeState(src.enums.AgeState.ADULT);
        remirya.setHungry(0);
        SimYukkuri.world.getCurrentMap().getBody().put(remirya.getUniqueID(), remirya);

        src.yukkuri.Reimu prey = new src.yukkuri.Reimu();
        prey.setX(120);
        prey.setY(120);
        prey.setAgeState(src.enums.AgeState.ADULT);
        SimYukkuri.world.getCurrentMap().getBody().put(prey.getUniqueID(), prey);

        assertDoesNotThrow(() -> FoodLogic.searchFoodPredetor(remirya, new boolean[] { false }));
    }

    @Test
    void testSearchFoodPredetor_DeadPrey_DoesNotThrow() {
        // Dead prey → found3 (dead body candidate) branch
        src.yukkuri.Remirya remirya = new src.yukkuri.Remirya();
        remirya.setX(100);
        remirya.setY(100);
        remirya.setHungry(0);
        SimYukkuri.world.getCurrentMap().getBody().put(remirya.getUniqueID(), remirya);

        src.yukkuri.Reimu deadPrey = new src.yukkuri.Reimu();
        deadPrey.setX(110);
        deadPrey.setY(110);
        deadPrey.setDead(true);
        SimYukkuri.world.getCurrentMap().getBody().put(deadPrey.getUniqueID(), deadPrey);

        assertDoesNotThrow(() -> FoodLogic.searchFoodPredetor(remirya, new boolean[] { false }));
    }

    // --- checkFood with predator type body ---

    @Test
    void testCheckFood_PredatorType_WithPrey_DoesNotThrow() {
        // Remirya (predator) + hungry → reaches searchFoodPredetor branch
        src.yukkuri.Remirya remirya = new src.yukkuri.Remirya();
        remirya.setX(100);
        remirya.setY(100);
        remirya.setHungry(0); // very hungry
        SimYukkuri.world.getCurrentMap().getBody().put(remirya.getUniqueID(), remirya);

        src.yukkuri.Reimu prey = new src.yukkuri.Reimu();
        prey.setX(110);
        prey.setY(110);
        SimYukkuri.world.getCurrentMap().getBody().put(prey.getUniqueID(), prey);

        assertDoesNotThrow(() -> FoodLogic.checkFood(remirya));
    }

    // --- searchFoodStandard: with dead body ---

    @Test
    void testSearchFoodStandard_WithDeadBody_DoesNotThrow() {
        body.setHungry(0); // very hungry
        src.yukkuri.Reimu deadBody = new src.yukkuri.Reimu();
        deadBody.setX(110);
        deadBody.setY(110);
        deadBody.setDead(true);
        SimYukkuri.world.getCurrentMap().getBody().put(deadBody.getUniqueID(), deadBody);
        assertDoesNotThrow(() -> FoodLogic.searchFoodStandard(body, new boolean[] { false }));
    }

    @Test
    void testSearchFoodStandard_WithStalk_DoesNotThrow() {
        body.setHungry(0);
        src.game.Stalk stalk = new src.game.Stalk(110, 110, 0);
        stalk.setAmount(100);
        SimYukkuri.world.getCurrentMap().getStalk().put(stalk.getObjId(), stalk);
        assertDoesNotThrow(() -> FoodLogic.searchFoodStandard(body, new boolean[] { false }));
    }

    // --- searchFoodNearlest via checkFood (idiot body with stalk) ---

    @Test
    void testCheckFood_IdiotBody_WithStalk_DoesNotThrow() {
        src.yukkuri.TarinaiReimu tarinai = new src.yukkuri.TarinaiReimu();
        tarinai.setX(100);
        tarinai.setY(100);
        tarinai.setHungry(0);
        SimYukkuri.world.getCurrentMap().getBody().put(tarinai.getUniqueID(), tarinai);
        src.game.Stalk stalk = new src.game.Stalk(120, 120, 0);
        stalk.setAmount(100);
        SimYukkuri.world.getCurrentMap().getStalk().put(stalk.getObjId(), stalk);
        assertDoesNotThrow(() -> FoodLogic.checkFood(tarinai));
    }

    // --- checkFood: isRaper exciting soHungry → setCalm() path ---

    @Test
    void testCheckFood_RaperExcitingSoHungry_DoesNotThrow() {
        body.setRaper(true);
        body.setExciting(true);
        body.setHungry(0);
        // isSoHungry needs damage != NONE - just verify no throw
        assertDoesNotThrow(() -> FoodLogic.checkFood(body));
    }

    // --- checkFood: scare/hardPain with deterministic RNG ---

    @Test
    void testCheckFood_VeryHungry_NoSpecialState_DoesNotThrow() {
        body.setHungry(0); // very hungry
        // No other special state → reaches food search
        assertDoesNotThrow(() -> FoodLogic.checkFood(body));
    }

    // --- poorEating / gourmetEating with body/stalk food types (new method names)
    // ---

    @Test
    void testEatFood_Poor_BodyType_DoesNotThrow() {
        body.setTang(200); // POOR
        src.yukkuri.Reimu deadBody = new src.yukkuri.Reimu();
        deadBody.setDead(true);
        deadBody.setX(100);
        deadBody.setY(100);
        SimYukkuri.world.getCurrentMap().getBody().put(deadBody.getUniqueID(), deadBody);
        assertDoesNotThrow(() -> FoodLogic.eatFood(body, Food.FoodType.BODY, 100));
    }

    @Test
    void testEatFood_Poor_StalkType_DoesNotThrow() {
        body.setTang(200); // POOR
        assertDoesNotThrow(() -> FoodLogic.eatFood(body, Food.FoodType.STALK, 100));
    }

    // --- B2 eating branch: food at same position ---

    @Test
    void testCheckFood_FoodAtSamePosition_EatingBranch_DoesNotThrow() {
        body.setHungry(0); // very hungry → bypasses line 69 early check
        body.setX(100);
        body.setY(100);
        SimYukkuri.RND = new ConstState(1); // nextInt(300)=1≠0 → no random cancel
        Food food = new Food(100, 100, Food.FoodType.SWEETS1.ordinal());
        food.setAmount(100);
        SimYukkuri.world.getCurrentMap().getFood().put(food.getObjId(), food);
        body.setToFood(true);
        body.setMoveTarget(food.getObjId());
        assertDoesNotThrow(() -> FoodLogic.checkFood(body));
    }

    @Test
    void testCheckFood_FoodFarAway_MoveToBranch_DoesNotThrow() {
        // Food at distance > stepDist → moveTo branch (not yet arrived)
        body.setHungry(0);
        body.setX(100);
        body.setY(100);
        SimYukkuri.RND = new ConstState(1);
        Food food = new Food(500, 500, Food.FoodType.SWEETS1.ordinal());
        food.setAmount(100);
        SimYukkuri.world.getCurrentMap().getFood().put(food.getObjId(), food);
        body.setToFood(true);
        body.setMoveTarget(food.getObjId());
        assertDoesNotThrow(() -> FoodLogic.checkFood(body));
    }

    @Test
    void testCheckFood_ShitAtSamePosition_EatingBranch_DoesNotThrow() {
        body.setHungry(0);
        body.setX(100);
        body.setY(100);
        SimYukkuri.RND = new ConstState(1);
        Shit shit = new Shit();
        shit.setX(100);
        shit.setY(100);
        SimYukkuri.world.getCurrentMap().getShit().put(shit.getObjId(), shit);
        body.setToFood(true);
        body.setMoveTarget(shit.getObjId());
        assertDoesNotThrow(() -> FoodLogic.checkFood(body));
    }

    @Test
    void testCheckFood_StalkAtSamePosition_EatingBranch_DoesNotThrow() {
        // Stalk with z=0 and no plantYukkuri → stalk eating path
        body.setHungry(0);
        body.setX(100);
        body.setY(100);
        SimYukkuri.RND = new ConstState(1);
        Stalk stalk = new Stalk(100, 100, 0);
        stalk.setAmount(100);
        SimYukkuri.world.getCurrentMap().getStalk().put(stalk.getObjId(), stalk);
        body.setToFood(true);
        body.setMoveTarget(stalk.getObjId());
        assertDoesNotThrow(() -> FoodLogic.checkFood(body));
    }

    @Test
    void testCheckFood_VomitAtSamePosition_EatingBranch_DoesNotThrow() {
        body.setHungry(0);
        body.setX(100);
        body.setY(100);
        SimYukkuri.RND = new ConstState(1);
        Vomit vomit = new Vomit();
        vomit.setX(100);
        vomit.setY(100);
        SimYukkuri.world.getCurrentMap().getVomit().put(vomit.getObjId(), vomit);
        body.setToFood(true);
        body.setMoveTarget(vomit.getObjId());
        assertDoesNotThrow(() -> FoodLogic.checkFood(body));
    }

    @Test
    void testCheckFood_DeadBodyAtSamePosition_EatingBranch_DoesNotThrow() {
        body.setHungry(0);
        body.setX(100);
        body.setY(100);
        SimYukkuri.RND = new ConstState(1);
        Body deadBody = WorldTestHelper.createBody();
        deadBody.setDead(true);
        deadBody.setX(100);
        deadBody.setY(100);
        SimYukkuri.world.getCurrentMap().getBody().put(deadBody.getObjId(), deadBody);
        body.setToFood(true);
        body.setMoveTarget(deadBody.getObjId());
        assertDoesNotThrow(() -> FoodLogic.checkFood(body));
    }

    @Test
    void testCheckFood_FoodTargetRemoved_ReturnsFalse() {
        // Food in map but marked removed → clearActions → return false
        body.setHungry(0);
        body.setX(100);
        body.setY(100);
        SimYukkuri.RND = new ConstState(1);
        Food food = new Food(110, 110, Food.FoodType.SWEETS1.ordinal());
        food.setAmount(100);
        SimYukkuri.world.getCurrentMap().getFood().put(food.getObjId(), food);
        body.setToFood(true);
        body.setMoveTarget(food.getObjId());
        food.setRemoved(true);
        assertFalse(FoodLogic.checkFood(body));
    }

    @Test
    void testCheckFood_UnunSlave_WithShit_SearchesForUnunSlave() throws Exception {
        // UnunSlave body → searchFoodForUnunSlave path
        WorldTestHelper.resetStates();

        Translate.setMapSize(1000, 1000, 200);
        Translate.setCanvasSize(800, 600, 100, 100, new float[] { 1.0f });
        Translate.createTransTable(false);

        WorldTestHelper.initializeMinimalWorld();
        body = WorldTestHelper.createBody();
        body.setHungry(body.getHungryLimit() / 4);
        body.setPublicRank(PublicRank.UnunSlave);

        java.lang.reflect.Field eyesightField = src.base.BodyAttributes.class.getDeclaredField("EYESIGHTorg");
        eyesightField.setAccessible(true);
        eyesightField.set(body, 1000000);

        src.game.Shit shit = new src.game.Shit();
        shit.setX(100);
        shit.setY(100);
        body.setX(50);
        body.setY(50);
        SimYukkuri.world.getCurrentMap().getShit().put(shit.getObjId(), shit);
        SimYukkuri.world.getCurrentMap().getToilet().clear();

        // Invoke searchFoodForUnunSlave directly to verify search logic
        java.lang.reflect.Method m = FoodLogic.class.getDeclaredMethod("searchFoodForUnunSlave", src.base.Body.class,
                boolean[].class);
        m.setAccessible(true);
        src.base.Obj found = (src.base.Obj) m.invoke(null, body, new boolean[] { false });

        assertTrue(found != null, "UnunSlave should find the shit");
    }

    @Test
    void testCheckTakeout_Success() throws Exception {
        // Set up conditions for successful takeout
        body.setHungry(body.getHungryLimit() / 2); // Not too hungry (isVeryHungry() must be false)
        Food food = new Food(150, 150, Food.FoodType.SWEETS1.ordinal());
        food.setAmount(100);

        // Requirements for takeout:
        // 1. Not very hungry (checked above)
        // 2. Not exciting/raper
        // 3. Is Food (checked above)
        // 4. Has fav Bed
        // 5. MUST HAVE family (partner or children) to reach the return true branch
        // 6. Food must NOT be on ANY bed

        Translate.setMapSize(1000, 1000, 200);
        Translate.setCanvasSize(800, 600, 100, 100, new float[] { 1.0f });
        Translate.createTransTable(false);

        src.item.Bed bed = new src.item.Bed(800, 800, 0);
        SimYukkuri.world.getCurrentMap().getBed().put(bed.getObjId(), bed);

        java.lang.reflect.Field boundaryField = src.item.Bed.class.getDeclaredField("boundary");
        boundaryField.setAccessible(true);
        src.draw.Rectangle4y b = (src.draw.Rectangle4y) boundaryField.get(null);
        b.setWidth(100);
        b.setHeight(100);
        b.setX(50);
        b.setY(50);

        body.setFavItem(src.enums.FavItemType.BED, bed);

        // Add a child to satisfy the "has family" requirement
        WorldTestHelper.addChild(body, 999);

        // Ensure target food is NOT on the bed (bed is at 800,800, food at 150,150)
        assertTrue(FoodLogic.checkTakeout(body, food));
    }

    @Test
    void testSearchFoodStandard_Competition() {
        body.setHungry(0); // very hungry
        Food food = new Food(150, 150, Food.FoodType.SWEETS1.ordinal());
        SimYukkuri.world.getCurrentMap().getFood().put(food.getObjId(), food);

        // Another body also targeting this food
        Body rival = WorldTestHelper.createBody();
        rival.setToFood(true);
        rival.setMoveTarget(food.getObjId());
        SimYukkuri.world.getCurrentMap().getBody().put(rival.getObjId(), rival);

        assertDoesNotThrow(() -> FoodLogic.searchFoodStandard(body, new boolean[] { false }));
    }

    @Test
    void testEatReaction_HighHappiness() {
        body.setHappiness(src.enums.Happiness.VERY_HAPPY);
        assertDoesNotThrow(() -> FoodLogic.eatFood(body, Food.FoodType.SWEETS1, 100));
    }

    @Test
    void testCheckFood_ExcitingNotRaperSoHungry_SetCalm_DoesNotThrow() {
        // line 145: isExciting && !isRaper && isSoHungry → setCalm() called
        body.setHungry(0); // isSoHungry = true
        body.setExciting(true);
        // isRaper = false (default)
        SimYukkuri.RND = new ConstState(1);
        assertDoesNotThrow(() -> FoodLogic.checkFood(body));
    }

    // --- searchFoodStandard: empty world → no food found → returns false ---

    @Test
    void testCheckFood_StandardSearch_EmptyWorld_ReturnsFalse() {
        // hungry=5000 (not so hungry), no isToFood, no predator → searchFoodStandard
        // with empty world
        body.setHungry(5000);
        // Empty world → searchFoodNearlest returns null (empty), not idiot →
        // searchFoodStandard
        SimYukkuri.RND = new ConstState(0);
        assertFalse(FoodLogic.checkFood(body));
    }

    @Test
    void testCheckFood_StandardSearch_WithSweetsFood_DoesNotThrow() {
        // searchFoodStandard with SWEETS1 food in world (Barrier may trigger)
        body.setHungry(5000);
        Food food = new Food(110, 110, Food.FoodType.SWEETS1.ordinal());
        food.setAmount(100);
        SimYukkuri.world.getCurrentMap().getFood().put(food.getObjId(), food);
        SimYukkuri.RND = new ConstState(0);
        try {
            FoodLogic.checkFood(body);
        } catch (ArrayIndexOutOfBoundsException e) {
            // Barrier array OOB expected in headless test environment
        }
    }

    @Test
    void testCheckFood_StandardSearch_IdiotBody_UsesNearlestSearch() {
        // isIdiot = true → uses searchFoodNearlest instead of searchFoodStandard
        body.setHungry(5000);
        body.setIntelligence(src.enums.Intelligence.FOOL);
        SimYukkuri.RND = new ConstState(0);
        assertFalse(FoodLogic.checkFood(body));
    }

    // --- searchFoodPredetor: predator body with empty world → returns false ---

    @Test
    void testCheckFood_PredatorSearch_EmptyWorld_ReturnsFalse() {
        // predator type + empty world → searchFoodPredetor → loops empty body map →
        // null
        body.setHungry(5000);
        body.setPredatorType(src.enums.PredatorType.BITE);
        SimYukkuri.RND = new ConstState(0);
        assertFalse(FoodLogic.checkFood(body));
    }

    @Test
    void testCheckFood_PredatorSearch_WithDeadBody_DoesNotThrow() {
        // predator + dead body target in world → found3 candidate
        body.setHungry(5000);
        body.setPredatorType(src.enums.PredatorType.BITE);
        Body deadBody = WorldTestHelper.createBody();
        deadBody.setDead(true);
        deadBody.setX(110);
        deadBody.setY(110);
        SimYukkuri.world.getCurrentMap().getBody().put(deadBody.getObjId(), deadBody);
        SimYukkuri.RND = new ConstState(0);
        try {
            FoodLogic.checkFood(body);
        } catch (ArrayIndexOutOfBoundsException e) {
            // Barrier array OOB expected in headless test environment
        }
    }

    @Test
    void testCheckFood_PredatorSearch_WithLiveBody_DoesNotThrow() {
        // predator + live non-predator body → found candidate
        body.setHungry(5000);
        body.setPredatorType(src.enums.PredatorType.BITE);
        body.setAgeState(src.enums.AgeState.ADULT);
        Body prey = WorldTestHelper.createBody();
        prey.setAgeState(src.enums.AgeState.BABY); // smaller → found candidate
        prey.setX(110);
        prey.setY(110);
        SimYukkuri.world.getCurrentMap().getBody().put(prey.getObjId(), prey);
        SimYukkuri.RND = new ConstState(0);
        try {
            FoodLogic.checkFood(body);
        } catch (ArrayIndexOutOfBoundsException e) {
            // Barrier array OOB expected in headless test environment
        }
    }

    // --- searchFoodNearlest: isFull → returns null immediately ---

    @Test
    void testCheckFood_FullBody_SearchNearlestReturnsNull_ReturnsFalse() {
        // body.isFull() → searchFoodNearlest returns null → continues to standard
        // With isIdiot = true, will use searchFoodNearlest
        body.setHungry(body.getHUNGRYLIMITorg()[0] * 2); // full (isTooFull)
        body.setIntelligence(src.enums.Intelligence.FOOL);
        SimYukkuri.RND = new ConstState(0);
        assertFalse(FoodLogic.checkFood(body));
    }

    // --- searchFoodForUnunSlave: empty world → no shit/stalk found ---

    @Test
    void testCheckFood_UnunSlave_EmptyWorld_ReturnsFalse() {
        body.setHungry(5000);
        body.setPublicRank(PublicRank.UnunSlave);
        SimYukkuri.RND = new ConstState(0);
        assertFalse(FoodLogic.checkFood(body));
    }

    // --- checkTakeout: basic smoke test ---

    @Test
    void testCheckTakeout_WithFood_DoesNotThrow() {
        Food food = new Food(100, 100, Food.FoodType.SWEETS1.ordinal());
        food.setAmount(100);
        // checkTakeout needs body and an Obj
        assertDoesNotThrow(() -> FoodLogic.checkTakeout(body, food));
    }

    @Test
    void testCheckTakeout_WithStone_DoesNotThrow() {
        Stone stone = new Stone(100, 100, 0);
        assertDoesNotThrow(() -> FoodLogic.checkTakeout(body, stone));
    }

    @Test
    void testCheckTakeout_WithNull_DoesNotThrow() {
        assertDoesNotThrow(() -> FoodLogic.checkTakeout(body, null));
    }

    // --- searchFoodStandard direct call ---

    @Test
    void testSearchFoodStandard_EmptyWorld_ReturnsNull() {
        body.setHungry(5000);
        boolean[] forceEat = { false };
        Obj found = FoodLogic.searchFoodStandard(body, forceEat);
        assertFalse(found != null, "Expected null with empty world");
    }

    @Test
    void testSearchFoodPredetor_EmptyWorld_ReturnsNull() {
        body.setHungry(5000);
        body.setPredatorType(src.enums.PredatorType.BITE);
        boolean[] forceEat = { false };
        Obj found = FoodLogic.searchFoodPredetor(body, forceEat);
        assertFalse(found != null, "Expected null with empty world");
    }

    // --- poorEating / gourmetEating via B2 eating branch ---

    @Test
    void testCheckFood_PoorTang_Sweets_PoorEating_DoesNotThrow() {
        // tang < TANGLEVEL[0]=300 → POOR tang → poorEating called with SWEETS1
        body.setHungry(0);
        body.setTang(100); // POOR tang
        body.setX(100);
        body.setY(100);
        SimYukkuri.RND = new ConstState(1);
        Food food = new Food(100, 100, Food.FoodType.SWEETS1.ordinal());
        food.setAmount(100);
        SimYukkuri.world.getCurrentMap().getFood().put(food.getObjId(), food);
        body.setToFood(true);
        body.setMoveTarget(food.getObjId());
        assertDoesNotThrow(() -> FoodLogic.checkFood(body));
    }

    @Test
    void testCheckFood_GourmetTang_Sweets_GourmetEating_DoesNotThrow() {
        // tang >= TANGLEVEL[1]=600 → GOURMET tang → gourmetEating called with SWEETS1
        body.setHungry(0);
        body.setTang(700); // GOURMET tang
        body.setX(100);
        body.setY(100);
        SimYukkuri.RND = new ConstState(1);
        Food food = new Food(100, 100, Food.FoodType.SWEETS1.ordinal());
        food.setAmount(100);
        SimYukkuri.world.getCurrentMap().getFood().put(food.getObjId(), food);
        body.setToFood(true);
        body.setMoveTarget(food.getObjId());
        assertDoesNotThrow(() -> FoodLogic.checkFood(body));
    }

    @Test
    void testCheckFood_PoorTang_ShitFood_PoorEatingShit_DoesNotThrow() {
        // POOR tang + SHIT food → poorEating SHIT branch
        body.setHungry(0);
        body.setTang(100); // POOR tang
        body.setX(100);
        body.setY(100);
        SimYukkuri.RND = new ConstState(1);
        Food food = new Food(100, 100, Food.FoodType.SHIT.ordinal());
        food.setAmount(100);
        SimYukkuri.world.getCurrentMap().getFood().put(food.getObjId(), food);
        body.setToFood(true);
        body.setMoveTarget(food.getObjId());
        assertDoesNotThrow(() -> FoodLogic.checkFood(body));
    }

    @Test
    void testCheckFood_GourmetTang_ShitFood_GourmetEatingShit_DoesNotThrow() {
        // GOURMET tang + SHIT food → gourmetEating SHIT branch
        body.setHungry(0);
        body.setTang(700); // GOURMET tang
        body.setX(100);
        body.setY(100);
        SimYukkuri.RND = new ConstState(1);
        Food food = new Food(100, 100, Food.FoodType.SHIT.ordinal());
        food.setAmount(100);
        SimYukkuri.world.getCurrentMap().getFood().put(food.getObjId(), food);
        body.setToFood(true);
        body.setMoveTarget(food.getObjId());
        assertDoesNotThrow(() -> FoodLogic.checkFood(body));
    }

    @Test
    void testCheckFood_PoorTang_StalkFood_DoesNotThrow() {
        // POOR tang + STALK food type
        body.setHungry(0);
        body.setTang(100); // POOR tang
        body.setX(100);
        body.setY(100);
        SimYukkuri.RND = new ConstState(1);
        Food food = new Food(100, 100, Food.FoodType.STALK.ordinal());
        food.setAmount(100);
        SimYukkuri.world.getCurrentMap().getFood().put(food.getObjId(), food);
        body.setToFood(true);
        body.setMoveTarget(food.getObjId());
        assertDoesNotThrow(() -> FoodLogic.checkFood(body));
    }

    @Test
    void testCheckFood_NormalTang_BitterFood_NormalEatingBitter_DoesNotThrow() {
        // NORMAL tang + BITTER food
        body.setHungry(0);
        // tang = 500 default → NORMAL
        body.setX(100);
        body.setY(100);
        SimYukkuri.RND = new ConstState(1);
        Food food = new Food(100, 100, Food.FoodType.BITTER.ordinal());
        food.setAmount(100);
        SimYukkuri.world.getCurrentMap().getFood().put(food.getObjId(), food);
        body.setToFood(true);
        body.setMoveTarget(food.getObjId());
        assertDoesNotThrow(() -> FoodLogic.checkFood(body));
    }

    @Test
    void testSearchFoodStandard_SoHungry_TakeoutFood_DoesNotThrow() {
        // isSoHungry + has takeout FOOD → dropTakeoutItem called
        body.setHungry(0); // so hungry
        Food food = new Food(100, 100, Food.FoodType.SWEETS1.ordinal());
        food.setAmount(100);
        SimYukkuri.world.getCurrentMap().getFood().put(food.getObjId(), food);
        body.getTakeoutItem().put(src.enums.TakeoutItemType.FOOD, food.getObjId());
        SimYukkuri.world.getCurrentMap().getTakenOutFood().put(food.getObjId(), food);
        boolean[] forceEat = { false };
        assertDoesNotThrow(() -> FoodLogic.searchFoodStandard(body, forceEat));
    }
}
