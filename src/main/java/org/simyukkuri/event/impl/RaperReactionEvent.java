package org.simyukkuri.event.impl;

import java.util.Map;

import org.simyukkuri.Const;
import org.simyukkuri.draw.Translate;
import org.simyukkuri.entity.core.Entity;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.enums.ActionState;
import org.simyukkuri.enums.Attitude;
import org.simyukkuri.enums.BurialState;
import org.simyukkuri.enums.Direction;
import org.simyukkuri.enums.EffectType;
import org.simyukkuri.enums.Happiness;
import org.simyukkuri.enums.ImageCode;
import org.simyukkuri.enums.Intelligence;
import org.simyukkuri.enums.PublicRank;
import org.simyukkuri.enums.YukkuriType;
import org.simyukkuri.event.EventPacket;
import org.simyukkuri.event.EventPacket.EventPriority;
import org.simyukkuri.event.EventPacket.UpdateState;
import org.simyukkuri.field.impl.Barrier;
import org.simyukkuri.logic.YukkuriLogic;
import org.simyukkuri.logic.FamilyActionLogic;
import org.simyukkuri.system.MessagePool;
import org.simyukkuri.util.GameMessages;
import org.simyukkuri.util.GameRandom;
import org.simyukkuri.util.GameText;
import org.simyukkuri.util.GameView;
import org.simyukkuri.util.GameWorld;

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

	/** Jackson デシリアライズ用デフォルトコンストラクタ。 */
	public RaperReactionEvent() {

	}

	/** ゆっくりの年齢（ティック）を返す。 */
	public int getAge() {
		return age;
	}

	/** ゆっくりの年齢をセットする。 */
	public void setAge(int age) {
		this.age = age;
	}

	// 参加チェック
	/** イベントへの参加可否を判定し、参加可能なら true を返す。 */
	@Override
	public boolean checkEventResponse(Yukkuri body) {
		// 最低限のチェックはRaperWakeupEventで済んでるんで省略
		setHighPriority();
		if (body.canflyCheck()) {
			// 飛べる固体
			return false;
		} else {
			boolean foundRaper = false;

			// 全ゆっくりに対してチェック
			for (Map.Entry<Integer, Yukkuri> entry : GameWorld.get().getCurrentWorldState().getYukkuriRegistry().entrySet()) {
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
						Barrier.BODY_BLOCK_FLAGS[body.getAgeState().ordinal()] + Barrier.BARRIER_KEKKAI)) {
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
			if (body.getPublicRank() == PublicRank.UNUN_SLAVE) {
				state = ActionState.ESCAPE;
			} else {
				// 飛べない固体
				if ((body.isAdult() && !body.isDamaged() && !body.isSick() && !body.hasBabyOrStalk()
						&& (body.isSmart() && body.getIntelligence() == Intelligence.FOOL) && !body.isDontMove()) ||
						body.getType() == YukkuriType.DOSMARISA) {
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
	/** イベントの開始処理を実行する。 */
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

	/** イベントの進行ステートを返す。 */
	public ActionState getState() {
		return state;
	}

	/** イベントの進行ステートをセットする。 */
	public void setState(ActionState state) {
		this.state = state;
	}

	// 毎フレーム処理
	/** 毎ティック状態を更新する。 */
	@Override
	public UpdateState update(Yukkuri body) {
		Yukkuri sourceBody = org.simyukkuri.util.YukkuriLookup.getYukkuriById(getFrom());
		// 相手が消えてしまったら他のレイパーを捜索
		if (sourceBody == null || sourceBody.isRemoved() || sourceBody.isDead() || !sourceBody.isRaper()) {
			setFrom(searchNextTarget());
			sourceBody = org.simyukkuri.util.YukkuriLookup.getYukkuriById(getFrom());
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
			if (body.hasBabyOrStalk() && body.getType() != YukkuriType.DOSMARISA) {
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
				if (body.getType() == YukkuriType.DOSMARISA ||
						(body.isAdult() && body.getIntelligence() == Intelligence.WISE &&
								body.getPublicRank() != PublicRank.UNUN_SLAVE)) {
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
						for (Map.Entry<Integer, Yukkuri> entry : GameWorld.get().getCurrentWorldState().getYukkuriRegistry().entrySet()) {
							Yukkuri eventBody = entry.getValue();
							if (eventBody.getCurrentEvent() instanceof RaperReactionEvent) {
								// うんうん奴隷は不参加
								if (eventBody.getPublicRank() == PublicRank.UNUN_SLAVE)
									continue;
								// 妊娠、大人以外は不参加.動けない場合も不参加
								if (eventBody.hasBabyOrStalk() || eventBody.isSick() || !eventBody.isAdult()
										|| eventBody.isDontMove())
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
								if (eventBody.getType() == YukkuriType.DOSMARISA) {
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
		body.setWorldEventResMessage(GameMessages.getMessage(body, MessagePool.Action.ScareRapist), Const.HOLDMESSAGE,
				true,
				false);
	}

	/**
	 * 反撃するときのメッセージを設定する.
	 * 
	 * @param body 反撃する個体
	 */
	public void setCounterWorldEventMessage(Yukkuri body) {
		body.setWorldEventResMessage(GameMessages.getMessage(body, MessagePool.Action.CounterRapist), Const.HOLDMESSAGE,
				true,
				false);
	}

	/**
	 * 制裁されない条件。
	 * れいぱーに対するリアクションであれば、れいぱーであり続ける場合
	 * 
	 * @return !制裁条件
	 */
	public boolean checkConditionOfTarget() {
		Yukkuri sourceBody = org.simyukkuri.util.YukkuriLookup.getYukkuriById(getFrom());
		if (sourceBody == null) {
			setFrom(-1);
			return false;
		}
		return sourceBody.isExciting();
	}

	// イベント目標に到着した際に呼ばれる
	// trueを返すとイベント終了
	/** イベント終了判定を行い true で終了する。 */
	@Override
	public boolean execute(Yukkuri body) {
		Yukkuri sourceBody = org.simyukkuri.util.YukkuriLookup.getYukkuriById(getFrom());
		// 相手が消えてしまったら他のレイパーを捜索
		if (sourceBody == null || sourceBody.isRemoved() || sourceBody.isDead()) {
			setFrom(searchNextTarget());
			sourceBody = org.simyukkuri.util.YukkuriLookup.getYukkuriById(getFrom());
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
	@Override
	public Yukkuri searchNextTarget() {
		Yukkuri nextTargetBody = null;
		for (Map.Entry<Integer, Yukkuri> entry : GameWorld.get().getCurrentWorldState().getYukkuriRegistry().entrySet()) {
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
		for (Map.Entry<Integer, Yukkuri> entry : GameWorld.get().getCurrentWorldState().getYukkuriRegistry().entrySet()) {
			Yukkuri candidateBody = entry.getValue();
			if (!candidateBody.isDead() && candidateBody.isExciting() && candidateBody.isRaper()
					&& candidateBody.isSukkiri()) {
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
		Yukkuri sourceBody = org.simyukkuri.util.YukkuriLookup.getYukkuriById(getFrom());
		if (sourceBody == null) {
			return;
		}
		int collisionX = YukkuriLogic.calcCollisionX(body, sourceBody);
		body.moveToEvent(this, sourceBody.getX() + collisionX, sourceBody.getY());
	}

	/**
	 * 敵から逃げるように移動する.
	 * 
	 * @param body 敵
	 */
	protected void escapeTarget(Yukkuri body) {
		int mapX = Translate.getWorldWidth();
		int mapY = Translate.getWorldHeight();
		Yukkuri sourceBody = org.simyukkuri.util.YukkuriLookup.getYukkuriById(getFrom());
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

	/** イベント名の文字列表現を返す。 */
	@Override
	public String toString() {
		return GameText.read("event_raperreaction");
	}
}
