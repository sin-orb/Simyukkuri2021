package org.simyukkuri.util;

import org.simyukkuri.SimYukkuri;
import org.simyukkuri.draw.World;
import org.simyukkuri.entity.core.meta.Player;
import org.simyukkuri.system.MapPlaceData;

public final class GameWorld {
	private static WorldSource override;

	private GameWorld() {
	}

	public static World get() {
		if (override != null) {
			return override.getWorld();
		}
		return SimYukkuri.world;
	}

	public static void set(World world) {
		SimYukkuri.world = world;
	}

	public static MapPlaceData getCurrentMap() {
		return get().getCurrentMap();
	}

	public static Player getPlayer() {
		return get().getPlayer();
	}

	public static void setOverride(WorldSource source) {
		override = source;
	}

	public static void clearOverride() {
		override = null;
	}
}
