package src.logic;

import src.base.BodyAttributes;

/**
 * 体調と破裂状態の判定をまとめる窓口クラス。
 */
public final class BodyDamageRule {
	private BodyDamageRule() {
	}

	public static boolean isNoDamaged(BodyAttributes body) {
		return BodyVitals.isNoDamaged(body);
	}

	public static boolean isDamagedLightly(BodyAttributes body) {
		return BodyVitals.isDamagedLightly(body);
	}

	public static boolean isDamaged(BodyAttributes body) {
		return BodyVitals.isDamaged(body);
	}

	public static boolean isDamagedHeavily(BodyAttributes body) {
		return BodyVitals.isDamagedHeavily(body);
	}

	public static boolean isCantDie(BodyAttributes body) {
		return BodyBurstRule.isCantDie(body);
	}

	public static boolean isBurst(BodyAttributes body) {
		return BodyBurstRule.isBurst(body);
	}

	public static boolean isAboutToBurst(BodyAttributes body) {
		return BodyBurstRule.isAboutToBurst(body);
	}

	public static boolean isInfration(BodyAttributes body) {
		return BodyBurstRule.isInfration(body);
	}
}
