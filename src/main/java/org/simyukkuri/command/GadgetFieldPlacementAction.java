package org.simyukkuri.command;

import org.simyukkuri.SimYukkuri;
import org.simyukkuri.command.GadgetMenu.ActionTarget;
import org.simyukkuri.command.GadgetMenu.GadgetMenuChoice;
import org.simyukkuri.command.GadgetMenu.MainCategoryName;
import org.simyukkuri.draw.Translate;
import org.simyukkuri.entity.core.world.WorldEntity;
import org.simyukkuri.entity.core.world.item.BeltconveyorObj;
import org.simyukkuri.field.impl.Barrier;
import org.simyukkuri.field.impl.Beltconveyor;
import org.simyukkuri.field.impl.Farm;
import org.simyukkuri.field.impl.Pool;

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
	public static WorldEntity leftMultiClickEvaluate(GadgetMenuChoice item, int[] fieldMousePos) {

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

	private static void handleBarrierPlacement(GadgetMenuChoice item, int[] fieldMousePos) {
		if (SimYukkuri.fieldSx == -1 || SimYukkuri.fieldSy == -1) {
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
				case NO_UNUN:
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
					SimYukkuri.fieldSx = fieldMousePos[0];
					SimYukkuri.fieldSy = fieldMousePos[1];
					SimYukkuri.fieldEx = SimYukkuri.fieldSx;
					SimYukkuri.fieldEy = SimYukkuri.fieldSy;
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
				case NO_UNUN:
				case KEKKAI:
					if (!Translate.inInvertLimit(fieldMousePos[0], fieldMousePos[1])) {
						break;
					}
					SimYukkuri.fieldEx = fieldMousePos[0];
					SimYukkuri.fieldEy = fieldMousePos[1];
					if ((SimYukkuri.fieldSx != SimYukkuri.fieldEx)
							|| (SimYukkuri.fieldSy != SimYukkuri.fieldEy)) {
						new Barrier(SimYukkuri.fieldSx, SimYukkuri.fieldSy, SimYukkuri.fieldEx,
								SimYukkuri.fieldEy, SimYukkuri.fieldType);
					}
					SimYukkuri.fieldSx = SimYukkuri.fieldEx;
					SimYukkuri.fieldSy = SimYukkuri.fieldEy;
					SimYukkuri.fieldEx = SimYukkuri.fieldSx;
					SimYukkuri.fieldEy = SimYukkuri.fieldSy;
					break;
				case POOL:
					SimYukkuri.fieldEx = fieldMousePos[0];
					SimYukkuri.fieldEy = fieldMousePos[1];
					new Pool(SimYukkuri.fieldSx, SimYukkuri.fieldSy,
							SimYukkuri.fieldEx, SimYukkuri.fieldEy);
					SimYukkuri.fieldSx = -1;
					SimYukkuri.fieldSy = -1;
					SimYukkuri.fieldEx = -1;
					SimYukkuri.fieldEy = -1;
					break;
				case FARM:
					SimYukkuri.fieldEx = fieldMousePos[0];
					SimYukkuri.fieldEy = fieldMousePos[1];
					new Farm(SimYukkuri.fieldSx, SimYukkuri.fieldSy, SimYukkuri.fieldEx, SimYukkuri.fieldEy);
					SimYukkuri.fieldSx = -1;
					SimYukkuri.fieldSy = -1;
					SimYukkuri.fieldEx = -1;
					SimYukkuri.fieldEy = -1;
					break;
				case BELTCONVEYOR:
					SimYukkuri.fieldEx = fieldMousePos[0];
					SimYukkuri.fieldEy = fieldMousePos[1];
					new Beltconveyor(SimYukkuri.fieldSx, SimYukkuri.fieldSy, SimYukkuri.fieldEx,
							SimYukkuri.fieldEy);
					SimYukkuri.fieldSx = -1;
					SimYukkuri.fieldSy = -1;
					SimYukkuri.fieldEx = -1;
					SimYukkuri.fieldEy = -1;
					break;
				default:
					break;
			}
		}
	}

	private static void handleConveyorPlacement(GadgetMenuChoice item, int[] fieldMousePos) {
		if (SimYukkuri.fieldSx == -1 || SimYukkuri.fieldSy == -1) {
			// 始点のクリック
			switch (item) {
				case BELTCONVEYOR_CUSTOM:
					SimYukkuri.fieldSx = fieldMousePos[0];
					SimYukkuri.fieldSy = fieldMousePos[1];
					SimYukkuri.fieldEx = SimYukkuri.fieldSx;
					SimYukkuri.fieldEy = SimYukkuri.fieldSy;
					break;
				default:
					break;
			}
		} else {
			// 終点のクリック
			switch (item) {
				case BELTCONVEYOR_CUSTOM:
					SimYukkuri.fieldEx = fieldMousePos[0];
					SimYukkuri.fieldEy = fieldMousePos[1];
					new BeltconveyorObj(0, 0, 5);
					SimYukkuri.fieldSx = -1;
					SimYukkuri.fieldSy = -1;
					SimYukkuri.fieldEx = -1;
					SimYukkuri.fieldEy = -1;
					break;
				default:
					break;
			}
		}
	}
}
