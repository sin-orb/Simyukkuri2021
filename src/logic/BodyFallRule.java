package src.logic;

import src.base.BodyAttributes;

/**
 * Bodyの落下関連フラグを集約する.
 */
public final class BodyFallRule {
	private BodyFallRule() {
	}

	/**
	 * 次の落下でダメージを受けないかを判定する.
	 *
	 * @param body 判定対象
	 * @return 次の落下でダメージを受けないならtrue
	 */
	public static boolean isNoDamageNextFall(BodyAttributes body) {
		return body.isNoDamageNextFallRaw();
	}
}
