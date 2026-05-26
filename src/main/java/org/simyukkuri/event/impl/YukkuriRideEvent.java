package org.simyukkuri.event.impl;

import org.simyukkuri.draw.Translate;
import org.simyukkuri.engine.Terrarium;
import org.simyukkuri.entity.core.Entity;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.world.item.Food;
import org.simyukkuri.enums.Happiness;
import org.simyukkuri.enums.ImageCode;
import org.simyukkuri.enums.Intelligence;
import org.simyukkuri.enums.TakeoutItemType;
import org.simyukkuri.event.EventPacket;
import org.simyukkuri.logic.BedLogic;
import org.simyukkuri.logic.FamilyActionLogic;
import org.simyukkuri.system.MessagePool;
import org.simyukkuri.util.GameEnvironment;
import org.simyukkuri.util.GameMessages;
import org.simyukkuri.util.GameRandom;
import org.simyukkuri.util.GameText;

/**
 * おちびちゃん運びイベント
 * protected Yukkuri from; // 乗せるゆっくり
 * protected Yukkuri to; // 乗るゆっくり
 * protected Entity target; // 未使用
 * protected int count; // 100
 */
public class YukkuriRideEvent extends EventPacket {

	private static final long serialVersionUID = 7916220303996368395L;
	int tick = 0;
	boolean hasRideTarget = false;

	/**
	 * コンストラクタ.
	 */
	public YukkuriRideEvent(Yukkuri f, Yukkuri t, Entity tgt, int cnt) {
		super(f, t, tgt, cnt);
		priority = EventPriority.MIDDLE; // 食事、睡眠、トイレよりは上
	}

	/** Jackson デシリアライズ用デフォルトコンストラクタ。 */
	public YukkuriRideEvent() {
	}

	/** イベントの進行ティックカウンタを返す。 */
	public int getTick() {
		return tick;
	}

	/** イベントの進行ティックカウンタをセットする。 */
	public void setTick(int tick) {
		this.tick = tick;
	}

	/** 移動先が設定済みかを返す。 */
	public boolean isMoveTarget() {
		return hasRideTarget;
	}

	/** 移動ターゲット ID フラグをセットする。 */
	public void setMoveTargetId(boolean moveTargetId) {
		this.hasRideTarget = moveTargetId;
	}

	// 参加チェック
	// ここで各種チェックを行い、イベントへ参加するかを返す
	// また、イベント優先度も必要に応じて設定できる
	/** イベントへの参加可否を判定し、参加可能なら true を返す。 */
	@Override
	public boolean checkEventResponse(Yukkuri body) {
		Yukkuri targetBody = org.simyukkuri.util.YukkuriLookup.getYukkuriById(getTo());
		if (targetBody == null) {
			return false;
		}
		Yukkuri sourceBody = org.simyukkuri.util.YukkuriLookup.getYukkuriById(getFrom());
		if (sourceBody == body) {
			return true;
		}
		return targetBody == body;
	}

	// イベント開始動作
	/** イベントの開始処理を実行する。 */
	@Override
	public void start(Yukkuri body) {
		Yukkuri targetBody = org.simyukkuri.util.YukkuriLookup.getYukkuriById(getTo());
		if (targetBody == null) {
			return;
		}
		body.setCurrentEvent(this);
		body.clearActionsForEvent();
		body.moveToEvent(this, targetBody.getX(), targetBody.getY());
	}

	// 毎フレーム処理
	/** 毎ティック状態を更新する。 */
	@Override
	public UpdateState update(Yukkuri body) {
		tick++;
		Yukkuri sourceBody = org.simyukkuri.util.YukkuriLookup.getYukkuriById(getFrom());
		if (sourceBody == null || !sourceBody.canActionForEvent() || sourceBody.isRemoved()) {
			return UpdateState.ABORT;
		}
		Yukkuri targetBody = org.simyukkuri.util.YukkuriLookup.getYukkuriById(getTo());
		if (targetBody == null || targetBody.isDead() || targetBody.isRemoved()) {
			return UpdateState.ABORT;
		}

		if (sourceBody.getCurrentEvent() != this) {
			return UpdateState.ABORT;
		}

		if (!sourceBody.isIdiot() && sourceBody.getIntelligence() != Intelligence.FOOL
				&& sourceBody.findSick(targetBody)) {
			sourceBody.setMessage(GameMessages.getMessage(targetBody, MessagePool.Action.Surprise), 30);
			sourceBody.setHappiness(Happiness.VERY_SAD);
			sourceBody.setForceFace(ImageCode.CRYING.ordinal());
			return UpdateState.ABORT;
		}

		if (targetBody.isNormalDirty()) {
			targetBody.setParentLinkId(-1);
			sourceBody.doPeropero(targetBody);
			return UpdateState.ABORT;
		}

		// 親
		if (body == sourceBody) {
			// 一定期間で終了
			if (tick > 10000) {
				targetBody.setCalcZ(sourceBody.getZ());
				targetBody.setParentLinkId(-1);
				return UpdateState.ABORT;
			}

			if (targetBody.takeMappedObj(targetBody.getParentLinkId()) == null) {
				if (tick % 20 != 0) {
					return null;
				}
				int distanceToTarget = Translate.getRealDistance(sourceBody.getX(), sourceBody.getY(),
						targetBody.getX(), targetBody.getY());
				if (3 < distanceToTarget) {
					// 子供に近づく
					sourceBody.moveToEvent(this, targetBody.getX(), targetBody.getY());
				} else {
					// 子供を頭にのせる
					targetBody.setParentLinkId(sourceBody.objId);
				}
			} else {
				// 子供をのせて移動する
				targetBody.setCalcX(sourceBody.getX());
				targetBody.setCalcY(sourceBody.getY());
				int liftZ = Translate.invertZ(sourceBody.getCollisionY() + 15);
				liftZ += sourceBody.getZ();
				targetBody.setCalcZ(liftZ);
				targetBody.setDirection(sourceBody.getDirection());
				Entity targetObject = body.takeMappedObj(this.target);
				if (targetObject != null) {
					hasRideTarget = true;
				}

				// 目的地がない場合は目的地チェック
				if (!hasRideTarget) {
					// 空腹
					if (targetObject == null) {
						if (targetBody.isHungry()) {
							// 餌を持っていたら落とす
							body.dropTakeoutItem(TakeoutItemType.FOOD);
							Entity candidateObject = FamilyActionLogic.searchFood(body);
							if (candidateObject != null) {
								targetObject = candidateObject;
								hasRideTarget = true;
								sourceBody.moveToEvent(this, targetObject.getX(), targetObject.getY());
							}
						}
					}

					// トイレ
					if (targetObject == null) {
						if (targetBody.wantToShit()) {
							Entity candidateObject = FamilyActionLogic.searchToilet(body);
							if (candidateObject != null) {
								targetObject = candidateObject;
								hasRideTarget = true;
								sourceBody.moveToEvent(this, targetObject.getX(), targetObject.getY());
							}
						}
					}

					// ベッド
					if (targetObject == null) {
						if (targetBody.isSleepy()
								|| GameEnvironment.getDayState().ordinal() >= Terrarium.DayState.EVENING.ordinal()) {
							Entity candidateObject = BedLogic.searchBed(body);
							if (candidateObject != null) {
								targetObject = candidateObject;
								hasRideTarget = true;
								sourceBody.moveToEvent(this, targetObject.getX(), targetObject.getY());
							}
						}
					}
				} else {
					if (targetObject instanceof Food) {
						// 餌を持っていたら落とす
						if (body.getCarryItem(TakeoutItemType.FOOD) != null) {
							body.dropTakeoutItem(TakeoutItemType.FOOD);
							targetBody.setCalcZ(sourceBody.getZ());
							targetBody.setParentLinkId(-1);
							return UpdateState.ABORT;
						}
					}
					// 目的地についたなら終了
					if (targetObject != null) {
						int distanceToTarget = Translate.getRealDistance(sourceBody.getX(), sourceBody.getY(),
								targetObject.getX(),
								targetObject.getY());
						if (3 < distanceToTarget) {
							sourceBody.moveToEvent(this, targetObject.getX(), targetObject.getY());
						} else {
							targetBody.setCalcZ(sourceBody.getZ());
							targetBody.setParentLinkId(-1);
							return UpdateState.ABORT;
						}
					}
				}
			}
		} else {
			// 子供
			if (body.takeMappedObj(body.getParentLinkId()) == null) {
				int distanceToParent = Translate.getRealDistance(targetBody.getX(), targetBody.getY(),
						sourceBody.getX(), sourceBody.getY());
				if (3 < distanceToParent) {
					// 親に近づく
					targetBody.moveToEvent(this, sourceBody.getX(), sourceBody.getY());
				} else {
					targetBody.stay();
				}
			} else {
				if (!targetBody.isDamaged() && !targetBody.isNeedled()) {
					// 親の頭の上で待機
					if (GameRandom.nextInt(30) == 0) {
						targetBody.addMemories(10);
						targetBody.addStress(-150);
						if (!targetBody.isSleeping() && !targetBody.isDead()) {
							if (GameRandom.nextInt(10) == 0) {
								targetBody.setMessage(GameMessages.getMessage(targetBody, MessagePool.Action.Flying),
										30);
							} else {
								targetBody.setMessage(GameMessages.getMessage(targetBody, MessagePool.Action.Relax),
										30);
							}
						}
					}
				}
			}
		}

		return null;
	}

	// イベント目標に到着した際に呼ばれる
	// trueを返すとイベント終了
	/** イベント終了判定を行い true で終了する。 */
	@Override
	public boolean execute(Yukkuri body) {
		return false;
	}

	/**
	 * End.
	 *
	 * @param body the body
	 */
	@Override
	public void end(Yukkuri body) {
		// 他のイベントで強制的にイベントが終わることがある
		// 子供をおろす
		Yukkuri targetBody = org.simyukkuri.util.YukkuriLookup.getYukkuriById(getTo());
		if (targetBody != null) {
			targetBody.setParentLinkId(-1);
		}
	}

	/** イベント名の文字列表現を返す。 */
	@Override
	public String toString() {
		return GameText.read("event_ride");
	}
}
