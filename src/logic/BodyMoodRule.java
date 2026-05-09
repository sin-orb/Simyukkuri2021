package src.logic;

import src.base.BodyAttributes;
import src.enums.Happiness;

/**
 * 感情系の単純判定を集約する.
 */
public final class BodyMoodRule {
	private BodyMoodRule() {
	}

	public static boolean isAngry(BodyAttributes body) {
		return !body.isDead() && body.isAngryRaw();
	}

	public static boolean isScare(BodyAttributes body) {
		return !body.isDead() && body.isScareRaw();
	}

	public static boolean isSad(BodyAttributes body) {
		return !body.isDead() && body.getHappiness() == Happiness.SAD;
	}

	public static boolean isVerySad(BodyAttributes body) {
		return !body.isDead() && body.getHappiness() == Happiness.VERY_SAD;
	}

	public static boolean isHappy(BodyAttributes body) {
		return !body.isDead() && (body.getHappiness() == Happiness.HAPPY || body.getHappiness() == Happiness.VERY_HAPPY);
	}

	public static boolean isUnhappy(BodyAttributes body) {
		return !body.isDead() && (body.getHappiness() == Happiness.SAD || body.getHappiness() == Happiness.VERY_SAD);
	}
}
