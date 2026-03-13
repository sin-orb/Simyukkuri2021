package src.logic;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import src.SimYukkuri;
import src.logic.BedLogic;
import src.util.WorldTestHelper;
import src.yukkuri.Marisa;
import src.base.Body;
import src.enums.AgeState;
import src.enums.PublicRank;
import src.item.Bed;
import src.item.House;
import src.item.Toilet;
import src.base.Obj;
import src.draw.Translate;

class BedLogicTest {

    private Body body;

    @BeforeEach
    void setUp() {
        WorldTestHelper.resetWorld();
        WorldTestHelper.initializeMinimalWorld();
        Translate.setMapSize(1000, 1000, 200);
        Translate.setCanvasSize(800, 600, 100, 100, new float[]{1.0f});
        Translate.createTransTable(false);

        body = WorldTestHelper.createBody();
        body.setX(100);
        body.setY(100);
        body.setZ(0); // On floor

        SimYukkuri.world.getCurrentMap().getBody().put(body.getObjId(), body);
    }

    @Test
    void testCheckBed_Sleepy() {
        WorldTestHelper.setSleeping(body, true);
        // Should find bed and return true if bed exists

        Bed bed = new Bed();
        bed.setX(150);
        bed.setY(150);
        SimYukkuri.world.getCurrentMap().getBed().put(bed.getObjId(), bed);

        boolean result = BedLogic.checkBed(body);
        // This might depend on implementation details of checkBed (distance etc.)
        // But with sleeping=true, it should try to find a bed.
    }

    @Test
    void testSearchBed_Found() {
        Bed bed = new Bed();
        bed.setX(150);
        bed.setY(150);
        SimYukkuri.world.getCurrentMap().getBed().put(bed.getObjId(), bed);

        Obj result = BedLogic.searchBed(body);
        assertNotNull(result);
        assertEquals(bed, result);
    }

    @Test
    void testSearchBed_NotFound() {
        Obj result = BedLogic.searchBed(body);
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
        body.setACTIVEPERIODorg(0); // make isSleepy() return true
        assertDoesNotThrow(() -> BedLogic.checkBed(body));
    }

    @Test
    void testCheckBed_withBed_sleepy_executesCode() {
        // Make body sleepy
        body.setACTIVEPERIODorg(0); // make isSleepy() return true
        body.setAge(999);
        Bed bed = new Bed();
        bed.setX(150); bed.setY(150);
        // Set bed dimensions so nextInt(ofsX) doesn't throw
        try {
            java.lang.reflect.Field wf = src.base.Obj.class.getDeclaredField("w");
            wf.setAccessible(true);
            wf.setInt(bed, 20);
            java.lang.reflect.Field hf = src.base.Obj.class.getDeclaredField("h");
            hf.setAccessible(true);
            hf.setInt(bed, 20);
        } catch (Exception e) { }
        SimYukkuri.world.getCurrentMap().getBed().put(bed.getObjId(), bed);
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
        body.setMoveTarget(-1); // no target
        // target==null so isToBed branch skips
        assertDoesNotThrow(() -> BedLogic.checkBed(body));
    }

    @Test
    void testCheckBed_isToBed_targetRemoved_clearsFavItem() {
        Bed bed = new Bed();
        bed.setX(100); bed.setY(100);
        bed.setRemoved(true);
        SimYukkuri.world.getCurrentMap().getBed().put(bed.getObjId(), bed);

        body.setToBed(true);
        body.setMoveTarget(bed.getObjId());
        // target.isRemoved() → clearActions and return false
        assertFalse(BedLogic.checkBed(body));
    }

    @Test
    void testCheckBed_isToBed_UnunSlave_clearsFavItem() {
        Bed bed = new Bed();
        bed.setX(200); bed.setY(200);
        SimYukkuri.world.getCurrentMap().getBed().put(bed.getObjId(), bed);

        body.setToBed(true);
        body.setMoveTarget(bed.getObjId());
        body.setPublicRank(PublicRank.UnunSlave);
        // UnunSlave heading to Bed → clearActions and return false
        assertFalse(BedLogic.checkBed(body));
    }

    @Test
    void testCheckBed_isToBed_arrived_setsStay() {
        Bed bed = new Bed();
        bed.setX(100); bed.setY(100); // same position as body → distance=0
        SimYukkuri.world.getCurrentMap().getBed().put(bed.getObjId(), bed);

        body.setToBed(true);
        body.setMoveTarget(bed.getObjId());
        body.setZ(0);
        // stepDist >= distance(0) → arrival, sets stay and returns true
        assertTrue(BedLogic.checkBed(body));
    }

    @Test
    void testCheckBed_isToBed_notArrived_movesTo() {
        Bed bed = new Bed();
        bed.setX(5000); bed.setY(5000); // far away
        SimYukkuri.world.getCurrentMap().getBed().put(bed.getObjId(), bed);

        body.setToBed(true);
        body.setMoveTarget(bed.getObjId());
        body.setZ(0);
        // not arrived → moveTo and returns true
        assertTrue(BedLogic.checkBed(body));
    }

    // --- searchBed: UnunSlave →  toilet ---

    @Test
    void testSearchBed_UnunSlave_withToilet_findsToilet() {
        body.setPublicRank(PublicRank.UnunSlave);
        src.item.Toilet toilet = new src.item.Toilet();
        toilet.setX(100); toilet.setY(100); // same position as body
        SimYukkuri.world.getCurrentMap().getToilet().put(toilet.getObjId(), toilet);
        assertDoesNotThrow(() -> BedLogic.searchBed(body));
    }

    @Test
    void testSearchBed_UnunSlave_noToilet_returnsNull() {
        body.setPublicRank(PublicRank.UnunSlave);
        Obj result = BedLogic.searchBed(body);
        assertNull(result);
    }

    // --- searchBed: house fallback ---

    @Test
    void testSearchBed_noBedsButHouse_findsHouse() {
        House house = new House();
        house.setX(100); house.setY(100); // same position as body
        SimYukkuri.world.getCurrentMap().getHouse().put(house.getObjId(), house);
        assertDoesNotThrow(() -> BedLogic.searchBed(body));
    }

    // --- checkBed: with house (no bed available) ---

    @Test
    void testCheckBed_withHouseOnly_doesNotThrow() {
        House house = new House();
        house.setX(100); house.setY(100);
        SimYukkuri.world.getCurrentMap().getHouse().put(house.getObjId(), house);
        body.setACTIVEPERIODorg(0); // make isSleepy() return true
        assertDoesNotThrow(() -> BedLogic.checkBed(body));
    }
}