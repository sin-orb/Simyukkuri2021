package src.entity.core.living.yukkuri;

import java.util.Map;
import java.util.Set;

import src.entity.core.Entity;
import src.entity.core.world.item.Food;
import src.entity.core.world.mobile.Shit;
import src.enums.PurposeOfMoving;
import src.enums.TakeoutItemType;
import src.enums.Where;
import src.system.MapPlaceData;
import src.draw.Translate;
import src.util.GameWorld;
import src.util.GameMessages;
import src.system.MessagePool;

/**
 * ゆっくりの所持品・持ち帰り関連を切り出した委譲クラス.
 */
public final class YukkuriCarryDelegate {
	private final Yukkuri body;

	/**
	 * 持ち帰りアイテム関連を扱う委譲を生成する.
	 *
	 * @param body 対象のゆっくり
	 */
	public YukkuriCarryDelegate(Yukkuri body) {
		this.body = body;
	}

	/**
	 * アイテムを持たせる.
	 *
	 * @param key 持たせるアイテム種別
	 * @param val 持たせる実体
	 */
	public void setCarryItem(TakeoutItemType key, Entity val) {
		body.getCarryItems().put(key, val.objId);
		body.setInOutTakeoutItem(true);
		body.setToTakeout(false);
		if (body.getPurposeOfMoving() == PurposeOfMoving.TAKEOUT) {
			body.setPurposeOfMoving(PurposeOfMoving.NONE);
		}

		if (val instanceof Shit) {
			Map<Integer, Shit> shits = GameWorld.get().getCurrentMap().getShit();
			shits.remove(val.objId);
			GameWorld.get().getCurrentMap().getTakenOutShit().put(val.objId, (Shit) val);
			val.setWhere(Where.IN_YUKKURI);
		}

		if (val instanceof Food) {
			Map<Integer, Food> foods = GameWorld.get().getCurrentMap().getFood();
			foods.remove(val.objId);
			GameWorld.get().getCurrentMap().getTakenOutFood().put(val.objId, (Food) val);
			val.setWhere(Where.IN_YUKKURI);
		}
	}

	/**
	 * 持っているアイテムを取り出して落とす.
	 *
	 * @param key 取り出すアイテム種別
	 * @return 落とした実体
	 */
	public Entity dropTakeoutItem(TakeoutItemType key) {
		Entity val = takeTakenOutItem(key);
		if (val == null) {
			body.getCarryItems().remove(key);
			return null;
		}
		body.setInOutTakeoutItem(true);
		body.setMessage(GameMessages.getMessage(body, MessagePool.Action.DropItem));

		if (val instanceof Shit) {
			Map<Integer, Shit> shits = GameWorld.get().getCurrentMap().getShit();
			shits.put(val.objId, (Shit) val);
			GameWorld.get().getCurrentMap().getTakenOutShit().remove(val.objId);
			val.setCalcX(body.getX());
			if (body.getY() + 3 <= Translate.getMapH()) {
				val.setCalcY(body.getY());
			} else {
				val.setCalcY(body.getY() + 3);
			}
			val.setCalcZ(body.getZ() + 10);
			val.setWhere(Where.ON_FLOOR);
			body.getCarryItems().remove(key);
		}
		if (val instanceof Food) {
			Map<Integer, Food> foods = GameWorld.get().getCurrentMap().getFood();
			foods.put(val.objId, (Food) val);
			GameWorld.get().getCurrentMap().getTakenOutFood().remove(val.objId);
			val.setCalcX(body.getX());
			if (body.getY() + 3 <= Translate.getMapH()) {
				val.setCalcY(body.getY() + 3);
			} else {
				val.setCalcY(body.getY());
			}
			val.setCalcZ(body.getZ() + 10);
			val.setWhere(Where.ON_FLOOR);
			body.getCarryItems().remove(key);
		}
		return val;
	}

	private Entity takeTakenOutItem(TakeoutItemType key) {
		Integer i = body.getCarryItems().get(key);
		if (i == null) {
			return null;
		}
		MapPlaceData m = GameWorld.get().getCurrentMap();
		if (m.getTakenOutFood().containsKey(i.intValue())) {
			return m.getTakenOutFood().get(i.intValue());
		}
		if (m.getTakenOutShit().containsKey(i.intValue())) {
			return m.getTakenOutShit().get(i.intValue());
		}
		return null;
	}

	/**
	 * すべての持ち帰りアイテムを落とす.
	 */
	public void dropAllTakeoutItem() {
		if (body.getCarryItems() == null || body.getCarryItems().size() == 0) {
			return;
		}
		Set<TakeoutItemType> keyset = body.getCarryItems().keySet();
		for (TakeoutItemType key : keyset) {
			dropTakeoutItem(key);
		}
	}
}
