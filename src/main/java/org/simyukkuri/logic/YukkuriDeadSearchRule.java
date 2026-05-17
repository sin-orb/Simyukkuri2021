package org.simyukkuri.logic;

import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.enums.Happiness;
import org.simyukkuri.system.MessagePool;
import org.simyukkuri.util.GameEnvironment;
import org.simyukkuri.util.GameMessages;
import org.simyukkuri.util.GameRandom;

/**
 * Dead-body handling used by YukkuriLogic.checkPartner.
 */
public final class YukkuriDeadSearchRule {

	private YukkuriDeadSearchRule() {
	}

	/**
	 * パートナー探索の死体分岐を処理する。
	 *
	 * @param actorBody  行動主体ゆっくり
	 * @param targetBody 処理対象の死体ゆっくり
	 * @param colX       衝突補正後のX座標
	 * @param mz         目標Z座標
	 * @return 分岐が処理を消費した場合は true
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
						actorBody.moveToYukkuri(targetBody, targetBody.getX() + colX, targetBody.getY(), mz);
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
						actorBody.moveToYukkuri(targetBody, targetBody.getX() + colX, targetBody.getY(), mz);
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
