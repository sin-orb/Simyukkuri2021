package src.command;

import java.awt.event.MouseEvent;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import src.base.Obj;
import src.base.ObjEX;
import src.command.GadgetMenu.ActionTarget;
import src.command.GadgetMenu.GadgetList;
import src.command.GadgetMenu.MainCategoryName;
import src.draw.Point4y;
import src.draw.Rectangle4y;
import src.draw.Translate;
import src.item.Barrier;
import src.item.Beltconveyor;
import src.item.Farm;
import src.item.Pool;
import src.system.Cash;

/******************************************************************
 * 
 * 各コマンドの実行部分
 * SimYukkuri.javaが長いので分割
 * 
 */

public class GadgetAction {

	/**
	 * 即時実行処理(清掃系)
	 *
	 * @param item 対象オブジェクト
	 */
	public static final void immediateEvaluate(GadgetList item) {
		GadgetCleanupAction.immediateEvaluate(item);
	}

	/**
	 * 左クリック処理
	 *
	 * @param item          実行内容
	 * @param target        対象アイテム
	 * @param ev            入力されたマウス操作
	 * @param fieldMousePos マウスの座標
	 * @return
	 */
	public static final ObjEX leftClickEvaluate(GadgetList item, Obj target, MouseEvent ev, int[] fieldMousePos) {
		ObjEX ret = null;
		ActionTarget eActionTarget = item.getActionTarget();

		// 実行対象の選別
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
			Rectangle4y bound = null;
			try {
				mtd = cls.getMethod("getBounding", (Class<?>[]) null);
				bound = (Rectangle4y) mtd.invoke(cls, (Object[]) null);
			} catch (NoSuchMethodException | SecurityException
					| IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				e.printStackTrace();
			}

			Point4y pos = Translate.calcObjctPutPoint(fieldMousePos[0], fieldMousePos[1], bound);
			if (pos != null) {
				// 設置実行
				try {
					Constructor<?> cst = cls.getConstructor(int.class, int.class, int.class);
					ret = (ObjEX) cst.newInstance(pos.getX(), pos.getY(), item.getInitOption());
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
					Point4y pos = Translate.invert(fieldMousePos[0], fieldMousePos[1]);
					if (pos != null) {
						Barrier found = Barrier.getBarrier(pos.getX(), pos.getY(), 1);
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

	/**
	 * 左複数クリック処理
	 *
	 * @param item          実行内容
	 * @param target        対象アイテム
	 * @param ev            入力されたマウス操作
	 * @param fieldMousePos マウスの座標
	 * @return
	 */
	public static final ObjEX leftMultiClickEvaluate(GadgetList item, Obj target, MouseEvent ev, int[] fieldMousePos) {
		return GadgetFieldPlacementAction.leftMultiClickEvaluate(item, fieldMousePos);
	}

	/**
	 * プレイヤー操作によらないアイテム設置
	 *
	 * @param cls        設置されるアイテム
	 * @param px         設置場所のX座標
	 * @param py         設置場所のY座標
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

	/**
	 * 対象物をクリックするアクション1
	 * <br>
	 * Toolカテゴリの実行
	 *
	 * @param item  実行内容
	 * @param ev    入力されたマウスの動作
	 * @param found 対象オブジェクト
	 */
	public static void evaluateTool(GadgetList item, MouseEvent ev, Obj found) {
		GadgetToolAction.evaluateTool(item, ev, found);
	}

	/**
	 * 対象物をクリックするアクション2
	 * <br>
	 * 道具2カテゴリの実行
	 *
	 * @param item  実行内容
	 * @param ev    入力されたマウスの動作
	 * @param found 対象オブジェクト
	 */
	public static void evaluateTool2(GadgetList item, MouseEvent ev, Obj found) {
		GadgetToolAction.evaluateTool2(item, ev, found);
	}

	/**
	 * 対象物をクリックするアクション3
	 * <br>
	 * アンプルカテゴリの実行
	 *
	 * @param item  実行内容
	 * @param ev    入力されたマウスの動作
	 * @param found 対象オブジェクト
	 */
	public static void evaluateAmpoule(GadgetList item, MouseEvent ev, Obj found) {
		GadgetAmpouleAction.evaluateAmpoule(item, ev, found);
	}

	/**
	 * 対象物をクリックするアクション4
	 * <br>
	 * 清掃カテゴリの実行
	 *
	 * @param item  実行内容
	 * @param ev    入力されたマウスの動作
	 * @param found 対象オブジェクト
	 */
	public static void evaluateClean(GadgetList item, MouseEvent ev, Obj found) {
		GadgetBodyAction.evaluateClean(item, ev, found);
	}

	/**
	 * 対象物をクリックするアクション5
	 * <br>
	 * おかざりカテゴリの実行
	 *
	 * @param item  実行内容
	 * @param ev    入力されたマウスの動作
	 * @param found 対象オブジェクト
	 */
	public static void evaluateAccessory(GadgetList item, MouseEvent ev, Obj found) {
		GadgetBodyAction.evaluateAccessory(item, ev, found);
	}

	/**
	 * 対象物をクリックするアクション6
	 * <br>
	 * おくるみカテゴリの実行
	 *
	 * @param item  実行内容
	 * @param ev    入力されたマウスの動作
	 * @param found 対象オブジェクト
	 */
	public static void evaluatePants(GadgetList item, MouseEvent ev, Obj found) {
		GadgetBodyAction.evaluatePants(item, ev, found);
	}

	/**
	 * 対象物をクリックするアクション7
	 * <br>
	 * 床設置カテゴリの実行
	 *
	 * @param item  実行内容
	 * @param ev    入力されたマウスの動作
	 * @param found 対象オブジェクト
	 */
	public static void evaluateFloorItems(GadgetList item, MouseEvent ev, Obj found) {
		GadgetItemSetupAction.evaluateFloorItems(item, ev, found);
	}

	/**
	 * 対象物をクリックするアクション8
	 * <br>
	 * おもちゃカテゴリの実行
	 *
	 * @param item  実行内容
	 * @param ev    入力されたマウスの動作
	 * @param found 対象オブジェクト
	 */
	public static void evaluateToys(GadgetList item, MouseEvent ev, Obj found) {
		GadgetItemSetupAction.evaluateToys(item, ev, found);
	}

	/**
	 * 対象物をクリックするアクション9
	 * <br>
	 * コンベアカテゴリの実行
	 *
	 * @param item  実行内容
	 * @param ev    入力されたマウスの動作
	 * @param found 対象オブジェクト
	 */
	public static void evaluateConveyor(GadgetList item, MouseEvent ev, Obj found) {
		GadgetItemSetupAction.evaluateConveyor(item, ev, found);
	}

	/**
	 * 対象物をクリックするアクション10
	 * <br>
	 * 声かけカテゴリの実行
	 *
	 * @param item  実行内容
	 * @param ev    入力されたマウスの動作
	 * @param found 対象オブジェクト
	 */
	public static void evaluateCommunicate(GadgetList item, MouseEvent ev, Obj found) {
		GadgetBodyAction.evaluateCommunicate(item, ev, found);
	}

	/**
	 * 対象物をクリックするアクション11
	 * <br>
	 * テストカテゴリの実行
	 *
	 * @param item  実行内容
	 * @param ev    入力されたマウスの動作
	 * @param found 対象オブジェクト
	 */
	public static void evaluateTest(GadgetList item, MouseEvent ev, Obj found) {
		GadgetDebugAction.evaluateTest(item, ev, found);
	}
}

