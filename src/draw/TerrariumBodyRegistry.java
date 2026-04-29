package src.draw;

import src.base.Body;
import src.util.GameWorld;

/**
 * Terrarium から切り出した body の登録補助。
 */
public final class TerrariumBodyRegistry {
	private TerrariumBodyRegistry() {
	}

	/**
	 * 生成済み body を現在マップへ登録する。
	 *
	 * @param body 登録する body
	 */
	public static void register(Body body) {
		GameWorld.get().getCurrentMap().getBody().put(body.getUniqueID(), body);
	}
}
