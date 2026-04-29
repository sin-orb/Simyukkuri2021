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
import src.util.YukkuriUtil;

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
	 * @param b 判定するゆっくり
	 * @param o 対象オブジェクト
	 * @return 持ち帰るならtrue
	 */
	public static boolean checkTakeout(Body b, Obj o) {
		if (b == null || o == null) {
			return false;
		}
		if (b.isVeryHungry()) {
			return false;
		}
		if (b.getPublicRank() == PublicRank.UnunSlave) {
			return checkShitTakeout(b, o);
		}
		return checkFoodTakeout(b, o);
	}

	private static boolean checkShitTakeout(Body b, Obj o) {
		if (!(o instanceof Shit)) {
			return false;
		}
		if (b.getTakeoutItem(TakeoutItemType.SHIT) != null) {
			return false;
		}
		Shit s = (Shit) o;
		boolean hasSlaveToilet = false;
		boolean inSlaveToilet = false;
		for (Map.Entry<Integer, Toilet> entry : GameWorld.get().getCurrentMap().getToilet().entrySet()) {
			Toilet t = entry.getValue();
			if (!t.isForSlave()) {
				continue;
			}
			hasSlaveToilet = true;
			if (t.checkHitObj(null, s)) {
				inSlaveToilet = true;
				break;
			}
		}
		return hasSlaveToilet && !inSlaveToilet;
	}

	private static boolean checkFoodTakeout(Body b, Obj o) {
		if (b.isExciting() || b.isRaper()) {
			return false;
		}
		if (!(o instanceof Food)) {
			return false;
		}
		Food food = (Food) o;
		if (food.isEmpty()) {
			return false;
		}
		if (b.getTakeoutItem(TakeoutItemType.FOOD) != null) {
			return false;
		}
		Obj oFav = b.getFavItem(FavItemType.BED);
		if (!(oFav instanceof ObjEX)) {
			return false;
		}
		ObjEX oExFav = (ObjEX) oFav;
		if (YukkuriUtil.getBodyInstance(b.getPartner()) == null && b.getChildrenListSize() == 0) {
			return false;
		}
		for (Map.Entry<Integer, Food> entry : GameWorld.get().getCurrentMap().getFood().entrySet()) {
			Food foodOnMyBed = entry.getValue();
			if (foodOnMyBed.isEmpty()) {
				continue;
			}
			if (oExFav.checkHitObj(foodOnMyBed, false)) {
				return false;
			}
		}
		for (Map.Entry<Integer, Bed> entry : GameWorld.get().getCurrentMap().getBed().entrySet()) {
			Bed bed = entry.getValue();
			if (bed.checkHitObj(o, false)) {
				return false;
			}
		}
		return true;
	}
}
