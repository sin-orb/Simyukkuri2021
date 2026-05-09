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
import src.enums.Happiness;
import src.enums.ImageCode;
import src.enums.PublicRank;
import src.field.impl.Barrier;
import src.logic.BodyLogic;
import src.system.MessagePool;
import src.system.ResourceUtil;

/***************************************************
 * 葬式イベント
 * protected Yukkuri from; // イベントを発した個体
 * protected Yukkuri to; // 弔われる個体
 * protected Entity target; // 未使用
 * protected int count; // 10
 */
public class FuneralEvent extends EventPacket {

	private static final long serialVersionUID = 3784662418868039745L;
	int tick = 0;
	boolean actionFlag = true;
	boolean ununActionFlag = true;
	int fromWaitCount = 0;

	// 行動ステート
	enum STATE {
		GO, // 移動
		FIND, // 待機
		INTRODUCE, // 死亡説明
		START, // イベント開始時
		SING, // おとむらい
		TALK, // おしゃべり
		GOODBYE, // おわかれ
		END, // イベント終了時
	}

	/** 状態 */
	private STATE state = STATE.GO;

	/**
	 * コンストラクタ.
	 */
	public FuneralEvent(Yukkuri f, Yukkuri t, Entity tgt, int cnt) {
		super(f, t, tgt, cnt);
		priority = EventPriority.HIGH;
	}

	public FuneralEvent() {
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
	public boolean simpleEventAction(Yukkuri b) {
		Yukkuri from = src.util.BodyRegistry.getBodyInstance(getFrom());
		if (from == null || from.isShutmouth()) {
			return true;
		}
		if (from == b) {
			return true;
		}
		return false;
	}

	// 参加チェック
	// ここで各種チェックを行い、イベントへ参加するかを返す
	// また、イベント優先度も必要に応じて設定できる
	@Override
	public boolean checkEventResponse(Yukkuri b) {
		Yukkuri from = src.util.BodyRegistry.getBodyInstance(getFrom());
		if (from.getUniqueID() == b.getUniqueID()) {
			return true;
		}
		// うんうん奴隷の場合は参加しない
		if (from == null || b.getPublicRank() == PublicRank.UnunSlave)
			return false;
		// 父母がいない場合は参加しない
		if (src.util.BodyRegistry.getBodyInstance(b.getFather()) == null &&
				src.util.BodyRegistry.getBodyInstance(b.getMother()) == null)
			return false;
		// 例外状況
		if (!b.canEventResponse()) {
			return false;
		}
		// つがいも参加する
		if (from.isPartner(b)) {
			return true;
		}
		// Fromの子供だけ参加する(※Fromが教育係のときは全ての子供が参加するようにする？)
		if (!b.isChild(from)) {
			return false;
		}
		// 赤、子ゆのみ参加
		if (b.isAdult()) {
			return false;
		}
		b.setWorldEventResMessage(GameMessages.getMessage(b, MessagePool.Action.ProudChildsGO), Const.HOLDMESSAGE, true,
				false);
		b.setHappiness(Happiness.HAPPY);
		b.wakeup();
		b.clearActions();
		return true;
	}

	// イベント開始動作
	@Override
	public void start(Yukkuri b) {
		b.setCurrentEvent(this);
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
	public UpdateState update(Yukkuri b) {
		Yukkuri from = src.util.BodyRegistry.getBodyInstance(getFrom());
		// イベント中止
		if (b == null || from == null) {
			return UpdateState.ABORT;
		}
		if (b.isNYD()) {
			return UpdateState.ABORT;
		}
		if (b.isDead() || b.isRemoved()) {
			return UpdateState.ABORT;
		}
		// 相手が消えてしまったら
		if (from.isRemoved()) {
			b.setHappiness(Happiness.VERY_HAPPY);
			return UpdateState.ABORT;
		}
		// if (from.getCurrentEvent() == null) {
		// return UpdateState.ABORT;
		// }
		// 産気づいたら
		if (b.nearToBirth()) {
			return UpdateState.ABORT;
		}
		// 3秒に1回（FROMのみ tick を進め、参加者数に依らず30フレーム周期を保つ）
		if (b == from) {
			if (tick++ % 30 != 0) return null;
		} else {
			if (tick % 30 != 0) return null;
		}
		// 親を持ち上げたときの反応
		if (!from.canflyCheck() && from.getZ() >= 5) {
			if (GameRandom.nextInt(50) == 0)
				return UpdateState.ABORT;
			else if (b == from) {
				// 空処理
			} else {
				if (b.isSad())
					b.setMessage(GameMessages.getMessage(b, MessagePool.Action.LookForParents), false);
				else
					b.setMessage(GameMessages.getMessage(b, MessagePool.Action.LookForParents), true);
				b.setHappiness(Happiness.SAD);
				return null;
			}
		}

		// 自慢中は寝ない
		if (b.isSleeping())
			b.wakeup();
		// 空腹状態なら60%にする(強制イベント救済措置)
		if (b.isHungry()) {
			b.setHungry(b.getHungryLimit() * 6 / 10);
		}

		// つがいは別処理
		if (b.isPartner(from)) {
			if (GameRandom.nextInt(50) == 0) {
				b.setMessage(GameMessages.getMessage(b, MessagePool.Action.SadnessForChild), true);
			}
			// 集まるとき以外は留まる
			if (state != STATE.GO) {
				b.stay();
			} else {
				int colX = BodyLogic.calcCollisionX(b, from);
				b.moveTo(from.getX() + colX * 2, from.getY());
			}
			return null;
		}

		// イベント本番
		int waitTicks = 2000;
		int stayTicks = 300;
		// 親
		if (b == from) {
			// 何らかの理由で終了しそうにないなら終わらせる
			if (2000 < fromWaitCount) {
				return UpdateState.ABORT;
			}
			fromWaitCount++;
			// 子のみ集合
			List<Yukkuri> childrenList = BodyLogic.createActiveChildList(from, false);
			if ((childrenList == null) || (childrenList.size() == 0)) {
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
					if (GameRandom.nextInt(40) == 0) {
						b.setMessage(GameMessages.getMessage(b, MessagePool.Action.ProudChildsGOFrom), true);
					}
					b.setHappiness(Happiness.SAD);
					boolean gathered = BodyLogic.gatheringYukkuriFront(from, childrenList, this);
					if (gathered) {
						state = STATE.FIND;
						actionFlag = false;
					}
					b.stay(stayTicks);
					break;
				case FIND:
					if (checkWait(b, waitTicks)) {
						b.setBodyEventResMessage(GameMessages.getMessage(b, MessagePool.Action.SadnessForChild), 52,
								true,
								false);
						state = STATE.START;
						actionFlag = false;
					}
					b.stay(stayTicks);
					break;
				case START:
					if (checkWait(b, waitTicks)) {
						if (!actionFlag) {
							b.setBodyEventResMessage(GameMessages.getMessage(b, MessagePool.Action.FuneralSTARTFrom), 52,
									true, false);
							b.setHappiness(Happiness.AVERAGE);
							actionFlag = true;
							b.stay(stayTicks + 10);
							b.addMemories(10);
						} else {
							state = STATE.INTRODUCE;
							actionFlag = false;
						}
					}
					break;
				case INTRODUCE:
					if (checkWait(b, waitTicks)) {
						if (!actionFlag) {
							b.setBodyEventResMessage(GameMessages.getMessage(b, MessagePool.Action.FuneralIntroduceFrom),
									52,
									true, false);
							actionFlag = true;
							b.stay(stayTicks + 10);
							b.addMemories(10);
						} else {
							state = STATE.SING;
							actionFlag = false;
						}
					}
					break;
				case SING:
					if (checkWait(b, waitTicks)) {
						if (!actionFlag) {
							b.setBodyEventResMessage(GameMessages.getMessage(b, MessagePool.Action.Requiem), 52, true,
									false);
							if (GameRandom.nextBoolean())
								actionFlag = true;
							b.setNobinobi(true);
							b.stay(stayTicks);
							b.addMemories(10);
							b.setHappiness(Happiness.HAPPY);
						} else {
							state = STATE.TALK;
							actionFlag = false;
						}
					}
					break;
				case TALK:
					if (checkWait(b, waitTicks)) {
						if (!actionFlag) {
							b.setBodyEventResMessage(GameMessages.getMessage(b, MessagePool.Action.FuneralTalkFrom), 52,
									true, false);
							if (GameRandom.nextInt(4) == 0)
								actionFlag = true;
							b.stay(stayTicks);
							b.setHappiness(Happiness.HAPPY);
							b.addMemories(10);
						} else {
							state = STATE.GOODBYE;
							actionFlag = false;
						}
					}
					break;
				case GOODBYE:
					if (checkWait(b, waitTicks)) {
						if (!actionFlag) {
							b.setBodyEventResMessage(GameMessages.getMessage(b, MessagePool.Action.GoodbyeForever), 52,
									true,
									false);
							b.getInVain(false);
							Yukkuri to = src.util.BodyRegistry.getBodyInstance(getTo());
							if (to != null) {
								to.takeOkazari(false);
								actionFlag = true;
								b.stay(stayTicks);
								b.addMemories(10);
							}
						} else {
							state = STATE.END;
							actionFlag = false;
						}
					}
					break;
				case END:
					if (checkWait(b, waitTicks)) {
						if (!actionFlag) {
							b.setBodyEventResMessage(GameMessages.getMessage(b, MessagePool.Action.FuneralENDFrom), 52,
									true,
									false);
							actionFlag = true;
							b.stay(52);
							b.addMemories(10);
							b.setHappiness(Happiness.HAPPY);
							return UpdateState.ABORT;
						}
					}
				default:
					break;
			}
		} else {
			// 子供
			switch (state) {
				case GO:
					// 壁に引っかかってるなら終了
					if (Barrier.onBarrier(b.getX(), b.getY(), from.getX(), from.getY(),
							Barrier.MAP_BODY[b.getBodyAgeState().ordinal()] + Barrier.BARRIER_KEKKAI)) {
						return UpdateState.ABORT;
					}
					if (b.isDontMove()) {
						return UpdateState.ABORT;
					}
					if (GameRandom.nextInt(30) == 0) {
						b.setMessage(GameMessages.getMessage(b, MessagePool.Action.ProudChildsGO), true);
					}
					break;
				case FIND:
					if (checkWait(b, waitTicks)) {
						Yukkuri to = src.util.BodyRegistry.getBodyInstance(getTo());
						if (to != null) {
							if (to.isElderSister(b)) {
								b.setBodyEventResMessage(
										GameMessages.getMessage(b, MessagePool.Action.SadnessForEldersister),
										52, true, false);
							} else {
								b.setBodyEventResMessage(GameMessages.getMessage(b, MessagePool.Action.SadnessForSister),
										52,
										true, false);
							}
							b.setHappiness(Happiness.VERY_SAD);
							b.setForceFace(ImageCode.CRYING.ordinal());
							b.addMemories(5);
						}
					}
					b.stay();
					break;
				case START:
					if (actionFlag) {
						if (checkWait(b, waitTicks)) {
							b.setBodyEventResMessage(GameMessages.getMessage(b, MessagePool.Action.FuneralSTART), 52,
									true,
									false);
							b.stay(stayTicks);
							b.addMemories(10);
						}
					}
					break;
				case INTRODUCE:
					if (actionFlag) {
						if (checkWait(b, waitTicks)) {
							b.setBodyEventResMessage(GameMessages.getMessage(b, MessagePool.Action.FuneralIntroduce), 52,
									true, false);
							b.setHappiness(Happiness.SAD);
							b.stay(stayTicks);
							b.addMemories(10);
						}
					}
					break;
				case SING:
					if (!actionFlag) {
						if (checkWait(b, waitTicks)) {
							b.setBodyEventResMessage(GameMessages.getMessage(b, MessagePool.Action.Requiem), 52, true,
									false);
							b.setNobinobi(true);
							b.stay(stayTicks);
							b.addMemories(10);
						}
					}
					break;
				case TALK:
					if (actionFlag) {
						if (checkWait(b, waitTicks)) {
							b.setBodyEventResMessage(GameMessages.getMessage(b, MessagePool.Action.FuneralTalk), 52,
									true, false);
							b.setHappiness(Happiness.HAPPY);
							b.getInVain(false);
							b.stay(stayTicks);
							b.addMemories(10);
						}
					}
					break;
				case GOODBYE:
					if (actionFlag) {
						if (checkWait(b, waitTicks)) {
							b.setBodyEventResMessage(GameMessages.getMessage(b, MessagePool.Action.GoodbyeForever), 52,
									true,
									false);
							if (b.isRude() && GameRandom.nextBoolean()) {
								b.setFurifuri(true);
							} else
								b.getInVain(false);
							b.stay(stayTicks);
							b.addMemories(10);
						}
					}
					break;
				case END:
					if (b.isRude())
						b.setBodyEventResMessage(GameMessages.getMessage(b, MessagePool.Action.FuneralEND), 52, true,
								false);
					b.setHappiness(Happiness.HAPPY);
					b.stay(52);
				default:
					break;
			}
		}

		// 一定時間経過、赤ゆ全集合で開始
		return null;
	}

	// イベント目標に到着した際に呼ばれる
	// trueを返すとイベント終了
	@Override
	public boolean execute(Yukkuri b) {
		return false;
	}

	@Override
	public void end(Yukkuri b) {
		b.setCurrentEvent(null);
		return;
	}

	@Override
	public String toString() {
		return GameText.read("event_funeral");
	}
}
