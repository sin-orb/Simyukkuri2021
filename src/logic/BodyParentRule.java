package src.logic;

import src.draw.Point4y;
import src.draw.Translate;
import src.entity.core.attachment.impl.Ants;
import src.entity.core.living.yukkuri.Yukkuri;
import src.field.impl.Barrier;

/**
 * Parent-nearness handling used by BodyLogic.
 */
public final class BodyParentRule {

	private BodyParentRule() {
	}

	/**
	 * Check and process a nearby parent body.
	 *
	 * @param body target body
	 */
	public static void checkNearParent(Yukkuri body) {
		if (body.isAdult()) {
			return;
		}

		int nearestDistance = body.getEyesightBase();
		Yukkuri parentBody = src.util.BodyRegistry.getBodyInstance(body.getMother());
		if (parentBody == null) {
			parentBody = src.util.BodyRegistry.getBodyInstance(body.getFather());
		}
		if (parentBody == null) {
			int elderSisterCount = body.getElderSisterListSize();
			if (0 < elderSisterCount) {
				parentBody = body.getElderSister(0);
			}
		}
		if (parentBody == null) {
			return;
		}

		int distanceToParent = Translate.distance(body.getX(), body.getY(), parentBody.getX(), parentBody.getY());
		int parentDistanceRatio = 32;

		if (body.isCallingParents() && parentBody.isSleeping()) {
			parentBody.wakeup();
		}
		if ((body.isDirty() || body.getAttachmentSize(Ants.class) != 0) && parentBody.canEventResponse()) {
			if (distanceToParent <= parentBody.getStepDist()) {
				parentBody.constraintDirection(body, false);
				parentBody.doPeropero(body);
				return;
			}
			body.moveTo(parentBody.getX(), parentBody.getY());
			return;
		}

		if (distanceToParent < nearestDistance / parentDistanceRatio) {
			return;
		}

		if (nearestDistance / parentDistanceRatio <= distanceToParent) {
			if (Barrier.acrossBarrier(body.getX(), body.getY(), parentBody.getX(), parentBody.getY(),
					Barrier.MAP_BODY[body.getBodyAgeState().ordinal()] + Barrier.BARRIER_KEKKAI)) {
				return;
			}

			int moveDistance = (int) Math.sqrt(distanceToParent)
					- (int) Math.sqrt(nearestDistance / parentDistanceRatio);
			double radian = Translate.getRadian(body.getX(), body.getY(), parentBody.getX(), parentBody.getY());
			Point4y destination = Translate.getPointByDistAndRad(body.getX(), body.getY(), moveDistance, radian);
			body.moveTo(destination.getX(), destination.getY(), body.getZ());
		}
	}
}
