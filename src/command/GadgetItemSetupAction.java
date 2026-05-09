package src.command;

import java.awt.event.MouseEvent;

import src.base.Obj;
import src.command.GadgetMenu.GadgetList;
import src.item.Beltconveyor;
import src.item.BeltconveyorObj;
import src.item.BreedingPool;
import src.item.Diffuser;
import src.item.OrangePool;
import src.item.Yunba;

/**
 * アイテムのセットアップコマンド群
 * <br>床設置、おもちゃ、コンベアカテゴリの実行
 */
public class GadgetItemSetupAction {

	/**
	 * 床設置カテゴリの実行
	 *
	 * @param actionItem  実行内容
	 * @param ev          入力されたマウスの動作
	 * @param targetObject 対象オブジェクト
	 */
	public static void evaluateFloorItems(GadgetList actionItem, MouseEvent ev, Obj targetObject) {
		switch (actionItem) {
			case DIFFUSER:
				if (targetObject instanceof Diffuser) {
					Diffuser.setupDiffuser((Diffuser) targetObject, true);
				}
				break;
			case ORANGE_POOL:
				if (targetObject instanceof OrangePool) {
					OrangePool.setupOrange((OrangePool) targetObject, true);
				}
				break;
			case BREED_POOL:
				if (targetObject instanceof BreedingPool) {
					BreedingPool.setupPool((BreedingPool) targetObject, true);
				}
				break;
			default:
				break;
		}
	}

	/**
	 * おもちゃカテゴリの実行
	 *
	 * @param actionItem  実行内容
	 * @param ev          入力されたマウスの動作
	 * @param targetObject 対象オブジェクト
	 */
	public static void evaluateToys(GadgetList actionItem, MouseEvent ev, Obj targetObject) {
		switch (actionItem) {
			case YUNBA_SETUP:
				if (targetObject instanceof Yunba) {
					Yunba.setupYunba((Yunba) targetObject, true);
				}
				break;
			default:
				break;
		}
	}

	/**
	 * コンベアカテゴリの実行
	 *
	 * @param actionItem  実行内容
	 * @param ev          入力されたマウスの動作
	 * @param targetObject 対象オブジェクト
	 */
	public static void evaluateConveyor(GadgetList actionItem, MouseEvent ev, Obj targetObject) {
		switch (actionItem) {
			case BELTCONVEYOR_SETUP:
				if (targetObject instanceof BeltconveyorObj) {
					BeltconveyorObj.setBeltconveyor((BeltconveyorObj) targetObject, true);
				}
				break;
			default:
				break;
		}
	}
}
