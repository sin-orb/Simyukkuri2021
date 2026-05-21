package org.simyukkuri.event.impl;

import org.simyukkuri.Const;
import org.simyukkuri.entity.core.Entity;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.enums.Happiness;
import org.simyukkuri.enums.ImageCode;
import org.simyukkuri.enums.Intelligence;
import org.simyukkuri.enums.PublicRank;
import org.simyukkuri.event.EventPacket;
import org.simyukkuri.field.impl.Barrier;
import org.simyukkuri.logic.YukkuriLogic;
import org.simyukkuri.system.MessagePool;
import org.simyukkuri.util.GameMessages;
import org.simyukkuri.util.GameRandom;
import org.simyukkuri.util.GameText;

/**
 * かびたゆっくりへの反応イベント.
 */
public class AvoidMoldEvent extends EventPacket {

	private static final long serialVersionUID = -8441703224895176376L;

	/**
	 * コンストラクタ.
	 */
	public AvoidMoldEvent(Yukkuri f, Yukkuri t, Entity tgt, int cnt) {
		super(f, t, tgt, cnt);
	}

	/** Jackson デシリアライズ用デフォルトコンストラクタ. */
	public AvoidMoldEvent() {

	}

	/**
	 * 参加チェック.
	 * ここで各種チェックを行い、イベントへ参加するかを返す.
	 * また、イベント優先度も必要に応じて設定できる.
	 */
	@Override
	public boolean checkEventResponse(Yukkuri body) {
		priority = EventPriority.MIDDLE;
		// うんうん奴隷は参加しない
		if (body.getPublicRank() == PublicRank.UNUN_SLAVE) {
			return false;
		}
		// 足りないゆは参加しない
		if (body.isIdiot()) {
			return false;
		}
		// 非ゆっくり症は参加しない
		if (!body.canEventResponse()) {
			return false;
		}
		// 相手との間に壁があればスキップ
		Yukkuri targetBody = org.simyukkuri.util.YukkuriLookup.getYukkuriById(getTo());
		if (targetBody != null && Barrier.acrossBarrier(body.getX(), body.getY(), targetBody.getX(), targetBody.getY(),
				Barrier.BODY_BLOCK_FLAGS[body.getAgeState().ordinal()] + Barrier.BARRIER_KEKKAI)) {
			return false;
		}
		return true;
	}

	/**
	 * イベント開始動作
	 */
	@Override
	public void start(Yukkuri body) {
		Yukkuri targetBody = org.simyukkuri.util.YukkuriLookup.getYukkuriById(getTo());
		if (targetBody == null) {
			return;
		}
		int collisionX = YukkuriLogic.calcCollisionX(body, targetBody);
		body.moveToEvent(this, targetBody.getX() + collisionX, targetBody.getY());
	}

	/**
	 * 毎フレーム処理
	 * UpdateState.ABORTを返すとイベント終了
	 */
	@Override
	public UpdateState update(Yukkuri body) {
		// 相手が消えてしまったらイベント中断
		Yukkuri targetBody = org.simyukkuri.util.YukkuriLookup.getYukkuriById(getTo());
		if (targetBody == null) {
			return UpdateState.ABORT;
		}
		if (targetBody.isDead() || targetBody.isRemoved()) {
			return UpdateState.ABORT;
		}
		targetBody.stay();
		int collisionX = YukkuriLogic.calcCollisionX(body, targetBody);
		body.moveToEvent(this, targetBody.getX() + collisionX, targetBody.getY());
		return null;
	}

	/**
	 * イベント目標に到着した際に呼ばれる
	 * trueを返すとイベント終了
	 */
	@Override
	public boolean execute(Yukkuri body) {
		Yukkuri targetBody = org.simyukkuri.util.YukkuriLookup.getYukkuriById(getTo());
		Yukkuri sourceBody = org.simyukkuri.util.YukkuriLookup.getYukkuriById(getFrom());
		if (sourceBody == null || targetBody == null) {
			return true;
		}
		// ドゲスの場合、楽しんで制裁
		if (sourceBody.isVeryRude()) {
			sourceBody.setEventResMessage(GameMessages.getMessage(sourceBody, MessagePool.Action.HateMoldyYukkuri),
					Const.HOLDMESSAGE, true, false);
			targetBody.strikeByYukkuri(sourceBody, this, false);
			sourceBody.setForceFace(ImageCode.PUFF.ordinal());
			if (sourceBody.getIntelligence() == Intelligence.FOOL) {
				sourceBody.addSickPeriod(100);
			}
			return true;
		}

		// 自分が成体のばあい。ちゃんと制裁する
		if (sourceBody.isAdult()) {
			if (!body.isTalking()) {
				body.setHappiness(Happiness.VERY_SAD);
				body.addMemories(-1);
				body.addStress(80);

				if (sourceBody.isParent(targetBody) || sourceBody.isPartner(targetBody)) {
					switch (sourceBody.getIntelligence()) {
						case FOOL:
							if (GameRandom.nextInt(5) == 0) {
								sourceBody.doPeropero(targetBody);
								return true;
							}
							saySadMessage(sourceBody, targetBody);
							return false;
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
							}
							saySadMessage(sourceBody, targetBody);
							return false;
					}
				} else if (sourceBody.isFamily(targetBody)) {
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
							}
							saySadMessage(sourceBody, targetBody);
							return false;
					}
				} else {
					sourceBody.setEventResMessage(
							GameMessages.getMessage(sourceBody, MessagePool.Action.HateMoldyYukkuri),
							Const.HOLDMESSAGE, true, false);
					targetBody.strikeByYukkuri(sourceBody, this, false);
					sourceBody.setForceFace(ImageCode.PUFF.ordinal());
					if (sourceBody.getIntelligence() == Intelligence.FOOL) {
						sourceBody.forceSetSick();
					}
					return true;
				}
			}
			return true;
		}

		// 子ゆ、赤ゆの場合。嘆くのみ
		body.setHappiness(Happiness.VERY_SAD);
		body.addStress(80);
		body.addMemories(-1);
		if (sourceBody.isChild(targetBody)) {
			body.addStress(70);
			switch (sourceBody.getIntelligence()) {
				case FOOL:
					if (GameRandom.nextInt(5) == 0) {
						sourceBody.doPeropero(targetBody);
						return false;
					}
					saySadMessage(sourceBody, targetBody);
					return false;
				case WISE:
					if (GameRandom.nextInt(5) == 0) {
						sayApologyMessage(sourceBody, targetBody);
						targetBody.runAway(targetBody.getX(), targetBody.getY());
						return true;
					}
					saySadMessage(sourceBody, targetBody);
					return false;
				default:
					if (GameRandom.nextInt(25) == 0) {
						sayApologyMessage(sourceBody, targetBody);
						targetBody.runAway(targetBody.getX(), targetBody.getY());
						return true;
					}
					if (GameRandom.nextInt(5) == 0) {
						sourceBody.doPeropero(targetBody);
						return false;
					}
					saySadMessage(sourceBody, targetBody);
					return false;
			}
		} else if (targetBody.isFamily(sourceBody)) {
			switch (sourceBody.getIntelligence()) {
				case FOOL:
					if (GameRandom.nextInt(5) == 0) {
						sourceBody.doPeropero(targetBody);
					} else {
						saySadMessage(sourceBody, targetBody);
					}
					return false;
				case WISE:
					if (GameRandom.nextInt(5) == 0) {
						sayApologyMessage(sourceBody, targetBody);
						targetBody.runAway(targetBody.getX(), targetBody.getY());
						return true;
					}
					saySadMessage(sourceBody, targetBody);
					return false;
				default:
					if (GameRandom.nextInt(10) == 0) {
						sayApologyMessage(sourceBody, targetBody);
						targetBody.runAway(targetBody.getX(), targetBody.getY());
						return true;
					}
					saySadMessage(sourceBody, targetBody);
					return false;
			}
		} else {
			sourceBody.setEventResMessage(
					GameMessages.getMessage(sourceBody, MessagePool.Action.HateMoldyYukkuri),
					Const.HOLDMESSAGE, true, false);
			sourceBody.runAway(targetBody.getX(), targetBody.getY());
			sourceBody.setForceFace(ImageCode.PUFF.ordinal());
			if (sourceBody.getIntelligence() == Intelligence.FOOL) {
				sourceBody.forceSetSick();
			}
			return true;
		}
	}

	/**
	 * 悲しみのメッセージを言う.
	 * 
	 * @param sourceBody イベント発生元
	 * @param targetBody イベント対象
	 */
	public void saySadMessage(Yukkuri sourceBody, Yukkuri targetBody) {
		Yukkuri actorBody = org.simyukkuri.util.YukkuriLookup.getYukkuriById(getFrom());
		if (actorBody == null) {
			return;
		}
		String message = null;
		if (sourceBody.isParent(targetBody)) {
			message = GameMessages.getMessage(actorBody, MessagePool.Action.SadnessForMoldyChild);
		} else if (sourceBody.isPartner(targetBody)) {
			message = GameMessages.getMessage(actorBody, MessagePool.Action.SadnessForMoldyPartner);
		} else if (targetBody.isParent(sourceBody)) {
			if (targetBody.isFather(sourceBody)) {
				message = GameMessages.getMessage(actorBody, MessagePool.Action.SadnessForMoldyFather);
			} else {
				message = GameMessages.getMessage(actorBody, MessagePool.Action.SadnessForMoldyMother);
			}
		} else if (targetBody.isSister(sourceBody)) {
			if (targetBody.getAge() >= sourceBody.getAge()) {
				message = GameMessages.getMessage(actorBody, MessagePool.Action.SadnessForEldersister);
			} else {
				message = GameMessages.getMessage(actorBody, MessagePool.Action.SadnessForMoldySister);
			}
		}
		sourceBody.setEventResMessage(message, Const.HOLDMESSAGE, true, GameRandom.nextBoolean());
	}

	/**
	 * 謝罪する.
	 * 
	 * @param sourceBody イベント発生元
	 * @param targetBody イベント対象
	 */
	public void sayApologyMessage(Yukkuri sourceBody, Yukkuri targetBody) {
		Yukkuri actorBody = org.simyukkuri.util.YukkuriLookup.getYukkuriById(getFrom());
		if (actorBody == null) {
			return;
		}
		String message = null;
		if (sourceBody.isParent(targetBody)) {
			message = GameMessages.getMessage(actorBody, MessagePool.Action.ApologyToChild);
		} else if (targetBody.isFamily(sourceBody)) {
			message = GameMessages.getMessage(actorBody, MessagePool.Action.ApologyToFamily);
		}
		sourceBody.setEventResMessage(message, Const.HOLDMESSAGE, true, GameRandom.nextBoolean());
	}

	/** イベント名の文字列表現を返す。 */
	@Override
	public String toString() {
		return GameText.read("event_mold");
	}
}
