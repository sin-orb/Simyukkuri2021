package org.simyukkuri.logic;

import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.enums.Direction;

/**
 * Exciting contact handling used by YukkuriLogic.
 */
public final class YukkuriExcitementRule {

	private YukkuriExcitementRule() {
	}

	/**
	 * 興奮ゆっくりとの接触分岐を処理する。
	 *
	 * @param targetBody 処理対象ゆっくり
	 * @param actorBody  行動主体ゆっくり
	 * @return 分岐が処理を消費した場合は true
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
