package src.logic;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import src.SimYukkuri;
import src.base.Body;
import src.base.Obj;
import src.draw.Translate;
import src.enums.CoreAnkoState;
import src.enums.PublicRank;
import src.game.Shit;
import src.item.Toilet;
import src.logic.ToiletLogic;
import src.util.WorldTestHelper;

class ToiletLogicTest {

    private Body body;

    @BeforeEach
    void setUp() {
        WorldTestHelper.resetWorld();
        WorldTestHelper.initializeMinimalWorld();
        Translate.setMapSize(1000, 1000, 200);

        body = new src.yukkuri.Marisa() {
            @Override
            public int getCollisionX() {
                return 10;
            }

            @Override
            public src.enums.AgeState getBodyAgeState() {
                return src.enums.AgeState.ADULT;
            }
        };
        body.setX(100);
        body.setY(100);

        SimYukkuri.world.getCurrentMap().getBody().put(body.getObjId(), body);
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
        assertTrue(body.getMoveTarget() != -1);
        Obj target = SimYukkuri.world.getCurrentMap().getToilet().get(body.getMoveTarget());
        assertTrue(target instanceof Toilet);
    }

    @Test
    void testCheckShit_NoShitInWorld() {
        // shitListが空 → false
        boolean result = ToiletLogic.checkShit(body);
        assertFalse(result);
    }

    @Test
    void testCheckShit_WithShit_BodyAgeNotModulo15() {
        Shit shit = new Shit();
        shit.setX(150); shit.setY(150);
        SimYukkuri.world.getCurrentMap().getShit().put(shit.getObjId(), shit);

        // age % 15 != 0 → early return false
        body.setAge(1); // 1 % 15 != 0
        boolean result = ToiletLogic.checkShit(body);
        assertFalse(result);
    }

    @Test
    void testCheckShit_NYDBodyReturnsFalse() {
        Shit shit = new Shit();
        shit.setX(150); shit.setY(150);
        SimYukkuri.world.getCurrentMap().getShit().put(shit.getObjId(), shit);

        body.setAge(0); // 0 % 15 == 0
        body.seteCoreAnkoState(CoreAnkoState.NonYukkuriDisease);
        boolean result = ToiletLogic.checkShit(body);
        assertFalse(result);
    }

    @Test
    void testGetSetBodyUnunSlave() {
        assertNull(ToiletLogic.getBodyUnunSlave());
        Body slave = WorldTestHelper.createBody();
        ToiletLogic.setBodyUnunSlave(slave);
        assertNotNull(ToiletLogic.getBodyUnunSlave());
        ToiletLogic.setBodyUnunSlave(null);
    }

    @Test
    void testCheckToilet_IsNYD() {
        body.seteCoreAnkoState(CoreAnkoState.NonYukkuriDisease);
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
        body.setToBody(true);
        assertFalse(ToiletLogic.checkToilet(body));
    }

    @Test
    void testCheckToilet_HasToiletTarget_NotArrived_returnsTrue() {
        body.setShit(body.getShitLimit()); // wantToShit = true
        Toilet toilet = new Toilet();
        toilet.setX(500); toilet.setY(500); // far from body at (100,100)
        SimYukkuri.world.getCurrentMap().getToilet().put(toilet.getObjId(), toilet);
        body.setMoveTarget(toilet.getObjId());
        body.setToShit(true);
        assertTrue(ToiletLogic.checkToilet(body));
    }

    @Test
    void testCheckToilet_WantShit_WithToilet_WantShitBranch_returnsTrue() {
        body.setShit(body.getShitLimit()); // wantToShit = true
        Toilet toilet = new Toilet();
        toilet.setX(150); toilet.setY(150);
        SimYukkuri.world.getCurrentMap().getToilet().put(toilet.getObjId(), toilet);
        // No move target yet → searches and finds toilet
        assertTrue(ToiletLogic.checkToilet(body));
    }

    // --- Additional checkShit tests ---

    @Test
    void testCheckShit_ShitNearby_BodyHatesShit_returnsTrue() {
        Shit shit = new Shit();
        shit.setX(101); shit.setY(101); // very close to body at (100,100)
        SimYukkuri.world.getCurrentMap().getShit().put(shit.getObjId(), shit);
        body.setAge(0); // 0 % 15 == 0
        // Section C: distance ≈ 1 < stepDist*2 → hates shit, sets stress, returns true
        assertTrue(ToiletLogic.checkShit(body));
    }

    @Test
    void testCheckShit_WithUnunSlaveInWorld_CanTransportFalse_HatesNearbyShit() {
        Shit shit = new Shit();
        shit.setX(101); shit.setY(101);
        SimYukkuri.world.getCurrentMap().getShit().put(shit.getObjId(), shit);
        body.setAge(0);
        // Add UnunSlave → bCanTransport=false
        Body slave = WorldTestHelper.createBody();
        slave.setPublicRank(PublicRank.UnunSlave);
        SimYukkuri.world.getCurrentMap().getBody().put(slave.getUniqueID(), slave);
        // Section C still runs even when bCanTransport=false
        assertTrue(ToiletLogic.checkShit(body));
    }

    @Test
    void testCheckShit_ShitNotVeryClose_BodyDoesNotHate_returnsFalse() {
        Shit shit = new Shit();
        // stepDist*2 = 32; distance from (100,100) to (134,100) = 34 > 32 → no hate
        // And all coords < 152 → within wallMap bounds
        shit.setX(134); shit.setY(100);
        SimYukkuri.world.getCurrentMap().getShit().put(shit.getObjId(), shit);
        body.setAge(0);
        // Section C: distance 34 >= stepDist*2=32 → no hate reaction → returns false
        assertFalse(ToiletLogic.checkShit(body));
    }

    @Test
    void testCheckShit_BodyAlreadyTalking_NoHate_returnsFalse() {
        Shit shit = new Shit();
        shit.setX(101); shit.setY(101);
        SimYukkuri.world.getCurrentMap().getShit().put(shit.getObjId(), shit);
        body.setAge(0);
        body.setMessageCount(1); // isTalking()=true → skips hate reaction
        assertFalse(ToiletLogic.checkShit(body));
    }

    @Test
    void testCheckShit_BodyIsToShit_NoHate_returnsFalse() {
        Shit shit = new Shit();
        shit.setX(101); shit.setY(101);
        SimYukkuri.world.getCurrentMap().getShit().put(shit.getObjId(), shit);
        body.setAge(0);
        body.setToShit(true); // isToShit=true → skips hate reaction
        assertFalse(ToiletLogic.checkShit(body));
    }

    @Test
    void testConstructor_doesNotThrow() {
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> new ToiletLogic());
    }
}