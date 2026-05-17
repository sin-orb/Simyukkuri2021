package org.simyukkuri.logic;

import org.simyukkuri.draw.Point4y;
import org.simyukkuri.draw.Translate;
import org.simyukkuri.entity.core.attachment.impl.Ants;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.field.impl.Barrier;

/**
 * Parent-nearness handling used by YukkuriLogic.
 */
public final class YukkuriParentRule {

	private YukkuriParentRule() {
	}

	/**
	 * 近くにいる親ゆっくりを探索して接近行動を処理する。
	 *
	 * @param body ゆっくり
	 */
	public static void checkNearParent(Yukkuri body) {
		if (body.isAdult()) {
			return;
		}

		int nearestDistance = body.getEyesightBase();
		Yukkuri parentBody = org.simyukkuri.util.YukkuriLookup.getYukkuriById(body.getMother());
		if (parentBody == null) {
			parentBody = org.simyukkuri.util.YukkuriLookup.getYukkuriById(body.getFather());
		}
		if (parentBody == null) {
			int elderSisterCount = body.getElderSistersCount();
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
					Barrier.BODY_BLOCK_FLAGS[body.getAgeState().ordinal()] + Barrier.BARRIER_KEKKAI)) {
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
