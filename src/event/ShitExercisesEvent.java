package src.event;

import java.util.List;

import src.Const;
import src.SimYukkuri;
import src.base.Body;
import src.base.EventPacket;
import src.base.Obj;
import src.draw.Translate;
import src.enums.AgeState;
import src.enums.Event;
import src.enums.Happiness;
import src.enums.PublicRank;
import src.item.Barrier;
import src.logic.BodyLogic;
import src.system.MessagePool;
import src.system.ResourceUtil;
import src.util.YukkuriUtil;

/***************************************************
	うんうん体操イベント
	protected Body from;			// イベントを発した個体
	protected Body to;				// 未使用
	protected Obj target;			// トイレ
	protected int count;			// 10
*/
public class ShitExercisesEvent extends EventPacket implements java.io.Serializable {

	private static final long serialVersionUID = 2219635802037985212L;
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
		/** ♪みぎに　ひだりに　ゆ～ら♪　ゆ～ら♪ */
		YURAYURA,
		/** ♪おてんとさま　まで　の～び♪　の～び♪ */
		NOBINOBI,
		/** ♪ぽんぽん　ぽかぽか　ゆわわ～い♪ */
		POKAPOKA,
		/** ♪うんうんさん　も　おでかけするよっ♪ */
		UNUN,
		/** イベント終了時 */
		END
	}

	/** 状態 */
	private STATE state = STATE.GO;

	/**
	 * コンストラクタ.
	 */
	public ShitExercisesEvent(Body f, Body t, Obj tgt, int cnt) {
		super(f, t, tgt, cnt);
		priority = EventPriority.HIGH;
	}
	
	public ShitExercisesEvent() {
		
	}

	@Override
	public boolean simpleEventAction(Body b) {
		Body from = YukkuriUtil.getBodyInstance(getFrom());
		if (from == null || from.isShutmouth()) {
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
		if (from == null) return false;
		boolean ret = false;
		if (from == b) {
			return true;
		}
		// うんうん奴隷の場合は参加しない
		if (b.getPublicRank() == PublicRank.UnunSlave)
			return false;

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
		// 赤ゆ以外は終了
		if (!b.isBaby())
			return false;

		b.setWorldEventResMessage(MessagePool.getMessage(b, MessagePool.Action.ShitExercisesGO), Const.HOLDMESSAGE,
				true, false);
		b.setHappiness(Happiness.VERY_HAPPY);
		b.wakeup();
		b.clearActions();
		ret = true;
		return ret;
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
		if (from.getCurrentEvent() == null) {
			return UpdateState.ABORT;
		}
		// 産気づいたら
		if (b.nearToBirth()) {
			return UpdateState.ABORT;
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

		//間隔をあけてチェック
		if (tick % 20 != 0) {
			return null;
		}

		// 体操中は寝ない
		b.wakeup();

		// 満腹度が2%以下なら2%にする(強制イベント救済措置)
		if (2 > 100 * b.getHungry() / b.getHungryLimit()) {
			b.setHungry(2 * b.getHungryLimit() / 100);
		}

		// つがいはスキップ
		if (b.isPartner(from)) {
			if (SimYukkuri.RND.nextInt(100) == 0) {
				b.setMessage(MessagePool.getMessage(b, MessagePool.Action.GladAboutChild), true);
			}

			if (state != STATE.GO) {
				b.stay();
			} else {
				int colX = BodyLogic.calcCollisionX(b, from);
				b.moveTo(from.getX() + colX * 2, from.getY());
			}

			return null;
		}

		int nWait = 2000;
		int nWait2 = 300;
		// 親
		if (b == from) {
			// 何らかの理由で終了しそうにないなら終わらせる
			if (2000 < nFromWaitCount) {
				return UpdateState.ABORT;
			}
			nFromWaitCount++;

			// 赤ゆのみ集合
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
				if (SimYukkuri.RND.nextInt(30) == 0) {
					b.setMessage(MessagePool.getMessage(b, MessagePool.Action.ShitExercisesGOFrom), true);
				}
				//b.setBodyEventResMessage(MessagePool.getMessage(b, MessagePool.Action.ShitExercisesWAITFrom), 52, true, false);
				boolean bResult = BodyLogic.gatheringYukkuriFront(from, childrenList, this);

				int nDistanceToilet = 0;
				Obj target = b.takeMappedObj(this.target);
				if (target != null) {
					nDistanceToilet = Translate.getRealDistance(b.getX(), b.getY(), target.getX(), target.getY() - 20);
				}
				// 親はトイレの近くで待つ
				if (nDistanceToilet <= 1) {
					// 目的地に到着
					if (bResult) {
						state = STATE.WAIT;
					}
					b.stay();
				} else {
					if (target != null) {
						// トイレに近づく
						b.moveToEvent(this, target.getX(), target.getY() - 20);
					}
				}

				break;
			case WAIT:
				if (checkWait(b, nWait)) {
					b.setBodyEventResMessage(MessagePool.getMessage(b, MessagePool.Action.ShitExercisesWAITFrom), 52,
							true, false);
					state = STATE.START;
					bActionFlag = false;
				}
				b.stay(nWait2);
				break;
			case START:
				if (checkWait(b, nWait)) {
					if (!bActionFlag) {
						b.setBodyEventResMessage(MessagePool.getMessage(b, MessagePool.Action.ShitExercisesSTARTFrom),
								52, true, false);
						bActionFlag = true;
						b.stay(nWait2);
						b.addMemories(10);
					} else {
						state = STATE.YURAYURA;
						bActionFlag = false;
					}
				}
				break;
			case YURAYURA:
				if (checkWait(b, nWait)) {
					if (!bActionFlag) {
						b.setBodyEventResMessage(
								MessagePool.getMessage(b, MessagePool.Action.ShitExercisesYURAYURAFrom), 52, true,
								false);
						bActionFlag = true;
						b.stay(nWait2);
						b.addMemories(10);
					} else {
						state = STATE.NOBINOBI;
						bActionFlag = false;
					}
				}
				break;
			case NOBINOBI:
				if (checkWait(b, nWait)) {
					if (!bActionFlag) {
						b.setBodyEventResMessage(
								MessagePool.getMessage(b, MessagePool.Action.ShitExercisesNOBINOBIFrom), 52, true,
								false);
						bActionFlag = true;
						b.stay(nWait2);
						b.addMemories(10);
					} else {
						state = STATE.POKAPOKA;
						bActionFlag = false;
					}
				}
				break;
			case POKAPOKA:
				if (checkWait(b, nWait)) {
					if (!bActionFlag) {
						b.setBodyEventResMessage(
								MessagePool.getMessage(b, MessagePool.Action.ShitExercisesPOKAPOKAFrom), 52, true,
								false);
						bActionFlag = true;
						b.stay(nWait2);
						b.addMemories(10);
					} else {
						state = STATE.UNUN;
						bActionFlag = false;
					}
				}
				break;
			case UNUN:
				if (checkWait(b, nWait)) {
					if (!bActionFlag) {
						b.setBodyEventResMessage(MessagePool.getMessage(b, MessagePool.Action.ShitExercisesUNUNFrom),
								52, true, false);
						bActionFlag = true;
						b.stay(nWait2);
						b.addMemories(10);
					}
					if (bUnunActionFlag) {
						bActionFlag = false;
					}
				}
				break;
			case END:
				if (checkWait(b, nWait)) {
					if (!bActionFlag) {
						b.setBodyEventResMessage(MessagePool.getMessage(b, MessagePool.Action.ShitExercisesENDFrom), 52,
								true, false);
						bActionFlag = true;
						b.stay(nWait2);
						b.addMemories(10);
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
					b.setMessage(MessagePool.getMessage(b, MessagePool.Action.ShitExercisesGO), true);
					b.addMemories(5);
				}

				break;
			case WAIT:
				if (checkWait(b, nWait)) {
					b.setBodyEventResMessage(MessagePool.getMessage(b, MessagePool.Action.ShitExercisesWAIT), 52, true,
							false);
					b.addMemories(5);
				}
				b.stay();
				break;
			case START:
				if (bActionFlag) {
					if (checkWait(b, nWait)) {
						b.setBodyEventResMessage(MessagePool.getMessage(b, MessagePool.Action.ShitExercisesSTART), 52,
								true, false);
						b.stay(nWait2);
						b.addMemories(10);
					}
				}
				break;
			case YURAYURA:
				if (bActionFlag) {
					if (checkWait(b, nWait)) {
						b.setBodyEventResMessage(MessagePool.getMessage(b, MessagePool.Action.ShitExercisesYURAYURA),
								52, true, false);
						b.stay(nWait2);
						b.addMemories(10);
					}
				}
				break;
			case NOBINOBI:
				if (bActionFlag) {
					if (checkWait(b, nWait)) {
						b.setBodyEventResMessage(MessagePool.getMessage(b, MessagePool.Action.ShitExercisesNOBINOBI),
								52, true, false);
						b.setNobinobi(true);
						b.stay(nWait2);
						b.addMemories(10);
					}
				}
				break;
			case POKAPOKA:
				if (bActionFlag) {
					if (checkWait(b, nWait)) {
						b.setBodyEventResMessage(MessagePool.getMessage(b, MessagePool.Action.ShitExercisesPOKAPOKA),
								52, true, false);
						b.setFurifuri(true);
						bUnunActionFlag = false;
						b.stay(nWait2);
						b.addMemories(10);
					}
				}
				break;
			case UNUN:
				if (bActionFlag) {
					if (checkWait(b, nWait)) {
						b.setBodyEventResMessage(MessagePool.getMessage(b, MessagePool.Action.ShitExercisesUNUN), 52,
								true, false);
						b.setFurifuri(true);
						// 肛門が塞がれてなければ排泄
						if ((!b.isAnalClose()) && !b.isHasPants()) {
							if (b.getBodyAgeState() == AgeState.BABY) {
								b.setHappiness(Happiness.VERY_HAPPY);
								b.addStress(250);
								// お尻が汚れる
								if (SimYukkuri.RND.nextInt(4) == 0) {
									b.makeDirty(true);
									// 汚れた場合、親の元に移動
									// ゆっくり同士が重ならないように目標地点は体のサイズを考慮
									int colX = BodyLogic.calcCollisionX(b, from);
									b.moveToBody(from, from.getX() + colX, from.getY());
									b.setTargetBind(true);
								}
							}
							b.setShit(0, false);
							// アクション設定
							b.setEventResultAction(Event.DOSHIT);
							b.addMemories(10);
						} else {
							b.setShit(10, true);
						}
						b.stay();
						bUnunActionFlag = true;
					}
				} else {
					// うんうんチェック
					if (bUnunActionFlag) {
						b.addMemories(5);
						// 子供たちのうんうんが終わったらステート変更
						state = STATE.END;
					}
				}
				break;
			case END:
			default:
				break;
			}
		}

		// 一定時間経過、赤ゆ全集合でうんうん体操開始
		return null;
	}

	// イベント目標に到着した際に呼ばれる
	// trueを返すとイベント終了
	@Override
	public boolean execute(Body b) {
		return false;
	}

	/*public void end(Body b) {
		return;
	}*/

	@Override
	public String toString() {
		return ResourceUtil.getInstance().read("event_unun");
	}
}
