package src.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import src.SimYukkuri;
import src.base.Body;
import src.base.EventPacket;
import src.base.Obj;
import src.draw.Terrarium;
import src.draw.Translate;
import src.enums.PublicRank;
import src.enums.TakeoutItemType;
import src.enums.TangType;
import src.event.FuneralEvent;
import src.event.ProudChildEvent;
import src.event.ShitExercisesEvent;
import src.event.SuperEatingTimeEvent;
import src.event.YukkuriRideEvent;
import src.item.Barrier;
import src.item.Food;
import src.item.Toilet;
import src.system.MessagePool;



/***************************************************
	家族イベント関係の処理
 */
public class FamilyActionLogic {

	private static Random rnd = new Random();

	public static final boolean checkFamilyAction(Body b) {
		// 他の用事がある場合
		if( b.isToFood() || b.isToBody() || b.isToSukkiri() ||
				b.isToBed() || b.isToShit() || b.isToSteal() || b.isToTakeout()) {
			return false;
		}
		
		if(rnd.nextInt(300) != 0 ){
			return false;
		}
		
		//-------------------------------------
		// イベント処理
		//-------------------------------------
		EventPacket p = b.getCurrentEvent();
		// イベント中なら終了
		if(p instanceof SuperEatingTimeEvent || p instanceof ShitExercisesEvent || p instanceof YukkuriRideEvent ||p instanceof ProudChildEvent||p instanceof FuneralEvent) {
			return true;
		}
		else if( p != null ) {
			return false;
		}
		// 大人だけが実行する
		if(!b.isAdult()){
			return false;
		}
		
		// パートナーもチェック
		Body partner = b.getPartner();
		if( partner != null ){
			// イベント中なら終了
			if(partner.getCurrentEvent() != null) {
				return false;
			}
			// 同時にイベントを行わないよう、歳をとっている方にイベントを任せる
			if( partner.getAge() <  b.getAge() ){
				return false;
			}
		}

		//--------------------------------------------------
		//自分の状態チェック
		if( b.isIdiot() || b.isDamaged() || !b.hasOkazari() ) return false;
		// うんうん奴隷の場合
		if( b.getPublicRank() == PublicRank.UnunSlave ) return false;
		// 非ゆっくり症の場合
		if( b.isNYD()) return false;
		// うんうん中、出産中、食事中は終了
		if ( b.isShitting() || b.isBirth() || b.isEating() || b.nearToBirth() ) {
			return false;
		}
		//　子供のリストに生きている子供がいるか
		ArrayList<Body>childrenList = BodyLogic.createActiveChildList(b, true);
		if( childrenList == null || childrenList.size() == 0){
			return false;
		}
		// 興奮中は終了
		if ( b.isExciting() ) {
			return false;
		}

		//-------------------------------
		// 番の状態チェック
		Body bPartner = b.getPartner();
		if( bPartner != null ){
			if( bPartner.isDamaged() 	||
				bPartner.isLockmove() 	||
				bPartner.isNeedled()	||
				bPartner.getCriticalDamegeType() != null ||
				!bPartner.hasOkazari() )
			{
				return false;
			}
			// 産気づいたら終了
			if( partner.nearToBirth()){
				return false;	
			}
			// うんうん中、出産中は終了
			if ( bPartner.isShitting() || bPartner.isBirth() ) {
				return false;
			}
		}
		//-------------------------------
		// 子供の状態チェック
		boolean bWantToShit = true;
		boolean bWantToEat = true;
		boolean bIsBaby = false;
		// 自分が満腹なら食欲はない
		if( b.isFull() ){
			bWantToEat = false;				
		}
		// 子供がダメージを受けている、動けない場合は終了
		for(Body bodyChild: childrenList){
			if(bodyChild == null){
				continue;
			}
			
			// 怪我をしている
			if( bodyChild.isDamaged() || bodyChild.isNeedled() || bodyChild.getCriticalDamegeType() != null){
				bWantToShit = false;
				bWantToEat = false;	
				break;
			}
			if(bodyChild.isLockmove() || !bodyChild.hasOkazari()){
				bWantToShit = false;
				bWantToEat = false;	
				continue;
			}
			
			// 子供の初回食事がすんでいない場合はやらない
			if( !bodyChild.isbFirstEatStalk()){
				bWantToShit = false;
				bWantToEat = false;	
				break;
			}
			
			// 自分と子ゆとの間に壁があるなら終了
			if (Barrier.onBarrier(b.getX(), b.getY(), bodyChild.getX(), bodyChild.getY(), Barrier.BARRIER_YUKKURI)) {
				bWantToShit = false;
				bWantToEat = false;	
				break;
			}

			//-------------------------------------
			// うんうん判定
			double dShitPer = 100*bodyChild.getShit()/bodyChild.getShitLimit();
			// 赤ゆのみチェック
			if( bodyChild.isBaby() ){
				bIsBaby = true;
				// 子供がうんうん中ならスキップ
				if( bodyChild.isShitting() ){
					bWantToShit = false;
				}
				// 各子供のうんうん量が25%以下、100%以上ならスキップ
				if( dShitPer <= 25 || 100  <= dShitPer){
					bWantToShit = false;
				}
				// 子供が空腹ならスキップ
				if( bodyChild.isHungry() ){
					bWantToShit = false;
				}
			}

			//-------------------------------------
			// 子供が食事中なら何もしない
			if( bodyChild.isEating() ){
				bWantToEat = false;				
			}
			double dHungryPer = 100*bodyChild.getHungry()/bodyChild.getHungryLimit();
			// 各子供の満腹度が80%以上ならスキップ
			if( dHungryPer >= 80 ){
				bWantToEat = false;
			}
			else{
				// うんうん量が多いならやらない
				if( 50 < dShitPer ){
					bWantToEat = false;					
				}
			}
		}

		// 赤ゆがいないならうんうん体操はしない
		if( !bIsBaby){
			bWantToShit = false;
		}

		// おチビちゃん運び判定
		ArrayList<Body> childrenListForRideYukkuriTarget = new ArrayList<Body>();
		if( !bWantToShit && !bWantToEat){
			// 子供がダメージを受けている、動けない場合は終了
			for(Body bodyChild: childrenList){
				if(bodyChild == null || bodyChild.canAction()==false || bodyChild.isRemoved() ){
					continue;
				}
				if( bodyChild.getCurrentEvent() != null ){
					continue;
				}
				if( bodyChild.isLockmove() || !bodyChild.hasOkazari()){
					continue;
				}
				// 子供の初回食事がすんでいない場合はやらない
				if( !bodyChild.isbFirstEatStalk()){
					break;
				}
				// 子供がうんうん中ならスキップ
				if( bodyChild.isShitting() ){
					continue;
				}
				// 子供が食事中なら何もしない
				if( bodyChild.isEating() ){
					continue;
				}
				if( !bodyChild.isBaby()){
					continue;
				}

				// 自分と子ゆとの間に壁があるなら終了
				if (Barrier.onBarrier(b.getX(), b.getY(), bodyChild.getX(), bodyChild.getY(), Barrier.BARRIER_YUKKURI)) {
					continue;
				}
				childrenListForRideYukkuriTarget.add(bodyChild);
			}		
		}
		//-------------------------
		// 親が主体で行動を起こす
		//-------------------------
		
		// ・子が空腹の場合、家族一緒に餌まで移動する
		//   ・家族で移動する場合、移動速度は一番若い子ゆに合わせる
		//   ・餌まで移動した場合、一緒に食事をする
		//   ・空腹じゃなくても食べて家族で空腹度を合わせる
		if( bWantToEat ){
			if( goToEat(b,childrenList) ){
				return true;
			}
		}
		// ・子がうんうんをためていた場合、家族一緒にトイレまで移動する
		//   ・トイレの近くでうんうん体操をする
		//   ・トイレにうんうんを片付ける
		//   ・少量でも出して家族でうんうん量を合わせる
		//   ・汚れていた場合ぺろぺろする
		//     ・子は親に近づく
		if( bWantToShit ){
			if( goToShit(b, childrenList) ){
				return true;
			}
		}

		//おちび自慢
		if(rnd.nextBoolean()){
			if( proudChild(b, childrenList) ){
				return true;
			}
		}
		
		// おちびちゃん運び
		if( rideOnParent(b, childrenListForRideYukkuriTarget) ){
				return true;
		}

		
		// 未実装
		// ・ランダムで家族でピクニック
		// ・夕方になると家族でベッド（おうち）まで移動する
		// ・夜になると眠くなくても家族よりそって寝る

		return false;
	}
	// うんうん体操
	public static final boolean goToShit(Body b,ArrayList<Body>childrenList){
		Obj found = searchToilet(b);
		if(!b.checkWait(2000)){
			return false;
		}
		b.setLastActionTime();
		// うんうん体操実施
		ShitExercisesEvent ev = new ShitExercisesEvent(b, null, found, 10 );
		EventLogic.addWorldEvent(ev, b, MessagePool.getMessage(b, MessagePool.Action.ShitExercisesGOFrom));
		// イベント開始
		//b.currentEvent = ev);
		ev.start(b);
		return true;
	}
	public static Obj searchToilet(Body b){
		Obj found = null;
		ArrayList<Toilet> toiletList = SimYukkuri.world.currentMap.toilet;
		int minDistance = b.getEYESIGHT();
		for (Toilet t: toiletList) {
			// 最小距離のものが見つかっていたら
			if( minDistance < 1 ){
				break;
			}
			int distance = Translate.distance(b.getX(), b.getY(), t.getX(), t.getY() - t.getH()/6);
			if (minDistance > distance) {
				if (Barrier.acrossBarrier(b.getX(), b.getY(), t.getX(), t.getY() - t.getH()/6, Barrier.BARRIER_YUKKURI + Barrier.BARRIER_KEKKAI)) {
					continue;
				}
				found = (Toilet)t;
				minDistance = distance;
			}
		}	
		return found;
	}
	
	
	// 食事
	public static final boolean goToEat(Body b,ArrayList<Body>childrenList){
		// 餌を持っていたら落とす
		b.dropTakeoutItem(TakeoutItemType.FOOD);
		// フィールドの餌検索
		// 基本普通の餌でしかイベントは起こさない。茎があれば終了。
		Obj found = searchFood(b);
		if( found == null ){
			return false;
		}
		if(!b.checkWait(5000)){
			return false;
		}
		b.setLastActionTime();
		SuperEatingTimeEvent ev = new SuperEatingTimeEvent(b, null, found, 10 );
		EventLogic.addWorldEvent( ev, b, MessagePool.getMessage(b, MessagePool.Action.FamilyEatingTimeWait));
		// イベント開始
		//b.currentEvent = ev);
		ev.start(b);
		return true;
	}
	public static final Obj searchFood(Body b){
		Obj found = null;
		int minDistance = b.getEYESIGHT();
		int looks = -1000;
		
		// フィールドの餌検索
		ArrayList<Food> foodList = SimYukkuri.world.currentMap.food;
		for (Food f: foodList) {
			if (f.isEmpty()) {
				continue;
			}
			// 最小距離のものが見つかっていたら
			if( minDistance < 1 ){
				break;
			}
			int distance = Translate.distance(b.getX(), b.getY(), f.getX(), f.getY());
			if (minDistance > distance) {
				// 餌と自分との間に何らかの壁があればスキップ
				if (Barrier.acrossBarrier(b.getX(), b.getY(), f.getX(), f.getY(), Barrier.BARRIER_YUKKURI + Barrier.BARRIER_KEKKAI)) {
					continue;
				}
				boolean flag = false;
				switch(f.getFoodType()) {
					// 普通のフード
					default:
						flag = true;
						break;
					// 噛み砕いた茎
					case STALK:
						flag = true;
						break;
						//return null;
					// あまあま
					case SWEETS1:
					case SWEETS2:
						flag = true;
						break;
					// 生ゴミ
					case WASTE:
						// 飢餓状態かバカ舌なら食べる
						if(b.isTooHungry() || b.getTangType() == TangType.POOR) flag = true;
						break;
				}
				
				// 候補の中から最も価値の高いもの、近いものを食べに行く
				if(flag) {
					if(looks <= f.getLooks()) {
						found = f;
						minDistance = distance;
						looks = f.getLooks();
					}
				}
			}
		}
		return found;
	}
	
	// レイパーしかいないなら全員興奮終了
	public static final boolean checkRaperFamily(){
		boolean bIsNotRaperTarget = isRapeTarget();
		// レイプ対象がいない
		if( !bIsNotRaperTarget){
			Body[] bodyList = SimYukkuri.world.currentMap.body.toArray(new Body[0]);
			if( bodyList != null && bodyList.length != 0 ){
				for(Body b:bodyList){
					if( b.isRaper() ){
						b.setExciting(false);
					}
				}	
				return true;
			}
		}
		return false;
	}

	public static final boolean isRapeTarget(){
		Body[] bodyList = SimYukkuri.world.currentMap.body.toArray(new Body[0]);
		if( bodyList != null && bodyList.length != 0 ){
			for(Body b:bodyList){
				// レイプの対象がいる
				if( !b.isUnBirth() && !b.isDead() && !b.isRemoved() && !b.isRaper()){
					return true;
				}
			}
		}
		return false;
	}

	public static final boolean rideOnParent(Body b,ArrayList<Body>childrenList){
		if( childrenList == null || childrenList.size() == 0 ){
			return false;
		}
		
		if(!b.checkWait(3000)){
			return false;
		}
		b.setLastActionTime();
		Collections.shuffle(childrenList);
		for(Body child:childrenList){
			if( child.isBaby() && !child.isEating() && !child.isShitting() ){
				Obj target = null;
				// 空腹
				if( target == null ){
					if( child.isHungry() ){
						if( b.getTakeoutItem(TakeoutItemType.FOOD) == null ){
							Obj found = FamilyActionLogic.searchFood(b);
							if( found != null ){
								target = found;
							}
						}
					}
				}

				// トイレ
				if( target == null ){
					if( child.wantToShit() ){
						Obj found = FamilyActionLogic.searchToilet(b);
						if( found != null ){
							target = found;
						}
					}
				}

				// ベッド
				if( target == null ){
					if( child.isSleepy() || Terrarium.getDayState().ordinal() >= Terrarium.DayState.EVENING.ordinal()){
						Obj found = BedLogic.searchBed(b);
						if( found != null ){
							target = found;
						}
					}
				}

				// 目的地有り
				if( target != null ){
					// 近いなら運ばない
					int distance = Translate.distance(child.getX(), child.getY(), target.getX(), target.getY());
					if( distance < 10 ){
						target = null;
						continue;
					}
					else{
						// おちびちゃん運び実施
						YukkuriRideEvent ev = new YukkuriRideEvent(b, childrenList.get(0), target, 10 );
						EventLogic.addWorldEvent(ev, b, MessagePool.getMessage(b, MessagePool.Action.RideOnMe));
						// イベント開始
						//b.currentEvent = ev);
						ev.start(b);
						return true;
					}
				}
			}
		}
		return false;
	}

	public static final boolean proudChild(Body b,ArrayList<Body>childrenList){
		if(!b.checkWait(2000)){
			return false;
		}
		b.setLastActionTime();
		
		// 実施
		ProudChildEvent ev = new ProudChildEvent(b, null, null, 10 );
		EventLogic.addWorldEvent(ev, b, MessagePool.getMessage(b, MessagePool.Action.ProudChildsGOFrom));
		// イベント開始
		//b.currentEvent = ev);
		ev.start(b);
		return true;
	}
}

