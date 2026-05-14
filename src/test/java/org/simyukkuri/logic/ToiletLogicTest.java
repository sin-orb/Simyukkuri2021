package org.simyukkuri.logic;

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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.simyukkuri.SimYukkuri;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.world.item.Toilet;
import org.simyukkuri.entity.core.world.mobile.Shit;
import org.simyukkuri.event.EventPacket;
import org.simyukkuri.entity.core.Entity;
import org.simyukkuri.enums.AgeState;
import org.simyukkuri.enums.Attitude;
import org.simyukkuri.enums.CoreAnkoState;
import org.simyukkuri.enums.PublicRank;
import org.simyukkuri.enums.TakeoutItemType;
import org.simyukkuri.logic.ToiletLogic;
import org.simyukkuri.util.WorldTestHelper;

class ToiletLogicTest {

    private Yukkuri body;

    @BeforeEach
    void setUp() {
        WorldTestHelper.resetWorld();
        WorldTestHelper.initializeMinimalWorld();
        WorldTestHelper.initializeStandardTranslate200();

        ToiletLogic.setUnunSlave(null);

        body = new org.simyukkuri.entity.core.living.yukkuri.impl.Marisa() {
            @Override
            public int getCollisionX() {
                return 10;
            }

            @Override
            public org.simyukkuri.enums.AgeState getAgeState() {
                return org.simyukkuri.enums.AgeState.ADULT;
            }
        };
        body.setX(100);
        body.setY(100);

        SimYukkuri.world.getCurrentMap().getYukkuriMap().put(body.getObjId(), body);
    }

    @AfterEach
    void tearDown() {
        WorldTestHelper.resetWorld();
    }

    @Test
    void testCheckToilet_NoShit() {
        body.setShit(0);
        boolean result = ToiletLogic.checkToilet(body);
        assertFalse(result);
    }

    @Test
    void testCheckToilet_WantShit_NoToilet() {
        body.setShit(body.getShitLimit()); // Want to shit

        boolean result = ToiletLogic.checkToilet(body);
        // Should seek toilet if not found might return false or do something else
        // With logic, it searches using searchToilet. If not found, returns false (or
        // maybe shits nearby)
        // If searchToilet returns null, it might return false or start "shitting"
        // action on spot if critical.
        // checkToilet: if (searchToilet() != null) -> true

        // Assert false if no toilet
        assertFalse(result);
    }

    @Test
    void testCheckToilet_WantShit_WithToilet() {
        body.setShit(body.getShitLimit()); // Want to shit

        Toilet toilet = new Toilet();
        toilet.setX(150);
        toilet.setY(150);
        SimYukkuri.world.getCurrentMap().getToilet().put(toilet.getObjId(), toilet);

        boolean result = ToiletLogic.checkToilet(body);
        assertTrue(result);
        assertTrue(body.getMoveTargetId() != -1);
        Entity target = SimYukkuri.world.getCurrentMap().getToilet().get(body.getMoveTargetId());
        assertTrue(target instanceof Toilet);
    }

    @Test
    void testCheckShit_NoShitInWorld() {
        // shitListが空 → false (age=0 to pass age%15 check)
        body.setAge(0);
        boolean result = ToiletLogic.checkShit(body);
        assertFalse(result);
    }

    @Test
    void testCheckShit_WithShit_BodyAgeNotModulo15() {
        Shit shit = new Shit();
        shit.setX(150);
        shit.setY(150);
        SimYukkuri.world.getCurrentMap().getShit().put(shit.getObjId(), shit);

        // age % 15 != 0 → early return false
        body.setAge(1); // 1 % 15 != 0
        boolean result = ToiletLogic.checkShit(body);
        assertFalse(result);
    }

    @Test
    void testCheckShit_NYDBodyReturnsFalse() {
        Shit shit = new Shit();
        shit.setX(150);
        shit.setY(150);
        SimYukkuri.world.getCurrentMap().getShit().put(shit.getObjId(), shit);

        body.setAge(0); // 0 % 15 == 0
        body.setCoreAnkoState(CoreAnkoState.NonYukkuriDisease);
        boolean result = ToiletLogic.checkShit(body);
        assertFalse(result);
    }

    @Test
    void testGetSetBodyUnunSlave() {
        assertNull(ToiletLogic.getUnunSlave());
        Yukkuri slave = WorldTestHelper.createBody();
        ToiletLogic.setUnunSlave(slave);
        assertNotNull(ToiletLogic.getUnunSlave());
        ToiletLogic.setUnunSlave(null);
    }

    @Test
    void testCheckToilet_IsNYD() {
        body.setCoreAnkoState(CoreAnkoState.NonYukkuriDisease);
        assertFalse(ToiletLogic.checkToilet(body));
    }

    @Test
    void testCheckToilet_IsDead() {
        body.setDead(true);
        assertFalse(ToiletLogic.checkToilet(body));
    }

    // --- Additional checkToilet tests ---

    @Test
    void testCheckToilet_IsToFood_returnsFalse() {
        body.setToFood(true);
        assertFalse(ToiletLogic.checkToilet(body));
    }

    @Test
    void testCheckToilet_IsToBody_returnsFalse() {
        body.setToYukkuri(true);
        assertFalse(ToiletLogic.checkToilet(body));
    }

    @Test
    void testCheckToilet_HasToiletTarget_NotArrived_returnsTrue() {
        body.setShit(body.getShitLimit()); // wantToShit = true
        Toilet toilet = new Toilet();
        toilet.setX(500);
        toilet.setY(500); // far from body at (100,100)
        SimYukkuri.world.getCurrentMap().getToilet().put(toilet.getObjId(), toilet);
        body.setMoveTargetId(toilet.getObjId());
        body.setToShit(true);
        assertTrue(ToiletLogic.checkToilet(body));
    }

    @Test
    void testCheckToilet_WantShit_WithToilet_WantShitBranch_returnsTrue() {
        body.setShit(body.getShitLimit()); // wantToShit = true
        Toilet toilet = new Toilet();
        toilet.setX(150);
        toilet.setY(150);
        SimYukkuri.world.getCurrentMap().getToilet().put(toilet.getObjId(), toilet);
        // No move target yet → searches and finds toilet
        assertTrue(ToiletLogic.checkToilet(body));
    }

    // --- Additional checkShit tests ---

    @Test
    void testCheckShit_ShitNearby_BodyHatesShit_returnsTrue() {
        Shit shit = new Shit();
        shit.setX(101);
        shit.setY(101); // very close to body at (100,100)
        SimYukkuri.world.getCurrentMap().getShit().put(shit.getObjId(), shit);
        body.setAge(0); // 0 % 15 == 0
        // Section C: distance ≈ 1 < stepDist*2 → hates shit, sets stress, returns true
        assertTrue(ToiletLogic.checkShit(body));
    }

    @Test
    void testCheckShit_WithUnunSlaveInWorld_CanTransportFalse_HatesNearbyShit() {
        Shit shit = new Shit();
        shit.setX(101);
        shit.setY(101);
        SimYukkuri.world.getCurrentMap().getShit().put(shit.getObjId(), shit);
        body.setAge(0);
        // Add UnunSlave → bCanTransport=false
        Yukkuri slave = WorldTestHelper.createBody();
        slave.setPublicRank(PublicRank.UnunSlave);
        SimYukkuri.world.getCurrentMap().getYukkuriMap().put(slave.getUniqueID(), slave);
        // Section C still runs even when bCanTransport=false
        assertTrue(ToiletLogic.checkShit(body));
    }

    @Test
    void testCheckShit_ShitNotVeryClose_BodyDoesNotHate_returnsFalse() {
        Shit shit = new Shit();
        // stepDist*2 = 32; distance from (100,100) to (134,100) = 34 > 32 → no hate
        // And all coords < 152 → within wallMap bounds
        shit.setX(134);
        shit.setY(100);
        SimYukkuri.world.getCurrentMap().getShit().put(shit.getObjId(), shit);
        body.setAge(0);
        // Section C: distance 34 >= stepDist*2=32 → no hate reaction → returns false
        assertFalse(ToiletLogic.checkShit(body));
    }

    @Test
    void testCheckShit_BodyAlreadyTalking_NoHate_returnsFalse() {
        Shit shit = new Shit();
        shit.setX(101);
        shit.setY(101);
        SimYukkuri.world.getCurrentMap().getShit().put(shit.getObjId(), shit);
        body.setAge(0);
        body.setMessageTicks(1); // isTalking()=true → skips hate reaction
        assertFalse(ToiletLogic.checkShit(body));
    }

    @Test
    void testCheckShit_BodyIsToShit_NoHate_returnsFalse() {
        Shit shit = new Shit();
        shit.setX(101);
        shit.setY(101);
        SimYukkuri.world.getCurrentMap().getShit().put(shit.getObjId(), shit);
        body.setAge(0);
        body.setToShit(true); // isToShit=true → skips hate reaction
        assertFalse(ToiletLogic.checkShit(body));
    }

    @Test
    void testConstructor_doesNotThrow() {
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> new ToiletLogic());
    }

    // --- checkShit: isExciting early return ---

    @Test
    void testCheckShit_Exciting_ReturnsFalse() {
        Shit shit = new Shit();
        shit.setX(101);
        shit.setY(101);
        SimYukkuri.world.getCurrentMap().getShit().put(shit.getObjId(), shit);
        body.setAge(0); // 0 % 15 == 0
        body.setExciting(true); // line 47: isExciting() → return false
        assertFalse(ToiletLogic.checkShit(body));
    }

    // --- checkShit: nearToBirth early return ---

    @Test
    void testCheckShit_NearToBirth_ReturnsFalse() {
        Shit shit = new Shit();
        shit.setX(101);
        shit.setY(101);
        SimYukkuri.world.getCurrentMap().getShit().put(shit.getObjId(), shit);
        body.setAge(0);
        body.setHasBaby(true);
        body.setPregnantPeriod(body.getPregPeriodBase()); // nearToBirth()=true
        assertFalse(ToiletLogic.checkShit(body));
    }

    // --- checkShit: isDontMove → bCanTransport=false (line 83) ---

    @Test
    void testCheckShit_DontMove_bCanTransportFalse_HatesNearbyShit() {
        Shit shit = new Shit();
        shit.setX(101);
        shit.setY(101);
        SimYukkuri.world.getCurrentMap().getShit().put(shit.getObjId(), shit);
        body.setAge(0);
        body.setLockmove(true); // isDontMove()=true → bCanTransport=false
        // section C still runs → hates nearby shit → returns true
        assertTrue(ToiletLogic.checkShit(body));
    }

    // --- checkShit: isDamaged → bCanTransport=false (line 83) ---

    @Test
    void testCheckShit_Damaged_bCanTransportFalse_HatesNearbyShit() {
        Shit shit = new Shit();
        shit.setX(101);
        shit.setY(101);
        SimYukkuri.world.getCurrentMap().getShit().put(shit.getObjId(), shit);
        body.setAge(0);
        // Set damage to half+1 of ADULT limit → isDamaged()=VERY → bCanTransport=false
        int damageLimit = body.getDamageLimitBase()[org.simyukkuri.enums.AgeState.ADULT.ordinal()];
        WorldTestHelper.setDamage(body, damageLimit / 2 + 1);
        // section C still runs → hates nearby shit → returns true
        assertTrue(ToiletLogic.checkShit(body));
    }

    // --- checkShit: FOOL child ShitIntimidation (line 193) ---

    @Test
    void testCheckShit_FoolChild_NearShit_ShitIntimidation() {
        // Use anonymous subclass with getCollisionX() override to avoid sprite NPE
        Yukkuri child = new org.simyukkuri.entity.core.living.yukkuri.impl.Marisa() {
            @Override
            public int getCollisionX() {
                return 10;
            }

            @Override
            public org.simyukkuri.enums.AgeState getAgeState() {
                return org.simyukkuri.enums.AgeState.CHILD;
            }
        };
        child.setX(100);
        child.setY(100);
        child.setAge(0); // 0 % 15 == 0
        child.setIntelligence(org.simyukkuri.enums.Intelligence.FOOL);
        SimYukkuri.world.getCurrentMap().getYukkuriMap().put(child.getObjId(), child);

        Shit shit = new Shit();
        shit.setX(101);
        shit.setY(101); // very close → nDistance < stepDist*2
        SimYukkuri.world.getCurrentMap().getShit().put(shit.getObjId(), shit);

        // nextBoolean()=true → triggers ShitIntimidation branch
        org.simyukkuri.ConstState rng = new org.simyukkuri.ConstState(0);
        rng.setFixedBoolean(true);
        SimYukkuri.RND = rng;
        assertDoesNotThrow(() -> ToiletLogic.checkShit(child));
    }

    // --- checkToilet: target.getZ() != 0 → clearActions, return false ---

    @Test
    void testCheckToilet_ToShit_TargetZNonZero_ReturnsFalse() {
        body.setShit(body.getShitLimit()); // wantToShit=true so bHasShit or isToShit triggers
        Toilet toilet = new Toilet();
        toilet.setX(100);
        toilet.setY(100); // same position as body → arrived
        try {
            java.lang.reflect.Field zf = org.simyukkuri.entity.core.Entity.class.getDeclaredField("z");
            zf.setAccessible(true);
            zf.setInt(toilet, 10); // toilet.getZ()=10
        } catch (Exception e) {
        }
        SimYukkuri.world.getCurrentMap().getToilet().put(toilet.getObjId(), toilet);
        body.setMoveTargetId(toilet.getObjId());
        body.setToShit(true);
        // arrived at toilet with Z=10 → clearActions, return false
        assertFalse(ToiletLogic.checkToilet(body));
    }

    // --- checkToilet: UnunSlave arrived, hasShit → drops ---

    @Test
    void testCheckToilet_UnunSlave_HasShit_Arrived_Drops() {
        body.setPublicRank(PublicRank.UnunSlave);
        Shit shit = new Shit();
        SimYukkuri.world.getCurrentMap().getTakenOutShit().put(shit.getObjId(), shit);
        body.getCarryItems().put(org.simyukkuri.enums.TakeoutItemType.SHIT, shit.getObjId());

        Toilet toilet = new Toilet();
        toilet.setX(100);
        toilet.setY(100); // same position as body → arrived
        SimYukkuri.world.getCurrentMap().getToilet().put(toilet.getObjId(), toilet);
        body.setMoveTargetId(toilet.getObjId());
        // !wantToShit (shit=0) + UnunSlave + bHasShit → drops shit, returns true
        assertDoesNotThrow(() -> ToiletLogic.checkToilet(body));
    }

    // --- checkToilet: non-slave arrived with hasShit → drops ---

    @Test
    void testCheckToilet_NonSlave_HasShit_Arrived_Drops() {
        Shit shit = new Shit();
        SimYukkuri.world.getCurrentMap().getTakenOutShit().put(shit.getObjId(), shit);
        body.getCarryItems().put(org.simyukkuri.enums.TakeoutItemType.SHIT, shit.getObjId());

        Toilet toilet = new Toilet();
        toilet.setX(100);
        toilet.setY(100); // same position as body → arrived
        SimYukkuri.world.getCurrentMap().getToilet().put(toilet.getObjId(), toilet);
        body.setMoveTargetId(toilet.getObjId());
        // bHasShit=true, !wantToShit, non-slave → drops shit, returns true
        assertDoesNotThrow(() -> ToiletLogic.checkToilet(body));
    }

    // --- checkToilet: !wantToShit + !hasShit + non-auto toilet at body position
    // ---

    @Test
    void testCheckToilet_NotWantShit_NoHasShit_NonAutoToilet_returnsFalse() {
        // !wantToShit (shit=0), !bHasShit, non-auto toilet → else block runs, return
        // false
        Toilet toilet = new Toilet(); // autoClean=false by default
        toilet.setX(100);
        toilet.setY(100);
        SimYukkuri.world.getCurrentMap().getToilet().put(toilet.getObjId(), toilet);
        assertFalse(ToiletLogic.checkToilet(body));
    }

    // --- checkToilet: no toilet, hasShit → drop on spot ---

    @Test
    void testCheckToilet_NoToilet_HasShit_DropsOnSpot() {
        // bHasShit=true, no toilet → found=null → drop takeout on spot
        Shit shit = new Shit();
        SimYukkuri.world.getCurrentMap().getTakenOutShit().put(shit.getObjId(), shit);
        body.getCarryItems().put(org.simyukkuri.enums.TakeoutItemType.SHIT, shit.getObjId());
        // No toilet in map, no moveTargetId
        assertDoesNotThrow(() -> ToiletLogic.checkToilet(body));
    }

    // --- checkShit: bHasShit=true (L62) ---

    @Test
    void testCheckShit_HasShitTakeout_NearShit_returnsTrue() {
        // body is carrying shit (bHasShit=true at L62)
        Shit carried = new Shit();
        SimYukkuri.world.getCurrentMap().getTakenOutShit().put(carried.getObjId(), carried);
        body.getCarryItems().put(TakeoutItemType.SHIT, carried.getObjId());

        Shit nearby = new Shit();
        nearby.setX(101);
        nearby.setY(101); // very close
        SimYukkuri.world.getCurrentMap().getShit().put(nearby.getObjId(), nearby);
        body.setAge(0);
        // section C: distance < stepDist*2 → hate reaction → true
        assertTrue(ToiletLogic.checkShit(body));
    }

    // --- checkShit: toilet with large colW, shit ON toilet → bIsOnToilet=true
    // (L119-120) → continue (L133) ---

    @Test
    void testCheckShit_ShitOnLargeToilet_bIsOnToilet_continue_returnsTrue() {
        // Toilet with large colW makes checkHitObj(shit, false)=true
        Toilet toilet = new Toilet();
        toilet.setX(100);
        toilet.setY(100);
        toilet.setColW(1000);
        toilet.setColH(1000);
        SimYukkuri.world.getCurrentMap().getToilet().put(toilet.getObjId(), toilet);

        Shit shit = new Shit();
        shit.setX(100);
        shit.setY(100); // same position as body/toilet
        SimYukkuri.world.getCurrentMap().getShit().put(shit.getObjId(), shit);

        body.setAge(0);
        // section B: shit ON toilet → bIsOnToilet=true → L133 continue
        // section C: same shit, distance=0 < stepDist*2 → hate reaction → true
        assertDoesNotThrow(() -> ToiletLogic.checkShit(body));
    }

    // --- checkShit: toilet with default colW=0, shit NOT on toilet →
    // bFoundMyToilet=true (L125-126) ---

    @Test
    void testCheckShit_ToiletExists_ShitNotOnIt_bFoundMyToilet_returnsTrue() {
        // Toilet with default colW=0 → checkHitObj(shit,false)=false → else →
        // bFoundMyToilet=true
        Toilet toilet = new Toilet();
        toilet.setX(140);
        toilet.setY(140);
        SimYukkuri.world.getCurrentMap().getToilet().put(toilet.getObjId(), toilet);

        Shit shit = new Shit();
        shit.setX(101);
        shit.setY(101);
        SimYukkuri.world.getCurrentMap().getShit().put(shit.getObjId(), shit);

        body.setAge(0);
        // section B: toilet exists, shit not on it → bFoundMyToilet=true (L125-126)
        // section C: shit close → hate reaction → true
        assertTrue(ToiletLogic.checkShit(body));
    }

    // --- checkShit: another non-slave body in map → loop fall-through (L92
    // continue, L99) ---

    @Test
    void testCheckShit_OtherBodyInMap_LoopFallthrough_returnsTrue() {
        Shit shit = new Shit();
        shit.setX(101);
        shit.setY(101);
        SimYukkuri.world.getCurrentMap().getShit().put(shit.getObjId(), shit);

        // Another non-slave, non-dead body in the map
        Yukkuri other = new org.simyukkuri.entity.core.living.yukkuri.impl.Marisa() {
            @Override
            public int getCollisionX() {
                return 10;
            }

            @Override
            public AgeState getAgeState() {
                return AgeState.ADULT;
            }
        };
        other.setX(300);
        other.setY(300);
        SimYukkuri.world.getCurrentMap().getYukkuriMap().put(other.getObjId(), other);

        body.setAge(0);
        // loop: body itself → L92 continue; other body (non-slave) → L99 fall-through
        // section C: hate reaction → true
        assertTrue(ToiletLogic.checkShit(body));
    }

    // --- checkToilet: isNYD with NonYukkuriDiseaseNear (isDontMove=false) → L228
    // ---

    @Test
    void testCheckToilet_NYD_NearState_ReturnsFalse() {
        // NonYukkuriDiseaseNear → isNYD=true, isDontMove=false → L223 passes, L227
        // returns false
        body.setCoreAnkoState(CoreAnkoState.NonYukkuriDiseaseNear);
        assertFalse(ToiletLogic.checkToilet(body));
    }

    // --- checkToilet: getCurrentEvent with HIGH priority → L231 ---

    @Test
    void testCheckToilet_CurrentEvent_HighPriority_ReturnsFalse() {
        EventPacket ep = new EventPacket() {
            @Override
            public boolean checkEventResponse(Yukkuri b) {
                return false;
            }

            @Override
            public void start(Yukkuri b) {
            }

            @Override
            public boolean execute(Yukkuri b) {
                return false;
            }
        };
        ep.setPriority(EventPacket.EventPriority.HIGH);
        body.setCurrentEvent(ep);
        assertFalse(ToiletLogic.checkToilet(body));
    }

    // --- checkToilet: non-Toilet move target → L241 ---

    @Test
    void testCheckToilet_NonToiletTarget_ReturnsFalse() {
        body.setShit(body.getShitLimit()); // wantToShit=true (pass other checks)
        Shit shit = new Shit();
        shit.setX(150);
        shit.setY(150);
        SimYukkuri.world.getCurrentMap().getShit().put(shit.getObjId(), shit);
        body.setMoveTargetId(shit.getObjId());
        // oTarget is Shit (not Toilet) → return false at L241
        assertFalse(ToiletLogic.checkToilet(body));
    }

    // --- checkToilet: target removed → L294-295 ---

    @Test
    void testCheckToilet_Target_Removed_ReturnsFalse() {
        body.setShit(body.getShitLimit());
        Toilet toilet = new Toilet();
        toilet.setX(500);
        toilet.setY(500); // far, not arrived
        SimYukkuri.world.getCurrentMap().getToilet().put(toilet.getObjId(), toilet);
        body.setMoveTargetId(toilet.getObjId());
        body.setToShit(true);
        toilet.setRemoved(true);
        // target.isRemoved() → clearActions, return false
        assertFalse(ToiletLogic.checkToilet(body));
    }

    // --- checkToilet: arrived at toilet, wantToShit=true, Z=0 → b.stay() (L329)
    // ---

    @Test
    void testCheckToilet_Arrived_WantShit_Z0_Stay() {
        body.setShit(body.getShitLimit()); // wantToShit=true
        Toilet toilet = new Toilet();
        toilet.setX(100);
        toilet.setY(100); // same pos as body → arrived (distance=0)
        SimYukkuri.world.getCurrentMap().getToilet().put(toilet.getObjId(), toilet);
        body.setMoveTargetId(toilet.getObjId());
        body.setToShit(true);
        // arrived, Z=0, wantToShit=true → else: b.stay() (L329)
        assertDoesNotThrow(() -> ToiletLogic.checkToilet(body));
    }

    // --- checkToilet: UnunSlave + isToSteal + !wantToShit → L252 ---

    @Test
    void testCheckToilet_UnunSlave_isToSteal_NotWantShit_ReturnsFalse() {
        body.setPublicRank(PublicRank.UnunSlave);
        body.setToSteal(true);
        // !wantToShit (shit=0 default)
        assertFalse(ToiletLogic.checkToilet(body));
    }

    // --- checkToilet: UnunSlave on slave toilet, bHasShit → drops (L260-262) ---

    @Test
    void testCheckToilet_UnunSlave_OnSlaveToilet_HasShit_Drops() {
        body.setPublicRank(PublicRank.UnunSlave);
        Shit shit = new Shit();
        SimYukkuri.world.getCurrentMap().getTakenOutShit().put(shit.getObjId(), shit);
        body.getCarryItems().put(TakeoutItemType.SHIT, shit.getObjId());

        Toilet slaveTiolet = new Toilet();
        slaveTiolet.setX(100);
        slaveTiolet.setY(100);
        slaveTiolet.setColW(1000);
        slaveTiolet.setColH(1000);
        slaveTiolet.setForSlave(true);
        SimYukkuri.world.getCurrentMap().getToilet().put(slaveTiolet.getObjId(), slaveTiolet);
        // bHasShit=true, UnunSlave on slave toilet → drops shit (L260-262)
        assertDoesNotThrow(() -> ToiletLogic.checkToilet(body));
    }

    // --- checkToilet: UnunSlave on slave toilet, !bHasShit → return false (L266)
    // ---

    @Test
    void testCheckToilet_UnunSlave_OnSlaveToilet_NoShit_ReturnsFalse() {
        body.setPublicRank(PublicRank.UnunSlave);
        // no shit in takeout
        Toilet slaveToilet = new Toilet();
        slaveToilet.setX(100);
        slaveToilet.setY(100);
        slaveToilet.setColW(1000);
        slaveToilet.setColH(1000);
        slaveToilet.setForSlave(true);
        SimYukkuri.world.getCurrentMap().getToilet().put(slaveToilet.getObjId(), slaveToilet);
        // UnunSlave on slave toilet, !bHasShit → return false (L266)
        assertFalse(ToiletLogic.checkToilet(body));
    }

    // --- checkToilet: non-auto toilet with large colW, body inside → runAway
    // (L279-281) ---

    @Test
    void testCheckToilet_NonAutoToilet_BodyInside_RunAway() {
        // !wantToShit, !bHasShit, non-auto toilet at body position → runAway
        Toilet toilet = new Toilet(); // autoClean=false by default
        toilet.setX(100);
        toilet.setY(100);
        toilet.setColW(1000);
        toilet.setColH(1000);
        SimYukkuri.world.getCurrentMap().getToilet().put(toilet.getObjId(), toilet);
        // !isTalking (default)
        assertFalse(ToiletLogic.checkToilet(body));
    }

    // --- checkToilet: bHasShit=true, !wantToShit, no target → moveToYukkuri (L379)
    // ---

    @Test
    void testCheckToilet_HasShit_NotWantShit_NoTarget_moveToYukkuri() {
        Shit shit = new Shit();
        SimYukkuri.world.getCurrentMap().getTakenOutShit().put(shit.getObjId(), shit);
        body.getCarryItems().put(TakeoutItemType.SHIT, shit.getObjId());
        // bHasShit=true, !wantToShit (shit=0), no target set

        Toilet toilet = new Toilet();
        toilet.setX(140);
        toilet.setY(140);
        SimYukkuri.world.getCurrentMap().getToilet().put(toilet.getObjId(), toilet);
        // found != null, !wantToShit → moveToYukkuri (L379) → returns true
        assertTrue(ToiletLogic.checkToilet(body));
    }

    // --- checkToilet: UnunSlave, non-slave toilet → L362 skip → found=null → false
    // ---

    @Test
    void testCheckToilet_UnunSlave_NonSlaveToilet_Skipped_ReturnsFalse() {
        body.setPublicRank(PublicRank.UnunSlave);
        body.setShit(body.getShitLimit()); // wantToShit=true
        Toilet toilet = new Toilet(); // isForSlave=false by default
        toilet.setX(140);
        toilet.setY(140);
        SimYukkuri.world.getCurrentMap().getToilet().put(toilet.getObjId(), toilet);
        // UnunSlave search: !isForSlave() → L362 continue → found=null → returns false
        assertFalse(ToiletLogic.checkToilet(body));
    }

    // --- checkShit: pre-existing bodyUnunSlave alive → bCanTransport=false (L73)
    // ---

    @Test
    void testCheckShit_PreExistingUnunSlave_bCanTransportFalse_returnsTrue() {
        Yukkuri slave = WorldTestHelper.createBody();
        slave.setPublicRank(PublicRank.UnunSlave);
        ToiletLogic.setUnunSlave(slave); // pre-existing alive UnunSlave → L73

        Shit shit = new Shit();
        shit.setX(101);
        shit.setY(101);
        SimYukkuri.world.getCurrentMap().getShit().put(shit.getObjId(), shit);
        body.setAge(0);
        // bCanTransport=false via pre-existing slave (L73)
        // section C: hate reaction → true
        assertTrue(ToiletLogic.checkShit(body));
    }

    // --- checkShit: bCanTransport=true (non-zero hungry) → loop L85 continue for
    // self ---

    @Test
    void testCheckShit_bCanTransportTrue_LoopSelfContinue_returnsTrue() {
        // Set hungry > 20% threshold so isSoHungry=false → bCanTransport=true
        body.setHungry(body.getHungryLimitBase()[AgeState.ADULT.ordinal()]);

        Shit shit = new Shit();
        shit.setX(101);
        shit.setY(101);
        SimYukkuri.world.getCurrentMap().getShit().put(shit.getObjId(), shit);

        Yukkuri other = new org.simyukkuri.entity.core.living.yukkuri.impl.Marisa() {
            @Override
            public int getCollisionX() {
                return 10;
            }

            @Override
            public AgeState getAgeState() {
                return AgeState.ADULT;
            }
        };
        other.setX(300);
        other.setY(300);
        other.setHungry(body.getHungryLimitBase()[AgeState.ADULT.ordinal()]);
        SimYukkuri.world.getCurrentMap().getYukkuriMap().put(other.getObjId(), other);

        body.setAge(0);
        // bCanTransport=true → loop: body(self)→L85 continue,
        // other(non-slave)→fall-through
        // section C: hate reaction → true
        assertTrue(ToiletLogic.checkShit(body));
    }

    // --- checkToilet: canflyCheck=true → wallMode=ADULT (L346) ---

    @Test
    void testCheckToilet_CanflyCheck_WallModeAdult_returnsTrue() {
        Yukkuri flyingBody = new org.simyukkuri.entity.core.living.yukkuri.impl.Marisa() {
            @Override
            public int getCollisionX() {
                return 10;
            }

            @Override
            public AgeState getAgeState() {
                return AgeState.CHILD;
            }

            @Override
            public boolean isFlyingType() {
                return true;
            }
        };
        flyingBody.setX(100);
        flyingBody.setY(100);
        flyingBody.setShit(flyingBody.getShitLimit()); // wantToShit=true
        SimYukkuri.world.getCurrentMap().getYukkuriMap().put(flyingBody.getObjId(), flyingBody);

        Toilet toilet = new Toilet();
        toilet.setX(140);
        toilet.setY(140);
        SimYukkuri.world.getCurrentMap().getToilet().put(toilet.getObjId(), toilet);
        // canflyCheck=true → wallMode=ADULT (L346) → finds toilet → true
        assertTrue(ToiletLogic.checkToilet(flyingBody));
    }

    // --- checkShit: bodyUnunSlave != null but NOT UnunSlave rank → else branch
    // (L63 mb) ---

    @Test
    void testCheckShit_BodyUnunSlave_NotSlaveRank_elseBranch() {
        Yukkuri notSlave = WorldTestHelper.createBody();
        // default rank is NONE (not UnunSlave) → L63 false → else
        ToiletLogic.setUnunSlave(notSlave);
        Shit shit = new Shit();
        shit.setX(101);
        shit.setY(101);
        SimYukkuri.world.getCurrentMap().getShit().put(shit.getObjId(), shit);
        body.setAge(0);
        assertTrue(ToiletLogic.checkShit(body));
    }

    // --- checkShit: bodyUnunSlave is dead UnunSlave → else branch (L64 mb) ---

    @Test
    void testCheckShit_BodyUnunSlave_Dead_elseBranch() {
        Yukkuri deadSlave = WorldTestHelper.createBody();
        deadSlave.setPublicRank(PublicRank.UnunSlave);
        deadSlave.setDead(true); // !isDead()=false → AND short-circuits at L64
        ToiletLogic.setUnunSlave(deadSlave);
        Shit shit = new Shit();
        shit.setX(101);
        shit.setY(101);
        SimYukkuri.world.getCurrentMap().getShit().put(shit.getObjId(), shit);
        body.setAge(0);
        assertTrue(ToiletLogic.checkShit(body));
    }

    // --- checkShit: bodyUnunSlave is removed UnunSlave → else branch (L65 mb) ---

    @Test
    void testCheckShit_BodyUnunSlave_Removed_elseBranch() {
        Yukkuri removedSlave = WorldTestHelper.createBody();
        removedSlave.setPublicRank(PublicRank.UnunSlave);
        removedSlave.setRemoved(true); // !isRemoved()=false → AND short-circuits at L65
        ToiletLogic.setUnunSlave(removedSlave);
        Shit shit = new Shit();
        shit.setX(101);
        shit.setY(101);
        SimYukkuri.world.getCurrentMap().getShit().put(shit.getObjId(), shit);
        body.setAge(0);
        assertTrue(ToiletLogic.checkShit(body));
    }

    // --- checkShit: isFeelPain=true → bCanTransport=false (L69 C-branch) ---

    @Test
    void testCheckShit_FeelPain_bCanTransportFalse_HatesNearbyShit() {
        Yukkuri painBody = new org.simyukkuri.entity.core.living.yukkuri.impl.Marisa() {
            @Override
            public int getCollisionX() {
                return 10;
            }

            @Override
            public AgeState getAgeState() {
                return AgeState.ADULT;
            }

            @Override
            public boolean isFeelPain() {
                return true;
            }
        };
        painBody.setX(100);
        painBody.setY(100);
        // setHungry to avoid isSoHungry short-circuiting before isFeelPain
        painBody.setHungry(painBody.getHungryLimitBase()[AgeState.ADULT.ordinal()]);
        SimYukkuri.world.getCurrentMap().getYukkuriMap().put(painBody.getObjId(), painBody);
        Shit shit = new Shit();
        shit.setX(101);
        shit.setY(101);
        SimYukkuri.world.getCurrentMap().getShit().put(shit.getObjId(), shit);
        painBody.setAge(0);
        // isDontMove=false, isDamaged=false, isFeelPain=true → C branch of OR (L69)
        assertTrue(ToiletLogic.checkShit(painBody));
    }

    // --- checkShit: dead body in map → isDead→continue (L77-78) ---

    @Test
    void testCheckShit_DeadBodyAndSelfInMap_L78_continue() {
        body.setHungry(body.getHungryLimitBase()[AgeState.ADULT.ordinal()]); // isSoHungry=false → bCanTransport=true
        Yukkuri deadBody = new org.simyukkuri.entity.core.living.yukkuri.impl.Marisa() {
            @Override
            public int getCollisionX() {
                return 10;
            }

            @Override
            public AgeState getAgeState() {
                return AgeState.ADULT;
            }
        };
        deadBody.setDead(true);
        // clear and re-add with explicit unique keys to avoid collision
        SimYukkuri.world.getCurrentMap().getYukkuriMap().clear();
        SimYukkuri.world.getCurrentMap().getYukkuriMap().put(99997, deadBody); // isDead=true → continue
        SimYukkuri.world.getCurrentMap().getYukkuriMap().put(99998, body); // self → bodyOther==b → continue
        Shit shit = new Shit();
        shit.setX(101);
        shit.setY(101);
        SimYukkuri.world.getCurrentMap().getShit().put(shit.getObjId(), shit);
        body.setAge(0);
        // bCanTransport=true → loop: deadBody(isDead→continue), body(self→continue)
        assertTrue(ToiletLogic.checkShit(body));
    }

    // --- checkShit: shit with z=1 → z != b.getZ()=0 → continue (L91) ---

    @Test
    void testCheckShit_ShitZNonZero_continue_returnsFalse() {
        Shit shit = new Shit();
        shit.setX(101);
        shit.setY(101);
        try {
            java.lang.reflect.Field zf = org.simyukkuri.entity.core.Entity.class.getDeclaredField("z");
            zf.setAccessible(true);
            zf.setInt(shit, 1); // z=1 != body.z=0 → continue
        } catch (Exception e) {
        }
        SimYukkuri.world.getCurrentMap().getShit().put(shit.getObjId(), shit);
        body.setAge(0);
        // z mismatch → all shits skipped → no hate reaction → returns false
        assertFalse(ToiletLogic.checkShit(body));
    }

    // --- checkShit: child with non-NONE rank → L103 B false → else (HateShit) ---

    @Test
    void testCheckShit_ChildNotNoneRank_HateShit_returnsTrue() {
        Yukkuri childSlave = new org.simyukkuri.entity.core.living.yukkuri.impl.Marisa() {
            @Override
            public int getCollisionX() {
                return 10;
            }

            @Override
            public AgeState getAgeState() {
                return AgeState.CHILD;
            }
        };
        childSlave.setX(100);
        childSlave.setY(100);
        childSlave.setAge(0);
        childSlave.setPublicRank(PublicRank.UnunSlave); // not NONE → L103 B false → else
        SimYukkuri.world.getCurrentMap().getYukkuriMap().put(childSlave.getObjId(), childSlave);
        Shit shit = new Shit();
        shit.setX(101);
        shit.setY(101);
        SimYukkuri.world.getCurrentMap().getShit().put(shit.getObjId(), shit);
        assertDoesNotThrow(() -> ToiletLogic.checkShit(childSlave));
    }

    // --- checkShit: child NONE rank but not FOOL → L103 C false → else (HateShit)
    // ---

    @Test
    void testCheckShit_ChildNoneNotFool_HateShit_returnsTrue() {
        Yukkuri childNormal = new org.simyukkuri.entity.core.living.yukkuri.impl.Marisa() {
            @Override
            public int getCollisionX() {
                return 10;
            }

            @Override
            public AgeState getAgeState() {
                return AgeState.CHILD;
            }
        };
        childNormal.setX(100);
        childNormal.setY(100);
        childNormal.setAge(0);
        childNormal.setIntelligence(org.simyukkuri.enums.Intelligence.AVERAGE); // not FOOL → L103 C false → else
        SimYukkuri.world.getCurrentMap().getYukkuriMap().put(childNormal.getObjId(), childNormal);
        Shit shit = new Shit();
        shit.setX(101);
        shit.setY(101);
        SimYukkuri.world.getCurrentMap().getShit().put(shit.getObjId(), shit);
        assertDoesNotThrow(() -> ToiletLogic.checkShit(childNormal));
    }

    // --- checkShit: child NONE FOOL nextBoolean=false → L103 D false → else
    // (HateShit) ---

    @Test
    void testCheckShit_ChildFoolNextBoolFalse_HateShit_returnsTrue() {
        Yukkuri childFool = new org.simyukkuri.entity.core.living.yukkuri.impl.Marisa() {
            @Override
            public int getCollisionX() {
                return 10;
            }

            @Override
            public AgeState getAgeState() {
                return AgeState.CHILD;
            }
        };
        childFool.setX(100);
        childFool.setY(100);
        childFool.setAge(0);
        childFool.setIntelligence(org.simyukkuri.enums.Intelligence.FOOL);
        SimYukkuri.world.getCurrentMap().getYukkuriMap().put(childFool.getObjId(), childFool);
        Shit shit = new Shit();
        shit.setX(101);
        shit.setY(101);
        SimYukkuri.world.getCurrentMap().getShit().put(shit.getObjId(), shit);
        // nextBoolean=false → L103 D false → else HateShit (not ShitIntimidation)
        org.simyukkuri.ConstState rng = new org.simyukkuri.ConstState(0);
        rng.setFixedBoolean(false);
        SimYukkuri.RND = rng;
        assertDoesNotThrow(() -> ToiletLogic.checkShit(childFool));
    }

    // --- checkToilet: isToSukkiri → return false (L125) ---

    @Test
    void testCheckToilet_IsToSukkiri_returnsFalse() {
        body.setToSukkiri(true);
        assertFalse(ToiletLogic.checkToilet(body));
    }

    // --- checkToilet: isToBed → return false (L125) ---

    @Test
    void testCheckToilet_IsToBed_returnsFalse() {
        body.setToBed(true);
        assertFalse(ToiletLogic.checkToilet(body));
    }

    // --- checkToilet: nearToBirth → return false (L128) ---

    @Test
    void testCheckToilet_NearToBirth_returnsFalse() {
        body.setHasBaby(true);
        body.setPregnantPeriod(body.getPregPeriodBase()); // nearToBirth=true
        assertFalse(ToiletLogic.checkToilet(body));
    }

    // --- checkToilet: autoClean toilet → no runaway reaction (L178 A false) ---

    @Test
    void testCheckToilet_AutoCleanToilet_NoRunAway_returnsFalse() {
        Toilet toilet = new Toilet();
        toilet.setX(100);
        toilet.setY(100);
        toilet.setColW(1000);
        toilet.setColH(1000);
        toilet.setAutoClean(true); // autoClean=true → L178 condition false → no runaway
        SimYukkuri.world.getCurrentMap().getToilet().put(toilet.getObjId(), toilet);
        // !wantToShit, !bHasShit → else block → loop → autoClean=true → skip → return
        // false
        assertFalse(ToiletLogic.checkToilet(body));
    }

    // --- checkToilet: UnunSlave arrives, no shit → L210 null path → return true
    // ---

    @Test
    void testCheckToilet_UnunSlave_Arrived_NoShit_returnTrue() {
        body.setPublicRank(PublicRank.UnunSlave);
        // no shit in takeout, no wantToShit; use non-slave toilet (B1 loop does
        // nothing)
        Toilet toilet = new Toilet();
        toilet.setX(100);
        toilet.setY(100); // same pos → arrived
        SimYukkuri.world.getCurrentMap().getToilet().put(toilet.getObjId(), toilet);
        body.setMoveTargetId(toilet.getObjId());
        // UnunSlave → L192 true → arrived → !wantToShit → UnunSlave → L210: no shit →
        // null → return true
        assertDoesNotThrow(() -> assertTrue(ToiletLogic.checkToilet(body)));
    }

    // --- checkToilet: non-slave arrives with isToShit=true, no shit, !wantToShit →
    // L220 null ---

    @Test
    void testCheckToilet_NonSlave_ArrivedIsToShit_NoShit_returnTrue() {
        body.setShit(body.getShitLimit()); // wantToShit=true → L174 condition false → skip return false
        body.setToShit(true); // isToShit=true so L192 condition is true
        // no shit in takeout
        Toilet toilet = new Toilet();
        toilet.setX(100);
        toilet.setY(100);
        SimYukkuri.world.getCurrentMap().getToilet().put(toilet.getObjId(), toilet);
        body.setMoveTargetId(toilet.getObjId());
        // wantToShit=true → L174 skip → L192: isToShit=true && target!=null → return
        // true
        assertTrue(ToiletLogic.checkToilet(body));
    }

    // --- checkToilet: isRude=true → skip barrier check (L254 false path) ---

    @Test
    void testCheckToilet_IsRude_SkipsBarrierCheck_returnsTrue() {
        body.setShit(body.getShitLimit()); // wantToShit=true
        body.setAttitude(Attitude.SHITHEAD); // isRude=true → L254 !isRude=false → no barrier check
        Toilet toilet = new Toilet();
        toilet.setX(140);
        toilet.setY(140);
        SimYukkuri.world.getCurrentMap().getToilet().put(toilet.getObjId(), toilet);
        assertTrue(ToiletLogic.checkToilet(body));
    }

    // --- checkToilet: 3 toilets at different distances → L266 found != null ---

    @Test
    void testCheckToilet_ThreeToilets_FoundNotNull_L266() {
        body.setShit(body.getShitLimit()); // wantToShit=true
        Toilet t1 = new Toilet();
        t1.setX(130);
        t1.setY(100); // distance~=30
        SimYukkuri.world.getCurrentMap().getToilet().put(t1.getObjId(), t1);
        Toilet t2 = new Toilet();
        t2.setX(120);
        t2.setY(100); // distance~=20
        SimYukkuri.world.getCurrentMap().getToilet().put(t2.getObjId(), t2);
        Toilet t3 = new Toilet();
        t3.setX(110);
        t3.setY(100); // distance~=10
        SimYukkuri.world.getCurrentMap().getToilet().put(t3.getObjId(), t3);
        // With 3 toilets, at least one pair triggers L266 (found != null for 2nd+)
        assertTrue(ToiletLogic.checkToilet(body));
    }

    // --- checkToilet: slave toilet found first → non-slave skipped at L268 ---

    @Test
    void testCheckToilet_SlaveToiletPreferred_NonSlaveSkipped_L268() {
        body.setShit(body.getShitLimit()); // wantToShit=true
        Toilet slaveTlt = new Toilet();
        slaveTlt.setX(140);
        slaveTlt.setY(100); // distance~=40
        slaveTlt.setForSlave(true);
        SimYukkuri.world.getCurrentMap().getToilet().put(slaveTlt.getObjId(), slaveTlt);
        Toilet nonSlaveTlt = new Toilet();
        nonSlaveTlt.setX(120);
        nonSlaveTlt.setY(100); // distance~=20
        SimYukkuri.world.getCurrentMap().getToilet().put(nonSlaveTlt.getObjId(), nonSlaveTlt);
        // If slave found first, non-slave: L266 true, L267(slave&&!non-slave)=true →
        // L268 continue
        // If non-slave found first, slave: L266 true, L267 false → found=slave (slave
        // wins)
        assertTrue(ToiletLogic.checkToilet(body));
    }

    // --- checkToilet: HIGH priority event → return false (L135 true branch) ---

    @Test
    void testCheckToilet_HighPriorityEvent_returnsFalse() {
        body.setCurrentEvent(new TestEventPacket(EventPacket.EventPriority.HIGH));
        assertFalse(ToiletLogic.checkToilet(body));
    }

    // --- checkToilet: LOW priority event → L135 not blocked, continues (false
    // branch) ---

    @Test
    void testCheckToilet_LowPriorityEvent_notBlocked_returnsTrue() {
        body.setCurrentEvent(new TestEventPacket(EventPacket.EventPriority.LOW));
        body.setShit(body.getShitLimit()); // wantToShit=true
        Toilet toilet = new Toilet();
        toilet.setX(100);
        toilet.setY(100);
        SimYukkuri.world.getCurrentMap().getToilet().put(toilet.getObjId(), toilet);
        // LOW priority event → L135 condition false → continues → finds toilet → true
        assertTrue(ToiletLogic.checkToilet(body));
    }

    // --- checkShit: isIdiot=true → return false (L47 isIdiot branch) ---

    @Test
    void testCheckShit_Idiot_returnsFalse() {
        Shit shit = new Shit();
        shit.setX(150);
        shit.setY(150);
        SimYukkuri.world.getCurrentMap().getShit().put(shit.getObjId(), shit);
        Yukkuri idiotBody = new org.simyukkuri.entity.core.living.yukkuri.impl.Marisa() {
            @Override
            public int getCollisionX() {
                return 10;
            }

            @Override
            public boolean isIdiot() {
                return true;
            }
        };
        idiotBody.setAge(0); // age%15=0 → pass L46
        assertFalse(ToiletLogic.checkShit(idiotBody));
    }

    // --- checkToilet: isIdiot=true → return false (L128 isIdiot branch) ---

    @Test
    void testCheckToilet_Idiot_returnsFalse() {
        Yukkuri idiotBody = new org.simyukkuri.entity.core.living.yukkuri.impl.Marisa() {
            @Override
            public int getCollisionX() {
                return 10;
            }

            @Override
            public boolean isIdiot() {
                return true;
            }
        };
        assertFalse(ToiletLogic.checkToilet(idiotBody));
    }

    // --- checkShit: isRemoved body → continue (L77 isRemoved branch) ---

    @Test
    void testCheckShit_RemovedBodyInMap_continue_returnsTrue() {
        body.setHungry(body.getHungryLimitBase()[AgeState.ADULT.ordinal()]); // isSoHungry=false
        Yukkuri removedBody = new org.simyukkuri.entity.core.living.yukkuri.impl.Marisa() {
            @Override
            public int getCollisionX() {
                return 10;
            }
        };
        removedBody.setRemoved(true); // isRemoved=true → L77 continue
        SimYukkuri.world.getCurrentMap().getYukkuriMap().clear();
        SimYukkuri.world.getCurrentMap().getYukkuriMap().put(99990, removedBody);
        SimYukkuri.world.getCurrentMap().getYukkuriMap().put(99991, body);
        Shit shit = new Shit();
        shit.setX(101);
        shit.setY(101);
        SimYukkuri.world.getCurrentMap().getShit().put(shit.getObjId(), shit);
        body.setAge(0);
        assertTrue(ToiletLogic.checkShit(body));
    }

    // --- checkToilet: two non-slave toilets, farther processed first (key=1,2) →
    // L265 true, L267 found.slave=false ---

    @Test
    void testCheckToilet_TwoToilets_FoundNotNull_L265_nonSlave() {
        body.setShit(body.getShitLimit()); // wantToShit=true
        Toilet t1 = new Toilet();
        t1.setX(150);
        t1.setY(100); // farther: distance=(50^2)=2500
        Toilet t2 = new Toilet();
        t2.setX(110);
        t2.setY(100); // closer: distance=(10^2)=100
        // key 1 < key 2 → HashMap iterates key 1 first (bucket 1 before bucket 2)
        SimYukkuri.world.getCurrentMap().getToilet().put(1, t1);
        SimYukkuri.world.getCurrentMap().getToilet().put(2, t2);
        // t1 found first (found=null→false L265), t2: found!=null(L265 true),
        // found.slave=false→AND false→no continue
        assertTrue(ToiletLogic.checkToilet(body));
    }

    // --- checkToilet: slave found first, non-slave closer → L267 found.slave=true
    // && !t.slave=true → continue ---

    @Test
    void testCheckToilet_TwoToilets_L267_slaveSkipsNonSlave() {
        body.setShit(body.getShitLimit());
        Toilet slaveTlt = new Toilet();
        slaveTlt.setX(150);
        slaveTlt.setY(100); // farther=2500
        slaveTlt.setForSlave(true);
        Toilet nonSlaveTlt = new Toilet();
        nonSlaveTlt.setX(110);
        nonSlaveTlt.setY(100); // closer=100
        SimYukkuri.world.getCurrentMap().getToilet().put(1, slaveTlt); // processed first
        SimYukkuri.world.getCurrentMap().getToilet().put(2, nonSlaveTlt);
        // slaveTlt found first; nonSlaveTlt: found.slave=true && !non-slave=true →
        // continue (preferred slave)
        // final found = slaveTlt (slave toilet even though farther)
        assertTrue(ToiletLogic.checkToilet(body));
    }

    // --- checkToilet: both slave toilets → L267 found.slave=true && !t.slave=false
    // → no continue ---

    @Test
    void testCheckToilet_TwoToilets_L267_bothSlaveNoSkip() {
        body.setShit(body.getShitLimit());
        Toilet slave1 = new Toilet();
        slave1.setX(150);
        slave1.setY(100); // farther=2500
        slave1.setForSlave(true);
        Toilet slave2 = new Toilet();
        slave2.setX(110);
        slave2.setY(100); // closer=100
        slave2.setForSlave(true);
        SimYukkuri.world.getCurrentMap().getToilet().put(1, slave1); // processed first
        SimYukkuri.world.getCurrentMap().getToilet().put(2, slave2);
        // slave1 found first; slave2: found.slave=true && !slave2.slave=false → AND
        // false → no continue → found=slave2
        assertTrue(ToiletLogic.checkToilet(body));
    }

    // --- checkToilet: closest toilet at key=1, farther at key=2 → L252
    // minDistance>distance false ---

    @Test
    void testCheckToilet_FartherToilet_L252_false() {
        body.setShit(body.getShitLimit());
        Toilet tClose = new Toilet();
        tClose.setX(105);
        tClose.setY(100); // very close: distance=25
        Toilet tFar = new Toilet();
        tFar.setX(150);
        tFar.setY(100); // farther: distance=2500
        SimYukkuri.world.getCurrentMap().getToilet().put(1, tClose); // processed first → minDist=25
        SimYukkuri.world.getCurrentMap().getToilet().put(2, tFar); // distance=2500 > minDist=25 → L252 false
        // tFar is skipped (L252 false), found=tClose
        assertTrue(ToiletLogic.checkToilet(body));
    }

    // TestEventPacket helper for event-related tests
    private static class TestEventPacket extends EventPacket {
        private static final long serialVersionUID = 1L;

        public TestEventPacket(EventPriority priority) {
            this.priority = priority;
        }

        @Override
        public boolean checkEventResponse(Yukkuri b) {
            return true;
        }

        @Override
        public void start(Yukkuri b) {
        }

        @Override
        public boolean execute(Yukkuri b) {
            return true;
        }
    }

    @Nested
    class RegressionScenarios {

        @Test
        void testScenario_ArrivalWithCarriedShitDropsItAndRelaxes() {
            Shit carried = new Shit();
            SimYukkuri.world.getCurrentMap().getTakenOutShit().put(carried.getObjId(), carried);
            body.getCarryItems().put(TakeoutItemType.SHIT, carried.getObjId());
            body.addStress(30);

            Toilet toilet = new Toilet();
            toilet.setX(100);
            toilet.setY(100);
            SimYukkuri.world.getCurrentMap().getToilet().put(toilet.getObjId(), toilet);
            body.setMoveTargetId(toilet.getObjId());

            assertTrue(ToiletLogic.checkToilet(body));
            assertNull(body.getCarryItem(TakeoutItemType.SHIT));
            assertFalse(body.isToShit());
            assertTrue(body.getStress() < 30, "dropping the carried shit at arrival should reduce stress");
        }
    }
}
