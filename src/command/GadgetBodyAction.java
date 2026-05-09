package src.command;

import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.List;

import src.base.Body;
import src.base.Obj;
import src.base.Okazari;
import src.command.GadgetMenu.GadgetList;
import src.util.GameWorld;

/**
 * Body に対する操作コマンド群
 * <br>清掃、おかざり、おくるみ、声かけカテゴリの実行
 */
public class GadgetBodyAction {

	/**
	 * 清掃カテゴリの実行
	 *
	 * @param item  実行内容
	 * @param ev    入力されたマウスの動作
	 * @param targetObject 対象オブジェクト
	 */
	public static void evaluateClean(GadgetList item, MouseEvent ev, Obj targetObject) {
		switch (item) {
			case INDIVIDUAL:
				if (targetObject instanceof Body) {
					Body body = (Body) targetObject;
					if (body.isDead()) {
						targetObject.remove();
					} else {
						body.setCleaning();
					}
				} else {
					targetObject.remove();
				}
				break;
			default:
				break;
		}
	}

	/**
	 * おかざりカテゴリの実行
	 *
	 * @param item  実行内容
	 * @param ev    入力されたマウスの動作
	 * @param targetObject 対象オブジェクト
	 */
	public static void evaluateAccessory(GadgetList item, MouseEvent ev, Obj targetObject) {
		List<Body> bodyList = new LinkedList<Body>(GameWorld.get().getCurrentMap().getBody().values());
		if (ev.isShiftDown()) {
			boolean shouldGiveAccessory = true;
			if (targetObject instanceof Body) {
				shouldGiveAccessory = !((Body) targetObject).hasOkazari();
			}
			for (Body body : bodyList) {
				if (!shouldGiveAccessory && body.hasOkazari()) {
					body.takeOkazari(true);
				} else if (shouldGiveAccessory && !body.hasOkazari()) {
					body.giveOkazari(Okazari.OkazariType.DEFAULT);
				}
			}
		} else if (ev.isControlDown()) {
			for (Body body : bodyList) {
				if (body.hasOkazari()) {
					body.takeOkazari(true);
				} else {
					body.giveOkazari(Okazari.OkazariType.DEFAULT);
				}
			}
		} else {
			if (targetObject instanceof Body) {
				Body body = (Body) targetObject;
				if (body.hasOkazari()) {
					body.takeOkazari(true);
				} else {
					body.giveOkazari(Okazari.OkazariType.DEFAULT);
				}
			}
		}
	}

	/**
	 * おくるみカテゴリの実行
	 *
	 * @param item  実行内容
	 * @param ev    入力されたマウスの動作
	 * @param targetObject 対象オブジェクト
	 */
	public static void evaluatePants(GadgetList item, MouseEvent ev, Obj targetObject) {
		List<Body> bodyList = new LinkedList<Body>(GameWorld.get().getCurrentMap().getBody().values());
		if (ev.isShiftDown()) {
			boolean shouldGivePants = true;
			if (targetObject instanceof Body) {
				shouldGivePants = !((Body) targetObject).isHasPants();
			}
			for (Body body : bodyList) {
				if (!shouldGivePants && body.isHasPants())
					body.takePants();
				else if (shouldGivePants && !body.isHasPants())
					body.givePants();
			}
		} else if (ev.isControlDown()) {
			for (Body body : bodyList) {
				if (body.isHasPants())
					body.takePants();
				else
					body.givePants();
			}
		} else {
			if (targetObject instanceof Body) {
				if (((Body) targetObject).isHasPants())
					((Body) targetObject).takePants();
				else
					((Body) targetObject).givePants();
			}
		}
	}

	/**
	 * 声かけカテゴリの実行
	 *
	 * @param item  実行内容
	 * @param ev    入力されたマウスの動作
	 * @param targetObject 対象オブジェクト
	 */
	public static void evaluateCommunicate(GadgetList item, MouseEvent ev, Obj targetObject) {
		switch (item) {
			case YUKKURISITEITTENE:
				BodyMethodDispatcher.execute(ev, targetObject, "voiceReaction", 0);
				return;
			case YUKKURIDIE:
				BodyMethodDispatcher.execute(ev, targetObject, "voiceReaction", 1);
				return;
			case YUKKURIFURIFURI:
				BodyMethodDispatcher.execute(ev, targetObject, "voiceReaction", 2);
				return;
			default:
				break;
		}
	}
}
