package src.logic;

import src.base.Body;
import src.base.Obj;
import src.enums.PublicRank;
import src.game.Shit;
import src.game.Stalk;
import src.game.Vomit;
import src.item.Food;
import src.item.Food.FoodType;
import src.system.MessagePool;
import src.util.GameMessages;
import src.util.GameRandom;

/**
 * 見つけた餌への反応をまとめたヘルパー。
 */
public final class FoodFoundReaction {
	private FoodFoundReaction() {
	}

	public static boolean handleFoundFood(Body b, Obj found, boolean[] forceEat) {
		if (b.isOnlyAmaama() && b.getPublicRank() != PublicRank.UnunSlave) {
			if (!b.isStarving()) {
				if (found instanceof Food) {
					Food f = (Food) found;
					if (f.getFoodType() != Food.FoodType.SWEETS1 && f.getFoodType() != Food.FoodType.SWEETS2
							&& f.getFoodType() != Food.FoodType.SWEETS_NORA1
							&& f.getFoodType() != Food.FoodType.SWEETS_NORA2
							&& f.getFoodType() != Food.FoodType.SWEETS_YASEI1
							&& f.getFoodType() != Food.FoodType.SWEETS_YASEI2) {
						b.setToFood(false);
						if (b.isTooHungry()) {
							b.setMessage(GameMessages.getMessage(b, MessagePool.Action.WantAmaama));
							b.setAngry();
						} else if (GameRandom.nextInt(150) == 0) {
							b.setMessage(GameMessages.getMessage(b, MessagePool.Action.WantAmaama));
							b.setAngry();
						}
						return false;
					}
				} else {
					return false;
				}
			}
		}
		if (b.isHungry() || forceEat[0] || b.isToTakeout()) {
			int mz = 0;
			if (b.canflyCheck()) {
				mz = found.getZ();
			}
			if (found instanceof Food) {
				if (b.isNotNYD()) {
					if (((Food) found).getFoodType() == FoodType.SWEETS1
							|| ((Food) found).getFoodType() == FoodType.SWEETS2
							|| ((Food) found).getFoodType() == FoodType.SWEETS_NORA1
							|| ((Food) found).getFoodType() == FoodType.SWEETS_NORA2
							|| ((Food) found).getFoodType() == FoodType.SWEETS_YASEI1
							|| ((Food) found).getFoodType() == FoodType.SWEETS_YASEI2) {
						b.setMessage(GameMessages.getMessage(b, MessagePool.Action.FindAmaama));
					} else if (b.isOnlyAmaama()) {
						b.setMessage(GameMessages.getMessage(b, MessagePool.Action.WantAmaama));
					} else {
						b.setMessage(GameMessages.getMessage(b, MessagePool.Action.WantFood));
					}
				}
				boolean takeOut = false;
				if (b.isToTakeout()) {
					takeOut = true;
				}
				b.moveToFood(found, ((Food) found).getFoodType(), found.getX(), found.getY(), mz);
				if (takeOut) {
					b.setToTakeout(true);
				}
			} else if (found instanceof Shit) {
				boolean takeOut = false;
				if (b.getPublicRank() == PublicRank.UnunSlave && b.isToTakeout()) {
					b.setMessage(GameMessages.getMessage(b, MessagePool.Action.TransportShit), false);
					takeOut = true;
				} else {
					b.setMessage(GameMessages.getMessage(b, MessagePool.Action.NoFood), false);
				}
				b.moveToFood(found, FoodType.SHIT, found.getX(), found.getY(), mz);
				if (takeOut) {
					b.setToTakeout(true);
				}
			} else if (found instanceof Body) {
				b.moveToFood(found, FoodType.BODY, found.getX(), found.getY(), mz);
			} else if (found instanceof Stalk) {
				b.moveToFood(found, FoodType.STALK, found.getX(), found.getY(), mz);
			} else if (found instanceof Vomit) {
				b.setMessage(GameMessages.getMessage(b, MessagePool.Action.NoFood), false);
				b.moveToFood(found, FoodType.VOMIT, found.getX(), found.getY(), mz);
			}
			return true;
		}
		return false;
	}
}
