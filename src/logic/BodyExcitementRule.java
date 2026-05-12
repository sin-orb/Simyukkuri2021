package src.logic;

import src.entity.core.living.yukkuri.Yukkuri;
import src.enums.Direction;

/**
 * Exciting contact handling used by BodyLogic.
 */
public final class BodyExcitementRule {

	private BodyExcitementRule() {
	}

	/**
	 * Handle the exciting-body contact branch.
	 *
	 * @param targetBody target body
	 * @param actorBody  actor body
	 * @return true when the branch consumed the action
	 */
	public static boolean handleExcitingContact(Yukkuri targetBody, Yukkuri actorBody) {
		if (!actorBody.isExciting()) {
			return false;
		}
		// れいぱーまたは確率でドゲスはれいぷする
		if (actorBody.isRaper() || actorBody.isVeryRude()) {
			if (!targetBody.isRaper()) {
				if (actorBody.getX() < targetBody.getX()) {
					actorBody.setDirection(Direction.RIGHT);
				} else {
					actorBody.setDirection(Direction.LEFT);
				}
				actorBody.constraintDirection(targetBody, true);
				actorBody.doRape(targetBody);
				actorBody.clearActions();
				return true;
			}
			return false;
		}
		// 大人が相手の場合は、プロポーズしてからすっきりする
		if (targetBody.isAdult()) {
			actorBody.constraintDirection(targetBody, false);
			actorBody.clearActions();
			if (actorBody.isPartner(targetBody) || targetBody.isPartner(actorBody)) {
				actorBody.doSukkiri(targetBody);
				return true;
			}
			actorBody.doOnanism();
			return true;
		}
		// 強制的に発情させられた場合は見境なし
		if (actorBody.isForceExciting()) {
			actorBody.doSukkiri(targetBody);
			actorBody.clearActions();
		}
		return false;
	}
}
