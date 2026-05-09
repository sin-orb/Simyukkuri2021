package src.logic;

import src.base.BodyAttributes;

/**
 * Bodyの行動・繁殖系の設定判定を集約する.
 */
public final class BodyBehaviorRule {
	private BodyBehaviorRule() {
	}

	public static int getBraidBreakChance(BodyAttributes body) {
		return body.getBraidBreakChance();
	}

	public static void setBraidBreakChance(BodyAttributes body, int value) {
		body.setBraidBreakChance(value);
	}

	public static int getSurisuriAccidentProb(BodyAttributes body) {
		return body.getSurisuriAccidentProb();
	}

	public static void setSurisuriAccidentProb(BodyAttributes body, int value) {
		body.setSurisuriAccidentProb(value);
	}

	public static int getCarAccidentProb(BodyAttributes body) {
		return body.getCarAccidentProb();
	}

	public static void setCarAccidentProb(BodyAttributes body, int value) {
		body.setCarAccidentProb(value);
	}

	public static int getBreakBodyByShitProb(BodyAttributes body) {
		return body.getBreakBodyByShitProb();
	}

	public static void setBreakBodyByShitProb(BodyAttributes body, int value) {
		body.setBreakBodyByShitProb(value);
	}

	public static int getDiarrheaProb(BodyAttributes body) {
		return body.getDiarrheaProb();
	}

	public static void setDiarrheaProb(BodyAttributes body, int value) {
		body.setDiarrheaProb(value);
	}

	public static int getExciteProb(BodyAttributes body) {
		return body.getExciteProb();
	}

	public static void setExciteProb(BodyAttributes body, int value) {
		body.setExciteProb(value);
	}

	public static int getImmunityStrength(BodyAttributes body) {
		return body.getImmunityStrength();
	}

	public static void setImmunityStrength(BodyAttributes body, int value) {
		body.setImmunityStrength(value);
	}

	public static int getAttitudePoint(BodyAttributes body) {
		return body.getAttitudePoint();
	}

	public static void setAttitudePoint(BodyAttributes body, int value) {
		body.setAttitudePoint(value);
	}

	public static int getPregnantLimit(BodyAttributes body) {
		return body.getPregnantLimit();
	}

	public static void setPregnantLimit(BodyAttributes body, int value) {
		body.setPregnantLimit(value);
	}

	public static boolean isNotChangeCharacter(BodyAttributes body) {
		return body.isNotChangeCharacter();
	}

	public static boolean isUseRealPregnantLimit(BodyAttributes body) {
		return body.isUseRealPregnantLimit();
	}
}
