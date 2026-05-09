package src.logic;

import src.base.BodyAttributes;

/**
 * Bodyの基本状態フラグを集約する.
 */
public final class BodyConditionRule {
	private BodyConditionRule() {
	}

	public static boolean isDead(BodyAttributes body) {
		return body.isDeadRaw();
	}

	public static boolean isFirstGround(BodyAttributes body) {
		return body.isFirstGroundRaw();
	}

	public static boolean isCrushed(BodyAttributes body) {
		return body.isCrushedRaw();
	}

	public static boolean isBurned(BodyAttributes body) {
		return body.isBurnedRaw();
	}

	public static boolean isRelax(BodyAttributes body) {
		return body.isRelaxRaw();
	}

	public static boolean isNightmare(BodyAttributes body) {
		return body.isNightmareRaw();
	}

	public static boolean isRapist(BodyAttributes body) {
		return body.isRapistRaw();
	}

	public static boolean isSuperRapist(BodyAttributes body) {
		return body.isSuperRapistRaw();
	}

	public static boolean isWet(BodyAttributes body) {
		return body.isWetRaw();
	}

	public static boolean isMelt(BodyAttributes body) {
		return body.isMeltRaw();
	}

	public static boolean isPealed(BodyAttributes body) {
		return body.isPealedRaw();
	}

	public static boolean isPacked(BodyAttributes body) {
		return body.isPackedRaw();
	}

	public static boolean isBlind(BodyAttributes body) {
		return body.isBlindRaw();
	}

	public static boolean isFatherRaper(BodyAttributes body) {
		return body.isFatherRaperRaw();
	}
}
