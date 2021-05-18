package src.logic;

import java.util.List;
import java.util.Random;

import src.SimYukkuri;
import src.base.Body;
import src.base.EventPacket;
import src.base.Obj;
import src.draw.Translate;
import src.enums.AgeState;
import src.enums.ImageCode;
import src.enums.Intelligence;
import src.enums.PublicRank;
import src.enums.TakeoutItemType;
import src.game.Shit;
import src.item.Barrier;
import src.item.Toilet;
import src.system.MessagePool;



/***************************************************
 *トイレ関係の処理
 */
public class ToiletLogic {
	/** うんうんどれい */
	public static Body bodyUnunSlave;
	protected static Random rnd = new Random();
	/**
	 * うんうん処理
	 * @param b ゆっくり
	 * @return 処理が行われたか
	 */
	public static final boolean checkShit(Body b) {
		//A.トイレ行動中止
		// 毎フレームチェックは重いのでインターバル
		if(b.getAge() % 15 != 0) return false;
		if (b.canAction()==false|| b.isIdiot() || b.isExciting() || b.nearToBirth() ) {
			return false;
		}
		if(b.getCurrentEvent() != null && b.getCurrentEvent().getPriority() != EventPacket.EventPriority.LOW) {
			return false;
		}
		// 非ゆっくり症の場合
		if( b.isNYD() ){
			return false;
		}

		boolean ret = false;
		int colX = b.getCollisionX();
		boolean bHasShit = false;
		if( b.getTakeoutItem(TakeoutItemType.SHIT) != null ){
			bHasShit = true;
		}

		List<Toilet> toiletList = SimYukkuri.world.currentMap.toilet;
		List<Shit> shitList = SimYukkuri.world.currentMap.shit;
		if( shitList == null || shitList.size() == 0 ){
			return false;
		}


		//B.子供のうんうん運び関連
		// 子供のうんうんを運ぶする余裕が有るか
		boolean bCanTransport = true;
		// 前回チェックしたうんうんどれいがまだいるなら自分で運ばない
		if( bodyUnunSlave != null &&
				bodyUnunSlave.getPublicRank() == PublicRank.UnunSlave &&
				!bodyUnunSlave.isDead() &&
				!bodyUnunSlave.isRemoved()){
			bCanTransport = false;
		}
		else{
			if( b.isDontMove()  || b.isDamaged()  || b.isFeelPain() ==true || b.isSoHungry() ==true){
				bCanTransport = false;
			}
		}
		// うんうん奴隷がいれば運ばない
		if( bCanTransport  ){
			Body[] bodyList = SimYukkuri.world.currentMap.body.toArray(new Body[0]);
			for(Body bodyOther : bodyList){
				if(bodyOther == b || bodyOther.isDead() || bodyOther.isRemoved() ){
					continue;
				}
				if(bodyOther.getPublicRank() == PublicRank.UnunSlave ){
					bCanTransport = false;
					bodyUnunSlave = bodyOther;
					break;
				}
			}
		}

		// 自分のうんうんがトイレ外にあるかチェック
		boolean bFoundMyShitOutOfToilet = false;
		boolean bFoundMyToilet = false;
		if( bCanTransport ){
			for (Shit s: shitList) {
				if(s.getZ() != b.getZ()) continue;
				if( b!= s.owner ) continue;

				// 壁があるなら無視
				if (Barrier.acrossBarrier(b.getX(), b.getY(), s.getX(), s.getY(), Barrier.MAP_BODY[b.getBodyAgeState().ordinal()] + Barrier.BARRIER_KEKKAI)) {
					continue;
				}
				// トイレの上にあるか
				boolean bIsOnToilet = false;
				if( toiletList != null && toiletList.size() != 0){
					for (Toilet t: toiletList){
						if( t.checkHitObj(s, false) ){
							bIsOnToilet = true;
							break;
						}
						else{
							// 壁があるなら無視
							if(!Barrier.acrossBarrier(b.getX(), b.getY(), t.getX(), t.getY(), Barrier.MAP_BODY[b.getBodyAgeState().ordinal()] + Barrier.BARRIER_KEKKAI)) {
								bFoundMyToilet = true;
								break;
							}
						}
					}
				}
				// トイレの上にあればスキップ
				if( bIsOnToilet ){
					continue;
				}
				bFoundMyShitOutOfToilet = true;
				break;
			}
		}

		//C.うんうん探索
		// うんうんが近くにあるかチェック
		for (Shit s: shitList) {
			if(s.getZ() != b.getZ()) continue;
			// 壁があるなら無視
			if (Barrier.acrossBarrier(b.getX(), b.getY(), s.getX(), s.getY(), Barrier.MAP_BODY[b.getBodyAgeState().ordinal()] + Barrier.BARRIER_KEKKAI)) {
				continue;
			}

			int nDistance = Translate.getRealDistance(b.getX(), b.getY(), s.getX(), s.getY());
			// ある程度近いうんうんが自分の子供のものかチェック。すでにうんうんを運んでる、自分のうんうんがトイレ外にあれば無視する
			if( bCanTransport  && nDistance < colX && !bHasShit && !bFoundMyShitOutOfToilet && bFoundMyToilet  ){
				Body bodyOwner = s.owner;
				// 自分の子ゆのうんうんは片付ける。
				if( bodyOwner != null && b.isParent(bodyOwner) && !bodyOwner.isAdult()){
					boolean bIsOnToilet = false;
					if( toiletList != null && toiletList.size() != 0){
						for (Toilet t: toiletList) {
							if( t.checkHitObj(s, false) ){
								bIsOnToilet = true;
								break;
							}
						}
					}
					// トイレの上にない
					if( !bIsOnToilet ){
						if (nDistance < b.getStepDist()){
							// おもちかえりする
							b.setTakeoutItem(TakeoutItemType.SHIT, s);
							b.setMessage(MessagePool.getMessage(b, MessagePool.Action.TransportShit));
							b.stay();
							b.clearActions();
						}
						else{
							b.moveToBody(s, s.getX(), s.getY());
						}
						break;
					}
				}
			}

			if( nDistance < b.getStepDist()*2){
				// 嫌がる
				if (!b.isTalking() && !b.isToShit()) {
					//うんうん奴隷じゃない、餡子脳の赤、子ゆはランダムで威嚇。
					if(!b.isAdult() && b.getPublicRank() == PublicRank.NONE &&  b.getIntelligence() == Intelligence.FOOL && rnd.nextBoolean()){
						b.setForceFace(ImageCode.PUFF.ordinal());
						b.setMessage(MessagePool.getMessage(b, MessagePool.Action.ShitIntimidation), false);
					}
					//ついでに足りないゆも
					else if(b.isIdiot() && rnd.nextBoolean()){
						b.setForceFace(ImageCode.PUFF.ordinal());
						b.setMessage(MessagePool.getMessage(b, MessagePool.Action.ShitIntimidation), false);
					}
					else b.setMessage(MessagePool.getMessage(b, MessagePool.Action.HateShit), false);
					b.addStress(5);
					ret = true;
					break;
				}
			}
		}
		return ret;
	}

	/**
	 * トイレ処理
	 * @param b ゆっくり
	 * @return 処理が行われたか
	 */
	public static final boolean checkToilet(Body b) {
		//A.トイレ行動の中止
		// 他の用事がある場合
		if( b.isToFood() || b.isToBody() || b.isToSukkiri() || b.isToBed() || /*b.isToShit() ||*/ b.isToSteal() ){
			return false;
		}
		if (b.isIdiot() || b.isDontMove() || b.nearToBirth() ) {
			return false;
		}
		// 非ゆっくり症の場合
		if( b.isNYD() ){
			return false;
		}
		if(b.getCurrentEvent() != null && b.getCurrentEvent().getPriority() != EventPacket.EventPriority.LOW) {
			return false;
		}
		//A2.フラグ設定
//		Random rnd = new Random();
		boolean bHasShit = false;
		if( b.getTakeoutItem(TakeoutItemType.SHIT) != null ){
			bHasShit = true;
		}
		Obj oTarget = b.getMoveTarget();
		if( oTarget != null ){
			if( !(oTarget instanceof Toilet)){
				return false;
			}
		}


		//B1.トイレに向かわないならリターンを返す
		PublicRank ePublicRank = b.getPublicRank();
		// うんうんどれいの場合
		if( ePublicRank == PublicRank.UnunSlave ){
			//盗もうとしてて、かつうんうんしないならトイレには向かわない
			if(b.isToSteal()&& !b.wantToShit() ){
					return false;
			}
			List<Toilet> toiletList = SimYukkuri.world.currentMap.toilet;
			for (Toilet t: toiletList) {
				// うんうん奴隷用トイレのどれかにいれば終了＝トイレに向かわない
				if( ((Toilet)t).isForSlave()  && t.checkHitObj(null, b)){
					// うんうんを持ち歩いている場合
					if(bHasShit ){
						b.dropTakeoutItem(TakeoutItemType.SHIT);
						b.setMessage(MessagePool.getMessage(b, MessagePool.Action.HateShit));
						b.addStress(10);
					}
					// うんうんを持ち歩いていない場合
					else{
						return false;
					}
				}
			}
		}
		else{
			// うんうん奴隷ではない場合、用がない、かつうんうんを持ってないなら終了
			if ( !b.wantToShit() && !bHasShit ) {
				List<Toilet> toiletList = SimYukkuri.world.currentMap.toilet;
				for (Toilet t: toiletList) {
					// 自動清掃でないトイレに入った時の反応
					if(!((Toilet)t).getAutoClean() && t.checkHitObj(null, b) &&!b.isTalking()){
						if(b.isSleeping()) b.wakeup();
						b.setMessage(MessagePool.getMessage(b, MessagePool.Action.HateShit));
						b.runAway(t.getX(), t.getY());
						break;
					}
				}
				return false;
			}
		}


		// 対象が決まっていたら到達したかチェック
		if( (b.isToShit() || ePublicRank == PublicRank.UnunSlave || bHasShit) && b.getMoveTarget() != null) {
			// 途中で消されてたら他の候補を探す
			if(b.getMoveTarget().isRemoved()) {
				b.clearActions();
				return false;
			}
			//到着済み
			if (b.getStepDist() >= Translate.distance(b.getX(), b.getY(), b.getMoveTarget().getX(), b.getMoveTarget().getY())) {
				//トイレが空中にあるとき
				if (b.getMoveTarget().getZ() != 0) {
					// 他の候補を探す
					b.clearActions();
					return false;
				}
				// うんうんをしたいわけではないとき
				if ( !b.wantToShit() ){
					// うんうんどれいは常にトイレにいる
					if( ePublicRank == PublicRank.UnunSlave ){
						if( b.getTakeoutItem(TakeoutItemType.SHIT) != null){
						// うんうん奴隷がうんうんを持っていれば落とす
							b.dropTakeoutItem(TakeoutItemType.SHIT);
							b.setMessage(MessagePool.getMessage(b, MessagePool.Action.HateShit));
							b.addStress(10);
							b.stay();
						}
					}
					else{
						// うんうんを持っていれば落とす
						if( b.getTakeoutItem(TakeoutItemType.SHIT) != null){
							b.dropTakeoutItem(TakeoutItemType.SHIT);
							b.setMessage(MessagePool.getMessage(b, MessagePool.Action.Relax));
							b.addStress(-15);
							b.stay();
						}
					}
				}
				// うんうんをしたい時は待機状態へ
				else {
					b.stay();
				}
			}
			//未到着の場合
			else{
				b.moveTo(b.getMoveTarget().getX(), b.getMoveTarget().getY(), 0);
			}
			return true;
		}

		//対象が未決定の場合
		boolean ret = false;
		Toilet found = null;
		int minDistance = b.getEYESIGHT();
		int wallMode = b.getBodyAgeState().ordinal();
		// 飛行可能なら壁以外は通過可能
		if(b.canflyCheck()) {
			wallMode = AgeState.ADULT.ordinal();
		}

		List<Toilet> toiletList = SimYukkuri.world.currentMap.toilet;
		for (Toilet t: toiletList) {
			int distance = Translate.distance(b.getX(), b.getY(), t.getX(), t.getY() - t.getH()/6);
			if (minDistance > distance) {
				if (!b.isRude()) {
					if (Barrier.acrossBarrier(b.getX(), b.getY(), t.getX(), t.getY() - t.getH()/6, Barrier.MAP_BODY[wallMode] + Barrier.BARRIER_KEKKAI)) {
						continue;
					}
				}
				// うんうん奴隷の場合
				if( ePublicRank == PublicRank.UnunSlave ){
					// うんうん奴隷用じゃないならスキップ
					if( !((Toilet)t).isForSlave()){
						continue;
					}
				}
				if( found != null ){
					// うんうん奴隷用トイレを優先する
					if( (found.isForSlave()) && !((Toilet)t).isForSlave() ){
						continue;
					}
				}
				found = (Toilet)t;
				minDistance = distance;
			}
		}

		if (found != null) {
			if ( !b.wantToShit() ) {
				// うんうんをしない
				b.moveToBody(found, found.getX(), found.getY());
			}
			else{
				// うんうんをする
				b.moveToToilet(found, found.getX(), found.getY(), 0);
			}
			ret = true;
		}
		else{
			// トイレがないのにうんうんを持っていればその場に落とす
			if( bHasShit){
				b.dropTakeoutItem(TakeoutItemType.SHIT);
				b.addStress(5);
				b.stay();
			}
		}
		return ret;
	}
}


