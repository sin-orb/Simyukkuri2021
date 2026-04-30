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

	/**
	 * おかざりカテゴリの実行
	 *
	 * @param item  実行内容
	 * @param ev    入力されたマウスの動作
	 * @param found 対象オブジェクト
	 */
	public static void evaluateAccessory(GadgetList item, MouseEvent ev, Obj found) {
		List<Body> bodyList = new LinkedList<Body>(GameWorld.get().getCurrentMap().getBody().values());
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

	/**
	 * おくるみカテゴリの実行
	 *
	 * @param item  実行内容
	 * @param ev    入力されたマウスの動作
	 * @param found 対象オブジェクト
	 */
	public static void evaluatePants(GadgetList item, MouseEvent ev, Obj found) {
		List<Body> bodyList = new LinkedList<Body>(GameWorld.get().getCurrentMap().getBody().values());
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

	/**
	 * 声かけカテゴリの実行
	 *
	 * @param item  実行内容
	 * @param ev    入力されたマウスの動作
	 * @param found 対象オブジェクト
	 */
	public static void evaluateCommunicate(GadgetList item, MouseEvent ev, Obj found) {
		switch (item) {
			case YUKKURISITEITTENE:
				BodyMethodDispatcher.execute(ev, found, "voiceReaction", 0);
				return;
			case YUKKURIDIE:
				BodyMethodDispatcher.execute(ev, found, "voiceReaction", 1);
				return;
			case YUKKURIFURIFURI:
				BodyMethodDispatcher.execute(ev, found, "voiceReaction", 2);
				return;
			default:
				break;
		}
	}
}
