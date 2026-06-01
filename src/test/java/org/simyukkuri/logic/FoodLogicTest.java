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
import org.simyukkuri.enums.FootBake;
import org.simyukkuri.enums.Intelligence;
import org.simyukkuri.enums.PredatorType;
import org.simyukkuri.enums.PublicRank;
import org.simyukkuri.entity.core.world.bodylinked.Stalk;
import org.simyukkuri.event.EventPacket;
import org.simyukkuri.event.impl.SuperEatingTimeEvent;
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

    // ================================================================
    // TEST_EXPANTION_PLAN: FoodActionGate.shouldSkipBeforeSearch
    // ================================================================

    @Test
    void testCheckFood_Dead_ReturnsFalse() {
        body.setHungry(body.getHungryLimit() / 2);
        body.setDead(true);
        assertFalse(FoodLogic.checkFood(body));
    }

    @Test
    void testCheckFood_ForceEat_SuperEatingTimeEventStart_BypassesRandomSkip() {
        // ConstState(0) は nextInt(300)==0 でランダムスキップを引き起こす。
        // forceEat=false なら clearActions して false を返すが、
        // SuperEatingTimeEvent(State.START, HIGH) が currentEvent にあると forceEat=true になり
        // そのスキップを回避して食料サーチに入り、food が見つかれば true を返す。
        body.setHungry(0); // isVeryHungry=true → canAction特例（currentEvent有りでも許可）
        Food food = new Food(102, 100, Food.FoodType.SWEETS1.ordinal());
        SimYukkuri.world.getCurrentWorldState().getFoods().put(food.getObjId(), food);

        // forceEat なしではランダムスキップ（nextInt(300)==0）が発動して false
        SimYukkuri.RND = new ConstState(0);
        assertFalse(FoodLogic.checkFood(body));

        // forceEat=true でスキップ回避 → food 発見 → true
        SuperEatingTimeEvent ev = new SuperEatingTimeEvent(body, null, food, 1);
        ev.setPriority(EventPacket.EventPriority.HIGH);
        ev.setState(SuperEatingTimeEvent.State.START);
        body.setCurrentEvent(ev);
        SimYukkuri.RND = new ConstState(0);
        assertTrue(FoodLogic.checkFood(body));
    }

    // ================================================================
    // TEST_EXPANTION_PLAN: Stalk 所有者ガード（B1 分岐）
    // ================================================================

    @Test
    void testCheckFood_SelfPlantedStalk_SkipsAndClearsTarget() {
        // 自分が植わっている茎を自分で食べようとする → スキップ・ターゲット解除
        body.setHungry(body.getHungryLimit() / 2);
        Stalk stalk = new Stalk(102, 100, 0);
        stalk.setPlantYukkuri(body.getUniqueId()); // 自分が植わっている
        SimYukkuri.world.getCurrentWorldState().getStalks().put(stalk.getObjId(), stalk);
        SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(body.getUniqueId(), body);

        body.setToFood(true);
        body.setMoveTargetId(stalk.getObjId());

        assertFalse(FoodLogic.checkFood(body));
        assertFalse(body.isToFood());
    }

    @Test
    void testCheckFood_OtherPlantedStalk_NonBuried_ReturnsFalse() {
        // 他人が植わっているが非埋没 → 食べようとしない
        body.setHungry(body.getHungryLimit() / 2);
        Yukkuri other = WorldTestHelper.createBody();
        other.setX(50);
        other.setY(50);
        other.setBurialState(BurialState.NONE);
        SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(other.getUniqueId(), other);

        Stalk stalk = new Stalk(102, 100, 0);
        stalk.setPlantYukkuri(other.getUniqueId());
        SimYukkuri.world.getCurrentWorldState().getStalks().put(stalk.getObjId(), stalk);

        body.setToFood(true);
        body.setMoveTargetId(stalk.getObjId());

        assertFalse(FoodLogic.checkFood(body));
    }

    @Test
    void testCheckFood_OtherPlantedStalk_FullyBuried_DoesNotClearTarget() {
        // 他人が完全埋没している茎 → 食べようとする（clearActions されない）
        body.setHungry(body.getHungryLimit() / 2);
        Yukkuri other = WorldTestHelper.createBody();
        other.setX(103);
        other.setY(100);
        other.setBurialState(BurialState.ALL);
        SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(other.getUniqueId(), other);

        Stalk stalk = new Stalk(103, 100, 0);
        stalk.setPlantYukkuri(other.getUniqueId());
        SimYukkuri.world.getCurrentWorldState().getStalks().put(stalk.getObjId(), stalk);

        body.setToFood(true);
        body.setMoveTargetId(stalk.getObjId());

        // 食べようとする（到着判定へ進む）= isToFood が維持されるか true を返す
        FoodLogic.checkFood(body);
        assertTrue(body.isToFood());
    }

    // ================================================================
    // TEST_EXPANTION_PLAN: 足焼き CRITICAL → 最寄りサーチ
    // ================================================================

    @Test
    void testCheckFood_FootBakeCritical_NonFlying_UsesNearestSearch() {
        // 足焼き CRITICAL の非飛行種は searchFoodNearest ルートに入る
        // World が空なので null → false を返すが、ルート切り替えの確認として機能する
        body.setHungry(body.getHungryLimit() / 2);
        body.setFlyingType(false);
        // footBakePeriod を damageLimitBase 超えにして CRITICAL にする
        body.setFootBakePeriod(body.getDamageLimitBase()[AgeState.ADULT.ordinal()] + 1);
        assertEquals(FootBake.CRITICAL, body.getFootBakeLevel());

        // 世界に食料なし → false
        assertFalse(FoodLogic.checkFood(body));
    }

    @Test
    void testCheckFood_FootBakeCritical_NonFlying_FindsNearestFood() {
        // 足焼き CRITICAL で最寄り食料を発見する
        body.setHungry(body.getHungryLimit() / 2);
        body.setFlyingType(false);
        body.setFootBakePeriod(body.getDamageLimitBase()[AgeState.ADULT.ordinal()] + 1);
        assertEquals(FootBake.CRITICAL, body.getFootBakeLevel());

        Food food = new Food(102, 100, Food.FoodType.SWEETS1.ordinal());
        SimYukkuri.world.getCurrentWorldState().getFoods().put(food.getObjId(), food);

        assertTrue(FoodLogic.checkFood(body));
    }

    // ================================================================
    // TEST_EXPANTION_PLAN: FoodArrivalActionPolicy — 空中食料を飛行不可種がスキップ
    // ================================================================

    @Test
    void testCheckFood_CanActionFalse_NotVeryHungry_ReturnsFalse() {
        // isPacked=true → canAction=false、isVeryHungry=false → スキップして false
        body.setHungry(body.getHungryLimit() / 2); // isVeryHungry=false
        body.setPacked(true); // canAction=false
        assertFalse(FoodLogic.checkFood(body));
    }

    @Test
    void testCheckFood_PredatorType_WithPrey_ReturnsTrue() {
        // 捕食種が prey を正しく発見する（searchFoodPredetor ルートに入る）
        Remirya hunter = new Remirya() {
            @Override public int getCollisionX() { return 10; }
        };
        hunter.setX(100);
        hunter.setY(100);
        hunter.setHungry(0); // isVeryHungry=true でFoodActionGate をパス
        hunter.setAgeState(AgeState.ADULT); // prey(BABY) より大きい必要がある
        hunter.setPredatorType(PredatorType.BITE);
        SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(hunter.getUniqueId(), hunter);

        TarinaiReimu prey = new TarinaiReimu() {
            @Override public int getCollisionX() { return 10; }
        };
        prey.setX(110);
        prey.setY(110);
        prey.setAgeState(AgeState.BABY);
        SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(prey.getUniqueId(), prey);

        assertTrue(FoodLogic.checkFood(hunter));
    }

    @Test
    void testCheckFood_UnunSlave_WithNormalFood_ReturnsFalse() {
        // UNUN_SLAVE は SWEETS1 等の通常食料を無視する（WASTE タイプのみを対象とする）
        body.setHungry(5000);
        body.setPublicRank(PublicRank.UNUN_SLAVE);

        Food food = new Food(102, 100, Food.FoodType.SWEETS1.ordinal());
        SimYukkuri.world.getCurrentWorldState().getFoods().put(food.getObjId(), food);

        SimYukkuri.RND = new ConstState(0);
        assertFalse(FoodLogic.checkFood(body));
    }

    @Test
    void testCheckFood_AirborneFood_NonFlying_ReturnsFalse() {
        // Z≠0 の食べ物を飛行できない種がターゲット設定していても、到着判定でスキップ
        body.setHungry(body.getHungryLimit() / 2);
        body.setFlyingType(false);
        Food food = new Food(102, 100, Food.FoodType.SWEETS1.ordinal());
        food.setZ(10); // 空中
        SimYukkuri.world.getCurrentWorldState().getFoods().put(food.getObjId(), food);

        body.setToFood(true);
        body.setMoveTargetId(food.getObjId());

        assertFalse(FoodLogic.checkFood(body));
        assertFalse(body.isToFood());
    }

    // ================================================================
    // TEST_EXPANTION_PLAN: FoodArrivalActionPolicy.handleArrivedFood
    // ================================================================

    @Test
    void testHandleArrivedFood_EmptyFood_ClearsActionsReturnsFalse() {
        // 空の食料到着時 → clearActions + false
        // handleArrivedFood は body.takeMoveTarget() で food を取得するため、
        // food をワールドに登録して moveTargetId を設定する必要がある
        Food food = new Food(102, 100, Food.FoodType.SWEETS1.ordinal());
        food.eatFood(food.getAmount()); // isEmpty=true にする
        assertTrue(food.isEmpty());
        SimYukkuri.world.getCurrentWorldState().getFoods().put(food.getObjId(), food);

        body.setToFood(true);
        body.setMoveTargetId(food.getObjId());
        boolean result = FoodArrivalActionPolicy.handleArrivedFood(body, food, new boolean[]{false});

        assertFalse(result);
        assertFalse(body.isToFood());
    }

    @Test
    void testHandleArrivedFood_Sweets1_IncreasesHungry() {
        // SWEETS1 を食べると hungry が増加する
        body.setHungry(0);
        body.setToTakeout(false);
        Food food = new Food(102, 100, Food.FoodType.SWEETS1.ordinal());
        food.setAmount(500);
        SimYukkuri.world.getCurrentWorldState().getFoods().put(food.getObjId(), food);
        body.setMoveTargetId(food.getObjId());

        FoodArrivalActionPolicy.handleArrivedFood(body, food, new boolean[]{false});

        assertTrue(body.getHungry() > 0);
    }

    @Test
    void testHandleArrivedFood_ToTakeout_NotVeryHungry_CarriesFood() {
        // isToTakeout=true + !isVeryHungry → 食料を持ち帰りモードにする
        body.setHungry(body.getHungryLimit() / 2); // !isVeryHungry
        body.setToTakeout(true);
        Food food = new Food(102, 100, Food.FoodType.SWEETS1.ordinal());
        food.setAmount(500);
        SimYukkuri.world.getCurrentWorldState().getFoods().put(food.getObjId(), food);
        body.setMoveTargetId(food.getObjId());

        FoodArrivalActionPolicy.handleArrivedFood(body, food, new boolean[]{false});

        // 持ち帰りモード：食料を carryItem として設定
        assertNotNull(body.getCarryItem(org.simyukkuri.enums.TakeoutItemType.FOOD));
    }

    // ================================================================
    // TEST_EXPANTION_PLAN: FoodNearestSearchPolicy.searchFoodNearest
    // ================================================================

    // ================================================================
    // TEST_EXPANTION_PLAN: FoodArrivalActionPolicy — 捕食種が prey を食べる
    // ================================================================

    @Test
    void testHandleArrivedFood_PredatorType_PreyCriticalDamageSet() {
        // 捕食種が非飛行で prey に到達 → bodyInjure → prey が INJURED 状態になる
        Remirya hunter = new Remirya() {
            @Override public int getCollisionX() { return 10; }
        };
        hunter.setFlyingType(false); // canflyCheck()=false にして地上食いルートへ
        hunter.setAgeState(AgeState.ADULT);
        hunter.setPredatorType(PredatorType.BITE);
        SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(hunter.getUniqueId(), hunter);

        TarinaiReimu prey = new TarinaiReimu() {
            @Override public int getCollisionX() { return 10; }
        };
        prey.setAgeState(AgeState.BABY);
        // BurialState.ALL にして bodyInjure 内の GameView.addVomit を skip (headless NPE 回避)
        prey.setBurialState(BurialState.ALL);
        SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(prey.getUniqueId(), prey);

        hunter.setMoveTargetId(prey.getUniqueId());

        FoodArrivalActionPolicy.handleArrivedFood(hunter, prey, new boolean[]{false});

        // bodyInjure が呼ばれたことを確認
        // bodyInjure → INJURED セット後、eatYukkuri で ankoAmount が閾値以下なら CUT に遷移する場合もある
        // 初期値 null → null でなければ bodyInjure が実行されたことを示す
        assertNotNull(prey.getCriticalDamege());
    }

    @Test
    void testSearchFoodNearest_EyesightZero_ReturnsNull() {
        // eyesightBase=0 → nearestDistance=0 → 即 break → 食料が視野内でも null を返す
        body.setHungry(5000);
        body.setEyesightBase(0);
        Food food = new Food(101, 100, Food.FoodType.SWEETS1.ordinal());
        SimYukkuri.world.getCurrentWorldState().getFoods().put(food.getObjId(), food);

        assertNull(FoodNearestSearchPolicy.searchFoodNearest(body, new boolean[]{false}));
    }

    @Test
    void testHandleArrivedFood_ToTakeout_VeryHungry_EatsInstead() {
        // isToTakeout=true でも isVeryHungry=true なら食べる
        body.setHungry(0); // isVeryHungry=true
        body.setToTakeout(true);
        Food food = new Food(102, 100, Food.FoodType.SWEETS1.ordinal());
        food.setAmount(500);
        SimYukkuri.world.getCurrentWorldState().getFoods().put(food.getObjId(), food);
        body.setMoveTargetId(food.getObjId());

        FoodArrivalActionPolicy.handleArrivedFood(body, food, new boolean[]{false});

        // 非常に空腹なので食べる → hungry が増加
        assertTrue(body.getHungry() > 0);
    }
}
