package src.logic;

import src.attachment.Ants;
import src.base.Body;
import src.draw.Point4y;
import src.draw.Translate;
import src.item.Barrier;
import src.util.YukkuriUtil;

/**
 * Parent-nearness handling used by BodyLogic.
 */
public final class BodyParentRule {

	private BodyParentRule() {
	}

	/**
	 * Check and process a nearby parent body.
	 *
	 * @param b target body
	 */
	public static void checkNearParent(Body b) {
		if (b.isAdult()) {
			return;
		}

		int minDistance = b.getEYESIGHTorg();
		Body bodyParent = YukkuriUtil.getBodyInstance(b.getMother());
		if (bodyParent == null) {
			bodyParent = YukkuriUtil.getBodyInstance(b.getFather());
		}
		if (bodyParent == null) {
			int nSize = b.getElderSisterListSize();
			if (0 < nSize) {
				bodyParent = b.getElderSister(0);
			}
		}
		if (bodyParent == null) {
			return;
		}

		int dist = Translate.distance(b.getX(), b.getY(), bodyParent.getX(), bodyParent.getY());
		int nParcent = 32;

		if (b.isCallingParents() && bodyParent.isSleeping()) {
			bodyParent.wakeup();
		}
		if ((b.isDirty() || b.getAttachmentSize(Ants.class) != 0) && bodyParent.canEventResponse()) {
			if (dist <= bodyParent.getStepDist()) {
				bodyParent.constraintDirection(b, false);
				bodyParent.doPeropero(b);
				return;
			}
			b.moveTo(bodyParent.getX(), bodyParent.getY());
			return;
		}

		if (dist < minDistance / nParcent) {
			return;
		}

		if (minDistance / nParcent <= dist) {
			if (Barrier.acrossBarrier(b.getX(), b.getY(), bodyParent.getX(), bodyParent.getY(),
					Barrier.MAP_BODY[b.getBodyAgeState().ordinal()] + Barrier.BARRIER_KEKKAI)) {
				return;
			}

			int nToDist = (int) Math.sqrt(dist) - (int) Math.sqrt(minDistance / nParcent);
			double dRad = Translate.getRadian(b.getX(), b.getY(), bodyParent.getX(), bodyParent.getY());
			Point4y p2 = Translate.getPointByDistAndRad(b.getX(), b.getY(), nToDist, dRad);
			b.moveTo(p2.getX(), p2.getY(), b.getZ());
		}
	}
}
