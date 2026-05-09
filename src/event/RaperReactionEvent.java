package src.event;
import src.util.GameView;
import src.util.GameMessages;
import src.util.GameText;

import java.util.Map;

import src.Const;
import src.SimYukkuri;
import src.util.GameRandom;
import src.util.GameWorld;
import src.base.Yukkuri;
import src.event.EventPacket;
import src.base.Entity;
import src.draw.Translate;
import src.enums.ActionState;
import src.enums.Attitude;
import src.enums.BurialState;
import src.enums.Direction;
import src.enums.EffectType;
import src.enums.Happiness;
import src.enums.ImageCode;
import src.enums.Intelligence;
import src.enums.PublicRank;
import src.field.impl.Barrier;
import src.logic.BodyLogic;
import src.logic.FamilyActionLogic;
import src.system.MessagePool;
import src.system.ResourceUtil;

/***************************************************
 * レイパー襲撃に対する反応イベント
 * protected Yukkuri from; // レイパー
 * protected Yukkuri getTo(); // 未使用
 * protected Entity target; // 未使用
 * protected int count; // 1
 */
public class RaperReactionEvent extends EventPacket {

	private static final long serialVersionUID = 4071981374906143863L;

	private int age = 0;

	private ActionState state = null;

	/**
	 * コンストラクタ.
	 */
	public RaperReactionEvent(Yukkuri fromBody, Yukkuri toBody, Entity targetObject, int count) {
		super(fromBody, toBody, targetObject, count);
	}

	public RaperReactionEvent() {

	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	// 参加チェック
	@Override
	public boolean checkEventResponse(Yukkuri body) {
		// 最低限のチェックはRaperWakeupEventで済んでるんで省略
		priority = EventPriority.HIGH;
		if (body.canflyCheck()) {
			// 飛べる固体
			return false;
		} else {
			boolean foundRaper = false;

			// 全ゆっくりに対してチェック
			for (Map.Entry<Integer, Yukkuri> entry : GameWorld.get().getCurrentMap().getBody().entrySet()) {
				Yukkuri predatorBody = entry.getValue();
				// 自分同士のチェックは無意味なのでスキップ
				if (predatorBody == body) {
					continue;
				}

				// 興奮したレイパーでなければスキップ
				if (!predatorBody.isRaper() && !predatorBody.isExciting() && !predatorBody.isDead()) {
					continue;
				}

				// 相手との間に壁があればスキップ
				if (Barrier.acrossBarrier(body.getX(), body.getY(), predatorBody.getX(), predatorBody.getY(),
						Barrier.MAP_BODY[body.getBodyAgeState().ordinal()] + Barrier.BARRIER_KEKKAI)) {
					continue;
				}

				foundRaper = true;
			}

			// レイパーが近くにいない
			if (!foundRaper) {
				return false;
			}

			// 埋まっていたら参加しない
			if (body.getBurialState() == BurialState.ALL || body.getBurialState() == BurialState.NEARLY_ALL) {
				return false;
			}

			// うんうん奴隷は逃げる
			if (body.getPublicRank() == PublicRank.UnunSlave) {
				state = ActionState.ESCAPE;
			} else {
				// 飛べない固体
				if ((body.isAdult() && !body.isDamaged() && !body.isSick() && !body.hasBabyOrStalk()
						&& (body.isSmart() && body.getIntelligence() == Intelligence.FOOL) && !body.isDontMove()) ||
						body.getType() == 2006) {
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
	public void start(Yukkuri body) {
		if (body.isNYD()) {
			return;
		}
		if (state == ActionState.ATTACK) {
			// 攻撃は敵に向かう
			moveTargetId(body);
			body.setAngry();
		} else {
			// 逃げは敵と反対方向へ
			escapeTarget(body);
			body.setHappiness(Happiness.VERY_SAD);
		}
	}

	public ActionState getState() {
		return state;
	}

	public void setState(ActionState state) {
		this.state = state;
	}

	// 毎フレーム処理
	@Override
	public UpdateState update(Yukkuri body) {
		Yukkuri sourceBody = src.util.BodyRegistry.getBodyInstance(getFrom());
		// 相手が消えてしまったら他のレイパーを捜索
		if (sourceBody == null || sourceBody.isRemoved() || sourceBody.isDead() || !sourceBody.isRaper()) {
			setFrom(searchNextTarget());
			if (sourceBody == null)
				return UpdateState.ABORT;
		}

		if (GameRandom.nextInt(500) == 0) {
			if (!FamilyActionLogic.isRapeTarget()) {
				return UpdateState.ABORT;
			}
		}

		if (state == ActionState.ATTACK) {
			// 妊娠したらドスでない限り逃げに変更
			if (body.hasBabyOrStalk() && body.getType() != 2006) {
				state = ActionState.ESCAPE;
			} else {
				// 攻撃は敵に向かう。ドスは妊娠させられようが何しようが駆除に向かう。
				body.setForceFace(ImageCode.PUFF.ordinal());
				moveTargetId(body);
				if (GameRandom.nextInt(20) == 0) {
					body.setWorldEventResMessage(GameMessages.getMessage(body, MessagePool.Action.AttackRapist),
							Const.HOLDMESSAGE, true, false);
				}
			}
		} else {
			// 賢い固体は反撃チェック
			if ((age % 10) == 0) {
				if (body.getType() == 2006 ||
						(body.isAdult() && body.getIntelligence() == Intelligence.WISE &&
								body.getPublicRank() != PublicRank.UnunSlave)) {
					Yukkuri targetBody = null;
					// 何らかの原因で発情が解除されたら制裁
					if (!checkConditionOfTarget()) {
						targetBody = sourceBody;
						if (targetBody.isDead()) {
							targetBody = searchAttackTarget();
						}
					} else {
						targetBody = searchAttackTarget();
					}
					if (targetBody != null) {
						int participantCount = 0;
						// 反撃対象が見つかったら同イベント実行中の固体イベントを書き換え
						for (Map.Entry<Integer, Yukkuri> entry : GameWorld.get().getCurrentMap().getBody().entrySet()) {
							Yukkuri eventBody = entry.getValue();
							if (eventBody.getCurrentEvent() instanceof RaperReactionEvent) {
								// うんうん奴隷は不参加
								if (eventBody.getPublicRank() == PublicRank.UnunSlave)
									continue;
								// 妊娠、大人以外は不参加.動けない場合も不参加
								if (eventBody.hasBabyOrStalk() || eventBody.isSick() || !eventBody.isAdult() || eventBody.isDontMove())
									continue;
								// ドゲスは不参加、善良ほど参加しやすく
								if (eventBody.getAttitude() == Attitude.SUPER_SHITHEAD)
									participantCount = 1;
								else if (eventBody.getAttitude() == Attitude.SHITHEAD)
									participantCount = GameRandom.nextInt(3);
								else if (eventBody.getAttitude() == Attitude.AVERAGE)
									participantCount = GameRandom.nextInt(2);
								else
									participantCount = 0;
								// ドスは常に参加。ドスはとにかく群れをゆっくりさせるため、れいぱー駆除に命をかける
								if (eventBody.getType() == 2006) {
									participantCount = 0;
								}
								if (participantCount == 0) {
									RaperReactionEvent ev = (RaperReactionEvent) eventBody.getCurrentEvent();
									ev.setFrom(targetBody);
									ev.state = ActionState.ATTACK;
								}
							}
						}
						setCounterWorldEventMessage(body);
					} else {
						// れいぱーがもういない
						return UpdateState.ABORT;
					}
				}
			} else {
				// 逃げは敵と反対方向へ
				body.setForceFace(ImageCode.CRYING.ordinal());
				if ((age % 10) == 0) {
					escapeTarget(body);
				}
				if (GameRandom.nextInt(20) == 0) {
					setScareWorldEventMessage(body);
				}
			}
		}
		age++;
		return null;
	}

	/**
	 * 逃げるときのメッセージを設定する.
	 * 
	 * @param body 逃げる個体
	 */
	public void setScareWorldEventMessage(Yukkuri body) {
		body.setWorldEventResMessage(GameMessages.getMessage(body, MessagePool.Action.ScareRapist), Const.HOLDMESSAGE, true,
				false);
	}

	/**
	 * 反撃するときのメッセージを設定する.
	 * 
	 * @param body 反撃する個体
	 */
	public void setCounterWorldEventMessage(Yukkuri body) {
		body.setWorldEventResMessage(GameMessages.getMessage(body, MessagePool.Action.CounterRapist), Const.HOLDMESSAGE, true,
				false);
	}

	/**
	 * 制裁されない条件。
	 * れいぱーに対するリアクションであれば、れいぱーであり続ける場合
	 * 
	 * @return !制裁条件
	 */
	public boolean checkConditionOfTarget() {
		Yukkuri sourceBody = src.util.BodyRegistry.getBodyInstance(getFrom());
		if (sourceBody == null) {
			setFrom(-1);
			return false;
		}
		return sourceBody.isExciting();
	}

	// イベント目標に到着した際に呼ばれる
	// trueを返すとイベント終了
	@Override
	public boolean execute(Yukkuri body) {
		Yukkuri sourceBody = src.util.BodyRegistry.getBodyInstance(getFrom());
		// 相手が消えてしまったら他のレイパーを捜索
		if (sourceBody == null || sourceBody.isRemoved() || sourceBody.isDead()) {
			setFrom(searchNextTarget());
			sourceBody = src.util.BodyRegistry.getBodyInstance(getFrom());
			// レイパー全滅でイベント終了
			if (sourceBody == null)
				return true;
			return false;
		}

		if (state == ActionState.ATTACK && !body.isDontMove()) {
			// 攻撃
			if (sourceBody.getZ() < 5) {
				body.setWorldEventResMessage(GameMessages.getMessage(body, MessagePool.Action.RevengeAttack),
						Const.HOLDMESSAGE, true, false);
				if (body.getDirection() == Direction.LEFT) {
					GameView.addEffect(EffectType.HIT, body.getX() - 10, body.getY(), 0,
							0, 0, 0, false, 500, 1, true, false, true);
				} else {
					GameView.addEffect(EffectType.HIT, body.getX() + 10, body.getY(), 0,
							0, 0, 0, true, 500, 1, true, false, true);
				}
				body.setForceFace(ImageCode.PUFF.ordinal());
				sourceBody.strikeByYukkuri(body, this, false);
				body.addStress(-300);
			}
		} else {
			// 逃げ
			escapeTarget(body);
			if (GameRandom.nextInt(20) == 0) {
				setScareWorldEventMessage(body);
			}
		}
		return false;
	}

	/**
	 * 次のターゲットを探す.
	 * れいぱーに対するリアクションであれば、死んでない発情れいぱー
	 * 
	 * @return 次のターゲット
	 */
	public Yukkuri searchNextTarget() {
		Yukkuri nextTargetBody = null;
		for (Map.Entry<Integer, Yukkuri> entry : GameWorld.get().getCurrentMap().getBody().entrySet()) {
			Yukkuri candidateBody = entry.getValue();
			if (candidateBody.isRaper() && candidateBody.isExciting() && !candidateBody.isDead()) {
				nextTargetBody = candidateBody;
				break;
			}
		}
		return nextTargetBody;
	}

	/**
	 * 次の攻撃ターゲットを探す.
	 * れいぱーに対するリアクションであれば、発情れいぱーですっきり中のやつ。
	 * 
	 * @return 次の攻撃ターゲット
	 */
	public Yukkuri searchAttackTarget() {
		Yukkuri attackTargetBody = null;
		for (Map.Entry<Integer, Yukkuri> entry : GameWorld.get().getCurrentMap().getBody().entrySet()) {
			Yukkuri candidateBody = entry.getValue();
			if (!candidateBody.isDead() && candidateBody.isExciting() && candidateBody.isRaper() && candidateBody.isSukkiri()) {
				attackTargetBody = candidateBody;
				break;
			}
		}
		return attackTargetBody;
	}

	/**
	 * ターゲットまで移動する.
	 * 
	 * @param body ターゲット
	 */
	public void moveTargetId(Yukkuri body) {
		Yukkuri sourceBody = src.util.BodyRegistry.getBodyInstance(getFrom());
		if (sourceBody == null) {
			return;
		}
		int collisionX = BodyLogic.calcCollisionX(body, sourceBody);
		body.moveToEvent(this, sourceBody.getX() + collisionX, sourceBody.getY());
	}

	/**
	 * 敵から逃げるように移動する.
	 * 
	 * @param body 敵
	 */
	protected void escapeTarget(Yukkuri body) {
		int mapX = Translate.getMapW();
		int mapY = Translate.getMapH();
		Yukkuri sourceBody = src.util.BodyRegistry.getBodyInstance(getFrom());
		if (sourceBody == null) {
			return;
		}
		int vx = body.getX() - sourceBody.getX();
		if (body.getX() < 2) {
			vx = mapX;
		} else if (body.getX() > mapX - 2) {
			vx = 0;
		} else {
			if (vx > 0)
				vx = mapX;
			else
				vx = 0;
		}
		int vy = body.getY() - sourceBody.getY();
		if (body.getY() < 2) {
			vy = mapY;
		} else if (body.getY() > mapY - 2) {
			vy = 0;
		} else {
			if (vy > 0)
				vy = mapY;
			else
				vy = 0;
		}
		body.moveToEvent(this, vx, vy);
	}

	@Override
	public String toString() {
		return GameText.read("event_raperreaction");
	}
}
