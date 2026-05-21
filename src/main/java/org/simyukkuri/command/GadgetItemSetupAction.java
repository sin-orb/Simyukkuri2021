package org.simyukkuri.command;

import java.awt.event.MouseEvent;
import org.simyukkuri.command.GadgetMenu.GadgetMenuChoice;
import org.simyukkuri.entity.core.Entity;
import org.simyukkuri.entity.core.world.item.BeltconveyorObj;
import org.simyukkuri.entity.core.world.item.BreedingPool;
import org.simyukkuri.entity.core.world.item.Diffuser;
import org.simyukkuri.entity.core.world.item.OrangePool;
import org.simyukkuri.entity.core.world.item.Yunba;

/**
 * アイテムのセットアップコマンド群
 * <br>
 * 床設置、おもちゃ、コンベアカテゴリの実行
 */
public class GadgetItemSetupAction {

	/**
	 * 床設置カテゴリの実行
	 *
	 * @param actionItem   実行内容
	 * @param ev           入力されたマウスの動作
	 * @param targetObject 対象オブジェクト
	 */
	public static void evaluateFloorItems(GadgetMenuChoice actionItem, MouseEvent ev, Entity targetObject) {
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
	 * @param actionItem   実行内容
	 * @param ev           入力されたマウスの動作
	 * @param targetObject 対象オブジェクト
	 */
	public static void evaluateToys(GadgetMenuChoice actionItem, MouseEvent ev, Entity targetObject) {
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
	 * @param actionItem   実行内容
	 * @param ev           入力されたマウスの動作
	 * @param targetObject 対象オブジェクト
	 */
	public static void evaluateConveyor(GadgetMenuChoice actionItem, MouseEvent ev, Entity targetObject) {
		switch (actionItem) {
			case BELTCONVEYOR_SETUP:
				if (targetObject instanceof BeltconveyorObj) {
					BeltconveyorObj.setBeltconveyors((BeltconveyorObj) targetObject, true);
				}
				break;
			default:
				break;
		}
	}
}
