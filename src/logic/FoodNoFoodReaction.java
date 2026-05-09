package src.logic;

import src.base.Yukkuri;
import src.enums.Happiness;
import src.system.MessagePool;
import src.util.GameMessages;
import src.util.GameRandom;

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
		if (!body.isNotNYD()) {
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
