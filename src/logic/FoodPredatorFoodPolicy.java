package src.logic;

import java.util.List;
import java.util.Map;

import src.base.Yukkuri;
import src.base.Entity;
import src.draw.Translate;
import src.enums.BurialState;
import src.enums.Intelligence;
import src.enums.TangType;
import src.game.Shit;
import src.game.Stalk;
import src.game.Vomit;
import src.field.impl.Barrier;
import src.item.Food;
import src.item.Food.FoodType;
import src.util.GameWorld;

/**
 * 捕食種向けの food 候補探索.
 */
public final class FoodPredatorFoodPolicy {

	private FoodPredatorFoodPolicy() {
	}

	/**
	 * Food / stalk / vomit / shit をまとめて探索する.
	 */
	public static FoodSearchResult searchFood(Yukkuri body, boolean[] forceEat, int wallMode, Entity nearestObject,
			Entity nearestDeadObject, int nearestDistance, int looks) {
		Entity result = nearestObject;
		Entity deadCandidate = nearestDeadObject;
		int distanceLimit = nearestDistance;
		int bestLooks = looks;

		for (Map.Entry<Integer, Food> entry : GameWorld.get().getCurrentMap().getFood().entrySet()) {
			Food food = entry.getValue();
			if (food.isEmpty()) {
				continue;
			}
			int distance = Translate.distance(body.getX(), body.getY(), food.getX(), food.getY());
			if (distanceLimit > distance) {
				if (Barrier.acrossBarrier(body.getX(), body.getY(), food.getX(), food.getY(),
						Barrier.MAP_BODY[wallMode] + Barrier.BARRIER_KEKKAI)) {
					continue;
				}
				boolean acceptable = false;
				switch (food.getFoodType()) {
				case STALK:
					if (body.isBaby()) {
						if (!body.isFirstEatStalk()) {
							acceptable = true;
							forceEat[0] = true;
						} else if (body.isHungry()) {
							acceptable = true;
						}
					} else if (body.isRude() && body.isSoHungry()) {
						acceptable = true;
					} else if (!body.isRude() && body.isVeryHungry()) {
						acceptable = true;
					} else if (!body.isRude() && body.isRaper()) {
						acceptable = true;
					}
					break;
				case SWEETS1:
				case SWEETS2:
				case SWEETS_NORA1:
				case SWEETS_NORA2:
				case SWEETS_YASEI1:
				case SWEETS_YASEI2:
					if (!body.isTooFull()) {
						acceptable = true;
					} else if (!body.isOverEating() && (body.isRude() || body.isNormal())) {
						acceptable = true;
						forceEat[0] = true;
					}
					break;
				case WASTE:
				case WASTE_NORA:
				case WASTE_YASEI:
					if (body.isTooHungry() || body.getTangType() == TangType.POOR) {
						acceptable = true;
					}
					break;
				default:
					if (!body.isFull()) {
						acceptable = true;
					}
					break;
				}
				if (acceptable && bestLooks <= food.getLooks()) {
					deadCandidate = food;
					distanceLimit = distance;
					bestLooks = food.getLooks();
				}
			}
		}

		if (deadCandidate != null && (result == null || body.getIntelligence() == Intelligence.WISE || body.isDamaged())) {
			result = deadCandidate;
		}
		if (deadCandidate != null && forceEat[0]) {
			result = deadCandidate;
		}
		if (result == null && body.isFull()) {
			return new FoodSearchResult(result, distanceLimit, bestLooks);
		}

		for (Map.Entry<Integer, Stalk> entry : GameWorld.get().getCurrentMap().getStalk().entrySet()) {
			Stalk stalk = entry.getValue();
			Yukkuri plantBody = GameWorld.get().getCurrentMap().getBody().get(stalk.getPlantYukkuri());
			if (plantBody != null) {
				if (plantBody == body) {
					continue;
				}
				if (plantBody.getBurialState() != BurialState.ALL &&
						!(plantBody.getBurialState() == BurialState.NEARLY_ALL && !plantBody.hasOkazari())) {
					continue;
				}
				List<Integer> babyList = stalk.getBindBabies();
				if (babyList != null && babyList.size() != 0) {
					boolean hasBaby = false;
					for (int babyId : babyList) {
						Yukkuri baby = src.util.BodyRegistry.getBodyInstance(babyId);
						if (baby == null) {
							continue;
						}
						hasBaby = true;
						break;
					}
					if (hasBaby) {
						continue;
					}
				}
			}

			int distance = Translate.distance(body.getX(), body.getY(), stalk.getX(), stalk.getY());
			if (distanceLimit > distance) {
				if (Barrier.acrossBarrier(body.getX(), body.getY(), stalk.getX(), stalk.getY(),
						Barrier.MAP_BODY[wallMode] + Barrier.BARRIER_KEKKAI)) {
					continue;
				}
				result = stalk;
				distanceLimit = distance;
			}
		}
		if (result == null) {
			result = deadCandidate;
		}

		if (result == null) {
			for (Map.Entry<Integer, Vomit> entry : GameWorld.get().getCurrentMap().getVomit().entrySet()) {
				Vomit vomit = entry.getValue();
				int distance = Translate.distance(body.getX(), body.getY(), vomit.getX(), vomit.getY());
				if (distanceLimit > distance) {
					if (Barrier.acrossBarrier(body.getX(), body.getY(), vomit.getX(), vomit.getY(),
							Barrier.MAP_BODY[wallMode] + Barrier.BARRIER_KEKKAI)) {
						continue;
					}
					result = vomit;
					distanceLimit = distance;
				}
			}
		}
		if (result == null) {
			for (Map.Entry<Integer, Shit> entry : GameWorld.get().getCurrentMap().getShit().entrySet()) {
				Shit shit = entry.getValue();
				if (!body.isTooHungry()) {
					break;
				}
				int distance = Translate.distance(body.getX(), body.getY(), shit.getX(), shit.getY());
				if (distanceLimit > distance) {
					if (Barrier.acrossBarrier(body.getX(), body.getY(), shit.getX(), shit.getY(),
							Barrier.MAP_BODY[wallMode] + Barrier.BARRIER_KEKKAI)) {
						continue;
					}
					result = shit;
					distanceLimit = distance;
				}
			}
		}
		return new FoodSearchResult(result, distanceLimit, bestLooks);
	}

	public static final class FoodSearchResult {
		private final Entity nearestObject;
		private final int nearestDistance;
		private final int looks;

		FoodSearchResult(Entity nearestObject, int nearestDistance, int looks) {
			this.nearestObject = nearestObject;
			this.nearestDistance = nearestDistance;
			this.looks = looks;
		}

		public Entity getFound() {
			return nearestObject;
		}

		public int getMinDistance() {
			return nearestDistance;
		}

		public int getLooks() {
			return looks;
		}
	}
}
