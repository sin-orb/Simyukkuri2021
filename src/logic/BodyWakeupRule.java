package src.logic;

import java.util.Map;

import src.base.Body;
import src.enums.BaryInUGState;
import src.enums.PublicRank;
import src.util.GameWorld;
import src.draw.Translate;
import src.item.Barrier;

/**
 * Other-body wakeup checks used by BodyLogic.
 */
public final class BodyWakeupRule {

	private BodyWakeupRule() {
	}

	/**
	 * Check whether there is any awake yukkuri in sight.
	 *
	 * @param b subject body
	 * @return true if an awake body is visible
	 */
	public static boolean checkWakeupOtherYukkuri(Body b) {
		boolean bIsWakeup = false;
		int minDistance = b.getEYESIGHTorg();
		for (Map.Entry<Integer, Body> entry : GameWorld.get().getCurrentMap().getBody().entrySet()) {
			Body p = entry.getValue();
			if (p == b) {
				continue;
			}
			if (p.isDead() || p.isRemoved() || p.isUnBirth()) {
				continue;
			}
			if (p.isNYD()) {
				continue;
			}
			if (b.getPublicRank() == PublicRank.NONE && p.getPublicRank() == PublicRank.UnunSlave) {
				continue;
			}
			if (p.getBaryState() != BaryInUGState.NONE) {
				continue;
			}

			int dist = Translate.distance(b.getX(), b.getY(), p.getX(), p.getY());
			if (minDistance > dist) {
				if (Barrier.acrossBarrier(b.getX(), b.getY(), p.getX(), p.getY(),
						Barrier.MAP_BODY[b.getBodyAgeState().ordinal()] + Barrier.BARRIER_KEKKAI)) {
					continue;
				}
			}
			if (!p.isSleeping()) {
				bIsWakeup = true;
				break;
			}
		}
		return bIsWakeup;
	}
}
