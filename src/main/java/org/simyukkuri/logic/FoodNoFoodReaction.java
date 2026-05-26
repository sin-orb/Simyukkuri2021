package org.simyukkuri.logic;

import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.enums.Happiness;
import org.simyukkuri.system.MessagePool;
import org.simyukkuri.util.GameMessages;
import org.simyukkuri.util.GameRandom;

/**
 * 食べ物が見つからなかったときの反応.
 */
public final class FoodNoFoodReaction {

	private FoodNoFoodReaction() {
	}

	/**
	 * 空振り時の気分・メッセージ更新を行う.
	 */
	public static void handleNoFoodFound(Yukkuri body) {
		if (!body.isNotNyd()) {
			return;
		}
		if (!body.isSoHungry() || !body.isLockmove()) {
			return;
		}
		body.setToFood(false);
		if (!body.isTalking() && (GameRandom.nextInt(20) == 0)) {
			body.setMessage(GameMessages.getMessage(body, MessagePool.Action.NoFood), false);
			body.stay();
		}
		body.setHappiness(Happiness.SAD);
	}
}
