package org.simyukkuri.event.impl;

import org.simyukkuri.Const;
import org.simyukkuri.draw.Translate;
import org.simyukkuri.entity.core.Entity;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.enums.Direction;
import org.simyukkuri.enums.EffectType;
import org.simyukkuri.enums.Happiness;
import org.simyukkuri.enums.ImageCode;
import org.simyukkuri.event.EventPacket;
import org.simyukkuri.logic.BodyLogic;
import org.simyukkuri.system.MessagePool;
import org.simyukkuri.util.GameMessages;
import org.simyukkuri.util.GameRandom;
import org.simyukkuri.util.GameText;
import org.simyukkuri.util.GameView;

/***************************************************
 * ゆっくりが攻撃されたときの反撃イベント
 * protected Yukkuri from; // イベントを発した個体
 * protected Yukkuri to; // 攻撃対象
 * protected Entity target; // 未使用
 * protected int count; // 1
 */
public class RevengeAttackEvent extends EventPacket {

	private static final long serialVersionUID = -7412180348011586698L;

	/**
	 * コンストラクタ.
	 */
	public RevengeAttackEvent(Yukkuri f, Yukkuri t, Entity tgt, int cnt) {
		super(f, t, tgt, cnt);
	}

	public RevengeAttackEvent() {

	}

	// 参加チェック
	@Override
	public boolean checkEventResponse(Yukkuri body) {
		priority = EventPriority.HIGH;
		// これは特殊な扱いをするイベントで先に条件をチェックしてから
		// 自分自身のリストに登録するので無条件にtrue
		return true;
	}

	// イベント開始動作
	@Override
	public void start(Yukkuri body) {
		Yukkuri targetBody = org.simyukkuri.util.BodyRegistry.getBodyInstance(getTo());
		body.setToFood(false);
		body.setToBed(false);
		body.setToShit(false);
		body.setToSteal(false);
		body.setToSukkiri(false);
		body.setToTakeout(true);
		body.setWakeUpTime(body.getAge());// 眠気が覚める
		if (targetBody != null) {
			int colX = BodyLogic.calcCollisionX(body, targetBody);
			body.moveToEvent(this, targetBody.getX() + colX, targetBody.getY());
		}
	}

	// 毎フレーム処理
	// UpdateState.ABORTを返すとイベント終了
	@Override
	public UpdateState update(Yukkuri body) {
		Yukkuri targetBody = org.simyukkuri.util.BodyRegistry.getBodyInstance(getTo());
		// 相手が消えてしまったらイベント中断
		if (targetBody == null || targetBody.isRemoved() || targetBody.isTaken())
			return UpdateState.ABORT;
		// 相手に追いつけないケースがあるため、一定距離まで近づいたら相手を呼び止める
		if (Translate.distance(body.getX(), body.getY(), targetBody.getX(), targetBody.getY()) < 2500) {
			targetBody.stay();
		}
		int colX = BodyLogic.calcCollisionX(body, targetBody);
		body.moveToEvent(this, targetBody.getX() + colX, targetBody.getY());
		return null;
	}

	// イベント目標に到着した際に呼ばれる
	// trueを返すとイベント終了
	@Override
	public boolean execute(Yukkuri body) {
		// 動けない場合と、ランダムであきらめる
		if (body.isDontMove() || GameRandom.nextInt(50) == 0) {
			body.setMessage(GameMessages.getMessage(body, MessagePool.Action.LamentNoYukkuri), 40, true, true);
			body.setHappiness(Happiness.SAD);
			return true;
		}
		Yukkuri targetBody = org.simyukkuri.util.BodyRegistry.getBodyInstance(getTo());
		// 相手が残っていたら攻撃
		if (targetBody != null && !targetBody.isRemoved() && targetBody.getZ() < 5) {
			body.setWorldEventResMessage(GameMessages.getMessage(body, MessagePool.Action.RevengeAttack),
					Const.HOLDMESSAGE,
					true, false);
			if (body.getDirection() == Direction.LEFT) {
				GameView.addEffect(EffectType.HIT, body.getX() - 10, body.getY(), 0,
						0, 0, 0, false, 500, 1, true, false, true);
			} else {
				GameView.addEffect(EffectType.HIT, body.getX() + 10, body.getY(), 0,
						0, 0, 0, true, 500, 1, true, false, true);
			}
			body.setForceFace(ImageCode.PUFF.ordinal());
			targetBody.strikeByYukkuri(body, this, false);
			body.addStress(-500);
		}
		return true;
	}

	@Override
	public String toString() {
		return GameText.read("event_revenge");
	}
}
