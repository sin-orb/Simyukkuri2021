package src.command;

import java.awt.event.MouseEvent;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import src.base.Entity;
import src.base.WorldEntity;
import src.command.GadgetMenu.ActionTarget;
import src.command.GadgetMenu.GadgetList;
import src.command.GadgetMenu.MainCategoryName;
import src.draw.Point4y;
import src.draw.Rectangle4y;
import src.draw.Translate;
import src.field.impl.Barrier;
import src.field.impl.Beltconveyor;
import src.field.impl.Farm;
import src.field.impl.Pool;
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
	 * @param actionItem 対象オブジェクト
	 */
	public static final void immediateEvaluate(GadgetList actionItem) {
		GadgetCleanupAction.immediateEvaluate(actionItem);
	}

	/**
	 * 左クリック処理
	 *
	 * @param actionItem    実行内容
	 * @param targetObject  対象アイテム
	 * @param ev            入力されたマウス操作
	 * @param fieldMousePos マウスの座標
	 * @return
	 */
	public static final WorldEntity leftClickEvaluate(GadgetList actionItem, Entity targetObject, MouseEvent ev, int[] fieldMousePos) {
		WorldEntity placedObject = null;
		ActionTarget actionTarget = actionItem.getActionTarget();

		// 実行対象の選別
		if (actionTarget == ActionTarget.TERRAIN_AND_GADET) {
			if (targetObject != null) {
				actionTarget = ActionTarget.GADGET;
			} else {
				actionTarget = ActionTarget.TERRAIN;
			}
		}

		// クリック対象とガジェットの選択モードチェック
		if (actionTarget == ActionTarget.TERRAIN) {
			// 背景設置物
				if (targetObject != null)
					return null;

			// 今のところキーは使用しないので未チェック
			// 座標変換と設置可能範囲チェック
				Class<?> cls = actionItem.getGadgetClass();
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
						placedObject = (WorldEntity) cst.newInstance(pos.getX(), pos.getY(), actionItem.getInitOption());
						if (placedObject != null) {
							Cash.buyItem(placedObject);
					}
				} catch (NoSuchMethodException | SecurityException | InstantiationException
						| IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		} else if (actionTarget == ActionTarget.WALL) {
			// 壁選択
			switch (actionItem) {
				case WALL_DELETE:
					Point4y pos = Translate.invert(fieldMousePos[0], fieldMousePos[1]);
					if (pos != null) {
						Barrier targetBarrier = Barrier.getBarrier(pos.getX(), pos.getY(), 1);
						if (targetBarrier != null) {
							Barrier.clearBarrier(targetBarrier);
						}
					}
					break;
				default:
					break;
			}
			} else if (actionItem.getActionTarget() == ActionTarget.FIELD) {
			// フィールド選択
				switch (actionItem) {
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
			if (targetObject == null)
				return null;
			switch (actionItem.getGroup()) {
				case TOOL:
					evaluateTool(actionItem, ev, targetObject);
					break;
				case TOOL2:
					evaluateTool2(actionItem, ev, targetObject);
					break;
				case AMPOULE:
					evaluateAmpoule(actionItem, ev, targetObject);
					break;
				case CLEAN:
					evaluateClean(actionItem, ev, targetObject);
					break;
				case ACCESSORY:
					evaluateAccessory(actionItem, ev, targetObject);
					break;
				case PANTS:
					evaluatePants(actionItem, ev, targetObject);
					break;
				case FLOOR:
					evaluateFloorItems(actionItem, ev, targetObject);
					break;
				case TOYS:
					evaluateToys(actionItem, ev, targetObject);
					break;
				case CONVEYOR:
					evaluateConveyor(actionItem, ev, targetObject);
					break;
				case VOICE:
					evaluateCommunicate(actionItem, ev, targetObject);
					break;
				case TEST:
					evaluateTest(actionItem, ev, targetObject);
					break;
				default:
					break;
			}
		}
		return placedObject;
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
	public static final WorldEntity leftMultiClickEvaluate(GadgetList item, Entity target, MouseEvent ev, int[] fieldMousePos) {
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
	public static WorldEntity putObjEX(Class<?> cls, int px, int py, int initOption) {
		WorldEntity placedObject = null;
		try {
			Constructor<?> cst = cls.getConstructor(int.class, int.class, int.class);
			placedObject = (WorldEntity) cst.newInstance(px, py, initOption);
		} catch (NoSuchMethodException | SecurityException | InstantiationException
				| IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return placedObject;
	}

	// 対象物をクリックするアクション-----------------------------------------------

	/**
	 * 対象物をクリックするアクション1
	 * <br>
	 * Toolカテゴリの実行
	 *
	 * @param item  実行内容
	 * @param ev    入力されたマウスの動作
	 * @param targetObject 対象オブジェクト
	 */
	public static void evaluateTool(GadgetList actionItem, MouseEvent ev, Entity targetObject) {
		GadgetToolAction.evaluateTool(actionItem, ev, targetObject);
	}

	/**
	 * 対象物をクリックするアクション2
	 * <br>
	 * 道具2カテゴリの実行
	 *
	 * @param item  実行内容
	 * @param ev    入力されたマウスの動作
	 * @param targetObject 対象オブジェクト
	 */
	public static void evaluateTool2(GadgetList actionItem, MouseEvent ev, Entity targetObject) {
		GadgetToolAction.evaluateTool2(actionItem, ev, targetObject);
	}

	/**
	 * 対象物をクリックするアクション3
	 * <br>
	 * アンプルカテゴリの実行
	 *
	 * @param item  実行内容
	 * @param ev    入力されたマウスの動作
	 * @param targetObject 対象オブジェクト
	 */
	public static void evaluateAmpoule(GadgetList actionItem, MouseEvent ev, Entity targetObject) {
		GadgetAmpouleAction.evaluateAmpoule(actionItem, ev, targetObject);
	}

	/**
	 * 対象物をクリックするアクション4
	 * <br>
	 * 清掃カテゴリの実行
	 *
	 * @param item  実行内容
	 * @param ev    入力されたマウスの動作
	 * @param targetObject 対象オブジェクト
	 */
	public static void evaluateClean(GadgetList actionItem, MouseEvent ev, Entity targetObject) {
		GadgetBodyAction.evaluateClean(actionItem, ev, targetObject);
	}

	/**
	 * 対象物をクリックするアクション5
	 * <br>
	 * おかざりカテゴリの実行
	 *
	 * @param item  実行内容
	 * @param ev    入力されたマウスの動作
	 * @param targetObject 対象オブジェクト
	 */
	public static void evaluateAccessory(GadgetList actionItem, MouseEvent ev, Entity targetObject) {
		GadgetBodyAction.evaluateAccessory(actionItem, ev, targetObject);
	}

	/**
	 * 対象物をクリックするアクション6
	 * <br>
	 * おくるみカテゴリの実行
	 *
	 * @param item  実行内容
	 * @param ev    入力されたマウスの動作
	 * @param targetObject 対象オブジェクト
	 */
	public static void evaluatePants(GadgetList actionItem, MouseEvent ev, Entity targetObject) {
		GadgetBodyAction.evaluatePants(actionItem, ev, targetObject);
	}

	/**
	 * 対象物をクリックするアクション7
	 * <br>
	 * 床設置カテゴリの実行
	 *
	 * @param item  実行内容
	 * @param ev    入力されたマウスの動作
	 * @param targetObject 対象オブジェクト
	 */
	public static void evaluateFloorItems(GadgetList actionItem, MouseEvent ev, Entity targetObject) {
		GadgetItemSetupAction.evaluateFloorItems(actionItem, ev, targetObject);
	}

	/**
	 * 対象物をクリックするアクション8
	 * <br>
	 * おもちゃカテゴリの実行
	 *
	 * @param item  実行内容
	 * @param ev    入力されたマウスの動作
	 * @param targetObject 対象オブジェクト
	 */
	public static void evaluateToys(GadgetList actionItem, MouseEvent ev, Entity targetObject) {
		GadgetItemSetupAction.evaluateToys(actionItem, ev, targetObject);
	}

	/**
	 * 対象物をクリックするアクション9
	 * <br>
	 * コンベアカテゴリの実行
	 *
	 * @param item  実行内容
	 * @param ev    入力されたマウスの動作
	 * @param targetObject 対象オブジェクト
	 */
	public static void evaluateConveyor(GadgetList actionItem, MouseEvent ev, Entity targetObject) {
		GadgetItemSetupAction.evaluateConveyor(actionItem, ev, targetObject);
	}

	/**
	 * 対象物をクリックするアクション10
	 * <br>
	 * 声かけカテゴリの実行
	 *
	 * @param item  実行内容
	 * @param ev    入力されたマウスの動作
	 * @param targetObject 対象オブジェクト
	 */
	public static void evaluateCommunicate(GadgetList actionItem, MouseEvent ev, Entity targetObject) {
		GadgetBodyAction.evaluateCommunicate(actionItem, ev, targetObject);
	}

	/**
	 * 対象物をクリックするアクション11
	 * <br>
	 * テストカテゴリの実行
	 *
	 * @param item  実行内容
	 * @param ev    入力されたマウスの動作
	 * @param targetObject 対象オブジェクト
	 */
	public static void evaluateTest(GadgetList actionItem, MouseEvent ev, Entity targetObject) {
		GadgetDebugAction.evaluateTest(actionItem, ev, targetObject);
	}
}

