package src.event;

import src.Const;
import src.SimYukkuri;
import src.base.Body;
import src.base.EventPacket;
import src.base.Obj;
import src.draw.Translate;
import src.enums.Direction;
import src.enums.EffectType;
import src.enums.Happiness;
import src.enums.ImageCode;
import src.logic.BodyLogic;
import src.system.MessagePool;
import src.system.ResourceUtil;
import src.util.YukkuriUtil;

/***************************************************
	ゆっくりが攻撃されたときの反撃イベント
	protected Body from;			// イベントを発した個体
	protected Body to;				// 攻撃対象
	protected Obj target;			// 未使用
	protected int count;			// 1
*/
public class RevengeAttackEvent extends EventPacket implements java.io.Serializable {

	private static final long serialVersionUID = -7412180348011586698L;

	/**
	 * コンストラクタ.
	 */
	public RevengeAttackEvent(Body f, Body t, Obj tgt, int cnt) {
		super(f, t, tgt, cnt);
	}
	
	public RevengeAttackEvent() {
		
	}

	// 参加チェック
	@Override
	public boolean checkEventResponse(Body b) {
		priority = EventPriority.HIGH;
		// これは特殊な扱いをするイベントで先に条件をチェックしてから
		// 自分自身のリストに登録するので無条件にtrue
		return true;
	}

	// イベント開始動作
	@Override
	public void start(Body b) {
		Body to = YukkuriUtil.getBodyInstance(getTo());
		b.setToFood(false);
		b.setToBed(false);
		b.setToShit(false);
		b.setToSteal(false);
		b.setToSukkiri(false);
		b.setToTakeout(true);
		b.setWakeUpTime(b.getAge());//眠気が覚める
		if (to != null) {
			int colX = BodyLogic.calcCollisionX(b, to);
			b.moveToEvent(this, to.getX() + colX, to.getY());
		}
	}

	// 毎フレーム処理
	// UpdateState.ABORTを返すとイベント終了
	@Override
	public UpdateState update(Body b) {
		Body to = YukkuriUtil.getBodyInstance(getTo());
		// 相手が消えてしまったらイベント中断
		if (to == null || to.isRemoved() || to.isTaken())
			return UpdateState.ABORT;
		// 相手に追いつけないケースがあるため、一定距離まで近づいたら相手を呼び止める
		if (Translate.distance(b.getX(), b.getY(), to.getX(), to.getY()) < 2500) {
			to.stay();
		}
		int colX = BodyLogic.calcCollisionX(b, to);
		b.moveToEvent(this, to.getX() + colX, to.getY());
		return null;
	}

	// イベント目標に到着した際に呼ばれる
	// trueを返すとイベント終了
	@Override
	public boolean execute(Body b) {
		// 動けない場合と、ランダムであきらめる
		if (b.isDontMove() || SimYukkuri.RND.nextInt(50) == 0) {
			b.setMessage(MessagePool.getMessage(b, MessagePool.Action.LamentNoYukkuri), 40, true, true);
			b.setHappiness(Happiness.SAD);
			return true;
		}
		Body to = YukkuriUtil.getBodyInstance(getTo());
		// 相手が残っていたら攻撃
		if (to != null && !to.isRemoved() && to.getZ() < 5) {
			b.setWorldEventResMessage(MessagePool.getMessage(b, MessagePool.Action.RevengeAttack), Const.HOLDMESSAGE,
					true, false);
			if (b.getDirection() == Direction.LEFT) {
				SimYukkuri.mypane.getTerrarium().addEffect(EffectType.HIT, b.getX() - 10, b.getY(), 0,
						0, 0, 0, false, 500, 1, true, false, true);
			} else {
				SimYukkuri.mypane.getTerrarium().addEffect(EffectType.HIT, b.getX() + 10, b.getY(), 0,
						0, 0, 0, true, 500, 1, true, false, true);
			}
			b.setForceFace(ImageCode.PUFF.ordinal());
			to.strikeByYukkuri(b, this, false);
			b.addStress(-500);
		}
		return true;
	}

	@Override
	public String toString() {
		return ResourceUtil.getInstance().read("event_revenge");
	}
}