package src.logic;

import src.base.Body;
import src.draw.Translate;
import src.util.GameRandom;

/**
 * Non-contact approach handling used by BodyLogic.
 */
public final class BodyApproachRule {

	private BodyApproachRule() {
	}

	/**
	 * Handle the non-contact approach branch in doActionOther.
	 *
	 * @param p target body
	 * @param b actor body
	 */
	public static void handleApproach(Body p, Body b, int rangeX) {
		int dir = 1;
		if (b.getX() < p.getX()) {
			dir = -1;
		}
		rangeX *= dir;
		if (b.canflyCheck()) {
			b.moveTo(p.getX() + rangeX, p.getY(), p.getZ());
		} else {
			b.moveTo(p.getX() + rangeX, p.getY());
		}
		// 相手に追いつけないケースがあるため、一定距離まで近づいたら相手を呼び止める
		if (Translate.distance(b.getX(), b.getY(), p.getX(), p.getY()) < 2500) {
			if (GameRandom.nextInt(3) == 0) {
				if (b.isTargetBind()) {
					p.stay();
				}
			}
		}
	}
}
