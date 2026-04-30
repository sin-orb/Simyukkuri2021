package src.logic;

import src.base.Body;
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
	public static void handleNoFoodFound(Body b) {
		if (!b.isNotNYD()) {
			return;
		}
		if (!b.isSoHungry() || !b.isLockmove()) {
			return;
		}
		b.setToFood(false);
		if (!b.isTalking() && (GameRandom.nextInt(20) == 0)) {
			b.setMessage(GameMessages.getMessage(b, MessagePool.Action.NoFood), false);
			b.stay();
		}
		b.setHappiness(Happiness.SAD);
	}
}
