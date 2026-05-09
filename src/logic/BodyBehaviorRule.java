package src.logic;

import src.base.BodyAttributes;
import src.base.BodyBehaviorProfile;

/**
 * Bodyの行動・繁殖系の設定判定を集約する.
 */
public final class BodyBehaviorRule {
	private BodyBehaviorRule() {
	}

	private static BodyBehaviorProfile behavior(BodyAttributes body) {
		return body.getBodyBehaviorProfileRaw();
	}

	public static int getBraidBreakChance(BodyAttributes body) {
		return behavior(body).getBraidBreakChance();
	}

	public static void setBraidBreakChance(BodyAttributes body, int value) {
		behavior(body).setBraidBreakChance(value);
	}

	public static int getSurisuriAccidentProb(BodyAttributes body) {
		return behavior(body).getSurisuriAccidentProb();
	}

	public static void setSurisuriAccidentProb(BodyAttributes body, int value) {
		behavior(body).setSurisuriAccidentProb(value);
	}

	public static int getCarAccidentProb(BodyAttributes body) {
		return behavior(body).getCarAccidentProb();
	}

	public static void setCarAccidentProb(BodyAttributes body, int value) {
		behavior(body).setCarAccidentProb(value);
	}

	public static int getBreakBodyByShitProb(BodyAttributes body) {
		return behavior(body).getBreakBodyByShitProb();
	}

	public static void setBreakBodyByShitProb(BodyAttributes body, int value) {
		behavior(body).setBreakBodyByShitProb(value);
	}

	public static int getDiarrheaProb(BodyAttributes body) {
		return behavior(body).getDiarrheaProb();
	}

	public static void setDiarrheaProb(BodyAttributes body, int value) {
		behavior(body).setDiarrheaProb(value);
	}

	public static int getExciteProb(BodyAttributes body) {
		return behavior(body).getExciteProb();
	}

	public static void setExciteProb(BodyAttributes body, int value) {
		behavior(body).setExciteProb(value);
	}

	public static int getImmunityStrength(BodyAttributes body) {
		return behavior(body).getImmunityStrength();
	}

	public static void setImmunityStrength(BodyAttributes body, int value) {
		behavior(body).setImmunityStrength(value);
	}

	public static int getAttitudePoint(BodyAttributes body) {
		return behavior(body).getAttitudePoint();
	}

	public static void setAttitudePoint(BodyAttributes body, int value) {
		behavior(body).setAttitudePoint(value);
	}

	public static int getPregnantLimit(BodyAttributes body) {
		return behavior(body).getPregnantLimit();
	}

	public static void setPregnantLimit(BodyAttributes body, int value) {
		behavior(body).setPregnantLimit(value);
	}

	public static boolean isNotChangeCharacter(BodyAttributes body) {
		return body.isNotChangeCharacterRaw();
	}

	public static boolean isUseRealPregnantLimit(BodyAttributes body) {
		return body.isUseRealPregnantLimitRaw();
	}
}
