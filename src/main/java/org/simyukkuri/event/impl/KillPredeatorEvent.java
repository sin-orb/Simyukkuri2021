package org.simyukkuri.event.impl;

import java.util.Map;

import org.simyukkuri.Const;
import org.simyukkuri.entity.core.Entity;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.enums.Direction;
import org.simyukkuri.enums.EffectType;
import org.simyukkuri.enums.ImageCode;
import org.simyukkuri.enums.PublicRank;
import org.simyukkuri.enums.YukkuriType;
import org.simyukkuri.event.EventPacket;
import org.simyukkuri.event.EventPacket.EventPriority;
import org.simyukkuri.event.EventPacket.UpdateState;
import org.simyukkuri.field.impl.Barrier;
import org.simyukkuri.logic.YukkuriLogic;
import org.simyukkuri.system.MessagePool;
import org.simyukkuri.util.GameMessages;
import org.simyukkuri.util.GameRandom;
import org.simyukkuri.util.GameText;
import org.simyukkuri.util.GameView;
import org.simyukkuri.util.GameWorld;

/**
 * 善良なゆっくりが主に参加し、ドスは捕食種がいる限り殺しに行く.
 */
public class KillPredeatorEvent extends RevengeAttackEvent {

	private static final long serialVersionUID = -3947180002572305098L;

	/**
	 * コンストラクタ.
	 * 
	 * @param f   イベントを発した個体
	 * @param t   攻撃対象の捕食種
	 * @param tgt 未使用
	 * @param cnt 10
	 */
	public KillPredeatorEvent(Yukkuri f, Yukkuri t, Entity tgt, int cnt) {
		super(f, t, tgt, cnt);
	}

	public KillPredeatorEvent() {

	}

	/**
	 * 参加チェック
	 * ここで各種チェックを行い、イベントへ参加するかを返す
	 * また、イベント優先度も必要に応じて設定できる
	 */
	@Override
	public boolean checkEventResponse(Yukkuri body) {
		setHighPriority();
		// 死体、睡眠、皮なし、目無しはスキップ
		if (!body.canEventResponse())
			return true;
		// 非ゆっくり症、針刺し状態はスキップ
		if (body.isNYD() || body.isNeedled())
			return false;
		boolean isNearPredator = false;
		// 全ゆっくりに対してチェック
		for (Map.Entry<Integer, Yukkuri> entry : GameWorld.get().getCurrentWorldState().getYukkuriRegistry().entrySet()) {
			Yukkuri predatorBody = entry.getValue();
			// 自分同士のチェックは無意味なのでスキップ
			if (predatorBody == body) {
				continue;
			}
			// 捕食種でないか、捕食種でも親子または姉妹であればスキップ
			if (!predatorBody.isPredatorType() || predatorBody.isParent(body) || body.isParent(predatorBody)
					|| body.isSister(predatorBody) || body.isElderSister(predatorBody)) {
				continue;
			}
			// 相手との間に壁があればスキップ
			if (Barrier.acrossBarrier(body.getX(), body.getY(), predatorBody.getX(), predatorBody.getY(),
					Barrier.BODY_BLOCK_FLAGS[body.getAgeState().ordinal()] + Barrier.BARRIER_KEKKAI)) {
				continue;
			}
			isNearPredator = true;
			break;
		}
		// 捕食種が近くにいない
		if (!isNearPredator) {
			return false;
		}
		body.clearActionsForEvent();
		return true;
	}

	/**
	 * 次のターゲットを探す.
	 * 
	 * @return 次のターゲット
	 */
	@Override
	public Yukkuri searchNextTarget() {
		Yukkuri nextTarget = null;
		for (Map.Entry<Integer, Yukkuri> entry : GameWorld.get().getCurrentWorldState().getYukkuriRegistry().entrySet()) {
			Yukkuri body = entry.getValue();
			if (body.isPredatorType()) {
				nextTarget = body;
				break;
			}
		}
		return nextTarget;
	}

	@Override
	public UpdateState update(Yukkuri body) {
		// ランダムで復讐を諦める
		if (GameRandom.nextInt(1000) == 0) {
			return UpdateState.ABORT;
		}
		Yukkuri from = org.simyukkuri.util.YukkuriLookup.getYukkuriById(getFrom());
		// 相手が消えてしまったら他の捕食種を捜索
		if (from.isRemoved() || from.isDead() || !from.isPredatorType()) {
			setFrom(searchNextTarget());
			from = org.simyukkuri.util.YukkuriLookup.getYukkuriById(getFrom());
			if (from == null)
				return UpdateState.ABORT;
		}

		body.setForceFace(ImageCode.PUFF.ordinal());
		int colX = YukkuriLogic.calcCollisionX(body, from);
		body.moveToEvent(this, from.getX() + colX, from.getY());
		if (body.getType() == YukkuriType.DOSMARISA ||
				(body.isAdult() && body.getPublicRank() != PublicRank.UNUN_SLAVE)) {
			Yukkuri target = searchNextTarget();
			setFrom(target);
			body.setWorldEventResMessage(GameMessages.getMessage(body, MessagePool.Action.RevengeForChild),
					Const.HOLDMESSAGE, true, false);
		}
		return null;
	}

	@Override
	public void start(Yukkuri body) {
		if (body.isNYD()) {
			return;
		}
		body.setAngry();
		Yukkuri from = org.simyukkuri.util.YukkuriLookup.getYukkuriById(getFrom());
		int colX = YukkuriLogic.calcCollisionX(body, from);
		body.moveToEvent(this, from.getX() + colX, from.getY());
	}

	@Override
	public boolean execute(Yukkuri body) {
		Yukkuri from = org.simyukkuri.util.YukkuriLookup.getYukkuriById(getFrom());
		// 相手が消えてしまったら他の捕食種を捜索
		if (from == null || from.isRemoved() || from.isDead()) {
			setFrom(searchNextTarget());
			from = org.simyukkuri.util.YukkuriLookup.getYukkuriById(getFrom());
			// 捕食種全滅でイベント終了
			if (from == null)
				return true;
			return false;
		}
		if (from.getZ() < 5) {
			body.setWorldEventResMessage(GameMessages.getMessage(body, MessagePool.Action.RevengeForChild),
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
			from.strikeByYukkuri(body, this, false);
			body.addStress(-300);
		}
		return false;
	}

	@Override
	public String toString() {
		return GameText.read("event_killremi");
	}
}
