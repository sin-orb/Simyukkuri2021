package src.logic;

import java.util.Map;

import src.SimYukkuri;
import src.base.Body;
import src.base.EventPacket;
import src.base.Obj;
import src.base.ObjEX;
import src.draw.Terrarium;
import src.draw.Translate;
import src.enums.AgeState;
import src.enums.Direction;
import src.enums.FavItemType;
import src.enums.PublicRank;
import src.enums.TakeoutItemType;
import src.event.FavCopyEvent;
import src.item.Barrier;
import src.item.Bed;
import src.item.House;
import src.item.Toilet;



/***************************************************
 * ベッド関係の処理
 */
public class BedLogic {

	/**
	 * ベッド関連処理を行う
	 * @param b ゆっくり
	 * @return ベッド関連処理の対象かどうか
	 */
	public static final boolean checkBed(Body b) {
		// 他の用事がある場合
		if( b.isToFood() || b.isToBody() || /*b.isToBed() ||*/ b.isToShit() || 
				b.isToSukkiri() || b.isToSteal() || b.isToTakeout())
		{
			return false;
		}

		if(b.isIdiot()) return false;

		EventPacket p = b.getCurrentEvent();
		if(b.nearToBirth()) {
			if(p != null && p.getPriority() == EventPacket.EventPriority.HIGH) {
				return false;
			}
		}
		else {
			if(p != null && p.getPriority() != EventPacket.EventPriority.LOW) {
				return false;
			}
		}

		// 非ゆっくり症の場合
		if( b.isNYD() )
		{
			return false;
		}

		// 対象が決まっていたら到達したかチェック
		Obj target = b.takeMappedObj(b.getMoveTarget());
		if(b.isToBed() && target != null) {
			// 途中で消されてたら他の候補を探す
			if(target.isRemoved()) {
				b.setFavItem(FavItemType.BED, null);
				b.clearActions();
				return false;
			}

			// うんうん奴隷の場合
			if( b.getPublicRank() == PublicRank.UnunSlave){
				// ベッドには向かわない
				if( target instanceof Bed){
					b.setFavItem(FavItemType.BED, null);
					b.clearActions();
					return false;
				}
			}

			if ((b.getStepDist()) >= Translate.distance(b.getX(), b.getY(), target.getX() + b.getTargetPosOfsX(), target.getY() + b.getTargetPosOfsY())
					&& b.getZ() == 0) {
				// 到着したら待機状態へ
				if(b.getFavItem(FavItemType.BED) == null) {
					// うんうん奴隷ではない場合
					if( b.getPublicRank() != PublicRank.UnunSlave){
						// 見つけたベッドをお気に入りにして家族にも伝達
						b.setFavItem(FavItemType.BED, target);
						EventLogic.addWorldEvent(new FavCopyEvent(b, null, null, 1), null, null);
					}
				}
				// 餌を保持している
				if(b.getTakeoutItem(TakeoutItemType.FOOD) != null){
					// 吐き出す
					b.dropTakeoutItem(TakeoutItemType.FOOD);
				}
				b.clearActions();
				b.stay();
			}
			else{
				b.moveTo(target.getX() + b.getTargetPosOfsX(), target.getY() + b.getTargetPosOfsY(), 0);
			}
			return true;
		}

		// ベッドに向かう条件
		boolean flag = false;
		if ((b.isSleepy()	//	眠い
			|| Terrarium.getDayState().ordinal() >= Terrarium.DayState.EVENING.ordinal()	// 夜になった
			|| b.nearToBirth()) // 出産間近
			&& b.getCurrentEvent() == null) {// イベントがない <- イベントありのままだと不眠ディフューザーとかでおかしくなる
			flag = true;
		}
		if(b.getTakeoutItem(TakeoutItemType.FOOD) != null){
			flag = true;
		}

		if(!flag) return false;

		boolean ret = false;
		Obj found = searchBed(b);

		if (found != null) {
			int ofsX = 0;
			int ofsY = 0;
			if(b.hasBabyOrStalk()) {
				ofsY = Translate.invertY(found.getH() - 4);
				ofsY = -(ofsY >> 1);
				// 茎妊娠の場合は茎がベッドの上に収まるように親を茎の反対方向にオフセット
				if(b.isHasStalk()) {
					int stalkOffset = Translate.invertX(15, found.getY());
					if(b.getDirection() == Direction.RIGHT) {
						ofsX = -stalkOffset; // 茎が右にあるので親を左に
					} else {
						ofsX = stalkOffset; // 茎が左にあるので親を右に
					}
				}
			}
			else {
				ofsX = Translate.invertX(found.getW(), found.getY() - 4);
				ofsX = -(ofsX >> 1) + SimYukkuri.RND.nextInt(ofsX);
				ofsY = Translate.invertY(found.getH() - 4);
				ofsY = -(ofsY >> 1) + SimYukkuri.RND.nextInt(ofsY);
			}
			b.moveToBed(found, found.getX() + ofsX, found.getY() + ofsY, 0);
			b.setTargetMoveOffset(ofsX, ofsY);
			ret = true;
		}
		return ret;
	}
	/**
	 * ベッドを探し出す.
	 * @param b ゆっくり
	 * @return 探しだしたベッドのオブジェクト
	 */
	public static Obj searchBed(Body b){
		Obj found = b.getFavItem(FavItemType.BED);
		int minDistance = b.getEYESIGHTorg();
		// うんうん奴隷の場合
		if( b.getPublicRank() == PublicRank.UnunSlave){
			b.setFavItem(FavItemType.BED, null);
			found = null;
		}
		int wallMode = b.getBodyAgeState().ordinal();
		// 飛行可能なら壁以外は通過可能
		if(b.canflyCheck()) {
			wallMode = AgeState.ADULT.ordinal();
		}
		if (found != null) {
			// お気に入りが壁で到達できなくなってたらリセット
			if (Barrier.acrossBarrier(b.getX(), b.getY(), found.getX(), found.getY(), Barrier.MAP_BODY[wallMode] + Barrier.BARRIER_KEKKAI)) {
				found = null;
			}
		}


		// うんうん奴隷ではない場合
		if( b.getPublicRank() != PublicRank.UnunSlave){
			if(found == null) {
				for (Map.Entry<Integer, Bed> entry : SimYukkuri.world.getCurrentMap().bed.entrySet()) {
					ObjEX t = entry.getValue();
					int distance = Translate.distance(b.getX(), b.getY(), t.getX(), t.getY());
					if (minDistance > distance) {
						if (Barrier.acrossBarrier(b.getX(), b.getY(), t.getX(), t.getY(), Barrier.MAP_BODY[wallMode] + Barrier.BARRIER_KEKKAI)) {
							continue;
						}
						found = (Bed)t;
						minDistance = distance;
					}
				}
			}
	//// 仮 おうち検索
			if(found == null) {
				for (Map.Entry<Integer, House> entry : SimYukkuri.world.getCurrentMap().house.entrySet()) {
					ObjEX t = entry.getValue();
					int distance = Translate.distance(b.getX(), b.getY(), t.getX(), t.getY());
					if (minDistance > distance) {
						if (Barrier.acrossBarrier(b.getX(), b.getY(), t.getX(), t.getY(), Barrier.MAP_BODY[wallMode] + Barrier.BARRIER_KEKKAI)) {
							continue;
						}
						found = (House)t;
						minDistance = distance;
					}
				}
			}
		}
		else{
			// うんうん奴隷の場合、トイレを探す
			if(found == null) {
				for (Map.Entry<Integer, Toilet> entry : SimYukkuri.world.getCurrentMap().toilet.entrySet()) {
					ObjEX t = entry.getValue();
					int distance = Translate.distance(b.getX(), b.getY(), t.getX(), t.getY());
					if (minDistance > distance) {
						if (Barrier.acrossBarrier(b.getX(), b.getY(), t.getX(), t.getY(), Barrier.MAP_BODY[wallMode] + Barrier.BARRIER_KEKKAI)) {
							continue;
						}

						found = (Toilet)t;
						minDistance = distance;
					}
				}
			}
		}
		return found;
	}
}

