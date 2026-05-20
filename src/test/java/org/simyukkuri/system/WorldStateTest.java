package org.simyukkuri.system;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.simyukkuri.SimYukkuri;
import org.simyukkuri.entity.core.effect.Effect;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.world.bodylinked.Okazari;
import org.simyukkuri.entity.core.world.bodylinked.Stalk;
import org.simyukkuri.entity.core.world.item.AutoFeeder;
import org.simyukkuri.entity.core.world.item.Bed;
import org.simyukkuri.entity.core.world.item.BeltconveyorObj;
import org.simyukkuri.entity.core.world.item.BreedingPool;
import org.simyukkuri.entity.core.world.item.Diffuser;
import org.simyukkuri.entity.core.world.item.Food;
import org.simyukkuri.entity.core.world.item.FoodMaker;
import org.simyukkuri.entity.core.world.item.GarbageChute;
import org.simyukkuri.entity.core.world.item.GarbageStation;
import org.simyukkuri.entity.core.world.item.HotPlate;
import org.simyukkuri.entity.core.world.item.House;
import org.simyukkuri.entity.core.world.item.MachinePress;
import org.simyukkuri.entity.core.world.item.Mixer;
import org.simyukkuri.entity.core.world.item.OrangePool;
import org.simyukkuri.entity.core.world.item.ProcessorPlate;
import org.simyukkuri.entity.core.world.item.ProductChute;
import org.simyukkuri.entity.core.world.item.StickyPlate;
import org.simyukkuri.entity.core.world.item.Stone;
import org.simyukkuri.entity.core.world.item.Sui;
import org.simyukkuri.entity.core.world.item.Toilet;
import org.simyukkuri.entity.core.world.item.Toy;
import org.simyukkuri.entity.core.world.item.Trampoline;
import org.simyukkuri.entity.core.world.item.Trash;
import org.simyukkuri.entity.core.world.item.Yunba;
import org.simyukkuri.entity.core.world.mobile.Shit;
import org.simyukkuri.entity.core.world.mobile.Vomit;
import org.simyukkuri.event.EventPacket;
import org.simyukkuri.field.impl.Barrier;
import org.simyukkuri.field.impl.Beltconveyor;
import org.simyukkuri.field.impl.Farm;
import org.simyukkuri.field.impl.Pool;
import org.simyukkuri.util.WorldTestHelper;

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

        WorldState.clearGrid(map);

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                assertEquals(0, map[i][j]);
            }
        }
    }

    @Test
    void testSetFiledFlag() {
        // We need to access SimYukkuri.world.getCurrentWorldState().getFieldGrid() to
        // verify
        WorldState mapData = SimYukkuri.world.getCurrentWorldState();
        assertNotNull(mapData);

        // Ensure map is large enough (default world setup should be large enough)
        // Helper creates World(0,0) -> default windowtype 0
        // WindowType 0 usually small but > 100x100

        int[][] fieldMap = mapData.getFieldGrid();
        // Clear first
        WorldState.clearGrid(fieldMap);

        // Set flag
        // setFieldFlag(int[][] map, int x, int y, int w, int h, boolean setFlag, int
        // attribute)
        // Note: The method signature takes "map" but implementation ignores it and uses
        // SimYukkuri.world.getCurrentWorldState().getFieldGrid()!
        // Wait, looking at code:
        // public static void setFieldFlag(int[][] map, int x, int y, int w, int h,
        // boolean setFlag, int attribute) {
        // WorldState tmp = SimYukkuri.world.getCurrentWorldState();
        // ...
        // tmp.getFieldGrid()[px][py] = tmp.getFieldGrid()[px][py] | attribute;
        // }
        // The "map" argument seems unused or mismatched in my reading?
        // Let's check reading again.
        // "tmp.getFieldGrid()[px][py] = ..." is what I saw.
        // So I can pass null as map? Or correct map?
        // I should probably pass fieldMap just to be safe, but verify on
        // mapData.getFieldGrid().

        int attr = 1;
        WorldState.setFieldFlag(fieldMap, 10, 10, 5, 5, true, attr);

        assertEquals(attr, fieldMap[10][10]);
        assertEquals(attr, fieldMap[14][14]);

        // Remove flag
        WorldState.setFieldFlag(fieldMap, 10, 10, 5, 5, false, attr);
        assertEquals(0, fieldMap[10][10]);
    }

    @Test
    void testSetWallLine() {
        WorldState mapData = SimYukkuri.world.getCurrentWorldState();
        int[][] wallMap = mapData.getWallGrid(); // Use wallMap for wall line
        WorldState.clearGrid(wallMap);

        int attr = 2;
        // setWallLine(int[][] map, int x1, int y1, int x2, int y2, boolean setFlag, int
        // attribute)
        // Implementation: map[x][y] = ... uses the passed map argument!
        WorldState.setWallLine(wallMap, 0, 0, 0, 10, true, attr);

        assertEquals(attr, wallMap[0][0]);
        assertEquals(attr, wallMap[0][5]);
        assertEquals(attr, wallMap[0][10]);

        // Remove
        WorldState.setWallLine(wallMap, 0, 0, 0, 10, false, attr);
        assertEquals(0, wallMap[0][5]);
    }

    @Test
    void testGetters() {
        WorldState data = new WorldState(1);
        assertEquals(1, data.getWorldIndex());
        assertNotNull(data.getYukkuriRegistry());
        assertNotNull(data.getShit());
    }

    // --- setWorldIndex ---

    @Test
    void testSetGetMapIndex() {
        WorldState data = new WorldState(0);
        data.setWorldIndex(5);
        assertEquals(5, data.getWorldIndex());
    }

    // --- alarm ---

    @Test
    void testSetGetAlarm() {
        WorldState data = new WorldState(0);
        data.setAlarm(true);
        assertTrue(data.isAlarm());
        data.setAlarm(false);
        assertFalse(data.isAlarm());
    }

    // --- alarmPeriod ---

    @Test
    void testSetGetAlarmPeriod() {
        WorldState data = new WorldState(0);
        data.setAlarmPeriod(3000);
        assertEquals(3000, data.getAlarmPeriod());
    }

    // --- body ---

    @Test
    void testSetGetBody() {
        WorldState data = new WorldState(0);
        Map<Integer, Yukkuri> m = new HashMap<>();
        data.setYukkuriRegistry(m);
        assertEquals(m, data.getYukkuriRegistry());
    }

    // --- shit ---

    @Test
    void testSetGetShit() {
        WorldState data = new WorldState(0);
        Map<Integer, Shit> m = new HashMap<>();
        data.setShit(m);
        assertEquals(m, data.getShit());
    }

    // --- vomit ---

    @Test
    void testSetGetVomit() {
        WorldState data = new WorldState(0);
        Map<Integer, Vomit> m = new HashMap<>();
        data.setVomit(m);
        assertEquals(m, data.getVomit());
    }

    // --- barrier ---

    @Test
    void testSetGetBarrier() {
        WorldState data = new WorldState(0);
        List<Barrier> m = new LinkedList<>();
        data.setBarriers(m);
        assertEquals(m, data.getBarriers());
    }

    // --- event ---

    @Test
    void testSetGetEvent() {
        WorldState data = new WorldState(0);
        List<EventPacket> m = new LinkedList<>();
        data.setEvents(m);
        assertEquals(m, data.getEvents());
    }

    // --- sortEffect / frontEffect ---

    @Test
    void testSetGetSortEffect() {
        WorldState data = new WorldState(0);
        Map<Integer, Effect> m = new HashMap<>();
        data.setSortedEffects(m);
        assertEquals(m, data.getSortedEffects());
    }

    @Test
    void testSetGetFrontEffect() {
        WorldState data = new WorldState(0);
        Map<Integer, Effect> m = new HashMap<>();
        data.setFrontEffects(m);
        assertEquals(m, data.getFrontEffects());
    }

    // --- food / takenOutFood / takenOutShit ---

    @Test
    void testSetGetFood() {
        WorldState data = new WorldState(0);
        Map<Integer, Food> m = new HashMap<>();
        data.setFoods(m);
        assertEquals(m, data.getFoods());
    }

    @Test
    void testSetGetTakenOutFood() {
        WorldState data = new WorldState(0);
        Map<Integer, Food> m = new HashMap<>();
        data.setTakenOutFoods(m);
        assertEquals(m, data.getTakenOutFoods());
    }

    @Test
    void testSetGetTakenOutShit() {
        WorldState data = new WorldState(0);
        Map<Integer, Shit> m = new HashMap<>();
        data.setTakenOutShits(m);
        assertEquals(m, data.getTakenOutShits());
    }

    // --- toilet / bed / toy / stone / trampoline ---

    @Test
    void testSetGetToilet() {
        WorldState data = new WorldState(0);
        Map<Integer, Toilet> m = new HashMap<>();
        data.setToilets(m);
        assertEquals(m, data.getToilets());
    }

    @Test
    void testSetGetBed() {
        WorldState data = new WorldState(0);
        Map<Integer, Bed> m = new HashMap<>();
        data.setBeds(m);
        assertEquals(m, data.getBeds());
    }

    @Test
    void testSetGetToy() {
        WorldState data = new WorldState(0);
        Map<Integer, Toy> m = new HashMap<>();
        data.setToys(m);
        assertEquals(m, data.getToys());
    }

    @Test
    void testSetGetStone() {
        WorldState data = new WorldState(0);
        Map<Integer, Stone> m = new HashMap<>();
        data.setStones(m);
        assertEquals(m, data.getStones());
    }

    @Test
    void testSetGetTrampoline() {
        WorldState data = new WorldState(0);
        Map<Integer, Trampoline> m = new HashMap<>();
        data.setTrampolines(m);
        assertEquals(m, data.getTrampolines());
    }

    // --- breedingPool / garbageChute / foodMaker / orangePool / productChute ---

    @Test
    void testSetGetBreedingPool() {
        WorldState data = new WorldState(0);
        Map<Integer, BreedingPool> m = new HashMap<>();
        data.setBreedingPools(m);
        assertEquals(m, data.getBreedingPools());
    }

    @Test
    void testSetGetGarbageChute() {
        WorldState data = new WorldState(0);
        Map<Integer, GarbageChute> m = new HashMap<>();
        data.setGarbageChutes(m);
        assertEquals(m, data.getGarbageChutes());
    }

    @Test
    void testSetGetFoodMaker() {
        WorldState data = new WorldState(0);
        Map<Integer, FoodMaker> m = new HashMap<>();
        data.setFoodMakers(m);
        assertEquals(m, data.getFoodMakers());
    }

    @Test
    void testSetGetOrangePool() {
        WorldState data = new WorldState(0);
        Map<Integer, OrangePool> m = new HashMap<>();
        data.setOrangePools(m);
        assertEquals(m, data.getOrangePools());
    }

    @Test
    void testSetGetProductChute() {
        WorldState data = new WorldState(0);
        Map<Integer, ProductChute> m = new HashMap<>();
        data.setProductChutes(m);
        assertEquals(m, data.getProductChutes());
    }

    // --- stickyPlate / hotPlate / processorPlate / mixer / autoFeeder /
    // machinePress ---

    @Test
    void testSetGetStickyPlate() {
        WorldState data = new WorldState(0);
        Map<Integer, StickyPlate> m = new HashMap<>();
        data.setStickyPlates(m);
        assertEquals(m, data.getStickyPlates());
    }

    @Test
    void testSetGetHotPlate() {
        WorldState data = new WorldState(0);
        Map<Integer, HotPlate> m = new HashMap<>();
        data.setHotPlates(m);
        assertEquals(m, data.getHotPlates());
    }

    @Test
    void testSetGetProcessorPlate() {
        WorldState data = new WorldState(0);
        Map<Integer, ProcessorPlate> m = new HashMap<>();
        data.setProcessorPlates(m);
        assertEquals(m, data.getProcessorPlates());
    }

    @Test
    void testSetGetMixer() {
        WorldState data = new WorldState(0);
        Map<Integer, Mixer> m = new HashMap<>();
        data.setMixers(m);
        assertEquals(m, data.getMixers());
    }

    @Test
    void testSetGetAutoFeeder() {
        WorldState data = new WorldState(0);
        Map<Integer, AutoFeeder> m = new HashMap<>();
        data.setAutoFeeders(m);
        assertEquals(m, data.getAutoFeeders());
    }

    @Test
    void testSetGetMachinePress() {
        WorldState data = new WorldState(0);
        Map<Integer, MachinePress> m = new HashMap<>();
        data.setMachinePresses(m);
        assertEquals(m, data.getMachinePresses());
    }

    // --- stalk / diffuser / yunba / sui / trash / garbageStation / house /
    // beltconveyorObj ---

    @Test
    void testSetGetStalk() {
        WorldState data = new WorldState(0);
        Map<Integer, Stalk> m = new HashMap<>();
        data.setStalks(m);
        assertEquals(m, data.getStalks());
    }

    @Test
    void testSetGetDiffuser() {
        WorldState data = new WorldState(0);
        Map<Integer, Diffuser> m = new HashMap<>();
        data.setDiffusers(m);
        assertEquals(m, data.getDiffusers());
    }

    @Test
    void testSetGetYunba() {
        WorldState data = new WorldState(0);
        Map<Integer, Yunba> m = new HashMap<>();
        data.setYunbas(m);
        assertEquals(m, data.getYunbas());
    }

    @Test
    void testSetGetSui() {
        WorldState data = new WorldState(0);
        Map<Integer, Sui> m = new HashMap<>();
        data.setSuis(m);
        assertEquals(m, data.getSuis());
    }

    @Test
    void testSetGetTrash() {
        WorldState data = new WorldState(0);
        Map<Integer, Trash> m = new HashMap<>();
        data.setTrashObjects(m);
        assertEquals(m, data.getTrashObjects());
    }

    @Test
    void testSetGetGarbageStation() {
        WorldState data = new WorldState(0);
        Map<Integer, GarbageStation> m = new HashMap<>();
        data.setGarbageStations(m);
        assertEquals(m, data.getGarbageStations());
    }

    @Test
    void testSetGetHouse() {
        WorldState data = new WorldState(0);
        Map<Integer, House> m = new HashMap<>();
        data.setHouses(m);
        assertEquals(m, data.getHouses());
    }

    @Test
    void testSetGetBeltconveyorObj() {
        WorldState data = new WorldState(0);
        Map<Integer, BeltconveyorObj> m = new HashMap<>();
        data.setBeltconveyorObjects(m);
        assertEquals(m, data.getBeltconveyorObjects());
    }

    // --- beltconveyor / pool / farm / okazari ---

    @Test
    void testSetGetBeltconveyor() {
        WorldState data = new WorldState(0);
        List<Beltconveyor> m = new LinkedList<>();
        data.setBeltconveyors(m);
        assertEquals(m, data.getBeltconveyors());
    }

    @Test
    void testSetGetPool() {
        WorldState data = new WorldState(0);
        List<Pool> m = new LinkedList<>();
        data.setPools(m);
        assertEquals(m, data.getPools());
    }

    @Test
    void testSetGetFarm() {
        WorldState data = new WorldState(0);
        List<Farm> m = new LinkedList<>();
        data.setFarms(m);
        assertEquals(m, data.getFarms());
    }

    @Test
    void testSetGetOkazari() {
        WorldState data = new WorldState(0);
        Map<Integer, Okazari> m = new HashMap<>();
        data.setOkazaris(m);
        assertEquals(m, data.getOkazaris());
    }

    // --- hasDos ---

    @Test
    void testSetGetHasDos() {
        WorldState data = new WorldState(0);
        data.setHasDos(true);
        assertTrue(data.isHasDos());
        data.setHasDos(false);
        assertFalse(data.isHasDos());
    }

    // --- wallMap / fieldMap ---

    @Test
    void testSetGetWallMap() {
        WorldState data = new WorldState(0);
        int[][] m = new int[10][10];
        data.setWallGrid(m);
        assertEquals(m, data.getWallGrid());
    }

    @Test
    void testSetGetFieldMap() {
        WorldState data = new WorldState(0);
        int[][] m = new int[10][10];
        data.setFieldGrid(m);
        assertEquals(m, data.getFieldGrid());
    }

    @Nested
    class RegressionScenarios {

        @Test
        void testScenario_SetFiledFlagWritesToCurrentWorldFieldMapNotPassedArray() {
            WorldState mapData = SimYukkuri.world.getCurrentWorldState();
            int[][] actualFieldMap = mapData.getFieldGrid();
            int[][] dummyMap = new int[actualFieldMap.length][actualFieldMap[0].length];
            WorldState.clearGrid(actualFieldMap);
            WorldState.clearGrid(dummyMap);

            WorldState.setFieldFlag(dummyMap, 10, 10, 2, 2, true, 4);

            assertEquals(4, actualFieldMap[10][10]);
            assertEquals(4, actualFieldMap[11][11]);
            assertEquals(0, dummyMap[10][10]);
        }

        @Test
        void testScenario_SetWallLineMarksPrimaryAndAdjacentCellsThenClearsThem() {
            WorldState mapData = SimYukkuri.world.getCurrentWorldState();
            int[][] wallMap = mapData.getWallGrid();
            WorldState.clearGrid(wallMap);

            WorldState.setWallLine(wallMap, 10, 10, 10, 12, true, 2);

            assertEquals(2, wallMap[10][10]);
            assertEquals(2, wallMap[11][10]);
            assertEquals(2, wallMap[10][11]);
            assertEquals(2, wallMap[10][12]);

            WorldState.setWallLine(wallMap, 10, 10, 10, 12, false, 2);

            assertEquals(0, wallMap[10][10]);
            assertEquals(0, wallMap[11][10]);
            assertEquals(0, wallMap[10][11]);
            assertEquals(0, wallMap[10][12]);
        }
    }
}
