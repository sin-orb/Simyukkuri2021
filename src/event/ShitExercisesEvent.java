package src.event;
import src.util.GameMessages;
import src.util.GameText;

import java.util.List;

import src.Const;
import src.SimYukkuri;
import src.util.GameRandom;
import src.base.Yukkuri;
import src.event.EventPacket;
import src.base.Entity;
import src.draw.Translate;
import src.enums.AgeState;
import src.enums.Event;
import src.enums.Happiness;
import src.enums.PublicRank;
import src.field.impl.Barrier;
import src.logic.BodyLogic;
import src.system.MessagePool;
import src.system.ResourceUtil;

/***************************************************
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
	enum STATE {
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
	private STATE state = STATE.GO;

	/**
	 * コンストラクタ.
	 */
	public ShitExercisesEvent(Yukkuri f, Yukkuri t, Entity tgt, int cnt) {
		super(f, t, tgt, cnt);
		priority = EventPriority.HIGH;
	}

	public ShitExercisesEvent() {

	}

	public int getTick() {
		return tick;
	}

	public void setTick(int tick) {
		this.tick = tick;
	}

	public boolean isActionFlag() {
		return actionFlag;
	}

	public void setActionFlag(boolean actionFlag) {
		this.actionFlag = actionFlag;
	}

	public boolean isUnunActionFlag() {
		return ununActionFlag;
	}

	public void setUnunActionFlag(boolean ununActionFlag) {
		this.ununActionFlag = ununActionFlag;
	}

	public int getFromWaitCount() {
		return fromWaitCount;
	}

	public void setFromWaitCount(int fromWaitCount) {
		this.fromWaitCount = fromWaitCount;
	}

	@Override
	public boolean simpleEventAction(Yukkuri body) {
		Yukkuri sourceBody = src.util.BodyRegistry.getBodyInstance(getFrom());
		if (sourceBody == null || sourceBody.isShutmouth()) {
			return true;
		}
		return false;
	}

	// 参加チェック
	// ここで各種チェックを行い、イベントへ参加するかを返す
	// また、イベント優先度も必要に応じて設定できる
	@Override
	public boolean checkEventResponse(Yukkuri body) {
		Yukkuri sourceBody = src.util.BodyRegistry.getBodyInstance(getFrom());
		if (sourceBody == null)
			return false;
		boolean accepted = false;
		if (sourceBody == body) {
			return true;
		}
		// うんうん奴隷の場合は参加しない
		if (body.getPublicRank() == PublicRank.UnunSlave)
			return false;

		// つがいも参加する
		if (sourceBody.isPartner(body)) {
			return true;
		}
		if (!body.canEventResponse()) {
			return false;
		}
		// Fromの子供だけ参加する(※Fromが教育係のときは全ての子供が参加するようにする？)
		if (!body.isChild(sourceBody))
			return false;
		// 赤ゆ以外は終了
		if (!body.isBaby())
			return false;

		body.setWorldEventResMessage(GameMessages.getMessage(body, MessagePool.Action.ShitExercisesGO), Const.HOLDMESSAGE,
				true, false);
		body.setHappiness(Happiness.VERY_HAPPY);
		body.wakeup();
		body.clearActions();
		accepted = true;
		return accepted;
	}

	// イベント開始動作
	@Override
	public void start(Yukkuri body) {
		body.setCurrentEvent(this);
	}

	public STATE getState() {
		return state;
	}

	public void setState(STATE state) {
		this.state = state;
	}

	// 毎フレーム処理
	// "UpdateState.ABORT"を返すとイベント終了
	// 親→子供→次のステート、の順で処理をする
	@Override
	public UpdateState update(Yukkuri body) {
		Yukkuri sourceBody = src.util.BodyRegistry.getBodyInstance(getFrom());
		if (body == null || sourceBody == null) {
			return UpdateState.ABORT;
		}
		if (body.isNYD() || body.isDead() || body.isRemoved()) {
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
			if (state != STATE.GO) {
				body.stay();
			} else {
				int collisionX = BodyLogic.calcCollisionX(body, sourceBody);
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

			List<Yukkuri> childrenList = BodyLogic.createActiveChildList(sourceBody, false);
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
					sourceBody.setMessage(GameMessages.getMessage(sourceBody, MessagePool.Action.ShitExercisesGOFrom), true);
				}
				boolean gathered = BodyLogic.gatheringYukkuriFront(sourceBody, childrenList, this);
				int distanceToToilet = 0;
				Entity targetObject = sourceBody.takeMappedObj(this.target);
				if (targetObject != null) {
					distanceToToilet = Translate.getRealDistance(sourceBody.getX(), sourceBody.getY(),
							targetObject.getX(), targetObject.getY() - 20);
				}
				if (distanceToToilet <= 1) {
					if (gathered) {
						state = STATE.WAIT;
					}
					sourceBody.stay();
				} else if (targetObject != null) {
					sourceBody.moveToEvent(this, targetObject.getX(), targetObject.getY() - 20);
				}
				break;
			case WAIT:
				if (checkWait(sourceBody, waitTicks)) {
					sourceBody.setBodyEventResMessage(
							GameMessages.getMessage(sourceBody, MessagePool.Action.ShitExercisesWAITFrom), 52, true, false);
					state = STATE.START;
					actionFlag = false;
				}
				sourceBody.stay(stayTicks);
				break;
			case START:
				if (checkWait(sourceBody, waitTicks)) {
					if (!actionFlag) {
						sourceBody.setBodyEventResMessage(
								GameMessages.getMessage(sourceBody, MessagePool.Action.ShitExercisesSTARTFrom), 52, true,
								false);
						actionFlag = true;
						sourceBody.stay(stayTicks);
						sourceBody.addMemories(10);
					} else {
						state = STATE.YURAYURA;
						actionFlag = false;
					}
				}
				break;
			case YURAYURA:
				if (checkWait(sourceBody, waitTicks)) {
					if (!actionFlag) {
						sourceBody.setBodyEventResMessage(
								GameMessages.getMessage(sourceBody, MessagePool.Action.ShitExercisesYURAYURAFrom), 52, true,
								false);
						actionFlag = true;
						sourceBody.stay(stayTicks);
						sourceBody.addMemories(10);
					} else {
						state = STATE.NOBINOBI;
						actionFlag = false;
					}
				}
				break;
			case NOBINOBI:
				if (checkWait(sourceBody, waitTicks)) {
					if (!actionFlag) {
						sourceBody.setBodyEventResMessage(
								GameMessages.getMessage(sourceBody, MessagePool.Action.ShitExercisesNOBINOBIFrom), 52, true,
								false);
						actionFlag = true;
						sourceBody.stay(stayTicks);
						sourceBody.addMemories(10);
					} else {
						state = STATE.POKAPOKA;
						actionFlag = false;
					}
				}
				break;
			case POKAPOKA:
				if (checkWait(sourceBody, waitTicks)) {
					if (!actionFlag) {
						sourceBody.setBodyEventResMessage(
								GameMessages.getMessage(sourceBody, MessagePool.Action.ShitExercisesPOKAPOKAFrom), 52,
								true, false);
						actionFlag = true;
						sourceBody.stay(stayTicks);
						sourceBody.addMemories(10);
					} else {
						state = STATE.UNUN;
						actionFlag = false;
					}
				}
				break;
			case UNUN:
				if (checkWait(sourceBody, waitTicks)) {
					if (!actionFlag) {
						sourceBody.setBodyEventResMessage(
								GameMessages.getMessage(sourceBody, MessagePool.Action.ShitExercisesUNUNFrom), 52, true,
								false);
						actionFlag = true;
						sourceBody.stay(stayTicks);
						sourceBody.addMemories(10);
					}
					if (ununActionFlag) {
						actionFlag = false;
					}
				}
				break;
			case END:
				if (checkWait(sourceBody, waitTicks)) {
					if (!actionFlag) {
						sourceBody.setBodyEventResMessage(
								GameMessages.getMessage(sourceBody, MessagePool.Action.ShitExercisesENDFrom), 52, true, false);
						actionFlag = true;
						sourceBody.stay(stayTicks);
						sourceBody.addMemories(10);
						return UpdateState.ABORT;
					}
				}
				break;
			default:
				break;
			}
		} else {
			switch (state) {
			case GO:
				if (Barrier.onBarrier(body.getX(), body.getY(), sourceBody.getX(), sourceBody.getY(),
						Barrier.MAP_BODY[body.getBodyAgeState().ordinal()] + Barrier.BARRIER_KEKKAI)) {
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
				if (checkWait(body, waitTicks)) {
					body.setBodyEventResMessage(GameMessages.getMessage(body, MessagePool.Action.ShitExercisesWAIT), 52, true,
							false);
					body.addMemories(5);
				}
				body.stay();
				break;
			case START:
				if (actionFlag && checkWait(body, waitTicks)) {
					body.setBodyEventResMessage(GameMessages.getMessage(body, MessagePool.Action.ShitExercisesSTART), 52, true,
							false);
					body.stay(stayTicks);
					body.addMemories(10);
				}
				break;
			case YURAYURA:
				if (actionFlag && checkWait(body, waitTicks)) {
					body.setBodyEventResMessage(GameMessages.getMessage(body, MessagePool.Action.ShitExercisesYURAYURA), 52,
							true, false);
					body.stay(stayTicks);
					body.addMemories(10);
				}
				break;
			case NOBINOBI:
				if (actionFlag && checkWait(body, waitTicks)) {
					body.setBodyEventResMessage(GameMessages.getMessage(body, MessagePool.Action.ShitExercisesNOBINOBI), 52,
							true, false);
					body.setNobinobi(true);
					body.stay(stayTicks);
					body.addMemories(10);
				}
				break;
			case POKAPOKA:
				if (actionFlag && checkWait(body, waitTicks)) {
					body.setBodyEventResMessage(GameMessages.getMessage(body, MessagePool.Action.ShitExercisesPOKAPOKA), 52,
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
						body.setBodyEventResMessage(GameMessages.getMessage(body, MessagePool.Action.ShitExercisesUNUN), 52,
								true, false);
						body.setFurifuri(true);
						if (!body.isAnalClose() && !body.isHasPants()) {
							if (body.getBodyAgeState() == AgeState.BABY) {
								body.setHappiness(Happiness.VERY_HAPPY);
								body.addStress(250);
								if (GameRandom.nextInt(4) == 0) {
									body.makeDirty(true);
									int collisionX = BodyLogic.calcCollisionX(body, sourceBody);
									body.moveToBody(sourceBody, sourceBody.getX() + collisionX, sourceBody.getY());
									body.setTargetBind(true);
								}
							}
							body.setShit(0, false);
							body.setEventResult(Event.DOSHIT);
							body.addMemories(10);
						} else {
							body.setShit(10, true);
						}
						body.stay();
						ununActionFlag = true;
					}
				} else if (ununActionFlag) {
					body.addMemories(5);
					state = STATE.END;
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
