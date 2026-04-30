package src.logic;

import src.base.Body;

/**
 * doActionOther の接触時処理をまとめる.
 */
public final class BodyContactRule {

	private BodyContactRule() {
	}

	/**
	 * 隣接状態の接触処理を実行する.
	 *
	 * @param p      自分が接触している相手
	 * @param b      自分
	 * @param rangeX 接触判定の横方向閾値
	 * @param distY  Y方向距離
	 * @param range  X方向の近接度
	 * @return 処理を消費したか
	 */
	public static boolean handleAdjacentContact(Body p, Body b, int rangeX, int distY, int range) {
		if (range >= 3 || distY >= Math.max(rangeX / 2, 10)) {
			return false;
		}

		if (p.isDead()) {
			return BodyDeadActionRule.handleDeadBodyInteraction(p, b);
		}
		if (b.isToSteal()) {
			return BodyStealRule.handleOkazariSteal(p, b);
		}
		if (BodyExcitementRule.handleExcitingContact(p, b)) {
			return true;
		}
		if (p.isNeedled()) {
			return BodyNeedleRule.handleNeedledBody(p, b);
		}
		if (BodyContactEffectRule.handleContactEffects(p, b)) {
			return true;
		}
		return BodySkinshipRule.handleSkinship(p, b);
	}
}
