package src.event;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

import src.Const;
import src.SimYukkuri;
import src.base.Body;
import src.base.Obj;
import src.enums.ActionState;
import src.enums.ImageCode;
import src.enums.PublicRank;
import src.item.Barrier;
import src.system.MessagePool;

/**
 * 善良なゆっくりが主に参加し、ドスは捕食種がいる限り殺しに行く.
 */
public class KillPredeatorEvent extends RaperReactionEvent implements Serializable {
	
	private Random rnd = new Random();
	private int age = 0;

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

	/**
	 * 参加チェック
	 * ここで各種チェックを行い、イベントへ参加するかを返す
	 * また、イベント優先度も必要に応じて設定できる
	 */
	@Override
	public boolean checkEventResponse(Body b) {
		priority = EventPriority.HIGH;
		state = ActionState.ATTACK;
		// 自分自身はスキップ
		if (b == getFrom())
			return false;
		// 死体、睡眠、皮なし、目無しはスキップ
		if (!b.canEventResponse())
			return true;
		//非ゆっくり症、針刺し状態はスキップ
		if (b.isNYD() || b.isNeedled())
			return false;
		boolean bIsNearPreadeator = false;
		// 全ゆっくりに対してチェック
		ArrayList<Body> bodyList = SimYukkuri.world.currentMap.body;
		for (Body p:bodyList) {
			// 自分同士のチェックは無意味なのでスキップ
			if (p == b) {
				continue;
			}
			// 捕食種でないか、捕食種でも親子または姉妹であればスキップ
			if( !p.isPredatorType() || p.isParent(b) || b.isParent(p) || b.isSister(p) || b.isElderSister(p)){
				continue;
			}
			// 相手との間に壁があればスキップ
			if (Barrier.acrossBarrier(b.getX(), b.getY(), p.getX(), p.getY(), Barrier.MAP_BODY[b.getBodyAgeState().ordinal()]+Barrier.BARRIER_KEKKAI)) {
				continue;
			}
			bIsNearPreadeator = true;
			break;
		}
		// 捕食種が近くにいない
		if( !bIsNearPreadeator)
		{
			return false;
		}

		// うんうん奴隷は逃げる
		if (b.getPublicRank() == PublicRank.UnunSlave) {
			state = ActionState.ESCAPE;
		} else {
			if ((b.isAdult() && !b.isDontMove())) {
				// 動ける大人は母なら復讐に向かう
				state = ActionState.ATTACK;
			} else {
				// それ以外はひとまず逃げる
				state = ActionState.ESCAPE;
			}
		}
		return true;
	}

	/**
	 * 逃げるときのメッセージを設定する.
	 * @param b 逃げる個体
	 */
	@Override
	public void setScareWorldEventMessage(Body b) {
		b.setWorldEventResMessage(MessagePool.getMessage(b, MessagePool.Action.EscapeFromRemirya), Const.HOLDMESSAGE, true, false);
	}
	/**
	 * 反撃するときのメッセージを設定する.
	 * @param b 反撃する個体
	 */
	@Override
	public void setCounterWorldEventMessage(Body b) {
		b.setWorldEventResMessage(MessagePool.getMessage(b, MessagePool.Action.RevengeAttack), Const.HOLDMESSAGE, true, false);
	}

	/**
	 * 制裁されない条件。
	 * れいぱーに対するリアクションであれば、れいぱーであり続ける場合
	 * @return !制裁条件
	 */
	@Override
	public boolean checkConditionOfTarget() {
		return true;
	}

	/**
	 * 次のターゲットを探す.
	 * れいぱーに対するリアクションであれば、死んでない発情れいぱー
	 * @return 次のターゲット
	 */
	@Override
	public Body searchNextTarget() {
		Body ret = null;
		ArrayList<Body> bodyList = SimYukkuri.world.currentMap.body;
		for(Body b :bodyList) {
			if(b.isPredatorType()) {
				ret = b;
				break;
			}
		}
		return ret;
	}

	/**
	 * 次の攻撃ターゲットを探す.
	 * れいぱーに対するリアクションであれば、発情れいぱーですっきり中のやつ。
	 * @return 次の攻撃ターゲット
	 */
	@Override
	public Body searchAttackTarget() {
		return searchNextTarget();
	}
	
	@Override
	public UpdateState update(Body b) {
		// 相手が消えてしまったら他の捕食種を捜索
		if(getFrom().isRemoved() || getFrom().isDead() || !getFrom().isPredatorType() ) {
			setFrom(searchNextTarget());
			if(getFrom() == null) return UpdateState.ABORT;
		}
		
		if(state == ActionState.ATTACK) {
			b.setForceFace(ImageCode.PUFF.ordinal());
			moveTarget(b);
			if(rnd.nextInt(20) == 0) {
				b.setWorldEventResMessage(MessagePool.getMessage(b, MessagePool.Action.RevengeAttack), Const.HOLDMESSAGE, true, false);
			}
		}
		else {
			if((age % 10) == 0) {
				if(b.getType() == 2006 ||
					(b.isAdult() && b.getPublicRank() != PublicRank.UnunSlave)) {
					Body target = null;
					if(!checkConditionOfTarget()) {
						target = getFrom();
					}
					else {
						target = searchAttackTarget();
					}
					if(target != null) {
						// 反撃対象が見つかったら同イベント実行中の固体イベントを書き換え
						ArrayList<Body> bodyList = SimYukkuri.world.currentMap.body;
						for(Body body :bodyList) {
							if(body.getCurrentEvent() instanceof KillPredeatorEvent) {
								// うんうん奴隷は不参加
								if( body.getPublicRank() == PublicRank.UnunSlave ) continue;
								// 妊娠、大人以外は不参加.動けない場合も不参加
								if(body.hasBabyOrStalk() || body.isSick() || !body.isAdult() || body.isDontMove()) continue;
								KillPredeatorEvent ev = (KillPredeatorEvent)body.getCurrentEvent();
								ev.setFrom(target);
								ev.state = ActionState.ATTACK;
							}
						}
						setCounterWorldEventMessage(b);
					}
				}
			}
			else {
				// 逃げは敵と反対方向へ
				b.setForceFace(ImageCode.CRYING.ordinal());
				if((age % 10) == 0) {
					escapeTarget(b);
				}
				if(rnd.nextInt(20) == 0) {
					setScareWorldEventMessage(b);
				}
			}
		}
		age++;
		return null;
	}

}
