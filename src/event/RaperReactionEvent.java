package src.event;

import java.util.Map;

import src.Const;
import src.SimYukkuri;
import src.base.Body;
import src.base.EventPacket;
import src.base.Obj;
import src.draw.Translate;
import src.enums.ActionState;
import src.enums.Attitude;
import src.enums.BaryInUGState;
import src.enums.Direction;
import src.enums.EffectType;
import src.enums.Happiness;
import src.enums.ImageCode;
import src.enums.Intelligence;
import src.enums.PublicRank;
import src.item.Barrier;
import src.logic.BodyLogic;
import src.logic.FamilyActionLogic;
import src.system.MessagePool;
import src.system.ResourceUtil;
import src.util.YukkuriUtil;

/***************************************************
	レイパー襲撃に対する反応イベント
	protected Body from;			// レイパー
	protected Body getTo();				// 未使用
	protected Obj target;			// 未使用
	protected int count;			// 1
*/
public class RaperReactionEvent extends EventPacket implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private int age = 0;

	public ActionState state = null;

	/**
	 * コンストラクタ.
	 */
	public RaperReactionEvent(Body f, Body t, Obj tgt, int cnt) {
		super(f, t, tgt, cnt);
	}
	
	public RaperReactionEvent() {
		
	}

	// 参加チェック
	@Override
	public boolean checkEventResponse(Body b) {
		// 最低限のチェックはRaperWakeupEventで済んでるんで省略
		priority = EventPriority.HIGH;
		if (b.canflyCheck()) {
			// 飛べる固体
			return false;
		} else {
			boolean bIsNearRaper = false;

			// 全ゆっくりに対してチェック
			for (Map.Entry<Integer, Body> entry : SimYukkuri.world.getCurrentMap().body.entrySet()) {
				Body p = entry.getValue();
				// 自分同士のチェックは無意味なのでスキップ
				if (p == b) {
					continue;
				}

				// 興奮したレイパーでなければスキップ
				if (!p.isRaper() && !p.isExciting() && !p.isDead()) {
					continue;
				}

				// 相手との間に壁があればスキップ
				if (Barrier.acrossBarrier(b.getX(), b.getY(), p.getX(), p.getY(),
						Barrier.MAP_BODY[b.getBodyAgeState().ordinal()] + Barrier.BARRIER_KEKKAI)) {
					continue;
				}

				bIsNearRaper = true;
			}

			// レイパーが近くにいない
			if (!bIsNearRaper) {
				return false;
			}

			// 埋まっていたら参加しない
			if (b.getBaryState() == BaryInUGState.ALL || b.getBaryState() == BaryInUGState.NEARLY_ALL) {
				return false;
			}

			// うんうん奴隷は逃げる
			if (b.getPublicRank() == PublicRank.UnunSlave) {
				state = ActionState.ESCAPE;
			} else {
				// 飛べない固体
				if ((b.isAdult() && !b.isDamaged() && !b.isSick() && !b.hasBabyOrStalk()
						&& (b.isSmart() && b.getIntelligence() == Intelligence.FOOL) && !b.isDontMove()) ||
						b.getType() == 2006) {
					// 健康でバカな善良な大人（またはドスまりさは状態に限らず常に）迎撃に向かう
					state = ActionState.ATTACK;
				} else {
					// それ以外はひとまず逃げる
					state = ActionState.ESCAPE;
				}
			}
		}
		return true;
	}

	// イベント開始動作
	@Override
	public void start(Body b) {
		if (b.isNYD()) {
			return;
		}
		if (state == ActionState.ATTACK) {
			// 攻撃は敵に向かう
			moveTarget(b);
			b.setAngry();
		} else {
			// 逃げは敵と反対方向へ
			escapeTarget(b);
			b.setHappiness(Happiness.VERY_SAD);
		}
	}

	// 毎フレーム処理
	@Override
	public UpdateState update(Body b) {
		Body from = YukkuriUtil.getBodyInstance(getFrom());
		// 相手が消えてしまったら他のレイパーを捜索
		if (from == null || from.isRemoved() || from.isDead() || !from.isRaper()) {
			setFrom(searchNextTarget());
			if (from == null)
				return UpdateState.ABORT;
		}

		if (SimYukkuri.RND.nextInt(500) == 0) {
			if (!FamilyActionLogic.isRapeTarget()) {
				return UpdateState.ABORT;
			}
		}

		if (state == ActionState.ATTACK) {
			// 妊娠したらドスでない限り逃げに変更
			if (b.hasBabyOrStalk() && b.getType() != 2006) {
				state = ActionState.ESCAPE;
			} else {
				// 攻撃は敵に向かう。ドスは妊娠させられようが何しようが駆除に向かう。
				b.setForceFace(ImageCode.PUFF.ordinal());
				moveTarget(b);
				if (SimYukkuri.RND.nextInt(20) == 0) {
					b.setWorldEventResMessage(MessagePool.getMessage(b, MessagePool.Action.AttackRapist),
							Const.HOLDMESSAGE, true, false);
				}
			}
		} else {
			// 賢い固体は反撃チェック
			if ((age % 10) == 0) {
				if (b.getType() == 2006 ||
						(b.isAdult() && b.getIntelligence() == Intelligence.WISE &&
								b.getPublicRank() != PublicRank.UnunSlave)) {
					Body target = null;
					// 何らかの原因で発情が解除されたら制裁
					if (!checkConditionOfTarget()) {
						target = from;
						if (target.isDead()) {
							target = searchAttackTarget();
						}
					} else {
						target = searchAttackTarget();
					}
					if (target != null) {
						int num = 0;
						// 反撃対象が見つかったら同イベント実行中の固体イベントを書き換え
						for (Map.Entry<Integer, Body> entry : SimYukkuri.world.getCurrentMap().body.entrySet()) {
							Body body = entry.getValue();
							if (body.getCurrentEvent() instanceof RaperReactionEvent) {
								// うんうん奴隷は不参加
								if (body.getPublicRank() == PublicRank.UnunSlave)
									continue;
								// 妊娠、大人以外は不参加.動けない場合も不参加
								if (body.hasBabyOrStalk() || body.isSick() || !body.isAdult() || body.isDontMove())
									continue;
								// ドゲスは不参加、善良ほど参加しやすく
								if (body.getAttitude() == Attitude.SUPER_SHITHEAD)
									num = 1;
								else if (body.getAttitude() == Attitude.SHITHEAD)
									num = SimYukkuri.RND.nextInt(3);
								else if (body.getAttitude() == Attitude.AVERAGE)
									num = SimYukkuri.RND.nextInt(2);
								else
									num = 0;
								//ドスは常に参加。ドスはとにかく群れをゆっくりさせるため、れいぱー駆除に命をかける
								if (body.getType() == 2006) {
									num = 0;
								}
								if (num == 0) {
									RaperReactionEvent ev = (RaperReactionEvent) body.getCurrentEvent();
									ev.setFrom(target);
									ev.state = ActionState.ATTACK;
								}
							}
						}
						setCounterWorldEventMessage(b);
					} else {
						// れいぱーがもういない
						return UpdateState.ABORT;
					}
				}
			} else {
				// 逃げは敵と反対方向へ
				b.setForceFace(ImageCode.CRYING.ordinal());
				if ((age % 10) == 0) {
					escapeTarget(b);
				}
				if (SimYukkuri.RND.nextInt(20) == 0) {
					setScareWorldEventMessage(b);
				}
			}
		}
		age++;
		return null;
	}

	/**
	 * 逃げるときのメッセージを設定する.
	 * @param b 逃げる個体
	 */
	public void setScareWorldEventMessage(Body b) {
		b.setWorldEventResMessage(MessagePool.getMessage(b, MessagePool.Action.ScareRapist), Const.HOLDMESSAGE, true,
				false);
	}

	/**
	 * 反撃するときのメッセージを設定する.
	 * @param b 反撃する個体
	 */
	public void setCounterWorldEventMessage(Body b) {
		b.setWorldEventResMessage(MessagePool.getMessage(b, MessagePool.Action.CounterRapist), Const.HOLDMESSAGE, true,
				false);
	}

	/**
	 * 制裁されない条件。
	 * れいぱーに対するリアクションであれば、れいぱーであり続ける場合
	 * @return !制裁条件
	 */
	public boolean checkConditionOfTarget() {
		Body from = YukkuriUtil.getBodyInstance(getFrom());
		if(from == null) {
			setFrom(-1);
			return false;
		}
		return from.isExciting();
	}

	// イベント目標に到着した際に呼ばれる
	// trueを返すとイベント終了
	@Override
	public boolean execute(Body b) {
		Body from = YukkuriUtil.getBodyInstance(getFrom());
		// 相手が消えてしまったら他のレイパーを捜索
		if (from == null || from.isRemoved() || from.isDead()) {
			setFrom(searchNextTarget());
			from = YukkuriUtil.getBodyInstance(getFrom());
			// レイパー全滅でイベント終了
			if (from == null)
				return true;
			return false;
		}

		if (state == ActionState.ATTACK && !b.isDontMove()) {
			// 攻撃
			if (from.getZ() < 5) {
				b.setWorldEventResMessage(MessagePool.getMessage(b, MessagePool.Action.RevengeAttack),
						Const.HOLDMESSAGE, true, false);
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
		} else {
			// 逃げ
			escapeTarget(b);
			if (SimYukkuri.RND.nextInt(20) == 0) {
				setScareWorldEventMessage(b);
			}
		}
		return false;
	}

	/**
	 * 次のターゲットを探す.
	 * れいぱーに対するリアクションであれば、死んでない発情れいぱー
	 * @return 次のターゲット
	 */
	public Body searchNextTarget() {
		Body ret = null;
		for (Map.Entry<Integer, Body> entry : SimYukkuri.world.getCurrentMap().body.entrySet()) {
			Body b = entry.getValue();
			if (b.isRaper() && b.isExciting() && !b.isDead()) {
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
	public Body searchAttackTarget() {
		Body ret = null;
		for (Map.Entry<Integer, Body> entry : SimYukkuri.world.getCurrentMap().body.entrySet()) {
			Body b = entry.getValue();
			if (!b.isDead()&& b.isExciting() && b.isRaper() && b.isSukkiri()) {
				ret = b;
				break;
			}
		}
		return ret;
	}

	/**
	 * ターゲットまで移動する.
	 * @param b ターゲット
	 */
	public void moveTarget(Body b) {
		Body from = YukkuriUtil.getBodyInstance(getFrom());
		if (from == null) {
			return;
		}
		int colX = BodyLogic.calcCollisionX(b, from);
		b.moveToEvent(this, from.getX() + colX, from.getY());
	}

	/**
	 *  敵から逃げるように移動する.
	 * @param b 敵
	 */
	protected void escapeTarget(Body b) {
		int mapX = Translate.mapW;
		int mapY = Translate.mapH;
		Body from = YukkuriUtil.getBodyInstance(getFrom());
		if (from == null) {
			return;
		}
		int vx = b.getX() - from.getX();
		if (b.getX() < 2) {
			vx = mapX;
		} else if (b.getX() > mapX - 2) {
			vx = 0;
		} else {
			if (vx > 0)
				vx = mapX;
			else
				vx = 0;
		}
		int vy = b.getY() - from.getY();
		if (b.getY() < 2) {
			vy = mapY;
		} else if (b.getY() > mapY - 2) {
			vy = 0;
		} else {
			if (vy > 0)
				vy = mapY;
			else
				vy = 0;
		}
		b.moveToEvent(this, vx, vy);
	}

	@Override
	public String toString() {
		return ResourceUtil.getInstance().read("event_raperreaction");
	}
}
