package src.event;

import java.util.List;

import src.Const;
import src.SimYukkuri;
import src.base.Body;
import src.base.EventPacket;
import src.base.Obj;
import src.enums.Happiness;
import src.enums.PublicRank;
import src.item.Barrier;
import src.logic.BodyLogic;
import src.system.MessagePool;
import src.system.ResourceUtil;
import src.util.YukkuriUtil;

/***************************************************
	おちびちゃん自慢イベント
	protected Body from;			// イベントを発した個体
	protected Body to;				// 未使用
	protected Obj target;			// 未使用
	protected int count;			// 10
*/
public class ProudChildEvent extends EventPacket implements java.io.Serializable {

	private static final long serialVersionUID = -7224287918980312380L;
	int tick = 0;
	boolean bActionFlag = true;
	boolean bUnunActionFlag = true;
	int nFromWaitCount = 0;

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
	public STATE state = STATE.GO;

	/**
	 * コンストラクタ.
	 */
	public ProudChildEvent(Body f, Body t, Obj tgt, int cnt) {
		super(f, t, tgt, cnt);
		priority = EventPriority.MIDDLE;
	}
	
	public ProudChildEvent() {
		
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
		// うんうん奴隷の場合は参加しない
		if (b.getPublicRank() == PublicRank.UnunSlave)
			return false;
		//父母がいない場合は参加しない
		if (YukkuriUtil.getBodyInstance(b.getFather()) == null &&
				YukkuriUtil.getBodyInstance(b.getMother()) == null)
			return false;
		Body from = YukkuriUtil.getBodyInstance(getFrom());
		if (from == null) return false;
		// つがいも参加する
		if (from.isPartner(b)) {
			return true;
		}
		if (!b.canEventResponse()) {
			return false;
		}
		// Fromの子供だけ参加する(※Fromが教育係のときは全ての子供が参加するようにする？)
		if (!b.isChild(from))
			return false;
		// 赤、子ゆのみ参加
		if (b.isAdult())
			return false;

		b.setWorldEventResMessage(MessagePool.getMessage(b, MessagePool.Action.ProudChildsGO), Const.HOLDMESSAGE, true,
				false);
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

	// 毎フレーム処理
	// "UpdateState.ABORT"を返すとイベント終了
	// 親→子供→次のステート、の順で処理をする
	@Override
	public UpdateState update(Body b) {
		Body from = YukkuriUtil.getBodyInstance(getFrom());
		//イベント中止のお知らせ
		if (b == null || from == null) {
			return UpdateState.ABORT;
		}
		if (b.getPanicType() != null) {
			return UpdateState.ABORT;
		}
		if (b.isNYD()) {
			return UpdateState.ABORT;
		}
		if (from.isRemoved()) {
			b.setHappiness(Happiness.SAD);
			return UpdateState.ABORT;
		}
		if (from.getCurrentEvent() == null) {
			return UpdateState.ABORT;
		}
		// 産気づいたら
		if (b.nearToBirth()) {
			return UpdateState.ABORT;
		}
		if (from.isUnhappy()) {
			b.setHappiness(Happiness.SAD);
			return UpdateState.ABORT;
		}

		//3秒に1回
		if (tick % 30 != 0) {
			return null;
		}
		//親を持ち上げたときの反応
		if (!from.canflyCheck() && from.getZ() >= 2) {
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
				b.setMessage(MessagePool.getMessage(b, MessagePool.Action.GladAboutChild), true);
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
				b.setHappiness(Happiness.HAPPY);
				//b.setBodyEventResMessage(MessagePool.getMessage(b, MessagePool.Action.ShitExercisesWAITFrom), 52, true, false);
				boolean bResult = BodyLogic.gatheringYukkuriFront(from, childrenList, this);
				if (bResult) {
					state = STATE.START;
					bActionFlag = false;
				}
				b.stay(nWait2);
				break;
			/*				case WAIT:
								if( checkWait(b,nWait) )
								{
									b.setBodyEventResMessage(MessagePool.getMessage(b, MessagePool.Action.ProudChildsWAITFrom), 52, true, false);
									state = STATE.START;
									bActionFlag = false;
								}
								b.stay(nWait2);
								break;*/
			case START:
				if (checkWait(b, nWait)) {
					if (!bActionFlag) {
						b.setBodyEventResMessage(MessagePool.getMessage(b, MessagePool.Action.ProudChildsSTARTFrom), 52,
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
						b.setBodyEventResMessage(MessagePool.getMessage(b, MessagePool.Action.ProudChildsSING), 52,
								true, false);
						if (SimYukkuri.RND.nextBoolean())
							bActionFlag = true;
						b.setNobinobi(true);
						b.stay(nWait2);
						b.addMemories(10);
						b.setHappiness(Happiness.HAPPY);
					} else {
						state = STATE.PROUD;
						bActionFlag = false;
					}
				}
				break;
			case PROUD:
				if (checkWait(b, nWait)) {
					if (!bActionFlag) {
						b.setBodyEventResMessage(MessagePool.getMessage(b, MessagePool.Action.ProudChildsPROUDFrom), 52,
								true, false);
						b.getInVain(false);
						bActionFlag = true;
						b.stay(nWait2);
						b.setHappiness(Happiness.VERY_HAPPY);
						b.addMemories(10);
					} else {
						state = STATE.END;
						bActionFlag = false;
					}
				}
				break;
			case END:
				if (checkWait(b, nWait)) {
					if (!bActionFlag) {
						b.setBodyEventResMessage(MessagePool.getMessage(b, MessagePool.Action.ProudChildsENDFrom), 52,
								true, false);
						bActionFlag = true;
						b.stay(52);
						b.addMemories(10);
						b.setHappiness(Happiness.VERY_HAPPY);
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
					b.setHappiness(Happiness.VERY_HAPPY);
					b.addMemories(5);
				}

				break;
			/*				case WAIT:
								if( checkWait(b,nWait)){
									b.setBodyEventResMessage(MessagePool.getMessage(b, MessagePool.Action.ProudChildsWAIT), 52, true, false);
									b.addMemories(5);
								}
								b.stay();
								break;*/
			case START:
				if (bActionFlag) {
					if (checkWait(b, nWait)) {
						b.setBodyEventResMessage(MessagePool.getMessage(b, MessagePool.Action.ProudChildsSTART), 52,
								true, false);
						b.stay(nWait2);
						b.addMemories(10);
					}
				}
				break;
			case SING:
				if (!bActionFlag) {
					if (checkWait(b, nWait)) {
						b.setBodyEventResMessage(MessagePool.getMessage(b, MessagePool.Action.ProudChildsSING), 52,
								true, false);
						b.setNobinobi(true);
						b.stay(nWait2);
						b.addMemories(10);
					}
				}
				break;
			case PROUD:
				if (bActionFlag) {
					if (checkWait(b, nWait)) {
						b.setBodyEventResMessage(MessagePool.getMessage(b, MessagePool.Action.ProudChildsPROUD), 52,
								true, false);
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
					b.setBodyEventResMessage(MessagePool.getMessage(b, MessagePool.Action.ProudChildsEND), 52, true,
							false);
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
		return ResourceUtil.getInstance().read("event_proudchild");
	}
}
