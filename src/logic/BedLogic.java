package src.logic;

import java.util.ArrayList;
import java.util.Random;

import src.SimYukkuri;
import src.base.Body;
import src.base.EventPacket;
import src.base.Obj;
import src.base.ObjEX;
import src.draw.Terrarium;
import src.draw.Translate;
import src.enums.AgeState;
import src.enums.CoreAnkoState;
import src.enums.FavItemType;
import src.enums.PublicRank;
import src.enums.TakeoutItemType;
import src.event.FavCopyEvent;
import src.item.Barrier;
import src.item.Bed;
import src.item.House;
import src.item.Toilet;



/***************************************************
	ベッド関係の処理
 */
public class BedLogic {

	private static Random rnd = new Random();

	public static final boolean checkBed(Body b) {
		// 他の用事がある場合
		if( b.isToFood() || b.isToBody() || /*b.isToBed() ||*/ b.isToShit() || b.isToSukkiri() || b.isToSteal() )
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
/*		else {
			if(p != null && p.getPriority() != EventPacket.EventPriority.LOW) {
				return false;
			}
		}
*/
		// 非ゆっくり症の場合
		if( b.geteCoreAnkoState() != CoreAnkoState.DEFAULT )
		{
			return false;
		}

		// 対象が決まっていたら到達したかチェック
		Obj target = b.getMoveTarget();
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
		if (b.isSleepy()	//	眠い
			|| Terrarium.getDayState().ordinal() >= Terrarium.DayState.EVENING.ordinal()	// 夜になった
			|| b.nearToBirth()) {	// 出産間近
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
			}
			else {
				ofsX = Translate.invertX(found.getW(), found.getY() - 4);
				ofsX = -(ofsX >> 1) + rnd.nextInt(ofsX);
				ofsY = Translate.invertY(found.getH() - 4);
				ofsY = -(ofsY >> 1) + rnd.nextInt(ofsY);
			}
			b.moveToBed(found, found.getX() + ofsX, found.getY() + ofsY, 0);
			b.setTargetMoveOffset(ofsX, ofsY);
			ret = true;
		}
		return ret;
	}

	public static Obj searchBed(Body b){
		Obj found = b.getFavItem(FavItemType.BED);
		int minDistance = b.getEYESIGHT();
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
			ArrayList<Bed> list = SimYukkuri.world.currentMap.bed;
				for (ObjEX t: list) {
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
			ArrayList<House> list = SimYukkuri.world.currentMap.house;
			for (ObjEX t: list) {
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
			ArrayList<Toilet> list = SimYukkuri.world.currentMap.toilet;
				for (ObjEX t: list) {
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

