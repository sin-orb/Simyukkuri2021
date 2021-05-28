package src.event;

import src.Const;
import src.SimYukkuri;
import src.base.Body;
import src.base.EventPacket;
import src.base.Obj;
import src.draw.Translate;
import src.enums.EnumRelationMine;
import src.enums.FavItemType;
import src.enums.Happiness;
import src.enums.PublicRank;
import src.item.Sui;
import src.logic.BodyLogic;
import src.logic.EventLogic;
import src.system.MessagePool;

/***************************************************
	すぃーの乗車管理イベント
	protected Body from;			// 乗るゆっくり
	protected Body to;				// 未使用
	protected Obj target;			// すぃー
	protected int count;			// 100
*/
public class SuiRideEvent extends EventPacket implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	int tick = 0;
	boolean memberride = false;

	/**
	 * コンストラクタ.
	 */
	public SuiRideEvent(Body f, Body t, Obj tgt, int cnt) {
		super(f, t, tgt, cnt);
		priority = EventPriority.MIDDLE; // すぃーの乗車イベントを食事、睡眠、トイレより上にする
	}

	// 参加チェック
	// ここで各種チェックを行い、イベントへ参加するかを返す
	// また、イベント優先度も必要に応じて設定できる
	@Override
	public boolean checkEventResponse(Body b) {
		//		boolean ret = false;
		if (target == null) {
			return false;
		}

		if (getFrom() == b) {
			return true;
		}
		if (!b.canEventResponse()) {
			return false;
		} else if (getFrom().getCurrentEvent() == this) {
			if (b.isParent(getFrom()) || getFrom().isParent(b) || b.isPartner(getFrom()) || getFrom().isSister(b)) {
				if (b.canAction() == false || b.isExciting() || b.isScare()) {
					return false;
				}
				// うんうん奴隷の場合
				if (b.getPublicRank() == PublicRank.UnunSlave) {
					// 自分との関係
					EnumRelationMine eRelation = BodyLogic.checkMyRelation(b, getFrom());
					// 嘆く
					switch (eRelation) {
					case FATHER: // 父
					case MOTHER: // 母
						b.setMessage(MessagePool.getMessage(b, MessagePool.Action.HateWithEnvyAboutMother));
						break;
					case PARTNAR: // つがい
						b.setMessage(MessagePool.getMessage(b, MessagePool.Action.HateWithEnvyAboutPartner));
						break;
					case CHILD_FATHER: // 父の子供
						b.setMessage(MessagePool.getMessage(b, MessagePool.Action.HateWithEnvyAboutFather));
						break;
					case CHILD_MOTHER: // 母の子供
						b.setMessage(MessagePool.getMessage(b, MessagePool.Action.HateWithEnvyAboutMother));
						break;
					case ELDERSISTER: // 姉
						b.setMessage(MessagePool.getMessage(b, MessagePool.Action.HateWithEnvyAboutSister));
						break;
					case YOUNGSISTER: // 妹
						b.setMessage(MessagePool.getMessage(b, MessagePool.Action.HateWithEnvyAboutElderSister));
						break;
					default: // 他人
						break;
					}
					b.setHappiness(Happiness.VERY_SAD);
					b.stay();

					return false;
				}

				b.setWorldEventResMessage(MessagePool.getMessage(b, MessagePool.Action.FindGetSuiOtner),
						Const.HOLDMESSAGE, true, false);
				return true;
			}
		}

		return false;
	}

	// イベント開始動作
	@Override
	public void start(Body b) {
		if (target == null) {
			return;
		}
		b.moveToEvent(this, target.getX(), target.getY());
	}

	// 毎フレーム処理
	@Override
	public UpdateState update(Body b) {
		Sui s = (Sui) target;
		if (s == null) {
			return UpdateState.ABORT;
		}

		// すぃーを所持している場合
		if (b.getFavItem(FavItemType.SUI) != null) {

			//　対象のすぃーに乗っていない場合は終了
			if (!s.isriding(b)) {
				return null;
			}

			if (getFrom() == b) {
				// 乗客数が上限、またはカウント50以上の場合
				if (s.getcurrent_bindbody_num() >= 3 || tick > 50) {
					b.setHappiness(Happiness.HAPPY);
					// すぃーが待機中の場合
					if (s.getcurrent_condition() == 1) {
						// 乗ろうとしているゆっくりがいない、またはカウントが50の倍数の場合ランダムに移動する
						// ※移動中はすぃーの状態を変えるなりなんなりした方がいいのでは
						if (!memberride || tick % 50 == 0) {
							b.moveTo(SimYukkuri.RND.nextInt(Translate.mapW),
									SimYukkuri.RND.nextInt(Translate.mapH - Sui.getBounding().height / 2));
						}
						// カウントが500を超える場合
						if (tick > 500) {
							// しゃべってないなら降りる宣言
							if (!b.isTalking()) {
								b.setMessage(MessagePool.getMessage(b, MessagePool.Action.RideOffSui), true);
							}

							// すぃーから降りる
							s.rideOff(b);
							return UpdateState.ABORT;
						}
					} else {
						if (!b.isTalking()) {
							if (SimYukkuri.RND.nextBoolean()) {
								b.setBodyEventResMessage(MessagePool.getMessage(b, MessagePool.Action.RidingSui),
										Const.HOLDMESSAGE, true, false);
							} else {
								b.setBodyEventResMessage(MessagePool.getMessage(b, MessagePool.Action.DrivingSui),
										Const.HOLDMESSAGE, true, false);
							}
						}
						if (SimYukkuri.RND.nextInt(100) == 0) {
							EventLogic.addWorldEvent(new SuiSpeake(null, null, target, 1), null, null);
						}
					}
				}
				tick++;
			} else {
				// 処理対象とすぃーに乗ろうとしているゆっくりが異なる場合
				// しゃべっていないかつ、すぃーが待機中ではない場合
				if (!b.isTalking() && s.getcurrent_condition() != 1) {
					// すぃーに乗っている時のセリフ
					b.setBodyEventResMessage(MessagePool.getMessage(b, MessagePool.Action.RidingSui), Const.HOLDMESSAGE,
							true, false);
				}

				// イベント実施中ではない、かつすぃーが待機中の場合
				if (getFrom().getCurrentEvent() != this && s.getcurrent_condition() == 1) {
					// すぃーからおりる
					if (!b.isTalking()) {
						b.setMessage(MessagePool.getMessage(b, MessagePool.Action.RideOffSui), true);
					}
					s.rideOff(b);
					return UpdateState.ABORT;
				}
			}
		} else {
			// すぃーを所持していない場合

			// いずれかのすぃーに乗っている場合
			if (b.getLinkParent() != null) {
				return null;
			}

			// 移動する
			b.moveToEvent(this, target.getX(), target.getY());
			if (getFrom() == b && s.iscanriding() || s.getcurrent_bindbody_num() >= 3) {
				memberride = false;
				return UpdateState.ABORT;
			}
			if (getFrom() != b && getFrom().getCurrentEvent() == null) {
				memberride = false;
				return UpdateState.ABORT;
			}
			if (b.isDontMove() || b.isExciting() || b.isScare()) {
				memberride = false;
				return UpdateState.ABORT;
			}
			if (getFrom() != b && getFrom().getFavItem(FavItemType.SUI) != null
					&& b.getFavItem(FavItemType.SUI) == null && memberride == false && SimYukkuri.RND.nextBoolean()) {
				if (!b.isTalking()) {
					// 他人のすぃーに乗りたがる
					b.setMessage(MessagePool.getMessage(b, MessagePool.Action.WantRideSuiOtner), true);
				}
				getFrom().moveTo(b.getX(), b.getY());
				memberride = true;
			}
		}
		return null;
	}

	// イベント目標に到着した際に呼ばれる
	// trueを返すとイベント終了
	@Override
	public boolean execute(Body b) {
		if (target == null) {
			return false;
		}

		Sui s = (Sui) target;
		// すぃーが待機状態
		if (s.getcurrent_condition() == 1) {
			// すぃーに乗る
			s.rideOn(b);
			memberride = false;
		}

		return false;
	}

	@Override
	public void end(Body b) {
		//他のイベントで強制的にイベントが終わることがある	
		// すぃーにのってたら降りる
		Sui s = (Sui) b.getLinkParent();
		if (s != null) {
			s.rideOff(b);
		}

		memberride = false;
	}

	@Override
	public String toString() {
		return "すぃーにのるよ";
	}
}