package org.simyukkuri.logic;

import org.simyukkuri.entity.core.Entity;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.world.bodylinked.Stalk;
import org.simyukkuri.entity.core.world.item.Food;
import org.simyukkuri.entity.core.world.item.Food.FoodType;
import org.simyukkuri.entity.core.world.mobile.Shit;
import org.simyukkuri.entity.core.world.mobile.Vomit;
import org.simyukkuri.enums.PublicRank;
import org.simyukkuri.system.MessagePool;
import org.simyukkuri.util.GameMessages;
import org.simyukkuri.util.GameRandom;

/**
 * 見つけた餌への反応をまとめたヘルパー。
 */
public final class FoodFoundReaction {
	private FoodFoundReaction() {
	}

	/**
	 * 発見した食べ物への反応処理を行い、行動継続可否を返す。
	 *
	 * @param body ゆっくり
	 * @param targetObject 対象エンティティ
	 * @param forceEat 強制給餌フラグ
	 *
	 * @return 食べ物への移動が継続する場合は true、キャンセルされた場合は false
	 */
	public static boolean handleFoundFood(Yukkuri body, Entity targetObject, boolean[] forceEat) {
		if (body.isOnlyAmaama() && body.getPublicRank() != PublicRank.UNUN_SLAVE) {
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
				if (body.isNotNyd()) {
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
				if (body.getPublicRank() == PublicRank.UNUN_SLAVE && body.isToTakeout()) {
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
