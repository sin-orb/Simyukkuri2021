package org.simyukkuri.logic;

import java.util.Map;

import org.simyukkuri.entity.core.Entity;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.world.WorldEntity;
import org.simyukkuri.entity.core.world.item.Bed;
import org.simyukkuri.entity.core.world.item.Food;
import org.simyukkuri.entity.core.world.item.Toilet;
import org.simyukkuri.entity.core.world.mobile.Shit;
import org.simyukkuri.enums.FavItemType;
import org.simyukkuri.enums.PublicRank;
import org.simyukkuri.enums.TakeoutItemType;
import org.simyukkuri.util.GameWorld;

/**
 * お持ち帰り判定を集約するロジック。
 * <p>
 * 既存の {@link FoodLogic#checkTakeout(Yukkuri, Entity)} の実装を切り出し、
 * 食べ物とシットの判定を小さくまとめる。
 * </p>
 */
public final class FoodTakeoutPolicy {
	private FoodTakeoutPolicy() {
	}

	/**
	 * お持ち帰り対象かどうかを判定する。
	 *
	 * @param body   判定するゆっくり
	 * @param target 対象オブジェクト
	 * @return 持ち帰るならtrue
	 */
	public static boolean checkTakeout(Yukkuri body, Entity target) {
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

	private static boolean checkShitTakeout(Yukkuri body, Entity target) {
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

	private static boolean checkFoodTakeout(Yukkuri body, Entity target) {
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
		Entity favoriteBed = body.getFavoriteItem(FavItemType.BED);
		if (!(favoriteBed instanceof WorldEntity)) {
			return false;
		}
		WorldEntity favoriteBedObj = (WorldEntity) favoriteBed;
		if (org.simyukkuri.util.YukkuriLookup.getYukkuriById(body.getPartner()) == null && body.getChildrenListSize() == 0) {
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
