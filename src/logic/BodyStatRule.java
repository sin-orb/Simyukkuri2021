package src.logic;

import src.base.BodyAttributes;
import src.base.BodyStatProfile;

/**
 * BodyAttributes の配列型統計値をまとめるルール.
 */
public final class BodyStatRule {
	private BodyStatRule() {
	}

	private static BodyStatProfile stat(BodyAttributes body) {
		return body.getBodyStatProfileRaw();
	}

	public static int[] getEatAmountBase(BodyAttributes body) {
		return stat(body).getEatAmountBase();
	}

	public static void setEatAmountBase(BodyAttributes body, int[] value) {
		stat(body).setEatAmountBase(value);
	}

	public static int[] getWeightBase(BodyAttributes body) {
		return stat(body).getWeightBase();
	}

	public static void setWeightBase(BodyAttributes body, int[] value) {
		stat(body).setWeightBase(value);
	}

	public static int[] getHungryLimitBase(BodyAttributes body) {
		return stat(body).getHungryLimitBase();
	}

	public static void setHungryLimitBase(BodyAttributes body, int[] value) {
		stat(body).setHungryLimitBase(value);
	}

	public static int[] getShitLimitBase(BodyAttributes body) {
		return stat(body).getShitLimitBase();
	}

	public static void setShitLimitBase(BodyAttributes body, int[] value) {
		stat(body).setShitLimitBase(value);
	}

	public static int[] getDamageLimitBase(BodyAttributes body) {
		return stat(body).getDamageLimitBase();
	}

	public static void setDamageLimitBase(BodyAttributes body, int[] value) {
		stat(body).setDamageLimitBase(value);
	}

	public static int[] getStressLimitBase(BodyAttributes body) {
		return stat(body).getStressLimitBase();
	}

	public static void setStressLimitBase(BodyAttributes body, int[] value) {
		stat(body).setStressLimitBase(value);
	}

	public static int[] getTangLevelBase(BodyAttributes body) {
		return stat(body).getTangLevelBase();
	}

	public static void setTangLevelBase(BodyAttributes body, int[] value) {
		stat(body).setTangLevelBase(value);
	}

	public static int[] getStrengthBase(BodyAttributes body) {
		return stat(body).getStrengthBase();
	}

	public static void setStrengthBase(BodyAttributes body, int[] value) {
		stat(body).setStrengthBase(value);
	}

	public static int[] getImmunity(BodyAttributes body) {
		return stat(body).getImmunity();
	}

	public static void setImmunity(BodyAttributes body, int[] value) {
		stat(body).setImmunity(value);
	}

	public static int[] getRudeLimit(BodyAttributes body) {
		return stat(body).getRudeLimit();
	}

	public static void setRudeLimit(BodyAttributes body, int[] value) {
		stat(body).setRudeLimit(value);
	}
}
