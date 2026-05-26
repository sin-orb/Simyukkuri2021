package org.simyukkuri.logic;

import org.simyukkuri.entity.core.living.yukkuri.Yukkuri;
import org.simyukkuri.event.impl.AvoidMoldEvent;
import org.simyukkuri.event.impl.HateNoOkazariEvent;
import org.simyukkuri.system.MessagePool;
import org.simyukkuri.util.GameMessages;
import org.simyukkuri.util.GameRandom;

/**
 * Contact-side effect handling used by YukkuriLogic.
 */
public final class YukkuriContactEffectRule {

	private YukkuriContactEffectRule() {
	}

	/**
	 * カビ回避とおかざりなし嫌悪反応の分岐を処理する。
	 *
	 * @param targetBody 処理対象ゆっくり
	 * @param actorBody  行動主体ゆっくり
	 * @return 分岐が処理を担当した場合は true
	 */
	public static boolean handleContactEffects(Yukkuri targetBody, Yukkuri actorBody) {
		// 自分がかびてなくてかつ、相手がかびてるとき
		if (actorBody.findSick(targetBody) && !actorBody.isSick()) {
			EventLogic.addYukkuriEvent(actorBody, new AvoidMoldEvent(actorBody, targetBody, null, 1), null, null);
			return true;
		}
		// 相手がかびてなくてかつ、自分がかびてるとき
		if (targetBody.findSick(actorBody) && !targetBody.isSick()) {
			EventLogic.addYukkuriEvent(targetBody, new AvoidMoldEvent(targetBody, actorBody, null, 1), null, null);
			return true;
		}
		// 相手が子供でも、子供にお飾りがなくてかつ親がバカならなら制裁する
		if (actorBody.isAdult() && !targetBody.isAdult()
				&& (targetBody.isChild(actorBody) || actorBody.isMother(targetBody))
				&& (actorBody.getIntelligence() == org.simyukkuri.enums.Intelligence.FOOL && !targetBody.hasOkazari())) {
			if (actorBody.getCurrentEvent() == null && targetBody.isNyd() && GameRandom.nextBoolean()) {
				actorBody.clearActions();
				EventLogic.addWorldEvent(new HateNoOkazariEvent(actorBody, targetBody, null, 10), actorBody,
						GameMessages.getMessage(actorBody, MessagePool.Action.HateYukkuri));
			}
			return true;
		}
		return false;
	}
}
