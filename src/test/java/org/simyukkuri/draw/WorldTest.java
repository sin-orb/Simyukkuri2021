package org.simyukkuri.draw;

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

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import org.simyukkuri.SimYukkuri;
import org.simyukkuri.engine.ModLoader;
import org.simyukkuri.engine.World;
import org.simyukkuri.system.WorldState;
import org.simyukkuri.ui.WorldSelectionWindow;

public class WorldTest {

    @BeforeAll
    public static void setUpClass() {
        System.setProperty("java.awt.headless", "true");
    }

    @Test
    public void testWorldConstructor() {
        World world = new World();
        assertNotNull(world.getPlayer());
        assertNotNull(world.getWorldStates());
        assertEquals(WorldSelectionWindow.WorldSelection.values().length, world.getWorldStates().size());
        assertEquals(0, world.getCurrentWorldStateIndex());
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
        WorldState map = world.getCurrentWorldState();
        assertNotNull(map);
        assertEquals(0, map.getWorldIndex());
    }

    @Test
    public void testSetCurrentMapIdx() {
        World world = new World();
        world.setCurrentWorldStateIndex(1);
        assertEquals(1, world.getCurrentWorldStateIndex());
        assertEquals(1, world.getCurrentWorldState().getWorldIndex());
    }

    @Test
    public void testNextMap() {
        World world = new World();
        assertEquals(-1, world.getNextWorldStateIndex());
        world.setNextWorldStateIndex(2);
        assertEquals(2, world.getNextWorldStateIndex());
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
