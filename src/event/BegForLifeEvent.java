package src.event;

import java.util.Random;

import src.attachment.Fire;
import src.base.Body;
import src.base.EventPacket;
import src.base.Obj;
import src.enums.CriticalDamegeType;
import src.enums.Happiness;
import src.enums.ImageCode;
import src.enums.Intelligence;
import src.system.MessagePool;

/***************************************************
	命乞いイベント
	protected Body from;			// イベントを発した個体
	protected Body to;				// 攻撃対象
	protected Obj target;			// 未使用
	protected int count;			// 10
*/
public class BegForLifeEvent extends EventPacket implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	Random rnd = new Random();
	private int roop = 0;
	private int roop2 = 0;
	private int roop3 = 0;


	int tick = 0 ;
	private int wait = 0 ;

	public BegForLifeEvent(Body f, Body t, Obj tgt, int cnt) {
		super(f, t, tgt, cnt);
	}

	// 参加チェック
	// ここで各種チェックを行い、イベントへ参加するかを返す
	// また、イベント優先度も必要に応じて設定できる
	public boolean checkEventResponse(Body b) {

		priority = EventPriority.HIGH;
		if(b == getFrom()  && !b.isUnBirth())return true;
		return false;
	}

	// イベント開始動作
	public void start(Body b) {
	}

	// 毎フレーム処理
	// UpdateState.ABORTを返すとイベント終了
	public UpdateState update(Body b) {
		if(b.isTalking()){
//			b.setBodyEventResMessage(MessagePool.getMessage(b, MessagePool.Action.ApologyToHuman), 20, false, true);
			return null;
		}
		if(b.getDamage() ==0 ){
				roop = 0;
				roop2 = 0;
		}
		if(tick == 0) {
			b.wakeup();
			// 興奮状態解除
			b.setCalm();
			b.stayPurupuru(5);
			b.setForceFace(ImageCode.VAIN.ordinal());
//			b.setForceFace(ImageCode.FEAR.ordinal());
			roop = rnd.nextInt(5)+5;
			roop2 = rnd.nextInt(10)+8;
			roop3 = rnd.nextInt(3)+1;
		}
		else if(tick >= 7 && roop !=0 && roop2 != 0&& roop3 != 0) {
			b.stay(30);
			b.setForceFace(ImageCode.CRYING.ordinal());
			b.setBegging(true);
			b.setBodyEventResMessage(MessagePool.getMessage(b, MessagePool.Action.ApologyToHuman), 20, false,  rnd.nextBoolean());
			roop --;
		}

		else if(roop == 0 && roop2 != 0&& roop3 != 0) {
			// 反応する
			b.stay(80);
			b.setBodyEventResMessage(MessagePool.getMessage(b, MessagePool.Action.BegForLife), 20, false, rnd.nextBoolean());
			b.setHappiness(Happiness.VERY_SAD);
			b.setForceFace(ImageCode.CRYING.ordinal());
			// なつき度設定
			b.addLovePlayer(-10);
			roop2 --;
			wait = 0;
		}
		else if(wait == 50 && roop == 0 && roop2 == 0 && roop3 != 0) {
			b.setBegging(false);
			//着火状態か足が破れてる状態で見逃してもらう
			if(b. getAttachmentSize(Fire.class) != 0 || b.getCriticalDamegeType() == CriticalDamegeType.CUT){
				b.setBodyEventResMessage(MessagePool.getMessage(b, MessagePool.Action.ThanksHuman2), 25, true, false);
				b.setHappiness(Happiness.VERY_SAD);
				b.setForceFace(ImageCode.CRYING.ordinal());
				return UpdateState.ABORT;
			}
			//ダメージ状態で見逃してもらう
			if(b.isDamaged() ){
				b.setBodyEventResMessage(MessagePool.getMessage(b, MessagePool.Action.ThanksHuman2), 25, true, false);
				b.setHappiness(Happiness.SAD);
				b.setForceFace(ImageCode.TIRED.ordinal());
				//増長
				switch(b.getAttitude()) {
					case VERY_NICE:
						b.addStress(b.getStressLimit()/30);
						b.plusAttitude(10);
						break;
					case NICE:
						b.addStress(b.getStressLimit()/20);
						b.plusAttitude(5);
						break;
					case AVERAGE:
						b.addStress(b.getStressLimit()/20);
						//b.plusAttitude(0);
						break;
					case SHITHEAD:
					case SUPER_SHITHEAD:
						b.addStress(b.getStressLimit()/10);
						if(b.getIntelligence() == Intelligence.FOOL)b.plusAttitude(-30);
						else b.plusAttitude(-20);
						break;
					default:
						break;
				}
			}

			//命乞い成功
			else{
				//賢くないゲス
				if(b.isRude() && b.getIntelligence() != Intelligence.WISE){
					b.setBodyEventResMessage(MessagePool.getMessage(b, MessagePool.Action.ThanksHuman), 25, true, true);
					b.setHappiness(Happiness.VERY_HAPPY);
					b.setForceFace(ImageCode.RUDE.ordinal());
				}
				else{
					b.setBodyEventResMessage(MessagePool.getMessage(b, MessagePool.Action.ThanksHuman), 25, true, true);
					b.setHappiness(Happiness.VERY_HAPPY);
					b.setForceFace(ImageCode.SMILE.ordinal());
				}

				//増長
				switch(b.getAttitude()) {
					case VERY_NICE:
						b.addStress(-b.getStressLimit()/20);
						b.plusAttitude(10);
						break;
					case NICE:
						b.addStress(-b.getStressLimit()/10);
						//b.plusAttitude(0);
						break;
					case AVERAGE:
						b.addStress(-b.getStressLimit()/5);
						b.plusAttitude(-30);
						break;
					case SHITHEAD:
					case SUPER_SHITHEAD:
						b.addStress(-b.getStressLimit()/3);
						b.plusAttitude(-50);
						break;
					default:
						break;
				}
			}
			roop3 --;
			wait = 0;
		}
		else if(wait == 30 && roop == 0 && roop2 == 0 && roop3 == 0) {
			//独り言ちる
			b.setMessage(MessagePool.getMessage(b, MessagePool.Action.Monologue),true);
			return UpdateState.ABORT;
		}
		tick++;
		wait++;
		return null;
	}

	// イベント目標に到着した際に呼ばれる
	// trueを返すとイベント終了
	public boolean execute(Body b) {
		return true;
	}

	//もしもの後始末
	@Override
	public void end(Body b) {
		b.setBegging(false);
		return;
	}
}
