package src.command;

import src.SimYukkuri;
import src.base.WorldEntity;
import src.command.GadgetMenu.ActionTarget;
import src.command.GadgetMenu.GadgetList;
import src.command.GadgetMenu.MainCategoryName;
import src.field.impl.Barrier;
import src.field.impl.Beltconveyor;
import src.item.BeltconveyorObj;
import src.field.impl.Farm;
import src.field.impl.Pool;
import src.draw.Translate;

/**
 * バリア/フィールド/コンベアの複数クリック設置処理
 */
public class GadgetFieldPlacementAction {

	/**
	 * 左複数クリック処理
	 *
	 * @param item          実行内容
	 * @param fieldMousePos マウスの座標
	 * @return 設置されたオブジェクト (通常 null)
	 */
	public static WorldEntity leftMultiClickEvaluate(GadgetList item, int[] fieldMousePos) {

		if (item.getActionTarget() != ActionTarget.TERRAIN) {
			return null;
		}

		if (item.getGroup() == MainCategoryName.BARRIER) {
			handleBarrierPlacement(item, fieldMousePos);
		}

		if (item.getGroup() == MainCategoryName.CONVEYOR) {
			handleConveyorPlacement(item, fieldMousePos);
		}

		return null;
	}

	private static void handleBarrierPlacement(GadgetList item, int[] fieldMousePos) {
		if (SimYukkuri.fieldSX == -1 || SimYukkuri.fieldSY == -1) {
			// 始点のクリック
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
					SimYukkuri.fieldType = 100;
					break;
				case FARM:
					SimYukkuri.fieldType = 101;
					break;
				case BELTCONVEYOR:
					SimYukkuri.fieldType = 102;
					break;
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
					if (!Translate.inInvertLimit(fieldMousePos[0], fieldMousePos[1])) {
						break;
					}
					SimYukkuri.fieldEX = fieldMousePos[0];
					SimYukkuri.fieldEY = fieldMousePos[1];
					if ((SimYukkuri.fieldSX != SimYukkuri.fieldEX)
							|| (SimYukkuri.fieldSY != SimYukkuri.fieldEY)) {
						new Barrier(SimYukkuri.fieldSX, SimYukkuri.fieldSY, SimYukkuri.fieldEX,
								SimYukkuri.fieldEY, SimYukkuri.fieldType);
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

	private static void handleConveyorPlacement(GadgetList item, int[] fieldMousePos) {
		if (SimYukkuri.fieldSX == -1 || SimYukkuri.fieldSY == -1) {
			// 始点のクリック
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
