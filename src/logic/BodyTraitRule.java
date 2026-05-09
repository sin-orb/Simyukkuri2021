package src.logic;

import src.base.BodyAttributes;

/**
 * Bodyの種族特性・嗜好に関する単純判定を集約する.
 */
public final class BodyTraitRule {
	private BodyTraitRule() {
	}

	/**
	 * 希少種かどうかを判定する.
	 *
	 * @param body 判定対象
	 * @return 希少種ならtrue
	 */
	public static boolean isRareType(BodyAttributes body) {
		return body.isRareTypeRaw();
	}

	public static void setRareType(BodyAttributes body, boolean value) {
		body.setRareTypeRaw(value);
	}

	/**
	 * 苦いえさが好きかどうかを判定する.
	 *
	 * @param body 判定対象
	 * @return 苦いえさが好きならtrue
	 */
	public static boolean isLikeBitterFood(BodyAttributes body) {
		return body.isLikeBitterFoodRaw();
	}

	public static void setLikeBitterFood(BodyAttributes body, boolean value) {
		body.setLikeBitterFoodRaw(value);
	}

	/**
	 * 辛いえさが好きかどうかを判定する.
	 *
	 * @param body 判定対象
	 * @return 辛いえさが好きならtrue
	 */
	public static boolean isLikeHotFood(BodyAttributes body) {
		return body.isLikeHotFoodRaw();
	}

	public static void setLikeHotFood(BodyAttributes body, boolean value) {
		body.setLikeHotFoodRaw(value);
	}

	/**
	 * 水が平気かどうかを判定する.
	 *
	 * @param body 判定対象
	 * @return 水が平気ならtrue
	 */
	public static boolean isLikeWater(BodyAttributes body) {
		return body.isLikeWaterRaw();
	}

	public static void setLikeWater(BodyAttributes body, boolean value) {
		body.setLikeWaterRaw(value);
	}

	/**
	 * 空を飛ぶかどうかを判定する.
	 *
	 * @param body 判定対象
	 * @return 空を飛ぶならtrue
	 */
	public static boolean isFlyingType(BodyAttributes body) {
		return body.isFlyingTypeRaw();
	}

	public static void setFlyingType(BodyAttributes body, boolean value) {
		body.setFlyingTypeRaw(value);
	}

	/**
	 * 種族としてお下げ、羽、尻尾を持つかどうかを判定する.
	 *
	 * @param body 判定対象
	 * @return お下げ、羽、尻尾を持つならtrue
	 */
	public static boolean isBraidType(BodyAttributes body) {
		return body.isBraidTypeRaw();
	}

	public static void setBraidType(BodyAttributes body, boolean value) {
		body.setBraidTypeRaw(value);
	}
}
