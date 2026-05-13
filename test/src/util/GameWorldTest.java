package src.util;

import src.entity.core.Entity;
import src.entity.core.attachment.*;
import src.entity.core.attachment.impl.*;
import src.entity.core.effect.*;
import src.entity.core.effect.impl.*;
import src.entity.core.living.yukkuri.Dna;
import src.entity.core.living.yukkuri.Yukkuri;
import src.entity.core.living.yukkuri.impl.*;
import src.entity.core.world.bodylinked.*;
import src.entity.core.world.item.*;
import src.entity.core.world.mobile.*;

import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import src.SimYukkuri;
import src.draw.World;

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
