package src.logic;
import src.util.GameEnvironment;

import java.util.Map;

import src.SimYukkuri;
import src.util.GameRandom;
import src.util.GameWorld;
import src.base.Yukkuri;
import src.event.EventPacket;
import src.base.Entity;
import src.base.WorldEntity;
import src.draw.Terrarium;
import src.draw.Translate;
import src.enums.AgeState;
import src.enums.Direction;
import src.enums.FavItemType;
import src.enums.PublicRank;
import src.enums.TakeoutItemType;
import src.event.FavCopyEvent;
import src.field.impl.Barrier;
import src.item.Bed;
import src.item.House;
import src.item.Toilet;



/***************************************************
 * ベッド関係の処理
 */
public class BedLogic {

	/**
	 * ベッド関連処理を行う
	 * @param body ゆっくり
	 * @return ベッド関連処理の対象かどうか
	 */
	public static final boolean checkBed(Yukkuri body) {
		// 他の用事がある場合
		if( body.isToFood() || body.isToBody() || /*body.isToBed() ||*/ body.isToShit() || 
				body.isToSukkiri() || body.isToSteal() || body.isToTakeout())
		{
			return false;
		}

		if(body.isIdiot()) return false;

		EventPacket currentEvent = body.getCurrentEvent();
		if(body.nearToBirth()) {
			if(currentEvent != null && currentEvent.getPriority() == EventPacket.EventPriority.HIGH) {
				return false;
			}
		}
		else {
			if(currentEvent != null && currentEvent.getPriority() != EventPacket.EventPriority.LOW) {
				return false;
			}
		}

		// 非ゆっくり症の場合
		if( body.isNYD() )
		{
			return false;
		}

		// 対象が決まっていたら到達したかチェック
		Entity target = body.takeMappedObj(body.getMoveTargetId());
		if(body.isToBed() && target != null) {
			// 途中で消されてたら他の候補を探す
			if(target.isRemoved()) {
				body.setFavoriteItem(FavItemType.BED, null);
				body.clearActions();
				return false;
			}

			// うんうん奴隷の場合
			if( body.getPublicRank() == PublicRank.UnunSlave){
				// ベッドには向かわない
				if( target instanceof Bed){
					body.setFavoriteItem(FavItemType.BED, null);
					body.clearActions();
					return false;
				}
			}

			if ((body.getStepDist()) >= Translate.distance(body.getX(), body.getY(), target.getX() + body.getTargetOffsetX(), target.getY() + body.getTargetOffsetY())
					&& body.getZ() == 0) {
				// 到着したら待機状態へ
				if(body.getFavoriteItem(FavItemType.BED) == null) {
					// うんうん奴隷ではない場合
					if( body.getPublicRank() != PublicRank.UnunSlave){
						// 見つけたベッドをお気に入りにして家族にも伝達
						body.setFavoriteItem(FavItemType.BED, target);
						EventLogic.addWorldEvent(new FavCopyEvent(body, null, null, 1), null, null);
					}
				}
				// 餌を保持している
				if(body.getCarryItem(TakeoutItemType.FOOD) != null){
					// 吐き出す
					body.dropTakeoutItem(TakeoutItemType.FOOD);
				}
				body.clearActions();
				body.stay();
			}
			else{
				body.moveTo(target.getX() + body.getTargetOffsetX(), target.getY() + body.getTargetOffsetY(), 0);
			}
			return true;
		}

		// ベッドに向かう条件
		boolean shouldSearchBed = false;
		if ((body.isSleepy()	//	眠い
			|| GameEnvironment.getDayState().ordinal() >= Terrarium.DayState.EVENING.ordinal()	// 夜になった
			|| body.nearToBirth()) // 出産間近
			&& body.getCurrentEvent() == null) {// イベントがない <- イベントありのままだと不眠ディフューザーとかでおかしくなる
			shouldSearchBed = true;
		}
		if(body.getCarryItem(TakeoutItemType.FOOD) != null){
			shouldSearchBed = true;
		}

		if(!shouldSearchBed) return false;

		boolean foundBed = false;
		Entity targetObject = searchBed(body);

		if (targetObject != null) {
			int offsetX = 0;
			int offsetY = 0;
			if(body.hasBabyOrStalk()) {
				offsetY = Translate.invertY(targetObject.getH() - 4);
				offsetY = -(offsetY >> 1);
				// 茎妊娠の場合は茎がベッドの上に収まるように親を茎の反対方向にオフセット
				if(body.isHasStalk()) {
					int stalkOffset = Translate.invertX(15, targetObject.getY());
					if(body.getDirection() == Direction.RIGHT) {
						offsetX = -stalkOffset; // 茎が右にあるので親を左に
					} else {
						offsetX = stalkOffset; // 茎が左にあるので親を右に
					}
				}
			}
			else {
				offsetX = Translate.invertX(targetObject.getW(), targetObject.getY() - 4);
				offsetX = -(offsetX >> 1) + GameRandom.nextInt(offsetX);
				offsetY = Translate.invertY(targetObject.getH() - 4);
				offsetY = -(offsetY >> 1) + GameRandom.nextInt(offsetY);
			}
			body.moveToBed(targetObject, targetObject.getX() + offsetX, targetObject.getY() + offsetY, 0);
			body.setTargetMoveOffset(offsetX, offsetY);
			foundBed = true;
		}
		return foundBed;
	}
	/**
	 * ベッドを探し出す.
	 * @param body ゆっくり
	 * @return 探しだしたベッドのオブジェクト
	 */
	public static Entity searchBed(Yukkuri body){
		Entity targetObject = body.getFavoriteItem(FavItemType.BED);
		int nearestDistance = body.getEyesightBase();
		// うんうん奴隷の場合
		if( body.getPublicRank() == PublicRank.UnunSlave){
			body.setFavoriteItem(FavItemType.BED, null);
			targetObject = null;
		}
		int wallMode = body.getBodyAgeState().ordinal();
		// 飛行可能なら壁以外は通過可能
		if(body.canflyCheck()) {
			wallMode = AgeState.ADULT.ordinal();
		}
		if (targetObject != null) {
			// お気に入りが壁で到達できなくなってたらリセット
			if (Barrier.acrossBarrier(body.getX(), body.getY(), targetObject.getX(), targetObject.getY(), Barrier.MAP_BODY[wallMode] + Barrier.BARRIER_KEKKAI)) {
				targetObject = null;
			}
		}


		// うんうん奴隷ではない場合
		if( body.getPublicRank() != PublicRank.UnunSlave){
			if(targetObject == null) {
				for (Map.Entry<Integer, Bed> entry : GameWorld.get().getCurrentMap().getBed().entrySet()) {
					WorldEntity t = entry.getValue();
					int distance = Translate.distance(body.getX(), body.getY(), t.getX(), t.getY());
					if (nearestDistance > distance) {
						if (Barrier.acrossBarrier(body.getX(), body.getY(), t.getX(), t.getY(), Barrier.MAP_BODY[wallMode] + Barrier.BARRIER_KEKKAI)) {
							continue;
						}
						targetObject = (Bed)t;
						nearestDistance = distance;
					}
				}
			}
		//// 仮 おうち検索
			if(targetObject == null) {
				for (Map.Entry<Integer, House> entry : GameWorld.get().getCurrentMap().getHouse().entrySet()) {
					WorldEntity t = entry.getValue();
					int distance = Translate.distance(body.getX(), body.getY(), t.getX(), t.getY());
					if (nearestDistance > distance) {
						if (Barrier.acrossBarrier(body.getX(), body.getY(), t.getX(), t.getY(), Barrier.MAP_BODY[wallMode] + Barrier.BARRIER_KEKKAI)) {
							continue;
						}
						targetObject = (House)t;
						nearestDistance = distance;
					}
				}
			}
		}
		else{
			// うんうん奴隷の場合、トイレを探す
			if(targetObject == null) {
				for (Map.Entry<Integer, Toilet> entry : GameWorld.get().getCurrentMap().getToilet().entrySet()) {
					WorldEntity t = entry.getValue();
					int distance = Translate.distance(body.getX(), body.getY(), t.getX(), t.getY());
					if (nearestDistance > distance) {
						if (Barrier.acrossBarrier(body.getX(), body.getY(), t.getX(), t.getY(), Barrier.MAP_BODY[wallMode] + Barrier.BARRIER_KEKKAI)) {
							continue;
						}

						targetObject = (Toilet)t;
						nearestDistance = distance;
					}
				}
			}
		}
		return targetObject;
	}
}


