package src.draw;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import src.SimYukkuri;
import src.system.MapPlaceData;
import src.system.MapWindow;

public class WorldTest {

    @BeforeAll
    public static void setUpClass() {
        System.setProperty("java.awt.headless", "true");
    }

    @Test
    public void testWorldConstructor() {
        World world = new World();
        assertNotNull(world.getPlayer());
        assertNotNull(world.getMapList());
        assertEquals(MapWindow.MAP.values().length, world.getMapList().size());
        assertEquals(0, world.getCurrentMapIdx());
    }

    @Test
    public void testParameterizedConstructor() {
        World world = new World(1, 2);
        assertEquals(1, world.getWindowType());
        assertEquals(2, world.getTerrariumSizeIndex());
    }

    @Test
    public void testGetCurrentMap() {
        World world = new World();
        MapPlaceData map = world.getCurrentMap();
        assertNotNull(map);
        assertEquals(0, map.getMapIndex());
    }

    @Test
    public void testSetCurrentMapIdx() {
        World world = new World();
        world.setCurrentMapIdx(1);
        assertEquals(1, world.getCurrentMapIdx());
        assertEquals(1, world.getCurrentMap().getMapIndex());
    }

    @Test
    public void testNextMap() {
        World world = new World();
        assertEquals(-1, world.getNextMap());
        world.setNextMap(2);
        assertEquals(2, world.getNextMap());
    }

    @Test
    public void testGettersAndSetters() {
        World world = new World();
        world.setWindowType(10);
        world.setTerrariumSizeIndex(5);
        assertEquals(10, world.getWindowType());
        assertEquals(5, world.getTerrariumSizeIndex());
    }
}
