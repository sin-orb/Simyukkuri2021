package src.logic;

import src.entity.core.Entity;
import src.entity.core.living.yukkuri.Yukkuri;
import src.entity.core.world.bodylinked.Stalk;
import src.entity.core.world.item.Food;
import src.entity.core.world.item.Food.FoodType;
import src.entity.core.world.mobile.Shit;
import src.entity.core.world.mobile.Vomit;
import src.enums.PublicRank;
import src.system.MessagePool;
import src.util.GameMessages;
import src.util.GameRandom;

/**
 * 見つけた餌への反応をまとめたヘルパー。
 */
public final class FoodFoundReaction {
	private FoodFoundReaction() {
	}

	public static boolean handleFoundFood(Yukkuri body, Entity targetObject, boolean[] forceEat) {
		if (body.isOnlyAmaama() && body.getPublicRank() != PublicRank.UnunSlave) {
			if (!body.isStarving()) {
				if (targetObject instanceof Food) {
					Food food = (Food) targetObject;
					if (food.getFoodType() != Food.FoodType.SWEETS1 && food.getFoodType() != Food.FoodType.SWEETS2
							&& food.getFoodType() != Food.FoodType.SWEETS_NORA1
							&& food.getFoodType() != Food.FoodType.SWEETS_NORA2
							&& food.getFoodType() != Food.FoodType.SWEETS_YASEI1
							&& food.getFoodType() != Food.FoodType.SWEETS_YASEI2) {
						body.setToFood(false);
						if (body.isTooHungry()) {
							body.setMessage(GameMessages.getMessage(body, MessagePool.Action.WantAmaama));
							body.setAngry();
						} else if (GameRandom.nextInt(150) == 0) {
							body.setMessage(GameMessages.getMessage(body, MessagePool.Action.WantAmaama));
							body.setAngry();
						}
						return false;
					}
				} else {
					return false;
				}
			}
		}
		if (body.isHungry() || forceEat[0] || body.isToTakeout()) {
			int destinationZ = 0;
			if (body.canflyCheck()) {
				destinationZ = targetObject.getZ();
			}
			if (targetObject instanceof Food) {
				if (body.isNotNYD()) {
					if (((Food) targetObject).getFoodType() == FoodType.SWEETS1
							|| ((Food) targetObject).getFoodType() == FoodType.SWEETS2
							|| ((Food) targetObject).getFoodType() == FoodType.SWEETS_NORA1
							|| ((Food) targetObject).getFoodType() == FoodType.SWEETS_NORA2
							|| ((Food) targetObject).getFoodType() == FoodType.SWEETS_YASEI1
							|| ((Food) targetObject).getFoodType() == FoodType.SWEETS_YASEI2) {
						body.setMessage(GameMessages.getMessage(body, MessagePool.Action.FindAmaama));
					} else if (body.isOnlyAmaama()) {
						body.setMessage(GameMessages.getMessage(body, MessagePool.Action.WantAmaama));
					} else {
						body.setMessage(GameMessages.getMessage(body, MessagePool.Action.WantFood));
					}
				}
				boolean takeOut = false;
				if (body.isToTakeout()) {
					takeOut = true;
				}
				body.moveToFood(targetObject, ((Food) targetObject).getFoodType(), targetObject.getX(),
						targetObject.getY(), destinationZ);
				if (takeOut) {
					body.setToTakeout(true);
				}
			} else if (targetObject instanceof Shit) {
				boolean takeOut = false;
				if (body.getPublicRank() == PublicRank.UnunSlave && body.isToTakeout()) {
					body.setMessage(GameMessages.getMessage(body, MessagePool.Action.TransportShit), false);
					takeOut = true;
				} else {
					body.setMessage(GameMessages.getMessage(body, MessagePool.Action.NoFood), false);
				}
				body.moveToFood(targetObject, FoodType.SHIT, targetObject.getX(), targetObject.getY(), destinationZ);
				if (takeOut) {
					body.setToTakeout(true);
				}
			} else if (targetObject instanceof Yukkuri) {
				body.moveToFood(targetObject, FoodType.BODY, targetObject.getX(), targetObject.getY(), destinationZ);
			} else if (targetObject instanceof Stalk) {
				body.moveToFood(targetObject, FoodType.STALK, targetObject.getX(), targetObject.getY(), destinationZ);
			} else if (targetObject instanceof Vomit) {
				body.setMessage(GameMessages.getMessage(body, MessagePool.Action.NoFood), false);
				body.moveToFood(targetObject, FoodType.VOMIT, targetObject.getX(), targetObject.getY(), destinationZ);
			}
			return true;
		}
		return false;
	}
}
