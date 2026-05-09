package src.logic;

import java.util.Map;

import src.base.Body;
import src.base.Obj;
import src.draw.Translate;
import src.enums.AgeState;
import src.enums.BodyRank;
import src.enums.PublicRank;
import src.enums.TakeoutItemType;
import src.game.Shit;
import src.game.Vomit;
import src.field.impl.Barrier;
import src.item.Food;
import src.util.GameWorld;

/**
 * うんうん奴隷用の食料探索ルール。
 */
public final class FoodUnunSlaveSearchPolicy {
	private FoodUnunSlaveSearchPolicy() {
	}

	public static Obj searchFoodForUnunSlave(Body body, boolean[] forceEat) {
		Obj targetObject = null;
		int minDistance = body.getEyesightBase();
		int wallMode = body.getBodyAgeState().ordinal();

		forceEat[0] = false;

		if (body.canflyCheck()) {
			wallMode = AgeState.ADULT.ordinal();
		}

		if (body.isVeryHungry()) {
			if (body.getCarryItem(TakeoutItemType.SHIT) != null) {
				body.dropTakeoutItem(TakeoutItemType.SHIT);
			}
		}

		if (targetObject == null) {
			for (Map.Entry<Integer, Shit> entry : GameWorld.get().getCurrentMap().getShit().entrySet()) {
				Shit shit = entry.getValue();
				if (minDistance < 1) {
					break;
				}
				int distance = Translate.distance(body.getX(), body.getY(), shit.getX(), shit.getY());
				if (minDistance > distance) {
					if (Barrier.acrossBarrier(body.getX(), body.getY(), shit.getX(), shit.getY(),
							Barrier.MAP_BODY[wallMode] + Barrier.BARRIER_KEKKAI)) {
						continue;
					}
					if (FoodLogic.checkTakeout(body, shit)) {
						boolean hasOtherTarget = false;
						for (Map.Entry<Integer, Body> entry2 : GameWorld.get().getCurrentMap().getBody().entrySet()) {
							Body otherBody = entry2.getValue();
							if (body == otherBody || otherBody == null || otherBody.isDead() || otherBody.isRemoved()) {
								continue;
							}
							Obj targetObjectByBody = otherBody.takeMoveTarget();
							if (shit == targetObjectByBody) {
								hasOtherTarget = true;
								break;
							}
						}
						if (hasOtherTarget) {
							continue;
						}
						body.setToTakeout(true);
						targetObject = shit;
					}
					if (!body.isToTakeout()) {
						targetObject = shit;
					}
					minDistance = distance;
				}
			}
		}

		if (targetObject == null) {
			for (Map.Entry<Integer, Vomit> entry : GameWorld.get().getCurrentMap().getVomit().entrySet()) {
				Vomit vomit = entry.getValue();
				int distance = Translate.distance(body.getX(), body.getY(), vomit.getX(), vomit.getY());
				if (minDistance > distance) {
					if (Barrier.acrossBarrier(body.getX(), body.getY(), vomit.getX(), vomit.getY(),
							Barrier.MAP_BODY[wallMode] + Barrier.BARRIER_KEKKAI)) {
						continue;
					}
					targetObject = vomit;
					minDistance = distance;
				}
			}
		}

		if (targetObject == null) {
			for (Map.Entry<Integer, Body> entry : GameWorld.get().getCurrentMap().getBody().entrySet()) {
				Body candidateBody = entry.getValue();
				if (body == candidateBody) {
					continue;
				}
				if (!FoodLogic.checkCanEatBody(body, candidateBody)) {
					continue;
				}
				if (!body.isSoHungry() || !body.isTooHungry()) {
					break;
				}
				int distance = Translate.distance(body.getX(), body.getY(), candidateBody.getX(), candidateBody.getY());
				if (minDistance > distance) {
					if (Barrier.acrossBarrier(body.getX(), body.getY(), candidateBody.getX(), candidateBody.getY(),
							Barrier.MAP_BODY[wallMode] + Barrier.BARRIER_KEKKAI)) {
						continue;
					}
					targetObject = candidateBody;
					minDistance = distance;
				}
			}
		}

		if (targetObject == null) {
			for (Map.Entry<Integer, Food> entry : GameWorld.get().getCurrentMap().getFood().entrySet()) {
				Food food = entry.getValue();
				if (food.isEmpty()) {
					continue;
				}
				int distance = Translate.distance(body.getX(), body.getY(), food.getX(), food.getY());
				if (minDistance > distance) {
					if (Barrier.acrossBarrier(body.getX(), body.getY(), food.getX(), food.getY(),
							Barrier.MAP_BODY[wallMode] + Barrier.BARRIER_KEKKAI)) {
						continue;
					}
					if (food.getFoodType() == Food.FoodType.WASTE ||
							food.getFoodType() == Food.FoodType.WASTE_NORA ||
							food.getFoodType() == Food.FoodType.WASTE_YASEI) {
						if (body.isTooHungry()) {
							targetObject = food;
							break;
						}
					}
				}
			}
		}
		return targetObject;
	}
}
