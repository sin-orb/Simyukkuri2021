package src.logic;

import src.base.Body;
import src.base.Obj;

/**
 * 到着前の餌追従.
 */
public final class FoodApproachPolicy {

	private FoodApproachPolicy() {
	}

	/**
	 * 目標へ移動する.
	 */
	public static boolean handleUnarrivedFood(Body b, Obj food) {
		if (!b.canflyCheck()) {
			b.moveTo(food.getX(), food.getY(), 0);
		} else {
			b.moveTo(food.getX(), food.getY(), food.getZ());
		}
		return true;
	}
}
