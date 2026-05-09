package src.logic;

import java.util.List;
import java.util.Map;

import src.base.Yukkuri;
import src.base.Entity;
import src.draw.Translate;
import src.enums.AgeState;
import src.enums.BurialState;
import src.enums.Intelligence;
import src.game.Shit;
import src.game.Stalk;
import src.game.Vomit;
import src.field.impl.Barrier;
import src.item.Food;
import src.item.Food.FoodType;
import src.util.GameWorld;

/**
 * 近傍の餌を選ぶ最短候補探索.
 */
public final class FoodNearestSearchPolicy {

	private FoodNearestSearchPolicy() {
	}

	/**
	 * 足りないゆ、足焼き用 最も近いものを適当に食べる.
	 */
	public static Entity searchFoodNearest(Yukkuri body, boolean[] forceEat) {
		Entity nearestFood = null;
		int nearestDistance = body.getEyesightBase();
		int wallMode = body.getBodyAgeState().ordinal();
		forceEat[0] = false;
		if (body.isFull()) {
			return null;
		}

		if (body.canflyCheck()) {
			wallMode = AgeState.ADULT.ordinal();
		}

		for (Map.Entry<Integer, Food> entry : GameWorld.get().getCurrentMap().getFood().entrySet()) {
			Food food = entry.getValue();
			if (food.isEmpty()) {
				continue;
			}
			if (nearestDistance < 1) {
				break;
			}
			int distance = Translate.distance(body.getX(), body.getY(), food.getX(), food.getY());
			if (nearestDistance > distance) {
				if (Barrier.acrossBarrier(body.getX(), body.getY(), food.getX(), food.getY(),
						Barrier.MAP_BODY[wallMode] + Barrier.BARRIER_KEKKAI)) {
					continue;
				}
				nearestFood = food;
				nearestDistance = distance;
			}
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
			if (nearestDistance > distance) {
				if (Barrier.acrossBarrier(body.getX(), body.getY(), stalk.getX(), stalk.getY(),
						Barrier.MAP_BODY[wallMode] + Barrier.BARRIER_KEKKAI)) {
					continue;
				}
				nearestFood = stalk;
				nearestDistance = distance;
			}
		}
		for (Map.Entry<Integer, Vomit> entry : GameWorld.get().getCurrentMap().getVomit().entrySet()) {
			Vomit vomit = entry.getValue();
			int distance = Translate.distance(body.getX(), body.getY(), vomit.getX(), vomit.getY());
			if (nearestDistance > distance) {
				if (Barrier.acrossBarrier(body.getX(), body.getY(), vomit.getX(), vomit.getY(),
						Barrier.MAP_BODY[wallMode] + Barrier.BARRIER_KEKKAI)) {
					continue;
				}
				nearestFood = vomit;
				nearestDistance = distance;
			}
		}
		for (Map.Entry<Integer, Yukkuri> entry : GameWorld.get().getCurrentMap().getBody().entrySet()) {
			Yukkuri candidateBody = entry.getValue();
			if (body == candidateBody) {
				continue;
			}
			if (!FoodLogic.checkCanEatBody(body, candidateBody)) {
				continue;
			}
			int distance = Translate.distance(body.getX(), body.getY(), candidateBody.getX(), candidateBody.getY());
			if (nearestDistance > distance) {
				if (Barrier.acrossBarrier(body.getX(), body.getY(), candidateBody.getX(), candidateBody.getY(),
						Barrier.MAP_BODY[wallMode] + Barrier.BARRIER_KEKKAI)) {
					continue;
				}
				nearestFood = candidateBody;
				nearestDistance = distance;
			}
		}
		for (Map.Entry<Integer, Shit> entry : GameWorld.get().getCurrentMap().getShit().entrySet()) {
			Shit shit = entry.getValue();
			int distance = Translate.distance(body.getX(), body.getY(), shit.getX(), shit.getY());
			if (nearestDistance > distance) {
				if (Barrier.acrossBarrier(body.getX(), body.getY(), shit.getX(), shit.getY(),
						Barrier.MAP_BODY[wallMode] + Barrier.BARRIER_KEKKAI)) {
					continue;
				}
				nearestFood = shit;
				nearestDistance = distance;
			}
		}
		return nearestFood;
	}
}
