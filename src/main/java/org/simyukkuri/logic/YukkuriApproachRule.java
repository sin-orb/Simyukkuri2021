package org.simyukkuri.logic;

import org.simyukkuri.draw.Translate;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.util.GameRandom;

/**
 * Non-contact approach handling used by YukkuriLogic.
 */
public final class YukkuriApproachRule {

	private YukkuriApproachRule() {
	}

	/**
	 * doActionOther の非接触接近分岐を処理する。
	 *
	 * @param targetBody 処理対象ゆっくり
	 * @param actorBody  行動主体ゆっくり
	 */
	public static void handleApproach(Yukkuri targetBody, Yukkuri actorBody, int offsetX) {
		int directionSign = 1;
		if (actorBody.getX() < targetBody.getX()) {
			directionSign = -1;
		}
		offsetX *= directionSign;
		if (actorBody.canflyCheck()) {
			actorBody.moveTo(targetBody.getX() + offsetX, targetBody.getY(), targetBody.getZ());
		} else {
			actorBody.moveTo(targetBody.getX() + offsetX, targetBody.getY());
		}
		// 相手に追いつけないケースがあるため、一定距離まで近づいたら相手を呼び止める
		if (Translate.distance(actorBody.getX(), actorBody.getY(), targetBody.getX(), targetBody.getY()) < 2500) {
			if (GameRandom.nextInt(3) == 0) {
				if (actorBody.isTargetBind()) {
					targetBody.stay();
				}
			}
		}
	}
}
