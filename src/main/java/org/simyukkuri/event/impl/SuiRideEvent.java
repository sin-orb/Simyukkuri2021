package org.simyukkuri.event.impl;

import org.simyukkuri.Const;
import org.simyukkuri.draw.Translate;
import org.simyukkuri.entity.core.Entity;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.world.item.Sui;
import org.simyukkuri.enums.EnumRelationMine;
import org.simyukkuri.enums.FavItemType;
import org.simyukkuri.enums.Happiness;
import org.simyukkuri.enums.PublicRank;
import org.simyukkuri.event.EventPacket;
import org.simyukkuri.event.EventPacket.EventPriority;
import org.simyukkuri.event.EventPacket.UpdateState;
import org.simyukkuri.logic.BodyLogic;
import org.simyukkuri.logic.EventLogic;
import org.simyukkuri.system.MessagePool;
import org.simyukkuri.util.GameMessages;
import org.simyukkuri.util.GameRandom;
import org.simyukkuri.util.GameText;

/***************************************************
 * すぃーの乗車管理イベント
 * protected Yukkuri from; // 乗るゆっくり
 * protected Yukkuri to; // 未使用
 * protected Entity target; // すぃー
 * protected int count; // 100
 */
public class SuiRideEvent extends EventPacket {

	private static final long serialVersionUID = -3480227497799647328L;
	int tick = 0;
	boolean hasMemberRide = false;

	/**
	 * コンストラクタ.
	 */
	public SuiRideEvent(Yukkuri f, Yukkuri t, Entity tgt, int cnt) {
		super(f, t, tgt, cnt);
		priority = EventPriority.MIDDLE; // すぃーの乗車イベントを食事、睡眠、トイレより上にする
	}

	public SuiRideEvent() {
	}

	public int getTick() {
		return tick;
	}

	public void setTick(int tick) {
		this.tick = tick;
	}

	public boolean isMemberride() {
		return hasMemberRide;
	}

	public void setMemberride(boolean memberride) {
		this.hasMemberRide = memberride;
	}

	// 参加チェック
	// ここで各種チェックを行い、イベントへ参加するかを返す
	// また、イベント優先度も必要に応じて設定できる
	@Override
	public boolean checkEventResponse(Yukkuri body) {
		Entity targetObject = body.takeMappedObj(this.target);
		if (targetObject == null) {
			return false;
		}
		Yukkuri sourceBody = org.simyukkuri.util.BodyRegistry.getBodyInstance(getFrom());
		if (sourceBody == null)
			return false;
		if (sourceBody == body) {
			return true;
		}
		if (!body.canEventResponse()) {
			return false;
		} else if (sourceBody.getCurrentEvent() == this) {
			if (body.isParent(sourceBody) || sourceBody.isParent(body) || body.isPartner(sourceBody)
					|| sourceBody.isSister(body)) {
				if (body.canAction() == false || body.isExciting() || body.isScare()) {
					return false;
				}
				// うんうん奴隷の場合
				if (body.getPublicRank() == PublicRank.UnunSlave) {
					// 自分との関係
					EnumRelationMine relation = BodyLogic.checkMyRelation(body, sourceBody);
					// 嘆く
					switch (relation) {
						case FATHER: // 父
						case MOTHER: // 母
							body.setMessage(GameMessages.getMessage(body, MessagePool.Action.HateWithEnvyAboutMother));
							break;
						case PARTNAR: // つがい
							body.setMessage(GameMessages.getMessage(body, MessagePool.Action.HateWithEnvyAboutPartner));
							break;
						case CHILD_FATHER: // 父の子供
							body.setMessage(GameMessages.getMessage(body, MessagePool.Action.HateWithEnvyAboutFather));
							break;
						case CHILD_MOTHER: // 母の子供
							body.setMessage(GameMessages.getMessage(body, MessagePool.Action.HateWithEnvyAboutMother));
							break;
						case ELDERSISTER: // 姉
							body.setMessage(GameMessages.getMessage(body, MessagePool.Action.HateWithEnvyAboutSister));
							break;
						case YOUNGSISTER: // 妹
							body.setMessage(
									GameMessages.getMessage(body, MessagePool.Action.HateWithEnvyAboutElderSister));
							break;
						default: // 他人
							break;
					}
					body.setHappiness(Happiness.VERY_SAD);
					body.stay();

					return false;
				}

				body.setWorldEventResMessage(GameMessages.getMessage(body, MessagePool.Action.FindGetSuiOtner),
						Const.HOLDMESSAGE, true, false);
				return true;
			}
		}

		return false;
	}

	// イベント開始動作
	@Override
	public void start(Yukkuri body) {
		Entity targetObject = body.takeMappedObj(this.target);
		if (targetObject == null) {
			return;
		}
		body.moveToEvent(this, targetObject.getX(), targetObject.getY());
	}

	// 毎フレーム処理
	@Override
	public UpdateState update(Yukkuri body) {
		Entity targetObject = body.takeMappedObj(this.target);
		Sui targetSui = (Sui) targetObject;
		if (targetSui == null) {
			return UpdateState.ABORT;
		}

		// すぃーを所持している場合
		if (body.getFavoriteItem(FavItemType.SUI) != null) {

			// 対象のすぃーに乗っていない場合は終了
			if (!targetSui.isriding(body)) {
				return null;
			}
			Yukkuri sourceBody = org.simyukkuri.util.BodyRegistry.getBodyInstance(getFrom());
			if (sourceBody == null)
				return UpdateState.ABORT;
			if (sourceBody == body) {
				// 乗客数が上限、またはカウント50以上の場合
				if (targetSui.getCurrent_bindbody_num() >= 3 || tick > 50) {
					body.setHappiness(Happiness.HAPPY);
					// すぃーが待機中の場合
					if (targetSui.getCurrent_condition() == 1) {
						// 乗ろうとしているゆっくりがいない、またはカウントが50の倍数の場合ランダムに移動する
						// ※移動中はすぃーの状態を変えるなりなんなりした方がいいのでは
						if (!hasMemberRide || tick % 50 == 0) {
							body.moveTo(GameRandom.nextInt(Translate.getMapW()),
									GameRandom.nextInt(Translate.getMapH() - Sui.getBounding().getHeight() / 2));
						}
						// カウントが500を超える場合
						if (tick > 500) {
							// しゃべってないなら降りる宣言
							if (!body.isTalking()) {
								body.setMessage(GameMessages.getMessage(body, MessagePool.Action.RideOffSui), true);
							}

							// すぃーから降りる
							targetSui.rideOff(body);
							return UpdateState.ABORT;
						}
					} else {
						if (!body.isTalking()) {
							if (GameRandom.nextBoolean()) {
								body.setBodyEventResMessage(GameMessages.getMessage(body, MessagePool.Action.RidingSui),
										Const.HOLDMESSAGE, true, false);
							} else {
								body.setBodyEventResMessage(
										GameMessages.getMessage(body, MessagePool.Action.DrivingSui),
										Const.HOLDMESSAGE, true, false);
							}
						}
						if (GameRandom.nextInt(100) == 0) {
							EventLogic.addWorldEvent(new SuiSpeake(null, null, targetObject, 1), null, null);
						}
					}
				}
				tick++;
			} else {
				// 処理対象とすぃーに乗ろうとしているゆっくりが異なる場合
				// しゃべっていないかつ、すぃーが待機中ではない場合
				if (!body.isTalking() && targetSui.getCurrent_condition() != 1) {
					// すぃーに乗っている時のセリフ
					body.setBodyEventResMessage(GameMessages.getMessage(body, MessagePool.Action.RidingSui),
							Const.HOLDMESSAGE,
							true, false);
				}

				// イベント実施中ではない、かつすぃーが待機中の場合
				if (sourceBody.getCurrentEvent() != this && targetSui.getCurrent_condition() == 1) {
					// すぃーからおりる
					if (!body.isTalking()) {
						body.setMessage(GameMessages.getMessage(body, MessagePool.Action.RideOffSui), true);
					}
					targetSui.rideOff(body);
					return UpdateState.ABORT;
				}
			}
		} else {
			// すぃーを所持していない場合

			// いずれかのすぃーに乗っている場合
			if (body.takeMappedObj(body.getParentLinkId()) != null) {
				return null;
			}
			Yukkuri sourceBody = org.simyukkuri.util.BodyRegistry.getBodyInstance(getFrom());
			// 移動する
			body.moveToEvent(this, targetObject.getX(), targetObject.getY());
			if (sourceBody == null)
				return UpdateState.ABORT;
			if (sourceBody == body && targetSui.iscanriding() || targetSui.getCurrent_bindbody_num() >= 3) {
				hasMemberRide = false;
				return UpdateState.ABORT;
			}
			if (sourceBody != body && sourceBody.getCurrentEvent() == null) {
				hasMemberRide = false;
				return UpdateState.ABORT;
			}
			if (body.isDontMove() || body.isExciting() || body.isScare()) {
				hasMemberRide = false;
				return UpdateState.ABORT;
			}
			if (sourceBody != body && sourceBody.getFavoriteItem(FavItemType.SUI) != null
					&& body.getFavoriteItem(FavItemType.SUI) == null && hasMemberRide == false
					&& GameRandom.nextBoolean()) {
				if (!body.isTalking()) {
					// 他人のすぃーに乗りたがる
					body.setMessage(GameMessages.getMessage(body, MessagePool.Action.WantRideSuiOtner), true);
				}
				sourceBody.moveTo(body.getX(), body.getY());
				hasMemberRide = true;
			}
		}
		return null;
	}

	// イベント目標に到着した際に呼ばれる
	// trueを返すとイベント終了
	@Override
	public boolean execute(Yukkuri body) {
		Entity targetObject = body.takeMappedObj(this.target);
		if (targetObject == null) {
			return false;
		}

		Sui targetSui = (Sui) targetObject;
		// すぃーが待機状態
		if (targetSui.getCurrent_condition() == 1) {
			// すぃーに乗る
			targetSui.rideOn(body);
			hasMemberRide = false;
		}

		return false;
	}

	@Override
	public void end(Yukkuri body) {
		// 他のイベントで強制的にイベントが終わることがある
		// すぃーにのってたら降りる
		Sui targetSui = (Sui) body.takeMappedObj(body.getParentLinkId());
		if (targetSui != null) {
			targetSui.rideOff(body);
		}

		hasMemberRide = false;
	}

	@Override
	public String toString() {
		return GameText.read("event_ridesui");
	}
}
