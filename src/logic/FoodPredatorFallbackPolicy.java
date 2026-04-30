package src.logic;

import java.util.List;
import java.util.Map;

import src.base.Body;
import src.base.Obj;
import src.draw.Translate;
import src.item.Barrier;
import src.item.Food;
import src.game.Shit;
import src.game.Vomit;
import src.enums.BaryInUGState;
import src.util.GameWorld;
import src.util.YukkuriUtil;

/**
 * 捕食種向けの非 body 食候補検索.
 */
public final class FoodPredatorFallbackPolicy {

	private FoodPredatorFallbackPolicy() {
	}

	/**
	 * 残りの食候補を探索する.
	 */
	public static Obj searchFallbackFood(Body b, Obj found, Obj found3, int minDistance, int wallMode) {
		if (found == null && b.isFull()) {
			return found;
		}

		Obj result = found;

		// 非常食検索
		for (Map.Entry<Integer, src.game.Stalk> entry : GameWorld.get().getCurrentMap().getStalk().entrySet()) {
			src.game.Stalk s = entry.getValue();
			Body p = GameWorld.get().getCurrentMap().getBody().get(s.getPlantYukkuri());
			if (p != null) {
				if (p == b) {
					continue;
				}
				// 地中に埋まっているなら引っこ抜いて食べる
				if (p.getBaryState() != BaryInUGState.ALL &&
						!(p.getBaryState() == BaryInUGState.NEARLY_ALL && !p.hasOkazari())) {
					continue;
				}

				// 通常は実ゆつきは食べない
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
			if (minDistance > distance) {
				if (Barrier.acrossBarrier(b.getX(), b.getY(), s.getX(), s.getY(),
						Barrier.MAP_BODY[wallMode] + Barrier.BARRIER_KEKKAI)) {
					continue;
				}
				result = s;
				minDistance = distance;
			}
		}

		if (result == null) {
			result = found3;
		}

		if (result == null) {
			for (Map.Entry<Integer, Vomit> entry : GameWorld.get().getCurrentMap().getVomit().entrySet()) {
				Vomit v = entry.getValue();
				int distance = Translate.distance(b.getX(), b.getY(), v.getX(), v.getY());
				if (minDistance > distance) {
					if (Barrier.acrossBarrier(b.getX(), b.getY(), v.getX(), v.getY(),
							Barrier.MAP_BODY[wallMode] + Barrier.BARRIER_KEKKAI)) {
						continue;
					}
					result = v;
					minDistance = distance;
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
				if (minDistance > distance) {
					if (Barrier.acrossBarrier(b.getX(), b.getY(), s.getX(), s.getY(),
							Barrier.MAP_BODY[wallMode] + Barrier.BARRIER_KEKKAI)) {
						continue;
					}
					result = s;
					minDistance = distance;
				}
			}
		}

		return result;
	}
}
