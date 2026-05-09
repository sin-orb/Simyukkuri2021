package src.logic;

import src.base.BodyAttributes;
import src.enums.Burst;

/**
 * Bodyの破裂・死ねない期間判定を集約する.
 */
public final class BodyBurstRule {
	private BodyBurstRule() {
	}

	public static boolean isCantDie(BodyAttributes body) {
		return body.getCantDiePeriod() > 0;
	}

	public static boolean isBurst(BodyAttributes body) {
		return body.getBurstState() == Burst.BURST;
	}

	public static boolean isAboutToBurst(BodyAttributes body) {
		return body.getBurstState() == Burst.NEAR;
	}

	public static boolean isInfration(BodyAttributes body) {
		return body.getBurstState() != Burst.NONE;
	}
}
