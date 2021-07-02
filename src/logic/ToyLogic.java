package src.logic;

import java.util.LinkedList;
import java.util.List;

import src.SimYukkuri;
import src.base.Body;
import src.base.Obj;
import src.base.ObjEX;
import src.draw.Translate;
import src.enums.FavItemType;
import src.enums.Happiness;
import src.enums.Intelligence;
import src.event.SuiRideEvent;
import src.event.SuiSpeake;
import src.item.Barrier;
import src.item.Sui;
import src.item.Toy;
import src.item.Trampoline;
import src.system.MessagePool;



/***************************************************
 * おもちゃ関係の処理
 */
public class ToyLogic {

	/**
	 *  ボールで遊ぶ
	 * @param b ゆっくり
	 * @return 処理が行われたか
	 */
	public static final boolean checkToy(Body b) {
		if( b == null ){
			return false;
		}

		List<Toy> list = new LinkedList<>(SimYukkuri.world.getCurrentMap().toy.values());
		if( list == null || list.size() == 0 ){
			return false;
		}

		if(canPlay(b)==false)return false;
		if (!b.isRude() && (b.isAdult() || b.wantToShit()) ){
			return false;
		}

		boolean ret = true;
		Toy found = null;
		int minDistance = b.getEYESIGHTorg();
		for (Toy t: list) {
			// 最小距離のものが見つかっていたら
			if( minDistance < b.getStepDist() ){
				break;
			}
			int distance = Translate.distance(b.getX(), b.getY(), t.getX(), t.getY());
			if (minDistance > distance) {
				if (!b.isRude()) {
					if (Barrier.acrossBarrier(b.getX(), b.getY(), t.getX(), t.getY(), Barrier.MAP_BODY[b.getBodyAgeState().ordinal()]+Barrier.BARRIER_KEKKAI )) {
						continue;
					}
				}
				found = (Toy)t;
				minDistance = distance;
			}
		}
		if (found != null) {
			boolean bOwnedFamily = false;
			// プレイヤーに持ち上げられたら所有権を消す
			if( found.isGrabbed() && found.getZ() != 0 ){
				found.setOwner(null);
			}
			else{
				Body owner = found.getOwner();
				if( owner == null || owner == b || b.isFamily(owner) ){
					bOwnedFamily = true;
				}
			}
			if (minDistance <= b.getStepDist()) {
				// 空中
				if (found.getZ() != 0) {
					// お気に入りのボールの所有者が家族ではない場合
					if (b.getFavItem(FavItemType.BALL) == found && !bOwnedFamily) {
						if (b.getAge() % 20 == 0) {
							if (b.isRude()) {
								b.setHappiness(Happiness.VERY_SAD);
								b.addStress(20);
							}
							else {
								b.setHappiness(Happiness.SAD);
								b.addStress(5);
							}
							if (!b.isTalking()){
								b.setMessage(MessagePool.getMessage(b, MessagePool.Action.LostTreasure), true);
							}
						}
					}
					return true;
				}
				int strength[] = {-1, -4, -6};
				if (b.getAge() % 20 == 0) {
					b.setHappiness(Happiness.HAPPY);
					b.addStress(-400);
				}
				// 自分のものではない
				if (!found.isOwned(b)) {
					found.setOwner(b);
					b.setFavItem(FavItemType.BALL, found);
					if (!b.isTalking()) {
						b.setHappiness(Happiness.HAPPY);
						b.setMessage(MessagePool.getMessage(b, MessagePool.Action.GetTreasure), true);
					}
				}
				else if(SimYukkuri.RND.nextInt(10) == 0){
					b.getInVain(true);
				}
				else{
					found.kick(b.getDirX() * b.getStep(), b.getDirY() * b.getStep(), strength[b.getBodyAgeState().ordinal()]);
				}
			}
			else {
				if (b.getFavItem(FavItemType.BALL) == found && !bOwnedFamily ) {
					if (b.getAge() % 20 == 0) {
						if (b.isRude()) {
							b.setHappiness(Happiness.VERY_SAD);
							b.addStress(20);
						}
						else {
							b.setHappiness(Happiness.SAD);
							b.addStress(5);
						}
						if (!b.isTalking()) {
							b.setMessage(MessagePool.getMessage(b, MessagePool.Action.LostTreasure), true);
						}
					}
				}
				b.moveTo(found.getX(), found.getY());
			}
		}
		return ret;
	}

	/**
	 *  すぃーで遊ぶ
	 * @param b ゆっくり
	 * @return 処理が行われたか
	 */
	public static final boolean checkSui(Body b) {
		List<Sui> list = new LinkedList<>(SimYukkuri.world.getCurrentMap().sui.values());
		if( list == null || list.size() == 0 ){
			return false;
		}

		if(canPlay(b)==false)return false;
		if (//				|| SimYukkuri.RND.nextInt(100) != 0 ||
				b.takeMappedObj(b.getLinkParent()) instanceof Sui) {
			return false;
		}

		int sui_num = list.size();
		// すぃーがあるかつ1/150の確率でゆっくりする
		if(SimYukkuri.RND.nextInt(150) == 0 && sui_num > 0) {
			if(!b.isTalking()) {
				b.setMessage(MessagePool.getMessage(b, MessagePool.Action.YukkuringSui), true);
			}
			return false;
		}
		boolean ret = true;
		Obj found = b.getFavItem(FavItemType.SUI);

		// 自分のすぃーがない場合
		if(found == null) {
			int minDistance = b.getEYESIGHTorg();
			for (ObjEX s: list) {
				// 最小距離のものが見つかっていたら
				if( minDistance < b.getStepDist()){
					break;
				}
				int distance = Translate.distance(b.getX(), b.getY(), s.getX(), s.getY());
				// 視界内にすぃーがある
				if (minDistance > distance) {
					// 壁の向こうならなにもしない
					if (Barrier.acrossBarrier(b.getX(), b.getY(), s.getX(), s.getY(), Barrier.MAP_BODY[b.getBodyAgeState().ordinal()]+Barrier.BARRIER_KEKKAI )) {
						continue;
					}

					// 所有できない場合
					if(((Sui)s).NoCanBind()) {
						Body bindBody = (Body)((Sui)s).getbindobj();
						// 所有者が家族ではないならなにもしない
						if(!(b.isParent(bindBody) || bindBody.isParent(b) || b.isPartner(bindBody) || bindBody.isSister(b))){
							continue;
						}
					}
					found = s;
					minDistance = distance;
				}
			}
		}
		else if(SimYukkuri.RND.nextBoolean() && !b.isTalking() && Translate.distance(b.getX(), b.getY(), found.getX(), found.getY()) < 200000){
			b.setMessage(MessagePool.getMessage(b, MessagePool.Action.hasSui), true);
			EventLogic.addWorldEvent(new SuiSpeake(b, null, found, 10), null, null);
			return false;
		}
		// すぃーが見つかった場合
		if (found != null) {
			Body bindBody =(Body)((Sui)found).getbindobj() ;
			// プレイヤーに持ち上げられたら所有権を消す
			if( found.isGrabbed() && found.getZ() != 0 ){
				bindBody = null;
			}
			// 所有者がいない場合
			if(bindBody == null){
				if (!b.isTalking()) {
					// すぃー発見セリフ
					EventLogic.addWorldEvent(new SuiRideEvent(b, null, found, 100), b, MessagePool.getMessage(b, MessagePool.Action.FindSui));
				}
			}
			else if(bindBody == b){
				// 所有者が自分の場合
				if (!b.isTalking()) {
					// 自分のすぃーにのりにいくセリフ
					EventLogic.addWorldEvent(new SuiRideEvent(b, null, found, 100), b, MessagePool.getMessage(b, MessagePool.Action.FindGetSui));
				}
			}
			else{
				// 自分以外が所有者の場合
				if (!b.isTalking()) {
		//			EventLogic.addWorldEvent(new SuiRideEvent(bindBody, null, found, 100), b, MessagePool.getMessage(b, MessagePool.Action.HateYukkuri));
				}
			}
		}
		else if(SimYukkuri.world.getCurrentMap().sui.size() > 0){
			EventLogic.addBodyEvent(b,new SuiSpeake(null, null, null, 10), null, null);
		}
		return ret;
	}
	/**
	 * トランポリンで遊ぶ
	 * @param b ゆっくり
	 * @return 処理が行われたか
	 */
	public static final boolean checkTrampoline(Body b){
		if( b == null ){
			return false;
		}

		List<Trampoline> trampolineList = new LinkedList<>(SimYukkuri.world.getCurrentMap().trampoline.values());
		if( trampolineList == null || trampolineList.size() == 0 ){
			return false;
		}

		if(canPlay(b)==false)return false;
		if(!b.isRude() && (b.isAdult() || b.wantToShit())){
			return false;
		}

		Trampoline found = null;
		// 視界内の一番近いトランポリンを取得
		int minDistance = b.getEYESIGHTorg();
		for(Trampoline t: trampolineList ){
			// 最小距離のものが見つかっていたら
			if( minDistance < b.getStepDist() )
			{
				break;
			}
			int distance = Translate.distance(b.getX(), b.getY(), t.getX(), t.getY());
			if(minDistance > distance && (b.isRude() || !Barrier.acrossBarrier(b.getX(), b.getY(), t.getX(), t.getY(), Barrier.MAP_BODY[b.getBodyAgeState().ordinal()] +Barrier.BARRIER_KEKKAI )))
			{
				found = (Trampoline)t;
				minDistance = distance;
			}
		}

		if(found == null){
			return false;
		}

		// 乗った場合
		if(minDistance <= b.getStepDist()){
			int strength[] = {-1, -4, -6};
			if(b.getAge() % 20L == 0L){
				b.setHappiness(Happiness.HAPPY);
				b.addStress(-200);
			}

			if(b.getZ() == 0){
				if(found.option == 0){
					if(b.getIntelligence() == Intelligence.FOOL && SimYukkuri.RND.nextInt(100) + 1 < found.accident2 || b.getIntelligence() != Intelligence.FOOL && SimYukkuri.RND.nextInt(100) + 1 < found.accident1)
					{
						b.kick(0, 0, ((strength[b.getBodyAgeState().ordinal()] + strength[b.getBodyAgeState().ordinal()] * SimYukkuri.RND.nextInt(1)) - SimYukkuri.RND.nextInt(5)) * 3);
					}
					else{
						b.kick(0, 0, (strength[b.getBodyAgeState().ordinal()] + strength[b.getBodyAgeState().ordinal()] * SimYukkuri.RND.nextInt(1)) - SimYukkuri.RND.nextInt(5));
					}
				}
				else{
					if(b.getIntelligence() == Intelligence.FOOL && SimYukkuri.RND.nextInt(100) + 1 <= found.accident2 || b.getIntelligence() != Intelligence.FOOL && SimYukkuri.RND.nextInt(100) + 1 < found.accident1)
					{
						b.kick(b.getDirX() * b.getStep(), b.getDirY() * b.getStep(), ((strength[b.getBodyAgeState().ordinal()] + strength[b.getBodyAgeState().ordinal()] * SimYukkuri.RND.nextInt(1)) - SimYukkuri.RND.nextInt(5)) * 3);
					}
					else{
						b.kick(b.getDirX() * b.getStep(), b.getDirY() * b.getStep(), (strength[b.getBodyAgeState().ordinal()] + strength[b.getBodyAgeState().ordinal()] * SimYukkuri.RND.nextInt(1)) - SimYukkuri.RND.nextInt(5));
					}
				}
			}
		}
		else{
			b.moveTo(found.getX(), found.getY());
		}
			return true;
	}
	/**
	 * 遊べる状態かどうか
	 * @param b ゆっくり
	 * @return 遊べる状態かどうか
	 */
	public static boolean canPlay(Body b){
		// 他の用事がある場合
		if( b.isToFood() || b.isToBody() || b.isToSukkiri() || b.isToSteal() || b.isToBed() || b.isToShit() ){
			return false;
		}
		if (!b.canAction() || b.isDontMove() || b.isExciting() || b.isScare() || b.isDamaged() ){
			return false;
		}
		if(b.getCurrentEvent() != null) {
			return false;
		}
		if(!b.canEventResponse()){
			return false;
		}
		return true;
	}

}


