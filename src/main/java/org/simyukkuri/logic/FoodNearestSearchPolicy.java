package org.simyukkuri.logic;

import java.util.List;
import java.util.Map;

import org.simyukkuri.draw.Translate;
import org.simyukkuri.entity.core.Entity;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.world.bodylinked.Stalk;
import org.simyukkuri.entity.core.world.item.Food;
import org.simyukkuri.entity.core.world.mobile.Shit;
import org.simyukkuri.entity.core.world.mobile.Vomit;
import org.simyukkuri.enums.AgeState;
import org.simyukkuri.enums.BurialState;
import org.simyukkuri.field.impl.Barrier;
import org.simyukkuri.util.GameWorld;

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
		int wallMode = body.getAgeState().ordinal();
		forceEat[0] = false;
		if (body.isFull()) {
			return null;
		}

		if (body.canflyCheck()) {
			wallMode = AgeState.ADULT.ordinal();
		}

		for (Map.Entry<Integer, Food> entry : GameWorld.get().getCurrentWorldState().getFoods().entrySet()) {
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
						Barrier.BODY_BLOCK_FLAGS[wallMode] + Barrier.BARRIER_KEKKAI)) {
					continue;
				}
				nearestFood = food;
				nearestDistance = distance;
			}
		}
		for (Map.Entry<Integer, Stalk> entry : GameWorld.get().getCurrentWorldState().getStalks().entrySet()) {
			Stalk stalk = entry.getValue();
			Yukkuri plantBody = GameWorld.get().getCurrentWorldState().getYukkuriRegistry().get(stalk.getPlantYukkuri());
			if (plantBody != null) {
				if (plantBody == body) {
					continue;
				}
				if (plantBody.getBurialState() != BurialState.ALL &&
						!(plantBody.getBurialState() == BurialState.NEARLY_ALL && !plantBody.hasOkazari())) {
					continue;
				}

				List<Integer> babyList = stalk.getAttachedBabyIds();
				if (babyList != null && babyList.size() != 0) {
					boolean hasBaby = false;
					for (int babyId : babyList) {
						Yukkuri baby = org.simyukkuri.util.YukkuriLookup.getYukkuriById(babyId);
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
						Barrier.BODY_BLOCK_FLAGS[wallMode] + Barrier.BARRIER_KEKKAI)) {
					continue;
				}
				nearestFood = stalk;
				nearestDistance = distance;
			}
		}
		for (Map.Entry<Integer, Vomit> entry : GameWorld.get().getCurrentWorldState().getVomit().entrySet()) {
			Vomit vomit = entry.getValue();
			int distance = Translate.distance(body.getX(), body.getY(), vomit.getX(), vomit.getY());
			if (nearestDistance > distance) {
				if (Barrier.acrossBarrier(body.getX(), body.getY(), vomit.getX(), vomit.getY(),
						Barrier.BODY_BLOCK_FLAGS[wallMode] + Barrier.BARRIER_KEKKAI)) {
					continue;
				}
				nearestFood = vomit;
				nearestDistance = distance;
			}
		}
		for (Map.Entry<Integer, Yukkuri> entry : GameWorld.get().getCurrentWorldState().getYukkuriRegistry().entrySet()) {
			Yukkuri candidateBody = entry.getValue();
			if (body == candidateBody) {
				continue;
			}
			if (!FoodLogic.checkCanEatYukkuri(body, candidateBody)) {
				continue;
			}
			int distance = Translate.distance(body.getX(), body.getY(), candidateBody.getX(), candidateBody.getY());
			if (nearestDistance > distance) {
				if (Barrier.acrossBarrier(body.getX(), body.getY(), candidateBody.getX(), candidateBody.getY(),
						Barrier.BODY_BLOCK_FLAGS[wallMode] + Barrier.BARRIER_KEKKAI)) {
					continue;
				}
				nearestFood = candidateBody;
				nearestDistance = distance;
			}
		}
		for (Map.Entry<Integer, Shit> entry : GameWorld.get().getCurrentWorldState().getShit().entrySet()) {
			Shit shit = entry.getValue();
			int distance = Translate.distance(body.getX(), body.getY(), shit.getX(), shit.getY());
			if (nearestDistance > distance) {
				if (Barrier.acrossBarrier(body.getX(), body.getY(), shit.getX(), shit.getY(),
						Barrier.BODY_BLOCK_FLAGS[wallMode] + Barrier.BARRIER_KEKKAI)) {
					continue;
				}
				nearestFood = shit;
				nearestDistance = distance;
			}
		}
		return nearestFood;
	}
}
