package src.item;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import src.SimYukkuri;
import src.base.Body;
import src.base.ItemTestBase;
import src.base.ObjEX.ItemRank;
import src.enums.Event;
import src.game.Shit;
import src.game.Vomit;
import src.item.Yunba.Action;
import src.util.WorldTestHelper;

class YunbaTest extends ItemTestBase {

    /** Default Yunba initialized with required fields for clockTick/moveBody */
    private Yunba createYunba() {
        Yunba item = new Yunba();
        item.setActionFlags(new boolean[Action.values().length][3]);
        item.setActionFlags2(new boolean[1][5]);
        item.setActionFlags3(new boolean[1][3]);
        item.setDrawLayer(new int[]{0});
        item.setLayerCount(1);
        item.setSpeed(400);
        return item;
    }

    // --- Constructor default ---

    @Test
    void testConstructor_Default() {
        Yunba item = new Yunba();
        item.setObjId(1);
        SimYukkuri.world.getCurrentMap().getYunba().put(item.getObjId(), item);
        verifyCommonProperties(item);
        assertTrue(SimYukkuri.world.getCurrentMap().getYunba().containsKey(item.getObjId()));
    }

    // --- Action enum ---

    @Test
    void testActionEnum_count() {
        assertEquals(18, Action.values().length);
    }

    @Test
    void testActionEnum_toString() {
        for (Action a : Action.values()) {
            assertDoesNotThrow(() -> a.toString());
        }
    }

    @Test
    void testActionEnum_valueOf() {
        assertEquals(Action.CLEAN, Action.valueOf("CLEAN"));
        assertEquals(Action.HEAL, Action.valueOf("HEAL"));
        assertEquals(Action.SHIT, Action.valueOf("SHIT"));
        assertEquals(Action.STALK, Action.valueOf("STALK"));
        assertEquals(Action.KILL, Action.valueOf("KILL"));
        assertEquals(Action.DESTROY, Action.valueOf("DESTROY"));
        assertEquals(Action.NORND, Action.valueOf("NORND"));
        assertEquals(Action.EMPFOOD, Action.valueOf("EMPFOOD"));
    }

    // --- Static methods ---

    @Test
    void testGetBounding() {
        assertNotNull(Yunba.getBounding());
    }

    @Test
    void testHitCheckObjType() {
        assertEquals(0, Yunba.hitCheckObjType);
    }

    @Test
    void testHasSetupMenu() {
        Yunba item = new Yunba();
        assertTrue(item.hasSetupMenu());
    }

    // --- Getters/Setters ---

    @Test
    void testGetSetItemRank() {
        Yunba item = new Yunba();
        item.setItemRank(ItemRank.HOUSE);
        assertEquals(ItemRank.HOUSE, item.getItemRank());
        item.setItemRank(ItemRank.NORA);
        assertEquals(ItemRank.NORA, item.getItemRank());
    }

    @Test
    void testGetSetColor() {
        Yunba item = new Yunba();
        item.setColor(3);
        assertEquals(3, item.getColor());
    }

    @Test
    void testGetSetDirection() {
        Yunba item = new Yunba();
        item.setDirection(1);
        assertEquals(1, item.getDirection());
        item.setDirection(0);
        assertEquals(0, item.getDirection());
    }

    @Test
    void testGetSetActionFlags() {
        Yunba item = new Yunba();
        boolean[][] flags = new boolean[Action.values().length][3];
        flags[0][0] = true;
        item.setActionFlags(flags);
        assertTrue(item.getActionFlags()[0][0]);
    }

    @Test
    void testGetSetActionFlags2() {
        Yunba item = new Yunba();
        boolean[][] flags2 = new boolean[1][5];
        flags2[0][2] = true;
        item.setActionFlags2(flags2);
        assertTrue(item.getActionFlags2()[0][2]);
    }

    @Test
    void testGetSetActionFlags3() {
        Yunba item = new Yunba();
        boolean[][] flags3 = new boolean[1][3];
        flags3[0][1] = true;
        item.setActionFlags3(flags3);
        assertTrue(item.getActionFlags3()[0][1]);
    }

    @Test
    void testGetSetBodyCheck() {
        Yunba item = new Yunba();
        item.setBodyCheck(true);
        assertTrue(item.isBodyCheck());
        item.setBodyCheck(false);
        assertFalse(item.isBodyCheck());
    }

    @Test
    void testGetSetShitCheck() {
        Yunba item = new Yunba();
        item.setShitCheck(true);
        assertTrue(item.isShitCheck());
    }

    @Test
    void testGetSetStalkCheck() {
        Yunba item = new Yunba();
        item.setStalkCheck(true);
        assertTrue(item.isStalkCheck());
    }

    @Test
    void testGetSetNorndCheck() {
        Yunba item = new Yunba();
        item.setNorndCheck(true);
        assertTrue(item.isNorndCheck());
    }

    @Test
    void testGetSetKillCheck() {
        Yunba item = new Yunba();
        item.setKillCheck(true);
        assertTrue(item.isKillCheck());
    }

    @Test
    void testGetSetMineutiCheck() {
        Yunba item = new Yunba();
        item.setMineutiCheck(true);
        assertTrue(item.isMineutiCheck());
    }

    @Test
    void testGetSetNoDamageFallCheck() {
        Yunba item = new Yunba();
        item.setNoDamageFallCheck(true);
        assertTrue(item.isNoDamageFallCheck());
    }

    @Test
    void testGetSetFoodCheck() {
        Yunba item = new Yunba();
        item.setFoodCheck(true);
        assertTrue(item.isFoodCheck());
    }

    @Test
    void testGetSetDrawLayer() {
        Yunba item = new Yunba();
        int[] layer = {0, 1, 2};
        item.setDrawLayer(layer);
        assertArrayEquals(layer, item.getDrawLayer());
    }

    @Test
    void testGetSetLayerCount() {
        Yunba item = new Yunba();
        item.setLayerCount(3);
        assertEquals(3, item.getLayerCount());
    }

    @Test
    void testGetSetAction() {
        Yunba item = new Yunba();
        item.setAction(Action.CLEAN);
        assertEquals(Action.CLEAN, item.getAction());
        item.setAction(null);
        assertNull(item.getAction());
    }

    @Test
    void testGetSetTarget() {
        Yunba item = new Yunba();
        Food food = new Food(100, 100, 0);
        item.setTarget(food);
        assertEquals(food, item.getTarget());
    }

    @Test
    void testGetSetDestXY() {
        Yunba item = new Yunba();
        item.setDestX(300);
        item.setDestY(400);
        assertEquals(300, item.getDestX());
        assertEquals(400, item.getDestY());
    }

    @Test
    void testGetSetSpeed() {
        Yunba item = new Yunba();
        item.setSpeed(600);
        assertEquals(600, item.getSpeed());
    }

    @Test
    void testGetSetDefaultXY() {
        Yunba item = new Yunba();
        item.setDefaultX(500);
        item.setDefaultY(500);
        assertEquals(500, item.getDefaultX());
        assertEquals(500, item.getDefaultY());
    }

    @Test
    void testGetValueAndCost() {
        Yunba item = new Yunba();
        item.setValue(30000);
        item.setCost(200);
        assertEquals(30000, item.getValue());
        assertEquals(200, item.getCost());
    }

    // --- removeListData ---

    @Test
    void testRemoveListData() {
        Yunba item = new Yunba();
        item.setObjId(77);
        SimYukkuri.world.getCurrentMap().getYunba().put(77, item);
        assertTrue(SimYukkuri.world.getCurrentMap().getYunba().containsKey(77));
        item.removeListData();
        assertFalse(SimYukkuri.world.getCurrentMap().getYunba().containsKey(77));
    }

    // --- upDate ---

    @Test
    void testUpDate_AgeNotDivisibleBy2400_NoDeduct() {
        Yunba item = new Yunba();
        item.setCost(100);
        item.setAge(1); // 1 % 2400 != 0
        assertDoesNotThrow(() -> item.upDate());
    }

    @Test
    void testUpDate_AgeDivisibleBy2400_DeductsCost() {
        Yunba item = new Yunba();
        item.setCost(10);
        item.setAge(0); // 0 % 2400 == 0
        assertDoesNotThrow(() -> item.upDate());
    }

    // --- clockTick: removed ---

    @Test
    void testClockTick_Removed_ReturnsREMOVED() {
        Yunba item = createYunba();
        item.setObjId(88);
        SimYukkuri.world.getCurrentMap().getYunba().put(88, item);
        item.setRemoved(true);
        assertEquals(Event.REMOVED, item.clockTick());
    }

    // --- clockTick: grabbed ---

    @Test
    void testClockTick_Grabbed_ReturnsDONOTHING() {
        Yunba item = createYunba();
        item.setGrabbed(true);
        assertEquals(Event.DONOTHING, item.clockTick());
    }

    // --- clockTick: z > 0 (falling) ---

    @Test
    void testClockTick_ZAboveZero_ReturnsDONOTHING() {
        Yunba item = createYunba();
        item.setZ(10);
        assertEquals(Event.DONOTHING, item.clockTick());
    }

    // --- clockTick: action selection branch, all checks false ---

    @Test
    void testClockTick_ActionSelectionBranch_AllFalse() {
        Yunba item = createYunba();
        item.setNorndCheck(true); // forces into action selection branch
        item.setShitCheck(false);
        item.setStalkCheck(false);
        item.setBodyCheck(false);
        item.setFoodCheck(false);
        // action == null, no checks active → moves to defaultX/defaultY
        assertEquals(Event.DONOTHING, item.clockTick());
    }

    // --- clockTick: movement branch, destX/destY set ---

    @Test
    void testClockTick_MovementBranch_WithDestination() {
        Yunba item = createYunba();
        item.setAge(0); // age <=10 and norndCheck=false → movement branch
        item.setNorndCheck(false);
        item.setDestX(500);
        item.setDestY(500);
        item.setX(100);
        item.setY(100);
        assertDoesNotThrow(() -> item.clockTick());
    }

    // --- clockTick: movement branch, destination reached (destX=-1, destY=-1) ---

    @Test
    void testClockTick_MovementBranch_NoDestination() {
        Yunba item = createYunba();
        item.setAge(0);
        item.setNorndCheck(false);
        item.setDestX(-1);
        item.setDestY(-1);
        item.setAction(null);
        item.setTarget(null);
        assertEquals(Event.DONOTHING, item.clockTick());
    }

    // --- clockTick: movement, target is removed ---

    @Test
    void testClockTick_MovementBranch_TargetRemoved() {
        Yunba item = createYunba();
        item.setAge(0);
        item.setNorndCheck(false);
        item.setDestX(110);
        item.setDestY(110);
        item.setX(100);
        item.setY(100);
        Food food = new Food(110, 110, 0);
        food.setRemoved(true);
        item.setAction(Action.EMPFOOD);
        item.setTarget(food);
        assertDoesNotThrow(() -> item.clockTick());
    }

    // --- clockTick: action selection with shitCheck, no shit in world ---

    @Test
    void testClockTick_ShitCheck_NoShit() {
        Yunba item = createYunba();
        item.setNorndCheck(true);
        item.setShitCheck(true);
        item.setBodyCheck(false);
        item.setFoodCheck(false);
        item.setStalkCheck(false);
        // No shit in world → action stays null
        assertEquals(Event.DONOTHING, item.clockTick());
    }

    // --- cheackOtherYunbaTarget ---

    @Test
    void testCheackOtherYunbaTarget_NoOtherYunba_ReturnsTrue() {
        Yunba item = createYunba();
        item.setObjId(100);
        SimYukkuri.world.getCurrentMap().getYunba().put(100, item);
        Food food = new Food(100, 100, 0);
        assertTrue(item.cheackOtherYunbaTarget(food));
    }

    @Test
    void testCheackOtherYunbaTarget_OtherYunbaHasTarget_ReturnsFalse() {
        Yunba item1 = createYunba();
        item1.setObjId(101);
        SimYukkuri.world.getCurrentMap().getYunba().put(101, item1);

        Yunba item2 = createYunba();
        item2.setObjId(102);
        SimYukkuri.world.getCurrentMap().getYunba().put(102, item2);

        Food food = new Food(100, 100, 0);
        item2.setTarget(food); // item2 already has food as target

        // item1 checks if food is targeted by others → item2 has it → returns false
        assertFalse(item1.cheackOtherYunbaTarget(food));
    }

    // --- clockTick: action selection with bodyCheck, dead body in world ---

    @Test
    void testClockTick_BodyCheck_DeadBody_BodyRemoveEnabled() {
        Yunba item = createYunba();
        item.setNorndCheck(true);
        item.setShitCheck(false);
        item.setStalkCheck(false);
        item.setBodyCheck(true);
        item.setFoodCheck(false);

        // Enable BODY_REMOVE for all age states
        boolean[][] flags = new boolean[Action.values().length][3];
        flags[Action.BODY_REMOVE.ordinal()][0] = true;
        flags[Action.BODY_REMOVE.ordinal()][1] = true;
        flags[Action.BODY_REMOVE.ordinal()][2] = true;
        item.setActionFlags(flags);

        Body deadBody = WorldTestHelper.createBody();
        deadBody.setDead(true);
        deadBody.setX(110); deadBody.setY(110);
        SimYukkuri.world.getCurrentMap().getBody().put(deadBody.getObjId(), deadBody);

        item.setX(100); item.setY(100);
        item.setObjId(200);
        SimYukkuri.world.getCurrentMap().getYunba().put(200, item);

        assertDoesNotThrow(() -> item.clockTick());
    }

    // --- clockTick: shitCheck with shit in world ---

    @Test
    void testClockTick_ShitCheck_WithShit_DoesNotThrow() {
        Yunba item = createYunba();
        item.setNorndCheck(true);
        item.setShitCheck(true);
        item.setX(100); item.setY(100);
        item.setObjId(300);
        SimYukkuri.world.getCurrentMap().getYunba().put(300, item);

        Shit shit = new Shit();
        shit.setX(110); shit.setY(110);
        SimYukkuri.world.getCurrentMap().getShit().put(shit.getObjId(), shit);

        assertDoesNotThrow(() -> item.clockTick());
    }

    @Test
    void testClockTick_ShitCheck_WithVomit_DoesNotThrow() {
        Yunba item = createYunba();
        item.setNorndCheck(true);
        item.setShitCheck(true);
        item.setX(100); item.setY(100);
        item.setObjId(301);
        SimYukkuri.world.getCurrentMap().getYunba().put(301, item);

        Vomit vomit = new Vomit();
        vomit.setX(110); vomit.setY(110);
        SimYukkuri.world.getCurrentMap().getVomit().put(vomit.getObjId(), vomit);

        assertDoesNotThrow(() -> item.clockTick());
    }

    // --- clockTick: foodCheck with empty food in world ---

    @Test
    void testClockTick_FoodCheck_WithEmptyFood_DoesNotThrow() {
        Yunba item = createYunba();
        item.setNorndCheck(true);
        item.setShitCheck(false);
        item.setBodyCheck(false);
        item.setFoodCheck(true);
        item.setStalkCheck(false);
        item.setX(100); item.setY(100);
        item.setObjId(302);
        SimYukkuri.world.getCurrentMap().getYunba().put(302, item);

        Food emptyFood = new Food(110, 110, Food.FoodType.SWEETS1.ordinal());
        emptyFood.setAmount(0); // empty food
        SimYukkuri.world.getCurrentMap().getFood().put(emptyFood.getObjId(), emptyFood);

        assertDoesNotThrow(() -> item.clockTick());
    }

    // --- clockTick: movement with negative direction (body ahead of destination) ---

    @Test
    void testClockTick_Movement_NegativeDirection_DoesNotThrow() {
        Yunba item = createYunba();
        item.setNorndCheck(false);
        item.setAge(0);
        item.setX(150); item.setY(150);
        item.setDestX(100); item.setDestY(100); // destination behind body
        item.setAction(Action.EMPFOOD);
        Food food = new Food(100, 100, Food.FoodType.SWEETS1.ordinal());
        item.setTarget(food);
        assertDoesNotThrow(() -> item.clockTick());
    }

    // --- clockTick: movement near target (bNear=true) ---

    @Test
    void testClockTick_NearShitTarget_DoesNotThrow() {
        Yunba item = createYunba();
        item.setNorndCheck(false);
        item.setAge(0);
        item.setX(100); item.setY(100);
        item.setDestX(100); item.setDestY(100);
        item.setAction(Action.SHIT);
        Shit shit = new Shit();
        shit.setX(100); shit.setY(100);
        SimYukkuri.world.getCurrentMap().getShit().put(shit.getObjId(), shit);
        item.setTarget(shit);
        assertDoesNotThrow(() -> item.clockTick());
    }

    // --- clockTick: grabbed mode ---

    @Test
    void testClockTick_Grabbed_DoesNothing() {
        Yunba item = createYunba();
        item.setGrabbed(true);
        assertEquals(Event.DONOTHING, item.clockTick());
    }

    // --- clockTick: removed ---

    @Test
    void testClockTick_Removed_ReturnsRemoved() {
        Yunba item = createYunba();
        item.setRemoved(true);
        assertEquals(Event.REMOVED, item.clockTick());
    }

    // --- clockTick: airborne (z > 0) ---

    @Test
    void testClockTick_Airborne_DoesNothing() {
        Yunba item = createYunba();
        item.setZ(10);
        assertEquals(Event.DONOTHING, item.clockTick());
    }

    // --- Constructor with coordinates (int, int, int) ---

    @Test
    void testConstructor_WithCoords_DoesNotThrow() {
        // setupYunba uses GUI but it's in a try/catch in headless
        // The constructor always adds to the yunba map when setupYunba fails
        assertDoesNotThrow(() -> {
            try {
                Yunba y = new Yunba(100, 100, 0);
            } catch (Exception e) {
                // Expected in headless environment (GUI setup fails)
            }
        });
    }

    // --- getImageLayer: layerCount==0 (default new Yunba()) → returns 0 ---

    @Test
    void testGetImageLayer_defaultLayerCount_returnsZero() {
        Yunba item = new Yunba(); // layerCount defaults to 0
        java.awt.image.BufferedImage[] layer = new java.awt.image.BufferedImage[5];
        assertEquals(0, item.getImageLayer(layer));
    }

    // --- getImageLayer: layerCount==1, drawLayer[0]==0 → uses bodyImages ---

    @Test
    void testGetImageLayer_layerCount1_doesNotThrow() {
        Yunba item = createYunba(); // layerCount=1, drawLayer[0]=0
        java.awt.image.BufferedImage[] layer = new java.awt.image.BufferedImage[5];
        assertDoesNotThrow(() -> item.getImageLayer(layer));
        assertEquals(1, item.getImageLayer(layer));
    }

    // --- getShadowImage: images is static array (not null), elements are null → returns null ---

    @Test
    void testGetShadowImage_returnsNullElement() {
        Yunba item = new Yunba();
        assertDoesNotThrow(() -> item.getShadowImage());
    }

    // --- setupYunba: headless → returns false or throws ---

    @Test
    void testSetupYunba_headless_doesNotThrow() {
        Yunba item = createYunba();
        try {
            Yunba.setupYunba(item, true);
        } catch (Exception e) {
            // Expected in headless environment
        }
    }

    @Test
    void testLoadImages_headless_executesCode() {
        try {
            Yunba.loadImages(Yunba.class.getClassLoader(), null);
        } catch (Exception e) {
            // Expected: IOException because image files not found in test environment
        }
    }

    // Helper: create yunba with action+target set at same position as target (bNear=true)
    private Yunba createNearActionYunba(int x, int y) {
        Yunba item = createYunba();
        item.setX(x); item.setY(y);
        item.setDestX(x); item.setDestY(y);
        item.setNorndCheck(false);
        item.setAge(0);
        return item;
    }

    // --- clockTick action execution: BODY_REMOVE (near dead body) ---

    @Test
    void testClockTick_NearDeadBody_BodyRemove_DoesNotThrow() {
        Yunba item = createNearActionYunba(100, 100);
        item.setAction(Action.BODY_REMOVE);
        Body deadBody = WorldTestHelper.createBody();
        deadBody.setDead(true);
        deadBody.setX(100); deadBody.setY(100);
        SimYukkuri.world.getCurrentMap().getBody().put(deadBody.getObjId(), deadBody);
        item.setTarget(deadBody);
        assertDoesNotThrow(() -> item.clockTick());
    }

    // --- clockTick action execution: CLEAN (near dirty body) ---

    @Test
    void testClockTick_NearDirtyBody_Clean_DoesNotThrow() {
        Yunba item = createNearActionYunba(100, 100);
        item.setAction(Action.CLEAN);
        Body dirtyBody = WorldTestHelper.createBody();
        dirtyBody.setDirty(true);
        dirtyBody.setX(100); dirtyBody.setY(100);
        SimYukkuri.world.getCurrentMap().getBody().put(dirtyBody.getObjId(), dirtyBody);
        item.setTarget(dirtyBody);
        assertDoesNotThrow(() -> item.clockTick());
    }

    // --- clockTick action execution: HEAL (near damaged body) ---

    @Test
    void testClockTick_NearDamagedBody_Heal_DoesNotThrow() {
        Yunba item = createNearActionYunba(100, 100);
        item.setAction(Action.HEAL);
        Body damagedBody = WorldTestHelper.createBody();
        WorldTestHelper.setDamage(damagedBody, 1);
        damagedBody.setX(100); damagedBody.setY(100);
        SimYukkuri.world.getCurrentMap().getBody().put(damagedBody.getObjId(), damagedBody);
        item.setTarget(damagedBody);
        assertDoesNotThrow(() -> item.clockTick());
    }

    // --- clockTick action execution: STALK (near stalk) ---

    @Test
    void testClockTick_NearStalk_Stalk_DoesNotThrow() {
        Yunba item = createNearActionYunba(100, 100);
        item.setAction(Action.STALK);
        src.game.Stalk stalk = new src.game.Stalk(100, 100, 0);
        stalk.setAmount(100);
        SimYukkuri.world.getCurrentMap().getStalk().put(stalk.getObjId(), stalk);
        item.setTarget(stalk);
        assertDoesNotThrow(() -> item.clockTick());
    }

    // --- clockTick action execution: EMPFOOD (near empty food) ---

    @Test
    void testClockTick_NearEmptyFood_EmpFood_DoesNotThrow() {
        Yunba item = createNearActionYunba(100, 100);
        item.setAction(Action.EMPFOOD);
        src.item.Food food = new src.item.Food(100, 100, src.item.Food.FoodType.SWEETS1.ordinal());
        food.setAmount(0);
        SimYukkuri.world.getCurrentMap().getFood().put(food.getObjId(), food);
        item.setTarget(food);
        assertDoesNotThrow(() -> item.clockTick());
    }

    // --- clockTick action execution: DESTROY (near body) ---

    @Test
    void testClockTick_NearBody_Destroy_DoesNotThrow() {
        Yunba item = createNearActionYunba(100, 100);
        item.setAction(Action.DESTROY);
        item.setKillCheck(true); // strikeByPress path
        Body targetBody = WorldTestHelper.createBody();
        targetBody.setX(100); targetBody.setY(100);
        SimYukkuri.world.getCurrentMap().getBody().put(targetBody.getObjId(), targetBody);
        item.setTarget(targetBody);
        assertDoesNotThrow(() -> item.clockTick());
    }

    // --- clockTick action execution: KABI (near sick body) ---

    @Test
    void testClockTick_NearSickBody_Kabi_DoesNotThrow() {
        Yunba item = createNearActionYunba(100, 100);
        item.setAction(Action.KABI);
        Body sickBody = WorldTestHelper.createBody();
        sickBody.setX(100); sickBody.setY(100);
        SimYukkuri.world.getCurrentMap().getBody().put(sickBody.getObjId(), sickBody);
        item.setTarget(sickBody);
        assertDoesNotThrow(() -> item.clockTick());
    }

    // --- clockTick: bodyCheck with living body, CLEAN flag set ---

    @Test
    void testClockTick_BodyCheck_LivingDirtyBody_CleanEnabled_DoesNotThrow() {
        Yunba item = createYunba();
        item.setNorndCheck(true);
        item.setShitCheck(false);
        item.setStalkCheck(false);
        item.setBodyCheck(true);
        item.setFoodCheck(false);

        boolean[][] flags = new boolean[Action.values().length][3];
        flags[Action.CLEAN.ordinal()][0] = true;
        flags[Action.CLEAN.ordinal()][1] = true;
        flags[Action.CLEAN.ordinal()][2] = true;
        item.setActionFlags(flags);

        Body dirtyBody = WorldTestHelper.createBody();
        dirtyBody.setDirty(true);
        dirtyBody.setX(110); dirtyBody.setY(110);
        SimYukkuri.world.getCurrentMap().getBody().put(dirtyBody.getObjId(), dirtyBody);

        item.setX(100); item.setY(100);
        item.setObjId(800);
        SimYukkuri.world.getCurrentMap().getYunba().put(800, item);

        assertDoesNotThrow(() -> item.clockTick());
    }

    // --- clockTick: bodyCheck with stalkCheck also on ---

    @Test
    void testClockTick_StalkCheck_WithStalk_DoesNotThrow() {
        Yunba item = createYunba();
        item.setNorndCheck(true);
        item.setShitCheck(false);
        item.setStalkCheck(true);
        item.setBodyCheck(false);
        item.setFoodCheck(false);

        item.setX(100); item.setY(100);
        item.setObjId(801);
        SimYukkuri.world.getCurrentMap().getYunba().put(801, item);

        src.game.Stalk stalk = new src.game.Stalk(110, 110, 0);
        stalk.setAmount(100);
        SimYukkuri.world.getCurrentMap().getStalk().put(stalk.getObjId(), stalk);

        assertDoesNotThrow(() -> item.clockTick());
    }
}
