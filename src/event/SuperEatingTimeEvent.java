package src.event;

import java.util.ArrayList;
import java.util.Random;

import src.base.Body;
import src.base.EventPacket;
import src.base.Obj;
import src.draw.Translate;
import src.enums.GatheringDirection;
import src.enums.Happiness;
import src.item.Barrier;
import src.item.Food;
import src.logic.BodyLogic;
import src.logic.FoodLogic;
import src.system.MessagePool;

/***************************************************
	すーぱーむーしゃむーしゃたいむイベント
	protected Body from;			// イベントを発した個体
	protected Body to;				// 未使用
	protected Obj target;			// 移動先
	protected int count;			// 10
*/
public class SuperEatingTimeEvent extends EventPacket implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	Random rnd = new Random();
	int tick = 0;
	int nFromWaitCount = 0;
	public STATE state = STATE.WAIT;
	int nLowestStep = 0;

	// 行動ステート
	public enum STATE {
		GO,			// 移動
		WAIT,		// 待機
		START_BEFORE,// イベント開始直前
		START,		// イベント開始時
		END,		// イベント終了時
	}


	public SuperEatingTimeEvent(Body f, Body t, Obj tgt, int cnt) {
		super(f, t, tgt, cnt);
		priority = EventPriority.HIGH;
	}

	public boolean simpleEventAction(Body b) {
		if(getFrom().isShutmouth() ){
			return true;
			}
		return false;
	}

	// 参加チェック
	// ここで各種チェックを行い、イベントへ参加するかを返す
	// また、イベント優先度も必要に応じて設定できる
	public boolean checkEventResponse(Body b) {
		boolean ret = false;
		if(getFrom() == b && !(b.isShutmouth())) {
			return true;
		}

		if(b.isShutmouth() ){
			return false;
		}

		if( b.getPublicRank() != getFrom().getPublicRank() )
		{
			return false;
		}

		// 動けないなら参加しない
		if(b.isDontMove()  )
		{
			return false;
		}

		// つがいも参加する
		if(getFrom().isPartner(b) ) {
			return true;
		}

		if( b.isNYD() )
		{
			return false;
		}

		// Fromの子供だけ参加する(※Fromが教育係のときは全ての子供が参加するようにする？)
		if(!b.isChild(getFrom())) return false;
		// 大人は終了
		if(b.isAdult() ) return false;

		ret = true;

		return ret;
	}

	// イベント開始動作
	public void start(Body b) {
		b.setCurrentEvent(this);
	}

	public boolean checkWait(Body b,int nWaitTime)
	{
		b.checkWait(nWaitTime);
		if( !b.checkWait(nWaitTime))
		{
			return false;
		}
		b.setLastActionTime();
		return true;
	}

	public int getLowestStep()
	{
		return nLowestStep;
	}

	public STATE getState()
	{
		return state;
	}

	// 毎フレーム処理
	// trueを返すとイベント終了
	// 親→子供→次のステート、の順で処理をする
	public UpdateState update(Body b) {
		if( b == null || getFrom() == null){
			return UpdateState.ABORT;
		}
		if( b.isNYD() ){
			return UpdateState.ABORT;
		}

		// 親が消えてしまったらイベント中断
		if(getFrom().isRemoved()) return UpdateState.ABORT;
		// ターゲットが消えてしまったらイベント中断
		if(target == null || target.isRemoved()){
			getFrom().setMessage(MessagePool.getMessage(getFrom(), MessagePool.Action.NoFood));
			getFrom().setHappiness(Happiness.VERY_SAD);
			getFrom().stay();
			return UpdateState.ABORT;
		}

		// 産気づいたら終了
		if( b.nearToBirth()  ){
			return UpdateState.ABORT;
		}

		if( (10 < nFromWaitCount && getFrom().getCurrentEvent() == null) || target == null || target.isRemoved() ){
			return UpdateState.ABORT;
		}

		if( tick%20 != 0){
			return null;
		}

		// 満腹度が2%以下なら2%にする(強制イベント救済措置)
		if( 2 > 100*b.getHungry()/b.getHungryLimit()){
			b.setHungry(2*b.getHungryLimit()/100);
		}

		b.wakeup();

		// 親
		if(b == getFrom()){
			// 何らかの理由で終了しそうにないなら終わらせる
			if( 5000< nFromWaitCount ){
				return UpdateState.ABORT;
			}
			nFromWaitCount++;
			// 子ゆがいなければ終了
			ArrayList<Body> childrenList = BodyLogic.createActiveChildList(getFrom(), true);
			if( (childrenList == null) || (childrenList.size() == 0)){
				return UpdateState.ABORT;
			}

			//最小歩幅の設定と、子ゆがイベント中かのチェック
			Body bodyTarget = childrenList.get(0);
			boolean bIsChildInEvent = false;
			for(Body child:childrenList){
				int step = child.getStep();
				if (nLowestStep == 0 || step < nLowestStep) {
					nLowestStep = step;
				}
				if( child.getCurrentEvent() == this ){
					bIsChildInEvent = true;
				}
			}
			if( 1000 < nFromWaitCount ){
				// イベント参加者がいないなら終了
				if( !bIsChildInEvent){
					return UpdateState.ABORT;
				}
			}

			//番の設定
			Body partner = getFrom().getPartner();
			if( partner == getFrom() ){
				partner = null;
			}
			
			//親のステート
			boolean bResult = false;
			switch(state){
				case WAIT:// ごはんさんをたべにいくよ！みんなあつまってね！
					// 家族を集める
					bResult = BodyLogic.gatheringYukkuriSquare(getFrom(), childrenList, GatheringDirection.DOWN, this);
					for(Body bChild:childrenList){
						if(bChild != null){
							// 他に用事があれば除外
							bChild.setMoveTarget(null);
							bChild.wakeup();
						}
					}
					if(rnd.nextInt(100) == 0){
						b.setMessage(MessagePool.getMessage(b, MessagePool.Action.FamilyEatingTimeWait), true);
					}
					b.setHappiness(Happiness.HAPPY);
					// 番の処理
					if( partner != null ){
						int colX = BodyLogic.calcCollisionX(partner,getFrom());
						partner.moveTo( getFrom().getX()+colX*2, getFrom().getY());
						partner.setHappiness(Happiness.HAPPY);
						// 他に用事があれば除外
						partner.setMoveTarget(null);
					}
					//ステート移行
					if( bResult){
						state = STATE.GO;
					}
					else{
						b.stay(100);
					}
					// 何らかの理由で終了しそうにないなら終わらせる
					if( 1000< nFromWaitCount ){
						return UpdateState.ABORT;
					}
					nFromWaitCount++;
					break;
				case GO:// ごはんさんのにおいがするよ！
					// 移動開始
					bResult = BodyLogic.gatheringYukkuriBackLine(getFrom(), childrenList, this);
					for(Body bChild:childrenList){
						// 他に用事があれば除外
						bChild.setMoveTarget(null);
						bChild.wakeup();
					}
					int nDistance = Translate.getRealDistance(b.getX(), b.getY(), bodyTarget.getX(), bodyTarget.getY());
					int colXChild = Math.abs(BodyLogic.calcCollisionX(b, bodyTarget));
					// 一定距離を保つ
					if(colXChild*3 < nDistance ){
						b.stay();
					}
					
					// 番の処理
					if( partner != null ){
						int colX = BodyLogic.calcCollisionX(partner,getFrom());
						partner.moveTo( getFrom().getX()+colX*2, getFrom().getY());
						partner.setHappiness(Happiness.HAPPY);
						// 他に用事があれば除外
						partner.setMoveTarget(null);
						if(rnd.nextInt(50) == 0 ){
							partner.setMessage(MessagePool.getMessage(partner, MessagePool.Action.WantFood));
						}
					}
					
					int colX = Translate.invertX(b.getCollisionX(), target.getY());
					colX = Translate.transSize(colX);
					int nDistanceFood = Translate.getRealDistance(b.getX(), b.getY(), target.getX(), target.getY() - 20);
					// 餌の近くで待つ
					if( nDistanceFood <= 1 ){
						if( bResult ){
							state = STATE.START_BEFORE;
						}
						b.stay();
					}
					// 餌に近づく
					else{
						b.moveToEvent(this, target.getX(), target.getY() - 20);
						if(rnd.nextInt(50) == 0 ){
							b.setMessage(MessagePool.getMessage(b, MessagePool.Action.WantFood));
						}
					}
					break;
				case START_BEFORE:// ごはんの上に集合
					b.stay();
					bResult = BodyLogic.gatheringYukkuriSquare(target, childrenList, GatheringDirection.UP, this);
					for(Body bChild:childrenList){
						// 他に用事があれば除外
						bChild.setMoveTarget(null);
						bChild.wakeup();
					}

					// 配置済みの場合
					if(bResult){
						b.setMessage(MessagePool.getMessage(b, MessagePool.Action.SuperEatingTime));
						b.setHappiness(Happiness.VERY_HAPPY);
						b.addMemories(1);
						// 番の処理
						if( partner != null ){
							partner.setMessage(MessagePool.getMessage(partner, MessagePool.Action.SuperEatingTime));
							partner.setHappiness(Happiness.VERY_HAPPY);
							partner.addMemories(1);
						}
						//子ゆの処理
						for(Body bChild:childrenList){
							if(bChild != null){
								bChild.setMessage(MessagePool.getMessage(b, MessagePool.Action.SuperEatingTime));
								bChild.setHappiness(Happiness.VERY_HAPPY);
								bChild.stay();
								bChild.addMemories(1);
							}
						}
						state = STATE.START;
					}
					else{
						// 番の処理
						if( partner != null ){
							colX = BodyLogic.calcCollisionX(partner,getFrom());
							partner.moveTo( (int)(getFrom().getX()+colX*1.5), getFrom().getY());
							partner.setHappiness(Happiness.HAPPY);
							// 他に用事があれば除外
							partner.setMoveTarget(null);
						}
					}
					break;
				case START:
					boolean bIsHungry = false;
					int noHungerPeriod = 500;
					
					// ご飯がない時の処理
					if (target instanceof Food) {
						Food food = (Food)target;
						if( food.isEmpty() ){
							b.setMessage(MessagePool.getMessage(b, MessagePool.Action.NoFood));
							b.setHappiness(Happiness.VERY_SAD);
							b.stay();
							return UpdateState.ABORT;
						}
					}

					for(Body bChild:childrenList){
						if(bChild != null){
							bChild.wakeup();
							if(bChild.getHungryLimit()*10/100  > bChild.getHungry()){
								bChild.setMoveTarget(target);
								bChild.setToFood(true);
								bIsHungry = true;
							}
							else{
								bChild.addMemories(10);
							}
							bChild.setNoHungrybySupereatingTimePeriod(noHungerPeriod);
						}
					}

					if( !bIsHungry){
						// 子供の食事が終わってから食べる
						b.setMoveTarget(target);
						b.setToFood(true);
						b.setNoHungrybySupereatingTimePeriod(noHungerPeriod);
						b.addMemories(10);
						// 番の処理
						if( partner != null ){
							partner.setMoveTarget(target);
							partner.setToFood(true);
							partner.setNoHungrybySupereatingTimePeriod(noHungerPeriod);
							partner.addMemories(10);
						}

						if(b.getHungry() < b.getHungryLimit()*10/100 ){
							if( partner != null ){
								if(partner.getHungry() < partner.getHungryLimit()*10/100 ){
									return UpdateState.ABORT;
								}
							}
							else{
								return UpdateState.ABORT;
							}
						}
					}
					else{
						// 子供の食事が終わってなくても空腹なら食べる
						if(b.isHungry()){
							b.setMoveTarget(target);
							b.setToFood(true);
						}
						else{
							b.setMoveTarget(null);
							b.stay(100);
							if(rnd.nextInt(30) == 0 ){
								// 余裕なら子供の状態を喜ぶ
								b.setMessage(MessagePool.getMessage(b, MessagePool.Action.GladAboutChild), false);
								b.setHappiness(Happiness.VERY_HAPPY);
							}
						}
						// 番の処理
						if( partner != null ){
							// 子供の食事が終わってなくても空腹なら食べる
							if(partner.isHungry()){
								partner.setMoveTarget(target);
								partner.setToFood(true);
							}
							else{
								partner.setMoveTarget(null);
								partner.stay(100);
								if(rnd.nextInt(30) == 0 ){
									// 余裕なら子供の状態を喜ぶ
									partner.setMessage(MessagePool.getMessage(b, MessagePool.Action.GladAboutChild), false);
									partner.setHappiness(Happiness.VERY_HAPPY);
								}
							}
						}
					}
					break;
				default:
					break;
			}
		}
		
		//親以外の処理
		else{
			// つがいはスキップ。主催側で処理
			if(b.isPartner(getFrom()) ) {
				// 壁に引っかかってるなら終了
				if (Barrier.onBarrier(b.getX(), b.getY(), getFrom().getX(), getFrom().getY(), Barrier.MAP_BODY[b.getBodyAgeState().ordinal()]+Barrier.BARRIER_KEKKAI)) {
					b.clearEvent();
				}
				return null;
			}

			// 子供
			switch(state){
				case GO:
					// 壁に引っかかってるなら終了
					if (Barrier.onBarrier(b.getX(), b.getY(), getFrom().getX(), getFrom().getY(), Barrier.MAP_BODY[b.getBodyAgeState().ordinal()]+Barrier.BARRIER_KEKKAI)) {
						b.clearEvent();
						return null;
					}
					b.setHappiness(Happiness.HAPPY);
					if(rnd.nextInt(50) == 0 ){
						b.setMessage(MessagePool.getMessage(b, MessagePool.Action.WantFood));
					}
					break;
				case START:
					b.setHappiness(Happiness.VERY_HAPPY);
					int nDistance = Translate.getRealDistance(b.getX(), b.getY(), target.getX(), target.getY());
					if( nDistance < 3 ){
						Food f = (Food)target;
						FoodLogic.eatFood(b, f.getFoodType(), Math.min(b.getEatAmount(), f.amount));
						f.eatFood(Math.min(b.getEatAmount(), f.amount));
						b.addMemories(10);
						b.clearActions();
					}
					else{
						b.moveToEvent(this, target.getX(), target.getY());
						b.setToFood(true);
					}
					//return UpdateState.FORCE_EXEC;
					break;
				default:
					b.setHappiness(Happiness.HAPPY);
					break;
			}
		}

		return null;
	}

	// イベント目標に到着した際に呼ばれる
	// trueを返すとイベント終了
	public boolean execute(Body b) {
		return false;
	}
	
	@Override
	public String toString() {
		return "すーぱーむーしゃむーしゃたいむ";
	}

}