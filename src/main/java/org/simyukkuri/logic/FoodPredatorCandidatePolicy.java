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
	public static SearchResult considerLiveYukkuri(Yukkuri hunter, Yukkuri candidate, int nearestLiveDistance,
			int nearestOtherDistance, int candidateSize, int wallMode, Entity nearestLiveObject,
			Entity nearestOtherObject) {
		if (hunter.getIntelligence() != Intelligence.FOOL && hunter.findSick(candidate) && !hunter.isTooHungry()) {
			return new SearchResult(nearestLiveObject, nearestOtherObject, nearestLiveDistance,
					nearestOtherDistance, candidateSize);
		}
		if (candidate.isPredatorType()) {
			return new SearchResult(nearestLiveObject, nearestOtherObject, nearestLiveDistance,
					nearestOtherDistance, candidateSize);
		}
		if (hunter.isFamily(candidate)) {
			return new SearchResult(nearestLiveObject, nearestOtherObject, nearestLiveDistance,
					nearestOtherDistance, candidateSize);
		}
		if (!hunter.canflyCheck() && candidate.getZ() != 0) {
			return new SearchResult(nearestLiveObject, nearestOtherObject, nearestLiveDistance,
					nearestOtherDistance, candidateSize);
		}
		if (candidate.isServantOf(hunter.getType()) && hunter.isPredator()) {
			return new SearchResult(nearestLiveObject, nearestOtherObject, nearestLiveDistance,
					nearestOtherDistance, candidateSize);
		}

		int distance = Translate.distance(hunter.getX(), hunter.getY(), candidate.getX(), candidate.getY());
		if (candidate.getAgeState().ordinal() < hunter.getAgeState().ordinal()) {
			if (nearestLiveDistance > distance || candidate.getAgeState().ordinal() < candidateSize) {
				if (Barrier.acrossBarrier(hunter.getX(), hunter.getY(), candidate.getX(), candidate.getY(),
						Barrier.MAP_BODY[wallMode] + Barrier.BARRIER_KEKKAI)) {
					return new SearchResult(nearestLiveObject, nearestOtherObject, nearestLiveDistance,
							nearestOtherDistance, candidateSize);
				}
				nearestLiveObject = candidate;
				nearestLiveDistance = distance;
				candidateSize = candidate.getAgeState().ordinal();
			}
		} else {
			if (nearestOtherDistance > distance) {
				if (Barrier.acrossBarrier(hunter.getX(), hunter.getY(), candidate.getX(), candidate.getY(),
						Barrier.MAP_BODY[wallMode] + Barrier.BARRIER_KEKKAI)) {
					return new SearchResult(nearestLiveObject, nearestOtherObject, nearestLiveDistance,
							nearestOtherDistance, candidateSize);
				}
				nearestOtherObject = candidate;
				nearestOtherDistance = distance;
			}
		}
		return new SearchResult(nearestLiveObject, nearestOtherObject, nearestLiveDistance,
				nearestOtherDistance, candidateSize);
	}

	/**
	 * 死体候補を評価する.
	 */
	public static SearchResult considerDeadYukkuri(Yukkuri hunter, Yukkuri candidate, int nearestDeadDistance,
			int wallMode,
			Entity nearestDeadObject) {
		if (!hunter.isRude() && candidate.hasOkazari() && hunter.isFamily(candidate)) {
			return new SearchResult(null, null, 0, 0, 0, nearestDeadObject, nearestDeadDistance);
		}
		int distance = Translate.distance(hunter.getX(), hunter.getY(), candidate.getX(), candidate.getY());
		if (nearestDeadDistance > distance) {
			if (Barrier.acrossBarrier(hunter.getX(), hunter.getY(), candidate.getX(), candidate.getY(),
					Barrier.MAP_BODY[wallMode] + Barrier.BARRIER_KEKKAI)) {
				return new SearchResult(null, null, 0, 0, 0, nearestDeadObject, nearestDeadDistance);
			}
			nearestDeadObject = candidate;
			nearestDeadDistance = distance;
		}
		return new SearchResult(null, null, 0, 0, 0, nearestDeadObject, nearestDeadDistance);
	}

	public static final class SearchResult {
		private final Entity nearestLiveObject;
		private final Entity nearestOtherObject;
		private final int nearestLiveDistance;
		private final int nearestOtherDistance;
		private final int size;
		private final Entity nearestDeadObject;
		private final int nearestDeadDistance;

		SearchResult(Entity nearestLiveObject, Entity nearestOtherObject, int nearestLiveDistance,
				int nearestOtherDistance, int size) {
			this(nearestLiveObject, nearestOtherObject, nearestLiveDistance, nearestOtherDistance, size, null, 0);
		}

		SearchResult(Entity nearestLiveObject, Entity nearestOtherObject, int nearestLiveDistance,
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

		public Entity getNearestLiveObject() {
			return nearestLiveObject;
		}

		public Entity getNearestOtherObject() {
			return nearestOtherObject;
		}

		public int getNearestLiveDistance() {
			return nearestLiveDistance;
		}

		public int getNearestOtherDistance() {
			return nearestOtherDistance;
		}

		public int getSize() {
			return size;
		}

		public Entity getNearestDeadObject() {
			return nearestDeadObject;
		}

		public int getNearestDeadDistance() {
			return nearestDeadDistance;
		}
	}
}
