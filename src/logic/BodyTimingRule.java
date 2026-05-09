package src.logic;

import src.base.BodyAttributes;

/**
 * BodyAttributes の時刻・閾値系パラメータをまとめたルール.
 */
public final class BodyTimingRule {
	private BodyTimingRule() {
	}

	public static int getBabyLimitBase(BodyAttributes body) {
		return body.getBabyLimitBase();
	}

	public static void setBabyLimitBase(BodyAttributes body, int value) {
		body.setBabyLimitBase(value);
	}

	public static int getChildLimitBase(BodyAttributes body) {
		return body.getChildLimitBase();
	}

	public static void setChildLimitBase(BodyAttributes body, int value) {
		body.setChildLimitBase(value);
	}

	public static int getLifeLimitBase(BodyAttributes body) {
		return body.getLifeLimitBase();
	}

	public static void setLifeLimitBase(BodyAttributes body, int value) {
		body.setLifeLimitBase(value);
	}

	public static int getRottingTimeBase(BodyAttributes body) {
		return body.getRottingTimeBase();
	}

	public static void setRottingTimeBase(BodyAttributes body, int value) {
		body.setRottingTimeBase(value);
	}

	public static int getDeclinePeriodBase(BodyAttributes body) {
		return body.getDeclinePeriodBase();
	}

	public static void setDeclinePeriodBase(BodyAttributes body, int value) {
		body.setDeclinePeriodBase(value);
	}

	public static int getBlockedLimitBase(BodyAttributes body) {
		return body.getBlockedLimitBase();
	}

	public static void setBlockedLimitBase(BodyAttributes body, int value) {
		body.setBlockedLimitBase(value);
	}

	public static int getDirtyPeriodBase(BodyAttributes body) {
		return body.getDirtyPeriodBase();
	}

	public static void setDirtyPeriodBase(BodyAttributes body, int value) {
		body.setDirtyPeriodBase(value);
	}

	public static int getEyesightBase(BodyAttributes body) {
		return body.getEyesightBase();
	}

	public static void setEyesightBase(BodyAttributes body, int value) {
		body.setEyesightBase(value);
	}

	public static int getIncubationPeriodBase(BodyAttributes body) {
		return body.getIncubationPeriodBase();
	}

	public static void setIncubationPeriodBase(BodyAttributes body, int value) {
		body.setIncubationPeriodBase(value);
	}

	public static int[] getStepBase(BodyAttributes body) {
		return body.getStepBase();
	}

	public static void setStepBase(BodyAttributes body, int[] value) {
		body.setStepBase(value);
	}

	public static int getRelaxPeriodBase(BodyAttributes body) {
		return body.getRelaxPeriodBase();
	}

	public static void setRelaxPeriodBase(BodyAttributes body, int value) {
		body.setRelaxPeriodBase(value);
	}

	public static int getExcitePeriodBase(BodyAttributes body) {
		return body.getExcitePeriodBase();
	}

	public static void setExcitePeriodBase(BodyAttributes body, int value) {
		body.setExcitePeriodBase(value);
	}

	public static int getPregPeriodBase(BodyAttributes body) {
		return body.getPregPeriodBase();
	}

	public static void setPregPeriodBase(BodyAttributes body, int value) {
		body.setPregPeriodBase(value);
	}

	public static int getSleepPeriodBase(BodyAttributes body) {
		return body.getSleepPeriodBase();
	}

	public static void setSleepPeriodBase(BodyAttributes body, int value) {
		body.setSleepPeriodBase(value);
	}

	public static int getActivePeriodBase(BodyAttributes body) {
		return body.getActivePeriodBase();
	}

	public static void setActivePeriodBase(BodyAttributes body, int value) {
		body.setActivePeriodBase(value);
	}

	public static int getAngryPeriodBase(BodyAttributes body) {
		return body.getAngryPeriodBase();
	}

	public static void setAngryPeriodBase(BodyAttributes body, int value) {
		body.setAngryPeriodBase(value);
	}

	public static int getScarePeriodBase(BodyAttributes body) {
		return body.getScarePeriodBase();
	}

	public static void setScarePeriodBase(BodyAttributes body, int value) {
		body.setScarePeriodBase(value);
	}
}
