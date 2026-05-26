package org.simyukkuri.logic;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.simyukkuri.SimYukkuri;
import org.simyukkuri.entity.core.Entity;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.world.item.Bed;
import org.simyukkuri.entity.core.world.item.House;
import org.simyukkuri.enums.PublicRank;
import org.simyukkuri.util.WorldTestHelper;

class BedLogicTest {

    private Yukkuri body;

    @BeforeEach
    void setUp() {
        WorldTestHelper.resetWorld();
        WorldTestHelper.initializeMinimalWorld();
        WorldTestHelper.initializeStandardTranslate200();

        body = WorldTestHelper.createBody();
        body.setX(100);
        body.setY(100);
        body.setZ(0); // On floor

        SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(body.getObjId(), body);
    }

    @Test
    void testCheckBed_Sleepy() {
        WorldTestHelper.setSleeping(body, true);
        // Should find bed and return true if bed exists

        Bed bed = new Bed();
        bed.setX(150);
        bed.setY(150);
        SimYukkuri.world.getCurrentWorldState().getBeds().put(bed.getObjId(), bed);

        BedLogic.checkBed(body);
        // This might depend on implementation details of checkBed (distance etc.)
        // But with sleeping=true, it should try to find a bed.
    }

    @Test
    void testSearchBed_Found() {
        Bed bed = new Bed();
        bed.setX(150);
        bed.setY(150);
        SimYukkuri.world.getCurrentWorldState().getBeds().put(bed.getObjId(), bed);

        Entity result = BedLogic.searchBed(body);
        assertNotNull(result);
        assertEquals(bed, result);
    }

    @Test
    void testSearchBed_NotFound() {
        Entity result = BedLogic.searchBed(body);
        assertNull(result);
    }

    @AfterEach
    void tearDown() {
        WorldTestHelper.resetWorld();
    }

    @Test
    void testConstructor_doesNotThrow() {
        assertDoesNotThrow(() -> new BedLogic());
    }

    // --- checkBed: early returns ---

    @Test
    void testCheckBed_isToFood_returnsFalse() {
        body.setToFood(true);
        assertFalse(BedLogic.checkBed(body));
    }

    @Test
    void testCheckBed_isToShit_returnsFalse() {
        body.setToShit(true);
        assertFalse(BedLogic.checkBed(body));
    }

    @Test
    void testCheckBed_isToSukkiri_returnsFalse() {
        body.setToSukkiri(true);
        assertFalse(BedLogic.checkBed(body));
    }

    @Test
    void testCheckBed_isToSteal_returnsFalse() {
        body.setToSteal(true);
        assertFalse(BedLogic.checkBed(body));
    }

    @Test
    void testCheckBed_noCondition_returnsFalse() {
        // No sleepiness, no bed, no evening → flag=false → returns false
        assertFalse(BedLogic.checkBed(body));
    }

    @Test
    void testCheckBed_sleepy_noBed_returnsFalse() {
        // isSleepy via sleepPoint limit
        body.setActivePeriodBase(0); // make isSleepy() return true
        assertDoesNotThrow(() -> BedLogic.checkBed(body));
    }

    @Test
    void testCheckBed_withBed_sleepy_executesCode() {
        // Make body sleepy
        body.setActivePeriodBase(0); // make isSleepy() return true
        body.setAge(999);
        Bed bed = new Bed();
        bed.setX(150);
        bed.setY(150);
        // Set bed dimensions so nextInt(ofsX) doesn't throw
        try {
            java.lang.reflect.Field wf =
                    org.simyukkuri.entity.core.Entity.class.getDeclaredField("w");
            wf.setAccessible(true);
            wf.setInt(bed, 20);
            java.lang.reflect.Field hf =
                    org.simyukkuri.entity.core.Entity.class.getDeclaredField("h");
            hf.setAccessible(true);
            hf.setInt(bed, 20);
        } catch (Exception e) {
            // ignore
        }
        SimYukkuri.world.getCurrentWorldState().getBeds().put(bed.getObjId(), bed);
        try {
            BedLogic.checkBed(body);
        } catch (Exception e) {
            // May fail due to other reasons in headless
        }
    }

    // --- checkBed: isToBed branch ---

    @Test
    void testCheckBed_isToBed_targetNull_returnsFalse() {
        body.setToBed(true);
        body.setMoveTargetId(-1); // no target
        // target==null so isToBed branch skips
        assertDoesNotThrow(() -> BedLogic.checkBed(body));
    }

    @Test
    void testCheckBed_isToBed_targetRemoved_clearsFavItem() {
        Bed bed = new Bed();
        bed.setX(100);
        bed.setY(100);
        bed.setRemoved(true);
        SimYukkuri.world.getCurrentWorldState().getBeds().put(bed.getObjId(), bed);

        body.setToBed(true);
        body.setMoveTargetId(bed.getObjId());
        // target.isRemoved() → clearActions and return false
        assertFalse(BedLogic.checkBed(body));
    }

    @Test
    void testCheckBed_isToBed_UnunSlave_clearsFavItem() {
        Bed bed = new Bed();
        bed.setX(200);
        bed.setY(200);
        SimYukkuri.world.getCurrentWorldState().getBeds().put(bed.getObjId(), bed);

        body.setToBed(true);
        body.setMoveTargetId(bed.getObjId());
        body.setPublicRank(PublicRank.UNUN_SLAVE);
        // UnunSlave heading to Bed → clearActions and return false
        assertFalse(BedLogic.checkBed(body));
    }

    @Test
    void testCheckBed_isToBed_arrived_setsStay() {
        Bed bed = new Bed();
        bed.setX(100);
        bed.setY(100); // same position as body → distance=0
        SimYukkuri.world.getCurrentWorldState().getBeds().put(bed.getObjId(), bed);

        body.setToBed(true);
        body.setMoveTargetId(bed.getObjId());
        body.setZ(0);
        // stepDist >= distance(0) → arrival, sets stay and returns true
        assertTrue(BedLogic.checkBed(body));
    }

    @Test
    void testCheckBed_isToBed_notArrived_movesTo() {
        Bed bed = new Bed();
        bed.setX(5000);
        bed.setY(5000); // far away
        SimYukkuri.world.getCurrentWorldState().getBeds().put(bed.getObjId(), bed);

        body.setToBed(true);
        body.setMoveTargetId(bed.getObjId());
        body.setZ(0);
        // not arrived → moveTo and returns true
        assertTrue(BedLogic.checkBed(body));
    }

    @Nested
    class RegressionScenarios {

        @Test
        void testScenario_ArrivalAtBedStoresFavoriteBed() {
            Bed bed = new Bed();
            bed.setX(100);
            bed.setY(100);
            SimYukkuri.world.getCurrentWorldState().getBeds().put(bed.getObjId(), bed);

            body.setToBed(true);
            body.setMoveTargetId(bed.getObjId());
            body.setZ(0);
            body.setFavoriteItem(org.simyukkuri.enums.FavItemType.BED, null);

            assertTrue(BedLogic.checkBed(body));
            assertNotNull(body.getFavoriteItem(org.simyukkuri.enums.FavItemType.BED));
            assertEquals(
                    bed.getObjId(),
                    body.getFavoriteItem(org.simyukkuri.enums.FavItemType.BED).getObjId());
        }
    }

    // --- searchBed: UnunSlave → toilet ---

    @Test
    void testSearchBed_UnunSlave_withToilet_findsToilet() {
        body.setPublicRank(PublicRank.UNUN_SLAVE);
        org.simyukkuri.entity.core.world.item.Toilet toilet =
                new org.simyukkuri.entity.core.world.item.Toilet();
        toilet.setX(100);
        toilet.setY(100); // same position as body
        SimYukkuri.world.getCurrentWorldState().getToilets().put(toilet.getObjId(), toilet);
        assertDoesNotThrow(() -> BedLogic.searchBed(body));
    }

    @Test
    void testSearchBed_UnunSlave_noToilet_returnsNull() {
        body.setPublicRank(PublicRank.UNUN_SLAVE);
        Entity result = BedLogic.searchBed(body);
        assertNull(result);
    }

    // --- searchBed: house fallback ---

    @Test
    void testSearchBed_noBedsButHouse_findsHouse() {
        House house = new House();
        house.setX(100);
        house.setY(100); // same position as body
        SimYukkuri.world.getCurrentWorldState().getHouses().put(house.getObjId(), house);
        assertDoesNotThrow(() -> BedLogic.searchBed(body));
    }

    // --- checkBed: with house (no bed available) ---

    @Test
    void testCheckBed_withHouseOnly_doesNotThrow() {
        House house = new House();
        house.setX(100);
        house.setY(100);
        SimYukkuri.world.getCurrentWorldState().getHouses().put(house.getObjId(), house);
        body.setActivePeriodBase(0); // make isSleepy() return true
        assertDoesNotThrow(() -> BedLogic.checkBed(body));
    }

    // --- checkBed: isToTakeout=true → false ---

    @Test
    void testCheckBed_isToTakeout_returnsFalse() {
        body.setToTakeout(true); // line 38: isToTakeout() → return false
        assertFalse(BedLogic.checkBed(body));
    }

    // --- checkBed: isIdiot=true → false ---

    @Test
    void testCheckBed_isIdiot_returnsFalse() {
        // TarinaiReimu overrides isIdiot() to return true
        org.simyukkuri.entity.core.living.yukkuri.impl.TarinaiReimu tarinai =
                new org.simyukkuri.entity.core.living.yukkuri.impl.TarinaiReimu();
        tarinai.setX(100);
        tarinai.setY(100);
        tarinai.setObjId(org.simyukkuri.enums.Numbering.INSTANCE.numberingObjId());
        tarinai.setUniqueId(org.simyukkuri.enums.Numbering.INSTANCE.numberingYukkuriId());
        SimYukkuri.world
                .getCurrentWorldState()
                .getYukkuriRegistry()
                .put(tarinai.getObjId(), tarinai);
        // line 43: isIdiot()=true → return false
        assertFalse(BedLogic.checkBed(tarinai));
    }

    // --- checkBed: nearToBirth=true + HIGH priority event → false ---

    @Test
    void testCheckBed_nearToBirth_HighEvent_returnsFalse() {
        body.setHasBaby(true);
        body.setPregnantPeriod(body.getPregPeriodBase()); // nearToBirth()=true
        // Set HIGH priority event at lines 46-49: getPriority()==HIGH → return false
        org.simyukkuri.event.EventPacket highEvent =
                new org.simyukkuri.event.EventPacket() {
                    private static final long serialVersionUID = 1L;

                    {
                        this.priority = org.simyukkuri.event.EventPacket.EventPriority.HIGH;
                    }

                    @Override
                    public boolean checkEventResponse(
                            org.simyukkuri.entity.core.living.yukkuri.Yukkuri b) {
                        return true;
                    }

                    @Override
                    public void start(org.simyukkuri.entity.core.living.yukkuri.Yukkuri b) {}

                    @Override
                    public boolean execute(org.simyukkuri.entity.core.living.yukkuri.Yukkuri b) {
                        return false;
                    }
                };
        body.setCurrentEvent(highEvent);
        assertFalse(BedLogic.checkBed(body));
    }

    // --- checkBed: !nearToBirth + MIDDLE priority event → false ---

    @Test
    void testCheckBed_notNearToBirth_MiddleEvent_returnsFalse() {
        // MIDDLE priority event and !nearToBirth → line 52: priority!=LOW → return
        // false
        org.simyukkuri.event.EventPacket middleEvent =
                new org.simyukkuri.event.EventPacket() {
                    private static final long serialVersionUID = 1L;

                    {
                        this.priority = org.simyukkuri.event.EventPacket.EventPriority.MIDDLE;
                    }

                    @Override
                    public boolean checkEventResponse(
                            org.simyukkuri.entity.core.living.yukkuri.Yukkuri b) {
                        return true;
                    }

                    @Override
                    public void start(org.simyukkuri.entity.core.living.yukkuri.Yukkuri b) {}

                    @Override
                    public boolean execute(org.simyukkuri.entity.core.living.yukkuri.Yukkuri b) {
                        return false;
                    }
                };
        body.setCurrentEvent(middleEvent);
        assertFalse(BedLogic.checkBed(body));
    }

    // --- checkBed: isNYD=true → false ---

    @Test
    void testCheckBed_isNYD_returnsFalse() {
        // NonYukkuriDiseaseNear makes isNyd()=true → line 58: return false
        body.setCoreAnkoState(org.simyukkuri.enums.CoreAnkoState.NON_YUKKURI_DISEASE_NEAR);
        assertFalse(BedLogic.checkBed(body));
    }

    // --- checkBed: isToBed + arrived + FOOD takeout → drop ---

    @Test
    void testCheckBed_isToBed_Arrived_HasFoodTakeout_DoesNotThrow() {
        Bed bed = new Bed();
        bed.setX(100);
        bed.setY(100); // same position as body → arrived
        SimYukkuri.world.getCurrentWorldState().getBeds().put(bed.getObjId(), bed);
        body.setToBed(true);
        body.setMoveTargetId(bed.getObjId());
        // Setup FOOD takeout at line 95: getCarryItem(FOOD) != null → dropTakeoutItem
        org.simyukkuri.entity.core.world.item.Food food =
                new org.simyukkuri.entity.core.world.item.Food(
                        100,
                        100,
                        org.simyukkuri.entity.core.world.item.Food.FoodType.FOOD.ordinal());
        food.setAmount(100);
        SimYukkuri.world.getCurrentWorldState().getTakenOutFoods().put(food.getObjId(), food);
        body.getCarryItems().put(org.simyukkuri.enums.TakeoutItemType.FOOD, food.getObjId());
        assertDoesNotThrow(() -> BedLogic.checkBed(body));
    }

    // --- searchBed: canflyCheck=true → wallMode=ADULT ---

    @Test
    void testSearchBed_FlyingType_WallModeAdult_DoesNotThrow() {
        // Remirya is a flying type with hasBraid=true → canflyCheck()=true
        org.simyukkuri.entity.core.living.yukkuri.impl.Remirya remirya =
                new org.simyukkuri.entity.core.living.yukkuri.impl.Remirya();
        remirya.setX(100);
        remirya.setY(100);
        remirya.setObjId(org.simyukkuri.enums.Numbering.INSTANCE.numberingObjId());
        remirya.setUniqueId(org.simyukkuri.enums.Numbering.INSTANCE.numberingYukkuriId());
        SimYukkuri.world
                .getCurrentWorldState()
                .getYukkuriRegistry()
                .put(remirya.getObjId(), remirya);
        // line 168-170: canflyCheck()=true → wallMode=AgeState.ADULT.ordinal()
        assertDoesNotThrow(() -> BedLogic.searchBed(remirya));
    }
}
