package org.simyukkuri.command;

import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.List;

import org.simyukkuri.command.GadgetMenu.GadgetList;
import org.simyukkuri.entity.core.Entity;
import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.entity.core.world.bodylinked.Okazari;
import org.simyukkuri.util.GameWorld;

/**
 * Yukkuri に対する操作コマンド群
 * <br>
 * 清掃、おかざり、おくるみ、声かけカテゴリの実行
 */
public class GadgetBodyAction {

	/**
	 * 清掃カテゴリの実行
	 *
	 * @param item         実行内容
	 * @param ev           入力されたマウスの動作
	 * @param targetObject 対象オブジェクト
	 */
	public static void evaluateClean(GadgetList item, MouseEvent ev, Entity targetObject) {
		switch (item) {
			case INDIVIDUAL:
				if (targetObject instanceof Yukkuri) {
					Yukkuri body = (Yukkuri) targetObject;
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
	 * @param item         実行内容
	 * @param ev           入力されたマウスの動作
	 * @param targetObject 対象オブジェクト
	 */
	public static void evaluateAccessory(GadgetList item, MouseEvent ev, Entity targetObject) {
		List<Yukkuri> bodyList = new LinkedList<Yukkuri>(GameWorld.get().getCurrentMap().getYukkuriMap().values());
		if (ev.isShiftDown()) {
			boolean shouldGiveAccessory = true;
			if (targetObject instanceof Yukkuri) {
				shouldGiveAccessory = !((Yukkuri) targetObject).hasOkazari();
			}
			for (Yukkuri body : bodyList) {
				if (!shouldGiveAccessory && body.hasOkazari()) {
					body.takeOkazari(true);
				} else if (shouldGiveAccessory && !body.hasOkazari()) {
					body.giveOkazari(Okazari.OkazariType.DEFAULT);
				}
			}
		} else if (ev.isControlDown()) {
			for (Yukkuri body : bodyList) {
				if (body.hasOkazari()) {
					body.takeOkazari(true);
				} else {
					body.giveOkazari(Okazari.OkazariType.DEFAULT);
				}
			}
		} else {
			if (targetObject instanceof Yukkuri) {
				Yukkuri body = (Yukkuri) targetObject;
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
	 * @param item         実行内容
	 * @param ev           入力されたマウスの動作
	 * @param targetObject 対象オブジェクト
	 */
	public static void evaluatePants(GadgetList item, MouseEvent ev, Entity targetObject) {
		List<Yukkuri> bodyList = new LinkedList<Yukkuri>(GameWorld.get().getCurrentMap().getYukkuriMap().values());
		if (ev.isShiftDown()) {
			boolean shouldGivePants = true;
			if (targetObject instanceof Yukkuri) {
				shouldGivePants = !((Yukkuri) targetObject).isHasPants();
			}
			for (Yukkuri body : bodyList) {
				if (!shouldGivePants && body.isHasPants())
					body.takePants();
				else if (shouldGivePants && !body.isHasPants())
					body.givePants();
			}
		} else if (ev.isControlDown()) {
			for (Yukkuri body : bodyList) {
				if (body.isHasPants())
					body.takePants();
				else
					body.givePants();
			}
		} else {
			if (targetObject instanceof Yukkuri) {
				if (((Yukkuri) targetObject).isHasPants())
					((Yukkuri) targetObject).takePants();
				else
					((Yukkuri) targetObject).givePants();
			}
		}
	}

	/**
	 * 声かけカテゴリの実行
	 *
	 * @param item         実行内容
	 * @param ev           入力されたマウスの動作
	 * @param targetObject 対象オブジェクト
	 */
	public static void evaluateCommunicate(GadgetList item, MouseEvent ev, Entity targetObject) {
		switch (item) {
			case YUKKURISITEITTENE:
				YukkuriMethodDispatcher.execute(ev, targetObject, "voiceReaction", 0);
				return;
			case YUKKURIDIE:
				YukkuriMethodDispatcher.execute(ev, targetObject, "voiceReaction", 1);
				return;
			case YUKKURIFURIFURI:
				YukkuriMethodDispatcher.execute(ev, targetObject, "voiceReaction", 2);
				return;
			default:
				break;
		}
	}
}
