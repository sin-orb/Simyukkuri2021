package src.event;

import src.SimYukkuri;
import src.base.Body;
import src.base.EventPacket;
import src.base.Obj;
import src.draw.Terrarium;
import src.draw.Translate;
import src.enums.Happiness;
import src.enums.ImageCode;
import src.enums.Intelligence;
import src.enums.TakeoutItemType;
import src.item.Food;
import src.logic.BedLogic;
import src.logic.FamilyActionLogic;
import src.system.MessagePool;

/***************************************************
	おちびちゃん運びイベント
	protected Body from;			// 乗せるゆっくり
	protected Body to;				// 乗るゆっくり
	protected Obj target;			// 未使用
	protected int count;			// 100
*/
public class YukkuriRideEvent extends EventPacket implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	int tick = 0;
	boolean bMoveTarget = false;

	/**
	 * コンストラクタ.
	 */
	public YukkuriRideEvent(Body f, Body t, Obj tgt, int cnt) {
		super(f, t, tgt, cnt);
		priority = EventPriority.MIDDLE;// 食事、睡眠、トイレよりは上
	}

	// 参加チェック
	// ここで各種チェックを行い、イベントへ参加するかを返す
	// また、イベント優先度も必要に応じて設定できる
	@Override
	public boolean checkEventResponse(Body b) {
		if (to == null) {
			return false;
		}

		if (getFrom() == b) {
			return true;
		}

		if (to == b) {
			return true;
		}

		return false;
	}

	// イベント開始動作
	@Override
	public void start(Body b) {
		if (to == null) {
			return;
		}
		b.setCurrentEvent(this);
		b.clearActionsForEvent();
		b.moveToEvent(this, to.getX(), to.getY());
	}

	// 毎フレーム処理
	@Override
	public UpdateState update(Body b) {
		tick++;
		if (getFrom() == null || getFrom().canActionForEvent() == false || getFrom().isRemoved()) {
			return UpdateState.ABORT;
		}
		if (to == null || to.isDead() || to.isRemoved()) {
			return UpdateState.ABORT;
		}

		if (getFrom().getCurrentEvent() != this) {
			return UpdateState.ABORT;
		}

		if (!getFrom().isIdiot() && getFrom().getIntelligence() != Intelligence.FOOL && getFrom().findSick(to)) {
			getFrom().setMessage(MessagePool.getMessage(to, MessagePool.Action.Surprise), 30);
			getFrom().setHappiness(Happiness.VERY_SAD);
			getFrom().setForceFace(ImageCode.CRYING.ordinal());
			return UpdateState.ABORT;
		}

		if (to.isNormalDirty()) {
			to.setLinkParent(null);
			getFrom().doPeropero(to);
			return UpdateState.ABORT;
		}

		// 親
		if (b == getFrom()) {
			// 一定期間で終了
			if (tick > 10000) {
				to.setZ(getFrom().getZ());
				to.setLinkParent(null);
				return UpdateState.ABORT;
			}

			if (to.getLinkParent() == null) {
				if (tick % 20 != 0) {
					return null;
				}
				int nDistance = Translate.getRealDistance(getFrom().getX(), getFrom().getY(), to.getX(), to.getY());
				if (3 < nDistance) {
					// 子供に近づく
					getFrom().moveToEvent(this, to.getX(), to.getY());
				} else {
					// 子供を頭にのせる
					to.setLinkParent(getFrom());
				}
			} else {
				// 子供をのせて移動する
				to.setX(getFrom().getX());
				to.setY(getFrom().getY());
				int nZ = Translate.invertZ(getFrom().getCollisionY() + 15);
				nZ += getFrom().getZ();
				to.setZ(nZ);
				to.setDirection(getFrom().getDirection());

				if (target != null) {
					bMoveTarget = true;
				}

				// 目的地がない場合は目的地チェック
				if (!bMoveTarget) {
					// 空腹
					if (target == null) {
						if (to.isHungry()) {
							// 餌を持っていたら落とす
							b.dropTakeoutItem(TakeoutItemType.FOOD);
							Obj found = FamilyActionLogic.searchFood(b);
							if (found != null) {
								target = found;
								bMoveTarget = true;
								getFrom().moveToEvent(this, target.getX(), target.getY());
							}
						}
					}

					// トイレ
					if (target == null) {
						if (to.wantToShit()) {
							Obj found = FamilyActionLogic.searchToilet(b);
							if (found != null) {
								target = found;
								bMoveTarget = true;
								getFrom().moveToEvent(this, target.getX(), target.getY());
							}
						}
					}

					// ベッド
					if (target == null) {
						if (to.isSleepy()
								|| Terrarium.getDayState().ordinal() >= Terrarium.DayState.EVENING.ordinal()) {
							Obj found = BedLogic.searchBed(b);
							if (found != null) {
								target = found;
								bMoveTarget = true;
								getFrom().moveToEvent(this, target.getX(), target.getY());
							}
						}
					}
				} else {
					if (target instanceof Food) {
						// 餌を持っていたら落とす
						if (b.getTakeoutItem(TakeoutItemType.FOOD) != null) {
							b.dropTakeoutItem(TakeoutItemType.FOOD);
							to.setZ(getFrom().getZ());
							to.setLinkParent(null);
							return UpdateState.ABORT;
						}
					}
					// 目的地についたなら終了
					if (target != null) {
						int nDistance = Translate.getRealDistance(getFrom().getX(), getFrom().getY(), target.getX(),
								target.getY());
						if (3 < nDistance) {
							getFrom().moveToEvent(this, target.getX(), target.getY());
						} else {
							to.setZ(getFrom().getZ());
							to.setLinkParent(null);
							return UpdateState.ABORT;
						}
					}
				}
			}
		} else {
			// 子供
			if (b.getLinkParent() == null) {
				int nDistance = Translate.getRealDistance(to.getX(), to.getY(), getFrom().getX(), getFrom().getY());
				if (3 < nDistance) {
					// 親に近づく
					to.moveToEvent(this, getFrom().getX(), getFrom().getY());
				} else {
					to.stay();
				}
			} else {
				if (!to.isDamaged() && !to.isNeedled()) {
					// 親の頭の上で待機
					if (SimYukkuri.RND.nextInt(30) == 0) {
						to.addMemories(10);
						to.addStress(-150);
						if (!to.isSleeping() && !to.isDead()) {
							if (SimYukkuri.RND.nextInt(10) == 0) {
								to.setMessage(MessagePool.getMessage(to, MessagePool.Action.Flying), 30);
							} else {
								to.setMessage(MessagePool.getMessage(to, MessagePool.Action.Relax), 30);
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
	@Override
	public boolean execute(Body b) {
		return false;
	}

	@Override
	public void end(Body b) {
		//他のイベントで強制的にイベントが終わることがある
		// 子供をおろす
		to.setLinkParent(null);
	}

	@Override
	public String toString() {
		return "おちびはこび";
	}
}