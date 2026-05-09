package src.logic;

import src.base.BodyAttributes;

/**
 * Bodyの自己表現・姿勢に関する単純判定を集約する.
 */
public final class BodyStyleRule {
	private BodyStyleRule() {
	}

	/**
	 * ふりふりしているかどうかを判定する.
	 *
	 * @param body 判定対象
	 * @return ふりふりしているならtrue
	 */
	public static boolean isFurifuri(BodyAttributes body) {
		return !body.isDead() && body.isFurifuriRaw();
	}

	/**
	 * のびのびしているかどうかを判定する.
	 *
	 * @param body 判定対象
	 * @return のびのびしているならtrue
	 */
	public static boolean isNobinobi(BodyAttributes body) {
		return !body.isDead() && body.isNobinobiRaw();
	}

	/**
	 * キリッ！かどうかを判定する.
	 *
	 * @param body 判定対象
	 * @return キリッ！ならtrue
	 */
	public static boolean isVain(BodyAttributes body) {
		return !body.isDead() && body.isVainRaw();
	}

	/**
	 * キリッ！中かどうかを判定する.
	 *
	 * @param body 判定対象
	 * @return キリッ！中ならtrue
	 */
	public static boolean isBeVain(BodyAttributes body) {
		return !body.isDead() && body.isBeVainRaw();
	}
}
