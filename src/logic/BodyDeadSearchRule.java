package src.logic;

import src.base.Body;
import src.enums.Happiness;
import src.system.MessagePool;
import src.util.GameMessages;
import src.util.GameRandom;
import src.util.GameEnvironment;

/**
 * Dead-body handling used by BodyLogic.checkPartner.
 */
public final class BodyDeadSearchRule {

	private BodyDeadSearchRule() {
	}

	/**
	 * Handle the dead-body branch of partner search.
	 *
	 * @param b      actor body
	 * @param found  found dead body
	 * @param colX   collision-adjusted x coordinate
	 * @param mz     destination z coordinate
	 * @return true when a branch consumed the action
	 */
	public static boolean handleDeadFound(Body b, Body found, int colX, int mz) {
		boolean ret = false;
		if (b.isExciting()) {
			// すっきり
			b.moveToSukkiri(found, found.getX() + colX, found.getY(), mz);
			b.setTargetBind(false);
			return true;
		}
		if (GameRandom.nextInt(10) != 0) {
			return false;
		}
		// 片方だけがうんうん奴隷の場合はなにもしない
		if (b.getPublicRank() == found.getPublicRank()) {
			// レイパーじゃないなら気にする
			if (!b.isRaper()) {
				// 家族の死体に嘆く
				if (b.isAdult()) {
					if (b.isParent(found) || b.isPartner(found) || found.isParent(b)) {
						b.moveToBody(found, found.getX() + colX, found.getY(), mz);
						b.setTargetBind(false);
						ret = true;
					} else {
						if ((b.isPredatorType() && found.isPredatorType() || !b.isPredatorType())
								&& !GameEnvironment.isPredatorSteam()) {
							b.lookTo(found.getX() + colX, found.getY());
						}
					}
				} else {
					// 自身が対象死体の姉妹または対象死体が自身の親なら、そちらに向かう
					if (b.isSister(found) || found.isParent(b)) {
						b.moveToBody(found, found.getX() + colX, found.getY(), mz);
						b.setTargetBind(false);
						ret = true;
					} else {
						// 自身も対象死体も捕食種、または自身が通常種の場合、死体から逃げる
						if ((b.isPredatorType() && found.isPredatorType() || !b.isPredatorType())
								&& !GameEnvironment.isPredatorSteam()) {
							b.runAway(found.getX() + colX, found.getY());
						}
					}
				}
			}
		}

		// フィールドの死体に怯える
		if (!b.isTalking()) {
			if ((b.isPredatorType() && found.isPredatorType() || !b.isPredatorType())
					&& !GameEnvironment.isPredatorSteam()) {
				if (b.isNotNYD()) {
					// レイパー,捕食種じゃないなら気にする
					if (!b.isRaper() && !b.isPredatorType()) {
						b.setMessage(GameMessages.getMessage(b, MessagePool.Action.Scare));
						b.setHappiness(Happiness.SAD);
						b.addMemories(-1);
					}
				}
			}
		}
		return ret;
	}
}
