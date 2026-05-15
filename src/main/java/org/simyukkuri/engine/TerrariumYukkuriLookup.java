package org.simyukkuri.engine;

import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.util.GameWorld;

/**
 * Terrarium から切り出した body の登録補助。
 */
public final class TerrariumYukkuriLookup {
	private TerrariumYukkuriLookup() {
	}

	/**
	 * 生成済み body を現在マップへ登録する。
	 *
	 * @param body 登録する body
	 */
	public static void register(Yukkuri body) {
		GameWorld.get().getCurrentWorldState().getYukkuriRegistry().put(body.getUniqueID(), body);
	}
}
