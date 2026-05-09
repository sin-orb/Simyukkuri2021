package src.logic;

import src.base.BodyAttributes;
import src.base.BodyTimingProfile;

/**
 * BodyAttributes の時刻・閾値系パラメータをまとめたルール.
 */
public final class BodyTimingRule {
	private BodyTimingRule() {
	}

	private static BodyTimingProfile timing(BodyAttributes body) {
		return body.getBodyTimingProfileRaw();
	}

	public static int getBabyLimitBase(BodyAttributes body) {
		return timing(body).getBabyLimitBase();
	}

	public static void setBabyLimitBase(BodyAttributes body, int value) {
		timing(body).setBabyLimitBase(value);
	}

	public static int getChildLimitBase(BodyAttributes body) {
		return timing(body).getChildLimitBase();
	}

	public static void setChildLimitBase(BodyAttributes body, int value) {
		timing(body).setChildLimitBase(value);
	}

	public static int getLifeLimitBase(BodyAttributes body) {
		return timing(body).getLifeLimitBase();
	}

	public static void setLifeLimitBase(BodyAttributes body, int value) {
		timing(body).setLifeLimitBase(value);
	}

	public static int getRottingTimeBase(BodyAttributes body) {
		return timing(body).getRottingTimeBase();
	}

	public static void setRottingTimeBase(BodyAttributes body, int value) {
		timing(body).setRottingTimeBase(value);
	}

	public static int getDeclinePeriodBase(BodyAttributes body) {
		return timing(body).getDeclinePeriodBase();
	}

	public static void setDeclinePeriodBase(BodyAttributes body, int value) {
		timing(body).setDeclinePeriodBase(value);
	}

	public static int getBlockedLimitBase(BodyAttributes body) {
		return timing(body).getBlockedLimitBase();
	}

	public static void setBlockedLimitBase(BodyAttributes body, int value) {
		timing(body).setBlockedLimitBase(value);
	}

	public static int getDirtyPeriodBase(BodyAttributes body) {
		return timing(body).getDirtyPeriodBase();
	}

	public static void setDirtyPeriodBase(BodyAttributes body, int value) {
		timing(body).setDirtyPeriodBase(value);
	}

	public static int getEyesightBase(BodyAttributes body) {
		return timing(body).getEyesightBase();
	}

	public static void setEyesightBase(BodyAttributes body, int value) {
		timing(body).setEyesightBase(value);
	}

	public static int getIncubationPeriodBase(BodyAttributes body) {
		return timing(body).getIncubationPeriodBase();
	}

	public static void setIncubationPeriodBase(BodyAttributes body, int value) {
		timing(body).setIncubationPeriodBase(value);
	}

	public static int[] getStepBase(BodyAttributes body) {
		return body.getBodyStatProfileRaw().getStepBase();
	}

	public static void setStepBase(BodyAttributes body, int[] value) {
		body.getBodyStatProfileRaw().setStepBase(value);
	}

	public static int getRelaxPeriodBase(BodyAttributes body) {
		return timing(body).getRelaxPeriodBase();
	}

	public static void setRelaxPeriodBase(BodyAttributes body, int value) {
		timing(body).setRelaxPeriodBase(value);
	}

	public static int getExcitePeriodBase(BodyAttributes body) {
		return timing(body).getExcitePeriodBase();
	}

	public static void setExcitePeriodBase(BodyAttributes body, int value) {
		timing(body).setExcitePeriodBase(value);
	}

	public static int getPregPeriodBase(BodyAttributes body) {
		return timing(body).getPregPeriodBase();
	}

	public static void setPregPeriodBase(BodyAttributes body, int value) {
		timing(body).setPregPeriodBase(value);
	}

	public static int getSleepPeriodBase(BodyAttributes body) {
		return timing(body).getSleepPeriodBase();
	}

	public static void setSleepPeriodBase(BodyAttributes body, int value) {
		timing(body).setSleepPeriodBase(value);
	}

	public static int getActivePeriodBase(BodyAttributes body) {
		return timing(body).getActivePeriodBase();
	}

	public static void setActivePeriodBase(BodyAttributes body, int value) {
		timing(body).setActivePeriodBase(value);
	}

	public static int getAngryPeriodBase(BodyAttributes body) {
		return timing(body).getAngryPeriodBase();
	}

	public static void setAngryPeriodBase(BodyAttributes body, int value) {
		timing(body).setAngryPeriodBase(value);
	}

	public static int getScarePeriodBase(BodyAttributes body) {
		return timing(body).getScarePeriodBase();
	}

	public static void setScarePeriodBase(BodyAttributes body, int value) {
		timing(body).setScarePeriodBase(value);
	}
}
