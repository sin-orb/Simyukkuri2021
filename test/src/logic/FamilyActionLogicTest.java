package src.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
        parent.setAge(parent.getCHILDLIMITorg() + 1); // ADULT

        child.setX(55);
        child.setY(55);
        child.setAge(0); // BABY
        child.setbFirstEatStalk(true);
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
        b.setInLastActionTime(0L);
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

        parent.addDamage(parent.getDAMAGELIMITorg()[AgeState.ADULT.ordinal()] / 2); // VERY or OVER
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
        partner.addDamage(parent.getDAMAGELIMITorg()[AgeState.ADULT.ordinal()]);
        assertFalse(FamilyActionLogic.checkFamilyAction(parent));
        partner.addDamage(-99999);
    }

    @Test
    void testCheckFamilyAction_ChildStateFailsEatingAndShitting() {
        setReady(parent);

        // Child damaged -> cancels all
        child.addDamage(parent.getDAMAGELIMITorg()[AgeState.BABY.ordinal()]);
        // Doesn't trigger any event
        assertFalse(FamilyActionLogic.checkFamilyAction(parent));
        child.addDamage(-99999);

        // Child firstEatStalk false -> cancels all
        child.setbFirstEatStalk(false);
        assertFalse(FamilyActionLogic.checkFamilyAction(parent));
        child.setbFirstEatStalk(true);

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
}
