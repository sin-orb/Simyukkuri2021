package src.logic;

import src.base.Yukkuri;

/**
 * 死体を食べられるかどうかの判定ロジックを集約するクラス。
 * <p>
 * Phase 5では、{@link FoodLogic} が持っていた条件分岐を小さく切り出し、
 * テストから直接固定しやすい入口を作る。public APIの互換は {@link FoodLogic}
 * 側で維持する。
 * </p>
 */
public final class FoodEligibility {
	private FoodEligibility() {
	}

	/**
	 * 対象の死体を食べられるかどうかを判定する。
	 *
	 * @param eater 食べる側のゆっくり
	 * @param prey 食べられるか調べる対象
	 * @return 食べられる場合はtrue
	 */
	public static boolean checkCanEatBody(Yukkuri eater, Yukkuri prey) {
		if (eater.isPredatorType()) {
			return true;
		}
		if (!prey.isDead()) {
			return false;
		}
		if (prey.hasBindStalk()) {
			return false;
		}
		if (!eater.isVeryRude() && prey.hasOkazari()) {
			return false;
		}
		if (eater.getIntelligence() != src.enums.Intelligence.FOOL && eater.findSick(prey) && !eater.isTooHungry()) {
			return false;
		}
		return true;
	}
}
