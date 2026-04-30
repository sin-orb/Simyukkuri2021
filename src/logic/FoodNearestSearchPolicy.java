package src.logic;

import java.util.List;
import java.util.Map;

import src.base.Body;
import src.base.Obj;
import src.draw.Translate;
import src.enums.AgeState;
import src.enums.BaryInUGState;
import src.enums.Intelligence;
import src.game.Shit;
import src.game.Stalk;
import src.game.Vomit;
import src.item.Barrier;
import src.item.Food;
import src.item.Food.FoodType;
import src.util.GameWorld;
import src.util.YukkuriUtil;

/**
 * 近傍の餌を選ぶ最短候補探索.
 */
public final class FoodNearestSearchPolicy {

	private FoodNearestSearchPolicy() {
	}

	/**
	 * 足りないゆ、足焼き用 最も近いものを適当に食べる.
	 */
	public static Obj searchFoodNearest(Body b, boolean[] forceEat) {
		Obj found = null;
		int minDistance = b.getEYESIGHTorg();
		int wallMode = b.getBodyAgeState().ordinal();
		forceEat[0] = false;
		if (b.isFull()) {
			return null;
		}

		if (b.canflyCheck()) {
			wallMode = AgeState.ADULT.ordinal();
		}

		for (Map.Entry<Integer, Food> entry : GameWorld.get().getCurrentMap().getFood().entrySet()) {
			Food f = entry.getValue();
			if (f.isEmpty()) {
				continue;
			}
			if (minDistance < 1) {
				break;
			}
			int distance = Translate.distance(b.getX(), b.getY(), f.getX(), f.getY());
			if (minDistance > distance) {
				if (Barrier.acrossBarrier(b.getX(), b.getY(), f.getX(), f.getY(),
						Barrier.MAP_BODY[wallMode] + Barrier.BARRIER_KEKKAI)) {
					continue;
				}
				found = f;
				minDistance = distance;
			}
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
			if (minDistance > distance) {
				if (Barrier.acrossBarrier(b.getX(), b.getY(), s.getX(), s.getY(),
						Barrier.MAP_BODY[wallMode] + Barrier.BARRIER_KEKKAI)) {
					continue;
				}
				found = s;
				minDistance = distance;
			}
		}
		for (Map.Entry<Integer, Vomit> entry : GameWorld.get().getCurrentMap().getVomit().entrySet()) {
			Vomit v = entry.getValue();
			int distance = Translate.distance(b.getX(), b.getY(), v.getX(), v.getY());
			if (minDistance > distance) {
				if (Barrier.acrossBarrier(b.getX(), b.getY(), v.getX(), v.getY(),
						Barrier.MAP_BODY[wallMode] + Barrier.BARRIER_KEKKAI)) {
					continue;
				}
				found = v;
				minDistance = distance;
			}
		}
		for (Map.Entry<Integer, Body> entry : GameWorld.get().getCurrentMap().getBody().entrySet()) {
			Body d = entry.getValue();
			if (b == d) {
				continue;
			}
			if (!FoodLogic.checkCanEatBody(b, d)) {
				continue;
			}
			int distance = Translate.distance(b.getX(), b.getY(), d.getX(), d.getY());
			if (minDistance > distance) {
				if (Barrier.acrossBarrier(b.getX(), b.getY(), d.getX(), d.getY(),
						Barrier.MAP_BODY[wallMode] + Barrier.BARRIER_KEKKAI)) {
					continue;
				}
				found = d;
				minDistance = distance;
			}
		}
		for (Map.Entry<Integer, Shit> entry : GameWorld.get().getCurrentMap().getShit().entrySet()) {
			Shit s = entry.getValue();
			int distance = Translate.distance(b.getX(), b.getY(), s.getX(), s.getY());
			if (minDistance > distance) {
				if (Barrier.acrossBarrier(b.getX(), b.getY(), s.getX(), s.getY(),
						Barrier.MAP_BODY[wallMode] + Barrier.BARRIER_KEKKAI)) {
					continue;
				}
				found = s;
				minDistance = distance;
			}
		}
		return found;
	}
}
