package org.simyukkuri.event.impl;

import java.util.Map;

import org.simyukkuri.Const;
import org.simyukkuri.draw.Translate;
import org.simyukkuri.entity.core.Entity;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.world.item.Toilet;
import org.simyukkuri.enums.Direction;
import org.simyukkuri.enums.EffectType;
import org.simyukkuri.enums.ImageCode;
import org.simyukkuri.enums.Intelligence;
import org.simyukkuri.enums.PublicRank;
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

/***************************************************
 * おかざりのないゆっくりへの攻撃イベント
 * protected Yukkuri from; // イベントを発した個体
 * protected Yukkuri to; // 攻撃対象
 * protected Entity target; // 未使用
 * protected int count; // 10
 */
public class HateNoOkazariEvent extends EventPacket {

	private static final long serialVersionUID = 7555245333944945758L;

	/**
	 * コンストラクタ.
	 */
	public HateNoOkazariEvent(Yukkuri fromBody, Yukkuri toBody, Entity targetObject, int count) {
		super(fromBody, toBody, targetObject, count);
	}

	public HateNoOkazariEvent() {

	}

	// 参加チェック
	// ここで各種チェックを行い、イベントへ参加するかを返す
	// また、イベント優先度も必要に応じて設定できる
	@Override
	public boolean checkEventResponse(Yukkuri body) {
		boolean accepted = false;

		priority = EventPriority.MIDDLE;
		// うんうん奴隷は参加しない
		if (body.getPublicRank() == PublicRank.UnunSlave)
			return false;
		// 善良は参加しない
		if (body.isSmart())
			return false;
		// 足りないゆは参加しない
		if (body.isIdiot())
			return false;
		Yukkuri targetBody = org.simyukkuri.util.YukkuriLookup.getYukkuriById(getTo());
		if (targetBody == null)
			return false;
		// 自分が賢い場合はおかざりがなくても家族を認識して参加しない
		if (body.getIntelligence() == Intelligence.WISE) {
			if (targetBody.isParent(body) || targetBody.isPartner(body) || body.isParent(targetBody)
					|| body.isPartner(targetBody))
				return false;
		}
		// 死体、睡眠、皮なし、目無し、非ゆっくり症は参加しない
		if (!body.canEventResponse()) {
			return false;
		}

		// 自分が通常種で相手が捕食種の場合は参加しない
		if (!body.isPredatorType() && targetBody.isPredatorType()) {
			return false;
		}

		// 自分がお飾りあり、健康で動ける状況にあるなら参加チェック
		if (body.hasOkazari() && !body.isDamaged() && !body.isDontMove()) {
			// ドゲスは参加
			if (body.isVeryRude()) {
				accepted = true;
			} else {
				// ゲス、普通は相手が瀕死じゃなければ参加
				if (!targetBody.isDamaged()) {
					if (body.isRude() || GameRandom.nextBoolean())
						accepted = true;
				}
			}
		}
		// 相手との間に壁があればスキップ
		if (Barrier.acrossBarrier(body.getX(), body.getY(), targetBody.getX(), targetBody.getY(),
				Barrier.MAP_BODY[body.getAgeState().ordinal()] + Barrier.BARRIER_KEKKAI)) {
			return false;
		}

		if (accepted) {
			Yukkuri sourceBody = org.simyukkuri.util.YukkuriLookup.getYukkuriById(getFrom());
			if (sourceBody != body) {
				body.setWorldEventResMessage(GameMessages.getMessage(body, MessagePool.Action.HateYukkuri),
						Const.HOLDMESSAGE,
						true, false);
			}
		}
		return accepted;
	}

	// イベント開始動作
	@Override
	public void start(Yukkuri body) {
		Yukkuri targetBody = org.simyukkuri.util.YukkuriLookup.getYukkuriById(getTo());
		if (targetBody == null)
			return;
		int collisionX = YukkuriLogic.calcCollisionX(body, targetBody);
		body.moveToEvent(this, targetBody.getX() + collisionX, targetBody.getY());
	}

	// 毎フレーム処理
	// UpdateState.ABORTを返すとイベント終了
	@Override
	public UpdateState update(Yukkuri body) {
		Yukkuri targetBody = org.simyukkuri.util.YukkuriLookup.getYukkuriById(getTo());
		// 相手が消えてしまったらイベント中断
		if (targetBody == null || targetBody.isRemoved())
			return UpdateState.ABORT;
		// 相手に追いつけないケースがあるため、一定距離まで近づいたら相手を呼び止める
		if (Translate.distance(body.getX(), body.getY(), targetBody.getX(), targetBody.getY()) < 2500) {
			targetBody.stay();
		}
		int collisionX = YukkuriLogic.calcCollisionX(body, targetBody);
		body.moveToEvent(this, targetBody.getX() + collisionX, targetBody.getY());
		return null;
	}

	// イベント目標に到着した際に呼ばれる
	// trueを返すとイベント終了
	@Override
	public boolean execute(Yukkuri body) {
		Yukkuri targetBody = org.simyukkuri.util.YukkuriLookup.getYukkuriById(getTo());
		// 相手が残っていたら攻撃
		if (targetBody != null && !targetBody.isDead() && !targetBody.isRemoved() && targetBody.getZ() < 5) {
			// うんうん奴隷ではない場合
			if (targetBody.getPublicRank() != PublicRank.UnunSlave) {
				boolean hasSlaveToilet = false;
				for (Map.Entry<Integer, Toilet> entry : GameWorld.get().getCurrentMap().getToilet().entrySet()) {
					// うんうん奴隷用トイレがあるか
					if (entry.getValue().isForSlave()) {
						hasSlaveToilet = true;
						break;
					}
				}
				// うんうん奴隷用トイレがある場合
				if (hasSlaveToilet) {
					targetBody.setPublicRank(PublicRank.UnunSlave); // うんうんどれい認定
					targetBody.getFavoriteItems().clear();
					Yukkuri partnerBody = org.simyukkuri.util.YukkuriLookup.getYukkuriById(body.getPartner());
					if (partnerBody != null) {
						// うんうんどれいになるようなくずとは りこんっ！だよ！！
						body.setPartner(-1);
						partnerBody.setPartner(-1);
					}
					body.setWorldEventResMessage(GameMessages.getMessage(body, MessagePool.Action.EngageUnunSlave),
							Const.HOLDMESSAGE, true, false);
				} else {
					body.setWorldEventResMessage(GameMessages.getMessage(body, MessagePool.Action.HateYukkuri),
							Const.HOLDMESSAGE, true, false);
				}
			} else {
				body.setWorldEventResMessage(GameMessages.getMessage(body, MessagePool.Action.HateYukkuri),
						Const.HOLDMESSAGE,
						true, false);
			}

			if (body.getDirection() == Direction.LEFT) {
				GameView.addEffect(EffectType.HIT, body.getX() - 10, body.getY(), 0,
						0, 0, 0, false, 500, 1, true, false, true);
			} else {
				GameView.addEffect(EffectType.HIT, body.getX() + 10, body.getY(), 0, 0, 0, 0, true,
						500, 1, true, false, true);
			}

			// 瀕死かつたりないゆでない場合は攻撃されないで見逃される
			if (!targetBody.isDamagedHeavily() || targetBody.isIdiot()) {
				targetBody.strikeByYukkuri(body, this, true);
			}
			body.setForceFace(ImageCode.PUFF.ordinal());
			body.addStress(-800);
		}
		return true;
	}

	@Override
	public String toString() {
		return GameText.read("event_noacc");
	}
}
