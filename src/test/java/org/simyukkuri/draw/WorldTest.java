package org.simyukkuri.draw;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.simyukkuri.SimYukkuri;
import org.simyukkuri.engine.World;
import org.simyukkuri.ui.WorldSelectionWindow;

public class WorldTest {

	@BeforeAll
	public static void setUpClass() {
		System.setProperty("java.awt.headless", "true");
	}

	@Test
	public void testDefaultConstructorInitializesWorldStates() {
		World world = new World();
		assertNotNull(world.getPlayer());
		assertNotNull(world.getWorldStates());
		assertEquals(WorldSelectionWindow.WorldSelection.values().length, world.getWorldStates().size());
		assertEquals(0, world.getCurrentWorldStateIndex());
		assertEquals(-1, world.getNextWorldStateIndex());
		assertEquals(0, world.getCurrentWorldState().getWorldIndex());
	}

	@Test
	public void testParameterizedConstructorRecalculatesWorldSize() {
		World world = new World(1, 2);
		int expectedScale = SimYukkuri.fieldScaleData[2];
		assertEquals(1, world.getWindowType());
		assertEquals(2, world.getTerrariumSizeIndex());
		assertEquals(expectedScale, Translate.getWorldScale());
		assertEquals(SimYukkuri.DEFAULT_MAP_X[1] * expectedScale / 100 + 1, Translate.getWorldWidth());
		assertEquals(SimYukkuri.DEFAULT_MAP_Y[1] * expectedScale / 100 + 1, Translate.getWorldHeight());
		assertEquals(SimYukkuri.DEFAULT_MAP_Z[1] * expectedScale / 100 + 1, Translate.getWorldDepth());
	}

	@Test
	public void testSetCurrentWorldStateIndexChangesCurrentStateImmediately() {
		World world = new World();
		world.setCurrentWorldStateIndex(1);
		assertEquals(1, world.getCurrentWorldStateIndex());
		assertEquals(1, world.getCurrentWorldState().getWorldIndex());
		world.setCurrentWorldStateIndex(0);
		assertEquals(0, world.getCurrentWorldStateIndex());
		assertEquals(0, world.getCurrentWorldState().getWorldIndex());
	}

	@Test
	public void testNextWorldStateIndexQueuesAndChangeWorldStateAppliesIt() {
		World world = new World();
		assertEquals(-1, world.getNextWorldStateIndex());
		world.setNextWorldStateIndex(2);
		assertEquals(2, world.getNextWorldStateIndex());
		assertEquals(0, world.getCurrentWorldStateIndex());
		assertEquals(0, world.getCurrentWorldState().getWorldIndex());

		assertEquals(2, world.changeWorldState().getWorldIndex());
		assertEquals(2, world.getCurrentWorldStateIndex());
		assertEquals(2, world.getCurrentWorldState().getWorldIndex());
		assertEquals(-1, world.getNextWorldStateIndex());
	}
}
