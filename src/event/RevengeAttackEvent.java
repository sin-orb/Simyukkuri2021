package src.event;
import src.util.GameView;
import src.util.GameMessages;
import src.util.GameText;

import src.Const;
import src.SimYukkuri;
import src.util.GameRandom;
import src.base.Body;
import src.event.EventPacket;
import src.base.Obj;
import src.draw.Translate;
import src.enums.Direction;
import src.enums.EffectType;
import src.enums.Happiness;
import src.enums.ImageCode;
import src.logic.BodyLogic;
import src.system.MessagePool;
import src.system.ResourceUtil;

/***************************************************
 * ゆっくりが攻撃されたときの反撃イベント
 * protected Body from; // イベントを発した個体
 * protected Body to; // 攻撃対象
 * protected Obj target; // 未使用
 * protected int count; // 1
 */
public class RevengeAttackEvent extends EventPacket {

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
	public boolean checkEventResponse(Body body) {
		priority = EventPriority.HIGH;
		// これは特殊な扱いをするイベントで先に条件をチェックしてから
		// 自分自身のリストに登録するので無条件にtrue
		return true;
	}

	// イベント開始動作
	@Override
	public void start(Body body) {
		Body targetBody = src.util.BodyRegistry.getBodyInstance(getTo());
		body.setToFood(false);
		body.setToBed(false);
		body.setToShit(false);
		body.setToSteal(false);
		body.setToSukkiri(false);
		body.setToTakeout(true);
		body.setWakeUpTime(body.getAge());// 眠気が覚める
		if (targetBody != null) {
			int colX = BodyLogic.calcCollisionX(body, targetBody);
			body.moveToEvent(this, targetBody.getX() + colX, targetBody.getY());
		}
	}

	// 毎フレーム処理
	// UpdateState.ABORTを返すとイベント終了
	@Override
	public UpdateState update(Body body) {
		Body targetBody = src.util.BodyRegistry.getBodyInstance(getTo());
		// 相手が消えてしまったらイベント中断
		if (targetBody == null || targetBody.isRemoved() || targetBody.isTaken())
			return UpdateState.ABORT;
		// 相手に追いつけないケースがあるため、一定距離まで近づいたら相手を呼び止める
		if (Translate.distance(body.getX(), body.getY(), targetBody.getX(), targetBody.getY()) < 2500) {
			targetBody.stay();
		}
		int colX = BodyLogic.calcCollisionX(body, targetBody);
		body.moveToEvent(this, targetBody.getX() + colX, targetBody.getY());
		return null;
	}

	// イベント目標に到着した際に呼ばれる
	// trueを返すとイベント終了
	@Override
	public boolean execute(Body body) {
		// 動けない場合と、ランダムであきらめる
		if (body.isDontMove() || GameRandom.nextInt(50) == 0) {
			body.setMessage(GameMessages.getMessage(body, MessagePool.Action.LamentNoYukkuri), 40, true, true);
			body.setHappiness(Happiness.SAD);
			return true;
		}
		Body targetBody = src.util.BodyRegistry.getBodyInstance(getTo());
		// 相手が残っていたら攻撃
		if (targetBody != null && !targetBody.isRemoved() && targetBody.getZ() < 5) {
			body.setWorldEventResMessage(GameMessages.getMessage(body, MessagePool.Action.RevengeAttack), Const.HOLDMESSAGE,
					true, false);
			if (body.getDirection() == Direction.LEFT) {
				GameView.addEffect(EffectType.HIT, body.getX() - 10, body.getY(), 0,
						0, 0, 0, false, 500, 1, true, false, true);
			} else {
				GameView.addEffect(EffectType.HIT, body.getX() + 10, body.getY(), 0,
						0, 0, 0, true, 500, 1, true, false, true);
			}
			body.setForceFace(ImageCode.PUFF.ordinal());
			targetBody.strikeByYukkuri(body, this, false);
			body.addStress(-500);
		}
		return true;
	}

	@Override
	public String toString() {
		return GameText.read("event_revenge");
	}
}
