package src.logic;

import java.util.LinkedList;
import java.util.List;

import src.base.Body;
import src.base.EventPacket;
import src.base.Obj;
import src.draw.Translate;
import src.enums.EnumRelationMine;
import src.enums.GatheringDirection;
import src.enums.PublicRank;

/***************************************************
 * ゆっくり同士の処理
 */
public class BodyLogic {

	public static enum ActionGo {
		NONE, WAIT, GO, BACK
	};

	/**
	 * 自分が相手にとって何なのか判定
	 * 
	 * @param me  自分
	 * @param you 相手
	 * @return 関係性
	 */
	public static final EnumRelationMine checkMyRelation(Body me, Body you) {
		return BodyRelations.checkMyRelation(me, you);
	}

	/**
	 * 行動トリガーと、移動先決定
	 * 
	 * @param body ゆっくり
	 * @return 処理を行ったかどうか
	 */
	public static final boolean checkPartner(Body body) {
		if (BodyPartnerEntryRule.shouldSkipPartnerAction(body)) {
			return false;
		}

		// 初期値
		boolean handled = false;
		Body targetBody = null;
		int nearestDistance = body.getEyesightBase();
		int secondNearestDistance = body.getEyesightBase();

		Body mappedTarget = BodyPartnerEntryRule.resolveMappedTarget(body, nearestDistance);
		if (mappedTarget != null) {
			return doActionOther(mappedTarget, body);
		}

		/////////////////////////////////
		// 移動判定
		/////////////////////////////////
		Body bodyHasOkazari = null;
		Body bodyHasOkazariAndPherommone = null;
		Body bodyHasPheromone = null;
		Body bodyOldMoveTarget = BodyPartnerEntryRule.resolveMoveTarget(body);

		// 発情時
		// レイパーですっきり中なら続けて同ターゲットに
		Body partner = BodyPartnerEntryRule.getPartnerIfPreferred(body);
		if (body.isExciting() && body.isRaper() && body.isToSukkiri() && bodyOldMoveTarget != null
				&& !bodyOldMoveTarget.isRaper()) {
			targetBody = bodyOldMoveTarget;
			nearestDistance = Translate.distance(body.getX(), body.getY(), targetBody.getX(), targetBody.getY());
		}
		// つがいが既にいるなら優先して向かう
		else if (partner != null) {
			targetBody = partner;
			nearestDistance = Translate.distance(body.getX(), body.getY(), partner.getX(), partner.getY());
		}

		// 自分が泣き叫んでいるなら、他ゆに目をくれない。
		else if (BodyPartnerEntryRule.shouldGoToParent(body)) {
			checkNearParent(body);
			return false;
		} else {
			if (targetBody == null) {
				BodyPartnerSearchRule.SearchResult searchResult = BodyPartnerSearchRule.selectTargets(body, targetBody,
						nearestDistance, secondNearestDistance);
				targetBody = searchResult.getFound();
				bodyHasOkazari = searchResult.getBodyHasOkazari();
			}
		}

		// 目標が定まっていないなら終了
		if (targetBody == null) {
			if (BodyPartnerEntryRule.handleNoFoundTarget(body)) {
				return true;
			}
			return handled;
		}

		// 目標が定まったら移動セット
		int mz = 0;
		// 飛行種はZも移動可能
		if (body.canflyCheck()) {
			mz = targetBody.getZ();
		}

		// ゆっくり同士が重ならないように目標地点は体のサイズを考慮
		int colX = calcCollisionX(body, targetBody);
		return BodyPartnerActionRule.handleFoundTarget(body, targetBody, bodyHasOkazari, colX, mz);
	}

	/**
	 * 接触している場合の動作
	 * 
	 * @param self 自分
	 * @param target 相手
	 * @return 動作を行った場合
	 */
	public static final boolean doActionOther(Body self, Body target) {
		// 途中で消されてたら他の候補を探す
		if (self.isRemoved()) {
			target.clearActions();
			return false;
		}

		// 相手が宙に浮いてたら無視
		if (!target.canflyCheck() && self.getZ() != 0) {
			target.clearActions();
			return false;
		}

		if (target.isNYD()) {
			return false;
		}

		// 片方だけがうんうん奴隷の場合はなにもしない
		if (target.getPublicRank() != self.getPublicRank() && !(target.isRaper() && target.isExciting())) {
			// 盗みに行かない場合は終了
			if (!target.isToSteal()) {
				target.clearActions();
				return false;
			}
		}

		int collisionOffsetX = Translate.invertX((int) ((target.getCollisionX() + self.getCollisionX()) * 0.6f), self.getY());
		collisionOffsetX = Translate.transSize(collisionOffsetX);
		int distanceX = Math.abs(target.getX() - self.getX());
		int distanceY = Math.abs(target.getY() - self.getY());
		int collisionRange = Math.abs(collisionOffsetX - distanceX);
		// 見つかった相手に対するコリジョンチェック
		// 体が隣接するように横長のボックスで判定を取る
		// Y軸の閾値をrangeXに比例させる(distY < 5だと厳しすぎて到達できない問題の修正)
		if (BodyContactRule.handleAdjacentContact(self, target, collisionOffsetX, distanceY, collisionRange)) {
			return true;
		}
		if (target.isToSteal()) {
			return false;
		}

		// 非接触状態の場合
		else {
			BodyApproachRule.handleApproach(self, target, collisionOffsetX);
		}
		return true;
	}

	/**
	 * 体同士が触れる位置のX座標を求める
	 * 
	 * @param from ゆっくり
	 * @param to   相手のゆっくり
	 * @return X座標
	 */
	public static final int calcCollisionX(Body from, Body to) {
		if (from == null || to == null) {
			return 0;
		}

		int collisionOffsetX = Translate.invertX((int) ((from.getCollisionX() + to.getCollisionX()) * 0.6f), to.getY());
		collisionOffsetX = Translate.transSize(collisionOffsetX);

		// お互いの位置から右と左最短距離を選択
		int directionSign = 1;
		if (from.getX() < to.getX())
			directionSign = -1;
		collisionOffsetX *= directionSign;

		return collisionOffsetX;
	}

	/**
	 * 他のゆっくりがプレイヤーにすりすりされていた場合の行動判定
	 * 
	 * @param body 自分
	 * @param bodyTarget 相手
	 * @return 行動をしたかどうか
	 */
	public static final ActionGo checkActionSurisuriFromPlayer(Body body, Body bodyTarget) {
		return BodySurisuriRule.checkActionSurisuriFromPlayer(body, bodyTarget);
	}

	/**
	 * 婚姻候補のリストを作る。既婚の場合は、相手のみを含むリストを作る
	 * 
	 * @param body 自分
	 * @param age ゆん生のステージ
	 * @return 婚姻候補のリスト
	 */
	public static final List<Body> createActiveFianceeList(Body body, int age) {
		return BodySelectionRule.createActiveFianceeList(body, age);
	}

	/**
	 * アクティブな赤ゆ/子ゆのリストを作成する.
	 * 
	 * @param body ゆっくり
	 * @param includeChildren 子ゆっくりを入れるかどうか（これがfalseなら赤ゆのみのリストになる）
	 * @return アクティブな赤ゆ/子ゆのリスト
	 */
	public static final List<Body> createActiveChildList(Body body, boolean includeChildren) {
		return BodySelectionRule.createActiveChildList(body, includeChildren);
	}

	/**
	 * ぜんゆん集合
	 */
	public static final void gatheringYukkuri() {
		BodyGatheringRule.gatheringYukkuri();
	}

	/**
	 * ぜんゆん集合(四角形前面)
	 * 
	 * @param topBody   先頭ゆ
	 * @param targetList 並べるゆっくりのリスト
	 * @return 並んだかどうか
	 */
	public static final boolean gatheringYukkuriFront(Body topBody, List<Body> targetList) {
		return BodyGatheringRule.gatheringYukkuriFront(topBody, targetList);
	}

	/**
	 * ぜんゆん集合(四角形前面)
	 * 
	 * @param topBody   先頭ゆ
	 * @param targetList 並べるゆっくりのリスト
	 * @param event     イベント
	 * @return 並んだかどうか
	 */
	public static final boolean gatheringYukkuriFront(Body topBody, List<Body> targetList, EventPacket event) {
		return BodyGatheringRule.gatheringYukkuriFront(topBody, targetList, event);
	}

	/**
	 * ぜんゆん集合
	 * 
	 * @param topObject  先頭ゆ
	 * @param targetList 並べるゆっくりのリスト
	 * @param direction  並べる方向
	 * @param event      イベント
	 * @return 並んだかどうか
	 */
	public static final boolean gatheringYukkuriSquare(Obj topObject, Body[] targetList, GatheringDirection direction,
			EventPacket event) {
		return BodyGatheringRule.gatheringYukkuriSquare(topObject, targetList, direction, event);
	}

	/**
	 * ぜんゆん集合(先頭の後ろに一列)
	 * 
	 * @param topBody   先頭ゆ
	 * @param targetList 並べるゆっくりのリスト
	 * @param event     イベント
	 * @return 並んだかどうか
	 */
	public static final boolean gatheringYukkuriBackLine(Body topBody, List<Body> targetList, EventPacket event) {
		return BodyGatheringRule.gatheringYukkuriBackLine(topBody, targetList, event);
	}

	/**
	 * うんうんどれいの感情処理
	 * 
	 * @param body 自分
	 * @param bodyTarget 相手
	 * @return 処理を行ったかどうか
	 */
	public static boolean checkEmotionFromUnunSlave(Body body, Body bodyTarget) {
		return BodyUnunSlaveEmotionRule.checkEmotionFromUnunSlave(body, bodyTarget);
	}

	/**
	 * 近い親をチェックする.
	 * 
	 * @param body 赤ゆなど
	 */
	public static void checkNearParent(Body body) {
		BodyParentRule.checkNearParent(body);
	}

	/**
	 * 視界内に起きているゆっくりがいないかチェック
	 * 
	 * @param body ゆっくり
	 * @return 視界内に起きているゆっくりがいるかどうか
	 */
	public static boolean checkWakeupOtherYukkuri(Body body) {
		return BodyWakeupRule.checkWakeupOtherYukkuri(body);
	}
}
