package src.event;

import java.util.Map;

import src.SimYukkuri;
import src.base.Body;
import src.base.EventPacket;
import src.base.Obj;
import src.draw.Terrarium;
import src.draw.Translate;
import src.enums.AgeState;
import src.enums.Happiness;
import src.enums.ImageCode;
import src.enums.PlayStyle;
import src.item.Barrier;
import src.item.Food;
import src.logic.FoodLogic;
import src.logic.ToyLogic;
import src.system.MessagePool;
import src.system.ResourceUtil;
import src.util.YukkuriUtil;
import src.yukkuri.Fran;
import src.yukkuri.Meirin;
import src.yukkuri.Remirya;
import src.yukkuri.Sakuya;


/***************************************************
	空中捕食イベント
	protected Body from;			// イベントを発した個体
	protected Body to;				// 未使用
	protected Obj target;			// 未使用
	protected int count;			// 1
*/
public class PredatorsGameEvent extends EventPacket implements java.io.Serializable {

	private static final long serialVersionUID = -1709368077378752533L;
	private static final int[] ofsZ = {2, 0, -5};
	int tick = 0;
	int tick2 = 0;
	/** おもちゃにする対象のゆっくり */
	protected int toy = -1;
	boolean FlyGame = false;
	boolean grabbing = false;
	boolean snack = false;
	/**
	 * コンストラクタ.
	 */
	public PredatorsGameEvent(Body f, Body t, Obj tgt, int cnt) {
		super(f, t, tgt, cnt);
	}
	
	public PredatorsGameEvent() {
		
	}

	// 参加チェック
	//このイベントがスタートできるのはれみりゃ、ふらんのみ
	@Override
	public boolean checkEventResponse(Body b) {
		priority = EventPriority.LOW;

		if(b.isDead()) return false;
		Body from = YukkuriUtil.getBodyInstance(getFrom());
		if(b.isPredatorType() && b== from){
			//遊び相手の決定
			for (Map.Entry<Integer, Body> entry : SimYukkuri.world.getCurrentMap().getBody().entrySet()) {
				Body d = entry.getValue();
				int minDistance = b.getEYESIGHTorg();
				int wallMode = b.getBodyAgeState().ordinal();
				int size = b.getBodyAgeState().ordinal();
				// 飛行可能なら壁以外は通過可能
				if(b.canflyCheck()) {
					wallMode = AgeState.ADULT.ordinal();
				}
				if(b == d) continue;
				//生餌
				if(!d.isDead()) {
					// 捕食種では遊ばない
					if(d.isPredatorType()) continue;
					// 家族では遊ばない
					if(b.isFamily(d)) continue;
					// 飛べるのでは遊ばない
					if(d.canflyCheck()) continue;
					//かびてるやつは避ける
					if(b.findSick(d)) continue;
					/// れみりゃやふらんはさくや、めーりんでは遊ばない
					if((d.getType() == Sakuya.type || d.getType() == Meirin.type) && (b.getType() == Remirya.type || b.getType() == Fran.type)) continue;
					// 最小距離のものが見つかっていたら
					if( minDistance < 1 ){
						break;
					}
					int distance = Translate.distance(b.getX(), b.getY(), d.getX(), d.getY());
					if(d.getBodyAgeState().ordinal() <= b.getBodyAgeState().ordinal()) {
						// 自分以下の大きさの相手の場合
						if (minDistance > distance || d.getBodyAgeState().ordinal() <= size) {
							if (Barrier.acrossBarrier(b.getX(), b.getY(), d.getX(), d.getY(), Barrier.MAP_BODY[wallMode] + Barrier.BARRIER_KEKKAI)) {
								continue;
							}
							toy = d.objId;
							minDistance = distance;
							size = d.getBodyAgeState().ordinal();
						}
					}
				}
			return true;
			}
		}
		return false;
	}

	// イベント開始動作
	@Override
	public void start(Body b) {
		b.setMessage(MessagePool.getMessage(b, MessagePool.Action.GameStart),true);
	}

	// 毎フレーム処理
	// UpdateState.ABORTを返すとイベント終了
	@Override
	public UpdateState update(Body b) {
		Body from = YukkuriUtil.getBodyInstance(getFrom());
		Body toy = YukkuriUtil.getBodyInstance(this.toy);
		//対象が決定できなかったり、捕食防止ディフューザー環境だったりしたら中止。ボール遊びを試す
		if(from == null || toy == null || Terrarium.isPredatorSteam()){
			if(ToyLogic.checkToy(from)){
				from.setPlaying(PlayStyle.BALL);
				from.setPlayingLimit(150 +SimYukkuri.RND.nextInt(100)-49);
			}
			return UpdateState.ABORT;
		}
		// 相手が消えてしまったらイベント中断
		if(toy.isRemoved() || toy.isGrabbed()) {
			toy.setLinkParent(-1);
			return UpdateState.ABORT;
		}
		// 相手が死んだらイベント中断
		if(toy.isDead()) {
			from.setMessage(MessagePool.getMessage(b, MessagePool.Action.ComplainAboutFragleness),true);
			from.setForceFace(ImageCode.PUFF.ordinal());
			toy.setLinkParent(-1);
			return UpdateState.ABORT;
		}
		//各動作は1回ずつ
		if(b!=from){
			return  null;
		}
		//おやつ中は別挙動
		if(snack){
			return UpdateState.FORCE_EXEC;
		}
		//満足したら辞める
		if(from.isVeryHungry() || from.isSleepy() || SimYukkuri.RND.nextInt(1000)==0){
			//b.setMessage(MessagePool.getMessage(b, MessagePool.Action.GameEnd));
			toy.setLinkParent(-1);
			return UpdateState.ABORT;
		}

		//待機時間
		if(tick>=0){
			if(b.canflyCheck() && SimYukkuri.RND.nextBoolean()){
				FlyGame = true;
			}
			else{
				FlyGame = false;
			}
			tick--;
			return null;
		}

		//以下行動

		//おもちゃをつかんでるとき
		if(grabbing){
			//おもちゃのセリフ
			if(SimYukkuri.RND.nextInt(8)==0){
				toy.setForceFace(ImageCode.CRYING.ordinal());
				if(SimYukkuri.RND.nextBoolean() && toy.getZ()>0) toy.setPikoMessage(MessagePool.getMessage(toy, MessagePool.Action.Flying),true);
				else toy.setPikoMessage(MessagePool.getMessage(toy, MessagePool.Action.DontPlayMe),true);
			}
			// 高度に達してたら落とす
			if(Math.abs(b.getZ() - Translate.getFlyHeightLimit()) < 3) {
				//空腹だったらおやつに食べる。優先度も変更
				if(from.isHungry()){
					snack = true;
					priority = EventPriority.MIDDLE;
					return UpdateState.FORCE_EXEC;
				}
				//ランダムで落とす
				if(SimYukkuri.RND.nextInt(20)== 0){
					grabbing = false;
					b.setForceFace(ImageCode.SMILE.ordinal());
					b.setMessage(MessagePool.getMessage(b, MessagePool.Action.DropYukkuri));
					b.addStress(-100);
					toy.setLinkParent(-1);
					toy.strikeByYukkuri(b, this,false);
					b.moveTo(toy.getX(), toy.getY(), Translate.getFlyHeightLimit());
					//toy.addDamage(25);
					tick = 20;
				}
				//それ以外は空中でいじめてダメージ
				else{
					b.moveTo(b.getX(), b.getY(), Translate.getFlyHeightLimit());
					if(SimYukkuri.RND.nextInt(12)==0){
						if(b.isRude() && SimYukkuri.RND.nextBoolean())b.setForceFace(ImageCode.RUDE.ordinal());
						else b.setForceFace(ImageCode.SMILE.ordinal());
						toy.setForceFace(ImageCode.PAIN.ordinal());
						toy.setPikoMessage(MessagePool.getMessage(toy, MessagePool.Action.Scream),true);
						if(SimYukkuri.RND.nextInt(100)==0)toy.bodyInjure();
						toy.addDamage(15);
					}
				}

			}
			else{
				//高度を稼ぐ
				b.moveTo(b.getX(), b.getY(), Translate.getFlyHeightLimit());
				// 相手の座標を縛る
				toy.setCalcX(b.getX());
				toy.setCalcY(b.getY() + 1);
				int toysHeight=b.getZ() + ofsZ[toy.getBodyAgeState().ordinal()];
				if(toysHeight>=0)toy.setCalcZ(toysHeight);
				else toy.setCalcZ(0);
			}
			return null;
		}
		/*int rangeX = Translate.invertX((int)((b.getCollisionX() + toy.getCollisionX()) * 0.6f), toy.getY());
		rangeX = Translate.transSize(rangeX);
		int distX = Math.abs(b.getX() - toy.getX());
		int distY = Math.abs(b.getY() - toy.getY());
		int range = Math.abs(rangeX - distX);*/

		//接触状態
		if ((b.getStepDist() + 2) >= Translate.distance(b.getX(), b.getY(), toy.getX(),toy.getY())) {
			//空中で遊ぶ場合はつかむ
			if(FlyGame){
				if(b.isRude() && SimYukkuri.RND.nextBoolean())b.setForceFace(ImageCode.RUDE.ordinal());
				else b.setForceFace(ImageCode.SMILE.ordinal());
				b.setMessage(MessagePool.getMessage(b, MessagePool.Action.CaughtYou),true);
				toy.setLinkParent(b.objId);
				grabbing = true;
				b.moveTo(b.getX(), b.getY(), 5);
			}
			//体当たりで遊ぶ
			else{
				//b.setMessage(MessagePool.getMessage(b, MessagePool.Action.PlayTreasure));
				if(SimYukkuri.RND.nextBoolean())b.setForceFace(ImageCode.RUDE.ordinal());
				else b.setForceFace(ImageCode.SMILE.ordinal());
				toy.strikeByYukkuri(b, this,false);
				if(b.canflyCheck()) b.moveTo(b.getX(), b.getY(), Translate.getFlyHeightLimit());
				b.addStress(-50);
				tick = 15;
			}
		}
		//非接触状態なら向かう
		else{
			b.moveTo(toy.getX(), toy.getY(), toy.getZ() /*Translate.getFlyHeightLimit()*/);
			if(SimYukkuri.RND.nextInt(10)==0){
				if(b.isRude() && SimYukkuri.RND.nextBoolean())b.setForceFace(ImageCode.RUDE.ordinal());
				else b.setForceFace(ImageCode.SMILE.ordinal());
			}
			if(SimYukkuri.RND.nextInt(15)==0)b.setMessage(MessagePool.getMessage(b, MessagePool.Action.HeyYouWait));
			toy.stay();
		}

		return null;
	}

	// イベント目標に到着した際に呼ばれる(このイベントでは別挙動の扱いをしている)
	// trueを返すとイベント終了
	@Override
	public boolean execute(Body b) {
		Body toy = YukkuriUtil.getBodyInstance(this.toy);
		// 相手が消えてしまったらイベント中断
		if(toy == null || toy.isRemoved()) {
			toy.setLinkParent(-1);
			return true;
		}
		Body from = YukkuriUtil.getBodyInstance(getFrom());
		if (from == null) return true;
		// 相手が捕まれたらイベント中断
		if(toy.isGrabbed()) {
			Body to = YukkuriUtil.getBodyInstance(getTo());
			if (to != null) to.setLinkParent(-1);
			return true;
		}
		// 相手が死んだらイベント中断
		if(toy.isDead()) {
			toy.setLinkParent(-1);
			return true;
		}
		// 相手の座標を縛る
		toy.setCalcX(from.getX());
		toy.setCalcY(from.getY() + 1);
		toy.setCalcZ(from.getZ() + ofsZ[toy.getBodyAgeState().ordinal()]);

		tick2++;
		if(tick2 == 20) {
			tick2 = 0;
			FoodLogic.eatFood(b, Food.FoodType.BODY, Math.min(b.getEatAmount(), toy.getBodyAmount()));
			toy.eatBody(Math.min(b.getEatAmount(), toy.getBodyAmount()));
			if (toy.isSick() && SimYukkuri.RND.nextBoolean()) b.addSickPeriod(100);
			if(toy.isDead()) {
				toy.setMessage(MessagePool.getMessage(toy, MessagePool.Action.Dead));
				toy.setLinkParent(-1);
				return true;
			}
			else {
				if( toy.isNotNYD() ){
					toy.setMessage(MessagePool.getMessage(toy, MessagePool.Action.EatenByBody2));
					toy.setHappiness(Happiness.VERY_SAD);
					toy.setForceFace(ImageCode.PAIN.ordinal());
				}
			}
		}
		return false;
	}

	// イベント終了処理
	@Override
	public void end(Body b) {
		b.setMessage(MessagePool.getMessage(b, MessagePool.Action.GameEnd));
		grabbing=false;
		Body toy = YukkuriUtil.getBodyInstance(this.toy);
		if(toy!=null){
			toy.setLinkParent(-1);
			toy=null;
		}
	}
	
	@Override
	public String toString() {
		return ResourceUtil.getInstance().read("event_pgame");
	}
}
