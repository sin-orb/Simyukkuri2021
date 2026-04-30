package src.logic;

import src.base.Body;
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
	 * @param p target body
	 * @param b actor body
	 * @return true when the branch consumed the action
	 */
	public static boolean handleExcitingContact(Body p, Body b) {
		if (!b.isExciting()) {
			return false;
		}
		// れいぱーまたは確率でドゲスはれいぷする
		if (b.isRaper() || b.isVeryRude()) {
			if (!p.isRaper()) {
				if (b.getX() < p.getX()) {
					b.setDirection(Direction.RIGHT);
				} else {
					b.setDirection(Direction.LEFT);
				}
				b.constraintDirection(p, true);
				b.doRape(p);
				b.clearActions();
				return true;
			}
			return false;
		}
		// 大人が相手の場合は、プロポーズしてからすっきりする
		if (p.isAdult()) {
			b.constraintDirection(p, false);
			b.clearActions();
			if (b.isPartner(p) || p.isPartner(b)) {
				b.doSukkiri(p);
				return true;
			}
			b.doOnanism();
			return true;
		}
		// 強制的に発情させられた場合は見境なし
		if (b.isForceExciting()) {
			b.doSukkiri(p);
			b.clearActions();
		}
		return false;
	}
}
