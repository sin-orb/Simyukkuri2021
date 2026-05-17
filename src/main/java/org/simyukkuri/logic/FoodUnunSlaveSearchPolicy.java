package org.simyukkuri.logic;

import java.util.Map;

import org.simyukkuri.draw.Translate;
import org.simyukkuri.entity.core.Entity;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.world.item.Food;
import org.simyukkuri.entity.core.world.mobile.Shit;
import org.simyukkuri.entity.core.world.mobile.Vomit;
import org.simyukkuri.enums.AgeState;
import org.simyukkuri.enums.TakeoutItemType;
import org.simyukkuri.field.impl.Barrier;
import org.simyukkuri.system.WorldState;
import org.simyukkuri.util.GameWorld;

/**
 * うんうん奴隷用の食料探索ルール。
 */
public final class FoodUnunSlaveSearchPolicy {
	private FoodUnunSlaveSearchPolicy() {
	}

	/**
	 * うんうん奴隷用の食べ物を探索して返す。
	 *
	 * @param body ゆっくり
	 * @param forceEat 強制給餌フラグ
	 *
	 * @return 対象を発見した場合はそのオブジェクト、見つからない場合は null
	 */
	public static Entity searchFoodForUnunSlave(Yukkuri body, boolean[] forceEat) {
		return searchFoodForUnunSlave(body, forceEat, GameWorld.get().getCurrentWorldState());
	}

	/**
	 * うんうん奴隷用の食べ物を探索して返す。
	 *
	 * @param body ゆっくり
	 * @param forceEat 強制給餌フラグ
	 * @param ws ワールド状態
	 *
	 * @return 対象を発見した場合はそのオブジェクト、見つからない場合は null
	 */
	public static Entity searchFoodForUnunSlave(Yukkuri body, boolean[] forceEat, WorldState ws) {
		Entity targetObject = null;
		int minDistance = body.getEyesightBase();
		int wallMode = body.getAgeState().ordinal();

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
			for (Map.Entry<Integer, Shit> entry : ws.getShit().entrySet()) {
				Shit shit = entry.getValue();
				if (minDistance < 1) {
					break;
				}
				int distance = Translate.distance(body.getX(), body.getY(), shit.getX(), shit.getY());
				if (minDistance > distance) {
					if (Barrier.acrossBarrier(body.getX(), body.getY(), shit.getX(), shit.getY(),
							Barrier.BODY_BLOCK_FLAGS[wallMode] + Barrier.BARRIER_KEKKAI)) {
						continue;
					}
					if (FoodLogic.checkTakeout(body, shit, ws)) {
						boolean hasOtherTarget = false;
						for (Map.Entry<Integer, Yukkuri> entry2 : ws.getYukkuriRegistry()
								.entrySet()) {
							Yukkuri otherBody = entry2.getValue();
							if (body == otherBody || otherBody == null || otherBody.isDead() || otherBody.isRemoved()) {
								continue;
							}
							Entity targetObjectByBody = otherBody.takeMoveTarget();
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
			for (Map.Entry<Integer, Vomit> entry : ws.getVomit().entrySet()) {
				Vomit vomit = entry.getValue();
				int distance = Translate.distance(body.getX(), body.getY(), vomit.getX(), vomit.getY());
				if (minDistance > distance) {
					if (Barrier.acrossBarrier(body.getX(), body.getY(), vomit.getX(), vomit.getY(),
							Barrier.BODY_BLOCK_FLAGS[wallMode] + Barrier.BARRIER_KEKKAI)) {
						continue;
					}
					targetObject = vomit;
					minDistance = distance;
				}
			}
		}

		if (targetObject == null) {
			for (Map.Entry<Integer, Yukkuri> entry : ws.getYukkuriRegistry().entrySet()) {
				Yukkuri candidateBody = entry.getValue();
				if (body == candidateBody) {
					continue;
				}
				if (!FoodLogic.checkCanEatYukkuri(body, candidateBody)) {
					continue;
				}
				if (!body.isSoHungry() || !body.isTooHungry()) {
					break;
				}
				int distance = Translate.distance(body.getX(), body.getY(), candidateBody.getX(), candidateBody.getY());
				if (minDistance > distance) {
					if (Barrier.acrossBarrier(body.getX(), body.getY(), candidateBody.getX(), candidateBody.getY(),
							Barrier.BODY_BLOCK_FLAGS[wallMode] + Barrier.BARRIER_KEKKAI)) {
						continue;
					}
					targetObject = candidateBody;
					minDistance = distance;
				}
			}
		}

		if (targetObject == null) {
			for (Map.Entry<Integer, Food> entry : ws.getFoods().entrySet()) {
				Food food = entry.getValue();
				if (food.isEmpty()) {
					continue;
				}
				int distance = Translate.distance(body.getX(), body.getY(), food.getX(), food.getY());
				if (minDistance > distance) {
					if (Barrier.acrossBarrier(body.getX(), body.getY(), food.getX(), food.getY(),
							Barrier.BODY_BLOCK_FLAGS[wallMode] + Barrier.BARRIER_KEKKAI)) {
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
