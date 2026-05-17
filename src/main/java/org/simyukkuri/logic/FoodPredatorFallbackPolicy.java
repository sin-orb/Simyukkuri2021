package org.simyukkuri.logic;

import java.util.List;
import java.util.Map;

import org.simyukkuri.draw.Translate;
import org.simyukkuri.entity.core.Entity;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.world.bodylinked.Stalk;
import org.simyukkuri.entity.core.world.mobile.Shit;
import org.simyukkuri.entity.core.world.mobile.Vomit;
import org.simyukkuri.enums.BurialState;
import org.simyukkuri.field.impl.Barrier;
import org.simyukkuri.system.WorldState;
import org.simyukkuri.util.GameWorld;

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
		return searchFallbackFood(body, nearestFood, fallbackFood, nearestDistance, wallMode,
				GameWorld.get().getCurrentWorldState());
	}

	/**
	 * フォールバック候補として最適な食べ物を探索して返す。
	 *
	 * @param body ゆっくり
	 * @param nearestFood 最も近い食べ物の現在候補
	 * @param fallbackFood フォールバック候補の食べ物
	 * @param nearestDistance 現在の最短距離（二乗値）
	 * @param wallMode 壁通過モード
	 * @param ws ワールド状態
	 *
	 * @return 対象を発見した場合はそのオブジェクト、見つからない場合は null
	 */
	public static Entity searchFallbackFood(Yukkuri body, Entity nearestFood, Entity fallbackFood, int nearestDistance,
			int wallMode, WorldState ws) {
		if (nearestFood == null && body.isFull()) {
			return nearestFood;
		}

		Entity selectedFood = nearestFood;

		// 非常食検索
		for (Map.Entry<Integer, Stalk> entry : ws.getStalks().entrySet()) {
			Stalk stalk = entry.getValue();
			Yukkuri plantBody = ws.getYukkuriRegistry().get(stalk.getPlantYukkuri());
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
				selectedFood = stalk;
				nearestDistance = distance;
			}
		}

		if (selectedFood == null) {
			selectedFood = fallbackFood;
		}

		if (selectedFood == null) {
			for (Map.Entry<Integer, Vomit> entry : ws.getVomit().entrySet()) {
				Vomit vomit = entry.getValue();
				int distance = Translate.distance(body.getX(), body.getY(), vomit.getX(), vomit.getY());
				if (nearestDistance > distance) {
					if (Barrier.acrossBarrier(body.getX(), body.getY(), vomit.getX(), vomit.getY(),
							Barrier.BODY_BLOCK_FLAGS[wallMode] + Barrier.BARRIER_KEKKAI)) {
						continue;
					}
					selectedFood = vomit;
					nearestDistance = distance;
				}
			}
		}
		if (selectedFood == null) {
			for (Map.Entry<Integer, Shit> entry : ws.getShit().entrySet()) {
				Shit shit = entry.getValue();
				if (!body.isTooHungry()) {
					break;
				}
				int distance = Translate.distance(body.getX(), body.getY(), shit.getX(), shit.getY());
				if (nearestDistance > distance) {
					if (Barrier.acrossBarrier(body.getX(), body.getY(), shit.getX(), shit.getY(),
							Barrier.BODY_BLOCK_FLAGS[wallMode] + Barrier.BARRIER_KEKKAI)) {
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
