package src.logic;

import src.base.Body;
import src.base.BodyAttributes;
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
	 * @param targetBody target body
	 * @param actorBody actor body
	 * @return true when the branch handled the action
	 */
	public static boolean handleNeedledBody(Body targetBody, Body actorBody) {
		if (!targetBody.isNeedled()) {
			return false;
		}
		if (actorBody.isAdult() && !targetBody.isAdult() && (targetBody.isChild(actorBody) || actorBody.isMother(targetBody))) {
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

	/**
	 * 針でさされ中かを判定する.
	 *
	 * @param body 判定対象
	 * @return 針でさされ中ならtrue
	 */
	public static boolean isNeedled(BodyAttributes body) {
		return body.isNeedledRaw();
	}

}
