package org.simyukkuri.logic;

import java.util.LinkedList;
import java.util.List;

import org.simyukkuri.draw.Translate;
import org.simyukkuri.entity.core.Entity;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.world.WorldEntity;
import org.simyukkuri.entity.core.world.item.Sui;
import org.simyukkuri.entity.core.world.item.Toy;
import org.simyukkuri.entity.core.world.item.Trampoline;
import org.simyukkuri.enums.FavItemType;
import org.simyukkuri.enums.Happiness;
import org.simyukkuri.enums.Intelligence;
import org.simyukkuri.event.impl.SuiRideEvent;
import org.simyukkuri.event.impl.SuiSpeake;
import org.simyukkuri.field.impl.Barrier;
import org.simyukkuri.system.MessagePool;
import org.simyukkuri.util.GameMessages;
import org.simyukkuri.util.GameRandom;
import org.simyukkuri.util.GameWorld;

/***************************************************
 * おもちゃ関係の処理
 */
public class ToyLogic {

	/**
	 * ボールで遊ぶ
	 * 
	 * @param body ゆっくり
	 * @return 処理が行われたか
	 */
	public static final boolean checkToy(Yukkuri body) {
		if (body == null) {
			return false;
		}

		List<Toy> list = new LinkedList<>(GameWorld.get().getCurrentMap().getToy().values());
		if (list.size() == 0) {
			return false;
		}

		if (canPlay(body) == false)
			return false;
		if (!body.isRude() && (body.isAdult() || body.wantToShit())) {
			return false;
		}

		boolean handled = true;
		Toy foundToy = null;
		int nearestDistance = body.getEyesightBase();
		for (Toy toy : list) {
			// 最小距離のものが見つかっていたら
			if (nearestDistance < body.getStepDist()) {
				break;
			}
			int distance = Translate.distance(body.getX(), body.getY(), toy.getX(), toy.getY());
			if (nearestDistance > distance) {
				if (!body.isRude()) {
					if (Barrier.acrossBarrier(body.getX(), body.getY(), toy.getX(), toy.getY(),
							Barrier.MAP_BODY[body.getBodyAgeState().ordinal()] + Barrier.BARRIER_KEKKAI)) {
						continue;
					}
				}
				foundToy = toy;
				nearestDistance = distance;
			}
		}
		if (foundToy != null) {
			boolean ownedByFamily = false;
			// プレイヤーに持ち上げられたら所有権を消す
			if (foundToy.isGrabbed() && foundToy.getZ() != 0) {
				foundToy.setOwner(null);
			} else {
				Yukkuri ownerBody = foundToy.getOwner();
				if (ownerBody == null || ownerBody == body || body.isFamily(ownerBody)) {
					ownedByFamily = true;
				}
			}
			if (nearestDistance <= body.getStepDist()) {
				// 空中
				if (foundToy.getZ() != 0) {
					// お気に入りのボールの所有者が家族ではない場合
					if (body.getFavoriteItem(FavItemType.BALL) == foundToy && !ownedByFamily) {
						if (body.getAge() % 20 == 0) {
							if (body.isRude()) {
								body.setHappiness(Happiness.VERY_SAD);
								body.addStress(20);
							} else {
								body.setHappiness(Happiness.SAD);
								body.addStress(5);
							}
							if (!body.isTalking()) {
								body.setMessage(GameMessages.getMessage(body, MessagePool.Action.LostTreasure), true);
							}
						}
					}
					return true;
				}
				int kickStrength[] = { -1, -4, -6 };
				if (body.getAge() % 20 == 0) {
					body.setHappiness(Happiness.HAPPY);
					body.addStress(-400);
				}
				// 自分のものではない
				if (!foundToy.isOwned(body)) {
					foundToy.setOwner(body);
					body.setFavoriteItem(FavItemType.BALL, foundToy);
					if (!body.isTalking()) {
						body.setHappiness(Happiness.HAPPY);
						body.setMessage(GameMessages.getMessage(body, MessagePool.Action.GetTreasure), true);
					}
				} else if (GameRandom.nextInt(10) == 0) {
					body.getInVain(true);
				} else {
					foundToy.kick(body.getDirX() * body.getStep(), body.getDirY() * body.getStep(),
							kickStrength[body.getBodyAgeState().ordinal()]);
				}
			} else {
				if (body.getFavoriteItem(FavItemType.BALL) == foundToy && !ownedByFamily) {
					if (body.getAge() % 20 == 0) {
						if (body.isRude()) {
							body.setHappiness(Happiness.VERY_SAD);
							body.addStress(20);
						} else {
							body.setHappiness(Happiness.SAD);
							body.addStress(5);
						}
						if (!body.isTalking()) {
							body.setMessage(GameMessages.getMessage(body, MessagePool.Action.LostTreasure), true);
						}
					}
				}
				body.moveTo(foundToy.getX(), foundToy.getY());
			}
		}
		return handled;
	}

	/**
	 * すぃーで遊ぶ
	 * 
	 * @param body ゆっくり
	 * @return 処理が行われたか
	 */
	public static final boolean checkSui(Yukkuri body) {
		List<Sui> list = new LinkedList<>(GameWorld.get().getCurrentMap().getSui().values());
		if (list.size() == 0) {
			return false;
		}

		if (canPlay(body) == false)
			return false;
		if (// || GameRandom.nextInt(100) != 0 ||
		body.takeMappedObj(body.getParentLinkId()) instanceof Sui) {
			return false;
		}

		// すぃーがあるかつ1/150の確率でゆっくりする
		if (GameRandom.nextInt(150) == 0) {
			if (!body.isTalking()) {
				body.setMessage(GameMessages.getMessage(body, MessagePool.Action.YukkuringSui), true);
			}
			return false;
		}
		boolean handled = true;
		Entity favoriteSui = body.getFavoriteItem(FavItemType.SUI);

		// 自分のすぃーがない場合
		if (favoriteSui == null) {
			int nearestDistance = body.getEyesightBase();
			for (WorldEntity sui : list) {
				// 最小距離のものが見つかっていたら
				if (nearestDistance < body.getStepDist()) {
					break;
				}
				int distance = Translate.distance(body.getX(), body.getY(), sui.getX(), sui.getY());
				// 視界内にすぃーがある
				if (nearestDistance > distance) {
					// 壁の向こうならなにもしない
					if (Barrier.acrossBarrier(body.getX(), body.getY(), sui.getX(), sui.getY(),
							Barrier.MAP_BODY[body.getBodyAgeState().ordinal()] + Barrier.BARRIER_KEKKAI)) {
						continue;
					}

					// 所有できない場合
					if (((Sui) sui).NoCanBind()) {
						Yukkuri bindBody = (Yukkuri) ((Sui) sui).getBindobj();
						// 所有者が家族ではないならなにもしない
						if (!(body.isParent(bindBody) || bindBody.isParent(body) || body.isPartner(bindBody)
								|| bindBody.isSister(body))) {
							continue;
						}
					}
					favoriteSui = sui;
					nearestDistance = distance;
				}
			}
		} else if (GameRandom.nextBoolean() && !body.isTalking()
				&& Translate.distance(body.getX(), body.getY(), favoriteSui.getX(), favoriteSui.getY()) < 200000) {
			body.setMessage(GameMessages.getMessage(body, MessagePool.Action.hasSui), true);
			EventLogic.addWorldEvent(new SuiSpeake(body, null, favoriteSui, 10), null, null);
			return false;
		}
		// すぃーが見つかった場合
		if (favoriteSui != null) {
			Yukkuri bindBody = (Yukkuri) ((Sui) favoriteSui).getBindobj();
			// プレイヤーに持ち上げられたら所有権を消す
			if (favoriteSui.isGrabbed() && favoriteSui.getZ() != 0) {
				bindBody = null;
			}
			// 所有者がいない場合
			if (bindBody == null) {
				if (!body.isTalking()) {
					// すぃー発見セリフ
					EventLogic.addWorldEvent(new SuiRideEvent(body, null, favoriteSui, 100), body,
							GameMessages.getMessage(body, MessagePool.Action.FindSui));
				}
			} else if (bindBody == body) {
				// 所有者が自分の場合
				if (!body.isTalking()) {
					// 自分のすぃーにのりにいくセリフ
					EventLogic.addWorldEvent(new SuiRideEvent(body, null, favoriteSui, 100), body,
							GameMessages.getMessage(body, MessagePool.Action.FindGetSui));
				}
			} else {
				// 自分以外が所有者の場合
				if (!body.isTalking()) {
					// EventLogic.addWorldEvent(new SuiRideEvent(bindBody, null, favoriteSui, 100),
					// body, GameMessages.getMessage(body, MessagePool.Action.HateYukkuri));
				}
			}
		} else if (GameWorld.get().getCurrentMap().getSui().size() > 0) {
			EventLogic.addBodyEvent(body, new SuiSpeake(null, null, null, 10), null, null);
		}
		return handled;
	}

	/**
	 * トランポリンで遊ぶ
	 * 
	 * @param body ゆっくり
	 * @return 処理が行われたか
	 */
	public static final boolean checkTrampoline(Yukkuri body) {
		if (body == null) {
			return false;
		}

		List<Trampoline> trampolineList = new LinkedList<>(GameWorld.get().getCurrentMap().getTrampoline().values());
		if (trampolineList.size() == 0) {
			return false;
		}

		if (canPlay(body) == false)
			return false;
		if (!body.isRude() && (body.isAdult() || body.wantToShit())) {
			return false;
		}

		Trampoline foundTrampoline = null;
		// 視界内の一番近いトランポリンを取得
		int nearestDistance = body.getEyesightBase();
		for (Trampoline trampoline : trampolineList) {
			// 最小距離のものが見つかっていたら
			if (nearestDistance < body.getStepDist()) {
				break;
			}
			int distance = Translate.distance(body.getX(), body.getY(), trampoline.getX(), trampoline.getY());
			if (nearestDistance > distance && (body.isRude()
					|| !Barrier.acrossBarrier(body.getX(), body.getY(), trampoline.getX(), trampoline.getY(),
							Barrier.MAP_BODY[body.getBodyAgeState().ordinal()] + Barrier.BARRIER_KEKKAI))) {
				foundTrampoline = trampoline;
				nearestDistance = distance;
			}
		}

		if (foundTrampoline == null) {
			return false;
		}

		// 乗った場合
		if (nearestDistance <= body.getStepDist()) {
			int kickStrength[] = { -1, -4, -6 };
			if (body.getAge() % 20L == 0L) {
				body.setHappiness(Happiness.HAPPY);
				body.addStress(-200);
			}

			if (body.getZ() == 0) {
				if (foundTrampoline.getOption() == 0) {
					if (body.getIntelligence() == Intelligence.FOOL
							&& GameRandom.nextInt(100) + 1 < foundTrampoline.getAccident2()
							|| body.getIntelligence() != Intelligence.FOOL
									&& GameRandom.nextInt(100) + 1 < foundTrampoline.getAccident1()) {
						body.kick(0, 0,
								((kickStrength[body.getBodyAgeState().ordinal()]
										+ kickStrength[body.getBodyAgeState().ordinal()] * GameRandom.nextInt(1))
										- GameRandom.nextInt(5)) * 3);
					} else {
						body.kick(0, 0,
								(kickStrength[body.getBodyAgeState().ordinal()]
										+ kickStrength[body.getBodyAgeState().ordinal()] * GameRandom.nextInt(1))
										- GameRandom.nextInt(5));
					}
				} else {
					if (body.getIntelligence() == Intelligence.FOOL
							&& GameRandom.nextInt(100) + 1 <= foundTrampoline.getAccident2()
							|| body.getIntelligence() != Intelligence.FOOL
									&& GameRandom.nextInt(100) + 1 < foundTrampoline.getAccident1()) {
						body.kick(body.getDirX() * body.getStep(), body.getDirY() * body.getStep(),
								((kickStrength[body.getBodyAgeState().ordinal()]
										+ kickStrength[body.getBodyAgeState().ordinal()] * GameRandom.nextInt(1))
										- GameRandom.nextInt(5)) * 3);
					} else {
						body.kick(body.getDirX() * body.getStep(), body.getDirY() * body.getStep(),
								(kickStrength[body.getBodyAgeState().ordinal()]
										+ kickStrength[body.getBodyAgeState().ordinal()] * GameRandom.nextInt(1))
										- GameRandom.nextInt(5));
					}
				}
			}
		} else {
			body.moveTo(foundTrampoline.getX(), foundTrampoline.getY());
		}
		return true;
	}

	/**
	 * 遊べる状態かどうか
	 * 
	 * @param body ゆっくり
	 * @return 遊べる状態かどうか
	 */
	public static boolean canPlay(Yukkuri body) {
		// 他の用事がある場合
		if (body.isToFood() || body.isToBody() || body.isToSukkiri() || body.isToSteal() || body.isToBed()
				|| body.isToShit()) {
			return false;
		}
		if (!body.canAction() || body.isDontMove() || body.isExciting() || body.isScare() || body.isDamaged()) {
			return false;
		}
		if (!body.canEventResponse()) {
			return false;
		}
		return true;
	}

}
