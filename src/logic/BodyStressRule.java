package src.logic;

import src.entity.core.living.yukkuri.Yukkuri;

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
	public static boolean isStressful(Yukkuri body) {
		return body.getStressLimit() * body.checkNonYukkuriDiseaseTolerance() / 100 * 2 / 5 < body.getStress();
	}

	/**
	 * とてもストレスフルかどうかを判定する.
	 *
	 * @param body 判定対象
	 * @return とてもストレスフルならtrue
	 */
	public static boolean isVeryStressful(Yukkuri body) {
		return body.getStressLimit() * body.checkNonYukkuriDiseaseTolerance() / 100 * 3 / 5 < body.getStress();
	}
}
