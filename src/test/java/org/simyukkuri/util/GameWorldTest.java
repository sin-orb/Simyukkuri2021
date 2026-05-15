package org.simyukkuri.util;

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

import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import org.simyukkuri.SimYukkuri;
import org.simyukkuri.engine.World;

public class GameWorldTest {

	@AfterEach
	public void tearDown() {
		GameWorld.clearOverride();
		SimYukkuri.world = null;
	}

	@Test
	public void testGetDelegatesToSimYukkuriWorldByDefault() {
		World world = new World();
		SimYukkuri.world = world;

		assertSame(world, GameWorld.get());
	}

	@Test
	public void testSetUpdatesSimYukkuriWorld() {
		World world = new World();

		GameWorld.set(world);

		assertSame(world, SimYukkuri.world);
		assertSame(world, GameWorld.get());
	}

	@Test
	public void testGetUsesOverrideWhenSet() {
		final World defaultWorld = new World();
		final World overrideWorld = new World();
		SimYukkuri.world = defaultWorld;

		GameWorld.setOverride(new WorldSource() {
			@Override
			public World getWorld() {
				return overrideWorld;
			}
		});

		assertSame(overrideWorld, GameWorld.get());
		assertSame(defaultWorld, SimYukkuri.world);
	}
}
