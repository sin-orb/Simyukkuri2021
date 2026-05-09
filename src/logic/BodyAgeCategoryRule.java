package src.logic;

import src.base.BodyAttributes;
import src.enums.AgeState;

/**
 * Bodyの年齢区分判定を集約する.
 */
public final class BodyAgeCategoryRule {
	private BodyAgeCategoryRule() {
	}

	public static boolean isAdult(BodyAttributes body) {
		return body.getBodyAgeState() == AgeState.ADULT;
	}

	public static boolean isChild(BodyAttributes body) {
		return body.getBodyAgeState() == AgeState.CHILD;
	}

	public static boolean isBaby(BodyAttributes body) {
		return body.getBodyAgeState() == AgeState.BABY;
	}
}
