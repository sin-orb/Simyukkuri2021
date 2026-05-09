package src.logic;

import src.base.Yukkuri;
import src.event.AvoidMoldEvent;
import src.event.HateNoOkazariEvent;
import src.system.MessagePool;
import src.util.GameMessages;
import src.util.GameRandom;

/**
 * Contact-side effect handling used by BodyLogic.
 */
public final class BodyContactEffectRule {

	private BodyContactEffectRule() {
	}

	/**
	 * Handle mold avoidance and hate-no-okazari reaction branches.
	 *
	 * @param targetBody target body
	 * @param actorBody actor body
	 * @return true when the branch handled the action
	 */
	public static boolean handleContactEffects(Yukkuri targetBody, Yukkuri actorBody) {
		// 自分がかびてなくてかつ、相手がかびてるとき
		if (actorBody.findSick(targetBody) && !actorBody.isSick()) {
			EventLogic.addBodyEvent(actorBody, new AvoidMoldEvent(actorBody, targetBody, null, 1), null, null);
			return true;
		}
		// 相手がかびてなくてかつ、自分がかびてるとき
		if (targetBody.findSick(actorBody) && !targetBody.isSick()) {
			EventLogic.addBodyEvent(targetBody, new AvoidMoldEvent(targetBody, actorBody, null, 1), null, null);
			return true;
		}
		// 相手が子供でも、子供にお飾りがなくてかつ親がバカならなら制裁する
		if (actorBody.isAdult() && !targetBody.isAdult() && (targetBody.isChild(actorBody) || actorBody.isMother(targetBody))
				&& (actorBody.getIntelligence() == src.enums.Intelligence.FOOL && !targetBody.hasOkazari())) {
			if (actorBody.getCurrentEvent() == null && targetBody.isNYD() && GameRandom.nextBoolean()) {
				actorBody.clearActions();
				EventLogic.addWorldEvent(new HateNoOkazariEvent(actorBody, targetBody, null, 10), actorBody,
						GameMessages.getMessage(actorBody, MessagePool.Action.HateYukkuri));
			}
			return true;
		}
		return false;
	}
}
