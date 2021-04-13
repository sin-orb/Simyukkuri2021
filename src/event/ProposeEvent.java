package src.event;

import java.util.Random;

import src.base.Body;
import src.base.EventPacket;
import src.base.Obj;
import src.enums.CoreAnkoState;
import src.enums.Happiness;
import src.enums.ImageCode;
import src.enums.Intelligence;
import src.logic.BodyLogic;
import src.system.MessagePool;


/***************************************************
	プロポーズイベント
	protected Body from;			// イベントを発した個体
	protected Body to;				// 結婚対象
	protected Obj target;			// 未使用
	protected int count;			// 1
*/
public class ProposeEvent extends EventPacket implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	//private static final int[] ofsZ = {2, 0, -5};
	int tick = 0;
	Random rnd = new Random();
	protected boolean started =false;

	public ProposeEvent(Body f, Body t, Obj tgt, int cnt) {
		super(f, t, tgt, cnt);
		priority = EventPriority.HIGH;
	}

	// 参加チェック
	public boolean checkEventResponse(Body b) {
		if(b==getFrom()||b==to)return true;

		return false;
	}

	// イベント開始動作
	public void start(Body b) {
		to.wakeup();
		getFrom().setCurrentEvent(this);
		to.setCurrentEvent(this);
		int colX = BodyLogic.calcCollisionX(b, to);
		if(getFrom().canflyCheck()) getFrom().moveToEvent(this, to.getX() + colX, to.getY(),to.getZ());
		else getFrom().moveToEvent(this, to.getX() + colX, to.getY());
	}

	// 毎フレーム処理
	// UpdateState.ABORTを返すとイベント終了
	public UpdateState update(Body b) {
		if(getFrom()==null || to==null || getFrom().isDead() || getFrom().isRemoved())return UpdateState.ABORT;
		//相手が死んだか 相手が消えてしまったか非ゆっくり症発症したらイベント中断
		if(to.isDead() || to.isRemoved() || to.geteCoreAnkoState() != CoreAnkoState.DEFAULT) {
			getFrom().setCalm();
			getFrom().setBodyEventResMessage(MessagePool.getMessage(getFrom(), MessagePool.Action.Surprise), 30, true, false);
			getFrom().setHappiness(Happiness.VERY_SAD);
			getFrom().addStress(getFrom().getStressLimit()/10);
			if(rnd.nextBoolean()){
				getFrom().setForceFace(ImageCode.TIRED.ordinal());
			}
			else{
				getFrom().setForceFace(ImageCode.CRYING.ordinal());
				if(rnd.nextInt(3) == 0) getFrom().doYunnyaa(true);
			}
			return UpdateState.ABORT;
		}
		
		int colX = BodyLogic.calcCollisionX(b, to);
		//相手がつかまれているとき
		if(to.isGrabbed()){
			getFrom().setBodyEventResMessage(MessagePool.getMessage(getFrom(), MessagePool.Action.DontPreventUs), 30, false, rnd.nextBoolean());
			getFrom().setForceFace(ImageCode.PUFF.ordinal());
			getFrom().setAngry();
			started=false;
			getFrom().setLockmove(false);
			to.setLockmove(false);
			if(getFrom().canflyCheck()) getFrom().moveToEvent(this, to.getX() + colX, to.getY(),to.getZ());
			else getFrom().moveToEvent(this, to.getX() + colX, to.getY());
			//ランダムであきらめる
			if(getFrom().getIntelligence() != Intelligence.FOOL && rnd.nextInt(1500)==0){
				if(rnd.nextBoolean()){
					getFrom().setBodyEventResMessage(MessagePool.getMessage(getFrom(), MessagePool.Action.LamentNoYukkuri), 30, true, true);
					getFrom().setHappiness(Happiness.VERY_SAD);
					getFrom().setForceFace(ImageCode.CRYING.ordinal());
				}
				else{
					getFrom().setBodyEventResMessage(MessagePool.getMessage(getFrom(), MessagePool.Action.LamentLowYukkuri), 30, true, true);
					getFrom().setHappiness(Happiness.SAD);
					getFrom().setForceFace(ImageCode.TIRED.ordinal());
				}
				return UpdateState.ABORT;
			}
			return null;
		}

		//イベントが始まってたら飛ばす
		if(started) return UpdateState.FORCE_EXEC;

		//たどり着くまで
		if(getFrom().canflyCheck()) getFrom().moveToEvent(this, to.getX() + colX, to.getY(),to.getZ());
		else getFrom().moveToEvent(this, to.getX() + colX, to.getY());
		tick = 0;
		//行動主の呼び止め
//		from.setCalm();
//		from.setForceFace(ImageCode.EXCITING.ordinal());
		getFrom().setExciting(true);
		//相手も興奮して、ぺにぺに相撲になるのの防止
		if(to.isExciting()){
			to.setCalm();
			to.clearEvent();
		}
		to.stay();
		if(rnd.nextInt(20)== 0){
			if(rnd.nextBoolean())getFrom().setBodyEventResMessage(MessagePool.getMessage(getFrom(), MessagePool.Action.PleaseWait), 30, true, false);
			else getFrom().setBodyEventResMessage(MessagePool.getMessage(getFrom(), MessagePool.Action.Excite), 30, true, false);
		}
		return null;
	}

	// イベント目標に到着した際に呼ばれる
	// trueを返すとイベント終了
	public boolean execute(Body b) {
		if(to.isGrabbed()){
			return false;
		}
		//相手がかびてるor食われてる時の挙動
		if(getFrom().findSick(to) || to.isEatenByAnimals() || to.hasDisorder()){
			getFrom().setCalm();
			getFrom().setBodyEventResMessage(MessagePool.getMessage(getFrom(), MessagePool.Action.Surprise), 30, true, false);
			getFrom().setHappiness(Happiness.VERY_SAD);
			getFrom().addStress(getFrom().getStressLimit()/10);
			if(rnd.nextBoolean()){
				getFrom().setForceFace(ImageCode.TIRED.ordinal());
			}
			else{
				getFrom().setForceFace(ImageCode.CRYING.ordinal());
				if(rnd.nextInt(3) == 0) getFrom().doYunnyaa(true);
			}
			//夫婦関係の解消
			getFrom().setPartner(null);
			if(to.getPartner() == getFrom()){
				to.setPartner(null);
			}
			return true;
		}

		if(tick == 0) {
			//行動主の呼び止め
			getFrom().setCalm();
			getFrom().stayPurupuru(30);
			getFrom().addStress(10);
			if(getFrom().isRude()) getFrom().setForceFace(ImageCode.VAIN.ordinal());
			else getFrom().setForceFace(ImageCode.EMBARRASSED.ordinal());
			getFrom().setBodyEventResMessage(MessagePool.getMessage(getFrom(), MessagePool.Action.PleaseWait), 30, true, false);
			started =true;
		}
		else if(tick == 5) {
			//振り向く
			to.setCurrentEvent(this);
			to.constraintDirection(getFrom(), false);
			getFrom().setLockmove(true);
			to.setLockmove(true);
		}
		else if(tick == 20) {
			// 告白
			if(getFrom().isRude() || rnd.nextInt(20)==0)getFrom().setForceFace(ImageCode.VAIN.ordinal());
			else getFrom().setForceFace(ImageCode.EMBARRASSED.ordinal());
			//カップルの設定(ただし、ここではやる側のみ)
			getFrom().setPartner(to);
			//告白セリフ
			getFrom().setBodyEventResMessage(MessagePool.getMessage(getFrom(), MessagePool.Action.Propose), 30, true, false);
			getFrom().stayPurupuru(50);
			to.setForceFace(ImageCode.EMBARRASSED.ordinal());
		}
		else if(tick == 40) {
			//双方の反応
			//成功判定
			boolean sayOK=acceptPropose(getFrom(),to);
			
			//成功
			if(sayOK){
				to.setForceFace(ImageCode.SMILE.ordinal());
				to.setPartner(getFrom());
				to.setBodyEventResMessage(MessagePool.getMessage(to, MessagePool.Action.ProposeYes), 30, true, false);
				// ゲスほど幸福度は低い
				switch(getFrom().getAttitude()) {
					case VERY_NICE:
						getFrom().addStress(-getFrom().getStressLimit()/5);
						getFrom().addMemories(50);
						break;
					case NICE:
						getFrom().addStress(-getFrom().getStressLimit()/10);
						getFrom().addMemories(40);
						break;
					case AVERAGE:
						getFrom().addStress(-getFrom().getStressLimit()/20);
						getFrom().addMemories(30);
						break;
					case SHITHEAD:
					case SUPER_SHITHEAD:
						getFrom().addStress(-getFrom().getStressLimit()/30);
						getFrom().addMemories(30);
						break;
				}
				switch(to.getAttitude()) {
					case VERY_NICE:
						to.addStress(-to.getStressLimit()/5);
						to.addMemories(50);
						break;
					case NICE:
						to.addStress(-to.getStressLimit()/10);
						to.addMemories(40);
						break;
					case AVERAGE:
						to.addStress(-to.getStressLimit()/20);
						to.addMemories(30);
						break;
					case SHITHEAD:
					case SUPER_SHITHEAD:
						to.addStress(-to.getStressLimit()/30);
						to.addMemories(30);
						break;
				}
			}
			//失敗
			else{
				if(to.findSick(getFrom())){
					to.setBodyEventResMessage(MessagePool.getMessage(to, MessagePool.Action.HateMoldyYukkuri), 30, true, false);
					to.setForceFace(ImageCode.PUFF.ordinal());
				}
				else {
					to.setBodyEventResMessage(MessagePool.getMessage(to, MessagePool.Action.ProposeNo), 30, true, false);
					if(to.isRude()){
						to.setForceFace(ImageCode.RUDE.ordinal());
					}
					else{
						to.setForceFace(ImageCode.TIRED.ordinal());
					}
				}
				getFrom().setPartner(null);
				// ストレスと思い出の上下
				switch(getFrom().getAttitude()) {
					case VERY_NICE:
						getFrom().addStress(getFrom().getStressLimit()/20);
						getFrom().addMemories(-50);
						break;
					case NICE:
						getFrom().addStress(getFrom().getStressLimit()/16);
						getFrom().addMemories(-40);
						break;
					case AVERAGE:
						getFrom().addStress(getFrom().getStressLimit()/10);
						getFrom().addMemories(-30);
						break;
					case SHITHEAD:
					case SUPER_SHITHEAD:
						getFrom().addStress(getFrom().getStressLimit()/6);
						getFrom().addMemories(-20);
						break;
				}
				switch(to.getAttitude()) {
					case VERY_NICE:
						to.addStress(-to.getStressLimit()/20);
						break;
					case NICE:
						to.addStress(-to.getStressLimit()/16);
						break;
					case AVERAGE:
						to.addStress(-to.getStressLimit()/10);
						break;
					case SHITHEAD:
					case SUPER_SHITHEAD:
						to.addStress(-to.getStressLimit()/6);
						break;
				}
			}
		}
		else if(tick == 60) {
			//成功時はすっきりを迫る
			if(getFrom().getPartner() == to){
				getFrom().setHappiness(Happiness.VERY_HAPPY);
				getFrom().setExciting(true);
				getFrom().setForceFace(ImageCode.EXCITING.ordinal());
				getFrom().setBodyEventResMessage(MessagePool.getMessage(getFrom(), MessagePool.Action.LetsPlay), 30, true, false);
				to.setBodyEventResMessage(MessagePool.getMessage(to, MessagePool.Action.OKcome), 30, true, false);
			}
			//失敗時は泣いて逃げる
			else{
				getFrom().setHappiness(Happiness.VERY_SAD);
				getFrom().setForceFace(ImageCode.CRYING.ordinal());
				getFrom().setBodyEventResMessage(MessagePool.getMessage(getFrom(), MessagePool.Action.Heartbreak), 30, true, false);
				getFrom().runAway(to.getX(), to.getY());
				return true;
			}
		}
		else if(tick == 70){
			to.constraintDirection(getFrom(), true);
			getFrom().doSukkiri(to);
			return true;
		}
		tick++;
		return false;
	}

	//fのプロポーズのtによる判定。プロポーズはfがした側、tがされた側
	public boolean acceptPropose(Body f,Body t){
		//既婚
		if(t.getPartner()!=null)return false;
		//カビ発見
		if(t.findSick(f)) return false;
		//妊娠中個体
		if(f.hasBabyOrStalk())return false;
		//障害ゆん
		if(f.hasDisorder())return false;
		
		return true;
	}
	
	// イベント終了処理
	public void end(Body b) {
		getFrom().setCalm();
		getFrom().setCurrentEvent(null);
		to.setCurrentEvent(null);
		getFrom().setLockmove(false);
		to.setLockmove(false);
	}
}
