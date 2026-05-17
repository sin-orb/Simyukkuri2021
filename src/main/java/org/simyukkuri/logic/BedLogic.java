package org.simyukkuri.logic;

import java.util.Map;

import org.simyukkuri.engine.Terrarium;
import org.simyukkuri.draw.Translate;
import org.simyukkuri.entity.core.Entity;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.world.WorldEntity;
import org.simyukkuri.entity.core.world.item.Bed;
import org.simyukkuri.entity.core.world.item.House;
import org.simyukkuri.entity.core.world.item.Toilet;
import org.simyukkuri.enums.AgeState;
import org.simyukkuri.enums.Direction;
import org.simyukkuri.enums.FavItemType;
import org.simyukkuri.enums.PublicRank;
import org.simyukkuri.enums.TakeoutItemType;
import org.simyukkuri.event.EventPacket;
import org.simyukkuri.event.impl.FavCopyEvent;
import org.simyukkuri.field.impl.Barrier;
import org.simyukkuri.util.GameEnvironment;
import org.simyukkuri.system.WorldState;
import org.simyukkuri.util.GameRandom;
import org.simyukkuri.util.GameWorld;

/***************************************************
 * ベッド関係の処理
 */
public class BedLogic {

	/**
	 * ベッド関連処理を行う
	 * 
	 * @param body ゆっくり
	 * @return ベッド関連処理の対象かどうか
	 */
	public static final boolean checkBed(Yukkuri body) {
		return checkBed(body, GameWorld.get().getCurrentWorldState());
	}

	/**
	 * ベッド行動を処理して行動実行有無を返す。
	 *
	 * @param body ゆっくり
	 * @param ws ワールド状態
	 *
	 * @return 処理が実行された場合は true、それ以外は false
	 */
	public static final boolean checkBed(Yukkuri body, WorldState ws) {
		// 他の用事がある場合
		if (body.isToFood() || body.isToYukkuri() || /* body.isToBed() || */ body.isToShit() ||
				body.isToSukkiri() || body.isToSteal() || body.isToTakeout()) {
			return false;
		}

		if (body.isIdiot())
			return false;

		EventPacket currentEvent = body.getCurrentEvent();
		if (body.nearToBirth()) {
			if (currentEvent != null && currentEvent.getPriority() == EventPacket.EventPriority.HIGH) {
				return false;
			}
		} else {
			if (currentEvent != null && currentEvent.getPriority() != EventPacket.EventPriority.LOW) {
				return false;
			}
		}

		// 非ゆっくり症の場合
		if (body.isNYD()) {
			return false;
		}

		// 対象が決まっていたら到達したかチェック
		Entity target = body.takeMappedObj(body.getMoveTargetId());
		if (body.isToBed() && target != null) {
			// 途中で消されてたら他の候補を探す
			if (target.isRemoved()) {
				body.setFavoriteItem(FavItemType.BED, null);
				body.clearActions();
				return false;
			}

			// うんうん奴隷の場合
			if (body.getPublicRank() == PublicRank.UNUN_SLAVE) {
				// ベッドには向かわない
				if (target instanceof Bed) {
					body.setFavoriteItem(FavItemType.BED, null);
					body.clearActions();
					return false;
				}
			}

			if ((body.getStepDist()) >= Translate.distance(body.getX(), body.getY(),
					target.getX() + body.getTargetOffsetX(), target.getY() + body.getTargetOffsetY())
					&& body.getZ() == 0) {
				// 到着したら待機状態へ
				if (body.getFavoriteItem(FavItemType.BED) == null) {
					// うんうん奴隷ではない場合
					if (body.getPublicRank() != PublicRank.UNUN_SLAVE) {
						// 見つけたベッドをお気に入りにして家族にも伝達
						body.setFavoriteItem(FavItemType.BED, target);
						EventLogic.addWorldEvent(new FavCopyEvent(body, null, null, 1), null, null);
					}
				}
				// 餌を保持している
				if (body.getCarryItem(TakeoutItemType.FOOD) != null) {
					// 吐き出す
					body.dropTakeoutItem(TakeoutItemType.FOOD);
				}
				body.clearActions();
				body.stay();
			} else {
				body.moveTo(target.getX() + body.getTargetOffsetX(), target.getY() + body.getTargetOffsetY(), 0);
			}
			return true;
		}

		// ベッドに向かう条件
		boolean shouldSearchBed = false;
		if ((body.isSleepy() // 眠い
				|| GameEnvironment.getDayState().ordinal() >= Terrarium.DayState.EVENING.ordinal() // 夜になった
				|| body.nearToBirth()) // 出産間近
				&& body.getCurrentEvent() == null) {// イベントがない <- イベントありのままだと不眠ディフューザーとかでおかしくなる
			shouldSearchBed = true;
		}
		if (body.getCarryItem(TakeoutItemType.FOOD) != null) {
			shouldSearchBed = true;
		}

		if (!shouldSearchBed)
			return false;

		boolean foundBed = false;
		Entity targetObject = searchBed(body, ws);

		if (targetObject != null) {
			int offsetX = 0;
			int offsetY = 0;
			if (body.hasBabyOrStalk()) {
				offsetY = Translate.invertY(targetObject.getH() - 4);
				offsetY = -(offsetY >> 1);
				// 茎妊娠の場合は茎がベッドの上に収まるように親を茎の反対方向にオフセット
				if (body.isHasStalk()) {
					int stalkOffset = Translate.invertX(15, targetObject.getY());
					if (body.getDirection() == Direction.RIGHT) {
						offsetX = -stalkOffset; // 茎が右にあるので親を左に
					} else {
						offsetX = stalkOffset; // 茎が左にあるので親を右に
					}
				}
			} else {
				offsetX = Translate.invertX(targetObject.getW(), targetObject.getY() - 4);
				offsetX = -(offsetX >> 1) + GameRandom.nextInt(offsetX);
				offsetY = Translate.invertY(targetObject.getH() - 4);
				offsetY = -(offsetY >> 1) + GameRandom.nextInt(offsetY);
			}
			body.moveToBed(targetObject, targetObject.getX() + offsetX, targetObject.getY() + offsetY, 0);
			body.setTargetMoveOffset(offsetX, offsetY);
			foundBed = true;
		}
		return foundBed;
	}

	/**
	 * ベッドを探し出す.
	 * 
	 * @param body ゆっくり
	 * @return 探しだしたベッドのオブジェクト
	 */
	public static Entity searchBed(Yukkuri body) {
		return searchBed(body, GameWorld.get().getCurrentWorldState());
	}

	/**
	 * 最適な寝床を視野内で探索して返す。
	 *
	 * @param body ゆっくり
	 * @param ws ワールド状態
	 *
	 * @return 対象を発見した場合はそのオブジェクト、見つからない場合は null
	 */
	public static Entity searchBed(Yukkuri body, WorldState ws) {
		Entity targetObject = body.getFavoriteItem(FavItemType.BED);
		int nearestDistance = body.getEyesightBase();
		// うんうん奴隷の場合
		if (body.getPublicRank() == PublicRank.UNUN_SLAVE) {
			body.setFavoriteItem(FavItemType.BED, null);
			targetObject = null;
		}
		int wallMode = body.getAgeState().ordinal();
		// 飛行可能なら壁以外は通過可能
		if (body.canflyCheck()) {
			wallMode = AgeState.ADULT.ordinal();
		}
		if (targetObject != null) {
			// お気に入りが壁で到達できなくなってたらリセット
			if (Barrier.acrossBarrier(body.getX(), body.getY(), targetObject.getX(), targetObject.getY(),
					Barrier.BODY_BLOCK_FLAGS[wallMode] + Barrier.BARRIER_KEKKAI)) {
				targetObject = null;
			}
		}

		// うんうん奴隷ではない場合
		if (body.getPublicRank() != PublicRank.UNUN_SLAVE) {
			if (targetObject == null) {
				for (Map.Entry<Integer, Bed> entry : ws.getBeds().entrySet()) {
					WorldEntity t = entry.getValue();
					int distance = Translate.distance(body.getX(), body.getY(), t.getX(), t.getY());
					if (nearestDistance > distance) {
						if (Barrier.acrossBarrier(body.getX(), body.getY(), t.getX(), t.getY(),
								Barrier.BODY_BLOCK_FLAGS[wallMode] + Barrier.BARRIER_KEKKAI)) {
							continue;
						}
						targetObject = (Bed) t;
						nearestDistance = distance;
					}
				}
			}
			//// 仮 おうち検索
			if (targetObject == null) {
				for (Map.Entry<Integer, House> entry : ws.getHouses().entrySet()) {
					WorldEntity t = entry.getValue();
					int distance = Translate.distance(body.getX(), body.getY(), t.getX(), t.getY());
					if (nearestDistance > distance) {
						if (Barrier.acrossBarrier(body.getX(), body.getY(), t.getX(), t.getY(),
								Barrier.BODY_BLOCK_FLAGS[wallMode] + Barrier.BARRIER_KEKKAI)) {
							continue;
						}
						targetObject = (House) t;
						nearestDistance = distance;
					}
				}
			}
		} else {
			// うんうん奴隷の場合、トイレを探す
			if (targetObject == null) {
				for (Map.Entry<Integer, Toilet> entry : ws.getToilets().entrySet()) {
					WorldEntity t = entry.getValue();
					int distance = Translate.distance(body.getX(), body.getY(), t.getX(), t.getY());
					if (nearestDistance > distance) {
						if (Barrier.acrossBarrier(body.getX(), body.getY(), t.getX(), t.getY(),
								Barrier.BODY_BLOCK_FLAGS[wallMode] + Barrier.BARRIER_KEKKAI)) {
							continue;
						}

						targetObject = (Toilet) t;
						nearestDistance = distance;
					}
				}
			}
		}
		return targetObject;
	}
}
