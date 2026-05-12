package src.logic;

import src.entity.core.living.yukkuri.Yukkuri;
import src.enums.Happiness;
import src.system.MessagePool;
import src.util.GameEnvironment;
import src.util.GameMessages;
import src.util.GameRandom;

/**
 * Dead-body handling used by BodyLogic.checkPartner.
 */
public final class BodyDeadSearchRule {

	private BodyDeadSearchRule() {
	}

	/**
	 * Handle the dead-body branch of partner search.
	 *
	 * @param actorBody  actor body
	 * @param targetBody target dead body
	 * @param colX       collision-adjusted x coordinate
	 * @param mz         destination z coordinate
	 * @return true when a branch consumed the action
	 */
	public static boolean handleDeadFound(Yukkuri actorBody, Yukkuri targetBody, int colX, int mz) {
		boolean handled = false;
		if (actorBody.isExciting()) {
			// すっきり
			actorBody.moveToSukkiri(targetBody, targetBody.getX() + colX, targetBody.getY(), mz);
			actorBody.setTargetBind(false);
			return true;
		}
		if (GameRandom.nextInt(10) != 0) {
			return false;
		}
		// 片方だけがうんうん奴隷の場合はなにもしない
		if (actorBody.getPublicRank() == targetBody.getPublicRank()) {
			// レイパーじゃないなら気にする
			if (!actorBody.isRaper()) {
				// 家族の死体に嘆く
				if (actorBody.isAdult()) {
					if (actorBody.isParent(targetBody) || actorBody.isPartner(targetBody)
							|| targetBody.isParent(actorBody)) {
						actorBody.moveToBody(targetBody, targetBody.getX() + colX, targetBody.getY(), mz);
						actorBody.setTargetBind(false);
						handled = true;
					} else {
						if ((actorBody.isPredatorType() && targetBody.isPredatorType() || !actorBody.isPredatorType())
								&& !GameEnvironment.isPredatorSteam()) {
							actorBody.lookTo(targetBody.getX() + colX, targetBody.getY());
						}
					}
				} else {
					// 自身が対象死体の姉妹または対象死体が自身の親なら、そちらに向かう
					if (actorBody.isSister(targetBody) || targetBody.isParent(actorBody)) {
						actorBody.moveToBody(targetBody, targetBody.getX() + colX, targetBody.getY(), mz);
						actorBody.setTargetBind(false);
						handled = true;
					} else {
						// 自身も対象死体も捕食種、または自身が通常種の場合、死体から逃げる
						if ((actorBody.isPredatorType() && targetBody.isPredatorType() || !actorBody.isPredatorType())
								&& !GameEnvironment.isPredatorSteam()) {
							actorBody.runAway(targetBody.getX() + colX, targetBody.getY());
						}
					}
				}
			}
		}

		// フィールドの死体に怯える
		if (!actorBody.isTalking()) {
			if ((actorBody.isPredatorType() && targetBody.isPredatorType() || !actorBody.isPredatorType())
					&& !GameEnvironment.isPredatorSteam()) {
				if (actorBody.isNotNYD()) {
					// レイパー,捕食種じゃないなら気にする
					if (!actorBody.isRaper() && !actorBody.isPredatorType()) {
						actorBody.setMessage(GameMessages.getMessage(actorBody, MessagePool.Action.Scare));
						actorBody.setHappiness(Happiness.SAD);
						actorBody.addMemories(-1);
					}
				}
			}
		}
		return handled;
	}
}
