package src.logic;

import java.util.Map;

import src.base.Body;
import src.base.EventPacket;
import src.base.Obj;
import src.draw.Translate;
import src.enums.BaryInUGState;
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
import src.util.YukkuriUtil;

/**
 * 到着済みの餌に対する行動.
 */
public final class FoodArrivalActionPolicy {

	private FoodArrivalActionPolicy() {
	}

	/**
	 * 到着済みの target を処理する.
	 */
	public static boolean handleArrivedFood(Body b, Obj food, boolean[] forceEat) {
		boolean sweets = false;
		boolean goodsweets = false;
		boolean fullmessage = false;

		if (food instanceof Food) {
			Food f = (Food) b.takeMoveTarget();
			if (f.isEmpty()) {
				b.clearActions();
				return false;
			}
			if (!b.isToTakeout() || b.isVeryHungry()) {
				eatFood(b, f.getFoodType(), Math.min(b.getEatAmount(), f.getAmount()));
				f.eatFood(Math.min(b.getEatAmount(), f.getAmount()));
				if (f.getFoodType() == FoodType.STALK && f.isEmpty()) {
					f.remove();
				}
				if (f.getFoodType() == FoodType.SWEETS1 || f.getFoodType() == FoodType.SWEETS2
						|| f.getFoodType() == FoodType.SWEETS_NORA1 || f.getFoodType() == FoodType.SWEETS_NORA2
						|| f.getFoodType() == FoodType.SWEETS_YASEI1 || f.getFoodType() == FoodType.SWEETS_YASEI2) {
					sweets = true;
				}
				if (f.getFoodType() == FoodType.SWEETS2 || f.getFoodType() == FoodType.SWEETS_NORA2
						|| f.getFoodType() == FoodType.SWEETS_YASEI2) {
					goodsweets = true;
				}
				if (f.getFoodType() != FoodType.STALK && f.getFoodType() != FoodType.BITTER
						&& f.getFoodType() != FoodType.HOT && f.getFoodType() != FoodType.WASTE
						&& f.getFoodType() != FoodType.BITTER_NORA && f.getFoodType() != FoodType.HOT_NORA
						&& f.getFoodType() != FoodType.WASTE_NORA && f.getFoodType() != FoodType.BITTER_YASEI
						&& f.getFoodType() != FoodType.HOT_YASEI && f.getFoodType() != FoodType.WASTE_YASEI
						|| (f.getFoodType() == FoodType.WASTE && b.getTangType() == TangType.POOR)
						|| (f.getFoodType() == FoodType.WASTE_NORA && b.getTangType() == TangType.POOR)
						|| (f.getFoodType() == FoodType.WASTE_YASEI && b.getTangType() == TangType.POOR)) {
					fullmessage = true;
				}
			} else {
				boolean alreadyTakenOut = false;
				for (Map.Entry<src.enums.TakeoutItemType, Integer> entry : b.getTakeoutItem().entrySet()) {
					src.enums.TakeoutItemType t = entry.getKey();
					if (t == src.enums.TakeoutItemType.FOOD) {
						alreadyTakenOut = true;
						break;
					}
				}
				if (!alreadyTakenOut) {
					b.clearActions();
					b.setTakeoutItem(src.enums.TakeoutItemType.FOOD, f);
					b.setToTakeout(true);
					b.setMessage(GameMessages.getMessage(b, MessagePool.Action.TransportFood));
					b.addStress(10);
					b.stay();
				} else {
					b.setToTakeout(false);
					b.setPurposeOfMoving(PurposeOfMoving.NONE);
				}
			}
		} else if (food instanceof Shit) {
			Shit f = (Shit) food;
			if (!b.isToTakeout()) {
				eatFood(b, FoodType.SHIT, b.getEatAmount());
				f.eatShit(b.getEatAmount());
			} else {
				b.setTakeoutItem(src.enums.TakeoutItemType.SHIT, f);
				b.clearActions();
				b.setToTakeout(true);
				if (b.getPublicRank() == PublicRank.UnunSlave) {
					b.setMessage(GameMessages.getMessage(b, MessagePool.Action.HateShit));
					b.addStress(20);
					b.stay();
				}
			}
		} else if (food instanceof Body) {
			Body f = (Body) food;
			if (!f.isDead()) {
				if (b.isPredatorType() && !GameEnvironment.isPredatorSteam()) {
					f.bodyInjure();
					if (b.canflyCheck()) {
						b.clearActions();
						EventLogic.addBodyEvent(b, new FlyingEatEvent(b, f, null, 1), null, null);
					} else {
						eatFood(b, FoodType.BODY, Math.min(b.getEatAmount(), f.getBodyAmount()));
						f.eatBody(Math.min(b.getEatAmount(), f.getBodyAmount()), b);
						if (f.isSick()) {
							b.addSickPeriod(100);
						}
					}
					Body m = YukkuriUtil.getBodyInstance(f.getMother());
					if (GameRandom.nextInt(3) == 0 && m != null && !m.isDead() && !m.isRemoved()) {
						m.clearEvent();
						m.setPanic(false, null);
						m.setPeropero(false);
						m.setAngry();
						EventLogic.addBodyEvent(m, new KillPredeatorEvent(m, b, null, 10), null, null);
					}
				} else {
					if (b.isRaper() && f.isUnBirth()) {
						eatFood(b, FoodType.BODY, Math.min(b.getEatAmount(), f.getBodyAmount()));
						f.eatBody(Math.min(b.getEatAmount(), f.getBodyAmount()), b);
						if (f.isSick()) {
							b.addSickPeriod(100);
						}
					} else {
						b.setPurposeOfMoving(PurposeOfMoving.NONE);
						b.clearActions();
						return false;
					}
				}
			} else {
				eatFood(b, FoodType.BODY, Math.min(b.getEatAmount(), f.getBodyAmount()));
				f.eatBody(Math.min(b.getEatAmount(), f.getBodyAmount()));
				if (!FoodLogic.checkCanEatBody(b, f)) {
					b.clearActions();
					EventLogic.addBodyEvent(b, new EatBodyEvent(b, f, null, 30), null, null);
				}
				if (f.isSick() && GameRandom.nextBoolean()) {
					b.forceSetSick();
				}
			}
		} else if (food instanceof Stalk) {
			Stalk s = (Stalk) food;
			Body p = GameWorld.get().getCurrentMap().getBody().get(s.getPlantYukkuri());
			if (s.getZ() == 0 && p == null) {
				eatFood(b, FoodType.STALK, Math.min(b.getEatAmount(), s.getAmount()));
				s.eatStalk(Math.min(b.getEatAmount(), s.getAmount()));
			} else {
				if (p != null) {
					p.removeStalk(s);
					s.setPlantYukkuri(null);
					if (p.getBaryState() == BaryInUGState.ALL ||
							(p.getBaryState() == BaryInUGState.NEARLY_ALL && !p.hasOkazari())) {
						b.setMessage(GameMessages.getMessage(b, MessagePool.Action.FindVegetable), fullmessage);
						b.setHappiness(Happiness.VERY_HAPPY);
					}
					b.stay();
				}
			}
		} else if (food instanceof Vomit) {
			Vomit f = (Vomit) food;
			eatFood(b, FoodType.VOMIT, b.getEatAmount());
			f.eatVomit(b.getEatAmount());
		}

		if (goodsweets) {
			b.addAmaamaDiscipline(5);
		} else if (sweets) {
			b.addAmaamaDiscipline(3);
		} else if (food instanceof Body) {
			b.addAmaamaDiscipline(1);
		} else {
			b.addAmaamaDiscipline(-1);
		}

		if (b.isFull()) {
			if (b.isNotNYD()) {
				if (sweets) {
					b.setMessage(GameMessages.getMessage(b, MessagePool.Action.EatingAmaama), false);
					b.setEating(true);
					b.stay();
				} else {
					b.setMessage(GameMessages.getMessage(b, MessagePool.Action.Full), fullmessage);
					b.stay();
					b.clearActions();
				}
			}
		}
		if (!b.isbFirstEatStalk()) {
			b.setbFirstEatStalk(true);
		}
		return true;
	}

	private static void eatFood(Body b, FoodType foodType, int amount) {
		FoodLogic.eatFood(b, foodType, amount);
	}
}
