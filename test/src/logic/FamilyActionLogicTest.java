package src.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import src.util.WorldTestHelper;
import src.base.Body;
import src.item.Food;
import src.item.Toilet;
import src.base.Obj;
import src.SimYukkuri;
import src.enums.AgeState;
import src.enums.PublicRank;
import src.event.ProudChildEvent;
import src.event.ShitExercisesEvent;
import src.event.SuperEatingTimeEvent;
import src.event.YukkuriRideEvent;
import src.event.FuneralEvent;

import src.ConstState;

class FamilyActionLogicTest {

    private Body parent;
    private Body child;
    private java.util.Random originalRandom;

    @BeforeEach
    void setUp() {
        WorldTestHelper.resetWorld();
        WorldTestHelper.initializeMinimalWorld();

        parent = WorldTestHelper.createBody();
        child = WorldTestHelper.createBody();

        parent.setX(50);
        parent.setY(50);
        parent.setAge(parent.getChildLimitBase() + 1); // ADULT

        child.setX(55);
        child.setY(55);
        child.setAge(0); // BABY
        child.setFirstEatStalk(true);
        child.giveOkazari(src.base.Okazari.OkazariType.DEFAULT); // Ensure proper OKAZARI
        parent.giveOkazari(src.base.Okazari.OkazariType.DEFAULT);

        WorldTestHelper.addChild(parent, child.getUniqueID());
        WorldTestHelper.setParents(child, parent.getUniqueID(), -1);

        SimYukkuri.world.getCurrentMap().getBody().put(parent.getUniqueID(), parent);
        SimYukkuri.world.getCurrentMap().getBody().put(child.getUniqueID(), child);

        originalRandom = SimYukkuri.RND;
        // Use ConstState(0) to force nextInt(X) to return 0, ensuring basic
        // probabilities pass
        SimYukkuri.RND = new ConstState(0);
    }

    @AfterEach
    void tearDown() {
        SimYukkuri.RND = originalRandom;
        WorldTestHelper.resetWorld();
    }

    // A helper method to fast-forward age so we can do wait time checks easily
    private void setReady(Body b) {
        b.setLastActionTime(0L);
    }

    @Test
    void testCheckFamilyAction_EarlyReturns_Tasks() {
        parent.setToFood(true);
        assertFalse(FamilyActionLogic.checkFamilyAction(parent));
        parent.setToFood(false);

        parent.setToSukkiri(true);
        assertFalse(FamilyActionLogic.checkFamilyAction(parent));
        parent.setToSukkiri(false);
    }

    @Test
    void testCheckFamilyAction_RandomCheckFails() {
        SimYukkuri.RND = new ConstState(1); // nextInt(300) will be 1 -> != 0
        assertFalse(FamilyActionLogic.checkFamilyAction(parent));
    }

    @Test
    void testCheckFamilyAction_EventChecks() {
        parent.setCurrentEvent(new SuperEatingTimeEvent(parent, null, null, 1));
        assertTrue(FamilyActionLogic.checkFamilyAction(parent));

        parent.setCurrentEvent(new ShitExercisesEvent(parent, null, null, 1));
        assertTrue(FamilyActionLogic.checkFamilyAction(parent));

        parent.setCurrentEvent(new YukkuriRideEvent(parent, null, null, 1));
        assertTrue(FamilyActionLogic.checkFamilyAction(parent));

        parent.setCurrentEvent(new ProudChildEvent(parent, null, null, 1));
        assertTrue(FamilyActionLogic.checkFamilyAction(parent));

        parent.setCurrentEvent(new FuneralEvent(parent, null, null, 1));
        assertTrue(FamilyActionLogic.checkFamilyAction(parent));

        // Unknown event -> false (using an ad-hoc anonymous event)
        parent.setCurrentEvent(new src.base.EventPacket(parent, null, null, 1) {
            @Override
            public void start(Body b) {
            }

            @Override
            public boolean execute(Body b) {
                return false;
            }

            @Override
            public boolean checkEventResponse(Body b) {
                return false;
            }
        });
        assertFalse(FamilyActionLogic.checkFamilyAction(parent));
        parent.setCurrentEvent(null);
    }

    @Test
    void testCheckFamilyAction_NotAdult() {
        parent.setAge(10); // Not adult
        assertFalse(FamilyActionLogic.checkFamilyAction(parent));
    }

    @Test
    void testCheckFamilyAction_SelfStateChecks() {

        parent.addDamage(parent.getDamageLimitBase()[AgeState.ADULT.ordinal()] / 2); // VERY or OVER
        assertFalse(FamilyActionLogic.checkFamilyAction(parent));
        parent.addDamage(-99999);

        parent.takeOkazari(true);
        assertFalse(FamilyActionLogic.checkFamilyAction(parent));
        parent.giveOkazari(src.base.Okazari.OkazariType.DEFAULT);

        parent.setPublicRank(PublicRank.UnunSlave);
        assertFalse(FamilyActionLogic.checkFamilyAction(parent));
        parent.setPublicRank(PublicRank.NONE);

        // NYD test skipped to avoid complex internal state manipulation

        parent.setShitting(true);
        assertFalse(FamilyActionLogic.checkFamilyAction(parent));
        parent.setShitting(false);

        parent.setExciting(true);
        assertFalse(FamilyActionLogic.checkFamilyAction(parent));
        parent.setExciting(false);
    }

    @Test
    void testCheckFamilyAction_PartnerStateChecks() {
        Body partner = WorldTestHelper.createBody();
        // Make partner older to pass the age delegation check
        partner.setAge(parent.getAge() + 100);
        partner.giveOkazari(src.base.Okazari.OkazariType.DEFAULT);

        parent.setPartner(partner.getUniqueID());
        SimYukkuri.world.getCurrentMap().getBody().put(partner.getUniqueID(), partner);

        partner.setCurrentEvent(new src.base.EventPacket(partner, null, null, 1) {
            @Override
            public void start(Body b) {
            }

            @Override
            public boolean execute(Body b) {
                return false;
            }

            @Override
            public boolean checkEventResponse(Body b) {
                return false;
            }
        });
        assertFalse(FamilyActionLogic.checkFamilyAction(parent));
        partner.setCurrentEvent(null);

        // Partner is YOUNGER (parent delegates action and returns false)
        partner.setAge(parent.getAge() - 100);
        assertFalse(FamilyActionLogic.checkFamilyAction(parent));
        // Reset to older to continue testing other partner states
        partner.setAge(parent.getAge() + 100);

        // Partner damaged
        partner.addDamage(parent.getDamageLimitBase()[AgeState.ADULT.ordinal()]);
        assertFalse(FamilyActionLogic.checkFamilyAction(parent));
        partner.addDamage(-99999);
    }

    @Test
    void testCheckFamilyAction_ChildStateFailsEatingAndShitting() {
        setReady(parent);

        // Child damaged -> cancels all
        child.addDamage(parent.getDamageLimitBase()[AgeState.BABY.ordinal()]);
        // Doesn't trigger any event
        assertFalse(FamilyActionLogic.checkFamilyAction(parent));
        child.addDamage(-99999);

        // Child firstEatStalk false -> cancels all
        child.setFirstEatStalk(false);
        assertFalse(FamilyActionLogic.checkFamilyAction(parent));
        child.setFirstEatStalk(true);

        // Child shit <= 25% -> cancels shit
        child.setShit((int) (child.getShitLimit() * 0.1));
        child.setHungry(child.getHungryLimit()); // Full
        // Returns true because rideOnParent hits with nextInt
        // Actually rideOnParent checks child.isHungry, wantToShit, sleepy.
        // If all false, might not do anything.
        assertFalse(FamilyActionLogic.checkFamilyAction(parent));
    }

    @Test
    void testSearchFood_StalkAndWaste() {
        Food stalk = new Food(150, 150, Food.FoodType.STALK.ordinal());
        Food waste = new Food(120, 120, Food.FoodType.WASTE.ordinal());
        SimYukkuri.world.getCurrentMap().getFood().put(stalk.getObjId(), stalk);
        SimYukkuri.world.getCurrentMap().getFood().put(waste.getObjId(), waste);

        // Normal adult doesn't eat waste usually
        Obj found = FamilyActionLogic.searchFood(parent);
        assertNotNull(found); // Stalk is found

        // Setup POOR tang property implicitly by starvation
        parent.setHungry(0); // Starving
        Obj foundWaste = FamilyActionLogic.searchFood(parent);
        assertNotNull(foundWaste);
    }

    @Test
    void testCheckFamilyAction_HungryChild_GoesToEat() {
        setReady(parent);

        // Child hungry
        child.setHungry((int) (child.getHungryLimit() * 0.1));
        child.setShit(0);

        Food food = new Food(150, 150, Food.FoodType.SWEETS1.ordinal());
        SimYukkuri.world.getCurrentMap().getFood().put(food.getObjId(), food);

        boolean result = FamilyActionLogic.checkFamilyAction(parent);
        assertTrue(result, "Should go to eat");
    }

    @Test
    void testCheckFamilyAction_ShitChild_GoesToShit() {
        setReady(parent);

        child.setHungry(child.getHungryLimit());
        child.setShit((int) (child.getShitLimit() * 0.8)); // 80%

        Toilet toilet = new Toilet();
        toilet.setX(150);
        toilet.setY(150);
        SimYukkuri.world.getCurrentMap().getToilet().put(toilet.getObjId(), toilet);

        boolean result = FamilyActionLogic.checkFamilyAction(parent);
        assertTrue(result, "Should go to shit");
    }

    @Test
    void testCheckFamilyAction_ProudChild() {
        setReady(parent);

        child.setHungry(child.getHungryLimit());
        child.setShit(0); // Neither hungry nor needs to shit

        // Ensure nextBoolean() returns true for proudChild, and nextInt(300) returns 0
        // to pass the initial check
        SimYukkuri.RND = new java.util.Random() {
            @Override
            public int nextInt(int bound) {
                return 0;
            }

            @Override
            public boolean nextBoolean() {
                return true;
            }
        };

        boolean result = FamilyActionLogic.checkFamilyAction(parent);
        assertTrue(result, "Should initiate proud child or ride");
    }

    @Test
    void testCheckFamilyAction_HungryChildStartsSuperEatingTimeEvent() {
        setReady(parent);

        child.setHungry(0);
        child.setShit(0);

        Food food = new Food(150, 150, Food.FoodType.SWEETS1.ordinal());
        SimYukkuri.world.getCurrentMap().getFood().put(food.getObjId(), food);

        boolean result = FamilyActionLogic.checkFamilyAction(parent);

        assertTrue(result);
        assertEquals(1, SimYukkuri.world.getCurrentMap().getEvent().size());
        assertTrue(SimYukkuri.world.getCurrentMap().getEvent().get(0) instanceof SuperEatingTimeEvent);
        assertTrue(parent.getCurrentEvent() instanceof SuperEatingTimeEvent);
    }

    @Test
    void testCheckFamilyAction_BabyNeedsToShitStartsShitExercisesEvent() {
        setReady(parent);

        child.setHungry(child.getHungryLimit());
        child.setShit((int) (child.getShitLimit() * 0.8));

        Toilet toilet = new Toilet();
        toilet.setX(150);
        toilet.setY(150);
        SimYukkuri.world.getCurrentMap().getToilet().put(toilet.getObjId(), toilet);

        boolean result = FamilyActionLogic.checkFamilyAction(parent);

        assertTrue(result);
        assertEquals(1, SimYukkuri.world.getCurrentMap().getEvent().size());
        assertTrue(SimYukkuri.world.getCurrentMap().getEvent().get(0) instanceof ShitExercisesEvent);
        assertTrue(parent.getCurrentEvent() instanceof ShitExercisesEvent);
    }

    @Test
    void testCheckFamilyAction_NoEatNoShitStartsProudChildEvent() {
        setReady(parent);

        child.setHungry(child.getHungryLimit());
        child.setShit(0);

        SimYukkuri.RND = new java.util.Random() {
            @Override
            public int nextInt(int bound) {
                return 0;
            }

            @Override
            public boolean nextBoolean() {
                return true;
            }
        };

        boolean result = FamilyActionLogic.checkFamilyAction(parent);

        assertTrue(result);
        assertEquals(1, SimYukkuri.world.getCurrentMap().getEvent().size());
        assertTrue(SimYukkuri.world.getCurrentMap().getEvent().get(0) instanceof ProudChildEvent);
        assertTrue(parent.getCurrentEvent() instanceof ProudChildEvent);
    }

    @Test
    void testRideOnParent() {
        setReady(parent);

        child.setHungry(child.getHungryLimit());
        child.setShit(0);

        // Let child seek bed implicitly by starvation instead if needed
        // We will rely on searching for Food below

        // Prevent proudChild from executing using ConstState(0)
        // Note: ConstState(0) makes nextBoolean return false usually (in some java
        // impls nextInt is mapped)
        SimYukkuri.RND = new ConstState(0) {
            @Override
            public boolean nextBoolean() {
                return false;
            }
        };

        boolean result = FamilyActionLogic.checkFamilyAction(parent);
        // Will initiate rideOnParent if it finds a bed, but MinimalWorld has no bed.
        // Let's create a bed or food. We will make child hungry again.
        child.setHungry((int) (child.getHungryLimit() * 0.1));
        Food food = new Food(80, 80, Food.FoodType.SWEETS1.ordinal());
        SimYukkuri.world.getCurrentMap().getFood().put(food.getObjId(), food);

        // However, bWantToEat is evaluated in checkFamilyAction!
        // To test ONLY rideOnParent directly, we call it manually
        setReady(parent); // Reset wait time again
        java.util.List<Body> list = new java.util.ArrayList<>();
        list.add(child);
        assertTrue(FamilyActionLogic.rideOnParent(parent, list));
        // No need to reset day state
    }

    @Test
    void testIsRapeTarget() {
        // Child is neither unbirth, dead, removed, nor raper
        assertTrue(FamilyActionLogic.isRapeTarget());

        child.setUnBirth(true);
        parent.setUnBirth(true);
        assertFalse(FamilyActionLogic.isRapeTarget());
    }

    @Test
    void testCheckFamilyAction_IgnoresStrayChild() {
        setReady(parent);

        Body stray = WorldTestHelper.createBody();
        stray.setX(60);
        stray.setY(60);
        stray.setHungry(0); // Starving
        SimYukkuri.world.getCurrentMap().getBody().put(stray.getUniqueID(), stray);

        // Child is fine
        child.setHungry(child.getHungryLimit());
        child.setShit(0);

        // Food is available
        Food food = new Food(150, 150, Food.FoodType.SWEETS1.ordinal());
        SimYukkuri.world.getCurrentMap().getFood().put(food.getObjId(), food);

        // Should return false because stray is not family
        assertFalse(FamilyActionLogic.checkFamilyAction(parent));
    }

    @Test
    void testSearchToilet_Distance() {
        Toilet farToilet = new Toilet();
        farToilet.setX(300);
        farToilet.setY(300);

        Toilet closeToilet = new Toilet();
        closeToilet.setX(60);
        closeToilet.setY(60);

        SimYukkuri.world.getCurrentMap().getToilet().put(farToilet.getObjId(), farToilet);
        SimYukkuri.world.getCurrentMap().getToilet().put(closeToilet.getObjId(), closeToilet);

        Obj found = FamilyActionLogic.searchToilet(parent);
        assertEquals((Object) closeToilet, (Object) found, "Should find the closest toilet");
    }

    @Test
    void testCheckFamilyAction_DelegatesToYoungerPartner() {
        setReady(parent);
        parent.setHungry(0); // Not full

        Body partner = WorldTestHelper.createBody();
        partner.setAge(parent.getAge() + 10); // partner is OLDER
        parent.setPartner(partner.getUniqueID());
        partner.setPartner(parent.getUniqueID());
        partner.setHungry(0); // Not full
        partner.giveOkazari(src.base.Okazari.OkazariType.DEFAULT);

        SimYukkuri.world.getCurrentMap().getBody().put(partner.getUniqueID(), partner);
        WorldTestHelper.setParents(child, parent.getUniqueID(), partner.getUniqueID());
        WorldTestHelper.addChild(partner, child.getUniqueID()); // Partner also needs to know about child

        // Child needs food
        child.setHungry(0);
        Food food = new Food(150, 150, Food.FoodType.SWEETS1.ordinal());
        SimYukkuri.world.getCurrentMap().getFood().put(food.getObjId(), food);

        // Younger (parent) SHOULD act
        assertTrue(src.logic.FamilyActionLogic.checkFamilyAction(parent), "Younger partner should act");

        // Older (partner) SHOULD delegate (return false)
        setReady(partner);
        assertFalse(src.logic.FamilyActionLogic.checkFamilyAction(partner), "Older partner should delegate");
    }

    // =========================================================
    // Group 1: Other early return flags in checkFamilyAction L40
    // =========================================================

    @Test
    void testCheckFamilyAction_EarlyReturns_OtherFlags() {
        parent.setToBody(true);
        assertFalse(FamilyActionLogic.checkFamilyAction(parent));
        parent.setToBody(false);

        parent.setToBed(true);
        assertFalse(FamilyActionLogic.checkFamilyAction(parent));
        parent.setToBed(false);

        parent.setToShit(true);
        assertFalse(FamilyActionLogic.checkFamilyAction(parent));
        parent.setToShit(false);

        parent.setToSteal(true);
        assertFalse(FamilyActionLogic.checkFamilyAction(parent));
        parent.setToSteal(false);

        parent.setToTakeout(true);
        assertFalse(FamilyActionLogic.checkFamilyAction(parent));
        parent.setToTakeout(false);
    }

    // =========================================================
    // Group 2: isNYD check in self state
    // =========================================================

    @Test
    void testCheckFamilyAction_IsNYD_ReturnsFalse() {
        parent.setCoreAnkoState(src.enums.CoreAnkoState.NonYukkuriDiseaseNear);
        assertFalse(FamilyActionLogic.checkFamilyAction(parent));
    }

    // =========================================================
    // Group 3: isBirth, isEating, nearToBirth checks
    // =========================================================

    @Test
    void testCheckFamilyAction_IsBirth_ReturnsFalse() {
        parent.setBirth(true);
        assertFalse(FamilyActionLogic.checkFamilyAction(parent));
        parent.setBirth(false);
    }

    @Test
    void testCheckFamilyAction_IsEating_ReturnsFalse() {
        parent.setEating(true);
        assertFalse(FamilyActionLogic.checkFamilyAction(parent));
        parent.setEating(false);
    }

    @Test
    void testCheckFamilyAction_NearToBirth_ReturnsFalse() {
        parent.setHasBaby(true);
        parent.setPregnantPeriod(parent.getPregPeriodBase());
        assertFalse(FamilyActionLogic.checkFamilyAction(parent));
    }

    // =========================================================
    // Group 4: Partner second block checks
    // =========================================================

    @Test
    void testCheckFamilyAction_PartnerLockmove_ReturnsFalse() {
        Body partner2 = WorldTestHelper.createBody();
        partner2.setAge(parent.getAge() + 100);
        partner2.giveOkazari(src.base.Okazari.OkazariType.DEFAULT);
        partner2.setLockmove(true);
        parent.setPartner(partner2.getUniqueID());
        SimYukkuri.world.getCurrentMap().getBody().put(partner2.getUniqueID(), partner2);
        assertFalse(FamilyActionLogic.checkFamilyAction(parent));
    }

    @Test
    void testCheckFamilyAction_PartnerNearToBirth_ReturnsFalse() {
        Body partner2 = WorldTestHelper.createBody();
        partner2.setAge(parent.getAge() + 100);
        partner2.giveOkazari(src.base.Okazari.OkazariType.DEFAULT);
        partner2.setHasBaby(true);
        partner2.setPregnantPeriod(partner2.getPregPeriodBase());
        parent.setPartner(partner2.getUniqueID());
        SimYukkuri.world.getCurrentMap().getBody().put(partner2.getUniqueID(), partner2);
        assertFalse(FamilyActionLogic.checkFamilyAction(parent));
    }

    @Test
    void testCheckFamilyAction_PartnerShitting_ReturnsFalse() {
        Body partner2 = WorldTestHelper.createBody();
        partner2.setAge(parent.getAge() + 100);
        partner2.giveOkazari(src.base.Okazari.OkazariType.DEFAULT);
        partner2.setShitting(true);
        parent.setPartner(partner2.getUniqueID());
        SimYukkuri.world.getCurrentMap().getBody().put(partner2.getUniqueID(), partner2);
        assertFalse(FamilyActionLogic.checkFamilyAction(parent));
    }

    @Test
    void testCheckFamilyAction_PartnerBirth_ReturnsFalse() {
        Body partner2 = WorldTestHelper.createBody();
        partner2.setAge(parent.getAge() + 100);
        partner2.giveOkazari(src.base.Okazari.OkazariType.DEFAULT);
        partner2.setBirth(true);
        parent.setPartner(partner2.getUniqueID());
        SimYukkuri.world.getCurrentMap().getBody().put(partner2.getUniqueID(), partner2);
        assertFalse(FamilyActionLogic.checkFamilyAction(parent));
    }

    @Test
    void testCheckFamilyAction_PartnerNeedled_ReturnsFalse() {
        Body partner2 = WorldTestHelper.createBody();
        partner2.setAge(parent.getAge() + 100);
        partner2.giveOkazari(src.base.Okazari.OkazariType.DEFAULT);
        partner2.setNeedled(true);
        parent.setPartner(partner2.getUniqueID());
        SimYukkuri.world.getCurrentMap().getBody().put(partner2.getUniqueID(), partner2);
        assertFalse(FamilyActionLogic.checkFamilyAction(parent));
    }

    // =========================================================
    // Group 5: Parent isFull → bWantToEat=false
    // =========================================================

    @Test
    void testCheckFamilyAction_ParentFull_ChildFull_ReturnsFalse() {
        setReady(parent);
        // hungry >= 80% of limit → isFull()=true
        parent.setHungry((int)(parent.getHungryLimit() * 0.9));
        // child full and no shit
        child.setHungry((int)(child.getHungryLimit() * 0.9));
        child.setShit(0);
        SimYukkuri.RND = new ConstState(0) {
            @Override
            public boolean nextBoolean() { return false; }
        };
        // bWantToEat=false (parent full), bWantToShit=false (child shit<=25%), bIsBaby=true but bWantToShit forced false
        // rideOnParent: child not hungry, not wantToShit, not sleepy → no target → false
        assertFalse(FamilyActionLogic.checkFamilyAction(parent));
    }

    // =========================================================
    // Group 6: Child isNeedled → bWantToShit=bWantToEat=false
    // =========================================================

    @Test
    void testCheckFamilyAction_ChildNeedled_ReturnsFalse() {
        setReady(parent);
        child.setNeedled(true);
        SimYukkuri.RND = new ConstState(0) {
            @Override
            public boolean nextBoolean() { return false; }
        };
        // isDamaged=false but isNeedled=true → bWantToShit=false, bWantToEat=false (break)
        // rideOnParent with empty childrenListForRideYukkuriTarget → false
        assertFalse(FamilyActionLogic.checkFamilyAction(parent));
    }

    // =========================================================
    // Group 7: Child isShitting → bWantToShit=false
    // =========================================================

    @Test
    void testCheckFamilyAction_BabyChildShitting_WantToShitFalse() {
        setReady(parent);
        child.setShit((int)(child.getShitLimit() * 0.5));
        // hungry >= 80% to force bWantToEat=false
        child.setHungry((int)(child.getHungryLimit() * 0.9));
        child.setShitting(true);
        SimYukkuri.RND = new ConstState(0) {
            @Override
            public boolean nextBoolean() { return false; }
        };
        // bWantToShit=false (child shitting), bWantToEat=false (dHungryPer>=80)
        // rideOnParent: child isShitting → continue → empty list → false
        assertFalse(FamilyActionLogic.checkFamilyAction(parent));
        child.setShitting(false);
    }

    // =========================================================
    // Group 8: Child shit >= 100% → bWantToShit=false
    // =========================================================

    @Test
    void testCheckFamilyAction_BabyChildShitFull_WantToShitFalse() {
        setReady(parent);
        child.setShit(child.getShitLimit()); // 100%
        child.setHungry((int)(child.getHungryLimit() * 0.9)); // >=80%
        SimYukkuri.RND = new ConstState(0) {
            @Override
            public boolean nextBoolean() { return false; }
        };
        assertFalse(FamilyActionLogic.checkFamilyAction(parent));
    }

    // =========================================================
    // Group 9: Child isHungry + isBaby → bWantToShit=false; dShitPer>50 → bWantToEat=false
    // =========================================================

    @Test
    void testCheckFamilyAction_BabyChildHungryAndShit60_BothFalse() {
        setReady(parent);
        child.setShit((int)(child.getShitLimit() * 0.6)); // 60% shit > 50
        // hungry <= 50% of limit → isHungry=true, and dHungryPer<80
        child.setHungry((int)(child.getHungryLimit() * 0.3));
        SimYukkuri.RND = new ConstState(0) {
            @Override
            public boolean nextBoolean() { return false; }
        };
        // bWantToShit=false (child isHungry), bWantToEat=false (dShitPer>50)
        // rideOnParent: child isHungry, searchFood=null → no target → false
        assertFalse(FamilyActionLogic.checkFamilyAction(parent));
    }

    // =========================================================
    // Group 10: Child isEating → bWantToEat=false
    // =========================================================

    @Test
    void testCheckFamilyAction_ChildEating_WantToEatFalse() {
        setReady(parent);
        child.setEating(true);
        child.setShit(0); // 0% → bWantToShit=false (<=25%)
        child.setHungry((int)(child.getHungryLimit() * 0.9)); // >=80%
        SimYukkuri.RND = new ConstState(0) {
            @Override
            public boolean nextBoolean() { return false; }
        };
        // bWantToEat=false (eating), bWantToShit=false (shit<=25%)
        // rideOnParent: child isEating=true → continue → false
        assertFalse(FamilyActionLogic.checkFamilyAction(parent));
        child.setEating(false);
    }

    // =========================================================
    // Group 11: Child isLockmove in child loop → continue (not break)
    // =========================================================

    @Test
    void testCheckFamilyAction_ChildLockmove_Continue() {
        setReady(parent);
        child.setLockmove(true);
        child.setHungry((int)(child.getHungryLimit() * 0.9));
        child.setShit(0);
        SimYukkuri.RND = new ConstState(0) {
            @Override
            public boolean nextBoolean() { return false; }
        };
        assertFalse(FamilyActionLogic.checkFamilyAction(parent));
        child.setLockmove(false);
    }

    // =========================================================
    // Group 12: Non-baby child → bIsBaby=false → bWantToShit=false
    // =========================================================

    @Test
    void testCheckFamilyAction_NoBabyChild_WantToShitFalse() {
        setReady(parent);
        // Make child an adult (not baby)
        child.setAge(child.getChildLimitBase() + 1);
        child.setHungry((int)(child.getHungryLimit() * 0.9)); // >=80%
        child.setShit(0);
        SimYukkuri.RND = new ConstState(0) {
            @Override
            public boolean nextBoolean() { return false; }
        };
        // bIsBaby=false → bWantToShit=false
        // bWantToEat=false (dHungryPer>=80)
        // rideOnParent: child.isBaby()=false → skipped → false
        assertFalse(FamilyActionLogic.checkFamilyAction(parent));
    }

    @Test
    void testCheckFamilyAction_ChildAgeOnly_NoProudChild() {
        // 赤ゆがいない（子ゆのみ）の場合 proudChild が発火しないことを確認するテスト
        // 修正前は bIsBaby=false でも proudChild が呼ばれバグの原因だった
        setReady(parent);
        // 子を子ゆ年齢（CHILD）に設定（BABY ではないが ADULT でもない）
        child.setAge(child.getBabyLimitBase() + 1); // CHILD age
        child.setHungry((int)(child.getHungryLimit() * 0.9)); // >=80% → bWantToEat=false
        child.setShit(0);
        SimYukkuri.RND = new ConstState(0) {
            @Override
            public boolean nextBoolean() { return true; } // bIsBaby=true なら proudChild に入る
        };
        // bIsBaby=false (子ゆのみ) → proudChild スキップ → rideOnParent も空 → false
        assertFalse(FamilyActionLogic.checkFamilyAction(parent));
        assertEquals(0, SimYukkuri.world.getCurrentMap().getEvent().size());
    }

    // =========================================================
    // Group 13: goToShit direct tests
    // =========================================================

    @Test
    void testGoToShit_CheckWaitFails_ReturnsFalse() {
        parent.setLastActionTime(); // set to now → checkWait(2000) fails
        java.util.List<Body> list = new java.util.ArrayList<>();
        list.add(child);
        assertFalse(FamilyActionLogic.goToShit(parent, list));
    }

    @Test
    void testGoToShit_Success() {
        setReady(parent);
        java.util.List<Body> list = new java.util.ArrayList<>();
        list.add(child);
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> {
            FamilyActionLogic.goToShit(parent, list);
        });
    }

    // =========================================================
    // Group 14: goToEat direct tests
    // =========================================================

    @Test
    void testGoToEat_NoFood_ReturnsFalse() {
        setReady(parent);
        java.util.List<Body> list = new java.util.ArrayList<>();
        list.add(child);
        assertFalse(FamilyActionLogic.goToEat(parent, list));
    }

    @Test
    void testGoToEat_CheckWaitFails_ReturnsFalse() {
        Food food = new Food(80, 80, Food.FoodType.SWEETS1.ordinal());
        SimYukkuri.world.getCurrentMap().getFood().put(food.getObjId(), food);
        parent.setLastActionTime(); // checkWait(5000) fails
        java.util.List<Body> list = new java.util.ArrayList<>();
        list.add(child);
        assertFalse(FamilyActionLogic.goToEat(parent, list));
    }

    @Test
    void testGoToEat_Success() {
        setReady(parent);
        Food food = new Food(80, 80, Food.FoodType.SWEETS1.ordinal());
        SimYukkuri.world.getCurrentMap().getFood().put(food.getObjId(), food);
        java.util.List<Body> list = new java.util.ArrayList<>();
        list.add(child);
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> {
            FamilyActionLogic.goToEat(parent, list);
        });
    }

    // =========================================================
    // Group 15: checkRaperFamily
    // =========================================================

    @Test
    void testCheckRaperFamily_TargetExists_ReturnsFalse() {
        // child is alive, not unbirth, not raper → isRapeTarget=true → returns false
        assertFalse(FamilyActionLogic.checkRaperFamily());
    }

    @Test
    void testCheckRaperFamily_NoTarget_ReturnsTrue() {
        // All bodies unbirth → isRapeTarget=false → return true
        parent.setUnBirth(true);
        child.setUnBirth(true);
        assertTrue(FamilyActionLogic.checkRaperFamily());
    }

    // =========================================================
    // Group 16: rideOnParent edge cases
    // =========================================================

    @Test
    void testRideOnParent_EmptyList_ReturnsFalse() {
        assertFalse(FamilyActionLogic.rideOnParent(parent, new java.util.ArrayList<>()));
    }

    @Test
    void testRideOnParent_CheckWaitFails_ReturnsFalse() {
        parent.setLastActionTime(); // makes checkWait(3000) fail
        java.util.List<Body> list = new java.util.ArrayList<>();
        list.add(child);
        assertFalse(FamilyActionLogic.rideOnParent(parent, list));
    }

    @Test
    void testRideOnParent_ChildNotBaby_SkippedReturnsFalse() {
        setReady(parent);
        // Make child adult
        child.setAge(child.getChildLimitBase() + 1);
        child.setHungry(0); // hungry
        java.util.List<Body> list = new java.util.ArrayList<>();
        list.add(child);
        // child.isBaby()=false → skipped → return false
        assertFalse(FamilyActionLogic.rideOnParent(parent, list));
    }

    @Test
    void testRideOnParent_BabyChildHungryFoodNearby_DistanceSmall_ReturnsFalse() {
        setReady(parent);
        child.setHungry(0); // hungry
        // Put food very close to child (at child's location)
        Food food = new Food(child.getX(), child.getY(), Food.FoodType.SWEETS1.ordinal());
        SimYukkuri.world.getCurrentMap().getFood().put(food.getObjId(), food);
        java.util.List<Body> list = new java.util.ArrayList<>();
        list.add(child);
        // distance < 10 → target=null, continue → return false
        assertFalse(FamilyActionLogic.rideOnParent(parent, list));
    }

    @Test
    void testRideOnParent_BabyChildHungryFoodFar_ReturnsTrue() {
        setReady(parent);
        child.setHungry(0); // hungry
        Food food = new Food(300, 300, Food.FoodType.SWEETS1.ordinal());
        SimYukkuri.world.getCurrentMap().getFood().put(food.getObjId(), food);
        java.util.List<Body> list = new java.util.ArrayList<>();
        list.add(child);
        // food far → distance >= 10 → YukkuriRideEvent started → return true
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> {
            assertTrue(FamilyActionLogic.rideOnParent(parent, list));
        });
    }

    @Test
    void testRideOnParent_BabyChildWantToShitToiletFar_ReturnsTrue() {
        setReady(parent);
        child.setHungry((int)(child.getHungryLimit() * 0.9)); // not hungry
        // Use setShit to push up shit value; wantToShit() uses getShitLimitBase
        child.setShit(child.getShitLimit() - 1); // near limit → wantToShit=true
        Toilet toilet = new Toilet();
        toilet.setX(300);
        toilet.setY(300);
        SimYukkuri.world.getCurrentMap().getToilet().put(toilet.getObjId(), toilet);
        java.util.List<Body> list = new java.util.ArrayList<>();
        list.add(child);
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> {
            assertTrue(FamilyActionLogic.rideOnParent(parent, list));
        });
    }

    @Test
    void testRideOnParent_BabyChildNoTargetFound_ReturnsFalse() {
        setReady(parent);
        child.setHungry((int)(child.getHungryLimit() * 0.9)); // not hungry
        child.setShit(0); // not wantToShit
        // not sleepy (wakeUpTime=0, ACTIVEPERIODorg=100*6=600, age is fresh)
        java.util.List<Body> list = new java.util.ArrayList<>();
        list.add(child);
        // No food, no toilet, no bed → no target → return false
        assertFalse(FamilyActionLogic.rideOnParent(parent, list));
    }

    // =========================================================
    // Group 17: proudChild direct tests
    // =========================================================

    @Test
    void testProudChild_CheckWaitFails_ReturnsFalse() {
        parent.setLastActionTime();
        java.util.List<Body> list = new java.util.ArrayList<>();
        list.add(child);
        assertFalse(FamilyActionLogic.proudChild(parent, list));
    }

    @Test
    void testProudChild_Success() {
        setReady(parent);
        java.util.List<Body> list = new java.util.ArrayList<>();
        list.add(child);
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> {
            assertTrue(FamilyActionLogic.proudChild(parent, list));
        });
    }

    // =========================================================
    // Group 18: searchFood additional cases
    // =========================================================

    @Test
    void testSearchFood_SWEETS1_Found() {
        Food sweets = new Food(80, 80, Food.FoodType.SWEETS1.ordinal());
        SimYukkuri.world.getCurrentMap().getFood().put(sweets.getObjId(), sweets);
        Obj found = FamilyActionLogic.searchFood(parent);
        assertNotNull(found);
    }

    @Test
    void testSearchFood_SWEETS2_Found() {
        Food sweets = new Food(80, 80, Food.FoodType.SWEETS2.ordinal());
        SimYukkuri.world.getCurrentMap().getFood().put(sweets.getObjId(), sweets);
        Obj found = FamilyActionLogic.searchFood(parent);
        assertNotNull(found);
    }

    @Test
    void testSearchFood_EmptyFood_Skipped() {
        Food empty = new Food(80, 80, Food.FoodType.SWEETS1.ordinal()) {
            @Override
            public boolean isEmpty() { return true; }
        };
        SimYukkuri.world.getCurrentMap().getFood().put(empty.getObjId(), empty);
        Obj found = FamilyActionLogic.searchFood(parent);
        org.junit.jupiter.api.Assertions.assertNull(found);
    }

    @Test
    void testSearchFood_Waste_NormalFullParent_NotFound() {
        // Only waste food, parent is full → waste not taken by normal parent
        Food waste = new Food(80, 80, Food.FoodType.WASTE.ordinal());
        SimYukkuri.world.getCurrentMap().getFood().put(waste.getObjId(), waste);
        parent.setHungry((int)(parent.getHungryLimit() * 0.9)); // full
        Obj found = FamilyActionLogic.searchFood(parent);
        org.junit.jupiter.api.Assertions.assertNull(found);
    }

    @Test
    void testSearchFood_Stalk_Found() {
        Food stalk = new Food(80, 80, Food.FoodType.STALK.ordinal());
        SimYukkuri.world.getCurrentMap().getFood().put(stalk.getObjId(), stalk);
        Obj found = FamilyActionLogic.searchFood(parent);
        assertNotNull(found);
    }

    // ---- isIdiot branch ----

    @Test
    void testCheckFamilyAction_IsIdiot_ReturnsFalse() {
        Body idiotBody = new src.yukkuri.Marisa() {
            @Override public boolean isIdiot() { return true; }
        };
        idiotBody.setX(50); idiotBody.setY(50);
        idiotBody.setAge(idiotBody.getChildLimitBase() + 1);
        idiotBody.giveOkazari(src.base.Okazari.OkazariType.DEFAULT);
        WorldTestHelper.addChild(idiotBody, child.getUniqueID());
        SimYukkuri.world.getCurrentMap().getBody().put(idiotBody.getUniqueID(), idiotBody);
        assertFalse(FamilyActionLogic.checkFamilyAction(idiotBody));
    }

    // ---- partner second block: getCriticalDamegeType and !hasOkazari ----

    @Test
    void testCheckFamilyAction_PartnerCriticalDamage_SecondBlock_ReturnsFalse() {
        Body partner = WorldTestHelper.createBody();
        partner.setAge(parent.getAge() + 100);
        partner.giveOkazari(src.base.Okazari.OkazariType.DEFAULT);
        partner.setCriticalDamegeType(src.enums.CriticalDamegeType.INJURED);
        parent.setPartner(partner.getUniqueID());
        SimYukkuri.world.getCurrentMap().getBody().put(partner.getUniqueID(), partner);
        assertFalse(FamilyActionLogic.checkFamilyAction(parent));
    }

    @Test
    void testCheckFamilyAction_PartnerNoOkazari_SecondBlock_ReturnsFalse() {
        Body partner = WorldTestHelper.createBody();
        partner.setAge(parent.getAge() + 100);
        partner.takeOkazari(true);
        parent.setPartner(partner.getUniqueID());
        SimYukkuri.world.getCurrentMap().getBody().put(partner.getUniqueID(), partner);
        assertFalse(FamilyActionLogic.checkFamilyAction(parent));
    }

    // ---- child loop: getCriticalDamegeType != null (INJURED passes isNotAllright) ----

    @Test
    void testCheckFamilyAction_ChildCriticalDamageInjured_BothFalse() {
        setReady(parent);
        child.setHungry(child.getHungryLimit());
        child.setShit(0);
        child.setCriticalDamegeType(src.enums.CriticalDamegeType.INJURED);
        SimYukkuri.RND = new ConstState(0) {
            @Override public boolean nextBoolean() { return false; }
        };
        assertFalse(FamilyActionLogic.checkFamilyAction(parent));
    }

    // ---- child loop: !hasOkazari -> continue (covers L143-146 and ride loop L216-217) ----

    @Test
    void testCheckFamilyAction_ChildNoOkazari_LoopContinue() {
        setReady(parent);
        child.takeOkazari(true); // !hasOkazari -> L143 true -> L144-146 executed -> both false
        child.setHungry(child.getHungryLimit());
        child.setShit(0);
        SimYukkuri.RND = new ConstState(0) {
            @Override public boolean nextBoolean() { return false; }
        };
        // ride loop: same child !hasOkazari -> L216 continue -> empty ride list -> false
        assertFalse(FamilyActionLogic.checkFamilyAction(parent));
        child.giveOkazari(src.base.Okazari.OkazariType.DEFAULT);
    }

    // ---- ride loop: child has currentEvent -> skip (L213-214) ----

    @Test
    void testCheckFamilyAction_RideLoop_ChildEvent_Skip() {
        setReady(parent);
        child.setHungry(child.getHungryLimit()); // full -> bWantToEat=false
        child.setShit(0); // 0% -> bWantToShit=false
        // Give child an event so ride loop skips it
        child.setCurrentEvent(new src.base.EventPacket(child, null, null, 1) {
            @Override public void start(Body b) {}
            @Override public boolean execute(Body b) { return false; }
            @Override public boolean checkEventResponse(Body b) { return false; }
        });
        SimYukkuri.RND = new ConstState(0) {
            @Override public boolean nextBoolean() { return false; }
        };
        // Both flags false -> ride loop entered -> child has event -> skipped -> empty ride list -> false
        assertFalse(FamilyActionLogic.checkFamilyAction(parent));
        child.setCurrentEvent(null);
    }

    // ---- L252 false branch: bWantToEat=true, goToEat finds no food ----

    @Test
    void testCheckFamilyAction_HungryChildNoFood_GoesToEatFails() {
        setReady(parent);
        child.setHungry(0); // hungry -> dHungryPer<80 -> bWantToEat stays true; isHungry=true -> bWantToShit=false
        child.setShit(0);
        SimYukkuri.RND = new ConstState(0) {
            @Override public boolean nextBoolean() { return false; }
        };
        // goToEat: no food in world -> found=null -> return false
        // rideOnParent: bWantToEat=true -> ride loop NOT entered -> empty list -> false
        assertFalse(FamilyActionLogic.checkFamilyAction(parent));
    }

    // ---- L263 false branch: bWantToShit=true, goToShit checkWait fails ----

    @Test
    void testCheckFamilyAction_WantToShitGoToShitCheckWaitFails() {
        // Do NOT call setReady(parent) -> checkWait fails
        child.setHungry((int)(child.getHungryLimit() * 0.9)); // dHungryPer=90>=80 -> bWantToEat=false; isHungry=false
        child.setShit((int)(child.getShitLimit() * 0.5)); // 50% -> bWantToShit stays true for baby
        parent.setLastActionTime(); // goToShit checkWait(2000) fails
        SimYukkuri.RND = new ConstState(0) {
            @Override public boolean nextBoolean() { return false; }
        };
        // bWantToShit=true, bWantToEat=false -> goToShit called -> checkWait fails -> false
        // bWantToShit=true -> ride loop NOT entered -> rideOnParent(empty) -> false
        assertFalse(FamilyActionLogic.checkFamilyAction(parent));
    }

    // ---- L270 false branch: nextBoolean=true, proudChild checkWait fails ----

    @Test
    void testCheckFamilyAction_ProudChildCheckWaitFails() {
        child.setHungry((int)(child.getHungryLimit() * 0.9)); // bWantToEat=false
        child.setShit(0); // bWantToShit=false
        parent.setLastActionTime(); // proudChild checkWait(2000) fails; rideOnParent checkWait(3000) also fails
        SimYukkuri.RND = new java.util.Random() {
            @Override public int nextInt(int bound) { return 0; }
            @Override public boolean nextBoolean() { return true; } // enter proudChild branch
        };
        assertFalse(FamilyActionLogic.checkFamilyAction(parent));
    }

    // ---- L276-277: rideOnParent returns true via checkFamilyAction (sleepy child + bed) ----

    @Test
    void testCheckFamilyAction_RideOnParentReturnsTrueViaBed() {
        setReady(parent);
        child.setHungry((int)(child.getHungryLimit() * 0.9)); // dHungryPer=90>=80 -> bWantToEat=false; isHungry=false
        child.setShit(0); // bWantToShit=false
        child.setWakeUpTime(Long.MIN_VALUE / 2); // isSleepy=true even at age=0

        src.item.Bed bed = new src.item.Bed();
        bed.setX(300); bed.setY(300);
        SimYukkuri.world.getCurrentMap().getBed().put(bed.getObjId(), bed);

        SimYukkuri.RND = new ConstState(0) {
            @Override public boolean nextBoolean() { return false; }
        };
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() ->
            assertTrue(FamilyActionLogic.checkFamilyAction(parent))
        );
    }

    // ---- searchFood: default food type (FOOD) ----

    @Test
    void testSearchFood_DefaultFoodType_Found() {
        Food food = new Food(80, 80, Food.FoodType.FOOD.ordinal());
        SimYukkuri.world.getCurrentMap().getFood().put(food.getObjId(), food);
        Obj found = FamilyActionLogic.searchFood(parent);
        assertNotNull(found);
    }

    // ---- checkRaperFamily: with actual raper body (covers L432-433 setExciting) ----

    @Test
    void testCheckRaperFamily_WithActualRaper() {
        // Make parent a raper, child unbirth -> isRapeTarget=false -> loop -> raper.setExciting(false)
        parent.setRaper(true);
        child.setUnBirth(true);
        assertTrue(FamilyActionLogic.checkRaperFamily());
    }

    // ---- isRapeTarget: dead and raper cases ----

    @Test
    void testIsRapeTarget_DeadBody() {
        parent.setDead(true);
        child.setDead(true);
        assertFalse(FamilyActionLogic.isRapeTarget());
    }

    @Test
    void testIsRapeTarget_RaperBody() {
        parent.setRaper(true);
        child.setRaper(true);
        assertFalse(FamilyActionLogic.isRapeTarget());
    }

    // ---- rideOnParent: null list, child isEating, child isShitting, parent has takeout ----

    @Test
    void testRideOnParent_NullList_ReturnsFalse() {
        assertFalse(FamilyActionLogic.rideOnParent(parent, null));
    }

    @Test
    void testRideOnParent_ChildEating_SkipReturnsFalse() {
        setReady(parent);
        child.setHungry(0); // hungry
        child.setEating(true);
        java.util.List<Body> list = new java.util.ArrayList<>();
        list.add(child);
        // child.isBaby() && !child.isEating() -> false -> block skipped -> return false
        assertFalse(FamilyActionLogic.rideOnParent(parent, list));
        child.setEating(false);
    }

    @Test
    void testRideOnParent_ChildShitting_SkipReturnsFalse() {
        setReady(parent);
        child.setHungry(0); // hungry
        child.setShitting(true);
        java.util.List<Body> list = new java.util.ArrayList<>();
        list.add(child);
        // child.isBaby() && !child.isShitting() -> false -> block skipped -> return false
        assertFalse(FamilyActionLogic.rideOnParent(parent, list));
        child.setShitting(false);
    }

    @Test
    void testRideOnParent_ParentHasTakeoutFood_NoFoodSearch_ReturnsFalse() {
        setReady(parent);
        child.setHungry(0); // isHungry=true
        child.setShit(0);
        // Give parent a food takeout item
        Food takenFood = new Food(50, 50, Food.FoodType.SWEETS1.ordinal());
        SimYukkuri.world.getCurrentMap().getTakenOutFood().put(takenFood.getObjId(), takenFood);
        parent.setCarryItem(src.enums.TakeoutItemType.FOOD, takenFood);
        java.util.List<Body> list = new java.util.ArrayList<>();
        list.add(child);
        // child.isHungry=true but parent has takeout food -> no food search -> target=null
        // child.wantToShit=false, child.isSleepy=false -> no target -> return false
        assertFalse(FamilyActionLogic.rideOnParent(parent, list));
    }

    // ---- Middle-age child: covers L167 false branch (isBaby=false in child loop)
    //      and L231-232 (!isBaby in ride loop) ----

    @Test
    void testCheckFamilyAction_MiddleAgeChild_NoBabyLoop() {
        setReady(parent);
        // Set child to middle age: not baby, not adult
        int midAge = child.getBabyLimitBase() + 1; // just past baby threshold
        child.setAge(midAge);
        child.setHungry(child.getHungryLimit()); // full -> dHungryPer>=80 -> bWantToEat=false
        child.setShit(0);
        // After child loop: bIsBaby=false -> bWantToShit=false. bWantToEat=false.
        // Ride loop: child.isBaby()=false -> L231 true -> L232 continue -> empty ride list
        SimYukkuri.RND = new ConstState(0) {
            @Override public boolean nextBoolean() { return false; }
        };
        assertFalse(FamilyActionLogic.checkFamilyAction(parent));
    }

    // ---- Sleeping child in ride loop: covers L210-211 (canAction=false) ----

    @Test
    void testCheckFamilyAction_RideLoop_SleepingChild_CanActionFalse() {
        setReady(parent);
        child.setHungry(child.getHungryLimit()); // full -> bWantToEat=false
        child.setShit(0); // 0% -> bWantToShit=false (baby, <=25%)
        child.setSleeping(true); // isSleeping=true -> canAction()=false
        SimYukkuri.RND = new ConstState(0) {
            @Override public boolean nextBoolean() { return false; }
        };
        // ride loop: canAction()=false -> L210 true -> L211 continue -> empty ride list -> false
        assertFalse(FamilyActionLogic.checkFamilyAction(parent));
        child.setSleeping(false);
    }

    // ---- searchFood: WASTE with POOR tang (covers L404-405) ----

    @Test
    void testSearchFood_WasteWithPoorTang_Found() {
        Food waste = new Food(80, 80, Food.FoodType.WASTE.ordinal());
        SimYukkuri.world.getCurrentMap().getFood().put(waste.getObjId(), waste);
        parent.setTang(100); // tang < 300 -> TangType.POOR -> WASTE accepted
        Obj found = FamilyActionLogic.searchFood(parent);
        assertNotNull(found);
    }

    // ---- searchFood: multiple foods, looks comparison (covers L411 false branch) ----

    @Test
    void testSearchFood_MultipleFood_HigherLooksWins() {
        // Two foods at different distances; looks comparison needed
        Food food1 = new Food(70, 70, Food.FoodType.SWEETS1.ordinal()); // closer
        Food food2 = new Food(100, 100, Food.FoodType.STALK.ordinal()); // farther but different looks
        SimYukkuri.world.getCurrentMap().getFood().put(food1.getObjId(), food1);
        SimYukkuri.world.getCurrentMap().getFood().put(food2.getObjId(), food2);
        Obj found = FamilyActionLogic.searchFood(parent);
        assertNotNull(found); // some food found; looks comparison was triggered
    }

    // ---- isRapeTarget: removed body ----

    @Test
    void testIsRapeTarget_RemovedBody() {
        parent.setRemoved(true);
        child.setRemoved(true);
        assertFalse(FamilyActionLogic.isRapeTarget());
    }

    // ---- L94: null childrenList (parent has no children) ----

    @Test
    void testCheckFamilyAction_NoChildren_ReturnsFalse() {
        // 子なし大人: createActiveChildList → null/空 → L94 → false
        Body loner = new src.yukkuri.Marisa() {
            @Override public int getCollisionX() { return 10; }
        };
        loner.setX(50); loner.setY(50);
        loner.setAge(loner.getChildLimitBase() + 1); // adult
        loner.giveOkazari(src.base.Okazari.OkazariType.DEFAULT);
        // addChild を呼ばない → 子リスト空
        SimYukkuri.world.getCurrentMap().getBody().put(loner.getUniqueID(), loner);
        assertFalse(FamilyActionLogic.checkFamilyAction(loner));
    }

    // ---- L404: isTooHungry=true → WASTE food accepted ----

    @Test
    void testSearchFood_WasteWithIsTooHungry_Found() {
        Food waste = new Food(80, 80, Food.FoodType.WASTE.ordinal());
        SimYukkuri.world.getCurrentMap().getFood().put(waste.getObjId(), waste);
        parent.setHungry(0);   // hungry <= 0
        // getDamageState() != NONE: damage >= DAMAGELIMIT/2 で VERY になる
        WorldTestHelper.setDamage(parent,
            parent.getDamageLimitBase()[src.enums.AgeState.ADULT.ordinal()] / 2 + 1);
        Obj found = FamilyActionLogic.searchFood(parent);
        assertNotNull(found);
    }

    @Nested
    class RegressionScenarios {

        @Test
        void testScenario_RideOnParentPrefersFoodTargetOverToilet() {
            setReady(parent);

            child.setHungry(0);
            child.setShit((int) (child.getShitLimit() * 0.8));
            child.setSleeping(false);

            Food food = new Food(120, 120, Food.FoodType.SWEETS1.ordinal());
            SimYukkuri.world.getCurrentMap().getFood().put(food.getObjId(), food);

            Toilet toilet = new Toilet();
            toilet.setX(130);
            toilet.setY(130);
            SimYukkuri.world.getCurrentMap().getToilet().put(toilet.getObjId(), toilet);

            java.util.List<Body> list = new java.util.ArrayList<>();
            list.add(child);

            assertTrue(FamilyActionLogic.rideOnParent(parent, list));
            assertTrue(parent.getCurrentEvent() instanceof YukkuriRideEvent);
            assertEquals(food.getObjId(), parent.getCurrentEvent().getTarget());
        }

        @Test
        void testScenario_CheckFamilyActionStartsRideEventForSleepyBaby() {
            setReady(parent);

            child.setHungry((int) (child.getHungryLimit() * 0.9));
            child.setShit(0);
            child.setWakeUpTime(Long.MIN_VALUE / 2);

            src.item.Bed bed = new src.item.Bed();
            bed.setX(300);
            bed.setY(300);
            SimYukkuri.world.getCurrentMap().getBed().put(bed.getObjId(), bed);

            SimYukkuri.RND = new ConstState(0) {
                @Override
                public boolean nextBoolean() {
                    return false;
                }
            };

            assertTrue(FamilyActionLogic.checkFamilyAction(parent));
            assertTrue(parent.getCurrentEvent() instanceof YukkuriRideEvent);
            assertEquals(bed.getObjId(), parent.getCurrentEvent().getTarget());
            assertEquals(1, SimYukkuri.world.getCurrentMap().getEvent().size());
            assertTrue(SimYukkuri.world.getCurrentMap().getEvent().get(0) instanceof YukkuriRideEvent);
        }

        @Test
        void testScenario_GoToEatDropsTakenOutFoodAndCanImmediatelyRetargetDroppedFood() {
            setReady(parent);

            Food takenFood = new Food(50, 50, Food.FoodType.SWEETS1.ordinal());
            SimYukkuri.world.getCurrentMap().getTakenOutFood().put(takenFood.getObjId(), takenFood);
            parent.setCarryItem(src.enums.TakeoutItemType.FOOD, takenFood);

            Food fieldFood = new Food(140, 140, Food.FoodType.SWEETS2.ordinal());
            SimYukkuri.world.getCurrentMap().getFood().put(fieldFood.getObjId(), fieldFood);

            java.util.List<Body> list = new java.util.ArrayList<>();
            list.add(child);

            assertTrue(FamilyActionLogic.goToEat(parent, list));
            assertTrue(parent.getCurrentEvent() instanceof SuperEatingTimeEvent);
            assertEquals(takenFood.getObjId(), parent.getCurrentEvent().getTarget());
            assertEquals(1, SimYukkuri.world.getCurrentMap().getEvent().size());
            assertTrue(SimYukkuri.world.getCurrentMap().getEvent().get(0) instanceof SuperEatingTimeEvent);
            assertTrue(parent.getCarryItem(src.enums.TakeoutItemType.FOOD) == null);
        }

        @Test
        void testScenario_ProudChildDirectlyStartsEventAndQueuesWorldEvent() {
            setReady(parent);

            java.util.List<Body> list = new java.util.ArrayList<>();
            list.add(child);

            assertTrue(FamilyActionLogic.proudChild(parent, list));
            assertTrue(parent.getCurrentEvent() instanceof ProudChildEvent);
            assertEquals(1, SimYukkuri.world.getCurrentMap().getEvent().size());
            assertTrue(SimYukkuri.world.getCurrentMap().getEvent().get(0) instanceof ProudChildEvent);
        }

        @Test
        void testScenario_CheckRaperFamilyClearsExcitingOnExistingRapersWhenNoTargetsRemain() {
            Body raper = WorldTestHelper.createBody();
            raper.setRaper(true);
            raper.setExciting(true);
            SimYukkuri.world.getCurrentMap().getBody().put(raper.getUniqueID(), raper);

            parent.setUnBirth(true);
            child.setUnBirth(true);

            assertTrue(FamilyActionLogic.checkRaperFamily());
            assertFalse(raper.isExciting());
        }
    }

}
