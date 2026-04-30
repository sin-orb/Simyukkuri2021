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
	 * @param item  実行内容
	 * @param ev    入力されたマウスの動作
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

	/**
	 * おもちゃカテゴリの実行
	 *
	 * @param item  実行内容
	 * @param ev    入力されたマウスの動作
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

	/**
	 * コンベアカテゴリの実行
	 *
	 * @param item  実行内容
	 * @param ev    入力されたマウスの動作
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
}
