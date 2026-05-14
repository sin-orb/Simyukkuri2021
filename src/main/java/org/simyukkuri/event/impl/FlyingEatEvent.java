package org.simyukkuri.event.impl;

import org.simyukkuri.draw.Translate;
import org.simyukkuri.entity.core.Entity;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.world.item.Food;
import org.simyukkuri.enums.YukkuriRank;
import org.simyukkuri.enums.Happiness;
import org.simyukkuri.enums.ImageCode;
import org.simyukkuri.event.EventPacket;
import org.simyukkuri.event.EventPacket.EventPriority;
import org.simyukkuri.event.EventPacket.UpdateState;
import org.simyukkuri.logic.FoodLogic;
import org.simyukkuri.system.MessagePool;
import org.simyukkuri.util.GameMessages;
import org.simyukkuri.util.GameRandom;
import org.simyukkuri.util.GameText;

/***************************************************
 * 空中捕食イベント
 * protected Yukkuri from; // イベントを発した個体
 * protected Yukkuri to; // 捕食対象
 * protected Entity target; // 未使用
 * protected int count; // 1
 */
public class FlyingEatEvent extends EventPacket {

	private static final long serialVersionUID = -5535956926516784919L;
	private static final int[] ofsZ = { 2, 0, -5 };
	int tick = 0;

	/**
	 * コンストラクタ.
	 */
	public FlyingEatEvent(Yukkuri f, Yukkuri t, Entity tgt, int cnt) {
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
	public boolean checkEventResponse(Yukkuri body) {
		priority = EventPriority.HIGH;
		return true;
	}

	// イベント開始動作
	@Override
	public void start(Yukkuri body) {
		Yukkuri targetBody = org.simyukkuri.util.YukkuriLookup.getYukkuriById(getTo());
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
	public UpdateState update(Yukkuri body) {
		Yukkuri targetBody = org.simyukkuri.util.YukkuriLookup.getYukkuriById(getTo());
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
		targetBody.setCalcZ(body.getZ() + ofsZ[targetBody.getAgeState().ordinal()]);

		// 高度に達してたらexecuteへ
		if (Math.abs(body.getZ() - Translate.getFlyHeightLimit()) < 3)
			return UpdateState.FORCE_EXEC;
		return null;
	}

	// イベント目標に到着した際に呼ばれる
	// trueを返すとイベント終了
	@Override
	public boolean execute(Yukkuri body) {
		Yukkuri targetBody = org.simyukkuri.util.YukkuriLookup.getYukkuriById(getTo());
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
			targetBody.eatYukkuri(Math.min(body.getEatAmount(), targetBody.getAnkoAmount()));
			if (targetBody != null && targetBody.isSick() && GameRandom.nextBoolean())
				body.addSickPeriod(100);
			if (targetBody != null && targetBody.isCrushed()) {
				// to.setParentLinkId(null);
				return true;
			} else if (targetBody != null && targetBody.isDead()) {
				targetBody.setMessage(GameMessages.getMessage(targetBody, MessagePool.Action.Dead));
				if (body.getRank() != YukkuriRank.KAIYU || body.isRude()) {
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
	public void end(Yukkuri body) {
		Yukkuri targetBody = org.simyukkuri.util.YukkuriLookup.getYukkuriById(getTo());
		if (targetBody != null)
			targetBody.setParentLinkId(-1);
	}

	@Override
	public String toString() {
		return GameText.read("event_eatinair");
	}
}
