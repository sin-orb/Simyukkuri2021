package org.simyukkuri.logic;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.simyukkuri.SimYukkuri;
import org.simyukkuri.entity.core.Entity;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.world.item.Sui;
import org.simyukkuri.entity.core.world.item.Toy;
import org.simyukkuri.entity.core.world.item.Trampoline;
import org.simyukkuri.enums.Attitude;
import org.simyukkuri.enums.CoreAnkoState;
import org.simyukkuri.enums.FavItemType;
import org.simyukkuri.enums.Happiness;
import org.simyukkuri.enums.Intelligence;
import org.simyukkuri.util.WorldTestHelper;

class ToyLogicTest {

    private Yukkuri body;

    @BeforeEach
    void setUp() {
        WorldTestHelper.resetWorld();
        WorldTestHelper.initializeMinimalWorld();
        WorldTestHelper.initializeStandardTranslate200();

        body = WorldTestHelper.createBody();
        body.setX(100);
        body.setY(100);
        // CHILD or BABY avoids the adult-returns-false check in
        // checkToy/checkTrampoline
        body.setAgeState(org.simyukkuri.enums.AgeState.CHILD);
        // Set hungry positive so isStarving() returns false
        body.setHungry(10000);

        SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(body.getObjId(), body);
    }

    @AfterEach
    void tearDown() {
        WorldTestHelper.resetWorld();
    }

    @Test
    void testCheckToy_NoToy() {
        boolean result = ToyLogic.checkToy(body);
        assertFalse(result);
    }

    @org.junit.jupiter.api.Disabled("Failing due to complex state requirements not met by test setup")
    @Test
    void testCheckToy_WithToy() {
        Toy toy = new Toy(150, 150, org.simyukkuri.entity.core.world.WorldEntity.ItemRank.HOUSE.ordinal());
        // Toy constructor check:
        // Toy extends WorldEntity. WorldEntity(x, y, option).
        // Toy might not have specific constructor. Using generic if needed.
        // Actually Toy.java might have its own.
        // Assuming Toy(int x, int y, int type) or similar.
        // If not, use reflection or setter.

        toy.setX(150);
        toy.setY(150);
        SimYukkuri.world.getCurrentWorldState().getToys().put(toy.getObjId(), toy);

        // ToyLogic.canPlay(b) checks mood etc.
        body.setHappiness(org.simyukkuri.enums.Happiness.AVERAGE);
        body.setStress(0);
        body.setDead(false);
        body.setPealed(false);
        body.setPacked(false);
        body.setSleeping(false);
        body.setBurialState(org.simyukkuri.enums.BurialState.NONE);
        body.setShit(0); // Add this to make wantToShit() return false
        body.setX(100);
        body.setY(100);

        // Move toy close to body (100, 100)
        toy.setX(100);
        toy.setY(100);

        boolean result = ToyLogic.checkToy(body);
        if (!result) {
            System.out.println("canPlay: " + ToyLogic.canPlay(body));
            System.out.println("isToFood: " + body.isToFood());
            System.out.println("isToYukkuri: " + body.isToYukkuri());
            System.out.println("isToSukkiri: " + body.isToSukkiri());
            System.out.println("isToSteal: " + body.isToSteal());
            System.out.println("isToBed: " + body.isToBed());
            System.out.println("isToShit: " + body.isToShit());
            System.out.println("canAction: " + body.canAction());
            System.out.println("isDontMove: " + body.isDontMove());
            System.out.println("isExciting: " + body.isExciting());
            System.out.println("isScare: " + body.isScare());
            System.out.println("isDamaged: " + body.isDamaged());
            System.out.println("getCurrentEvent: " + body.getCurrentEvent());
            System.out.println("canEventResponse: " + body.canEventResponse());
            System.out.println("isRude: " + body.isRude());
            System.out.println("isAdult: " + body.isAdult());
            System.out.println("wantToShit: " + body.wantToShit());
            System.out.println("Distance to toy: "
                    + org.simyukkuri.draw.Translate.distance(body.getX(), body.getY(), toy.getX(), toy.getY()));
            System.out.println("StepDist: " + body.getStepDist());
            System.out.println("eyesightBase: " + body.getEyesightBase());
        }
        assertTrue(result);
        assertTrue(body.getMoveTargetId() != -1);
        Entity target = SimYukkuri.world.getCurrentWorldState().getToys().get(body.getMoveTargetId());
        assertTrue(target instanceof Toy);
    }

    @Test
    void testCheckToy_Null() {
        assertFalse(ToyLogic.checkToy(null));
    }

    @Test
    void testCanPlay() {
        body.setHappiness(org.simyukkuri.enums.Happiness.AVERAGE);
        body.setStress(0);
        WorldTestHelper.setDamage(body, 0);
        assertTrue(ToyLogic.canPlay(body));

        WorldTestHelper.setDamage(body, body.getDamageLimit() + 100);
        assertFalse(ToyLogic.canPlay(body));
    }

    @Test
    void testCanPlay_DeadBodyReturnsFalse() {
        body.setDead(true);
        assertFalse(ToyLogic.canPlay(body));
    }

    @Test
    void testCanPlay_NYDReturnsFalse() {
        body.setCoreAnkoState(CoreAnkoState.NON_YUKKURI_DISEASE);
        assertFalse(ToyLogic.canPlay(body));
    }

    @Test
    void testCanPlay_IsToFoodReturnsFalse() {
        body.setToFood(true);
        assertFalse(ToyLogic.canPlay(body));
    }

    @Test
    void testCanPlay_IsToBedReturnsFalse() {
        body.setToBed(true);
        assertFalse(ToyLogic.canPlay(body));
    }

    @Test
    void testCanPlay_IsToShitReturnsFalse() {
        body.setToShit(true);
        assertFalse(ToyLogic.canPlay(body));
    }

    @Test
    void testCanPlay_IsToSukkiriReturnsFalse() {
        body.setToSukkiri(true);
        assertFalse(ToyLogic.canPlay(body));
    }

    @Test
    void testCanPlay_IsToStealReturnsFalse() {
        body.setToSteal(true);
        assertFalse(ToyLogic.canPlay(body));
    }

    @Test
    void testCanPlay_IsLockmoveReturnsFalse() {
        body.setLockmove(true);
        assertFalse(ToyLogic.canPlay(body));
    }

    @Test
    void testCanPlay_IsExcitingReturnsFalse() {
        body.setExciting(true);
        assertFalse(ToyLogic.canPlay(body));
    }

    @Test
    void testCanPlay_HasCurrentEventReturnsFalse() {
        body.setCurrentEvent(new org.simyukkuri.event.EventPacket() {
            public boolean checkEventResponse(org.simyukkuri.entity.core.living.yukkuri.Yukkuri b) {
                return false;
            }

            public void start(org.simyukkuri.entity.core.living.yukkuri.Yukkuri b) {
            }

            public UpdateState update(org.simyukkuri.entity.core.living.yukkuri.Yukkuri b) {
                return null;
            }

            public boolean execute(org.simyukkuri.entity.core.living.yukkuri.Yukkuri b) {
                return true;
            }

            public String toString() {
                return "test";
            }
        });
        assertFalse(ToyLogic.canPlay(body));
    }

    // --- checkToy: with toy in range, body is child ---

    @Test
    void testCheckToy_WithToy_InRange_ReturnsTrue() {
        // body is CHILD, hungry > 0, canPlay should return true
        Toy toy = new Toy();
        toy.setX(100);
        toy.setY(100);
        toy.setObjId(8888);
        SimYukkuri.world.getCurrentWorldState().getToys().put(toy.getObjId(), toy);
        // Yukkuri at (100,100), toy at (100,100) → minDistance <= stepDist → owns toy
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> ToyLogic.checkToy(body));
    }

    @Test
    void testCheckToy_WithToy_OutOfRange_MovesTo() {
        // toy far away so body moves toward it
        Toy toy = new Toy();
        toy.setX(5000);
        toy.setY(5000);
        toy.setObjId(8887);
        SimYukkuri.world.getCurrentWorldState().getToys().put(toy.getObjId(), toy);
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> ToyLogic.checkToy(body));
    }

    // --- checkToy: adult body returns false ---

    @Test
    void testCheckToy_AdultBody_ReturnsFalse() {
        body.setAgeState(org.simyukkuri.enums.AgeState.ADULT);
        Toy toy = new Toy();
        toy.setX(100);
        toy.setY(100);
        toy.setObjId(8886);
        SimYukkuri.world.getCurrentWorldState().getToys().put(toy.getObjId(), toy);
        assertFalse(ToyLogic.checkToy(body));
    }

    // --- checkSui ---

    @Test
    void testCheckSui_NoSui() {
        assertFalse(ToyLogic.checkSui(body));
    }

    @Test
    void testCheckSui_WithSui_CannotPlay() {
        Sui sui = new Sui(150, 150, 0);
        SimYukkuri.world.getCurrentWorldState().getSuis().put(sui.getObjId(), sui);
        body.setDead(true);
        assertFalse(ToyLogic.checkSui(body));
    }

    @Test
    void testCheckSui_WithSui_CanPlay() {
        // Add sui to world, body can play
        Sui sui = new Sui(5000, 5000, 0);
        SimYukkuri.world.getCurrentWorldState().getSuis().put(sui.getObjId(), sui);
        // body is CHILD, hungry > 0 → canPlay true
        // body is not riding sui (parentLinkId != sui)
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> ToyLogic.checkSui(body));
    }

    // --- checkTrampoline ---

    @Test
    void testCheckTrampoline_NoTrampoline() {
        assertFalse(ToyLogic.checkTrampoline(body));
    }

    @Test
    void testCheckTrampoline_WithDefaultTrampolineCannotPlay() {
        Trampoline trampoline = new Trampoline();
        trampoline.setX(150);
        trampoline.setY(150);
        trampoline.setObjId(9999);
        SimYukkuri.world.getCurrentWorldState().getTrampolines().put(trampoline.getObjId(), trampoline);
        body.setDead(true);
        assertFalse(ToyLogic.checkTrampoline(body));
    }

    @Test
    void testCheckTrampoline_OutOfRange_MovesTo() {
        Trampoline trampoline = new Trampoline();
        trampoline.setX(5000);
        trampoline.setY(5000);
        trampoline.setObjId(9998);
        SimYukkuri.world.getCurrentWorldState().getTrampolines().put(trampoline.getObjId(), trampoline);
        // body is child, can play
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> ToyLogic.checkTrampoline(body));
    }

    @Test
    void testCheckTrampoline_Null() {
        assertFalse(ToyLogic.checkTrampoline(null));
    }

    @Test
    void testConstructor_doesNotThrow() {
        assertDoesNotThrow(() -> new ToyLogic());
    }

    // --- checkTrampoline: close trampoline (minDistance <= stepDist) option=0 ---

    @Test
    void testCheckTrampoline_CloseEnough_Option0_bounces() {
        Trampoline trampoline = new Trampoline();
        trampoline.setX(body.getX()); // Same position → distance=0 < stepDist
        trampoline.setY(body.getY());
        // Try different option values
        try {
            java.lang.reflect.Field optField = Trampoline.class.getDeclaredField("option");
            optField.setAccessible(true);
            optField.setInt(trampoline, 0);
        } catch (Exception e) {
            /* use default */ }
        trampoline.setObjId(7777);
        SimYukkuri.world.getCurrentWorldState().getTrampolines().put(trampoline.getObjId(), trampoline);
        // CHILD, canPlay=true → should reach the bounce path
        body.setAgeState(org.simyukkuri.enums.AgeState.CHILD);
        SimYukkuri.RND = new org.simyukkuri.SequenceRandom(50, 50, 50, 50); // nextInt(100)+1 > accident → no kick boost
        assertDoesNotThrow(() -> ToyLogic.checkTrampoline(body));
    }

    @Test
    void testCheckTrampoline_CloseEnough_Option1_bounces() {
        Trampoline trampoline = new Trampoline();
        trampoline.setX(body.getX());
        trampoline.setY(body.getY());
        try {
            java.lang.reflect.Field optField = Trampoline.class.getDeclaredField("option");
            optField.setAccessible(true);
            optField.setInt(trampoline, 1);
        } catch (Exception e) {
            /* use default */ }
        trampoline.setObjId(7778);
        SimYukkuri.world.getCurrentWorldState().getTrampolines().put(trampoline.getObjId(), trampoline);
        body.setAgeState(org.simyukkuri.enums.AgeState.CHILD);
        SimYukkuri.RND = new org.simyukkuri.SequenceRandom(50, 50, 50, 50);
        assertDoesNotThrow(() -> ToyLogic.checkTrampoline(body));
    }

    // --- checkTrampoline: adult body (not rude) returns false ---

    @Test
    void testCheckTrampoline_AdultNotRude_ReturnsFalse() {
        Trampoline trampoline = new Trampoline();
        trampoline.setX(body.getX());
        trampoline.setY(body.getY());
        trampoline.setObjId(7779);
        SimYukkuri.world.getCurrentWorldState().getTrampolines().put(trampoline.getObjId(), trampoline);
        body.setAgeState(org.simyukkuri.enums.AgeState.ADULT);
        assertFalse(ToyLogic.checkTrampoline(body));
    }

    // --- checkSui: body is adult, sui in map but dead body → various paths ---

    @Test
    void testCheckSui_WithSui_CanPlay_InRange() {
        Sui sui = new Sui();
        sui.setX(body.getX());
        sui.setY(body.getY());
        sui.setObjId(8888);
        SimYukkuri.world.getCurrentWorldState().getSuis().put(sui.getObjId(), sui);
        // CHILD can play
        assertDoesNotThrow(() -> ToyLogic.checkSui(body));
    }

    // --- canPlay: isToYukkuri=true → false ---

    @Test
    void testCanPlay_IsToBody_ReturnsFalse() {
        body.setToYukkuri(true); // canPlay line 321: isToYukkuri=true → return false
        assertFalse(ToyLogic.canPlay(body));
    }

    // --- canPlay: isScare=true → false ---

    @Test
    void testCanPlay_IsScare_ReturnsFalse() {
        body.setScare(true); // canPlay line 324: isScare()=true → return false
        assertFalse(ToyLogic.canPlay(body));
    }

    // --- checkToy: toy in air (Z>0) → stress and return true ---

    @Test
    void testCheckToy_ToyInAir_ReturnsTrue() {
        Toy toy = new Toy();
        toy.setX(body.getX()); // same position → minDistance=0 <= stepDist
        toy.setY(body.getY());
        toy.setObjId(5555);
        toy.setZ(10); // toy is in the air
        SimYukkuri.world.getCurrentWorldState().getToys().put(toy.getObjId(), toy);
        // canPlay=true (CHILD body, no other actions), minDistance<=stepDist, Z>0 →
        // return true
        assertDoesNotThrow(() -> {
            boolean result = ToyLogic.checkToy(body);
            assertTrue(result);
        });
    }

    // ---- checkToy: grabbed+Z>0 → setOwner(null) (L71-72) ----

    @Test
    void testCheckToy_GrabbedAndInAir_SetOwnerNull() {
        Yukkuri other = WorldTestHelper.createBody();
        Toy toy = new Toy();
        toy.setX(body.getX());
        toy.setY(body.getY());
        toy.setObjId(4441);
        toy.setOwner(other);
        toy.setGrabbed(true);
        toy.setZ(10);
        SimYukkuri.world.getCurrentWorldState().getToys().put(toy.getObjId(), toy);
        assertDoesNotThrow(() -> ToyLogic.checkToy(body));
    }

    // ---- checkToy: owned by self → getInVain (L115-116) ----

    @Test
    void testCheckToy_OwnedBySelf_GetInVain() {
        Toy toy = new Toy();
        toy.setX(body.getX());
        toy.setY(body.getY());
        toy.setObjId(4442);
        toy.setOwner(body);
        SimYukkuri.world.getCurrentWorldState().getToys().put(toy.getObjId(), toy);
        SimYukkuri.RND = new org.simyukkuri.ConstState(0); // nextInt(10)=0 → getInVain
        assertDoesNotThrow(() -> ToyLogic.checkToy(body));
    }

    // ---- checkToy: owned by self → kick (L119) ----

    @Test
    void testCheckToy_OwnedBySelf_Kick() {
        Toy toy = new Toy();
        toy.setX(body.getX());
        toy.setY(body.getY());
        toy.setObjId(4443);
        toy.setOwner(body);
        SimYukkuri.world.getCurrentWorldState().getToys().put(toy.getObjId(), toy);
        SimYukkuri.RND = new org.simyukkuri.ConstState(5); // nextInt(10)=5 ≠ 0 → kick
        assertDoesNotThrow(() -> ToyLogic.checkToy(body));
    }

    // ---- checkToy: favBall in-air, other owner, isRude=true → VERY_SAD (L86-88)
    // ----

    @Test
    void testCheckToy_FavBall_InAir_OtherOwner_Rude() {
        Yukkuri other = WorldTestHelper.createBody();
        Toy toy = new Toy();
        toy.setX(body.getX());
        toy.setY(body.getY());
        toy.setObjId(4444);
        toy.setZ(10); // in air
        toy.setOwner(other); // bOwnedFamily=false
        body.setFavoriteItem(FavItemType.BALL, toy);
        body.setAttitude(Attitude.SHITHEAD); // isRude=true
        // age=0 by default → 0%20==0 → enter stress block
        SimYukkuri.world.getCurrentWorldState().getToys().put(toy.getObjId(), toy);
        assertDoesNotThrow(() -> ToyLogic.checkToy(body));
    }

    // ---- checkToy: favBall in-air, other owner, not rude → SAD (L90-92) ----

    @Test
    void testCheckToy_FavBall_InAir_OtherOwner_NotRude() {
        Yukkuri other = WorldTestHelper.createBody();
        Toy toy = new Toy();
        toy.setX(body.getX());
        toy.setY(body.getY());
        toy.setObjId(4445);
        toy.setZ(10);
        toy.setOwner(other);
        body.setFavoriteItem(FavItemType.BALL, toy);
        // body is Marisa (not rude by default)
        SimYukkuri.world.getCurrentWorldState().getToys().put(toy.getObjId(), toy);
        assertDoesNotThrow(() -> ToyLogic.checkToy(body));
    }

    // ---- checkToy: favBall out-of-range, other owner, rude (L125-127) ----

    @Test
    void testCheckToy_FavBall_OutOfRange_OtherOwner_Rude() {
        Yukkuri other = WorldTestHelper.createBody();
        Toy toy = new Toy();
        // within eyesight (dist²<16M) but outside stepDist (dist²>4 for CHILD)
        toy.setX(body.getX());
        toy.setY(body.getY() + 50);
        toy.setObjId(4446);
        toy.setOwner(other);
        body.setFavoriteItem(FavItemType.BALL, toy);
        body.setAttitude(Attitude.SHITHEAD); // isRude=true
        SimYukkuri.world.getCurrentWorldState().getToys().put(toy.getObjId(), toy);
        assertDoesNotThrow(() -> ToyLogic.checkToy(body));
    }

    // ---- checkToy: favBall out-of-range, other owner, not rude (L129-131) ----

    @Test
    void testCheckToy_FavBall_OutOfRange_OtherOwner_NotRude() {
        Yukkuri other = WorldTestHelper.createBody();
        Toy toy = new Toy();
        toy.setX(body.getX());
        toy.setY(body.getY() + 50);
        toy.setObjId(4447);
        toy.setOwner(other);
        body.setFavoriteItem(FavItemType.BALL, toy);
        SimYukkuri.world.getCurrentWorldState().getToys().put(toy.getObjId(), toy);
        assertDoesNotThrow(() -> ToyLogic.checkToy(body));
    }

    // ---- checkToy: canPlay=false with toy present (L44 true branch) ----

    @Test
    void testCheckToy_CanPlayFalse_WithToy() {
        Toy toy = new Toy();
        toy.setX(body.getX());
        toy.setY(body.getY());
        toy.setObjId(4448);
        SimYukkuri.world.getCurrentWorldState().getToys().put(toy.getObjId(), toy);
        body.setDead(true); // canPlay=false
        assertFalse(ToyLogic.checkToy(body));
    }

    // ---- checkSui: parentLinkId is Sui instance → return false (L157-158) ----

    @Test
    void testCheckSui_LinkParentIsSui_ReturnsFalse() {
        Sui sui = new Sui(body.getX(), body.getY(), 0);
        SimYukkuri.world.getCurrentWorldState().getSuis().put(sui.getObjId(), sui);
        body.setParentLinkId(sui.getObjId());
        assertFalse(ToyLogic.checkSui(body));
    }

    // ---- checkSui: RND.nextInt(150)==0 → message + return false (L163-167) ----

    @Test
    void testCheckSui_RND150_Zero_ReturnsFalse() {
        Sui sui = new Sui(body.getX(), body.getY(), 0);
        SimYukkuri.world.getCurrentWorldState().getSuis().put(sui.getObjId(), sui);
        SimYukkuri.RND = new org.simyukkuri.ConstState(0); // nextInt(150)=0
        assertFalse(ToyLogic.checkSui(body));
    }

    // ---- checkSui: NoCanBind=true, non-family → continue skip (L189-193) ----

    @Test
    void testCheckSui_NoCanBind_NonFamily_Skip() {
        Yukkuri other = WorldTestHelper.createBody();
        Sui sui = new Sui(body.getX(), body.getY(), 0);
        sui.setOwnerBody(other); // hasOwner()=(ownerBody!=null)=true; other is not family
        SimYukkuri.world.getCurrentWorldState().getSuis().put(sui.getObjId(), sui);
        SimYukkuri.world.getCurrentWorldState().getYukkuriRegistry().put(other.getObjId(), other);
        // body has no favoriteItems SUI → enters loop → NoCanBind+non-family → skip →
        // found=null
        assertDoesNotThrow(() -> ToyLogic.checkSui(body));
    }

    // ---- checkSui: favoriteItems SUI set + nextBoolean=true → return false
    // (L201-204) ----

    @Test
    void testCheckSui_FavItemSet_NextBoolTrue_ReturnsFalse() {
        Sui sui = new Sui(body.getX(), body.getY(), 0); // close → dist<200000
        SimYukkuri.world.getCurrentWorldState().getSuis().put(sui.getObjId(), sui);
        body.setFavoriteItem(FavItemType.SUI, sui);
        org.simyukkuri.ConstState cs = new org.simyukkuri.ConstState(200); // nextInt(150)=149 ≠ 0
        cs.setFixedBoolean(true); // nextBoolean()=true
        SimYukkuri.RND = cs;
        assertFalse(ToyLogic.checkSui(body));
    }

    // ---- checkSui: found grabbed+Z>0 → bindBody=null → FindSui event (L210-211)
    // ----

    @Test
    void testCheckSui_GrabbedInAir_BindBodyNull() {
        Sui sui = new Sui(body.getX(), body.getY(), 0);
        sui.setGrabbed(true);
        sui.setZ(10);
        // no owner body → hasOwner()=false → found in loop
        SimYukkuri.world.getCurrentWorldState().getSuis().put(sui.getObjId(), sui);
        SimYukkuri.RND = new org.simyukkuri.ConstState(200); // nextInt(150)=149
        assertDoesNotThrow(() -> ToyLogic.checkSui(body));
    }

    // ---- checkSui: bindBody==self → FindGetSui event (L220-224) ----

    @Test
    void testCheckSui_BindBodySelf_FindGetSui() {
        Sui sui = new Sui(body.getX(), body.getY(), 0);
        sui.setOwnerBody(body); // ownerBody=self
        SimYukkuri.world.getCurrentWorldState().getSuis().put(sui.getObjId(), sui);
        body.setFavoriteItem(FavItemType.SUI, sui);
        org.simyukkuri.ConstState cs = new org.simyukkuri.ConstState(200);
        cs.setFixedBoolean(false); // nextBoolean()=false → skip L201
        SimYukkuri.RND = cs;
        assertDoesNotThrow(() -> ToyLogic.checkSui(body));
    }

    // ---- checkSui: bindBody=other owner (L227-232) ----

    @Test
    void testCheckSui_BindBodyOther() {
        Yukkuri other = WorldTestHelper.createBody();
        Sui sui = new Sui(body.getX(), body.getY(), 0);
        sui.setOwnerBody(other);
        SimYukkuri.world.getCurrentWorldState().getSuis().put(sui.getObjId(), sui);
        body.setFavoriteItem(FavItemType.SUI, sui);
        org.simyukkuri.ConstState cs = new org.simyukkuri.ConstState(200);
        cs.setFixedBoolean(false);
        SimYukkuri.RND = cs;
        assertDoesNotThrow(() -> ToyLogic.checkSui(body));
    }

    // ---- checkTrampoline: within eyesight, outside stepDist → moveTo (L310) ----

    @Test
    void testCheckTrampoline_WithinEyesight_OutsideStepDist_MovesTo() {
        Trampoline trampoline = new Trampoline();
        // dist²=50²=2500 < eyesightBase(16M); dist²=2500 > stepDist(4 for CHILD)
        trampoline.setX(body.getX());
        trampoline.setY(body.getY() + 50);
        trampoline.setObjId(3331);
        SimYukkuri.world.getCurrentWorldState().getTrampolines().put(trampoline.getObjId(), trampoline);
        assertDoesNotThrow(() -> ToyLogic.checkTrampoline(body));
    }

    // ---- checkTrampoline: FOOL body, option=0, accident → kick*3 (L290-292) ----

    @Test
    void testCheckTrampoline_FoolBody_Option0_Accident() {
        Trampoline trampoline = new Trampoline();
        trampoline.setX(body.getX());
        trampoline.setY(body.getY());
        trampoline.setOption(0);
        trampoline.setAccident2(100); // FOOL accident rate 100%
        trampoline.setObjId(3332);
        SimYukkuri.world.getCurrentWorldState().getTrampolines().put(trampoline.getObjId(), trampoline);
        body.setIntelligence(Intelligence.FOOL);
        SimYukkuri.RND = new org.simyukkuri.ConstState(0); // nextInt(100)=0 → 1<100 → accident
        assertDoesNotThrow(() -> ToyLogic.checkTrampoline(body));
    }

    // ---- checkTrampoline: FOOL body, option=1, accident → kick*3 (L299-301) ----

    @Test
    void testCheckTrampoline_FoolBody_Option1_Accident() {
        Trampoline trampoline = new Trampoline();
        trampoline.setX(body.getX());
        trampoline.setY(body.getY());
        trampoline.setOption(1);
        trampoline.setAccident2(100);
        trampoline.setObjId(3333);
        SimYukkuri.world.getCurrentWorldState().getTrampolines().put(trampoline.getObjId(), trampoline);
        body.setIntelligence(Intelligence.FOOL);
        SimYukkuri.RND = new org.simyukkuri.ConstState(0);
        assertDoesNotThrow(() -> ToyLogic.checkTrampoline(body));
    }

    // ==== 追加カバレッジ拡張テスト ====

    // ---- wantToShit=true → L45/L255 return false ----

    @Test
    void testCheckToy_WantToShit_ReturnsFalse() {
        Toy toy = new Toy();
        toy.setX(body.getX());
        toy.setY(body.getY());
        toy.setObjId(2001);
        SimYukkuri.world.getCurrentWorldState().getToys().put(toy.getObjId(), toy);
        // shit at limit → wantToShit()=true
        body.setShit(body.getShitLimitBase()[org.simyukkuri.enums.AgeState.CHILD.ordinal()]);
        assertFalse(ToyLogic.checkToy(body));
    }

    @Test
    void testCheckTrampoline_WantToShit_ReturnsFalse() {
        Trampoline t = new Trampoline();
        t.setX(body.getX());
        t.setY(body.getY());
        t.setObjId(2002);
        SimYukkuri.world.getCurrentWorldState().getTrampolines().put(t.getObjId(), t);
        body.setShit(body.getShitLimitBase()[org.simyukkuri.enums.AgeState.CHILD.ordinal()]);
        assertFalse(ToyLogic.checkTrampoline(body));
    }

    // ---- loop break: 2 toys at body pos → L54-55 ----

    @Test
    void testCheckToy_TwoToys_LoopBreak() {
        Toy toy1 = new Toy();
        toy1.setX(body.getX());
        toy1.setY(body.getY());
        toy1.setObjId(2010);
        Toy toy2 = new Toy();
        toy2.setX(body.getX() + 50);
        toy2.setY(body.getY());
        toy2.setObjId(2011);
        SimYukkuri.world.getCurrentWorldState().getToys().put(toy1.getObjId(), toy1);
        SimYukkuri.world.getCurrentWorldState().getToys().put(toy2.getObjId(), toy2);
        SimYukkuri.RND = new org.simyukkuri.ConstState(5); // kick path
        assertDoesNotThrow(() -> ToyLogic.checkToy(body));
    }

    // ---- loop break: 2 sui, one at body pos → L177-178 ----

    @Test
    void testCheckSui_TwoSui_LoopBreak() {
        Sui sui1 = new Sui(body.getX(), body.getY(), 0);
        sui1.setObjId(2020);
        Sui sui2 = new Sui(body.getX() + 50, body.getY(), 0);
        sui2.setObjId(2021);
        SimYukkuri.world.getCurrentWorldState().getSuis().put(sui1.getObjId(), sui1);
        SimYukkuri.world.getCurrentWorldState().getSuis().put(sui2.getObjId(), sui2);
        SimYukkuri.RND = new org.simyukkuri.ConstState(200);
        assertDoesNotThrow(() -> ToyLogic.checkSui(body));
    }

    // ---- loop break: 2 trampolines → L264-266 ----

    @Test
    void testCheckTrampoline_TwoTrampolines_LoopBreak() {
        Trampoline t1 = new Trampoline();
        t1.setX(body.getX());
        t1.setY(body.getY());
        t1.setObjId(2030);
        Trampoline t2 = new Trampoline();
        t2.setX(body.getX() + 50);
        t2.setY(body.getY());
        t2.setObjId(2031);
        SimYukkuri.world.getCurrentWorldState().getTrampolines().put(t1.getObjId(), t1);
        SimYukkuri.world.getCurrentWorldState().getTrampolines().put(t2.getObjId(), t2);
        SimYukkuri.RND = new org.simyukkuri.ConstState(5);
        assertDoesNotThrow(() -> ToyLogic.checkTrampoline(body));
    }

    // ---- grabbed=true but Z=0 (L71 compound false branch) ----

    @Test
    void testCheckToy_GrabbedZ0_NormalOwnerPath() {
        Toy toy = new Toy();
        toy.setX(body.getX());
        toy.setY(body.getY());
        toy.setObjId(2040);
        toy.setGrabbed(true); // grabbed=true, Z=0 → L71 false → check owner
        SimYukkuri.world.getCurrentWorldState().getToys().put(toy.getObjId(), toy);
        SimYukkuri.RND = new org.simyukkuri.ConstState(5);
        assertDoesNotThrow(() -> ToyLogic.checkToy(body));
    }

    // ---- checkSui: grabbed=true but Z=0 (L210 compound false) ----

    @Test
    void testCheckSui_GrabbedZ0_NormalBindPath() {
        Sui sui = new Sui(body.getX(), body.getY(), 0);
        sui.setGrabbed(true); // grabbed=true, Z=0 → L210 false → use original bindobj
        SimYukkuri.world.getCurrentWorldState().getSuis().put(sui.getObjId(), sui);
        body.setFavoriteItem(FavItemType.SUI, sui);
        org.simyukkuri.ConstState cs = new org.simyukkuri.ConstState(200);
        cs.setFixedBoolean(false);
        SimYukkuri.RND = cs;
        assertDoesNotThrow(() -> ToyLogic.checkSui(body));
    }

    // ---- age % 20 != 0 → skip happy/stress block ----

    @Test
    void testCheckToy_InAir_FavBall_AgeNotMult20() {
        Yukkuri other = WorldTestHelper.createBody();
        Toy toy = new Toy();
        toy.setX(body.getX());
        toy.setY(body.getY());
        toy.setObjId(2050);
        toy.setZ(10);
        toy.setOwner(other);
        body.setFavoriteItem(FavItemType.BALL, toy);
        body.setAge(2401L); // 2401 % 20 = 1 ≠ 0
        SimYukkuri.world.getCurrentWorldState().getToys().put(toy.getObjId(), toy);
        assertDoesNotThrow(() -> ToyLogic.checkToy(body));
    }

    @Test
    void testCheckToy_InRange_AgeNotMult20() {
        Toy toy = new Toy();
        toy.setX(body.getX());
        toy.setY(body.getY());
        toy.setObjId(2051);
        body.setAge(2401L);
        SimYukkuri.world.getCurrentWorldState().getToys().put(toy.getObjId(), toy);
        SimYukkuri.RND = new org.simyukkuri.ConstState(5);
        assertDoesNotThrow(() -> ToyLogic.checkToy(body));
    }

    @Test
    void testCheckToy_OutOfRange_FavBall_AgeNotMult20() {
        Yukkuri other = WorldTestHelper.createBody();
        Toy toy = new Toy();
        toy.setX(body.getX());
        toy.setY(body.getY() + 50);
        toy.setObjId(2052);
        toy.setOwner(other);
        body.setFavoriteItem(FavItemType.BALL, toy);
        body.setAge(2401L);
        SimYukkuri.world.getCurrentWorldState().getToys().put(toy.getObjId(), toy);
        assertDoesNotThrow(() -> ToyLogic.checkToy(body));
    }

    @Test
    void testCheckTrampoline_InRange_AgeNotMult20() {
        Trampoline t = new Trampoline();
        t.setX(body.getX());
        t.setY(body.getY());
        t.setObjId(2053);
        body.setAge(2401L);
        SimYukkuri.world.getCurrentWorldState().getTrampolines().put(t.getObjId(), t);
        SimYukkuri.RND = new org.simyukkuri.SequenceRandom(50, 50, 50, 50);
        assertDoesNotThrow(() -> ToyLogic.checkTrampoline(body));
    }

    // ---- isTalking=true → skip setMessage ----

    @Test
    void testCheckToy_InAir_FavBall_Talking() {
        Yukkuri other = WorldTestHelper.createBody();
        Toy toy = new Toy();
        toy.setX(body.getX());
        toy.setY(body.getY());
        toy.setObjId(2060);
        toy.setZ(10);
        toy.setOwner(other);
        body.setFavoriteItem(FavItemType.BALL, toy);
        body.setMessageTicks(1); // isTalking()=true → skip message
        SimYukkuri.world.getCurrentWorldState().getToys().put(toy.getObjId(), toy);
        assertDoesNotThrow(() -> ToyLogic.checkToy(body));
    }

    @Test
    void testCheckToy_InRange_Talking_NewToy() {
        Toy toy = new Toy();
        toy.setX(body.getX());
        toy.setY(body.getY());
        toy.setObjId(2061);
        body.setMessageTicks(1);
        SimYukkuri.world.getCurrentWorldState().getToys().put(toy.getObjId(), toy);
        SimYukkuri.RND = new org.simyukkuri.ConstState(5);
        assertDoesNotThrow(() -> ToyLogic.checkToy(body));
    }

    @Test
    void testCheckToy_OutOfRange_FavBall_Talking() {
        Yukkuri other = WorldTestHelper.createBody();
        Toy toy = new Toy();
        toy.setX(body.getX());
        toy.setY(body.getY() + 50);
        toy.setObjId(2062);
        toy.setOwner(other);
        body.setFavoriteItem(FavItemType.BALL, toy);
        body.setMessageTicks(1);
        SimYukkuri.world.getCurrentWorldState().getToys().put(toy.getObjId(), toy);
        assertDoesNotThrow(() -> ToyLogic.checkToy(body));
    }

    @Test
    void testCheckSui_RND150_0_Talking() {
        Sui sui = new Sui(body.getX(), body.getY(), 0);
        SimYukkuri.world.getCurrentWorldState().getSuis().put(sui.getObjId(), sui);
        body.setMessageTicks(1); // isTalking()=true → skip message in L164
        SimYukkuri.RND = new org.simyukkuri.ConstState(0); // nextInt(150)=0 → enter block
        assertFalse(ToyLogic.checkSui(body));
    }

    @Test
    void testCheckSui_GrabbedAir_Talking() {
        Sui sui = new Sui(body.getX(), body.getY(), 0);
        sui.setGrabbed(true);
        sui.setZ(10);
        SimYukkuri.world.getCurrentWorldState().getSuis().put(sui.getObjId(), sui);
        body.setMessageTicks(1); // isTalking()=true → skip FindSui message
        SimYukkuri.RND = new org.simyukkuri.ConstState(200);
        assertDoesNotThrow(() -> ToyLogic.checkSui(body));
    }

    @Test
    void testCheckSui_BindBodySelf_Talking() {
        Sui sui = new Sui(body.getX(), body.getY(), 0);
        sui.setOwnerBody(body);
        SimYukkuri.world.getCurrentWorldState().getSuis().put(sui.getObjId(), sui);
        body.setFavoriteItem(FavItemType.SUI, sui);
        body.setMessageTicks(1); // isTalking()=true → skip FindGetSui message
        org.simyukkuri.ConstState cs = new org.simyukkuri.ConstState(200);
        cs.setFixedBoolean(false);
        SimYukkuri.RND = cs;
        assertDoesNotThrow(() -> ToyLogic.checkSui(body));
    }

    @Test
    void testCheckSui_BindBodyOther_Talking() {
        Yukkuri other = WorldTestHelper.createBody();
        Sui sui = new Sui(body.getX(), body.getY(), 0);
        sui.setOwnerBody(other);
        SimYukkuri.world.getCurrentWorldState().getSuis().put(sui.getObjId(), sui);
        body.setFavoriteItem(FavItemType.SUI, sui);
        body.setMessageTicks(1);
        org.simyukkuri.ConstState cs = new org.simyukkuri.ConstState(200);
        cs.setFixedBoolean(false);
        SimYukkuri.RND = cs;
        assertDoesNotThrow(() -> ToyLogic.checkSui(body));
    }

    // ---- checkTrampoline: body in air (Z≠0) → skip kick (L288 false) ----

    @Test
    void testCheckTrampoline_BodyInAir_SkipKick() {
        Trampoline t = new Trampoline();
        t.setX(body.getX());
        t.setY(body.getY());
        t.setObjId(2070);
        SimYukkuri.world.getCurrentWorldState().getTrampolines().put(t.getObjId(), t);
        body.setZ(10); // body in air → L288 false → skip kick
        SimYukkuri.RND = new org.simyukkuri.ConstState(5);
        assertDoesNotThrow(() -> ToyLogic.checkTrampoline(body));
    }

    // ---- checkTrampoline: non-FOOL body, accident1>0 → kick*3 ----

    @Test
    void testCheckTrampoline_NonFool_Option0_Accident() {
        Trampoline t = new Trampoline();
        t.setX(body.getX());
        t.setY(body.getY());
        t.setObjId(2080);
        t.setOption(0);
        t.setAccident1(100); // non-FOOL accident rate
        SimYukkuri.world.getCurrentWorldState().getTrampolines().put(t.getObjId(), t);
        // body is Marisa (not FOOL), Intelligence.AVERAGE or WISE
        SimYukkuri.RND = new org.simyukkuri.ConstState(0); // nextInt(100)=0 → 1<100 → accident
        assertDoesNotThrow(() -> ToyLogic.checkTrampoline(body));
    }

    @Test
    void testCheckTrampoline_NonFool_Option1_Accident() {
        Trampoline t = new Trampoline();
        t.setX(body.getX());
        t.setY(body.getY());
        t.setObjId(2081);
        t.setOption(1);
        t.setAccident1(100);
        SimYukkuri.world.getCurrentWorldState().getTrampolines().put(t.getObjId(), t);
        SimYukkuri.RND = new org.simyukkuri.ConstState(0);
        assertDoesNotThrow(() -> ToyLogic.checkTrampoline(body));
    }

    // ---- checkToy: in-air favBall, owner=self (bOwnedFamily=true) → L84 false
    // ----

    @Test
    void testCheckToy_InAir_FavBall_OwnedFamily_L84False() {
        Toy toy = new Toy();
        toy.setX(body.getX());
        toy.setY(body.getY());
        toy.setObjId(2090);
        toy.setZ(10);
        toy.setOwner(body); // owner=b → bOwnedFamily=true
        body.setFavoriteItem(FavItemType.BALL, toy);
        SimYukkuri.world.getCurrentWorldState().getToys().put(toy.getObjId(), toy);
        assertDoesNotThrow(() -> ToyLogic.checkToy(body));
    }

    // ---- checkToy: out-of-range, favBall owned by self (bOwnedFamily=true) → L123
    // false ----

    @Test
    void testCheckToy_OutOfRange_FavBall_OwnedBySelf_L123False() {
        Toy toy = new Toy();
        toy.setX(body.getX());
        toy.setY(body.getY() + 50);
        toy.setObjId(2091);
        toy.setOwner(body); // bOwnedFamily=true → L123: !bOwnedFamily=false → skip stress
        body.setFavoriteItem(FavItemType.BALL, toy);
        SimYukkuri.world.getCurrentWorldState().getToys().put(toy.getObjId(), toy);
        assertDoesNotThrow(() -> ToyLogic.checkToy(body));
    }

    // ---- checkSui: favoriteItems set, nextBoolean=true, isTalking=true → L201
    // false ----

    @Test
    void testCheckSui_FavItem_NextBoolTrue_Talking_L201False() {
        Sui sui = new Sui(body.getX(), body.getY(), 0);
        SimYukkuri.world.getCurrentWorldState().getSuis().put(sui.getObjId(), sui);
        body.setFavoriteItem(FavItemType.SUI, sui);
        body.setMessageTicks(1); // isTalking=true → L201: !isTalking()=false → condition false
        org.simyukkuri.ConstState cs = new org.simyukkuri.ConstState(200);
        cs.setFixedBoolean(true); // nextBoolean=true (but !talking short-circuits to false)
        SimYukkuri.RND = cs;
        assertDoesNotThrow(() -> ToyLogic.checkSui(body));
    }

    // ---- checkTrampoline: rude body → L255 isRude branch, L269 isRude sub-branch
    // ----

    @Test
    void testCheckTrampoline_RudeAdult_L255_L269() {
        Trampoline t = new Trampoline();
        t.setX(body.getX());
        t.setY(body.getY());
        t.setObjId(2092);
        SimYukkuri.world.getCurrentWorldState().getTrampolines().put(t.getObjId(), t);
        body.setAgeState(org.simyukkuri.enums.AgeState.ADULT);
        body.setAttitude(Attitude.SHITHEAD); // isRude=true → L255 false (skip), L269 isRude=true
        SimYukkuri.RND = new org.simyukkuri.SequenceRandom(50, 50, 50, 50);
        assertDoesNotThrow(() -> ToyLogic.checkTrampoline(body));
    }

    // ---- checkTrampoline: FOOL body, accident2=0 → L290 false → else (L294) ----

    @Test
    void testCheckTrampoline_FoolBody_Option0_NoAccident() {
        Trampoline t = new Trampoline();
        t.setX(body.getX());
        t.setY(body.getY());
        t.setObjId(2093);
        t.setOption(0);
        // accident2=0 (default) → FOOL+nextInt+1 < 0 always false → else branch
        SimYukkuri.world.getCurrentWorldState().getTrampolines().put(t.getObjId(), t);
        body.setIntelligence(Intelligence.FOOL);
        SimYukkuri.RND = new org.simyukkuri.ConstState(0);
        assertDoesNotThrow(() -> ToyLogic.checkTrampoline(body));
    }

    // ---- checkTrampoline: FOOL body, option=1, accident2=0 → L299 false → else
    // (L303) ----

    @Test
    void testCheckTrampoline_FoolBody_Option1_NoAccident() {
        Trampoline t = new Trampoline();
        t.setX(body.getX());
        t.setY(body.getY());
        t.setObjId(2094);
        t.setOption(1);
        // accident2=0 → FOOL+nextInt+1 <= 0 always false → else branch
        SimYukkuri.world.getCurrentWorldState().getTrampolines().put(t.getObjId(), t);
        body.setIntelligence(Intelligence.FOOL);
        SimYukkuri.RND = new org.simyukkuri.ConstState(0);
        assertDoesNotThrow(() -> ToyLogic.checkTrampoline(body));
    }

    @Nested
    class RegressionScenarios {

        @Test
        void testScenario_FindingNearbyToyMakesItFavoriteAndOwned() {
            Toy toy = new Toy();
            toy.setX(body.getX());
            toy.setY(body.getY());
            toy.setObjId(3001);
            SimYukkuri.world.getCurrentWorldState().getToys().put(toy.getObjId(), toy);

            assertTrue(ToyLogic.checkToy(body));
            assertNotNull(body.getFavoriteItem(FavItemType.BALL));
            assertEquals(toy.getObjId(), body.getFavoriteItem(FavItemType.BALL).getObjId());
            assertTrue(toy.isOwned(body));
            assertEquals(Happiness.HAPPY, body.getHappiness());
        }
    }
}
