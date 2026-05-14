package org.simyukkuri.logic;

import org.simyukkuri.draw.Translate;
import org.simyukkuri.entity.core.Entity;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.enums.Intelligence;
import org.simyukkuri.field.impl.Barrier;

/**
 * 捕食種向けの body 候補更新.
 */
public final class FoodPredatorCandidatePolicy {

	private FoodPredatorCandidatePolicy() {
	}

	/**
	 * live body を候補として評価する.
	 */
	public static BodyCandidateResult considerLiveBody(Yukkuri hunter, Yukkuri candidate, int nearestLiveDistance,
			int nearestOtherDistance, int candidateSize, int wallMode, Entity nearestLiveObject,
			Entity nearestOtherObject) {
		if (hunter.getIntelligence() != Intelligence.FOOL && hunter.findSick(candidate) && !hunter.isTooHungry()) {
			return new BodyCandidateResult(nearestLiveObject, nearestOtherObject, nearestLiveDistance,
					nearestOtherDistance, candidateSize);
		}
		if (candidate.isPredatorType()) {
			return new BodyCandidateResult(nearestLiveObject, nearestOtherObject, nearestLiveDistance,
					nearestOtherDistance, candidateSize);
		}
		if (hunter.isFamily(candidate)) {
			return new BodyCandidateResult(nearestLiveObject, nearestOtherObject, nearestLiveDistance,
					nearestOtherDistance, candidateSize);
		}
		if (!hunter.canflyCheck() && candidate.getZ() != 0) {
			return new BodyCandidateResult(nearestLiveObject, nearestOtherObject, nearestLiveDistance,
					nearestOtherDistance, candidateSize);
		}
		if (candidate.isServantOf(hunter.getType()) && hunter.isPredator()) {
			return new BodyCandidateResult(nearestLiveObject, nearestOtherObject, nearestLiveDistance,
					nearestOtherDistance, candidateSize);
		}

		int distance = Translate.distance(hunter.getX(), hunter.getY(), candidate.getX(), candidate.getY());
		if (candidate.getBodyAgeState().ordinal() < hunter.getBodyAgeState().ordinal()) {
			if (nearestLiveDistance > distance || candidate.getBodyAgeState().ordinal() < candidateSize) {
				if (Barrier.acrossBarrier(hunter.getX(), hunter.getY(), candidate.getX(), candidate.getY(),
						Barrier.MAP_BODY[wallMode] + Barrier.BARRIER_KEKKAI)) {
					return new BodyCandidateResult(nearestLiveObject, nearestOtherObject, nearestLiveDistance,
							nearestOtherDistance, candidateSize);
				}
				nearestLiveObject = candidate;
				nearestLiveDistance = distance;
				candidateSize = candidate.getBodyAgeState().ordinal();
			}
		} else {
			if (nearestOtherDistance > distance) {
				if (Barrier.acrossBarrier(hunter.getX(), hunter.getY(), candidate.getX(), candidate.getY(),
						Barrier.MAP_BODY[wallMode] + Barrier.BARRIER_KEKKAI)) {
					return new BodyCandidateResult(nearestLiveObject, nearestOtherObject, nearestLiveDistance,
							nearestOtherDistance, candidateSize);
				}
				nearestOtherObject = candidate;
				nearestOtherDistance = distance;
			}
		}
		return new BodyCandidateResult(nearestLiveObject, nearestOtherObject, nearestLiveDistance,
				nearestOtherDistance, candidateSize);
	}

	/**
	 * 死体候補を評価する.
	 */
	public static BodyCandidateResult considerDeadBody(Yukkuri hunter, Yukkuri candidate, int nearestDeadDistance,
			int wallMode,
			Entity nearestDeadObject) {
		if (!hunter.isRude() && candidate.hasOkazari() && hunter.isFamily(candidate)) {
			return new BodyCandidateResult(null, null, 0, 0, 0, nearestDeadObject, nearestDeadDistance);
		}
		int distance = Translate.distance(hunter.getX(), hunter.getY(), candidate.getX(), candidate.getY());
		if (nearestDeadDistance > distance) {
			if (Barrier.acrossBarrier(hunter.getX(), hunter.getY(), candidate.getX(), candidate.getY(),
					Barrier.MAP_BODY[wallMode] + Barrier.BARRIER_KEKKAI)) {
				return new BodyCandidateResult(null, null, 0, 0, 0, nearestDeadObject, nearestDeadDistance);
			}
			nearestDeadObject = candidate;
			nearestDeadDistance = distance;
		}
		return new BodyCandidateResult(null, null, 0, 0, 0, nearestDeadObject, nearestDeadDistance);
	}

	public static final class BodyCandidateResult {
		private final Entity nearestLiveObject;
		private final Entity nearestOtherObject;
		private final int nearestLiveDistance;
		private final int nearestOtherDistance;
		private final int size;
		private final Entity nearestDeadObject;
		private final int nearestDeadDistance;

		BodyCandidateResult(Entity nearestLiveObject, Entity nearestOtherObject, int nearestLiveDistance,
				int nearestOtherDistance, int size) {
			this(nearestLiveObject, nearestOtherObject, nearestLiveDistance, nearestOtherDistance, size, null, 0);
		}

		BodyCandidateResult(Entity nearestLiveObject, Entity nearestOtherObject, int nearestLiveDistance,
				int nearestOtherDistance, int size, Entity nearestDeadObject,
				int nearestDeadDistance) {
			this.nearestLiveObject = nearestLiveObject;
			this.nearestOtherObject = nearestOtherObject;
			this.nearestLiveDistance = nearestLiveDistance;
			this.nearestOtherDistance = nearestOtherDistance;
			this.size = size;
			this.nearestDeadObject = nearestDeadObject;
			this.nearestDeadDistance = nearestDeadDistance;
		}

		public Entity getFound() {
			return nearestLiveObject;
		}

		public Entity getFound2() {
			return nearestOtherObject;
		}

		public int getMinDistance() {
			return nearestLiveDistance;
		}

		public int getMinDistance2() {
			return nearestOtherDistance;
		}

		public int getSize() {
			return size;
		}

		public Entity getFound3() {
			return nearestDeadObject;
		}

		public int getMinDistance3() {
			return nearestDeadDistance;
		}
	}
}
