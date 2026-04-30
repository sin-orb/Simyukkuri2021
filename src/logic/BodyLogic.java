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

	public static enum eActionGo {
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
	 * @param b ゆっくり
	 * @return 処理を行ったかどうか
	 */
	public static final boolean checkPartner(Body b) {
		if (BodyPartnerEntryRule.shouldSkipPartnerAction(b)) {
			return false;
		}

		// 初期値
		boolean ret = false;
		Body found = null;
		int minDistance = b.getEYESIGHTorg();
		int secondMinDistance = b.getEYESIGHTorg();

		Body mappedTarget = BodyPartnerEntryRule.resolveMappedTarget(b, minDistance);
		if (mappedTarget != null) {
			return doActionOther(mappedTarget, b);
		}

		/////////////////////////////////
		// 移動判定
		/////////////////////////////////
		Body bodyHasOkazari = null;
		Body bodyHasOkazariAndPherommone = null;
		Body bodyHasPheromone = null;
		Body bodyOldMoveTarget = BodyPartnerEntryRule.resolveMoveTarget(b);

		// 発情時
		// レイパーですっきり中なら続けて同ターゲットに
		Body pa = BodyPartnerEntryRule.getPartnerIfPreferred(b);
		if (b.isExciting() && b.isRaper() && b.isToSukkiri() && bodyOldMoveTarget != null
				&& !bodyOldMoveTarget.isRaper()) {
			found = bodyOldMoveTarget;
			minDistance = Translate.distance(b.getX(), b.getY(), found.getX(), found.getY());
		}
		// つがいが既にいるなら優先して向かう
		else if (pa != null) {
			found = pa;
			minDistance = Translate.distance(b.getX(), b.getY(), pa.getX(), pa.getY());
		}

		// 自分が泣き叫んでいるなら、他ゆに目をくれない。
		else if (BodyPartnerEntryRule.shouldGoToParent(b)) {
			checkNearParent(b);
			return false;
		} else {
			if (found == null) {
				BodyPartnerSearchRule.SearchResult searchResult = BodyPartnerSearchRule.selectTargets(b, found,
						minDistance, secondMinDistance);
				found = searchResult.getFound();
				bodyHasOkazari = searchResult.getBodyHasOkazari();
			}
		}

		// 目標が定まっていないなら終了
		if (found == null) {
			if (BodyPartnerEntryRule.handleNoFoundTarget(b)) {
				return true;
			}
			return ret;
		}

		// 目標が定まったら移動セット
		int mz = 0;
		// 飛行種はZも移動可能
		if (b.canflyCheck()) {
			mz = found.getZ();
		}

		// ゆっくり同士が重ならないように目標地点は体のサイズを考慮
		int colX = calcCollisionX(b, found);
		return BodyPartnerActionRule.handleFoundTarget(b, found, bodyHasOkazari, colX, mz);
	}

	/**
	 * 接触している場合の動作
	 * 
	 * @param p 自分
	 * @param b 相手
	 * @return 動作を行った場合
	 */
	public static final boolean doActionOther(Body p, Body b) {
		// 途中で消されてたら他の候補を探す
		if (p.isRemoved()) {
			b.clearActions();
			return false;
		}

		// 相手が宙に浮いてたら無視
		if (!b.canflyCheck() && p.getZ() != 0) {
			b.clearActions();
			return false;
		}

		if (b.isNYD()) {
			return false;
		}

		// 片方だけがうんうん奴隷の場合はなにもしない
		if (b.getPublicRank() != p.getPublicRank() && !(b.isRaper() && b.isExciting())) {
			// 盗みに行かない場合は終了
			if (!b.isToSteal()) {
				b.clearActions();
				return false;
			}
		}

		int rangeX = Translate.invertX((int) ((b.getCollisionX() + p.getCollisionX()) * 0.6f), p.getY());
		rangeX = Translate.transSize(rangeX);
		int distX = Math.abs(b.getX() - p.getX());
		int distY = Math.abs(b.getY() - p.getY());
		int range = Math.abs(rangeX - distX);
		// 見つかった相手に対するコリジョンチェック
		// 体が隣接するように横長のボックスで判定を取る
		// Y軸の閾値をrangeXに比例させる(distY < 5だと厳しすぎて到達できない問題の修正)
		if (BodyContactRule.handleAdjacentContact(p, b, rangeX, distY, range)) {
			return true;
		}
		if (b.isToSteal()) {
			return false;
		}

		// 非接触状態の場合
		else {
			BodyApproachRule.handleApproach(p, b, rangeX);
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

		int colX = Translate.invertX((int) ((from.getCollisionX() + to.getCollisionX()) * 0.6f), to.getY());
		colX = Translate.transSize(colX);

		// お互いの位置から右と左最短距離を選択
		int dir = 1;
		if (from.getX() < to.getX())
			dir = -1;
		colX *= dir;

		return colX;
	}

	/**
	 * 他のゆっくりがプレイヤーにすりすりされていた場合の行動判定
	 * 
	 * @param b          自分
	 * @param bodyTarget 相手
	 * @return 行動をしたかどうか
	 */
	public static final eActionGo checkActionSurisuriFromPlayer(Body b, Body bodyTarget) {
		return BodySurisuriRule.checkActionSurisuriFromPlayer(b, bodyTarget);
	}

	/**
	 * 婚姻候補のリストを作る。既婚の場合は、相手のみを含むリストを作る
	 * 
	 * @param b   自分
	 * @param age ゆん生のステージ
	 * @return 婚姻候補のリスト
	 */
	public static final List<Body> createActiveFianceeList(Body b, int age) {
		return BodySelectionRule.createActiveFianceeList(b, age);
	}

	/**
	 * アクティブな赤ゆ/子ゆのリストを作成する.
	 * 
	 * @param b      ゆっくり
	 * @param bState 子ゆっくりを入れるかどうか（これがfalseなら赤ゆのみのリストになる）
	 * @return アクティブな赤ゆ/子ゆのリスト
	 */
	public static final List<Body> createActiveChildList(Body b, boolean bState) {
		return BodySelectionRule.createActiveChildList(b, bState);
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
	 * @param bTop       先頭ゆ
	 * @param TargetList 並べるゆっくりのリスト
	 * @return 並んだかどうか
	 */
	public static final boolean gatheringYukkuriFront(Body bTop, List<Body> TargetList) {
		return BodyGatheringRule.gatheringYukkuriFront(bTop, TargetList);
	}

	/**
	 * ぜんゆん集合(四角形前面)
	 * 
	 * @param bTop       先頭ゆ
	 * @param TargetList 並べるゆっくりのリスト
	 * @param e          イベント
	 * @return 並んだかどうか
	 */
	public static final boolean gatheringYukkuriFront(Body bTop, List<Body> TargetList, EventPacket e) {
		return BodyGatheringRule.gatheringYukkuriFront(bTop, TargetList, e);
	}

	/**
	 * ぜんゆん集合
	 * 
	 * @param oTop       先頭ゆ
	 * @param TargetList 並べるゆっくりのリスト
	 * @param eDir       並べる方向
	 * @param e          イベント
	 * @return 並んだかどうか
	 */
	public static final boolean gatheringYukkuriSquare(Obj oTop, Body[] TargetList, GatheringDirection eDir,
			EventPacket e) {
		return BodyGatheringRule.gatheringYukkuriSquare(oTop, TargetList, eDir, e);
	}

	/**
	 * ぜんゆん集合(先頭の後ろに一列)
	 * 
	 * @param bTop       先頭ゆ
	 * @param TargetList 並べるゆっくりのリスト
	 * @param e          イベント
	 * @return 並んだかどうか
	 */
	public static final boolean gatheringYukkuriBackLine(Body bTop, List<Body> TargetList, EventPacket e) {
		return BodyGatheringRule.gatheringYukkuriBackLine(bTop, TargetList, e);
	}

	/**
	 * うんうんどれいの感情処理
	 * 
	 * @param b          自分
	 * @param bodyTarget 相手
	 * @return 処理を行ったかどうか
	 */
	public static boolean checkEmotionFromUnunSlave(Body b, Body bodyTarget) {
		return BodyUnunSlaveEmotionRule.checkEmotionFromUnunSlave(b, bodyTarget);
	}

	/**
	 * 近い親をチェックする.
	 * 
	 * @param b 赤ゆなど
	 */
	public static void checkNearParent(Body b) {
		BodyParentRule.checkNearParent(b);
	}

	/**
	 * 視界内に起きているゆっくりがいないかチェック
	 * 
	 * @param b ゆっくり
	 * @return 視界内に起きているゆっくりがいるかどうか
	 */
	public static boolean checkWakeupOtherYukkuri(Body b) {
		return BodyWakeupRule.checkWakeupOtherYukkuri(b);
	}
}
