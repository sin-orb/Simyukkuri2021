package src.event;

import java.util.List;

import src.Const;
import src.SimYukkuri;
import src.base.Body;
import src.base.EventPacket;
import src.base.Obj;
import src.enums.Happiness;
import src.enums.ImageCode;
import src.enums.PublicRank;
import src.item.Barrier;
import src.logic.BodyLogic;
import src.system.MessagePool;
import src.system.ResourceUtil;
import src.util.YukkuriUtil;

/***************************************************
	葬式イベント
	protected Body from;			// イベントを発した個体
	protected Body to;				// 弔われる個体
	protected Obj target;			// 未使用
	protected int count;			// 10
*/
public class FuneralEvent extends EventPacket implements java.io.Serializable {

	private static final long serialVersionUID = 3784662418868039745L;
	int tick = 0;
	boolean bActionFlag = true;
	boolean bUnunActionFlag = true;
	int nFromWaitCount = 0;

	// 行動ステート
	enum STATE {
		GO, // 移動
		FIND, // 待機
		INTRODUCE, //死亡説明
		START, // イベント開始時
		SING, //おとむらい
		TALK, //おしゃべり
		GOODBYE, //おわかれ
		END, // イベント終了時
	}

	/** 状態 */
	private STATE state = STATE.GO;

	/**
	 * コンストラクタ.
	 */
	public FuneralEvent(Body f, Body t, Obj tgt, int cnt) {
		super(f, t, tgt, cnt);
		priority = EventPriority.HIGH;
	}
	public FuneralEvent() {
	}
	@Override
	public boolean simpleEventAction(Body b) {
		Body from = YukkuriUtil.getBodyInstance(getFrom());
		if (from == null || from.isShutmouth()) {
			return true;
		}
		if(from == b) {
			return true;
		}
		return false;
	}

	// 参加チェック
	// ここで各種チェックを行い、イベントへ参加するかを返す
	// また、イベント優先度も必要に応じて設定できる
	@Override
	public boolean checkEventResponse(Body b) {
		Body from = YukkuriUtil.getBodyInstance(getFrom());
		if(from.getUniqueID() == b.getUniqueID()) {
			return true;
		}
		// うんうん奴隷の場合は参加しない
		if (from == null || b.getPublicRank() == PublicRank.UnunSlave)
			return false;
		//父母がいない場合は参加しない
		if (YukkuriUtil.getBodyInstance(b.getFather()) == null && 
				YukkuriUtil.getBodyInstance(b.getMother()) == null)
			return false;
		//例外状況
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
		b.setWorldEventResMessage(MessagePool.getMessage(b, MessagePool.Action.ProudChildsGO), Const.HOLDMESSAGE, true, false);
		b.setHappiness(Happiness.HAPPY);
		b.wakeup();
		b.clearActions();
		return true;
	}

	// イベント開始動作
	@Override
	public void start(Body b) {
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
	public UpdateState update(Body b) {
		Body from = YukkuriUtil.getBodyInstance(getFrom());
		//イベント中止
		if (b == null || from == null) {
			return UpdateState.ABORT;
		}
		if (b.isNYD()) {
			return UpdateState.ABORT;
		}
		// 相手が消えてしまったら
		if (from.isRemoved()) {
			b.setHappiness(Happiness.VERY_HAPPY);
			return UpdateState.ABORT;
		}
//		if (from.getCurrentEvent() == null) {
//			return UpdateState.ABORT;
//		}
		// 産気づいたら
		if (b.nearToBirth()) {
			return UpdateState.ABORT;
		}
		//3秒に1回
		if (tick % 30 != 0) {
			return null;
		}
		//親を持ち上げたときの反応
		if (!from.canflyCheck() && from.getZ() >= 5) {
			if (SimYukkuri.RND.nextInt(50) == 0)
				return UpdateState.ABORT;
			else if (b == from) {
				//空処理
			} else {
				if (b.isSad())
					b.setMessage(MessagePool.getMessage(b, MessagePool.Action.LookForParents), false);
				else
					b.setMessage(MessagePool.getMessage(b, MessagePool.Action.LookForParents), true);
				b.setHappiness(Happiness.SAD);
				return null;
			}
		}

		// 自慢中は寝ない
		if (b.isSleeping())
			b.wakeup();
		// 満腹度が2%以下なら2%にする(強制イベント救済措置)
		if (2 > 100 * b.getHungry() / b.getHungryLimit()) {
			b.setHungry(2 * b.getHungryLimit() / 100);
		}

		// つがいは別処理
		if (b.isPartner(from)) {
			if (SimYukkuri.RND.nextInt(50) == 0) {
				b.setMessage(MessagePool.getMessage(b, MessagePool.Action.SadnessForChild), true);
			}
			//集まるとき以外は留まる
			if (state != STATE.GO) {
				b.stay();
			} else {
				int colX = BodyLogic.calcCollisionX(b, from);
				b.moveTo(from.getX() + colX * 2, from.getY());
			}
			return null;
		}

		//イベント本番
		int nWait = 2000;
		int nWait2 = 300;
		// 親
		if (b == from) {
			// 何らかの理由で終了しそうにないなら終わらせる
			if (2000 < nFromWaitCount) {
				return UpdateState.ABORT;
			}
			nFromWaitCount++;
			// 子のみ集合
			List<Body> childrenList = BodyLogic.createActiveChildList(from, false);
			if ((childrenList == null) || (childrenList.size() == 0)) {
				return UpdateState.ABORT;
			}
			if (10 < nFromWaitCount) {
				boolean bIsChildInEvent = false;
				for (Body child : childrenList) {
					if (child.getCurrentEvent() == this) {
						bIsChildInEvent = true;
						break;
					}
				}
				if (!bIsChildInEvent) {
					return UpdateState.ABORT;
				}
			}

			switch (state) {
			case GO:
				if (SimYukkuri.RND.nextInt(40) == 0) {
					b.setMessage(MessagePool.getMessage(b, MessagePool.Action.ProudChildsGOFrom), true);
				}
				b.setHappiness(Happiness.SAD);
				boolean bResult = BodyLogic.gatheringYukkuriFront(from, childrenList, this);
				if (bResult) {
					state = STATE.FIND;
					bActionFlag = false;
				}
				b.stay(nWait2);
				break;
			case FIND:
				if (checkWait(b, nWait)) {
					b.setBodyEventResMessage(MessagePool.getMessage(b, MessagePool.Action.SadnessForChild), 52, true,
							false);
					state = STATE.START;
					bActionFlag = false;
				}
				b.stay(nWait2);
				break;
			case START:
				if (checkWait(b, nWait)) {
					if (!bActionFlag) {
						b.setBodyEventResMessage(MessagePool.getMessage(b, MessagePool.Action.FuneralSTARTFrom), 52,
								true, false);
						b.setHappiness(Happiness.AVERAGE);
						bActionFlag = true;
						b.stay(nWait2 + 10);
						b.addMemories(10);
					} else {
						state = STATE.INTRODUCE;
						bActionFlag = false;
					}
				}
				break;
			case INTRODUCE:
				if (checkWait(b, nWait)) {
					if (!bActionFlag) {
						b.setBodyEventResMessage(MessagePool.getMessage(b, MessagePool.Action.FuneralIntroduceFrom), 52,
								true, false);
						bActionFlag = true;
						b.stay(nWait2 + 10);
						b.addMemories(10);
					} else {
						state = STATE.SING;
						bActionFlag = false;
					}
				}
				break;
			case SING:
				if (checkWait(b, nWait)) {
					if (!bActionFlag) {
						b.setBodyEventResMessage(MessagePool.getMessage(b, MessagePool.Action.Requiem), 52, true,
								false);
						if (SimYukkuri.RND.nextBoolean())
							bActionFlag = true;
						b.setNobinobi(true);
						b.stay(nWait2);
						b.addMemories(10);
						b.setHappiness(Happiness.HAPPY);
					} else {
						state = STATE.TALK;
						bActionFlag = false;
					}
				}
				break;
			case TALK:
				if (checkWait(b, nWait)) {
					if (!bActionFlag) {
						b.setBodyEventResMessage(MessagePool.getMessage(b, MessagePool.Action.FuneralTalkFrom), 52,
								true, false);
						if (SimYukkuri.RND.nextInt(4) == 0)
							bActionFlag = true;
						b.stay(nWait2);
						b.setHappiness(Happiness.HAPPY);
						b.addMemories(10);
					} else {
						state = STATE.GOODBYE;
						bActionFlag = false;
					}
				}
				break;
			case GOODBYE:
				if (checkWait(b, nWait)) {
					if (!bActionFlag) {
						b.setBodyEventResMessage(MessagePool.getMessage(b, MessagePool.Action.GoodbyeForever), 52, true,
								false);
						b.getInVain(false);
						Body to = YukkuriUtil.getBodyInstance(getTo());
						if (to != null) {
							to.takeOkazari(false);
							bActionFlag = true;
							b.stay(nWait2);
							b.addMemories(10);
						}
					} else {
						state = STATE.END;
						bActionFlag = false;
					}
				}
				break;
			case END:
				if (checkWait(b, nWait)) {
					if (!bActionFlag) {
						b.setBodyEventResMessage(MessagePool.getMessage(b, MessagePool.Action.FuneralENDFrom), 52, true,
								false);
						bActionFlag = true;
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
				if (SimYukkuri.RND.nextInt(30) == 0) {
					b.setMessage(MessagePool.getMessage(b, MessagePool.Action.ProudChildsGO), true);
				}
				break;
			case FIND:
				if (checkWait(b, nWait)) {
					Body to = YukkuriUtil.getBodyInstance(getTo());
					if (to != null) {
						if (to.isElderSister(b)) {
							b.setBodyEventResMessage(MessagePool.getMessage(b, MessagePool.Action.SadnessForEldersister),
									52, true, false);
						} else {
							b.setBodyEventResMessage(MessagePool.getMessage(b, MessagePool.Action.SadnessForSister), 52,
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
				if (bActionFlag) {
					if (checkWait(b, nWait)) {
						b.setBodyEventResMessage(MessagePool.getMessage(b, MessagePool.Action.FuneralSTART), 52, true,
								false);
						b.stay(nWait2);
						b.addMemories(10);
					}
				}
				break;
			case INTRODUCE:
				if (bActionFlag) {
					if (checkWait(b, nWait)) {
						b.setBodyEventResMessage(MessagePool.getMessage(b, MessagePool.Action.FuneralIntroduce), 52,
								true, false);
						b.setHappiness(Happiness.SAD);
						b.stay(nWait2);
						b.addMemories(10);
					}
				}
				break;
			case SING:
				if (!bActionFlag) {
					if (checkWait(b, nWait)) {
						b.setBodyEventResMessage(MessagePool.getMessage(b, MessagePool.Action.Requiem), 52, true,
								false);
						b.setNobinobi(true);
						b.stay(nWait2);
						b.addMemories(10);
					}
				}
				break;
			case TALK:
				if (bActionFlag) {
					if (checkWait(b, nWait)) {
						b.setBodyEventResMessage(MessagePool.getMessage(b, MessagePool.Action.FuneralTalk), 52, true, false);
						b.setHappiness(Happiness.HAPPY);
						b.getInVain(false);
						b.stay(nWait2);
						b.addMemories(10);
					}
				}
				break;
			case GOODBYE:
				if (bActionFlag) {
					if (checkWait(b, nWait)) {
						b.setBodyEventResMessage(MessagePool.getMessage(b, MessagePool.Action.GoodbyeForever), 52, true,
								false);
						if (b.isRude() && SimYukkuri.RND.nextBoolean()) {
							b.setFurifuri(true);
						} else
							b.getInVain(false);
						b.stay(nWait2);
						b.addMemories(10);
					}
				}
				break;
			case END:
				if (b.isRude())
					b.setBodyEventResMessage(MessagePool.getMessage(b, MessagePool.Action.FuneralEND), 52, true, false);
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
	public boolean execute(Body b) {
		return false;
	}

	@Override
	public void end(Body b) {
		b.setCurrentEvent(null);
		return;
	}

	@Override
	public String toString() {
		return ResourceUtil.getInstance().read("event_funeral");
	}
}
