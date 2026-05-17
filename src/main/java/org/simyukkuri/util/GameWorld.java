package org.simyukkuri.util;

import org.simyukkuri.SimYukkuri;
import org.simyukkuri.engine.World;
import org.simyukkuri.entity.core.meta.Player;
import org.simyukkuri.system.WorldState;

/**
 * GameWorld.
 */
public final class GameWorld {
	private static WorldSource override;

	private GameWorld() {
	}

	/** ワールドインスタンスを返す。 */
	public static World get() {
		if (override != null) {
			return override.getWorld();
		}
		return SimYukkuri.world;
	}

	/** ワールドインスタンスをセットする。 */
	public static void set(World world) {
		SimYukkuri.world = world;
	}

	/** 現在のワールド状態を返す。 */
	public static WorldState getCurrentWorldState() {
		return get().getCurrentWorldState();
	}

	/** プレイヤーを返す。 */
	public static Player getPlayer() {
		return get().getPlayer();
	}

	/** テスト用の注入ソースをセットする。 */
	public static void setOverride(WorldSource source) {
		override = source;
	}

	/** テスト用注入ソースをクリアする。 */
	public static void clearOverride() {
		override = null;
	}
}
