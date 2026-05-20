package org.simyukkuri.logic;

import java.util.Map;

import org.simyukkuri.draw.Translate;
import org.simyukkuri.entity.core.Entity;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.world.bodylinked.Stalk;
import org.simyukkuri.entity.core.world.item.Food.FoodType;
import org.simyukkuri.enums.AgeState;
import org.simyukkuri.enums.BurialState;
import org.simyukkuri.enums.FootBake;
import org.simyukkuri.enums.PublicRank;
import org.simyukkuri.system.WorldState;
import org.simyukkuri.util.GameEnvironment;
import org.simyukkuri.util.GameWorld;

/***************************************************
 * 餌関係の処理
 */
public class FoodLogic {

	/**
	 * フィールド内から餌候補の検索と移動、捕食処理
	 * 
	 * @param body ゆっくり
	 * @return 処理が行われたか
	 */
	public static final boolean checkFood(Yukkuri body) {
		return checkFood(body, GameWorld.get().getCurrentWorldState());
	}

	/**
	 * 食べ物行動を処理して行動実行有無を返す。
	 *
	 * @param body ゆっくり
	 * @param ws ワールド状態
	 *
	 * @return 処理が実行された場合は true、それ以外は false
	 */
	public static final boolean checkFood(Yukkuri body, WorldState ws) {
		/*
		 * 流れとしては、C1→C2→B1→B2 (Aはキャンセル判定)といった感じ
		 */
		boolean[] forceEat = { false };

		// A.餌行動の終了
		/*
		 * // 他の用事がある場合
		 * if(body.isToYukkuri() || body.isToBed() || (body.isToShit() &&
		 * !body.isSoHungry() && !(body.isAdult() && body.intelligence ==
		 * Intelligence.WISE)) || (body.isAdult() && body.isToSukkiri()) ||
		 * body.isToSteal() ){
		 * return false;
		 * }
		 */

		if (FoodActionGate.shouldSkipBeforeSearch(body, forceEat)) {
			return false;
		}

		// B1.餌補足済みの時の特殊行動
		// 食べる対象が決まっていたら到達したかチェック
		Entity food = body.takeMoveTarget();

		// 対象が決まってる時
		if ((body.isToFood() || body.isToTakeout()) && food != null) {
			// 途中で消されてたら他の餌候補を探す
			if (food.isRemoved()) {
				body.clearActions();
				return false;
			}
			// 茎の場合の探索
			if (food instanceof Stalk) {
				Yukkuri plantBody = ws.getYukkuriRegistry().get(((Stalk) food).getPlantYukkuri());
				// 自分の茎は無視
				if (plantBody == body) {
					body.clearActions();
					return false;
				}
				if (plantBody != null) {
					// 地中に埋まっているなら引っこ抜いて食べる。それ以外はスキップ
					if (plantBody.getBurialState() != BurialState.ALL &&
							!(plantBody.getBurialState() == BurialState.NEARLY_ALL && !plantBody.hasOkazari())) {
						body.clearActions();
						return false;
					}
				}
			}
			// 食べることができなかったら他の餌候補を探す
			else {
				if (!body.canflyCheck() && food.getZ() != 0) {
					body.clearActions();
					return false;
				}
			}

			if ((body.getStepDist() + 2) >= Translate.distance(body.getX(), body.getY(), food.getX(), food.getY())) {
				return FoodArrivalActionPolicy.handleArrivedFood(body, food, forceEat, ws);
			}
			// 餌に未到着の時
			else {
				return FoodApproachPolicy.handleUnarrivedFood(body, food);
			}
		}

		// C.餌探索
		Entity candidate = null;
		// うんうん奴隷の場合
		if (body.getPublicRank() == PublicRank.UNUN_SLAVE) {
			candidate = FoodUnunSlaveSearchPolicy.searchFoodForUnunSlave(body, forceEat, ws);
		} else {
			// フィールドの餌検索
			if (body.isIdiot() || (body.getFootBakeLevel() == FootBake.CRITICAL && !body.canflyCheck())) {
				// 足りないゆ、完全足焼き用
				candidate = FoodNearestSearchPolicy.searchFoodNearest(body, forceEat, ws);
			} else {
				if (body.isPredatorType() && !GameEnvironment.isPredatorSteam()) {
					// 捕食種用
					candidate = searchFoodPredetor(body, forceEat, ws);
				} else {
					// 通常種用
					candidate = searchFoodStandard(body, forceEat, ws);
				}
			}
		}

		// C2.探索して補足した餌に対する反応
		if (candidate != null) {
			return FoodFoundReaction.handleFoundFood(body, candidate, forceEat);
		}
		// 何も見つからなかったとき
		else {
			FoodNoFoodReaction.handleNoFoodFound(body);
		}
		return false;
	}

	// 餌検索B
	// 一般用
	/**
	 * 一般種用の食べ物を探索して返す。
	 *
	 * @param body ゆっくり
	 * @param forceEat 強制給餌フラグ
	 *
	 * @return 対象を発見した場合はそのオブジェクト、見つからない場合は null
	 */
	public static final Entity searchFoodStandard(Yukkuri body, boolean[] forceEat) {
		return FoodSearchPolicy.searchFoodStandard(body, forceEat);
	}

	/**
	 * 一般種用の食べ物を探索して返す。
	 *
	 * @param body ゆっくり
	 * @param forceEat 強制給餌フラグ
	 * @param ws ワールド状態
	 *
	 * @return 対象を発見した場合はそのオブジェクト、見つからない場合は null
	 */
	public static final Entity searchFoodStandard(Yukkuri body, boolean[] forceEat, WorldState ws) {
		return FoodSearchPolicy.searchFoodStandard(body, forceEat, ws);
	}

	// 餌検索C
	/**
	 * 捕食種用エサ検索
	 * 
	 * @param body     捕食種
	 * @param forceEat 強制給餌フラグ
	 * @return 検索されたエサオブジェクト
	 */
	public static final Entity searchFoodPredetor(Yukkuri body, boolean[] forceEat) {
		return searchFoodPredetor(body, forceEat, GameWorld.get().getCurrentWorldState());
	}

	/**
	 * 捕食種用の食べ物を探索して返す。
	 *
	 * @param body ゆっくり
	 * @param forceEat 強制給餌フラグ
	 * @param ws ワールド状態
	 *
	 * @return 対象を発見した場合はそのオブジェクト、見つからない場合は null
	 */
	public static final Entity searchFoodPredetor(Yukkuri body, boolean[] forceEat, WorldState ws) {
		Entity candidate = null;
		Entity candidate2 = null; // 副候補
		Entity deadCandidate = null; // 死体候補
		int nearestDistance = body.getEyesightBase();
		int secondaryNearestDistance = nearestDistance;
		int deadNearestDistance = nearestDistance;
		int size = body.getAgeState().ordinal();
		int looks = -1000;
		int wallMode = body.getAgeState().ordinal();
		forceEat[0] = false;
		// 飛行可能なら壁以外は通過可能
		if (body.canflyCheck()) {
			wallMode = AgeState.ADULT.ordinal();
		}

		// ゆっくりから検索
		for (Map.Entry<Integer, Yukkuri> entry : ws.getYukkuriRegistry().entrySet()) {
			Yukkuri candidateBody = entry.getValue();
			if (body == candidateBody) {
				continue;
			}
			if (!candidateBody.isDead()) {
				FoodPredatorCandidatePolicy.SearchResult result = FoodPredatorCandidatePolicy
						.considerLiveYukkuri(body, candidateBody, nearestDistance, secondaryNearestDistance, size,
								wallMode, candidate, candidate2);
				candidate = result.getNearestLiveObject();
				candidate2 = result.getNearestOtherObject();
				nearestDistance = result.getNearestLiveDistance();
				secondaryNearestDistance = result.getNearestOtherDistance();
				size = result.getSize();
			} else {
				FoodPredatorCandidatePolicy.SearchResult result = FoodPredatorCandidatePolicy
						.considerDeadYukkuri(body, candidateBody, deadNearestDistance, wallMode, deadCandidate);
				deadCandidate = result.getNearestDeadObject();
				deadNearestDistance = result.getNearestDeadDistance();
			}
		}
		// 自分より小さい相手がいなかったら副目標にする
		if (candidate == null)
			candidate = candidate2;

		FoodPredatorFoodPolicy.FoodSearchResult foodResult = FoodPredatorFoodPolicy.searchFood(body, forceEat, wallMode,
				candidate, deadCandidate, nearestDistance, looks, ws);
		candidate = foodResult.getNearestObject();
		nearestDistance = foodResult.getNearestDistance();
		looks = foodResult.getLooks();
		if (candidate == null && body.isFull()) {
			return candidate;
		}
		return FoodPredatorFallbackPolicy.searchFallbackFood(body, candidate, deadCandidate, nearestDistance, wallMode, ws);
	}

	/**
	 * 食事処理
	 * 
	 * @param body     ゆっくり
	 * @param foodType エサタイプ
	 * @param amount   食事量
	 */
	public static final void eatFood(Yukkuri body, FoodType foodType, int amount) {
		FoodConsumptionPolicy.eatFood(body, foodType, amount);
	}

	/**
	 * お持ち帰り判定
	 * 
	 * @param body ゆっくり
	 * @param o    エサオブジェクト
	 * @return 持ち帰るかどうか
	 */
	public static boolean checkTakeout(Yukkuri body, Entity target) {
		return FoodTakeoutPolicy.checkTakeout(body, target);
	}

	/**
	 * 持ち帰り行動の可否を判定して返す。
	 *
	 * @param body ゆっくり
	 * @param target 対象エンティティ
	 * @param ws ワールド状態
	 *
	 * @return 処理が実行された場合は true、それ以外は false
	 */
	public static boolean checkTakeout(Yukkuri body, Entity target, WorldState ws) {
		return FoodTakeoutPolicy.checkTakeout(body, target, ws);
	}

	/**
	 * 死体食べ判定
	 * 
	 * @param body   ゆっくり
	 * @param target 死体
	 * @return 食べるかどうか
	 */
	public static boolean checkCanEatYukkuri(Yukkuri body, Yukkuri target) {
		return FoodEligibility.checkCanEatYukkuri(body, target);
	}

}
