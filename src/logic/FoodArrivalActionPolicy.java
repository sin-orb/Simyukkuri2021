package src.logic;

import java.util.Map;

import src.base.Body;
import src.event.EventPacket;
import src.base.Obj;
import src.draw.Translate;
import src.enums.BurialState;
import src.enums.Happiness;
import src.enums.PurposeOfMoving;
import src.enums.PublicRank;
import src.enums.TangType;
import src.event.EatBodyEvent;
import src.event.FlyingEatEvent;
import src.event.KillPredeatorEvent;
import src.game.Shit;
import src.game.Stalk;
import src.game.Vomit;
import src.item.Food;
import src.item.Food.FoodType;
import src.system.MessagePool;
import src.util.GameEnvironment;
import src.util.GameMessages;
import src.util.GameRandom;
import src.util.GameWorld;

/**
 * 到着済みの餌に対する行動.
 */
public final class FoodArrivalActionPolicy {

	private FoodArrivalActionPolicy() {
	}

	/**
	 * 到着済みの target を処理する.
	 */
	public static boolean handleArrivedFood(Body body, Obj targetObject, boolean[] forceEat) {
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
						|| food.getFoodType() == FoodType.SWEETS_YASEI1 || food.getFoodType() == FoodType.SWEETS_YASEI2) {
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
				for (Map.Entry<src.enums.TakeoutItemType, Integer> entry : body.getCarryItems().entrySet()) {
					src.enums.TakeoutItemType t = entry.getKey();
					if (t == src.enums.TakeoutItemType.FOOD) {
						alreadyTakenOut = true;
						break;
					}
				}
				if (!alreadyTakenOut) {
					body.clearActions();
					body.setCarryItem(src.enums.TakeoutItemType.FOOD, food);
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
				body.setCarryItem(src.enums.TakeoutItemType.SHIT, shit);
				body.clearActions();
				body.setToTakeout(true);
				if (body.getPublicRank() == PublicRank.UnunSlave) {
					body.setMessage(GameMessages.getMessage(body, MessagePool.Action.HateShit));
					body.addStress(20);
					body.stay();
				}
			}
		} else if (targetObject instanceof Body) {
			Body candidateBody = (Body) targetObject;
			if (!candidateBody.isDead()) {
				if (body.isPredatorType() && !GameEnvironment.isPredatorSteam()) {
					candidateBody.bodyInjure();
					if (body.canflyCheck()) {
						body.clearActions();
						EventLogic.addBodyEvent(body, new FlyingEatEvent(body, candidateBody, null, 1), null, null);
					} else {
						eatFood(body, FoodType.BODY, Math.min(body.getEatAmount(), candidateBody.getAnkoAmount()));
						candidateBody.eatBody(Math.min(body.getEatAmount(), candidateBody.getAnkoAmount()), body);
						if (candidateBody.isSick()) {
							body.addSickPeriod(100);
						}
					}
					Body motherBody = src.util.BodyRegistry.getBodyInstance(candidateBody.getMother());
					if (GameRandom.nextInt(3) == 0 && motherBody != null && !motherBody.isDead() && !motherBody.isRemoved()) {
						motherBody.clearEvent();
						motherBody.setPanic(false, null);
						motherBody.setPeropero(false);
						motherBody.setAngry();
						EventLogic.addBodyEvent(motherBody, new KillPredeatorEvent(motherBody, body, null, 10), null, null);
					}
				} else {
					if (body.isRaper() && candidateBody.isUnBirth()) {
						eatFood(body, FoodType.BODY, Math.min(body.getEatAmount(), candidateBody.getAnkoAmount()));
						candidateBody.eatBody(Math.min(body.getEatAmount(), candidateBody.getAnkoAmount()), body);
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
				candidateBody.eatBody(Math.min(body.getEatAmount(), candidateBody.getAnkoAmount()));
				if (!FoodLogic.checkCanEatBody(body, candidateBody)) {
					body.clearActions();
					EventLogic.addBodyEvent(body, new EatBodyEvent(body, candidateBody, null, 30), null, null);
				}
				if (candidateBody.isSick() && GameRandom.nextBoolean()) {
					body.forceSetSick();
				}
			}
		} else if (targetObject instanceof Stalk) {
			Stalk stalk = (Stalk) targetObject;
			Body plantBody = GameWorld.get().getCurrentMap().getBody().get(stalk.getPlantYukkuri());
			if (stalk.getZ() == 0 && plantBody == null) {
				eatFood(body, FoodType.STALK, Math.min(body.getEatAmount(), stalk.getAmount()));
				stalk.eatStalk(Math.min(body.getEatAmount(), stalk.getAmount()));
			} else {
				if (plantBody != null) {
					plantBody.removeStalk(stalk);
					stalk.setPlantYukkuri(null);
					if (plantBody.getBurialState() == BurialState.ALL ||
							(plantBody.getBurialState() == BurialState.NEARLY_ALL && !plantBody.hasOkazari())) {
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
		} else if (targetObject instanceof Body) {
			body.addAmaamaDiscipline(1);
		} else {
			body.addAmaamaDiscipline(-1);
		}

		if (body.isFull()) {
			if (body.isNotNYD()) {
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

	private static void eatFood(Body body, FoodType foodType, int amount) {
		FoodLogic.eatFood(body, foodType, amount);
	}
}
