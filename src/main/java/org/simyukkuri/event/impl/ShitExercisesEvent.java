package org.simyukkuri.event.impl;

import java.util.List;
import org.simyukkuri.Const;
import org.simyukkuri.draw.Translate;
import org.simyukkuri.entity.core.Entity;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.enums.AgeState;
import org.simyukkuri.enums.Happiness;
import org.simyukkuri.enums.PublicRank;
import org.simyukkuri.enums.TickResult;
import org.simyukkuri.event.EventPacket;
import org.simyukkuri.field.impl.Barrier;
import org.simyukkuri.logic.YukkuriLogic;
import org.simyukkuri.system.MessagePool;
import org.simyukkuri.util.GameMessages;
import org.simyukkuri.util.GameRandom;
import org.simyukkuri.util.GameText;

/**
 * うんうん体操イベント
 * protected Yukkuri from; // イベントを発した個体
 * protected Yukkuri to; // 未使用
 * protected Entity target; // トイレ
 * protected int count; // 10
 */
public class ShitExercisesEvent extends EventPacket {

	private static final long serialVersionUID = 2219635802037985212L;
	int tick = 0;
	boolean actionFlag = true;
	boolean ununActionFlag = true;
	int fromWaitCount = 0;

	/** 行動ステート */
	enum State {
		/** 移動 */
		GO,
		/** 待機 */
		WAIT,
		/** イベント開始時 */
		START,
		/** ♪みぎに ひだりに ゆ～ら♪ ゆ～ら♪ */
		YURAYURA,
		/** ♪おてんとさま まで の～び♪ の～び♪ */
		NOBINOBI,
		/** ♪ぽんぽん ぽかぽか ゆわわ～い♪ */
		POKAPOKA,
		/** ♪うんうんさん も おでかけするよっ♪ */
		UNUN,
		/** イベント終了時 */
		END
	}

	/** 状態 */
	private State state = State.GO;

	/**
	 * コンストラクタ.
	 */
	public ShitExercisesEvent(Yukkuri f, Yukkuri t, Entity tgt, int cnt) {
		super(f, t, tgt, cnt);
		setHighPriority();
	}

	/** Jackson デシリアライズ用デフォルトコンストラクタ。 */
	public ShitExercisesEvent() {

	}

	/** イベントの進行ティックカウンタを返す。 */
	public int getTick() {
		return tick;
	}

	/** イベントの進行ティックカウンタをセットする。 */
	public void setTick(int tick) {
		this.tick = tick;
	}

	/** 行動フラグを返す。 */
	public boolean isActionFlag() {
		return actionFlag;
	}

	/** 行動フラグをセットする。 */
	public void setActionFlag(boolean actionFlag) {
		this.actionFlag = actionFlag;
	}

	/** うんうん行動フラグを返す。 */
	public boolean isUnunActionFlag() {
		return ununActionFlag;
	}

	/** うんうん行動フラグをセットする。 */
	public void setUnunActionFlag(boolean ununActionFlag) {
		this.ununActionFlag = ununActionFlag;
	}

	/** 発信者側の待機カウントを返す。 */
	public int getFromWaitCount() {
		return fromWaitCount;
	}

	/** 発信者側の待機カウントをセットする。 */
	public void setFromWaitCount(int fromWaitCount) {
		this.fromWaitCount = fromWaitCount;
	}

	/** ゆっくり以外のエンティティに対する簡易参加チェック。 */
	@Override
	public boolean simpleEventAction(Yukkuri body) {
		Yukkuri sourceBody = org.simyukkuri.util.YukkuriLookup.getYukkuriById(getFrom());
		if (sourceBody == null || sourceBody.isShutmouth()) {
			return true;
		}
		return false;
	}

	// 参加チェック
	// ここで各種チェックを行い、イベントへ参加するかを返す
	// また、イベント優先度も必要に応じて設定できる
	/** イベントへの参加可否を判定し、参加可能なら true を返す。 */
	@Override
	public boolean checkEventResponse(Yukkuri body) {
		Yukkuri sourceBody = org.simyukkuri.util.YukkuriLookup.getYukkuriById(getFrom());
		if (sourceBody == null) {
			return false;
		}
		if (sourceBody == body) {
			// 赤ゆがいなければ参加しない（子消滅後の再参加ループ防止）
			List<Yukkuri> babyList = YukkuriLogic.createActiveChildren(body, false);
			if (babyList == null || babyList.isEmpty()) {
				return false;
			}
			return true;
		}
		// 親がイベント参加中でなければ参加しない
		if (sourceBody.getCurrentEvent() == null) {
			return false;
		}
		// うんうん奴隷の場合は参加しない
		if (body.getPublicRank() == PublicRank.UNUN_SLAVE) {
			return false;
		}
		// つがいも参加する
		if (sourceBody.isPartner(body)) {
			return true;
		}
		if (!body.canEventResponse()) {
			return false;
		}
		// Fromの子供だけ参加する(※Fromが教育係のときは全ての子供が参加するようにする？)
		if (!body.isChild(sourceBody)) {
			return false;
		}
		// 赤ゆ以外は終了
		if (!body.isBaby()) {
			return false;
		}
		body.setWorldEventResMessage(GameMessages.getMessage(body, MessagePool.Action.ShitExercisesGO),
				Const.HOLDMESSAGE,
				true, false);
		body.setHappiness(Happiness.VERY_HAPPY);
		body.wakeup();
		body.clearActions();
		return true;
	}

	// イベント開始動作
	/** イベントの開始処理を実行する。 */
	@Override
	public void start(Yukkuri body) {
		body.setCurrentEvent(this);
		// うんうん体操もイベント開始時はその場で集合させる。
		// 以前の移動先が残ると、親ゆがフラフラ歩き続けてしまう。
		body.clearActionsForEvent();
		body.setMoveTargetId(-1);
		body.setDestX(-1);
		body.setDestY(-1);
		body.setDestZ(-1);
		body.setTargetOffsetX(0);
		body.setTargetOffsetY(0);
		body.setTargetBind(false);
		body.setBlockedTicks(0);
		body.stopStaying();

		Yukkuri sourceBody = org.simyukkuri.util.YukkuriLookup.getYukkuriById(getFrom());
		if (sourceBody != null) {
			java.util.List<Yukkuri> babyList = YukkuriLogic.createActiveChildren(sourceBody, false);
			if (babyList != null) {
				for (Yukkuri child : babyList) {
					if (child != null && child.getCurrentEvent() == null) {
						child.setCurrentEvent(this);
					}
					if (child != null) {
						child.setMoveTargetId(-1);
						child.setBlockedTicks(0);
						child.clearActionsForEvent();
						child.stopStaying();
					}
				}
			}
		}
	}

	/** イベントの進行ステートを返す。 */
	public State getState() {
		return state;
	}

	/** イベントの進行ステートをセットする。 */
	public void setState(State state) {
		this.state = state;
	}

	// 毎フレーム処理
	// "UpdateState.ABORT"を返すとイベント終了
	// 親→子供→次のステート、の順で処理をする
	/** 毎ティック状態を更新する。 */
	@Override
	public UpdateState update(Yukkuri body) {
		Yukkuri sourceBody = org.simyukkuri.util.YukkuriLookup.getYukkuriById(getFrom());
		if (body == null || sourceBody == null) {
			return UpdateState.ABORT;
		}
		if (body.isNyd() || body.isDead() || body.isRemoved()) {
			return UpdateState.ABORT;
		}
		if (sourceBody.isRemoved() || sourceBody.getCurrentEvent() == null) {
			body.setHappiness(Happiness.VERY_HAPPY);
			return UpdateState.ABORT;
		}
		if (body.nearToBirth()) {
			return UpdateState.ABORT;
		}
		if (!sourceBody.canflyCheck() && sourceBody.getZ() >= 2) {
			if (GameRandom.nextInt(50) == 0) {
				return UpdateState.ABORT;
			}
			if (body != sourceBody) {
				if (body.isSad()) {
					body.setMessage(GameMessages.getMessage(body, MessagePool.Action.LookForParents), false);
				} else {
					body.setMessage(GameMessages.getMessage(body, MessagePool.Action.LookForParents), true);
				}
				body.setHappiness(Happiness.SAD);
				return null;
			}
		}

		if (body == sourceBody) {
			if (tick++ % 20 != 0) {
				return null;
			}
		} else if (tick % 20 != 0) {
			return null;
		}

		body.wakeup();
		if (body.isHungry()) {
			body.setHungry(body.getHungryLimit() * 6 / 10);
		}

		if (body.isPartner(sourceBody)) {
			if (GameRandom.nextInt(100) == 0) {
				body.setMessage(GameMessages.getMessage(body, MessagePool.Action.GladAboutChild), true);
			}
			if (state != State.GO) {
				body.stay();
			} else {
				int collisionX = YukkuriLogic.calcCollisionX(body, sourceBody);
				body.moveTo(sourceBody.getX() + collisionX * 2, sourceBody.getY());
			}
			return null;
		}

		int waitTicks = 2000;
		int stayTicks = 300;
		if (body == sourceBody) {
			if (2000 < fromWaitCount) {
				return UpdateState.ABORT;
			}
			fromWaitCount++;

			List<Yukkuri> childrenList = YukkuriLogic.createActiveChildren(sourceBody, false);
			if (childrenList == null || childrenList.isEmpty()) {
				return UpdateState.ABORT;
			}
			if (10 < fromWaitCount) {
				boolean childInEvent = false;
				for (Yukkuri child : childrenList) {
					if (child.getCurrentEvent() == this) {
						childInEvent = true;
						break;
					}
				}
				if (!childInEvent) {
					return UpdateState.ABORT;
				}
			}

			switch (state) {
				case GO:
					if (GameRandom.nextInt(30) == 0) {
						sourceBody.setMessage(
								GameMessages.getMessage(sourceBody, MessagePool.Action.ShitExercisesGOFrom), true);
					}
					YukkuriLogic.gatheringYukkuriFront(sourceBody, childrenList, this);
					int distanceToToilet = 0;
					Entity targetObject = sourceBody.takeMappedObj(this.target);
					if (targetObject != null) {
						distanceToToilet = Translate.getRealDistance(sourceBody.getX(), sourceBody.getY(),
								targetObject.getX(), targetObject.getY() - 20);
					}
					if (distanceToToilet <= 1) {
						state = State.WAIT;
						sourceBody.stay();
					} else if (targetObject != null) {
						sourceBody.moveToEvent(this, targetObject.getX(), targetObject.getY() - 20);
					}
					break;
				case WAIT:
					if (checkWait(sourceBody, waitTicks)) {
						String msg = GameMessages.getMessage(
								sourceBody, MessagePool.Action.ShitExercisesWAITFrom);
						sourceBody.setEventResMessage(msg, 52, true, false);
						state = State.START;
						actionFlag = false;
					}
					sourceBody.stay(stayTicks);
					break;
				case START:
					if (checkWait(sourceBody, waitTicks)) {
						if (!actionFlag) {
							String msg = GameMessages.getMessage(
									sourceBody, MessagePool.Action.ShitExercisesSTARTFrom);
							sourceBody.setEventResMessage(msg, 52, true, false);
							actionFlag = true;
							sourceBody.stay(stayTicks);
							sourceBody.addMemories(10);
						} else {
							state = State.YURAYURA;
							actionFlag = false;
						}
					}
					break;
				case YURAYURA:
					if (checkWait(sourceBody, waitTicks)) {
						if (!actionFlag) {
							String msg = GameMessages.getMessage(
									sourceBody, MessagePool.Action.ShitExercisesYURAYURAFrom);
							sourceBody.setEventResMessage(msg, 52, true, false);
							actionFlag = true;
							sourceBody.stay(stayTicks);
							sourceBody.addMemories(10);
						} else {
							state = State.NOBINOBI;
							actionFlag = false;
						}
					}
					break;
				case NOBINOBI:
					if (checkWait(sourceBody, waitTicks)) {
						if (!actionFlag) {
							String msg = GameMessages.getMessage(
									sourceBody, MessagePool.Action.ShitExercisesNOBINOBIFrom);
							sourceBody.setEventResMessage(msg, 52, true, false);
							actionFlag = true;
							sourceBody.stay(stayTicks);
							sourceBody.addMemories(10);
						} else {
							state = State.POKAPOKA;
							actionFlag = false;
						}
					}
					break;
				case POKAPOKA:
					if (checkWait(sourceBody, waitTicks)) {
						if (!actionFlag) {
							String msg = GameMessages.getMessage(
									sourceBody, MessagePool.Action.ShitExercisesPOKAPOKAFrom);
							sourceBody.setEventResMessage(msg, 52, true, false);
							actionFlag = true;
							sourceBody.stay(stayTicks);
							sourceBody.addMemories(10);
						} else {
							state = State.UNUN;
							actionFlag = false;
						}
					}
					break;
				case UNUN:
					if (checkWait(sourceBody, waitTicks)) {
						if (!actionFlag) {
							String msg = GameMessages.getMessage(
									sourceBody, MessagePool.Action.ShitExercisesUNUNFrom);
							sourceBody.setEventResMessage(msg, 52, true, false);
							sourceBody.stay(stayTicks);
							sourceBody.addMemories(10);
						} else {
							sourceBody.stay(stayTicks);
							sourceBody.addMemories(10);
						}
						ununActionFlag = true;
						actionFlag = false;
						state = State.END;
					}
					break;
				case END:
					if (checkWait(sourceBody, waitTicks)) {
						if (!actionFlag) {
							String msg = GameMessages.getMessage(
									sourceBody, MessagePool.Action.ShitExercisesENDFrom);
							sourceBody.setEventResMessage(msg, 52, true, false);
							sourceBody.stay(stayTicks);
							sourceBody.addMemories(10);
						}
						return UpdateState.ABORT;
					}
					break;
				default:
					break;
			}
		} else {
			switch (state) {
				case GO:
					body.moveToEvent(this, sourceBody.getX(), sourceBody.getY());
					if (Barrier.onBarrier(body.getX(), body.getY(), sourceBody.getX(), sourceBody.getY(),
							Barrier.BODY_BLOCK_FLAGS[body.getAgeState().ordinal()] + Barrier.BARRIER_KEKKAI)) {
						return UpdateState.ABORT;
					}
					if (body.isDontMove()) {
						return UpdateState.ABORT;
					}
					if (GameRandom.nextInt(30) == 0) {
						body.setMessage(GameMessages.getMessage(body, MessagePool.Action.ShitExercisesGO), true);
						body.addMemories(5);
					}
					break;
				case WAIT:
					body.moveToEvent(this, sourceBody.getX(), sourceBody.getY());
					if (checkWait(body, waitTicks)) {
						body.setEventResMessage(GameMessages.getMessage(body, MessagePool.Action.ShitExercisesWAIT),
								52, true,
								false);
						body.addMemories(5);
					}
					body.stay();
					break;
				case START:
					if (actionFlag && checkWait(body, waitTicks)) {
						body.setEventResMessage(
								GameMessages.getMessage(body, MessagePool.Action.ShitExercisesSTART), 52, true,
								false);
						body.stay(stayTicks);
						body.addMemories(10);
					}
					break;
				case YURAYURA:
					if (actionFlag && checkWait(body, waitTicks)) {
						body.setEventResMessage(
								GameMessages.getMessage(body, MessagePool.Action.ShitExercisesYURAYURA), 52,
								true, false);
						body.stay(stayTicks);
						body.addMemories(10);
					}
					break;
				case NOBINOBI:
					if (actionFlag && checkWait(body, waitTicks)) {
						body.setEventResMessage(
								GameMessages.getMessage(body, MessagePool.Action.ShitExercisesNOBINOBI), 52,
								true, false);
						body.setNobinobi(true);
						body.stay(stayTicks);
						body.addMemories(10);
					}
					break;
				case POKAPOKA:
					if (actionFlag && checkWait(body, waitTicks)) {
						body.setEventResMessage(
								GameMessages.getMessage(body, MessagePool.Action.ShitExercisesPOKAPOKA), 52,
								true, false);
						body.setFurifuri(true);
						ununActionFlag = false;
						body.stay(stayTicks);
						body.addMemories(10);
					}
					break;
				case UNUN:
					if (actionFlag) {
						if (checkWait(body, waitTicks)) {
							body.setEventResMessage(
									GameMessages.getMessage(body, MessagePool.Action.ShitExercisesUNUN), 52,
									true, false);
							body.setFurifuri(true);
							if (!body.isAnalClose() && !body.isHasPants()) {
								if (body.getAgeState() == AgeState.BABY) {
									body.setHappiness(Happiness.VERY_HAPPY);
									body.addStress(250);
									if (GameRandom.nextInt(4) == 0) {
										body.makeDirty(true);
										int collisionX = YukkuriLogic.calcCollisionX(body, sourceBody);
										int targetX = sourceBody.getX() + collisionX;
										body.moveToYukkuri(sourceBody, targetX, sourceBody.getY());
										body.setTargetBind(true);
									}
								}
								body.setShit(0, false);
								body.setEventResult(TickResult.SHIT);
								body.addMemories(10);
							} else {
								body.setShit(10, true);
							}
							body.stay();
							ununActionFlag = true;
							actionFlag = false;
							state = State.END;
						}
					} else if (ununActionFlag) {
						body.addMemories(5);
						state = State.END;
					}
					break;
				case END:
					body.stay(52);
					return UpdateState.ABORT;
				default:
					break;
			}
		}
		return null;
	}

	// イベント目標に到着した際に呼ばれる
	// trueを返すとイベント終了
	/** イベント終了判定を行い true で終了する。 */
	@Override
	public boolean execute(Yukkuri b) {
		return false;
	}

	/*
	 * public void end(Yukkuri b) {
	 * return;
	 * }
	 */

	@Override
	public String toString() {
		return GameText.read("event_unun");
	}
}
