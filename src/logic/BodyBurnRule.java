package src.logic;

import src.base.BodyAttributes;
import src.enums.BodyBake;
import src.enums.FootBake;

/**
 * Bodyの焼損判定を集約する.
 */
public final class BodyBurnRule {
	private BodyBurnRule() {
	}

	/**
	 * やけどの有無を判定する.
	 *
	 * @param body 判定対象
	 * @return やけどしているならtrue
	 */
	public static boolean isGotBurned(BodyAttributes body) {
		return body.getFootBakeLevel() != FootBake.NONE || body.getBodyBakeLevel() != BodyBake.NONE;
	}

	/**
	 * 深刻なやけどの有無を判定する.
	 *
	 * @param body 判定対象
	 * @return 深刻にやけどしているならtrue
	 */
	public static boolean isGotBurnedHeavily(BodyAttributes body) {
		return body.getFootBakeLevel() != FootBake.NONE || body.getBodyBakeLevel() == BodyBake.CRITICAL;
	}
}
