package src.logic;

import src.base.BodyAttributes;
import src.enums.Pain;

/**
 * Bodyの行動・感覚状態の単純判定を集約する.
 */
public final class BodyActionStateRule {
	private BodyActionStateRule() {
	}

	public static boolean isBeggingForLife(BodyAttributes body) {
		return !body.isDead() && body.isBeggingRaw();
	}

	public static boolean isStrike(BodyAttributes body) {
		return !body.isDead() && body.isStrikeRaw();
	}

	public static boolean isFeelPain(BodyAttributes body) {
		return body.getPainState() == Pain.VERY || body.getPainState() == Pain.SOME;
	}

	public static boolean isFeelHardPain(BodyAttributes body) {
		return body.getPainState() == Pain.VERY;
	}

	public static boolean isBirth(BodyAttributes body) {
		return !body.isDead() && body.isBirthRaw();
	}

	public static boolean isNewborn(BodyAttributes body) {
		return body.isNewbornRaw();
	}

	public static boolean isEating(BodyAttributes body) {
		return !body.isDead() && body.isEatingRaw();
	}

	public static boolean isEatingShit(BodyAttributes body) {
		return !body.isDead() && body.isEatingShitRaw();
	}

	public static boolean isSukkiri(BodyAttributes body) {
		return !body.isDead() && body.isSukkiriRaw();
	}

	public static boolean isNeedled(BodyAttributes body) {
		return !body.isDead() && body.isNeedledRaw();
	}
}
