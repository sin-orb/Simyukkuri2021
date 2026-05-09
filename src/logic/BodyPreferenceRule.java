package src.logic;

import src.base.BodyAttributes;

/**
 * Bodyの好み・設定系の値を集約する.
 */
public final class BodyPreferenceRule {
	private BodyPreferenceRule() {
	}

	public static int getLovePlayerLimitBase(BodyAttributes body) {
		return body.getLovePlayerLimitBase();
	}

	public static void setLovePlayerLimitBase(BodyAttributes body, int value) {
		body.setLovePlayerLimitBase(value);
	}

	public static int[] getCleaningFailProbWise(BodyAttributes body) {
		return body.getCleaningFailProbWise();
	}

	public static void setCleaningFailProbWise(BodyAttributes body, int[] value) {
		body.setCleaningFailProbWise(value);
	}

	public static int[] getCleaningFailProbAverage(BodyAttributes body) {
		return body.getCleaningFailProbAverage();
	}

	public static void setCleaningFailProbAverage(BodyAttributes body, int[] value) {
		body.setCleaningFailProbAverage(value);
	}

	public static int[] getCleaningFailProbFool(BodyAttributes body) {
		return body.getCleaningFailProbFool();
	}

	public static void setCleaningFailProbFool(BodyAttributes body, int[] value) {
		body.setCleaningFailProbFool(value);
	}

	public static int[] getNiceLimit(BodyAttributes body) {
		return body.getNiceLimit();
	}

	public static void setNiceLimit(BodyAttributes body, int[] value) {
		body.setNiceLimit(value);
	}

	public static int getSameDirectionFactor(BodyAttributes body) {
		return body.getSameDirectionFactor();
	}

	public static void setSameDirectionFactor(BodyAttributes body, int value) {
		body.setSameDirectionFactor(value);
	}

	public static boolean isNotChangeCharacter(BodyAttributes body) {
		return body.isNotChangeCharacter();
	}

	public static void setNotChangeCharacter(BodyAttributes body, boolean value) {
		body.setNotChangeCharacter(value);
	}

	public static boolean isUseRealPregnantLimit(BodyAttributes body) {
		return body.isUseRealPregnantLimit();
	}

	public static void setUseRealPregnantLimit(BodyAttributes body, boolean value) {
		body.setUseRealPregnantLimit(value);
	}
}
