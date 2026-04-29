package src.logic;

import java.util.List;
import java.util.Map;

import src.base.Body;
import src.base.Obj;
import src.draw.Translate;
import src.enums.AgeState;
import src.enums.BaryInUGState;
import src.enums.BodyRank;
import src.enums.FavItemType;
import src.enums.FootBake;
import src.enums.Happiness;
import src.enums.PublicRank;
import src.enums.TakeoutItemType;
import src.enums.TangType;
import src.game.Shit;
import src.game.Stalk;
import src.game.Vomit;
import src.item.Barrier;
import src.item.Food;
import src.item.Food.FoodType;
import src.util.GameEnvironment;
import src.util.GameMessages;
import src.util.GameRandom;
import src.util.GameWorld;
import src.util.YukkuriUtil;

/**
 * 食料探索のルールをまとめたヘルパー。
 */
public final class FoodSearchPolicy {
	private FoodSearchPolicy() {
	}

	public static Obj searchFoodStandard(Body b, boolean[] forceEat) {
		Obj found = null;
		Obj foundTakeout = null;
		int minDistance = b.getEYESIGHTorg();
		int looks = -1000;
		int wallMode = b.getBodyAgeState().ordinal();
		forceEat[0] = false;
		if (b.canflyCheck()) {
			wallMode = AgeState.ADULT.ordinal();
		}

		if (b.isSoHungry()) {
			if (b.getTakeoutItem(TakeoutItemType.FOOD) != null) {
				found = b.dropTakeoutItem(TakeoutItemType.FOOD);
			}
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

				boolean flag = false;
				boolean flagtakeout = false;
				switch (f.getFoodType()) {
				case STALK:
					if (b.isBaby()) {
						if (!b.isbFirstEatStalk()) {
							flag = true;
							forceEat[0] = true;
						} else if (b.isHungry()) {
							flag = true;
						}
					} else if (b.isRude() && b.isSoHungry()) {
						flag = true;
					} else if (!b.isRude() && b.isVeryHungry()) {
						flag = true;
					} else if (b.isRaper() && b.isExciting()) {
						flag = true;
					}
					break;
				case SWEETS1:
				case SWEETS2:
				case SWEETS_NORA1:
				case SWEETS_NORA2:
				case SWEETS_YASEI1:
				case SWEETS_YASEI2:
					if (!b.isTooFull()) {
						flag = true;
					} else if (!b.isOverEating() && (b.isRude() || b.isNormal())) {
						flag = true;
						forceEat[0] = true;
					} else {
						flagtakeout = true;
					}
					break;
				case WASTE:
				case WASTE_NORA:
				case WASTE_YASEI:
					if (b.getTangType() == TangType.GOURMET && b.isStarving()) {
						flag = true;
					} else if (b.getTangType() == TangType.NORMAL && b.isTooHungry()) {
						flag = true;
					} else if (b.getTangType() == TangType.POOR) {
						if (b.isHungry()) {
							flag = true;
						} else {
							flagtakeout = true;
						}
					}
					break;
				default:
					if (b.isHungry()) {
						flag = true;
					} else {
						flagtakeout = true;
					}
					break;
				}

				if (flag) {
					if (looks <= f.getLooks()) {
						found = f;
						minDistance = distance;
						looks = f.getLooks();
					}
				}
				if (flagtakeout) {
					if (looks <= f.getLooks()) {
						foundTakeout = f;
						minDistance = distance;
						looks = f.getLooks();
					}
				}
			}
		}

		if (foundTakeout != null) {
			if (b.getTakeoutItem(TakeoutItemType.FOOD) == null) {
				if (FoodLogic.checkTakeout(b, foundTakeout)) {
					b.setToTakeout(true);
					return foundTakeout;
				}
			}
		}
		if (found == null && b.isFull()) {
			return null;
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
				List<Integer> babyList = ((Stalk) s).getBindBabies();
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
				if (d == null || d.isRemoved()) {
					continue;
				}
				if (b == d) {
					continue;
				}
				if (b.isRaper()) {
					if (!d.isDead() && !d.isUnBirth()) {
						continue;
					}
				} else {
					if (!FoodLogic.checkCanEatBody(b, d)) {
						continue;
					}
				}
				if (d.isbindStalk()) {
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
		}
		if (found == null) {
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
					found = s;
					minDistance = distance;
				}
			}
		}
		return found;
	}
}
