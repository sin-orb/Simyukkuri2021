package src.event;
import src.util.GameMessages;
import src.util.GameText;

import src.SimYukkuri;
import src.util.GameRandom;
import src.base.Body;
import src.event.EventPacket;
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
 * 空中捕食イベント
 * protected Body from; // イベントを発した個体
 * protected Body to; // 捕食対象
 * protected Obj target; // 未使用
 * protected int count; // 1
 */
public class FlyingEatEvent extends EventPacket {

	private static final long serialVersionUID = -5535956926516784919L;
	private static final int[] ofsZ = { 2, 0, -5 };
	int tick = 0;

	/**
	 * コンストラクタ.
	 */
	public FlyingEatEvent(Body f, Body t, Obj tgt, int cnt) {
		super(f, t, tgt, cnt);
	}

	public FlyingEatEvent() {

	}

	public int getTick() {
		return tick;
	}

	public void setTick(int tick) {
		this.tick = tick;
	}

	// 参加チェック
	@Override
	public boolean checkEventResponse(Body body) {
		priority = EventPriority.HIGH;
		return true;
	}

	// イベント開始動作
	@Override
	public void start(Body body) {
		Body targetBody = src.util.BodyRegistry.getBodyInstance(getTo());
		if (targetBody == null)
			return;
		body.setToBed(false);
		body.setToFood(false);
		body.setToShit(false);
		body.setToSteal(false);
		body.setToSukkiri(false);
		body.setToTakeout(true);
		body.moveToEvent(this, body.getX(), body.getY(), Translate.getFlyHeightLimit());
		body.setWakeUpTime(body.getAge());// 眠気が覚める
		targetBody.setParentLinkId(body.objId);
	}

	// 毎フレーム処理
	// UpdateState.ABORTを返すとイベント終了
	@Override
	public UpdateState update(Body body) {
		Body targetBody = src.util.BodyRegistry.getBodyInstance(getTo());
		// 相手が消えてしまったらイベント中断
		if (targetBody == null || targetBody.isRemoved()) {
			// to.setParentLinkId(null);
			return UpdateState.ABORT;
		}
		// 相手が捕まれたらイベント中断
		if (targetBody.isGrabbed()) {
			// to.setParentLinkId(null);
			return UpdateState.ABORT;
		}
		/*
		 * // 相手が死んだらイベント中断
		 * if(to.dead) {
		 * to.parentLinkId = null);
		 * return UpdateState.ABORT;
		 * }
		 */
		// 相手の座標を縛る
		targetBody.setCalcX(body.getX());
		targetBody.setCalcY(body.getY() + 1);
		targetBody.setCalcZ(body.getZ() + ofsZ[targetBody.getBodyAgeState().ordinal()]);

		// 高度に達してたらexecuteへ
		if (Math.abs(body.getZ() - Translate.getFlyHeightLimit()) < 3)
			return UpdateState.FORCE_EXEC;
		return null;
	}

	// イベント目標に到着した際に呼ばれる
	// trueを返すとイベント終了
	@Override
	public boolean execute(Body body) {
		Body targetBody = src.util.BodyRegistry.getBodyInstance(getTo());
		if (targetBody == null)
			return true;
		// 相手が消えてしまったらイベント中断
		if (targetBody.isRemoved()) {
			// to.setParentLinkId(null);
			return true;
		}
		// 相手が捕まれたらイベント中断
		if (targetBody.isGrabbed()) {
			// to.setParentLinkId(null);
			return true;
		}

		tick++;
		if (tick == 20) {
			tick = 0;
			FoodLogic.eatFood(body, Food.FoodType.BODY, Math.min(body.getEatAmount(), targetBody.getAnkoAmount()));
			targetBody.eatBody(Math.min(body.getEatAmount(), targetBody.getAnkoAmount()));
			if (targetBody != null && targetBody.isSick() && GameRandom.nextBoolean())
				body.addSickPeriod(100);
			if (targetBody != null && targetBody.isCrushed()) {
				// to.setParentLinkId(null);
				return true;
			} else if (targetBody != null && targetBody.isDead()) {
				targetBody.setMessage(GameMessages.getMessage(targetBody, MessagePool.Action.Dead));
				if (body.getBodyRank() != BodyRank.KAIYU || body.isRude()) {
					// to.setParentLinkId(null);
					return true;
				}
			} else {
				if (body.isFull()) {
					// うー。おなかいっぱいだからもういらないんだどー。ぽいするどー。
					body.setMessage(GameMessages.getMessage(body, MessagePool.Action.POI));
					// to.setParentLinkId(null);
					return true;
				}
				if (targetBody != null && targetBody.isNotNYD()) {
					targetBody.setMessage(GameMessages.getMessage(targetBody, MessagePool.Action.EatenByBody2));
					targetBody.setHappiness(Happiness.VERY_SAD);
					targetBody.setForceFace(ImageCode.PAIN.ordinal());
				}
			}
		}
		return false;
	}

	// イベント終了処理
	@Override
	public void end(Body body) {
		Body targetBody = src.util.BodyRegistry.getBodyInstance(getTo());
		if (targetBody != null)
			targetBody.setParentLinkId(-1);
	}

	@Override
	public String toString() {
		return GameText.read("event_eatinair");
	}
}
