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
import src.item.Barrier;
import src.item.Food;
import src.util.GameWorld;

/**
 * うんうん奴隷用の食料探索ルール。
 */
public final class FoodUnunSlaveSearchPolicy {
	private FoodUnunSlaveSearchPolicy() {
	}

	public static Obj searchFoodForUnunSlave(Body b, boolean[] forceEat) {
		Obj found = null;
		int minDistance = b.getEYESIGHTorg();
		int wallMode = b.getBodyAgeState().ordinal();

		forceEat[0] = false;

		if (b.canflyCheck()) {
			wallMode = AgeState.ADULT.ordinal();
		}

		if (b.isVeryHungry()) {
			if (b.getTakeoutItem(TakeoutItemType.SHIT) != null) {
				b.dropTakeoutItem(TakeoutItemType.SHIT);
			}
		}

		if (found == null) {
			for (Map.Entry<Integer, Shit> entry : GameWorld.get().getCurrentMap().getShit().entrySet()) {
				Shit s = entry.getValue();
				if (minDistance < 1) {
					break;
				}
				int distance = Translate.distance(b.getX(), b.getY(), s.getX(), s.getY());
				if (minDistance > distance) {
					if (Barrier.acrossBarrier(b.getX(), b.getY(), s.getX(), s.getY(),
							Barrier.MAP_BODY[wallMode] + Barrier.BARRIER_KEKKAI)) {
						continue;
					}
					if (FoodLogic.checkTakeout(b, s)) {
						boolean bOtherTarget = false;
						for (Map.Entry<Integer, Body> entry2 : GameWorld.get().getCurrentMap().getBody().entrySet()) {
							Body bodyOther = entry2.getValue();
							if (b == bodyOther || bodyOther == null || bodyOther.isDead() || bodyOther.isRemoved()) {
								continue;
							}
							Obj objTarget = bodyOther.takeMoveTarget();
							if (s == objTarget) {
								bOtherTarget = true;
								break;
							}
						}
						if (bOtherTarget) {
							continue;
						}
						b.setToTakeout(true);
						found = s;
					}
					if (!b.isToTakeout()) {
						found = s;
					}
					minDistance = distance;
				}
			}
		}

		if (found == null) {
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
		}

		if (found == null) {
			for (Map.Entry<Integer, Body> entry : GameWorld.get().getCurrentMap().getBody().entrySet()) {
				Body d = entry.getValue();
				if (b == d) {
					continue;
				}
				if (!FoodLogic.checkCanEatBody(b, d)) {
					continue;
				}
				if (!b.isSoHungry() || !b.isTooHungry()) {
					break;
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
		}

		if (found == null) {
			for (Map.Entry<Integer, Food> entry : GameWorld.get().getCurrentMap().getFood().entrySet()) {
				Food f = entry.getValue();
				if (f.isEmpty()) {
					continue;
				}
				int distance = Translate.distance(b.getX(), b.getY(), f.getX(), f.getY());
				if (minDistance > distance) {
					if (Barrier.acrossBarrier(b.getX(), b.getY(), f.getX(), f.getY(),
							Barrier.MAP_BODY[wallMode] + Barrier.BARRIER_KEKKAI)) {
						continue;
					}
					if (f.getFoodType() == Food.FoodType.WASTE ||
							f.getFoodType() == Food.FoodType.WASTE_NORA ||
							f.getFoodType() == Food.FoodType.WASTE_YASEI) {
						if (b.isTooHungry()) {
							found = f;
							break;
						}
					}
				}
			}
		}
		return found;
	}
}
