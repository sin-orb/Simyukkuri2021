package org.simyukkuri.logic;

import org.simyukkuri.entity.core.Entity;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;

/**
 * 到着前の餌追従.
 */
public final class FoodApproachPolicy {

	private FoodApproachPolicy() {
	}

	/**
	 * 目標へ移動する.
	 */
	public static boolean handleUnarrivedFood(Yukkuri body, Entity food) {
		if (!body.canflyCheck()) {
			body.moveTo(food.getX(), food.getY(), 0);
		} else {
			body.moveTo(food.getX(), food.getY(), food.getZ());
		}
		return true;
	}
}
