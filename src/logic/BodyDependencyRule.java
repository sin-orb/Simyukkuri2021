package src.logic;

import src.base.BodyAttributes;

/**
 * Bodyが抱える依存系フラグの判定を集約する.
 */
public final class BodyDependencyRule {
	private BodyDependencyRule() {
	}

	/**
	 * 茎が生えているかどうかを判定する.
	 *
	 * @param body 判定対象
	 * @return 茎が生えていればtrue
	 */
	public static boolean hasBindStalk(BodyAttributes body) {
		return body.getBindStalk() != null;
	}

	/**
	 * 子または茎を持っているかどうかを判定する.
	 *
	 * @param body 判定対象
	 * @return 子または茎があればtrue
	 */
	public static boolean hasBabyOrStalk(BodyAttributes body) {
		return body.isHasBaby() || body.isHasStalk();
	}
}
