package src.logic;

import src.base.BodyAttributes;

/**
 * Bodyの外観・繁殖・去勢に関する単純判定を集約する.
 */
public final class BodyStructureRule {
	private BodyStructureRule() {
	}

	public static boolean isHasBraid(BodyAttributes body) {
		return body.isHasBraidRaw();
	}

	public static boolean isHasPants(BodyAttributes body) {
		return body.isHasPantsRaw();
	}

	public static boolean isHasBaby(BodyAttributes body) {
		return body.isHasBabyRaw();
	}

	public static boolean isHasStalk(BodyAttributes body) {
		return body.isHasStalkRaw();
	}

	public static boolean isAnalClose(BodyAttributes body) {
		return body.isAnalCloseRaw();
	}

	public static boolean isBodyCastration(BodyAttributes body) {
		return body.isBodyCastrationRaw();
	}

	public static boolean isStalkCastration(BodyAttributes body) {
		return body.isStalkCastrationRaw();
	}
}
