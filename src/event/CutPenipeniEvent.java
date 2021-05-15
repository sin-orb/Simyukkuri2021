package src.event;

import java.util.Random;

import src.base.Body;
import src.base.EventPacket;
import src.base.Obj;
import src.enums.Happiness;
import src.enums.ImageCode;
import src.system.MessagePool;

/***************************************************
	ぺに切りの反応イベント
	protected Body from;			// イベントを発した個体
	protected Body to;				// 攻撃対象
	protected Obj target;			// 未使用
	protected int count;			// 10
*/
public class CutPenipeniEvent extends EventPacket implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	Random rnd = new Random();
	int tick = 0 ;
	/**
	 * コンストラクタ.
	 */
	public CutPenipeniEvent(Body f, Body t, Obj tgt, int cnt) {
		super(f, t, tgt, cnt);
	}

	// 参加チェック
	// ここで各種チェックを行い、イベントへ参加するかを返す
	// また、イベント優先度も必要に応じて設定できる
	@Override
	public boolean checkEventResponse(Body b) {

		priority = EventPriority.HIGH;
		if(b == getFrom())return true;
		return false;
	}

	// イベント開始動作
	@Override
	public void start(Body b) {
	}

	// 毎フレーム処理
	// UpdateState.ABORTを返すとイベント終了
	@Override
	public UpdateState update(Body b) {
		if(b.isUnBirth()){
			b.wakeup();
			// ぺにぺ二があれば切断
			b.setbPenipeniCutted(true);
			// 興奮状態解除
			b.setCalm();
			// レイパーじゃなくなる
			b.setRaper(false);
			// ダメージをくらう
			b.addDamage(50);
			b.setForceFace(ImageCode.CUTPENIPENI.ordinal());
			b.setHappiness(Happiness.VERY_SAD);
			b.setCanTalk(true);
			b.setBodyEventResMessage(MessagePool.getMessage(b, MessagePool.Action.Scream2), 50, true, true);
			b.setCanTalk(false);
			return UpdateState.FORCE_EXEC;
		}
		
		if(tick == 0) {
			b.wakeup();
			// ぺにぺ二があれば切断
			b.setbPenipeniCutted(true);
			// 興奮状態解除
			b.setCalm();
			// レイパーじゃなくなる
			b.setRaper(false);
			// 固まる
			b.stayPurupuru(40);
			// ダメージをくらう
			b.addDamage(50);
			b.setLockmove(true);
			b.setForceFace(ImageCode.CUTPENIPENI.ordinal());
		}
		else if(tick == 20) {
			// 驚く
			b.setLockmove(false);
			if( b.isNotNYD() ){
				b.setForceFace(ImageCode.SURPRISE.ordinal());
				if(rnd.nextInt(2)==0)b.setBodyEventResMessage(MessagePool.getMessage(b, MessagePool.Action.Scream2), 30, true, false);
				else b.setBodyEventResMessage(MessagePool.getMessage(b, MessagePool.Action.Surprise), 30, true, false);
			}
		}
		else if(tick == 40) {
			// 反応する
			if( b.isNotNYD() ){
				b.setBodyEventResMessage(MessagePool.getMessage(b, MessagePool.Action.PenipeniCutting), 50, true, true);
				b.setHappiness(Happiness.VERY_SAD);
				b.setForceFace(ImageCode.CRYING.ordinal());
				b.stay(30);
			}
			// なつき度設定
			b.addLovePlayer(-500);
			b.stay(20);
			// ゲスほどストレスを受ける
			switch(b.getAttitude()) {
				default:
					break;
				case VERY_NICE:
					b.addStress(b.getStressLimit()/10);
					break;
				case NICE:
					b.addStress(b.getStressLimit()/8);
					break;
				case AVERAGE:
					b.addStress(b.getStressLimit()/5);
					break;
				case SHITHEAD:
				case SUPER_SHITHEAD:
					b.addStress(b.getStressLimit()/3);
					break;
			}
		}
		else if(tick == 70) {
			if(rnd.nextBoolean()) b.doYunnyaa(true);
			return UpdateState.FORCE_EXEC;
		}
		tick++;
		return null;
	}

	// イベント目標に到着した際に呼ばれる
	// trueを返すとイベント終了
	@Override
	public boolean execute(Body b) {
		return true;
	}
	
	// イベント終了処理
	@Override
	public void end(Body b) {
		b.setCalm();
		b.setbPenipeniCutted(true);
		b.setHappiness(Happiness.VERY_SAD);
		b.setLockmove(false);
	}
	
	@Override
	public String toString() {
		return "ぺにぺにがぁあ！";
	}
}