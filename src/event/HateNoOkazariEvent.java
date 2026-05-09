package src.event;
import src.util.GameView;
import src.util.GameMessages;
import src.util.GameText;

import java.util.Map;

import src.Const;
import src.SimYukkuri;
import src.util.GameRandom;
import src.util.GameWorld;
import src.base.Body;
import src.event.EventPacket;
import src.base.Obj;
import src.draw.Translate;
import src.enums.Direction;
import src.enums.EffectType;
import src.enums.ImageCode;
import src.enums.Intelligence;
import src.enums.PublicRank;
import src.field.impl.Barrier;
import src.item.Toilet;
import src.logic.BodyLogic;
import src.system.MessagePool;
import src.system.ResourceUtil;

/***************************************************
 * おかざりのないゆっくりへの攻撃イベント
 * protected Body from; // イベントを発した個体
 * protected Body to; // 攻撃対象
 * protected Obj target; // 未使用
 * protected int count; // 10
 */
public class HateNoOkazariEvent extends EventPacket {

	private static final long serialVersionUID = 7555245333944945758L;

	/**
	 * コンストラクタ.
	 */
	public HateNoOkazariEvent(Body fromBody, Body toBody, Obj targetObject, int count) {
		super(fromBody, toBody, targetObject, count);
	}

	public HateNoOkazariEvent() {

	}

	// 参加チェック
	// ここで各種チェックを行い、イベントへ参加するかを返す
	// また、イベント優先度も必要に応じて設定できる
	@Override
	public boolean checkEventResponse(Body body) {
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
		Body targetBody = src.util.BodyRegistry.getBodyInstance(getTo());
		if (targetBody == null)
			return false;
		// 自分が賢い場合はおかざりがなくても家族を認識して参加しない
		if (body.getIntelligence() == Intelligence.WISE) {
			if (targetBody.isParent(body) || targetBody.isPartner(body) || body.isParent(targetBody) || body.isPartner(targetBody))
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
			}
			else {
				// ゲス、普通は相手が瀕死じゃなければ参加
				if (!targetBody.isDamaged()) {
					if (body.isRude() || GameRandom.nextBoolean())
						accepted = true;
				}
			}
		}
		// 相手との間に壁があればスキップ
		if (Barrier.acrossBarrier(body.getX(), body.getY(), targetBody.getX(), targetBody.getY(),
				Barrier.MAP_BODY[body.getBodyAgeState().ordinal()] + Barrier.BARRIER_KEKKAI)) {
			return false;
		}

		if (accepted) {
			Body sourceBody = src.util.BodyRegistry.getBodyInstance(getFrom());
			if (sourceBody != body) {
				body.setWorldEventResMessage(GameMessages.getMessage(body, MessagePool.Action.HateYukkuri), Const.HOLDMESSAGE,
						true, false);
			}
		}
		return accepted;
	}

	// イベント開始動作
	@Override
	public void start(Body body) {
		Body targetBody = src.util.BodyRegistry.getBodyInstance(getTo());
		if (targetBody == null)
			return;
		int collisionX = BodyLogic.calcCollisionX(body, targetBody);
		body.moveToEvent(this, targetBody.getX() + collisionX, targetBody.getY());
	}

	// 毎フレーム処理
	// UpdateState.ABORTを返すとイベント終了
	@Override
	public UpdateState update(Body body) {
		Body targetBody = src.util.BodyRegistry.getBodyInstance(getTo());
		// 相手が消えてしまったらイベント中断
		if (targetBody == null || targetBody.isRemoved())
			return UpdateState.ABORT;
		// 相手に追いつけないケースがあるため、一定距離まで近づいたら相手を呼び止める
		if (Translate.distance(body.getX(), body.getY(), targetBody.getX(), targetBody.getY()) < 2500) {
			targetBody.stay();
		}
		int collisionX = BodyLogic.calcCollisionX(body, targetBody);
		body.moveToEvent(this, targetBody.getX() + collisionX, targetBody.getY());
		return null;
	}

	// イベント目標に到着した際に呼ばれる
	// trueを返すとイベント終了
	@Override
	public boolean execute(Body body) {
		Body targetBody = src.util.BodyRegistry.getBodyInstance(getTo());
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
					Body partnerBody = src.util.BodyRegistry.getBodyInstance(body.getPartner());
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
				body.setWorldEventResMessage(GameMessages.getMessage(body, MessagePool.Action.HateYukkuri), Const.HOLDMESSAGE,
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
