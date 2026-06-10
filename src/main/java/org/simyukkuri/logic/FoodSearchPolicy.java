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
import org.simyukkuri.enums.CoreAnkoState;
import org.simyukkuri.enums.TakeoutItemType;
import org.simyukkuri.enums.TangType;
import org.simyukkuri.field.impl.Barrier;
import org.simyukkuri.system.WorldState;
import org.simyukkuri.util.GameWorld;

/**
 * 食料探索のルールをまとめたヘルパー。
 */
public final class FoodSearchPolicy {
	private FoodSearchPolicy() {
	}

	/**
	 * 一般種用の食べ物を探索して返す。
	 *
	 * @param body ゆっくり
	 * @param forceEat 強制給餌フラグ
	 *
	 * @return 対象を発見した場合はそのオブジェクト、見つからない場合は null
	 */
	public static Entity searchFoodStandard(Yukkuri body, boolean[] forceEat) {
		return searchFoodStandard(body, forceEat, GameWorld.get().getCurrentWorldState());
	}

	/**
	 * 一般種用の食べ物を探索して返す。
	 *
	 * @param body ゆっくり
	 * @param forceEat 強制給餌フラグ
	 * @param ws ワールド状態
	 *
	 * @return 対象を発見した場合はそのオブジェクト、見つからない場合は null
	 */
	public static Entity searchFoodStandard(Yukkuri body, boolean[] forceEat, WorldState ws) {
		int wallMode = body.getAgeState().ordinal();
		forceEat[0] = false;
		if (body.canflyCheck()) {
			wallMode = AgeState.ADULT.ordinal();
		}

		Entity targetObject = null;
		if (body.isSoHungry()) {
			if (body.getCarryItem(TakeoutItemType.FOOD) != null) {
				targetObject = body.dropTakeoutItem(TakeoutItemType.FOOD);
			}
		}

		Entity takeoutTargetObject = null;
		int minDistance = body.getEyesightBase();
		int looks = -1000;
		for (Map.Entry<Integer, Food> entry : ws.getFoods().entrySet()) {
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
						Barrier.BODY_BLOCK_FLAGS[wallMode] + Barrier.BARRIER_KEKKAI)) {
					continue;
				}

				// NON_YUKKURI_DISEASE 状態ではあまあまのみを候補とする
				if (body.getCoreAnkoState() == CoreAnkoState.NON_YUKKURI_DISEASE) {
					switch (f.getFoodType()) {
						case SWEETS1: case SWEETS2:
						case SWEETS_NORA1: case SWEETS_NORA2:
						case SWEETS_YASEI1: case SWEETS_YASEI2:
							break;
						default:
							continue;
					}
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
				if (FoodLogic.checkTakeout(body, takeoutTargetObject, ws)) {
					body.setToTakeout(true);
					return takeoutTargetObject;
				}
			}
		}
		if (targetObject == null && body.isFull()) {
			return null;
		}

		for (Map.Entry<Integer, Stalk> entry : ws.getStalks().entrySet()) {
			Stalk s = entry.getValue();
			Yukkuri plantBody = ws.getYukkuriRegistry().get(s.getPlantYukkuri());
			if (plantBody != null) {
				if (plantBody == body) {
					continue;
				}
				if (plantBody.getBurialState() != BurialState.ALL
						&& !(plantBody.getBurialState() == BurialState.NEARLY_ALL && !plantBody.hasOkazari())) {
					continue;
				}
				List<Integer> babyList = s.getAttachedBabyIds();
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
			int distance = Translate.distance(body.getX(), body.getY(), s.getX(), s.getY());
			if (minDistance > distance) {
				if (Barrier.acrossBarrier(body.getX(), body.getY(), s.getX(), s.getY(),
						Barrier.BODY_BLOCK_FLAGS[wallMode] + Barrier.BARRIER_KEKKAI)) {
					continue;
				}
				targetObject = s;
				minDistance = distance;
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
				Yukkuri deadCandidate = entry.getValue();
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
					if (!FoodLogic.checkCanEatYukkuri(body, deadCandidate)) {
						continue;
					}
				}
				if (deadCandidate.hasBindStalk()) {
					continue;
				}
				int distance = Translate.distance(body.getX(), body.getY(), deadCandidate.getX(), deadCandidate.getY());
				if (minDistance > distance) {
					if (Barrier.acrossBarrier(body.getX(), body.getY(), deadCandidate.getX(), deadCandidate.getY(),
							Barrier.BODY_BLOCK_FLAGS[wallMode] + Barrier.BARRIER_KEKKAI)) {
						continue;
					}
					targetObject = deadCandidate;
					minDistance = distance;
				}
			}
		}
		if (targetObject == null) {
			for (Map.Entry<Integer, Shit> entry : ws.getShit().entrySet()) {
				Shit shit = entry.getValue();
				if (!body.isTooHungry()) {
					break;
				}
				int distance = Translate.distance(body.getX(), body.getY(), shit.getX(), shit.getY());
				if (minDistance > distance) {
					if (Barrier.acrossBarrier(body.getX(), body.getY(), shit.getX(), shit.getY(),
							Barrier.BODY_BLOCK_FLAGS[wallMode] + Barrier.BARRIER_KEKKAI)) {
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
