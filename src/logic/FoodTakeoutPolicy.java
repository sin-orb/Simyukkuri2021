package src.logic;

import java.util.Map;

import src.base.Body;
import src.base.Obj;
import src.base.ObjEX;
import src.enums.FavItemType;
import src.enums.PublicRank;
import src.enums.TakeoutItemType;
import src.game.Shit;
import src.item.Bed;
import src.item.Food;
import src.item.Toilet;
import src.util.GameWorld;

/**
 * お持ち帰り判定を集約するロジック。
 * <p>
 * 既存の {@link FoodLogic#checkTakeout(Body, Obj)} の実装を切り出し、
 * 食べ物とシットの判定を小さくまとめる。
 * </p>
 */
public final class FoodTakeoutPolicy {
	private FoodTakeoutPolicy() {
	}

	/**
	 * お持ち帰り対象かどうかを判定する。
	 *
	 * @param body 判定するゆっくり
	 * @param target 対象オブジェクト
	 * @return 持ち帰るならtrue
	 */
	public static boolean checkTakeout(Body body, Obj target) {
		if (body == null || target == null) {
			return false;
		}
		if (body.isVeryHungry()) {
			return false;
		}
		if (body.getPublicRank() == PublicRank.UnunSlave) {
			return checkShitTakeout(body, target);
		}
		return checkFoodTakeout(body, target);
	}

	private static boolean checkShitTakeout(Body body, Obj target) {
		if (!(target instanceof Shit)) {
			return false;
		}
		if (body.getCarryItem(TakeoutItemType.SHIT) != null) {
			return false;
		}
		Shit shit = (Shit) target;
		boolean hasSlaveToilet = false;
		boolean inSlaveToilet = false;
		for (Map.Entry<Integer, Toilet> entry : GameWorld.get().getCurrentMap().getToilet().entrySet()) {
			Toilet toilet = entry.getValue();
			if (!toilet.isForSlave()) {
				continue;
			}
			hasSlaveToilet = true;
			if (toilet.checkHitObj(null, shit)) {
				inSlaveToilet = true;
				break;
			}
		}
		return hasSlaveToilet && !inSlaveToilet;
	}

	private static boolean checkFoodTakeout(Body body, Obj target) {
		if (body.isExciting() || body.isRaper()) {
			return false;
		}
		if (!(target instanceof Food)) {
			return false;
		}
		Food food = (Food) target;
		if (food.isEmpty()) {
			return false;
		}
		if (body.getCarryItem(TakeoutItemType.FOOD) != null) {
			return false;
		}
		Obj favoriteBed = body.getFavoriteItem(FavItemType.BED);
		if (!(favoriteBed instanceof ObjEX)) {
			return false;
		}
		ObjEX favoriteBedObj = (ObjEX) favoriteBed;
		if (src.util.BodyRegistry.getBodyInstance(body.getPartner()) == null && body.getChildrenListSize() == 0) {
			return false;
		}
		for (Map.Entry<Integer, Food> entry : GameWorld.get().getCurrentMap().getFood().entrySet()) {
			Food foodOnFavoriteBed = entry.getValue();
			if (foodOnFavoriteBed.isEmpty()) {
				continue;
			}
			if (favoriteBedObj.checkHitObj(foodOnFavoriteBed, false)) {
				return false;
			}
		}
		for (Map.Entry<Integer, Bed> entry : GameWorld.get().getCurrentMap().getBed().entrySet()) {
			Bed bed = entry.getValue();
			if (bed.checkHitObj(target, false)) {
				return false;
			}
		}
		return true;
	}
}
