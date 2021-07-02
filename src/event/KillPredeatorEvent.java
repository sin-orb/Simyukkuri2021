package src.event;

import java.io.Serializable;
import java.util.Map;

import src.Const;
import src.SimYukkuri;
import src.base.Body;
import src.base.Obj;
import src.enums.Direction;
import src.enums.EffectType;
import src.enums.ImageCode;
import src.enums.PublicRank;
import src.item.Barrier;
import src.logic.BodyLogic;
import src.system.MessagePool;
import src.system.ResourceUtil;
import src.util.YukkuriUtil;

/**
 * 善良なゆっくりが主に参加し、ドスは捕食種がいる限り殺しに行く.
 */
public class KillPredeatorEvent extends RevengeAttackEvent implements Serializable {

	/**
	 * コンストラクタ.
	 * @param f イベントを発した個体
	 * @param t 攻撃対象の捕食種
	 * @param tgt 未使用
	 * @param cnt 10
	 */
	public KillPredeatorEvent(Body f, Body t, Obj tgt, int cnt) {
		super(f, t, tgt, cnt);
	}
	
	public KillPredeatorEvent() {
		
	}

	/**
	 * 参加チェック
	 * ここで各種チェックを行い、イベントへ参加するかを返す
	 * また、イベント優先度も必要に応じて設定できる
	 */
	@Override
	public boolean checkEventResponse(Body b) {
		priority = EventPriority.HIGH;
		// 死体、睡眠、皮なし、目無しはスキップ
		if (!b.canEventResponse())
			return true;
		//非ゆっくり症、針刺し状態はスキップ
		if (b.isNYD() || b.isNeedled())
			return false;
		boolean bIsNearPreadeator = false;
		// 全ゆっくりに対してチェック
		for (Map.Entry<Integer, Body> entry : SimYukkuri.world.getCurrentMap().body.entrySet()) {
			Body p = entry.getValue();
			// 自分同士のチェックは無意味なのでスキップ
			if (p == b) {
				continue;
			}
			// 捕食種でないか、捕食種でも親子または姉妹であればスキップ
			if (!p.isPredatorType() || p.isParent(b) || b.isParent(p) || b.isSister(p) || b.isElderSister(p)) {
				continue;
			}
			// 相手との間に壁があればスキップ
			if (Barrier.acrossBarrier(b.getX(), b.getY(), p.getX(), p.getY(),
					Barrier.MAP_BODY[b.getBodyAgeState().ordinal()] + Barrier.BARRIER_KEKKAI)) {
				continue;
			}
			bIsNearPreadeator = true;
			break;
		}
		// 捕食種が近くにいない
		if (!bIsNearPreadeator) {
			return false;
		}
		b.clearActionsForEvent();
		return true;
	}

	/**
	 * 次のターゲットを探す.
	 * @return 次のターゲット
	 */
	public Body searchNextTarget() {
		Body ret = null;
		for (Map.Entry<Integer, Body> entry : SimYukkuri.world.getCurrentMap().body.entrySet()) {
			Body b = entry.getValue();
			if (b.isPredatorType()) {
				ret = b;
				break;
			}
		}
		return ret;
	}

	@Override
	public UpdateState update(Body b) {
		// ランダムで復讐を諦める
		if (SimYukkuri.RND.nextInt(1000) == 0) {
			return UpdateState.ABORT;
		}
		Body from = YukkuriUtil.getBodyInstance(getFrom());
		// 相手が消えてしまったら他の捕食種を捜索
		if (from.isRemoved() || from.isDead() || !from.isPredatorType()) {
			setFrom(searchNextTarget());
			from = YukkuriUtil.getBodyInstance(getFrom());
			if (from == null)
				return UpdateState.ABORT;
		}

		b.setForceFace(ImageCode.PUFF.ordinal());
		int colX = BodyLogic.calcCollisionX(b, from);
		b.moveToEvent(this, from.getX() + colX, from.getY());
		if (b.getType() == 2006 ||
				(b.isAdult() && b.getPublicRank() != PublicRank.UnunSlave)) {
			Body target = searchNextTarget();
			setFrom(target);
			b.setWorldEventResMessage(MessagePool.getMessage(b, MessagePool.Action.RevengeForChild),
					Const.HOLDMESSAGE, true, false);
		}
		return null;
	}

	@Override
	public void start(Body b) {
		if (b.isNYD()) {
			return;
		}
		b.setAngry();
		Body from = YukkuriUtil.getBodyInstance(getFrom());
		int colX = BodyLogic.calcCollisionX(b, from);
		b.moveToEvent(this, from.getX() + colX, from.getY());
	}

	@Override
	public boolean execute(Body b) {
		Body from = YukkuriUtil.getBodyInstance(getFrom());
		// 相手が消えてしまったら他の捕食種を捜索
		if (from == null || from.isRemoved() || from.isDead()) {
			setFrom(searchNextTarget());
			from = YukkuriUtil.getBodyInstance(getFrom());
			// 捕食種全滅でイベント終了
			if (from == null)
				return true;
			return false;
		}
		if (from.getZ() < 5) {
			b.setWorldEventResMessage(MessagePool.getMessage(b, MessagePool.Action.RevengeForChild), Const.HOLDMESSAGE,
					true, false);
			if (b.getDirection() == Direction.LEFT) {
				SimYukkuri.mypane.terrarium.addEffect(EffectType.HIT, b.getX() - 10, b.getY(), 0,
						0, 0, 0, false, 500, 1, true, false, true);
			} else {
				SimYukkuri.mypane.terrarium.addEffect(EffectType.HIT, b.getX() + 10, b.getY(), 0,
						0, 0, 0, true, 500, 1, true, false, true);
			}
			b.setForceFace(ImageCode.PUFF.ordinal());
			from.strikeByYukkuri(b, this, false);
			b.addStress(-300);
		}
		return false;
	}

	@Override
	public String toString() {
		return ResourceUtil.getInstance().read("event_killremi");
	}
}
