package org.simyukkuri.event.impl;

import java.util.List;

import org.simyukkuri.draw.Translate;
import org.simyukkuri.entity.core.Entity;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.world.item.Food;
import org.simyukkuri.enums.GatheringDirection;
import org.simyukkuri.enums.Happiness;
import org.simyukkuri.event.EventPacket;
import org.simyukkuri.field.impl.Barrier;
import org.simyukkuri.logic.YukkuriLogic;
import org.simyukkuri.logic.FoodLogic;
import org.simyukkuri.system.MessagePool;
import org.simyukkuri.util.GameMessages;
import org.simyukkuri.util.GameRandom;
import org.simyukkuri.util.GameText;

/***************************************************
 * すーぱーむーしゃむーしゃたいむイベント
 * protected Yukkuri from; // イベントを発した個体
 * protected Yukkuri to; // 未使用
 * protected Entity target; // 移動先
 * protected int count; // 10
 */
public class SuperEatingTimeEvent extends EventPacket {

	private static final long serialVersionUID = -2604356330046082053L;
	int tick = 0;
	int waitTicks = 0;
	private STATE state = STATE.WAIT;
	int minimumStep = 0;

	/** 行動ステート */
	public enum STATE {
		/** 移動 */
		GO,
		/** 待機 */
		WAIT,
		/** イベント開始直前 */
		START_BEFORE,
		/** イベント開始時 */
		START,
		/** イベント終了時 */
		END,
	}

	/**
	 * コンストラクタ.
	 */
	public SuperEatingTimeEvent(Yukkuri f, Yukkuri t, Entity tgt, int cnt) {
		super(f, t, tgt, cnt);
		setHighPriority();
	}

	/** Jackson デシリアライズ用デフォルトコンストラクタ。 */
	public SuperEatingTimeEvent() {

	}

	/** イベントの進行ティックカウンタを返す。 */
	public int getTick() {
		return tick;
	}

	/** イベントの進行ティックカウンタをセットする。 */
	public void setTick(int tick) {
		this.tick = tick;
	}

	/** 次フェーズまでの待機ティック数を返す。 */
	public int getWaitTicks() {
		return waitTicks;
	}

	/** 待機ティック数をセットする。 */
	public void setWaitTicks(int waitTicks) {
		this.waitTicks = waitTicks;
	}

	/** ゆっくり以外のエンティティに対する簡易参加チェック。 */
	@Override
	public boolean simpleEventAction(Yukkuri b) {
		Yukkuri from = org.simyukkuri.util.YukkuriLookup.getYukkuriById(getFrom());
		if (from == null || from.isShutmouth()) {
			return true;
		}
		return false;
	}

	// 参加チェック
	// ここで各種チェックを行い、イベントへ参加するかを返す
	// また、イベント優先度も必要に応じて設定できる
	/** イベントへの参加可否を判定し、参加可能なら true を返す。 */
	@Override
	public boolean checkEventResponse(Yukkuri b) {
		Yukkuri from = org.simyukkuri.util.YukkuriLookup.getYukkuriById(getFrom());
		if (from == null)
			return false;
		if (from == b && !(b.isShutmouth())) {
			return true;
		}

		if (b.isShutmouth() || b.isDead()) {
			return false;
		}

		if (b.getPublicRank() != from.getPublicRank()) {
			return false;
		}

		// 動けないなら参加しない
		if (b.isDontMove()) {
			return false;
		}

		// つがいも参加する
		if (from.isPartner(b)) {
			return true;
		}

		if (b.isNYD() || b.isTaken()) {
			return false;
		}

		// Fromの子供だけ参加する(※Fromが教育係のときは全ての子供が参加するようにする？)
		if (!b.isChild(from))
			return false;
		// 大人は終了
		if (b.isAdult())
			return false;

		return true;
	}

	// イベント開始動作
	/** イベント開始時の初期化処理。 */
	@Override
	public void start(Yukkuri b) {
		b.setCurrentEvent(this);
	}

	/** 最小ステップ数を返す。 */
	public int getMinimumStep() {
		return minimumStep;
	}

	/** 最小ステップ数をセットする。 */
	public void setMinimumStep(int minimumStep) {
		this.minimumStep = minimumStep;
	}

	/** イベントの進行ステートを返す。 */
	public STATE getState() {
		return state;
	}

	/** イベントの進行ステートをセットする。 */
	public void setState(STATE state) {
		this.state = state;
	}

	// 毎フレーム処理
	// trueを返すとイベント終了
	// 親→子供→次のステート、の順で処理をする
	/** 毎ティック状態を更新する。 */
	@Override
	public UpdateState update(Yukkuri b) {
		b.clearActionsForEvent();
		Yukkuri from = org.simyukkuri.util.YukkuriLookup.getYukkuriById(getFrom());
		if (b == null || from == null) {
			return UpdateState.ABORT;
		}
		if (b.isNYD()) {
			return UpdateState.ABORT;
		}
		if (b.isDead() || b.isRemoved()) {
			return UpdateState.ABORT;
		}

		// 親が消えてしまったらイベント中断
		if (from.isRemoved())
			return UpdateState.ABORT;
		// ターゲットが消えてしまったらイベント中断
		Entity target = b.takeMappedObj(this.target);
		if (target == null || target.isRemoved()) {
			from.setMessage(GameMessages.getMessage(from, MessagePool.Action.NoFood));
			from.setHappiness(Happiness.VERY_SAD);
			from.stay();
			return UpdateState.ABORT;
		}

		// 産気づいたら終了
		if (b.nearToBirth()) {
			return UpdateState.ABORT;
		}

		if ((10 < waitTicks && from.getCurrentEvent() == null) || target == null || target.isRemoved()) {
			return UpdateState.ABORT;
		}

		if (tick % 20 != 0) {
			return null;
		}

		// 空腹状態なら60%にする(強制イベント救済措置)
		if (b.isHungry()) {
			b.setHungry(b.getHungryLimit() * 6 / 10);
		}

		b.wakeup();

		// 親
		if (b == from) {
			// 何らかの理由で終了しそうにないなら終わらせる
			if (5000 < waitTicks) {
				return UpdateState.ABORT;
			}
			waitTicks++;
			// 子ゆがいなければ終了
			List<Yukkuri> childrenList = YukkuriLogic.createActiveChildren(from, true);
			if ((childrenList == null) || (childrenList.size() == 0)) {
				return UpdateState.ABORT;
			}

			// 最小歩幅の設定と、子ゆがイベント中かのチェック
			boolean childInEvent = false;
			for (Yukkuri child : childrenList) {
				int step = child.getStep();
				if (minimumStep == 0 || step < minimumStep) {
					minimumStep = step;
				}
				if (child.getCurrentEvent() == this) {
					childInEvent = true;
				}
			}
			if (1000 < waitTicks) {
				// イベント参加者がいないなら終了
				if (!childInEvent) {
					return UpdateState.ABORT;
				}
			}

			// 番の設定
			Yukkuri partner = org.simyukkuri.util.YukkuriLookup.getYukkuriById(from.getPartner());
			if (partner == from) {
				partner = null;
			}

			// 親のステート
			boolean gathered = false;
			switch (state) {
				case WAIT:// ごはんさんをたべにいくよ！みんなあつまってね！
					// 家族を集める
					gathered = YukkuriLogic.gatheringYukkuriSquare(from, childrenList.toArray(new Yukkuri[0]),
							GatheringDirection.DOWN, this);
					for (Yukkuri childBody : childrenList) {
						if (childBody != null) {
							// 他に用事があれば除外
							childBody.setMoveTargetId(-1);
							childBody.wakeup();
						}
					}
					if (GameRandom.nextInt(100) == 0) {
						b.setMessage(GameMessages.getMessage(b, MessagePool.Action.FamilyEatingTimeWait), true);
					}
					b.setHappiness(Happiness.HAPPY);
					// 番の処理
					if (partner != null) {
						int colX = YukkuriLogic.calcCollisionX(partner, from);
						partner.moveTo(from.getX() + colX * 2, from.getY());
						partner.setHappiness(Happiness.HAPPY);
						// 他に用事があれば除外
						partner.setMoveTargetId(-1);
					}
					// ステート移行
					if (gathered) {
						state = STATE.GO;
					} else {
						b.stay(100);
					}
					// 何らかの理由で終了しそうにないなら終わらせる
					if (1000 < waitTicks) {
						return UpdateState.ABORT;
					}
					waitTicks++;
					break;
				case GO:// ごはんさんのにおいがするよ！
					// 移動開始
					gathered = YukkuriLogic.gatheringYukkuriBackLine(from, childrenList, this);
					for (Yukkuri childBody : childrenList) {
						// 他に用事があれば除外
						childBody.setMoveTargetId(-1);
						childBody.wakeup();
					}
					Yukkuri firstChild = childrenList.get(0);
					int distance = Translate.getRealDistance(b.getX(), b.getY(), firstChild.getX(), firstChild.getY());
					int colXChild = Math.abs(YukkuriLogic.calcCollisionX(b, firstChild));
					// 一定距離を保つ
					if (colXChild * 3 < distance) {
						b.stay();
					}

					// 番の処理
					if (partner != null) {
						int colX = YukkuriLogic.calcCollisionX(partner, from);
						partner.moveTo(from.getX() + colX * 2, from.getY());
						partner.setHappiness(Happiness.HAPPY);
						// 他に用事があれば除外
						partner.setMoveTargetId(-1);
						if (GameRandom.nextInt(50) == 0) {
							partner.setMessage(GameMessages.getMessage(partner, MessagePool.Action.WantFood));
						}
					}

					int colX = Translate.invertX(b.getCollisionX(), target.getY());
					colX = Translate.transSize(colX);
					int distanceToFood = Translate.getRealDistance(b.getX(), b.getY(), target.getX(),
							target.getY() - 20);
					// 餌の近くで待つ
					if (distanceToFood <= 1) {
						if (gathered) {
							state = STATE.START_BEFORE;
						}
						b.stay();
					}
					// 餌に近づく
					else {
						b.moveToEvent(this, target.getX(), target.getY() - 20);
						if (GameRandom.nextInt(50) == 0) {
							b.setMessage(GameMessages.getMessage(b, MessagePool.Action.WantFood));
						}
					}
					break;
				case START_BEFORE:// ごはんの上に集合
					b.stay();
					gathered = YukkuriLogic.gatheringYukkuriSquare(target, childrenList.toArray(new Yukkuri[0]),
							GatheringDirection.UP, this);
					for (Yukkuri childBody : childrenList) {
						// 他に用事があれば除外
						childBody.setMoveTargetId(-1);
						childBody.wakeup();
					}

					// 配置済みの場合
					if (gathered) {
						b.setMessage(GameMessages.getMessage(b, MessagePool.Action.SuperEatingTime));
						b.setHappiness(Happiness.VERY_HAPPY);
						b.addMemories(1);
						// 番の処理
						if (partner != null) {
							partner.setMessage(GameMessages.getMessage(partner, MessagePool.Action.SuperEatingTime));
							partner.setHappiness(Happiness.VERY_HAPPY);
							partner.addMemories(1);
						}
						// 子ゆの処理
						for (Yukkuri childBody : childrenList) {
							if (childBody != null) {
								childBody.setMessage(GameMessages.getMessage(b, MessagePool.Action.SuperEatingTime));
								childBody.setHappiness(Happiness.VERY_HAPPY);
								childBody.stay();
								childBody.addMemories(1);
							}
						}
						state = STATE.START;
					} else {
						// 番の処理
						if (partner != null) {
							colX = YukkuriLogic.calcCollisionX(partner, from);
							partner.moveTo((int) (from.getX() + colX * 1.5), from.getY());
							partner.setHappiness(Happiness.HAPPY);
							// 他に用事があれば除外
							partner.setMoveTargetId(-1);
						}
					}
					break;
				case START:
					boolean isHungry = false;
					int noHungerPeriod = 500;

					// ご飯がない時の処理
					if (target instanceof Food) {
						Food food = (Food) target;
						if (food.isEmpty()) {
							b.setMessage(GameMessages.getMessage(b, MessagePool.Action.NoFood));
							b.setHappiness(Happiness.VERY_SAD);
							b.stay();
							return UpdateState.ABORT;
						}
					}

					for (Yukkuri childBody : childrenList) {
						if (childBody != null) {
							childBody.wakeup();
							if (childBody.getHungryLimit() * 10 / 100 > childBody.getHungry()) {
								childBody.setMoveTargetId(target.objId);
								childBody.setToFood(true);
								isHungry = true;
							} else {
								childBody.addMemories(10);
							}
							childBody.setSuperEatingNoHungryPeriod(noHungerPeriod);
						}
					}

					if (!isHungry) {
						// 子供の食事が終わってから食べる
						b.setMoveTargetId(target.objId);
						b.setToFood(true);
						b.setSuperEatingNoHungryPeriod(noHungerPeriod);
						b.addMemories(10);
						// 番の処理
						if (partner != null) {
							partner.setMoveTargetId(target.objId);
							partner.setToFood(true);
							partner.setSuperEatingNoHungryPeriod(noHungerPeriod);
							partner.addMemories(10);
						}

						if (b.getHungry() < b.getHungryLimit() * 10 / 100) {
							if (partner != null) {
								if (partner.getHungry() < partner.getHungryLimit() * 10 / 100) {
									return UpdateState.ABORT;
								}
							} else {
								return UpdateState.ABORT;
							}
						}
					} else {
						// 子供の食事が終わってなくても空腹なら食べる
						if (b.isHungry()) {
							b.setMoveTargetId(target.objId);
							b.setToFood(true);
						} else {
							b.setMoveTargetId(-1);
							b.stay(100);
							if (GameRandom.nextInt(30) == 0) {
								// 余裕なら子供の状態を喜ぶ
								b.setMessage(GameMessages.getMessage(b, MessagePool.Action.GladAboutChild), false);
								b.setHappiness(Happiness.VERY_HAPPY);
							}
						}
						// 番の処理
						if (partner != null) {
							// 子供の食事が終わってなくても空腹なら食べる
							if (partner.isHungry()) {
								partner.setMoveTargetId(target.objId);
								partner.setToFood(true);
							} else {
								partner.setMoveTargetId(-1);
								partner.stay(100);
								if (GameRandom.nextInt(30) == 0) {
									// 余裕なら子供の状態を喜ぶ
									partner.setMessage(GameMessages.getMessage(b, MessagePool.Action.GladAboutChild),
											false);
									partner.setHappiness(Happiness.VERY_HAPPY);
								}
							}
						}
					}
					break;
				default:
					break;
			}
		}

		// 親以外の処理
		else {
			// つがいはスキップ。主催側で処理
			if (b.isPartner(from)) {
				// 壁に引っかかってるなら終了
				if (Barrier.onBarrier(b.getX(), b.getY(), from.getX(), from.getY(),
						Barrier.BODY_BLOCK_FLAGS[b.getAgeState().ordinal()] + Barrier.BARRIER_KEKKAI)) {
					b.clearEvent();
				}
				return null;
			}

			// 子供
			switch (state) {
				case GO:
					// 壁に引っかかってるなら終了
					if (Barrier.onBarrier(b.getX(), b.getY(), from.getX(), from.getY(),
							Barrier.BODY_BLOCK_FLAGS[b.getAgeState().ordinal()] + Barrier.BARRIER_KEKKAI)) {
						b.clearEvent();
						return null;
					}
					b.setHappiness(Happiness.HAPPY);
					if (GameRandom.nextInt(50) == 0) {
						b.setMessage(GameMessages.getMessage(b, MessagePool.Action.WantFood));
					}
					break;
				case START:
					b.setHappiness(Happiness.VERY_HAPPY);
					int distance = Translate.getRealDistance(b.getX(), b.getY(), target.getX(), target.getY());
					if (distance < 3) {
						Food f = (Food) target;
						FoodLogic.eatFood(b, f.getFoodType(), Math.min(b.getEatAmount(), f.getAmount()));
						f.eatFood(Math.min(b.getEatAmount(), f.getAmount()));
						b.addMemories(10);
						b.clearActions();
					} else {
						b.moveToEvent(this, target.getX(), target.getY());
						b.setToFood(true);
					}
					// return UpdateState.FORCE_EXEC;
					break;
				default:
					b.setHappiness(Happiness.HAPPY);
					break;
			}
		}

		tick++;
		return null;
	}

	// イベント目標に到着した際に呼ばれる
	// trueを返すとイベント終了
	/** イベント終了判定を行い true で終了する。 */
	@Override
	public boolean execute(Yukkuri b) {
		return false;
	}

	/** イベント名の文字列表現を返す。 */
	@Override
	public String toString() {
		return GameText.read("event_super");
	}

}
