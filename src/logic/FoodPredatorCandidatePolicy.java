package src.logic;

import src.base.Body;
import src.base.Obj;
import src.draw.Translate;
import src.enums.Intelligence;
import src.enums.PredatorType;
import src.item.Barrier;
import src.util.GameRandom;

/**
 * 捕食種向けの body 候補更新.
 */
public final class FoodPredatorCandidatePolicy {

	private FoodPredatorCandidatePolicy() {
	}

	/**
	 * live body を候補として評価する.
	 */
	public static BodyCandidateResult considerLiveBody(Body hunter, Body candidate, int nearestLiveDistance,
			int nearestOtherDistance, int candidateSize, int wallMode, Obj nearestLiveObject, Obj nearestOtherObject) {
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
	public static BodyCandidateResult considerDeadBody(Body hunter, Body candidate, int nearestDeadDistance, int wallMode,
			Obj nearestDeadObject) {
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
		private final Obj nearestLiveObject;
		private final Obj nearestOtherObject;
		private final int nearestLiveDistance;
		private final int nearestOtherDistance;
		private final int size;
		private final Obj nearestDeadObject;
		private final int nearestDeadDistance;

		BodyCandidateResult(Obj nearestLiveObject, Obj nearestOtherObject, int nearestLiveDistance, int nearestOtherDistance, int size) {
			this(nearestLiveObject, nearestOtherObject, nearestLiveDistance, nearestOtherDistance, size, null, 0);
		}

		BodyCandidateResult(Obj nearestLiveObject, Obj nearestOtherObject, int nearestLiveDistance, int nearestOtherDistance, int size, Obj nearestDeadObject,
				int nearestDeadDistance) {
			this.nearestLiveObject = nearestLiveObject;
			this.nearestOtherObject = nearestOtherObject;
			this.nearestLiveDistance = nearestLiveDistance;
			this.nearestOtherDistance = nearestOtherDistance;
			this.size = size;
			this.nearestDeadObject = nearestDeadObject;
			this.nearestDeadDistance = nearestDeadDistance;
		}

		public Obj getFound() {
			return nearestLiveObject;
		}

		public Obj getFound2() {
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

		public Obj getFound3() {
			return nearestDeadObject;
		}

		public int getMinDistance3() {
			return nearestDeadDistance;
		}
	}
}
