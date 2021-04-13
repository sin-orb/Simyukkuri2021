package src.event;

import java.util.Random;

import src.SimYukkuri;
import src.base.Body;
import src.base.EventPacket;
import src.base.Obj;
import src.draw.Translate;
import src.enums.Attitude;
import src.enums.CoreAnkoState;
import src.enums.Direction;
import src.enums.Happiness;
import src.enums.ImageCode;
import src.system.MessagePool;

/***************************************************
	死体食事中におかざりがもどってきたイベント
	protected Body from;			// 食べてる側
	protected Body to;				// 食べられてる側
	protected Obj target;			// 未使用
	protected int count;			// 30
*/
public class EatBodyEvent extends EventPacket implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	Random rnd = new Random();
	int tick = 0;

	public EatBodyEvent(Body f, Body t, Obj tgt, int cnt) {
		super(f, t, tgt, cnt);
	}

	// 参加チェック
	// ここで各種チェックを行い、イベントへ参加するかを返す
	// また、イベント優先度も必要に応じて設定できる
	public boolean checkEventResponse(Body b) {
		if(getFrom() == b && b.canEventResponse() && b.getAttitude() != Attitude.SUPER_SHITHEAD)return true;
		return false;
	}

	// イベント開始動作
	public void start(Body b) {
		// ゆっくりが隠れないように死体の奥に出る
		b.moveToEvent(this, to.getX() + 5, to.getY() + 4);
	}

	// イベント目標に到着した際に呼ばれる
	// trueを返すとイベント終了
	public boolean execute(Body b) {
		// 複数の動作を順次行うのでtickで管理
		if(tick == 0) {
			// 固まる
			b.lookTo(to.getX(), to.getY());
			b.setLockmove(true);
			b.setMessage(null, true);
			b.setForceFace(ImageCode.NORMAL.ordinal());
			b.stay();
		}
		else if(tick == 10) {
			// 驚く
			b.lookTo(to.getX(), to.getY());
			b.setLockmove(false);
			b.setForceFace(ImageCode.SURPRISE.ordinal());
			b.setBodyEventResMessage(MessagePool.getMessage(b, MessagePool.Action.Surprise), 52, true, false);
			b.stay();
		}
		else if(tick == 70) {
			// 吐く
			b.lookTo(to.getX(), to.getY());
			b.setForceFace(ImageCode.CRYING.ordinal());
			b.setBodyEventResMessage(MessagePool.getMessage(b, MessagePool.Action.Vomit), 62, true, false);
			int ofsX = Translate.invertX(b.getCollisionX()>>1, b.getY());
			if(b.getDirection() == Direction.LEFT) ofsX = -ofsX;
			SimYukkuri.mypane.terrarium.addVomit(b.getX() + ofsX, b.getY(), b.getZ(), b, b.getShitType());
			b.stay();
		}
		else if(tick == 120) {
			// 善良ほどストレスを受ける
			switch(b.getAttitude()) {
				default:
					break;
				case VERY_NICE:
					b.addStress(5000);
					break;
				case NICE:
					b.addStress(3000);
					break;
				case AVERAGE:
					b.addStress(2000);
					break;
				case SHITHEAD:
					b.addStress(700);
					break;
			}
			if( b.geteCoreAnkoState() == CoreAnkoState.DEFAULT ){
				b.setHappiness(Happiness.VERY_SAD);
			}
			return true;
		}
		else {
			b.lookTo(to.getX(), to.getY());
			b.stay();
		}
		tick++;
		return false;
	}
	
	//もしもの時のために解除
	public void end(Body b) {
		b.setLockmove(false);
		return;
	}
}
