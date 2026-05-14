package org.simyukkuri.logic;

import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.util.GameRandom;

/**
 * Needled yukkuri handling used by YukkuriLogic.
 */
public final class YukkuriNeedleRule {

	private YukkuriNeedleRule() {
	}

	/**
	 * Handle the needled branch in doActionOther.
	 *
	 * @param targetBody target body
	 * @param actorBody  actor body
	 * @return true when the branch handled the action
	 */
	public static boolean handleNeedledYukkuri(Yukkuri targetBody, Yukkuri actorBody) {
		if (!targetBody.isNeedled()) {
			return false;
		}
		if (actorBody.isAdult() && !targetBody.isAdult()
				&& (targetBody.isChild(actorBody) || actorBody.isMother(targetBody))) {
			actorBody.constraintDirection(targetBody, false);
			actorBody.doGuriguri(targetBody);
		} else if (targetBody.isPartner(actorBody)) {
			actorBody.constraintDirection(targetBody, false);
			actorBody.doGuriguri(targetBody);
		} else if (!actorBody.isAdult() && actorBody.isSister(targetBody) && GameRandom.nextInt(1) == 0) {
			actorBody.constraintDirection(targetBody, false);
			actorBody.doGuriguri(targetBody);
		}
		actorBody.clearActions();
		return true;
	}
}
