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
	public static boolean handleUnarrivedFood(Body body, Obj food) {
		if (!body.canflyCheck()) {
			body.moveTo(food.getX(), food.getY(), 0);
		} else {
			body.moveTo(food.getX(), food.getY(), food.getZ());
		}
		return true;
	}
}
