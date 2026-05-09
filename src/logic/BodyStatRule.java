package src.logic;

import src.base.BodyAttributes;

/**
 * BodyAttributes の配列型統計値をまとめるルール.
 */
public final class BodyStatRule {
	private BodyStatRule() {
	}

	public static int[] getEatAmountBase(BodyAttributes body) {
		return body.getEatAmountBase();
	}

	public static void setEatAmountBase(BodyAttributes body, int[] value) {
		body.setEatAmountBase(value);
	}

	public static int[] getWeightBase(BodyAttributes body) {
		return body.getWeightBase();
	}

	public static void setWeightBase(BodyAttributes body, int[] value) {
		body.setWeightBase(value);
	}

	public static int[] getHungryLimitBase(BodyAttributes body) {
		return body.getHungryLimitBase();
	}

	public static void setHungryLimitBase(BodyAttributes body, int[] value) {
		body.setHungryLimitBase(value);
	}

	public static int[] getShitLimitBase(BodyAttributes body) {
		return body.getShitLimitBase();
	}

	public static void setShitLimitBase(BodyAttributes body, int[] value) {
		body.setShitLimitBase(value);
	}

	public static int[] getDamageLimitBase(BodyAttributes body) {
		return body.getDamageLimitBase();
	}

	public static void setDamageLimitBase(BodyAttributes body, int[] value) {
		body.setDamageLimitBase(value);
	}

	public static int[] getStressLimitBase(BodyAttributes body) {
		return body.getStressLimitBase();
	}

	public static void setStressLimitBase(BodyAttributes body, int[] value) {
		body.setStressLimitBase(value);
	}

	public static int[] getTangLevelBase(BodyAttributes body) {
		return body.getTangLevelBase();
	}

	public static void setTangLevelBase(BodyAttributes body, int[] value) {
		body.setTangLevelBase(value);
	}

	public static int[] getStrengthBase(BodyAttributes body) {
		return body.getStrengthBase();
	}

	public static void setStrengthBase(BodyAttributes body, int[] value) {
		body.setStrengthBase(value);
	}

	public static int[] getImmunity(BodyAttributes body) {
		return body.getImmunity();
	}

	public static void setImmunity(BodyAttributes body, int[] value) {
		body.setImmunity(value);
	}

	public static int[] getRudeLimit(BodyAttributes body) {
		return body.getRudeLimit();
	}

	public static void setRudeLimit(BodyAttributes body, int[] value) {
		body.setRudeLimit(value);
	}
}
