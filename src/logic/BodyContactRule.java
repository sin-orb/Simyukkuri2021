package src.logic;

import src.base.Yukkuri;

import src.base.Yukkuri;

/**
 * doActionOther の接触時処理をまとめる.
 */
public final class BodyContactRule {

	private BodyContactRule() {
	}

	/**
	 * 隣接状態の接触処理を実行する.
	 *
	 * @param targetBody 相手
	 * @param actorBody  自分
	 * @param rangeX     接触判定の横方向閾値
	 * @param distY      Y方向距離
	 * @param range      X方向の近接度
	 * @return 処理を消費したか
	 */
	public static boolean handleAdjacentContact(Yukkuri targetBody, Yukkuri actorBody, int rangeX, int distY, int range) {
		if (range >= 3 || distY >= Math.max(rangeX / 2, 10)) {
			return false;
		}

		if (targetBody.isDead()) {
			return BodyDeadActionRule.handleDeadBodyInteraction(targetBody, actorBody);
		}
		if (actorBody.isToSteal()) {
			return BodyStealRule.handleOkazariSteal(targetBody, actorBody);
		}
		if (BodyExcitementRule.handleExcitingContact(targetBody, actorBody)) {
			return true;
		}
		if (targetBody.isNeedled()) {
			return BodyNeedleRule.handleNeedledBody(targetBody, actorBody);
		}
		if (BodyContactEffectRule.handleContactEffects(targetBody, actorBody)) {
			return true;
		}
		return BodySkinshipRule.handleSkinship(targetBody, actorBody);
	}
}
