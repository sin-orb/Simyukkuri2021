package org.simyukkuri.event.impl;

import java.util.List;

import org.simyukkuri.Const;
import org.simyukkuri.entity.core.Entity;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.enums.Happiness;
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

/***************************************************
 * おちびちゃん自慢イベント
 * protected Yukkuri from; // イベントを発した個体
 * protected Yukkuri to; // 未使用
 * protected Entity target; // 未使用
 * protected int count; // 10
 */
public class ProudChildEvent extends EventPacket {

	private static final long serialVersionUID = -7224287918980312380L;
	int eventTick = 0;
	boolean childActionEnabled = true;
	boolean slaveActionEnabled = true;
	int fromWaitTicks = 0;

	/** 行動ステート */
	enum STATE {
		/** 移動 */
		GO,
		/** 待機 */
		WAIT,
		/** イベント開始時 */
		START,
		/** おうた */
		SING,
		/** おちび自慢or要求 */
		PROUD,
		/** イベント終了時 */
		END,
	}

	/** 状態 */
	private STATE state = STATE.GO;

	/**
	 * コンストラクタ.
	 */
	public ProudChildEvent(Yukkuri f, Yukkuri t, Entity tgt, int cnt) {
		super(f, t, tgt, cnt);
		priority = EventPriority.MIDDLE;
	}

	/** Jackson デシリアライズ用デフォルトコンストラクタ。 */
	public ProudChildEvent() {

	}

	/** イベントの進行ティックカウンタを返す。 */
	public int getTick() {
		return eventTick;
	}

	/** イベントの進行ティックカウンタをセットする。 */
	public void setTick(int tick) {
		this.eventTick = tick;
	}

	/** 行動フラグを返す。 */
	public boolean isActionFlag() {
		return childActionEnabled;
	}

	/** 行動フラグをセットする。 */
	public void setActionFlag(boolean actionFlag) {
		this.childActionEnabled = actionFlag;
	}

	/** うんうん行動フラグを返す。 */
	public boolean isUnunActionFlag() {
		return slaveActionEnabled;
	}

	/** うんうん行動フラグをセットする。 */
	public void setUnunActionFlag(boolean ununActionFlag) {
		this.slaveActionEnabled = ununActionFlag;
	}

	/** 発信者側の待機カウントを返す。 */
	public int getFromWaitCount() {
		return fromWaitTicks;
	}

	/** 発信者側の待機カウントをセットする。 */
	public void setFromWaitCount(int fromWaitCount) {
		this.fromWaitTicks = fromWaitCount;
	}

	/** ゆっくり以外のエンティティに対する簡易参加チェック。 */
	@Override
	public boolean simpleEventAction(Yukkuri body) {
		Yukkuri sourceBody = org.simyukkuri.util.YukkuriLookup.getYukkuriById(getFrom());

		if (sourceBody == null || sourceBody.isShutmouth()) {
			return true;
		}
		if (sourceBody == body) {
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
		// うんうん奴隷の場合は参加しない
		if (body.getPublicRank() == PublicRank.UNUN_SLAVE)
			return false;
		// 父母がいない場合は参加しない
		if (org.simyukkuri.util.YukkuriLookup.getYukkuriById(body.getFather()) == null &&
				org.simyukkuri.util.YukkuriLookup.getYukkuriById(body.getMother()) == null)
			return false;
		Yukkuri sourceBody = org.simyukkuri.util.YukkuriLookup.getYukkuriById(getFrom());
		if (sourceBody == null)
			return false;
		// つがいも参加する
		if (sourceBody.isPartner(body)) {
			return true;
		}
		if (!body.canEventResponse()) {
			return false;
		}
		// 生まれてしばらく経っていない赤ゆは、おちび自慢イベントには入れない。
		// 着地フラグだけだとすり抜けるので、出生からの経過時間も見る。
		// さらに、茎から落下中の個体もここで止める。
		if (body.isFirstGround() || body.isNewborn() || body.getZ() > body.getMostDepth()) {
			return false;
		}
		// Fromの子供だけ参加する(※Fromが教育係のときは全ての子供が参加するようにする？)
		if (!body.isChild(sourceBody))
			return false;
		// うまれたての赤ゆは参加しない
		if (body.isBirthMessageForced() || body.getBirthEventBlockedTicks() > 0) {
			return false;
		}
		// 赤、子ゆのみ参加
		if (body.isAdult())
			return false;

		body.setWorldEventResMessage(GameMessages.getMessage(body, MessagePool.Action.ProudChildsGO), Const.HOLDMESSAGE,
				true,
				false);
		body.setHappiness(Happiness.HAPPY);
		body.wakeup();
		body.clearActions();
		return true;
	}

	// イベント開始動作
	/** イベントの開始処理を実行する。 */
	@Override
	public void start(Yukkuri body) {
		body.setCurrentEvent(this);
	}

	/** イベントの進行ステートを返す。 */
	public STATE getState() {
		return state;
	}

	/** イベントの進行ステートをセットする。 */
	public void setState(STATE state) {
		this.state = state;
	}

	// 毎フレーム処理
	// "UpdateState.ABORT"を返すとイベント終了
	// 親→子供→次のステート、の順で処理をする
	/** 毎ティック状態を更新する。 */
	@Override
	public UpdateState update(Yukkuri body) {
		Yukkuri sourceBody = org.simyukkuri.util.YukkuriLookup.getYukkuriById(getFrom());
		// イベント中止のお知らせ
		if (body == null || sourceBody == null) {
			return UpdateState.ABORT;
		}
		if (body.getPanicType() != null) {
			return UpdateState.ABORT;
		}
		if (body.isNYD()) {
			return UpdateState.ABORT;
		}
		if (body.isDead() || body.isRemoved()) {
			return UpdateState.ABORT;
		}
		if (sourceBody.isRemoved()) {
			body.setHappiness(Happiness.SAD);
			return UpdateState.ABORT;
		}
		if (sourceBody.getCurrentEvent() == null) {
			return UpdateState.ABORT;
		}
		// 産気づいたら
		if (body.nearToBirth()) {
			return UpdateState.ABORT;
		}
		if (sourceBody.isUnhappy()) {
			body.setHappiness(Happiness.SAD);
			return UpdateState.ABORT;
		}
		if ((body.isFirstGround() || body.isNewborn() || body.getBirthEventBlockedTicks() > 0 || body.getZ() > body.getMostDepth())
				&& !body.isPartner(sourceBody)) {
			return UpdateState.ABORT;
		}
		if (body.isDamaged() && !body.isPartner(sourceBody)) {
			return UpdateState.ABORT;
		}

		// 3秒に1回（FROMのみ tick を進め、参加者数に依らず30フレーム周期を保つ）
		if (body == sourceBody) {
			if (eventTick++ % 30 != 0)
				return null;
		} else {
			if (eventTick % 30 != 0)
				return null;
		}
		// 親を持ち上げたときの反応
		if (!sourceBody.canflyCheck() && sourceBody.getZ() >= 2) {
			if (GameRandom.nextInt(50) == 0)
				return UpdateState.ABORT;
			else if (body == sourceBody) {
				// 空処理
			} else {
				if (body.isSad())
					body.setMessage(GameMessages.getMessage(body, MessagePool.Action.LookForParents), false);
				else
					body.setMessage(GameMessages.getMessage(body, MessagePool.Action.LookForParents), true);
				body.setHappiness(Happiness.SAD);
				return null;
			}
		}

		// 自慢中は寝ない
		if (body.isSleeping())
			body.wakeup();
		// 空腹状態なら60%にする(強制イベント救済措置)
		if (body.isHungry()) {
			body.setHungry(body.getHungryLimit() * 6 / 10);
		}

		// つがいは別処理
		if (body.isPartner(sourceBody)) {
			if (GameRandom.nextInt(50) == 0) {
				body.setMessage(GameMessages.getMessage(body, MessagePool.Action.GladAboutChild), true);
			}
			// 集まるとき以外は留まる
			if (state != STATE.GO) {
				body.stay();
			} else {
				int collisionX = YukkuriLogic.calcCollisionX(body, sourceBody);
				body.moveTo(sourceBody.getX() + collisionX * 2, sourceBody.getY());
			}
			return null;
		}

		// イベント本番
		int waitTicks = 2000;
		int stayTicks = 300;
		// 親
		if (body == sourceBody) {
			// 何らかの理由で終了しそうにないなら終わらせる
			if (2000 < fromWaitTicks) {
				return UpdateState.ABORT;
			}
			fromWaitTicks++;

			// 子のみ集合
			List<Yukkuri> childrenList = YukkuriLogic.createActiveChildren(sourceBody, false);
			if ((childrenList == null) || (childrenList.size() == 0)) {
				return UpdateState.ABORT;
			}
			boolean allSleeping = true;
			for (Yukkuri child : childrenList) {
				if (child != null && !child.isSleeping()) {
					allSleeping = false;
					break;
				}
			}
			if (allSleeping) {
				return UpdateState.ABORT;
			}

			if (10 < fromWaitTicks) {
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
					if (GameRandom.nextInt(40) == 0) {
						body.setMessage(GameMessages.getMessage(body, MessagePool.Action.ProudChildsGOFrom), true);
					}
					body.setHappiness(Happiness.HAPPY);
					// body.setEventResMessage(GameMessages.getMessage(body,
					// MessagePool.Action.ShitExercisesWAITFrom), 52, true, false);
					boolean gathered = YukkuriLogic.gatheringYukkuriFront(sourceBody, childrenList, this);
					if (gathered) {
						state = STATE.START;
						childActionEnabled = false;
					}
					body.stay(stayTicks);
					break;
				/*
				 * case WAIT:
				 * if (checkWait(body, waitTicks)) {
				 * body.setEventResMessage(GameMessages.getMessage(body,
				 * MessagePool.Action.ProudChildsWAITFrom), 52, true, false);
				 * state = STATE.START;
				 * childActionEnabled = false;
				 * }
				 * body.stay(stayTicks);
				 * break;
				 */
				case START:
					if (checkWait(body, waitTicks)) {
						if (!childActionEnabled) {
							body.setEventResMessage(
									GameMessages.getMessage(body, MessagePool.Action.ProudChildsSTARTFrom),
									52,
									true, false);
							childActionEnabled = true;
							body.stay(stayTicks + 10);
							body.addMemories(10);
						} else {
							state = STATE.SING;
							childActionEnabled = false;
						}
					}
					break;
				case SING:
					if (checkWait(body, waitTicks)) {
						if (!childActionEnabled) {
							body.setEventResMessage(
									GameMessages.getMessage(body, MessagePool.Action.ProudChildsSING), 52,
									true, false);
							if (GameRandom.nextBoolean())
								childActionEnabled = true;
							body.setNobinobi(true);
							body.stay(stayTicks);
							body.addMemories(10);
							body.setHappiness(Happiness.HAPPY);
						} else {
							state = STATE.PROUD;
							childActionEnabled = false;
						}
					}
					break;
				case PROUD:
					if (checkWait(body, waitTicks)) {
						if (!childActionEnabled) {
							body.setEventResMessage(
									GameMessages.getMessage(body, MessagePool.Action.ProudChildsPROUDFrom),
									52,
									true, false);
							body.getInVain(false);
							childActionEnabled = true;
							body.stay(stayTicks);
							body.setHappiness(Happiness.VERY_HAPPY);
							body.addMemories(10);
						} else {
							state = STATE.END;
							childActionEnabled = false;
						}
					}
					break;
				case END:
					if (!childActionEnabled) {
						body.setEventResMessage(
								GameMessages.getMessage(body, MessagePool.Action.ProudChildsENDFrom),
								52,
								true, false);
						childActionEnabled = true;
						body.stay(52);
						body.addMemories(10);
						body.setHappiness(Happiness.VERY_HAPPY);
					}
					return UpdateState.ABORT;
				default:
					break;
			}
		} else {
			// 子供
			switch (state) {
				case GO:
					// 壁に引っかかってるなら終了
					if (Barrier.onBarrier(body.getX(), body.getY(), sourceBody.getX(), sourceBody.getY(),
							Barrier.BODY_BLOCK_FLAGS[body.getAgeState().ordinal()] + Barrier.BARRIER_KEKKAI)) {
						return UpdateState.ABORT;
					}

					if (body.isDontMove()) {
						return UpdateState.ABORT;
					}

					if (GameRandom.nextInt(30) == 0) {
						body.setMessage(GameMessages.getMessage(body, MessagePool.Action.ProudChildsGO), true);
						body.setHappiness(Happiness.VERY_HAPPY);
						body.addMemories(5);
					}

					break;
					/*
					 * case WAIT:
					 * if (checkWait(body, waitTicks)) {
					 * body.setEventResMessage(GameMessages.getMessage(body,
					 * MessagePool.Action.ProudChildsWAIT), 52, true, false);
					 * body.addMemories(5);
					 * }
					 * body.stay();
					 * break;
					 */
				case START:
					if (childActionEnabled) {
						if (checkWait(body, waitTicks)) {
							body.setEventResMessage(
									GameMessages.getMessage(body, MessagePool.Action.ProudChildsSTART), 52,
									true, false);
							body.stay(stayTicks);
							body.addMemories(10);
						}
					}
					break;
				case SING:
					if (!childActionEnabled) {
						if (checkWait(body, waitTicks)) {
							body.setEventResMessage(
									GameMessages.getMessage(body, MessagePool.Action.ProudChildsSING), 52,
									true, false);
							body.setNobinobi(true);
							body.stay(stayTicks);
							body.addMemories(10);
						}
					}
					break;
				case PROUD:
					if (childActionEnabled) {
						if (checkWait(body, waitTicks)) {
							body.setEventResMessage(
									GameMessages.getMessage(body, MessagePool.Action.ProudChildsPROUD), 52,
									true, false);
							if (body.isRude() && GameRandom.nextBoolean()) {
								body.setFurifuri(true);
							} else
								body.getInVain(false);
							body.stay(stayTicks);
							body.addMemories(10);
						}
					}
					break;
				case END:
					if (body.isRude())
						body.setEventResMessage(GameMessages.getMessage(body, MessagePool.Action.ProudChildsEND),
								52, true,
								false);
					body.stay(52);
					return UpdateState.ABORT;
				default:
					break;
			}
		}

		// 一定時間経過、赤ゆ全集合で開始
		return null;
	}

	// イベント目標に到着した際に呼ばれる
	// trueを返すとイベント終了
	/** イベント終了判定を行い true で終了する。 */
	@Override
	public boolean execute(Yukkuri body) {
		return false;
	}

	@Override
	/**
	 * End.
	 *
	 * @param body the body
	 */
	public void end(Yukkuri body) {
		body.setCurrentEvent(null);
		return;
	}

	/** イベント名の文字列表現を返す。 */
	@Override
	public String toString() {
		return GameText.read("event_proudchild");
	}
}
