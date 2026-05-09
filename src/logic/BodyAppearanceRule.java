package src.logic;

import src.base.BodyAttributes;

/**
 * Bodyの見た目・装飾に関する単純判定を集約する.
 */
public final class BodyAppearanceRule {
	private BodyAppearanceRule() {
	}

	/**
	 * おかざりを持っているかどうかを判定する.
	 *
	 * @param body 判定対象
	 * @return おかざりがあるならtrue
	 */
	public static boolean hasOkazari(BodyAttributes body) {
		return body.getOkazari() != null;
	}

	/**
	 * 捕食種かどうかを判定する.
	 *
	 * @param body 判定対象
	 * @return 捕食種ならtrue
	 */
	public static boolean isPredatorType(BodyAttributes body) {
		return body.getPredatorType() != null;
	}
}
