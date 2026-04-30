package src.logic;

import src.base.Body;
import src.base.Obj;
import src.draw.Translate;
import src.enums.Intelligence;
import src.enums.PredatorType;
import src.item.Barrier;
import src.yukkuri.Fran;
import src.yukkuri.Meirin;
import src.yukkuri.Remirya;
import src.yukkuri.Sakuya;
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
	public static BodyCandidateResult considerLiveBody(Body b, Body d, int minDistance, int minDistance2, int size,
			int wallMode, Obj found, Obj found2) {
		if (b.getIntelligence() != Intelligence.FOOL && b.findSick(d) && !b.isTooHungry()) {
			return new BodyCandidateResult(found, found2, minDistance, minDistance2, size);
		}
		if (d.isPredatorType()) {
			return new BodyCandidateResult(found, found2, minDistance, minDistance2, size);
		}
		if (b.isFamily(d)) {
			return new BodyCandidateResult(found, found2, minDistance, minDistance2, size);
		}
		if (!b.canflyCheck() && d.getZ() != 0) {
			return new BodyCandidateResult(found, found2, minDistance, minDistance2, size);
		}
		if ((d.getType() == Sakuya.type || d.getType() == Meirin.type)
				&& (b.getType() == Remirya.type || b.getType() == Fran.type)) {
			return new BodyCandidateResult(found, found2, minDistance, minDistance2, size);
		}

		int distance = Translate.distance(b.getX(), b.getY(), d.getX(), d.getY());
		if (d.getBodyAgeState().ordinal() < b.getBodyAgeState().ordinal()) {
			if (minDistance > distance || d.getBodyAgeState().ordinal() < size) {
				if (Barrier.acrossBarrier(b.getX(), b.getY(), d.getX(), d.getY(),
						Barrier.MAP_BODY[wallMode] + Barrier.BARRIER_KEKKAI)) {
					return new BodyCandidateResult(found, found2, minDistance, minDistance2, size);
				}
				found = d;
				minDistance = distance;
				size = d.getBodyAgeState().ordinal();
			}
		} else {
			if (minDistance2 > distance) {
				if (Barrier.acrossBarrier(b.getX(), b.getY(), d.getX(), d.getY(),
						Barrier.MAP_BODY[wallMode] + Barrier.BARRIER_KEKKAI)) {
					return new BodyCandidateResult(found, found2, minDistance, minDistance2, size);
				}
				found2 = d;
				minDistance2 = distance;
			}
		}
		return new BodyCandidateResult(found, found2, minDistance, minDistance2, size);
	}

	/**
	 * 死体候補を評価する.
	 */
	public static BodyCandidateResult considerDeadBody(Body b, Body d, int minDistance3, int wallMode, Obj found3) {
		if (!b.isRude() && d.hasOkazari() && b.isFamily(d)) {
			return new BodyCandidateResult(null, null, 0, 0, 0, found3, minDistance3);
		}
		int distance = Translate.distance(b.getX(), b.getY(), d.getX(), d.getY());
		if (minDistance3 > distance) {
			if (Barrier.acrossBarrier(b.getX(), b.getY(), d.getX(), d.getY(),
					Barrier.MAP_BODY[wallMode] + Barrier.BARRIER_KEKKAI)) {
				return new BodyCandidateResult(null, null, 0, 0, 0, found3, minDistance3);
			}
			found3 = d;
			minDistance3 = distance;
		}
		return new BodyCandidateResult(null, null, 0, 0, 0, found3, minDistance3);
	}

	public static final class BodyCandidateResult {
		private final Obj found;
		private final Obj found2;
		private final int minDistance;
		private final int minDistance2;
		private final int size;
		private final Obj found3;
		private final int minDistance3;

		BodyCandidateResult(Obj found, Obj found2, int minDistance, int minDistance2, int size) {
			this(found, found2, minDistance, minDistance2, size, null, 0);
		}

		BodyCandidateResult(Obj found, Obj found2, int minDistance, int minDistance2, int size, Obj found3,
				int minDistance3) {
			this.found = found;
			this.found2 = found2;
			this.minDistance = minDistance;
			this.minDistance2 = minDistance2;
			this.size = size;
			this.found3 = found3;
			this.minDistance3 = minDistance3;
		}

		public Obj getFound() {
			return found;
		}

		public Obj getFound2() {
			return found2;
		}

		public int getMinDistance() {
			return minDistance;
		}

		public int getMinDistance2() {
			return minDistance2;
		}

		public int getSize() {
			return size;
		}

		public Obj getFound3() {
			return found3;
		}

		public int getMinDistance3() {
			return minDistance3;
		}
	}
}
