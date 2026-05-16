package org.simyukkuri.logic;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.simyukkuri.draw.Translate;
import org.simyukkuri.entity.core.Entity;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.world.item.Toilet;
import org.simyukkuri.entity.core.world.mobile.Shit;
import org.simyukkuri.enums.AgeState;
import org.simyukkuri.enums.ImageCode;
import org.simyukkuri.enums.Intelligence;
import org.simyukkuri.enums.PublicRank;
import org.simyukkuri.enums.TakeoutItemType;
import org.simyukkuri.event.EventPacket;
import org.simyukkuri.field.impl.Barrier;
import org.simyukkuri.system.MessagePool;
import org.simyukkuri.util.GameMessages;
import org.simyukkuri.util.GameRandom;
import org.simyukkuri.system.WorldState;
import org.simyukkuri.util.GameWorld;

/***************************************************
 * トイレ関係の処理
 */
public class ToiletLogic {
	/** うんうんどれい */
	private static Yukkuri bodyUnunSlave;

	public static Yukkuri getUnunSlave() {
		return bodyUnunSlave;
	}

	public static void setUnunSlave(Yukkuri bodyUnunSlave) {
		ToiletLogic.bodyUnunSlave = bodyUnunSlave;
	}

	/**
	 * うんうん処理
	 *
	 * @param body ゆっくり
	 * @return 処理が行われたか
	 */
	public static final boolean checkShit(Yukkuri body) {
		return checkShit(body, GameWorld.get().getCurrentWorldState());
	}

	public static final boolean checkShit(Yukkuri body, WorldState ws) {
		// A.トイレ行動中止
		// 毎フレームチェックは重いのでインターバル
		if (body.getAge() % 15 != 0)
			return false;
		if (body.canAction() == false || body.isIdiot() || body.isExciting() || body.nearToBirth()) {
			return false;
		}
		if (body.getCurrentEvent() != null && body.getCurrentEvent().getPriority() != EventPacket.EventPriority.LOW) {
			return false;
		}
		// 非ゆっくり症の場合
		if (body.isNYD()) {
			return false;
		}

		boolean shouldReturn = false;
		int collisionX = body.getCollisionX();
		boolean hasShit = false;
		if (body.getCarryItem(TakeoutItemType.SHIT) != null) {
			hasShit = true;
		}

		List<Toilet> toiletList = new LinkedList<>(ws.getToilets().values());
		List<Shit> shitList = new LinkedList<>(ws.getShit().values());
		if (shitList == null || shitList.size() == 0) {
			return false;
		}

		// B.子供のうんうん運び関連
		// 子供のうんうんを運ぶする余裕が有るか
		boolean canTransport = true;
		// 前回チェックしたうんうんどれいがまだいるなら自分で運ばない
		if (bodyUnunSlave != null &&
				bodyUnunSlave.getPublicRank() == PublicRank.UNUN_SLAVE &&
				!bodyUnunSlave.isDead() &&
				!bodyUnunSlave.isRemoved()) {
			canTransport = false;
		} else {
			if (body.isDontMove() || body.isDamaged() || body.isFeelPain() == true || body.isSoHungry() == true) {
				canTransport = false;
			}
		}
		// うんうん奴隷がいれば運ばない
		if (canTransport) {
			for (Map.Entry<Integer, Yukkuri> entry : ws.getYukkuriRegistry().entrySet()) {
				Yukkuri bodyOther = entry.getValue();
				if (bodyOther == body || bodyOther.isDead() || bodyOther.isRemoved()) {
					continue;
				}
				if (bodyOther.getPublicRank() == PublicRank.UNUN_SLAVE) {
					canTransport = false;
					bodyUnunSlave = bodyOther;
					break;
				}
			}
		}

		// 自分のうんうんがトイレ外にあるかチェック
		boolean foundMyShitOutOfToilet = false;
		boolean foundMyToilet = false;
		if (canTransport) {
			for (Shit s : shitList) {
				if (s.getZ() != body.getZ())
					continue;
				if (body.getUniqueID() == s.getOwnerId())
					continue;

				// 壁があるなら無視
				if (Barrier.acrossBarrier(body.getX(), body.getY(), s.getX(), s.getY(),
						Barrier.BODY_BLOCK_FLAGS[body.getAgeState().ordinal()] + Barrier.BARRIER_KEKKAI)) {
					continue;
				}
				// トイレの上にあるか
				boolean isOnToilet = false;
				if (toiletList != null && toiletList.size() != 0) {
					for (Toilet t : toiletList) {
						if (t.checkHitObj(s, false)) {
							isOnToilet = true;
							break;
						} else {
							// 壁があるなら無視
							if (!Barrier.acrossBarrier(body.getX(), body.getY(), t.getX(), t.getY(),
									Barrier.BODY_BLOCK_FLAGS[body.getAgeState().ordinal()] + Barrier.BARRIER_KEKKAI)) {
								foundMyToilet = true;
								break;
							}
						}
					}
				}
				// トイレの上にあればスキップ
				if (isOnToilet) {
					continue;
				}
				foundMyShitOutOfToilet = true;
				break;
			}
		}

		// C.うんうん探索
		// うんうんが近くにあるかチェック
		for (Shit s : shitList) {
			if (s.getZ() != body.getZ())
				continue;
			// 壁があるなら無視
			if (Barrier.acrossBarrier(body.getX(), body.getY(), s.getX(), s.getY(),
					Barrier.BODY_BLOCK_FLAGS[body.getAgeState().ordinal()] + Barrier.BARRIER_KEKKAI)) {
				continue;
			}

			int distance = Translate.getRealDistance(body.getX(), body.getY(), s.getX(), s.getY());
			// ある程度近いうんうんが自分の子供のものかチェック。すでにうんうんを運んでる、自分のうんうんがトイレ外にあれば無視する
			if (canTransport && distance < collisionX && !hasShit && !foundMyShitOutOfToilet && foundMyToilet) {
				int bodyOwnerId = s.getOwnerId();
				Yukkuri owner = null;
				for (Map.Entry<Integer, Yukkuri> entry : ws.getYukkuriRegistry().entrySet()) {
					Yukkuri otherBody = entry.getValue();
					if (otherBody.getUniqueID() == bodyOwnerId) {
						owner = otherBody;
						break;
					}
				}
				// 自分の子ゆのうんうんは片付ける。
				if (owner != null && body.isParent(owner) && !owner.isAdult()) {
					boolean isOnToilet = false;
					if (toiletList != null && toiletList.size() != 0) {
						for (Toilet t : toiletList) {
							if (t.checkHitObj(s, false)) {
								isOnToilet = true;
								break;
							}
						}
					}
					// トイレの上にない
					if (!isOnToilet) {
						if (distance < body.getStepDist()) {
							// おもちかえりする
							body.setCarryItem(TakeoutItemType.SHIT, s);
							body.setMessage(GameMessages.getMessage(body, MessagePool.Action.TransportShit));
							body.stay();
							body.clearActions();
						} else {
							body.moveToYukkuri(s, s.getX(), s.getY());
						}
						break;
					}
				}
			}

			if (distance < body.getStepDist() * 2) {
				// 嫌がる
				if (!body.isTalking() && !body.isToShit()) {
					// うんうん奴隷じゃない、餡子脳の赤、子ゆはランダムで威嚇。
					if (!body.isAdult() && body.getPublicRank() == PublicRank.NONE
							&& body.getIntelligence() == Intelligence.FOOL && GameRandom.nextBoolean()) {
						body.setForceFace(ImageCode.PUFF.ordinal());
						body.setMessage(GameMessages.getMessage(body, MessagePool.Action.ShitIntimidation), false);
					}
					// ついでに足りないゆも
					else if (body.isIdiot() && GameRandom.nextBoolean()) {
						body.setForceFace(ImageCode.PUFF.ordinal());
						body.setMessage(GameMessages.getMessage(body, MessagePool.Action.ShitIntimidation), false);
					} else
						body.setMessage(GameMessages.getMessage(body, MessagePool.Action.HateShit), false);
					body.addStress(5);
					shouldReturn = true;
					break;
				}
			}
		}
		return shouldReturn;
	}

	/**
	 * トイレ処理
	 * 
	 * @param body ゆっくり
	 * @return 処理が行われたか
	 */
	public static final boolean checkToilet(Yukkuri body) {
		return checkToilet(body, GameWorld.get().getCurrentWorldState());
	}

	public static final boolean checkToilet(Yukkuri body, WorldState ws) {
		// A.トイレ行動の中止
		// 他の用事がある場合
		if (body.isToFood() || body.isToYukkuri() || body.isToSukkiri() || body.isToBed()
				|| /* body.isToShit() || */ body.isToSteal()) {
			return false;
		}
		if (body.isIdiot() || body.isDontMove() || body.nearToBirth()) {
			return false;
		}
		// 非ゆっくり症の場合
		if (body.isNYD()) {
			return false;
		}
		if (body.getCurrentEvent() != null && body.getCurrentEvent().getPriority() != EventPacket.EventPriority.LOW) {
			return false;
		}
		// A2.フラグ設定
		boolean hasShit = false;
		if (body.getCarryItem(TakeoutItemType.SHIT) != null) {
			hasShit = true;
		}
		Entity targetObject = body.takeMoveTarget();
		if (targetObject != null) {
			if (!(targetObject instanceof Toilet)) {
				return false;
			}
		}

		// B1.トイレに向かわないならリターンを返す
		PublicRank publicRank = body.getPublicRank();
		// うんうんどれいの場合
		if (publicRank == PublicRank.UNUN_SLAVE) {
			// 盗もうとしてて、かつうんうんしないならトイレには向かわない
			if (body.isToSteal() && !body.wantToShit()) {
				return false;
			}
			for (Map.Entry<Integer, Toilet> entry : ws.getToilets().entrySet()) {
				Toilet t = entry.getValue();
				// うんうん奴隷用トイレのどれかにいれば終了＝トイレに向かわない
				if (t.isForSlave() && t.checkHitObj(null, body)) {
					// うんうんを持ち歩いている場合
					if (hasShit) {
						body.dropTakeoutItem(TakeoutItemType.SHIT);
						body.setMessage(GameMessages.getMessage(body, MessagePool.Action.HateShit));
						body.addStress(10);
					}
					// うんうんを持ち歩いていない場合
					else {
						return false;
					}
				}
			}
		} else {
			// うんうん奴隷ではない場合、用がない、かつうんうんを持ってないなら終了
			if (!body.wantToShit() && !hasShit) {
				for (Map.Entry<Integer, Toilet> entry : ws.getToilets().entrySet()) {
					Toilet t = entry.getValue();
					// 自動清掃でないトイレに入った時の反応
					if (!t.getAutoClean() && t.checkHitObj(null, body) && !body.isTalking()) {
						if (body.isSleeping())
							body.wakeup();
						body.setMessage(GameMessages.getMessage(body, MessagePool.Action.HateShit));
						body.runAway(t.getX(), t.getY());
						break;
					}
				}
				return false;
			}
		}

		// 対象が決まっていたら到達したかチェック
		Entity target = body.takeMoveTarget();
		if ((body.isToShit() || publicRank == PublicRank.UNUN_SLAVE || hasShit) && target != null) {
			// 途中で消されてたら他の候補を探す
			if (target.isRemoved()) {
				body.clearActions();
				return false;
			}
			// 到着済み
			if (body.getStepDist() >= Translate.distance(body.getX(), body.getY(), target.getX(), target.getY())) {
				// トイレが空中にあるとき
				if (target.getZ() != 0) {
					// 他の候補を探す
					body.clearActions();
					return false;
				}
				// うんうんをしたいわけではないとき
				if (!body.wantToShit()) {
					// うんうんどれいは常にトイレにいる
					if (publicRank == PublicRank.UNUN_SLAVE) {
						if (body.getCarryItem(TakeoutItemType.SHIT) != null) {
							// うんうん奴隷がうんうんを持っていれば落とす
							body.dropTakeoutItem(TakeoutItemType.SHIT);
							body.setMessage(GameMessages.getMessage(body, MessagePool.Action.HateShit));
							body.addStress(10);
							body.stay();
						}
					} else {
						// うんうんを持っていれば落とす
						if (body.getCarryItem(TakeoutItemType.SHIT) != null) {
							body.dropTakeoutItem(TakeoutItemType.SHIT);
							body.setMessage(GameMessages.getMessage(body, MessagePool.Action.Relax));
							body.addStress(-15);
							body.stay();
						}
					}
				}
				// うんうんをしたい時は待機状態へ
				else {
					body.stay();
				}
			}
			// 未到着の場合
			else {
				body.moveTo(target.getX(), target.getY(), 0);
			}
			return true;
		}

		// 対象が未決定の場合
		boolean foundToilet = false;
		Toilet targetToilet = null;
		int minDistance = body.getEyesightBase();
		int wallMode = body.getAgeState().ordinal();
		// 飛行可能なら壁以外は通過可能
		if (body.canflyCheck()) {
			wallMode = AgeState.ADULT.ordinal();
		}

		for (Map.Entry<Integer, Toilet> entry : ws.getToilets().entrySet()) {
			Toilet t = entry.getValue();
			int distance = Translate.distance(body.getX(), body.getY(), t.getX(), t.getY() - t.getH() / 6);
			if (minDistance > distance) {
				if (!body.isRude()) {
					if (Barrier.acrossBarrier(body.getX(), body.getY(), t.getX(), t.getY() - t.getH() / 6,
							Barrier.BODY_BLOCK_FLAGS[wallMode] + Barrier.BARRIER_KEKKAI)) {
						continue;
					}
				}
				// うんうん奴隷の場合
				if (publicRank == PublicRank.UNUN_SLAVE) {
					// うんうん奴隷用じゃないならスキップ
					if (!((Toilet) t).isForSlave()) {
						continue;
					}
				}
				if (targetToilet != null) {
					// うんうん奴隷用トイレを優先する
					if ((targetToilet.isForSlave()) && !((Toilet) t).isForSlave()) {
						continue;
					}
				}
				targetToilet = (Toilet) t;
				minDistance = distance;
			}
		}

		if (targetToilet != null) {
			if (!body.wantToShit()) {
				// うんうんをしない
				body.moveToYukkuri(targetToilet, targetToilet.getX(), targetToilet.getY());
			} else {
				// うんうんをする
				body.moveToToilet(targetToilet, targetToilet.getX(), targetToilet.getY(), 0);
			}
			foundToilet = true;
		} else {
			// トイレがないのにうんうんを持っていればその場に落とす
			if (hasShit) {
				body.dropTakeoutItem(TakeoutItemType.SHIT);
				body.addStress(5);
				body.stay();
			}
		}
		return foundToilet;
	}
}
