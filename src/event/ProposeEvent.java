package src.event;

import src.SimYukkuri;
import src.base.Body;
import src.base.EventPacket;
import src.base.Obj;
import src.enums.Happiness;
import src.enums.ImageCode;
import src.enums.Intelligence;
import src.logic.BodyLogic;
import src.system.MessagePool;
import src.system.ResourceUtil;
import src.util.YukkuriUtil;

/***************************************************
	プロポーズイベント
	protected Body from;			// イベントを発した個体
	protected Body to;				// 結婚対象
	protected Obj target;			// 未使用
	protected int count;			// 1
*/
public class ProposeEvent extends EventPacket implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	int tick = 0;
	protected boolean started = false;

	/**
	 * コンストラクタ.
	 */
	public ProposeEvent(Body f, Body t, Obj tgt, int cnt) {
		super(f, t, tgt, cnt);
		priority = EventPriority.HIGH;
	}
	
	public ProposeEvent() {
		
	}

	// 参加チェック
	@Override
	public boolean checkEventResponse(Body b) {
		Body to = YukkuriUtil.getBodyInstance(getTo());
		Body from = YukkuriUtil.getBodyInstance(getFrom());
		if (b == from || b == to)
			return true;

		return false;
	}

	// イベント開始動作
	@Override
	public void start(Body b) {
		Body to = YukkuriUtil.getBodyInstance(getTo());
		Body from = YukkuriUtil.getBodyInstance(getFrom());
		if (to != null && from != null) {
			to.wakeup();
			from.setCurrentEvent(this);
			to.setCurrentEvent(this);
			int colX = BodyLogic.calcCollisionX(b, to);
			if (from.canflyCheck()) {
				from.moveToEvent(this, to.getX() + colX, to.getY(), to.getZ());
			} else {
				from.moveToEvent(this, to.getX() + colX, to.getY());
			}
		}
	}

	// 毎フレーム処理
	// UpdateState.ABORTを返すとイベント終了
	@Override
	public UpdateState update(Body b) {
		Body to = YukkuriUtil.getBodyInstance(getTo());
		Body from = YukkuriUtil.getBodyInstance(getFrom());
		if (from == null || to == null || from.isDead() || from.isRemoved())
			return UpdateState.ABORT;
		//相手が死んだか 相手が消えてしまったか非ゆっくり症発症したか取られたらイベント中断
		if (to.isDead() || to.isRemoved() || to.isNYD() || to.isTaken()) {
			from.setCalm();
			from.setBodyEventResMessage(MessagePool.getMessage(from, MessagePool.Action.Surprise), 30, true,
					false);
			from.setHappiness(Happiness.VERY_SAD);
			from.addStress(from.getStressLimit() / 10);
			if (SimYukkuri.RND.nextBoolean()) {
				from.setForceFace(ImageCode.TIRED.ordinal());
			} else {
				from.setForceFace(ImageCode.CRYING.ordinal());
				if (SimYukkuri.RND.nextInt(3) == 0)
					from.doYunnyaa(true);
			}
			return UpdateState.ABORT;
		}

		int colX = BodyLogic.calcCollisionX(b, to);
		//相手がつかまれているとき
		if (to.isGrabbed()) {
			from.setBodyEventResMessage(MessagePool.getMessage(from, MessagePool.Action.DontPreventUs), 30,
					false, SimYukkuri.RND.nextBoolean());
			from.setForceFace(ImageCode.PUFF.ordinal());
			from.setAngry();
			started = false;
			from.setLockmove(false);
			to.setLockmove(false);
			if (from.canflyCheck())
				from.moveToEvent(this, to.getX() + colX, to.getY(), to.getZ());
			else
				from.moveToEvent(this, to.getX() + colX, to.getY());
			//ランダムであきらめる
			if (from.getIntelligence() != Intelligence.FOOL && SimYukkuri.RND.nextInt(1500) == 0) {
				if (SimYukkuri.RND.nextBoolean()) {
					from.setBodyEventResMessage(
							MessagePool.getMessage(from, MessagePool.Action.LamentNoYukkuri), 30, true, true);
					from.setHappiness(Happiness.VERY_SAD);
					from.setForceFace(ImageCode.CRYING.ordinal());
				} else {
					from.setBodyEventResMessage(
							MessagePool.getMessage(from, MessagePool.Action.LamentLowYukkuri), 30, true, true);
					from.setHappiness(Happiness.SAD);
					from.setForceFace(ImageCode.TIRED.ordinal());
				}
				return UpdateState.ABORT;
			}
			return null;
		}

		//イベントが始まってたら飛ばす
		if (started)
			return UpdateState.FORCE_EXEC;

		//たどり着くまで
		if (from.canflyCheck())
			from.moveToEvent(this, to.getX() + colX, to.getY(), to.getZ());
		else
			from.moveToEvent(this, to.getX() + colX, to.getY());
		tick = 0;
		//行動主の呼び止め
		//		from.setCalm();
		//		from.setForceFace(ImageCode.EXCITING.ordinal());
		from.setExciting(true);
		from.clearActionsForEvent();
		//相手も興奮して、ぺにぺに相撲になるのの防止
		if (to.isExciting()) {
			to.setCalm();
			to.clearEvent();
		}
		to.stay();
		if (SimYukkuri.RND.nextInt(20) == 0) {
			if (SimYukkuri.RND.nextBoolean())
				from.setBodyEventResMessage(MessagePool.getMessage(from, MessagePool.Action.PleaseWait), 30,
						true, false);
			else
				from.setBodyEventResMessage(MessagePool.getMessage(from, MessagePool.Action.Excite), 30, true,
						false);
		}
		return null;
	}

	// イベント目標に到着した際に呼ばれる
	// trueを返すとイベント終了
	@Override
	public boolean execute(Body b) {
		Body to = YukkuriUtil.getBodyInstance(getTo());
		Body from = YukkuriUtil.getBodyInstance(getFrom());
		if (to == null || from == null) return true;
		if (to.isGrabbed()) {
			return false;
		}
		//相手がかびてるor食われてる時の挙動
		if (from.findSick(to) || to.isEatenByAnimals() || to.hasDisorder()) {
			from.setCalm();
			from.setBodyEventResMessage(MessagePool.getMessage(from, MessagePool.Action.Surprise), 30, true,
					false);
			from.setHappiness(Happiness.VERY_SAD);
			from.addStress(from.getStressLimit() / 10);
			if (SimYukkuri.RND.nextBoolean()) {
				from.setForceFace(ImageCode.TIRED.ordinal());
			} else {
				from.setForceFace(ImageCode.CRYING.ordinal());
				if (SimYukkuri.RND.nextInt(3) == 0)
					from.doYunnyaa(true);
			}
			//夫婦関係の解消
			from.setPartner(-1);
			if (to.getPartner() == from.getUniqueID()) {
				to.setPartner(-1);
			}
			return true;
		}

		if (tick == 0) {
			//行動主の呼び止め
			from.setCalm();
			from.stayPurupuru(30);
			from.addStress(10);
			if (from.isRude())
				from.setForceFace(ImageCode.VAIN.ordinal());
			else
				from.setForceFace(ImageCode.EMBARRASSED.ordinal());
			from.setBodyEventResMessage(MessagePool.getMessage(from, MessagePool.Action.PleaseWait), 30, true,
					false);
			started = true;
		} else if (tick == 5) {
			//振り向く
			to.setCurrentEvent(this);
			to.constraintDirection(from, false);
			from.setLockmove(true);
			to.setLockmove(true);
		} else if (tick == 20) {
			// 告白
			if (from.isRude() || SimYukkuri.RND.nextInt(20) == 0)
				from.setForceFace(ImageCode.VAIN.ordinal());
			else
				from.setForceFace(ImageCode.EMBARRASSED.ordinal());
			//カップルの設定(ただし、ここではやる側のみ)
			from.setPartner(to.getUniqueID());
			//告白セリフ
			from.setBodyEventResMessage(MessagePool.getMessage(from, MessagePool.Action.Propose), 30, true,
					false);
			from.stayPurupuru(50);
			to.setForceFace(ImageCode.EMBARRASSED.ordinal());
		} else if (tick == 40) {
			//双方の反応
			//成功判定
			boolean sayOK = acceptPropose(from, to);

			//成功
			if (sayOK) {
				to.setForceFace(ImageCode.SMILE.ordinal());
				to.setPartner(from.getUniqueID());
				to.setBodyEventResMessage(MessagePool.getMessage(to, MessagePool.Action.ProposeYes), 30, true,
						false);
				// ゲスほど幸福度は低い
				switch (from.getAttitude()) {
				case VERY_NICE:
					from.addStress(-from.getStressLimit() / 5);
					from.addMemories(50);
					break;
				case NICE:
					from.addStress(-from.getStressLimit() / 10);
					from.addMemories(40);
					break;
				case AVERAGE:
					from.addStress(-from.getStressLimit() / 20);
					from.addMemories(30);
					break;
				case SHITHEAD:
				case SUPER_SHITHEAD:
					from.addStress(-from.getStressLimit() / 30);
					from.addMemories(30);
					break;
				}
				switch (to.getAttitude()) {
				case VERY_NICE:
					to.addStress(-to.getStressLimit() / 5);
					to.addMemories(50);
					break;
				case NICE:
					to.addStress(-to.getStressLimit() / 10);
					to.addMemories(40);
					break;
				case AVERAGE:
					to.addStress(-to.getStressLimit() / 20);
					to.addMemories(30);
					break;
				case SHITHEAD:
				case SUPER_SHITHEAD:
					to.addStress(-to.getStressLimit() / 30);
					to.addMemories(30);
					break;
				}
			}
			//失敗
			else {
				if (to.findSick(from)) {
					to.setBodyEventResMessage(MessagePool.getMessage(to, MessagePool.Action.HateMoldyYukkuri),
							30, true, false);
					to.setForceFace(ImageCode.PUFF.ordinal());
				} else {
					to.setBodyEventResMessage(MessagePool.getMessage(to, MessagePool.Action.ProposeNo), 30,
							true, false);
					if (to.isRude()) {
						to.setForceFace(ImageCode.RUDE.ordinal());
					} else {
						to.setForceFace(ImageCode.TIRED.ordinal());
					}
				}
				from.setPartner(-1);
				// ストレスと思い出の上下
				switch (from.getAttitude()) {
				case VERY_NICE:
					from.addStress(from.getStressLimit() / 20);
					from.addMemories(-50);
					break;
				case NICE:
					from.addStress(from.getStressLimit() / 16);
					from.addMemories(-40);
					break;
				case AVERAGE:
					from.addStress(from.getStressLimit() / 10);
					from.addMemories(-30);
					break;
				case SHITHEAD:
				case SUPER_SHITHEAD:
					from.addStress(from.getStressLimit() / 6);
					from.addMemories(-20);
					break;
				}
				switch (to.getAttitude()) {
				case VERY_NICE:
					to.addStress(-to.getStressLimit() / 20);
					break;
				case NICE:
					to.addStress(-to.getStressLimit() / 16);
					break;
				case AVERAGE:
					to.addStress(-to.getStressLimit() / 10);
					break;
				case SHITHEAD:
				case SUPER_SHITHEAD:
					to.addStress(-to.getStressLimit() / 6);
					break;
				}
			}
		} else if (tick == 60) {
			//成功時はすっきりを迫る
			if (from.getPartner() == to.getUniqueID()) {
				from.setHappiness(Happiness.VERY_HAPPY);
				from.clearActionsForEvent();
				from.setExciting(true);
				from.setForceFace(ImageCode.EXCITING.ordinal());
				from.setBodyEventResMessage(MessagePool.getMessage(from, MessagePool.Action.LetsPlay), 30,
						true, false);
				to.setBodyEventResMessage(MessagePool.getMessage(to, MessagePool.Action.OKcome), 30, true,
						false);
			}
			//失敗時は泣いて逃げる
			else {
				from.setHappiness(Happiness.VERY_SAD);
				from.setForceFace(ImageCode.CRYING.ordinal());
				from.setBodyEventResMessage(MessagePool.getMessage(from, MessagePool.Action.Heartbreak), 30,
						true, false);
				from.runAway(to.getX(), to.getY());
				return true;
			}
		} else if (tick == 70) {
			to.constraintDirection(from, true);
			from.doSukkiri(to);
			return true;
		}
		tick++;
		return false;
	}

	/**
	 * fのプロポーズのtによる判定。プロポーズはfがした側、tがされた側
	 * @param f プロポーズした側
	 * @param t プロポーズされた側
	 * @return プロポーズ成功かどうか
	 */
	public boolean acceptPropose(Body f, Body t) {
		//既婚
		if (YukkuriUtil.getBodyInstance(t.getPartner()) != null)
			return false;
		//カビ発見
		if (t.findSick(f))
			return false;
		//妊娠中個体
		if (f.hasBabyOrStalk())
			return false;
		//障害ゆん
		if (f.hasDisorder())
			return false;

		return true;
	}

	// イベント終了処理
	@Override
	public void end(Body b) {
		Body to = YukkuriUtil.getBodyInstance(getTo());
		Body from = YukkuriUtil.getBodyInstance(getFrom());
		if (from == null || to == null) return;
		from.setCalm();
		from.setCurrentEvent(null);
		if (to != null) {
			to.setCurrentEvent(null);
			to.setLockmove(false);
		}
		from.setLockmove(false);
	}

	@Override
	public String toString() {
		return ResourceUtil.getInstance().read("event_proposal");
	}
}
