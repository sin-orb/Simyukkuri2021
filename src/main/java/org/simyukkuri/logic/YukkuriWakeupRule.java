package org.simyukkuri.logic;

import java.util.Map;
import org.simyukkuri.draw.Translate;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.enums.BurialState;
import org.simyukkuri.enums.PublicRank;
import org.simyukkuri.field.impl.Barrier;
import org.simyukkuri.util.GameWorld;

/**
 * Other-body wakeup checks used by YukkuriLogic.
 */
public final class YukkuriWakeupRule {

	private YukkuriWakeupRule() {
	}

	/**
	 * 視野内に起きているゆっくりがいるかを判定して返す。
	 *
	 * @param body ゆっくり
	 * @return 視野内に起きているゆっくりがいる場合は true
	 */
	public static boolean checkWakeupOtherYukkuri(Yukkuri body) {
		boolean isWakeup = false;
		int nearestDistance = body.getEyesightBase();
		for (Map.Entry<Integer, Yukkuri> entry : GameWorld.get().getCurrentWorldState().getYukkuriRegistry().entrySet()) {
			Yukkuri otherBody = entry.getValue();
			if (otherBody == body) {
				continue;
			}
			if (otherBody.isDead() || otherBody.isRemoved() || otherBody.isUnBirth()) {
				continue;
			}
			if (otherBody.isNYD()) {
				continue;
			}
			if (body.getPublicRank() == PublicRank.NONE && otherBody.getPublicRank() == PublicRank.UNUN_SLAVE) {
				continue;
			}
			if (otherBody.getBurialState() != BurialState.NONE) {
				continue;
			}

			int distance = Translate.distance(body.getX(), body.getY(), otherBody.getX(), otherBody.getY());
			if (nearestDistance > distance) {
				if (Barrier.acrossBarrier(body.getX(), body.getY(), otherBody.getX(), otherBody.getY(),
						Barrier.BODY_BLOCK_FLAGS[body.getAgeState().ordinal()] + Barrier.BARRIER_KEKKAI)) {
					continue;
				}
			}
			if (!otherBody.isSleeping()) {
				isWakeup = true;
				break;
			}
		}
		return isWakeup;
	}
}
