package src.event;

import src.SimYukkuri;
import src.base.Body;
import src.base.EventPacket;
import src.base.Obj;
import src.draw.Translate;
import src.enums.BodyRank;
import src.enums.Happiness;
import src.enums.ImageCode;
import src.item.Food;
import src.logic.FoodLogic;
import src.system.MessagePool;
import src.system.ResourceUtil;


/***************************************************
	空中捕食イベント
	protected Body from;			// イベントを発した個体
	protected Body to;				// 捕食対象
	protected Obj target;			// 未使用
	protected int count;			// 1
*/
public class FlyingEatEvent extends EventPacket implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private static final int[] ofsZ = {2, 0, -5};
	int tick = 0;
	/**
	 * コンストラクタ.
	 */
	public FlyingEatEvent(Body f, Body t, Obj tgt, int cnt) {
		super(f, t, tgt, cnt);
	}

	// 参加チェック
	@Override
	public boolean checkEventResponse(Body b) {
		priority = EventPriority.HIGH;
		return true;
	}

	// イベント開始動作
	@Override
	public void start(Body b) {
		b.setToFood(false);
		b.setToBed(false);
		b.setToShit(false);
		b.setToSteal(false);
		b.setToSukkiri(false);
		b.setToTakeout(true);
		b.moveToEvent(this, b.getX(), b.getY(),  Translate.getFlyHeightLimit());
		b.setWakeUpTime(b.getAge());//眠気が覚める
		to.setLinkParent(b);
	}

	// 毎フレーム処理
	// UpdateState.ABORTを返すとイベント終了
	@Override
	public UpdateState update(Body b) {
		// 相手が消えてしまったらイベント中断
		if(to.isRemoved()) {
			//to.setLinkParent(null);
			return UpdateState.ABORT;
		}
		// 相手が捕まれたらイベント中断
		if(to.isGrabbed()) {
			//to.setLinkParent(null);
			return UpdateState.ABORT;
		}
		/*// 相手が死んだらイベント中断
		if(to.dead) {
			to.linkParent = null);
			return UpdateState.ABORT;
		}*/
		// 相手の座標を縛る
		to.setX(b.getX());
		to.setY(b.getY() + 1);
		to.setZ(b.getZ() + ofsZ[to.getBodyAgeState().ordinal()]);

		// 高度に達してたらexecuteへ
		if(Math.abs(b.getZ() - Translate.getFlyHeightLimit()) < 3) return UpdateState.FORCE_EXEC;
		return null;
	}

	// イベント目標に到着した際に呼ばれる
	// trueを返すとイベント終了
	@Override
	public boolean execute(Body b) {
		// 相手が消えてしまったらイベント中断
		if(to.isRemoved()) {
			//to.setLinkParent(null);
			return true;
		}
		// 相手が捕まれたらイベント中断
		if(to.isGrabbed()) {
			//to.setLinkParent(null);
			return true;
		}

		tick++;
		if(tick == 20) {
			tick = 0;
			FoodLogic.eatFood(b, Food.FoodType.BODY, Math.min(b.getEatAmount(), to.getBodyAmount()));
			to.eatBody(Math.min(b.getEatAmount(), to.getBodyAmount()));
			if (to.isSick() && SimYukkuri.RND.nextBoolean()) b.addSickPeriod(100);
			if(to.isCrushed()){
				//to.setLinkParent(null);
				return true;
			}
			else if(to.isDead()) {
				to.setMessage(MessagePool.getMessage(to, MessagePool.Action.Dead));
				if(b.getBodyRank()!=BodyRank.KAIYU || b.isRude()){
					//to.setLinkParent(null);
					return true;
				}
			}
			else {
				if (b.isFull()) {
					// うー。おなかいっぱいだからもういらないんだどー。ぽいするどー。
					b.setMessage(MessagePool.getMessage(b, MessagePool.Action.POI));
					//to.setLinkParent(null);
					return true;
				}
				if( to.isNotNYD() ){
					to.setMessage(MessagePool.getMessage(to, MessagePool.Action.EatenByBody2));
					to.setHappiness(Happiness.VERY_SAD);
					to.setForceFace(ImageCode.PAIN.ordinal());
				}
			}
		}
		return false;
	}

	// イベント終了処理
	@Override
	public void end(Body b) {
		to.setLinkParent(null);
	}
	
	@Override
	public String toString() {
		return ResourceUtil.getInstance().read("event_eatinair");
	}
}