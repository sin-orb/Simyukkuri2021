package src.logic;

import src.base.BodyAttributes;

/**
 * 既定の種別判定をまとめる窓口クラス。
 */
public final class BodySpecialTypeRule {
	private BodySpecialTypeRule() {
	}

	public static boolean isIdiot(BodyAttributes body) {
		return false;
	}

	public static boolean isHybrid(BodyAttributes body) {
		return false;
	}
}
