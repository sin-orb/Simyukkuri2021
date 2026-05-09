package src.logic;

import src.base.BodyAttributes;

/**
 * Bodyの年齢判定を集約する.
 */
public final class BodyAgeRule {
	private BodyAgeRule() {
	}

	/**
	 * 老ゆかどうかを判定する.
	 *
	 * @param body 判定対象
	 * @return 老ゆかならtrue
	 */
	public static boolean isOld(BodyAttributes body) {
		return body.getAge() > (body.getLifeLimitBase() * 9 / 10);
	}
}
