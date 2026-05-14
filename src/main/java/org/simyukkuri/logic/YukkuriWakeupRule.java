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
	 * Check whether there is any awake yukkuri in sight.
	 *
	 * @param body subject body
	 * @return true if an awake body is visible
	 */
	public static boolean checkWakeupOtherYukkuri(Yukkuri body) {
		boolean isWakeup = false;
		int nearestDistance = body.getEyesightBase();
		for (Map.Entry<Integer, Yukkuri> entry : GameWorld.get().getCurrentMap().getYukkuriMap().entrySet()) {
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
			if (body.getPublicRank() == PublicRank.NONE && otherBody.getPublicRank() == PublicRank.UnunSlave) {
				continue;
			}
			if (otherBody.getBurialState() != BurialState.NONE) {
				continue;
			}

			int distance = Translate.distance(body.getX(), body.getY(), otherBody.getX(), otherBody.getY());
			if (nearestDistance > distance) {
				if (Barrier.acrossBarrier(body.getX(), body.getY(), otherBody.getX(), otherBody.getY(),
						Barrier.MAP_BODY[body.getAgeState().ordinal()] + Barrier.BARRIER_KEKKAI)) {
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
