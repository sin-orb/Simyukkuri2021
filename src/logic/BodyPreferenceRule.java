package src.logic;

import src.base.BodyAttributes;
import src.base.BodyBehaviorProfile;
import src.base.BodyStatProfile;

/**
 * Bodyの好み・設定系の値を集約する.
 */
public final class BodyPreferenceRule {
	private BodyPreferenceRule() {
	}

	private static BodyBehaviorProfile behavior(BodyAttributes body) {
		return body.getBodyBehaviorProfileRaw();
	}

	private static BodyStatProfile stat(BodyAttributes body) {
		return body.getBodyStatProfileRaw();
	}

	public static int getLovePlayerLimitBase(BodyAttributes body) {
		return behavior(body).getLovePlayerLimitBase();
	}

	public static void setLovePlayerLimitBase(BodyAttributes body, int value) {
		behavior(body).setLovePlayerLimitBase(value);
	}

	public static int[] getCleaningFailProbWise(BodyAttributes body) {
		return stat(body).getCleaningFailProbWise();
	}

	public static void setCleaningFailProbWise(BodyAttributes body, int[] value) {
		stat(body).setCleaningFailProbWise(value);
	}

	public static int[] getCleaningFailProbAverage(BodyAttributes body) {
		return stat(body).getCleaningFailProbAverage();
	}

	public static void setCleaningFailProbAverage(BodyAttributes body, int[] value) {
		stat(body).setCleaningFailProbAverage(value);
	}

	public static int[] getCleaningFailProbFool(BodyAttributes body) {
		return stat(body).getCleaningFailProbFool();
	}

	public static void setCleaningFailProbFool(BodyAttributes body, int[] value) {
		stat(body).setCleaningFailProbFool(value);
	}

	public static int[] getNiceLimit(BodyAttributes body) {
		return stat(body).getNiceLimit();
	}

	public static void setNiceLimit(BodyAttributes body, int[] value) {
		stat(body).setNiceLimit(value);
	}

	public static int getSameDirectionFactor(BodyAttributes body) {
		return behavior(body).getSameDirectionFactor();
	}

	public static void setSameDirectionFactor(BodyAttributes body, int value) {
		behavior(body).setSameDirectionFactor(value);
	}

	public static boolean isNotChangeCharacter(BodyAttributes body) {
		return behavior(body).isNotChangeCharacter();
	}

	public static void setNotChangeCharacter(BodyAttributes body, boolean value) {
		behavior(body).setNotChangeCharacter(value);
	}

	public static boolean isUseRealPregnantLimit(BodyAttributes body) {
		return behavior(body).isUseRealPregnantLimit();
	}

	public static void setUseRealPregnantLimit(BodyAttributes body, boolean value) {
		behavior(body).setUseRealPregnantLimit(value);
	}
}
