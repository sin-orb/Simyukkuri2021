package src.logic;

import java.util.List;
import java.util.Map;

import src.base.Body;
import src.base.Obj;
import src.draw.Translate;
import src.enums.BaryInUGState;
import src.enums.Intelligence;
import src.enums.TangType;
import src.game.Shit;
import src.game.Stalk;
import src.game.Vomit;
import src.item.Barrier;
import src.item.Food;
import src.item.Food.FoodType;
import src.util.GameWorld;
import src.util.YukkuriUtil;

/**
 * 捕食種向けの food 候補探索.
 */
public final class FoodPredatorFoodPolicy {

	private FoodPredatorFoodPolicy() {
	}

	/**
	 * Food / stalk / vomit / shit をまとめて探索する.
	 */
	public static FoodSearchResult searchFood(Body b, boolean[] forceEat, int wallMode, Obj found, Obj found3,
			int minDistance, int looks) {
		Obj result = found;
		Obj deadCandidate = found3;
		int distanceLimit = minDistance;
		int bestLooks = looks;

		for (Map.Entry<Integer, Food> entry : GameWorld.get().getCurrentMap().getFood().entrySet()) {
			Food f = entry.getValue();
			if (f.isEmpty()) {
				continue;
			}
			int distance = Translate.distance(b.getX(), b.getY(), f.getX(), f.getY());
			if (distanceLimit > distance) {
				if (Barrier.acrossBarrier(b.getX(), b.getY(), f.getX(), f.getY(),
						Barrier.MAP_BODY[wallMode] + Barrier.BARRIER_KEKKAI)) {
					continue;
				}
				boolean flag = false;
				switch (f.getFoodType()) {
				case STALK:
					if (b.isBaby()) {
						if (!b.isbFirstEatStalk()) {
							flag = true;
							forceEat[0] = true;
						} else if (b.isHungry()) {
							flag = true;
						}
					} else if (b.isRude() && b.isSoHungry()) {
						flag = true;
					} else if (!b.isRude() && b.isVeryHungry()) {
						flag = true;
					} else if (!b.isRude() && b.isRaper()) {
						flag = true;
					}
					break;
				case SWEETS1:
				case SWEETS2:
				case SWEETS_NORA1:
				case SWEETS_NORA2:
				case SWEETS_YASEI1:
				case SWEETS_YASEI2:
					if (!b.isTooFull()) {
						flag = true;
					} else if (!b.isOverEating() && (b.isRude() || b.isNormal())) {
						flag = true;
						forceEat[0] = true;
					}
					break;
				case WASTE:
				case WASTE_NORA:
				case WASTE_YASEI:
					if (b.isTooHungry() || b.getTangType() == TangType.POOR) {
						flag = true;
					}
					break;
				default:
					if (!b.isFull()) {
						flag = true;
					}
					break;
				}
				if (flag && bestLooks <= f.getLooks()) {
					deadCandidate = f;
					distanceLimit = distance;
					bestLooks = f.getLooks();
				}
			}
		}

		if (deadCandidate != null && (result == null || b.getIntelligence() == Intelligence.WISE || b.isDamaged())) {
			result = deadCandidate;
		}
		if (deadCandidate != null && forceEat[0]) {
			result = deadCandidate;
		}
		if (result == null && b.isFull()) {
			return new FoodSearchResult(result, distanceLimit, bestLooks);
		}

		for (Map.Entry<Integer, Stalk> entry : GameWorld.get().getCurrentMap().getStalk().entrySet()) {
			Stalk s = entry.getValue();
			Body p = GameWorld.get().getCurrentMap().getBody().get(s.getPlantYukkuri());
			if (p != null) {
				if (p == b) {
					continue;
				}
				if (p.getBaryState() != BaryInUGState.ALL &&
						!(p.getBaryState() == BaryInUGState.NEARLY_ALL && !p.hasOkazari())) {
					continue;
				}
				List<Integer> babyList = s.getBindBabies();
				if (babyList != null && babyList.size() != 0) {
					boolean bBabyFlag = false;
					for (int ibaby : babyList) {
						Body baby = YukkuriUtil.getBodyInstance(ibaby);
						if (baby == null) {
							continue;
						}
						bBabyFlag = true;
						break;
					}
					if (bBabyFlag) {
						continue;
					}
				}
			}

			int distance = Translate.distance(b.getX(), b.getY(), s.getX(), s.getY());
			if (distanceLimit > distance) {
				if (Barrier.acrossBarrier(b.getX(), b.getY(), s.getX(), s.getY(),
						Barrier.MAP_BODY[wallMode] + Barrier.BARRIER_KEKKAI)) {
					continue;
				}
				result = s;
				distanceLimit = distance;
			}
		}
		if (result == null) {
			result = deadCandidate;
		}

		if (result == null) {
			for (Map.Entry<Integer, Vomit> entry : GameWorld.get().getCurrentMap().getVomit().entrySet()) {
				Vomit v = entry.getValue();
				int distance = Translate.distance(b.getX(), b.getY(), v.getX(), v.getY());
				if (distanceLimit > distance) {
					if (Barrier.acrossBarrier(b.getX(), b.getY(), v.getX(), v.getY(),
							Barrier.MAP_BODY[wallMode] + Barrier.BARRIER_KEKKAI)) {
						continue;
					}
					result = v;
					distanceLimit = distance;
				}
			}
		}
		if (result == null) {
			for (Map.Entry<Integer, Shit> entry : GameWorld.get().getCurrentMap().getShit().entrySet()) {
				Shit s = entry.getValue();
				if (!b.isTooHungry()) {
					break;
				}
				int distance = Translate.distance(b.getX(), b.getY(), s.getX(), s.getY());
				if (distanceLimit > distance) {
					if (Barrier.acrossBarrier(b.getX(), b.getY(), s.getX(), s.getY(),
							Barrier.MAP_BODY[wallMode] + Barrier.BARRIER_KEKKAI)) {
						continue;
					}
					result = s;
					distanceLimit = distance;
				}
			}
		}
		return new FoodSearchResult(result, distanceLimit, bestLooks);
	}

	public static final class FoodSearchResult {
		private final Obj found;
		private final int minDistance;
		private final int looks;

		FoodSearchResult(Obj found, int minDistance, int looks) {
			this.found = found;
			this.minDistance = minDistance;
			this.looks = looks;
		}

		public Obj getFound() {
			return found;
		}

		public int getMinDistance() {
			return minDistance;
		}

		public int getLooks() {
			return looks;
		}
	}
}
