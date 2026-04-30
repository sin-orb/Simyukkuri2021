package src.logic;

import src.base.Body;
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
	 * @param p target body
	 * @param b actor body
	 * @return true when the branch handled the action
	 */
	public static boolean handleContactEffects(Body p, Body b) {
		// 自分がかびてなくてかつ、相手がかびてるとき
		if (b.findSick(p) && !b.isSick()) {
			EventLogic.addBodyEvent(b, new AvoidMoldEvent(b, p, null, 1), null, null);
			return true;
		}
		// 相手がかびてなくてかつ、自分がかびてるとき
		if (p.findSick(b) && !p.isSick()) {
			EventLogic.addBodyEvent(p, new AvoidMoldEvent(p, b, null, 1), null, null);
			return true;
		}
		// 相手が子供でも、子供にお飾りがなくてかつ親がバカならなら制裁する
		if (b.isAdult() && !p.isAdult() && (p.isChild(b) || b.isMother(p))
				&& (b.getIntelligence() == src.enums.Intelligence.FOOL && !p.hasOkazari())) {
			if (b.getCurrentEvent() == null && p.isNYD() && GameRandom.nextBoolean()) {
				b.clearActions();
				EventLogic.addWorldEvent(new HateNoOkazariEvent(b, p, null, 10), b,
						GameMessages.getMessage(b, MessagePool.Action.HateYukkuri));
			}
			return true;
		}
		return false;
	}
}
