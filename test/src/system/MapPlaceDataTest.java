package src.system;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import src.SimYukkuri;
import src.base.Body;
import src.base.Effect;
import src.base.EventPacket;
import src.base.Okazari;
import src.game.Shit;
import src.game.Stalk;
import src.game.Vomit;
import src.item.AutoFeeder;
import src.item.Barrier;
import src.item.Bed;
import src.item.Beltconveyor;
import src.item.BeltconveyorObj;
import src.item.BreedingPool;
import src.item.Diffuser;
import src.item.Farm;
import src.item.Food;
import src.item.FoodMaker;
import src.item.GarbageChute;
import src.item.GarbageStation;
import src.item.HotPlate;
import src.item.House;
import src.item.MachinePress;
import src.item.Mixer;
import src.item.OrangePool;
import src.item.Pool;
import src.item.ProcesserPlate;
import src.item.ProductChute;
import src.item.StickyPlate;
import src.item.Stone;
import src.item.Sui;
import src.item.Toilet;
import src.item.Toy;
import src.item.Trampoline;
import src.item.Trash;
import src.item.Yunba;
import src.util.WorldTestHelper;

class MapPlaceDataTest {

    @BeforeEach
    void setUp() {
        WorldTestHelper.initializeMinimalWorld();
    }

    @AfterEach
    void tearDown() {
        WorldTestHelper.resetWorld();
    }

    @Test
    void testClearMap() {
        int[][] map = new int[10][10];
        // Set some values
        map[0][0] = 1;
        map[5][5] = 2;

        MapPlaceData.clearMap(map);

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                assertEquals(0, map[i][j]);
            }
        }
    }

    @Test
    void testSetFiledFlag() {
        // We need to access SimYukkuri.world.getCurrentMap().getFieldMap() to verify
        MapPlaceData mapData = SimYukkuri.world.getCurrentMap();
        assertNotNull(mapData);

        // Ensure map is large enough (default world setup should be large enough)
        // Helper creates World(0,0) -> default windowtype 0
        // WindowType 0 usually small but > 100x100

        int[][] fieldMap = mapData.getFieldMap();
        // Clear first
        MapPlaceData.clearMap(fieldMap);

        // Set flag
        // setFiledFlag(int[][] map, int x, int y, int w, int h, boolean setFlag, int
        // attribute)
        // Note: The method signature takes "map" but implementation ignores it and uses
        // SimYukkuri.world.getCurrentMap().getFieldMap()!
        // Wait, looking at code:
        // public static void setFiledFlag(int[][] map, int x, int y, int w, int h,
        // boolean setFlag, int attribute) {
        // MapPlaceData tmp = SimYukkuri.world.getCurrentMap();
        // ...
        // tmp.getFieldMap()[px][py] = tmp.getFieldMap()[px][py] | attribute;
        // }
        // The "map" argument seems unused or mismatched in my reading?
        // Let's check reading again.
        // "tmp.getFieldMap()[px][py] = ..." is what I saw.
        // So I can pass null as map? Or correct map?
        // I should probably pass fieldMap just to be safe, but verify on
        // mapData.getFieldMap().

        int attr = 1;
        MapPlaceData.setFiledFlag(fieldMap, 10, 10, 5, 5, true, attr);

        assertEquals(attr, fieldMap[10][10]);
        assertEquals(attr, fieldMap[14][14]);

        // Remove flag
        MapPlaceData.setFiledFlag(fieldMap, 10, 10, 5, 5, false, attr);
        assertEquals(0, fieldMap[10][10]);
    }

    @Test
    void testSetWallLine() {
        MapPlaceData mapData = SimYukkuri.world.getCurrentMap();
        int[][] wallMap = mapData.getWallMap(); // Use wallMap for wall line
        MapPlaceData.clearMap(wallMap);

        int attr = 2;
        // setWallLine(int[][] map, int x1, int y1, int x2, int y2, boolean setFlag, int
        // attribute)
        // Implementation: map[x][y] = ... uses the passed map argument!
        MapPlaceData.setWallLine(wallMap, 0, 0, 0, 10, true, attr);

        assertEquals(attr, wallMap[0][0]);
        assertEquals(attr, wallMap[0][5]);
        assertEquals(attr, wallMap[0][10]);

        // Remove
        MapPlaceData.setWallLine(wallMap, 0, 0, 0, 10, false, attr);
        assertEquals(0, wallMap[0][5]);
    }

    @Test
    void testGetters() {
        MapPlaceData data = new MapPlaceData(1);
        assertEquals(1, data.getMapIndex());
        assertNotNull(data.getBody());
        assertNotNull(data.getShit());
    }

    // --- setMapIndex ---

    @Test
    void testSetGetMapIndex() {
        MapPlaceData data = new MapPlaceData(0);
        data.setMapIndex(5);
        assertEquals(5, data.getMapIndex());
    }

    // --- alarm ---

    @Test
    void testSetGetAlarm() {
        MapPlaceData data = new MapPlaceData(0);
        data.setAlarm(true);
        assertTrue(data.isAlarm());
        data.setAlarm(false);
        assertFalse(data.isAlarm());
    }

    // --- alarmPeriod ---

    @Test
    void testSetGetAlarmPeriod() {
        MapPlaceData data = new MapPlaceData(0);
        data.setAlarmPeriod(3000);
        assertEquals(3000, data.getAlarmPeriod());
    }

    // --- body ---

    @Test
    void testSetGetBody() {
        MapPlaceData data = new MapPlaceData(0);
        Map<Integer, Body> m = new HashMap<>();
        data.setBody(m);
        assertEquals(m, data.getBody());
    }

    // --- shit ---

    @Test
    void testSetGetShit() {
        MapPlaceData data = new MapPlaceData(0);
        Map<Integer, Shit> m = new HashMap<>();
        data.setShit(m);
        assertEquals(m, data.getShit());
    }

    // --- vomit ---

    @Test
    void testSetGetVomit() {
        MapPlaceData data = new MapPlaceData(0);
        Map<Integer, Vomit> m = new HashMap<>();
        data.setVomit(m);
        assertEquals(m, data.getVomit());
    }

    // --- barrier ---

    @Test
    void testSetGetBarrier() {
        MapPlaceData data = new MapPlaceData(0);
        List<Barrier> m = new LinkedList<>();
        data.setBarrier(m);
        assertEquals(m, data.getBarrier());
    }

    // --- event ---

    @Test
    void testSetGetEvent() {
        MapPlaceData data = new MapPlaceData(0);
        List<EventPacket> m = new LinkedList<>();
        data.setEvent(m);
        assertEquals(m, data.getEvent());
    }

    // --- sortEffect / frontEffect ---

    @Test
    void testSetGetSortEffect() {
        MapPlaceData data = new MapPlaceData(0);
        Map<Integer, Effect> m = new HashMap<>();
        data.setSortEffect(m);
        assertEquals(m, data.getSortEffect());
    }

    @Test
    void testSetGetFrontEffect() {
        MapPlaceData data = new MapPlaceData(0);
        Map<Integer, Effect> m = new HashMap<>();
        data.setFrontEffect(m);
        assertEquals(m, data.getFrontEffect());
    }

    // --- food / takenOutFood / takenOutShit ---

    @Test
    void testSetGetFood() {
        MapPlaceData data = new MapPlaceData(0);
        Map<Integer, Food> m = new HashMap<>();
        data.setFood(m);
        assertEquals(m, data.getFood());
    }

    @Test
    void testSetGetTakenOutFood() {
        MapPlaceData data = new MapPlaceData(0);
        Map<Integer, Food> m = new HashMap<>();
        data.setTakenOutFood(m);
        assertEquals(m, data.getTakenOutFood());
    }

    @Test
    void testSetGetTakenOutShit() {
        MapPlaceData data = new MapPlaceData(0);
        Map<Integer, Shit> m = new HashMap<>();
        data.setTakenOutShit(m);
        assertEquals(m, data.getTakenOutShit());
    }

    // --- toilet / bed / toy / stone / trampoline ---

    @Test
    void testSetGetToilet() {
        MapPlaceData data = new MapPlaceData(0);
        Map<Integer, Toilet> m = new HashMap<>();
        data.setToilet(m);
        assertEquals(m, data.getToilet());
    }

    @Test
    void testSetGetBed() {
        MapPlaceData data = new MapPlaceData(0);
        Map<Integer, Bed> m = new HashMap<>();
        data.setBed(m);
        assertEquals(m, data.getBed());
    }

    @Test
    void testSetGetToy() {
        MapPlaceData data = new MapPlaceData(0);
        Map<Integer, Toy> m = new HashMap<>();
        data.setToy(m);
        assertEquals(m, data.getToy());
    }

    @Test
    void testSetGetStone() {
        MapPlaceData data = new MapPlaceData(0);
        Map<Integer, Stone> m = new HashMap<>();
        data.setStone(m);
        assertEquals(m, data.getStone());
    }

    @Test
    void testSetGetTrampoline() {
        MapPlaceData data = new MapPlaceData(0);
        Map<Integer, Trampoline> m = new HashMap<>();
        data.setTrampoline(m);
        assertEquals(m, data.getTrampoline());
    }

    // --- breedingPool / garbageChute / foodMaker / orangePool / productChute ---

    @Test
    void testSetGetBreedingPool() {
        MapPlaceData data = new MapPlaceData(0);
        Map<Integer, BreedingPool> m = new HashMap<>();
        data.setBreedingPool(m);
        assertEquals(m, data.getBreedingPool());
    }

    @Test
    void testSetGetGarbageChute() {
        MapPlaceData data = new MapPlaceData(0);
        Map<Integer, GarbageChute> m = new HashMap<>();
        data.setGarbagechute(m);
        assertEquals(m, data.getGarbagechute());
    }

    @Test
    void testSetGetFoodMaker() {
        MapPlaceData data = new MapPlaceData(0);
        Map<Integer, FoodMaker> m = new HashMap<>();
        data.setFoodmaker(m);
        assertEquals(m, data.getFoodmaker());
    }

    @Test
    void testSetGetOrangePool() {
        MapPlaceData data = new MapPlaceData(0);
        Map<Integer, OrangePool> m = new HashMap<>();
        data.setOrangePool(m);
        assertEquals(m, data.getOrangePool());
    }

    @Test
    void testSetGetProductChute() {
        MapPlaceData data = new MapPlaceData(0);
        Map<Integer, ProductChute> m = new HashMap<>();
        data.setProductchute(m);
        assertEquals(m, data.getProductchute());
    }

    // --- stickyPlate / hotPlate / processerPlate / mixer / autoFeeder / machinePress ---

    @Test
    void testSetGetStickyPlate() {
        MapPlaceData data = new MapPlaceData(0);
        Map<Integer, StickyPlate> m = new HashMap<>();
        data.setStickyPlate(m);
        assertEquals(m, data.getStickyPlate());
    }

    @Test
    void testSetGetHotPlate() {
        MapPlaceData data = new MapPlaceData(0);
        Map<Integer, HotPlate> m = new HashMap<>();
        data.setHotPlate(m);
        assertEquals(m, data.getHotPlate());
    }

    @Test
    void testSetGetProcesserPlate() {
        MapPlaceData data = new MapPlaceData(0);
        Map<Integer, ProcesserPlate> m = new HashMap<>();
        data.setProcesserPlate(m);
        assertEquals(m, data.getProcesserPlate());
    }

    @Test
    void testSetGetMixer() {
        MapPlaceData data = new MapPlaceData(0);
        Map<Integer, Mixer> m = new HashMap<>();
        data.setMixer(m);
        assertEquals(m, data.getMixer());
    }

    @Test
    void testSetGetAutoFeeder() {
        MapPlaceData data = new MapPlaceData(0);
        Map<Integer, AutoFeeder> m = new HashMap<>();
        data.setAutofeeder(m);
        assertEquals(m, data.getAutofeeder());
    }

    @Test
    void testSetGetMachinePress() {
        MapPlaceData data = new MapPlaceData(0);
        Map<Integer, MachinePress> m = new HashMap<>();
        data.setMachinePress(m);
        assertEquals(m, data.getMachinePress());
    }

    // --- stalk / diffuser / yunba / sui / trash / garbageStation / house / beltconveyorObj ---

    @Test
    void testSetGetStalk() {
        MapPlaceData data = new MapPlaceData(0);
        Map<Integer, Stalk> m = new HashMap<>();
        data.setStalk(m);
        assertEquals(m, data.getStalk());
    }

    @Test
    void testSetGetDiffuser() {
        MapPlaceData data = new MapPlaceData(0);
        Map<Integer, Diffuser> m = new HashMap<>();
        data.setDiffuser(m);
        assertEquals(m, data.getDiffuser());
    }

    @Test
    void testSetGetYunba() {
        MapPlaceData data = new MapPlaceData(0);
        Map<Integer, Yunba> m = new HashMap<>();
        data.setYunba(m);
        assertEquals(m, data.getYunba());
    }

    @Test
    void testSetGetSui() {
        MapPlaceData data = new MapPlaceData(0);
        Map<Integer, Sui> m = new HashMap<>();
        data.setSui(m);
        assertEquals(m, data.getSui());
    }

    @Test
    void testSetGetTrash() {
        MapPlaceData data = new MapPlaceData(0);
        Map<Integer, Trash> m = new HashMap<>();
        data.setTrash(m);
        assertEquals(m, data.getTrash());
    }

    @Test
    void testSetGetGarbageStation() {
        MapPlaceData data = new MapPlaceData(0);
        Map<Integer, GarbageStation> m = new HashMap<>();
        data.setGarbageStation(m);
        assertEquals(m, data.getGarbageStation());
    }

    @Test
    void testSetGetHouse() {
        MapPlaceData data = new MapPlaceData(0);
        Map<Integer, House> m = new HashMap<>();
        data.setHouse(m);
        assertEquals(m, data.getHouse());
    }

    @Test
    void testSetGetBeltconveyorObj() {
        MapPlaceData data = new MapPlaceData(0);
        Map<Integer, BeltconveyorObj> m = new HashMap<>();
        data.setBeltconveyorObj(m);
        assertEquals(m, data.getBeltconveyorObj());
    }

    // --- beltconveyor / pool / farm / okazari ---

    @Test
    void testSetGetBeltconveyor() {
        MapPlaceData data = new MapPlaceData(0);
        List<Beltconveyor> m = new LinkedList<>();
        data.setBeltconveyor(m);
        assertEquals(m, data.getBeltconveyor());
    }

    @Test
    void testSetGetPool() {
        MapPlaceData data = new MapPlaceData(0);
        List<Pool> m = new LinkedList<>();
        data.setPool(m);
        assertEquals(m, data.getPool());
    }

    @Test
    void testSetGetFarm() {
        MapPlaceData data = new MapPlaceData(0);
        List<Farm> m = new LinkedList<>();
        data.setFarm(m);
        assertEquals(m, data.getFarm());
    }

    @Test
    void testSetGetOkazari() {
        MapPlaceData data = new MapPlaceData(0);
        Map<Integer, Okazari> m = new HashMap<>();
        data.setOkazari(m);
        assertEquals(m, data.getOkazari());
    }

    // --- hasDos ---

    @Test
    void testSetGetHasDos() {
        MapPlaceData data = new MapPlaceData(0);
        data.setHasDos(true);
        assertTrue(data.isHasDos());
        data.setHasDos(false);
        assertFalse(data.isHasDos());
    }

    // --- wallMap / fieldMap ---

    @Test
    void testSetGetWallMap() {
        MapPlaceData data = new MapPlaceData(0);
        int[][] m = new int[10][10];
        data.setWallMap(m);
        assertEquals(m, data.getWallMap());
    }

    @Test
    void testSetGetFieldMap() {
        MapPlaceData data = new MapPlaceData(0);
        int[][] m = new int[10][10];
        data.setFieldMap(m);
        assertEquals(m, data.getFieldMap());
    }
}
