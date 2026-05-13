package src.logic;

import src.entity.core.living.SocialEntity;

/**
 * Bodyのストレスに関する単純判定を集約する.
 */
public final class BodyStressRule {
	private BodyStressRule() {
	}

	/**
	 * ストレスフルかどうかを判定する.
	 *
	 * @param body 判定対象
	 * @return ストレスフルならtrue
	 */
	public static boolean isStressful(SocialEntity body) {
		return body.isStressful();
	}

	/**
	 * とてもストレスフルかどうかを判定する.
	 *
	 * @param body 判定対象
	 * @return とてもストレスフルならtrue
	 */
	public static boolean isVeryStressful(SocialEntity body) {
		return body.isVeryStressful();
	}
}
