package src.logic;

import src.base.BodyAttributes;

/**
 * Bodyの空腹・満腹に関する単純判定を集約する.
 */
public final class BodyHungerRule {
	private BodyHungerRule() {
	}

	/**
	 * 食べ過ぎかどうかを判定する.
	 *
	 * @param body 判定対象
	 * @return 食べ過ぎならtrue
	 */
	public static boolean isOverEating(BodyAttributes body) {
		return !body.isDead() && (body.getHungry() >= body.getHungryLimit() * 1.3f);
	}

	/**
	 * お腹いっぱいかどうかを判定する.
	 *
	 * @param body 判定対象
	 * @return お腹いっぱいならtrue
	 */
	public static boolean isTooFull(BodyAttributes body) {
		return !body.isDead() && body.getHungry() >= body.getHungryLimit();
	}
}
