package src.event;

import src.Const;
import src.SimYukkuri;
import src.base.Body;
import src.base.EventPacket;
import src.base.Obj;
import src.enums.Happiness;
import src.enums.ImageCode;
import src.enums.Intelligence;
import src.enums.PublicRank;
import src.item.Barrier;
import src.logic.BodyLogic;
import src.system.MessagePool;
import src.system.ResourceUtil;
import src.util.YukkuriUtil;

/***************************************************
 * かびたゆっくりへの反応イベント
 * protected Body from; // イベントを発した個体
 * protected Body to; // 攻撃対象
 * protected Obj target; // 未使用
 * protected int count; // 10
 */
public class AvoidMoldEvent extends EventPacket {

	private static final long serialVersionUID = -8441703224895176376L;

	/**
	 * コンストラクタ.
	 */
	public AvoidMoldEvent(Body f, Body t, Obj tgt, int cnt) {
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
	public boolean checkEventResponse(Body b) {
		priority = EventPriority.MIDDLE;
		// うんうん奴隷は参加しない
		if (b.getPublicRank() == PublicRank.UnunSlave)
			return false;
		// 足りないゆは参加しない
		if (b.isIdiot())
			return false;
		// 非ゆっくり症は参加しない
		if (!b.canEventResponse())
			return false;
		// 相手との間に壁があればスキップ
		Body to = YukkuriUtil.getBodyInstance(getTo());
		if (to != null && Barrier.acrossBarrier(b.getX(), b.getY(), to.getX(), to.getY(),
				Barrier.MAP_BODY[b.getBodyAgeState().ordinal()] + Barrier.BARRIER_KEKKAI)) {
			return false;
		}
		return true;
	}

	/**
	 * イベント開始動作
	 */
	@Override
	public void start(Body b) {
		Body to = YukkuriUtil.getBodyInstance(getTo());
		if (to == null) {
			return;
		}
		int colX = BodyLogic.calcCollisionX(b, to);
		b.moveToEvent(this, to.getX() + colX, to.getY());
	}

	/**
	 * 毎フレーム処理
	 * UpdateState.ABORTを返すとイベント終了
	 */
	@Override
	public UpdateState update(Body b) {
		// 相手が消えてしまったらイベント中断
		Body to = YukkuriUtil.getBodyInstance(getTo());
		if (to == null)
			return UpdateState.ABORT;
		if (to.isDead() || to.isRemoved())
			return UpdateState.ABORT;
		to.stay();
		int colX = BodyLogic.calcCollisionX(b, to);
		b.moveToEvent(this, to.getX() + colX, to.getY());
		return null;
	}

	/**
	 * イベント目標に到着した際に呼ばれる
	 * trueを返すとイベント終了
	 */
	@Override
	public boolean execute(Body b) {
		Body to = YukkuriUtil.getBodyInstance(getTo());
		Body from = YukkuriUtil.getBodyInstance(getFrom());
		if (from == null || to == null) {
			return true;
		}
		// ドゲスの場合、楽しんで制裁
		if (from.isVeryRude()) {
			from.setBodyEventResMessage(MessagePool.getMessage(from, MessagePool.Action.HateMoldyYukkuri),
					Const.HOLDMESSAGE, true, false);
			to.strikeByYukkuri(from, this, false);
			from.setForceFace(ImageCode.PUFF.ordinal());
			if (from.getIntelligence() == Intelligence.FOOL)
				from.addSickPeriod(100);
			return true;
		}

		// 自分が成体のばあい。ちゃんと制裁する
		if (from.isAdult()) {
			// 自分が成体で相手が家族なら嘆く
			if (!b.isTalking()) {
				// 共通処理
				b.setHappiness(Happiness.VERY_SAD);
				b.addMemories(-1);
				b.addStress(80);
				// 相手が子供か、又は番
				if (from.isParent(to) || from.isPartner(to)) {
					switch (from.getIntelligence()) {
						case FOOL:
							if (SimYukkuri.RND.nextInt(5) == 0) {
								from.doPeropero(to);
								return true;
							} else {
								saySadMessage(from, to);
								return false;
							}
						case WISE:
							if (SimYukkuri.RND.nextInt(5) == 0) {
								sayApologyMessage(from, to);
								to.strikeByYukkuri(from, this, false);
							}
							return false;
						default:
							if (SimYukkuri.RND.nextInt(5) == 0) {
								sayApologyMessage(from, to);
								to.strikeByYukkuri(from, this, false);
								return true;
							} else {
								saySadMessage(from, to);
								return false;
							}
					}
				}
				// 相手が親化、又は姉妹の場合
				else if (from.isFamily(to)) {
					switch (from.getIntelligence()) {
						case FOOL:
							if (SimYukkuri.RND.nextInt(5) == 0) {
								from.doPeropero(to);
							} else {
								saySadMessage(from, to);
							}
							return true;
						case WISE:
							if (SimYukkuri.RND.nextInt(5) == 0) {
								sayApologyMessage(from, to);
								to.runAway(to.getX(), to.getY());
							}
							return true;
						default:
							if (SimYukkuri.RND.nextInt(5) == 0) {
								sayApologyMessage(from, to);
								to.runAway(to.getX(), to.getY());
								return true;
							} else {
								saySadMessage(from, to);
								return false;
							}
					}
				}
				// 家族でない場合
				else {
					from.setBodyEventResMessage(MessagePool.getMessage(from, MessagePool.Action.HateMoldyYukkuri),
							Const.HOLDMESSAGE, true, false);
					to.strikeByYukkuri(from, this, false);
					from.setForceFace(ImageCode.PUFF.ordinal());
					if (from.getIntelligence() == Intelligence.FOOL)
						from.forceSetSick();
					return true;
				}
			}
		}

		// 子ゆ、赤ゆの場合。嘆くのみ
		else {
			// 共通処理
			b.setHappiness(Happiness.VERY_SAD);
			b.addStress(80);
			b.addMemories(-1);
			// かびてるのが親
			if (from.isChild(to)) {
				b.addStress(70);
				switch (from.getIntelligence()) {
					case FOOL:
						if (SimYukkuri.RND.nextInt(5) == 0) {
							from.doPeropero(to);
							return false;
						} else {
							saySadMessage(from, to);
							return false;
						}
					case WISE:
						if (SimYukkuri.RND.nextInt(5) == 0) {
							sayApologyMessage(from, to);
							to.runAway(to.getX(), to.getY());
							return true;
						} else {
							saySadMessage(from, to);
							return false;
						}
					default:
						if (SimYukkuri.RND.nextInt(25) == 0) {
							sayApologyMessage(from, to);
							to.runAway(to.getX(), to.getY());
							return true;
						} else if (SimYukkuri.RND.nextInt(5) == 0) {
							from.doPeropero(to);
							return false;
						} else {
							saySadMessage(from, to);
							return false;
						}
				}
			}
			// かびてるのが家族
			else if (to.isFamily(from)) {
				switch (from.getIntelligence()) {
					case FOOL:
						if (SimYukkuri.RND.nextInt(5) == 0) {
							from.doPeropero(to);
							return false;
						} else {
							saySadMessage(from, to);
							return false;
						}
					case WISE:
						if (SimYukkuri.RND.nextInt(5) == 0) {
							sayApologyMessage(from, to);
							to.runAway(to.getX(), to.getY());
							return true;
						} else {
							saySadMessage(from, to);
							return false;
						}
					default:
						if (SimYukkuri.RND.nextInt(10) == 0) {
							sayApologyMessage(from, to);
							to.runAway(to.getX(), to.getY());
							return true;
						} else {
							saySadMessage(from, to);
							return false;
						}
				}
			}
			// 家族でない場合
			else {
				from.setBodyEventResMessage(MessagePool.getMessage(from, MessagePool.Action.HateMoldyYukkuri),
						Const.HOLDMESSAGE, true, false);
				from.runAway(to.getX(), to.getY());
				from.setForceFace(ImageCode.PUFF.ordinal());
				if (from.getIntelligence() == Intelligence.FOOL)
					from.forceSetSick();
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
	public void saySadMessage(Body From, Body To) {
		Body from = YukkuriUtil.getBodyInstance(getFrom());
		if (from == null)
			return;
		String message = null;
		if (From.isParent(To)) {
			message = MessagePool.getMessage(from, MessagePool.Action.SadnessForMoldyChild);
		} else if (From.isPartner(To)) {
			message = MessagePool.getMessage(from, MessagePool.Action.SadnessForMoldyPartner);
		} else if (To.isParent(From)) {
			if (To.isFather(From))
				message = MessagePool.getMessage(from, MessagePool.Action.SadnessForMoldyFather);
			else
				message = MessagePool.getMessage(from, MessagePool.Action.SadnessForMoldyMother);
		} else if (To.isSister(From)) {
			if (To.getAge() >= From.getAge())
				message = MessagePool.getMessage(from, MessagePool.Action.SadnessForEldersister);
			else
				message = MessagePool.getMessage(from, MessagePool.Action.SadnessForMoldySister);
		}
		From.setBodyEventResMessage(message, Const.HOLDMESSAGE, true, SimYukkuri.RND.nextBoolean());
	}

	/**
	 * 謝罪する.
	 * 
	 * @param From イベント発生元
	 * @param To   イベント対象
	 */
	public void sayApologyMessage(Body From, Body To) {
		Body from = YukkuriUtil.getBodyInstance(getFrom());
		if (from == null)
			return;
		String message = null;
		if (From.isParent(To)) {
			message = MessagePool.getMessage(from, MessagePool.Action.ApologyToChild);
		} else if (To.isFamily(From)) {
			message = MessagePool.getMessage(from, MessagePool.Action.ApologyToFamily);
		}
		From.setBodyEventResMessage(message, Const.HOLDMESSAGE, true, SimYukkuri.RND.nextBoolean());
	}

	@Override
	public String toString() {
		return ResourceUtil.getInstance().read("event_mold");
	}
}