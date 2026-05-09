package src.logic;

import java.util.List;
import java.util.Map;

import src.base.Body;
import src.base.Obj;
import src.draw.Translate;
import src.enums.AgeState;
import src.enums.BurialState;
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
import src.field.impl.Barrier;
import src.item.Food;
import src.item.Food.FoodType;
import src.util.GameEnvironment;
import src.util.GameMessages;
import src.util.GameRandom;
import src.util.GameWorld;

/**
 * 食料探索のルールをまとめたヘルパー。
 */
public final class FoodSearchPolicy {
	private FoodSearchPolicy() {
	}

	public static Obj searchFoodStandard(Body body, boolean[] forceEat) {
		Obj targetObject = null;
		Obj takeoutTargetObject = null;
		int minDistance = body.getEyesightBase();
		int looks = -1000;
		int wallMode = body.getBodyAgeState().ordinal();
		forceEat[0] = false;
		if (body.canflyCheck()) {
			wallMode = AgeState.ADULT.ordinal();
		}

		if (body.isSoHungry()) {
			if (body.getCarryItem(TakeoutItemType.FOOD) != null) {
				targetObject = body.dropTakeoutItem(TakeoutItemType.FOOD);
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
			int distance = Translate.distance(body.getX(), body.getY(), f.getX(), f.getY());
			if (minDistance > distance) {
				if (Barrier.acrossBarrier(body.getX(), body.getY(), f.getX(), f.getY(),
						Barrier.MAP_BODY[wallMode] + Barrier.BARRIER_KEKKAI)) {
					continue;
				}

				boolean acceptable = false;
				boolean takeoutCandidate = false;
				switch (f.getFoodType()) {
				case STALK:
					if (body.isBaby()) {
						if (!body.isFirstEatStalk()) {
							acceptable = true;
							forceEat[0] = true;
						} else if (body.isHungry()) {
							acceptable = true;
						}
					} else if (body.isRude() && body.isSoHungry()) {
						acceptable = true;
					} else if (!body.isRude() && body.isVeryHungry()) {
						acceptable = true;
					} else if (body.isRaper() && body.isExciting()) {
						acceptable = true;
					}
					break;
				case SWEETS1:
				case SWEETS2:
				case SWEETS_NORA1:
				case SWEETS_NORA2:
				case SWEETS_YASEI1:
				case SWEETS_YASEI2:
					if (!body.isTooFull()) {
						acceptable = true;
					} else if (!body.isOverEating() && (body.isRude() || body.isNormal())) {
						acceptable = true;
						forceEat[0] = true;
					} else {
						takeoutCandidate = true;
					}
					break;
				case WASTE:
				case WASTE_NORA:
				case WASTE_YASEI:
					if (body.getTangType() == TangType.GOURMET && body.isStarving()) {
						acceptable = true;
					} else if (body.getTangType() == TangType.NORMAL && body.isTooHungry()) {
						acceptable = true;
					} else if (body.getTangType() == TangType.POOR) {
						if (body.isHungry()) {
							acceptable = true;
						} else {
							takeoutCandidate = true;
						}
					}
					break;
				default:
					if (body.isHungry()) {
						acceptable = true;
					} else {
						takeoutCandidate = true;
					}
					break;
				}

				if (acceptable) {
					if (looks <= f.getLooks()) {
						targetObject = f;
						minDistance = distance;
						looks = f.getLooks();
					}
				}
				if (takeoutCandidate) {
					if (looks <= f.getLooks()) {
						takeoutTargetObject = f;
						minDistance = distance;
						looks = f.getLooks();
					}
				}
			}
		}

		if (takeoutTargetObject != null) {
			if (body.getCarryItem(TakeoutItemType.FOOD) == null) {
				if (FoodLogic.checkTakeout(body, takeoutTargetObject)) {
					body.setToTakeout(true);
					return takeoutTargetObject;
				}
			}
		}
		if (targetObject == null && body.isFull()) {
			return null;
		}

		for (Map.Entry<Integer, Stalk> entry : GameWorld.get().getCurrentMap().getStalk().entrySet()) {
			Stalk s = entry.getValue();
			Body plantBody = GameWorld.get().getCurrentMap().getBody().get(s.getPlantYukkuri());
			if (plantBody != null) {
				if (plantBody == body) {
					continue;
				}
				if (plantBody.getBurialState() != BurialState.ALL &&
						!(plantBody.getBurialState() == BurialState.NEARLY_ALL && !plantBody.hasOkazari())) {
					continue;
				}
				List<Integer> babyList = ((Stalk) s).getBindBabies();
				if (babyList != null && babyList.size() != 0) {
					boolean hasBaby = false;
					for (int babyId : babyList) {
						Body baby = src.util.BodyRegistry.getBodyInstance(babyId);
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
			int distance = Translate.distance(body.getX(), body.getY(), s.getX(), s.getY());
			if (minDistance > distance) {
				if (Barrier.acrossBarrier(body.getX(), body.getY(), s.getX(), s.getY(),
						Barrier.MAP_BODY[wallMode] + Barrier.BARRIER_KEKKAI)) {
					continue;
				}
				targetObject = s;
				minDistance = distance;
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
				Body deadCandidate = entry.getValue();
				if (deadCandidate == null || deadCandidate.isRemoved()) {
					continue;
				}
				if (body == deadCandidate) {
					continue;
				}
				if (body.isRaper()) {
					if (!deadCandidate.isDead() && !deadCandidate.isUnBirth()) {
						continue;
					}
				} else {
					if (!FoodLogic.checkCanEatBody(body, deadCandidate)) {
						continue;
					}
				}
				if (deadCandidate.hasBindStalk()) {
					continue;
				}
				int distance = Translate.distance(body.getX(), body.getY(), deadCandidate.getX(), deadCandidate.getY());
				if (minDistance > distance) {
					if (Barrier.acrossBarrier(body.getX(), body.getY(), deadCandidate.getX(), deadCandidate.getY(),
							Barrier.MAP_BODY[wallMode] + Barrier.BARRIER_KEKKAI)) {
						continue;
					}
					targetObject = deadCandidate;
					minDistance = distance;
				}
			}
		}
		if (targetObject == null) {
			for (Map.Entry<Integer, Shit> entry : GameWorld.get().getCurrentMap().getShit().entrySet()) {
				Shit shit = entry.getValue();
				if (!body.isTooHungry()) {
					break;
				}
				int distance = Translate.distance(body.getX(), body.getY(), shit.getX(), shit.getY());
				if (minDistance > distance) {
					if (Barrier.acrossBarrier(body.getX(), body.getY(), shit.getX(), shit.getY(),
							Barrier.MAP_BODY[wallMode] + Barrier.BARRIER_KEKKAI)) {
						continue;
					}
					targetObject = shit;
					minDistance = distance;
				}
			}
		}
		return targetObject;
	}
}
