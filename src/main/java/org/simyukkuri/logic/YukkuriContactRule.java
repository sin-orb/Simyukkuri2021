package org.simyukkuri.logic;

import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;

/**
 * doActionOther の接触時処理をまとめる.
 */
public final class YukkuriContactRule {

	private YukkuriContactRule() {
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
	public static boolean handleAdjacentContact(Yukkuri targetBody, Yukkuri actorBody, int rangeX, int distY,
			int range) {
		if (range >= 3 || distY >= Math.max(rangeX / 2, 10)) {
			return false;
		}

		if (targetBody.isDead()) {
			return YukkuriDeadActionRule.handleDeadYukkuriInteraction(targetBody, actorBody);
		}
		if (actorBody.isToSteal()) {
			return YukkuriStealRule.handleOkazariSteal(targetBody, actorBody);
		}
		if (YukkuriExcitementRule.handleExcitingContact(targetBody, actorBody)) {
			return true;
		}
		if (targetBody.isNeedled()) {
			return YukkuriNeedleRule.handleNeedledYukkuri(targetBody, actorBody);
		}
		if (YukkuriContactEffectRule.handleContactEffects(targetBody, actorBody)) {
			return true;
		}
		return YukkuriSkinshipRule.handleSkinship(targetBody, actorBody);
	}
}
