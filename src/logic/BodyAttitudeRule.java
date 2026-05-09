package src.logic;

import src.base.BodyAttributes;
import src.enums.Attitude;

/**
 * Bodyの性格判定を集約する.
 */
public final class BodyAttitudeRule {
	private BodyAttitudeRule() {
	}

	/**
	 * ドゲスか否かを返却する.
	 *
	 * @param body 判定対象
	 * @return ドゲスか否か
	 */
	public static boolean isVeryRude(BodyAttributes body) {
		return body.getAttitudeRaw() == Attitude.SUPER_SHITHEAD;
	}

	/**
	 * ゲスまたはドゲスか否かを返却する.
	 *
	 * @param body 判定対象
	 * @return ゲスまたはドゲスか否か
	 */
	public static boolean isRude(BodyAttributes body) {
		return body.getAttitudeRaw() == Attitude.SHITHEAD || body.getAttitudeRaw() == Attitude.SUPER_SHITHEAD;
	}

	/**
	 * ゲス/善良の区分で普通か否かを返却する.
	 *
	 * @param body 判定対象
	 * @return ゲス/善良の区分で普通か否か
	 */
	public static boolean isNormal(BodyAttributes body) {
		return body.getAttitudeRaw() == Attitude.AVERAGE;
	}

	/**
	 * 善良または超善良か否かを返却する.
	 *
	 * @param body 判定対象
	 * @return 善良または超善良か否か
	 */
	public static boolean isSmart(BodyAttributes body) {
		return body.getAttitudeRaw() == Attitude.VERY_NICE || body.getAttitudeRaw() == Attitude.NICE;
	}
}
