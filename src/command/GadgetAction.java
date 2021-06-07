package src.command;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import src.SimYukkuri;
import src.attachment.ANYDAmpoule;
import src.attachment.AccelAmpoule;
import src.attachment.Ants;
import src.attachment.Badge;
import src.attachment.BreedingAmpoule;
import src.attachment.HungryAmpoule;
import src.attachment.OrangeAmpoule;
import src.attachment.PoisonAmpoule;
import src.attachment.StopAmpoule;
import src.attachment.VeryShitAmpoule;
import src.base.Body;
import src.base.Obj;
import src.base.ObjEX;
import src.base.Okazari;
import src.command.GadgetMenu.ActionTarget;
import src.command.GadgetMenu.GadgetList;
import src.command.GadgetMenu.MainCategoryName;
import src.draw.Terrarium;
import src.draw.Translate;
import src.enums.BodyRank;
import src.enums.Direction;
import src.enums.PublicRank;
import src.event.PredatorsGameEvent;
import src.game.Shit;
import src.game.Stalk;
import src.game.Vomit;
import src.item.Barrier;
import src.item.Beltconveyor;
import src.item.BeltconveyorObj;
import src.item.BreedingPool;
import src.item.Diffuser;
import src.item.Farm;
import src.item.Food;
import src.item.OrangePool;
import src.item.Pool;
import src.item.Yunba;
import src.logic.BadgeLogic;
import src.logic.BodyLogic;
import src.logic.EventLogic;
import src.logic.FamilyActionLogic;
import src.system.Cash;
import src.system.MainCommandUI;
import src.system.MapPlaceData;
import src.system.MessagePool;

/******************************************************************

	各コマンドの実行部分
	SimYukkuri.javaが長いので分割

*/

public class GadgetAction {

	/** 即時実行処理(清掃系)
	 *
	 * @param item 対象オブジェクト
	 */
	public static final void immediateEvaluate(GadgetList item) {
		boolean isBody = false;
		boolean isDead = false;
		boolean isShit = false;
		boolean isFood = false;
		boolean isWall = false;
		boolean isRemoveAll = false;

		switch (item) {
		// 清掃
		case YU_CLEAN:
			isBody = true;
			break;
		case BODY:
			isDead = true;
			break;
		case SHIT:
			isShit = true;
			break;
		case ETC:
			isFood = true;
			break;
		case ALL:
			isShit = true;
			isFood = true;
			isDead = true;
			break;
		// 壁
		case ALL_DELETE:
			isWall = true;
			break;
		case REMOVEALL:
			isRemoveAll = true;
		default:
			break;
		}

		Body[] bodyList = SimYukkuri.world.getCurrentMap().body.toArray(new Body[0]);
		List<Shit> shitList = SimYukkuri.world.getCurrentMap().shit;
		List<Vomit> vomitList = SimYukkuri.world.getCurrentMap().vomit;
		Food[] foodList = SimYukkuri.world.getCurrentMap().food.toArray(new Food[0]);
		List<Stalk> stalkList = SimYukkuri.world.getCurrentMap().stalk;
		List<Barrier> wallList = SimYukkuri.world.getCurrentMap().barrier;
		if (isBody) {
			for (Body b : bodyList) {
				if (!b.isDead())
					b.setCleaning();
			}
		}
		if (isDead) {
			for (Body b : bodyList) {
				if (b.isDead())
					b.remove();
			}
		}
		if (isShit) {
			for (Shit s : shitList) {
				s.remove();
			}
			for (Vomit v : vomitList) {
				v.remove();
			}
		}
		if (isFood) {
			for (Food f : foodList) {
				if (f.isEmpty() || f.getFoodType() == Food.FoodType.STALK) {
					f.remove();
				}
			}
			Stalk[] stalks = stalkList.toArray(new Stalk[0]);
			for (Stalk s : stalks) {
				if (s.getPlantYukkuri() == null) {
					s.remove();
				} else if (checkNoBabyStalk(s)) {
					s.remove();
				}
			}
		}
		if (isWall) {
			wallList.clear();
			MapPlaceData.clearMap(SimYukkuri.world.getCurrentMap().wallMap);
		}
		if (isRemoveAll) {
			for (Body bodyTarget : bodyList) {
				bodyTarget.remove();
			}
			for (Shit s : shitList) {
				s.remove();
			}
			shitList.clear();
			for (Vomit v : vomitList) {
				v.remove();
			}
			vomitList.clear();
			for (Food f : foodList) {
				if (f.isEmpty())
					f.remove();
				if (f.getFoodType() == Food.FoodType.STALK)
					f.remove();
			}

			for (Stalk s : stalkList) {
				if (s.getPlantYukkuri() == null) {
					s.remove();
				}
			}
			MapPlaceData.clearMap(SimYukkuri.world.getCurrentMap().wallMap);
		}
	}

	private static boolean checkNoBabyStalk(Stalk s) {
		if (s.getBindBaby().size() == 0) {
			return true;
		}
		for (Body b : s.getBindBaby()) {
			if (b != null) {
				if (!b.isDead()) {
					return false;
				}
			}
		}
		return true;
	}

	/** 左クリック処理
	 *
	 * @param item 実行内容
	 * @param target 対象アイテム
	 * @param ev 入力されたマウス操作
	 * @param fieldMousePos マウスの座標
	 * @return
	 */
	public static final ObjEX leftClickEvaluate(GadgetList item, Obj target, MouseEvent ev, int[] fieldMousePos) {
		ObjEX ret = null;
		ActionTarget eActionTarget = item.getActionTarget();

		//実行対象の選別
		if (eActionTarget == ActionTarget.TERRAIN_AND_GADET) {
			if (target != null) {
				eActionTarget = ActionTarget.GADGET;
			} else {
				eActionTarget = ActionTarget.TERRAIN;
			}
		}

		// クリック対象とガジェットの選択モードチェック
		if (eActionTarget == ActionTarget.TERRAIN) {
			// 背景設置物
			if (target != null)
				return null;

			// 今のところキーは使用しないので未チェック
			// 座標変換と設置可能範囲チェック
			Class<?> cls = item.getGadgetClass();
			Method mtd;
			Rectangle bound = null;
			try {
				mtd = cls.getMethod("getBounding", (Class<?>[]) null);
				bound = (Rectangle) mtd.invoke(cls, (Object[]) null);
			} catch (NoSuchMethodException | SecurityException
					| IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				e.printStackTrace();
			}

			Point pos = Translate.calcObjctPutPoint(fieldMousePos[0], fieldMousePos[1], bound);
			if (pos != null) {
				// 設置実行
				try {
					Constructor<?> cst = cls.getConstructor(int.class, int.class, int.class);
					ret = (ObjEX) cst.newInstance(pos.x, pos.y, item.getInitOption());
					if (ret != null) {
						Cash.buyItem(ret);
					}
				} catch (NoSuchMethodException | SecurityException | InstantiationException
						| IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		} else if (eActionTarget == ActionTarget.WALL) {
			// 壁選択
			switch (item) {
			case WALL_DELETE:
				Point pos = Translate.invert(fieldMousePos[0], fieldMousePos[1]);
				if (pos != null) {
					Barrier found = Barrier.getBarrier(pos.x, pos.y, 1);
					if (found != null) {
						Barrier.clearBarrier(found);
					}
				}
				break;
			default:
				break;
			}
		} else if (item.getActionTarget() == ActionTarget.FIELD) {
			// フィールド選択
			switch (item) {
			case FIELD_DELETE:
				Beltconveyor belt = Beltconveyor.getBeltconveyor(fieldMousePos[0], fieldMousePos[1]);
				if (belt != null) {
					Beltconveyor.deleteBelt(belt);
				} else {
					Farm farm = Farm.getFarm(fieldMousePos[0], fieldMousePos[1]);
					if (farm != null) {
						Farm.deleteFarm(farm);
					} else {
						Pool pool = Pool.getPool(fieldMousePos[0], fieldMousePos[1]);
						if (pool != null) {
							Pool.deletePool(pool);
						}
					}
				}
				break;
			default:
				break;
			}
		} else {
			// 選択物体
			if (target == null)
				return null;
			switch (item.getGroup()) {
			case TOOL:
				evaluateTool(item, ev, target);
				break;
			case TOOL2:
				evaluateTool2(item, ev, target);
				break;
			case AMPOULE:
				evaluateAmpoule(item, ev, target);
				break;
			case CLEAN:
				evaluateClean(item, ev, target);
				break;
			case ACCESSORY:
				evaluateAccessory(item, ev, target);
				break;
			case PANTS:
				evaluatePants(item, ev, target);
				break;
			case FLOOR:
				evaluateFloorItems(item, ev, target);
				break;
			case TOYS:
				evaluateToys(item, ev, target);
				break;
			case CONVEYOR:
				evaluateConveyor(item, ev, target);
				break;
			case VOICE:
				evaluateCommunicate(item, ev, target);
				break;
			case TEST:
				evaluateTest(item, ev, target);
				break;
			default:
				break;
			}
		}
		return ret;
	}

	/**左複数クリック処理
	 *
	 * @param item 実行内容
	 * @param target 対象アイテム
	 * @param ev 入力されたマウス操作
	 * @param fieldMousePos マウスの座標
	 * @return
	 */
	public static final ObjEX leftMultiClickEvaluate(GadgetList item, Obj target, MouseEvent ev, int[] fieldMousePos) {

		ObjEX ret = null;

		// いまのところフィールドグループのみ使用
		if (item.getActionTarget() == ActionTarget.TERRAIN) {
			if (item.getGroup() == MainCategoryName.BARRIER) {
				// 始点のクリック
				if (SimYukkuri.fieldSX == -1 || SimYukkuri.fieldSY == -1) {
					// フィールドタイプの設定
					SimYukkuri.fieldType = 0;
					switch (item) {
					case GAP_MINI:
						SimYukkuri.fieldType = Barrier.BARRIER_GAP_MINI;
						break;
					case GAP_BIG:
						SimYukkuri.fieldType = Barrier.BARRIER_GAP_BIG;
						break;
					case NET_MINI:
						SimYukkuri.fieldType = Barrier.BARRIER_NET_MINI;
						break;
					case NET_BIG:
						SimYukkuri.fieldType = Barrier.BARRIER_NET_BIG;
						break;
					case WALL:
						SimYukkuri.fieldType = Barrier.BARRIER_WALL;
						break;
					case ITEM:
						SimYukkuri.fieldType = Barrier.BARRIER_ITEM;
						break;
					case POOL:
						SimYukkuri.fieldType = 100; // 値に意味は無く0以外ならなんでも
						break;
					case FARM:
						SimYukkuri.fieldType = 101; // 値に意味は無く0以外ならなんでも
						break;
					case BELTCONVEYOR:
						SimYukkuri.fieldType = 102; // 値に意味は無く0以外ならなんでも
					case NoUNUN:
						SimYukkuri.fieldType = Barrier.BARRIER_NOUNUN;
						break;
					case KEKKAI:
						SimYukkuri.fieldType = Barrier.BARRIER_KEKKAI;
						break;
					default:
						break;
					}

					if (SimYukkuri.fieldType > 0) {
						// フィールド外で線を引かせない
						if (Translate.inInvertLimit(fieldMousePos[0], fieldMousePos[1])) {
							SimYukkuri.fieldSX = fieldMousePos[0];
							SimYukkuri.fieldSY = fieldMousePos[1];
							SimYukkuri.fieldEX = SimYukkuri.fieldSX;
							SimYukkuri.fieldEY = SimYukkuri.fieldSY;
						}
					}
				} else {
					// 終点のクリック
					switch (item) {
					case GAP_MINI:
					case GAP_BIG:
					case NET_MINI:
					case NET_BIG:
					case WALL:
					case ITEM:
					case NoUNUN:
					case KEKKAI:
						// フィールド外で線を引かせない
						if (!Translate.inInvertLimit(fieldMousePos[0], fieldMousePos[1])) {
							break;
						}
						SimYukkuri.fieldEX = fieldMousePos[0];
						SimYukkuri.fieldEY = fieldMousePos[1];
						if ((SimYukkuri.fieldSX != SimYukkuri.fieldEX)
								|| (SimYukkuri.fieldSY != SimYukkuri.fieldEY)) {
							new Barrier(SimYukkuri.fieldSX, SimYukkuri.fieldSY, SimYukkuri.fieldEX, SimYukkuri.fieldEY,
									SimYukkuri.fieldType);
						}
						SimYukkuri.fieldSX = SimYukkuri.fieldEX;
						SimYukkuri.fieldSY = SimYukkuri.fieldEY;
						SimYukkuri.fieldEX = SimYukkuri.fieldSX;
						SimYukkuri.fieldEY = SimYukkuri.fieldSY;
						break;
					case POOL:
						SimYukkuri.fieldEX = fieldMousePos[0];
						SimYukkuri.fieldEY = fieldMousePos[1];
						new Pool(SimYukkuri.fieldSX, SimYukkuri.fieldSY,
								SimYukkuri.fieldEX, SimYukkuri.fieldEY);
						SimYukkuri.fieldSX = -1;
						SimYukkuri.fieldSY = -1;
						SimYukkuri.fieldEX = -1;
						SimYukkuri.fieldEY = -1;
						break;
					case FARM:
						SimYukkuri.fieldEX = fieldMousePos[0];
						SimYukkuri.fieldEY = fieldMousePos[1];
						new Farm(SimYukkuri.fieldSX, SimYukkuri.fieldSY, SimYukkuri.fieldEX, SimYukkuri.fieldEY);
						SimYukkuri.fieldSX = -1;
						SimYukkuri.fieldSY = -1;
						SimYukkuri.fieldEX = -1;
						SimYukkuri.fieldEY = -1;
						break;
					case BELTCONVEYOR:
						SimYukkuri.fieldEX = fieldMousePos[0];
						SimYukkuri.fieldEY = fieldMousePos[1];
						new Beltconveyor(SimYukkuri.fieldSX, SimYukkuri.fieldSY, SimYukkuri.fieldEX,
								SimYukkuri.fieldEY);
						SimYukkuri.fieldSX = -1;
						SimYukkuri.fieldSY = -1;
						SimYukkuri.fieldEX = -1;
						SimYukkuri.fieldEY = -1;
						break;
					default:
						break;
					}
				}
			}

			// オブジェクトでベルトコンベアを置きたい
			if (item.getGroup() == MainCategoryName.CONVEYOR) {
				// 始点のクリック
				if (SimYukkuri.fieldSX == -1 || SimYukkuri.fieldSY == -1) {

					switch (item) {
					case BELTCONVEYOR_CUSTOM:
						SimYukkuri.fieldSX = fieldMousePos[0];
						SimYukkuri.fieldSY = fieldMousePos[1];
						SimYukkuri.fieldEX = SimYukkuri.fieldSX;
						SimYukkuri.fieldEY = SimYukkuri.fieldSY;
						break;
					default:
						break;
					}
				} else {
					// 終点のクリック
					switch (item) {
					case BELTCONVEYOR_CUSTOM:
						SimYukkuri.fieldEX = fieldMousePos[0];
						SimYukkuri.fieldEY = fieldMousePos[1];
						new BeltconveyorObj(0, 0, 5);
						SimYukkuri.fieldSX = -1;
						SimYukkuri.fieldSY = -1;
						SimYukkuri.fieldEX = -1;
						SimYukkuri.fieldEY = -1;
						break;
					default:
						break;
					}
				}
			}
		}
		return ret;
	}

	/**プレイヤー操作によらないアイテム設置
	 *
	 * @param cls 設置されるアイテム
	 * @param px 設置場所のX座標
	 * @param py 設置場所のY座標
	 * @param initOption 設置アイテムのオプション情報
	 * @return
	 */
	public static ObjEX putObjEX(Class<?> cls, int px, int py, int initOption) {
		ObjEX ret = null;
		try {
			Constructor<?> cst = cls.getConstructor(int.class, int.class, int.class);
			ret = (ObjEX) cst.newInstance(px, py, initOption);
		} catch (NoSuchMethodException | SecurityException | InstantiationException
				| IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return ret;
	}

	// 対象物をクリックするアクション-----------------------------------------------

	/**対象物をクリックするアクション1
	 * <br>Toolカテゴリの実行
	 *
	 * @param item 実行内容
	 * @param ev 入力されたマウスの動作
	 * @param found 対象オブジェクト
	 */
	public static void evaluateTool(GadgetList item, MouseEvent ev, Obj found) {
		switch (item) {
		case PUNISH:
			GadgetMenu.executeBodyMethod(ev, found, "strikeByPunish");
			break;
		case SNAPPING:
			if (ev.isShiftDown()) {
				List<Body> bodyList = SimYukkuri.world.getCurrentMap().body;
				for (Obj o : bodyList) {
					o.kick();
				}
				List<Shit> shitList = SimYukkuri.world.getCurrentMap().shit;
				for (Obj o : shitList) {
					o.kick();
				}
				List<Vomit> vomitList = SimYukkuri.world.getCurrentMap().vomit;
				for (Obj o : vomitList) {
					o.kick();
				}
			} else {
				found.kick();
			}
			break;
		case VIBRATOR:
			GadgetMenu.executeBodyMethod(ev, found, "forceToExcite");
			break;
		case PENICUT:
			GadgetMenu.executeBodyMethod(ev, found, "cutPenipeni");
			break;
		case JUICE:
			GadgetMenu.executeBodyMethod(ev, found, "giveJuice");
			break;
		case Medical_JUICE:
			GadgetMenu.executeBodyMethod(ev, found, "injectJuice");
			break;
		case LEMON_SPLAY:
			GadgetMenu.executeBodyMethod(ev, found, "forceToSleep");
			break;
		case Pheromone_SPLAY:
			GadgetMenu.executeBodyMethod(ev, found, "invPheromone");
			break;
		case HAMMER:
			if (ev.isShiftDown()) {
				List<Body> bodyList = SimYukkuri.world.getCurrentMap().body;
				for (Body b : bodyList) {
					b.strikeByHammer();
					if (!b.isHasPants() && !b.isDead() && !b.isShutmouth()) {
						int ofsX = Translate.invertX(b.getCollisionX() >> 1, b.getY());
						if (b.getDirection() == Direction.LEFT)
							ofsX = -ofsX;
						if (!b.isPacked() && !b.isShutmouth())
							SimYukkuri.mypane.terrarium.addVomit(b.getX() + ofsX, b.getY(), b.getZ(), b,
									b.getShitType());
						b.stay();
					}
					b.setDirty(true);
				}
				Terrarium.setAlarm();
			} else {
				if (found instanceof Body) {
					Body b = (Body) found;
					b.strikeByHammer();
					if (!b.isHasPants() && !b.isDead() && !b.isShutmouth()) {
						int ofsX = Translate.invertX(b.getCollisionX() >> 1, b.getY());
						if (b.getDirection() == Direction.LEFT)
							ofsX = -ofsX;
						if (!b.isPacked() && !b.isShutmouth())
							SimYukkuri.mypane.terrarium.addVomit(b.getX() + ofsX, b.getY(), b.getZ(), b,
									b.getShitType());
						b.stay();
					}
					b.setDirty(true);
					Terrarium.setAlarm();
				}
			}
			break;
		case GATHERINJECTINTO:
			if (found instanceof Body) {
				if (SimYukkuri.sperm == null) {
					SimYukkuri.sperm = ((Body) found).getDna();
				} else {
					((Body) found).injectInto(SimYukkuri.sperm);
					SimYukkuri.sperm = null;
				}
				// 精子餡の保持状態表示変更
				MainCommandUI.showPlayerStatus();
			} else {
				SimYukkuri.sperm = null;
			}
			break;
		case DRIPSPERM:
			if (found instanceof Body) {
				if (SimYukkuri.sperm == null) {
					SimYukkuri.sperm = ((Body) found).getDna();
					((Body) found).strikeByPunish();
					Terrarium.setAlarm();
				} else {
					((Body) found).dripSperm(SimYukkuri.sperm);
					SimYukkuri.sperm = null;
				}
				// 精子餡の保持状態表示変更
				MainCommandUI.showPlayerStatus();
			} else {
				SimYukkuri.sperm = null;
			}
			break;
		case PUNCH:
			if (ev.isShiftDown()) {
				List<Body> bodyList = SimYukkuri.world.getCurrentMap().body;
				for (Body b : bodyList) {
					b.strikeByPunch();
					if (!b.isHasPants() && !b.isDead() && !b.isShutmouth()) {
						int ofsX = Translate.invertX(b.getCollisionX() >> 1, b.getY());
						if (b.getDirection() == Direction.LEFT)
							ofsX = -ofsX;
						if (!b.isPacked() && !b.isShutmouth())
							SimYukkuri.mypane.terrarium.addVomit(b.getX() + ofsX, b.getY(), b.getZ(), b,
									b.getShitType());
						b.stay();
					}
					b.setDirty(true);
				}
				Terrarium.setAlarm();
			} else {
				if (found instanceof Body) {
					Body b = (Body) found;
					b.strikeByPunch();
					if (!b.isHasPants() && !b.isDead() && !b.isShutmouth()) {
						int ofsX = Translate.invertX(b.getCollisionX() >> 1, b.getY());
						if (b.getDirection() == Direction.LEFT)
							ofsX = -ofsX;
						if (!b.isPacked() && !b.isShutmouth())
							SimYukkuri.mypane.terrarium.addVomit(b.getX() + ofsX, b.getY(), b.getZ(), b,
									b.getShitType());
						b.stay();
					}
					b.setDirty(true);
					Terrarium.setAlarm();
				}
			}
			break;
		case GODHAND:
			if (ev.isShiftDown()) {
				List<Body> bodyList = SimYukkuri.world.getCurrentMap().body;
				int nSize = bodyList.size();
				for (int i = nSize - 1; -1 < i; i--) {
					Body b = bodyList.get(i);
					if (b != null) {
						GadgetTool.doGodHand(b);
					}
				}
			} else {
				if (found instanceof Body) {
					Body b = (Body) found;
					GadgetTool.doGodHand(b);
				}
			}
			break;
		case PEAL:
			Body[] bodyListP = SimYukkuri.world.getCurrentMap().body.toArray(new Body[0]);
			if (ev.isShiftDown()) {
				boolean flag = true;
				if (found instanceof Body) {
					flag = !((Body) found).isPealed();
				}
				for (Body b : bodyListP) {
					if (!flag && b.isPealed())
						b.Peal();
					else if (flag && !b.isPealed())
						b.Peal();
				}
			} else if (ev.isControlDown()) {
				for (Body b : bodyListP) {
					if (b.isPealed())
						b.Peal();
					else
						b.Peal();
				}
			} else {
				if (found instanceof Body) {
					if (((Body) found).isPealed())
						((Body) found).Peal();
					else
						((Body) found).Peal();
				}
			}
			break;

		case Blind:
			Body[] bodyListB = SimYukkuri.world.getCurrentMap().body.toArray(new Body[0]);
			if (ev.isShiftDown()) {
				boolean flag = true;
				if (found instanceof Body) {
					flag = !((Body) found).isBlind();
				}
				for (Body b : bodyListB) {
					if (!flag && b.isBlind())
						b.breakeyes();
					else if (flag && !b.isBlind())
						b.breakeyes();
				}
			} else if (ev.isControlDown()) {
				for (Body b : bodyListB) {
					if (b.isBlind())
						b.breakeyes();
					else
						b.breakeyes();
				}
			} else {
				if (found instanceof Body) {
					if (((Body) found).isBlind())
						((Body) found).breakeyes();
					else
						((Body) found).breakeyes();
				}
			}
			break;
		case SHUTMOUTH:
			Body[] bodyListS = SimYukkuri.world.getCurrentMap().body.toArray(new Body[0]);
			if (ev.isShiftDown()) {
				boolean flag = true;
				if (found instanceof Body) {
					flag = !((Body) found).isShutmouth();
				}
				for (Body b : bodyListS) {
					if (!flag && b.isShutmouth())
						b.ShutMouth();
					else if (flag && !b.isShutmouth())
						b.ShutMouth();
				}
			} else if (ev.isControlDown()) {
				for (Body b : bodyListS) {
					if (b.isShutmouth())
						b.ShutMouth();
					else
						b.ShutMouth();
				}
			} else {
				if (found instanceof Body) {
					if (((Body) found).isShutmouth())
						((Body) found).ShutMouth();
					else
						((Body) found).ShutMouth();
				}
			}
			break;
		case HAIRCUT:
			if (ev.isShiftDown()) {
				break;
			}
			GadgetMenu.executeBodyMethod(ev, found, "pickHair");
			break;
		case PACK:
			Body[] bodyListPa = SimYukkuri.world.getCurrentMap().body.toArray(new Body[0]);
			if (ev.isShiftDown()) {
				boolean flag = true;
				if (found instanceof Body) {
					flag = !((Body) found).isPacked();
				}
				for (Body b : bodyListPa) {
					if (!flag && b.isPacked())
						b.pack();
					else if (flag && !b.isPacked())
						b.pack();
				}
			} else if (ev.isControlDown()) {
				for (Body b : bodyListPa) {
					if (b.isPacked())
						b.pack();
					else
						b.pack();
				}
			} else {
				if (found instanceof Body) {
					((Body) found).pack();
				}
			}
			//				GadgetMenu.executeBodyMethod(ev, found, "Peal");
			break;
		case HOLD:
			if (ev.isShiftDown()) {
				break;
			}
			GadgetMenu.executeBodyMethod(ev, found, "Hold");
			break;
		case STOMP:
			GadgetMenu.executeBodyMethod(ev, found, "strikeByPress");
			break;
		default:
			break;
		}
	}

	/**対象物をクリックするアクション2
	 * <br>道具2カテゴリの実行
	 *
	 * @param item 実行内容
	 * @param ev 入力されたマウスの動作
	 * @param found 対象オブジェクト
	 */
	public static void evaluateTool2(GadgetList item, MouseEvent ev, Obj found) {
		switch (item) {
		case BRAID_PLUCK:
			GadgetMenu.executeBodyMethod(ev, found, "takeBraid");
			break;
		case ANAL_CLOSE:
			GadgetMenu.executeBodyMethod(ev, found, "isAnalClose", "setAnalClose", "invAnalClose");
			break;
		case STALK_CUT:
			GadgetMenu.executeBodyMethod(ev, found, "getStalkCastration", "setStalkCastration", "invStalkCastration");
			break;
		case STALK_UNPLUG:
			if (found instanceof Stalk) {
				Stalk s = ((Stalk) found);
				if (s.getPlantYukkuri() != null) {
					s.getPlantYukkuri().touchStalk();
				}
			}
			break;
		case CASTRATION:
			GadgetMenu.executeBodyMethod(ev, found, "getBodyCastration", "setBodyCastration", "invBodyCastration");
			break;
		case LIGHTER:
			GadgetMenu.executeBodyMethod(ev, found, "giveFire");
			break;
		case NEEDLE:
			GadgetMenu.executeBodyMethod(ev, found, "getNeedle", "setNeedle", "invNeedle");
			break;
		case WATER:
			GadgetMenu.executeBodyMethod(ev, found, "giveWater");
			break;
		case BURY:
			GadgetMenu.executeBodyMethod(ev, found, "baryInUnderGround");
			break;
		case SET_SICK:
			GadgetMenu.executeBodyMethod(ev, found, "moldToggle");
			break;
		case SET_RAPER:
			GadgetMenu.executeBodyMethod(ev, found, "raperToggle");
			break;
		default:
			break;
		}
	}

	/**対象物をクリックするアクション3
	 * <br>アンプルカテゴリの実行
	 *
	 * @param item 実行内容
	 * @param ev 入力されたマウスの動作
	 * @param found 対象オブジェクト
	 */
	public static void evaluateAmpoule(GadgetList item, MouseEvent ev, Obj found) {
		List<Body> bodyList = SimYukkuri.world.getCurrentMap().body;
		switch (item) {
		case ORANGE_AMP:
			if (ev.isShiftDown()) {
				int flag = 0;
				if (found instanceof Body) {
					flag = ((Body) found).getAttachmentSize(OrangeAmpoule.class);
				}
				for (Body b : bodyList) {
					if (flag == 0) {
						if (b.getAttachmentSize(OrangeAmpoule.class) == 0)
							b.addAttachment(new OrangeAmpoule(b));
					} else {
						if (b.getAttachmentSize(OrangeAmpoule.class) != 0)
							b.removeAttachment(OrangeAmpoule.class);
					}
				}
			} else if (ev.isControlDown()) {
				for (Body b : bodyList) {
					if (b.getAttachmentSize(OrangeAmpoule.class) != 0) {
						b.removeAttachment(OrangeAmpoule.class);
					} else {
						b.addAttachment(new OrangeAmpoule(b));
					}
				}
			} else {
				if (found instanceof Body) {
					Body b = (Body) found;
					if (b.getAttachmentSize(OrangeAmpoule.class) != 0) {
						b.removeAttachment(OrangeAmpoule.class);
					} else {
						b.addAttachment(new OrangeAmpoule((Body) found));
					}
				}
			}
			break;
		case ACCEL_AMP:
			if (ev.isShiftDown()) {
				int flag = 0;
				if (found instanceof Body) {
					flag = ((Body) found).getAttachmentSize(AccelAmpoule.class);
				}
				for (Body b : bodyList) {
					if (flag == 0) {
						if (b.getAttachmentSize(AccelAmpoule.class) == 0)
							b.addAttachment(new AccelAmpoule(b));
					} else {
						if (b.getAttachmentSize(AccelAmpoule.class) != 0)
							b.removeAttachment(AccelAmpoule.class);
					}
				}
			} else if (ev.isControlDown()) {
				for (Body b : bodyList) {
					if (b.getAttachmentSize(AccelAmpoule.class) != 0) {
						b.removeAttachment(AccelAmpoule.class);
					} else {
						b.addAttachment(new AccelAmpoule(b));
					}
				}
			} else {
				if (found instanceof Body) {
					Body b = (Body) found;
					if (b.getAttachmentSize(AccelAmpoule.class) != 0) {
						b.removeAttachment(AccelAmpoule.class);
					} else {
						b.addAttachment(new AccelAmpoule((Body) found));
					}
				}
			}
			break;
		case STOP_AMP:
			if (ev.isShiftDown()) {
				int flag = 0;
				if (found instanceof Body) {
					flag = ((Body) found).getAttachmentSize(StopAmpoule.class);
				}
				for (Body b : bodyList) {
					if (flag == 0) {
						if (b.getAttachmentSize(StopAmpoule.class) == 0)
							b.addAttachment(new StopAmpoule(b));
					} else {
						if (b.getAttachmentSize(StopAmpoule.class) != 0)
							b.removeAttachment(StopAmpoule.class);
					}
				}
			} else if (ev.isControlDown()) {
				for (Body b : bodyList) {
					if (b.getAttachmentSize(StopAmpoule.class) != 0) {
						b.removeAttachment(StopAmpoule.class);
					} else {
						b.addAttachment(new StopAmpoule(b));
					}
				}
			} else {
				if (found instanceof Body) {
					Body b = (Body) found;
					if (b.getAttachmentSize(StopAmpoule.class) != 0) {
						b.removeAttachment(StopAmpoule.class);
					} else {
						b.addAttachment(new StopAmpoule((Body) found));
					}
				}
			}
			break;
		case HUNGRY_AMP:
			if (ev.isShiftDown()) {
				int flag = 0;
				if (found instanceof Body) {
					flag = ((Body) found).getAttachmentSize(HungryAmpoule.class);
				}
				for (Body b : bodyList) {
					if (flag == 0) {
						if (b.getAttachmentSize(HungryAmpoule.class) == 0)
							b.addAttachment(new HungryAmpoule(b));
					} else {
						if (b.getAttachmentSize(HungryAmpoule.class) != 0)
							b.removeAttachment(HungryAmpoule.class);
					}
				}
			} else if (ev.isControlDown()) {
				for (Body b : bodyList) {
					if (b.getAttachmentSize(HungryAmpoule.class) != 0) {
						b.removeAttachment(HungryAmpoule.class);
					} else {
						b.addAttachment(new HungryAmpoule(b));
					}
				}
			} else {
				if (found instanceof Body) {
					Body b = (Body) found;
					if (b.getAttachmentSize(HungryAmpoule.class) != 0) {
						b.removeAttachment(HungryAmpoule.class);
					} else {
						b.addAttachment(new HungryAmpoule((Body) found));
					}
				}
			}
			break;
		case VERYSHIT_AMP:
			if (ev.isShiftDown()) {
				int flag = 0;
				if (found instanceof Body) {
					flag = ((Body) found).getAttachmentSize(VeryShitAmpoule.class);
				}
				for (Body b : bodyList) {
					if (flag == 0) {
						if (b.getAttachmentSize(VeryShitAmpoule.class) == 0)
							b.addAttachment(new VeryShitAmpoule(b));
					} else {
						if (b.getAttachmentSize(VeryShitAmpoule.class) != 0)
							b.removeAttachment(VeryShitAmpoule.class);
					}
				}
			} else if (ev.isControlDown()) {
				for (Body b : bodyList) {
					if (b.getAttachmentSize(VeryShitAmpoule.class) != 0) {
						b.removeAttachment(VeryShitAmpoule.class);
					} else {
						b.addAttachment(new VeryShitAmpoule(b));
					}
				}
			} else {
				if (found instanceof Body) {
					Body b = (Body) found;
					if (b.getAttachmentSize(VeryShitAmpoule.class) != 0) {
						b.removeAttachment(VeryShitAmpoule.class);
					} else {
						b.addAttachment(new VeryShitAmpoule((Body) found));
					}
				}
			}
			break;
		case POISON_AMP:
			if (ev.isShiftDown()) {
				int flag = 0;
				if (found instanceof Body) {
					flag = ((Body) found).getAttachmentSize(PoisonAmpoule.class);
				}
				for (Body b : bodyList) {
					if (flag == 0) {
						if (b.getAttachmentSize(PoisonAmpoule.class) == 0)
							b.addAttachment(new PoisonAmpoule(b));
					} else {
						if (b.getAttachmentSize(PoisonAmpoule.class) != 0)
							b.removeAttachment(PoisonAmpoule.class);
					}
				}
			} else if (ev.isControlDown()) {
				for (Body b : bodyList) {
					if (b.getAttachmentSize(PoisonAmpoule.class) != 0) {
						b.removeAttachment(PoisonAmpoule.class);
					} else {
						b.addAttachment(new PoisonAmpoule(b));
					}
				}
			} else {
				if (found instanceof Body) {
					Body b = (Body) found;
					if (b.getAttachmentSize(PoisonAmpoule.class) != 0) {
						b.removeAttachment(PoisonAmpoule.class);
					} else {
						b.addAttachment(new PoisonAmpoule((Body) found));
					}
				}
			}
			break;
		case BREEDING_AMP:
			if (ev.isShiftDown()) {
				int flag = 0;
				if (found instanceof Body) {
					flag = ((Body) found).getAttachmentSize(BreedingAmpoule.class);
				}
				for (Body b : bodyList) {
					if (flag == 0) {
						if (b.getAttachmentSize(BreedingAmpoule.class) == 0)
							b.addAttachment(new BreedingAmpoule(b));
					} else {
						if (b.getAttachmentSize(BreedingAmpoule.class) != 0)
							b.removeAttachment(BreedingAmpoule.class);
					}
				}
			} else if (ev.isControlDown()) {
				for (Body b : bodyList) {
					if (b.getAttachmentSize(BreedingAmpoule.class) != 0) {
						b.removeAttachment(BreedingAmpoule.class);
					} else {
						b.addAttachment(new BreedingAmpoule(b));
					}
				}
			} else {
				if (found instanceof Body) {
					Body b = (Body) found;
					if (b.getAttachmentSize(BreedingAmpoule.class) != 0) {
						b.removeAttachment(BreedingAmpoule.class);
					} else {
						b.addAttachment(new BreedingAmpoule((Body) found));
					}
				}
			}
			break;
		case ANYD_AMP:
			if (ev.isShiftDown()) {
				int flag = 0;
				if (found instanceof Body) {
					flag = ((Body) found).getAttachmentSize(ANYDAmpoule.class);
				}
				for (Body b : bodyList) {
					if (flag == 0) {
						if (b.getAttachmentSize(ANYDAmpoule.class) == 0)
							b.addAttachment(new ANYDAmpoule(b));
					} else {
						if (b.getAttachmentSize(ANYDAmpoule.class) != 0)
							b.removeAttachment(ANYDAmpoule.class);
					}
				}
			} else if (ev.isControlDown()) {
				for (Body b : bodyList) {
					if (b.getAttachmentSize(ANYDAmpoule.class) != 0) {
						b.removeAttachment(ANYDAmpoule.class);
					} else {
						b.addAttachment(new ANYDAmpoule(b));
					}
				}
			} else {
				if (found instanceof Body) {
					Body b = (Body) found;
					if (b.getAttachmentSize(ANYDAmpoule.class) != 0) {
						b.removeAttachment(ANYDAmpoule.class);
					} else {
						b.addAttachment(new ANYDAmpoule((Body) found));
					}
				}
			}
			break;

		default:
			break;
		}
	}

	/**対象物をクリックするアクション4
	 * <br>清掃カテゴリの実行
	 *
	 * @param item 実行内容
	 * @param ev 入力されたマウスの動作
	 * @param found 対象オブジェクト
	 */
	public static void evaluateClean(GadgetList item, MouseEvent ev, Obj found) {
		switch (item) {
		case INDIVIDUAL:
			if (found instanceof Body) {
				if (((Body) found).isDead()) {
					found.remove();
				} else {
					((Body) found).setCleaning();
				}
			} else {
				found.remove();
			}
			break;
		default:
			break;
		}
	}

	/**対象物をクリックするアクション5
	 * <br>おかざりカテゴリの実行
	 *
	 * @param item 実行内容
	 * @param ev 入力されたマウスの動作
	 * @param found 対象オブジェクト
	 */
	public static void evaluateAccessory(GadgetList item, MouseEvent ev, Obj found) {
		List<Body> bodyList = SimYukkuri.world.getCurrentMap().body;
		if (ev.isShiftDown()) {
			boolean flag = true;
			if (found instanceof Body) {
				flag = !((Body) found).hasOkazari();
			}
			for (Body b : bodyList) {
				if (!flag && b.hasOkazari()) {
					b.takeOkazari(true);
				} else if (flag && !b.hasOkazari()) {
					b.giveOkazari(Okazari.OkazariType.DEFAULT);
				}
			}
		} else if (ev.isControlDown()) {
			for (Body b : bodyList) {
				if (b.hasOkazari()) {
					b.takeOkazari(true);
				} else {
					b.giveOkazari(Okazari.OkazariType.DEFAULT);
				}
			}
		} else {
			if (found instanceof Body) {
				Body b = (Body) found;
				if (b.hasOkazari()) {
					b.takeOkazari(true);
				} else {
					b.giveOkazari(Okazari.OkazariType.DEFAULT);
				}
			}
		}
	}

	/**対象物をクリックするアクション6
	 * <br>おくるみカテゴリの実行
	 *
	 * @param item 実行内容
	 * @param ev 入力されたマウスの動作
	 * @param found 対象オブジェクト
	 */
	public static void evaluatePants(GadgetList item, MouseEvent ev, Obj found) {
		List<Body> bodyList = SimYukkuri.world.getCurrentMap().body;
		if (ev.isShiftDown()) {
			boolean flag = true;
			if (found instanceof Body) {
				flag = !((Body) found).isHasPants();
			}
			for (Body b : bodyList) {
				if (!flag && b.isHasPants())
					b.takePants();
				else if (flag && !b.isHasPants())
					b.givePants();
			}
		} else if (ev.isControlDown()) {
			for (Body b : bodyList) {
				if (b.isHasPants())
					b.takePants();
				else
					b.givePants();
			}
		} else {
			if (found instanceof Body) {
				if (((Body) found).isHasPants())
					((Body) found).takePants();
				else
					((Body) found).givePants();
			}
		}
	}

	/**対象物をクリックするアクション7
	 * <br>床設置カテゴリの実行
	 *
	 * @param item 実行内容
	 * @param ev 入力されたマウスの動作
	 * @param found 対象オブジェクト
	 */
	public static void evaluateFloorItems(GadgetList item, MouseEvent ev, Obj found) {
		switch (item) {
		case DIFFUSER:
			if (found instanceof Diffuser) {
				Diffuser.setupDiffuser((Diffuser) found, true);
			}
			break;
		case ORANGE_POOL:
			if (found instanceof OrangePool) {
				OrangePool.setupOrange((OrangePool) found, true);
			}
			break;
		case BREED_POOL:
			if (found instanceof BreedingPool) {
				BreedingPool.setupPool((BreedingPool) found, true);
			}
			break;
		default:
			break;
		}
	}

	/**対象物をクリックするアクション8
	 * <br>おもちゃカテゴリの実行
	 *
	 * @param item 実行内容
	 * @param ev 入力されたマウスの動作
	 * @param found 対象オブジェクト
	 */
	public static void evaluateToys(GadgetList item, MouseEvent ev, Obj found) {
		switch (item) {
		case YUNBA_SETUP:
			if (found instanceof Yunba) {
				Yunba.setupYunba((Yunba) found, true);
			}
			break;
		default:
			break;
		}
	}

	/**対象物をクリックするアクション9
	 * <br>コンベアカテゴリの実行
	 *
	 * @param item 実行内容
	 * @param ev 入力されたマウスの動作
	 * @param found 対象オブジェクト
	 */
	public static void evaluateConveyor(GadgetList item, MouseEvent ev, Obj found) {
		switch (item) {
		case BELTCONVEYOR_SETUP:
			if (found instanceof BeltconveyorObj) {
				BeltconveyorObj.setBeltconveyor((BeltconveyorObj) found, true);
			}
			break;
		default:
			break;
		}
	}

	/**対象物をクリックするアクション10
	 * <br>声かけカテゴリの実行
	 *
	 * @param item 実行内容
	 * @param ev 入力されたマウスの動作
	 * @param found 対象オブジェクト
	 */
	public static void evaluateCommunicate(GadgetList item, MouseEvent ev, Obj found) {
		switch (item) {
		case YUKKURISITEITTENE:
			GadgetMenu.executeBodyMethod(ev, found, "voiceReaction", 0);
			return;
		case YUKKURIDIE:
			GadgetMenu.executeBodyMethod(ev, found, "voiceReaction", 1);
			return;
		case YUKKURIFURIFURI:
			GadgetMenu.executeBodyMethod(ev, found, "voiceReaction", 2);
			return;
		default:
			break;
		}
	}

	/**対象物をクリックするアクション11
	 * <br>テストカテゴリの実行
	 *
	 * @param item 実行内容
	 * @param ev 入力されたマウスの動作
	 * @param found 対象オブジェクト
	 */
	public static void evaluateTest(GadgetList item, MouseEvent ev, Obj found) {

		switch (item) {
		case RANKSET:
			if (found instanceof Body) {
				Body b = (Body) found;
				BodyRank rank = b.getBodyRank();
				if (rank == BodyRank.KAIYU) {
					b.setBodyRank(BodyRank.NORAYU);
				} else {
					b.setBodyRank(BodyRank.KAIYU);
				}
			}
			break;
		case RANKSET2:
			if (found instanceof Body) {
				Body b = (Body) found;
				PublicRank rank = b.getPublicRank();
				if (rank == PublicRank.NONE) {
					b.setPublicRank(PublicRank.UnunSlave);
					b.getFavItem().clear();
					Body p = b.getPartner();
					if (p != null) {
						// うんうんどれいになるようなくずとは りこんっ！だよ！！
						b.setPartner(null);
						p.setPartner(null);
					}
				} else {
					b.setPublicRank(PublicRank.NONE);
					b.getFavItem().clear();
				}
			}
			break;
		case EVENT_SHIT:
			if (found instanceof Body) {
				Body b = (Body) found;
				b.wakeup();
				List<Body> childrenList = BodyLogic.createActiveChildList(b, true);
				if (childrenList != null && childrenList.size() != 0) {
					FamilyActionLogic.goToShit(b, childrenList);

				}
			}
			break;
		case EVENT_EAT:
			if (found instanceof Body) {
				Body b = (Body) found;
				b.wakeup();
				List<Body> childrenList = BodyLogic.createActiveChildList(b, true);
				if (childrenList != null && childrenList.size() != 0) {
					FamilyActionLogic.goToEat(b, childrenList);
				}
			}
			break;
		case EVENT_RIDEYUKKURI:
			if (found instanceof Body) {
				Body b = (Body) found;
				b.wakeup();
				List<Body> childrenList = BodyLogic.createActiveChildList(b, true);
				if (childrenList != null && childrenList.size() != 0) {
					FamilyActionLogic.rideOnParent(b, childrenList);
				}
			}
			break;
		case EVENT_PROUDCHILD:
			if (found instanceof Body) {
				Body b = (Body) found;
				b.wakeup();
				List<Body> childrenList = BodyLogic.createActiveChildList(b, true);
				if (childrenList != null && childrenList.size() != 0) {
					FamilyActionLogic.proudChild(b, childrenList);
				}
			}
			break;
		case SETVAIN:
			if (found instanceof Body) {
				Body b = (Body) found;
				b.getInVain(true);
			}
			break;
		case Yunnyaa:
			if (found instanceof Body) {
				Body b = (Body) found;
				b.doYunnyaa(true);
			}
			break;
		case BEGGINGFORLIFE:
			if (found instanceof Body) {
				Body b = (Body) found;
				b.begForLife(true);
			}
			break;
		case PREDATORSGAME:
			if (found instanceof Body) {
				Body b = (Body) found;
				if (b.isPredatorType())
					EventLogic.addWorldEvent(new PredatorsGameEvent(b, null, null, 1), b,
							MessagePool.getMessage(b, MessagePool.Action.GameStart));
			}
			break;
		case INVITEANTS:
			if (ev.isShiftDown() || ev.isControlDown()) {
				break;
			} else {
				if (found instanceof Body) {
					Body b = (Body) found;
					if (b.getAttachmentSize(Ants.class) != 0) {
						b.removeAnts();
					} else {
						b.addAttachment(new Ants((Body) found));
					}
				}
			}
			break;
		case FEED:
			if (ev.isShiftDown() || ev.isControlDown()) {
				break;
			} else {
				if (found instanceof Body) {
					Body b = (Body) found;
					if (b.isDead())
						break;
					b.feed();
				}
			}
			break;
		case BADGE:
			if (found instanceof Body) {
				Body b = (Body) found;
				if (b.getAttachmentSize(Badge.class) != 0) {
					b.removeAttachment(Badge.class);
				} else {
					BadgeLogic.badgeTest(b);
				}
			}
			break;
		case DEBUG2:
			if (found instanceof Body) {
				Body b = (Body) found;
				b.killTime();
			}
		default:
			break;
		}
	}
}
