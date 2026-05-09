package src.logic;

import src.base.BodyAttributes;
import src.enums.Damage;

/**
 * Bodyの空腹進行に関する判定を集約する.
 */
public final class BodyHungerStateRule {
	private BodyHungerStateRule() {
	}

	/**
	 * お腹いっぱい気味かどうかを判定する.
	 */
	public static boolean isFull(BodyAttributes body) {
		return !body.isDead() && body.getHungry() >= body.getHungryLimit() * 0.8f;
	}

	/**
	 * お腹が減ってきているかどうかを判定する.
	 */
	public static boolean isHungry(BodyAttributes body) {
		return !body.isDead() && body.getHungry() <= body.getHungryLimit() / 2;
	}

	/**
	 * お腹減り気味かどうかを判定する.
	 */
	public static boolean isSoHungry(BodyAttributes body) {
		return !body.isDead() && body.getHungry() <= body.getHungryLimit() * 0.2f;
	}

	/**
	 * お腹が完全に減っているかどうかを判定する.
	 */
	public static boolean isVeryHungry(BodyAttributes body) {
		return !body.isDead() && body.getHungry() <= 0;
	}

	/**
	 * 空腹によるダメージが発生しているかどうかを判定する.
	 */
	public static boolean isTooHungry(BodyAttributes body) {
		return !body.isDead() && body.getHungry() <= 0 && body.getDamageState() != Damage.NONE;
	}

	/**
	 * 餓死寸前かどうかを判定する.
	 */
	public static boolean isStarving(BodyAttributes body) {
		return !body.isDead() && body.getHungry() <= 0 && body.getDamageState() == Damage.TOOMUCH;
	}
}
