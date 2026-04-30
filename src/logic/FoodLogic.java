package src.logic;
import src.util.GameEnvironment;

import java.util.Map;

import src.util.GameWorld;
import src.base.Body;
import src.base.Obj;
import src.draw.Translate;
import src.enums.AgeState;
import src.enums.BaryInUGState;
import src.enums.FootBake;
import src.enums.PublicRank;
import src.game.Stalk;
import src.item.Food;
import src.item.Food.FoodType;

/***************************************************
	餌関係の処理
 */
public class FoodLogic {


	/**
	 *  フィールド内から餌候補の検索と移動、捕食処理
	 * @param b ゆっくり
	 * @return 処理が行われたか
	 */
	public static final boolean checkFood(Body b) {
		/*流れとしては、C1→C2→B1→B2　(Aはキャンセル判定)といった感じ
		*/
		boolean[] forceEat = { false };

		//A.餌行動の終了
		/*		// 他の用事がある場合
				if(b.isToBody() || b.isToBed() || (b.isToShit() && !b.isSoHungry() && !(b.isAdult() && b.intelligence == Intelligence.WISE)) || ( b.isAdult() && b.isToSukkiri()) || b.isToSteal() ){
					return false;
				}*/

		if (FoodActionGate.shouldSkipBeforeSearch(b, forceEat)) {
			return false;
		}

		//B1.餌補足済みの時の特殊行動
		// 食べる対象が決まっていたら到達したかチェック
		Obj food = b.takeMoveTarget();

		//対象が決まってる時
		if ((b.isToFood() || b.isToTakeout()) && food != null) {
			// 途中で消されてたら他の餌候補を探す
			if (food.isRemoved()) {
				b.clearActions();
				return false;
			}
			//茎の場合の探索
			if (food instanceof Stalk) {
				Body p = GameWorld.get().getCurrentMap().getBody().get(((Stalk) food).getPlantYukkuri());
				// 自分の茎は無視
				if (p == b) {
					b.clearActions();
					return false;
				}
				if (p != null) {
					// 地中に埋まっているなら引っこ抜いて食べる。それ以外はスキップ
					if (p.getBaryState() != BaryInUGState.ALL &&
							!(p.getBaryState() == BaryInUGState.NEARLY_ALL && !p.hasOkazari())) {
						b.clearActions();
						return false;
					}
				}
			}
			// 食べることができなかったら他の餌候補を探す
			else {
				if (!b.canflyCheck() && food.getZ() != 0) {
					b.clearActions();
					return false;
				}
			}

			if ((b.getStepDist() + 2) >= Translate.distance(b.getX(), b.getY(), food.getX(), food.getY())) {
				return FoodArrivalActionPolicy.handleArrivedFood(b, food, forceEat);
			}
			//餌に未到着の時
			else {
				return FoodApproachPolicy.handleUnarrivedFood(b, food);
			}
		}

		//C.餌探索
		Obj found = null;
		// うんうん奴隷の場合
		if (b.getPublicRank() == PublicRank.UnunSlave) {
			found = searchFoodForUnunSlave(b, forceEat);
		} else {
			// フィールドの餌検索
			if (b.isIdiot() || (b.getFootBakeLevel() == FootBake.CRITICAL && !b.canflyCheck())) {
				// 足りないゆ、完全足焼き用
				found = FoodNearestSearchPolicy.searchFoodNearest(b, forceEat);
			} else {
				if (b.isPredatorType() && !GameEnvironment.isPredatorSteam()) {
					// 捕食種用
					found = searchFoodPredetor(b, forceEat);
				} else {
					// 通常種用
					found = searchFoodStandard(b, forceEat);
				}
			}
		}

		//C2.探索して補足した餌に対する反応
		if (found != null) {
			return FoodFoundReaction.handleFoundFood(b, found, forceEat);
		}
		//何も見つからなかったとき
		else {
			FoodNoFoodReaction.handleNoFoodFound(b);
		}
		return false;
	}

	// 餌検索B
	// 一般用
	public static final Obj searchFoodStandard(Body b, boolean[] forceEat) {
		return FoodSearchPolicy.searchFoodStandard(b, forceEat);
	}

	// 餌検索C
	/**
	 *  捕食種用エサ検索
	 * @param b 捕食種
	 * @param forceEat 強制給餌フラグ
	 * @return 検索されたエサオブジェクト
	 */
	public static final Obj searchFoodPredetor(Body b, boolean[] forceEat) {
		Obj found = null;
		Obj found2 = null; // 副候補
		Obj found3 = null; // 死体候補
		int minDistance = b.getEYESIGHTorg();
		int minDistance2 = minDistance;
		int minDistance3 = minDistance;
		int size = b.getBodyAgeState().ordinal();
		int looks = -1000;
		int wallMode = b.getBodyAgeState().ordinal();
		forceEat[0] = false;
		// 飛行可能なら壁以外は通過可能
		if (b.canflyCheck()) {
			wallMode = AgeState.ADULT.ordinal();
		}

		// ゆっくりから検索
		for (Map.Entry<Integer, Body> entry : GameWorld.get().getCurrentMap().getBody().entrySet()) {
			Body d = entry.getValue();
			if (b == d) {
				continue;
			}
			if (!d.isDead()) {
				FoodPredatorCandidatePolicy.BodyCandidateResult result = FoodPredatorCandidatePolicy
						.considerLiveBody(b, d, minDistance, minDistance2, size, wallMode, found, found2);
				found = result.getFound();
				found2 = result.getFound2();
				minDistance = result.getMinDistance();
				minDistance2 = result.getMinDistance2();
				size = result.getSize();
			} else {
				FoodPredatorCandidatePolicy.BodyCandidateResult result = FoodPredatorCandidatePolicy
						.considerDeadBody(b, d, minDistance3, wallMode, found3);
				found3 = result.getFound3();
				minDistance3 = result.getMinDistance3();
			}
		}
		// 自分より小さい相手がいなかったら副目標にする
		if (found == null)
			found = found2;

		FoodPredatorFoodPolicy.FoodSearchResult foodResult = FoodPredatorFoodPolicy.searchFood(b, forceEat, wallMode,
				found, found3, minDistance, looks);
		found = foodResult.getFound();
		minDistance = foodResult.getMinDistance();
		looks = foodResult.getLooks();
		if (found == null && b.isFull()) {
			return found;
		}
		return FoodPredatorFallbackPolicy.searchFallbackFood(b, found, found3, minDistance, wallMode);
	}

	// 餌検索D
	// うんうん奴隷用
	private static final Obj searchFoodForUnunSlave(Body b, boolean[] forceEat) {
		return FoodUnunSlaveSearchPolicy.searchFoodForUnunSlave(b, forceEat);
	}

	// 餌検索A
	// 足りないゆ、足焼き用 最も近いものを適当に食べる
	private static final Obj searchFoodNearlest(Body b, boolean[] forceEat) {
		return FoodNearestSearchPolicy.searchFoodNearest(b, forceEat);
	}

	/**
	 *  食事処理
	 * @param b ゆっくり
	 * @param foodType エサタイプ
	 * @param amount 食事量
	 */
	public static final void eatFood(Body b, FoodType foodType, int amount) {
		FoodConsumptionPolicy.eatFood(b, foodType, amount);
	}

	/**
	 * お持ち帰り判定
	 * @param b ゆっくり
	 * @param o エサオブジェクト
	 * @return 持ち帰るかどうか
	 */
	public static boolean checkTakeout(Body b, Obj o) {
		return FoodTakeoutPolicy.checkTakeout(b, o);
	}

	/**
	 * 死体食べ判定
	 * @param b ゆっくり
	 * @param p 死体
	 * @return 食べるかどうか
	 */
	public static boolean checkCanEatBody(Body b, Body p) {
		return FoodEligibility.checkCanEatBody(b, p);
	}

}
