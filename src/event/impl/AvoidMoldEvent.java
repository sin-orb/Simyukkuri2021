package src.event.impl;

import src.Const;
import src.entity.core.Entity;
import src.entity.core.living.yukkuri.Yukkuri;
import src.enums.Happiness;
import src.enums.ImageCode;
import src.enums.Intelligence;
import src.enums.PublicRank;
import src.event.EventPacket;
import src.field.impl.Barrier;
import src.logic.BodyLogic;
import src.system.MessagePool;
import src.util.GameMessages;
import src.util.GameRandom;
import src.util.GameText;

/***************************************************
 * かびたゆっくりへの反応イベント
 * protected Yukkuri from; // イベントを発した個体
 * protected Yukkuri to; // 攻撃対象
 * protected Entity target; // 未使用
 * protected int count; // 10
 */
public class AvoidMoldEvent extends EventPacket {

	private static final long serialVersionUID = -8441703224895176376L;

	/**
	 * コンストラクタ.
	 */
	public AvoidMoldEvent(Yukkuri f, Yukkuri t, Entity tgt, int cnt) {
		super(f, t, tgt, cnt);
	}

	public AvoidMoldEvent() {

	}

	/**
	 * 参加チェック
	 * ここで各種チェックを行い、イベントへ参加するかを返す
	 * また、イベント優先度も必要に応じて設定できる
	 */
	@Override
	public boolean checkEventResponse(Yukkuri body) {
		priority = EventPriority.MIDDLE;
		// うんうん奴隷は参加しない
		if (body.getPublicRank() == PublicRank.UnunSlave)
			return false;
		// 足りないゆは参加しない
		if (body.isIdiot())
			return false;
		// 非ゆっくり症は参加しない
		if (!body.canEventResponse())
			return false;
		// 相手との間に壁があればスキップ
		Yukkuri targetBody = src.util.BodyRegistry.getBodyInstance(getTo());
		if (targetBody != null && Barrier.acrossBarrier(body.getX(), body.getY(), targetBody.getX(), targetBody.getY(),
				Barrier.MAP_BODY[body.getBodyAgeState().ordinal()] + Barrier.BARRIER_KEKKAI)) {
			return false;
		}
		return true;
	}

	/**
	 * イベント開始動作
	 */
	@Override
	public void start(Yukkuri body) {
		Yukkuri targetBody = src.util.BodyRegistry.getBodyInstance(getTo());
		if (targetBody == null) {
			return;
		}
		int collisionX = BodyLogic.calcCollisionX(body, targetBody);
		body.moveToEvent(this, targetBody.getX() + collisionX, targetBody.getY());
	}

	/**
	 * 毎フレーム処理
	 * UpdateState.ABORTを返すとイベント終了
	 */
	@Override
	public UpdateState update(Yukkuri body) {
		// 相手が消えてしまったらイベント中断
		Yukkuri targetBody = src.util.BodyRegistry.getBodyInstance(getTo());
		if (targetBody == null)
			return UpdateState.ABORT;
		if (targetBody.isDead() || targetBody.isRemoved())
			return UpdateState.ABORT;
		targetBody.stay();
		int collisionX = BodyLogic.calcCollisionX(body, targetBody);
		body.moveToEvent(this, targetBody.getX() + collisionX, targetBody.getY());
		return null;
	}

	/**
	 * イベント目標に到着した際に呼ばれる
	 * trueを返すとイベント終了
	 */
	@Override
	public boolean execute(Yukkuri body) {
		Yukkuri targetBody = src.util.BodyRegistry.getBodyInstance(getTo());
		Yukkuri sourceBody = src.util.BodyRegistry.getBodyInstance(getFrom());
		if (sourceBody == null || targetBody == null) {
			return true;
		}
		// ドゲスの場合、楽しんで制裁
		if (sourceBody.isVeryRude()) {
			sourceBody.setBodyEventResMessage(GameMessages.getMessage(sourceBody, MessagePool.Action.HateMoldyYukkuri),
					Const.HOLDMESSAGE, true, false);
			targetBody.strikeByYukkuri(sourceBody, this, false);
			sourceBody.setForceFace(ImageCode.PUFF.ordinal());
			if (sourceBody.getIntelligence() == Intelligence.FOOL)
				sourceBody.addSickPeriod(100);
			return true;
		}

		// 自分が成体のばあい。ちゃんと制裁する
		if (sourceBody.isAdult()) {
			// 自分が成体で相手が家族なら嘆く
			if (!body.isTalking()) {
				// 共通処理
				body.setHappiness(Happiness.VERY_SAD);
				body.addMemories(-1);
				body.addStress(80);
				// 相手が子供か、又は番
				if (sourceBody.isParent(targetBody) || sourceBody.isPartner(targetBody)) {
					switch (sourceBody.getIntelligence()) {
						case FOOL:
							if (GameRandom.nextInt(5) == 0) {
								sourceBody.doPeropero(targetBody);
								return true;
							} else {
								saySadMessage(sourceBody, targetBody);
								return false;
							}
						case WISE:
							if (GameRandom.nextInt(5) == 0) {
								sayApologyMessage(sourceBody, targetBody);
								targetBody.strikeByYukkuri(sourceBody, this, false);
							}
							return false;
						default:
							if (GameRandom.nextInt(5) == 0) {
								sayApologyMessage(sourceBody, targetBody);
								targetBody.strikeByYukkuri(sourceBody, this, false);
								return true;
							} else {
								saySadMessage(sourceBody, targetBody);
								return false;
							}
					}
				}
				// 相手が親化、又は姉妹の場合
				else if (sourceBody.isFamily(targetBody)) {
					switch (sourceBody.getIntelligence()) {
						case FOOL:
							if (GameRandom.nextInt(5) == 0) {
								sourceBody.doPeropero(targetBody);
							} else {
								saySadMessage(sourceBody, targetBody);
							}
							return true;
						case WISE:
							if (GameRandom.nextInt(5) == 0) {
								sayApologyMessage(sourceBody, targetBody);
								targetBody.runAway(targetBody.getX(), targetBody.getY());
							}
							return true;
						default:
							if (GameRandom.nextInt(5) == 0) {
								sayApologyMessage(sourceBody, targetBody);
								targetBody.runAway(targetBody.getX(), targetBody.getY());
								return true;
							} else {
								saySadMessage(sourceBody, targetBody);
								return false;
							}
					}
				}
				// 家族でない場合
				else {
					sourceBody.setBodyEventResMessage(
							GameMessages.getMessage(sourceBody, MessagePool.Action.HateMoldyYukkuri),
							Const.HOLDMESSAGE, true, false);
					targetBody.strikeByYukkuri(sourceBody, this, false);
					sourceBody.setForceFace(ImageCode.PUFF.ordinal());
					if (sourceBody.getIntelligence() == Intelligence.FOOL)
						sourceBody.forceSetSick();
					return true;
				}
			}
		}

		// 子ゆ、赤ゆの場合。嘆くのみ
		else {
			// 共通処理
			body.setHappiness(Happiness.VERY_SAD);
			body.addStress(80);
			body.addMemories(-1);
			// かびてるのが親
			if (sourceBody.isChild(targetBody)) {
				body.addStress(70);
				switch (sourceBody.getIntelligence()) {
					case FOOL:
						if (GameRandom.nextInt(5) == 0) {
							sourceBody.doPeropero(targetBody);
							return false;
						} else {
							saySadMessage(sourceBody, targetBody);
							return false;
						}
					case WISE:
						if (GameRandom.nextInt(5) == 0) {
							sayApologyMessage(sourceBody, targetBody);
							targetBody.runAway(targetBody.getX(), targetBody.getY());
							return true;
						} else {
							saySadMessage(sourceBody, targetBody);
							return false;
						}
					default:
						if (GameRandom.nextInt(25) == 0) {
							sayApologyMessage(sourceBody, targetBody);
							targetBody.runAway(targetBody.getX(), targetBody.getY());
							return true;
						} else if (GameRandom.nextInt(5) == 0) {
							sourceBody.doPeropero(targetBody);
							return false;
						} else {
							saySadMessage(sourceBody, targetBody);
							return false;
						}
				}
			}
			// かびてるのが家族
			else if (targetBody.isFamily(sourceBody)) {
				switch (sourceBody.getIntelligence()) {
					case FOOL:
						if (GameRandom.nextInt(5) == 0) {
							sourceBody.doPeropero(targetBody);
							return false;
						} else {
							saySadMessage(sourceBody, targetBody);
							return false;
						}
					case WISE:
						if (GameRandom.nextInt(5) == 0) {
							sayApologyMessage(sourceBody, targetBody);
							targetBody.runAway(targetBody.getX(), targetBody.getY());
							return true;
						} else {
							saySadMessage(sourceBody, targetBody);
							return false;
						}
					default:
						if (GameRandom.nextInt(10) == 0) {
							sayApologyMessage(sourceBody, targetBody);
							targetBody.runAway(targetBody.getX(), targetBody.getY());
							return true;
						} else {
							saySadMessage(sourceBody, targetBody);
							return false;
						}
				}
			}
			// 家族でない場合
			else {
				sourceBody.setBodyEventResMessage(
						GameMessages.getMessage(sourceBody, MessagePool.Action.HateMoldyYukkuri),
						Const.HOLDMESSAGE, true, false);
				sourceBody.runAway(targetBody.getX(), targetBody.getY());
				sourceBody.setForceFace(ImageCode.PUFF.ordinal());
				if (sourceBody.getIntelligence() == Intelligence.FOOL)
					sourceBody.forceSetSick();
				return true;
			}
		}
		return true;
	}

	/**
	 * 悲しみのメッセージを言う
	 * 
	 * @param From イベント発生元
	 * @param To   イベント対象
	 */
	public void saySadMessage(Yukkuri From, Yukkuri To) {
		Yukkuri sourceBody = src.util.BodyRegistry.getBodyInstance(getFrom());
		if (sourceBody == null)
			return;
		String message = null;
		if (From.isParent(To)) {
			message = GameMessages.getMessage(sourceBody, MessagePool.Action.SadnessForMoldyChild);
		} else if (From.isPartner(To)) {
			message = GameMessages.getMessage(sourceBody, MessagePool.Action.SadnessForMoldyPartner);
		} else if (To.isParent(From)) {
			if (To.isFather(From))
				message = GameMessages.getMessage(sourceBody, MessagePool.Action.SadnessForMoldyFather);
			else
				message = GameMessages.getMessage(sourceBody, MessagePool.Action.SadnessForMoldyMother);
		} else if (To.isSister(From)) {
			if (To.getAge() >= From.getAge())
				message = GameMessages.getMessage(sourceBody, MessagePool.Action.SadnessForEldersister);
			else
				message = GameMessages.getMessage(sourceBody, MessagePool.Action.SadnessForMoldySister);
		}
		From.setBodyEventResMessage(message, Const.HOLDMESSAGE, true, GameRandom.nextBoolean());
	}

	/**
	 * 謝罪する.
	 * 
	 * @param From イベント発生元
	 * @param To   イベント対象
	 */
	public void sayApologyMessage(Yukkuri From, Yukkuri To) {
		Yukkuri sourceBody = src.util.BodyRegistry.getBodyInstance(getFrom());
		if (sourceBody == null)
			return;
		String message = null;
		if (From.isParent(To)) {
			message = GameMessages.getMessage(sourceBody, MessagePool.Action.ApologyToChild);
		} else if (To.isFamily(From)) {
			message = GameMessages.getMessage(sourceBody, MessagePool.Action.ApologyToFamily);
		}
		From.setBodyEventResMessage(message, Const.HOLDMESSAGE, true, GameRandom.nextBoolean());
	}

	@Override
	public String toString() {
		return GameText.read("event_mold");
	}
}
