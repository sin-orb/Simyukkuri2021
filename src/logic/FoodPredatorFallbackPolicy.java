package src.logic;

import java.util.List;
import java.util.Map;

import src.draw.Translate;
import src.entity.core.Entity;
import src.entity.core.living.yukkuri.Yukkuri;
import src.entity.core.world.bodylinked.Stalk;
import src.entity.core.world.mobile.Shit;
import src.entity.core.world.mobile.Vomit;
import src.enums.BurialState;
import src.field.impl.Barrier;
import src.util.GameWorld;

/**
 * 捕食種向けの非 body 食候補検索.
 */
public final class FoodPredatorFallbackPolicy {

	private FoodPredatorFallbackPolicy() {
	}

	/**
	 * 残りの食候補を探索する.
	 */
	public static Entity searchFallbackFood(Yukkuri body, Entity nearestFood, Entity fallbackFood, int nearestDistance,
			int wallMode) {
		if (nearestFood == null && body.isFull()) {
			return nearestFood;
		}

		Entity selectedFood = nearestFood;

		// 非常食検索
		for (Map.Entry<Integer, Stalk> entry : GameWorld.get().getCurrentMap().getStalk().entrySet()) {
			Stalk stalk = entry.getValue();
			Yukkuri plantBody = GameWorld.get().getCurrentMap().getBody().get(stalk.getPlantYukkuri());
			if (plantBody != null) {
				if (plantBody == body) {
					continue;
				}
				// 地中に埋まっているなら引っこ抜いて食べる
				if (plantBody.getBurialState() != BurialState.ALL &&
						!(plantBody.getBurialState() == BurialState.NEARLY_ALL && !plantBody.hasOkazari())) {
					continue;
				}

				// 通常は実ゆつきは食べない
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
				selectedFood = stalk;
				nearestDistance = distance;
			}
		}

		if (selectedFood == null) {
			selectedFood = fallbackFood;
		}

		if (selectedFood == null) {
			for (Map.Entry<Integer, Vomit> entry : GameWorld.get().getCurrentMap().getVomit().entrySet()) {
				Vomit vomit = entry.getValue();
				int distance = Translate.distance(body.getX(), body.getY(), vomit.getX(), vomit.getY());
				if (nearestDistance > distance) {
					if (Barrier.acrossBarrier(body.getX(), body.getY(), vomit.getX(), vomit.getY(),
							Barrier.MAP_BODY[wallMode] + Barrier.BARRIER_KEKKAI)) {
						continue;
					}
					selectedFood = vomit;
					nearestDistance = distance;
				}
			}
		}
		if (selectedFood == null) {
			for (Map.Entry<Integer, Shit> entry : GameWorld.get().getCurrentMap().getShit().entrySet()) {
				Shit shit = entry.getValue();
				if (!body.isTooHungry()) {
					break;
				}
				int distance = Translate.distance(body.getX(), body.getY(), shit.getX(), shit.getY());
				if (nearestDistance > distance) {
					if (Barrier.acrossBarrier(body.getX(), body.getY(), shit.getX(), shit.getY(),
							Barrier.MAP_BODY[wallMode] + Barrier.BARRIER_KEKKAI)) {
						continue;
					}
					selectedFood = shit;
					nearestDistance = distance;
				}
			}
		}

		return selectedFood;
	}
}
