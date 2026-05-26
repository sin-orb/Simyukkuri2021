package org.simyukkuri.logic;

import java.util.Map;
import org.simyukkuri.entity.core.Entity;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.world.bodylinked.Stalk;
import org.simyukkuri.entity.core.world.item.Food;
import org.simyukkuri.entity.core.world.item.Food.FoodType;
import org.simyukkuri.entity.core.world.mobile.Shit;
import org.simyukkuri.entity.core.world.mobile.Vomit;
import org.simyukkuri.enums.BurialState;
import org.simyukkuri.enums.Happiness;
import org.simyukkuri.enums.PublicRank;
import org.simyukkuri.enums.PurposeOfMoving;
import org.simyukkuri.enums.TangType;
import org.simyukkuri.event.impl.EatBodyEvent;
import org.simyukkuri.event.impl.FlyingEatEvent;
import org.simyukkuri.event.impl.KillPredeatorEvent;
import org.simyukkuri.system.MessagePool;
import org.simyukkuri.system.WorldState;
import org.simyukkuri.util.GameEnvironment;
import org.simyukkuri.util.GameMessages;
import org.simyukkuri.util.GameRandom;
import org.simyukkuri.util.GameWorld;

/**
 * 到着済みの餌に対する行動.
 */
public final class FoodArrivalActionPolicy {

	private FoodArrivalActionPolicy() {
	}

	/**
	 * 到着済みの target を処理する.
	 */
	public static boolean handleArrivedFood(Yukkuri body, Entity targetObject, boolean[] forceEat) {
		return handleArrivedFood(body, targetObject, forceEat, GameWorld.get().getCurrentWorldState());
	}

	/**
	 * 到達した食べ物への摂食処理を行い、行動継続可否を返す。
	 *
	 * @param body ゆっくり
	 * @param targetObject 対象エンティティ
	 * @param forceEat 強制給餌フラグ
	 * @param ws ワールド状態
	 *
	 * @return 摂食処理が完了した場合は true、キャンセルされた場合は false
	 */
	public static boolean handleArrivedFood(Yukkuri body, Entity targetObject, boolean[] forceEat, WorldState ws) {
		boolean sweets = false;
		boolean goodsweets = false;
		boolean fullmessage = false;

		if (targetObject instanceof Food) {
			Food food = (Food) body.takeMoveTarget();
			if (food.isEmpty()) {
				body.clearActions();
				return false;
			}
			if (!body.isToTakeout() || body.isVeryHungry()) {
				eatFood(body, food.getFoodType(), Math.min(body.getEatAmount(), food.getAmount()));
				food.eatFood(Math.min(body.getEatAmount(), food.getAmount()));
				if (food.getFoodType() == FoodType.STALK && food.isEmpty()) {
					food.remove();
				}
				if (food.getFoodType() == FoodType.SWEETS1 || food.getFoodType() == FoodType.SWEETS2
						|| food.getFoodType() == FoodType.SWEETS_NORA1 || food.getFoodType() == FoodType.SWEETS_NORA2
						|| food.getFoodType() == FoodType.SWEETS_YASEI1
						|| food.getFoodType() == FoodType.SWEETS_YASEI2) {
					sweets = true;
				}
				if (food.getFoodType() == FoodType.SWEETS2 || food.getFoodType() == FoodType.SWEETS_NORA2
						|| food.getFoodType() == FoodType.SWEETS_YASEI2) {
					goodsweets = true;
				}
				if (food.getFoodType() != FoodType.STALK && food.getFoodType() != FoodType.BITTER
						&& food.getFoodType() != FoodType.HOT && food.getFoodType() != FoodType.WASTE
						&& food.getFoodType() != FoodType.BITTER_NORA && food.getFoodType() != FoodType.HOT_NORA
						&& food.getFoodType() != FoodType.WASTE_NORA && food.getFoodType() != FoodType.BITTER_YASEI
						&& food.getFoodType() != FoodType.HOT_YASEI && food.getFoodType() != FoodType.WASTE_YASEI
						|| (food.getFoodType() == FoodType.WASTE && body.getTangType() == TangType.POOR)
						|| (food.getFoodType() == FoodType.WASTE_NORA && body.getTangType() == TangType.POOR)
						|| (food.getFoodType() == FoodType.WASTE_YASEI && body.getTangType() == TangType.POOR)) {
					fullmessage = true;
				}
			} else {
				boolean alreadyTakenOut = false;
				for (Map.Entry<org.simyukkuri.enums.TakeoutItemType, Integer> entry : body.getCarryItems().entrySet()) {
					org.simyukkuri.enums.TakeoutItemType t = entry.getKey();
					if (t == org.simyukkuri.enums.TakeoutItemType.FOOD) {
						alreadyTakenOut = true;
						break;
					}
				}
				if (!alreadyTakenOut) {
					body.clearActions();
					body.setCarryItem(org.simyukkuri.enums.TakeoutItemType.FOOD, food);
					body.setToTakeout(true);
					body.setMessage(GameMessages.getMessage(body, MessagePool.Action.TransportFood));
					body.addStress(10);
					body.stay();
				} else {
					body.setToTakeout(false);
					body.setPurposeOfMoving(PurposeOfMoving.NONE);
				}
			}
		} else if (targetObject instanceof Shit) {
			Shit shit = (Shit) targetObject;
			if (!body.isToTakeout()) {
				eatFood(body, FoodType.SHIT, body.getEatAmount());
				shit.eatShit(body.getEatAmount());
			} else {
				body.setCarryItem(org.simyukkuri.enums.TakeoutItemType.SHIT, shit);
				body.clearActions();
				body.setToTakeout(true);
				if (body.getPublicRank() == PublicRank.UNUN_SLAVE) {
					body.setMessage(GameMessages.getMessage(body, MessagePool.Action.HateShit));
					body.addStress(20);
					body.stay();
				}
			}
		} else if (targetObject instanceof Yukkuri) {
			Yukkuri candidateBody = (Yukkuri) targetObject;
			if (!candidateBody.isDead()) {
				if (body.isPredatorType() && !GameEnvironment.isPredatorSteam()) {
					candidateBody.bodyInjure();
					if (body.canflyCheck()) {
						body.clearActions();
						EventLogic.addYukkuriEvent(body, new FlyingEatEvent(body, candidateBody, null, 1), null, null);
					} else {
						eatFood(body, FoodType.BODY, Math.min(body.getEatAmount(), candidateBody.getAnkoAmount()));
						candidateBody.eatYukkuri(Math.min(body.getEatAmount(), candidateBody.getAnkoAmount()), body);
						if (candidateBody.isSick()) {
							body.addSickPeriod(100);
						}
					}
					Yukkuri motherBody = org.simyukkuri.util.YukkuriLookup.getYukkuriById(candidateBody.getMother());
					if (GameRandom.nextInt(3) == 0 && motherBody != null && !motherBody.isDead()
							&& !motherBody.isRemoved()) {
						motherBody.clearEvent();
						motherBody.setPanic(false, null);
						motherBody.setPeropero(false);
						motherBody.setAngry();
						EventLogic.addYukkuriEvent(motherBody, new KillPredeatorEvent(motherBody, body, null, 10), null,
								null);
					}
				} else {
					if (body.isRaper() && candidateBody.isUnBirth()) {
						eatFood(body, FoodType.BODY, Math.min(body.getEatAmount(), candidateBody.getAnkoAmount()));
						candidateBody.eatYukkuri(Math.min(body.getEatAmount(), candidateBody.getAnkoAmount()), body);
						if (candidateBody.isSick()) {
							body.addSickPeriod(100);
						}
					} else {
						body.setPurposeOfMoving(PurposeOfMoving.NONE);
						body.clearActions();
						return false;
					}
				}
			} else {
				eatFood(body, FoodType.BODY, Math.min(body.getEatAmount(), candidateBody.getAnkoAmount()));
				candidateBody.eatYukkuri(Math.min(body.getEatAmount(), candidateBody.getAnkoAmount()));
				if (!FoodLogic.checkCanEatYukkuri(body, candidateBody)) {
					body.clearActions();
					EventLogic.addYukkuriEvent(body, new EatBodyEvent(body, candidateBody, null, 30), null, null);
				}
				if (candidateBody.isSick() && GameRandom.nextBoolean()) {
					body.forceSetSick();
				}
			}
		} else if (targetObject instanceof Stalk) {
			Stalk stalk = (Stalk) targetObject;
			Yukkuri plantBody = ws.getYukkuriRegistry().get(stalk.getPlantYukkuri());
			if (stalk.getZ() == 0 && plantBody == null) {
				eatFood(body, FoodType.STALK, Math.min(body.getEatAmount(), stalk.getAmount()));
				stalk.eatStalk(Math.min(body.getEatAmount(), stalk.getAmount()));
			} else {
				if (plantBody != null) {
					plantBody.removeStalk(stalk);
					stalk.setPlantYukkuri(null);
					if (plantBody.getBurialState() == BurialState.ALL
							|| (plantBody.getBurialState() == BurialState.NEARLY_ALL && !plantBody.hasOkazari())) {
						body.setMessage(GameMessages.getMessage(body, MessagePool.Action.FindVegetable), fullmessage);
						body.setHappiness(Happiness.VERY_HAPPY);
					}
					body.stay();
				}
			}
		} else if (targetObject instanceof Vomit) {
			Vomit vomit = (Vomit) targetObject;
			eatFood(body, FoodType.VOMIT, body.getEatAmount());
			vomit.eatVomit(body.getEatAmount());
		}

		if (goodsweets) {
			body.addAmaamaDiscipline(5);
		} else if (sweets) {
			body.addAmaamaDiscipline(3);
		} else if (targetObject instanceof Yukkuri) {
			body.addAmaamaDiscipline(1);
		} else {
			body.addAmaamaDiscipline(-1);
		}

		if (body.isFull()) {
			if (body.isNotNyd()) {
				if (sweets) {
					body.setMessage(GameMessages.getMessage(body, MessagePool.Action.EatingAmaama), false);
					body.setEating(true);
					body.stay();
				} else {
					body.setMessage(GameMessages.getMessage(body, MessagePool.Action.Full), fullmessage);
					body.stay();
					body.clearActions();
				}
			}
		}
		if (!body.isFirstEatStalk()) {
			body.setFirstEatStalk(true);
		}
		return true;
	}

	private static void eatFood(Yukkuri body, FoodType foodType, int amount) {
		FoodLogic.eatFood(body, foodType, amount);
	}
}
