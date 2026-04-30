package src.logic;

import src.base.Body;
import src.util.GameRandom;

/**
 * Needled-body handling used by BodyLogic.
 */
public final class BodyNeedleRule {

	private BodyNeedleRule() {
	}

	/**
	 * Handle the needled branch in doActionOther.
	 *
	 * @param p target body
	 * @param b actor body
	 * @return true when the branch handled the action
	 */
	public static boolean handleNeedledBody(Body p, Body b) {
		if (!p.isNeedled()) {
			return false;
		}
		if (b.isAdult() && !p.isAdult() && (p.isChild(b) || b.isMother(p))) {
			b.constraintDirection(p, false);
			b.doGuriguri(p);
		} else if (p.isPartner(b)) {
			b.constraintDirection(p, false);
			b.doGuriguri(p);
		} else if (!b.isAdult() && b.isSister(p) && GameRandom.nextInt(1) == 0) {
			b.constraintDirection(p, false);
			b.doGuriguri(p);
		}
		b.clearActions();
		return true;
	}
}
