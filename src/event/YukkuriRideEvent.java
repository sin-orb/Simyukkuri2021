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
import src.system.ResourceUtil;
import src.util.YukkuriUtil;

/***************************************************
	おちびちゃん運びイベント
	protected Body from;			// 乗せるゆっくり
	protected Body to;				// 乗るゆっくり
	protected Obj target;			// 未使用
	protected int count;			// 100
*/
public class YukkuriRideEvent extends EventPacket implements java.io.Serializable {

	private static final long serialVersionUID = 7916220303996368395L;
	int tick = 0;
	boolean bMoveTarget = false;

	/**
	 * コンストラクタ.
	 */
	public YukkuriRideEvent(Body f, Body t, Obj tgt, int cnt) {
		super(f, t, tgt, cnt);
		priority = EventPriority.MIDDLE;// 食事、睡眠、トイレよりは上
	}
	
	public YukkuriRideEvent() {
		
	}

	// 参加チェック
	// ここで各種チェックを行い、イベントへ参加するかを返す
	// また、イベント優先度も必要に応じて設定できる
	@Override
	public boolean checkEventResponse(Body b) {
		Body to = YukkuriUtil.getBodyInstance(getTo());
		if (to == null) {
			return false;
		}
		Body from = YukkuriUtil.getBodyInstance(getFrom());
		if (from == b) {
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
		Body to = YukkuriUtil.getBodyInstance(getTo());
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
		Body from = YukkuriUtil.getBodyInstance(getFrom());
		if (from == null || from.canActionForEvent() == false || from.isRemoved()) {
			return UpdateState.ABORT;
		}
		Body to = YukkuriUtil.getBodyInstance(getTo());
		if (to == null || to.isDead() || to.isRemoved()) {
			return UpdateState.ABORT;
		}

		if (from.getCurrentEvent() != this) {
			return UpdateState.ABORT;
		}

		if (!from.isIdiot() && from.getIntelligence() != Intelligence.FOOL && from.findSick(to)) {
			from.setMessage(MessagePool.getMessage(to, MessagePool.Action.Surprise), 30);
			from.setHappiness(Happiness.VERY_SAD);
			from.setForceFace(ImageCode.CRYING.ordinal());
			return UpdateState.ABORT;
		}

		if (to.isNormalDirty()) {
			to.setLinkParent(-1);
			from.doPeropero(to);
			return UpdateState.ABORT;
		}

		// 親
		if (b == from) {
			// 一定期間で終了
			if (tick > 10000) {
				to.setZ(from.getZ());
				to.setLinkParent(-1);
				return UpdateState.ABORT;
			}

			if (to.takeMappedObj(to.getLinkParent()) == null) {
				if (tick % 20 != 0) {
					return null;
				}
				int nDistance = Translate.getRealDistance(from.getX(), from.getY(), to.getX(), to.getY());
				if (3 < nDistance) {
					// 子供に近づく
					from.moveToEvent(this, to.getX(), to.getY());
				} else {
					// 子供を頭にのせる
					to.setLinkParent(from.objId);
				}
			} else {
				// 子供をのせて移動する
				to.setX(from.getX());
				to.setY(from.getY());
				int nZ = Translate.invertZ(from.getCollisionY() + 15);
				nZ += from.getZ();
				to.setZ(nZ);
				to.setDirection(from.getDirection());
				Obj target = b.takeMappedObj(this.target);
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
								from.moveToEvent(this, target.getX(), target.getY());
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
								from.moveToEvent(this, target.getX(), target.getY());
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
								from.moveToEvent(this, target.getX(), target.getY());
							}
						}
					}
				} else {
					if (target instanceof Food) {
						// 餌を持っていたら落とす
						if (b.getTakeoutItem(TakeoutItemType.FOOD) != null) {
							b.dropTakeoutItem(TakeoutItemType.FOOD);
							to.setZ(from.getZ());
							to.setLinkParent(-1);
							return UpdateState.ABORT;
						}
					}
					// 目的地についたなら終了
					if (target != null) {
						int nDistance = Translate.getRealDistance(from.getX(), from.getY(), target.getX(),
								target.getY());
						if (3 < nDistance) {
							from.moveToEvent(this, target.getX(), target.getY());
						} else {
							to.setZ(from.getZ());
							to.setLinkParent(-1);
							return UpdateState.ABORT;
						}
					}
				}
			}
		} else {
			// 子供
			if (b.takeMappedObj(b.getLinkParent()) == null) {
				int nDistance = Translate.getRealDistance(to.getX(), to.getY(), from.getX(), from.getY());
				if (3 < nDistance) {
					// 親に近づく
					to.moveToEvent(this, from.getX(), from.getY());
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
		Body to = YukkuriUtil.getBodyInstance(getTo());
		if (to != null) to.setLinkParent(-1);
	}

	@Override
	public String toString() {
		return ResourceUtil.getInstance().read("event_ride");
	}
}