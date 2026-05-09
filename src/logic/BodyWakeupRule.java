package src.logic;

import java.util.Map;

import src.base.Body;
import src.enums.BurialState;
import src.enums.PublicRank;
import src.util.GameWorld;
import src.draw.Translate;
import src.field.impl.Barrier;

/**
 * Other-body wakeup checks used by BodyLogic.
 */
public final class BodyWakeupRule {

	private BodyWakeupRule() {
	}

	/**
	 * Check whether there is any awake yukkuri in sight.
	 *
	 * @param body subject body
	 * @return true if an awake body is visible
	 */
	public static boolean checkWakeupOtherYukkuri(Body body) {
		boolean isWakeup = false;
		int nearestDistance = body.getEyesightBase();
		for (Map.Entry<Integer, Body> entry : GameWorld.get().getCurrentMap().getBody().entrySet()) {
			Body otherBody = entry.getValue();
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
						Barrier.MAP_BODY[body.getBodyAgeState().ordinal()] + Barrier.BARRIER_KEKKAI)) {
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
